package com.palmapp.master.baselib

import android.app.Application
import android.widget.Toast
import com.alibaba.android.arouter.launcher.ARouter
import com.cs.statistic.StatisticsManager
import com.facebook.stetho.Stetho
import com.palmapp.master.baselib.buychannel.BuyChannelProxy
import com.palmapp.master.baselib.deamon.DaemonProxy
import com.palmapp.master.baselib.event.GoogleAdvertisingIdEvent
import com.palmapp.master.baselib.manager.CrashHandler
import com.palmapp.master.baselib.manager.DebugProxy
import com.palmapp.master.baselib.manager.ForegroundManager
import com.palmapp.master.baselib.manager.ThreadExecutorProxy
import com.palmapp.master.baselib.manager.config.ConfigManager
import com.palmapp.master.baselib.proxy.GoogleServiceProxy
import com.palmapp.master.baselib.statistics.ScheduleTaskHandler
import com.palmapp.master.baselib.utils.AppUtil
import com.palmapp.master.baselib.utils.LogUtil
import com.palmapp.master.baselib.utils.VersionController
import com.tencent.mmkv.MMKV
import com.tencent.mmkv.MMKVHandler
import com.tencent.mmkv.MMKVLogLevel
import com.tencent.mmkv.MMKVRecoverStrategic
import io.reactivex.plugins.RxJavaPlugins
import org.greenrobot.eventbus.EventBus


class LibModuleApp(application: Application) : BaseModuleApp(application) {
    override fun onTerminate() {
    }

    override fun getPriority(): Int {
        return Int.MAX_VALUE
    }

    override fun attachBaseContext() {
        //初始化MMKV
        DaemonProxy.init(application)
        MMKV.initialize(application)
        MMKV.registerHandler(object : MMKVHandler {
            override fun onMMKVCRCCheckFail(s: String): MMKVRecoverStrategic {
                return MMKVRecoverStrategic.OnErrorRecover
            }

            override fun onMMKVFileLengthError(s: String): MMKVRecoverStrategic {
                return MMKVRecoverStrategic.OnErrorRecover
            }

            override fun wantLogRedirecting(): Boolean {
                return false
            }

            override fun mmkvLog(
                mmkvLogLevel: MMKVLogLevel,
                s: String,
                i: Int,
                s1: String,
                s2: String
            ) {

            }
        })
        GoCommonEnv.loadConfig(application)
        //初始化ARouter
        if (DebugProxy.isDebug()) {           // 这两行必须写在init之前，否则这些配置在init过程中将无效
            ARouter.openDebug()   // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
        }
        if (DebugProxy.isOpenLog()) {
            ARouter.openLog()     // 打印日志
        }
        ARouter.init(application) // 尽可能早，推荐在Application中初始化

        RxJavaPlugins.setErrorHandler {
            ThreadExecutorProxy.runOnMainThread(Runnable {
                if (DebugProxy.isOpenLog())
                    Toast.makeText(application, it.message, Toast.LENGTH_LONG).show()
            })
            it.printStackTrace()
        }
        closeAndroidPDialog()
    }

    /**
     * 解决androidP 第一次打开程序出现莫名弹窗
     * 弹窗内容“detected problems with api ”
     */
    private fun closeAndroidPDialog() {
        try {
            val aClass = Class.forName("android.content.pm.PackageParser\$Package")
            val declaredConstructor = aClass.getDeclaredConstructor(String::class.java)
            declaredConstructor.isAccessible = true
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            val cls = Class.forName("android.app.ActivityThread")
            val declaredMethod = cls.getDeclaredMethod("currentActivityThread")
            declaredMethod.isAccessible = true
            val activityThread = declaredMethod.invoke(null)
            val mHiddenApiWarningShown = cls.getDeclaredField("mHiddenApiWarningShown")
            mHiddenApiWarningShown.isAccessible = true
            mHiddenApiWarningShown.setBoolean(activityThread, true)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onCreate() {
        StatisticsManager.initBasicInfo(
            GoCommonEnv.applicationId,
            GoCommonEnv.innerChannel

            , "${GoCommonEnv.applicationId}.staticsdkprovider"
        )
        if (DebugProxy.isOpenLog()) {
            StatisticsManager.getInstance(application).enableLog(true)
        }
        CrashHandler().init(application)
        //目前非主进程都不需要初始化
        if (!AppUtil.isMainProcess(application))
            return
        ForegroundManager.init(application)
        VersionController.init()
        //初始化log
        LogUtil.setIsDebug(DebugProxy.isOpenLog())
        //初始化买量
        BuyChannelProxy.initBuyChannelApi()
        //初始化Stetho
        if (DebugProxy.isDebug() || DebugProxy.isOpenLog()) {
            Stetho.initializeWithDefaults(application)
        }

        ThreadExecutorProxy.execute(Runnable {
            val id = GoogleServiceProxy.getGoogleAdvertisingId()
            EventBus.getDefault().post(GoogleAdvertisingIdEvent(id))
        })
        ScheduleTaskHandler(application).startAllTasks()
    }
}
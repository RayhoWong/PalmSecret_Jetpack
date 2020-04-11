package com.palmapp.master.module_pay

import android.app.Application
import com.palmapp.master.baselib.BaseModuleApp
import com.palmapp.master.baselib.manager.DebugProxy
import com.cs.bd.subscribe.SubscribeHelper
import com.cs.bd.subscribe.client.UtmSrcInfo
import com.cs.bd.subscribe.client.ClientInfo
import com.palmapp.master.baselib.GoCommonEnv
import com.palmapp.master.baselib.Product.MODULE_ID_PAY_GUIDE
import com.palmapp.master.baselib.buychannel.BuyChannelProxy
import com.palmapp.master.baselib.constants.AppConstants
import com.palmapp.master.baselib.manager.ThreadExecutorProxy
import com.palmapp.master.baselib.manager.config.*
import com.palmapp.master.baselib.utils.AppUtil
import com.palmapp.master.baselib.utils.LogUtil
import com.palmapp.master.baselib.utils.VersionController
import com.palmapp.master.module_network.HttpClient
import io.reactivex.disposables.Disposable


/**
 *  支付相关App
 * @author :     xiemingrui
 * @since :      2019/8/5
 */
class PayModuleApp(application: Application) : BaseModuleApp(application) {
    @Volatile
    var hasLoadConfig = false
    var disposable: Disposable? = null
    override fun onCreate() {
        // SDK 初始化
        val bean = BuyChannelProxy.getBuyChannelBean()
        val builder = ClientInfo.newBuilder()
        builder.utmSrcInfo(UtmSrcInfo(bean.buyChannel, bean.secondUserType)) // 必填；买量信息
            .isUpgrade(!VersionController.isNewUser) // 必填；是否升级用户
            .installTimeStamp(
                AppUtil.getInstalledTime(
                    application,
                    application.packageName
                )
            ) // 必填；应用首次安装时间戳
            .gpBillingBse64PublicKey(AppConstants.PUBLIC_KEY) // 可选；可在Google Play developer console上查询到
            .isTestServer(DebugProxy.isDebug()) // 可选；是否测服
        SubscribeHelper.init(application, builder.build(), true)
        ConfigManager.addConfig(PayConfig())
            .addConfig(AbUserConfig())
            .addConfig(AdConfig())
            .addConfig(StarGuideConfig())
            .addConfig(AnimalConfig())
            .addConfig(HeartRateDialogConfig())
            .addConfig(TimeConfig())
            .addConfig(HomeConfig())
        ConfigManager.init()
        Thread(Runnable {
            Thread.sleep(9000)
            if (!hasLoadConfig) {
                val bean = BuyChannelProxy.getBuyChannelBean()
                var userType = "自然"
                if (bean.isUserBuy) {
                    userType = "买量"
                }
                LogUtil.d("PayModuleApp", "开始请求${userType}配置")
                ConfigManager.requestAbConfig()
                disposable = HttpClient.getConfRequest().getResource(MODULE_ID_PAY_GUIDE).subscribe {
                    if (!hasLoadConfig) {
                        GoCommonEnv.storePaymentGuideConfig(it)
                        LogUtil.d("PayModuleApp", "拿到${userType}基础服务配置")
                    }
                }
            }
        }).start()
    }

    override fun onBuyChannelUpdate() {
        if (!BuyChannelProxy.getBuyChannelBean().isUserBuy) {
            return
        }
        disposable?.dispose()
        hasLoadConfig = true
        ThreadExecutorProxy.execute(Runnable {
            val bean = BuyChannelProxy.getBuyChannelBean()
            var userType = "自然"
            if (bean.isUserBuy) {
                userType = "买量"
            }
            LogUtil.d("PayModuleApp", "开始请求${userType}配置: " + bean.buyChannel + " " + bean.secondUserType)
            ConfigManager.requestAbConfig()
            HttpClient.getConfRequest().getResource(MODULE_ID_PAY_GUIDE)
                .subscribe {
                    LogUtil.d("PayModuleApp", "拿到${userType}基础服务配置")
                    GoCommonEnv.storePaymentGuideConfig(it)
                }
            SubscribeHelper.setUtmSource(application, UtmSrcInfo(bean.buyChannel, bean.secondUserType)) // 必填；变化后的买量信息
        })
    }

    override fun onTerminate() {
    }

    override fun attachBaseContext() {
    }

    override fun getPriority(): Int {
        return 1
    }

}
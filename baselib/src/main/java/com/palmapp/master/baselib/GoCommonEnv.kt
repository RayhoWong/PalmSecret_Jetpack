package com.palmapp.master.baselib

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import com.google.gson.reflect.TypeToken
import com.palmapp.master.baselib.bean.pay.ModuleConfig
import com.palmapp.master.baselib.bean.tarot.TarotBean
import com.palmapp.master.baselib.bean.user.UserInfo
import com.palmapp.master.baselib.constants.PreConstants
import com.palmapp.master.baselib.manager.GoPrefManager
import com.palmapp.master.baselib.manager.PayGuideManager
import com.palmapp.master.baselib.manager.ThreadExecutorProxy
import com.palmapp.master.baselib.utils.GoGson
import java.io.InputStream
import java.io.InputStreamReader

/**
 * @ClassName:  GoCommonEnv
 * @Description:打包环境配置类
 * @Author:     xiemingrui
 * @CreateDate: 2019/7/23
 */
@SuppressLint("StaticFieldLeak")
object GoCommonEnv {

    private const val DEFAULT_CHANNEL = "200"   //默认200渠道
    var isOpenLog = true      //是否打开log，可以在jenkins配置
        private set
    var isAbUser = false       //是否打开本地ab
        private set
    var innerChannel = DEFAULT_CHANNEL      //内部渠道号
        private set
    var outerChannel = "" //买量渠道
        private set
    var applicationId = ""
        private set
    var svn = ""
        private set
    val DEFAULT_CONFIG_TYPE_1 = "style2"
    val DEFAULT_CONFIG_TYPE_3 = "style3"
    val DEFAULT_CONFIG_TYPE_6 = "style3"
    val DEFAULT_CONFIG_TYPE_8 = "style2"
    val DEFAULT_CONFIG_TYPE_10_11 = "style8"
    val DEFAULT_CONFIG_TYPE_15 = "style6"
    val DEFAULT_CONFIG_TYPE_14 = "style3"
    val DEFAULT_CONFIG_TYPE_16 = "style2"
//    private var mediaPlayer: MediaPlayer = MediaPlayer()

    private var mContext: Application? = null

    var userInfo: UserInfo? = null
        private set

    var tarotList = arrayListOf<TarotBean>()
        private set

    var tarotMap = hashMapOf<String, TarotBean>()

    var isFirstRunPayGuide: Boolean = false
        private set

    private val activityList = arrayListOf<Activity>()

    fun loadConfig(context: Application) {
        outerChannel =
            GoPrefManager.getDefault().getString(PreConstants.App.KEY_OUTER_CHANNEL_STR, "")
        try {
            val metaData = context.packageManager
                .getApplicationInfo(
                    context.packageName,
                    PackageManager.GET_META_DATA
                ).metaData

            applicationId = metaData.getString("applicationId")
            isOpenLog = metaData.getBoolean("openLog")
            val channel = metaData.get("channel").toString()
            innerChannel = channel ?: DEFAULT_CHANNEL
            isAbUser = metaData.getBoolean("AbUser")
            svn = metaData.get("svn").toString()
            Log.d(
                "GoCommonEnv",
                "openLog:$isOpenLog channel:$innerChannel applicationId:$applicationId isAbUser:$isAbUser"
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        userInfo = GoGson.fromJson(
            GoPrefManager.getDefault().getString(PreConstants.User.KEY_USER_INFO, ""),
            UserInfo::class.java
        )
        isFirstRunPayGuide = GoPrefManager.getDefault().getBoolean(PreConstants.First.KEY_FIRST_RUN, true)
//        val fd = context.resources.openRawResourceFd(R.raw.bgm)
//        val mAudioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
//        val maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
//        mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM, maxVolume, 0)
//        mediaPlayer.setDataSource(fd.fileDescriptor, fd.startOffset, fd.length)
//        mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM)
//        mediaPlayer.isLooping = true
//        mediaPlayer.prepare()
//        mediaPlayer.setVolume(1f, 1f)
        ThreadExecutorProxy.execute(Runnable {
            PayGuideManager.loadCacheConfig()
            val input = inputStream2String(context.assets.open("tarot.json"))
            val temp = GoGson.fromJson<List<TarotBean>>(
                input,
                object : TypeToken<List<TarotBean>>() {}.type
            )
            val cover = context.resources.obtainTypedArray(R.array.tarots_cover)
            val names = context.resources.obtainTypedArray(R.array.tarots_name)
            val keywords = context.resources.obtainTypedArray(R.array.tarots_keyword)
            if (temp != null) {
                repeat(temp.size) {
                    temp[it].cover = context.resources.getIdentifier(cover.getString(it), "mipmap", context.packageName)
                    temp[it].name = names.getString(it)
                    temp[it].keyword = keywords.getString(it)
                    tarotList.add(temp[it])
                    tarotMap.put(temp[it].card_key, temp[it])
                }
            }
            keywords.recycle()
            names.recycle()
            cover.recycle()
        })

        context.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
            override fun onActivityPaused(activity: Activity?) {
            }

            override fun onActivityResumed(activity: Activity?) {
            }

            override fun onActivityStarted(activity: Activity?) {
            }

            override fun onActivityDestroyed(activity: Activity?) {
                activityList.remove(activity)
            }

            override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
            }

            override fun onActivityStopped(activity: Activity?) {
            }

            override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
                if (activity != null) {
                    activityList.add(0, activity)
                }
            }
        }
        )
    }

    fun isTopActivity(activity: Activity, block: () -> Unit) {
        if (activityList.isNotEmpty() && activityList.firstOrNull() == activity) {
            block()
        }
    }

    private fun inputStream2String(inputStream: InputStream): String {
        val bufferSize = 1024
        val buffer = CharArray(bufferSize)
        val out = StringBuilder()
        val `in` = InputStreamReader(inputStream, "UTF-8")
        while (true) {
            val rsz = `in`.read(buffer, 0, buffer.size)
            if (rsz < 0)
                break
            out.append(buffer, 0, rsz)
        }
        return out.toString()
    }


    fun getApplication(): Application {
        return mContext!!
    }

    fun setApplication(application: Application) {
        mContext = application
    }


    fun startBGM() {
//        try {
//            if (!mediaPlayer.isPlaying) {
//                mediaPlayer.seekTo(0)
//                mediaPlayer.start()
//            }
//        } catch (e: java.lang.Exception) {
//            e.printStackTrace()
//        }
    }

    fun resumeBGM() {
//        try {
//            if (!mediaPlayer.isPlaying)
//                mediaPlayer.start()
//        } catch (e: java.lang.Exception) {
//            e.printStackTrace()
//        }
    }

    fun stopBGM() {
//        try {
//            mediaPlayer.pause()
//        } catch (e: java.lang.Exception) {
//            e.printStackTrace()
//        }
    }

    /**
     * 更新外部渠道名，如果已经有历史渠道，则不变化
     *
     * @param channelStr
     */
    fun storeOuterChannel(channelStr: String) {
        if (TextUtils.isEmpty(outerChannel)) {
            outerChannel = channelStr
            GoPrefManager.getDefault().putString(PreConstants.App.KEY_OUTER_CHANNEL_STR, channelStr)
                .apply()
        }
    }

    fun storeUserInfo(info: UserInfo?) {
        userInfo = info
        GoPrefManager.getDefault().putString(PreConstants.User.KEY_USER_INFO, GoGson.toJson(info))
            .apply()
    }

    fun storeFirstLauncher(first: Boolean) {
        isFirstRunPayGuide = first
        GoPrefManager.getDefault().putBoolean(PreConstants.First.KEY_FIRST_RUN, first).apply()
    }

    fun storePaymentGuideConfig(moduleConfig: ModuleConfig) {
        PayGuideManager.storePaymentGuideConfig(moduleConfig)
    }

    fun getPayStyle(type: String = "1"): String {
        if (type == "-1") {
            return ""
        }
        return PayGuideManager.getPayStyle(type)
    }
}

private var clickTime = 0L

fun <T : View> T.clickWithTrigger(block: (T) -> Unit) =
    setOnClickListener {
        if (System.currentTimeMillis() - clickTime > 600) {
            clickTime = System.currentTimeMillis()
            block(it as T)
        }
    }

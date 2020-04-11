package com.palmapp.master.baselib.manager

import android.text.TextUtils
import com.cpcphone.abtestcenter.AbtestCenterService
import com.cs.utils.net.request.THttpRequest
import com.google.gson.reflect.TypeToken
import com.palmapp.master.baselib.GoCommonEnv
import com.palmapp.master.baselib.bean.pay.ModuleConfig
import com.palmapp.master.baselib.bean.pay.PaymentGuideConfig
import com.palmapp.master.baselib.bean.pay.PaymentTypeConfig
import com.palmapp.master.baselib.buychannel.BuyChannelProxy
import com.palmapp.master.baselib.constants.AppConstants
import com.palmapp.master.baselib.constants.PreConstants
import com.palmapp.master.baselib.manager.config.LauncherConfig
import com.palmapp.master.baselib.proxy.AdSdkServiceProxy
import com.palmapp.master.baselib.utils.*
import org.json.JSONObject
import java.lang.Exception
import java.lang.IllegalArgumentException

/**
 *  订阅样式配置管理
 * @author :     xiemingrui
 * @since :      2019/10/10
 */
object PayGuideManager {
    const val FIRST_LAUNCHER = 1
    const val NOT_FIRST_LAUNCHER = 2

    //用来保存所有方案的样式《方案，《样式名称，具体样式》》
    var paymentGuideConfigs = hashMapOf<String, PaymentTypeConfig>()
    //用来保存启动页场景-样式关系
    var payGuideConfig = hashMapOf<Int, LauncherConfig>()

    var hasPayGuideConfig = false

    //目前只有启动页场景有多个样式
    fun mapForGuide(style: String?): String {
        return when (style) {
            "scheme1" -> "style1"
            "scheme2" -> "style2"
            "scheme3" -> "style3"
            "scheme4" -> "style4"
            "scheme5" -> "style5"
            "scheme7" -> "style7"
            "scheme9" -> "style9"
            else -> "style2"
        }
    }

    fun parsePayGuideConfig(obj: JSONObject): String? {
        var config: String? = null
        try {
            config = obj.getString("scheme")
        } catch (e: Exception) {
            e.printStackTrace();
            config = null;
        }
        return config
    }

    private fun cover2PaymentConfig(moduleConfig: ModuleConfig): HashMap<String, PaymentTypeConfig> {
        val result = HashMap<String, PaymentTypeConfig>()
        moduleConfig.contents.forEach { type ->
            //第一层，场景
            val paymentTypeConfig = PaymentTypeConfig()

            val constants = hashMapOf<String, PaymentGuideConfig>()
            type.contents.forEach { config ->
                //第二层，场景下具体样式
                val paymentGuideConfig = PaymentGuideConfig()
                paymentGuideConfig.type = config.name
                config.contents.forEach {
                    //第三层，样式下具体内容
                    when (it.name) {
                        "title" -> paymentGuideConfig.title = it.description
                        "title1" -> paymentGuideConfig.title1 = it.description
                        "title2" -> paymentGuideConfig.title2 = it.description
                        "main text" -> paymentGuideConfig.mainText = it.description
                        "video style" -> paymentGuideConfig.videoStyle = it.description
                        "main text1" -> paymentGuideConfig.mainText1 = it.description
                        "main text2" -> paymentGuideConfig.mainText2 = it.description
                        "option1" -> {
                            it.contents.forEach { it2 ->
                                when (it2.name) {
                                    "option text1" -> paymentGuideConfig.option1Text1 =
                                        it2.description
                                    "option text2" -> paymentGuideConfig.option1Text2 =
                                        it2.description
                                }
                            }
                            paymentGuideConfig.option1Sku = JSONObject(it.extra).getString("sku")
                        }
                        "state" -> paymentGuideConfig.state = it.description

                        "option2" -> {
                            it.contents.forEach { it2 ->
                                when (it2.name) {
                                    "option text1" -> paymentGuideConfig.option2Text1 =
                                        it2.description
                                    "option text2" -> paymentGuideConfig.option2Text2 =
                                        it2.description
                                    "tab" -> paymentGuideConfig.option2Tab =
                                        it2.description
                                }
                            }
                            paymentGuideConfig.option2Sku = JSONObject(it.extra).getString("sku")
                        }

                        "close button" -> {
                            val jsonObject = JSONObject(it.extra)
                            paymentGuideConfig.closeButtonAlpha =
                                jsonObject.getDouble("closebutton_transparence").toFloat()
                            paymentGuideConfig.closeButtonDelay =
                                jsonObject.getInt("closebutton_delay")
                        }

                        "video" -> {
                            paymentGuideConfig.video = it.banner
                            val task = VideoStreamWorkerTask()
                            task.execute(it.banner)
                        }

                        "video list" -> {
                            it.contents.forEach { it2 ->
                                paymentGuideConfig.videoList.add(it2.banner)
                                val task = VideoStreamWorkerTask()
                                task.execute(it2.banner)
                            }
                        }

                        "button1" -> {
                            it.contents.forEach { it2 ->
                                when (it2.name) {
                                    "button text1" -> paymentGuideConfig.button1 = it2.description
                                }
                            }
                            if (!TextUtils.isEmpty(it.extra))
                                paymentGuideConfig.button1Sku =
                                    JSONObject(it.extra).getString("sku")
                        }
                        "button2" -> {
                            it.contents.forEach { it2 ->
                                when (it2.name) {
                                    "button text1" -> paymentGuideConfig.button2 = it2.description
                                }
                            }
                            if (!TextUtils.isEmpty(it.extra))
                                paymentGuideConfig.button2Sku =
                                    JSONObject(it.extra).getString("sku")
                        }
                    }
                }
                constants.put(paymentGuideConfig.type, paymentGuideConfig)

            }

            paymentTypeConfig.contents = constants

            result.put(type.name, paymentTypeConfig)
        }

        return result
    }

    //保存订阅样式
    fun storePaymentGuideConfig(moduleConfig: ModuleConfig) {
        paymentGuideConfigs = cover2PaymentConfig(moduleConfig)
        GoPrefManager.getDefault()
            .putString(PreConstants.Pay.KEY_PAY_PAYMENT_CONFIG, GoGson.toJson(moduleConfig))
    }

    fun clearPaymentGuideConfig() {
        paymentGuideConfigs = hashMapOf<String, PaymentTypeConfig>()
        GoPrefManager.getDefault()
            .putString(PreConstants.Pay.KEY_PAY_PAYMENT_CONFIG, "")
    }

    //获取启动页场景的订阅样式名
    fun getPayStyle(type: String): String {
        val style = when (type) {
            "1", "2", "14", "16" -> {
                val isFirst = if (type == "1" || type == "14") FIRST_LAUNCHER else NOT_FIRST_LAUNCHER
                val plan_sign = payGuideConfig.get(isFirst)?.style
                val default_sign = when (type) {
                    "1" -> GoCommonEnv.DEFAULT_CONFIG_TYPE_1
                    "2" -> GoCommonEnv.DEFAULT_CONFIG_TYPE_1
                    "14" -> GoCommonEnv.DEFAULT_CONFIG_TYPE_14
                    "16" -> GoCommonEnv.DEFAULT_CONFIG_TYPE_16
                    else -> throw IllegalArgumentException()
                }
                if (TextUtils.isEmpty(plan_sign)) {
                    default_sign
                } else {
                    paymentGuideConfigs.get(type)?.contents?.get(plan_sign)?.type
                        ?: default_sign
                }
            }
            "15" -> {
                paymentGuideConfigs.get("15")?.contents?.values?.first()?.type
                    ?: GoCommonEnv.DEFAULT_CONFIG_TYPE_15
            }
            "3" -> paymentGuideConfigs.get("3")?.contents?.values?.first()?.type
                ?: GoCommonEnv.DEFAULT_CONFIG_TYPE_3
            "6" -> paymentGuideConfigs.get("6")?.contents?.values?.first()?.type
                ?: GoCommonEnv.DEFAULT_CONFIG_TYPE_6
            "8" -> paymentGuideConfigs.get("8")?.contents?.values?.first()?.type
                ?: GoCommonEnv.DEFAULT_CONFIG_TYPE_8
            "9" -> paymentGuideConfigs.get("9")?.contents?.values?.first()?.type
                ?: GoCommonEnv.DEFAULT_CONFIG_TYPE_3
            "10" -> paymentGuideConfigs.get("10")?.contents?.values?.first()?.type
                ?: GoCommonEnv.DEFAULT_CONFIG_TYPE_3
            "11" -> paymentGuideConfigs.get("11")?.contents?.values?.first()?.type
                ?: GoCommonEnv.DEFAULT_CONFIG_TYPE_3
            "12" -> paymentGuideConfigs.get("12")?.contents?.values?.first()?.type
                ?: GoCommonEnv.DEFAULT_CONFIG_TYPE_3
            "17" -> paymentGuideConfigs.get("17")?.contents?.values?.first()?.type
                ?: GoCommonEnv.DEFAULT_CONFIG_TYPE_3
            "13" -> paymentGuideConfigs.get("13")?.contents?.values?.first()?.type
                ?: GoCommonEnv.DEFAULT_CONFIG_TYPE_3
            else -> paymentGuideConfigs.get("0")?.contents?.values?.first()?.type
                ?: GoCommonEnv.DEFAULT_CONFIG_TYPE_1
        }

        return style
    }

    fun loadCacheConfig() {
        hasPayGuideConfig =
            GoPrefManager.getDefault().getBoolean(PreConstants.AbTest.KEY_HAS_ABTEST_VALUE, false)
        val cache = GoPrefManager.getDefault()
            .getString(PreConstants.AbTest.KEY_ABTEST_VALUE, "")
        val type = object : TypeToken<HashMap<Int, LauncherConfig>>() {}.type
        payGuideConfig = GoGson.fromJson<HashMap<Int, LauncherConfig>>(cache, type)
            ?: getDefaultLauncherConfig()
        if (payGuideConfig.isEmpty()) {
            payGuideConfig = getDefaultLauncherConfig()
        }
        val moduleConfig = GoGson.fromJson(
            GoPrefManager.getDefault().getString(PreConstants.Pay.KEY_PAY_PAYMENT_CONFIG, ""),
            ModuleConfig::class.java
        )
        moduleConfig?.let {
            paymentGuideConfigs = cover2PaymentConfig(it)
        }
    }

    private fun getDefaultLauncherConfig(): HashMap<Int, LauncherConfig> {
        val cache = HashMap<Int, LauncherConfig>()
        if (BuyChannelProxy.isBuyUser()) {
            cache.put(FIRST_LAUNCHER, LauncherConfig(GoCommonEnv.DEFAULT_CONFIG_TYPE_14, "1", "14"))
            cache.put(NOT_FIRST_LAUNCHER, LauncherConfig(GoCommonEnv.DEFAULT_CONFIG_TYPE_1, "0", "2"))
        } else {
            cache.put(FIRST_LAUNCHER, LauncherConfig(GoCommonEnv.DEFAULT_CONFIG_TYPE_1, "0", "1"))
            cache.put(NOT_FIRST_LAUNCHER, LauncherConfig(GoCommonEnv.DEFAULT_CONFIG_TYPE_1, "0", "2"))
        }
        return cache
    }
}
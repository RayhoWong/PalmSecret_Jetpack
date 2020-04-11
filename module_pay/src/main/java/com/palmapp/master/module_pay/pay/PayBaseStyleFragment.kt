package com.palmapp.master.module_pay.pay

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.alibaba.android.arouter.launcher.ARouter
import com.android.billingclient.api.BillingClient
import com.palmapp.master.baselib.bean.pay.PaymentGuideConfig
import com.palmapp.master.baselib.buychannel.BuyChannelProxy
import com.palmapp.master.baselib.constants.RouterConstants
import com.palmapp.master.baselib.manager.DebugProxy
import com.palmapp.master.baselib.manager.PayGuideManager
import com.palmapp.master.baselib.proxy.OnBillingCallBack
import com.palmapp.master.module_pay.R
import java.lang.IllegalArgumentException

/**
 *
 * @author :     xiemingrui
 * @since :      2019/8/21
 */
abstract class PayBaseStyleFragment : Fragment(), OnBillingCallBack {
    abstract fun getResLayout(): Int
    abstract fun getDefaultConfig(): PaymentGuideConfig?
    abstract fun initView()
    abstract fun getStyle(): String
    var entrance: String? = ""
    var type: String? = ""
    var mClickTimes = 0
    var paymentGuideConfig: PaymentGuideConfig? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        entrance = arguments?.getString("entrance")
        type = arguments?.getString("type")
        paymentGuideConfig = getPayGuideConfig(entrance)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(getResLayout(), container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun getStyle3Scen(): String {
        val campaign = BuyChannelProxy.getBuyChannelBean().campaign.replace("palmsecret", "").toLowerCase()
        return when {
            campaign.contains("old") -> {
                "6"
            }
            campaign.contains("baby") -> {
                "9"
            }
            campaign.contains("heart") || campaign.contains("heartrate") -> {
                "12"
            }
            campaign.contains("art") -> {
                "11"
            }
            else -> {
                "3"
            }
        }
    }

    fun getPayGuideConfig(type: String?): PaymentGuideConfig? {
        return when (type) {
            "1", "2" -> {
                if (PayGuideManager.payGuideConfig.isEmpty()) {
                    return getDefaultConfig()
                }
                PayGuideManager.paymentGuideConfigs.get(type)?.contents?.get(getStyle())
                    ?: getDefaultConfig()
            }
            "14", "16" -> {
                if (PayGuideManager.payGuideConfig.isEmpty()) {
                    return getDefaultConfig()
                }
                if (getStyle() == "style3") {
                    PayGuideManager.paymentGuideConfigs.get(getStyle3Scen())?.contents?.get(getStyle())
                        ?: getDefaultConfig()
                } else {
                    PayGuideManager.paymentGuideConfigs.get(type)?.contents?.get(getStyle())
                        ?: getDefaultConfig()
                }
            }
            "3", "6", "9", "10", "11" ,"12","13","17"-> {
                PayGuideManager.paymentGuideConfigs.get(type)?.contents?.values?.first()
                    ?: getDefaultConfig()
            }
            else -> PayGuideManager.paymentGuideConfigs.get("0")?.contents?.values?.first()
                ?: getDefaultConfig()
        }
    }


    override fun onPayError(code: Int) {
        val toast = when (code) {
            BillingClient.BillingResponse.BILLING_UNAVAILABLE -> "没有谷歌服务"
            BillingClient.BillingResponse.SERVICE_DISCONNECTED -> "GP连接不上"
            BillingClient.BillingResponse.SERVICE_UNAVAILABLE -> "没有网络"
            BillingClient.BillingResponse.USER_CANCELED -> "用户自己关了"
            BillingClient.BillingResponse.ITEM_UNAVAILABLE -> "该商品不可用"
            BillingClient.BillingResponse.DEVELOPER_ERROR -> "GP KEY ERROR"
            BillingClient.BillingResponse.ITEM_ALREADY_OWNED -> "这个商品你都有了"
            else -> ""
        }

        if (DebugProxy.isOpenLog() && toast.isNotEmpty() && activity != null) {
            Toast.makeText(activity, toast, Toast.LENGTH_LONG).show()
        }

        if (activity != null && code != BillingClient.BillingResponse.USER_CANCELED) {
            Toast.makeText(
                activity,
                activity?.getString(R.string.failed_to_subscribe),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onPaySuccess(sku: String) {
        if (activity != null) {
            Toast.makeText(
                activity,
                activity?.getString(R.string.success_to_subscribe),
                Toast.LENGTH_LONG
            ).show()
        }
        activity?.let {
            if (it is PayStyleActivity) {
                it.realFinish()
            }
        }

    }

    //是否禁用返回键
    abstract fun isNeedToBlockBack(): Boolean

    //是否为启动场景
    fun isLauncherScen(): Boolean {
        return when (entrance) {
            "1", "2", "8", "14", "15", "16" -> true
            else -> false
        }
    }
}
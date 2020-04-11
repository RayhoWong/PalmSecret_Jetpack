package com.palmapp.master.baselib.proxy

import android.app.Activity
import android.content.Context
import android.widget.Toast
import com.palmapp.master.baselib.R
import com.palmapp.master.baselib.constants.RouterConstants
import com.palmapp.master.baselib.manager.DebugProxy
import com.palmapp.master.baselib.manager.RouterServiceManager

/**
 *
 * @author :     xiemingrui
 * @since :      2019/8/19
 */
object BillingServiceProxy : IBillingService {
    override fun startPay(
        activity: Activity,
        sku: String,
        entrance: String,
        style: String,
        callBack: OnBillingCallBack
    ) {
        iBillingService?.startPay(activity, sku, entrance,  style, callBack)
    }

    private var iBillingService: IBillingService? =
        RouterServiceManager.providerService(RouterConstants.SERVICE_PAY)

    override fun isVip(): Boolean {
        return iBillingService?.isVip() ?: true
    }

    override fun queryVip() {
        iBillingService?.queryVip()
    }

    override fun init(context: Context?) {

    }

    fun handlePayFailed(activity: Activity, code: Int) {
        val toast = when (code) {
            3 -> "没有谷歌服务"
            -1 -> "GP连接不上"
            2 -> "没有网络"
            1 -> "用户自己关了"
            4 -> "该商品不可用"
            5 -> "GP KEY ERROR"
            7 -> "这个商品你都有了"
            else -> ""
        }

        if (DebugProxy.isOpenLog() && toast.isNotEmpty()) {
            Toast.makeText(activity, toast, Toast.LENGTH_LONG).show()
        }
        Toast.makeText(
            activity,
            activity.getString(R.string.failed_to_subscribe),
            Toast.LENGTH_LONG
        ).show()
    }

    fun handlePaySuccess(activity: Activity) {
        Toast.makeText(
            activity,
            activity.getString(R.string.success_to_subscribe),
            Toast.LENGTH_LONG
        ).show()
    }
}
package com.palmapp.master.module_pay

import android.app.Activity
import android.content.Context
import com.alibaba.android.arouter.facade.annotation.Route
import com.palmapp.master.baselib.constants.RouterConstants
import com.palmapp.master.baselib.proxy.IBillingService
import com.palmapp.master.baselib.proxy.OnBillingCallBack

/**
 *
 * @author :     xiemingrui
 * @since :      2019/8/19
 */
@Route(path = RouterConstants.SERVICE_PAY)
class BillingServiceImpl : IBillingService {
    override fun startPay(
        activity: Activity,
        sku: String,
        entrance: String,
        style:String,
        callBack: OnBillingCallBack
    ) {
        BillingManager.startPay(activity, sku, entrance, style, callBack,0)
    }

    override fun isVip(): Boolean {
        return BillingManager.isVip()
    }

    override fun queryVip() {
        BillingManager.queryVip()
    }

    override fun init(context: Context?) {
    }

}
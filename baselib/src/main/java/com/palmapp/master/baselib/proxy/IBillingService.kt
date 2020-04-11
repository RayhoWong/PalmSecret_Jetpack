package com.palmapp.master.baselib.proxy

import android.app.Activity
import com.alibaba.android.arouter.facade.template.IProvider

/**
 *
 * @author :     xiemingrui
 * @since :      2019/8/19
 */
interface IBillingService : IProvider {
    //是否为订阅用户
    fun isVip(): Boolean

    //查询订阅状态
    fun queryVip()

    //开始订阅
    fun startPay(activity: Activity,
                 sku: String,
                 entrance: String,
                 style:String,
                 callBack: OnBillingCallBack)

}
package com.palmapp.master.baselib.proxy

import android.app.Activity
import android.content.Context
import com.alibaba.android.arouter.facade.template.IProvider

/**
 *
 * @author :     xiemingrui
 * @since :      2019/8/5
 */
interface IPayService:IProvider{
    //判断google支付初始化是否完成
    fun isGooglePaySetup():Boolean
    //开始订阅页
    fun startSubsActivity(activity: Activity)
    //判断是否订阅
    fun isSubscribe():Boolean
}
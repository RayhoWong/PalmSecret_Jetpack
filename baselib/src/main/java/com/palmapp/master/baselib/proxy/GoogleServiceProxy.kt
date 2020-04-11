package com.palmapp.master.baselib.proxy

import android.content.Context
import com.palmapp.master.baselib.constants.RouterConstants
import com.palmapp.master.baselib.manager.RouterServiceManager

/**
 *google服务相关代理类
 * @author :     xiemingrui
 * @since :      2019/8/1
 */
object GoogleServiceProxy : IGoogleService {
    private val iGoogleService: IGoogleService? = RouterServiceManager.providerService(RouterConstants.SERVICE_GMS)
    override fun init(context: Context?) {

    }

    //获取google id，注意首次次获取需要在非主线程
    override fun getGoogleAdvertisingId(): String {
        return iGoogleService?.getGoogleAdvertisingId() ?: "UNABLE-TO-RETRIEVE"
    }
}
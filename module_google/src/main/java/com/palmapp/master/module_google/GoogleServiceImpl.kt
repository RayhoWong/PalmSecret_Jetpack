package com.palmapp.master.module_google

import android.content.Context
import android.os.Looper
import android.text.TextUtils
import com.alibaba.android.arouter.facade.annotation.Route
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.palmapp.master.baselib.GoCommonEnv
import com.palmapp.master.baselib.constants.RouterConstants
import com.palmapp.master.baselib.proxy.IGoogleService

/**
 *
 * @author :     xiemingrui
 * @since :      2019/8/1
 */
@Route(path = RouterConstants.SERVICE_GMS)
class GoogleServiceImpl : IGoogleService {
    private var googleId = ""
    override fun getGoogleAdvertisingId(): String {

        if (!TextUtils.isEmpty(googleId)) {
            return googleId
        }
        if (Looper.getMainLooper().thread == Thread.currentThread()) {
            return "UNABLE-TO-RETRIEVE"
        }
        var adInfo: AdvertisingIdClient.Info? = null
        try {
            adInfo = AdvertisingIdClient.getAdvertisingIdInfo(GoCommonEnv.getApplication())
        } catch (t: Throwable) {
            t.printStackTrace()
        }
        if (adInfo != null) {
            googleId = adInfo.id
            return googleId
        } else {
            return "UNABLE-TO-RETRIEVE"
        }
    }

    override fun init(context: Context?) {

    }

}
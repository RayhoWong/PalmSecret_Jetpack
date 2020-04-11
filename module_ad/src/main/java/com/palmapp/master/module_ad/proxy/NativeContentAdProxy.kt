package com.palmapp.master.module_ad.proxy

import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.formats.NativeAppInstallAd
import com.google.android.gms.ads.formats.NativeContentAd

/**
 *  admob native广告代理类
 * @author :     xiemingrui
 * @since :      2019/8/6
 */
class NativeContentAdProxy(val ad: NativeContentAd) : IAd {
    override fun getAd(): Any  = ad

    override fun show() {

    }

    override fun destroy() {
    }

    override fun isLoadSuccess(): Boolean {
        return ad != null
    }

    fun getAdView(): NativeContentAd = ad
}
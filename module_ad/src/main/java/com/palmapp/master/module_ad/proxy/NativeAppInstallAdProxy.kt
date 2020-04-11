package com.palmapp.master.module_ad.proxy

import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.formats.NativeAppInstallAd

/**
 *  admob native广告代理类
 * @author :     xiemingrui
 * @since :      2019/8/6
 */
class NativeAppInstallAdProxy(val ad: NativeAppInstallAd) : IAd {
    override fun getAd(): Any  = ad

    override fun show() {
    }

    override fun destroy() {
    }

    override fun isLoadSuccess(): Boolean {
        return ad != null
    }

    fun getAdView(): NativeAppInstallAd = ad
}
package com.palmapp.master.module_ad.proxy

import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.InterstitialAd

/**
 *  admob Banner广告代理类
 * @author :     xiemingrui
 * @since :      2019/8/6
 */
class BannerAdProxy(val ad: AdView) : IAd {
    override fun getAd(): Any  = ad

    override fun show() {

    }

    override fun destroy() {
    }

    override fun isLoadSuccess(): Boolean {
        return ad != null
    }

    fun getAdView(): AdView = ad
}
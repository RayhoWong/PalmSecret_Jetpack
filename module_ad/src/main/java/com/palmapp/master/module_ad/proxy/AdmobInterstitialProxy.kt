package com.palmapp.master.module_ad.proxy

import com.google.android.gms.ads.InterstitialAd

/**
 *  admob插屏广告代理类
 * @author :     xiemingrui
 * @since :      2019/8/6
 */
class AdmobInterstitialProxy(val ad: InterstitialAd) : IAd {
    override fun getAd(): Any = ad

    override fun show() {
        ad.show()
    }

    override fun destroy() {

    }

    override fun isLoadSuccess(): Boolean {
        return ad.isLoaded
    }

}
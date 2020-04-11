package com.palmapp.master.module_ad.proxy

import com.applovin.nativeAds.AppLovinNativeAd

class AppLovinNativeAdProxy (val ad:AppLovinNativeAd):IAd{
    override fun show() {
    }

    override fun destroy() {

    }

    override fun isLoadSuccess(): Boolean=ad!=null

    override fun getAd(): Any =ad

}
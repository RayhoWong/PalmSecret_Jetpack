package com.palmapp.master.module_ad.proxy

import com.applovin.adview.AppLovinAdView

class AppLovinBannerAdProxy (val ad: AppLovinAdView):IAd{
    override fun show() {
    }

    override fun destroy() {
    }

    override fun isLoadSuccess(): Boolean = ad!=null

    override fun getAd(): Any =ad
}
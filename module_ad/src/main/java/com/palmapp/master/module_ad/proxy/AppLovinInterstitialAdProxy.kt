package com.palmapp.master.module_ad.proxy

import com.applovin.adview.AppLovinInterstitialAd
import com.applovin.adview.AppLovinInterstitialAdDialog
import com.applovin.sdk.AppLovinAd
import com.applovin.sdk.AppLovinSdk
import com.palmapp.master.baselib.GoCommonEnv

class AppLovinInterstitialAdProxy (val ad: AppLovinInterstitialAdDialog) :IAd{
    var appLovinAd:AppLovinAd?=null
    override fun show() {
        ad.showAndRender(appLovinAd)
    }


    override fun destroy() {
    }

    override fun isLoadSuccess()=ad!=null

    override fun getAd(): Any =ad

}
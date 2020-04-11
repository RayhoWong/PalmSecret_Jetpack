package com.palmapp.master.module_ad.proxy

import com.applovin.adview.AppLovinIncentivizedInterstitial
import com.applovin.adview.AppLovinInterstitialAd
import com.applovin.adview.AppLovinInterstitialAdDialog
import com.applovin.sdk.*
import com.cs.bd.ad.params.OuterAdLoader
import com.palmapp.master.baselib.GoCommonEnv

class AppLovinRewardAdProxy (val ad: AppLovinIncentivizedInterstitial) :IAd,AppLovinAdRewardListener,AppLovinAdVideoPlaybackListener,AppLovinAdDisplayListener,AppLovinAdClickListener{
    override fun userRewardVerified(ad: AppLovinAd?, response: MutableMap<String, String>?) {
    }

    override fun userOverQuota(ad: AppLovinAd?, response: MutableMap<String, String>?) {
    }

    override fun validationRequestFailed(ad: AppLovinAd?, errorCode: Int) {
    }

    override fun userDeclinedToViewAd(ad: AppLovinAd?) {
    }

    override fun userRewardRejected(ad: AppLovinAd?, response: MutableMap<String, String>?) {
    }

    override fun videoPlaybackEnded(ad: AppLovinAd?, percentViewed: Double, fullyWatched: Boolean) {
        outerSdkAdSourceListener?.onAdClosed("1")
    }

    override fun videoPlaybackBegan(ad: AppLovinAd?) {
    }

    override fun adHidden(ad: AppLovinAd?) {
        outerSdkAdSourceListener?.onAdClosed("0")
    }

    override fun adDisplayed(ad: AppLovinAd?) {
        outerSdkAdSourceListener?.onAdShowed(this)
    }

    override fun adClicked(ad: AppLovinAd?) {
        outerSdkAdSourceListener?.onAdClicked(this)
    }
    var outerSdkAdSourceListener:OuterAdLoader.OuterSdkAdSourceListener? = null
    var appLovinAd:AppLovinAd?=null
    override fun show() {
        ad.show(appLovinAd,GoCommonEnv.getApplication(),this,this,this,this)
    }


    override fun destroy() {
        outerSdkAdSourceListener = null
    }

    override fun isLoadSuccess()=ad.isAdReadyToDisplay

    override fun getAd(): Any =ad

}
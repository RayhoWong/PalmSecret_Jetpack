package com.palmapp.master.module_ad.proxy

import com.applovin.adview.AppLovinAdView
import com.applovin.adview.AppLovinIncentivizedInterstitial
import com.applovin.adview.AppLovinInterstitialAdDialog
import com.applovin.sdk.AppLovinAd
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.formats.NativeAppInstallAd
import com.google.android.gms.ads.formats.NativeAppInstallAdView
import com.google.android.gms.ads.formats.NativeContentAd
import com.google.android.gms.ads.reward.RewardedVideoAd
import java.lang.NullPointerException

/**
 *
 * @author :     xiemingrui
 * @since :      2019/8/6
 */
object AdFactory {
    fun createAdProxy(ad: Any?): IAd? {
        if (ad is InterstitialAd) {
            return AdmobInterstitialProxy(ad)
        }
        if (ad is RewardedVideoAd) {
            return RewardedVideoAdProxy(ad)
        }
        if (ad is AdView) {
            return BannerAdProxy(ad)
        }
        if (ad is NativeAppInstallAd){
            return NativeAppInstallAdProxy(ad)
        }
        if (ad is NativeContentAd){
            return NativeContentAdProxy(ad)
        }
        if(ad is AppLovinAdView){
            return AppLovinBannerAdProxy(ad)
        }
        if(ad is AppLovinInterstitialAdProxy){
            return ad
        }
        if(ad is AppLovinRewardAdProxy){
            return ad
        }

        if (ad == null) {
            return null
        }
        throw NullPointerException("AdFactory.createAdProxy is null,ad type is${ad::class.java}")
    }
}
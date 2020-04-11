package com.palmapp.master.baselib.proxy

import android.app.Activity
import android.content.Context
import com.palmapp.master.baselib.constants.RouterConstants
import com.palmapp.master.baselib.manager.RouterServiceManager
import com.palmapp.master.baselib.manager.config.AdBean

/**
 *
 * @author :     xiemingrui
 * @since :      2019/8/19
 */
object AdSdkServiceProxy : IAdSdkService {
    override fun isRewardAdLoad(): Boolean {
        return iAdSdkService?.isRewardAdLoad()?:true
    }

    override fun loadRewardAd(activity: Activity, listener: OnLoadAdRewardListener) {
        iAdSdkService?.loadRewardAd(activity,listener)
    }

    override fun showRewardAd() {
        iAdSdkService?.showRewardAd()
    }

    override fun getRewardAdConfig(): AdBean? {
        return iAdSdkService?.getRewardAdConfig()
    }

    override fun showLauncherAd() {
        iAdSdkService?.showLauncherAd()
    }

    override fun showResultAd() {
        iAdSdkService?.showResultAd()
    }

    private val iAdSdkService: IAdSdkService? = RouterServiceManager.providerService(RouterConstants.SERVICE_AD)
    override fun getCDays(context: Context): Int {
        return iAdSdkService?.getCDays(context) ?: 1
    }
}
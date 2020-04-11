package com.palmapp.master.module_ad

import android.app.Activity
import android.content.Context
import com.alibaba.android.arouter.facade.annotation.Route
import com.cs.bd.ad.AdSdkApi
import com.palmapp.master.baselib.GoCommonEnv
import com.palmapp.master.baselib.constants.RouterConstants
import com.palmapp.master.baselib.manager.config.AdBean
import com.palmapp.master.baselib.proxy.IAdSdkService
import com.palmapp.master.baselib.proxy.OnLoadAdRewardListener
import com.palmapp.master.baselib.utils.AppUtil
import com.palmapp.master.module_ad.manager.RewardAdManager

/**
 *
 * @author :     xiemingrui
 * @since :      2019/8/19
 */
@Route(path = RouterConstants.SERVICE_AD)
class AdServiceImpl : IAdSdkService {
    override fun isRewardAdLoad(): Boolean {
        return RewardAdManager.isLoadAd()
    }

    override fun getRewardAdConfig(): AdBean? {
        return RewardAdManager.getConfig()
    }

    override fun showRewardAd() {
        RewardAdManager.showAd()
    }

    override fun loadRewardAd(activity: Activity, listener: OnLoadAdRewardListener) {
        RewardAdManager.loadAd(activity, listener)
    }

    override fun showLauncherAd() {
    }

    override fun showResultAd() {
    }

    override fun getCDays(context: Context): Int {
        return AdSdkApi.calculateCDays(
            context,
            AppUtil.getInstalledTime(context, GoCommonEnv.applicationId)
        )
    }
}
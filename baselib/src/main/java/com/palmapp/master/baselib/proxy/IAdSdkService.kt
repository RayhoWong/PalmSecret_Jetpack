package com.palmapp.master.baselib.proxy

import android.app.Activity
import android.content.Context
import com.alibaba.android.arouter.facade.template.IProvider
import com.palmapp.master.baselib.manager.config.AdBean

/**
 *
 * @author :     xiemingrui
 * @since :      2019/8/19
 */
interface IAdSdkService : IProvider {
    override fun init(context: Context?) {
    }

    fun getCDays(context: Context): Int

    //展示结果页广告
    fun showResultAd()

    //展示启动页广告
    fun showLauncherAd()

    fun loadRewardAd(activity: Activity, listener: OnLoadAdRewardListener)

    fun showRewardAd()

    fun getRewardAdConfig():AdBean?

    fun isRewardAdLoad():Boolean

}
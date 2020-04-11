package com.palmapp.master.module_ad.manager

import android.app.Activity
import android.text.TextUtils
import com.cs.bd.ad.http.bean.BaseModuleDataItemBean
import com.cs.bd.ad.manager.AdSdkManager
import com.cs.bd.utils.ToastUtils
import com.palmapp.master.baselib.GoCommonEnv
import com.palmapp.master.baselib.constants.PreConstants
import com.palmapp.master.baselib.manager.ForegroundManager
import com.palmapp.master.baselib.manager.GoPrefManager
import com.palmapp.master.baselib.manager.ThreadExecutorProxy
import com.palmapp.master.baselib.manager.config.AdBean
import com.palmapp.master.baselib.manager.config.AdConfig
import com.palmapp.master.baselib.manager.config.ConfigManager
import com.palmapp.master.baselib.manager.config.MODEL_REWARD_ID
import com.palmapp.master.baselib.proxy.BillingServiceProxy
import com.palmapp.master.baselib.proxy.OnLoadAdRewardListener
import com.palmapp.master.module_ad.R
import com.palmapp.master.module_ad.proxy.AdProxy
import com.palmapp.master.module_ad.proxy.IAd
import java.lang.ref.WeakReference

/**
 *
 * @author :     xiemingrui
 * @since :      2020/1/2
 */
private val DEFAULT_CONFIG = AdBean(MODEL_REWARD_ID, "1", 1, 0)

object RewardAdManager : AbsAdManager() {
    private var mLoading = false
    private val adProxy = AdProxy()
    private var listener: WeakReference<OnLoadAdRewardListener?>? = null
    override fun isLoad(baseModuleDataItemBean: BaseModuleDataItemBean): Boolean {
        return TextUtils.equals(getConfig().switch, "1")
    }

    fun isLoadAd() = TextUtils.equals(getConfig().switch, "1")

    override fun onAdLoadSuccess(adObj: Any?) {
        mLoading = false
        adProxy.setAdObject(adObj)
        listener?.get()?.onLoadSuccess()
    }

    override fun onAdShow(adObj: Any?) {

    }

    override fun onAdClick(adObj: Any?) {
    }

    override fun onAdClose(adObj: Any?) {
        if (adObj == "1") {
            onAdVideoPlayFinish(adObj)
        }
    }

    override fun onAdLoadFail(statusCode: Int) {
        ThreadExecutorProxy.runOnMainThread(Runnable {
            ToastUtils.makeEventToast(GoCommonEnv.getApplication(), GoCommonEnv.getApplication().getString(R.string.reward_video_error), true)
        })
        mLoading = false
        listener?.get()?.onLoadFail()
    }

    override fun onAdVideoPlayFinish(adObj: Any?) {
        listener?.get()?.onVideoFinish()
    }

    fun loadAd(activity: Activity, onLoadAdRewardListener: OnLoadAdRewardListener) {
        listener = WeakReference(onLoadAdRewardListener)
        if (mLoading) {
            return
        }
        if (adProxy.isLoadSuccess()) {
            listener?.get()?.onLoadSuccess()
            return
        }
        listener?.get()?.onLoading()
        mLoading = true
        super.loadAd(MODEL_REWARD_ID, activity)
    }

    fun showAd() {
        if (adProxy.isLoadSuccess()) {
            ForegroundManager.runOnForeground {
                adProxy.show()
            }
        }
    }

    fun getConfig(): AdBean {
        var adBean: AdBean =
            ConfigManager.getConfig(AdConfig::class.java)?.hashMap?.get(MODEL_REWARD_ID)
                ?: return DEFAULT_CONFIG
        return adBean
    }

    fun isLoadSuccess(): Boolean = adProxy.isLoadSuccess()

    fun destroy() {
        listener = null
    }

    fun isFreeUse(): Boolean {
        val space = getConfig().show_space
        val times = GoPrefManager.getDefault().getInt(PreConstants.App.KEY_ABTEST_FREE_USE, 0)
        return space == 1 && times <= 3
    }

    fun addFreeUse() {
        if (getConfig().show_space != 1) {
            return
        }
        if (BillingServiceProxy.isVip()) {
            return
        }
        var times = GoPrefManager.getDefault().getInt(PreConstants.App.KEY_ABTEST_FREE_USE, 0)
        times++
        GoPrefManager.getDefault().putInt(PreConstants.App.KEY_ABTEST_FREE_USE, times).commit()
    }
}
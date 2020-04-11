package com.palmapp.master.module_ad.manager

import android.app.Activity
import android.text.TextUtils
import android.util.SparseArray
import androidx.core.util.contains
import com.applovin.adview.AppLovinAdView
import com.cs.bd.ad.http.bean.BaseModuleDataItemBean
import com.cs.bd.commerce.util.LogUtils
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.formats.NativeAppInstallAd
import com.google.android.gms.ads.formats.NativeContentAd
import com.palmapp.master.baselib.Product
import com.palmapp.master.baselib.constants.PreConstants
import com.palmapp.master.baselib.manager.GoPrefManager
import com.palmapp.master.baselib.manager.config.*
import com.palmapp.master.baselib.utils.LogUtil
import com.palmapp.master.module_ad.proxy.*
import java.lang.ref.WeakReference
import java.util.*
import kotlin.collections.ArrayList

/**
 *  功能TAB底部广告
 * @author :     xiemingrui
 * @since :      2019/8/12
 */
private val DEFAULT_CONFIG = AdBean(MODEL_FUNCTION_ID, "1", 999, 0)

object TabBottomAdManager : AbsAdManager() {
    private var mTab: Int = 0
    private val TAG = "TabBottomAdManager"
    private var mLoading = false
    private val adProxy = AdProxy()
    private val listener: SparseArray<OnAdListener> = SparseArray()
    override fun isLoad(baseModuleDataItemBean: BaseModuleDataItemBean): Boolean {
        val config = getConfig()
        val isOpenAd = TextUtils.equals(config.switch, "1")
        val adShowCount = config.show_count//每天展示次数
        val adSplitTIme = config.show_space//广告展示时间间隔,min

        val currentTime = System.currentTimeMillis()


        val lastAdShowTime = GoPrefManager.getDefault()
            .getLong(PreConstants.AD.KEY_TAB_AD_LAST_SHOW_TIME, 0)//上次展示时间
        var adShowTimes =
            GoPrefManager.getDefault().getLong(PreConstants.AD.KEY_TAB_AD_SHOW_TIMES, 0)//今天展示次数
        //step0判断是否打开开关
        if (!isOpenAd) {
            LogUtil.i("TabBottomAdManager", "TabBottomAdManager 不满足开关")
            return false
        }
        //step1判断上次展示时机是否为今天
        val currentDay = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = lastAdShowTime
        val lastDay = if (lastAdShowTime == 0L) {
            0
        } else {
            calendar.get(Calendar.DAY_OF_YEAR)
        }
        if (currentDay != lastDay) {
            adShowTimes = 0
            GoPrefManager.getDefault().putLong(PreConstants.AD.KEY_TAB_AD_SHOW_TIMES, 0).apply()
        }
        //step2判断每天展示次数是否超过上限
        if (adShowTimes >= adShowCount) {
            LogUtil.i("TabBottomAdManager", "TabBottomAdManager 不满足展示次数")
            return false
        }
        //step3判断间隔时间
        if (currentTime - lastAdShowTime < adSplitTIme * 60 * 1000) {
            LogUtil.i("TabBottomAdManager", "TabBottomAdManager 不满足间隔时间")
            return false
        }
        return true
    }

    fun destroy() {
        listener.clear()
    }

    override fun onAdLoadSuccess(adObj: Any?) {
        mLoading = false
        adProxy.setAdObject(adObj)
        onAdShow()
    }

    private fun onAdShow() {
        if (adProxy.isLoadSuccess() && listener.get(mTab) != null) {
            when {
                adProxy.mAdObject is BannerAdProxy -> {
                    LogUtils.d(TAG, "banner加载成功 banner view不为null")
                    listener.get(mTab)
                        ?.getBannerAdView((adProxy.mAdObject as BannerAdProxy).getAdView())

                }
                adProxy.mAdObject is NativeAppInstallAdProxy -> {
                    LogUtils.d(TAG, "NativeAppInstallAd加载成功 NativeAppInstallAd不为null")
                    listener.get(mTab)
                        ?.getNativeAppInstallAd((adProxy.mAdObject as NativeAppInstallAdProxy).getAdView())

                }
                adProxy.mAdObject is NativeContentAdProxy -> {
                    LogUtils.d(TAG, "NativeContentAd加载成功 NativeContentAd不为null")
                    listener.get(mTab)
                        ?.getNativeContentAd((adProxy.mAdObject as NativeContentAdProxy).getAdView())

                }
                adProxy.mAdObject is AppLovinBannerAdProxy -> {
                    LogUtils.d(TAG, "AppLovinAdView加载成功 AppLovinAdView不为null")
                    listener.get(mTab)
                        ?.getAppLovinAdView((adProxy.mAdObject as AppLovinBannerAdProxy).ad)

                }
            }
            adProxy.show()
            super.onAdShowed(adProxy)
        } else {
            LogUtils.d(TAG, "广告加载失败")
        }
    }

    override fun onAdShow(adObj: Any?) {
        GoPrefManager.getDefault()
            .putLong(PreConstants.AD.KEY_TAB_AD_LAST_SHOW_TIME, System.currentTimeMillis())
            .apply()
        var currentAdShowCount =
            GoPrefManager.getDefault().getInt(PreConstants.AD.KEY_TAB_AD_SHOW_TIMES, 0)
        currentAdShowCount++
        GoPrefManager.getDefault()
            .putInt(PreConstants.AD.KEY_TAB_AD_SHOW_TIMES, currentAdShowCount)
            .apply()
    }

    override fun onAdClick(adObj: Any?) {
    }

    override fun onAdClose(adObj: Any?) {
    }

    override fun onAdLoadFail(statusCode: Int) {
        mLoading = false
    }

    override fun onAdVideoPlayFinish(adObj: Any?) {

    }

    fun preLoad(activity: Activity, tab: Int) {
        mTab = tab
        if (mLoading) {
            return
        }
        if (adProxy.isLoadSuccess()) {
            onAdShow()
            return
        }
        mLoading = true
        super.loadAd(MODEL_FUNCTION_ID, activity)
    }

    fun releaseTab(tab:Int){
        listener.remove(tab)
    }

    fun addAdListener(tab: Int, listener: OnAdListener) {
        this.listener.put(tab, listener)
        if (adProxy.isLoadSuccess()) {
            onAdShow()
            return
        }
    }


    fun getConfig(): AdBean {
        var adBean =
            ConfigManager.getConfig(AdConfig::class.java)?.hashMap?.get(MODEL_FUNCTION_ID)
                ?: return DEFAULT_CONFIG
        return adBean
    }


    interface OnAdListener {
        fun getBannerAdView(adView: AdView)
        fun getNativeAppInstallAd(ad: NativeAppInstallAd)
        fun getNativeContentAd(ad: NativeContentAd)
        fun getAppLovinAdView(appLovinAdView: AppLovinAdView)
    }
}
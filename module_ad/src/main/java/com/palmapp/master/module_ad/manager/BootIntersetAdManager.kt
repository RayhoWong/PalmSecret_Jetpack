package com.palmapp.master.module_ad.manager

import android.app.Activity
import android.text.TextUtils
import com.cs.bd.ad.http.bean.BaseModuleDataItemBean
import com.palmapp.master.baselib.Product
import com.palmapp.master.baselib.constants.PreConstants
import com.palmapp.master.baselib.manager.GoPrefManager
import com.palmapp.master.baselib.manager.ThreadExecutorProxy
import com.palmapp.master.baselib.manager.config.*
import com.palmapp.master.baselib.utils.LogUtil
import com.palmapp.master.module_ad.proxy.AdProxy
import java.util.*

/**
 *  启动插屏广告
 * @author :     xiemingrui
 * @since :      2019/8/12
 */
private val DEFAULT_CONFIG = AdBean(MODEL_LAUNCHER_ID, "1", 999, 0)

object BootIntersetAdManager : AbsAdManager() {
    private var mLoading = false
    private val adProxy = AdProxy()
    override fun isLoad(baseModuleDataItemBean: BaseModuleDataItemBean): Boolean {
        val config = getConfig()
        val isOpenAd = TextUtils.equals(config.switch, "1")
        val adShowCount = config.show_count//每天展示次数
        val adSplitTIme = config.show_space//广告展示时间间隔,min

        val currentTime = System.currentTimeMillis()


        val lastAdShowTime = GoPrefManager.getDefault()
            .getLong(PreConstants.AD.KEY_BOOT_AD_LAST_SHOW_TIME, 0)//上次展示时间
        var adShowTimes =
            GoPrefManager.getDefault().getLong(PreConstants.AD.KEY_BOOT_AD_SHOW_TIMES, 0)//今天展示次数
        //step0判断是否打开开关
        if (!isOpenAd) {
            LogUtil.i("BootIntersetAdManager", "BootIntersetAdManager 不满足开关")
            return false
        }
        //step1判断上次展示时机是否为今天
        val currentDay = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = lastAdShowTime
        val lastDay = calendar.get(Calendar.DAY_OF_YEAR)
        if (currentDay != lastDay) {
            adShowTimes = 0
            GoPrefManager.getDefault().putLong(PreConstants.AD.KEY_BOOT_AD_SHOW_TIMES, 0).apply()
        }
        //step2判断每天展示次数是否超过上限
        if (adShowTimes >= adShowCount) {
            LogUtil.i("BootIntersetAdManager", "BootIntersetAdManager 不满足展示次数")
            return false
        }
//        step3判断间隔时间
        if (currentTime - lastAdShowTime < adSplitTIme* 60 * 1000) {
            LogUtil.i("BootIntersetAdManager", "BootIntersetAdManager 不满足间隔时间")
            return false
        }
        return true
    }

    override fun onAdLoadSuccess(adObj: Any?) {
        mLoading = false
        adProxy.setAdObject(adObj)
    }

    override fun onAdShow(adObj: Any?) {
        GoPrefManager.getDefault()
            .putLong(PreConstants.AD.KEY_BOOT_AD_LAST_SHOW_TIME, System.currentTimeMillis())
            .apply()
        var currentAdShowCount =
            GoPrefManager.getDefault().getInt(PreConstants.AD.KEY_BOOT_AD_SHOW_TIMES, 0)
        currentAdShowCount++
        GoPrefManager.getDefault()
            .putInt(PreConstants.AD.KEY_BOOT_AD_SHOW_TIMES, currentAdShowCount)
            .apply()
    }

    override fun onAdClick(adObj: Any?) {
    }

    override fun onAdClose(adObj: Any?) {
        adProxy.destroy()
    }

    override fun onAdLoadFail(statusCode: Int) {
        mLoading = false
    }

    override fun onAdVideoPlayFinish(adObj: Any?) {

    }

    fun loadAd(activity: Activity) {
        if (mLoading) {
            return
        }
        if (adProxy.isLoadSuccess()) {
            return
        }
        mLoading = true
        super.loadAd(MODEL_LAUNCHER_ID, activity)
    }

    fun showAd() {
        ThreadExecutorProxy.runOnMainThread(Runnable {
            if (adProxy.isLoadSuccess()) {
                adProxy.show()
            }
        })
    }


    fun getConfig(): AdBean {
        var adBean: AdBean =
            ConfigManager.getConfig(AdConfig::class.java)?.hashMap?.get(MODEL_LAUNCHER_ID)
                ?: return DEFAULT_CONFIG
        return adBean
    }
}
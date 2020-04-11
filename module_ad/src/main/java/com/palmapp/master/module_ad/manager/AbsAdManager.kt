package com.palmapp.master.module_ad.manager

import android.app.Activity
import com.cs.bd.ad.AdSdkApi
import com.cs.bd.ad.bean.AdModuleInfoBean
import com.cs.bd.ad.http.bean.BaseModuleDataItemBean
import com.cs.bd.ad.manager.AdSdkManager
import com.cs.bd.ad.sdk.bean.SdkAdSourceAdWrapper
import com.palmapp.master.baselib.GoCommonEnv
import com.palmapp.master.baselib.proxy.BillingServiceProxy
import com.palmapp.master.baselib.statistics.BaseSeq101OperationStatistic
import com.palmapp.master.baselib.utils.LogUtil
import com.palmapp.master.module_ad.AdSdkProxy

/**
 *
 * @author :     xiemingrui
 * @since :      2019/8/6
 */
abstract class AbsAdManager : AdSdkManager.IVLoadAdvertDataListener {
    private var mBoostItemBean: BaseModuleDataItemBean? = null
    private var mBoostWrapper: SdkAdSourceAdWrapper? = null
    private var mLoading = false
    private var moduleId = -1
    protected fun loadAd(adModuleId: Int, activity: Activity?) {
        moduleId  = adModuleId
        if (BillingServiceProxy.isVip()) {
            LogUtil.d("AbsAdManager","vip用户不用请求广告")
            return
        }
        BaseSeq101OperationStatistic.uploadOperationStatisticData(BaseSeq101OperationStatistic.ad_request,"",moduleId.toString())
        mLoading = true
        val adControlInterceptor = AdSdkManager.IAdControlInterceptor {
            if (BillingServiceProxy.isVip())
                false
            else
                isLoad(it)
        }
        AdSdkProxy.loadAd(activity, adModuleId, adControlInterceptor, this)
    }

    abstract fun isLoad(baseModuleDataItemBean: BaseModuleDataItemBean): Boolean

    override fun onAdInfoFinish(p0: Boolean, adModuleInfoBean: AdModuleInfoBean?) {
        if (adModuleInfoBean == null) {
            return
        }
        mBoostItemBean = adModuleInfoBean.moduleDataItemBean
        val sdkAdBean = adModuleInfoBean
            .sdkAdSourceAdInfoBean
        if (sdkAdBean != null) {
            val wrapperList = sdkAdBean
                .adViewList
            if (wrapperList != null && !wrapperList.isEmpty()) {
                val wrapper = wrapperList[0]
                mBoostWrapper = wrapperList[0]
                val adObj = wrapper.adObject
                BaseSeq101OperationStatistic.uploadOperationStatisticData(BaseSeq101OperationStatistic.ad_request_re,"",moduleId.toString())
                onAdLoadSuccess(adObj)
            }
        }
    }

    //广告加载成功
    abstract fun onAdLoadSuccess(adObj: Any?)

    //广告展示回调
    abstract fun onAdShow(adObj: Any?)

    //广告点击回调
    abstract fun onAdClick(adObj: Any?)

    //广告关闭回调
    abstract fun onAdClose(adObj: Any?)

    //广告加载失败回调
    abstract fun onAdLoadFail(statusCode: Int)

    open fun onAdVideoPlayFinish(adObj: Any?){

    }

    override fun onVideoPlayFinish(adObj: Any?) {
        onAdVideoPlayFinish(adObj)
    }

    override fun onAdImageFinish(p0: AdModuleInfoBean?) {

    }

    override fun onAdFail(p0: Int) {
        onAdLoadFail(p0)
    }

    override fun onAdClosed(p0: Any?) {
        onAdClose(p0)
    }

    override fun onAdClicked(adViewObj: Any?) {
        BaseSeq101OperationStatistic.uploadOperationStatisticData(BaseSeq101OperationStatistic.ad_a000,"",moduleId.toString())
        uploadAdClickStatistic()
        onAdClick(adViewObj)
    }

    override fun onAdShowed(adViewObj: Any?) {
        BaseSeq101OperationStatistic.uploadOperationStatisticData(BaseSeq101OperationStatistic.ad_f000,"",moduleId.toString())
        uploadAdShowStatistic()
        onAdShow(adViewObj)
    }

    fun uploadAdShowStatistic() {
        AdSdkApi.sdkAdShowStatistic(
            GoCommonEnv.getApplication(),
            mBoostItemBean, mBoostWrapper, ""
        )
    }

    fun uploadAdClickStatistic() {
        AdSdkApi.sdkAdClickStatistic(
            GoCommonEnv.getApplication(),
            mBoostItemBean, mBoostWrapper, ""
        )
    }
}
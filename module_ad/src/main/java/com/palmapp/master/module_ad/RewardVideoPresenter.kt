package com.palmapp.master.module_ad

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.view.View
import com.palmapp.master.baselib.BaseMultiplePresenter
import com.palmapp.master.baselib.event.SubscribeUpdateEvent
import com.palmapp.master.baselib.proxy.BillingServiceProxy
import com.palmapp.master.baselib.proxy.OnLoadAdRewardListener
import com.palmapp.master.baselib.view.AdsWatchDialog
import com.palmapp.master.baselib.view.AdsWatchLeftDialog
import com.palmapp.master.baselib.view.LoadingDialog
import com.palmapp.master.module_ad.manager.RewardAdManager
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 *
 * @author :     xiemingrui
 * @since :      2020/3/12
 */
class RewardVideoPresenter(private val activity: Activity) : BaseMultiplePresenter<IRewardVideoView>(), OnLoadAdRewardListener {
    private var adDialog: Dialog? = null
    private val maxAdTimes = RewardAdManager.getConfig().show_count    //必须看广告的次数
    private var adTimes: Int = maxAdTimes       //剩余广告次数
    private var loadingDialog: LoadingDialog? = null

    private var isNeedToShow = false    //加载到广告后是否立刻展示

    var isCanUseFun = BillingServiceProxy.isVip()

    override fun onAttach(pView: IRewardVideoView, context: Context) {
        super.onAttach(pView, context)
        EventBus.getDefault().register(this)
        RewardAdManager.addFreeUse()
        isCanUseFun = BillingServiceProxy.isVip() || RewardAdManager.isFreeUse()
    }

    override fun onDetach() {
        super.onDetach()
        EventBus.getDefault().unregister(this)
    }

    //初始化参数
    private fun resetParams() {
        if (loadingDialog == null) {
            loadingDialog = LoadingDialog(activity)
        }
        isNeedToShow = false
        adTimes = maxAdTimes
    }

    /**
     * 尝试去请求广告
     */
    fun tryToWatchAd() {
        resetParams()
        showAdWatchDialog()
    }

    /**
     *
     */

    private fun showAdWatchDialog() {
        if (adTimes > 0) {
            adDialog = getWatchDialog()
            adDialog?.show()
        }
    }

    //因为广告弹窗有两种样式，所以要判断一下
    private fun getWatchDialog(): Dialog? {
        if (maxAdTimes == adTimes) {
            return AdsWatchDialog(activity, adTimes, View.OnClickListener {
                isNeedToShow = true
                RewardAdManager.loadAd(activity, this)
            })
        } else {
            return AdsWatchLeftDialog(activity, adTimes, View.OnClickListener {
                isNeedToShow = true
                RewardAdManager.loadAd(activity, this)
            })
        }
    }

    override fun onVideoFinish() {
        adDialog?.dismiss()
        adTimes--
        if (adTimes > 0) {
            showAdWatchDialog()
        } else {
            isCanUseFun = true
            getView()?.showRewardVipView()
        }
    }

    override fun onLoadSuccess() {
        loadingDialog?.dismiss()
        if (isNeedToShow)
            RewardAdManager.showAd()
    }

    override fun onLoadFail() {
        loadingDialog?.dismiss()
    }

    override fun onLoading() {
        loadingDialog?.show()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSubscribeUpdateEvent(event: SubscribeUpdateEvent) {
        if (event.isVip) {
            isCanUseFun = true
            getView()?.showRewardVipView()
        }
    }
}
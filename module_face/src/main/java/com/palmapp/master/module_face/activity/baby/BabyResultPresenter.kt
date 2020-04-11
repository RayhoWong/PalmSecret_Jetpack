package com.palmapp.master.module_face.activity.baby;

import android.app.Activity
import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.palmapp.master.baselib.BasePresenter
import com.palmapp.master.baselib.GoCommonEnv
import com.palmapp.master.baselib.bean.face.BabyGenerateRequest
import com.palmapp.master.baselib.bean.face.BabyGenerateResponse
import com.palmapp.master.baselib.bean.user.getCntCover
import com.palmapp.master.baselib.bean.user.getConstellationById
import com.palmapp.master.baselib.event.SubscribeUpdateEvent
import com.palmapp.master.baselib.manager.ShareManager
import com.palmapp.master.baselib.proxy.BillingServiceProxy
import com.palmapp.master.baselib.proxy.OnLoadAdRewardListener
import com.palmapp.master.baselib.view.AdsWatchLeftDialog
import com.palmapp.master.baselib.view.LoadingDialog
import com.palmapp.master.module_ad.manager.RewardAdManager
import com.palmapp.master.module_face.R
import com.palmapp.master.module_network.HttpClient
import com.palmapp.master.module_network.NetworkSubscriber
import com.palmapp.master.module_network.NetworkTransformer
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class BabyResultPresenter(
    val request: BabyGenerateRequest?,
    var male: BabyGenerateResponse?
) : BasePresenter<BabyResultView>(), OnLoadAdRewardListener {
    override fun onVideoFinish() {
        dialog?.dismiss()
        adTimes--
        if (adTimes > 0) {
            showAdWatchDialog()
        } else {
            getView()?.getUnlock(true)
            isSkipVip = true
            getView()?.showVipView(true)
            if (isSelectBoy) selectBoy() else selectGirl()
        }
    }

    override fun onLoading() {
        loadingDialog?.show()
    }

    override fun onLoadSuccess() {
        loadingDialog?.dismiss()
        if (isNeedToShow)
            RewardAdManager.showAd()
    }

    override fun onLoadFail() {
        loadingDialog?.dismiss()
    }

    fun onClickWatchAd() {
        adTimes = RewardAdManager.getConfig()?.show_count ?: 0
        isFirstShow = true
        isNeedToShow = false
        showAdWatchDialog()
    }

    private fun showAdWatchDialog() {
        val activity = getView()?.getContext() as Activity?
        activity?.let {
            if (adTimes > 0) {
                dialog = getWatchDialog()
                dialog?.show()
                isFirstShow = false
            }
        }

    }

    private fun getWatchDialog(): Dialog? {
        val activity = getView()?.getContext() as Activity?
        activity ?: return null
        if (isFirstShow) {
            isNeedToShow = true
            RewardAdManager.loadAd(activity, this)
            return null
        } else {
            return AdsWatchLeftDialog(activity, adTimes, View.OnClickListener {
                isNeedToShow = true
                RewardAdManager.loadAd(activity, this)
            })
        }
    }

    private var adTimes = RewardAdManager.getConfig()?.show_count ?: 0
    private var dialog: Dialog? = null
    private var loadingDialog: LoadingDialog? = null
    private var isNeedToShow = false

    private var isFirstShow = true
    private var isSkipVip = false


    private var femal: BabyGenerateResponse? = null
    private var isSelectBoy = true
    override fun onAttach() {
        if (!BillingServiceProxy.isVip() && !RewardAdManager.isFreeUse()) {
            val activity = getView()?.getContext() as Activity?
            activity?.let {
                RewardAdManager.loadAd(it, this)
                loadingDialog = LoadingDialog(it)
            }
        }

        EventBus.getDefault().register(this)
    }

    override fun onDetach() {
        EventBus.getDefault().unregister(this)
    }

    fun selectBoy() {
        isSelectBoy = true
        getView()?.showBoyPic()
        if (male != null) {
            getView()?.showResult(male?.babyReportInfo?.baby_image_url)
            return
        }

        if (request == null) {
            return
        }
        request.gender = "M"
        if (BillingServiceProxy.isVip() || RewardAdManager.isFreeUse() || isSkipVip) {
            getView()?.showLoadingDialog()
            HttpClient.getFaceRequest().generateBaby(request)
                .compose(NetworkTransformer.toMainSchedulers())
                .subscribe(object : NetworkSubscriber<BabyGenerateResponse>() {
                    override fun onNext(response: BabyGenerateResponse) {
                        if (response.isSuccess() == true) {
                            male = response
                            getView()?.showResult(male?.babyReportInfo?.baby_image_url)
                        }
                    }

                    override fun onError(t: Throwable) {
                        getView()?.showNetError()
                    }

                    override fun onFinish() {
                        getView()?.dismissLoadingDialog()
                    }
                })
        }
    }

    fun share(bitmap: Bitmap?, father: Drawable?, mother: Drawable?) {
        var url = ""
        if (isSelectBoy && male != null) {
            url = male?.babyReportInfo?.baby_image_url ?: ""
        } else if (!isSelectBoy && femal != null) {
            url = femal?.babyReportInfo?.baby_image_url ?: ""
        }
        if (url.isEmpty()) {
            return
        }
        val view = LayoutInflater.from(getView()?.getContext())
            .inflate(R.layout.face_activity_baby_share, null, false)
        if (TextUtils.isEmpty(GoCommonEnv.userInfo?.name ?: "")) {
            view.findViewById<View>(R.id.share_info_layout).visibility = View.INVISIBLE
        }
        view.findViewById<ImageView>(R.id.iv_left).setImageDrawable(mother)
        view.findViewById<ImageView>(R.id.iv_right).setImageDrawable(father)
        view.findViewById<ImageView>(R.id.iv_after).setImageBitmap(bitmap)
        view.findViewById<ImageView>(R.id.iv_me)
            .setImageResource(getCntCover(GoCommonEnv.userInfo?.constellation ?: 1))
        view.findViewById<TextView>(R.id.tv_user_name).text = GoCommonEnv.userInfo?.name ?: ""
        view.findViewById<TextView>(R.id.tv_user_cnt).text =
            getConstellationById(GoCommonEnv.userInfo?.constellation ?: 1)

        ShareManager.share(view, getView()?.getContext()?.getString(R.string.app_app_name) ?: "")
    }

    fun selectGirl() {
        isSelectBoy = false
        getView()?.showGirlPic()
        if (femal != null) {
            getView()?.showResult(femal?.babyReportInfo?.baby_image_url)
            return
        }
        if (request == null) {
            return
        }
        request.gender = "F"
        if (BillingServiceProxy.isVip()  || RewardAdManager.isFreeUse() || isSkipVip) {
            getView()?.showLoadingDialog()
            HttpClient.getFaceRequest().generateBaby(request)
                .compose(NetworkTransformer.toMainSchedulers())
                .subscribe(object : NetworkSubscriber<BabyGenerateResponse>() {
                    override fun onNext(response: BabyGenerateResponse) {
                        if (response.isSuccess() == true) {
                            femal = response
                            getView()?.showResult(femal?.babyReportInfo?.baby_image_url)
                        }
                    }

                    override fun onError(t: Throwable) {
                        getView()?.showNetError()
                    }

                    override fun onFinish() {
                        getView()?.dismissLoadingDialog()
                    }
                })
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSubscribeUpdateEvent(event: SubscribeUpdateEvent) {
        if (event.isVip) {
            isSkipVip = true
            getView()?.getUnlock(true)
            getView()?.showVipView(true)
            if (isSelectBoy) selectBoy() else selectGirl()
        }
    }
}
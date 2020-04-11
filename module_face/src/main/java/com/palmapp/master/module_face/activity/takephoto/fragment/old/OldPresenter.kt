package com.palmapp.master.module_face.activity.takephoto.fragment.old;

import android.app.Activity
import android.app.Dialog
import android.net.Uri
import android.view.View
import android.widget.Toast
import com.example.oldlib.old.FaceDetect
import com.palmapp.master.baselib.BasePresenter
import com.palmapp.master.baselib.event.SubscribeUpdateEvent
import com.palmapp.master.baselib.proxy.BillingServiceProxy
import com.palmapp.master.baselib.proxy.OnLoadAdRewardListener
import com.palmapp.master.baselib.utils.LogUtil
import com.palmapp.master.baselib.view.AdsWatchDialog
import com.palmapp.master.baselib.view.AdsWatchLeftDialog
import com.palmapp.master.baselib.view.LoadingDialog
import com.palmapp.master.module_ad.manager.RewardAdManager
import com.palmapp.master.module_face.R
import com.palmapp.master.module_face.activity.takephoto.fragment.DataObject
import kotlinx.android.synthetic.main.face_fragment_takephotoed.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class OldPresenter : BasePresenter<OldView>(), OnLoadAdRewardListener {
    override fun onVideoFinish() {
        dialog?.dismiss()
        adTimes--
        if (adTimes > 0) {
            showAdWatchDialog()
        } else {
            getView()?.toUpdateUIWithVip(true,true)
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

    private var adTimes = RewardAdManager.getConfig()?.show_count ?: 0
    private var dialog: Dialog? = null
    private var loadingDialog: LoadingDialog? = null
    private var isNeedToShow = false
    private var isFirstShow = true
    override fun onAttach() {
        EventBus.getDefault().register(this)
        if (!BillingServiceProxy.isVip() && !RewardAdManager.isFreeUse()) {
            val activity = getView()?.getContext() as Activity?
            activity?.let {
                RewardAdManager.loadAd(it, this)
                loadingDialog = LoadingDialog(it)
            }
        }
    }

    override fun onDetach() {
        EventBus.getDefault().unregister(this)
    }

    fun onClickWatchAd() {
        adTimes = RewardAdManager.getConfig()?.show_count ?: 0
        isFirstShow = true

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

    //接受某个选项的名称
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSubscribeUpdateEvent(event: SubscribeUpdateEvent) {
        getView()?.toUpdateUIWithVip(event.isVip,event.isVip)
    }
}
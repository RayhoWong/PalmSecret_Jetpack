package com.palmapp.master.infomation;

import com.palmapp.master.baselib.BasePresenter
import com.palmapp.master.baselib.utils.LogUtil
import com.palmapp.master.module_me.R
import com.palmapp.master.module_network.FeedbackBody
import com.palmapp.master.module_network.HttpClient
import com.palmapp.master.module_network.NetworkSubscriber
import com.palmapp.master.module_network.NetworkTransformer

class FeedbackPresenter : BasePresenter<FeedbackView>() {
    override fun onAttach() {
    }

    override fun onDetach() {
    }


    fun postFeedback(content:String) {

        HttpClient.getFeedbackRequest().postFeedback(
            FeedbackBody(content).getParam())
            .compose(NetworkTransformer.toMainSchedulers())
            .subscribe(object : NetworkSubscriber<String>() {
                override fun onNext(t: String) {
                   if (t != null && t.equals("1")) {
                       LogUtil.d("意见反馈成功了？")
                       getView()?.showToast(R.string.setting_feedback_success)
                   } else {
                       LogUtil.d("反馈失败了？")
                   }
                }
            })

    }
}
package com.palmapp.master.module_cnt.matching;

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.palmapp.master.baselib.BasePresenter
import com.palmapp.master.baselib.GoCommonEnv
import com.palmapp.master.baselib.bean.cnt.MatchingRequest
import com.palmapp.master.baselib.bean.cnt.MatchingResponse
import com.palmapp.master.baselib.bean.user.getCntCover
import com.palmapp.master.baselib.bean.user.getConstellationById
import com.palmapp.master.baselib.manager.ShareManager
import com.palmapp.master.baselib.statistics.BaseSeq103OperationStatistic
import com.palmapp.master.module_cnt.R
import com.palmapp.master.module_network.HttpClient
import com.palmapp.master.module_network.NetworkSubscriber
import com.palmapp.master.module_network.NetworkTransformer
import com.trello.rxlifecycle2.android.ActivityEvent
import io.reactivex.disposables.Disposable

class CntMatchPresenter : BasePresenter<CntMatchView>() {
    override fun onAttach() {
    }

    override fun onDetach() {
    }

    fun loadData(id1: Int, id2: Int) {
        val request = MatchingRequest()
        request.constellation1_id = id1
        request.constellation2_id = id2
        getView()?.showLoading()
        HttpClient.getCntRequest().matching(request).compose(activityProvider?.bindUntilEvent(ActivityEvent.DESTROY))
            .compose(NetworkTransformer.toMainSchedulers())
            .subscribe(object : NetworkSubscriber<MatchingResponse>() {
                override fun onNext(t: MatchingResponse) {
                    getView()?.showResult(t)
                }

                override fun onNetworkError() {
                    super.onNetworkError()
                    getView()?.showNetworkError()
                }

                override fun onServiceError() {
                    super.onServiceError()
                    getView()?.showServerError()
                }

                override fun onSubscribe(d: Disposable) {
                    super.onSubscribe(d)
                }
            })
    }

    fun share(cnt_id1: Int, cnt_id2: Int, percent: Int) {
        BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
            BaseSeq103OperationStatistic.share_a000,
            "",
            "10",
            ""
        )
        val view = LayoutInflater.from(getView()?.getContext()).inflate(R.layout.cnt_layout_share_match, null, false)
        if (TextUtils.isEmpty(GoCommonEnv.userInfo?.name ?: "")) {
            view.findViewById<View>(R.id.layout_header).visibility = View.GONE
        }
        view.findViewById<ImageView>(R.id.iv_me)
            .setImageResource(getCntCover(GoCommonEnv.userInfo?.constellation ?: 1))
        view.findViewById<ImageView>(R.id.iv2).setImageResource(getCntCover(cnt_id2))
        view.findViewById<ImageView>(R.id.iv1).setImageResource(getCntCover(cnt_id1))
        view.findViewById<TextView>(R.id.tv_user_name).text = GoCommonEnv.userInfo?.name ?: ""
        view.findViewById<TextView>(R.id.tv_user_cnt).text =
            getConstellationById(GoCommonEnv.userInfo?.constellation ?: 1)
        view.findViewById<TextView>(R.id.tv_cntmatching_score).text = "$percent%"
        view.findViewById<TextView>(R.id.tv_cntmatching_me).text = getConstellationById(cnt_id1)
        view.findViewById<TextView>(R.id.tv_cntmatching_other).text = getConstellationById(cnt_id2)
        ShareManager.share(view, getView()?.getContext()?.getString(R.string.app_app_name) ?: "")
    }
}
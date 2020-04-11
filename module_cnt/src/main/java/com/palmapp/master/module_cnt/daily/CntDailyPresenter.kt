package com.palmapp.master.module_cnt.daily;

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatRatingBar
import com.palmapp.master.baselib.BasePresenter
import com.palmapp.master.baselib.GoCommonEnv
import com.palmapp.master.baselib.bean.StatusResult
import com.palmapp.master.baselib.bean.cnt.*
import com.palmapp.master.baselib.bean.user.getCntCover
import com.palmapp.master.baselib.bean.user.getConstellationById
import com.palmapp.master.baselib.bean.user.getDateById
import com.palmapp.master.baselib.manager.ShareManager
import com.palmapp.master.baselib.statistics.BaseSeq103OperationStatistic
import com.palmapp.master.module_cnt.R
import com.palmapp.master.module_network.HttpClient
import com.palmapp.master.module_network.NetworkSubscriber
import com.palmapp.master.module_network.NetworkTransformer
import com.trello.rxlifecycle2.android.ActivityEvent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class CntDailyPresenter : BasePresenter<CntDailyView>() {


    fun loadData(id: Int) {
        if (id <= 0) {
            //非法参数
            return
        }
        getView()?.showHeader(getConstellationById(id), "(${getDateById(id)})", getCntCover(id))
        getView()?.showLoading()
        HttpClient.getCntRequest().getForecast(ForecastRequest(id, DAY, TOMORROW, WEEK, MONTH, YEAR))
            .compose(activityProvider?.bindUntilEvent(ActivityEvent.DESTROY))
            .compose(NetworkTransformer.toMainSchedulers()).observeOn(Schedulers.io()).map {
                val list = ArrayList<ForecastResponse>(3)
                list.add(
                    ForecastResponse(
                        StatusResult(),
                        it.forecast_infos.filter { TextUtils.equals(it.forecast_type, DAY) })
                )
                list.add(
                    ForecastResponse(
                        StatusResult(),
                        it.forecast_infos.filter { TextUtils.equals(it.forecast_type, TOMORROW) })
                )
                list.add(
                    ForecastResponse(
                        StatusResult(),
                        it.forecast_infos.filter {
                            !TextUtils.equals(
                                it.forecast_type,
                                DAY
                            ) && !TextUtils.equals(it.forecast_type, TOMORROW)
                        })
                )
                list
            }.observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : NetworkSubscriber<ArrayList<ForecastResponse>>() {
                override fun onNext(t: ArrayList<ForecastResponse>) {
                    getView()?.showForecast(t)
                }

                override fun onNetworkError() {
                    getView()?.showNetworkError()
                }

                override fun onServiceError() {
                    getView()?.showServerError()
                }
            })
    }

    fun share(vararg rating: Float) {
        BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
            BaseSeq103OperationStatistic.share_a000,
            "",
            "9",
            ""
        )
        val view = LayoutInflater.from(getView()?.getContext()).inflate(R.layout.cnt_layout_share_daily, null, false)
        if (TextUtils.isEmpty(GoCommonEnv.userInfo?.name ?: "")) {
            view.findViewById<View>(R.id.layout_header).visibility = View.GONE
        }
        view.findViewById<ImageView>(R.id.iv_me)
            .setImageResource(getCntCover(GoCommonEnv.userInfo?.constellation ?: -1))
        view.findViewById<TextView>(R.id.tv_user_name).text = GoCommonEnv.userInfo?.name ?: ""
        view.findViewById<TextView>(R.id.tv_user_cnt).text =
            getConstellationById(GoCommonEnv.userInfo?.constellation ?: 1)
        view.findViewById<AppCompatRatingBar>(R.id.rb_cntdaily_item_love).rating = rating[0]
        view.findViewById<AppCompatRatingBar>(R.id.rb_cntdaily_item_health).rating = rating[1]
        view.findViewById<AppCompatRatingBar>(R.id.rb_cntdaily_item_wealth).rating = rating[2]
        view.findViewById<AppCompatRatingBar>(R.id.rb_cntdaily_item_career).rating = rating[3]
        ShareManager.share(view, getView()?.getContext()?.getString(R.string.app_app_name) ?: "")
    }

    override fun onAttach() {
    }

    override fun onDetach() {
    }

}
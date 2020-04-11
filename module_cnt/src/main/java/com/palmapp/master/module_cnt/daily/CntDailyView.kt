package com.palmapp.master.module_cnt.daily;

import com.palmapp.master.baselib.IView
import com.palmapp.master.baselib.bean.cnt.ForecastResponse

interface CntDailyView : IView {
    fun showForecast(list:ArrayList<ForecastResponse>)
    fun showHeader(name:String,birth:String,cover:Int)
    fun showNetworkError()
    fun showServerError()
    fun showLoading()
}
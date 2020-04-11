package com.palmapp.master.baselib

import android.content.Context

interface INetworkView:IView {
    /**
     * 获取Context
     */
    fun showLoading()

    fun showNetworkError()

    fun showServerError()

}
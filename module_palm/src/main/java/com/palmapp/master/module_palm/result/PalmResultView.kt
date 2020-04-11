package com.palmapp.master.module_palm.result;

import com.palmapp.master.baselib.INetworkView
import com.palmapp.master.baselib.IView

interface PalmResultView : IView {
    fun onFinish()
    fun onTimeRemain(time:String)
}
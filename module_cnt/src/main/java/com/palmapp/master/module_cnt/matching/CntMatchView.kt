package com.palmapp.master.module_cnt.matching;

import com.palmapp.master.baselib.INetworkView
import com.palmapp.master.baselib.IView
import com.palmapp.master.baselib.bean.cnt.MatchingResponse

interface CntMatchView : INetworkView {
    fun showResult(response:MatchingResponse)
}
package com.palmapp.master.module_palm.match;

import com.palmapp.master.baselib.IView
import com.palmapp.master.baselib.bean.palm.PalmMatchResponse

interface PalmMatchLoadingView : IView {
    fun showResult(data: PalmMatchResponse)//展示结果
    fun showPicError()//图片识别失败
    fun showNetError()//网络识别失败
    fun finishActivity()
}
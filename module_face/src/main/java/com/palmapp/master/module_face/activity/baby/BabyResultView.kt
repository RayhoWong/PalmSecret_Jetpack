package com.palmapp.master.module_face.activity.baby;

import com.palmapp.master.baselib.IView

interface BabyResultView : IView {
    fun showBoyPic()

    fun showGirlPic()

    fun showResult(url:String?)

    fun showLoadingDialog()

    fun dismissLoadingDialog()

    fun showNetError()

    fun showVipView(isVip:Boolean)

    fun getUnlock(isUnlock: Boolean)
}
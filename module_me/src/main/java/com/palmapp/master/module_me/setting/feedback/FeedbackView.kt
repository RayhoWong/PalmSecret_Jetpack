package com.palmapp.master.infomation;

import com.palmapp.master.baselib.IView
import com.palmapp.master.baselib.bean.user.Gender

interface FeedbackView : IView {

    fun showToast(resId: Int)
}
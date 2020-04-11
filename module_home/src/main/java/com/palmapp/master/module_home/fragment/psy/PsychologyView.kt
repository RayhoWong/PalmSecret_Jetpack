package com.palmapp.master.module_home.fragment.psy;

import com.palmapp.master.baselib.INetworkView
import com.palmapp.master.baselib.IView
import com.palmapp.master.baselib.bean.quiz.QuizListBean

interface PsychologyView : INetworkView {
    fun showContent(list: List<QuizListBean>?)
}
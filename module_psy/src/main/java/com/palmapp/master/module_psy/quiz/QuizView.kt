package com.palmapp.master.module_psy.quiz;

import com.palmapp.master.baselib.INetworkView
import com.palmapp.master.baselib.IView
import com.palmapp.master.baselib.bean.quiz.AnswerResponse
import com.palmapp.master.baselib.bean.quiz.QuizContentBean

interface QuizView : INetworkView {
    fun showContent(quiz:QuizContentBean)
    fun showResult(response:AnswerResponse)
}
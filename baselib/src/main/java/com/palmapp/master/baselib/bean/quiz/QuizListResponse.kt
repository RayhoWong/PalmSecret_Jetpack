package com.palmapp.master.baselib.bean.quiz

import com.google.gson.annotations.SerializedName
import com.palmapp.master.baselib.bean.BaseResponse

/**
 * Created by huangweihao on 2019/8/13.
 * 测试列表的数据
 */
class QuizListResponse : BaseResponse() {
    @SerializedName("quizzes")
    var quizzes: List<QuizListBean>? = null
}
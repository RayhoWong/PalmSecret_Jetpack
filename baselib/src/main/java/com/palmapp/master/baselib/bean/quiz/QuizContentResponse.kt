package com.palmapp.master.baselib.bean.quiz

import com.google.gson.annotations.SerializedName
import com.palmapp.master.baselib.bean.BaseResponse

/**
 * Created by huangweihao on 2019/8/14.
 * 测试题内容
 */
class QuizContentResponse: BaseResponse() {
    @SerializedName("quiz_type")
    var quiz_type: String? = null

    @SerializedName("quiz")
    var quiz: QuizContentBean? = null
}
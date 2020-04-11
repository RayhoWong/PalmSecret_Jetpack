package com.palmapp.master.baselib.bean.quiz

import com.google.gson.annotations.SerializedName
import com.palmapp.master.baselib.bean.BaseRequest

/**
 *
 * @author :     huangweihao
 * @since :      2019/8/14
 * 测试题内容
 */
class QuizContentRequest : BaseRequest() {
    @SerializedName("quiz_id")
    var quiz_id: String? = null //测试题ID
}
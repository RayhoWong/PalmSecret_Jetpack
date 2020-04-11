package com.palmapp.master.baselib.bean.quiz

import com.google.gson.annotations.SerializedName
import com.palmapp.master.baselib.bean.BaseRequest

/**
 *
 * @author :     huangweihao
 * @since :      2019/8/13
 * 测试题列表
 */
class QuizListRequest : BaseRequest() {
    @SerializedName("category_id")
    var category_id: String? = null
    @SerializedName("accept_types")
    var accept_type: List<String>? = null //接受的类型

    companion object {
        const val QUIZ_CATEGORY_ID = "3" //测试题id
        const val PSY_CATEGORY_ID = "4" //手相ID
        const val ACCORDING_OPTION = "ACCORDING_OPTION" // 根据选项得到答案
        const val ACCORDING_SCORE = "ACCORDING_SCORE" // 根据分数得到答案
        const val JUMP = "JUMP" //跳转类型
        const val COMBINATION = "COMBINATION" //组合型
    }
}
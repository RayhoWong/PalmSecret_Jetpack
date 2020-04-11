package com.palmapp.master.baselib.bean.quiz

import com.palmapp.master.baselib.bean.BaseRequest

/**
 *
 * @author :     xiemingrui
 * @since :      2019/8/15
 */
class AnswerRequest : BaseRequest() {
    var question_id: Long? = null
    var quiz_type: String? = null
    var answer_list: ArrayList<QuizOptionBean>? = null
}
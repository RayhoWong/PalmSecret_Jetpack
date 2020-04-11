package com.palmapp.master.baselib.bean.quiz

/**
 * Created by huangweihao on 2019/8/14.
 * 测试题内容获取
 */
data class QuizContentBean(
    val quiz_id: String,
    val type: String,
    val category_id: String,
    val title: String,
    val summary: String,
    val overview_img: String,
    val cover_img: String,
    val description: String,
    val purchase: Boolean,
    val color: String,
    var group_id: Int,

    val questions: List<QuestionDTO>
)

data class QuestionDTO(
    val question_id: String,
    val type: String,
    val title: String,
    val description: String,
    val img: String,
    val options: List<OptionDTO>,
    val answers: List<QuziAnswerDTO>
)

data class OptionDTO(
    val option_id: String,
    val type: String,
    val name: String,
    val description: String,
    val score: Int,
    val picture: String,
    val jump_question_id: Long
)

data class QuziAnswerDTO(
    val answer_id: String,
    val option_id: String,
    val title: String,
    val description: String,
    val picture: Int,
    val nature: String
)
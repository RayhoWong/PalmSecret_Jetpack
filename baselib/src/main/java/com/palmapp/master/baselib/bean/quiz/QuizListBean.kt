package com.palmapp.master.baselib.bean.quiz

/**
 * Created by huangweihao on 2019/8/13.
 * 测试列表
 */
data class QuizListBean (
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
    var group_id: Int
)
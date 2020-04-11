package com.palmapp.master.baselib.bean.tarot

/**
 * Created by huangweihao on 2019/9/20.
 * 塔罗牌话题
 */
data class TarotTopicInfoBean (
    var topic_id: Int,
    var topic_pic: String,
    var card_array_id: String,
    var card_array_name: String,
    var card_array_description: String,
    var topic_name: String,
    var topic_description: String,
    var charge_status: Int,
    var home_status: Int,
    var answer_array_num: Int,
    var topic_kind: Int
)
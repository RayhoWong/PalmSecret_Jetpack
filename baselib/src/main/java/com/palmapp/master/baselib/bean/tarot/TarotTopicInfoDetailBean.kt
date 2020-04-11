package com.palmapp.master.baselib.bean.tarot

/**
 * Created by huangweihao on 2019/9/23.
 */
data class TarotTopicInfoDetailBean(
    var topic_id: Int,
    var topic_name: String,
    var topic_description: String,
    var topic_pic: String,
    var angle_num: Int,
    var card_array_id: String,
    var card_array_name: String,
    var card_array_description: String,
    var card_array_pic: String,
    var topic_angle_content_dto_list: List<TopicAngleContentDto>,
    var topic_answer: TopicAnswer
)

data class TopicAngleContentDto(
    var angle_number: Int,
    var content: String
)

data class TopicAnswer(
    var topic_answer_id: Int,
    var topic_id: Int,
    var share_page_url: String,
    var explanation: String,
    var topic_angle_answer_list: List<TopicAngleAnswer>
)

data class TopicAngleAnswer(
    var card_place_type: Int,
    var card_key: String,
    var explanation: String,
    var pic: String,
    var card_name: String,
    var angle_number: Int,
    var keyword: String,
    //卡牌是否已经选过
    var isFlip: Boolean = false,
    var topic: String
)
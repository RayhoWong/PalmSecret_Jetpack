package com.palmapp.master.baselib.bean.tarot

import com.google.gson.annotations.SerializedName
import com.palmapp.master.baselib.bean.BaseResponse

/**
 * Created by huangweihao on 2019/9/23.
 * 塔罗牌话题详细信息
 */
class TarotTopicInfoDetailResponse : BaseResponse() {

    @SerializedName("topic_id")
    var topic_id: Int? = null

    @SerializedName("topic_name")
    var topic_name: String? = null

    @SerializedName("topic_description")
    var topic_description: String? = null

    @SerializedName("topic_pic")
    var topic_pic: String? = null

    @SerializedName("angle_num")
    var angle_num: Int? = null

    @SerializedName("card_array_id")
    var card_array_id: String? = null

    @SerializedName("card_array_name")
    var card_array_name: String? = null

    @SerializedName("card_array_description")
    var card_array_description: String? = null

    @SerializedName("card_array_pic")
    var card_array_pic: String? = null

    @SerializedName("topic_angle_content_dto_list")
    var topic_angle_content_dto_list: List<TopicAngleContentDto>? = null

    @SerializedName("topic_answer")
    var topic_answer: TopicAnswer? = null

}
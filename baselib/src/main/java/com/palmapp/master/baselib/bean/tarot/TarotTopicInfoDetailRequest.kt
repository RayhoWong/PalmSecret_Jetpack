package com.palmapp.master.baselib.bean.tarot

import com.google.gson.annotations.SerializedName
import com.palmapp.master.baselib.bean.BaseRequest

/**
 * Created by huangweihao on 2019/9/23.
 * 塔罗牌话题详细信息
 */
class TarotTopicInfoDetailRequest : BaseRequest(){

    var topic_id: Int? = null

    //可以为空 不过滤答案 返回答案可能有重复
    @SerializedName("topic_answer_ids")
    var topic_answer_ids: List<Int>? = null

}
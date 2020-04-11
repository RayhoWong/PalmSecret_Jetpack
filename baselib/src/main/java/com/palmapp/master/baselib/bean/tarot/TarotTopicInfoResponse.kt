package com.palmapp.master.baselib.bean.tarot

import com.google.gson.annotations.SerializedName
import com.palmapp.master.baselib.bean.BaseResponse

/**
 * Created by huangweihao on 2019/9/20.
 * 塔罗牌话题列表
 */
class TarotTopicInfoResponse : BaseResponse() {
    @SerializedName("topic_info_list")
    var topic_info_list: List<TarotTopicInfoBean> = arrayListOf()
}
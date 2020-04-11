package com.palmapp.master.baselib.bean.tarot

import com.google.gson.annotations.SerializedName
import com.palmapp.master.baselib.bean.BaseRequest

/**
 * Created by huangweihao on 2019/9/20.
 * 塔罗牌话题列表
 */
class TarotTopicInfoRequest : BaseRequest(){

    companion object{
        //话题类别id(正式服和测试服一致)
        const val topic_kind_ids = 1
        //客户端支持的真爱牌阵id(正式服和测试服一致)
        const val tarot_card_array_id_love = "hps001"
        //客户端支持的职业牌阵id(正式服和测试服一致)
        const val tarot_card_array_id_career = "hps002"

    }

    @SerializedName("tarot_card_array_id")
    var tarot_card_array_id: List<String> = arrayListOf()

    @SerializedName("topic_kind_ids")
    var topic_kind_ids: List<Int>? = arrayListOf()

}
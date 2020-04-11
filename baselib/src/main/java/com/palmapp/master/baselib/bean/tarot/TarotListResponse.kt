package com.palmapp.master.baselib.bean.tarot

import com.google.gson.annotations.SerializedName
import com.palmapp.master.baselib.bean.BaseResponse
import com.palmapp.master.baselib.bean.quiz.QuizListBean

/**
 * Created by huangweihao on 2019/8/19.
 * 塔罗牌列表的数据
 */
class TarotListResponse : BaseResponse() {
    @SerializedName("cards")
    var cards: List<TarotListBean>? = null
}
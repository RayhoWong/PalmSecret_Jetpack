package com.palmapp.master.baselib.bean.tarot

import com.google.gson.annotations.SerializedName
import com.palmapp.master.baselib.bean.BaseRequest

/**
 * Created by huangweihao on 2019/8/21.
 */
class DailyTarotAnswerRequest : BaseRequest() {
    @SerializedName("card_keys")
    var card_keys: MutableList<String> = arrayListOf()
}
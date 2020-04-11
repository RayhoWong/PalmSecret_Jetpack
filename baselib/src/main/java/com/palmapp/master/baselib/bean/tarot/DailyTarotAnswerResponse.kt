package com.palmapp.master.baselib.bean.tarot

import com.google.gson.annotations.SerializedName
import com.palmapp.master.baselib.bean.BaseResponse

/**
 * Created by huangweihao on 2019/8/21.
 */
class DailyTarotAnswerResponse : BaseResponse() {
     @SerializedName("daily_tarot_answer_map")
     var daily_tarot_answer_map: Map<String,DailyTarotAnswerBean>? = null
}
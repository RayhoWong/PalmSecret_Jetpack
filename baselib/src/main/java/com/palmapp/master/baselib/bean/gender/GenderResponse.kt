package com.palmapp.master.baselib.bean.gender

import com.google.gson.annotations.SerializedName
import com.palmapp.master.baselib.bean.BaseResponse

/**
 * Created by huangweihao on 2019/12/6.
 * 变性响应体
 */
class GenderResponse : BaseResponse() {
   @SerializedName("gender_report")
   var gender_report: GenderReport? = null
}
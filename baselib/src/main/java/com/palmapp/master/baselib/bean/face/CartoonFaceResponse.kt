package com.palmapp.master.baselib.bean.face

import com.google.gson.annotations.SerializedName
import com.palmapp.master.baselib.bean.BaseResponse

/**
 * Created by huangweihao on 2020/4/1.
 * 卡通人像响应体
 */
class CartoonFaceResponse : BaseResponse() {
   @SerializedName("cartoon_report")
   var cartoon_report: CartoonReport? = null
}
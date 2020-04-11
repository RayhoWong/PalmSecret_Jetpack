package com.palmapp.master.baselib.bean.face

import com.google.gson.annotations.SerializedName
import com.palmapp.master.baselib.bean.BaseResponse

/**
 * Created by huangweihao on 2020/4/1.
 * 卡通模板响应体
 */
class CartoonTemplateResponse : BaseResponse() {
   @SerializedName("cartoon_templates")
   var cartoon_templates: List<CartoonTemplate>? = null //错误码   0 ：成功
}
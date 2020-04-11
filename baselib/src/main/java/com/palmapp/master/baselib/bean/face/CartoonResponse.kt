package com.palmapp.master.baselib.bean.face

import com.google.gson.annotations.SerializedName
import com.palmapp.master.baselib.bean.BaseResponse

/**
 * Created by huangweihao on 2020/2/27.
 * 卡通响应体
 */
class CartoonResponse : BaseResponse() {
   @SerializedName("errcode")
   var errcode: Int? = null //错误码   0 ：成功
   @SerializedName("errmsg")
   var errmsg: String? = null //错误信息
   @SerializedName("output")
   var output: String? = null //处理之后的结果,图片base64	如果失败，则此项为空
   @SerializedName("bodymask")
   var bodymask: String? = null //人体抠像掩码

}
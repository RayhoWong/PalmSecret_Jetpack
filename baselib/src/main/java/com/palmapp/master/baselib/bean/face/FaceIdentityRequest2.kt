package com.palmapp.master.baselib.bean.face

import com.google.gson.annotations.SerializedName
import com.palmapp.master.baselib.bean.BaseRequest
import com.palmapp.master.baselib.bean.S3ImageInfo

class FaceIdentityRequest2 : BaseRequest() {
    @SerializedName("img_base64")
    var img_base64: String? = null //图片base64编码

    @SerializedName("package_name") //包名
    var package_name: String = "com.palmsecret.horoscope"
}
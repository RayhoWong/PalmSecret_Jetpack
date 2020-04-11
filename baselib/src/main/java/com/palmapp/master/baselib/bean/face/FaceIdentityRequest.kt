package com.palmapp.master.baselib.bean.face

import com.google.gson.annotations.SerializedName
import com.palmapp.master.baselib.bean.BaseRequest
import com.palmapp.master.baselib.bean.S3ImageInfo

class FaceIdentityRequest : BaseRequest() {
    @SerializedName("image")
    var image: S3ImageInfo? = null
}
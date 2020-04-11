package com.palmapp.master.baselib.bean.palm

import com.google.gson.annotations.SerializedName
import com.palmapp.master.baselib.bean.BaseRequest
import com.palmapp.master.baselib.bean.S3ImageInfo

/**
 *
 * @author :     xiemingrui
 * @since :      2019/8/9
 */
class IdentityRequest : BaseRequest() {
    @SerializedName("hand_img")
    var hand_img: S3ImageInfo? = null
    @SerializedName("time_limit")
    var time_limit: Boolean? = null
}
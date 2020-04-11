package com.palmapp.master.baselib.bean.palm

import com.google.gson.annotations.SerializedName
import com.palmapp.master.baselib.bean.BaseRequest
import com.palmapp.master.baselib.bean.S3ImageInfo

/**
 *
 * @author :     xiemingrui
 * @since :      2019/8/9
 */
class IdentityRequestV2 : BaseRequest() {
    @SerializedName("type_content")
    var type_content: ArrayList<String>? = null
}
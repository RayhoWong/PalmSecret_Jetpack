package com.palmapp.master.baselib.bean.face

import com.google.gson.annotations.SerializedName
import com.palmapp.master.baselib.bean.BaseResponse

/**
 *
 * @author :     xiemingrui
 * @since :      2019/12/9
 */
class FaceIdentityResponse : BaseResponse() {
    @SerializedName("face_info")
    var face_info: List<FaceInfo>? = null
}
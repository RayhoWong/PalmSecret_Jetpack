package com.palmapp.master.baselib.bean.face

import com.google.gson.annotations.SerializedName
import com.palmapp.master.baselib.bean.BaseResponse

/**
 *
 * @author :     xiemingrui
 * @since :      2019/12/9
 */
class FaceInfo {
    @SerializedName("top")
    var top: Int = 0
    @SerializedName("left")
    var left: Int = 0
    @SerializedName("width")
    var width: Int = 0
    @SerializedName("height")
    var height: Int = 0
    @SerializedName("ethnicity")
    var ethnicity: Int = 0
    @SerializedName("gender")
    var gender: String = ""

}
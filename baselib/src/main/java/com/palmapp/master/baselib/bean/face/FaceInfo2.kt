package com.palmapp.master.baselib.bean.face

import com.google.gson.annotations.SerializedName
import com.palmapp.master.baselib.bean.BaseResponse

/**
 *
 * @author :     huangweihao
 * @since :      2020/2/27
 */
class FaceInfo2 {
    @SerializedName("age")
    var age: Int = 0
    @SerializedName("ethnicity")
    var ethnicity: String = ""
    @SerializedName("gender")
    var gender: String = ""
}
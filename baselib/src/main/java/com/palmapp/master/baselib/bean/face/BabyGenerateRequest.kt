package com.palmapp.master.baselib.bean.face

import android.text.TextUtils
import com.google.gson.annotations.SerializedName
import com.palmapp.master.baselib.bean.BaseRequest
import com.palmapp.master.baselib.bean.S3ImageInfo
import java.io.Serializable

/**
 *
 * @author :     xiemingrui
 * @since :      2019/12/9
 */
class BabyGenerateRequest : BaseRequest(), Serializable {
    @SerializedName("gender")
    var gender: String = "M"
    @SerializedName("ethnicity")
    var ethnicity: String = ""
    @SerializedName("mother_img")
    var mother_img: S3ImageInfo? = null
    @SerializedName("mother_face_rectangle")
    var mother_face_rectangle: FaceRectangle? = null
    @SerializedName("father_img")
    var father_img: S3ImageInfo? = null
    @SerializedName("father_face_rectangle")
    var father_face_rectangle: FaceRectangle? = null
    @SerializedName("time_limit")
    var time_limit = false

    fun isReady(): Boolean =
        !(TextUtils.isEmpty(ethnicity) || mother_img == null || mother_face_rectangle == null || father_img == null || father_face_rectangle == null)
}
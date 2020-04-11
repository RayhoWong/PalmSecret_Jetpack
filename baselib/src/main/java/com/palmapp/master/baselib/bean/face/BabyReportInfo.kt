package com.palmapp.master.baselib.bean.face

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 *
 * @author :     xiemingrui
 * @since :      2019/12/10
 */
class BabyReportInfo :Serializable{
    @SerializedName("baby_image_url")
    var baby_image_url: String = ""
    @SerializedName("gender")
    var gender: String = ""
}
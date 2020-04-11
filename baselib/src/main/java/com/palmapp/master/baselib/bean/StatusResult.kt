package com.palmapp.master.baselib.bean

import android.text.TextUtils
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 *
 * @author :     xiemingrui
 * @since :      2019/8/9
 */
class StatusResult :Serializable{
    @SerializedName("status_code")
    var status_code: String = ""

    @SerializedName("message")
    var message: String? = null

    fun isSuccess():Boolean = TextUtils.equals(status_code,"SUCCESS")
}
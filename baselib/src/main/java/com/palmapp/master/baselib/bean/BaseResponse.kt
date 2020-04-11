package com.palmapp.master.baselib.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 *
 * @author :     xiemingrui
 * @since :      2019/8/2
 */
const val RESPONSE_SUCCESS = "SUCCESS"
const val RESPONSE_FAIL = "FAIL"
const val RESPONSE_DECTECT_FAIL = "DECTECT_FAIL"    //识别手相失败
const val RESPONSE_TIME_LIMIT = "TIME_LIMIT"    //超过了识别次数
const val FACE_NOT_FOUND = "FACE_NOT_FOUND"    //识别不到人脸
const val BAD_FACE = "BAD_FACE"    //人面图片不清晰
const val TEMPLATE_NOT_FOUND = "TEMPLATE_NOT_FOUND"    //模板没找到

open class BaseResponse :Serializable{
    @SerializedName("status_result")
    var status_result: StatusResult? = null

    fun isSuccess() = status_result?.isSuccess()
}
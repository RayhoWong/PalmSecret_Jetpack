package com.palmapp.master.baselib.bean.palm

import com.google.gson.annotations.SerializedName
import com.palmapp.master.baselib.bean.BaseResponse

/**
 *
 * @author :     xiemingrui
 * @since :      2019/8/9
 */
class IdentityResponseV2 : BaseResponse() {
    @SerializedName("data")
    var data: ArrayList<PalmContentDto>? = null
}
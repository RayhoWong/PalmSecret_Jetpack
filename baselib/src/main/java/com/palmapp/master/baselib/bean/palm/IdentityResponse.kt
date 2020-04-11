package com.palmapp.master.baselib.bean.palm

import com.google.gson.annotations.SerializedName
import com.palmapp.master.baselib.bean.BaseResponse

/**
 *
 * @author :     xiemingrui
 * @since :      2019/8/9
 */
class IdentityResponse : BaseResponse() {
    @SerializedName("hand_infos")
    var hand_infos: ArrayList<HandleInfo>? = null
}
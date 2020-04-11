package com.palmapp.master.baselib.bean.face

import com.google.gson.annotations.SerializedName
import com.palmapp.master.baselib.bean.BaseResponse

/**
 *
 * @author :     xiemingrui
 * @since :      2019/12/10
 */
class BabyGenerateResponse : BaseResponse() {
    @SerializedName("baby_report")
    var babyReportInfo: BabyReportInfo? = null

}
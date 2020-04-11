package com.palmapp.master.baselib.bean.face

import com.google.gson.annotations.SerializedName
import com.palmapp.master.baselib.bean.BaseResponse

/**
 *
 * @author :     huangweihao
 * @since :      2020/2/27
 */
class FaceIdentityResponse2 : BaseResponse() {
    @SerializedName("errcode")
    var errcode: Int? = null //错误码   0 ：成功  其他值为失败

    @SerializedName("errmsg")
    var errmsg: String? = null //错误信息   可能的错误信息有：“No Face Detected!” 人脸检测不到

    @SerializedName("coords")
    var coords: List<FaceRectangle2>? = null //人脸坐标点列表

    @SerializedName("attrs")
    var attrs: List<FaceInfo2>? = null //人脸检测信息	age：年龄；gender：性别；ethnicity：人种(WHITE BLACK INDIA ASIAN Others)

}
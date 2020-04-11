package com.palmapp.master.baselib.bean.gender

import com.google.gson.annotations.SerializedName
import com.palmapp.master.baselib.bean.BaseRequest
import com.palmapp.master.baselib.bean.S3ImageInfo
import com.palmapp.master.baselib.bean.face.FaceRectangle

/**
 *
 * @author :     huangweihao
 * @since :      2019/12/6
 * 变性请求体
 */
class GenderRequest : BaseRequest() {
    @SerializedName("image")
    var image: S3ImageInfo? = null //人脸图片信息，参考S3ImageInfo类

    @SerializedName("face_rectangle")
    var face_rectangle: FaceRectangle? = null //人脸位置	非空

    @SerializedName("gender")
    var gender: String? = null //用户性别	非空, F-女，M-男

    @SerializedName("ethnicity")
    var ethnicity: Int? = null  //种族id 非空,1 白色人种,2 黄色人种,3 西班牙，4 拉丁美洲,5 黑色人种 ,6 其他

    @SerializedName("tag")
    var tag: Int? = null //标签id 非空

    @SerializedName("time_limit")
    var time_limit: Boolean? = null //服务器是否限制调用次数
}
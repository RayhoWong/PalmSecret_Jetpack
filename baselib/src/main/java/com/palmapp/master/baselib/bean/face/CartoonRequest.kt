package com.palmapp.master.baselib.bean.face

import com.google.gson.annotations.SerializedName
import com.palmapp.master.baselib.bean.BaseRequest
import com.palmapp.master.baselib.bean.S3ImageInfo
import com.palmapp.master.baselib.bean.face.FaceRectangle

/**
 *
 * @author :     huangweihao
 * @since :      2020/2/27
 * 卡通请求体
 */
class CartoonRequest : BaseRequest() {
    @SerializedName("img_base64")
    var img_base64: String? = null // 图片的base64格式

    @SerializedName("img_url")
    var img_url: String? = null //图片的完整url
//      注：img_base64 和 img_url 两者传一个就行。
////    如果都传，则优先 img_base64

    @SerializedName("race")
    var race: Int? = null //人种编号	可为空。为空时默认为0。

    @SerializedName("style")
    var style: Int? = null  //风格编号	 可为空。为空时默认为1。

    @SerializedName("flag")
    var flag: Int? = null //是否返回保留原背景的卡通效果	 可为空。为空时默认为0

    @SerializedName("child_flag")
    var child_flag: Int? = null //0代表成年人，1代表儿童		 可为空。为空时默认为0

    @SerializedName("package_name")
    var package_name: String = "com.palmsecret.horoscope" //必填，不允许为空
}
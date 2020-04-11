package com.palmapp.master.baselib.bean.face

import com.google.gson.annotations.SerializedName
import com.palmapp.master.baselib.bean.BaseRequest
import com.palmapp.master.baselib.bean.S3ImageInfo
import com.palmapp.master.baselib.bean.face.FaceRectangle

/**
 *
 * @author :     huangweihao
 * @since :      2020/4/1
 * 卡通人像请求体
 */
class CartoonFaceRequest : BaseRequest() {
    /**
     * 非空
     * 图片s3信息,参考S3ImageInfo类
     * key格式: image/cartoon/report/日期yyyyMMdd/{did}/时间戳+随机数.jpg
     * did:用户设备id
     */
    @SerializedName("image")
    var image: S3ImageInfo? = null

    @SerializedName("cartoon_param")
    var cartoon_param: CartoonParam? = null // 非空	卡通参数，具体参看CartoonParam项	非空

    @SerializedName("time_limit")
    var time_limit: Boolean = false // 服务器是否限制调用次数	默认true, 每人每天限制10次调用
}
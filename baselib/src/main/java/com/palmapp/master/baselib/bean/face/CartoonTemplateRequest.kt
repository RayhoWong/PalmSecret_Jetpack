package com.palmapp.master.baselib.bean.face

import com.google.gson.annotations.SerializedName
import com.palmapp.master.baselib.bean.BaseRequest
import com.palmapp.master.baselib.bean.S3ImageInfo
import com.palmapp.master.baselib.bean.face.FaceRectangle

/**
 *
 * @author :     huangweihao
 * @since :      2020/4/1
 * 卡通模板请求体
 */
class CartoonTemplateRequest : BaseRequest() {
    @SerializedName("module_id")
    var module_id: Int? = null // 模块id	需要运营在后台配置模块数据
}
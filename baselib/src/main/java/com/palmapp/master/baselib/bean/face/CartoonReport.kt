package com.palmapp.master.baselib.bean.face

/**
 * Created by huangweihao on 2020/4/1.
 * 卡通报告
 */
data class CartoonReport(
    var report_id: String,//报告ID
    var author_image_url: String, //非空  用户的上传的图片地址
    var cartoon_image_url: String //非空  卡通图片地址
)
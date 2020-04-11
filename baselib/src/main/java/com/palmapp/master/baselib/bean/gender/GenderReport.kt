package com.palmapp.master.baselib.bean.gender

/**
 * Created by huangweihao on 2019/12/6.
 * GenderReportDTO变性预测报告
 */
 data class GenderReport(
    val gender_image_url: String, //非空	变性合成图
    val author_image_url: String, //非空	作者图片
    val ethnicity: String, //非空	人种,1 白色人种,2 黄色人种,3 西班牙，4 拉丁美洲,5 黑色人种 ,6 其他
    val original_gender: String, //可空	原始性别,F-女，M-男
    val targe_gender: String //非空	目标性别,F-女，M-男
)
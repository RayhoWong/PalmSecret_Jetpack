package com.palmapp.master.baselib.bean.face

import com.google.gson.annotations.SerializedName

/**
 * Created by huangweihao on 2020/4/1.
 * 卡通参数
 */
class CartoonParam{
    @SerializedName("template_id")
    var template_id: Int = 0 //非空	模板id	由卡通模板接口下发

    @SerializedName("gender")
    var gender: String? = null //非空	性别	F-女，M-男

    @SerializedName("ethnicity")
    var ethnicity: Int = 0 //非空	种族	人种,1 白色人种,2 黄色人种,3 西班牙，4 拉丁美洲,5 黑色人种 ,6 其他,7 印第安,8中东,

    @SerializedName("child_flag")
    var child_flag: Int = 0 //可为空	0代表成年人，1代表儿童	为空时默认为0
}
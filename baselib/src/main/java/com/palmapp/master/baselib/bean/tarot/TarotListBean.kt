package com.palmapp.master.baselib.bean.tarot

/**
 * Created by huangweihao on 2019/8/19.
 * 塔罗牌列表
 */
data class TarotListBean(
    val card_key: String,
    val type: Int,
    val name: String,
    val keyword: String,
    val yes_no_status: Boolean,

    var position: Int, // 0: 逆位  1：正位
    var kind: String, //被选中塔罗牌的种类
    var content: String //卡牌所匹配的解析内容
)
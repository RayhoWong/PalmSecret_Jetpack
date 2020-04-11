package com.palmapp.master.module_ad

import com.palmapp.master.baselib.IView

/**
 *
 * @author :     xiemingrui
 * @since :      2020/3/12
 */
interface IHotRatingView :IView{
    /**
     * 没有好评引导时的回调，一般用于关闭activity和打开退出功能广告
     */
    fun withoutHotRating()
}
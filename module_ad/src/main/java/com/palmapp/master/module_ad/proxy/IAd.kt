package com.palmapp.master.module_ad.proxy

/**
 *  各种类型广告代理类
 * @author :     xiemingrui
 * @since :      2019/8/6
 */
interface IAd {
    //展示广告
    fun show()

    //广告销毁
    fun destroy()

    //是否加载成功
    fun isLoadSuccess():Boolean

    fun getAd():Any

}
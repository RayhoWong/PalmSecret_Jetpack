package com.palmapp.master.baselib.proxy

import com.alibaba.android.arouter.facade.template.IProvider

/**
 *  google服务相关
 * @author :     xiemingrui
 * @since :      2019/8/1
 */
interface IGoogleService : IProvider {
    fun getGoogleAdvertisingId(): String
}
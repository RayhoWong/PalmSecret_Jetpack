package com.palmapp.master.baselib.proxy

/**
 *
 * @author :     xiemingrui
 * @since :      2019/8/23
 */
interface OnBillingCallBack {
    fun onPaySuccess(sku: String)

    fun onPayError(code: Int)
}
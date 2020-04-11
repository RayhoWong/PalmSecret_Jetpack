package com.palmapp.master.module_ad.proxy

import com.palmapp.master.baselib.proxy.BillingServiceProxy
import com.palmapp.master.baselib.utils.LogUtil

/**
 *
 * @author :     xiemingrui
 * @since :      2019/8/6
 */
class AdProxy {
    private val TAG = "AdProxy"
    private var isShowing = false   //是否正在展示
    var mAdObject: IAd? = null
        private set

    fun setAdObject(adObject: Any?) {
        isShowing = false
        mAdObject = AdFactory.createAdProxy(adObject)
        LogUtil.d(TAG, "AdProxy show ${mAdObject?.toString()}")
    }

    fun show(): Boolean {
        if (mAdObject == null || isShowing || BillingServiceProxy.isVip()) {
            return false
        }

        isShowing = true
        mAdObject?.show()
        return true
    }

    fun destroy() {
        if (mAdObject == null || !isShowing) {
            return
        }

        mAdObject?.destroy()
    }

    fun isLoadSuccess(): Boolean {
        if (BillingServiceProxy.isVip()) {
            return false
        }
        if (mAdObject == null || isShowing) {
            return false
        }
        return mAdObject?.isLoadSuccess() ?: false
    }

}
package com.palmapp.master.module_pay

import android.content.Context
import android.text.TextUtils
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.annotation.Interceptor
import com.alibaba.android.arouter.facade.callback.InterceptorCallback
import com.alibaba.android.arouter.facade.template.IInterceptor
import com.alibaba.android.arouter.launcher.ARouter
import com.palmapp.master.baselib.constants.AppConstants
import com.palmapp.master.baselib.constants.RouterConstants
import java.lang.Exception

/**
 *
 * @author :     xiemingrui
 * @since :      2019/8/23
 */
//@Interceptor(priority = 1)
//class BillingInterceptor : IInterceptor {
//    override fun process(postcard: Postcard?, callback: InterceptorCallback?) {
//        if (postcard != null) {
//            if (TextUtils.equals(postcard.path, RouterConstants.ACTIVITY_PAY) && BillingManager.isVip()) {
//                callback?.onInterrupt(Exception("已经是vip"))
//            } else if ((postcard.tag == AppConstants.NEED_SUBSCRIBE || postcard.tag == AppConstants.NEED_SUBSCRIBE_AND_CURRENT) && !BillingManager.isVip()) {
//                    if (postcard.tag == AppConstants.NEED_SUBSCRIBE_AND_CURRENT) {
//                        postcard.tag = null
//                        callback?.onContinue(postcard)
//                    }
//                } else {
//                    postcard.tag = null
//                    callback?.onContinue(postcard)
//                }
//        } else {
//            callback?.onContinue(postcard)
//        }
//    }
//
//    override fun init(context: Context?) {
//    }
//
//}
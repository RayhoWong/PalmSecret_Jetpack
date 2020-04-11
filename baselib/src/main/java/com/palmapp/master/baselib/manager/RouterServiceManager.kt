package com.palmapp.master.baselib.manager

import android.app.Activity
import android.content.Intent
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.facade.callback.NavigationCallback
import com.alibaba.android.arouter.facade.template.IProvider
import com.alibaba.android.arouter.launcher.ARouter
import com.palmapp.master.baselib.constants.AppConstants
import com.palmapp.master.baselib.constants.RouterConstants

/**
 *
 * @author :     xiemingrui
 * @since :      2019/8/1
 */
object RouterServiceManager {
    fun <T : IProvider> providerService(path: String): T? {
        return ARouter.getInstance().build(path).navigation() as T?
    }

    fun Activity.goHomeAndClearTop(block: () -> Unit={}) {
        ARouter.getInstance().build(RouterConstants.ACTIVITY_APP_MAIN)
            .withFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            .navigation(this,object :NavigationCallback{
                override fun onLost(postcard: Postcard?) {
                }

                override fun onFound(postcard: Postcard?) {
                }

                override fun onInterrupt(postcard: Postcard?) {
                }

                override fun onArrival(postcard: Postcard?) {
                    block()
                }

            })
    }
}
package com.palmapp.master.module_imageloader

import android.app.Application
import com.palmapp.master.baselib.BaseModuleApp
import com.palmapp.master.baselib.utils.AppUtil
import com.palmapp.master.baselib.utils.LogUtil
import com.palmapp.master.module_imageloader.glide.GlideImageLoaderStrategy

/**
 *  图片模块app类
 * @author :     xiemingrui
 * @since :      2019/7/30
 */
class ImageLoaderModuleApp(application: Application) :
    BaseModuleApp(application) {
    override fun onCreate() {
        LogUtil.d(LogUtil.TAG_XMR, "图片框架初始化：ImageLoaderModuleApp")
        ImageLoader.init(GlideImageLoaderStrategy())
    }

    override fun onTerminate() {
    }

    override fun attachBaseContext() {
    }

    override fun getPriority(): Int {
        return 1
    }

}
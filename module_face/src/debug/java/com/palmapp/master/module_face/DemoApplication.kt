package com.palmapp.master.module_face

import com.palmapp.master.baselib.BaseApplication
import com.palmapp.master.baselib.LibModuleApp
import com.palmapp.master.baselib.manager.ModuleLifeManger
import com.palmapp.master.baselib.utils.LogUtil
import com.palmapp.master.module_ad.AdModuleApp
import com.palmapp.master.module_face.activity.FaceModuleApp
import com.palmapp.master.module_imageloader.ImageLoaderModuleApp

/**
 *
 * @author :     xiemingrui
 * @since :      2019/10/12
 */
class DemoApplication: BaseApplication() {
    override fun registerApp() {
        ModuleLifeManger.register(LibModuleApp(this))
        ModuleLifeManger.register(AdModuleApp(this))
        ModuleLifeManger.register(FaceModuleApp(this))
        ModuleLifeManger.register(ImageLoaderModuleApp(this))
    }

    override fun onCreate() {
        super.onCreate()
        LogUtil.d("asdsadsadas")
    }

}
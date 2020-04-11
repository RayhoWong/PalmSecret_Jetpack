package com.palmapp.master

import com.example.oldlib.old.FaceDetect
import com.example.oldlib.utils.ImageHelper
import com.palmapp.master.baselib.BaseApplication
import com.palmapp.master.baselib.LibModuleApp
import com.palmapp.master.baselib.manager.ModuleLifeManger
import com.palmapp.master.module_ad.AdModuleApp
import com.palmapp.master.module_face.activity.FaceModuleApp
import com.palmapp.master.module_imageloader.ImageLoaderModuleApp
import com.palmapp.master.module_imageloader.glide.GoGlide
import com.palmapp.master.module_palm.PalmModuleApp
import com.palmapp.master.module_pay.PayModuleApp

/**
 *
 * @author :     xiemingrui
 * @since :      2019/7/31
 */
class MainApplication : BaseApplication() {

    override fun registerApp() {
        ModuleLifeManger.register(LibModuleApp(this))
        ModuleLifeManger.register(PayModuleApp(this))
        ModuleLifeManger.register(PalmModuleApp(this))
        ModuleLifeManger.register(ImageLoaderModuleApp(this))
        ModuleLifeManger.register(AdModuleApp(this))
        ModuleLifeManger.register(MainModuleApp(this))
        ModuleLifeManger.register(FaceModuleApp(this))
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        GoGlide.get(applicationContext).onLowMemory()
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        GoGlide.get(applicationContext).onTrimMemory(level)
    }
}
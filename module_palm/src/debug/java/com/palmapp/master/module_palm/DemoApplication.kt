package com.palmapp.master.module_palm

import com.palmapp.master.baselib.BaseApplication
import com.palmapp.master.baselib.LibModuleApp
import com.palmapp.master.baselib.manager.ModuleLifeManger
import com.palmapp.master.module_ad.AdModuleApp
import com.palmapp.master.module_imageloader.ImageLoaderModuleApp

/**
 *
 * @author :     xiemingrui
 * @since :      2019/8/7
 */
class DemoApplication :BaseApplication(){
    override fun registerApp() {
        ModuleLifeManger.register(ImageLoaderModuleApp(this))
        ModuleLifeManger.register(LibModuleApp(this))
        ModuleLifeManger.register(AdModuleApp(this))
        ModuleLifeManger.register(PalmModuleApp(this))
    }

}
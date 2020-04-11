package com.palmapp.master.module_ad

import com.palmapp.master.baselib.BaseApplication
import com.palmapp.master.baselib.LibModuleApp
import com.palmapp.master.baselib.manager.ModuleLifeManger

/**
 *
 * @author :     xiemingrui
 * @since :      2019/8/7
 */
class DemoApplication :BaseApplication(){
    override fun registerApp() {
        ModuleLifeManger.register(LibModuleApp(this))
        ModuleLifeManger.register(AdModuleApp(this))
    }

}
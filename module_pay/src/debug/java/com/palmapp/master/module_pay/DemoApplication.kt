package com.palmapp.master.module_pay

import com.palmapp.master.baselib.BaseApplication
import com.palmapp.master.baselib.LibModuleApp
import com.palmapp.master.baselib.manager.ModuleLifeManger
import com.palmapp.master.module_ad.AdModuleApp

/**
 *
 * @author :     xiemingrui
 * @since :      2019/8/5
 */
class DemoApplication : BaseApplication() {

    override fun registerApp() {
        ModuleLifeManger.register(LibModuleApp(this))
        ModuleLifeManger.register(PayModuleApp(this))
        ModuleLifeManger.register(AdModuleApp(this))
    }

}
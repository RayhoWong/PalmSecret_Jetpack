package com.palmapp.master.baselib

import com.palmapp.master.baselib.constants.AppConstants
import com.palmapp.master.baselib.manager.ModuleLifeManger

/**
 *
 * @author :     xiemingrui
 * @since :      2019/8/8
 */
class LibApplication : BaseApplication() {
    override fun registerApp() {
        ModuleLifeManger.register(LibModuleApp(this))
    }

}
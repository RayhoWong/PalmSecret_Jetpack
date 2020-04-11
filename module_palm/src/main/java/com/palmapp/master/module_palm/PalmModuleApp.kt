package com.palmapp.master.module_palm

import android.app.Application
import com.palmapp.master.baselib.BaseModuleApp
import com.palmapp.master.baselib.utils.AppUtil
import com.palmapp.master.module_palm.scan.PalmImageManager

/**
 *
 * @author :     xiemingrui
 * @since :      2019/8/29
 */
class PalmModuleApp (application: Application) : BaseModuleApp(application){
    override fun onCreate() {
        if(AppUtil.isMainProcess(application)){
            PalmImageManager.initialize(application)
        }
    }

    override fun onTerminate() {
    }

    override fun attachBaseContext() {
    }

    override fun getPriority(): Int {
        return 1
    }

}
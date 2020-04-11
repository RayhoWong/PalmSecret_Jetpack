package com.palmapp.master

import android.app.Application
import com.palmapp.master.baselib.BaseModuleApp

/**
 *  闪屏页app
 * @ClassName:      MainModuleApp
 * @Description:
 * @Author:         xiemingrui
 * @CreateDate:     2019/7/24
 */
class MainModuleApp(application: Application) : BaseModuleApp(application) {
    override fun onCreate() {
    }

    override fun onTerminate() {
    }

    override fun attachBaseContext() {
    }

    override fun getPriority(): Int {
        return 0
    }
}
package com.palmapp.master.module_face.activity

import android.app.Application
import com.example.oldlib.old.FaceDetect
import com.example.oldlib.utils.ImageHelper
import com.palmapp.master.baselib.BaseModuleApp

/**
 * @Description TODO
 *
 * @Author zhangl
 * @Date 2019-08-23
 *
 */
class FaceModuleApp(application: Application) : BaseModuleApp(application) {
    override fun onCreate() {
        FaceDetect.init(application)
        ImageHelper.init(application)
    }

    override fun onTerminate() {

    }

    override fun attachBaseContext() {

    }

    override fun getPriority(): Int {
        return 0
    }

}

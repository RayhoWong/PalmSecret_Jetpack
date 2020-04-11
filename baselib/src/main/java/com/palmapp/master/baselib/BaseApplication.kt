package com.palmapp.master.baselib

import android.content.Context
import android.text.TextUtils
import androidx.multidex.MultiDexApplication
import com.palmapp.master.baselib.manager.ModuleLifeManger
import com.palmapp.master.baselib.utils.AppUtil

/**
 *
 * @author :     xiemingrui
 * @since :      2019/7/29
 */
abstract class BaseApplication : MultiDexApplication() {
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        GoCommonEnv.setApplication(this)
        registerApp()
        ModuleLifeManger.onAttachBaseContext()
    }

    override fun onCreate() {
        super.onCreate()
        ModuleLifeManger.onCreate()
    }

    override fun onTerminate() {
        super.onTerminate()
        ModuleLifeManger.onTerminate()
    }

    /**
     * 添加ModuleLifeManger
     */
    abstract fun registerApp()
}
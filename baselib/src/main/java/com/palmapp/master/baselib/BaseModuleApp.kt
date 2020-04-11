package com.palmapp.master.baselib

import android.app.Application

abstract class BaseModuleApp(val application: Application) {
    abstract fun onCreate()

    abstract fun onTerminate()

    abstract fun attachBaseContext()

    abstract fun getPriority(): Int
    //买量信息发生变化时回调
    open fun onBuyChannelUpdate(){

    }
}
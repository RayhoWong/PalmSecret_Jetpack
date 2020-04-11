package com.palmapp.master.baselib

import android.content.Context
import androidx.multidex.MultiDexApplication

class EmptyApplication : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
    }

    override fun onTerminate() {
        super.onTerminate()
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
    }
}
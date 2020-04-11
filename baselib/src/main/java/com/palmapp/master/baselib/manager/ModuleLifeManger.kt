package com.palmapp.master.baselib.manager

import com.palmapp.master.baselib.BaseModuleApp
import io.reactivex.internal.util.LinkedArrayList
import java.util.*

object ModuleLifeManger {
    private val mList = LinkedList<BaseModuleApp>()


    fun register(appBase: BaseModuleApp) {
        if (!mList.contains(appBase)) {
            mList.offer(appBase)
        }
    }

    fun onCreate() {
        mList.forEach {
            it.onCreate()
        }
    }

    fun onTerminate() {
        mList.forEach {
            it.onTerminate()
        }
        mList.clear()
    }

    fun onAttachBaseContext() {
        mList.forEach {
            it.attachBaseContext()
        }
    }

    fun onBuyChannelUpdate(){
        mList.forEach {
            it.onBuyChannelUpdate()
        }
    }

}
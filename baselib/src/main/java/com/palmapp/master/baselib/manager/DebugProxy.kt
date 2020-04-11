package com.palmapp.master.baselib.manager

import com.palmapp.master.baselib.BuildConfig
import com.palmapp.master.baselib.GoCommonEnv

/**
 *
 * @ClassName:      DebugProxy
 * @Description:
 * @Author:         xiemingrui
 * @CreateDate:     2019/7/23
 */
object DebugProxy {
    fun isDebug(): Boolean {
        return BuildConfig.DEBUG
    }

    fun isOpenLog(): Boolean {
        return GoCommonEnv.isOpenLog || isDebug()
    }
}
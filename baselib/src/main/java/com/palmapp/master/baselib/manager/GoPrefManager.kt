package com.palmapp.master.baselib.manager

import android.content.Context
import java.util.HashMap

/**
 *
 * @ClassName:      GoPrefManager
 * @Description:
 * @Author:         xiemingrui
 * @CreateDate:     2019/7/23
 */
object GoPrefManager {
    private val sPrefMap = HashMap<String, GoPrefProxy>()
    private val PREF_NAME_DEFAULT = "defalut_pref"

    fun getInstance(name: String, mode: Int): GoPrefProxy {
        var bean: GoPrefProxy? = sPrefMap.get(name)
        if (bean == null || bean.mMode != mode) {
            bean = GoPrefProxy(name, mode)
            sPrefMap[name] = bean
        }
        return bean
    }

    fun getDefault(): GoPrefProxy {
        return getInstance(PREF_NAME_DEFAULT, Context.MODE_PRIVATE)
    }
}
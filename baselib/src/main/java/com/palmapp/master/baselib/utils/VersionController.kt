package com.palmapp.master.baselib.utils

import android.annotation.SuppressLint
import com.palmapp.master.baselib.GoCommonEnv
import com.palmapp.master.baselib.constants.PreConstants
import com.palmapp.master.baselib.manager.GoPrefManager

/**
 *
 * @author :     xiemingrui
 * @since :      2019/7/29
 */
@SuppressLint("StaticFieldLeak")
object VersionController {
    private val sContext = GoCommonEnv.getApplication()
    var lastVersionCode = -1
        private set
    var isNewUser = false
        private set
    var isFirstRun = false
        private set
    var newVersionFirstRun = false
        private set

    fun init() {
        val lastVersionCode = GoPrefManager.getDefault().getInt(PreConstants.Version.KEY_LAST_VERSION_CODE, -1)
        val curVersionCode = GoPrefManager.getDefault().getInt(PreConstants.Version.KEY_CURRENT_VERSION_CODE, -1)

        if (lastVersionCode == -1 && curVersionCode == -1) {
            onFirstRun()
        } else if (curVersionCode != -1 && getVersionCode() != curVersionCode) {
            onNewVersionFirstRun(curVersionCode)
        }

        if (GoPrefManager.getDefault().getInt(PreConstants.Version.KEY_LAST_VERSION_CODE, -1) == -1) {
            isNewUser = true
        }

    }

    private fun onFirstRun() {
        isFirstRun = true
        newVersionFirstRun = true
        val pref = GoPrefManager.getDefault()
        pref.putInt(PreConstants.Version.KEY_LAST_VERSION_CODE, -1)
        pref.putInt(PreConstants.Version.KEY_CURRENT_VERSION_CODE, getVersionCode())
        pref.putLong(PreConstants.Version.KEY_APP_INSTALL_TIME, System.currentTimeMillis())
        pref.commit()
    }

    private fun onNewVersionFirstRun(lastVersionCode: Int) {
        newVersionFirstRun = true
        val pref = GoPrefManager.getDefault()
        pref.putInt(PreConstants.Version.KEY_LAST_VERSION_CODE, lastVersionCode)
        pref.putInt(PreConstants.Version.KEY_CURRENT_VERSION_CODE, getVersionCode())
        pref.commit()
    }

    fun getVersionCode(): Int {
        try {
            return sContext.packageManager.getPackageInfo(sContext.packageName, 0).versionCode
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return -1
    }

    fun getVersionName(): String {
        try {
            return sContext.packageManager.getPackageInfo(sContext.packageName, 0).versionName
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return ""
    }
}
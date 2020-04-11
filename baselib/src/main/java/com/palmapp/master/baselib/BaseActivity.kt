package com.palmapp.master.baselib

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.TypedArray
import android.os.Build
import android.os.Bundle
import com.palmapp.master.baselib.manager.PermissionManager
import com.palmapp.master.baselib.statistics.BaseSeq103OperationStatistic
import com.palmapp.master.baselib.utils.StatusBarUtil
import com.trello.rxlifecycle2.LifecycleProvider
import com.trello.rxlifecycle2.android.ActivityEvent
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import android.view.WindowManager


/**
 *
 * @author :     xiemingrui
 * @since :      2019/8/28
 */
open class BaseActivity : RxAppCompatActivity() {
    private var isTranslucentOrFloating = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isTranslucentOrFloating = isTranslucentOrFloating()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
        if (window.attributes.flags and WindowManager.LayoutParams.FLAG_FULLSCREEN != WindowManager.LayoutParams.FLAG_FULLSCREEN) {
            StatusBarUtil.setRootViewFitsSystemWindows(this, false)
            StatusBarUtil.setTranslucentStatus(this)
            StatusBarUtil.setStatusBarColor(this, 0x26000000)
        }
    }

    //解决8.0 activity透明并且设置方向导致崩溃的问题(8.0后谷歌修复该bug)
    override fun setRequestedOrientation(requestedOrientation: Int) {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O || !isTranslucentOrFloating) {
            super.setRequestedOrientation(requestedOrientation)
        }
    }

    private fun isTranslucentOrFloating(): Boolean {
        if (Build.VERSION.SDK_INT != Build.VERSION_CODES.O)
            return false
        var isTranslucentOrFloating = false
        try {
            val styleableRes =
                Class.forName("com.android.internal.R\$styleable").getField("Window").get(null) as IntArray
            val ta = obtainStyledAttributes(styleableRes)
            val m = ActivityInfo::class.java.getMethod("isTranslucentOrFloating", TypedArray::class.java)
            m.isAccessible = true
            isTranslucentOrFloating = m.invoke(null, ta) as Boolean
            m.isAccessible = false
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return isTranslucentOrFloating
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        PermissionManager.dealWithPermissionResult(requestCode, grantResults)
    }
}
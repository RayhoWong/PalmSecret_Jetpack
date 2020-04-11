package com.palmapp.master.baselib.permission

import com.palmapp.master.baselib.IView
import com.palmapp.master.baselib.manager.PermissionManager

/**
 *
 * @author :     xiemingrui
 * @since :      2020/3/10
 */
interface IPermissionView : IView {
    //获取到权限
    fun onPermissionGranted(@PermissionManager.PermissionCode code: Int)

    //拒绝了权限
    fun onPermissionDenied(@PermissionManager.PermissionCode code: Int)

    fun onPermissionEveryDenied(@PermissionManager.PermissionCode code: Int)
}
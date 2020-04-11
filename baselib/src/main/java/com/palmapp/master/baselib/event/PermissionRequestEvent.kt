package com.palmapp.master.baselib.event

import com.palmapp.master.baselib.manager.PermissionManager

/**
 *
 * @author :     xiemingrui
 * @since :      2019/8/8
 */
class PermissionRequestEvent(@PermissionManager.PermissionCode val requestCode: Int, val isSuccess: Boolean)

interface PermissionRequestListener {
    fun onPermissionRequest(event: PermissionRequestEvent)
}
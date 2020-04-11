package com.palmapp.master.baselib.manager

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.IntDef
import androidx.annotation.Nullable
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.palmapp.master.baselib.GoCommonEnv
import com.palmapp.master.baselib.event.PermissionRequestEvent
import com.palmapp.master.baselib.statistics.BaseSeq101OperationStatistic
import org.greenrobot.eventbus.EventBus

/**
 *
 * @author :     xiemingrui
 * @since :      2019/8/8
 */
@PermissionManager.PermissionCode const val PERMISSION_WRITE = 1
@PermissionManager.PermissionCode const val PERMISSION_CAMERA = 2

object PermissionManager {
    fun requestPermission(activity: Activity?, @PermissionCode requestCode: Int) {
        if (!isM())
            return
        if (activity == null)
            return
        if (requestCode == PERMISSION_WRITE) {
            BaseSeq101OperationStatistic.uploadOperationStatisticData(BaseSeq101OperationStatistic.storage_f000)
        } else if (requestCode == PERMISSION_CAMERA) {
            BaseSeq101OperationStatistic.uploadOperationStatisticData(BaseSeq101OperationStatistic.camera_f000)
        }
        ActivityCompat.requestPermissions(activity, arrayOf(getPermissionByCode(requestCode)), requestCode)
    }

    fun checkPermission(@PermissionCode requestCode: Int): Boolean {
        if (!isM()) {
            return true
        }
        return ContextCompat.checkSelfPermission(
            GoCommonEnv.getApplication(),
            getPermissionByCode(requestCode)
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun dealWithPermissionResult(@PermissionCode requestCode: Int, grantResults: IntArray) {
        if(grantResults.isNotEmpty()){
            val result = if(grantResults[0] == PackageManager.PERMISSION_GRANTED) "1" else "2"
            if (requestCode == PERMISSION_WRITE) {
                BaseSeq101OperationStatistic.uploadOperationStatisticData(BaseSeq101OperationStatistic.storage_a000,"",result)
            } else if (requestCode == PERMISSION_CAMERA) {
                BaseSeq101OperationStatistic.uploadOperationStatisticData(BaseSeq101OperationStatistic.camera_a000,"",result)
            }
        }
        EventBus.getDefault().post(
            PermissionRequestEvent(
                requestCode,
                grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
            )
        )
    }

    private fun getPermissionByCode(@PermissionCode requestCode: Int): String {
        var permission = ""
        when (requestCode) {
            PERMISSION_WRITE -> permission = Manifest.permission.WRITE_EXTERNAL_STORAGE
            PERMISSION_CAMERA -> permission = Manifest.permission.CAMERA
        }

        return permission
    }

    private fun isM() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M

    @IntDef(PERMISSION_CAMERA, PERMISSION_WRITE)
    @Retention(AnnotationRetention.SOURCE)
    annotation class PermissionCode
}
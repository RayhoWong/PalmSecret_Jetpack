package com.palmapp.master.baselib.permission

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.util.SparseArray
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.util.contains
import com.palmapp.master.baselib.BaseActivity
import com.palmapp.master.baselib.BaseMultiplePresenter
import com.palmapp.master.baselib.GoCommonEnv
import com.palmapp.master.baselib.R
import com.palmapp.master.baselib.event.PermissionRequestEvent
import com.palmapp.master.baselib.manager.PERMISSION_CAMERA
import com.palmapp.master.baselib.manager.PERMISSION_WRITE
import com.palmapp.master.baselib.manager.PermissionManager
import com.palmapp.master.baselib.utils.PermissionUtil
import com.palmapp.master.baselib.view.CameraPermissionGuideDialog
import com.palmapp.master.baselib.view.CommonDialog
import com.palmapp.master.baselib.view.StoragePermissionGuideDialog
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

/**
 *
 * @author :     xiemingrui
 * @since :      2020/3/10
 */
class PermissionPresenter(private val activity: Activity) : BaseMultiplePresenter<IPermissionView>() {
    private val permissionMap = hashMapOf<Int, String>()
    private var delayPermission = -1

    init {
        permissionMap.put(PERMISSION_CAMERA, Manifest.permission.CAMERA)
        permissionMap.put(PERMISSION_WRITE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    private val mCallback = SparseArray<ArrayList<IPermissionView>>()

    override fun onAttach(pView: IPermissionView, context: Context) {
        super.onAttach(pView, context)
        EventBus.getDefault().register(this)
    }

    override fun onDetach() {
        super.onDetach()
        EventBus.getDefault().unregister(this)
        mCallback.clear()
    }

    override fun onRestart() {
        super.onRestart()
        if (delayPermission == -1)
            return
        if (ContextCompat.checkSelfPermission(activity, permissionMap.get(delayPermission)!!) == PackageManager.PERMISSION_GRANTED) {
            onPermissionRequest(PermissionRequestEvent(delayPermission, true))
        } else {
            Toast.makeText(activity, R.string.camera_permission_not_granted, Toast.LENGTH_SHORT)
                .show()
        }
        delayPermission = -1
    }

    fun registerCallback(code: Int, view: IPermissionView) {
        if (mCallback.contains(code)) {
            mCallback.get(code).add(view)
        } else {
            val list = ArrayList<IPermissionView>(1)
            list.add(view)
            mCallback.put(code, list)
        }
    }

    fun checkPermission(@PermissionManager.PermissionCode permission: Int): Boolean {
        return ContextCompat.checkSelfPermission(
            GoCommonEnv.getApplication(),
            permissionMap.get(permission)!!
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun requestPermission(@PermissionManager.PermissionCode permission: Int) {
        if (checkPermission(permission)) {
            return
        }

        ActivityCompat.requestPermissions(activity, arrayOf(permissionMap.get(permission)!!), permission)
    }

    @Subscribe
    fun onPermissionRequest(event: PermissionRequestEvent) {
        if (event.isSuccess) {
            getView()?.onPermissionGranted(event.requestCode)
        } else if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, permissionMap.get(event.requestCode)!!)) {
            getView()?.onPermissionEveryDenied(event.requestCode)
        } else {
            getView()?.onPermissionDenied(event.requestCode)
        }
        val callbacks = mCallback.get(event.requestCode)
        callbacks.forEach {
            if (event.isSuccess) {
                it.onPermissionGranted(event.requestCode)
            } else if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, permissionMap.get(event.requestCode)!!)) {
                it.onPermissionEveryDenied(event.requestCode)
            } else {
                it.onPermissionDenied(event.requestCode)
            }
        }
    }

    fun showEveryDeniedPermissionDialog(code: Int) {
        if (code == PERMISSION_WRITE) {
            Toast.makeText(activity, R.string.storage_permission_not_granted, Toast.LENGTH_SHORT)
                .show()
            val dialog = StoragePermissionGuideDialog(activity)
            dialog.setOnClickBottomListener(object : CommonDialog.OnClickBottomListener {
                override fun onPositiveClick() {
                    delayPermission = code
                    PermissionUtil.GoToSetting(activity)
                }

                override fun onNegativeClick() {
                }
            })
            dialog.show()
        } else {
            Toast.makeText(activity, R.string.camera_permission_not_granted, Toast.LENGTH_SHORT)
                .show()
            val dialog = CameraPermissionGuideDialog(activity)
            dialog.setOnClickBottomListener(object : CommonDialog.OnClickBottomListener {

                override fun onPositiveClick() {
                    delayPermission = code
                    PermissionUtil.GoToSetting(activity)
                }

                override fun onNegativeClick() {
                }
            })
            dialog.show()
        }

    }
}
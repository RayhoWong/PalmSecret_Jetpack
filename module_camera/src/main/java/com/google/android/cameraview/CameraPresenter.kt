package com.google.android.cameraview

import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.hardware.Sensor
import android.hardware.SensorManager
import android.view.View
import com.google.android.cameraview.CameraView.FACING_BACK
import com.google.android.cameraview.CameraView.FACING_FRONT
import com.palmapp.master.baselib.BaseMultiplePresenter
import com.palmapp.master.baselib.manager.PERMISSION_CAMERA
import com.palmapp.master.baselib.permission.IPermissionView
import com.palmapp.master.baselib.permission.PermissionPresenter
import com.palmapp.master.baselib.utils.AppUtil
import com.palmapp.master.baselib.utils.LogUtil

/**
 *
 * @author :     xiemingrui
 * @since :      2020/3/10
 */
class CameraPresenter(val permission: PermissionPresenter, val isRotate: Boolean = false) :
    BaseMultiplePresenter<IPhotoSelectorView>(),
    IPermissionView {
    private lateinit var cameraView: CameraView

    private var mSensorManager: SensorManager? = null
    private var mOrientationListener: OrientationListener? = null
    private var orientation: Int = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT


    private fun initSensor() {
        mSensorManager = getContext()?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mOrientationListener = OrientationListener(
            OrientationListener.OnOrientationChangeListener { newOrientation: Int ->
                orientation = newOrientation
            }
        )

        mSensorManager!!.registerListener(
            mOrientationListener,
            mSensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    private val mCallback = object : CameraView.Callback() {

        override fun onCameraOpened(cameraView: CameraView) {
        }

        override fun onCameraClosed(cameraView: CameraView) {
        }

        override fun onPictureTaken(cameraView: CameraView, data: ByteArray) {
            cameraView.isClickable = true
            val option = BitmapFactory.Options()
            option.inJustDecodeBounds = true
            BitmapFactory.decodeByteArray(data, 0, data.size, option)
            option.inSampleSize =
                AppUtil.calculateInSampleSize(option, cameraView.width, cameraView.height)
            option.inJustDecodeBounds = false
            val bitmap = BitmapFactory.decodeByteArray(data, 0, data.size, option)
            LogUtil.d("CameraPresenter", "拍照成功:width = ${bitmap.width} height = ${bitmap.height}")
            val matrix = Matrix()
            var rotate = 0f
            if (bitmap.width > bitmap.height) {
                rotate = if (cameraView.facing == CameraView.FACING_FRONT) 270f else 90f

            }
            if (isRotate) {
                if (orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                    if (cameraView.facing == CameraView.FACING_FRONT) {
                        rotate += 90
                    } else {
                        rotate -= 90
                    }
                } else if (orientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
                    if (cameraView.facing == CameraView.FACING_FRONT) {
                        rotate -= 90
                    } else {
                        rotate += 90
                    }
                }
                LogUtil.d(this.javaClass.simpleName, "rotate:$rotate")
            }
            if (rotate != 0f) {
                matrix.postRotate(rotate)
            }
            if (cameraView.facing == CameraView.FACING_FRONT) {
                matrix.postScale(-1f, 1f)
            }


            getView()?.showPictureView(
                Bitmap.createBitmap(
                    bitmap,
                    0,
                    0,
                    bitmap.getWidth(),
                    bitmap.getHeight(),
                    matrix, false
                )
            )
        }
    }

    override fun onAttach(pView: IPhotoSelectorView, context: Context) {
        super.onAttach(pView, context)
        permission.registerCallback(PERMISSION_CAMERA, this)
        initSensor()
    }

    override fun onDetach() {
        super.onDetach()
        cameraView.removeCallback(mCallback)
        cameraView.stop()
        mSensorManager?.unregisterListener(mOrientationListener);
    }

    fun bindCameraView(view: CameraView) {
        cameraView = view
        cameraView.addCallback(mCallback)
    }

    fun takePicture() {
        cameraView.isClickable = false
        try {
            val result = cameraView.takePicture()
            if (!result) {
                cameraView.isClickable = true
            }
        } catch (e: Exception) {
        }
    }

    fun cancelPicture() {
        getView()?.showNormalView()
    }

    fun setFace(@CameraView.Facing facing: Int) {
        cameraView.facing = facing
    }

    fun changeFace() {
        cameraView.facing = if (cameraView.facing == FACING_FRONT) FACING_BACK else FACING_FRONT
    }

    fun startPreview() {
        if (permission.checkPermission(PERMISSION_CAMERA)) {
            cameraView.start()
            cameraView.visibility = View.VISIBLE
        } else {
            permission.requestPermission(PERMISSION_CAMERA)
        }
    }

    fun stopPreview() {
        cameraView.stop()
        cameraView.visibility = View.INVISIBLE
    }

    override fun onPermissionGranted(code: Int) {
        startPreview()
    }

    override fun onPermissionDenied(code: Int) {

    }

    override fun onPermissionEveryDenied(code: Int) {
        permission.showEveryDeniedPermissionDialog(PERMISSION_CAMERA)
    }
}
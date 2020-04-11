package com.palmapp.master.module_palm.match;

import android.Manifest
import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.hardware.Camera
import android.view.Surface
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.palmapp.master.baselib.BasePresenter
import com.palmapp.master.baselib.GoCommonEnv
import com.palmapp.master.baselib.R
import com.palmapp.master.baselib.event.PermissionRequestEvent
import com.palmapp.master.baselib.event.PermissionRequestListener
import com.palmapp.master.baselib.manager.PERMISSION_CAMERA
import com.palmapp.master.baselib.manager.PERMISSION_WRITE
import com.palmapp.master.baselib.manager.PermissionManager
import com.palmapp.master.baselib.manager.ThreadExecutorProxy
import com.palmapp.master.baselib.utils.AppUtil
import com.palmapp.master.baselib.utils.FileUtils
import com.palmapp.master.baselib.utils.PermissionUtil
import com.palmapp.master.baselib.view.CameraPermissionGuideDialog
import com.palmapp.master.baselib.view.CommonDialog
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.io.File

class PalmMatchPresenter(val path: String) : BasePresenter<PalmMatchView>(), PermissionRequestListener {
    var bitmap: Bitmap? = null
    var top = 0
    var orientation = 0
    private var mParameters: Camera.Parameters? = null
    private var mCamera: Camera? = null
    override fun onAttach() {
        EventBus.getDefault().register(this)
        //要跟ScanView的sTop一样，为了截取图片
        top = getView()?.getContext()?.resources?.getDimensionPixelSize(R.dimen.change_168px) ?: 0
    }

    override fun onDetach() {
        EventBus.getDefault().unregister(this)
    }

    fun stopScan() {
        try {
            mCamera?.stopPreview()
            mCamera?.release()
        } catch (e: Exception) {
        }
    }

    //开始准备摄像机
    fun startScan() {
        if (!PermissionManager.checkPermission(PERMISSION_CAMERA)) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(getView()?.getContext() as Activity, Manifest.permission.CAMERA)){
                Toast.makeText(getView()?.getContext() as Activity, R.string.camera_permission_not_granted, Toast.LENGTH_SHORT)
                    .show()
                val dialog = CameraPermissionGuideDialog(getView()?.getContext() as Activity)
                dialog.setOnClickBottomListener(object : CommonDialog.OnClickBottomListener{

                    override fun onPositiveClick() {
                        PermissionUtil.GoToSetting(getView()?.getContext() as Activity)
                    }

                    override fun onNegativeClick() {
                        (getView()?.getContext() as Activity).finish()
                    }
                })
                dialog.show()
            }else{
                PermissionManager.requestPermission(getView()?.getContext() as Activity, PERMISSION_CAMERA)
            }
            return
        }
        if (!PermissionManager.checkPermission(PERMISSION_WRITE)) {
            PermissionManager.requestPermission(getView()?.getContext() as Activity, PERMISSION_WRITE)
            return
        }
        safeCameraOpen(Camera.CameraInfo.CAMERA_FACING_BACK)
    }

    private var safeToTakePicture = true
    //拍照
    fun takePhoto() {
        System.gc()
        System.gc()
        if (safeToTakePicture) {
            safeToTakePicture = false
            mCamera?.let {
                try {
                    it.takePicture({}, null, { data, camera ->
                        try {
                            it.startPreview()
                            savePic(data)
                        } catch (e: Exception) {

                        } finally {
                            safeToTakePicture = true
                        }
                    })
                } catch (e: Exception) {

                } finally {
                    safeToTakePicture = true
                }
            }
        }
    }

    private fun savePic(data: ByteArray?) {
        ThreadExecutorProxy.execute(Runnable {
            try {
                val screenW = AppUtil.getScreenW(getView()?.getContext())
                val screenH = AppUtil.getScreenH(getView()?.getContext())
                val opts = BitmapFactory.Options()
                opts.inPreferredConfig = Bitmap.Config.RGB_565
                opts.inJustDecodeBounds = true
                BitmapFactory.decodeByteArray(data, 0, data?.size ?: 0, opts)
                opts.inSampleSize = AppUtil.calculateInSampleSize(opts, screenW, screenH)
                opts.inJustDecodeBounds = false
                bitmap = BitmapFactory.decodeByteArray(data, 0, data?.size ?: 0, opts)
                bitmap = rotate(bitmap, orientation.toFloat())
                bitmap?.let { src ->
                    bitmap = if ((top + src.width) > src.height) {
                        Bitmap.createBitmap(src, 0, 0, src.width, src.height)
                    } else {
                        Bitmap.createBitmap(src, 0, top, src.width, src.width)
                    }
                }
                FileUtils.writeBitmap(path, bitmap)
                bitmap?.recycle()
                ThreadExecutorProxy.runOnMainThread(Runnable {
                    getView()?.next()
                })
            } catch (e: Exception) {
                e.printStackTrace()
            }
        })
    }

    //旋转
    private fun rotate(rawBitmap: Bitmap?, degree: Float): Bitmap? {
        rawBitmap?.let {
            val matrix = Matrix()
            matrix.postRotate(degree)
            return Bitmap.createBitmap(it, 0, 0, it.width, it.height, matrix, true)
        }
        return null
    }

    private fun safeCameraOpen(id: Int): Boolean {
        if (supportCameraFacing(id)) {
            try {
                mCamera = Camera.open(id)
                getView()?.getCamera(mCamera)
                initParameters(mCamera)          //初始化相机配置信息
            } catch (e: Exception) {
                e.printStackTrace()
                return false
            }
        }
        return true
    }


    //判断是否支持某个相机
    private fun supportCameraFacing(cameraFacing: Int): Boolean {
        try {
            var info = Camera.CameraInfo()
            for (i in 0 until Camera.getNumberOfCameras()) {
                Camera.getCameraInfo(i, info)
                if (info.facing == cameraFacing) return true
            }
            return false
        } catch (e: java.lang.Exception) {

        }
        return true
    }

    //配置相机参数
    private fun initParameters(camera: Camera?) {
        camera?.apply {
            try {
                mParameters = camera.parameters
                mParameters?.previewFormat = ImageFormat.NV21   //设置预览图片的格式
                //获取与指定宽高相等或最接近的尺寸
                //设置预览尺寸
                val screenW = AppUtil.getScreenW(getView()?.getContext())
                val screenH = AppUtil.getScreenH(getView()?.getContext())
                val previewSize = getBestSize(screenW, screenH, mParameters!!.supportedPreviewSizes)
                previewSize?.let {
                    mParameters?.setPreviewSize(it.width, it.height)
                }
                val pictureSize = getBestSize(screenW, screenH, mParameters!!.supportedPictureSizes)
                pictureSize?.let {
                    mParameters?.setPictureSize(it.width, it.height)
                }
                //对焦模式
                if (mParameters?.supportedFocusModes?.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE) == true) {
                    //对焦模式
                    mParameters?.focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE
                }
                camera.parameters = mParameters
                orientation = getCameraDisplayOrientation(getView()?.getContext() as Activity)
                camera.setDisplayOrientation(orientation)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    //获取与指定宽高相等或最接近的尺寸
    private fun getBestSize(targetWidth: Int, targetHeight: Int, sizeList: List<Camera.Size>): Camera.Size? {
        var bestSize: Camera.Size? = null
        var targetRatio = (targetHeight.toDouble() / targetWidth)  //目标大小的宽高比
        var minDiff = targetRatio

        for (size in sizeList) {
            if (size.width == targetHeight && size.height == targetWidth) {
                bestSize = size
                break
            }
            var supportedRatio = (size.width.toDouble() / size.height)
            if (Math.abs(supportedRatio - targetRatio) < minDiff) {
                minDiff = Math.abs(supportedRatio - targetRatio)
                bestSize = size
            }
        }
        return bestSize
    }

    //设置预览旋转的角度
    private fun getCameraDisplayOrientation(activity: Activity): Int {
        var info = Camera.CameraInfo()
        var mDisplayOrientation = 0
        Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_BACK, info)
        val rotation = activity.windowManager.defaultDisplay.rotation

        var screenDegree = 0
        when (rotation) {
            Surface.ROTATION_0 -> screenDegree = 0
            Surface.ROTATION_90 -> screenDegree = 90
            Surface.ROTATION_180 -> screenDegree = 180
            Surface.ROTATION_270 -> screenDegree = 270
        }

        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            mDisplayOrientation = (info.orientation + screenDegree) % 360
            mDisplayOrientation = (360 - mDisplayOrientation) % 360          // compensate the mirror
        } else {
            mDisplayOrientation = (info.orientation - screenDegree + 360) % 360
        }
        return mDisplayOrientation
    }

    @Subscribe
    override fun onPermissionRequest(event: PermissionRequestEvent) {
        if ((event.requestCode == PERMISSION_CAMERA || event.requestCode == PERMISSION_WRITE) && event.isSuccess) {
            //开始打开摄像机
            startScan()
        }
    }
}
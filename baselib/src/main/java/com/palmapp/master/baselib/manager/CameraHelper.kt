package com.palmapp.master.baselib.manager

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.hardware.Camera
import android.view.Surface
import com.palmapp.master.baselib.GoCommonEnv
import com.palmapp.master.baselib.event.PermissionRequestEvent
import com.palmapp.master.baselib.event.PermissionRequestListener
import com.palmapp.master.baselib.utils.AppUtil
import com.palmapp.master.baselib.utils.FileUtils
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe


/**
 *
 * @author :     xiemingrui
 * @since :      2019/12/2
 */
class CameraHelper : PermissionRequestListener {
    @Subscribe
    override fun onPermissionRequest(event: PermissionRequestEvent) {
        if ((event.requestCode == PERMISSION_CAMERA || event.requestCode == PERMISSION_WRITE) && event.isSuccess) {
            //开始打开摄像机
            initCamera()
        }
    }

    private var bitmap: Bitmap? = null
    private var orientation = 0
    private var safeToTakePicture = true
    private var mParameters: Camera.Parameters? = null
    private lateinit var activity: Activity
    private lateinit var callback: OnTakePhotoCallBack
    private var mCamera: Camera? = null
    private var cameraFacing = Camera.CameraInfo.CAMERA_FACING_FRONT

    fun release() {
        if(EventBus.getDefault().isRegistered(this))
        EventBus.getDefault().unregister(this)
        mCamera?.release()
        bitmap = null
    }

    fun startCamera(a: Activity, c: OnTakePhotoCallBack, face: Int = Camera.CameraInfo.CAMERA_FACING_FRONT) {
        activity = a
        callback = c
        cameraFacing = face
        if(!EventBus.getDefault().isRegistered(this))
        EventBus.getDefault().register(this)
        initCamera()
    }

    private fun initCamera() {
        if (hasPermission(activity)) {
            if (safeCameraOpen(cameraFacing)) {
                callback.showCamera(mCamera)
            } else {
                callback.showCamera(null)
            }
        }
    }

    //拍照
    fun takePhoto(path: String) {
        System.gc()
        System.gc()
        if (safeToTakePicture) {
            safeToTakePicture = false
            mCamera?.let {
                try {
                    it.takePicture({}, null, { data, camera ->
                        try {
                            it.startPreview()
                            savePic(data, path)
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

    private fun savePic(data: ByteArray?, path: String) {
        ThreadExecutorProxy.execute(Runnable {
            try {
                val screenW = AppUtil.getScreenW(activity)
                val screenH = AppUtil.getScreenH(activity)
                val opts = BitmapFactory.Options()
                opts.inPreferredConfig = Bitmap.Config.RGB_565
                opts.inJustDecodeBounds = true
                BitmapFactory.decodeByteArray(data, 0, data?.size ?: 0, opts)
                opts.inSampleSize = AppUtil.calculateInSampleSize(opts, screenW, screenH)
                opts.inJustDecodeBounds = false
                bitmap = BitmapFactory.decodeByteArray(data, 0, data?.size ?: 0, opts)
                if (cameraFacing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    bitmap = mirror(rotate(bitmap, 360 - orientation.toFloat()))
                }else{
                    bitmap = rotate(bitmap, orientation.toFloat())
                }
                FileUtils.writeBitmap(path, bitmap)
                ThreadExecutorProxy.runOnMainThread(Runnable {
                    callback.showBitmap(bitmap)
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

    fun mirror(rawBitmap: Bitmap?): Bitmap? {
        rawBitmap ?: return null
        var matrix = Matrix()
        matrix.postScale(-1f, 1f)
        return Bitmap.createBitmap(rawBitmap, 0, 0, rawBitmap.width, rawBitmap.height, matrix, true)
    }

    private fun safeCameraOpen(id: Int): Boolean {
        if (supportCameraFacing(id)) {
            try {
                mCamera = Camera.open(id)
                initParameters(mCamera)          //初始化相机配置信息
            } catch (e: Exception) {
                e.printStackTrace()
                return false
            }
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
                val screenW = AppUtil.getScreenW(activity)
                val screenH = AppUtil.getScreenH(activity)
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
                orientation = getCameraDisplayOrientation(activity)
                camera.setDisplayOrientation(orientation)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    //获取与指定宽高相等或最接近的尺寸
    private fun getBestSize(
        targetWidth: Int,
        targetHeight: Int,
        sizeList: List<Camera.Size>
    ): Camera.Size? {
        var bestSize: Camera.Size? = null
        var targetRatio = (targetHeight.toDouble() / targetWidth)  //目标大小的宽高比
        var minDiff = targetRatio

        for (size in sizeList) {
            if (size.width == targetHeight && size.height == targetWidth) {
                bestSize = size
                break
            }

            if(size.width<targetWidth || size.height < targetWidth){
                continue
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
        Camera.getCameraInfo(cameraFacing, info)
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
            mDisplayOrientation =
                (360 - mDisplayOrientation) % 360          // compensate the mirror
        } else {
            mDisplayOrientation = (info.orientation - screenDegree + 360) % 360
        }
        return mDisplayOrientation
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

    private fun hasPermission(activity: Activity?): Boolean {
        if (!PermissionManager.checkPermission(PERMISSION_CAMERA)) {
            PermissionManager.requestPermission(activity, PERMISSION_CAMERA)
            return false
        }
        if (!PermissionManager.checkPermission(PERMISSION_WRITE)) {
            PermissionManager.requestPermission(activity, PERMISSION_WRITE)
            return false
        }

        return true
    }

    interface OnTakePhotoCallBack {
        fun showCamera(camera: Camera?)
        fun showBitmap(bitmap: Bitmap?)
    }
}
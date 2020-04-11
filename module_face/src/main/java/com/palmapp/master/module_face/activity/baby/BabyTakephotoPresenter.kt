package com.palmapp.master.module_face.activity.baby;

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.hardware.Camera
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.palmapp.master.baselib.BaseMultiplePresenter
import com.palmapp.master.baselib.BasePresenter
import com.palmapp.master.baselib.R
import com.palmapp.master.baselib.manager.CameraHelper
import com.palmapp.master.baselib.manager.PERMISSION_CAMERA
import com.palmapp.master.baselib.manager.PERMISSION_WRITE
import com.palmapp.master.baselib.manager.PermissionManager
import com.palmapp.master.baselib.utils.AppUtil
import com.palmapp.master.baselib.utils.FileUtils
import com.palmapp.master.baselib.utils.LogUtil
import com.palmapp.master.baselib.utils.PermissionUtil
import com.palmapp.master.baselib.view.CameraPermissionGuideDialog
import com.palmapp.master.baselib.view.CommonDialog
import com.palmapp.master.module_imageloader.ImageLoaderUtils
import com.palmapp.master.module_imageloader.glide.ImageLoadingListener
import java.io.*

class BabyTakephotoPresenter(val path: String) : BaseMultiplePresenter<BabyTakephotoView>() {


    private val cameraHelper = CameraHelper()

    override fun onDetach() {
        cameraHelper.release()
    }

    fun savePhoto(cropBitmap: Bitmap?) {
        val bitmap = AppUtil.resizeBitmap(cropBitmap, 250, 250)
        FileUtils.writeBitmap(path, bitmap)
    }

}
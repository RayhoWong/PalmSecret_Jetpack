package com.palmapp.master.baselib.utils

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import com.palmapp.master.baselib.R
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import com.yanzhenjie.permission.AndPermission
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception

/**
 * author : liwenjun
 * date   : 2020/3/24 11:15
 * desc   : 图片工具类
 */
object ImageUtil {
    interface OnSaveListener {
        fun onSuccess(filePath: String)
        fun onFail(exception: Throwable)
    }


    fun save(
        context: RxAppCompatActivity,
        bitmap: Bitmap,
        fileName: String = System.currentTimeMillis().toString(),
        isSaveMedia: Boolean = true,
        listener: OnSaveListener? = null
    ) {
        AndPermission.with(context)
            .runtime()
            .permission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            .onGranted {
                //已获取权限
                saveImage(context, bitmap, fileName, isSaveMedia, listener)
            }
            .onDenied { data ->
                //判断用户是不是选中不再显示权限弹窗了，若不再显示的话提醒进入权限设置页
                if (AndPermission.hasAlwaysDeniedPermission(context, data)) { //提醒打开权限设置页
                    PermissionUtil.GoToSetting(context)
                } else {
                    Toast.makeText(context, "Permisssion Denied!", Toast.LENGTH_SHORT).show()
                }
                listener?.let {
                    it.onFail(Throwable("Permisssion Denied!"))
                }
            }.start()

    }

    private fun saveImage(
        activity: RxAppCompatActivity,
        bitmap: Bitmap,
        name: String,
        isSaveMedia: Boolean,
        listener: OnSaveListener?
    ) {
        /*val url = MediaStore.Images.Media.insertImage(
                activity.contentResolver,
                bitmap,
                System.currentTimeMillis().toString(),
                ""
            )*/
        val disposable = Observable.fromCallable {
            // 首先保存图片
            val pictureFolder = Environment.getExternalStorageDirectory()
            val appDir = File(pictureFolder, "PalmSecret")
            if (!appDir.exists()) {
                appDir.mkdir()
            }
            val fileName = "$name.jpg"
            val file = File(appDir, fileName);
            val fos = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos.flush()
            fos.close()
            // 最后通知图库更新
//            MediaStore.Images.Media.insertImage(activity.contentResolver,
//                file.absolutePath, fileName, null)
            if (isSaveMedia) {
                val uri = Uri.fromFile(file);
                activity.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri))
            }
            listener?.let {
                it.onSuccess(file.absolutePath)
            }
            isSaveMedia
        }.compose(activity.bindToLifecycle())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (isSaveMedia) {
                    Toast.makeText(activity, R.string.download_successful, Toast.LENGTH_SHORT)
                        .show()
                }
            }, {thx ->
                thx.printStackTrace()
                if (isSaveMedia) {
                    Toast.makeText(activity, R.string.download_failed, Toast.LENGTH_SHORT)
                        .show()
                    listener?.let { listener
                        listener.onFail(thx)
                    }
                }
            })
    }
}
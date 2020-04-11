package com.palmapp.master.baselib.permission

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import com.palmapp.master.baselib.BaseMultiplePresenter
import com.palmapp.master.baselib.R
import com.palmapp.master.baselib.manager.PERMISSION_WRITE
import com.palmapp.master.baselib.statistics.BaseSeq103OperationStatistic
import com.palmapp.master.baselib.utils.FileUtils
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import java.io.File

/**
 *
 * @author :     xiemingrui
 * @since :      2020/4/8
 */
class DownloadPresenter(val permissionPresenter: PermissionPresenter, val activity: Activity) : BaseMultiplePresenter<IDownloadView>(), IPermissionView {
    private var mPath = ""
    override fun onAttach(pView: IDownloadView, context: Context) {
        super.onAttach(pView, context)
        permissionPresenter.registerCallback(PERMISSION_WRITE,this)
    }
    fun startDownload(path: String) {
        mPath = path
        if (!permissionPresenter.checkPermission(PERMISSION_WRITE)) {
            permissionPresenter.requestPermission(PERMISSION_WRITE)
            return
        }
        val gif = File(mPath)
        BaseSeq103OperationStatistic
            .uploadOperationStatisticDataFor103(BaseSeq103OperationStatistic.download_func_a000, "", "1", "")
        val disposable = Observable
            .create<File> { emitter ->
                //保存目录的目标文件
                var destFile: File? = null
                if (gif.length() > 0) {
                    //保存到sdcard的自定义目录
                    val pictureFolder = Environment.getExternalStorageDirectory()
                    //第二个参数为你想要保存的目录名称
                    val appDir = File(pictureFolder, "PalmSecret/gif")
                    if (!appDir.exists()) {
                        //创建目标文件目录
                        appDir.mkdirs()
                    }
                    val fileName = System.currentTimeMillis().toString() + ".gif"
                    //创建目标文件实例
                    destFile = File(appDir, fileName)
                    //把得到图片复制到目标文件中
                    FileUtils.copyFile(gif, destFile)
                }
                emitter.onNext(destFile!!)
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(Consumer<File> { destFile ->
                if (destFile.length() > 0) {
                    Toast.makeText(activity, activity.getString(R.string.download_successful), Toast.LENGTH_LONG).show()
                    // 最后通知图库更新
                    activity.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                        Uri.fromFile(File(destFile.path))))
                } else {
                    Toast.makeText(activity, activity.getString(R.string.download_failed), Toast.LENGTH_LONG).show()
                }
            })
    }

    override fun onPermissionGranted(code: Int) {
        if (code == PERMISSION_WRITE) {
            startDownload(mPath)
        }
    }

    override fun onPermissionDenied(code: Int) {
    }

    override fun onPermissionEveryDenied(code: Int) {
    }
}
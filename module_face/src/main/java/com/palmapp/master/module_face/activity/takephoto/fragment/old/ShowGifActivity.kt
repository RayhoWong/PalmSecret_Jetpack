package com.palmapp.master.module_face.activity.takephoto.fragment.old

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.FileProvider
import com.cs.bd.utils.ToastUtils
import com.palmapp.master.baselib.BaseActivity
import com.palmapp.master.baselib.GoCommonEnv
import com.palmapp.master.baselib.statistics.BaseSeq103OperationStatistic
import com.palmapp.master.baselib.utils.FileUtils
import com.palmapp.master.baselib.utils.GlideUtil
import com.palmapp.master.baselib.utils.PermissionUtil
import com.palmapp.master.module_face.R
import com.yanzhenjie.permission.AndPermission
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.face_activity_show_gif.*
import java.io.File

class ShowGifActivity : BaseActivity() {

    private var gif_path = GoCommonEnv.getApplication().cacheDir.path.plus(File.separator)
        .plus("face_gif.gif")

    private lateinit var gif: File

    private var isDownload = false
    private var isRequestPermission = false
    private var from = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.face_activity_show_gif)
        from = intent.getIntExtra("from", 0)
        if (from == 0) {
            gif = File(gif_path)
        } else {
            gif_path = GoCommonEnv.getApplication().cacheDir.path.plus(File.separator).plus("animal.gif")
            gif = File(gif_path)
        }
        if (from == 0) {
            BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(BaseSeq103OperationStatistic.face_scan_pro, "", "5", "")
        } else {

        }
        findViewById<ImageView>(R.id.iv_titlebar_back).setOnClickListener {
            finish()
        }
        findViewById<TextView>(R.id.tv_titlebar_title).text = if (from == 0) getString(R.string.face_daily_title) else getString(R.string.home_animal_title)
        findViewById<ImageView>(R.id.iv_titlebar_icon).setImageResource(R.mipmap.old_result_ic_gif)
        initView()
    }


    override fun onRestart() {
        super.onRestart()
        if (isRequestPermission && !isDownload)
            checkPermission()
    }

    private fun initView() {
        showGif()

        iv_share.setOnClickListener {
            share()
        }
        iv_download.setOnClickListener {
            checkPermission()
        }
    }


    private fun showGif() {
        GlideUtil.loadGif(this@ShowGifActivity, gif_path, iv_gif)
    }


    private fun share() {
        if (gif.length() > 0) {
            var uri = FileProvider.getUriForFile(GoCommonEnv.getApplication(), "${GoCommonEnv.applicationId}.fileprovider", gif)
            var imageIntent = Intent(Intent.ACTION_SEND)
            imageIntent.type = "image/gif"
            imageIntent.putExtra(Intent.EXTRA_STREAM, uri)
            startActivity(Intent.createChooser(imageIntent, "Share"))
        }
    }


    private fun checkPermission() {
        AndPermission.with(this)
            .runtime()
            .permission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE)
            .onGranted {
                //已获取权限
                saveImage()
                isRequestPermission = true
            }
            .onDenied { data ->
                isRequestPermission = true
                //判断用户是不是选中不再显示权限弹窗了，若不再显示的话提醒进入权限设置页
                if (AndPermission.hasAlwaysDeniedPermission(this, data)) {
                    //提醒打开权限设置页
                    PermissionUtil.GoToSetting(this)
                } else {
                    ToastUtils.makeEventToast(this, "Permisssion Denied!", false)
                }
            }.start()
    }


    @SuppressLint("CheckResult")
    private fun saveImage() {
        if (isDownload) {
            ToastUtils.makeEventToast(this, "the gif was already saved", false)
            return
        }
        if (from == 0){
            BaseSeq103OperationStatistic
                .uploadOperationStatisticDataFor103(BaseSeq103OperationStatistic.download_func_a000, "", "7", "")
        }else{
            BaseSeq103OperationStatistic
                .uploadOperationStatisticDataFor103(BaseSeq103OperationStatistic.download_func_a000, "", "8", "")
        }
        Observable
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
            .compose(bindToLifecycle())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(Consumer<File> { destFile ->
                if (destFile.length() > 0) {
                    ToastUtils.makeEventToast(this, getString(R.string.download_successful), false)
                    // 最后通知图库更新
                    sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                        Uri.fromFile(File(destFile.path))))
                    isDownload = true
                } else {
                    ToastUtils.makeEventToast(this, getString(R.string.download_failed), false)
                }
            })
    }

}

package com.palmapp.master.module_psy.heartrate

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Camera
import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.cs.bd.utils.ToastUtils
import com.palmapp.master.baselib.BaseActivity
import com.palmapp.master.baselib.GoCommonEnv
import com.palmapp.master.baselib.constants.AppConstants
import com.palmapp.master.baselib.constants.RouterConstants
import com.palmapp.master.baselib.statistics.BaseSeq101OperationStatistic
import com.palmapp.master.baselib.statistics.BaseSeq103OperationStatistic
import com.palmapp.master.baselib.utils.LogUtil
import com.palmapp.master.baselib.utils.PermissionUtil
import com.palmapp.master.baselib.utils.TimeUtils
import com.palmapp.master.baselib.view.CameraPermissionGuideDialog
import com.palmapp.master.baselib.view.CommonDialog
import com.palmapp.master.module_psy.R
import kotlinx.android.synthetic.main.psy_activity_heart_rate_detect.*
import org.greenrobot.eventbus.EventBus
import java.io.IOException
import java.lang.Exception
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.collections.ArrayList
import kotlin.math.log

@Route(path = RouterConstants.ACTIVITY_PSY_HEARTRATE_DETECT)
class HeartRateDetectActivity : BaseActivity() {
    @JvmField
    @Autowired(name = "newPlan")
    var newPlan = ""
    private val REQUEST_CODE_CAMERA = 0
    private val TAG = "HeartRateDetectActivity"

    //多线程并发下 只有符合条件的线程才能执行业务逻辑
    private val processing = AtomicBoolean(false)
    //预览设置信息
    private var previewHolder: SurfaceHolder? = null
    //Android手机相机句柄
    private var parameters: Camera.Parameters? = null
    private var camera: Camera? = null
    //private static View image = null;
    //	private static WakeLock wakeLock = null;
    private var averageIndex = 0
    private val averageArraySize = 4
    private val averageArray = IntArray(averageArraySize)

//    private var detectStartTime: Long = 0
//    private var detectEndTime: Long = 0

    private var noBeatsStartTime: Long = 0
    private var noBeatsEndTime: Long = 0

    //属性动画 中间的心跳
    private var heartbeatAnim_2: AnimatorSet? = null
    //是否开启过动画
    private var isAnimStarted = false

    private val heartRateDatas = ArrayList<Int>()

    private var beatsAvg = 0 //心率


    /**
     * 类型枚举
     */
    enum class TYPE {
        GREEN, RED
    }

    //设置默认类型
    private var currentType = TYPE.GREEN

    //获取当前类型
    fun getCurrent(): TYPE? {
        return currentType
    }

    //心跳下标值
    private var beatsIndex = 0
    //心跳数组的大小
    private val beatsArraySize = 3
    //心跳数组
    private val beatsArray = IntArray(beatsArraySize)
    //心跳脉冲
    private var beats = 0.0
    //每一帧的开始时间
    private var startTime: Long = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        ARouter.getInstance().inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.psy_activity_heart_rate_detect)

        initView()
    }


    @SuppressLint("InvalidWakeLockTag")
    private fun initView() {
        BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(BaseSeq103OperationStatistic.heart_page_f000, "", "2", "")

        val iv_back = heartrate_title_bar.findViewById<ImageView>(R.id.iv_titlebar_back)

        iv_back.setOnClickListener {
            finish()
        }
        iv_back.visibility = if (newPlan == "1") View.GONE else View.VISIBLE
        heartrate_title_bar.findViewById<TextView>(R.id.tv_titlebar_title).text = "Heart Rate"

        permissionCamera()
    }

    override fun onBackPressed() {
        if (newPlan == "1") {
            return
        }
        super.onBackPressed()

    }

    override fun onPause() {
        super.onPause()
        stopCamera()
    }

    override fun onRestart() {
        super.onRestart()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            showCamera()
        } else {
            Toast.makeText(this, R.string.camera_permission_not_granted, Toast.LENGTH_SHORT)
                .show()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        stopCamera()
        if (progressbar_heartrate.valueAnimator != null) {
            progressbar_heartrate.valueAnimator.removeAllListeners()
        }
        progressbar_heartrate.stopAnim()
    }


    private fun startHeartbeatAnim() {
        //心率进度条动画
        progressbar_heartrate.setProgress(100f)
        progressbar_heartrate.valueAnimator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                stopAnim()
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
            }

        })
        iv_beats.visibility = View.VISIBLE
        var scaleX = ObjectAnimator.ofFloat(iv_heart, "scaleX", 1f, 0.85f, 1f)
        var scaleY = ObjectAnimator.ofFloat(iv_heart, "scaleY", 1f, 0.85f, 1f)
        scaleX.repeatCount = ValueAnimator.INFINITE
        scaleY.repeatCount = ValueAnimator.INFINITE
        heartbeatAnim_2 = AnimatorSet()
        heartbeatAnim_2?.let {
            it.duration = 1000
            it.play(scaleX).with(scaleY)
            it.start()
        }

    }


    private fun closeHeartbeatAnim() {
        progressbar_heartrate.stopAnim()

        heartbeatAnim_2?.let {
            it.cancel()
        }
    }


    //相机权限处理
    private fun permissionCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            showCamera()
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                showPermissionTip(Manifest.permission.CAMERA)
            } else {
                BaseSeq101OperationStatistic.uploadOperationStatisticData(
                    BaseSeq101OperationStatistic.camera_f000
                )
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.CAMERA),
                    REQUEST_CODE_CAMERA
                )
            }
        }
    }


    @SuppressLint("InvalidWakeLockTag")
    private fun showCamera() {
        try {
//            camera?.release()
            camera = Camera.open()
        } catch (e: Exception) {
            ToastUtils.makeEventToast(this, getString(R.string.camera_error), true)
            return
        }
        camera?.apply {
            setDisplayOrientation(90)
        }
        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
            Toast.makeText(this, "No flash lights on your device!", Toast.LENGTH_LONG).show()
            return
        }

        if (parameters == null) {
            parameters = camera?.parameters
            val modes = parameters?.supportedFlashModes
            if (modes == null) {
                ToastUtils.makeEventToast(this, getString(R.string.camera_error), true)
                return
            } else {
                for (mode in modes) {
                    if (mode == Camera.Parameters.FLASH_MODE_TORCH) {
                        parameters?.flashMode = Camera.Parameters.FLASH_MODE_TORCH //打开闪光灯

                        previewHolder = heartrate_preview.holder
                        previewHolder?.apply {
                            addCallback(surfaceCallback)
                            setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
                        }
                        parameters?.let {
                            camera?.apply {
                                try {
                                    setPreviewDisplay(previewHolder)
                                    setPreviewCallback(previewCallback)
                                    parameters = it
                                    startPreview()
                                } catch (e: IOException) {
                                    e.printStackTrace()
                                    LogUtil.e("Camera Error", e.toString())
                                    ToastUtils.makeEventToast(
                                        this@HeartRateDetectActivity,
                                        getString(R.string.camera_error),
                                        true
                                    )
                                    return
                                }
                            }
                        }
                        startTime = System.currentTimeMillis()
                        noBeatsStartTime = System.currentTimeMillis()
                        break
                    }
                }
            }
//            parameters?.flashMode = Camera.Parameters.FLASH_MODE_TORCH //打开闪光灯
        }

//        parameters?.let {
//            camera?.apply {
//                try {
//                    setPreviewDisplay(previewHolder)
//                    setPreviewCallback(previewCallback)
//                    parameters = it
//                    startPreview()
//                } catch (e: IOException) {
//                    e.printStackTrace()
//                    LogUtil.e("Camera Error",e.toString())
//                }
//            }
//        }

    }


    private fun stopCamera() {
        camera?.apply {
            try {
                setPreviewDisplay(null)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            setPreviewCallback(null)
            stopPreview()
            release()
        }
        camera = null
    }


    private fun detectTimeOver() {
        if (heartRateDatas.size == 0) {
            heartRateDatas.add(0)
        }
        EventBus.getDefault().postSticky(heartRateDatas)
        if (newPlan == "1")
            EventBus.getDefault().postSticky(newPlan)
        var userInfo = GoCommonEnv.userInfo
        if (userInfo == null) {
//            Toast.makeText(this@HeartRateDetectActivity, "用户信息为空", Toast.LENGTH_LONG).show()
            startActivity(Intent(this, BirthActivity::class.java))
        } else {
            if (userInfo.birthday == 0L) {
                startActivity(Intent(this, BirthActivity::class.java))
            } else {
//                Toast.makeText(this@HeartRateDetectActivity, "已经记录生日信息", Toast.LENGTH_LONG).show()
                startActivity(Intent(this, HeartRateReportActivity::class.java))
            }
        }
        finish()
    }


    private fun stopAnim() {
        closeHeartbeatAnim()
        stopCamera()
        detectTimeOver()
    }


    private val previewCallback = object : Camera.PreviewCallback {
        override fun onPreviewFrame(data: ByteArray, camera: Camera) {
            if (data == null) {
                throw NullPointerException()
            }
            var size: Camera.Size = camera.parameters.previewSize ?: throw NullPointerException()
            if (!processing.compareAndSet(false, true)) {
                return
            }

            noBeatsEndTime = System.currentTimeMillis()
            LogUtil.d("test", "noBeatsEndTime:$noBeatsEndTime noBeatsStartTime:$noBeatsStartTime beatsAvg:$beatsAvg")
            if (noBeatsEndTime - noBeatsStartTime >= 10 * 1000) {
                LogUtil.d("test", "Completed")
                noBeatsStartTime = Long.MAX_VALUE
                cv_camera.visibility = View.INVISIBLE
                rl_bpm.visibility = View.VISIBLE
                startHeartbeatAnim()
                isAnimStarted = true
            }

//            detectEndTime = System.currentTimeMillis()
            //检测时间超过15s 停止检测
            if (heartbeatAnim_2?.isRunning == false) {
                return
            }

            val width = size.width
            val height = size.height
            //图像处理 得到红色像素的平均数
            val imgAvg = ImageProcessing.decodeYUV420SPtoRedAvg(data.clone(), height, width)
            if (imgAvg == 0 || imgAvg == 255) {
                //红色像素平均值为0 代表镜头没有检测到手指(人体器官)
//                Toast.makeText(this@HeartRateDetectActivity, "请用您的指尖盖住摄像头镜头！", Toast.LENGTH_SHORT).show()
                //重新检测
                processing.set(false)
                return
            }

            //计算平均值
            var averageArrayAvg = 0
            var averageArrayCnt = 0
            for (i in averageArray.indices) {
                if (averageArray[i] > 0) {
                    averageArrayAvg += averageArray[i]
                    averageArrayCnt++
                }
            }
            //计算符合条件的红色素平均数的平均值
            val rollingAverage = if (averageArrayCnt > 0) averageArrayAvg / averageArrayCnt else 0
            var newType = currentType
            if (imgAvg < rollingAverage) {
                newType = TYPE.RED
                if (newType != currentType) {
                    //跳动次数
                    beats++
                }
            } else if (imgAvg > rollingAverage) {
                newType = TYPE.GREEN
            }

            if (averageIndex == averageArraySize) {
                averageIndex = 0
            }
            averageArray[averageIndex] = imgAvg
            averageIndex++

            if (newType != currentType) {
                currentType = newType
            }

            //获取系统结束时间（ms）
            val endTime = System.currentTimeMillis()
            //每一帧心率计算的间隔时间
            val totalTimeInSecs: Double = (endTime - startTime) / 1000.0
            if (totalTimeInSecs >= 2) {
                //这段时间每秒跳动的次数
                val bps: Double = beats / totalTimeInSecs
                //心率值
                val dpm = (bps * 60.0).toInt()
                if (dpm < 30 || dpm > 180 || imgAvg < 200) { //不符合心率的正常范围  重新测量
                    startTime = System.currentTimeMillis()
                    //beats心跳总数
                    beats = 0.0
                    //重新检测
                    processing.set(false)
                    return
                }
                if (beatsIndex == beatsArraySize) {
                    beatsIndex = 0
                }
                //该一帧的心率
                beatsArray[beatsIndex] = dpm
                beatsIndex++
                var beatsArrayAvg = 0
                var beatsArrayCnt = 0
                for (i in beatsArray.indices) {
                    if (beatsArray[i] > 0) {
                        beatsArrayAvg += beatsArray[i]
                        beatsArrayCnt++
                    }
                }
                //计算平均值
                beatsAvg = beatsArrayAvg / beatsArrayCnt

                if (beatsAvg > 0 && !isAnimStarted) {
                    noBeatsStartTime = Long.MAX_VALUE
                    startHeartbeatAnim()
                    if (cv_camera.visibility == View.VISIBLE) {
                        cv_camera.visibility = View.INVISIBLE
                    }
                    if (rl_bpm.visibility == View.INVISIBLE) {
                        rl_bpm.visibility = View.VISIBLE
                    }
                    //第一次检测到心率
                    isAnimStarted = true
                }
                heartRateDatas.add(beatsAvg)
                tv_heartrate.text = (beatsAvg.toString())
                //心率计算成功后 重置下一帧的测量时间
                startTime = System.currentTimeMillis()
                beats = 0.0
            }
            processing.set(false)
        }
    }

    /**
     * 预览回调接口
     */
    private val surfaceCallback: SurfaceHolder.Callback = object : SurfaceHolder.Callback {
        //创建时调用
        @SuppressLint("LongLogTag")
        override fun surfaceCreated(holder: SurfaceHolder) {
            try {
                camera?.let {
                    it.setPreviewCallback(previewCallback)
                    it.setPreviewDisplay(previewHolder)
                }

            } catch (t: Throwable) {
                Log.e(TAG, "Exception in setPreviewDisplay()", t)
            }
        }

        //当预览改变的时候回调此方法
        override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            try {
                val parameters: Camera.Parameters? = camera?.parameters
                var size: Camera.Size? = null
                parameters?.let {
                    //                it.flashMode = Camera.Parameters.FLASH_MODE_TORCH
                    size = getSmallestPreviewSize(width, height, it)
                }
                size?.let {
                    camera?.stopPreview()
                    parameters?.setPreviewSize(it.width, it.height)
                }
                camera?.apply {
                    setParameters(parameters)
                    startPreview()
                    setPreviewCallback(previewCallback)
                    setPreviewDisplay(previewHolder)
                }
            } catch (t: Throwable) {
                Log.e(TAG, "Exception in setPreviewDisplay()", t)
            }

//            parameters?.let {
//                val size = getSmallestPreviewSize(width, height, it)
//                if (size != null) {
//                    camera?.stopPreview()
//                    it.setPreviewSize(size.width, size.height)
//                }
//                camera?.parameters = it
//                camera?.startPreview()
//            }
        }

        //销毁的时候调用
        override fun surfaceDestroyed(holder: SurfaceHolder) {}
    }


    /**
     * 获取相机最小的预览尺寸
     */
    private fun getSmallestPreviewSize(width: Int, height: Int, parameters: Camera.Parameters): Camera.Size? {
        var result: Camera.Size? = null
        for (size in parameters.supportedPreviewSizes) {
            if (size.width <= width && size.height <= height) {
                if (result == null) {
                    result = size
                } else {
                    val resultArea = result.width * result.height
                    val newArea = size.width * size.height
                    if (newArea < resultArea) {
                        result = size
                    }
                }
            }
        }
        return result
    }


    private fun showPermissionTip(permission: String) {
        when (permission) {
            Manifest.permission.CAMERA -> {
                Toast.makeText(this, R.string.camera_permission_not_granted, Toast.LENGTH_SHORT)
                    .show()
                val dialog = CameraPermissionGuideDialog(this)
                dialog.setOnClickBottomListener(object : CommonDialog.OnClickBottomListener {

                    override fun onPositiveClick() {
                        PermissionUtil.GoToSetting(this@HeartRateDetectActivity)
                    }

                    override fun onNegativeClick() {
                        dialog.dismiss()
                        finish()
                    }

                })
                dialog.show()
            }
        }
    }


    //权限回调结果处理
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_CODE_CAMERA -> {//拍照
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    BaseSeq101OperationStatistic.uploadOperationStatisticData(
                        BaseSeq101OperationStatistic.camera_a000,
                        "",
                        "1"
                    )
                    showCamera()
                } else {
                    BaseSeq101OperationStatistic.uploadOperationStatisticData(
                        BaseSeq101OperationStatistic.camera_a000,
                        "",
                        "2"
                    )
                    Toast.makeText(this, R.string.camera_permission_not_granted, Toast.LENGTH_SHORT)
                        .show()
                    finish()
                }
            }

        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppConstants.PAY_REQUEST_CODE && resultCode == AppConstants.PAY_RESULT_CODE) {
            finish()
        }
    }


}

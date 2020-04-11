package com.palmapp.master.module_face.activity.takephoto

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.cs.bd.commerce.util.LogUtils
import com.google.android.cameraview.CameraView
import com.palmapp.master.baselib.BaseMVPActivity
import com.palmapp.master.baselib.constants.PreConstants
import com.palmapp.master.baselib.constants.RouterConstants
import com.palmapp.master.baselib.manager.GoPrefManager
import com.palmapp.master.baselib.manager.RouterServiceManager.goHomeAndClearTop
import com.palmapp.master.baselib.manager.ThreadExecutorProxy
import com.palmapp.master.baselib.manager.config.ConfigManager
import com.palmapp.master.baselib.manager.config.StarGuideConfig
import com.palmapp.master.baselib.proxy.BillingServiceProxy
import com.palmapp.master.baselib.statistics.BaseSeq103OperationStatistic
import com.palmapp.master.baselib.utils.PermissionUtil
import com.palmapp.master.baselib.utils.TimeUtils
import com.palmapp.master.baselib.view.CameraPermissionGuideDialog
import com.palmapp.master.baselib.view.CommonDialog
import com.palmapp.master.baselib.view.LikeDialog
import com.palmapp.master.baselib.view.StoragePermissionGuideDialog
import com.palmapp.master.module_ad.manager.ExitAdManager
import com.palmapp.master.module_face.R
import com.palmapp.master.module_face.activity.takephoto.fragment.DataObject
import com.palmapp.master.module_face.activity.takephoto.fragment.old.OldFragment
import com.palmapp.master.module_face.activity.takephoto.fragment.takephotoed.TakephotoedFragment
import com.palmapp.master.module_imageloader.ImageLoader
import com.palmapp.master.module_imageloader.glide.GlideImageLoaderStrategy
import kotlinx.android.synthetic.main.face_activity_takephoto.*


/**
 * Created by zhangliang on 15-8-19.
 * 拍照页面
 */
@Route(path = RouterConstants.ACTVITIY_TakephotoActivity)
class TakephotoActivity : BaseMVPActivity<TakephotoView, TakephotoPresenter>(), TakephotoView,
    View.OnClickListener,
    DataObject.FragmentInteractionListener {
    private var isVip: Boolean = BillingServiceProxy.isVip()
    private val FRAGMENT_Takephotoed = "fragment_TakephotoedFragment"
    private val FRAGMENT_Old = "fragment_oldFragment"
    private val TAG = "TakephotoActivity"
    //    private val FRAGMENT_DIALOG = "dialog"
    private val PICTURE_REQUEST_CODE = 0x01

    @JvmField
    @Autowired(name = "newPlan")
    var newPlan = ""
    @JvmField
    @Autowired(name = "finish")
    var finish = ""
    private var isUnlock = false

    override var isStroagePermissionGranted = false
    override var isCameraPermissionGranted = false


    private val mCallback = object : CameraView.Callback() {

        override fun onCameraOpened(cameraView: CameraView) {
            Log.d(TAG, "onCameraOpened")
        }

        override fun onCameraClosed(cameraView: CameraView) {
            Log.d(TAG, "onCameraClosed")
        }

        override fun onPictureTaken(cameraView: CameraView, data: ByteArray) {
            Log.d(TAG, "onPictureTaken " + data.size)
            mPresenter?.scaleBitmp(getContext(), cameraView, data)
            face_picture?.isClickable = true
        }
    }

    override fun createPresenter(): TakephotoPresenter {
        return TakephotoPresenter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        ARouter.getInstance().inject(this)
        BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
            BaseSeq103OperationStatistic.face_scan_pro,
            "",
            "1", ""
        )
        super.onCreate(savedInstanceState)
        ImageLoader.init(GlideImageLoaderStrategy())
        setContentView(R.layout.face_activity_takephoto)
        face_camera?.addCallback(mCallback)
        face_picture?.setOnClickListener(this)
        face_picture_choose?.setOnClickListener(this)
        iv_takephoto_back?.setOnClickListener(this)
        ib_change_camera.setOnClickListener(this)
        iv_takephoto_back?.visibility = if (newPlan == "1") View.GONE else View.VISIBLE
        mPresenter?.checkPermission(this)
        mPresenter?.permissionCamera(this)

        if (finish != "1")
            return
        val fragment = OldFragment.newInstance("", "", newPlan)
        fragment.bitmap50 = BitmapFactory.decodeFile(fragment.bitmap50_path)
        fragment.bitmap70 = BitmapFactory.decodeFile(fragment.bitmap70_path)
        fragment.bitmap90 = BitmapFactory.decodeFile(fragment.bitmap90_path)
        fragment.originBitmap = BitmapFactory.decodeFile(fragment.orignBitmap_path)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment, fragment, FRAGMENT_Old)
            .commitAllowingStateLoss()
        stopCamera()
    }

    override fun onResume() {
        super.onResume()
        face_camera?.isClickable = true
    }


    override fun onRestart() {
        super.onRestart()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && !isCameraPermissionGranted) {
            isCameraPermissionGranted = true
            startCamera()
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && !isStroagePermissionGranted) {
            isStroagePermissionGranted = true
            startPicture()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                PICTURE_REQUEST_CODE -> mPresenter?.pictureResult(data, this)
            }
        }
    }

    private var lastClickTime = 0L
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.face_picture -> {
                val now = System.currentTimeMillis()
                if (now - lastClickTime > 2000) {
                    lastClickTime = now
                    face_camera?.let {
                        if (!face_camera.isCameraOpened) {
                            face_camera.start()
                        }
                        try {
                            face_picture?.isClickable = false
                            face_camera.takePicture()
                        } catch (e: Exception) {
                            face_picture?.isClickable = true
                        }
                    }
                }
            }
            R.id.face_picture_choose -> {
                mPresenter?.permissionMedia(this)
            }
            R.id.iv_takephoto_back -> {
                finish()
            }
            R.id.ib_change_camera -> {
                face_camera.facing = if (face_camera.facing == CameraView.FACING_FRONT) CameraView.FACING_BACK else CameraView.FACING_FRONT
            }
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        mPresenter?.permissionsResult(requestCode, permissions, grantResults)
    }


    override fun showFragmentDialog(permission: String) {
        when (permission) {
            Manifest.permission.CAMERA -> {
                Toast.makeText(this, R.string.camera_permission_not_granted, Toast.LENGTH_SHORT)
                    .show()
                val dialog = CameraPermissionGuideDialog(this)
                dialog.setOnClickBottomListener(object : CommonDialog.OnClickBottomListener {

                    override fun onPositiveClick() {
                        PermissionUtil.GoToSetting(this@TakephotoActivity)
                    }

                    override fun onNegativeClick() {
                        finish()
                    }

                })
                dialog.show()
            }
            Manifest.permission.WRITE_EXTERNAL_STORAGE -> {
                Toast.makeText(this, R.string.storage_permission_not_granted, Toast.LENGTH_SHORT)
                    .show()
                val dialog = StoragePermissionGuideDialog(this)
                dialog.setOnClickBottomListener(object : CommonDialog.OnClickBottomListener {

                    override fun onPositiveClick() {
                        PermissionUtil.GoToSetting(this@TakephotoActivity)
                    }

                    override fun onNegativeClick() {
                        finish()
                    }

                })
                dialog.show()
            }
        }
    }

    override fun onBackPressed() {
        if (newPlan == "1") {
            return
        }
        val fragment = supportFragmentManager.findFragmentByTag(FRAGMENT_Takephotoed)
        if (fragment != null && fragment.isResumed) {
            supportFragmentManager.beginTransaction().remove(fragment).commitAllowingStateLoss()
            startCamera()
        } else {
            if (isVip) {//订阅
                showScoreGuide()
            } else {
                ExitAdManager.showAd()
                super.onBackPressed()
            }
        }
    }


    override fun onFragmentInteraction(uri: Uri, datas: DataObject?) {
        when (uri?.path) {
            "/goto.TakephotoActivity" -> {
                val fragment = supportFragmentManager.findFragmentByTag(FRAGMENT_Takephotoed)
                fragment?.let {
                    supportFragmentManager.beginTransaction().remove(fragment)
                        .commitAllowingStateLoss()
                }
//                supportFragmentManager.popBackStack("TakephotoedFragment", 0)
                startCamera()
            }
            "/goto.OldFragment" -> {
                datas?.let {
                    val fragment = OldFragment.newInstance("", "", newPlan)
                    it.bitmaps?.let { images ->
                        fragment.bitmap50 = images.elementAt(0)
                        fragment.bitmap70 = images.elementAt(1)
                        fragment.bitmap90 = images.elementAt(2)
                    }
                    it.bitmap?.let { origin ->
                        fragment.originBitmap = origin
                    }
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment, fragment, FRAGMENT_Old)
                        .commitAllowingStateLoss()
                }
                stopCamera()
            }

            "/goto.Finish" -> {
                if (isVip) {
                    showScoreGuide()
                } else {
                    ExitAdManager.showAd()
                    super.onBackPressed()
                }
            }

            "/goto.SkipVip" -> {
                isVip = true
            }

            "/goto.UnlockVip" -> {
                isUnlock = true
            }
        }
    }


    override fun startCamera() {
        try {
            face_camera?.let {
                if (face_camera.isCameraOpened) {
                    face_camera?.stop()
                }
            }
            face_camera?.start()
        } catch (ex: Exception) {

        }
    }

    override fun stopCamera() {
        try {
            face_camera?.let {
                if (face_camera.isCameraOpened) {
                    face_camera.stop()
                }
            }
        } catch (ex: Exception) {

        }

    }

    override fun startTakephotoedFragment() {
        mPresenter?.let {
            it.fileUri?.let { it2 ->
                val fragment = TakephotoedFragment.newInstance(it2, "")
                supportFragmentManager.beginTransaction()
                    .add(R.id.fragment, fragment, FRAGMENT_Takephotoed)
                    .commitAllowingStateLoss()
            }

        }
    }

    override fun startPicture() {
        BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
            BaseSeq103OperationStatistic.face_scan_pro,
            "",
            "2", ""
        )
        val intentToPickPic = Intent(Intent.ACTION_PICK, null)
        intentToPickPic.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        startActivityForResult(intentToPickPic, PICTURE_REQUEST_CODE)
    }

    override fun permissionDenide() {
        finish()
    }


    private fun showAdWithoutGuide() {
        if (isUnlock) {
            //观看视频解锁 弹广告
            ExitAdManager.showAd()
        }
    }

    //好评引导
    private fun showScoreGuide() {
        var isOpen = ConfigManager.getConfig(StarGuideConfig::class.java)?.isOpenGuide
        var isResultOpen = ConfigManager.getConfig(StarGuideConfig::class.java)?.isResultGuide
        isOpen?.let {
            if (!it) { //好评总开关是关闭的
                showAdWithoutGuide()
                finish()
                return
            }
        }
        isResultOpen?.let {
            if (!it) { //好评结果页开关是关闭的
                showAdWithoutGuide()
                finish()
                return
            }
        }
        //好评引导出现的次数
        var guideShowedCount =
            GoPrefManager.getDefault().getInt(PreConstants.ScoreGuide.KEY_GUIDE_TOTAL_COUNT, 0)
        if (guideShowedCount == 3) {
            showAdWithoutGuide()
            finish()
            return
        }

        var isFirstShowed =
            GoPrefManager.getDefault().getBoolean(PreConstants.ScoreGuide.KEY_IS_FIRST_SHOW, false)
        if (!isFirstShowed) {
            ThreadExecutorProxy.runOnMainThread(Runnable {
                BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
                    BaseSeq103OperationStatistic.rating_f000,
                    "",
                    "1",
                    ""
                )
                var dialog = LikeDialog(this, "old")
                dialog.show()
                GoPrefManager.getDefault().putString(
                    PreConstants.ScoreGuide.KEY_CACHE_NOW_DATE,
                    TimeUtils.getStringDate()
                ).commit()

                GoPrefManager.getDefault().putBoolean(
                    PreConstants.ScoreGuide.KEY_IS_FIRST_SHOW,
                    true
                ).commit()

                guideShowedCount++
                GoPrefManager.getDefault().putInt(
                    PreConstants.ScoreGuide.KEY_GUIDE_TOTAL_COUNT,
                    guideShowedCount
                ).commit()
            })
            return
        }

        var isScored =
            GoPrefManager.getDefault().getBoolean(PreConstants.ScoreGuide.KEY_IS_SCORED, false)
        var cacheDate =
            GoPrefManager.getDefault().getString(PreConstants.ScoreGuide.KEY_CACHE_NOW_DATE, "")
        try {
            if (!cacheDate.isNullOrEmpty()) {
                var nowDate = TimeUtils.getStringDate()
                var mins = TimeUtils.getDistanceMin(nowDate, cacheDate)
                if (!isScored) {  //没有评论过
                    if (mins > 48 * 60) { //超过48h未出现过
                        ThreadExecutorProxy.runOnMainThread(Runnable {
                            BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
                                BaseSeq103OperationStatistic.rating_f000,
                                "",
                                "1",
                                ""
                            )
                            var dialog = LikeDialog(this, "old")
                            dialog.show()
                            GoPrefManager.getDefault().putString(
                                PreConstants.ScoreGuide.KEY_CACHE_NOW_DATE,
                                TimeUtils.getStringDate()
                            ).commit()

                            guideShowedCount++
                            GoPrefManager.getDefault().putInt(
                                PreConstants.ScoreGuide.KEY_GUIDE_TOTAL_COUNT,
                                guideShowedCount
                            ).commit()
                        })

                    } else {
                        showAdWithoutGuide()
                        finish()
                    }
                } else {
                    showAdWithoutGuide()
                    finish()
                }
            }
        } catch (e: Exception) {
            LogUtils.e(e.printStackTrace().toString())
        }
    }

    override fun finish() {
        if (supportFragmentManager.findFragmentByTag(FRAGMENT_Old) != null) {
            goHomeAndClearTop() {
                super.finish()
            }
        } else {
            super.finish()
        }
    }

}

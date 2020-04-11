package com.palmapp.master.module_transform.activity

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.google.android.cameraview.CameraView
import com.palmapp.master.baselib.BaseActivity
import com.palmapp.master.baselib.constants.AppConstants
import com.palmapp.master.baselib.constants.RouterConstants
import com.palmapp.master.baselib.statistics.BaseSeq101OperationStatistic
import com.palmapp.master.baselib.utils.PermissionUtil
import com.palmapp.master.baselib.view.CameraPermissionGuideDialog
import com.palmapp.master.baselib.view.CommonDialog
import com.palmapp.master.baselib.view.StoragePermissionGuideDialog
import com.palmapp.master.module_transform.R
import com.palmapp.master.module_transform.fragment.ConfirmFragment
import com.palmapp.master.module_transform.util.PhotoUtil
import kotlinx.android.synthetic.main.transform_activity_transform.*
import org.greenrobot.eventbus.EventBus

/**
 * 变性拍照页
 */
@Route(path = RouterConstants.ACTIVITY_TRANSFORM_TAKE_PHOTO)
class TakePhotoActivity : BaseActivity(), View.OnClickListener {

    private val REQUEST_CODE_CAMERA = 0
    private val REQUEST_CODE_ALBUM = 1
    private val FRAGMENT_TAG_CONFIRM = "FRAGMENT_TAG_CONFIRM"
    private val TAG = "TakePhotoActivity"

    private var mPhoto: String? = ""
    private var mResult: String? = ""

    private var isStroagePermissionGranted = false
    private var isCameraPermissionGranted = false

    private val mCallback = object : CameraView.Callback() {

        override fun onCameraOpened(cameraView: CameraView) {
            Log.d(TAG, "onCameraOpened")
        }

        override fun onCameraClosed(cameraView: CameraView) {
            Log.d(TAG, "onCameraClosed")
        }

        override fun onPictureTaken(cameraView: CameraView, data: ByteArray) {
            Log.d(TAG, "onPictureTaken " + data.size)
            PhotoUtil.scaleBitmap(this@TakePhotoActivity, cameraView, data)
            ib_take_photo.isClickable = true
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.transform_activity_transform)
        BaseSeq101OperationStatistic.uploadOperationStatisticData(
            BaseSeq101OperationStatistic.gender_page_f000,
            "",
            "1"
        )

        transform_camera.addCallback(mCallback)
        ib_take_photo.setOnClickListener(this)
        ib_album_choose.setOnClickListener(this)
        iv_takephoto_back.setOnClickListener(this)
        checkPermission()
        permissionCamera()
    }


    private fun checkPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            isCameraPermissionGranted = true
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            isStroagePermissionGranted = true
        }
    }


    private var lastTimeClick = 0L
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ib_take_photo -> {
                var now = System.currentTimeMillis()
                if (now - lastTimeClick > 2000) {
                    lastTimeClick = now
                    transform_camera?.let {
                        if (!transform_camera.isCameraOpened) {
                            transform_camera.start()
                        }
                        try {
                            ib_take_photo.isClickable = false
                            transform_camera.takePicture()
                        } catch (e: Exception) {
                            ib_take_photo.isClickable = true
                        }
                    }
                }
            }
            R.id.ib_album_choose -> {
                permissionAlbum()
            }
            R.id.iv_takephoto_back -> {
                finish()
            }
        }
    }


    //开启相机
    fun startCamera() {
        try {
            transform_camera?.let {
                if (transform_camera.isCameraOpened) {
                    transform_camera?.stop()
                }
            }
            transform_camera?.start()
        } catch (ex: Exception) {
            Log.e(TAG, ex.toString())
        }
    }

    //关闭相机
    fun stopCamera() {
        try {
            transform_camera?.let {
                if (transform_camera.isCameraOpened) {
                    transform_camera.stop()
                }
            }
        } catch (ex: Exception) {
            Log.e(TAG, ex.toString())
        }
    }


    //相册选择图片
    private fun choosePhoto() {
//        BaseSeq101OperationStatistic.uploadOperationStatisticData(BaseSeq101OperationStatistic.face_scan_pro, "", "2")
        val intentToPickPic = Intent(Intent.ACTION_PICK, null)
        intentToPickPic.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        startActivityForResult(intentToPickPic, REQUEST_CODE_ALBUM)
    }


    //相机权限处理
    private fun permissionCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
            startCamera()
        }else{
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)){
                showPermissionTip(Manifest.permission.CAMERA)
            }else{
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


    //相册权限处理
    private fun permissionAlbum() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> choosePhoto()

            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) -> showPermissionTip(Manifest.permission.WRITE_EXTERNAL_STORAGE)

            else -> {
                BaseSeq101OperationStatistic.uploadOperationStatisticData(
                    BaseSeq101OperationStatistic.storage_f000
                )
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    REQUEST_CODE_ALBUM
                )
            }
        }
    }


    private fun showPermissionTip(permission: String) {
        when (permission) {
            Manifest.permission.CAMERA -> {
                Toast.makeText(this, R.string.camera_permission_not_granted, Toast.LENGTH_SHORT)
                    .show()
                val dialog = CameraPermissionGuideDialog(this)
                dialog.setOnClickBottomListener(object : CommonDialog.OnClickBottomListener{

                    override fun onPositiveClick() {
                        PermissionUtil.GoToSetting(this@TakePhotoActivity)
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
                        PermissionUtil.GoToSetting(this@TakePhotoActivity)
                    }

                    override fun onNegativeClick() {
                        finish()
                    }

                })
                dialog.show()
            }
        }
    }


    override fun onRestart() {
        super.onRestart()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && !isCameraPermissionGranted) {
            isCameraPermissionGranted = true
            startCamera()
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && !isStroagePermissionGranted) {
            isStroagePermissionGranted = true
            choosePhoto()
        }
    }


    //权限回调结果处理
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE_CAMERA -> {//拍照
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    BaseSeq101OperationStatistic.uploadOperationStatisticData(
                        BaseSeq101OperationStatistic.camera_a000,
                        "",
                        "1"
                    )
                    isCameraPermissionGranted = true
                    startCamera()
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

            REQUEST_CODE_ALBUM -> {//相册
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    BaseSeq101OperationStatistic.uploadOperationStatisticData(
                        BaseSeq101OperationStatistic.storage_a000,
                        "",
                        "1"
                    )
                    isStroagePermissionGranted = true
                    choosePhoto()
                } else {
                    BaseSeq101OperationStatistic.uploadOperationStatisticData(
                        BaseSeq101OperationStatistic.storage_a000,
                        "",
                        "2"
                    )
                    Toast.makeText(this, R.string.storage_permission_not_granted, Toast.LENGTH_SHORT)
                        .show()
                    finish()
                }
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_ALBUM && resultCode == Activity.RESULT_OK) {
            PhotoUtil.getPhotoPath(data, this)

        } else if (requestCode == AppConstants.PAY_REQUEST_CODE && resultCode == AppConstants.PAY_RESULT_CODE) {
            EventBus.getDefault().postSticky(mResult)
            startActivity(
                Intent(
                    this,
                    ResultActivity::class.java
                )
            )
            finish()
        }
    }


    fun startConfirmFragment(photo: String?) {
        mPhoto = photo
        mPhoto?.let {
            val fragment = ConfirmFragment.newInstance(mPhoto!!)
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment, fragment, FRAGMENT_TAG_CONFIRM)
                .commitAllowingStateLoss()
        }
    }


    fun removeConfirmFragment() {
        val fragment = supportFragmentManager.findFragmentByTag(FRAGMENT_TAG_CONFIRM)
        if (fragment != null && fragment.isResumed) {
            supportFragmentManager.beginTransaction().remove(fragment)
                .commitAllowingStateLoss()
            startCamera()
        }
    }

    fun setResult(result: String) {
        mResult = result
    }

    fun toBeVip() {
        ARouter.getInstance().build(RouterConstants.ACTIVITY_PAY)
            .withString("entrance", "10")
            .navigation(this, AppConstants.PAY_REQUEST_CODE)
    }


    override fun onBackPressed() {
        val fragment = supportFragmentManager.findFragmentByTag(FRAGMENT_TAG_CONFIRM)
        if (fragment != null && fragment.isResumed) {
            supportFragmentManager.beginTransaction().remove(fragment).commitAllowingStateLoss()
            startCamera()
        } else {
            super.onBackPressed()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        stopCamera()
    }
}

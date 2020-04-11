package com.palmapp.master.module_face.activity.album

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.google.android.cameraview.CameraView
import com.palmapp.master.baselib.BaseActivity
import com.palmapp.master.baselib.constants.AppConstants
import com.google.android.cameraview.CameraView.FACING_BACK
import com.google.android.cameraview.CameraView.FACING_FRONT
import com.palmapp.master.baselib.constants.PreConstants
import com.palmapp.master.baselib.constants.RouterConstants
import com.palmapp.master.baselib.manager.GoPrefManager
import com.palmapp.master.baselib.statistics.BaseSeq101OperationStatistic
import com.palmapp.master.baselib.statistics.BaseSeq103OperationStatistic
import com.palmapp.master.baselib.utils.PermissionUtil
import com.palmapp.master.baselib.view.CameraPermissionGuideDialog
import com.palmapp.master.baselib.view.CommonDialog
import com.palmapp.master.baselib.view.StoragePermissionGuideDialog
import com.palmapp.master.module_face.R
import kotlinx.android.synthetic.main.face_activity_album_take_photo.*
import org.greenrobot.eventbus.EventBus


@Route(path = RouterConstants.ACTIVITY_ALBUM_TAKE_PHOTO)
class AlbumTakePhotoActivity : BaseActivity(), View.OnClickListener {
    @JvmField
    @Autowired(name = "newPlan")
    var newPlan = ""
    private val REQUEST_CODE_CAMERA = 0
    private val REQUEST_CODE_ALBUM = 1
    private val FRAGMENT_TAG_CONFIRM = "FRAGMENT_TAG__ALBUM_CONFIRM"
    private val TAG = "AlbumTakePhotoActivity"

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
            PhotoUtil.scaleBitmap(this@AlbumTakePhotoActivity, cameraView, data)
            ib_take_photo.isClickable = true
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ARouter.getInstance().inject(this)
        setContentView(R.layout.face_activity_album_take_photo)

        //首次进入
        var isFirstEnter = newPlan == "1"
        if (isFirstEnter) {
            BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(BaseSeq103OperationStatistic.comic_page_f000, "1", "1","")
            GoPrefManager.getDefault().putBoolean(PreConstants.Face.KEY_FACE_IS_FIRST_ENTER_ALBUM_TAKE_PHOTO, false).commit()
        } else {
            BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(BaseSeq103OperationStatistic.comic_page_f000, "2", "1","")
        }

        album_camera.addCallback(mCallback)
        ib_take_photo.setOnClickListener(this)
        ib_album_choose.setOnClickListener(this)
        ib_face_change.setOnClickListener(this)
        iv_takephoto_back.setOnClickListener(this)
        iv_takephoto_back.visibility = if(newPlan == "1") View.GONE else View.VISIBLE
        checkPermission()
        permissionCamera()
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


    private var lastTimeClick = 0L
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ib_take_photo -> {
                var now = System.currentTimeMillis()
                if (now - lastTimeClick > 2000) {
                    lastTimeClick = now
                    album_camera?.let {
                        if (!album_camera.isCameraOpened) {
                            album_camera.start()
                        }
                        try {
                            ib_take_photo.isClickable = false
                            album_camera.takePicture()
                        } catch (e: Exception) {
                            ib_take_photo.isClickable = true
                        }
                    }
                }
            }
            R.id.ib_album_choose -> {
                permissionAlbum()
            }
            R.id.ib_face_change -> {
                album_camera.facing = if (album_camera.facing == FACING_FRONT) FACING_BACK else FACING_FRONT
            }
            R.id.iv_takephoto_back -> {
                finish()
            }
        }
    }


    //开启相机
    fun startCamera() {
        try {
            album_camera?.let {
                if (album_camera.isCameraOpened) {
                    album_camera?.stop()
                }
            }
            album_camera?.start()
        } catch (ex: Exception) {
            Log.e(TAG, ex.toString())
        }
    }

    //关闭相机
    fun stopCamera() {
        try {
            album_camera?.let {
                if (album_camera.isCameraOpened) {
                    album_camera.stop()
                }
            }
        } catch (ex: Exception) {
            Log.e(TAG, ex.toString())
        }
    }


    //相册选择图片
    private fun choosePhoto() {
        BaseSeq101OperationStatistic.uploadOperationStatisticData(BaseSeq101OperationStatistic.face_scan_pro, "", "2")
        val intentToPickPic = Intent(Intent.ACTION_PICK, null)
        intentToPickPic.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        startActivityForResult(intentToPickPic, REQUEST_CODE_ALBUM)
    }


    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            isCameraPermissionGranted = true
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            isStroagePermissionGranted = true
        }
    }


    //相机权限处理
    private fun permissionCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startCamera()
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
                dialog.setOnClickBottomListener(object : CommonDialog.OnClickBottomListener {
                    override fun onPositiveClick() {
                        PermissionUtil.GoToSetting(this@AlbumTakePhotoActivity)
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
                        PermissionUtil.GoToSetting(this@AlbumTakePhotoActivity)
                    }

                    override fun onNegativeClick() {
                        finish()
                    }
                })
                dialog.show()
            }
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
//            startActivity(Intent(this, ResultActivity::class.java))
            finish()
        }
    }
    override fun onBackPressed() {
        if (newPlan == "1") {
            return
        }
        super.onBackPressed()
    }

    fun startConfirmFragment(photo: String?) {
        mPhoto = photo
        mPhoto?.let {
            val fragment = AlbumConfirmFragment.newInstance(mPhoto!!,newPlan)
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


}

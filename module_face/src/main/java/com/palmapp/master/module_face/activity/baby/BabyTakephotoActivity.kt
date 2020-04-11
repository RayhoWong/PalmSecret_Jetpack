package com.palmapp.master.module_face.activity.baby;

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.hardware.Camera
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.google.android.cameraview.AlbumPresenter
import com.google.android.cameraview.CameraPresenter
import com.google.android.cameraview.IPhotoSelectorView
import com.palmapp.master.baselib.BaseMVPActivity
import com.palmapp.master.baselib.BaseMultipleMVPActivity
import com.palmapp.master.baselib.BaseMultiplePresenter
import com.palmapp.master.baselib.constants.AppConstants
import com.palmapp.master.baselib.constants.PreConstants
import com.palmapp.master.baselib.constants.RouterConstants
import com.palmapp.master.baselib.manager.GoPrefManager
import com.palmapp.master.baselib.manager.PERMISSION_CAMERA
import com.palmapp.master.baselib.permission.IPermissionView
import com.palmapp.master.baselib.permission.PermissionPresenter
import com.palmapp.master.baselib.statistics.BaseSeq101OperationStatistic
import com.palmapp.master.baselib.utils.AppUtil
import com.palmapp.master.baselib.utils.FileUtils
import com.palmapp.master.baselib.view.CommonDialog
import com.palmapp.master.baselib.view.PermissionDialog
import com.palmapp.master.baselib.view.ScanPreview
import com.palmapp.master.module_face.R
import kotlinx.android.synthetic.main.face_activity_baby_takephoto.*

@Route(path = RouterConstants.ACTVITIY_FACE_BABY_TAKE_PHOTO)
class BabyTakephotoActivity : BaseMultipleMVPActivity(), IPhotoSelectorView, IPermissionView,
    BabyTakephotoView {
    private val permissionPresenter = PermissionPresenter(this)
    private val cameraPresenter = CameraPresenter(permissionPresenter)
    private val albumPresenter = AlbumPresenter(this, permissionPresenter)
    @JvmField
    @Autowired(name = "title")
    var title = ""
    @JvmField
    @Autowired(name = "path")
    var path = ""

    override fun onBackPressed() {
        if (btn_palmscan_ok.visibility == View.VISIBLE) {
            showNormalView()
        } else {
            super.onBackPressed()
        }
    }

    override fun addPresenters() {
        addToPresenter(permissionPresenter)
        addToPresenter(cameraPresenter)
        addToPresenter(albumPresenter)
    }

    override fun showResult() {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        ARouter.getInstance().inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.face_activity_baby_takephoto)
        val tv_title = findViewById<TextView>(R.id.tv_titlebar_title)
        findViewById<View>(R.id.iv_titlebar_back).setOnClickListener { finish() }
        tv_title.text = title

        face_picture.setOnClickListener {
            cameraPresenter.takePicture()
        }
        btn_palmscan_cancel.setOnClickListener {
            showNormalView()
        }
        ib_change_camera.setOnClickListener {
            cameraPresenter.changeFace()
        }
        btn_palmscan_ok.setOnClickListener {
            if (GoPrefManager.getDefault().getBoolean(
                    PreConstants.First.KEY_FIRST_UPLOAD_PERMISSION,
                    true
                )
            ) {
                val dialog = PermissionDialog(this)
                dialog.setOnClickBottomListener(object : CommonDialog.OnClickBottomListener {
                    override fun onPositiveClick() {
                        GoPrefManager.getDefault()
                            .putBoolean(PreConstants.First.KEY_FIRST_UPLOAD_PERMISSION, false)
                            .commit()
                        val bitmap = AppUtil.resizeBitmap(iv_face_result.getCropBitmap(false), 250, 250)
                        FileUtils.writeBitmap(path, bitmap)
                        setResult(1)
                        finish()
                    }

                    override fun onNegativeClick() {
                    }
                })
                dialog.show()
                return@setOnClickListener
            }
            val bitmap = AppUtil.resizeBitmap(iv_face_result.getCropBitmap(false), 250, 250)
            FileUtils.writeBitmap(path, bitmap)
            setResult(1)
            finish()
        }
        face_picture_choose.setOnClickListener {
            albumPresenter.choosePhoto()
        }
        cameraPresenter.bindCameraView(cartoon_camera)
        showNormalView()
    }

    override fun showPictureView(bitmap: Bitmap) {
        BaseSeq101OperationStatistic.uploadOperationStatisticData(BaseSeq101OperationStatistic.baby_page_f000, "", "4")
        face_picture_choose.visibility = View.INVISIBLE
        ib_change_camera.visibility = View.INVISIBLE
        face_picture.visibility = View.INVISIBLE
        btn_palmscan_cancel.visibility = View.VISIBLE
        btn_palmscan_ok.visibility = View.VISIBLE
        face_focusing_tip.visibility = View.INVISIBLE
        iv_face_result.setSource(bitmap)
        cameraPresenter.stopPreview()
    }

    override fun showNormalView() {
        if (TextUtils.equals(title, getString(R.string.face_baby_father))) {
            BaseSeq101OperationStatistic.uploadOperationStatisticData(BaseSeq101OperationStatistic.baby_page_f000, "", "3")
        } else {
            BaseSeq101OperationStatistic.uploadOperationStatisticData(BaseSeq101OperationStatistic.baby_page_f000, "", "2")
        }
        ib_change_camera.visibility = View.VISIBLE
        face_picture_choose.visibility = View.VISIBLE
        face_picture.visibility = View.VISIBLE
        btn_palmscan_cancel.visibility = View.INVISIBLE
        btn_palmscan_ok.visibility = View.INVISIBLE
        face_focusing_tip.visibility = View.VISIBLE
        iv_face_result.clearSource()
        cameraPresenter.startPreview()
    }

    override fun onPermissionGranted(code: Int) {
    }

    override fun onPermissionDenied(code: Int) {
        if (code == PERMISSION_CAMERA) {
            finish()
        }
    }

    override fun onPermissionEveryDenied(code: Int) {
    }

}
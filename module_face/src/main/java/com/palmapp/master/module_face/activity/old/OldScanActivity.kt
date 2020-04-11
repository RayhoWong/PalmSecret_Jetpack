package com.palmapp.master.module_face.activity.old

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import com.google.android.cameraview.AlbumPresenter
import com.google.android.cameraview.CameraPresenter
import com.google.android.cameraview.IPhotoSelectorView
import com.palmapp.master.baselib.BaseMultipleMVPActivity
import com.palmapp.master.baselib.BaseMultiplePresenter
import com.palmapp.master.baselib.manager.PERMISSION_CAMERA
import com.palmapp.master.baselib.permission.IPermissionView
import com.palmapp.master.baselib.permission.PermissionPresenter
import com.palmapp.master.baselib.statistics.BaseSeq103OperationStatistic
import com.palmapp.master.module_face.R
import com.palmapp.master.module_face.activity.cartoon.CartoonResultActivity
import kotlinx.android.synthetic.main.face_activity_cartoon_takephoto2.*
import kotlinx.android.synthetic.main.face_activity_old_scan.*
import kotlinx.android.synthetic.main.face_activity_old_scan.camera_view
import kotlinx.android.synthetic.main.face_activity_old_scan.confirm_view
import kotlinx.android.synthetic.main.face_activity_old_scan.ib_cancel
import kotlinx.android.synthetic.main.face_activity_old_scan.ib_change_camera
import kotlinx.android.synthetic.main.face_activity_old_scan.ib_ok
import org.greenrobot.eventbus.EventBus

/**
 *
 * @author :     xiemingrui
 * @since :      2020/3/19
 */
class OldScanActivity :BaseMultipleMVPActivity(),IPermissionView, IPhotoSelectorView {
    lateinit var finalBitmap :Bitmap
    private val permissionPresenter = PermissionPresenter(this)
    private val albumPresenter = AlbumPresenter(this,permissionPresenter)
    private val cameraPresenter = CameraPresenter(permissionPresenter)
    override fun addPresenters() {
        addToPresenter(permissionPresenter)
        addToPresenter(cameraPresenter)
        addToPresenter(albumPresenter)
    }
    override fun onPermissionGranted(code: Int) {
    }

    override fun onPermissionDenied(code: Int) {
        if(code == PERMISSION_CAMERA) {
            finish()
        }
    }

    override fun onPermissionEveryDenied(code: Int) {
    }

    override fun showPictureView(bitmap: Bitmap) {
        finalBitmap = bitmap
        iv_face_result.setSource(bitmap)
        camera_view.visibility = View.GONE
        confirm_view.visibility = View.VISIBLE
        cameraPresenter.stopPreview()
    }

    override fun showNormalView() {
        camera_view.visibility = View.VISIBLE
        confirm_view.visibility = View.GONE
        cameraPresenter.startPreview()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.face_activity_old_scan)
        cameraPresenter.bindCameraView(face_camera)
        showNormalView()

        face_picture.setOnClickListener {
            cameraPresenter.takePicture()
        }

        ib_cancel.setOnClickListener {
            showNormalView()
        }

        ib_ok.setOnClickListener {

        }

        ib_change_camera.setOnClickListener {
            cameraPresenter.changeFace()
        }
        face_picture_choose.setOnClickListener {
            albumPresenter.choosePhoto()
        }

        showNormalView()
    }
}
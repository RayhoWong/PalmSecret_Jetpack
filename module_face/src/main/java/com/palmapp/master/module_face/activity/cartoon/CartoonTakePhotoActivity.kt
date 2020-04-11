package com.palmapp.master.module_face.activity.cartoon

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.google.android.cameraview.AlbumPresenter
import com.palmapp.master.baselib.BaseMultipleMVPActivity
import com.palmapp.master.baselib.permission.IPermissionView
import com.palmapp.master.baselib.permission.PermissionPresenter
import com.palmapp.master.module_face.R
import com.google.android.cameraview.CameraPresenter
import com.google.android.cameraview.IPhotoSelectorView
import com.palmapp.master.baselib.GoCommonEnv
import com.palmapp.master.baselib.constants.RouterConstants
import com.palmapp.master.baselib.manager.PERMISSION_CAMERA
import com.palmapp.master.baselib.statistics.BaseSeq101OperationStatistic
import com.palmapp.master.baselib.statistics.BaseSeq103OperationStatistic
import com.palmapp.master.baselib.utils.FileUtils
import kotlinx.android.synthetic.main.face_activity_cartoon_takephoto2.*
import org.greenrobot.eventbus.EventBus
import java.io.File

/**
 *
 * @author :     xiemingrui
 * @since :      2020/3/10
 */
@Route(path = RouterConstants.ACTIVITY_CARTOON_TAKE_PHOTO)
class CartoonTakePhotoActivity : BaseMultipleMVPActivity(),
    IPhotoSelectorView, IPermissionView {
    @JvmField
    @Autowired(name = "newPlan")
    var newPlan = ""

    var sender = "1"
    lateinit var finalBitmap: Bitmap
    val permissionPresenter = PermissionPresenter(this)
    val albumPresenter = AlbumPresenter(this, permissionPresenter)
    val cameraPresenter =
        CameraPresenter(permissionPresenter,true)

    override fun onCreate(savedInstanceState: Bundle?) {
        ARouter.getInstance().inject(this)
        sender = if (newPlan == "1") "1" else "2"
        super.onCreate(savedInstanceState)
        setContentView(R.layout.face_activity_cartoon_takephoto2)
        ib_take_photo.setOnClickListener {
            cameraPresenter.takePicture()
        }

        ib_cancel.setOnClickListener {
            showNormalView()
        }

        ib_ok.setOnClickListener {
            BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
                BaseSeq103OperationStatistic.art_page_f000,
                sender, "3", ""
            )
            FileUtils.writeBitmap(GoCommonEnv.getApplication().cacheDir.path.plus(File.separator).plus("filter"), finalBitmap)
            EventBus.getDefault().postSticky(finalBitmap)
            ARouter.getInstance().build(RouterConstants.ACTIVITY_CARTOON_RESULT).withString("newPlan", newPlan)
                .navigation(this)
            CartoonTakePhotoActivity@ this.finish()
        }

        ib_change_camera.setOnClickListener {
            cameraPresenter.changeFace()
        }
        ib_album_choose.setOnClickListener {
            albumPresenter.choosePhoto()
        }
        cameraPresenter.bindCameraView(cartoon_camera)
        showNormalView()
        iv_takephoto_back.setOnClickListener {
            finish()
        }
        iv_takephoto_back.visibility = if (newPlan == "1") View.GONE else View.VISIBLE
    }

    override fun onBackPressed() {
        if (newPlan == "1") {
            return
        }
        super.onBackPressed()
    }

    override fun addPresenters() {
        addToPresenter(permissionPresenter)
        addToPresenter(cameraPresenter)
        addToPresenter(albumPresenter)
    }

    override fun showPictureView(bitmap: Bitmap) {
        BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
            BaseSeq103OperationStatistic.art_page_f000,
            sender, "2", ""
        )
        finalBitmap = bitmap
        camera_view.visibility = View.GONE
        confirm_view.visibility = View.VISIBLE
        face_image.setImageBitmap(bitmap)
        cameraPresenter.stopPreview()
    }

    override fun showNormalView() {
        BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
            BaseSeq103OperationStatistic.art_page_f000,
            sender, "1", ""
        )
        camera_view.visibility = View.VISIBLE
        confirm_view.visibility = View.GONE
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
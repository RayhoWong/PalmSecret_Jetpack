package com.palmapp.master.module_face.activity.animal

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.cs.bd.utils.ToastUtils
import com.example.oldlib.old.FaceDetect
import com.google.android.cameraview.AlbumPresenter
import com.google.android.cameraview.CameraPresenter
import com.google.android.cameraview.IPhotoSelectorView
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.ml.vision.face.FirebaseVisionFace
import com.palmapp.master.baselib.BaseMultipleMVPActivity
import com.palmapp.master.baselib.constants.RouterConstants
import com.palmapp.master.baselib.manager.PERMISSION_CAMERA
import com.palmapp.master.baselib.permission.IPermissionView
import com.palmapp.master.baselib.permission.PermissionPresenter
import com.palmapp.master.baselib.proxy.BillingServiceProxy
import com.palmapp.master.baselib.statistics.BaseSeq103OperationStatistic
import com.palmapp.master.baselib.ui.FaceAnimActivityInject
import com.palmapp.master.baselib.view.FaceAnimatorView
import com.palmapp.master.baselib.view.GradientBorderDrawable
import com.palmapp.master.baselib.view.PhotoEditView
import com.palmapp.master.module_face.R
import kotlinx.android.synthetic.main.face_activity_animal_scan.*
import org.greenrobot.eventbus.EventBus

/**
 *
 * @author :     xiemingrui
 * @since :      2020/3/31
 */
@Route(path = RouterConstants.ACTIVITY_ANIMAL_TAKE_PHOTO)
class AnimalScanActivity : BaseMultipleMVPActivity(), IPhotoSelectorView, IPermissionView {
    @JvmField
    @Autowired(name = "newPlan")
    var newPlan = ""
    private val permissionPresenter = PermissionPresenter(this)
    private val cameraPresenter = CameraPresenter(permissionPresenter)
    private val albumPresenter = AlbumPresenter(this, permissionPresenter)
    override fun onBackPressed() {
        if (face_button_layout.visibility == View.GONE) {
            showNormalView()
            return
        }
        if (newPlan == "1") {
            return
        }
        super.onBackPressed()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ARouter.getInstance().inject(this)
        setContentView(R.layout.face_activity_animal_scan)
        val drawable = GradientBorderDrawable(resources.getDimension(R.dimen.change_4px), resources.getDimension(R.dimen.change_84px), Color.parseColor("#0bc88c"), Color.parseColor("#187edc"))
        btn_cancel.background = drawable
        cameraPresenter.bindCameraView(cameraView)
        face_picture.setOnClickListener {
            cameraPresenter.takePicture()
        }
        ib_change_camera.setOnClickListener { cameraPresenter.changeFace() }
        face_picture_choose.setOnClickListener { albumPresenter.choosePhoto() }
        iv_palmscan_back.setOnClickListener { onBackPressed() }
        btn_cancel.setOnClickListener { showNormalView() }
        iv_palmscan_back.visibility = if (newPlan == "1") View.GONE else View.VISIBLE
        btn_complete.setOnClickListener {
            BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(BaseSeq103OperationStatistic.animal_page_f000, if (newPlan == "1") "1" else "2", "3", "")
            btn_complete.isClickable = false
            val result = scanview_animalscan.getCropBitmap(false)
            FaceAnimActivityInject.injectShowBitmap(this, layout_face_anim as ViewGroup, result, Runnable {
                FaceDetect.faceDetectToContourPoint(result, {
                    if (it.isEmpty()) {
                        BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(BaseSeq103OperationStatistic.animal_succ, "", "2", "")
                        FaceAnimActivityInject.injectFail(layout_face_anim as ViewGroup)
                        ToastUtils.makeEventToast(this, getString(R.string.face_daily_error), false)
                        showNormalView()
                    } else {
                        BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(BaseSeq103OperationStatistic.animal_succ, "", "1", "")
                        FaceAnimActivityInject.injectSuccess(this, layout_face_anim as ViewGroup, result, FaceDetect.getFloatList(it), object : FaceAnimatorView.OnAnimUpdateListener {
                            override fun onAnimEnd() {
                                EventBus.getDefault().postSticky(AnimalImageInfo(it, result))
                                runOnUiThread {
                                    ARouter.getInstance().build(RouterConstants.ACTIVITY_ANIMAL_RESULT).withString("newPlan", newPlan).navigation()
                                    finish()
                                }
                            }

                            override fun onUpdate(value: Int) {
                            }

                        })

                    }
                }, {
                    ToastUtils.makeEventToast(this, getString(R.string.face_daily_error), false)
                    BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(BaseSeq103OperationStatistic.animal_succ, "", "2", "")
                    FaceAnimActivityInject.injectFail(layout_face_anim as ViewGroup)
                    showNormalView()
                })
            })
        }
        showNormalView()
    }

    override fun onDestroy() {
        super.onDestroy()
        FaceAnimActivityInject.injectDetach(this, layout_face_anim as ViewGroup)
    }

    override fun addPresenters() {
        addToPresenter(permissionPresenter)
        addToPresenter(cameraPresenter)
        addToPresenter(albumPresenter)
    }

    override fun showPictureView(bitmap: Bitmap) {
        BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(BaseSeq103OperationStatistic.animal_page_f000, if (newPlan == "1") "1" else "2", "2", "")
        face_button_layout.visibility = View.GONE
        scanview_animalscan.setSource(bitmap)
        layout_content.visibility = View.VISIBLE
        scanview_animalscan.setMaskAndBorder(R.mipmap.image_frame_mask, R.mipmap.image_frame_border)
        cameraPresenter.stopPreview()
    }

    override fun showNormalView() {
        BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(BaseSeq103OperationStatistic.animal_page_f000, if (newPlan == "1") "1" else "2", "1", "")
        btn_complete.isClickable = true
        face_button_layout.visibility = View.VISIBLE
        layout_content.visibility = View.GONE
        scanview_animalscan.clearSource()
        scanview_animalscan.setMaskAndBorder(R.mipmap.scaning_face_mask, R.mipmap.scaning_face_line)
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
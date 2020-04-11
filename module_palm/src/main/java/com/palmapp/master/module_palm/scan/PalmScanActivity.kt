package com.palmapp.master.module_palm.scan;

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.google.android.cameraview.CameraPresenter
import com.google.android.cameraview.IPhotoSelectorView
import com.palmapp.master.baselib.BaseMultipleMVPActivity
import com.palmapp.master.baselib.GoCommonEnv
import com.palmapp.master.baselib.constants.PreConstants
import com.palmapp.master.baselib.constants.RouterConstants
import com.palmapp.master.baselib.constants.RouterConstants.ACTIVITY_PALM_SCAN
import com.palmapp.master.baselib.manager.GoPrefManager
import com.palmapp.master.baselib.permission.IPermissionView
import com.palmapp.master.baselib.permission.PermissionPresenter
import com.palmapp.master.baselib.statistics.BaseSeq101OperationStatistic
import com.palmapp.master.baselib.statistics.BaseSeq103OperationStatistic
import com.palmapp.master.baselib.view.CommonDialog
import com.palmapp.master.baselib.view.OkDialog
import com.palmapp.master.baselib.view.PermissionDialog
import com.palmapp.master.module_palm.R
import com.palmapp.master.module_palm.view.HandAnimatorView
import kotlinx.android.synthetic.main.palm_activity_palm_scan.*

@Route(path = ACTIVITY_PALM_SCAN)
class PalmScanActivity : BaseMultipleMVPActivity(), PalmScanView, IPhotoSelectorView, IPermissionView {
    @JvmField
    @Autowired(name = "newPlan")
    var newPlan = ""
    private val permissionPresenter = PermissionPresenter(this)
    private val cameraPresenter: CameraPresenter = CameraPresenter(permissionPresenter)
    private val palmScanPresenter = PalmScanPresenter()
    private var sender = "1"
    override fun finishActivity() {
        finish()
    }

    override fun onBackPressed() {
        if (newPlan == "1") {
            return
        }
        super.onBackPressed()
    }
    //切换左右手
    override fun changeHand() {
        scanview_palmscan.setChangeSide()
    }

    //上传图片失败
    override fun showNetWorkError() {
        PalmAnimActivityInject.injectDetach(this, layout_hand_view as ViewGroup)
        GoCommonEnv.stopBGM()
        BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
            BaseSeq103OperationStatistic.palm_succ,
            "1", "3", "3"
        )
        BaseSeq101OperationStatistic.uploadOperationStatisticData(BaseSeq101OperationStatistic.palm_scan, "", "3")
        runOnUiThread {
            showNormalView()
        }
    }

    //识别手相失败
    override fun showPicError() {
        PalmAnimActivityInject.injectDetach(this, layout_hand_view as ViewGroup)
        GoCommonEnv.stopBGM()
        BaseSeq101OperationStatistic.uploadOperationStatisticData(BaseSeq101OperationStatistic.palm_scan, "", "2")
        runOnUiThread {
            val dialog = OkDialog(this)
            dialog.title = getString(R.string.palm_scan_error)
            dialog.show()
            showNormalView()
        }
    }

    //出现扫描动画
    override fun showScan() {
        GoCommonEnv.startBGM()
        tv_palmscan_des.visibility = View.GONE
        iv_palmscan_takephoto.visibility = View.INVISIBLE
        iv_palmscan_change.visibility = View.GONE
        btn_palmscan_ok.visibility = View.GONE
        btn_palmscan_cancel.visibility = View.GONE
    }

    //出现摄像机
    override fun showCamera() {
        GoCommonEnv.stopBGM()
        BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
            BaseSeq103OperationStatistic.palm_scan_pro,
            sender,
            "1", ""
        )
        tv_palmscan_des.visibility = View.VISIBLE
        iv_palmscan_takephoto.visibility = View.VISIBLE
        iv_palmscan_change.visibility = View.VISIBLE
        btn_palmscan_ok.visibility = View.GONE
        btn_palmscan_cancel.visibility = View.GONE
        scanview_palmscan.clearSource()
        cameraPresenter.startPreview()
    }

    //出现确定页面
    override fun showConfirm(bitmap: Bitmap?) {
        BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
            BaseSeq103OperationStatistic.palm_scan_pro,
            sender,
            "2", ""
        )
        BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
            BaseSeq103OperationStatistic.enter_f000,
            "",
            "3",
            ""
        )
        tv_palmscan_des.visibility = View.GONE
        iv_palmscan_takephoto.visibility = View.INVISIBLE
        iv_palmscan_change.visibility = View.GONE
        scanview_palmscan.setSource(bitmap)
        btn_palmscan_ok.visibility = View.VISIBLE
        btn_palmscan_cancel.visibility = View.VISIBLE
        cameraPresenter.stopPreview()
    }

    //出现结果页
    override fun showResult(bitmap: Bitmap?,handleInfos: PalmResultCache) {
        BaseSeq101OperationStatistic.uploadOperationStatisticData(BaseSeq101OperationStatistic.palm_scan, "", "1")
        PalmAnimActivityInject.injectShowBitmap(this, layout_hand_view as ViewGroup, bitmap!!
            , Runnable {
                showPicError()
            }, object : HandAnimatorView.OnAnimUpdateListener{
                override fun onAnimEnd() {
                    runOnUiThread {
                        ARouter.getInstance().build(RouterConstants.ACTIVITY_PALM_RESULT).withString("newPlan", newPlan).navigation()
                        BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
                            BaseSeq103OperationStatistic.palm_scan_pro,
                            sender,
                            "3", ""
                        )
                        finish()
                    }
                }

                override fun onUpdate(value: Int) {
                }
            })

    }

    override fun addPresenters() {
        addToPresenter(palmScanPresenter)
        addToPresenter(permissionPresenter)
        addToPresenter(cameraPresenter)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        ARouter.getInstance().inject(this)
        sender = if (newPlan == "1") "1" else "2"
        super.onCreate(savedInstanceState)
        setContentView(R.layout.palm_activity_palm_scan)
        BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
            BaseSeq103OperationStatistic.palm_scan_pro,
            sender,
            "1", ""
        )
        if (GoCommonEnv.isFirstRunPayGuide)
            BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
                BaseSeq103OperationStatistic.enter_f000,
                "",
                "2",
                ""
            )
        iv_palmscan_back.visibility = if (newPlan == "1") View.GONE else View.VISIBLE
        iv_palmscan_back.setOnClickListener { finish() }
        cameraPresenter.bindCameraView(cameraView)
        iv_palmscan_takephoto.setOnClickListener {
            cameraPresenter.takePicture()
        }
        btn_palmscan_ok.setOnClickListener {
            val bitmap = scanview_palmscan.getCropBitmap(false)
            PalmAnimActivityInject.injectShowScan(this, layout_hand_view as ViewGroup, bitmap)
            palmScanPresenter.uploadPalm(bitmap)
        }
        btn_palmscan_cancel.setOnClickListener {
            showNormalView()
        }
        iv_palmscan_change.setOnClickListener {
            changeHand()
        }
        showNormalView()
    }

    override fun onDestroy() {
        if(layout_hand_view != null) {
            PalmAnimActivityInject.injectDetach(this, layout_hand_view as ViewGroup)
        }
        super.onDestroy()
    }

    override fun finish() {
        super.finish()
    }

    override fun onPause() {
        super.onPause()
        GoCommonEnv.stopBGM()
    }

    override fun showPictureView(bitmap: Bitmap) {
        showConfirm(bitmap)
    }

    override fun showNormalView() {
        showCamera()
    }

    override fun onPermissionGranted(code: Int) {

    }

    override fun onPermissionDenied(code: Int) {
        finish()
    }

    override fun onPermissionEveryDenied(code: Int) {
    }

}
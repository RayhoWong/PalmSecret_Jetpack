package com.palmapp.master.module_face.activity.baby;

import android.animation.ValueAnimator
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.TextView
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.palmapp.master.baselib.BaseMVPActivity
import com.palmapp.master.baselib.bean.face.BabyGenerateRequest
import com.palmapp.master.baselib.bean.face.BabyGenerateResponse
import com.palmapp.master.baselib.clickWithTrigger
import com.palmapp.master.baselib.constants.AppConstants
import com.palmapp.master.baselib.constants.RouterConstants
import com.palmapp.master.baselib.proxy.BillingServiceProxy
import com.palmapp.master.baselib.statistics.BaseSeq101OperationStatistic
import com.palmapp.master.baselib.statistics.BaseSeq103OperationStatistic
import com.palmapp.master.baselib.view.CommonDialog
import com.palmapp.master.baselib.view.LoadingDialog
import com.palmapp.master.baselib.view.OkDialog
import com.palmapp.master.module_ad.manager.RewardAdManager
import com.palmapp.master.module_face.R
import kotlinx.android.synthetic.main.face_activity_baby_match.*

@Route(path = RouterConstants.ACTVITIY_FACE_BABY_MATCH)
class BabyMatchActivity : BaseMVPActivity<BabyMatchView, BabyMatchPresenter>(), BabyMatchView {
    private var onClickTime = 0L
    @JvmField
    @Autowired(name = "newPlan")
    var newPlan = ""

    override fun showNetError() {
        BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(BaseSeq103OperationStatistic.face_scan, "2", "3", "")
        val dialog = OkDialog(this)
        dialog.title = getString(R.string.net_error)
        dialog.show()
    }

    private var translate: ValueAnimator? = null
    private var rotate: ValueAnimator? = null
    var loadingDialog: LoadingDialog? = null
    override fun showFaceError(status: String?) {
        val dialog = OkDialog(this)
        dialog.title = getString(R.string.face_daily_error)
        dialog.show()
    }

    override fun dismissLoadingDialog() {
        loadingDialog?.dismiss()
    }

    override fun showLoadingDialog() {
        loadingDialog?.dismiss()
        loadingDialog = LoadingDialog(this)
        loadingDialog?.show()
    }

    override fun showPicError() {
        val dialog = OkDialog(this)
        dialog.title = getString(R.string.face_baby_dialog_error)
        dialog.show()
    }

    override fun showCamera(isFather: Boolean) {
        val path = if (isFather) AppConstants.FACE_BABY_FATHER else AppConstants.FACE_BABY_MOTHER
        val title =
            if (isFather) getString(R.string.face_baby_father) else getString(R.string.face_baby_mother)
        val requestCode = if (isFather) 1001 else 1002
        ARouter.getInstance().build(RouterConstants.ACTVITIY_FACE_BABY_TAKE_PHOTO)
            .withString("path", path).withString("title", title).navigation(this, requestCode)
    }

    override fun showResult(request: BabyGenerateRequest, response: BabyGenerateResponse?) {
        ARouter.getInstance().build(RouterConstants.ACTVITIY_FACE_BABY_RESULT)
            .withSerializable("result", response).withSerializable("request", request).withString("newPlan", newPlan)
            .navigation(this)
        finish()
    }

    override fun showFatherPic(bitmap: Bitmap) {
        BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(BaseSeq103OperationStatistic.face_scan, "2", "1", "")
        iv_face_baby_src2.setImageBitmap(bitmap)
    }

    override fun showMotherPic(bitmap: Bitmap) {
        BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(BaseSeq103OperationStatistic.face_scan, "2", "1", "")
        iv_face_baby_src1.setImageBitmap(bitmap)
    }


    override fun showLoadingView() {
        BaseSeq101OperationStatistic.uploadOperationStatisticData(
            BaseSeq101OperationStatistic.baby_page_f000,
            "",
            "5"
        )
        iv_face_baby_camera1.visibility = View.INVISIBLE
        iv_face_baby_camera2.visibility = View.INVISIBLE
        iv_face_baby_scan1.visibility = View.VISIBLE
        iv_face_baby_scan2.visibility = View.VISIBLE
        iv_face_baby_circle1.visibility = View.VISIBLE
        iv_face_baby_circle2.visibility = View.VISIBLE

        rotate = ValueAnimator.ofFloat(0f, 360f)
        rotate?.duration = 1000
        rotate?.repeatCount = -1
        rotate?.interpolator = LinearInterpolator()
        rotate?.addUpdateListener {
            val value = it.animatedValue as Float
            iv_face_baby_circle1.rotation = value
            iv_face_baby_circle2.rotation = value
        }
        val start = iv_face_baby_scan1.height * -1f
        val end = iv_face_baby_circle1.height * 1f
        translate = ValueAnimator.ofFloat(start, end)
        translate?.duration = 2000
        translate?.repeatCount = -1
        translate?.interpolator = LinearInterpolator()
        translate?.addUpdateListener {
            val value = it.animatedValue as Float
            iv_face_baby_scan1.translationY = value
            iv_face_baby_scan2.translationY = value
        }
        rotate?.start()
        translate?.start()
    }

    override fun showDefaultView() {
        rotate?.cancel()
        translate?.cancel()
        iv_face_baby_camera1.visibility = View.VISIBLE
        iv_face_baby_camera2.visibility = View.VISIBLE
        iv_face_baby_scan1.visibility = View.INVISIBLE
        iv_face_baby_scan2.visibility = View.INVISIBLE
        iv_face_baby_circle1.visibility = View.INVISIBLE
        iv_face_baby_circle2.visibility = View.INVISIBLE
    }

    override fun createPresenter(): BabyMatchPresenter {
        return BabyMatchPresenter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        ARouter.getInstance().inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.face_activity_baby_match)
        BaseSeq101OperationStatistic.uploadOperationStatisticData(
            BaseSeq101OperationStatistic.baby_page_f000,
            "",
            "1"
        )
        val title = findViewById<TextView>(R.id.tv_titlebar_title)
        val iv_back = findViewById<View>(R.id.iv_titlebar_back)
        iv_back.visibility = if (newPlan == "1") View.GONE else View.VISIBLE
        iv_back.setOnClickListener { finish() }
        title.text = getString(R.string.face_baby_title)

        iv_face_baby_camera1.clickWithTrigger {
            showCamera(false)
        }

        iv_face_baby_camera2.clickWithTrigger {
            showCamera(true)
        }

        btn_payment_continue.setOnClickListener {
            mPresenter?.uploadPic()
        }
        iv_face_baby_src1.clickWithTrigger { showCamera(false) }
        iv_face_baby_src2.clickWithTrigger { showCamera(true) }
        showDefaultView()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == 1) {
            when (requestCode) {
                1001 -> mPresenter?.uploadFatherPic(BitmapFactory.decodeFile(AppConstants.FACE_BABY_FATHER))
                1002 -> mPresenter?.uploadMotherPic(BitmapFactory.decodeFile(AppConstants.FACE_BABY_MOTHER))
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        rotate?.cancel()
        translate?.cancel()
    }
}
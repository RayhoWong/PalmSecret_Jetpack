package com.palmapp.master.module_palm.match;

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.TextView
import com.alibaba.android.arouter.launcher.ARouter
import com.palmapp.master.baselib.BaseMVPActivity
import com.palmapp.master.baselib.GoCommonEnv
import com.palmapp.master.baselib.bean.palm.PalmMatchResponse
import com.palmapp.master.baselib.manager.ThreadExecutorProxy
import com.palmapp.master.baselib.statistics.BaseSeq101OperationStatistic
import com.palmapp.master.baselib.statistics.BaseSeq103OperationStatistic
import com.palmapp.master.baselib.view.OkDialog
import com.palmapp.master.module_palm.R
import com.palmapp.master.module_palm.scan.PalmImageManager
import kotlinx.android.synthetic.main.palm_activity_palm_match_loading.*
import org.greenrobot.eventbus.EventBus
import java.io.File

class PalmMatchLoadingActivity : BaseMVPActivity<PalmMatchLoadingView, PalmMatchLoadingPresenter>(),
    PalmMatchLoadingView, DialogInterface.OnDismissListener {
    var rotate: ValueAnimator? = null
    var translate: ValueAnimator? = null
    override fun finishActivity() {
        val intent = Intent(this, PalmMatchActivity::class.java)
        intent.putExtra("isFirst", true)
        startActivity(intent)
        finish()
    }

    override fun onDismiss(dialog: DialogInterface?) {
        val intent = Intent(this, PalmMatchActivity::class.java)
        intent.putExtra("isFirst", true)
        startActivity(intent)
        finish()
    }

    override fun showResult(data: PalmMatchResponse) {
        BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
            BaseSeq103OperationStatistic.palm_succ,
            "2","3", "1"
        )
        BaseSeq101OperationStatistic.uploadOperationStatisticData(
            BaseSeq101OperationStatistic.palm_match_result,
            "",
            "1"
        )
        val intent = Intent(this, PalmMatchResultActivity::class.java)
        intent.putExtra("result", data)
        startActivity(intent)
        finish()
    }

    override fun showPicError() {
        BaseSeq101OperationStatistic.uploadOperationStatisticData(
            BaseSeq101OperationStatistic.palm_match_result,
            "",
            "2"
        )
        val dialog = OkDialog(this)
        dialog.title = getString(R.string.palm_scan_error)
        dialog.setOnDismissListener(this)
        dialog.show()
    }

    override fun showNetError() {
        BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
            BaseSeq103OperationStatistic.palm_succ,
            "2","3", "3"
        )
        BaseSeq101OperationStatistic.uploadOperationStatisticData(
            BaseSeq101OperationStatistic.palm_match_result,
            "",
            "3"
        )
        val dialog = OkDialog(this)
        dialog.title = getString(R.string.net_error)
        dialog.setOnDismissListener(this)
        dialog.show()
    }

    private val path1 =
        GoCommonEnv.getApplication().cacheDir.path.plus(File.separator).plus("palm1")
    private val path2 =
        GoCommonEnv.getApplication().cacheDir.path.plus(File.separator).plus("palm2")

    override fun createPresenter(): PalmMatchLoadingPresenter {
        return PalmMatchLoadingPresenter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.palm_activity_palm_match_loading)
        BaseSeq101OperationStatistic.uploadOperationStatisticData(
            BaseSeq101OperationStatistic.palm_match_a000,
            "",
            "3"
        )
        findViewById<TextView>(R.id.tv_titlebar_title).text =
            getString(R.string.palm_palmmatch_title)
        findViewById<View>(R.id.iv_titlebar_back).setOnClickListener {
            finish()
        }
        val bitmap1 = BitmapFactory.decodeFile(path1)
        val bitmap2 = BitmapFactory.decodeFile(path2)
        if (bitmap1 == null || bitmap2 == null) {
            finish()
            return
        }
        iv_palmmatch_src1.setImageBitmap(bitmap1)
        iv_palmmatch_src2.setImageBitmap(bitmap2)
        ThreadExecutorProxy.runOnIdleThread(Runnable {
            rotate = ValueAnimator.ofFloat(0f, 360f)
            rotate?.duration = 1000
            rotate?.repeatCount = -1
            rotate?.interpolator = LinearInterpolator()
            rotate?.addUpdateListener {
                val value = it.animatedValue as Float
                iv_palmmatch_circle1.rotation = value
                iv_palmmatch_circle2.rotation = value
            }
            val start = iv_palmmatch_scan1.height * -1f
            val end = iv_palmmatch_circle1.height * 1f
            translate = ValueAnimator.ofFloat(start, end)
            translate?.duration = 2000
            translate?.repeatCount = -1
            translate?.interpolator = LinearInterpolator()
            translate?.addUpdateListener {
                val value = it.animatedValue as Float
                iv_palmmatch_scan1.translationY = value
                iv_palmmatch_scan2.translationY = value
            }
            rotate?.start()
            translate?.start()
        })
        mPresenter?.startRequest(bitmap1, bitmap2)
    }

    override fun onDestroy() {
        super.onDestroy()
        rotate?.cancel()
        translate?.cancel()
    }
}

class FinishEvent(val type: Int)
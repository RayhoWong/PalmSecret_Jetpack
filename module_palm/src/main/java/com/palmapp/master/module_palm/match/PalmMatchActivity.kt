package com.palmapp.master.module_palm.match;

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Camera
import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.alibaba.android.arouter.facade.annotation.Route
import com.palmapp.master.baselib.BaseMVPActivity
import com.palmapp.master.baselib.GoCommonEnv
import com.palmapp.master.baselib.constants.RouterConstants
import com.palmapp.master.baselib.statistics.BaseSeq101OperationStatistic
import com.palmapp.master.baselib.statistics.BaseSeq103OperationStatistic
import com.palmapp.master.module_palm.R
import com.palmapp.master.baselib.view.ScanPreview
import kotlinx.android.synthetic.main.palm_activity_palm_match.*
import org.greenrobot.eventbus.Subscribe
import java.io.File

@Route(path = RouterConstants.ACTIVITY_PALM_MATCH)
class PalmMatchActivity : BaseMVPActivity<PalmMatchView, PalmMatchPresenter>(), PalmMatchView {
    private var isFirst = true
    private var mPalm: ScanPreview? = null
    private var mContent: RelativeLayout? = null
    override fun getCamera(camera: Camera?) {
        camera?.apply {
            mPalm = ScanPreview(this@PalmMatchActivity, this)
            mContent?.addView(mPalm, 0)
        }
    }

    override fun next() {
        if (isFirst) {
            val intent = Intent(this, PalmMatchActivity::class.java)
            intent.putExtra("isFirst", false)
            startActivity(intent)
            finish()
        } else {
            startActivity(Intent(this, PalmMatchLoadingActivity::class.java))
            finish()
        }
    }


    override fun createPresenter(): PalmMatchPresenter {
        isFirst = intent.getBooleanExtra("isFirst", true)
        return PalmMatchPresenter(GoCommonEnv.getApplication().cacheDir.path.plus(File.separator).plus(if (isFirst) "palm1" else "palm2"))
    }


    override fun onRestart() {
        super.onRestart()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
            mPresenter?.startScan()
        }else{
            Toast.makeText(this, R.string.camera_permission_not_granted, Toast.LENGTH_SHORT)
                .show()
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.palm_activity_palm_match)
        findViewById<View>(R.id.iv_titlebar_back).setOnClickListener { finishActivity() }
        mContent = findViewById(R.id.camera_preview)
        iv_palmscan_takephoto.setOnClickListener {
            mPresenter?.takePhoto()
        }
        if (isFirst) {
            BaseSeq101OperationStatistic.uploadOperationStatisticData(
                BaseSeq101OperationStatistic.palm_match_a000,
                "",
                "1"
            )
            findViewById<TextView>(R.id.tv_titlebar_title).text = getString(R.string.palm_palmmatch_title1)
            tv_palmscan_des.text = getString(R.string.palm_palmmatch_message1)
        } else {
            BaseSeq101OperationStatistic.uploadOperationStatisticData(
                BaseSeq101OperationStatistic.palm_match_a000,
                "",
                "2"
            )
            findViewById<TextView>(R.id.tv_titlebar_title).text = getString(R.string.palm_palmmatch_title2)
            tv_palmscan_des.text = getString(R.string.palm_palmmatch_message2)
            scanview_palmscan.setChangeSide()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishActivity()
    }

    override fun onStart() {
        super.onStart()
        mPresenter?.startScan()
    }

    @Subscribe
    fun onFinishEvent(event: FinishEvent) {
        when (event.type) {
            -1 -> finish()
            1 -> if (isFirst) finish()
            2 -> if (!isFirst) finish()
        }
    }

    fun finishActivity() {
        if (!isFirst) {
            val intent = Intent(this, PalmMatchActivity::class.java)
            intent.putExtra("isFirst", true)
            startActivity(intent)
        }
        finish()
    }

    override fun finish() {
        mPresenter?.stopScan()
        super.finish()
    }
}
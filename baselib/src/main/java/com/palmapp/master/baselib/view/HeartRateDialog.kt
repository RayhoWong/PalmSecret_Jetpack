package com.palmapp.master.baselib.view

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import com.alibaba.android.arouter.launcher.ARouter
import com.palmapp.master.baselib.R
import com.palmapp.master.baselib.constants.RouterConstants
import com.palmapp.master.baselib.statistics.BaseSeq103OperationStatistic
import kotlinx.android.synthetic.main.layout_heartrate_dialog.*

/**
 * Created by Rayho on 2020/3/23.
 */
class HeartRateDialog(val context: Activity) :
    Dialog(context, R.style.CommonDialog) {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_heartrate_dialog)
        initView()
    }


    private fun initView() {
        window.setBackgroundDrawableResource(R.color.color_99000000)
        val lp = window.attributes
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.MATCH_PARENT
        lp.gravity = Gravity.CENTER
        window.attributes = lp
        setCancelable(false)
        setCanceledOnTouchOutside(false)

        iv_close.setOnClickListener {
            BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(BaseSeq103OperationStatistic.heart_rate_a000,"","2","")
            dismiss()
        }


        tv_try.setOnClickListener {
            BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(BaseSeq103OperationStatistic.heart_rate_a000,"","1","")
            ARouter.getInstance().build(RouterConstants.ACTIVITY_PSY_HEARTRATE_DETECT)
                .navigation(context)
            dismiss()
        }

    }
}
package com.palmapp.master.baselib.view

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.palmapp.master.baselib.R
import com.palmapp.master.baselib.statistics.BaseSeq101OperationStatistic

/**
 *
 * @author :     xiemingrui
 * @since :      2020/1/2
 */
class AdsWatchDialog(context: Context, val n: Int, val listener: View.OnClickListener) :
    Dialog(context, R.style.CommonDialog) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ad_layout_ads_watch)
        BaseSeq101OperationStatistic.uploadOperationStatisticData(BaseSeq101OperationStatistic.ad_channel_f000,"","2")
        setCancelable(false)
        setCanceledOnTouchOutside(false)
        findViewById<View>(R.id.tv_left).setOnClickListener {
            BaseSeq101OperationStatistic.uploadOperationStatisticData(BaseSeq101OperationStatistic.ad_channel_a000,"0","2")
            dismiss()
        }
        findViewById<View>(R.id.tv_right).setOnClickListener {
            BaseSeq101OperationStatistic.uploadOperationStatisticData(BaseSeq101OperationStatistic.ad_channel_a000,"1","2")
            listener.onClick(it)
        }
        val tv = findViewById<TextView>(R.id.content)
        tv.text = context.getString(R.string.pay_dialog_watch_ads_sub_title,n)
    }
}
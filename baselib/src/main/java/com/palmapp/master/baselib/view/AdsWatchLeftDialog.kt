package com.palmapp.master.baselib.view

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.TextView
import com.palmapp.master.baselib.R
import com.palmapp.master.baselib.manager.config.AdConfig
import com.palmapp.master.baselib.manager.config.ConfigManager
import com.palmapp.master.baselib.manager.config.MODEL_REWARD_ID
import com.palmapp.master.baselib.statistics.BaseSeq101OperationStatistic

/**
 *
 * @author :     xiemingrui
 * @since :      2020/1/2
 */
class AdsWatchLeftDialog(context: Context, val n: Int, val listener: View.OnClickListener) :
    Dialog(context, R.style.CommonDialog) {
    val times = (ConfigManager.getConfig(AdConfig::class.java)?.hashMap?.get(MODEL_REWARD_ID)
        ?.show_count?: 1)+1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ad_layout_ads_watch_left)
        BaseSeq101OperationStatistic.uploadOperationStatisticData(BaseSeq101OperationStatistic.ad_channel_f000,n.toString(),"3")
        findViewById<View>(R.id.confirm).setOnClickListener {
            BaseSeq101OperationStatistic.uploadOperationStatisticData(
                BaseSeq101OperationStatistic.ad_channel_a000,"${times-n}-0","3")
            listener.onClick(it)
        }
        findViewById<TextView>(R.id.content).text = n.toString()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            BaseSeq101OperationStatistic.uploadOperationStatisticData(
                BaseSeq101OperationStatistic.ad_channel_a000,"${times-n}-1","3")
        }
        return super.onKeyDown(keyCode, event)
    }
}
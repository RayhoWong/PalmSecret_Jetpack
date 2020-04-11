package com.palmapp.master.baselib.view

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.alibaba.android.arouter.launcher.ARouter
import com.github.mmin18.widget.RealtimeBlurView
import com.palmapp.master.baselib.R
import com.palmapp.master.baselib.constants.RouterConstants
import com.palmapp.master.baselib.event.SubscribeUpdateEvent
import com.palmapp.master.baselib.proxy.AdSdkServiceProxy
import com.palmapp.master.baselib.proxy.BillingServiceProxy
import com.palmapp.master.baselib.statistics.BaseSeq101OperationStatistic
import org.greenrobot.eventbus.EventBus

/**
 *  订阅引导模糊页
 * @author :     xiemingrui
 * @since :      2019/8/30
 */

class PalmBlurView(context: Context, attributeSet: AttributeSet) :
    FrameLayout(context, attributeSet) {
    var entrance: String = ""
    var watch: View? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.layout_blur_view, this)
        setOnClickListener { }
        findViewById<View>(R.id.btn_pay).setOnClickListener {
            ARouter.getInstance().build(RouterConstants.ACTIVITY_PAY)
                .withString("entrance", entrance).navigation()
        }
        val drawable = GradientBorderDrawable(
            context.resources.getDimension(R.dimen.change_4px),
            context.resources.getDimension(R.dimen.change_84px),
            Color.parseColor("#0bc88c"),
            Color.parseColor("#187edc")
        )
        watch = findViewById<View>(R.id.btn_pay_watch)
        watch?.background = drawable
        watch?.visibility = if (AdSdkServiceProxy.isRewardAdLoad()) View.VISIBLE else View.GONE
        if(AdSdkServiceProxy.isRewardAdLoad()){
            BaseSeq101OperationStatistic.uploadOperationStatisticData(
                BaseSeq101OperationStatistic.ad_channel_f000,"","1")
        }
    }

    fun setWatchClickListener(listener: OnClickListener) {
        watch?.setOnClickListener(View.OnClickListener {
            BaseSeq101OperationStatistic.uploadOperationStatisticData(
                BaseSeq101OperationStatistic.ad_channel_a000,"","1")
            listener.onClick(watch)
        })
    }
}
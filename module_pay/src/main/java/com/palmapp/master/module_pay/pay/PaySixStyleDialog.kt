package com.palmapp.master.module_pay.pay

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.*
import android.text.style.ForegroundColorSpan
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import com.palmapp.master.baselib.Product
import com.palmapp.master.baselib.bean.pay.PaymentGuideConfig
import com.palmapp.master.baselib.manager.PayGuideManager
import com.palmapp.master.baselib.proxy.BillingServiceProxy
import com.palmapp.master.baselib.proxy.OnBillingCallBack
import com.palmapp.master.baselib.statistics.BaseSeq101OperationStatistic
import com.palmapp.master.baselib.statistics.BaseSeq103OperationStatistic
import com.palmapp.master.baselib.statistics.BaseSeq59OperationStatistic
import com.palmapp.master.baselib.utils.AppUtil
import com.palmapp.master.module_pay.BillingManager
import com.palmapp.master.module_pay.R
import kotlinx.android.synthetic.main.pay_fragment_six.*
import java.lang.Exception

/**
 *
 * @author :     xiemingrui
 * @since :      2019/11/11
 * 2451
 */
class PaySixStyleDialog(val activity: Activity, val newPlan: Boolean) :
    Dialog(activity, R.style.CommonDialog) {
    private var mClickTimes: Int = 0
    private val entrance = if (newPlan) "15" else "8"
    private var style = "style6"
    private var time = 0L
    private val config: PaymentGuideConfig =
        PayGuideManager.paymentGuideConfigs.get(entrance)?.contents?.get("style6") ?: getDefaultConfig()

    private fun getDefaultConfig(): PaymentGuideConfig {
        style = "local_$style"
        val config = PaymentGuideConfig()
        config.mainText1 = context.resources.getString(R.string.pay_default_six_main_text1)
        config.mainText2 = context.resources.getString(R.string.pay_default_six_main_text2)
        config.title = context.resources.getString(R.string.pay_default_six_title)
        config.button1 = context.resources.getString(R.string.pay_default_six_button_text)
        config.button1Sku = Product.DEFAULT_SKU_MONTH
        config.state = context.resources.getString(R.string.pay_default_six_state)
        return config
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        val result = (System.currentTimeMillis() - time) / 1000
        BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(BaseSeq103OperationStatistic.subs_page_time,result.toString(),entrance,"")
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        time = System.currentTimeMillis()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pay_fragment_six)
        BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
            BaseSeq103OperationStatistic.vip_gui_page_f000,
            "",
            entrance,""
        )
        BaseSeq59OperationStatistic.uploadStatisticPayShow(context, config.button1Sku, entrance, style)
        val lp = window?.attributes
        lp?.gravity = Gravity.CENTER
        lp?.width = (AppUtil.getScreenW(context) * 0.833).toInt()
        lp?.height = WindowManager.LayoutParams.WRAP_CONTENT
        window?.attributes = lp
        setCanceledOnTouchOutside(false)
        initView()
    }

    private fun initView() {
        val banner = findViewById<View>(R.id.iv_payment_banner)
        val lp = banner.layoutParams
        lp.width = (AppUtil.getScreenW(context) * 0.833).toInt()
        lp.height = (lp.width / 3.06).toInt()
        banner.layoutParams = lp
        try {
            config.title = config.title.replace("<divider/>", "\n")
            val index1 = config.title.indexOf("@")
            val index2 = config.title.lastIndexOf("@")
            val free = config.title.subSequence(index1 + 1, index2).toString()
            val title = config.title.replace("@", "")
            val sp = SpannableString(title)
            sp.setSpan(
                ForegroundColorSpan(Color.parseColor("#ffe11b")),
                title.indexOf(free),
                title.indexOf(free) + free.length,
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE
            )
            tv_payment_title1.text = sp
        } catch (e: Exception) {
            tv_payment_title1.text = config.title
        }


        val split = config.mainText1.split("<divider/>")
        val t = arrayListOf<String>()

        repeat(split.size) {
            t.add(split.get(it))
        }

        repeat(t.size) {
            val textView =
                layoutInflater.inflate(
                    R.layout.pay_six_item,
                    tv_payment_content,
                    false
                ) as TextView
            if (it == 0) {
                textView.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD)
            }
            textView.text = t.get(it)
            textView.setPadding(
                textView.paddingLeft,
                0,
                0,
                context.resources.getDimensionPixelSize(R.dimen.change_42px)
            )
            tv_payment_content.addView(textView)
        }

        tv_payment_maintext2.text = config.mainText2
        tv_payment_continue.text = config.button1
        btn_payment_continue.setOnClickListener {
            BillingManager.startPay(
                activity,
                config.button1Sku,
                entrance,
                style,
                object : OnBillingCallBack {
                    override fun onPaySuccess(sku: String) {
                        BillingServiceProxy.handlePaySuccess(activity)
                        dismiss()
                    }

                    override fun onPayError(code: Int) {
                        BillingServiceProxy.handlePayFailed(activity, code)
                    }
                },++mClickTimes)
        }
        iv_payment_close.setOnClickListener {
            BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
                BaseSeq103OperationStatistic.subs_close_a000, "0", "8")
            dismiss()
        }
        tv_payment_state.text = config.state

    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
                BaseSeq103OperationStatistic.subs_close_a000, "1", "8")
        }
        return super.onKeyDown(keyCode, event)
    }
}

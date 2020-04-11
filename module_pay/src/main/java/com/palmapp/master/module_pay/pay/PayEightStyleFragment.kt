package com.palmapp.master.module_pay.pay

import android.content.Intent
import android.graphics.SurfaceTexture
import android.media.MediaPlayer
import android.net.Uri
import android.view.*
import com.palmapp.master.baselib.GoCommonEnv
import com.palmapp.master.baselib.bean.pay.PaymentGuideConfig
import com.palmapp.master.baselib.constants.AppConstants
import com.palmapp.master.module_pay.R
import android.os.Handler
import android.os.Message
import android.text.TextUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.children
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.palmapp.master.baselib.Product
import com.palmapp.master.baselib.manager.ThreadExecutorProxy
import com.palmapp.master.baselib.statistics.BaseSeq101OperationStatistic
import com.palmapp.master.baselib.statistics.BaseSeq103OperationStatistic
import com.palmapp.master.baselib.statistics.BaseSeq59OperationStatistic
import com.palmapp.master.module_pay.BillingManager
import com.palmapp.master.module_pay.shakeBtn
import kotlinx.android.synthetic.main.pay_fragment_eight.*
import java.lang.Exception


/**
 *
 * @author :     xiemingrui
 * @since :      2019/8/21
 */
class PayEightStyleFragment : PayBaseStyleFragment() {
    private var style = "style8"
    private var videoUrl = ""
    override fun isNeedToBlockBack(): Boolean {
        return iv_payment_close.visibility != View.VISIBLE
    }

    override fun getStyle(): String {
        return "style8"
    }

    override fun getResLayout() = R.layout.pay_fragment_eight

    override fun getDefaultConfig(): PaymentGuideConfig? {
        style = if (isLauncherScen()) "local_style8" else "internal_style8"
        val config = PaymentGuideConfig()
        config.option1Text1 =
            GoCommonEnv.getApplication().getString(R.string.pay_default_two_option1_text1)
        config.option1Text2 =
            GoCommonEnv.getApplication().getString(R.string.pay_default_two_option1_text2)
        config.option2Text1 =
            GoCommonEnv.getApplication().getString(R.string.pay_default_two_option2_text1)
        config.option2Text2 =
            GoCommonEnv.getApplication().getString(R.string.pay_default_two_option2_text2)
        config.option2Sku = Product.DEFAULT_SKU_MONTH
        config.option1Sku = Product.DEFAULT_SKU_YEAR
        config.closeButtonAlpha = 0.7f
        config.closeButtonDelay = 0
        config.button1 = GoCommonEnv.getApplication().getString(R.string.pay_default_two_btn1_title)
        config.mainText1 =
            if (TextUtils.equals(
                    type,
                    "11"
                )
            ) GoCommonEnv.getApplication().getString(R.string.pay_default_two_cnt_title) else GoCommonEnv.getApplication().getString(
                R.string.pay_default_two_tarot_title
            )
        videoUrl = if (TextUtils.equals(type, "10")) "style_video5" else "style_video6"
        return config
    }

    override fun initView() {
        paymentGuideConfig?.let { paymentGuideConfig ->
            if (TextUtils.isEmpty(paymentGuideConfig.title)) {
                tv_payment_title1.visibility = View.GONE
            } else {
                tv_payment_title1.visibility = View.VISIBLE
                tv_payment_title1.text = paymentGuideConfig.title.replace("<divider/>", "\n")
            }
            layout_round.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
            video_payment.setVideoURI(Uri.parse("android.resource://" + GoCommonEnv.getApplication().getPackageName() + "/raw/${videoUrl}"))
            video_payment.setOnPreparedListener {
                it.isLooping = true
                it.start()
            }
            tv_payment_title2.text = paymentGuideConfig.mainText1
            iv_payment_close.alpha = paymentGuideConfig.closeButtonAlpha
            ThreadExecutorProxy.runOnMainThread(Runnable {
                if (iv_payment_close != null) {
                    iv_payment_close.visibility = View.VISIBLE
                }
            }, paymentGuideConfig.closeButtonDelay * 1000L)

            btn_payment_continue.text = paymentGuideConfig.button1
            tv_payment_btn_text1.text = paymentGuideConfig.option1Text1
            if (TextUtils.isEmpty(paymentGuideConfig.option1Text2)) {
                tv_payment_btn_subtext1.visibility = View.GONE
            } else {
                tv_payment_btn_subtext1.text = paymentGuideConfig.option1Text2
            }
            tv_payment_btn_text2.text = paymentGuideConfig.option2Text1
            tv_payment_btn_subtext2.text = paymentGuideConfig.option2Text2
            if (TextUtils.isEmpty(paymentGuideConfig.option2Text2)) {
                tv_payment_btn_subtext2.visibility = View.GONE
            } else {
                tv_payment_btn_subtext2.text = paymentGuideConfig.option2Text2
            }
            activity?.let { activity ->
                view_payment_btn1.setOnClickListener {
                    view_payment_btn1.isSelected = true
                    view_payment_btn2.isSelected = false
                    iv_payment_checkbox1.isSelected = true
                    iv_payment_checkbox2.isSelected = false
                    tv_payment_btn_text1.isSelected = true
                    tv_payment_btn_text2.isSelected = false
                    if (!TextUtils.isEmpty(paymentGuideConfig.option1Text2)) {
                        tv_payment_btn_subtext1.visibility = View.VISIBLE
                    }
                    tv_payment_btn_subtext2.visibility = View.GONE
                }

                view_payment_btn2.setOnClickListener {
                    view_payment_btn1.isSelected = false
                    view_payment_btn2.isSelected = true
                    iv_payment_checkbox1.isSelected = false
                    iv_payment_checkbox2.isSelected = true
                    tv_payment_btn_text1.isSelected = false
                    tv_payment_btn_text2.isSelected = true
                    tv_payment_btn_subtext1.visibility = View.GONE
                    if (!TextUtils.isEmpty(paymentGuideConfig.option2Text2)) {
                        tv_payment_btn_subtext2.visibility = View.VISIBLE
                    }
                }

                view_payment_btn2.performClick()
                btn_payment_continue.shakeBtn()
                btn_payment_continue.setOnClickListener {
                    var sku = ""
                    if (view_payment_btn1.isSelected) {
                        sku = paymentGuideConfig.option1Sku
                    } else {
                        sku = paymentGuideConfig.option2Sku
                    }
                    BillingManager.startPay(activity, sku, entrance
                        ?: "", style, callBack = this, clickTimes = ++mClickTimes)
                }
            }
            iv_payment_close.alpha = paymentGuideConfig.closeButtonAlpha

            iv_payment_close.setOnClickListener {
                BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
                    BaseSeq103OperationStatistic.subs_close_a000, "0", entrance)
                val intent = Intent()
                intent.putExtra("status", "返回订阅状态")
                activity?.setResult(AppConstants.PAY_RESULT_CODE, intent)
                activity?.finish()
            }

            ThreadExecutorProxy.runOnMainThread(Runnable {
                if (iv_payment_close != null) {
                    iv_payment_close.visibility = View.VISIBLE
                }
            }, paymentGuideConfig.closeButtonDelay * 1000L)

            BaseSeq59OperationStatistic.uploadStatisticPayShow(
                GoCommonEnv.getApplication(),
                "${paymentGuideConfig.option1Sku}#${paymentGuideConfig.option2Sku}",
                entrance ?: ""
                , style
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

}
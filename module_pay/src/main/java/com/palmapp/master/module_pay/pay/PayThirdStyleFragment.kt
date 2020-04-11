package com.palmapp.master.module_pay.pay

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.*
import com.palmapp.master.baselib.GoCommonEnv
import com.palmapp.master.baselib.bean.pay.PaymentGuideConfig
import com.palmapp.master.baselib.constants.AppConstants
import com.palmapp.master.module_pay.R
import android.text.TextUtils
import com.palmapp.master.baselib.Product
import com.palmapp.master.baselib.manager.ThreadExecutorProxy
import com.palmapp.master.baselib.statistics.BaseSeq101OperationStatistic
import com.palmapp.master.baselib.statistics.BaseSeq103OperationStatistic
import com.palmapp.master.baselib.statistics.BaseSeq59OperationStatistic
import com.palmapp.master.module_pay.BillingManager
import kotlinx.android.synthetic.main.pay_fragment_third.*
import java.io.File


/**
 *
 * @author :     xiemingrui
 * @since :      2019/8/21
 */
class PayThirdStyleFragment : PayBaseStyleFragment() {
    private var style = "style3"

    override fun isNeedToBlockBack(): Boolean {
        return iv_payment_close.visibility != View.VISIBLE
    }

    override fun getStyle(): String {
        return "style3"
    }

    override fun getResLayout() = R.layout.pay_fragment_third

    override fun getDefaultConfig(): PaymentGuideConfig? {
        style = if (isLauncherScen()) "local_style3" else "internal_style3"
        return when (type) {
            "3" -> getPalmDefaultConfig()
            "6" -> getOldDefaultConfig()
            "9" -> getBabyDefaultConfig()
            "11" -> getPSDefaultConfig()
            "12" -> getHeartDefaultConfig()
            "17" -> getAnimalDefaultConfig()
            "13" -> getMangaDefaultConfig()
            else -> getTransformDefaultConfig()
        }
    }

    private fun getHeartDefaultConfig(): PaymentGuideConfig {
        val config = PaymentGuideConfig()
        config.title = GoCommonEnv.getApplication().getString(R.string.pay_default_third_hb_title)
        config.button1 =
            GoCommonEnv.getApplication().getString(R.string.pay_default_third_hb_btn)
        config.state = GoCommonEnv.getApplication().getString(R.string.pay_default_third_hb_state)
        config.mainText1 = GoCommonEnv.getApplication().getString(R.string.pay_default_third_hb_main_text)
        config.closeButtonDelay = 0
        config.closeButtonAlpha = 0.7f
        config.button1Sku = Product.DEFAULT_SKU_MONTH
        return config
    }

    private fun getPSDefaultConfig(): PaymentGuideConfig {
        val config = PaymentGuideConfig()
        config.title = GoCommonEnv.getApplication().getString(R.string.pay_default_third_ps_title)
        config.button1 =
            GoCommonEnv.getApplication().getString(R.string.pay_default_third_palm_btn1_title)
        config.state = GoCommonEnv.getApplication().getString(R.string.pay_default_third_ps_state)
        config.mainText1 = GoCommonEnv.getApplication().getString(R.string.pay_default_third_main_text)
        config.closeButtonDelay = 0
        config.closeButtonAlpha = 0.7f
        config.button1Sku = Product.DEFAULT_SKU_MONTH
        return config
    }

    private fun getPalmDefaultConfig(): PaymentGuideConfig {
        val config = PaymentGuideConfig()
        config.title = GoCommonEnv.getApplication().getString(R.string.pay_default_third_palm_title)
        config.button1 =
            GoCommonEnv.getApplication().getString(R.string.pay_default_third_palm_btn1_title)
        config.state = GoCommonEnv.getApplication().getString(R.string.pay_default_third_transform_state)
        config.mainText1 = GoCommonEnv.getApplication().getString(R.string.pay_default_third_main_text)
        config.closeButtonDelay = 0
        config.closeButtonAlpha = 0.7f
        config.button1Sku = Product.DEFAULT_SKU_MONTH
        return config
    }

    private fun getOldDefaultConfig(): PaymentGuideConfig {
        val config = PaymentGuideConfig()
        config.title = GoCommonEnv.getApplication().getString(R.string.pay_default_third_old_title)
        config.button1 =
            GoCommonEnv.getApplication().getString(R.string.pay_default_third_old_btn1_title)
        config.state = GoCommonEnv.getApplication().getString(R.string.pay_default_third_transform_state)
        config.mainText1 = GoCommonEnv.getApplication().getString(R.string.pay_default_third_main_text)
        config.closeButtonDelay = 0
        config.closeButtonAlpha = 0.7f
        config.button1Sku = Product.DEFAULT_SKU_MONTH
        return config
    }

    private fun getBabyDefaultConfig(): PaymentGuideConfig {
        val config = PaymentGuideConfig()
        config.title = GoCommonEnv.getApplication().getString(R.string.pay_default_third_baby_title)
        config.button1 =
            GoCommonEnv.getApplication().getString(R.string.pay_default_third_baby_btn1_title)
        config.state = GoCommonEnv.getApplication().getString(R.string.pay_default_third_transform_state)
        config.mainText1 = GoCommonEnv.getApplication().getString(R.string.pay_default_third_main_text)
        config.closeButtonDelay = 0
        config.closeButtonAlpha = 0.7f
        config.button1Sku = Product.DEFAULT_SKU_MONTH
        return config
    }

    private fun getTransformDefaultConfig(): PaymentGuideConfig {
        val config = PaymentGuideConfig()
        config.title = GoCommonEnv.getApplication().getString(R.string.pay_default_third_transform_title)
        config.button1 =
            GoCommonEnv.getApplication().getString(R.string.pay_default_third_transform_btn1_title)
        config.state = GoCommonEnv.getApplication().getString(R.string.pay_default_third_transform_state)
        config.mainText1 = GoCommonEnv.getApplication().getString(R.string.pay_default_third_main_text)
        config.closeButtonDelay = 0
        config.closeButtonAlpha = 0.7f
        config.button1Sku = Product.DEFAULT_SKU_MONTH
        return config
    }

    private fun getAnimalDefaultConfig(): PaymentGuideConfig {
        val config = PaymentGuideConfig()
        config.title = GoCommonEnv.getApplication().getString(R.string.pay_default_third_animal_title)
        config.button1 =
            GoCommonEnv.getApplication().getString(R.string.pay_default_third_animal_btn1_title)
        config.state = GoCommonEnv.getApplication().getString(R.string.pay_default_third_animal_state)
        config.mainText1 = GoCommonEnv.getApplication().getString(R.string.pay_default_third_main_text)
        config.closeButtonDelay = 0
        config.closeButtonAlpha = 0.7f
        config.button1Sku = Product.DEFAULT_SKU_MONTH
        return config
    }

    private fun getMangaDefaultConfig(): PaymentGuideConfig {
        val config = PaymentGuideConfig()
        config.title = GoCommonEnv.getApplication().getString(R.string.pay_default_third_manga_title)
        config.button1 =
            GoCommonEnv.getApplication().getString(R.string.pay_default_third_manga_btn1_title)
        config.state = GoCommonEnv.getApplication().getString(R.string.pay_default_third_manga_state)
        config.mainText1 = GoCommonEnv.getApplication().getString(R.string.pay_default_third_main_text)
        config.closeButtonDelay = 0
        config.closeButtonAlpha = 0.7f
        config.button1Sku = Product.DEFAULT_SKU_MONTH
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
            iv_payment_close.alpha = paymentGuideConfig.closeButtonAlpha
            ThreadExecutorProxy.runOnMainThread(Runnable {
                if (iv_payment_close != null) {
                    iv_payment_close.visibility = View.VISIBLE
                }
            }, paymentGuideConfig.closeButtonDelay * 1000L)

            tv_main_text.text = paymentGuideConfig.mainText1
            val path = if (TextUtils.equals(type, "3")) {
                GoCommonEnv.getApplication().cacheDir.path.plus(
                    File.separator
                ).plus("palmResult.png")
            } else if (TextUtils.equals(type, "6")) {
                GoCommonEnv.getApplication().cacheDir.path.plus(File.separator)
                    .plus("oldImage")
            } else if (TextUtils.equals(type, "11")) {
                GoCommonEnv.getApplication().cacheDir.path.plus(File.separator).plus("filter")
            } else {
                ""
            }
            if (!TextUtils.isEmpty(path)) {
                val options = BitmapFactory.Options()
                options.inPreferredConfig = Bitmap.Config.RGB_565
                val bitmap = BitmapFactory.decodeFile(path, options)

                iv_payment_src.setImageBitmap(bitmap)
            } else {
                bv_result.visibility = View.INVISIBLE
                iv_payment_src.setImageResource(
                    if (TextUtils.equals(type, "9")) {
                        R.mipmap.pay_baby_result_pic_baby_premium
                    } else if (TextUtils.equals(type, "10")) {
                        R.mipmap.pay_gender_result_pic_gender_premium
                    } else if (TextUtils.equals(type, "12")) {
                        R.mipmap.pay_heart_pic
                    } else if (TextUtils.equals(type, "17")) {
                        R.mipmap.face_animal_vip
                    } else {
                        R.mipmap.pay_manga_pic
                    }
                )
            }


            tv_payment_continue.text = paymentGuideConfig.button1
            activity?.let { activity ->
                btn_payment_continue.setOnClickListener {
                    val sku = paymentGuideConfig.button1Sku
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

            tv_payment_state.text = paymentGuideConfig.state

            ThreadExecutorProxy.runOnMainThread(Runnable {
                if (iv_payment_close != null) {
                    iv_payment_close.visibility = View.VISIBLE
                }
            }, paymentGuideConfig.closeButtonDelay * 1000L)

            BaseSeq59OperationStatistic.uploadStatisticPayShow(
                GoCommonEnv.getApplication(),
                paymentGuideConfig.button1Sku,
                entrance ?: "", style
            )
        }
    }

}
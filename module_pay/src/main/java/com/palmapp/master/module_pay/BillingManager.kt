package com.palmapp.master.module_pay

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.text.TextUtils
import android.view.View
import android.view.animation.LinearInterpolator
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.SkuDetailsResponseListener
import com.cs.bd.subscribe.StatusCode
import com.cs.bd.subscribe.SubscribeHelper
import com.cs.bd.subscribe.billing.Billing
import com.cs.bd.subscribe.billing.PurchasesResult
import com.cs.bd.subscribe.billing.SkuDetails
import com.palmapp.master.baselib.GoCommonEnv
import com.palmapp.master.baselib.bean.pay.ModuleConfig
import com.palmapp.master.baselib.constants.AppConstants
import com.palmapp.master.baselib.constants.PreConstants
import com.palmapp.master.baselib.event.SubscribeUpdateEvent
import com.palmapp.master.baselib.manager.GoPrefManager
import com.palmapp.master.baselib.proxy.OnBillingCallBack
import com.palmapp.master.baselib.statistics.BaseSeq103OperationStatistic
import com.palmapp.master.baselib.statistics.BaseSeq59OperationStatistic
import com.palmapp.master.baselib.utils.LogUtil
import com.palmapp.master.module_network.HttpClient
import com.palmapp.master.module_network.NetworkTransformer
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus
import java.lang.Exception
import java.util.HashMap
import com.appsflyer.AFInAppEventType
import com.appsflyer.AppsFlyerLib
import com.appsflyer.AFInAppEventParameterName
import com.palmapp.master.baselib.bean.FirstVipBean
import kotlinx.android.synthetic.main.pay_fragment_five.*


/**
 *  订阅管理类
 * @author :     xiemingrui
 * @since :      2019/8/19
 */
object BillingManager {
    const val TAG = "BillingManager"
    private var vip = ""

    fun queryVip() {
        vip = GoPrefManager.getDefault().getString(PreConstants.Pay.KEY_PAY_USER_TYPE, "")
        LogUtil.d(TAG, "开始查询订阅状态")
        try {
            SubscribeHelper.queryAllSubsPurchases(GoCommonEnv.getApplication()) {
                vip = if (it.purchasesList != null && !it.purchasesList.isEmpty()) {
                    val result = it.purchasesList.first().sku
                    GoPrefManager.getDefault().putString(PreConstants.Pay.KEY_PAY_USER_TYPE, result)
                        .apply()
                    result
                } else {
                    GoPrefManager.getDefault().putString(PreConstants.Pay.KEY_PAY_USER_TYPE, "")
                        .apply()
                    ""
                }
                LogUtil.d(TAG, "vip is ${isVip()}")
            }
        } catch (e: Exception) {

        }

    }

    //开始订阅
    fun startPay(
        activity: Activity,
        sku: String,
        entrance: String,
        style: String,
        callBack: OnBillingCallBack, clickTimes: Int
    ) {
        if (isVip()) {
            callBack.onPayError(BillingClient.BillingResponse.ITEM_ALREADY_OWNED)
            return
        }

        BaseSeq59OperationStatistic.uploadStatisticPayClick(
            activity,
            sku,
            entrance, style, "0", ""
        )
        var tab = GoPrefManager.getDefault().getInt(PreConstants.Launcher.KEY_BILLING_TIMES, -1)
        if (tab != -1 && tab < 4) {
            tab += 200
        } else {
            tab = -1
        }
        BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
            BaseSeq103OperationStatistic.vip_pay_but_a000,
            sku,
            entrance,
            if (tab == -1) "" else tab.toString(), "", ""
            , style
        )
        var mBilling: Billing? = null
        mBilling =
            SubscribeHelper.newBillingManager(activity, object : Billing.BillingUpdatesListener {
                override fun onConsumeFinished(token: String?, result: StatusCode?) {
                }

                override fun onQueryPurchasesFinished(purchasesResult: PurchasesResult?) {
                    if (purchasesResult?.isSuccess == false) {
                        callBack.onPayError(purchasesResult?.responseCode?.codeValue ?: -1)
                    }
                    LogUtil.d(
                        TAG,
                        "onQueryPurchasesFinished code ${purchasesResult?.responseCode?.codeValue} msg ${purchasesResult?.responseCode?.msg}"
                    )
                }

                override fun onBillingClientSetupFinished() {
                    LogUtil.d(TAG, "onBillingClientSetupFinished")
                    mBilling?.initiatePurchaseFlow(sku, Billing.SkuType.SUBS)
                }

                override fun onPurchasesUpdated(purchasesResult: PurchasesResult?) {
                    if (purchasesResult?.isSuccess == true) {

                        mBilling?.querySkuDetailsAsync(
                            BillingClient.SkuType.SUBS, listOf(sku)
                        ) { responseCode, skuDetailsList ->
                            if (skuDetailsList.isEmpty()) {
                                return@querySkuDetailsAsync
                            }
                            val price = skuDetailsList.first().priceAmountMicros
                            val code = skuDetailsList.first().priceCurrencyCode
                            val orderId = purchasesResult.purchasesList.first().orderId
                            LogUtil.d(
                                TAG,
                                "AF打点 REVENUE:${price} CONTENT_TYPE:sub CONTENT_ID:${orderId} CURRENCY:${code} "
                            )
                            val eventValue = HashMap<String, Any>()
                            eventValue[AFInAppEventParameterName.REVENUE] = price // 价格
                            eventValue[AFInAppEventParameterName.CONTENT_TYPE] =
                                "sub" // 订阅传 “sub”，内购传 “buy”
                            eventValue[AFInAppEventParameterName.CONTENT_ID] =
                                orderId // 订单号
                            eventValue[AFInAppEventParameterName.CURRENCY] = code // 货币单位
                            AppsFlyerLib.getInstance().trackEvent(
                                GoCommonEnv.getApplication(),
                                AFInAppEventType.PURCHASE,
                                eventValue
                            ) // AF打点上报
                        }
                        LogUtil.d(TAG, "购买成功：sku=$sku")
                        LogUtil.d(
                            TAG,
                            "onPurchasesUpdated code ${purchasesResult?.responseCode?.codeValue} msg ${purchasesResult?.responseCode?.msg}"
                        )
                        BaseSeq59OperationStatistic.uploadStatisticPaySuccess(
                            GoCommonEnv.getApplication(),
                            sku,
                            entrance, style
                        )
                        var tab = GoPrefManager.getDefault()
                            .getInt(PreConstants.Launcher.KEY_BILLING_TIMES, -1)
                        if (tab != -1 && tab < 4) {
                            tab += 200
                        } else {
                            tab = -1
                        }
                        try {
                            BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
                                BaseSeq103OperationStatistic.vip_pay_succ,
                                sku,
                                entrance,
                                tab.toString(),
                                purchasesResult.purchasesList.get(0).orderId
                                , clickTimes.toString()
                                , style
                            )

                            BaseSeq59OperationStatistic.uploadStatisticPayClick(
                                activity,
                                sku,
                                entrance, style, "1", purchasesResult.purchasesList.get(0).orderId
                            )
                        } catch (e: Exception) {

                        }
                        vip = sku
                        GoPrefManager.getDefault()
                            .putString(PreConstants.Pay.KEY_PAY_USER_TYPE, sku).apply()
                        EventBus.getDefault().post(SubscribeUpdateEvent(true))
                        callBack.onPaySuccess(sku)
                    } else {
                        EventBus.getDefault().post(SubscribeUpdateEvent(false))
                        callBack.onPayError(purchasesResult?.responseCode?.codeValue ?: -1)
                    }
                }
            })

    }

    //是否是订阅用户
    fun isVip(): Boolean {
        if (TextUtils.isEmpty(vip))
            return false
        return true
    }

}

//抖动动画
internal fun View.shakeBtn() {
    this.post {
        val initX = this.x
        val initDX =
            initX - GoCommonEnv.getApplication().resources.getDimension(R.dimen.change_24px)
        val initPX =
            initX + GoCommonEnv.getApplication().resources.getDimension(R.dimen.change_24px)
        val animatorSet = AnimatorSet()
        val o1 =
            ObjectAnimator.ofFloat(this, "x", initX, initDX, initPX, initDX, initPX, initX)
        o1.duration = 640
        animatorSet.play(o1)
        animatorSet.play(o1).after(500)
        animatorSet.interpolator = LinearInterpolator()
        animatorSet.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                animatorSet.start()
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
            }
        })
        animatorSet
            .start()
    }
}
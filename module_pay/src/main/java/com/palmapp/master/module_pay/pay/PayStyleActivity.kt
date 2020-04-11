package com.palmapp.master.module_pay.pay

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.facade.callback.NavigationCallback
import com.alibaba.android.arouter.launcher.ARouter
import com.palmapp.master.baselib.BaseMVPActivity
import com.palmapp.master.baselib.GoCommonEnv
import com.palmapp.master.baselib.constants.AppConstants
import com.palmapp.master.baselib.constants.PreConstants
import com.palmapp.master.baselib.constants.RouterConstants
import com.palmapp.master.baselib.event.SubscribeUpdateEvent
import com.palmapp.master.baselib.manager.AbTestUserManager
import com.palmapp.master.baselib.manager.GoPrefManager
import com.palmapp.master.baselib.proxy.AdSdkServiceProxy
import com.palmapp.master.baselib.proxy.OnLoadAdRewardListener
import com.palmapp.master.baselib.statistics.BaseSeq101OperationStatistic
import com.palmapp.master.baselib.statistics.BaseSeq103OperationStatistic
import com.palmapp.master.baselib.statistics.BaseSeq103OperationStatistic.vip_gui_page_f000
import com.palmapp.master.baselib.view.AdsWatchDialog
import com.palmapp.master.baselib.view.AdsWatchLeftDialog
import com.palmapp.master.baselib.view.LoadingDialog
import com.palmapp.master.module_ad.manager.RewardAdManager
import com.palmapp.master.module_pay.BillingManager
import com.palmapp.master.module_pay.R
import org.greenrobot.eventbus.EventBus

/**
 *
 * @author :     xiemingrui
 * @since :      2019/8/21
 */
@Route(path = RouterConstants.ACTIVITY_PAY)
class PayStyleActivity : BaseMVPActivity<PayStyleView, PayStylePresenter>(), PayStyleView,
    OnLoadAdRewardListener {

    private var time: Long = 0L
    private var adTimes = RewardAdManager.getConfig()?.show_count ?: 0
    private var dialog: Dialog? = null
    private var loadingDialog: LoadingDialog? = null
    private var isNeedToShow = false
    private var isFirstShow = true

    override fun onVideoFinish() {
        dialog?.dismiss()
        adTimes--
        if (adTimes > 0) {
            showAdWatchDialog()
        } else {
            EventBus.getDefault().post(SubscribeUpdateEvent(true))
            realFinish()
        }
    }

    override fun onLoading() {
        loadingDialog?.show()
    }

    override fun onLoadSuccess() {
        loadingDialog?.dismiss()
        if (isNeedToShow)
            RewardAdManager.showAd()
    }

    override fun onLoadFail() {
        loadingDialog?.dismiss()
    }

    private fun onClickWatchAd() {
        adTimes = RewardAdManager.getConfig()?.show_count ?: 0
        isFirstShow = true
        isNeedToShow = false
        showAdWatchDialog()
    }

    private fun showAdWatchDialog() {
        if (adTimes > 0) {
            dialog = getWatchDialog()
            dialog?.show()
            isFirstShow = false
        }

    }

    private fun getWatchDialog(): Dialog? {
        if (isFirstShow) {
            return AdsWatchDialog(this, adTimes, View.OnClickListener {
                isNeedToShow = true
                RewardAdManager.loadAd(this, this)
            })
        } else {
            return AdsWatchLeftDialog(this, adTimes, View.OnClickListener {
                isNeedToShow = true
                RewardAdManager.loadAd(this, this)
            })
        }
    }

    override fun createPresenter(): PayStylePresenter {
        return PayStylePresenter(entrance)
    }

    //"1.启动页首次展示
//2.启动页非首次展示
//3.手相
//4.星座
//5.塔罗牌
//6.变老
//7.个人页"
    @JvmField
    @Autowired(name = "entrance")
    var entrance = ""

    @JvmField
    @Autowired(name = "type")
    var type: String = ""

    @JvmField
    @Autowired(name = "watchAd")
    var watchAd = false

    @JvmField
    @Autowired(name = "newPlan")
    var newPlan = ""

    override fun onDestroy() {
        super.onDestroy()
        val result = (System.currentTimeMillis() - time) / 1000
        BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(BaseSeq103OperationStatistic.subs_page_time, result.toString(), entrance, "")
    }

    private var fragment: PayBaseStyleFragment? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        GoCommonEnv.storeFirstLauncher(false)
        time = System.currentTimeMillis()
        ARouter.getInstance().inject(this)
        entrance = if (TextUtils.isEmpty(entrance)) "0" else entrance
        type = if (TextUtils.isEmpty(type)) entrance else type
        super.onCreate(savedInstanceState)
        GoCommonEnv.resumeBGM()
        setContentView(R.layout.pay_activity_payment)
        var tab = GoPrefManager.getDefault().getInt(PreConstants.Launcher.KEY_BILLING_TIMES, -1)
        if (tab != -1 && tab < 4) {
            tab += 200
        } else {
            tab = -1
        }
        if (entrance == "1" || entrance == "14") {
            tab = -1
        }
        BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
            vip_gui_page_f000,
            "",
            entrance,
            if (tab == -1) "" else tab.toString(), "", ""
        )
        mPresenter?.loadStyleFragment()
    }

    override fun showFragment(f: PayBaseStyleFragment) {
        fragment = f
        val supportFragmentManager = supportFragmentManager
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        val bundle = Bundle()
        bundle.putString("entrance", entrance)
        bundle.putString("type", type)
        f.arguments = bundle
        fragmentTransaction.add(R.id.fl_content, f)
        fragmentTransaction.commit()
    }

    override fun onBackPressed() {
        if (fragment?.isNeedToBlockBack() == true) {
            return
        }
        BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
            BaseSeq103OperationStatistic.subs_close_a000,
            "1",
            entrance
        )
        finish()
    }

    override fun finish() {
        if (entrance == "1" || entrance == "2" || entrance == "14" || entrance == "16") {
            if (!BillingManager.isVip()) {
                val dialog = PaySixStyleDialog(this, newPlan == "1")
                dialog.show()
                dialog.setOnDismissListener {
                    realFinish()
                }
                return
            }
        }
        if (!RewardAdManager.isFreeUse() && RewardAdManager.isLoadAd() && (fragment is PayThirdStyleFragment || watchAd) && GoPrefManager.getDefault().isFirstTime(
                PreConstants.First.KEY_FIRST_SHOW_WATCH_AD
            ) && adTimes > 0
        ) {
            loadingDialog = LoadingDialog(this)
            onClickWatchAd()
            return
        }
        realFinish()
    }

    fun realFinish() {
        val intent = Intent()
        intent.putExtra("status", "返回订阅状态")
        setResult(AppConstants.PAY_RESULT_CODE, intent)
        if (newPlan == "0") {
            ARouter.getInstance().build(RouterConstants.ACTIVITY_APP_MAIN).navigation(this)
            super.finish()
        } else {
            super.finish()
        }
        GoCommonEnv.stopBGM()
    }

}
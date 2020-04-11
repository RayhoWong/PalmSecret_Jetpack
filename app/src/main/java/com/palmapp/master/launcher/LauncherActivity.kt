package com.palmapp.master.launcher;

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import com.alibaba.android.arouter.launcher.ARouter
import com.palmapp.master.R
import com.palmapp.master.baselib.BaseMVPActivity
import com.palmapp.master.baselib.GoCommonEnv
import com.palmapp.master.baselib.buychannel.BuyChannelProxy
import com.palmapp.master.baselib.constants.PreConstants
import com.palmapp.master.baselib.constants.RouterConstants
import com.palmapp.master.baselib.manager.ForegroundManager
import com.palmapp.master.baselib.manager.GoPrefManager
import com.palmapp.master.baselib.manager.PayGuideManager
import com.palmapp.master.baselib.manager.ThreadExecutorProxy
import com.palmapp.master.baselib.proxy.BillingServiceProxy
import com.palmapp.master.baselib.statistics.BaseSeq103OperationStatistic
import com.palmapp.master.baselib.utils.LogUtil
import com.palmapp.master.main.MainActivity
import com.palmapp.master.module_ad.manager.BootIntersetAdManager
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.app_activity_launcher.*
import java.util.concurrent.TimeUnit


class LauncherActivity : BaseMVPActivity<LauncherView, LauncherPresenter>(), LauncherView {

    companion object {
        var CODE_LAUNCHER = 2200L
    }

    override fun getContext(): Context? {
        return this
    }

    override fun createPresenter(): LauncherPresenter {
        return LauncherPresenter()
    }

    private fun goPaymentNewGuide() {
        val campaign = BuyChannelProxy.getBuyChannelBean().campaign.replace("palmsecret", "").toLowerCase()
        when {
            campaign.contains("old") -> {
                ARouter.getInstance().build(RouterConstants.ACTVITIY_TakephotoActivity).withString("newPlan", "1").navigation(this@LauncherActivity)
            }
            campaign.contains("baby") -> {
                ARouter.getInstance().build(RouterConstants.ACTVITIY_FACE_BABY_MATCH).withString("newPlan", "1").navigation(this@LauncherActivity)
            }
            campaign.contains("heart") || campaign.contains("heartrate") -> {
                ARouter.getInstance().build(RouterConstants.ACTIVITY_PSY_HEARTRATE_DETECT).withString("newPlan", "1").navigation(this@LauncherActivity)
            }
            campaign.contains("art") || campaign.contains("cartoon") -> {
                ARouter.getInstance().build(RouterConstants.ACTIVITY_ALBUM_TAKE_PHOTO).withString("newPlan", "1").navigation(this@LauncherActivity)
            }
            campaign.contains("art") -> {
                ARouter.getInstance().build(RouterConstants.ACTIVITY_CARTOON_TAKE_PHOTO).withString("newPlan", "1").navigation(this@LauncherActivity)
            }
            campaign.contains("animal") -> {
                ARouter.getInstance().build(RouterConstants.ACTIVITY_ANIMAL_TAKE_PHOTO).withString("newPlan", "1").navigation(this@LauncherActivity)
            }
            else -> {
                ARouter.getInstance().build(RouterConstants.ACTIVITY_PALM_SCAN).withString("newPlan", "1").navigation(this@LauncherActivity)
            }
        }
        CODE_LAUNCHER = 1500
        finish()
    }

    private fun goMainActivity() {
        CODE_LAUNCHER = 1500
        BootIntersetAdManager.showAd()
        startActivity(Intent(this@LauncherActivity, MainActivity::class.java))
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        video_bg.mediaPlayer.setOnCompletionListener(null)
        video_bg.release()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setBackgroundDrawable(null)
        BillingServiceProxy.queryVip()
        setContentView(R.layout.app_activity_launcher)
        GoCommonEnv.startBGM()
        BootIntersetAdManager.loadAd(this)
        video_bg.setOnInitCompleteListener {
            ThreadExecutorProxy.runOnMainThread(Runnable {

                val fd_bg = resources.openRawResourceFd(R.raw.app_launcher_bg)
                video_bg.mediaPlayer.setDataSource(fd_bg.fileDescriptor, fd_bg.startOffset, fd_bg.length)
                video_bg.mediaPlayer.prepare()
                video_bg.mediaPlayer.start()
                video_bg.mediaPlayer.setOnCompletionListener {
                    video_bg.mediaPlayer.reset()
                    val fd_logo = resources.openRawResourceFd(R.raw.app_launcher_logo)
                    video_bg.mediaPlayer.setDataSource(fd_logo.fileDescriptor, fd_logo.startOffset, fd_logo.length)
                    video_bg.mediaPlayer.prepare()
                    video_bg.mediaPlayer.start()
                        it.start()
                        it.isLooping = true
                        Observable.interval(CODE_LAUNCHER, 500, TimeUnit.MILLISECONDS)
                            .subscribeOn(Schedulers.io())
                            .observeOn(Schedulers.io())
                            .takeUntil {
                                CODE_LAUNCHER == 1500L || it == 14L || (PayGuideManager.hasPayGuideConfig && PayGuideManager.paymentGuideConfigs.isNotEmpty())
                            }.subscribe(object : Observer<Long> {
                                override fun onComplete() {
                                    if (!isFinishing) {
                                        ForegroundManager.runOnForeground {
                                            if (BillingServiceProxy.isVip()) {
                                                goMainActivity()
                                                return@runOnForeground
                                            }
                                            if (!GoCommonEnv.isFirstRunPayGuide) {
                                                if (GoPrefManager.getDefault().getInt(
                                                        PreConstants.Launcher.KEY_BILLING_TIMES,
                                                        1
                                                    ) <= 3
                                                ) {
                                                    val config = PayGuideManager.payGuideConfig.get(PayGuideManager.NOT_FIRST_LAUNCHER)!!
                                                    GoPrefManager.getDefault()
                                                        .addTimes(PreConstants.Launcher.KEY_BILLING_TIMES, 1)
                                                    if (config.plan == "0") {
                                                        ARouter.getInstance().build(RouterConstants.ACTIVITY_PAY)
                                                            .withString(
                                                                "entrance",
                                                                config.scen
                                                            ).withString("newPlan", "0").navigation(this@LauncherActivity)
                                                        CODE_LAUNCHER = 1500
                                                        finish()
                                                    } else {
                                                        goPaymentNewGuide()
                                                    }
                                                } else {
                                                    goMainActivity()
                                                }
                                            } else {
                                                val config = PayGuideManager.payGuideConfig.get(PayGuideManager.FIRST_LAUNCHER)!!
                                                if (config.plan == "0") {
                                                    //首次
                                                    ARouter.getInstance().build(RouterConstants.ACTIVITY_PAY)
                                                        .withString("entrance", config.scen)
                                                        .withString("newPlan", "0")
                                                        .navigation(this@LauncherActivity)
                                                    CODE_LAUNCHER = 1500
                                                    finish()
                                                } else {
                                                    goPaymentNewGuide()
                                                }

                                            }
                                        }
                                    }
                                }

                                override fun onSubscribe(d: Disposable) {
                                }

                                override fun onNext(t: Long) {
                                    LogUtil.d("LauncherActivity", "等待时长：${t}")
                                }

                                override fun onError(e: Throwable) {
                                }
                            })

                    }

                GoCommonEnv.storeFirstLauncher(
                    GoPrefManager.getDefault().getBoolean(
                        PreConstants.Launcher.KEY_LAUNCHER_FIRST_TIME,
                        true
                    )
                )
                GoPrefManager.getDefault().putBoolean(PreConstants.Launcher.KEY_LAUNCHER_FIRST_TIME, false)
                    .apply()
                BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(BaseSeq103OperationStatistic.p001)
            })
        }
    }
}
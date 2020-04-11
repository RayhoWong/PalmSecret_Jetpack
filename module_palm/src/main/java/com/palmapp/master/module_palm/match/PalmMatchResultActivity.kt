package com.palmapp.master.module_palm.match;

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.TextView
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.cs.bd.commerce.util.LogUtils
import com.palmapp.master.baselib.BaseMVPActivity
import com.palmapp.master.baselib.GoCommonEnv
import com.palmapp.master.baselib.bean.palm.PalmMatchResponse
import com.palmapp.master.baselib.constants.PreConstants
import com.palmapp.master.baselib.constants.RouterConstants
import com.palmapp.master.baselib.event.SubscribeUpdateEvent
import com.palmapp.master.baselib.manager.GoPrefManager
import com.palmapp.master.baselib.manager.ThreadExecutorProxy
import com.palmapp.master.baselib.manager.config.ConfigManager
import com.palmapp.master.baselib.manager.config.StarGuideConfig
import com.palmapp.master.baselib.proxy.BillingServiceProxy
import com.palmapp.master.baselib.proxy.OnLoadAdRewardListener
import com.palmapp.master.baselib.statistics.BaseSeq101OperationStatistic
import com.palmapp.master.baselib.statistics.BaseSeq103OperationStatistic
import com.palmapp.master.baselib.utils.TimeUtils
import com.palmapp.master.baselib.view.AdsWatchDialog
import com.palmapp.master.baselib.view.AdsWatchLeftDialog
import com.palmapp.master.baselib.view.LikeDialog
import com.palmapp.master.baselib.view.LoadingDialog
import com.palmapp.master.module_ad.manager.ExitAdManager
import com.palmapp.master.module_ad.manager.RewardAdManager
import com.palmapp.master.module_palm.R
import kotlinx.android.synthetic.main.palm_activity_match_result.*
import kotlinx.android.synthetic.main.palm_activity_palmprint_judg_result.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.io.File
import java.lang.Exception

class PalmMatchResultActivity : BaseMVPActivity<PalmMatchResultView, PalmMatchResultPresenter>(),
    PalmMatchResultView,
    OnLoadAdRewardListener {
    private var adTimes = RewardAdManager.getConfig()?.show_count ?: 0
    private var dialog: Dialog? = null
    private var loadingDialog: LoadingDialog? = null
    private var isNeedToShow = false

    private var isFirstShow = true

    private var isVip = BillingServiceProxy.isVip()

    private var isUnlock = false

    override fun onVideoFinish() {
        dialog?.dismiss()
        adTimes--
        if (adTimes > 0) {
            showAdWatchDialog()
        } else {
            //已订阅
            isVip = true
            isUnlock = true
            palm_blur.visibility = View.GONE
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
            isNeedToShow = true
            RewardAdManager.loadAd(this, this)
            return null
        } else {
            return AdsWatchLeftDialog(this, adTimes, View.OnClickListener {
                isNeedToShow = true
                RewardAdManager.loadAd(this, this)
            })
        }
    }

    var response: PalmMatchResponse? = null
    private val path1 =
        GoCommonEnv.getApplication().cacheDir.path.plus(File.separator).plus("palm1")
    private val path2 =
        GoCommonEnv.getApplication().cacheDir.path.plus(File.separator).plus("palm2")

    override fun createPresenter(): PalmMatchResultPresenter {
        return PalmMatchResultPresenter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.palm_activity_match_result)
        RewardAdManager.addFreeUse()
        loadingDialog = LoadingDialog(this)
        palm_blur.setWatchClickListener(View.OnClickListener {
            onClickWatchAd()
        })
        if (!BillingServiceProxy.isVip() && !RewardAdManager.isFreeUse()) {
            ExitAdManager.loadAd(this)
            ARouter.getInstance().build(RouterConstants.ACTIVITY_PAY).withString("entrance", "0")
                .withBoolean("watchAd", true).navigation()
            RewardAdManager.loadAd(this, this)
        }
        palm_blur.entrance = "0"
        BaseSeq101OperationStatistic.uploadOperationStatisticData(
            BaseSeq101OperationStatistic.palm_match_a000,
            "",
            "4"
        )
        EventBus.getDefault().register(this)
        response = intent.getSerializableExtra("result") as PalmMatchResponse
        findViewById<TextView>(R.id.tv_titlebar_title).text =
            getString(R.string.palm_palmmatch_title)
        findViewById<View>(R.id.iv_titlebar_back).setOnClickListener {
            if (isVip) {//订阅
                showScoreGuide()
            } else {
                ExitAdManager.showAd()
                finish()
            }
        }
        response?.let {
            val options = RequestOptions()
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
            Glide.with(this).load(File(path1)).apply(options).into(iv_palmresult_lefthand)
            Glide.with(this).load(File(path2)).apply(options).into(iv_palmresult_righthand)
            val anim = ValueAnimator.ofInt(0, it.score)
            anim.addUpdateListener {
                val v = it.animatedValue as Int
                tv_palmresult_score.text = String.format("%d%%", v)
            }
            anim.duration = 1000
            tv_palmresult_age.text = it.marry_age
            val baby = getString(R.string.palm_palmmatch_baby, it.children_count)
            val sp = SpannableString(baby)
            sp.setSpan(
                ForegroundColorSpan(Color.parseColor("#ffbb59")),
                baby.indexOf(it.children_count),
                baby.indexOf(it.children_count) + it.children_count.length,
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE
            )
            tv_palmresult_baby.text = sp
            val sb = StringBuffer()
            it.content.article.forEach {
                sb.append(it.text)
                sb.append("\n")
            }
            tv_palmresult_full.text = sb.toString()
            progress_palm1.startAnim(it.intimacy)
            progress_palm2.startAnim(it.suitable)
            anim.start()
        }
        palm_blur.visibility = if (BillingServiceProxy.isVip() || RewardAdManager.isFreeUse()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe
    fun onSubscribeUpdateEvent(event: SubscribeUpdateEvent) {
        if (event.isVip) {
            isVip = true
            isUnlock = true
        }
        palm_blur.visibility = if (event.isVip) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    //好评引导
    private fun showScoreGuide() {
        var isOpen = ConfigManager.getConfig(StarGuideConfig::class.java)?.isOpenGuide
        var isResultOpen = ConfigManager.getConfig(StarGuideConfig::class.java)?.isResultGuide
        isOpen?.let {
            if (!it) { //好评总开关是关闭的
                showAdWithoutGuide()
                finish()
                return
            }
        }
        isResultOpen?.let {
            if (!it) { //好评结果页开关是关闭的
                showAdWithoutGuide()
                finish()
                return
            }
        }
        //好评引导出现的次数
        var guideShowedCount =
            GoPrefManager.getDefault().getInt(PreConstants.ScoreGuide.KEY_GUIDE_TOTAL_COUNT, 0)
        if (guideShowedCount == 3) {
            showAdWithoutGuide()
            finish()
            return
        }

        var isFirstShowed =
            GoPrefManager.getDefault().getBoolean(PreConstants.ScoreGuide.KEY_IS_FIRST_SHOW, false)
        if (!isFirstShowed) {
            ThreadExecutorProxy.runOnMainThread(Runnable {
                BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
                    BaseSeq103OperationStatistic.rating_f000,
                    "",
                    "7",
                    ""
                )
                var dialog = LikeDialog(this, "palm_match")
                dialog.show()
                GoPrefManager.getDefault().putString(
                    PreConstants.ScoreGuide.KEY_CACHE_NOW_DATE,
                    TimeUtils.getStringDate()
                ).commit()

                GoPrefManager.getDefault().putBoolean(
                    PreConstants.ScoreGuide.KEY_IS_FIRST_SHOW,
                    true
                ).commit()

                guideShowedCount++
                GoPrefManager.getDefault().putInt(
                    PreConstants.ScoreGuide.KEY_GUIDE_TOTAL_COUNT,
                    guideShowedCount
                ).commit()
            })
            return
        }

        var isScored =
            GoPrefManager.getDefault().getBoolean(PreConstants.ScoreGuide.KEY_IS_SCORED, false)
        var cacheDate =
            GoPrefManager.getDefault().getString(PreConstants.ScoreGuide.KEY_CACHE_NOW_DATE, "")
        try {
            if (!cacheDate.isNullOrEmpty()) {
                var nowDate = TimeUtils.getStringDate()
                var mins = TimeUtils.getDistanceMin(nowDate, cacheDate)
                if (!isScored) {  //没有评论过
                    if (mins > 48 * 60) { //超过48h未出现过
                        ThreadExecutorProxy.runOnMainThread(Runnable {
                            BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
                                BaseSeq103OperationStatistic.rating_f000,
                                "",
                                "3",
                                ""
                            )
                            var dialog = LikeDialog(this, "palm_match")
                            dialog.show()
                            GoPrefManager.getDefault().putString(
                                PreConstants.ScoreGuide.KEY_CACHE_NOW_DATE,
                                TimeUtils.getStringDate()
                            ).commit()

                            guideShowedCount++
                            GoPrefManager.getDefault().putInt(
                                PreConstants.ScoreGuide.KEY_GUIDE_TOTAL_COUNT,
                                guideShowedCount
                            ).commit()
                        })

                    } else {
                        showAdWithoutGuide()
                        finish()
                    }
                } else {
                    showAdWithoutGuide()
                    finish()
                }
            }
        } catch (e: Exception) {
            LogUtils.e(e.printStackTrace().toString())
        }
    }


    private fun showAdWithoutGuide() {
        if (isUnlock) {
            //观看视频解锁 弹广告
            ExitAdManager.showAd()
        }
    }


    override fun onBackPressed() {
        if (isVip) {//订阅
            showScoreGuide()
        } else {
            ExitAdManager.showAd()
            super.onBackPressed()
        }
    }
}
package com.palmapp.master.module_cnt.matching;

import android.animation.*
import android.app.Dialog
import android.graphics.Typeface
import android.graphics.drawable.AnimationDrawable
import com.palmapp.master.baselib.BaseMVPActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.TextAppearanceSpan
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.widget.ImageView
import android.widget.TextView
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.cs.bd.commerce.util.LogUtils
import com.palmapp.master.baselib.GoCommonEnv
import com.palmapp.master.baselib.bean.cnt.MatchingResponse
import com.palmapp.master.baselib.bean.user.getCntCover
import com.palmapp.master.baselib.bean.user.getCntTranCover
import com.palmapp.master.baselib.bean.user.getConstellationById
import com.palmapp.master.baselib.constants.PreConstants
import com.palmapp.master.baselib.constants.RouterConstants
import com.palmapp.master.baselib.event.SubscribeUpdateEvent
import com.palmapp.master.baselib.manager.GoPrefManager
import com.palmapp.master.baselib.manager.ThreadExecutorProxy
import com.palmapp.master.baselib.manager.config.ConfigManager
import com.palmapp.master.baselib.manager.config.StarGuideConfig
import com.palmapp.master.baselib.proxy.AdSdkServiceProxy
import com.palmapp.master.baselib.proxy.BillingServiceProxy
import com.palmapp.master.baselib.proxy.OnLoadAdRewardListener
import com.palmapp.master.baselib.statistics.BaseSeq103OperationStatistic
import com.palmapp.master.baselib.utils.AppUtil
import com.palmapp.master.baselib.utils.TimeUtils
import com.palmapp.master.baselib.view.*
import com.palmapp.master.module_ad.manager.ExitAdManager
import com.palmapp.master.module_ad.manager.RewardAdManager
import com.palmapp.master.module_cnt.R
import kotlinx.android.synthetic.main.cnt_activity_daily.*
import kotlinx.android.synthetic.main.cnt_activity_matching.*
import kotlinx.android.synthetic.main.cnt_activity_matching.bv_result
import kotlinx.android.synthetic.main.cnt_activity_matching.network
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.lang.Exception
import kotlin.collections.ArrayList

@Route(path = RouterConstants.ACTIVITY_CNT_MATCHING)
class CntMatchActivity : BaseMVPActivity<CntMatchView, CntMatchPresenter>(), CntMatchView,
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
            bv_result.visibility = View.GONE
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
    override fun showLoading() {
        network.showNetworkLoadingView()
    }

    override fun showNetworkError() {
        network.showNetworkErrorView { mPresenter?.loadData(cnt_id1, cnt_id2) }
    }

    override fun showServerError() {
        network.showServerErrorView { mPresenter?.loadData(cnt_id1, cnt_id2) }
    }

    private var ivShare: ImageView? = null
    private var titleBar: View? = null
    private var isNeedBGM = true

    override fun onPause() {
        super.onPause()
        GoCommonEnv.stopBGM()
    }

    private fun doAnim(percent: Int) {
        var animatorSet = AnimatorSet()
        val o3 =
            ObjectAnimator.ofPropertyValuesHolder(
                tv_cntmatching_score,
                PropertyValuesHolder.ofFloat("alpha", 0f, 1f)
            )
                .setDuration(250L)
        val v0 = ValueAnimator.ofFloat(0f, 1f).setDuration(250L)
        v0.addUpdateListener {
            val alpha = it.animatedValue as Float
            iv1.alpha = alpha
            iv2.alpha = alpha
        }
        val v1 = ValueAnimator.ofInt(0, percent).setDuration(400L)
        v1.addUpdateListener {
            tv_cntmatching_score.text = "${it.animatedValue as Int}%"
        }

        val v2 = ValueAnimator.ofFloat(0f, 1f).setDuration(250)
        v2.addUpdateListener {
            tv_cntmatching_me.alpha = it.animatedValue as Float
            tv_cntmatching_other.alpha = it.animatedValue as Float
            ivShare?.alpha = it.animatedValue as Float
        }

        val v3 = ValueAnimator.ofFloat(1f, 1.4f, 1f).setDuration(500L)
        v3.addUpdateListener {
            tv_cntmatching_score.scaleX = it.animatedValue as Float
            tv_cntmatching_score.scaleY = it.animatedValue as Float
        }

        val t1 = ObjectAnimator.ofPropertyValuesHolder(
            iv1,
            PropertyValuesHolder.ofFloat("x", iv1.x, iv2.x)
        )
            .setDuration(600L)
        val t2 = ObjectAnimator.ofPropertyValuesHolder(
            iv2,
            PropertyValuesHolder.ofFloat("x", iv2.x, iv1.x)
        )
            .setDuration(600L)
        val t3 = ObjectAnimator.ofPropertyValuesHolder(
            layout_cntmatching_header,
            PropertyValuesHolder.ofFloat("y", layout_cntmatching_header.y, 0f)
        ).setDuration(400L)

        val t4 = ObjectAnimator.ofPropertyValuesHolder(
            layout_cntmatching_content,
            PropertyValuesHolder.ofFloat(
                "y",
                layout_cntmatching_content.y,
                layout_cntmatching_header.height.toFloat()
            )
        ).setDuration(300L)
        t4.interpolator = AccelerateInterpolator()
        animatorSet.play(v0).before(t1)
        animatorSet.play(t1).with(t2).after(500)
        animatorSet.play(t2).before(o3)
        animatorSet.play(o3).before(v1)
        animatorSet.play(v1).before(v3)
        animatorSet.play(v3).before(t3)
        animatorSet.play(t3).before(v2)
        animatorSet.play(v2).before(t4)
        animatorSet.start()
        animatorSet.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                isNeedBGM = false
                GoCommonEnv.stopBGM()
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
            }
        })
        network.hideAllView()
    }

    override fun showResult(response: MatchingResponse) {
        if (!BillingServiceProxy.isVip() && !RewardAdManager.isFreeUse()) {
            bv_result.visibility = View.VISIBLE
            ivShare?.visibility = View.GONE
        }
        ivShare?.setOnClickListener {
            if (!BillingServiceProxy.isVip()) {
                ARouter.getInstance().build(RouterConstants.ACTIVITY_PAY)
                    .withString("entrance", "0").navigation()
                return@setOnClickListener
            }
            mPresenter?.share(cnt_id1, cnt_id2, response.percent)
        }
        iv2.setImageResource(getCntCover(cnt_id2))
        iv1.setImageResource(getCntCover(cnt_id1))
        tv_cntmatching_me.text = getConstellationById(cnt_id1)
        tv_cntmatching_other.text = getConstellationById(cnt_id2)
        tv_cntmatching_title.text =
            getString(R.string.palm_match_best, getConstellationById(cnt_id1))
        tv_cntmatching_title2.text =
            getString(R.string.palm_match_best, getConstellationById(cnt_id2))
        tv_cntmatching_result.text = getConstellationById(response.best_match1_list[0])
        tv_cntmatching_result2.text = getConstellationById(response.best_match2_list[0])
        iv_cntmatching_result.setImageResource(getCntTranCover(response.best_match1_list[0]))
        iv_cntmatching_result2.setImageResource(getCntTranCover(response.best_match2_list[0]))
        val sb = StringBuffer()
        val index = ArrayList<IntArray>(3)
        try {
            response.content.article.forEach {
                sb.append(it.text).append("\n")
                if (it.type == 1) {
                    val i = intArrayOf(sb.lastIndexOf(it.text), it.text.length)
                    index.add(i)
                }
            }
            sb.removeRange(sb.length - 3, sb.length - 1)
            val sp = SpannableString(sb.toString())
            index.forEach {
                sp.setSpan(
                    TextAppearanceSpan(this, R.style.cnt_style_match_title), it[0], it[0] + it[1],
                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE
                )
            }
            tv_cntmatching_content.text = sp
        } catch (e: Exception) {
        }
        sv_cntmatching.post {
            layout_cntmatching_content.y = sv_cntmatching.bottom.toFloat()
            layout_cntmatching_header.y = (AppUtil.getScreenH(this) - (titleBar?.height ?: 0)) / 3f
            doAnim(response.percent)
            ThreadExecutorProxy.runOnMainThread(Runnable {
                val anims = IntArray(32)
                for (i in 64..95) {
                    // 获取Drawable文件夹下的图片文件
                    val id = resources.getIdentifier("cnt_a_000$i", "mipmap", packageName)
                    anims[i - 64] = id
                }

                val anim = AnimationsContainer.getInstance().createAnim(iv_cntmatching, 20, anims)
                anim.start()
            }, 850)
        }
    }

    @JvmField
    @Autowired(name = "pos1")
    var cnt_id1 = 0

    @JvmField
    @Autowired(name = "pos2")
    var cnt_id2 = 0

    override fun createPresenter(): CntMatchPresenter {
        return CntMatchPresenter()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSubscribeUpdateEvent(event: SubscribeUpdateEvent) {
        if (event.isVip) {
            isVip = true
            isUnlock = true
            bv_result.visibility = View.GONE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        RewardAdManager.addFreeUse()
        ARouter.getInstance().inject(this)
        if (!BillingServiceProxy.isVip() && !RewardAdManager.isFreeUse()) {
            ExitAdManager.loadAd(this)
            ARouter.getInstance().build(RouterConstants.ACTIVITY_PAY).withString("entrance", "0").withBoolean("watchAd",true)
                .navigation()
            RewardAdManager.loadAd(this, this)
        }
        loadingDialog = LoadingDialog(this)
        setContentView(R.layout.cnt_activity_matching)
        bv_result.entrance = "0"
        bv_result.setWatchClickListener(View.OnClickListener {
            onClickWatchAd()
        })
        EventBus.getDefault().register(this)
        network.bindView(sv_cntmatching)
        findViewById<TextView>(R.id.tv_titlebar_title).text = getString(R.string.cnt_match_title)
        titleBar = findViewById(R.id.layout_titlebar)
        ivShare = findViewById<ImageView>(R.id.iv_titlebar_icon)
        ivShare?.setImageResource(com.palmapp.master.module_cnt.R.mipmap.cnt_constellation_matching2_ic_share)
        ivShare?.alpha = 0f
        findViewById<ImageView>(R.id.iv_titlebar_back).setOnClickListener {
            if (isVip) {//订阅
                showScoreGuide()
            } else {
                ExitAdManager.showAd()
                finish()
            }
        }
        mPresenter?.loadData(cnt_id1, cnt_id2)
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
                    "9",
                    ""
                )
                var dialog = LikeDialog(this, "cnt_match")
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
                                "9",
                                ""
                            )
                            var dialog = LikeDialog(this, "cnt_match")
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
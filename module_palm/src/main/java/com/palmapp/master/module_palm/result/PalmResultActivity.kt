package com.palmapp.master.module_palm.result;

import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.palmapp.master.baselib.BaseMultipleMVPActivity
import com.palmapp.master.baselib.GoCommonEnv
import com.palmapp.master.baselib.constants.PreConstants
import com.palmapp.master.baselib.constants.RouterConstants
import com.palmapp.master.baselib.constants.RouterConstants.ACTIVITY_PALM_RESULT
import com.palmapp.master.baselib.manager.*
import com.palmapp.master.baselib.proxy.AdSdkServiceProxy
import com.palmapp.master.baselib.proxy.BillingServiceProxy
import com.palmapp.master.baselib.statistics.BaseSeq101OperationStatistic
import com.palmapp.master.baselib.utils.GoGson
import com.palmapp.master.baselib.view.GradientBorderDrawable
import com.palmapp.master.module_ad.HotRatingPresenter
import com.palmapp.master.module_ad.IHotRatingView
import com.palmapp.master.module_ad.IRewardVideoView
import com.palmapp.master.module_ad.RewardVideoPresenter
import com.palmapp.master.module_ad.manager.ExitAdManager
import com.palmapp.master.module_imageloader.DataHolder
import com.palmapp.master.module_imageloader.ImageLoader
import com.palmapp.master.module_imageloader.glide.ImageConfigImpl
import com.palmapp.master.module_palm.PalmLeadingMarginSpan
import com.palmapp.master.module_palm.R
import com.palmapp.master.module_palm.scan.PalmResultCache
import kotlinx.android.synthetic.main.palm_activity_palm_result2.*
import java.io.File
import kotlin.math.roundToInt
import com.palmapp.master.baselib.manager.RouterServiceManager.goHomeAndClearTop
import com.palmapp.master.baselib.statistics.BaseSeq103OperationStatistic
import com.palmapp.master.baselib.statistics.BaseSeq103OperationStatistic.palm_toast_f000
import com.palmapp.master.baselib.statistics.BaseSeq103OperationStatistic.wait_palm_a000

@Route(path = ACTIVITY_PALM_RESULT)
class PalmResultActivity : BaseMultipleMVPActivity(), IHotRatingView, IRewardVideoView,
    PalmResultView {
    @JvmField
    @Autowired(name = "newPlan")
    var newPlan = ""
    private var isFinish: Boolean = false
    private val palmResultPresenter = PalmResultPresenter()
    private val rewardVideoPresenter = RewardVideoPresenter(this)
    private val hotRatingPresenter = HotRatingPresenter(this, rewardVideoPresenter)
    private var startIndex = 0
    private var endIndex = 0
    var handleinfos: PalmResultCache? = null

    private var dialog: PalmChatDialog? = null

    override fun addPresenters() {
        addToPresenter(palmResultPresenter)
        addToPresenter(rewardVideoPresenter)
        addToPresenter(hotRatingPresenter)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        ARouter.getInstance().inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.palm_activity_palm_result2)
        if (!rewardVideoPresenter.isCanUseFun) {
            bv_result.visibility = View.VISIBLE
            findViewById<View>(com.palmapp.master.baselib.R.id.btn_pay).setOnClickListener {
                ARouter.getInstance().build(RouterConstants.ACTIVITY_PAY)
                    .withString("entrance", "3").navigation()
            }
            val drawable = GradientBorderDrawable(
                resources.getDimension(com.palmapp.master.baselib.R.dimen.change_4px),
                resources.getDimension(com.palmapp.master.baselib.R.dimen.change_84px),
                Color.parseColor("#0bc88c"),
                Color.parseColor("#187edc")
            )
            btn_pay_watch.background = drawable
            btn_pay_watch.visibility =
                if (AdSdkServiceProxy.isRewardAdLoad()) View.VISIBLE else View.GONE
            if (AdSdkServiceProxy.isRewardAdLoad()) {
                BaseSeq101OperationStatistic.uploadOperationStatisticData(
                    BaseSeq101OperationStatistic.ad_channel_f000, "", "1"
                )
            }
            btn_pay_watch?.setOnClickListener(View.OnClickListener {
                BaseSeq101OperationStatistic.uploadOperationStatisticData(
                    BaseSeq101OperationStatistic.ad_channel_a000, "", "1"
                )
                rewardVideoPresenter.tryToWatchAd()
            })
            if (newPlan == "1") {
                if (GoCommonEnv.isFirstRunPayGuide) {
                    val config =
                        PayGuideManager.payGuideConfig.get(PayGuideManager.FIRST_LAUNCHER)!!
                    ARouter.getInstance().build(RouterConstants.ACTIVITY_PAY)
                        .withString("entrance", config.scen).withString("type", "3")
                        .withString("newPlan", "1").navigation()
                } else {
                    val config =
                        PayGuideManager.payGuideConfig.get(PayGuideManager.NOT_FIRST_LAUNCHER)!!
                    ARouter.getInstance().build(RouterConstants.ACTIVITY_PAY)
                        .withString("entrance", config.scen).withString("type", "3")
                        .withString("newPlan", "1").navigation()
                }
            } else {
                ARouter.getInstance().build(RouterConstants.ACTIVITY_PAY)
                    .withString("entrance", "3").navigation()
            }
            BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
                BaseSeq103OperationStatistic.show_result_a000,
                "",
                "2",
                ""
            )
        }
        layout_portrait.setOnClickListener {
            if (TimerManager.hasFinishTimer(TIMER_PALM)) {
                BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(wait_palm_a000, "2")
                ARouter.getInstance().build(RouterConstants.ACTIVITY_PALM_CHAT)
                    .withSerializable("handleinfos", handleinfos).withString("newPlan", newPlan)
                    .navigation()
            } else {
                BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(wait_palm_a000, "1")
            }
        }
        findViewById<TextView>(R.id.tv_titlebar_title).text = getString(R.string.palm_daily_title)
        findViewById<View>(R.id.iv_titlebar_back).setOnClickListener {
            onBackPressed()
        }
        val cache = GoPrefManager.getDefault().getString(PreConstants.Palm.KEY_PALM_RESULT, "")
        handleinfos = GoGson.fromJson(cache, PalmResultCache::class.java)
        onTimeRemain("00:00:00")
        handleinfos?.let { handleinfos ->
            iv_palm_countdown.setImageResource(handleinfos.expertIcon)
            val path = GoCommonEnv.getApplication().cacheDir.path.plus(File.separator)
                .plus("palmResult.png")
            ImageLoader.getInstance().loadImage(
                this, ImageConfigImpl.builder().data(DataHolder.create(path))
                    .cacheStrategy(1)
                    .skipMemoryCache(true)
                    .imageView(iv_palm_title).build()
            )
            tv_palm_length.text =
                "${getString(R.string.palm_result_palm_lenth)} ${handleinfos.palm_length}"
            tv_palm_width.text =
                "${getString(R.string.palm_result_palm_width)} ${handleinfos.palm_width}"
            tv_finger_length.text =
                "${getString(R.string.palm_result_finger_lenth)} ${handleinfos.finger_length}"

            handleContent(
                handleinfos,
                layout_life_line,
                layout_business_line,
                layout_love_line,
                layout_money_line,
                layout_marry_line
            )
        }
        palmResultPresenter.startTime()
    }

    private fun handleContent(cache: PalmResultCache, vararg views: View) {
        for (contentView in views) {
            val tvTitleLife = contentView.findViewById<TextView>(R.id.tv_palm_title)
            val ivLife = contentView.findViewById<ImageView>(R.id.iv_palm)
            val tvLife = contentView.findViewById<TextView>(R.id.tv_palm)
            val btnLife = contentView.findViewById<Button>(R.id.btn_more)
            var image = 0
            var title = ""
            var content = ""
            when (contentView) {
                layout_life_line -> {
                    image = R.mipmap.palm_result_pic_lifeline
                    title = getString(R.string.palm_result_life_line)
                    content = cache.life_length
                }
                layout_business_line -> {
                    image = R.mipmap.palm_result_pic_wisdom
                    title = getString(R.string.palm_result_business_line)
                    content = cache.business_length
                }
                layout_love_line -> {
                    image = R.mipmap.palm_result_pic_emotionline
                    title = getString(R.string.palm_result_love_line)
                    content = cache.love_pos
                }
                layout_money_line -> {
                    image = R.mipmap.palm_result_pic_careerline
                    title = getString(R.string.palm_result_money_line)
                    content = cache.money_pos
                }
                layout_marry_line -> {
                    image = R.mipmap.palm_result_pic_marriageline
                    title = getString(R.string.palm_result_marry_line)
                    content = cache.marry_type
                }
            }

            ivLife.setImageResource(image)

            var lines = 0
            val imageSize = tvTitleLife.lineHeight + tvLife.lineHeight * 4
            val lp = ivLife.layoutParams
            lp.width = imageSize
            lp.height = imageSize
            ivLife.layoutParams = lp
            content = content.replace("\\n", "")
            content = content.replace("\n", "")
            lines =
                ((imageSize - tvTitleLife.lineHeight) / tvLife.lineHeight.toFloat()).roundToInt()
            val span = PalmLeadingMarginSpan(
                lines,
                imageSize + resources.getDimensionPixelOffset(R.dimen.change_42px)
            )
            val spannableString = SpannableString(content)
            spannableString.setSpan(span, 0, content.length - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            tvLife.text = spannableString

            tvTitleLife.text = title

            btnLife.setOnClickListener {
                btnLife.text = if (tvLife.maxLines == 4) "Collapse" else "More"
                tvLife.maxLines = (if (tvLife.maxLines == 4) Int.MAX_VALUE else 4)
            }
        }
    }

    /**
     * 没有好评引导时的回调，一般用于关闭activity和打开退出功能广告
     */
    override fun withoutHotRating() {
        if (BillingServiceProxy.isVip()) {
            ExitAdManager.showAd()
        }
        finish()
    }

    /**
     * 激励视频播放完后该展示的界面，一般是用来隐藏假结果页
     */
    override fun showRewardVipView() {
        bv_result.visibility = View.GONE

    }

    override fun onFinish() {
        isFinish = true
        tv_palm_countdown.text =
            getString(R.string.palm_result_contacted_expert, handleinfos?.expert_name)
        dialog?.dismiss()
    }

    val foreground = ForegroundColorSpan(Color.parseColor("#FFBB59"))
    override fun onTimeRemain(time: String) {
        val text = getString(R.string.palm_result_contacting_expert, time)
        val sp = SpannableString(text)
        if (startIndex == 0 && endIndex == 0) {
            startIndex = text.indexOf(time)
            endIndex = startIndex + time.length
        }
        sp.setSpan(foreground, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        tv_palm_countdown.text = sp
        dialog?.content?.text = sp
    }

    override fun onBackPressed() {
        if (!rewardVideoPresenter.isCanUseFun) {
            super.onBackPressed()
            return
        }
        if (isFinish) {
            super.onBackPressed()
            return
        }
        if (dialog == null) {
            dialog = PalmChatDialog(this, View.OnClickListener {
                dialog?.dismiss()
                finish()
            })
            dialog?.show()
            dialog?.iv?.setImageResource(handleinfos?.expertIcon ?: R.mipmap.palm_portrait_1)
            onTimeRemain("00:00:00")
            BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(palm_toast_f000)
        }
    }

    override fun finish() {
        goHomeAndClearTop() {
            super.finish()
        }
    }
}
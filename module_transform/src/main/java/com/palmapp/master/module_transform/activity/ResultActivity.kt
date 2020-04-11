package com.palmapp.master.module_transform.activity

import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.alibaba.android.arouter.launcher.ARouter
import com.cs.bd.commerce.util.LogUtils
import com.palmapp.master.baselib.BaseActivity
import com.palmapp.master.baselib.GoCommonEnv
import com.palmapp.master.baselib.bean.user.getCntCover
import com.palmapp.master.baselib.bean.user.getConstellationById
import com.palmapp.master.baselib.constants.PreConstants
import com.palmapp.master.baselib.constants.RouterConstants
import com.palmapp.master.baselib.event.SubscribeUpdateEvent
import com.palmapp.master.baselib.manager.GoPrefManager
import com.palmapp.master.baselib.manager.ShareManager
import com.palmapp.master.baselib.manager.ThreadExecutorProxy
import com.palmapp.master.baselib.manager.config.ConfigManager
import com.palmapp.master.baselib.manager.config.StarGuideConfig
import com.palmapp.master.baselib.proxy.BillingServiceProxy
import com.palmapp.master.baselib.proxy.OnLoadAdRewardListener
import com.palmapp.master.baselib.statistics.BaseSeq101OperationStatistic
import com.palmapp.master.baselib.statistics.BaseSeq103OperationStatistic
import com.palmapp.master.baselib.utils.ImageUtil
import com.palmapp.master.baselib.utils.StatusBarUtil
import com.palmapp.master.baselib.utils.TimeUtils
import com.palmapp.master.baselib.view.AdsWatchLeftDialog
import com.palmapp.master.baselib.view.GradientBorderDrawable
import com.palmapp.master.baselib.view.LikeDialog
import com.palmapp.master.baselib.view.LoadingDialog
import com.palmapp.master.module_ad.manager.ExitAdManager
import com.palmapp.master.module_ad.manager.RewardAdManager
import com.palmapp.master.module_imageloader.ImageLoaderUtils
import com.palmapp.master.module_imageloader.glide.ImageLoadingListener
import com.palmapp.master.module_transform.R
import kotlinx.android.synthetic.main.transform_activity_result.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * 变性结果页
 */
class ResultActivity : BaseActivity(), OnLoadAdRewardListener {
    private var adTimes = RewardAdManager.getConfig()?.show_count ?: 0
    private var dialog: Dialog? = null
    private var loadingDialog: LoadingDialog? = null
    private var isNeedToShow = false

    private var isFirstShow = true
    private var isUnlock = false //视频解锁

    private lateinit var mIvLike: ImageView
    private lateinit var mIvShare_2: ImageView
    private lateinit var mIvDownlod: ImageView

    override fun onVideoFinish() {
        dialog?.dismiss()
        adTimes--
        if (adTimes > 0) {
            showAdWatchDialog()
        } else {
            isUnlock = true
            isVip = true
            layout_pay.visibility = View.GONE
            iv_result.setImageBitmap(mResultBitmap)

            layout_share.visibility = View.VISIBLE
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


    private var mResult = ""
    private var mResultBitmap: Bitmap? = null //变性后的图片
    private var mOriginBitmap: Bitmap? = null //最终确定的图片


    private var isVip = BillingServiceProxy.isVip()

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onResultEvent(result: String) {
        mResult = result
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onOrigntEvent(orign: Bitmap) {
        EventBus.getDefault().removeStickyEvent(orign)
        mOriginBitmap = orign
    }

    //接受订阅的处理结果
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSubscribeUpdateEvent(event: SubscribeUpdateEvent) {
        if (event.isVip) {//订阅成功
//            ExitAdManager.addAdListener(this)
            isUnlock = true
            isVip = true
            layout_pay.visibility = View.GONE
            iv_result.setImageBitmap(mResultBitmap)

            layout_share.visibility = View.VISIBLE
        }
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        RewardAdManager.addFreeUse()
        isVip = BillingServiceProxy.isVip() || RewardAdManager.isFreeUse()
        if (!BillingServiceProxy.isVip() && !RewardAdManager.isFreeUse()) {
            ARouter.getInstance().build(RouterConstants.ACTIVITY_PAY)
                .withString("entrance", "10")
                .navigation(this)
            RewardAdManager.loadAd(this, this)
            ExitAdManager.loadAd(this)
            BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(BaseSeq103OperationStatistic.show_result_a000,"","5","")
        }
        loadingDialog = LoadingDialog(this)
        setContentView(R.layout.transform_activity_result)
        findViewById<TextView>(R.id.tv_titlebar_title).text =
            getString(R.string.gender_challenge)
        StatusBarUtil.setRootViewFitsSystemWindows(this, true)

        EventBus.getDefault().register(this)
        initView()
    }


    private fun initView() {
        mIvLike = layout_share.findViewById<ImageView>(R.id.iv_like)
        mIvShare_2 = layout_share.findViewById<ImageView>(R.id.iv_share)
        mIvDownlod = layout_share.findViewById<ImageView>(R.id.iv_download)
        mIvDownlod.visibility = View.VISIBLE

        result_title_bar.findViewById<ImageView>(R.id.iv_titlebar_back).setOnClickListener {
            if (isVip) {//订阅
                showScoreGuide()
            } else {
                ExitAdManager.showAd()
                finish()
            }
        }


        tv_pay.setOnClickListener {
            ARouter.getInstance().build(RouterConstants.ACTIVITY_PAY).withString("entrance", "10")
                .navigation()
        }
        val drawable = GradientBorderDrawable(
            resources.getDimension(com.palmapp.master.baselib.R.dimen.change_4px),
            resources.getDimension(com.palmapp.master.baselib.R.dimen.change_84px),
            Color.parseColor("#0bc88c"),
            Color.parseColor("#187edc")
        )
        btn_pay_watch.background = drawable
        btn_pay_watch.setOnClickListener {
            onClickWatchAd()
            BaseSeq101OperationStatistic.uploadOperationStatisticData(
                BaseSeq101OperationStatistic.ad_channel_a000, "", "1"
            )
        }
        btn_pay_watch.visibility = if (RewardAdManager.isLoadAd()) View.VISIBLE else View.GONE
        if (RewardAdManager.isLoadAd()) {
            BaseSeq101OperationStatistic.uploadOperationStatisticData(
                BaseSeq101OperationStatistic.ad_channel_f000,
                "",
                "1"
            )
        }
        if (mResult.isNotEmpty()) {
            ImageLoaderUtils.displayImage(this, mResult, object : ImageLoadingListener() {
                override fun onLoadingComplete(bitmap: Bitmap?) {
                    super.onLoadingComplete(bitmap)
                    mResultBitmap = bitmap
                    if (isVip) {
                        //已订阅
                        BaseSeq101OperationStatistic.uploadOperationStatisticData(
                            BaseSeq101OperationStatistic.gender_page_f000, "", "4"
                        )
                        layout_pay.visibility = View.GONE
                        iv_result.setImageBitmap(mResultBitmap)

                        layout_share.visibility = View.VISIBLE
                    } else {
                        BaseSeq101OperationStatistic.uploadOperationStatisticData(
                            BaseSeq101OperationStatistic.gender_page_f000, "", "5"
                        )
                        iv_result.setImageResource(R.mipmap.gender_result_pic_gender_premium)
                        layout_pay.visibility = View.VISIBLE

                        layout_share.visibility = View.INVISIBLE
                    }
                }

                override fun onLoadingFailed(bitmap: Bitmap?) {
                    super.onLoadingFailed(bitmap)
                }

                override fun onLoadingStarted(bitmap: Bitmap?) {
                    super.onLoadingStarted(bitmap)
                    iv_result.setImageResource(R.mipmap.placeholder_default)
                    mResultBitmap = bitmap
                }
            })
        }

        //点赞
        mIvLike.setOnClickListener {
            BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(BaseSeq103OperationStatistic.like_a000,"","3","")
            //帧动画
            val animationDrawable = mIvLike.drawable as AnimationDrawable
            animationDrawable.start()

            ThreadExecutorProxy.runOnMainThread(
                Runnable {
                    BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(BaseSeq103OperationStatistic.thumbs_up_a000)
                    var isLiked = GoPrefManager.getDefault().getBoolean(PreConstants.ScoreGuide.KEY_IS_FIRST_LIKE, false)
                    if (!isLiked){
                        BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
                            BaseSeq103OperationStatistic.rating_f000,
                            "",
                            "5",
                            ""
                        )
                        var dialog = LikeDialog(this, "like")
                        dialog.show()
                        GoPrefManager.getDefault().putBoolean(PreConstants.ScoreGuide.KEY_IS_FIRST_LIKE, true).commit()
                    }

                }, 1000)
        }

        mIvShare_2.setOnClickListener {
            share()
        }
        mIvDownlod.setOnClickListener {
            download()
        }
    }

    private fun download() {
        mResultBitmap?.let {
            BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
                BaseSeq103OperationStatistic.download_func_a000,
                "",
                "3",
                ""
            )
            ImageUtil.save(this, it)
        }
    }


    //分享 生成图片
    private fun share() {
        BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
            BaseSeq103OperationStatistic.share_a000,
            "",
            "3",
            ""
        )
        val view =
            LayoutInflater.from(this).inflate(R.layout.transform_activity_result_share, null, false)
        if (TextUtils.isEmpty(GoCommonEnv.userInfo?.name ?: "")) {
            view.findViewById<View>(R.id.share_info_layout).visibility = View.INVISIBLE
        }
        mOriginBitmap?.let {
            view.findViewById<ImageView>(R.id.iv_before).setImageBitmap(it)
        }
        mResultBitmap?.let {
            view.findViewById<ImageView>(R.id.iv_after).setImageBitmap(it)
        }
        view.findViewById<ImageView>(R.id.iv_me)
            .setImageResource(getCntCover(GoCommonEnv.userInfo?.constellation ?: 1))
        view.findViewById<TextView>(R.id.tv_user_name).text = GoCommonEnv.userInfo?.name ?: ""
        view.findViewById<TextView>(R.id.tv_user_cnt).text =
            getConstellationById(GoCommonEnv.userInfo?.constellation ?: 1)
        //改变"Palm Secret"文字的颜色
        val download = view.findViewById<TextView>(R.id.tv_download)
        try {
            val str = getString(R.string.share_download) ?: ""
            val index = str.indexOf("Palm Secret")
            val builder = SpannableStringBuilder(str)
            builder.setSpan(
                ForegroundColorSpan(Color.parseColor("#c8e8ff")),
                index,
                index + "Palm Secret".length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            download.setText(builder)
        } catch (e: Exception) {

        }

        ShareManager.share(view, getString(R.string.app_app_name) ?: "")
    }


    private fun showAdWithoutGuide() {
        if (isUnlock) {
            //观看视频解锁 弹广告
            ExitAdManager.showAd()
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
                    "3",
                    ""
                )
                var dialog = LikeDialog(this, "transform")
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
                            var dialog = LikeDialog(this, "transform")
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


    override fun onBackPressed() {
        if (isVip) {//订阅
            showScoreGuide()
        } else {
            ExitAdManager.showAd()
            super.onBackPressed()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

}

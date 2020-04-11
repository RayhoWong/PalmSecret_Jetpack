package com.palmapp.master.module_face.activity.baby;

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.cs.bd.commerce.util.LogUtils
import com.palmapp.master.baselib.BaseMVPActivity
import com.palmapp.master.baselib.GoCommonEnv
import com.palmapp.master.baselib.bean.face.BabyGenerateRequest
import com.palmapp.master.baselib.bean.face.BabyGenerateResponse
import com.palmapp.master.baselib.bean.user.getCntCover
import com.palmapp.master.baselib.bean.user.getConstellationById
import com.palmapp.master.baselib.constants.AppConstants
import com.palmapp.master.baselib.constants.PreConstants
import com.palmapp.master.baselib.constants.RouterConstants
import com.palmapp.master.baselib.manager.GoPrefManager
import com.palmapp.master.baselib.manager.PayGuideManager
import com.palmapp.master.baselib.manager.RouterServiceManager.goHomeAndClearTop
import com.palmapp.master.baselib.manager.ShareManager
import com.palmapp.master.baselib.manager.ThreadExecutorProxy
import com.palmapp.master.baselib.manager.config.ConfigManager
import com.palmapp.master.baselib.manager.config.StarGuideConfig
import com.palmapp.master.baselib.proxy.BillingServiceProxy
import com.palmapp.master.baselib.statistics.BaseSeq101OperationStatistic
import com.palmapp.master.baselib.statistics.BaseSeq103OperationStatistic
import com.palmapp.master.baselib.utils.ImageUtil
import com.palmapp.master.baselib.utils.TimeUtils
import com.palmapp.master.baselib.view.GradientBorderDrawable
import com.palmapp.master.baselib.view.LikeDialog
import com.palmapp.master.baselib.view.LoadingDialog
import com.palmapp.master.baselib.view.OkDialog
import com.palmapp.master.module_ad.manager.ExitAdManager
import com.palmapp.master.module_ad.manager.RewardAdManager
import com.palmapp.master.module_face.R
import com.palmapp.master.module_imageloader.ImageLoaderUtils
import com.palmapp.master.module_imageloader.glide.ImageLoadingListener
import kotlinx.android.synthetic.main.face_activity_baby_result.*
import java.io.File

@Route(path = RouterConstants.ACTVITIY_FACE_BABY_RESULT)
class BabyResultActivity : BaseMVPActivity<BabyResultView, BabyResultPresenter>(), BabyResultView {
    @JvmField
    @Autowired(name = "newPlan")
    var newPlan = ""

    private var resultBitmap: Bitmap? = null
    //    private var ivShare: ImageView? = null
    private var isVip = BillingServiceProxy.isVip()

    private var isUnlock = false


    override fun getUnlock(isUnlock: Boolean) {
        this.isUnlock = isUnlock
    }

    private lateinit var ivLike: ImageView
    private lateinit var ivShare: ImageView
    private lateinit var ivDownload: ImageView


    override fun showNetError() {
        val dialog = OkDialog(this)
        dialog.title = getString(R.string.net_error)
        dialog.show()
        dismissLoadingDialog()
    }

    override fun showVipView(isVip: Boolean) {
        this.isVip = isVip
        if (!isVip) {
            btn_pay.setOnClickListener {
                ARouter.getInstance().build(RouterConstants.ACTIVITY_PAY)
                    .withString("entrance", "9")
                    .navigation()
            }
            layout_face_result.visibility = View.VISIBLE
            iv_face_result.visibility = View.INVISIBLE
            ivLike.visibility = View.INVISIBLE
            ivShare.visibility = View.INVISIBLE
            ivDownload.visibility = View.INVISIBLE
            layout_share.visibility = View.INVISIBLE
            val drawable = GradientBorderDrawable(
                resources.getDimension(com.palmapp.master.baselib.R.dimen.change_4px),
                resources.getDimension(com.palmapp.master.baselib.R.dimen.change_84px),
                Color.parseColor("#0bc88c"),
                Color.parseColor("#187edc")
            )

            btn_pay_watch.background = drawable
            btn_pay_watch.setOnClickListener {
                mPresenter?.onClickWatchAd()
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
        } else {
            layout_face_result.visibility = View.INVISIBLE
            iv_face_result.visibility = View.VISIBLE
            ivLike.visibility = View.VISIBLE
            ivShare.visibility = View.VISIBLE
            ivDownload.visibility = View.VISIBLE
            layout_share.visibility = View.VISIBLE
        }
    }

    override fun showResult(url: String?) {
        resultBitmap = null
        ImageLoaderUtils.displayImage(this, url, object : ImageLoadingListener() {
            override fun onLoadingComplete(bitmap: Bitmap?) {
                super.onLoadingComplete(bitmap)
                iv_face_result.setImageBitmap(bitmap)
                resultBitmap = bitmap
//                ivShare?.visibility = View.VISIBLE
            }

            override fun onLoadingFailed(bitmap: Bitmap?) {
                super.onLoadingFailed(bitmap)
            }

            override fun onLoadingStarted(bitmap: Bitmap?) {
                super.onLoadingStarted(bitmap)
                iv_face_result.setImageResource(R.mipmap.placeholder_default)
//                ivShare?.visibility = View.INVISIBLE
            }
        })
    }

    override fun showBoyPic() {
        tv_face_boy.isSelected = true
        tv_face_girl.isSelected = false
        tv_face_boy.background = gradientBorderDrawable
        tv_face_girl.background = grayBorderDrawable

    }

    override fun showGirlPic() {
        tv_face_boy.isSelected = false
        tv_face_girl.isSelected = true
        tv_face_boy.background = grayBorderDrawable
        tv_face_girl.background = gradientBorderDrawable
    }

    override fun showLoadingDialog() {
        loadingDialog.show()
    }

    override fun dismissLoadingDialog() {
        loadingDialog.dismiss()
    }

    @JvmField
    @Autowired(name = "result")
    var result: BabyGenerateResponse? = null

    @JvmField
    @Autowired(name = "request")
    var request: BabyGenerateRequest? = null

    lateinit var loadingDialog: LoadingDialog

    val gradientBorderDrawable = GradientBorderDrawable(
        GoCommonEnv.getApplication().resources.getDimension(R.dimen.change_3px),
        GoCommonEnv.getApplication().resources.getDimension(R.dimen.change_84px),
        Color.parseColor("#0bc88c"),
        Color.parseColor("#187edc")
    )
    val grayBorderDrawable =
        GoCommonEnv.getApplication().resources.getDrawable(R.drawable.face_ic_baby_gray_bg)

    override fun createPresenter(): BabyResultPresenter {
        return BabyResultPresenter(request, result)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        RewardAdManager.addFreeUse()
        ARouter.getInstance().inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.face_activity_baby_result)
        ivLike = findViewById(R.id.iv_like)
        ivShare = findViewById(R.id.iv_share)
        ivDownload = findViewById(R.id.iv_download)
//        ivShare = findViewById<ImageView>(R.id.iv_titlebar_icon)
//        ivShare?.visibility = View.INVISIBLE
//        ivShare?.setImageResource(R.mipmap.face_ic_share)
//        ivShare?.setOnClickListener {
//            if (!BillingServiceProxy.isVip()) {
//                ARouter.getInstance().build(RouterConstants.ACTIVITY_PAY)
//                    .withString("entrance", "9").withString("type", "9").navigation()
//                return@setOnClickListener
//            }
//            mPresenter?.share(resultBitmap, iv_face_left.drawable, iv_face_right.drawable)
//        }
        if (BillingServiceProxy.isVip() || RewardAdManager.isFreeUse()) {
            BaseSeq101OperationStatistic.uploadOperationStatisticData(
                BaseSeq101OperationStatistic.baby_page_f000,
                "",
                "6"
            )
        } else {
            BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
                BaseSeq103OperationStatistic.show_result_a000,
                "",
                "4",
                ""
            )
            ExitAdManager.loadAd(this)
            if (newPlan == "1") {
                if (GoCommonEnv.isFirstRunPayGuide) {
                    val config =
                        PayGuideManager.payGuideConfig.get(PayGuideManager.FIRST_LAUNCHER)!!
                    ARouter.getInstance().build(RouterConstants.ACTIVITY_PAY)
                        .withString("entrance", config.scen).withString("type", "9")
                        .withString("newPlan", "1").navigation()
                } else {
                    val config =
                        PayGuideManager.payGuideConfig.get(PayGuideManager.NOT_FIRST_LAUNCHER)!!
                    ARouter.getInstance().build(RouterConstants.ACTIVITY_PAY)
                        .withString("entrance", config.scen).withString("type", "9")
                        .withString("newPlan", "1").navigation()
                }
            } else {
                ARouter.getInstance().build(RouterConstants.ACTIVITY_PAY)
                    .withString("entrance", "9")
                    .navigation()
            }

            BaseSeq101OperationStatistic.uploadOperationStatisticData(
                BaseSeq101OperationStatistic.baby_page_f000,
                "",
                "7"
            )
        }

        loadingDialog = LoadingDialog(this)
        findViewById<View>(R.id.iv_titlebar_back).setOnClickListener {
            if (isVip) {//订阅
                showScoreGuide()
            } else {
                ExitAdManager.showAd()
                finish()
            }
        }

        val tv = findViewById<TextView>(R.id.tv_titlebar_title)
        tv.text = getString(R.string.face_baby_title)
        ImageLoaderUtils.displayImage(this, File(AppConstants.FACE_BABY_MOTHER), iv_face_left)
        ImageLoaderUtils.displayImage(this, File(AppConstants.FACE_BABY_FATHER), iv_face_right)
        tv_face_boy.setOnClickListener {
            mPresenter?.selectBoy()
        }
        tv_face_girl.setOnClickListener {
            mPresenter?.selectGirl()
        }
        showVipView(BillingServiceProxy.isVip() || RewardAdManager.isFreeUse())
        mPresenter?.selectBoy()

        //点赞
        ivLike.setOnClickListener {
            BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(BaseSeq103OperationStatistic.like_a000,"","2","")
            //帧动画
            val animationDrawable = ivLike.drawable as AnimationDrawable
            animationDrawable.start()

            ThreadExecutorProxy.runOnMainThread(
                Runnable {
                    BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
                        BaseSeq103OperationStatistic.thumbs_up_a000
                    )
                    var isLiked = GoPrefManager.getDefault()
                        .getBoolean(PreConstants.ScoreGuide.KEY_IS_FIRST_LIKE, false)
                    if (!isLiked) {
                        BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
                            BaseSeq103OperationStatistic.rating_f000,
                            "",
                            "5",
                            ""
                        )
                        var dialog = LikeDialog(this, "like")
                        dialog.show()
                        GoPrefManager.getDefault()
                            .putBoolean(PreConstants.ScoreGuide.KEY_IS_FIRST_LIKE, true).commit()
                    }
                }, 1000
            )
        }

        ivShare.setOnClickListener {
            share()
        }
        ivDownload.setOnClickListener {
            download()
        }
    }

    private fun download() {
        resultBitmap?.let {
            BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
                BaseSeq103OperationStatistic.download_func_a000,
                "",
                "2",
                ""
            )
            ImageUtil.save(this, it)
        }
    }


    private fun share() {
//        mPresenter?.share(resultBitmap, iv_face_left.drawable, iv_face_right.drawable)
        BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
            BaseSeq103OperationStatistic.share_a000,
            "",
            "2",
            ""
        )
        val view = LayoutInflater.from(this)
            .inflate(R.layout.face_activity_baby_share, null, false)
        val bitmap = resultBitmap
        val bitmap1 = (iv_face_left.drawable as BitmapDrawable).bitmap
        val bitmap2 = (iv_face_right.drawable as BitmapDrawable).bitmap
        if (TextUtils.isEmpty(GoCommonEnv.userInfo?.name ?: "")) {
            view.findViewById<View>(R.id.share_info_layout).visibility = View.INVISIBLE
        }
        bitmap?.let {
            view.findViewById<ImageView>(R.id.iv_after).setImageBitmap(bitmap)
        }
        bitmap1?.let {
            view.findViewById<ImageView>(R.id.iv_left).setImageBitmap(bitmap1)
        }
        bitmap2?.let {
            view.findViewById<ImageView>(R.id.iv_right).setImageBitmap(bitmap2)
        }
        view.findViewById<ImageView>(R.id.iv_me)
            .setImageResource(getCntCover(GoCommonEnv.userInfo?.constellation ?: 1))
        view.findViewById<TextView>(R.id.tv_user_name).text =
            GoCommonEnv.userInfo?.name ?: ""
        view.findViewById<TextView>(R.id.tv_user_cnt).text =
            getConstellationById(GoCommonEnv.userInfo?.constellation ?: 1)

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
                    "2",
                    ""
                )
                var dialog = LikeDialog(this, "baby")
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
                                "2",
                                ""
                            )
                            var dialog = LikeDialog(this, "baby")
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

    override fun finish() {
        super.finish()
        goHomeAndClearTop() {
            super.finish()
        }
    }

}
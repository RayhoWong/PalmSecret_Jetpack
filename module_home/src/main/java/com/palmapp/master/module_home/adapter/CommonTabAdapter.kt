package com.palmapp.master.module_home.adapter

import android.app.Activity
import android.graphics.drawable.BitmapDrawable
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.launcher.ARouter
import com.applovin.adview.AppLovinAdView
import com.cs.bd.daemon.util.LogUtils
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.formats.NativeAppInstallAd
import com.google.android.gms.ads.formats.NativeAppInstallAdView
import com.google.android.gms.ads.formats.NativeContentAd
import com.google.android.gms.ads.formats.NativeContentAdView
import com.palmapp.master.baselib.GoCommonEnv
import com.palmapp.master.baselib.constants.RouterConstants
import com.palmapp.master.baselib.manager.*
import com.palmapp.master.baselib.manager.config.*
import com.palmapp.master.baselib.proxy.BillingServiceProxy
import com.palmapp.master.baselib.statistics.BaseSeq103OperationStatistic
import com.palmapp.master.module_ad.manager.TabBottomAdManager
import com.palmapp.master.module_home.R
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import java.text.SimpleDateFormat

/**
 * Created by huangweihao on 2019/8/12.
 * 每个Tab公用的adapter
 */
class CommonTabAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>, OnTimerListener {

    private var mTitles: MutableList<String>
    private var mContext: Activity
    private var mTab: String //tab名称

    private val NORMAL_VIEW = 0 //常规布局
    private val AD_VIEW = 1 //广告布局(默认最后一个位置)

    private val adTab: Int

    private lateinit var palmShapeJudgment: View
    private var formatter: SimpleDateFormat? = null
    private var timer: WeakReference<TextView>? = null
    private var isVip = BillingServiceProxy.isVip()

    constructor(
        mTitles: MutableList<String>,
        mContext: Activity,
        tab: String,
        adTab: Int
    ) : super() {
        this.mTitles = mTitles
        this.mContext = mContext
        this.mTab = tab
        this.adTab = adTab
        if (mTab == mContext.getString(R.string.home_palm_title) || mTab == mContext.getString(R.string.home_face_title)) {
            formatter = SimpleDateFormat("HH:mm:ss")
            formatter?.timeZone = java.util.TimeZone.getTimeZone("GMT+00:00")
        }
    }

    override fun getItemViewType(position: Int) =
        if (TextUtils.equals(mTitles[position], "ad")) AD_VIEW else NORMAL_VIEW


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            NORMAL_VIEW -> {
                var view = LayoutInflater.from(mContext)
                    .inflate(R.layout.home_item_common_tab, parent, false)
                return MyHolder(view)
            }
            AD_VIEW -> {
                var view = LayoutInflater.from(mContext)
                    .inflate(R.layout.home_item_ad_layout, parent, false)
                return AdHolder(view)
            }

            else -> {
                var view = LayoutInflater.from(mContext)
                    .inflate(R.layout.home_item_common_tab, parent, false)
                return MyHolder(view)
            }
        }
    }

    override fun getItemCount(): Int {
        return mTitles.size
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        //标题、图标、内容
        when (mTab) {
            mContext.getString(R.string.home_palm_title)//手相
            -> when (mTitles.get(position)) {
                TAB_PALM_DAILY -> {
                    if (holder is MyHolder) {
                        timer = WeakReference(holder.mTvCountDown)
                        if (TimerManager.hasTimer(TIMER_PALM)) {
                            holder.mTvCountDown.visibility = View.VISIBLE
                            if (TimerManager.hasFinishTimer(TIMER_PALM)) {
                                onFinish()
                            }
                        }
                        holder.mTvCountDown.tag = holder.mIvPremium
                        holder.mIvIcon.setImageResource(R.mipmap.palm_scan)
                        holder.mTvTitle.text = mContext.getString(R.string.palm_daily_title)
                        holder.mTvContent.text = mContext.getString(R.string.palm_daily_des)
                        holder.notifyVipMark()
                    }
                }
                TAB_PALM_JUDG -> {
                    if (holder is MyHolder) {
                        holder.mIvIcon.setImageResource(R.mipmap.palm_quiz)
                        holder.mTvTitle.text = mContext.getString(R.string.palm_quiz_title)
                        holder.mTvContent.text = mContext.getString(R.string.palm_quiz_des)
                        holder.notifyVipMark()
                    }
                }
                TAB_PALM_MATCH -> {
                    if (holder is MyHolder) {
                        holder.mIvIcon.setImageResource(R.mipmap.palm_matching)
                        holder.mTvTitle.text = mContext.getString(R.string.home_palmmatch_title)
                        holder.mTvContent.text = mContext.getString(R.string.home_palmmatch_message)
                        holder.notifyVipMark()
                    }
                }
                "ad" -> {//广告
                    if (holder is AdHolder) {
                        loadAd(holder, position)
                    }
                }
                TAB_PALM_TEST -> {
                    if (holder is MyHolder) {
                        palmShapeJudgment = holder.itemView
                        EventBus.getDefault().postSticky(palmShapeJudgment)

                        holder.mIvIcon.setImageResource(R.mipmap.palm_judg)
                        holder.mTvTitle.text = mContext.getString(R.string.palm_shape_title)
                        holder.mTvContent.text = mContext.getString(R.string.palm_shape_des)
                        holder.mTvComming.visibility = View.VISIBLE
                    }
                }

            }

            mContext.getString(R.string.home_cnt_title)//星座
            -> when (mTitles.get(position)) {
                "0" -> {
                    if (holder is MyHolder) {
                        holder.mIvIcon.setImageResource(R.mipmap.cnt_daily)
                        holder.mTvTitle.text = mContext.getString(R.string.cnt_daily_title)
                        holder.mTvContent.text = mContext.getString(R.string.cnt_daily_des)
                    }
                }
                "1" -> {
                    if (holder is MyHolder) {
                        holder.mIvIcon.setImageResource(R.mipmap.cnt_matching)
                        holder.mTvTitle.text = mContext.getString(R.string.cnt_match_title)
                        holder.mTvContent.text = mContext.getString(R.string.cnt_match_des)
                    }
                }
                "ad" -> {//广告
                    if (holder is AdHolder) {
                        loadAd(holder, position)
                    }
                }
                "2" -> {
                    if (holder is MyHolder) {
                        holder.mIvIcon.setImageResource(R.mipmap.cnt_maya)
                        holder.mTvTitle.text = mContext.getString(R.string.cnt_mayan_title)
                        holder.mTvContent.text = mContext.getString(R.string.cnt_mayan_des)
                        holder.mTvComming.visibility = View.VISIBLE
                    }
                }
                "3" -> {
                    if (holder is MyHolder) {
                        holder.mIvIcon.setImageResource(R.mipmap.cnt_druid)
                        holder.mTvTitle.text = mContext.getString(R.string.cnt_druid_title)
                        holder.mTvContent.text = mContext.getString(R.string.cnt_druid_des)
                        holder.mTvComming.visibility = View.VISIBLE
                    }
                }

            }

            mContext.getString(R.string.home_tarot_title)//塔罗牌
            -> when (mTitles.get(position)) {
                "0" -> {
                    if (holder is MyHolder) {
                        holder.mIvIcon.setImageResource(R.mipmap.tarot_daily)
                        holder.mTvTitle.text = mContext.getString(R.string.tarot_daily_title)
                        holder.mTvContent.text = mContext.getString(R.string.tarot_daily_des)
                    }
                }
                "1" -> {
                    if (holder is MyHolder) {
                        holder.mIvIcon.setImageResource(R.mipmap.banner_tarot_love_2)
                        holder.mTvTitle.text = mContext.getString(R.string.tarot_love_title)
                        holder.mTvContent.text = mContext.getString(R.string.tarot_love_des)
                    }
                }
                "2" -> {
                    if (holder is MyHolder) {
                        holder.mIvIcon.setImageResource(R.mipmap.tarot_career)
                        holder.mTvTitle.text = mContext.getString(R.string.tarot_career_title)
                        holder.mTvContent.text = mContext.getString(R.string.tarot_career_des)
                    }
                }
                "ad" -> {//广告
                    if (holder is AdHolder) {
                        loadAd(holder, position)
                    }
                }
                "3" -> {
                    if (holder is MyHolder) {
                        holder.mIvIcon.setImageResource(R.mipmap.tarot_health)
                        holder.mTvTitle.text = mContext.getString(R.string.tarot_health_title)
                        holder.mTvContent.text = mContext.getString(R.string.tarot_health_des)
                        holder.mTvComming.visibility = View.VISIBLE
                    }
                }


            }

            mContext.getString(R.string.home_face_title)//面相
            -> when (mTitles.get(position)) {
                TAB_FACE_OLD -> {//变老
                    if (holder is MyHolder) {
                        timer = WeakReference(holder.mTvCountDown)
                        if (TimerManager.hasTimer(TIMER_OLD)) {
                            holder.mTvCountDown.visibility = View.VISIBLE
                            if (TimerManager.hasFinishTimer(TIMER_OLD)) {
                                onFinish()
                            }
                        }
                        holder.mTvCountDown.tag = holder.mIvPremium
                        holder.mIvIcon.setImageResource(R.mipmap.face_old)
                        holder.mTvTitle.text = mContext.getString(R.string.face_daily_title)
                        holder.mTvContent.text = mContext.getString(R.string.face_daily_des)
                        holder.notifyVipMark()
                    }
                }
                TAB_FACE_BABY -> {//宝宝
                    if (holder is MyHolder) {
                        holder.mIvIcon.setImageResource(R.mipmap.face_baby)
                        holder.mTvTitle.text = mContext.getString(R.string.baby_predict)
                        holder.mTvContent.text = mContext.getString(R.string.baby_fature)
                        holder.notifyVipMark()
                    }
                }
                TAB_FACE_TRANSFORM -> {//变性
                    if (holder is MyHolder) {
                        holder.mIvIcon.setImageResource(R.mipmap.face_transform)
                        holder.mTvTitle.text = mContext.getString(R.string.gender_challenge)
                        holder.mTvContent.text = mContext.getString(R.string.transform_likes)
                        holder.notifyVipMark()
                    }
                }
                "ad" -> {//广告
                    if (holder is AdHolder) {
                        loadAd(holder, position)
                    }
                }
                TAB_FACE_CARTOON -> {//滤镜
                    if (holder is MyHolder) {
                        holder.mIvIcon.setImageResource(R.mipmap.face_filter)
                        holder.mTvTitle.text = mContext.getString(R.string.filter_cartoon_title)
                        holder.mTvContent.text = mContext.getString(R.string.filter_cartoon_des)
                        holder.notifyVipMark()
                    }
                }

                TAB_FACE_MANGA -> {
                    if (holder is MyHolder) {
                        holder.mIvIcon.setImageResource(R.mipmap.face_manga)
                        holder.mTvTitle.text = mContext.getString(R.string.magic_comic_title)
                        holder.mTvContent.text = mContext.getString(R.string.magic_comic_des)
                        holder.notifyVipMark()
                    }
                }

                TAB_FACE_ANIMAL -> {
                    if (holder is MyHolder) {
                        holder.mIvIcon.setImageResource(R.mipmap.face_animal)
                        holder.mTvTitle.text = mContext.getString(R.string.home_animal_title)
                        holder.mTvContent.text = mContext.getString(R.string.home_animal_des)
                        holder.notifyVipMark()
                    }
                }


            }

            mContext.getString(R.string.home_psy_title)//心里测试
            -> when (position) {
                0 -> {
//                    holder.mIvIcon.setImageResource(R.mipmap.cnt_daily)
//                    holder.mTvMessage.text = mContext.getString(R.string.cnt_daily_title)
//                    holder.mTvContent.text = mContext.getString(R.string.cnt_daily_des)
                }
            }
        }


        //跳转
        holder.itemView.setOnClickListener {
            when (mTab) {
                mContext.getString(R.string.home_palm_title)//手相
                -> {
                    when (mTitles.get(position)) {
                        TAB_PALM_DAILY -> {//手相扫描
                            BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
                                BaseSeq103OperationStatistic.palm_fun_a000,
                                "",
                                "1", ""
                            )
                            if (TimerManager.hasTimer(TIMER_PALM)) {
                                ARouter.getInstance().build(RouterConstants.ACTIVITY_PALM_RESULT)
                                    .navigation()
                            } else {
                                ARouter.getInstance().build(RouterConstants.ACTIVITY_PALM_SCAN)
                                    .navigation()
                            }
                        }
                        TAB_PALM_JUDG -> {//手相问答
                            BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
                                BaseSeq103OperationStatistic.palm_fun_a000,
                                "",
                                "2", ""
                            )
                            ARouter.getInstance().build(RouterConstants.ACTIVITY_PALM_QUIZ)
                                .navigation()
                        }
                        TAB_PALM_MATCH -> {
                            BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
                                BaseSeq103OperationStatistic.palm_fun_a000,
                                "",
                                "3", ""
                            )
                            ARouter.getInstance().build(RouterConstants.ACTIVITY_PALM_MATCH)
                                .navigation()
                        }
                    }
                }

                mContext.getString(R.string.home_cnt_title)//星座
                -> {
                    when (mTitles.get(position)) {
                        "0" -> {//每日星座
                            BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
                                BaseSeq103OperationStatistic.horoscope_fun_a000,
                                "",
                                "1", ""
                            )
                            ARouter.getInstance().build(RouterConstants.ACTIVITY_CNT_DAILY)
                                .navigation()
                        }
                        "1" -> {//星座匹配
                            BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
                                BaseSeq103OperationStatistic.horoscope_fun_a000,
                                "",
                                "2", ""
                            )
                            ARouter.getInstance().build(RouterConstants.ACTIVITY_CNT_SELECT)
                                .navigation()
                        }
                    }
                }

                mContext.getString(R.string.home_tarot_title)//塔罗牌
                -> {
                    when (mTitles.get(position)) {
                        "0" -> {//每日塔罗牌
                            BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
                                BaseSeq103OperationStatistic.tarot_fun_a000,
                                "",
                                "1", ""
                            )
                            ARouter.getInstance().build(RouterConstants.ACTIVITY_TAROT_DAILY)
                                .navigation()
                        }

                        "1" -> {//话题塔罗牌 真爱
                            BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
                                BaseSeq103OperationStatistic.tarot_fun_a000,
                                "",
                                "2", ""
                            )
                            ARouter.getInstance().build(RouterConstants.ACTIVITY_TAROT_TOPIC_LOVE)
                                .navigation()
                        }

                        "2" -> {//话题塔罗牌 职业
                            BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
                                BaseSeq103OperationStatistic.tarot_fun_a000,
                                "",
                                "3", ""
                            )
                            ARouter.getInstance().build(RouterConstants.ACTIVITY_TAROT_TOPIC_CAREER)
                                .navigation()
                        }
                    }
                }

                mContext.getString(R.string.home_face_title)//面相
                -> {
                    when (mTitles.get(position)) {
                        TAB_FACE_OLD -> {//变老
                            BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
                                BaseSeq103OperationStatistic.face_fun_a000,
                                "",
                                "1", ""
                            )
                            if (TimerManager.hasTimer(TIMER_OLD)) {
                                ARouter.getInstance().build(RouterConstants.ACTVITIY_TakephotoActivity).withString("finish", "1")
                                    .navigation()
                            } else {
                                ARouter.getInstance().build(RouterConstants.ACTVITIY_TakephotoActivity)
                                    .navigation()
                            }
                        }
                        TAB_FACE_BABY -> {//宝宝
                            BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
                                BaseSeq103OperationStatistic.face_fun_a000,
                                "",
                                "3", ""
                            )
                            ARouter.getInstance().build(RouterConstants.ACTVITIY_FACE_BABY_MATCH)
                                .navigation()
                        }
                        TAB_FACE_TRANSFORM -> {//变性
                            BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
                                BaseSeq103OperationStatistic.face_fun_a000,
                                "",
                                "4", ""
                            )
                            ARouter.getInstance()
                                .build(RouterConstants.ACTIVITY_TRANSFORM_TAKE_PHOTO).navigation()
                        }
                        TAB_FACE_CARTOON -> {
                            //艺术滤镜
                            BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
                                BaseSeq103OperationStatistic.face_fun_a000,
                                "",
                                "5", ""
                            )

                            ARouter.getInstance()
                                .build(RouterConstants.ACTIVITY_CARTOON_TAKE_PHOTO).navigation()
                        }
                        TAB_FACE_ANIMAL -> {
                            //变动物
                            BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
                                BaseSeq103OperationStatistic.face_fun_a000,
                                "",
                                "7", ""
                            )

                            ARouter.getInstance()
                                .build(RouterConstants.ACTIVITY_ANIMAL_TAKE_PHOTO).navigation()
                        }
                        TAB_FACE_MANGA -> {
                            //变漫画
                            BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
                                BaseSeq103OperationStatistic.face_fun_a000,
                                "",
                                "6", ""
                            )

                            ARouter.getInstance()
                                .build(RouterConstants.ACTIVITY_ALBUM_TAKE_PHOTO).navigation()
                        }
                    }
                }

                mContext.getString(R.string.home_psy_title)//心里测试
                -> when (position) {
                    0 -> {//测试
                        ARouter.getInstance().build(RouterConstants.ACTIVITY_PSY_LIST).navigation()
                    }
                }
            }
        }
    }

    @Suppress("MISSING_DEPENDENCY_CLASS")
    private fun loadAd(holder: AdHolder, pos: Int) {
        TabBottomAdManager.addAdListener(adTab, object : TabBottomAdManager.OnAdListener {
            override fun getAppLovinAdView(appLovinAdView: AppLovinAdView) {
                appLovinAdView.let {
                    LogUtils.d("CommonTabAdapter", "Banner加载成功 adapter")
                    holder.mContainer.setWrapParent()
                    holder.mRlNative.visibility = View.GONE
                    holder.mIvAd.visibility = View.VISIBLE
                    holder.mNativeContent.addView(it)
//
                    holder.mIvClose.setOnClickListener {
                        mTitles.removeAt(pos)
                        notifyDataSetChanged()
                    }
                }
            }


            override fun getNativeAppInstallAd(ad: NativeAppInstallAd) {
                ad.apply {
                    LogUtils.d("CommonTabAdapter", "NativeAppInstallAd加载成功 adapter")
                    holder.mContainer.setWrapParent()
                    holder.mRlNative.visibility = View.VISIBLE
                    var icon = icon
                    icon?.let {
                        holder.mIvIcon.setImageDrawable(it.drawable)
                    }
                    holder.mTvTitle.text = headline
                    holder.mTvContent.text = body
                    var images = images
                    if (!images.isNullOrEmpty()) {
                        val bitmap = (images[0].drawable as BitmapDrawable).bitmap
                        val w = mContext.resources.getDimensionPixelSize(R.dimen.change_822px)
                        val h = bitmap.height * w / bitmap.width
                        holder.mIvImage.setImageBitmap(bitmap)
                        val lp = holder.mIvImage.layoutParams
                        lp.width = w
                        lp.height = h
                        holder.mIvImage.layoutParams = lp
                    }
                    holder.mIvClose.setOnClickListener {
                        mTitles.removeAt(pos)
                        notifyDataSetChanged()
                    }
                    holder.mTvInstall.text = ad.callToAction
                    holder.mNativeAppInstall.iconView = holder.mIvIcon
                    holder.mNativeAppInstall.imageView = holder.mIvImage
                    holder.mNativeAppInstall.headlineView = holder.mTvTitle
                    holder.mNativeAppInstall.bodyView = holder.mTvContent
                    holder.mNativeAppInstall.setNativeAd(ad)
                    holder.mNativeAppInstall.callToActionView = holder.mTvInstall
                }
            }

            override fun getNativeContentAd(ad: NativeContentAd) {
                ad.apply {
                    LogUtils.d("CommonTabAdapter", "getNativeContentAd加载成功 adapter")
                    holder.mContainer.setWrapParent()
                    holder.mRlNative.visibility = View.VISIBLE
                    var icon = logo
                    icon?.let {
                        holder.mIvIcon.setImageDrawable(it.drawable)
                    }
                    holder.mTvTitle.text = headline
                    holder.mTvContent.text = body
                    var images = images
                    if (!images.isNullOrEmpty()) {
                        val bitmap = (images[0].drawable as BitmapDrawable).bitmap
                        val w = mContext.resources.getDimensionPixelSize(R.dimen.change_822px)
                        val h = bitmap.height * w / bitmap.width
                        holder.mIvImage.setImageBitmap(bitmap)
                        val lp = holder.mIvImage.layoutParams
                        lp.width = w
                        lp.height = h
                        holder.mIvImage.layoutParams = lp
                    }
                    holder.mIvClose.setOnClickListener {
                        mTitles.removeAt(pos)
                        notifyDataSetChanged()
                    }
                    holder.mTvInstall.text = ad.callToAction
                    holder.mNativeContent.logoView = holder.mIvIcon
                    holder.mNativeContent.imageView = holder.mIvImage
                    holder.mNativeContent.headlineView = holder.mTvTitle
                    holder.mNativeContent.bodyView = holder.mTvContent
                    holder.mNativeContent.setNativeAd(ad)
                    holder.mNativeContent.callToActionView = holder.mTvInstall
                }
            }

            override fun getBannerAdView(adView: AdView) {
                adView.let {
                    LogUtils.d("CommonTabAdapter", "Banner加载成功 adapter")
                    holder.mContainer.setWrapParent()
                    holder.mRlNative.visibility = View.GONE
                    holder.mIvAd.visibility = View.VISIBLE
                    holder.mNativeContent.addView(it)
                    val w = holder.mNativeContent.width
                    val h = w * 250 / 300
                    val lp = holder.mNativeContent.layoutParams
                    lp.width = w
                    lp.height = h
                    holder.mNativeContent.layoutParams = lp
//
                    holder.mIvClose.setOnClickListener {
                        mTitles.removeAt(pos)
                        notifyDataSetChanged()
                    }
                }
            }
        })
    }


    class AdHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mContainer: RelativeLayout = itemView.findViewById(R.id.fl_container)
        val mIvIcon: ImageView = itemView.findViewById(R.id.iv_icon)
        val mIvImage: ImageView = itemView.findViewById(R.id.iv_image)
        val mIvClose: ImageView = itemView.findViewById(R.id.iv_close)
        val mTvTitle: TextView = itemView.findViewById(R.id.tv_title)
        val mTvContent: TextView = itemView.findViewById(R.id.tv_content)
        val mTvInstall: TextView = itemView.findViewById(R.id.tv_install)
        val mRlNative: RelativeLayout = itemView.findViewById(R.id.rl_native)
        val mNativeAppInstall: NativeAppInstallAdView =
            itemView.findViewById(R.id.native_appinstall)
        val mNativeContent: NativeContentAdView = itemView.findViewById(R.id.native_content)
        val mIvAd: ImageView = itemView.findViewById(R.id.iv_ad2)
    }

    class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mIvIcon: ImageView = itemView.findViewById(R.id.iv_icon) //图标
        val mTvTitle: TextView = itemView.findViewById(R.id.tv_title) //标题
        val mTvContent: TextView = itemView.findViewById(R.id.tv_content) //内容
        val mTvComming: TextView = itemView.findViewById(R.id.tv_coming) //待定功能标记
        val mTvCountDown: TextView = itemView.findViewById(R.id.tv_count_down)
        val mIvPremium: ImageView = itemView.findViewById(R.id.iv_vip)//vip标记

        fun notifyVipMark() {
            if (BillingServiceProxy.isVip()) {
                mIvPremium.setImageResource(R.mipmap.home_lock_ic_premium)
            } else {
                mIvPremium.setImageResource(R.mipmap.home_lock_ic_lock)
            }

            if (mTvCountDown.visibility == View.VISIBLE) {
                mIvPremium.visibility = View.INVISIBLE
            } else {
                mIvPremium.visibility = View.VISIBLE
            }
        }
    }

    private fun View.setWrapParent() {
        val lp = this.layoutParams
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT
        this.layoutParams = lp
    }

    override fun onFinish() {
        timer?.get()?.text = GoCommonEnv.getApplication().getString(R.string.timer_completed)
    }

    override fun onTick(remainTimeMillis: Long) {
        timer?.get()?.text = formatter?.format(remainTimeMillis)
    }

    override fun onTimerCreate() {
        timer?.get()?.visibility = View.VISIBLE
        if(timer?.get()?.tag != null && timer?.get()?.tag is View) {
            (timer?.get()?.tag as View).visibility = View.INVISIBLE
        }
    }

    override fun onTimerDestroy() {
        timer?.get()?.visibility = View.INVISIBLE
        if(timer?.get()?.tag != null && timer?.get()?.tag is View) {
            (timer?.get()?.tag as View).visibility = View.VISIBLE
        }
    }
}
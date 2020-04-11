package com.palmapp.master.module_psy.heartrate

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.alibaba.android.arouter.launcher.ARouter
import com.cs.bd.commerce.util.LogUtils
import com.palmapp.master.baselib.BaseActivity
import com.palmapp.master.baselib.GoCommonEnv
import com.palmapp.master.baselib.constants.PreConstants
import com.palmapp.master.baselib.constants.RouterConstants
import com.palmapp.master.baselib.event.SubscribeUpdateEvent
import com.palmapp.master.baselib.manager.GoPrefManager
import com.palmapp.master.baselib.manager.PayGuideManager
import com.palmapp.master.baselib.manager.RouterServiceManager.goHomeAndClearTop
import com.palmapp.master.baselib.manager.ThreadExecutorProxy
import com.palmapp.master.baselib.manager.config.ConfigManager
import com.palmapp.master.baselib.manager.config.StarGuideConfig
import com.palmapp.master.baselib.proxy.BillingServiceProxy
import com.palmapp.master.baselib.proxy.OnLoadAdRewardListener
import com.palmapp.master.baselib.statistics.BaseSeq103OperationStatistic
import com.palmapp.master.baselib.utils.TimeUtils
import com.palmapp.master.baselib.view.AdsWatchLeftDialog
import com.palmapp.master.baselib.view.LikeDialog
import com.palmapp.master.baselib.view.LoadingDialog
import com.palmapp.master.module_ad.manager.ExitAdManager
import com.palmapp.master.module_ad.manager.RewardAdManager
import com.palmapp.master.module_psy.R
import kotlinx.android.synthetic.main.psy_activity_heart_rate_report.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.lang.Exception

class HeartRateReportActivity : BaseActivity(), OnLoadAdRewardListener {

    private var newPlan: String = ""
    private var adTimes = RewardAdManager.getConfig()?.show_count ?: 0
    private var dialog: Dialog? = null
    private var loadingDialog: LoadingDialog? = null
    private var isNeedToShow = false

    private var isFirstShow = true
    private var isUnlock = false //视频解锁
    private var isVip = BillingServiceProxy.isVip()


    private var heartrateDatas = ArrayList<Int>()
    private val fragments = ArrayList<Fragment>()

    private var lifeFragment: LifeFragment? = null
    private var loveFragment: LoveFragment? = null


    override fun onVideoFinish() {
        dialog?.dismiss()
        adTimes--
        if (adTimes > 0) {
            showAdWatchDialog()
        } else {
            //已订阅
            isVip = true
            isUnlock = true
            heartrate_blur.visibility = View.GONE
        }
    }

    override fun onLoadSuccess() {
        loadingDialog?.dismiss()
        if (isNeedToShow)
            RewardAdManager.showAd()
    }

    override fun onLoadFail() {
        loadingDialog?.dismiss()
    }

    override fun onLoading() {
        loadingDialog?.show()
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

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
//        outState?.putIntegerArrayList("data", heartrateDatas)
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


    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onResultEvent(data: ArrayList<Int>) {
        heartrateDatas = data
        heartrateDatas.sort()
        EventBus.getDefault().removeStickyEvent(data)
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onNewPlanEvent(data: String) {
        newPlan = data
        EventBus.getDefault().removeStickyEvent(data)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        if (savedInstanceState != null) {
//            savedInstanceState.getIntegerArrayList("data")?.let {
//                heartrateDatas = it
//            }
//        }
        setContentView(R.layout.psy_activity_heart_rate_report)
        BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(BaseSeq103OperationStatistic.heart_page_f000,"","4","")

        EventBus.getDefault().register(this)


        heartrate_blur.setWatchClickListener(View.OnClickListener {
            onClickWatchAd()
        })
        heartrate_blur.visibility = if (BillingServiceProxy.isVip() || RewardAdManager.isFreeUse()) {
            View.GONE
        } else {
            View.VISIBLE
        }

        if (!BillingServiceProxy.isVip() && !RewardAdManager.isFreeUse()) {
            if (newPlan == "1") {
                if (GoCommonEnv.isFirstRunPayGuide) {
                    val config = PayGuideManager.payGuideConfig.get(PayGuideManager.FIRST_LAUNCHER)!!
                    ARouter.getInstance().build(RouterConstants.ACTIVITY_PAY)
                        .withString("entrance", config.scen).withString("type", "12").withString("newPlan", "1").navigation()
                } else {
                    val config = PayGuideManager.payGuideConfig.get(PayGuideManager.NOT_FIRST_LAUNCHER)!!
                    ARouter.getInstance().build(RouterConstants.ACTIVITY_PAY)
                        .withString("entrance", config.scen).withString("type", "12").withString("newPlan", "1").navigation()
                }
            } else {
                ARouter.getInstance().build(RouterConstants.ACTIVITY_PAY)
                    .withString("entrance", "12").withString("type", "12")
                    .navigation(this)
            }
            RewardAdManager.loadAd(this, this)
            ExitAdManager.loadAd(this)
            BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
                BaseSeq103OperationStatistic.show_result_a000,"","7","")
        }
        heartrate_blur.entrance = "12"

        initView()
    }


    private fun initView() {
        lifeFragment = LifeFragment.getInstance()
        loveFragment = LoveFragment.getInstance()
        fragments.add(lifeFragment!!)
        fragments.add(loveFragment!!)

        var birthday = GoCommonEnv.userInfo?.birthday.toString()
        lifeFragment?.setData(birthday)
        loveFragment?.setData(birthday)

        var adapter = ReportPageAdapter(supportFragmentManager)
        viewpager_heartrate.adapter = adapter
        viewpager_heartrate.pageMargin = resources.getDimensionPixelSize(R.dimen.change_96px)
        viewpager_heartrate.currentItem = 0
        tv_life.isSelected = true

        tv_life.setOnClickListener {
            viewpager_heartrate.currentItem = 0
        }
        tv_love.setOnClickListener {
            viewpager_heartrate.currentItem = 1
        }

        findViewById<TextView>(R.id.tv_titlebar_title).text = "Heart Rate Report"
        findViewById<ImageView>(R.id.iv_titlebar_back).setOnClickListener {
            if (isVip) {//订阅
                showScoreGuide()
            } else {
                ExitAdManager.showAd()
                finish()
            }
        }

        viewpager_heartrate.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                tv_life.isSelected = false
                tv_love.isSelected = false
                when (position) {
                    0 -> {
                        tv_life.isSelected = true
                    }

                    1 -> {
                        tv_love.isSelected = true
                    }
                }
            }

        })

        if (heartrateDatas.isNotEmpty()) {
            var counts = 0
            for (i in heartrateDatas) {
                counts += i
            }
            var ave = counts / heartrateDatas.size
            tv_result.text = when (ave) {
                in 50..59 -> {
                    getString(R.string.heartrate_result_health_1)
                }
                in 60..100 -> {
                    getString(R.string.heartrate_result_health_2)
                }
                else -> {
                    getString(R.string.heartrate_result_health_3)
                }
            }
            tv_ave_bpm.text = "$ave"
            tv_heartrate_min.text = " : ${heartrateDatas[0]} bpm"
            tv_heartrate_max.text = " : ${heartrateDatas[heartrateDatas.size - 1]} bpm"
        }
    }


    private inner class ReportPageAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            return fragments[position]
        }

        override fun getCount(): Int {
            return fragments.size
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
        heartrate_blur.visibility = if (event.isVip) {
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

    override fun finish() {
        super.finish()
        goHomeAndClearTop() {
            super.finish()
        }
    }


}

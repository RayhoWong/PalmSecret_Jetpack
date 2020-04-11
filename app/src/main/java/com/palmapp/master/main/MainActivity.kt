package com.palmapp.master.main

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.cs.bd.commerce.util.LogUtils
import com.palmapp.master.R
import com.palmapp.master.baselib.BaseMVPActivity
import com.palmapp.master.baselib.GoCommonEnv
import com.palmapp.master.baselib.bean.FirstVipBean
import com.palmapp.master.baselib.buychannel.BuyChannelProxy
import com.palmapp.master.baselib.constants.PreConstants
import com.palmapp.master.baselib.constants.RouterConstants
import com.palmapp.master.baselib.event.UnlockEvent
import com.palmapp.master.baselib.manager.GoPrefManager
import com.palmapp.master.baselib.manager.PayGuideManager
import com.palmapp.master.baselib.manager.ThreadExecutorProxy
import com.palmapp.master.baselib.manager.config.ConfigManager
import com.palmapp.master.baselib.manager.config.HeartRateDialogConfig
import com.palmapp.master.baselib.manager.config.StarGuideConfig
import com.palmapp.master.baselib.proxy.BillingServiceProxy
import com.palmapp.master.baselib.statistics.BaseSeq101OperationStatistic
import com.palmapp.master.baselib.statistics.BaseSeq103OperationStatistic
import com.palmapp.master.baselib.utils.TimeUtils
import com.palmapp.master.baselib.view.AnimationsContainer
import com.palmapp.master.baselib.view.HeartRateDialog
import com.palmapp.master.baselib.view.LikeDialog
import com.palmapp.master.module_home.HomeFragment
import com.palmapp.master.module_me.MeFragment
import com.palmapp.master.module_pay.BillingManager
import com.palmapp.master.module_today.TodayFragment
import kotlinx.android.synthetic.main.app_activity_launcher.*
import kotlinx.android.synthetic.main.app_activity_main.*
import org.greenrobot.eventbus.EventBus
import java.util.*


@Route(path = RouterConstants.ACTIVITY_APP_MAIN)
class MainActivity : BaseMVPActivity<MainView, MainPresenter>(), MainView, View.OnClickListener {

    override fun createPresenter(): MainPresenter {
        return MainPresenter()
    }

    private lateinit var mHomeFragment: HomeFragment
    private lateinit var mTodayFragment: TodayFragment
    private lateinit var mMeFragment: MeFragment
    private lateinit var mFragment: Fragment //当前显示的fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.app_activity_main)
        BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
            BaseSeq103OperationStatistic.home_page_f000)
        initView()
        initFragment()
        showScoreGuide()
        showHeartRateDialog()
    }

    override fun onDestroy() {
        super.onDestroy()
        GoCommonEnv.stopBGM()
    }

    private fun initView() {
        //获取日期
        val day = Calendar.getInstance().get(Calendar.DATE)
        tv_today_date.text = day.toString()

        iv_tab_home.isSelected = true
        tv_tab_home.isSelected = true
        ll_tab_home.setOnClickListener(this)
        ll_tab_today.setOnClickListener(this)
        ll_tab_me.setOnClickListener(this)
    }


    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.ll_tab_home -> {
                setSelected()
                iv_tab_home.isSelected = true
                tv_tab_home.isSelected = true
                switichFragment(mHomeFragment, resources.getString(R.string.tab_home))
                BaseSeq101OperationStatistic.uploadOperationStatisticData(
                    BaseSeq101OperationStatistic.tab_a000,
                    "",
                    "1"
                )
            }

            R.id.ll_tab_today -> {
                setSelected()
                iv_tab_today.isSelected = true
                tv_tab_today.isSelected = true
                tv_today_date.isSelected = true
                switichFragment(mTodayFragment, resources.getString(R.string.tab_today))
                BaseSeq101OperationStatistic.uploadOperationStatisticData(
                    BaseSeq101OperationStatistic.tab_a000,
                    "",
                    "2"
                )
            }

            R.id.ll_tab_me -> {
                setSelected()
                iv_tab_me.isSelected = true
                tv_tab_me.isSelected = true
                switichFragment(mMeFragment, resources.getString(R.string.tab_me))
                BaseSeq101OperationStatistic.uploadOperationStatisticData(BaseSeq101OperationStatistic.tab_a000,
                    "",
                    "3"
                )
            }
        }
    }


    //好评引导
    private fun showScoreGuide() {
        var isOpen = ConfigManager.getConfig(StarGuideConfig::class.java)?.isOpenGuide
        var isHomeOpen = ConfigManager.getConfig(StarGuideConfig::class.java)?.isHomeGuide
        isOpen?.let {
            if (!it) { //好评总开关是关闭的
                return
            }
        }
        isHomeOpen?.let {
            if (!it) { //好评首页开关是关闭的
                return
            }
        }
        //首次进入app
        var isFirstEnterApp = GoPrefManager.getDefault().getBoolean(PreConstants.ScoreGuide.KEY_IS_FIRST_ENTER_APP, true)
        if (isFirstEnterApp) {
            GoPrefManager.getDefault().putBoolean(PreConstants.ScoreGuide.KEY_IS_FIRST_ENTER_APP, false).commit()
            return
        }
        //好评引导出现的次数
        var guideShowedCount = GoPrefManager.getDefault().getInt(PreConstants.ScoreGuide.KEY_GUIDE_TOTAL_COUNT, 0)
        if (guideShowedCount == 3) return
        //是否出现过
        var isFirstShowed =
            GoPrefManager.getDefault().getBoolean(PreConstants.ScoreGuide.KEY_IS_FIRST_SHOW, false)
        if (!isFirstShowed) {
            ThreadExecutorProxy.runOnMainThread(Runnable {
                BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(BaseSeq103OperationStatistic.rating_f000,
                    "",
                    "4",
                    ""
                )
                var dialog = LikeDialog(this, "home")
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
                            BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(BaseSeq103OperationStatistic.rating_f000,
                                "",
                                "4",
                                ""
                            )
                            var dialog = LikeDialog(this, "home")
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

                    }
                } else {

                }
            }
        } catch (e: Exception) {
            LogUtils.e(e.printStackTrace().toString())
        }
    }

    private fun updateShowVipAnim() {
        val firstPayConfig = GoPrefManager.getDefault()
            .getInt(PreConstants.Pay.KEY_FIRST_PAY_CONFIG, FirstVipBean.TYPE_INIT)
        if (BillingManager.isVip() && (firstPayConfig == FirstVipBean.TYPE_VIP || firstPayConfig ==  FirstVipBean.TYPE_INIT)) { // 已经是vip，不需要首页展示
            GoPrefManager.getDefault()
                .putInt(PreConstants.Pay.KEY_FIRST_PAY_CONFIG, FirstVipBean.TYPE_VIP).apply()
        } else if (BillingManager.isVip() && firstPayConfig != FirstVipBean.TYPE_INIT) { // 已经是vip，未订阅，首页需要展示解锁动画
            GoPrefManager.getDefault()
                .putInt(PreConstants.Pay.KEY_FIRST_PAY_CONFIG, FirstVipBean.TYPE_SHOW_VIP).apply()
        } else {
            GoPrefManager.getDefault()
                .putInt(PreConstants.Pay.KEY_FIRST_PAY_CONFIG, FirstVipBean.TYPE_NOT_VIP).apply()
        }
    }

    override fun onResume() {
        super.onResume()
        updateShowVipAnim()
        if (BillingManager.isVip() && GoPrefManager.getDefault().getInt(PreConstants.Pay.KEY_FIRST_PAY_CONFIG, FirstVipBean.TYPE_INIT) == FirstVipBean.TYPE_SHOW_VIP) {
            if (layout_locker != null) {
                val layout = layout_locker.inflate()
                val iv = layout.findViewById<ImageView>(R.id.iv)
                val anims = IntArray(34)
                for (i in 11..44) {
                    // 获取Drawable文件夹下的图片文件
                    val id = resources.getIdentifier("lock_000$i", "mipmap", packageName)
                    anims[i - 11] = id
                }

                val animation = AnimationsContainer.getInstance().createAnim(iv, 40, anims)
                animation.registerCallback {
                    layout.visibility = View.GONE
                }
                animation.start()
                EventBus.getDefault().post(UnlockEvent())
            }
            GoPrefManager.getDefault()
                .putInt(PreConstants.Pay.KEY_FIRST_PAY_CONFIG, FirstVipBean.TYPE_VIP).apply()
        }
    }

    private fun showHeartRateDialog() {
        var isOpen = ConfigManager.getConfig(HeartRateDialogConfig::class.java)?.isOpen
        isOpen?.let {
            if (!it) {
                return
            }
        }
        //首次进入app
        var isFirstEnterApp = GoPrefManager.getDefault().getBoolean(PreConstants.HeartRateDialog.KEY_IS_FIRST_HEARTRATE_ENTER_APP, true)
        if (isFirstEnterApp) {
            BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(BaseSeq103OperationStatistic.heart_rate_f000)
            var dialog = HeartRateDialog(this)
            dialog.show()
            GoPrefManager.getDefault().putBoolean(PreConstants.HeartRateDialog.KEY_IS_FIRST_HEARTRATE_ENTER_APP, false).commit()
        }
    }

    /**
     * 设置控件的选中状态
     */
    private fun setSelected() {
        iv_tab_home.isSelected = false
        tv_tab_home.isSelected = false
        iv_tab_today.isSelected = false
        tv_tab_today.isSelected = false
        tv_today_date.isSelected = false
        iv_tab_me.isSelected = false
        tv_tab_me.isSelected = false
    }


    private fun initFragment() {
        var fm = supportFragmentManager
        var transaction = fm.beginTransaction()
        mHomeFragment = HomeFragment.newInstance()
        mTodayFragment = TodayFragment.newInstance()
        mMeFragment = MeFragment.newInstance()
        //默认显示首页
        transaction.add(R.id.fl_fragment, mHomeFragment)
        transaction.commit()
        mFragment = mHomeFragment
    }


    private fun switichFragment(fragment: Fragment, tag: String) {
        var fm = supportFragmentManager
        var transaction = fm.beginTransaction()
        if (mFragment != fragment) {
            if (!fragment.isAdded) {
                transaction.hide(mFragment)
                transaction.add(R.id.fl_fragment, fragment, tag)
                transaction.commit()
            } else {
                transaction.hide(mFragment)
                transaction.show(fragment)
                transaction.commit()
            }
            mFragment = fragment
        }
    }

}
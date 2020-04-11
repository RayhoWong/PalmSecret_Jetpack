package com.palmapp.master.module_palm.test

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.alibaba.android.arouter.launcher.ARouter
import com.cs.bd.commerce.util.LogUtils
import com.palmapp.master.baselib.BaseActivity
import com.palmapp.master.baselib.bean.RESPONSE_SUCCESS
import com.palmapp.master.baselib.bean.quiz.*
import com.palmapp.master.baselib.constants.AppConstants
import com.palmapp.master.baselib.constants.PreConstants
import com.palmapp.master.baselib.constants.RouterConstants
import com.palmapp.master.baselib.event.SubscribeUpdateEvent
import com.palmapp.master.baselib.manager.GoPrefManager
import com.palmapp.master.baselib.manager.RouterServiceManager
import com.palmapp.master.baselib.manager.RouterServiceManager.goHomeAndClearTop
import com.palmapp.master.baselib.manager.ThreadExecutorProxy
import com.palmapp.master.baselib.manager.config.ConfigManager
import com.palmapp.master.baselib.manager.config.StarGuideConfig
import com.palmapp.master.baselib.proxy.BillingServiceProxy
import com.palmapp.master.baselib.proxy.OnLoadAdRewardListener
import com.palmapp.master.baselib.statistics.BaseSeq101OperationStatistic
import com.palmapp.master.baselib.statistics.BaseSeq103OperationStatistic
import com.palmapp.master.baselib.utils.NetworkUtil
import com.palmapp.master.baselib.utils.TimeUtils
import com.palmapp.master.baselib.view.AdsWatchDialog
import com.palmapp.master.baselib.view.AdsWatchLeftDialog
import com.palmapp.master.baselib.view.LikeDialog
import com.palmapp.master.baselib.view.LoadingDialog
import com.palmapp.master.module_ad.manager.ExitAdManager
import com.palmapp.master.module_ad.manager.RewardAdManager
import com.palmapp.master.module_network.HttpClient
import com.palmapp.master.module_network.NetworkSubscriber
import com.palmapp.master.module_network.NetworkTransformer
import com.palmapp.master.module_palm.R
import kotlinx.android.synthetic.main.palm_activity_palmprint_judg.*
import kotlinx.android.synthetic.main.palm_activity_palmprint_judg.lt_title_bar
import kotlinx.android.synthetic.main.palm_activity_palmprint_judg.tv_title
import kotlinx.android.synthetic.main.palm_activity_palmprint_judg_result.*
import kotlinx.android.synthetic.main.palm_activity_palmprint_judg_result.palm_loading
import kotlinx.android.synthetic.main.palm_activity_palmprint_judg_result.rcv
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.lang.Exception
import java.lang.ref.WeakReference
import kotlin.random.Random

/**
 * 手相问答结果
 */
class PalmprintJudgResultActivity : BaseActivity(), OnLoadAdRewardListener {
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
            ll_result.visibility = View.VISIBLE
            ll_result_blur.visibility = View.GONE
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

    private var mOptionName: String? = null //选项名
    private var mOptionAnswer: String? = null //选项描述
    private var mQuestionId: String? = null //问题id

    private lateinit var mAdapter: PalmprintJudgAdapter
    private lateinit var mData: MutableList<QuestionDTO>


    //接受某个选项的名称 from PalmprintJudgDetailActivity
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onOptionEvent(event1: OptionDTO) {
        mOptionName = event1.description
    }

    //接受某个选项的答案 from PalmprintJudgDetailActivity
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onAnswerEvent(event2: QuziAnswerDTO) {
        mOptionAnswer = event2.description
    }

    //接受某个问题的id from PalmprintJudgDetailActivity
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onQuestionIdEvent(id: String) {
        if (!TextUtils.isEmpty(id)) {
            mQuestionId = id
        }
    }

    //接受订阅的处理结果
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSubscribeUpdateEvent(event: SubscribeUpdateEvent) {
        if (event.isVip) {//订阅成功
            //已订阅
            isVip = true
            isUnlock = true
            ll_result.visibility = View.VISIBLE
            ll_result_blur.visibility = View.GONE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        RewardAdManager.addFreeUse()
        isVip = BillingServiceProxy.isVip() || RewardAdManager.isFreeUse()
        setContentView(R.layout.palm_activity_palmprint_judg_result)
        //注册事件
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }

        if (!BillingServiceProxy.isVip() && !RewardAdManager.isFreeUse()) {
            ExitAdManager.loadAd(this)
            //非订阅
            ARouter.getInstance().build(RouterConstants.ACTIVITY_PAY).withString("entrance", "0")
                .withBoolean("watchAd", true)
                .navigation(this)
            RewardAdManager.loadAd(this, this)
        }

        initView()
        getQuizId()
    }

    private fun initView() {
        var mTvTitle = lt_title_bar.findViewById<TextView>(R.id.tv_titlebar_title)
        var mIvBack = lt_title_bar.findViewById<ImageView>(R.id.iv_titlebar_back)
        mTvTitle.text = getString(R.string.psy_result)
        tv_title.text = mOptionName
        tv_result.text = mOptionAnswer

        mIvBack.setOnClickListener {
            backHandle()
        }

        rcv.layoutManager = GridLayoutManager(this, 2)
        tv_pay.setOnClickListener {
            ARouter.getInstance().build(RouterConstants.ACTIVITY_PAY).withString("entrance", "0")
                .navigation()
        }
        loadingDialog = LoadingDialog(this)
        btn_pay_watch.setOnClickListener {
            onClickWatchAd()
            BaseSeq101OperationStatistic.uploadOperationStatisticData(
                BaseSeq101OperationStatistic.ad_channel_a000, "", "1"
            )
        }
        btn_pay_watch.visibility = if (RewardAdManager.isLoadAd()) View.VISIBLE else View.GONE
        if (RewardAdManager.isLoadAd()) {
            BaseSeq101OperationStatistic.uploadOperationStatisticData(
                BaseSeq101OperationStatistic.ad_channel_f000, "", "1"
            )
        }
        if (BillingServiceProxy.isVip() || RewardAdManager.isFreeUse()) {
            //已订阅
            ll_result.visibility = View.VISIBLE
            ll_result_blur.visibility = View.GONE
        } else {
            //非订阅
            ll_result.visibility = View.GONE
            ll_result_blur.visibility = View.VISIBLE
        }
    }


    //获取手相问答的问题id
    private fun getQuizId() {
        //请求消息体
        val body = QuizListRequest()
        body.category_id = QuizListRequest.PSY_CATEGORY_ID
        body.accept_type = listOf("ACCORDING_SCORE", "ACCORDING_OPTION", "JUMP", "COMBINATION")
        if (NetworkUtil.isConnected(this)) {
            HttpClient
                .getFaceRequest()
                .getQuizList(body = body)
                .compose(NetworkTransformer.toMainSchedulers<QuizListResponse>())
                .subscribe(object : NetworkSubscriber<QuizListResponse>() {
                    override fun onNext(t: QuizListResponse) {
                        if (t.status_result?.status_code.equals(RESPONSE_SUCCESS) && t.quizzes!!.isNotEmpty()) {
                            for (item in t.quizzes!!) {
                                //选项类型是ACCORDING_OPTION
                                if (item.type == QuizListRequest.ACCORDING_OPTION) {
                                    getQuizContent(item.quiz_id)
                                }
                            }
                        } else {
                            Toast.makeText(
                                this@PalmprintJudgResultActivity,
                                getString(R.string.net_server_error),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                })
        } else {
            Toast.makeText(
                this@PalmprintJudgResultActivity, getString(R.string.net_error), Toast.LENGTH_SHORT
            ).show()
        }
    }


    /**
     * 测试题内容获取
     * @id 测试题id
     */
    private fun getQuizContent(id: String) {
        palm_loading.visibility = View.VISIBLE
        palm_loading.showNetworkLoadingView()
        var request = QuizContentRequest()
        request.quiz_id = id
        if (NetworkUtil.isConnected(this)) {
            HttpClient
                .getFaceRequest()
                .getQuizContent(request)
                .compose(NetworkTransformer.toMainSchedulers<QuizContentResponse>())
                .subscribe(object : NetworkSubscriber<QuizContentResponse>() {
                    override fun onNext(t: QuizContentResponse) {
                        palm_loading.hideAllView()
                        if (t.status_result?.status_code.equals(RESPONSE_SUCCESS) && t.quiz != null) {
                            if (t.quiz!!.questions.isNotEmpty()) {
                                mData = t.quiz!!.questions as MutableList<QuestionDTO>
                                //删除集合中与当前问题重复的问题item
                                var iterator = mData.iterator()
                                while (iterator.hasNext()) {
                                    if (iterator.next().question_id.equals(mQuestionId)) {
                                        iterator.remove()
                                    }
                                }
                                //随机删除一个问题item 保证问题数不超过4
                                if (mData.size > 4) {
                                    mData.remove(mData[Random.nextInt(mData.size)])
                                }
                                mAdapter =
                                    PalmprintJudgAdapter(this@PalmprintJudgResultActivity, mData)
                                rcv.adapter = mAdapter
                            }
                        } else {
                            Toast.makeText(
                                this@PalmprintJudgResultActivity,
                                getString(R.string.net_server_error),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                })
        } else {
            palm_loading.hideAllView()
            Toast.makeText(
                this@PalmprintJudgResultActivity, getString(R.string.net_error), Toast.LENGTH_SHORT
            ).show()
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
                    "8",
                    ""
                )
                var dialog = LikeDialog(this, "judge")
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
                                "8",
                                ""
                            )
                            var dialog = LikeDialog(this, "judge")
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


    //返回处理
    private fun backHandle() {
        goHomeAndClearTop()
        if (isVip) {//订阅
            showScoreGuide()
        } else {
            ExitAdManager.showAd()
            finish()
        }
    }


    override fun onBackPressed() {
        backHandle()
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }
}

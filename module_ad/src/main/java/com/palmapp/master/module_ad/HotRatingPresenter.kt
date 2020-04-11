package com.palmapp.master.module_ad

import android.app.Activity
import android.content.Context
import com.cs.bd.commerce.util.LogUtils
import com.palmapp.master.baselib.BaseMultiplePresenter
import com.palmapp.master.baselib.constants.PreConstants
import com.palmapp.master.baselib.manager.GoPrefManager
import com.palmapp.master.baselib.manager.ThreadExecutorProxy
import com.palmapp.master.baselib.manager.config.ConfigManager
import com.palmapp.master.baselib.manager.config.StarGuideConfig
import com.palmapp.master.baselib.statistics.BaseSeq103OperationStatistic
import com.palmapp.master.baselib.utils.TimeUtils
import com.palmapp.master.baselib.view.LikeDialog
import java.lang.Exception

/**
 *
 * @author :     xiemingrui
 * @since :      2020/3/12
 */
class HotRatingPresenter(private val activity: Activity, private val presenter: RewardVideoPresenter) : BaseMultiplePresenter<IHotRatingView>() {
    override fun onAttach(pView: IHotRatingView, context: Context) {
        super.onAttach(pView, context)
    }
    //肯定会拦截onBackPressed事件
    override fun onBackPressedEvent(): Boolean {
        if (presenter.isCanUseFun) {
            showScoreGuide()
        }else{
            showAdWithoutGuide()
        }
        return true
    }

    fun showLikeDialog(){
        var isLiked = GoPrefManager.getDefault()
            .getBoolean(PreConstants.ScoreGuide.KEY_IS_FIRST_LIKE, false)
        if (!isLiked) {
            BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
                BaseSeq103OperationStatistic.rating_f000,
                "",
                "5",
                ""
            )
            var dialog = LikeDialog(activity, "like")
            dialog.show()
            GoPrefManager.getDefault()
                .putBoolean(PreConstants.ScoreGuide.KEY_IS_FIRST_LIKE, true).commit()
        }
    }

    //好评引导
    private fun showScoreGuide() {
        var isOpen = ConfigManager.getConfig(StarGuideConfig::class.java)?.isOpenGuide
        var isResultOpen = ConfigManager.getConfig(StarGuideConfig::class.java)?.isResultGuide
        isOpen?.let {
            if (!it) { //好评总开关是关闭的
                showAdWithoutGuide()
                return
            }
        }
        isResultOpen?.let {
            if (!it) { //好评结果页开关是关闭的
                showAdWithoutGuide()
                return
            }
        }
        //好评引导出现的次数
        var guideShowedCount =
            GoPrefManager.getDefault().getInt(PreConstants.ScoreGuide.KEY_GUIDE_TOTAL_COUNT, 0)
        if (guideShowedCount == 3) {
            showAdWithoutGuide()
            return
        }

        var isFirstShowed =
            GoPrefManager.getDefault().getBoolean(PreConstants.ScoreGuide.KEY_IS_FIRST_SHOW, false)
        if (!isFirstShowed) {
            ThreadExecutorProxy.runOnMainThread(Runnable {
                BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
                    BaseSeq103OperationStatistic.rating_f000,
                    "",
                    "6",
                    ""
                )
                var dialog = LikeDialog(activity, "palm")
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
                                "6",
                                ""
                            )
                            var dialog = LikeDialog(activity, "palm")
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
                    }
                } else {
                    showAdWithoutGuide()
                }
            }
        } catch (e: Exception) {
            LogUtils.e(e.printStackTrace().toString())
        }
    }

    private fun showAdWithoutGuide() {
        getView()?.withoutHotRating()
    }
}
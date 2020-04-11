package com.palmapp.master.baselib.view

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import com.alibaba.android.arouter.launcher.ARouter
import com.cs.bd.commerce.util.statistics.Base101Statistic
import com.palmapp.master.baselib.R
import com.palmapp.master.baselib.constants.PreConstants
import com.palmapp.master.baselib.constants.RouterConstants
import com.palmapp.master.baselib.manager.GoPrefManager
import com.palmapp.master.baselib.statistics.BaseSeq101OperationStatistic
import com.palmapp.master.baselib.statistics.BaseSeq103OperationStatistic
import com.palmapp.master.baselib.utils.LogUtil
import kotlinx.android.synthetic.main.layout_like_dialog.*

/**
 * Created by huangweihao on 2019/12/30.
 * 好评dialog
 */
class LikeDialog(context: Context, private val tab: String) :
    Dialog(context, R.style.CommonDialog) {

    private var mContext: Context? = context


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_like_dialog)
        initView()
    }


    private fun initView() {
        window.setBackgroundDrawableResource(R.color.color_cc000000)
        val lp = window.attributes
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.MATCH_PARENT
        lp.gravity = Gravity.CENTER
        window.attributes = lp
        setCancelable(false)
        setCanceledOnTouchOutside(false)

        tv_no.setOnClickListener {
            when (tab) {
                "home" -> {
                    BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
                        BaseSeq103OperationStatistic.rating_a000,
                        "",
                        "2",
                        "4"
                    )
                }
                "old" -> {
                    BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
                        BaseSeq103OperationStatistic.rating_a000,
                        "",
                        "2",
                        "1"
                    )
                }
                "baby" -> {
                    BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
                        BaseSeq103OperationStatistic.rating_a000,
                        "",
                        "2",
                        "2"
                    )
                }
                "transform" -> {
                    BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
                        BaseSeq103OperationStatistic.rating_a000,
                        "",
                        "2",
                        "3"
                    )
                }
                "like" -> {
                    BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
                        BaseSeq103OperationStatistic.rating_a000,
                        "",
                        "2",
                        "5"
                    )
                }
                "palm" -> {
                    BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
                        BaseSeq103OperationStatistic.rating_a000,
                        "",
                        "2",
                        "6"
                    )
                }
                "palm_match" -> {
                    BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
                        BaseSeq103OperationStatistic.rating_a000,
                        "",
                        "2",
                        "7"
                    )
                }
                "palm_judge" -> {
                    BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
                        BaseSeq103OperationStatistic.rating_a000,
                        "",
                        "2",
                        "8"
                    )
                }
                "cnt_match" -> {
                    BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
                        BaseSeq103OperationStatistic.rating_a000,
                        "",
                        "2",
                        "9"
                    )
                }
            }
            toFeedback()
        }

        tv_yes.setOnClickListener {
            when (tab) {
                "home" -> {
                    BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
                        BaseSeq103OperationStatistic.rating_a000,
                        "",
                        "1",
                        "4"
                    )
                }
                "old" -> {
                    BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
                        BaseSeq103OperationStatistic.rating_a000,
                        "",
                        "1",
                        "1"
                    )
                }
                "baby" -> {
                    BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
                        BaseSeq103OperationStatistic.rating_a000,
                        "",
                        "1",
                        "2"
                    )
                }
                "transform" -> {
                    BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
                        BaseSeq103OperationStatistic.rating_a000,
                        "",
                        "1",
                        "3"
                    )
                }
                "like" -> {
                    BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
                        BaseSeq103OperationStatistic.rating_a000,
                        "",
                        "1",
                        "5"
                    )
                }
                "palm" -> {
                    BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
                        BaseSeq103OperationStatistic.rating_a000,
                        "",
                        "1",
                        "6"
                    )
                }
                "palm_match" -> {
                    BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
                        BaseSeq103OperationStatistic.rating_a000,
                        "",
                        "1",
                        "7"
                    )
                }
                "palm_judge" -> {
                    BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
                        BaseSeq103OperationStatistic.rating_a000,
                        "",
                        "1",
                        "8"
                    )
                }
                "cnt_match" -> {
                    BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
                        BaseSeq103OperationStatistic.rating_a000,
                        "",
                        "1",
                        "9"
                    )
                }
            }
            toScore()
        }
    }


    //跳转gp的应用的详情页
    private fun toScore() {
        try {
            val googlePlay = "com.android.vending"
            val uri = Uri.parse("market://details?id=" + "com.palmsecret.horoscope")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.setPackage(googlePlay)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            if (intent.resolveActivity(mContext?.packageManager) != null) {
                GoPrefManager.getDefault().putBoolean(PreConstants.ScoreGuide.KEY_IS_SCORED, true)
                    .commit()
                mContext?.startActivity(intent)
            } else {//没有应用市场，通过浏览器跳转到Google Play
                val intent2 = Intent(Intent.ACTION_VIEW)
                intent2.data =
                    Uri.parse("https://play.google.com/store/apps/details?id=" + "com.palmsecret.horoscope")
                if (intent2.resolveActivity(mContext?.packageManager) != null) {
                    GoPrefManager.getDefault()
                        .putBoolean(PreConstants.ScoreGuide.KEY_IS_SCORED, true).commit()
                    mContext?.startActivity(intent2)
                } else {
                    //没有Google Play 也没有浏览器
                }
            }
        } catch (e: Exception) {
            LogUtil.e(e.printStackTrace().toString())
        } finally {
            dismiss()
        }
    }


    //跳转反馈页面
    private fun toFeedback() {
        ARouter.getInstance().build(RouterConstants.ACTIVITY_MEFeedbackActivity).navigation()
        dismiss()
    }

}
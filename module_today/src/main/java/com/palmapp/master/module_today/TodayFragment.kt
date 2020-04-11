package com.palmapp.master.module_today

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.palmapp.master.baselib.BaseFragment
import com.palmapp.master.baselib.GoCommonEnv
import com.palmapp.master.baselib.bean.pay.ModuleConfig
import com.palmapp.master.baselib.bean.tarot.DailyTarotBean
import com.palmapp.master.baselib.bean.user.getCntCover
import com.palmapp.master.baselib.bean.user.getConstellationById
import com.palmapp.master.baselib.constants.AppConstants
import com.palmapp.master.baselib.manager.DailyTarotManager
import com.palmapp.master.baselib.manager.ShareManager
import com.palmapp.master.baselib.statistics.BaseSeq103OperationStatistic
import com.palmapp.master.baselib.utils.StatusBarUtil
import com.palmapp.master.baselib.view.FlipImageView
import com.palmapp.master.module_imageloader.ImageLoaderUtils
import kotlinx.android.synthetic.main.today_fragment_today.*

/**
 *
 * @author :     xiemingrui
 * @since :      2019/7/31
 * 今日
 */
class TodayFragment : BaseFragment<TodayView, TodayPresenter>(), TodayView {

    private var isFirst = true
    private fun showTarot(index: String, c: String) {
        var title: TextView? = null
        var des: TextView? = null

        when (index) {
            AppConstants.TAROT_CARD_TYPE_LOVE -> {
                title = tv_today_result_title1
                des = tv_today_result_content1
            }
            AppConstants.TAROT_CARD_TYPE_FORTUNE -> {
                title = tv_today_result_title2
                des = tv_today_result_content2
            }
            AppConstants.TAROT_CARD_TYPE_CAREER -> {
                title = tv_today_result_title3
                des = tv_today_result_content3
            }
        }
        title?.let {
            it.visibility = View.VISIBLE
            it.text = when (index) {
                AppConstants.TAROT_CARD_TYPE_LOVE -> getString(R.string.tarot_love)
                AppConstants.TAROT_CARD_TYPE_FORTUNE -> getString(R.string.tarot_fortune)
                AppConstants.TAROT_CARD_TYPE_CAREER -> getString(R.string.tarot_business)
                else -> ""
            }
        }

        des?.let {
            it.visibility = View.VISIBLE
            it.text = c
        }
    }

    override fun showTarotResult() {
        if (DailyTarotManager.cacheTarot.size != 3)
            return
        val love = DailyTarotManager.cacheTarot.get(0)
        val future = DailyTarotManager.cacheTarot.get(1)
        val carrier = DailyTarotManager.cacheTarot.get(2)

        if (!love.isFlip || !future.isFlip || !carrier.isFlip) {
            arrayOf(
                tv_today_result_title1,
                tv_today_result_title2,
                tv_today_result_title3,
                tv_today_result_content1,
                tv_today_result_title3,
                tv_today_result_content2,
                tv_today_result_title3,
                tv_today_result_content3
            ).forEach {
                it.visibility = View.GONE
            }
            return
        }

        arrayOf(love, future, carrier).forEach {
            showTarot(it.type, it.result)
        }
    }

    override fun showTarot(tarots: List<DailyTarotBean>) {
        if (tarots.size != 3)
            return
        iv_tarot1.reset()
        iv_tarot2.reset()
        iv_tarot3.reset()
        if (tarots.get(0).isFlip) {
            iv_tarot1.initFlipped()
        }
        if (tarots.get(1).isFlip) {
            iv_tarot2.initFlipped()
        }
        if (tarots.get(2).isFlip) {
            iv_tarot3.initFlipped()
        }
        iv_tarot1.setFlippedDrawable(resources.getDrawable(tarots.get(0).cover))
        iv_tarot2.setFlippedDrawable(resources.getDrawable(tarots.get(1).cover))
        iv_tarot3.setFlippedDrawable(resources.getDrawable(tarots.get(2).cover))
        val listener = object : FlipImageView.OnFlipListener {
            override fun onClick(view: FlipImageView?) {
                when (view) {
                    iv_tarot1 -> mPresenter?.showDialog(tarots.get(0).card_key)
                    iv_tarot2 -> mPresenter?.showDialog(tarots.get(1).card_key)
                    iv_tarot3 -> mPresenter?.showDialog(tarots.get(2).card_key)
                }
            }

            override fun onFlipStart(view: FlipImageView?) {
                when (view) {
                    iv_tarot1 -> mPresenter?.flipTarot(tarots.get(0).card_key)
                    iv_tarot2 -> mPresenter?.flipTarot(tarots.get(1).card_key)
                    iv_tarot3 -> mPresenter?.flipTarot(tarots.get(2).card_key)
                }
            }

            override fun onFlipEnd(view: FlipImageView?) {
            }
        }
        iv_tarot1.setOnFlipListener(listener)
        iv_tarot2.setOnFlipListener(listener)
        iv_tarot3.setOnFlipListener(listener)
    }

    override fun showQuote(config: ModuleConfig) {
        ImageLoaderUtils.displayImage(config.banner, iv_today_content, R.mipmap.placeholder_big)
        tv_today_content.text = config.extra
        tv_today_artist.text = config.name
    }

    override fun showLuckyColor(colors: IntArray, w: String) {
        iv_today_color1.setColorFilter(colors[0])
        iv_today_color2.setColorFilter(colors[1])
        iv_today_color3.setColorFilter(colors[2])
        tv_today_color_des.text = getString(R.string.today_daily_wear, w)
    }

    override fun showLuckyNum(num: Int) {
        tv_today_number.text = num.toString()
    }

    override fun createPresenter(): TodayPresenter {
        return TodayPresenter()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.today_fragment_today, container, false)
        StatusBarUtil.setStatusBarHeight(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mPresenter?.loadData()
        iv_today_share.setOnClickListener {
            BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
                BaseSeq103OperationStatistic.share_a000,
                "",
                "12",
                ""
            )
            val view = LayoutInflater.from(activity).inflate(R.layout.today_layout_share_daily, null, false)
            if (TextUtils.isEmpty(GoCommonEnv.userInfo?.name ?: "")) {
                view.findViewById<View>(R.id.layout_header).visibility = View.GONE
            }
            view.findViewById<ImageView>(R.id.iv_me)
                .setImageResource(getCntCover(GoCommonEnv.userInfo?.constellation ?: 1))
            view.findViewById<TextView>(R.id.tv_user_name).text = GoCommonEnv.userInfo?.name ?: ""
            view.findViewById<TextView>(R.id.tv_user_cnt).text =
                getConstellationById(GoCommonEnv.userInfo?.constellation ?: 1)
            view.findViewById<ImageView>(R.id.iv_today_content).setImageDrawable(iv_today_content.drawable)
            view.findViewById<TextView>(R.id.tv_today_content).text = tv_today_content.text
            view.findViewById<TextView>(R.id.tv_today_artist).text = tv_today_artist.text
            view.findViewById<ImageView>(R.id.iv_today_color1).colorFilter = iv_today_color2.colorFilter
            view.findViewById<ImageView>(R.id.iv_today_color2).colorFilter = iv_today_color2.colorFilter
            view.findViewById<ImageView>(R.id.iv_today_color3).colorFilter = iv_today_color2.colorFilter
            view.findViewById<TextView>(R.id.tv_today_color_des).text = tv_today_color_des.text
            view.findViewById<TextView>(R.id.tv_today_number).text = tv_today_number.text

            if (tv_today_result_title1.visibility == View.VISIBLE) {
                view.findViewById<TextView>(R.id.tv_today_result_title1).text = tv_today_result_title1.text
                view.findViewById<TextView>(R.id.tv_today_result_content1).text = tv_today_result_content1.text
                view.findViewById<TextView>(R.id.tv_today_result_title2).text = tv_today_result_title2.text
                view.findViewById<TextView>(R.id.tv_today_result_content2).text = tv_today_result_content2.text
                view.findViewById<TextView>(R.id.tv_today_result_title3).text = tv_today_result_title3.text
                view.findViewById<TextView>(R.id.tv_today_result_content3).text = tv_today_result_content3.text
                view.findViewById<ImageView>(R.id.iv_tarot1).setImageDrawable(iv_tarot1.drawable)
                view.findViewById<ImageView>(R.id.iv_tarot2).setImageDrawable(iv_tarot2.drawable)
                view.findViewById<ImageView>(R.id.iv_tarot3).setImageDrawable(iv_tarot3.drawable)
                view.findViewById<View>(R.id.tv_today_tarot_title).visibility = View.VISIBLE
                view.findViewById<View>(R.id.layout_today_tarot_des).visibility = View.VISIBLE
                view.findViewById<View>(R.id.layout_today_tarot).visibility = View.VISIBLE
            }

            ShareManager.share(view, getView()?.getContext()?.getString(R.string.app_app_name)?:"")
        }
    }

    companion object {
        fun newInstance(): TodayFragment {
            return TodayFragment()
        }
    }

    override fun onResume() {
        super.onResume()
        if (isFirst) {
            isFirst = false
            return
        }
        showTarot(DailyTarotManager.cacheTarot)
        showTarotResult()
    }

}
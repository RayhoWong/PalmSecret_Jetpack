package com.palmapp.master.module_cnt.daily;

import android.content.Intent
import com.palmapp.master.baselib.BaseMVPActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.cs.bd.commerce.util.statistics.Base101Statistic
import com.palmapp.master.baselib.GoCommonEnv
import com.palmapp.master.baselib.Product
import com.palmapp.master.baselib.bean.cnt.*
import com.palmapp.master.baselib.bean.user.UserInfo
import com.palmapp.master.baselib.constants.RouterConstants
import com.palmapp.master.baselib.event.SubscribeUpdateEvent
import com.palmapp.master.baselib.manager.AbTestUserManager
import com.palmapp.master.baselib.proxy.BillingServiceProxy
import com.palmapp.master.baselib.proxy.OnBillingCallBack
import com.palmapp.master.baselib.statistics.BaseSeq101OperationStatistic
import com.palmapp.master.baselib.statistics.BaseSeq103OperationStatistic
import com.palmapp.master.module_cnt.R
import kotlinx.android.synthetic.main.cnt_activity_daily.*
import kotlinx.android.synthetic.main.cnt_activity_daily.network
import kotlinx.android.synthetic.main.cnt_activity_matching.*
import kotlinx.android.synthetic.main.cnt_layout_daily_pay.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.lang.Exception

@Route(path = RouterConstants.ACTIVITY_CNT_DAILY)
class CntDailyActivity : BaseMVPActivity<CntDailyView, CntDailyPresenter>(), CntDailyView {
    private var id = 0
    private var payView: View? = null
    override fun showNetworkError() {
        network.showNetworkErrorView { mPresenter?.loadData(id) }
    }

    override fun showServerError() {
        network.showServerErrorView { mPresenter?.loadData(id) }
    }

    override fun showLoading() {
        network.showNetworkLoadingView()
    }

    private val mapString = HashMap<String, String>()

    //展示中间信息
    override fun showForecast(list: ArrayList<ForecastResponse>) {
        network.hideAllView()
        viewpager_cntdaily.adapter = PAdapter(list)
        tv_cntdaily_today.setOnClickListener {
            viewpager_cntdaily.currentItem = 0
        }
        tv_cntdaily_tomorrow.setOnClickListener {
            viewpager_cntdaily.currentItem = 1
        }
        tv_cntdaily_future.setOnClickListener {
            viewpager_cntdaily.currentItem = 2
        }
        viewpager_cntdaily.currentItem = 0
        tv_cntdaily_today.isSelected = true
        tv_cntdaily_tomorrow.isSelected = false
        tv_cntdaily_future.isSelected = false
    }

    //展示头部信息
    override fun showHeader(name: String, birth: String, cover: Int) {
        tv_cntdaily_title.text = name
        tv_cntdaily_date.text = birth
        iv_cntdaily_cnt.setImageResource(cover)
    }

    override fun createPresenter(): CntDailyPresenter {
        return CntDailyPresenter()
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSubscribeUpdateEvent(event: SubscribeUpdateEvent) {
        if (event.isVip) {
            layout_content.visibility = View.GONE
            viewpager_cntdaily.visibility = View.VISIBLE
            payView?.visibility = View.GONE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.cnt_activity_daily)
        if (GoCommonEnv.userInfo?.constellation ?: -1 == -1) {
            val intent = Intent(CntDailyActivity@ this, CntDailySelectActivity::class.java)
            startActivityForResult(intent, 2)
        }
        mapString.put(DAY, getString(R.string.cnt_daily_today))
        mapString.put(TOMORROW, getString(R.string.cnt_daily_tomorrow))
        mapString.put(WEEK, getString(R.string.cnt_daily_week))
        mapString.put(MONTH, getString(R.string.cnt_daily_month))
        mapString.put(YEAR, getString(R.string.cnt_daily_year))
        findViewById<View>(R.id.iv_titlebar_back).setOnClickListener { finish() }
        findViewById<TextView>(R.id.tv_titlebar_title).text = getString(R.string.cnt_daily_title)
        EventBus.getDefault().register(this)
        viewpager_cntdaily.pageMargin = resources.getDimensionPixelSize(R.dimen.change_96px)
        network.bindView(viewpager_cntdaily)
        id = GoCommonEnv.userInfo?.constellation ?: -1
        mPresenter?.loadData(id)
        tv_cntdaily_more.setOnClickListener {
            BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
                BaseSeq103OperationStatistic.horoscope_ever_a000,
                "",
                "4",""
            )
            val intent = Intent(CntDailyActivity@ this, CntDailySelectActivity::class.java)
            startActivityForResult(intent, 1)
        }
        viewpager_cntdaily.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                tv_cntdaily_today.isSelected = false
                tv_cntdaily_tomorrow.isSelected = false
                tv_cntdaily_future.isSelected = false
                when (position) {
                    0 -> {
                        tv_cntdaily_today.isSelected = true
                        BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
                            BaseSeq103OperationStatistic.horoscope_ever_a000,
                            "",
                            "1",""
                        )
                    }
                    1 -> {
                        tv_cntdaily_tomorrow.isSelected = true
                        BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
                            BaseSeq103OperationStatistic.horoscope_ever_a000,
                            "",
                            "2",""
                        )
                    }
                    2 -> {
                        tv_cntdaily_future.isSelected = true
                        BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
                            BaseSeq103OperationStatistic.horoscope_ever_a000,
                            "",
                            "3",""
                        )
                    }
                }
            }
        })
    }

    private inner class PAdapter(val list: List<ForecastResponse>) : PagerAdapter() {
        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object`
        }

        override fun getCount(): Int = list.size

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val recyclerView = RecyclerView(container.context)
            recyclerView.layoutManager = LinearLayoutManager(container.context)
            recyclerView.adapter = RAdapter(list[position].forecast_infos)
            container.addView(recyclerView, -1, -1)
            return recyclerView
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }
    }

    private inner class RAdapter(val list: List<Forecast_info>) : RecyclerView.Adapter<NoRatingHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoRatingHolder {
            return if (viewType == 0) {
                return Holder(
                    LayoutInflater.from(this@CntDailyActivity).inflate(
                        R.layout.cnt_item_daily,
                        parent,
                        false
                    )
                )
            } else {
                return NoRatingHolder(
                    LayoutInflater.from(this@CntDailyActivity).inflate(
                        R.layout.cnt_item_daily_no_rating,
                        parent,
                        false
                    )
                )
            }
        }

        override fun getItemViewType(position: Int): Int {
            val info = list[position]
            return if (TextUtils.equals(info.forecast_type, DAY)) {
                0
            } else {
                1
            }
        }

        override fun getItemCount() = list.size

        override fun onBindViewHolder(holder: NoRatingHolder, position: Int) {
            val info = list[position]
            try {
                holder.tvTitle.text = mapString[info.forecast_type]
                holder.tvMessage.text = info.content.article[0].text
                if (holder is Holder) {
                    holder.rbCareer.rating = info.rating.career.toFloat()
                    holder.rbHealth.rating = info.rating.health.toFloat()
                    holder.rbLove.rating = info.rating.love.toFloat()
                    holder.rbWealth.rating = info.rating.wealth.toFloat()
                    holder.ivShare.setOnClickListener {
                        mPresenter?.share(
                            info.rating.love.toFloat(),
                            info.rating.health.toFloat(),
                            info.rating.wealth.toFloat(),
                            info.rating.career.toFloat()
                        )
                    }
                }
            } catch (e: Exception) {

            }
        }
    }

    private open inner class NoRatingHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvTitle: TextView = itemView.findViewById(R.id.tv_cntdaily_item_title)
        var tvMessage: TextView = itemView.findViewById(R.id.tv_cntdaily_item_message)
    }

    private inner class Holder(itemView: View) : NoRatingHolder(itemView) {
        var ivShare: ImageView = itemView.findViewById(R.id.iv_cntdaily_item_share)
        var rbLove: RatingBar = itemView.findViewById(R.id.rb_cntdaily_item_love)
        var rbHealth: RatingBar = itemView.findViewById(R.id.rb_cntdaily_item_health)
        var rbWealth: RatingBar = itemView.findViewById(R.id.rb_cntdaily_item_wealth)
        var rbCareer: RatingBar = itemView.findViewById(R.id.rb_cntdaily_item_career)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == 100) {
            id = data?.getIntExtra("id", -1) ?: -1
            mPresenter?.loadData(id)

            if (requestCode == 2) {
                if (id != -1) {
                    //保存当前星座为用户星座
                    val userInfo = UserInfo()
                    userInfo.constellation = id
                    GoCommonEnv.storeUserInfo(userInfo)
                } else {
                    finish()
                }
            }
        }

    }
}
package com.palmapp.master.module_home.fragment


import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.palmapp.master.baselib.constants.PreConstants
import com.palmapp.master.baselib.event.UnlockEvent
import com.palmapp.master.baselib.manager.GoPrefManager
import com.palmapp.master.baselib.manager.OnTimerListener
import com.palmapp.master.baselib.manager.TIMER_PALM
import com.palmapp.master.baselib.manager.TimerManager
import com.palmapp.master.baselib.manager.config.*

import com.palmapp.master.module_home.R
import com.palmapp.master.module_home.adapter.CommonTabAdapter
import kotlinx.android.synthetic.main.home_fragment_plam.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*


/**
 * 手相
 */
class PlamFragment : Fragment() {

    private lateinit var mActivity: Activity
    private lateinit var mAdapter: CommonTabAdapter
    private var listener: OnTimerListener? = null

    companion object {
        fun newInstance(): PlamFragment {
            return PlamFragment()
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mActivity = context!! as Activity
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        EventBus.getDefault().register(this)
        return inflater.inflate(R.layout.home_fragment_plam, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        EventBus.getDefault().unregister(this)
        TimerManager.unregisterTimerListener(TIMER_PALM, mAdapter)
    }


    private fun initView() {
        var homeConfig = ConfigManager.getConfig(HomeConfig::class.java)?.datas?.get("1")
        var titles: ArrayList<String> = ArrayList()
        if (homeConfig != null) {
            for (config in homeConfig) {
                titles.add(config.func)
            }
            titles.add(3, "ad")
        } else {
            titles.addAll(getDefaultOrder())
        }
        mAdapter = CommonTabAdapter(titles.toMutableList(), mActivity, getString(R.string.home_palm_title), 1)
        val time = ConfigManager.getConfig(TimeConfig::class.java)?.palm_delay ?: 1000L
        TimerManager.registerTimerListener(TIMER_PALM, mAdapter)
        rcv_plam.layoutManager = LinearLayoutManager(mActivity)
        rcv_plam.adapter = mAdapter
        TimerManager.startTimer(TIMER_PALM, time)
    }

    private fun getDefaultOrder() =
        listOf(TAB_PALM_DAILY, TAB_PALM_JUDG, TAB_PALM_MATCH, "ad", TAB_PALM_TEST)

    override fun onResume() {
        super.onResume()
        val pref = GoPrefManager.getInstance("Timer", Context.MODE_PRIVATE)
        val day = pref.getInt(PreConstants.Palm.KEY_PALM_SHOW_TIME, 0)
        val current = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        val hasShowResult = pref.getBoolean(PreConstants.Palm.KEY_PALM_CHAT_SHOW, false)
        pref.putInt(PreConstants.Palm.KEY_PALM_SHOW_TIME, current).apply()
        if (hasShowResult && day != 0 && day != current) {
            //不是同一天，要清数据
            TimerManager.resetTimer(TIMER_PALM)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onUnlockEvent(event: UnlockEvent) {
        mAdapter.notifyDataSetChanged()
    }
}

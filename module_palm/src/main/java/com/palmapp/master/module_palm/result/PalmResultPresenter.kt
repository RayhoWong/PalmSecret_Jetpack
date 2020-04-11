package com.palmapp.master.module_palm.result

import android.content.Context
import com.palmapp.master.baselib.BaseMultiplePresenter
import com.palmapp.master.baselib.manager.GoPrefManager
import com.palmapp.master.baselib.manager.OnTimerListener
import com.palmapp.master.baselib.manager.TIMER_PALM
import com.palmapp.master.baselib.manager.TimerManager
import com.palmapp.master.baselib.manager.config.ConfigManager
import com.palmapp.master.baselib.manager.config.TimeConfig
import java.text.SimpleDateFormat

class PalmResultPresenter() : BaseMultiplePresenter<PalmResultView>(), OnTimerListener {
    val time = ConfigManager.getConfig(TimeConfig::class.java)?.palm_delay ?: 1000L
    val pref = GoPrefManager.getInstance("Timer", Context.MODE_PRIVATE)
    override fun onAttach(pView: PalmResultView, context: Context) {
        super.onAttach(pView, context)
        TimerManager.registerTimerListener(TIMER_PALM, this)
    }

    override fun onDetach() {
        super.onDetach()
        TimerManager.unregisterTimerListener(TIMER_PALM, this)
    }

    fun startTime() {
        TimerManager.startTimer(TIMER_PALM, time)
    }

    override fun onFinish() {
        getView()?.onFinish()
    }

    override fun onTick(remainTimeMillis: Long) {
        getView()?.onTimeRemain(formatter.format(remainTimeMillis))
    }

    override fun onTimerCreate() {
    }

    override fun onTimerDestroy() {
    }

    private val formatter = SimpleDateFormat("HH:mm:ss") //初始化Formatter的转换格式。

    init {
        formatter.timeZone = java.util.TimeZone.getTimeZone("GMT+00:00")
    }
}


package com.palmapp.master.baselib.manager

import android.content.Context
import android.os.CountDownTimer
import android.util.SparseArray
import androidx.core.util.contains
import com.palmapp.master.baselib.constants.PreConstants
import java.lang.ref.WeakReference

/**
 *
 * @author :     xiemingrui
 * @since :      2020/3/18
 */
const val TIMER_PALM = 1
const val TIMER_OLD = 2

object TimerManager {
    val listeners = SparseArray<ArrayList<OnTimerListener>>()
    private val times = SparseArray<MyCountDownTimer>()
    val TIME_OFFSET = 10 * 1000
    val pref = GoPrefManager.getInstance("Timer", Context.MODE_PRIVATE)
    fun registerTimerListener(type: Int, listener: OnTimerListener) {
        if (listeners.contains(type)) {
            listeners.get(type).add(listener)
        } else {
            val temp = ArrayList<OnTimerListener>(2)
            temp.add(listener)
            listeners.put(type, temp)
        }
    }

    fun unregisterTimerListener(type: Int, listener: OnTimerListener) {
        if (listeners.contains(type)) {
            listeners.get(type).remove(listener)
        }
    }

    fun startTimer(type: Int, time: Long) {
        if (!times.contains(type)) {
            if (hasFinishTimer(type)) {
                listeners.get(type).forEach {
                    it.onFinish()
                }
                return
            }
            if (!hasResult(type)) {
                return
            }
            val realTime = TimerManager.pref.getLong(type.toString(), time)
            pref.putLong(type.toString(), realTime)
            val timer = MyCountDownTimer(type, realTime, 1000)
            timer.start()
            times.put(type, timer)
            listeners.get(type).forEach { it.onTimerCreate() }
        }
    }

    private fun hasResult(type: Int): Boolean {
        return if (type == TIMER_PALM) {
            GoPrefManager.getDefault().contains(PreConstants.Palm.KEY_PALM_RESULT)
        } else {
            GoPrefManager.getDefault().contains(PreConstants.Palm.KEY_PALM_OLD_RESULT)
        }
    }

    fun cancelTimer(type: Int) {
        if (times.contains(type)) {
            times.get(type).cancel()
            times.remove(type)
        }
    }

    //重置计时器
    fun resetTimer(type: Int) {
        if (hasFinishTimer(type)) {
            listeners.get(type).forEach { it.onTimerDestroy() }
            cancelTimer(type)
            pref.remove(type.toString())
            if (type == TIMER_PALM) {
                pref.putBoolean(PreConstants.Palm.KEY_PALM_CHAT_SHOW, false)
                GoPrefManager.getDefault().remove(PreConstants.Palm.KEY_PALM_RESULT)
            }else{
                GoPrefManager.getDefault().remove(PreConstants.Palm.KEY_PALM_OLD_RESULT)
            }
        }
    }

    fun hasTimer(type: Int): Boolean {
        return pref.contains(type.toString())
    }

    fun hasFinishTimer(type: Int): Boolean {
        return pref.getLong(type.toString(), 0) == -1L
    }
}

private class MyCountDownTimer(val type: Int, millisInFuture: Long, countDownInterval: Long) : CountDownTimer(millisInFuture, countDownInterval) {
    private var currentTime = millisInFuture
    override fun onFinish() {
        TimerManager.pref.putLong(type.toString(), -1).apply()
        TimerManager.listeners.get(type).forEach {
            it.onFinish()
        }
        TimerManager.cancelTimer(type)
    }

    override fun onTick(millisUntilFinished: Long) {
        if (currentTime - millisUntilFinished >= TimerManager.TIME_OFFSET) {
            TimerManager.pref.putLong(type.toString(), millisUntilFinished).apply()
            currentTime = millisUntilFinished
        }
        TimerManager.listeners.get(type).forEach {
            it.onTick(millisUntilFinished)
        }
    }
}

interface OnTimerListener {
    fun onFinish()
    fun onTick(remainTimeMillis: Long)
    fun onTimerCreate()
    fun onTimerDestroy()
}
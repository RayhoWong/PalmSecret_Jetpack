package com.palmapp.master.module_today

import com.palmapp.master.baselib.IView
import com.palmapp.master.baselib.bean.pay.ModuleConfig
import com.palmapp.master.baselib.bean.tarot.DailyTarotBean
import com.palmapp.master.baselib.bean.tarot.TarotBean

/**
 *
 * @author :     xiemingrui
 * @since :      2019/7/31
 */
interface TodayView : IView {
    fun showQuote(config: ModuleConfig)
    fun showLuckyColor(colors: IntArray,w:String)
    fun showLuckyNum(num: Int)
    fun showTarot(cover:List<DailyTarotBean>)
    fun showTarotResult()
}
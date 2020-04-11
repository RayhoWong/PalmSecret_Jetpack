package com.palmapp.master.module_palm.result

import com.palmapp.master.baselib.IView

/**
 *
 * @author :     xiemingrui
 * @since :      2020/3/18
 */
interface PalmChatView :IView{
    fun showResult(datas:List<PalmDataHolder>)

    fun addResult(data:PalmDataHolder)
}
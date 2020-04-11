package com.palmapp.master.module_pay.pay;

import com.palmapp.master.baselib.BasePresenter
import com.palmapp.master.baselib.manager.PayGuideManager

class PayStylePresenter(val entrance: String) : BasePresenter<PayStyleView>() {
    override fun onAttach() {
    }

    override fun onDetach() {
    }

    fun loadStyleFragment() {

        val payStyle = PayGuideManager.getPayStyle(entrance)
        var fragment = getFragment(payStyle)
        if (fragment.getPayGuideConfig(entrance) == null) {
            //使用默认配置
            fragment = PayTwoStyleFragment()
        }
        getView()?.showFragment(fragment)
    }


    private fun getFragment(style: String?): PayBaseStyleFragment {
        return when (style) {
            "style2" -> PayTwoStyleFragment()
            "style4" -> PayFourStyleFragment()
            "style5" -> PayFiveStyleFragment()
            "style3" -> PayThirdStyleFragment()
            "style7" -> PaySevenStyleFragment()
            "style8" -> PayEightStyleFragment()
            "style9" -> PayNinetStyleFragment()
            else -> PayTwoStyleFragment()
        }
    }
}
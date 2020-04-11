package com.palmapp.master.baselib.manager.config

import android.text.TextUtils
import org.json.JSONArray
import org.json.JSONObject

/**
 *
 * @author :     xiemingrui
 * @since :      2020/1/2
 */
class StarGuideConfig : BaseConfig(875) {
    var isOpenGuide = false //好评引导总开关
    var isResultGuide = false //好评引导结果页开关
    var isHomeGuide = false //好评引导首页开关

    override fun parseConfig(response: JSONArray) {
        isOpenGuide = TextUtils.equals(response.getJSONObject(0).getString("lead_switch"), "1")
        isResultGuide = TextUtils.equals(response.getJSONObject(0).getString("result_switch"), "1")
        isHomeGuide = TextUtils.equals(response.getJSONObject(0).getString("home_switch"), "1")
    }

}
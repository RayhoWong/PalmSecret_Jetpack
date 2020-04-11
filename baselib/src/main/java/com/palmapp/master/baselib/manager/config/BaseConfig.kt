package com.palmapp.master.baselib.manager.config

import org.json.JSONArray
import org.json.JSONObject

/**
 *
 * @author :     xiemingrui
 * @since :      2019/12/27
 */
abstract class BaseConfig(val sid: Int) {
    var abtest_id = 0
    var filter_id = 0
    fun startParse(response: JSONObject) {
        abtest_id = response.getInt("abtest_id")
        filter_id = response.getInt("filter_id")
        val jsonArray = response.getJSONArray("cfgs")
        if (jsonArray != null && jsonArray.length() > 0) {
            parseConfig(jsonArray)
        }
    }

    abstract fun parseConfig(response: JSONArray)
    open fun onBuyChannelUpdate() {}
    open fun isIntercept(): Boolean = true
}
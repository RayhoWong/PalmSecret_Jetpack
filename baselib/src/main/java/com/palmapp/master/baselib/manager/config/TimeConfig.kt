package com.palmapp.master.baselib.manager.config

import org.json.JSONArray

/**
 *
 * @author :     xiemingrui
 * @since :      2020/3/17
 */
private const val DEFAULT_DELAY = 60 * 60 * 1000L

class TimeConfig : BaseConfig(900) {
    var palm_delay: Long = DEFAULT_DELAY
    var old_delay: Long = DEFAULT_DELAY
    override fun parseConfig(response: JSONArray) {
        palm_delay = (response.getJSONObject(0).getDouble("palm_delay") * 60 * 60 * 1000).toLong()
        old_delay = (response.getJSONObject(0).getDouble("old_delay") * 60 * 60 * 1000).toLong()
    }

}
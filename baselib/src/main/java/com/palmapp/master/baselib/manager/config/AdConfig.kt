package com.palmapp.master.baselib.manager.config

import com.palmapp.master.baselib.utils.LogUtil
import org.json.JSONArray
import org.json.JSONObject

/**
 *
 * @author :     xiemingrui
 * @since :      2020/1/2
 */
const val MODEL_LAUNCHER_ID = 8824
const val MODEL_REWARD_ID = 8823
const val MODEL_FUNCTION_ID = 8822
const val MODEL_EXIT_ID = 8837

class AdConfig : BaseConfig(874) {
    val hashMap = HashMap<Int, AdBean>()
    override fun parseConfig(response: JSONArray) {
        hashMap.clear()
        for (index in 0 until response.length()) {
            val jsonObject = response.getJSONObject(index)
            hashMap.put(
                jsonObject.getInt("module_id"),
                AdBean(
                    jsonObject.getInt("module_id"),
                    jsonObject.getString("switch"),
                    jsonObject.getInt("show_count"),
                    jsonObject.getInt("show_space")
                )
            )
        }
    }
}

data class AdBean(val module_id: Int, val switch: String, val show_count: Int, val show_space: Int)
package com.palmapp.master.baselib.manager.config

import android.text.TextUtils
import org.json.JSONArray
import org.json.JSONObject

/**
 *
 * @author :     xiemingrui
 * @since :      2020/1/2
 * 首页心率弹窗
 */
class HeartRateDialogConfig : BaseConfig(899) {
    var isOpen = false //开关

    override fun parseConfig(response: JSONArray) {
        isOpen = TextUtils.equals(response.getJSONObject(0).getString("switch"), "1")
    }

}
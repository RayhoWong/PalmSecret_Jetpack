package com.palmapp.master.baselib.manager.config

import android.text.TextUtils
import com.palmapp.master.baselib.Product
import com.palmapp.master.baselib.constants.PreConstants
import com.palmapp.master.baselib.manager.AbTestUserManager
import com.palmapp.master.baselib.manager.GoPrefManager
import com.palmapp.master.baselib.utils.LogUtil
import org.json.JSONArray
import org.json.JSONObject

/**
 *
 * @author :     xiemingrui
 * @since :      2020/1/2
 */
class AbUserConfig : BaseConfig(15) {

    override fun parseConfig(response: JSONArray) {
        val schema = response.getJSONObject(0).getString("schemecfg")
        if (!TextUtils.isEmpty(schema) && AbTestUserManager.isABTestNull()) {
            val result = when (schema) {
                "scheme1" -> AbTestUserManager.USER_A
                else -> ""
            }
            GoPrefManager.getDefault().putString(PreConstants.First.KEY_FIRST_TEST_USER, result)
        }
        LogUtil.d(AbTestUserManager.TAG, "云端AB配置 == $schema")
    }

    override fun isIntercept(): Boolean {
        return AbTestUserManager.isABTestNull()
    }
}
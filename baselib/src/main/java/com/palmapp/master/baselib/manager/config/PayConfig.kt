package com.palmapp.master.baselib.manager.config

import com.cs.bd.buychannel.BuyChannelApi
import com.google.gson.JsonObject
import com.palmapp.master.baselib.GoCommonEnv
import com.palmapp.master.baselib.Product
import com.palmapp.master.baselib.buychannel.BuyChannelProxy
import com.palmapp.master.baselib.constants.PreConstants
import com.palmapp.master.baselib.manager.GoPrefManager
import com.palmapp.master.baselib.manager.PayGuideManager
import com.palmapp.master.baselib.utils.GoGson
import org.json.JSONArray
import org.json.JSONObject

/**
 *
 * @author :     xiemingrui
 * @since :      2019/12/27
 */
class PayConfig() : BaseConfig(788) {
    private val hashMap = hashMapOf<String, String>()//子渠道对应表


    init {
        hashMap.put("unityads", "1")
        hashMap.put("fbmagictt", "2")
        hashMap.put("byte", "3")
        hashMap.put("bytedance_int", "4")
        hashMap.put("xmjuyun", "5")
        hashMap.put("applovin_int", "6")
        hashMap.put("adwords", "7")
        hashMap.put("snapchat", "8")
    }

    override fun onBuyChannelUpdate() {
        super.onBuyChannelUpdate()
    }

    override fun parseConfig(response: JSONArray) {
        val myChannel =
            hashMap.get(BuyChannelProxy.getBuyChannelBean().buyChannel)
                ?: "-1"
        val isUserBuy = BuyChannelProxy.getBuyChannelBean().isUserBuy
        for (i in 0 until response.length()) {
            val jsonObject = response.getJSONObject(i)
            val channel = jsonObject.getString("channel").split(",")
            val strategy = jsonObject.getString("strategy")
            //step1:判断是否有0
            if (channel.contains("0")) {
                if (strategy == "0") {
                    if (isUserBuy) {
                        saveConfig(jsonObject)
                        continue
                    }
                } else {
                    if (isUserBuy) {
                        continue
                    } else {
                        saveConfig(jsonObject)
                        continue
                    }
                }
            }

            //step2:判断子渠道
            if (channel.contains(myChannel)) {
                if (strategy == "0") {
                    saveConfig(jsonObject)
                    continue
                } else {
                    continue
                }
            } else {
                if (strategy == "0") {
                    continue
                } else {
                    saveConfig(jsonObject)
                    continue
                }
            }
        }
        PayGuideManager.payGuideConfig.let {
            GoPrefManager.getDefault()
                .putString(PreConstants.AbTest.KEY_ABTEST_VALUE, GoGson.toJson(it))
        }
        PayGuideManager.hasPayGuideConfig = true
        GoPrefManager.getDefault()
            .putBoolean(PreConstants.AbTest.KEY_HAS_ABTEST_VALUE, true)
    }

    private fun saveConfig(response: JSONObject) {
        val plan = response.getString("plan")
        val style = PayGuideManager.mapForGuide(response.getString("scheme"))
        val scene = response.getString("scene")
        val isFirst = if (scene == "1" || scene == "14") PayGuideManager.FIRST_LAUNCHER else PayGuideManager.NOT_FIRST_LAUNCHER
        PayGuideManager.payGuideConfig.put(isFirst, LauncherConfig(style, plan, scene))
    }
}
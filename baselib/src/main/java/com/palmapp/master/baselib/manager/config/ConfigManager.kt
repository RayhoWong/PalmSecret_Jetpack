package com.palmapp.master.baselib.manager.config

import com.cpcphone.abtestcenter.AbtestCenterService
import com.cs.utils.net.request.THttpRequest
import com.palmapp.master.baselib.GoCommonEnv
import com.palmapp.master.baselib.buychannel.BuyChannelProxy
import com.palmapp.master.baselib.constants.AppConstants
import com.palmapp.master.baselib.constants.PreConstants
import com.palmapp.master.baselib.manager.GoPrefManager
import com.palmapp.master.baselib.manager.GoPrefProxy
import com.palmapp.master.baselib.proxy.AdSdkServiceProxy
import com.palmapp.master.baselib.utils.LogUtil
import com.palmapp.master.baselib.utils.MachineUtil
import com.palmapp.master.baselib.utils.VersionController
import org.json.JSONObject
import java.lang.Exception

/**
 *
 * @author :     xiemingrui
 * @since :      2019/12/27
 */
//ab配置处理
object ConfigManager {
    private val configs: ArrayList<BaseConfig> = ArrayList()
    fun init() {
        startParse(GoPrefManager.getDefault().getString(PreConstants.App.KEY_AB_CONFIG, ""))
    }

    private fun startParse(response: String?) {
        response ?: return
        if (response.isEmpty()) {
            return
        }
        try {
            val jsonObject = JSONObject(response).getJSONObject("datas")
            val list = configs.filter { it.isIntercept() }
            if (list.size == 1) {
                list.get(0).startParse(jsonObject.getJSONObject("infos"))
            } else {
                list.forEach {
                    it.startParse(jsonObject.getJSONObject("infos_${it.sid}"))
                }
            }
        } catch (e: Exception) {

        }
    }

    fun addConfig(config: BaseConfig): ConfigManager {
        if (!configs.contains(config)) {
            configs.add(config)
        }
        return this
    }

    fun <T : BaseConfig> getConfig(clazz: Class<T>): T? {
        configs.forEach {
            if (it.javaClass == clazz) {
                return it as T
            }
        }
        return null
    }

    fun requestAbConfig() {
        val list = configs.filter { it.isIntercept() }
        if (list.isEmpty()) {
            return
        }
        val sids = IntArray(list.size)
        for (i in 0 until list.size) {
            sids[i] = list[i].sid
        }
        var utmSource = ""
        var userFrom = -1
        val channelBean = BuyChannelProxy.getBuyChannelBean()
        utmSource = channelBean.buyChannel
        userFrom = channelBean.secondUserType
        val service = AbtestCenterService.Builder().sid(sids).cid(
            AppConstants.APP_CID
        )
            .cid2(AppConstants.APP_CID2)
            .cversion(VersionController.getVersionCode())
            .local(MachineUtil.getCountry(GoCommonEnv.getApplication()))
            .utm_source(utmSource)
            .user_from(userFrom)
            .entrance(AbtestCenterService.Builder.Entrance.MAIN_PACKAGE)
            .cdays(AdSdkServiceProxy.getCDays(GoCommonEnv.getApplication()))
            .isupgrade(2)
            .aid(MachineUtil.getAndroidId(GoCommonEnv.getApplication()))
            .isSafe(true)
            .build(GoCommonEnv.getApplication())
        LogUtil.i(
            "ConfigManager",
            "sid: ${sids} cid: ${AppConstants.APP_CID} cid2:${AppConstants.APP_CID2} version:${VersionController.getVersionCode()} local:${MachineUtil.getCountry(
                GoCommonEnv.getApplication()
            )} utm_source:$utmSource userFrom:$userFrom cdays:${AdSdkServiceProxy.getCDays(
                GoCommonEnv.getApplication()
            )} aid:${MachineUtil.getAndroidId(GoCommonEnv.getApplication())}"
        )
        service.send(object : AbtestCenterService.ResultCallback {
            override fun onResponse(response: String?) {
                if (channelBean.isUserBuy) {
                    LogUtil.d("PayModuleApp", "拿到买量AB配置")
                } else {
                    LogUtil.d("PayModuleApp", "拿到自然AB配置")
                }
                LogUtil.d("AbTestManager", response)
                GoPrefManager.getDefault().putString(PreConstants.App.KEY_AB_CONFIG, response ?: "")
                    .commit()
                startParse(response)
            }

            override fun onException(tHttpRequest: THttpRequest?, errorMsg: String?, code: Int) {
            }

            override fun onException(tHttpRequest: THttpRequest?, code: Int) {
            }

        })
    }
}
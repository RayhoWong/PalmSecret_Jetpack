package com.palmapp.master.module_network

import com.palmapp.master.baselib.GoCommonEnv
import com.palmapp.master.baselib.bean.pay.ModuleConfig
import com.palmapp.master.baselib.buychannel.BuyChannelProxy
import com.palmapp.master.baselib.constants.AppConstants
import com.palmapp.master.baselib.constants.AppConstants.SIGNATURE_DES_CONF
import com.palmapp.master.baselib.proxy.GoogleServiceProxy
import com.palmapp.master.baselib.utils.LogUtil
import com.palmapp.master.baselib.utils.MachineUtil
import com.palmapp.master.baselib.utils.VersionController
import com.palmapp.master.module_network.signature.DesUtil
import io.reactivex.Observable
import okhttp3.Interceptor
import okhttp3.Response
import org.json.JSONObject
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Query

/**
 *  基础服务接口
 * @author :     xiemingrui
 * @since :      2019/8/7
 */
interface ConfRequest {
    //获取产品模块信息
    @Headers("Cache-Control:no-cache,max-age=0")
    @GET("/api/v1/website/navigations/module")
    fun getResource(
        @Query("module_id") module_id: Int, @Query(
            "pversion"
        ) pversion: Int = 1
    ): Observable<ModuleConfig>
}

class ConfInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var original = chain.request()
        val url = original.url().newBuilder()
            .addQueryParameter("client", DesUtil.encrypt(getClient(), SIGNATURE_DES_CONF))
            .addQueryParameter("product_id", AppConstants.CONF_PRODUCT_ID.toString()).build()

        original = original.newBuilder()
            .addHeader("Content-Type", "application/json")
            .addHeader(
                "X-Encrypt-Client",
                "1"
            )
            .url(url)
            .build()
        val response = chain.proceed(original)
        return response
    }

    private fun getClient(): String {
        val device = JSONObject()
        val context = GoCommonEnv.getApplication()
        LogUtil.d("ConfRequest", MachineUtil.getLanguage(context))
        device.put("aid", MachineUtil.getAndroidId(context))
        device.put("lang", MachineUtil.getLanguageConf(context))
        device.put("country", MachineUtil.getCountry(context))
        device.put("channel", GoCommonEnv.innerChannel)
        device.put("cversion_number", VersionController.getVersionCode())
        device.put("gadid", GoogleServiceProxy.getGoogleAdvertisingId())
        device.put("user_source", BuyChannelProxy.getBuyChannelBean().secondUserType)
        device.put("system_version_name",BuyChannelProxy.getBuyChannelBean().secondUserType)
        return device.toString()
    }
}
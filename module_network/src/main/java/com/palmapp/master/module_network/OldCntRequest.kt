package com.palmapp.master.module_network

import com.palmapp.master.baselib.GoCommonEnv
import com.palmapp.master.baselib.bean.palm.PalmMatchResponse
import com.palmapp.master.baselib.buychannel.BuyChannelProxy
import com.palmapp.master.baselib.constants.AppConstants
import com.palmapp.master.baselib.proxy.GoogleServiceProxy
import com.palmapp.master.baselib.utils.LogUtil
import com.palmapp.master.baselib.utils.MachineUtil
import com.palmapp.master.baselib.utils.VersionController
import com.palmapp.master.module_network.signature.Base64
import com.palmapp.master.module_network.signature.DesUtil
import com.palmapp.master.module_network.signature.Signature
import io.reactivex.Observable
import okhttp3.Interceptor
import okhttp3.Response
import org.json.JSONObject
import retrofit2.http.GET
import retrofit2.http.Query

/**
 *  旧星座接口http://wiki.3g.net.cn/pages/viewpage.action?pageId=20786717
 * @author :     xiemingrui
 * @since :      2019/9/25
 */
interface OldCntRequest {
    @GET("/api/v1/compatibility/palm")
    fun getPalmMatch(@Query("sign1") sign1: Int, @Query("sign2") sign2: Int):Observable<PalmMatchResponse>
}

class OldCntInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var original = chain.request()
        val url = original.url().newBuilder()
            .addQueryParameter("client", Base64.encodeBase64URLSafeString(getClient().toByteArray())).build()

        original = original.newBuilder()
            .url(url)
            .build()
        return chain.proceed(original)
    }

    private fun getClient(): String {
        val device = JSONObject()
        val context = GoCommonEnv.getApplication()
        LogUtil.d("ConfRequest", MachineUtil.getLanguage(context))
        device.put("aid", MachineUtil.getAndroidId(context))
        device.put("lang", MachineUtil.getLanguageOldCnt(context))
        device.put("country", MachineUtil.getCountry(context).toUpperCase())
        device.put("channel", GoCommonEnv.innerChannel)
        device.put("app_version", VersionController.getVersionCode())

        return device.toString()
    }
}
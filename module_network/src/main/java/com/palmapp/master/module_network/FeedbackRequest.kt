package com.palmapp.master.module_network

import com.cs.statistic.StatisticsManager
import com.palmapp.master.baselib.GoCommonEnv
import com.palmapp.master.baselib.constants.AppConstants
import com.palmapp.master.baselib.utils.MachineUtil
import com.palmapp.master.baselib.utils.VersionController
import com.palmapp.master.module_network.signature.DesUtil
import io.reactivex.Observable
import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * @Description 意见反馈
 *
 * @Author zhangl
 * @Date 2019-08-26
 * 这个坑人的接口 使用的form 表单提交数据，接口文档又不写提交数据方式，万分注意
 *
 */
interface FeedbackRequest {
    @FormUrlEncoded
    @POST("/userfeedback/interface/clientfeedbackdes.jsp")
    fun postFeedback(@FieldMap map: Map<String, String>): Observable<String>

}

class FeedbackInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val builder = request.newBuilder()
        builder.addHeader("Content-Type", "application/json")
        return chain.proceed(builder.build())
    }
}

class FeedbackBody(detail:String) {
    var detail:String = ""
    init {
        this.detail = detail
    }

    private fun getDevice():String {
        val buffer:StringBuffer = StringBuffer()
        buffer.append("PackageName=").append("com.palmsecret.horoscope")
        buffer.append("\n")
        buffer.append("AndroidVersion").append(VersionController.getVersionCode())
        buffer.append("\n")
        buffer.append("Goid").append(StatisticsManager.getUserId(GoCommonEnv.getApplication()))
        buffer.append("\n")
        buffer.append("Country").append(MachineUtil.getCountry(GoCommonEnv.getApplication()).toUpperCase())
        return buffer.toString()
    }

    fun getParam():Map<String, String> {
        val map:MutableMap<String, String> = mutableMapOf()
        map.put("pid", AppConstants.K_FEEDBACK_PID)
        map.put("contact", DesUtil.encrypt(AppConstants.k_CONTACT_MAIL, AppConstants.K_FEEDBACK_DES_KEY))
        map.put("detail", detail)
        map.put("versionname", VersionController.getVersionName())
        map.put("versioncode", VersionController.getVersionCode()?.toString())
        map.put("type", "1")
        map.put("commonproblem", "1")
        map.put("devinfo", DesUtil.encrypt(getDevice(), AppConstants.K_FEEDBACK_DES_KEY))
        map.put("adatas", "")
        return map
    }



}
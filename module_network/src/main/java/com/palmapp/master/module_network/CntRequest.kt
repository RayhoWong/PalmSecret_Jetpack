package com.palmapp.master.module_network

import com.palmapp.master.baselib.bean.BaseResponse
import com.palmapp.master.baselib.bean.palm.IdentityRequest
import com.palmapp.master.baselib.bean.palm.IdentityResponse
import com.palmapp.master.baselib.bean.cnt.ForecastRequest
import com.palmapp.master.baselib.bean.cnt.ForecastResponse
import com.palmapp.master.baselib.bean.cnt.MatchingRequest
import com.palmapp.master.baselib.bean.cnt.MatchingResponse
import com.palmapp.master.baselib.constants.AppConstants
import com.palmapp.master.module_network.signature.Signature
import io.reactivex.Observable
import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * 星座核心接口：http://wiki.3g.net.cn/pages/viewpage.action?pageId=23836595
 * @author :     xiemingrui
 * @since :      2019/8/2
 */
interface CntRequest {
    @POST("/api/v1/forecast")
    fun getForecast(@Body body: ForecastRequest): Observable<ForecastResponse>

    @POST("/api/v1/hand/identity")
    fun identity(@Body body: IdentityRequest): Observable<IdentityResponse>

    @POST("/api/v1/match/constellation")
    fun matching(@Body body: MatchingRequest): Observable<MatchingResponse>
}

class CntInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val builder = request.newBuilder()
        builder.addHeader("Content-Type", "application/json")
        builder.addHeader(
            "X-Signature",
            Signature.getSign(
                request.method(),
                request.url().encodedPath(),
                AppConstants.SIGNATURE_CNT_KEY,
                request.url().encodedQuery() ?: "",
                HttpClient.bodyToString(request, AppConstants.SIGNATURE_DES_CNT)
            )
        )
        return chain.proceed(builder.build())
    }
}
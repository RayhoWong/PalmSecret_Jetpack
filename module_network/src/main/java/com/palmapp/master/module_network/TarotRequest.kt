package com.palmapp.master.module_network

import com.palmapp.master.baselib.bean.tarot.*
import com.palmapp.master.baselib.constants.AppConstants
import com.palmapp.master.module_network.signature.Signature
import io.reactivex.Observable
import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

/**
 *  //塔罗牌接口：http://wiki.3g.net.cn/pages/viewpage.action?pageId=25199965
 * @ClassName:      TarotRequest
 * @Description:
 * @Author:         xiemingrui
 * @CreateDate:     2019/7/24
 */
interface TarotRequest {

    /**
     * 塔罗牌数据下发
     */
    @POST("/api/v1/card/list")
    fun getTarotList(@Body body: TarotListRequest): Observable<TarotListResponse>

    /**
     * 塔罗牌答案
     */
    @POST("/api/v1/daily_tarot/answer")
    fun getDailTarotAnswer(@Body body: DailyTarotAnswerRequest): Observable<DailyTarotAnswerResponse>


    /**
     * 塔罗牌话题列表
     */
    @POST("/api/v1/topic/list")
    fun getTarotTopicList(@Body body: TarotTopicInfoRequest): Observable<TarotTopicInfoResponse>


    /**
     * 塔罗牌话题详细
     */
    @POST("/api/v1/topic/detail/{topic_id}")
    fun getTarotTopicDetail(@Path("topic_id") topic_id: Int, @Body body: TarotTopicInfoDetailRequest): Observable<TarotTopicInfoDetailResponse>


}

class TarotInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val builder = request.newBuilder()
        builder.addHeader("Content-Type", "application/json")
        builder.addHeader(
            "X-Signature",
            Signature.getSign(
                request.method(),
                request.url().encodedPath(),
                AppConstants.SIGNATURE_TAROT_KEY,
                request.url().encodedQuery() ?: "",
                HttpClient.bodyToString(request, AppConstants.SIGNATURE_DES_TARCORE)
            )
        )
        return chain.proceed(builder.build())
    }
}
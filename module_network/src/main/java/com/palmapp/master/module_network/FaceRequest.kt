package com.palmapp.master.module_network

import com.palmapp.master.baselib.bean.face.*
import com.palmapp.master.baselib.bean.palm.IdentityRequest
import com.palmapp.master.baselib.bean.gender.GenderRequest
import com.palmapp.master.baselib.bean.gender.GenderResponse
import com.palmapp.master.baselib.bean.palm.IdentityRequestV2
import com.palmapp.master.baselib.bean.palm.IdentityResponseV2
import com.palmapp.master.baselib.bean.palm.PalmContentDto
import com.palmapp.master.baselib.bean.quiz.*
import com.palmapp.master.baselib.constants.AppConstants
import com.palmapp.master.baselib.utils.AppUtil
import com.palmapp.master.module_network.signature.Signature
import io.reactivex.Observable
import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * 面相核心接口：http://wiki.3g.net.cn/pages/viewpage.action?pageId=23836515
 * @author :     xiemingrui
 * @since :      2019/8/2
 */
interface FaceRequest {
    /**
     * 测试题列表获取
    @page 页码
    @size 页大小
    @category_id 测试题类别
    @body 请求消息体
     */
    @POST("/api/v1/quiz/list")
    fun getQuizList(
        @Query("page") page: Int = 1, @Query("size") size: Int = 10
        , @Body body: QuizListRequest
    ): Observable<QuizListResponse>

    /**
     * 测试题内容获取
     */
    @POST("/api/v1/quiz/fetch")
    fun getQuizContent(@Body body: QuizContentRequest): Observable<QuizContentResponse>

    /**
     * 测试结果获取
     */
    @POST("/api/v1/quiz/answer")
    fun getAnswer(@Body body: AnswerRequest): Observable<AnswerResponse>

    /**
     * 宝宝生成
     */
    @POST("/api/v1/baby/report/generate")
    fun generateBaby(@Body body:BabyGenerateRequest):Observable<BabyGenerateResponse>

    /**
     * 人脸检测
     */
    @POST("/api/v1/face/detect")
    fun detectFace(@Body face:FaceIdentityRequest):Observable<FaceIdentityResponse>

    /**
     * 变性
     */
    @POST("/api/v1/gender/report/generate")
    fun genderTransform(@Body body: GenderRequest): Observable<GenderResponse>

    @POST("/api/v1/palm/predict")
    fun predictPalm(@Body body: IdentityRequestV2):Observable<IdentityResponseV2>

    /**
     * 卡通模板下发
     */
    @POST("/api/v1/cartoon/template")
    fun cartoonTemplate(@Body body: CartoonTemplateRequest): Observable<CartoonTemplateResponse>

    /**
     * 卡通人像
     */
    @POST("/api/v1/cartoon/report/generate")
    fun cartoonFace(@Body body: CartoonFaceRequest): Observable<CartoonFaceResponse>

}

class FaceInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val builder = request.newBuilder()
        builder.addHeader("Content-Type", "application/json")
        builder.addHeader(
            "X-Signature",
            Signature.getSign(
                request.method(),
                request.url().encodedPath(),
                AppConstants.SIGNATURE_FACE_KEY,
                request.url().encodedQuery() ?: "",
                HttpClient.bodyToString(request, AppConstants.SIGNATURE_DES_FACE)
            )
        )
        builder.addHeader("X-Source", "com.palmsecret.horoscope")
        return chain.proceed(builder.build())
    }
}

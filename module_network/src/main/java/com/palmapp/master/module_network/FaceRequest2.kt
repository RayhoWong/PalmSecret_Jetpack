package com.palmapp.master.module_network

import com.palmapp.master.baselib.GoCommonEnv
import com.palmapp.master.baselib.Product
import com.palmapp.master.baselib.bean.face.*
import com.palmapp.master.baselib.bean.gender.GenderRequest
import com.palmapp.master.baselib.bean.gender.GenderResponse
import com.palmapp.master.baselib.constants.AppConstants
import com.palmapp.master.baselib.utils.LogUtil
import com.palmapp.master.baselib.utils.MachineUtil
import com.palmapp.master.baselib.utils.VersionController
import com.palmapp.master.module_network.signature.Base64
import com.palmapp.master.module_network.signature.Signature
import io.reactivex.Observable
import okhttp3.Interceptor
import okhttp3.Response
import org.json.JSONObject
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * 新面相接口： http://wiki.3g.net.cn/pages/viewpage.action?pageId=25223182
 * @author :     huangweihao
 * @since :      2020/2/27
 */
interface FaceRequest2 {

    /**
     * 人脸检测
     */
    @POST(Product.HTTP_FACECORE_HOST_2_FACE_DETECTION)
    fun detectFace(@Body body: FaceIdentityRequest2):Observable<FaceIdentityResponse2>

    /**
     * 卡通
     */
    @POST(Product.HTTP_FACECORE_HOST_2_CARTOON)
    fun toCartoon(@Body body: CartoonRequest): Observable<CartoonResponse>

}

class FaceInterceptor2 : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
//        val request = chain.request()
//        val builder = request.newBuilder()
//        builder.addHeader("Content-Type", "application/json")
//        builder.addHeader(
//            "X-Signature",
//            Signature.getSign(
//                request.method(),
//                request.url().encodedPath(),
//                AppConstants.SIGNATURE_FACE_KEY_2,
//                request.url().encodedQuery() ?: "",
//                HttpClient.bodyToString(request, AppConstants.SIGNATURE_DES_FACE_2)
//            )
//        )
//        builder.addHeader("X-Source", "com.palmsecret.horoscope")
//        return chain.proceed(builder.build())
        var original = chain.request()
        val url = original.url().newBuilder()
            .addQueryParameter("device", Base64.encodeBase64URLSafeString(getClient().toByteArray())).build()

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

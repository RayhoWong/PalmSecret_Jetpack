package com.palmapp.master.module_network

import com.facebook.stetho.okhttp3.StethoInterceptor
import com.palmapp.master.baselib.constants.AppConstants
import com.palmapp.master.baselib.manager.DebugProxy
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.IllegalArgumentException
import java.util.concurrent.TimeUnit
import javax.net.ssl.X509TrustManager

/**
 *
 * @ClassName:      RequestFactory
 * @Description:
 * @Author:         xiemingrui
 * @CreateDate:     2019/7/24
 */
object RequestFactory {
    private const val DEFAULT_READ_TIMEOUT = 10_000L
    private const val DEFAULT_WRITE_TIMEOUT = 10_000L
    private const val DEFAULT_CONNECT_TIMEOUT = 10_000L

    fun <T> createRequest(clazz: Class<T>): T {
        if (clazz == CntRequest::class.java) {
            //初始化okhttp
            val client = defaultClientBuilder(AppConstants.SIGNATURE_DES_CNT).addInterceptor(CntInterceptor()).build()
            val retrofit = defaultRetrofitBuilder(client, AppConstants.HTTP_CNT_HOST).addConverterFactory(
                SecurityConverterFactory.create(AppConstants.SIGNATURE_DES_CNT)
            ).build()
            return retrofit.create(CntRequest::class.java) as T

        } else if (clazz == TarotRequest::class.java) {
            val client =
                defaultClientBuilder(AppConstants.SIGNATURE_DES_TARCORE).addInterceptor(TarotInterceptor()).build()
            val retrofit = defaultRetrofitBuilder(client, AppConstants.HTTP_TARCORE_HOST).addConverterFactory(
                SecurityConverterFactory.create(AppConstants.SIGNATURE_DES_TARCORE)
            ).build()
            return retrofit.create(TarotRequest::class.java) as T

        } else if (clazz == FaceRequest::class.java) {
            val client = defaultClientBuilder(AppConstants.SIGNATURE_DES_FACE).addInterceptor(FaceInterceptor()).build()
            val retrofit = defaultRetrofitBuilder(client, AppConstants.HTTP_FACECORE_HOST).addConverterFactory(
                SecurityConverterFactory.create(AppConstants.SIGNATURE_DES_FACE)
            ).build()
            return retrofit.create(FaceRequest::class.java) as T
        }  else if (clazz == FaceRequest2::class.java) {
            val client = defaultClientBuilder("").addInterceptor(FaceInterceptor2()).build()
            val retrofit = defaultRetrofitBuilder(client, AppConstants.HTTP_FACECORE_HOST_2).addConverterFactory(
                GsonConverterFactory.create()
            ).build()
            return retrofit.create(FaceRequest2::class.java) as T
        } else if (clazz == ConfRequest::class.java) {
            val client = defaultClientBuilder("").addInterceptor(ConfInterceptor()).build()
            val retrofit = defaultRetrofitBuilder(client, AppConstants.HTTP_CONF_HOST).addConverterFactory(
                GsonConverterFactory.create()
            ).build()
            return retrofit.create(ConfRequest::class.java) as T
        } else if (clazz == FeedbackRequest::class.java) {
            val client = defaultClientBuilder("").addInterceptor(FeedbackInterceptor()).build()
            val retrofit = defaultRetrofitBuilder(client, AppConstants.HTTP_FEEDBACK_HOST).addConverterFactory(
                GsonConverterFactory.create()
            ).build()
            return retrofit.create(FeedbackRequest::class.java) as T
        } else if (clazz == OldCntRequest::class.java) {
            val client = defaultClientBuilder("").addInterceptor(OldCntInterceptor()).build()
            val retrofit = defaultRetrofitBuilder(client, AppConstants.HTTP_OLD_CNT_HOST).addConverterFactory(
                GsonConverterFactory.create()
            ).build()
            return retrofit.create(OldCntRequest::class.java) as T
        }
        throw IllegalArgumentException()
    }

    private fun defaultClientBuilder(key: String): OkHttpClient.Builder {
        val builder = OkHttpClient.Builder()
            .readTimeout(DEFAULT_READ_TIMEOUT, TimeUnit.MILLISECONDS)
            .writeTimeout(DEFAULT_WRITE_TIMEOUT, TimeUnit.MILLISECONDS)
            .connectTimeout(DEFAULT_CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
        if (DebugProxy.isOpenLog()) {
            var loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            loggingInterceptor.key = key
            builder.addInterceptor(loggingInterceptor)
            builder.addNetworkInterceptor(StethoInterceptor())
        }
        try {
            val trustAllCert = object : X509TrustManager {
                override fun checkClientTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {}

                override fun checkServerTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {}

                override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> {
                    return arrayOf()
                }
            }
            builder.sslSocketFactory(SSL(trustAllCert), trustAllCert)
        } catch (e: Exception) {

        }
        return builder
    }

    private fun defaultRetrofitBuilder(client: OkHttpClient, host: String): Retrofit.Builder {
        val retrofit = Retrofit.Builder()
            .client(client)
            .baseUrl(host)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        return retrofit
    }
}
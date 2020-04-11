package com.palmapp.master.module_network

import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken
import com.palmapp.master.module_network.signature.DesUtil
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody
import okio.Buffer
import retrofit2.Converter
import retrofit2.Retrofit
import java.io.IOException
import java.io.OutputStreamWriter
import java.lang.reflect.Type
import java.nio.charset.Charset

/**
 *
 * @author :     xiemingrui
 * @since :      2019/8/2
 */
class SecurityConverterFactory(val gson: Gson, val signature: String) : Converter.Factory() {
    private val MEDIA_TYPE = MediaType.parse("application/json; charset=UTF-8")
    private val UTF_8 = Charset.forName("UTF-8")

    companion object {
        fun create(signature: String): SecurityConverterFactory {
            return SecurityConverterFactory(Gson(), signature)
        }
    }

    override fun requestBodyConverter(
        type: Type,
        parameterAnnotations: Array<Annotation>,
        methodAnnotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<*, RequestBody>? {
        val adapter: TypeAdapter<*> = gson.getAdapter(TypeToken.get(type))
        return RequestBodyConverter(adapter)
    }

    override fun responseBodyConverter(
        type: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *>? {
        val adapter: TypeAdapter<*> = gson.getAdapter(TypeToken.get(type))
        return ResponseBodyConverter(adapter)
    }

    private inner class RequestBodyConverter<T>(val adapter: TypeAdapter<T>) : Converter<T, RequestBody> {
        @Throws(IOException::class)
        override fun convert(value: T): RequestBody {
            return RequestBody.create(MEDIA_TYPE, DesUtil.encrypt(gson.toJson(value), signature))
        }
    }

    private inner class ResponseBodyConverter<T>(val adapter: TypeAdapter<T>) :
        Converter<ResponseBody, T> {

        @Throws(IOException::class)
        override fun convert(value: ResponseBody): T {
            val result = DesUtil.decrypt(value.string(), signature)
            try {
                return adapter.fromJson(result)
            } finally {
                value.close()
            }
        }
    }
}

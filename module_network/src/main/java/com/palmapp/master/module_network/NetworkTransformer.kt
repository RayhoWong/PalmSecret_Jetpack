package com.palmapp.master.module_network

import com.palmapp.master.baselib.GoCommonEnv
import com.palmapp.master.baselib.bean.BaseResponse
import com.palmapp.master.baselib.manager.DebugProxy
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import java.lang.Exception
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 *
 * @author :     xiemingrui
 * @since :      2019/8/5
 */
object NetworkTransformer {
    fun <T> toMainSchedulers(): ObservableTransformer<T, T> {
        return ObservableTransformer<T, T> { upstream ->
            upstream.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).onErrorResumeNext { t: Throwable ->
                    Observable.error(handleException(t))
                }
        }
    }

    private fun handleException(e: Throwable): NetworkException {
        if (DebugProxy.isOpenLog()) {
            e.printStackTrace()
        }
        if (e is SocketTimeoutException) {
            return NetworkException(CODE_TIMEOUT, GoCommonEnv.getApplication().getString(R.string.net_error))
        }
        if (e is UnknownHostException) {
            return NetworkException(CODE_TIMEOUT, GoCommonEnv.getApplication().getString(R.string.net_error))
        }
        if (e is HttpException) {
            return NetworkException(
                CODE_SERVER_ERROR,
                GoCommonEnv.getApplication().getString(R.string.net_server_error)
            )
        }
        if (e is CustomException) {
            return NetworkException(CODE_CUSTOM, "自定义异常")
        }
        return NetworkException(CODE_CUSTOM, String.format("UNKNOWN Message:%s", e.message))
    }
}

class CustomException : Exception() {}
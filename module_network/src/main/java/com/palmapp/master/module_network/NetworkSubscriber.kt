package com.palmapp.master.module_network

import android.widget.Toast
import com.palmapp.master.baselib.GoCommonEnv
import com.palmapp.master.baselib.manager.DebugProxy
import io.reactivex.Observer
import io.reactivex.disposables.Disposable


/**
 *
 * @author :     xiemingrui
 * @since :      2019/8/5
 */
abstract class NetworkSubscriber<T> : Observer<T> {
    override fun onComplete() {
        onFinish()
    }

    override fun onSubscribe(d: Disposable) {
        onDispose(d)
    }

    override fun onError(t: Throwable) {
        if (t is NetworkException) {
            when (t.code) {
                CODE_TIMEOUT -> {
                    onNetworkError()
                    Toast.makeText(GoCommonEnv.getApplication(), t.msg, Toast.LENGTH_LONG).show()
                }
                CODE_SERVER_ERROR -> {
                    onServiceError()
                    Toast.makeText(GoCommonEnv.getApplication(), t.msg, Toast.LENGTH_LONG).show()
                }
                CODE_UNKNOWN -> {
                    onServiceError()
                    if (DebugProxy.isOpenLog()) {
                        Toast.makeText(GoCommonEnv.getApplication(), t.msg, Toast.LENGTH_LONG).show()
                    }
                }
                CODE_CUSTOM->{
                    onCustomError()
                }
            }
        } else {
            if (DebugProxy.isOpenLog()) {
                Toast.makeText(GoCommonEnv.getApplication(), t.message, Toast.LENGTH_LONG).show()
            }
        }
        onFinish()
    }

    //没有网络或者链接超时
    open fun onNetworkError() {
    }

    //服务器错误500或者解析错误
    open fun onServiceError() {}

    //自定义异常
    open fun onCustomError() {}

    open fun onFinish() {}


    /**
     * 便于CompositeDisposable添加订阅事件的disposable
     * 及时取消订阅 避免内存泄漏
     * --具体使用参考CardDetailActivity
     */
    open fun onDispose(d: Disposable) {}
}
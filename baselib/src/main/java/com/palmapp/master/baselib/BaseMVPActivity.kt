package com.palmapp.master.baselib

import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.TypedArray
import android.graphics.Color
import android.os.Bundle
import com.palmapp.master.baselib.manager.PermissionManager
import android.view.WindowManager
import android.os.Build
import com.palmapp.master.baselib.utils.StatusBarUtil
import com.trello.rxlifecycle2.LifecycleProvider
import com.trello.rxlifecycle2.android.ActivityEvent
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity


abstract class BaseMVPActivity<V : IView, T : BasePresenter<V>> : BaseActivity(), IView {

    protected var mPresenter: T? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        mPresenter = createPresenter()
        mPresenter?.attachActivityView(this as V, this as LifecycleProvider<ActivityEvent>)
    }


    override fun onDestroy() {
        mPresenter?.detachView()
        super.onDestroy()
    }


    abstract fun createPresenter(): T

    override fun getContext(): Context? {
        return this
    }

}
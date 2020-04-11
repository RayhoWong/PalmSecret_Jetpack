package com.palmapp.master.baselib

import android.content.Context
import android.os.Bundle
import com.trello.rxlifecycle2.LifecycleProvider
import com.trello.rxlifecycle2.android.FragmentEvent
import com.trello.rxlifecycle2.components.support.RxFragment

abstract class BaseFragment<V : IView, T : BasePresenter<V>> : RxFragment(), IView {
    protected var mPresenter: T? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mPresenter = createPresenter()
        mPresenter?.attachFragmentView(this as V, this as LifecycleProvider<FragmentEvent>)
    }

    override fun onDestroy() {
        mPresenter?.detachView()
        super.onDestroy()
    }

    abstract fun createPresenter(): T

    override fun getContext(): Context? {
        return activity
    }

}
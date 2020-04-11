package com.palmapp.master.baselib

import com.trello.rxlifecycle2.LifecycleProvider
import com.trello.rxlifecycle2.android.ActivityEvent
import com.trello.rxlifecycle2.android.FragmentEvent
import java.lang.ref.Reference
import java.lang.ref.WeakReference


abstract class BasePresenter<T : IView> {

    private var mViewRef: Reference<T>? = null
    var activityProvider: LifecycleProvider<ActivityEvent>? = null
        private set
    private var fragmentProvider: LifecycleProvider<FragmentEvent>? = null
        private set

    public fun getView(): T? {
        return mViewRef?.get()
    }

    public fun isViewAttached(): Boolean {
        return mViewRef?.get() != null
    }

    /**
     * 建立关联
     */
    public fun attachActivityView(
        view: T,
        lifecycleProvider: LifecycleProvider<ActivityEvent>
    ) {
        activityProvider = lifecycleProvider
        mViewRef = WeakReference<T>(view)
        onAttach()
    }

    /**
     * 建立关联
     */
    public fun attachFragmentView(
        view: T,
        lifecycleProvider: LifecycleProvider<FragmentEvent>
    ) {
        fragmentProvider = lifecycleProvider
        mViewRef = WeakReference<T>(view)
        onAttach()
    }

    /**
     * 解除关联
     */
    public fun detachView() {
        onDetach()
        if (mViewRef != null) {
            mViewRef?.clear();
            mViewRef = null;
        }
    }

    abstract fun onAttach()
    abstract fun onDetach()
}
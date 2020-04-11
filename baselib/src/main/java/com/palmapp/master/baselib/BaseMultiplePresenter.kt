package com.palmapp.master.baselib

import android.content.Context
import android.content.Intent
import java.lang.ref.WeakReference

/**
 *
 * @author :     xiemingrui
 * @since :      2020/3/10
 */
abstract class BaseMultiplePresenter<V : IView> : IMultipleBasePresenter<V> {
    private lateinit var mViewRef: WeakReference<V>
    private lateinit var mContext: WeakReference<Context>
    override fun onAttach(pView: V, context: Context) {
        mViewRef = WeakReference(pView)
        mContext = WeakReference(context)

    }

    override fun onDetach() {
        mViewRef.clear()
        mContext.clear()
    }

    override fun onPause() {

    }

    override fun onRestart() {

    }

    override fun onStart() {

    }

    override fun onResume() {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

    }

    override fun onBackPressedEvent(): Boolean = false

    fun getView(): V? {
        return mViewRef.get()
    }

    fun getContext(): Context? {
        return mContext.get()
    }

    override fun onFinishEvent(): Boolean = false
}
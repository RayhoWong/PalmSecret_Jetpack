package com.palmapp.master.baselib

import android.content.Context
import android.content.Intent

/**
 *
 * @author :     xiemingrui
 * @since :      2020/3/10
 */
interface IMultipleBasePresenter <V:IView>{
    /**
     * 绑定view到presenter
     *
     * @param pView
     * @param context
     */
    fun onAttach(pView: V, context: Context)

    /**
     * 解绑view
     */
    fun onDetach()

    /**
     * 是否拦截finish事件，true表示拦截
     */
    fun onFinishEvent():Boolean

    fun onBackPressedEvent():Boolean

    fun onStart()

    fun onRestart()

    fun onPause()

    fun onResume()

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
}
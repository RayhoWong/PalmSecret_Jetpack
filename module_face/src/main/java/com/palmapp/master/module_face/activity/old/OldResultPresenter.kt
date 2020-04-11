package com.palmapp.master.module_face.activity.old

import android.content.Context
import android.graphics.Bitmap
import com.palmapp.master.baselib.BaseMultiplePresenter
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

/**
 *
 * @author :     xiemingrui
 * @since :      2020/3/19
 */
class OldResultPresenter : BaseMultiplePresenter<OldScanView>() {
    private var mAge:Int = 0
    lateinit var finalBitmap: Bitmap
    override fun onAttach(pView: OldScanView, context: Context) {
        EventBus.getDefault().register(this)
    }

    override fun onDetach() {
        EventBus.getDefault().unregister(this)
    }

    @Subscribe
    fun onFinalBitmap(bitmap: Bitmap){
        finalBitmap = bitmap
        EventBus.getDefault().removeStickyEvent(bitmap)
    }

    fun selectAge(age:Int){
        mAge = age

    }
}
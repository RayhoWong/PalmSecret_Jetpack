package com.palmapp.master.module_palm.scan;

import android.graphics.Bitmap
import com.palmapp.master.baselib.IView

interface PalmScanView : IView {
    fun showCamera()

    fun showScan()

    fun showConfirm(bitmap: Bitmap?)

    fun showResult(bitmap: Bitmap?,handleInfos: PalmResultCache)

    fun showNetWorkError()

    fun showPicError()

    fun changeHand()

    fun finishActivity()
}
package com.palmapp.master.module_face.activity.cartoon;

import android.graphics.Bitmap
import com.palmapp.master.baselib.IView

interface CartoonResultView : IView {
    fun showResult(bitmap: Bitmap?)
    fun showOriginalBitmap(bitmap: Bitmap?)
    fun showLoading()
}
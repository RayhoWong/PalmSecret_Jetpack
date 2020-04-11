package com.palmapp.master.module_face.activity.album;

import android.graphics.Bitmap
import com.palmapp.master.baselib.IView

interface AlbumResultView : IView {
    fun showResult(bitmap: Bitmap?)
    fun showOriginalBitmap(bitmap: Bitmap?)
    fun showLoading()
}
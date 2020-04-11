package com.google.android.cameraview

import android.graphics.Bitmap
import com.palmapp.master.baselib.IView

/**
 *
 * @author :     xiemingrui
 * @since :      2020/3/10
 */
interface IPhotoSelectorView :IView{
    fun showPictureView(bitmap: Bitmap)
    fun showNormalView()
}
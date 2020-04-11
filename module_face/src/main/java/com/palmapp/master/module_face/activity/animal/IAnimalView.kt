package com.palmapp.master.module_face.activity.animal

import android.graphics.Bitmap
import android.graphics.Point
import com.palmapp.master.baselib.IView

interface IAnimalView : IView {
    fun onAnimalLoadCompleted()
    fun onAnimalLoadFailed()
    fun onReadyToStart()
    fun showLoading()
}
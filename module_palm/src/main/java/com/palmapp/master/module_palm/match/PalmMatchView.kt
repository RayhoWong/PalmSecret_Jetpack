package com.palmapp.master.module_palm.match;

import android.hardware.Camera
import com.palmapp.master.baselib.IView

interface PalmMatchView : IView {
    fun getCamera(camera: Camera?)
    fun next()
}
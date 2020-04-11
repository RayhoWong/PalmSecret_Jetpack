package com.palmapp.master.baselib.view

import android.content.Context
import android.hardware.Camera
import android.view.SurfaceHolder
import android.view.SurfaceView
import java.io.IOException

/**
 *
 * @author :     xiemingrui
 * @since :      2019/8/8
 */
class ScanPreview(
    context: Context,
    private val mCamera: Camera
) : SurfaceView(context), SurfaceHolder.Callback {

    private val mHolder: SurfaceHolder = holder.apply {
        addCallback(this@ScanPreview)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        mCamera.apply {
            try {
                setPreviewDisplay(holder)
            } catch (e: IOException) {
            }
        }
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, w: Int, h: Int) {
        if (mHolder.surface == null) {
            return
        }
        try {
            mCamera.stopPreview()
        } catch (e: Exception) {
        }
        mCamera.apply {
            try {
                setPreviewDisplay(mHolder)
                startPreview()
            } catch (e: Exception) {
            }
        }
    }

    fun stopPreview() {
        try {
            mCamera.stopPreview()
        } catch (e: Exception) {

        }
    }

    fun startPreview() {
        try {
            mCamera.startPreview()
        } catch (e: Exception) {

        }
    }

    fun release() {
        mCamera.release()
    }
}
package com.palmapp.master.baselib.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import android.widget.FrameLayout
import com.palmapp.master.baselib.utils.AppUtil

/**
 *
 * @author :     xiemingrui
 * @since :      2019/8/27
 */
class ShareView(context: Context) : FrameLayout(context) {
    private val IMAGE_WIDTH = AppUtil.getScreenW(context)
    private val IMAGE_HEIGHT = AppUtil.getScreenH(context)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    fun createBitmap(): Bitmap {
        val widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(IMAGE_WIDTH, View.MeasureSpec.AT_MOST)
        val heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(IMAGE_HEIGHT, View.MeasureSpec.AT_MOST)
        measure(widthMeasureSpec, heightMeasureSpec)
        layout(0, 0, measuredWidth, measuredHeight)
        val bitmap = Bitmap.createBitmap(
            measuredWidth,
            measuredHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        draw(canvas)
        return bitmap
    }
}
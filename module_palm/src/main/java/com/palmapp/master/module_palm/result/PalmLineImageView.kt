package com.palmapp.master.module_palm.result

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import com.palmapp.master.baselib.bean.XPoint
import com.palmapp.master.baselib.constants.AppConstants
import com.palmapp.master.baselib.utils.AppUtil
import com.palmapp.master.baselib.view.PalmImageView
import com.palmapp.master.module_palm.scan.PalmImageManager

/**
 *
 * @author :     xiemingrui
 * @since :      2019/8/16
 */
class PalmLineImageView(context: Context, attributeSet: AttributeSet) :
    PalmImageView(context, attributeSet) {
    private val bitmaps = arrayListOf<Bitmap?>()
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        for (bitmap in bitmaps) {
            if (bitmap != null) {
                canvas?.drawBitmap(bitmap, imageMatrix, null)
            }
        }
    }

    fun setData(bitmap: Bitmap?) {
        bitmaps.clear()
        bitmaps.add(bitmap)
        invalidate()
    }

    fun setDatas(bitmap: List<Bitmap?>) {
        bitmaps.clear()
        bitmaps.addAll(bitmap)
        invalidate()
    }
}
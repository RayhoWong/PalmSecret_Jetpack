package com.palmapp.master.baselib.view

import android.graphics.*
import android.graphics.drawable.Drawable

/**
 * @Description 渐变色圆角边框
 *
 * @Author zhangl
 * @Date 2019-08-21
 *
 */
class GradientBorderDrawable: Drawable {

    var srokeWidth = 5.0f

    var radius:Float = 0.0f

    var width:Float = 0.0f

    var height:Float = 0.0f

    var startColor:Int = Color.WHITE

    var endColor:Int = Color.WHITE

    val paint:Paint = Paint()

    constructor(srokeWidth:Float, radius:Float, startColor:Int, endColor:Int) {
        this.srokeWidth = srokeWidth
        this.radius = radius
        this.startColor = startColor
        this.endColor = endColor
        this.paint.color = Color.WHITE
        this.paint.isAntiAlias = true
        this.paint.strokeWidth = srokeWidth
        this.paint.style = Paint.Style.STROKE
    }

    override fun draw(canvas: Canvas) {

        val rec = RectF(srokeWidth / 2, srokeWidth / 2, width - srokeWidth, height - srokeWidth)

        canvas?.let {
            if (radius == -1.0f) {
                radius = height * 0.2f
            }
            it.drawRoundRect(rec, radius, radius, paint)
        }
    }

    override fun setAlpha(alpha: Int) {

    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {

    }

    override fun onBoundsChange(bounds: Rect?) {
        bounds?.let {
            this.width = it.width()?.toFloat()
            this.height = it.height()?.toFloat()
            val linearGradient = LinearGradient(0f, 0f, this.width , 0f, startColor, endColor, Shader.TileMode.CLAMP)
            paint.shader = linearGradient
        }
        super.onBoundsChange(bounds)
    }

}
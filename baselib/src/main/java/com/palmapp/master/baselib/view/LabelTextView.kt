package com.palmapp.master.baselib.view

import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.palmapp.master.baselib.R

/**
 * @Description 渐变圆角边框
 *
 * @Author zhangl
 * @Date 2019-08-21
 *
 */
class LabelTextView: AppCompatTextView {

    var srokeWidth = 5.0f

    var startColor:Int = Color.WHITE

    var endColor:Int = Color.WHITE

    var radius:Float = 0.0f

    private val paint: Paint = Paint()
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0)
    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : super(context, attributeSet, defStyleAttr) {

        if (attributeSet != null) {
            val array: TypedArray = context.obtainStyledAttributes(attributeSet, R.styleable.LabelTextView)
            radius = array.getDimensionPixelSize(R.styleable.LabelTextView_radius, 0).toFloat()
            startColor = array.getColor(R.styleable.LabelTextView_startColor, Color.WHITE)
            endColor = array.getColor(R.styleable.LabelTextView_endColor, Color.WHITE)
            srokeWidth = array.getDimensionPixelSize(R.styleable.LabelTextView_srokeWidth, 5).toFloat()
            array.recycle()
        }


        paint.color = Color.WHITE
        paint.isAntiAlias = true
        paint.strokeWidth = srokeWidth
        paint.style = Paint.Style.STROKE
    }


    override fun onDraw(canvas: Canvas?) {

        val linearGradient = LinearGradient(0f, 0f, this.width - srokeWidth, 0f, startColor, endColor, Shader.TileMode.CLAMP)
        paint.shader = linearGradient

        val rec = RectF(srokeWidth / 2, srokeWidth / 2, width - srokeWidth, height - srokeWidth)

        canvas?.let {
            if (radius == -1.0f) {
                radius = height * 0.2f
            }
            it.drawRoundRect(rec, radius, radius, paint)
        }

        super.onDraw(canvas)
    }

}
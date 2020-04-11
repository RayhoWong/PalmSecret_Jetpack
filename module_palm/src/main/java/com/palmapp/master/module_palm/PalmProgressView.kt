package com.palmapp.master.module_palm

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.opengl.ETC1.getHeight
import android.opengl.ETC1.getWidth
import android.opengl.ETC1.getHeight
import android.opengl.ETC1.getWidth


/**
 *
 * @author :     xiemingrui
 * @since :      2019/9/26
 */
class PalmProgressView(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {
    val bgPaint = Paint()
    val linePaint = Paint()
    val textPaint = Paint()
    var startColor = -1
    var endColor = -1
    val rectF = RectF()
    val strokeWidth = resources.getDimension(R.dimen.change_18px)
    var progress = 0
        set(value) {
            field = value
            invalidate()
        }

    init {
        val ta = context.obtainStyledAttributes(attributeSet, R.styleable.PalmProgressView)
        startColor = ta.getColor(R.styleable.PalmProgressView_palm_startColor, Color.WHITE)
        endColor = ta.getColor(R.styleable.PalmProgressView_palm_endColor, Color.WHITE)
        ta.recycle()

        linePaint.style = Paint.Style.STROKE
        linePaint.isAntiAlias = true
        linePaint.isDither = true
        linePaint.strokeWidth = strokeWidth
        linePaint.strokeCap = Paint.Cap.ROUND

        bgPaint.style = Paint.Style.STROKE
        bgPaint.isAntiAlias = true
        bgPaint.isDither = true
        bgPaint.strokeWidth = strokeWidth
        bgPaint.strokeCap = Paint.Cap.ROUND
        bgPaint.color = Color.parseColor("#1ac8e8ff")

        textPaint.isAntiAlias = true
        textPaint.textSize = resources.getDimension(R.dimen.change_84px)
        textPaint.color = resources.getColor(R.color.theme_text_color)
        textPaint.textAlign = Paint.Align.CENTER
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val linearGradient =
            LinearGradient(0f, h / 2f, w.toFloat(), h / 2f, startColor, endColor, Shader.TileMode.CLAMP)

        val matrix = Matrix()
        matrix.setRotate(180f, w.toFloat(), h.toFloat())
        linePaint.shader = linearGradient
        rectF.set(
            0f + strokeWidth / 2,
            0f + strokeWidth / 2,
            w.toFloat() - strokeWidth / 2,
            (h.toFloat() - strokeWidth) * 2
        )
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawArc(rectF, 180f, 180f , false, bgPaint)
        canvas?.drawArc(rectF, 180f, 180f * progress / 100, false, linePaint)

        canvas?.drawText(String.format("%d%%",progress), width / 2f, height.toFloat() - strokeWidth, textPaint)

    }

    fun startAnim(endValue: Int) {
        val anim = ObjectAnimator.ofInt(this, "progress", 0, endValue)
        anim.duration = 1000
        anim.start()
    }
}
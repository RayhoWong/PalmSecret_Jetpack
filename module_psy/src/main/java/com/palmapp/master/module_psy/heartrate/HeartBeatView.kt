package com.palmapp.master.module_psy.heartrate

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator


/**
 *
 * @author :     xiemingrui
 * @since :      2020/3/23
 */
class HeartBeatView(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {
    private lateinit var shader: LinearGradient
    private var mMatrix = Matrix()
    private val MAX_LENGTH = 1000
    private var initH = 0f
    private var initW = 0f
    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val path = Path()
    private val mDst = Path()
    private val pathMeasure = PathMeasure()
    private val valueAnimator = ValueAnimator.ofFloat(0f, 1f)
    private var progress = 0f
    private var mLength = 0f

    init {
        linePaint.color = Color.WHITE
        linePaint.strokeCap = Paint.Cap.ROUND
        linePaint.strokeWidth = 5f
        linePaint.style = Paint.Style.STROKE
        valueAnimator.duration = 3000
        valueAnimator.addUpdateListener {
            progress = it.animatedValue as Float
            postInvalidate()
        }
        valueAnimator.interpolator = LinearInterpolator()
        valueAnimator.repeatCount = ValueAnimator.INFINITE
        valueAnimator.repeatMode = ValueAnimator.RESTART
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        initH = h.toFloat()
        initW = w.toFloat()
        path.moveTo(0f, initH * 0.68f)
        shader = LinearGradient(0f, initH / 2, 0.219f * initW, initH / 2, Color.TRANSPARENT, Color.WHITE, Shader.TileMode.CLAMP)
        val wL = 0.219f * initW
        for (i in 0..5) {
            if (wL * i > initW) {
                break
            }
            moveTo(0.055f, 0.68f, (wL * i))
            moveTo(0.063f, 0.55f, (wL * i))
            moveTo(0.073f, 0.77f, (wL * i))
            moveTo(0.088f, 0.18f, (wL * i))
            moveTo(0.108f, 0.92f, (wL * i))
            moveTo(0.125f, 0.55f, (wL * i))
            moveTo(0.125f, 0.55f, (wL * i))
            moveTo(0.136f, 0.68f, (wL * i))
            moveTo(0.219f, 0.68f, (wL * i))
        }
//        path.lineTo(0f,initH * 0.68f)
        linePaint.shader = shader
        pathMeasure.setPath(path, false)
        mLength = pathMeasure.length
        valueAnimator.start()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        valueAnimator.cancel()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(suggestedMinimumHeight, MeasureSpec.EXACTLY))
    }

    private fun moveTo(wP: Float, hP: Float, offset: Float) {
        path.lineTo(wP * initW + offset, hP * initH)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.translate(progress * initW / 4 - 0.219f * initW, 0f)
        mMatrix.setTranslate(progress * initW, 0f)
        shader.setLocalMatrix(mMatrix)
        var start = 0f
        var end = 0f
        start = progress * mLength
        end = start + MAX_LENGTH
        mDst.rewind()
        pathMeasure.getSegment(start, end, mDst, true)
        canvas?.drawPath(mDst, linePaint)
    }
}
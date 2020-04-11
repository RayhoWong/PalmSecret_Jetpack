package com.palmapp.master.baselib.view

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import com.palmapp.master.baselib.R

/**
 *
 * @author :     xiemingrui
 * @since :      2019/8/26
 */
class PalmLoadingView(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {
    private val mWidth = resources.getDimension(R.dimen.change_54px)
    private val mHeight = resources.getDimension(R.dimen.change_46px)
    private val linePaint = Paint()
    private val path1 = Path()
    private val path2 = Path()
    private val matrix1 = Matrix()
    private val matrix2 = Matrix()
    private var CurDegree: Float = 0f
    private var times = 1
    private val animator: Animator
    init {
        linePaint.isAntiAlias = true
        linePaint.isDither = true
        linePaint.strokeWidth = resources.getDimensionPixelOffset(R.dimen.change_3px).toFloat()
        linePaint.style = Paint.Style.STROKE
        linePaint.shader = LinearGradient(
            0f,
            0f,
            0f,
            mWidth.toFloat(),
            Color.parseColor("#b5f0ff"),
            Color.parseColor("#5bacff"),
            Shader.TileMode.CLAMP
        )


        animator = ValueAnimator.ofFloat(0f, 30f)
        animator.duration = 200
        animator.interpolator = LinearInterpolator()
        animator.addUpdateListener {
            rotate(it.animatedValue as Float)
        }

        animator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {

                CurDegree = times * 30f
                times++
                if (CurDegree == 360f) {
                    CurDegree = 0f
                    times = 1
                }
                animation?.startDelay = 400L
                animation?.start()
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
            }
        })

        animator.start()
    }

    private fun rotate(degree: Float) {
        path1.rewind()
        path2.rewind()
        matrix1.setRotate(Math.abs(CurDegree + degree), width / 2f, height / 2f)
        matrix2.setRotate(Math.abs(CurDegree + degree) * -1, width / 2f, height / 2f)
        path1.drawTriangle()
        path2.drawTriangle()
        path1.transform(matrix1)
        path2.transform(matrix2)
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val spec = MeasureSpec.makeMeasureSpec((mWidth * 2).toInt(), MeasureSpec.EXACTLY)
        super.onMeasure(spec, spec)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawPath(path1, linePaint)
        canvas?.drawPath(path2, linePaint)
    }

    private fun Path.drawTriangle() {
        this.moveTo(mWidth, mHeight / 2f)
        this.lineTo(mWidth / 2f, mHeight + mHeight / 2f)
        this.lineTo(mWidth + mWidth / 2f, mHeight + mHeight / 2f)
        this.lineTo(mWidth, mHeight / 2f)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        animator.cancel()
        animator.removeAllListeners()
    }
}
package com.palmapp.master.baselib.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import com.palmapp.master.baselib.R

/**
 * @Description 扫描动画
 *
 * @Author zhangl
 * @Date 2019-08-21
 *
 */
class ScanAnimatorView (context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {
    private val mPaintHand = Paint(Paint.ANTI_ALIAS_FLAG)
    private var mGrid: Bitmap
    private var mScan: Bitmap
    private var animator: ValueAnimator
    private val sTop = resources.getDimension(R.dimen.change_268px)
    private var mTop = sTop
    private val clipRect: Rect
    private var isRunning:Boolean = false
    init {
        mScan = BitmapFactory.decodeResource(resources, R.mipmap.scaning_pic_scan)
        mGrid = BitmapFactory.decodeResource(resources, R.mipmap.scaning_pic_grid)
        clipRect = Rect()
        animator = ValueAnimator.ofFloat(0f, 1f).setDuration(2000)
        animator.interpolator = LinearInterpolator()
        animator.repeatCount = ValueAnimator.INFINITE
        animator.repeatMode = ValueAnimator.RESTART
        animator.addUpdateListener {
            val v = it.animatedValue as Float
            setProgress(v)
        }
        visibility = GONE
    }

    fun startScan() {
        isRunning = true
        animator.start()
        visibility = VISIBLE
    }

    fun stopScan(){
        isRunning = false
        animator.cancel()
        visibility = GONE
    }

    private fun setProgress(p: Float) {
        mTop = p * (this.height + sTop)
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (isRunning) {
            canvas?.let {
                clipRect.set(
                    (width - mGrid.width) / 2,
                    sTop.toInt(),
                    (width - mGrid.width) / 2 + mGrid.width,
                    this.height + sTop.toInt()
                )
                canvas.clipRect(clipRect)
                canvas.drawBitmap(mGrid, (width - mGrid.width) / 2f, mTop, mPaintHand)
                canvas.drawBitmap(mScan, (width - mScan.width) / 2f, mTop + (mGrid.height - mScan.height), mPaintHand)
            }
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        animator.cancel()
    }
}
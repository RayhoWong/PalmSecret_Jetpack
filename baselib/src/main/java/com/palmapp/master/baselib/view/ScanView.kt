package com.palmapp.master.baselib.view

import android.animation.ValueAnimator
import android.animation.ValueAnimator.INFINITE
import android.animation.ValueAnimator.RESTART
import android.content.Context
import android.graphics.*
import android.graphics.Canvas.ALL_SAVE_FLAG
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import com.palmapp.master.baselib.R
import com.palmapp.master.baselib.utils.AppUtil

/**
 *
 * @author :     xiemingrui
 * @since :      2019/8/8
 */
class ScanView(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {
    private var isChange: Boolean = false
    private val mPaintMask = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mPaintHand = Paint(Paint.ANTI_ALIAS_FLAG)
    var mBorder: Bitmap
    var mMask: Bitmap

    private var mGrid: Bitmap
    private var mScan: Bitmap
    private var animator: ValueAnimator
    private var sTop = resources.getDimension(R.dimen.change_168px)
    private var mTop = sTop
    private val xDstOut = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
    private val xSrcIn = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    private val clipRect: Rect
    private var isScanning = false
    private val m = Matrix()
    private var mScale = 1f

    init {
        val ta = context.obtainStyledAttributes(attributeSet, R.styleable.ScanView)
        mBorder = BitmapFactory.decodeResource(resources, ta.getResourceId(R.styleable.ScanView_border, -1))
        mMask = BitmapFactory.decodeResource(resources, ta.getResourceId(R.styleable.ScanView_mask, -1))
        mScale = ta.getFloat(R.styleable.ScanView_scale, 1f)
        ta.recycle()
        mScan = BitmapFactory.decodeResource(resources, R.mipmap.scaning_pic_scan)
        mGrid = BitmapFactory.decodeResource(resources, R.mipmap.scaning_pic_grid)
//        if (mScale != 1f) {
            val w = (AppUtil.getScreenW(context) * mScale).toInt()
            mBorder = Bitmap.createScaledBitmap(
                mBorder,
                w,
                ((w.toFloat() / mBorder.width.toFloat()) * mBorder.height).toInt(),
                false
            )
            mMask = Bitmap.createScaledBitmap(
                mMask,
                w,
                ((w.toFloat() / mMask.width.toFloat()) * mMask.height).toInt(),
                false
            )
            mScan = Bitmap.createScaledBitmap(
                mScan,
                w,
                ((w.toFloat() / mScan.width.toFloat()) * mScan.height).toInt(),
                false
            )
            mGrid = Bitmap.createScaledBitmap(
                mGrid,
                w,
                ((w.toFloat() / mGrid.width.toFloat()) * mGrid.height).toInt(),
                false
            )
//        }
        clipRect = Rect()
        animator = ValueAnimator.ofFloat(0f, 1f).setDuration(2000)
        animator.interpolator = LinearInterpolator()
        animator.repeatCount = INFINITE
        animator.repeatMode = RESTART
        animator.addUpdateListener {
            val v = it.animatedValue as Float
            setProgress(v)
        }
    }

    fun startScan() {
        isScanning = true
        animator.start()
    }

    fun stopScan() {
        isScanning = false
        animator.cancel()
        invalidate()
    }

    fun setTop(top: Float) {
        sTop = top
        invalidate()
    }

    private fun setProgress(p: Float) {
        mTop = p * (mBorder.height + sTop)
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let {
            clipRect.set(
                (width - mGrid.width) / 2,
                sTop.toInt(),
                (width - mGrid.width) / 2 + mGrid.width,
                mBorder.height + sTop.toInt()
            )
            if (isScanning) {
                canvas.clipRect(clipRect)
                canvas.drawBitmap(mGrid, (width - mGrid.width) / 2f, mTop, mPaintHand)
                canvas.drawBitmap(mScan, (width - mScan.width) / 2f, mTop + (mGrid.height - mScan.height), mPaintHand)
                return
            }
            canvas.saveLayer(0f, 0f, width.toFloat(), height.toFloat(), null, ALL_SAVE_FLAG)
            canvas.drawColor(Color.parseColor("#9b000000"))
            mPaintMask.xfermode = xDstOut
            m.reset()
            if (isChange) {
                m.postScale(-1f, 1f);   //镜像水平翻转
                m.postTranslate(mMask.width.toFloat(), 0f)
            }
            m.postTranslate(
                (width - mMask.width) / 2f,
                sTop
            )
            canvas.drawBitmap(
                mMask,
                m,
                mPaintMask
            )
            mPaintMask.xfermode = null
            canvas.restore()
            m.reset()
            if (isChange) {
                m.postScale(-1f, 1f);   //镜像水平翻转
                m.postTranslate(mBorder.width.toFloat(), 0f)
            }
            m.postTranslate(
                (width - mBorder.width) / 2f,
                sTop
            )
            canvas.drawBitmap(mBorder, m, mPaintHand)

        }
    }

    fun setChangeSide() {
        isChange = !isChange
        invalidate()
    }

    fun setScale() {

    }
}
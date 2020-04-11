package com.palmapp.master.baselib.view

import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import com.palmapp.master.baselib.R

open class PalmImageView : AppCompatImageView {
    companion object {
        val SHAPE_RECTANGLE = 1
        val SHAPE_OVAL = 2

        val CORNER_TOP_LEFT = 0
        val CORNER_TOP_RIGHT = 2
        val CORNER_BOTTOM_RIGHT = 4
        val CORNER_BOTTOM_LEFT = 6

        val MESURE_TYPE_MAX = 1
        val MESURE_TYPE_MIN = 2
        val MESURE_TYPE_WIDTH = 3
        val MESURE_TYPE_HEIGHT = 4

    }

    private val mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var mViewHeight = 0f
    private var mViewWidth = 0f
    private var mShape = 0
    private var mCornerRadius = FloatArray(8)
    private var mCorner = 0f
    private var mMeasureType = 0
    open var mPressColor = 0
    private var mPath = Path()
    private var mClipPath = Path()
    private var mRadius = 0f
    private var mRectF = RectF()
    private lateinit var mXfermode: Xfermode
    private var mIsNeedClip = false

    private var viewWidth = 0
    private var viewHeight = 0

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        val typedArray: TypedArray? = context?.obtainStyledAttributes(attrs, R.styleable.PalmImageView)
        typedArray?.let {
            mCorner = typedArray.getDimension(R.styleable.PalmImageView_giv_radius, 0f)
            updateAttr(CORNER_TOP_LEFT, typedArray.getDimension(R.styleable.PalmImageView_giv_radius_left_top, 0f))
            updateAttr(CORNER_TOP_RIGHT, typedArray.getDimension(R.styleable.PalmImageView_giv_radius_right_top, 0f))
            updateAttr(CORNER_BOTTOM_LEFT, typedArray.getDimension(R.styleable.PalmImageView_giv_radius_left_bottom, 0f))
            updateAttr(CORNER_BOTTOM_RIGHT, typedArray.getDimension(R.styleable.PalmImageView_giv_radius_right_bottom, 0f))
            mShape = typedArray.getInt(R.styleable.PalmImageView_giv_shape, SHAPE_RECTANGLE)
            mMeasureType = typedArray.getInt(R.styleable.PalmImageView_giv_measure, 0)
            mPressColor = typedArray.getColor(R.styleable.PalmImageView_giv_press_background, 0)
            typedArray.recycle()
        }
    }

    private fun updateAttr(index: Int, result: Float) {
        mCornerRadius[index] = result
        mCornerRadius[index + 1] = result
    }

    override fun draw(canvas: Canvas?) {
        canvas?.let {
            if (mIsNeedClip) {
                canvas.saveLayer(0f, 0f, mViewWidth, mViewHeight, null, Canvas.ALL_SAVE_FLAG)
                super.draw(canvas)
                mPaint.style = Paint.Style.FILL
                mPaint.xfermode = mXfermode
                mPaint.color = Color.BLACK
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O) {
                    canvas.drawPath(mPath, mPaint)
                } else {
                    canvas.drawPath(mClipPath, mPaint)
                }
                canvas.restore()
            } else {
                super.draw(canvas)
            }
            if (isPressed && mPressColor != 0) {
                mPaint.xfermode = null
                mPaint.color = mPressColor
                canvas.drawPath(mPath, mPaint)
            }
        }
    }

    fun setSize(w: Int, h: Int) {
        viewWidth = w
        viewHeight = h
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var widthSize = View.MeasureSpec.getSize(widthMeasureSpec)
        var widthMode = View.MeasureSpec.getMode(widthMeasureSpec)
        var heightSize = View.MeasureSpec.getSize(heightMeasureSpec)
        var heightMode = View.MeasureSpec.getMode(heightMeasureSpec)
        val drawable: Drawable? = drawable
        widthSize = getSize(widthMode, widthSize, drawable?.intrinsicWidth
                ?: 0, paddingLeft + paddingRight)
        heightSize = getSize(heightMode, heightSize, drawable?.intrinsicHeight
                ?: 0, paddingTop + paddingBottom)
        if (viewWidth != 0) {
            widthSize = viewWidth
            widthMode = View.MeasureSpec.EXACTLY
        }
        if (viewHeight != 0) {
            heightSize = viewHeight
            heightMode = View.MeasureSpec.EXACTLY
        }
        if(mMeasureType!=0){
            widthMode = View.MeasureSpec.EXACTLY
            heightMode = View.MeasureSpec.EXACTLY
        }
        when (mMeasureType) {
            MESURE_TYPE_MAX -> {
                widthSize = Math.max(widthSize, heightSize)
                heightSize = Math.max(widthSize, heightSize)
            }
            MESURE_TYPE_MIN -> {
                widthSize = Math.min(widthSize, heightSize)
                heightSize = Math.min(widthSize, heightSize)
            }
            MESURE_TYPE_WIDTH -> heightSize = widthSize
            MESURE_TYPE_HEIGHT -> widthSize = heightSize
        }

        super.onMeasure(View.MeasureSpec.makeMeasureSpec(widthSize, widthMode), View.MeasureSpec.makeMeasureSpec(heightSize, heightMode))
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        Log.d("GOMSUIC", "onSizeChanged")
        super.onSizeChanged(w, h, oldw, oldh)
        mViewHeight = h.toFloat()
        mViewWidth = w.toFloat()
        mRadius = (if (mViewHeight > mViewWidth) mViewWidth else mViewHeight) / 2f
        mRectF.set(0f, 0f, mViewWidth, mViewHeight)
        mPath.reset()
        mClipPath.reset()
        if (mShape == SHAPE_OVAL) {
            mIsNeedClip = true
            mPath.addCircle(mViewWidth / 2, mViewHeight / 2, mRadius, Path.Direction.CW)
        } else {
            if (mCorner > 0) {
                mIsNeedClip = true
                mPath.addRoundRect(mRectF, mCorner, mCorner, Path.Direction.CW)
            } else if (anyExist(mCornerRadius)) {
                mIsNeedClip = true
                mPath.addRoundRect(mRectF, mCornerRadius, Path.Direction.CW)
            } else {
                mPath.addRect(mRectF, Path.Direction.CW)
            }
        }
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O) {
            mXfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
        } else {
            //这玩意有个bug，drawPath方法在9.0将不会把整个Rect画一遍 https://issuetracker.google.com/issues/111819103
            mXfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
            mClipPath.addRect(mRectF, Path.Direction.CW)
            mClipPath.op(mPath, Path.Op.DIFFERENCE)
        }
    }

    private fun anyExist(array: FloatArray) = array.any { it > 0f }
    private fun getSize(mode: Int, size: Int, drawable: Int, padding: Int) = if (mode != View.MeasureSpec.EXACTLY) drawable + padding else size


    override fun setPressed(pressed: Boolean) {
        super.setPressed(pressed)
        if (mPressColor != 0)
            invalidate()
    }
}
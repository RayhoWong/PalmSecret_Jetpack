package com.palmapp.master.baselib.view

import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.widget.FrameLayout
import com.palmapp.master.baselib.R

class RoundLayout : FrameLayout {

    private var roundLayoutRadius:Float = 0f
    private var isCycle:Boolean = false
    private lateinit var roundPath: Path
    private lateinit var rectF:RectF
    private lateinit var mXfermode:Xfermode
    private lateinit var mClipPath: Path
    private lateinit var mPaint: Paint
    private var mRadiiArray:FloatArray? = null



    constructor(context: Context) : this(context, null)
    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0)
    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : super(context, attributeSet, defStyleAttr) {
        init(context, attributeSet)
    }

    fun init(context: Context, attributeSet: AttributeSet?) {
        if (attributeSet != null) {
            val array: TypedArray = context.obtainStyledAttributes(attributeSet, R.styleable.RoundLayout)
            roundLayoutRadius = array.getDimensionPixelSize(R.styleable.RoundLayout_radius, getResources().getDimensionPixelSize(R.dimen.change_24px)).toFloat()
            isCycle = array.getBoolean(R.styleable.RoundLayout_cycle, false)

            if (roundLayoutRadius != 0f) {
                mRadiiArray = floatArrayOf(roundLayoutRadius,roundLayoutRadius,roundLayoutRadius,roundLayoutRadius,roundLayoutRadius,roundLayoutRadius,roundLayoutRadius,roundLayoutRadius)
            } else {
                val topLeftRadius = array.getDimensionPixelSize(R.styleable.RoundLayout_topLeftRadius, 0).toFloat()
                val topRightRadius = array.getDimensionPixelSize(R.styleable.RoundLayout_topRightRadius, 0).toFloat()
                val bottomLeftRadius = array.getDimensionPixelSize(R.styleable.RoundLayout_bottomLeftRadius, 0).toFloat()
                val bottomRightRadius = array.getDimensionPixelSize(R.styleable.RoundLayout_bottomRightRadius, 0).toFloat()
                mRadiiArray = floatArrayOf(topLeftRadius,topLeftRadius,topRightRadius,topRightRadius,bottomRightRadius,bottomRightRadius,bottomLeftRadius,bottomLeftRadius)
            }

            array.recycle()
        }
        setWillNotDraw(false)
        roundPath = Path()
        mClipPath = Path()
        rectF = RectF()
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    }

    private fun setPath() {
        if (isCycle) {
            roundPath.addCircle(rectF.centerX(), rectF.centerY(), rectF.width() * 0.5f, Path.Direction.CW)
        } else {
            mRadiiArray?.let {
                if (it.size == 8) {
                    roundPath.addRoundRect(rectF, mRadiiArray, Path.Direction.CW)
                } else {
                    roundPath.addRoundRect(rectF, roundLayoutRadius, roundLayoutRadius, Path.Direction.CW)
                }
            }
        }
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O) {
            mXfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
        } else {
            //这玩意有个bug，drawPath方法在9.0将不会把整个Rect画一遍 https://issuetracker.google.com/issues/111819103
            mXfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
            mClipPath.addRect(rectF, Path.Direction.CW)
            mClipPath.op(roundPath, Path.Op.DIFFERENCE)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        rectF.set(0f,0f, w.toFloat(), h.toFloat())
        this.setPath()
    }

    override fun draw(canvas: Canvas?) {
        val save = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            canvas?.saveLayer(0f, 0f, rectF.width(), rectF.height(), null)
        } else {
            canvas?.saveLayer(0f, 0f, rectF.width(), rectF.height(), null, Canvas.ALL_SAVE_FLAG)
        }

        super.draw(canvas)
        mPaint.setXfermode(mXfermode)
        mPaint.setColor(Color.BLACK)
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O) {
            canvas?.drawPath(roundPath, mPaint)
        } else {
            canvas?.drawPath(mClipPath, mPaint)
        }
        save?.let {
            canvas?.restoreToCount(save)
        }
    }

    fun getRoundLayoutRadius(): Float {
        return roundLayoutRadius
    }

    fun setRadiiArray(radiiArray: FloatArray) {
        this.mRadiiArray = radiiArray
        postInvalidate()
    }
}
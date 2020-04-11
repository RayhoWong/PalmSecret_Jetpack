package com.palmapp.master.baselib.view

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.graphics.Canvas.ALL_SAVE_FLAG
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.LinearLayout
import com.palmapp.master.baselib.R


/**
 * @Description 手掌动画
 *
 * @Author zhangl
 * @Date 2019-08-21
 *
 */
class FaceAnimatorView(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {
    //    private var mSourceRect: Rect? = null
    private var rect: Rect? = null
    private var rectF: RectF? = null
    private var mScan: Bitmap
    private val mPaintHand = Paint(Paint.ANTI_ALIAS_FLAG)
    private var animator: ValueAnimator? = null

    private var points: MutableList<MutableList<PointF>>? = null
    private var mSource: Bitmap? = null

    private var mDisplayRect: RectF? = null

    private var mHeight = 0
    private var mWidth: Int = 0
    private val mPaint: Paint
    private val mBitmapPaint: Paint
    private val mModePaint: Paint

    private var mScanSrcRect: Rect
    private val mScanDstRect = Rect()
    /**
     * 正在绘画的path
     */
    private var mPathMeasure = PathMeasure()

    private var mPointBitmap: Bitmap? = null

    private var listener: OnAnimUpdateListener? = null

    private var scale: Float = 0f

    init {
        mModePaint = Paint()
        mModePaint.style = Paint.Style.FILL
        mModePaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        mModePaint.isAntiAlias = true

        mBitmapPaint = Paint()
        mBitmapPaint.isAntiAlias = true
        mBitmapPaint.color = Color.parseColor("#ffffff")
        mBitmapPaint.style = Paint.Style.FILL
        mPointBitmap = BitmapFactory.decodeResource(this.resources, R.mipmap.facial_scan_pic_point)
        mPaint = Paint()
        mPaint.color = Color.parseColor("#80ffffff")
        mPaint.isAntiAlias = true
        mPaint.style = Paint.Style.STROKE

        mPaint.strokeWidth = 2.0f


        mScan = BitmapFactory.decodeResource(resources, R.mipmap.scaning_pic_scan)
        this.mScanSrcRect = Rect(0, 0, mScan.width, mScan.height)

    }

    private val mPaths = ArrayList<Path>()

    fun setData(bitmap: Bitmap?) {
        animator?.cancel()
        this.mSource = bitmap
        val maxValue = resources.getDimensionPixelOffset(R.dimen.change_960px)
        scale =  maxValue / mSource!!.width.coerceAtLeast(mSource!!.height).toFloat()
        scale = scale.coerceAtMost(1.0f)
        rect = Rect(0, 0, mSource!!.width, mSource!!.height)
        rectF = RectF(rect)
        animator = ValueAnimator.ofInt(0, 1000).setDuration(1000)
        animator?.interpolator = LinearInterpolator()
        animator?.addUpdateListener {
            mCurrentProgress = it.animatedValue as Int
            postInvalidate()
        }
        animator?.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                if (points != null) {
                    startAnim()
                } else {
                    setData(bitmap)
                }
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
            }

        })

        val params = layoutParams as LinearLayout.LayoutParams
        params.gravity = Gravity.CENTER_HORIZONTAL
        params.width = mSource!!.width
        params.height = mSource!!.height
        layoutParams = params
        animator?.start()
    }

    fun setData(bitmap: Bitmap?, points: MutableList<MutableList<PointF>>, listener: OnAnimUpdateListener) {
        mPaths.clear()
        for ((curr, point) in points.withIndex()) {
            val path = Path()
            var isClose = false
            var firstPoint: PointF? = null
            for ((index, item) in point.withIndex()) {
                if (index == 0 || isClose) {
                    path.moveTo(item.x, item.y)
                    isClose = false
                    firstPoint = item
                } else if(item.x == -1f && item.y == -1f) {
                    firstPoint?.let {
                        path.lineTo(it.x, it.y)
                    }
                    isClose = true
                } else {
                    path.lineTo(item.x, item.y)
                }
            }
            firstPoint?.let {
                path.lineTo(it.x, it.y)
            }
            mPaths.add(path)
        }
        this.mSource = bitmap
        this.points = points
        this.listener = listener

    }

    fun stopAnim() {
        animator?.cancel()
    }

    private fun startAnim() {
        rect = Rect(0, 0, mSource!!.width, mSource!!.height)
        rectF = RectF(rect)

//        mSourceRect = Rect(0,0,mSource!!.width, mSource!!.height)
        if(points != null) {
            animator = ValueAnimator.ofInt(1000, (points!!.size + 1) * 1000).setDuration(5000)
            animator?.interpolator = LinearInterpolator()
            animator?.addUpdateListener {
                mCurrentProgress = it.animatedValue as Int
                postInvalidate()
                listener?.onUpdate(mCurrentProgress)
            }
            animator?.addListener(object : Animator.AnimatorListener{
                override fun onAnimationRepeat(animation: Animator?) {
                }

                override fun onAnimationEnd(animation: Animator?) {
                    listener?.onUpdate(mCurrentProgress)
                    listener?.onAnimEnd()
                }

                override fun onAnimationCancel(animation: Animator?) {
                }

                override fun onAnimationStart(animation: Animator?) {
                }

            })
            Log.d(this.javaClass.simpleName, "mSource:$mSource,points:$points")
            animator?.start()
        }
    }

    interface OnAnimUpdateListener {
        fun onAnimEnd()
        fun onUpdate(value: Int)
    }

    private var mCurrentProgress = 0




    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        Log.d(this.javaClass.simpleName, "w:$w,h:$h")
        mWidth = w
        mHeight = h

    }

    private fun drawPoint(canvas: Canvas, point: PointF, alpha: Int = 100) {
        mPointBitmap?.let {
            mPaint.alpha = alpha
            if(point.x != -1f && point.y != -1f) {
                canvas.drawBitmap(
                    it,
                    point.x - it.width / 2,
                    point.y - it.height / 2,
                    mPaint
                )
            }
        }
    }

    private val mDst = Path()

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (mSource != null) {
            canvas?.let {
                it.save()
                it.scale(scale, scale)
                it.translate((mWidth - mWidth * scale) / 2.toFloat(),0f)

                val sc = canvas.saveLayer(0f, 0f, width.toFloat(), height.toFloat(), mBitmapPaint, ALL_SAVE_FLAG)

                rectF?.let { it1 -> canvas.drawRoundRect(it1, 48f, 48f, mBitmapPaint) }
                canvas.drawBitmap(mSource!!, 0f, 0f, mModePaint)
                canvas.restoreToCount(sc)
                if (mCurrentProgress < 1000) { // 扫描
                    mScanDstRect.left = 0
                    mScanDstRect.top = (mSource!!.height / 1000.toFloat() * mCurrentProgress).toInt()
                    mScanDstRect.right = mSource!!.width
                    mScanDstRect.bottom = (mSource!!.height / 1000.toFloat() * mCurrentProgress + mScan.height).toInt()
                    canvas.drawBitmap(mScan, mScanSrcRect, mScanDstRect, mPaintHand)
                }
                if (points != null) {
                    for ((index, pointArray) in points!!.withIndex()) {
                        val currentProgress = mCurrentProgress % 1000
                        val currentShowPosition = (mCurrentProgress / 1000 - 1)
                        if (index > currentShowPosition) {
                            break
                        }
                        for (point in pointArray) {
                            if (index == currentShowPosition) {
                                drawPoint(canvas, point, if (currentProgress <= 200) currentProgress / 2 else 100)
                            } else if (index < currentShowPosition) {
                                drawPoint(canvas, point)
                            }
                        }
                    }
                    for ((index, path) in mPaths.withIndex()) {
                        mPaint.alpha = 100
                        val currentProgress = mCurrentProgress % 1000
                        val currentShowPosition = (mCurrentProgress / 1000 - 1)
                        if (index > currentShowPosition) {
                            break
                        }
                        if (currentShowPosition == index && currentProgress > 200) {
                            mPathMeasure.setPath(mPaths[index], false)
//                            var length = mPathMeasure.length
                            while (mPathMeasure.nextContour()) {
                                val length = mPathMeasure.length
                                val progress = length * (currentProgress - 200) / 800
                                Log.d("hook", "pro$progress")
                                mDst.rewind()
                                mPathMeasure.getSegment(0f, progress, mDst, true)
                                canvas.drawPath(mDst, mPaint)
                            }

                        } else if (index < currentShowPosition) {
                            canvas.drawPath(path, mPaint)
                        }
                    }
                }
                // 画布状态回滚
                canvas.restore()
            }
        }
    }


    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        animator?.cancel()
    }
}
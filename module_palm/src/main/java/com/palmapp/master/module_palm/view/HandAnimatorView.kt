package com.palmapp.master.module_palm.view

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.LinearLayout
import com.palmapp.master.baselib.R
import com.palmapp.master.baselib.utils.LogUtil


/**
 * @Description 手掌动画
 *
 * @Author zhangl
 * @Date 2019-08-21
 *
 */
class HandAnimatorView(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {
    private var rectF: RectF? = null
    private var rect: Rect? = null
    private val mPaintHand = Paint(Paint.ANTI_ALIAS_FLAG)
    private var animator: ValueAnimator? = null
    private var mSource: Bitmap? = null

    private var mLine: List<Bitmap?>? = null
    private var mHeight = 0


    private var mCurrentProgress: Int = 0
    private var mWidth: Int = 0
    private val mPaint: Paint = Paint()
    private var scale: Float = 0f

    private var listener: OnAnimUpdateListener? = null

    interface OnAnimUpdateListener {
        fun onAnimEnd()
        fun onUpdate(value: Int)
    }

    private var mPointBitmap: Bitmap? = null
    private var isShowScan: Boolean = false

    private var mPathMeasure = PathMeasure()
    private val mDst = Path()

    private var mScanSrcRect: Rect
    private val mScanDstRect = Rect()
    private var mScan: Bitmap


    private val mBitmapPaint: Paint
    private val mModePaint: Paint = Paint()
    fun stopAnim() {
        animator?.cancel()
    }
    init {
        mModePaint.style = Paint.Style.FILL
        mModePaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        mModePaint.isAntiAlias = true

        mBitmapPaint = Paint()
        mBitmapPaint.isAntiAlias = true
        mBitmapPaint.color = Color.parseColor("#ffffff")
        mBitmapPaint.style = Paint.Style.FILL
        mBitmapPaint.strokeWidth = 4.0f

        mPaint.isAntiAlias = true
        mPaint.color = Color.parseColor("#80ffffff")
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeWidth = 4.0f

        mPointBitmap = BitmapFactory.decodeResource(this.resources, R.mipmap.facial_scan_pic_point)
        mScan = BitmapFactory.decodeResource(resources, R.mipmap.scaning_pic_scan)
        this.mScanSrcRect = Rect(0, 0, mScan.width, mScan.height)
    }

    data class Data(var points: List<PointF>, var path: Path)

    private val mAnim1 = ArrayList<Data>()
    private val mAnim2 = ArrayList<Data>()
    private val mAnim3 = ArrayList<Data>()


    fun setData(bitmap: Bitmap?, points: List<ArrayList<PointF>>?, line: List<Bitmap?>, listener: OnAnimUpdateListener? = null) {
        this.mSource = bitmap
        this.mLine = line
        this.listener = listener

        if (points != null) {
            val keyPoints = points[5]
            for(pointArray in points) {
                for(point in pointArray) {
                    point.x = point.x * mSource!!.width / 192f
                    point.y = point.y * mSource!!.width / 192f
                }
            }


            var path = Path()
            path.moveTo(keyPoints[3].x, keyPoints[8].y)
            path.lineTo(keyPoints[20].x, keyPoints[8].y)
            var data = Data(arrayOf(PointF(keyPoints[3].x, keyPoints[8].y), PointF(keyPoints[20].x, keyPoints[8].y)).toList(), path)
            mAnim1.add(data)

            path = Path()
            path.moveTo(keyPoints[17].x, keyPoints[8].y)
            path.lineTo(keyPoints[17].x, keyPoints[20].y)
            data = Data(arrayOf(PointF(keyPoints[17].x, keyPoints[8].y), PointF(keyPoints[17].x, keyPoints[20].y)).toList(), path)
            mAnim1.add(data)

            path = Path()
            path.moveTo(keyPoints[17].x, keyPoints[19].y)
            path.lineTo(keyPoints[17].x, keyPoints[21].y)
            data = Data(arrayOf(PointF(keyPoints[17].x, keyPoints[19].y), PointF(keyPoints[17].x, keyPoints[21].y)).toList(), path)
            mAnim1.add(data)


            path = Path()
            path.moveTo(keyPoints[2].x, keyPoints[2].y)
            path.lineTo(keyPoints[0].x, keyPoints[0].y)
            data = Data(arrayOf(keyPoints[2], keyPoints[0]).toList(), path)
            mAnim2.add(data)

            path = Path()
            path.moveTo(keyPoints[7].x, keyPoints[7].y)
            path.lineTo(keyPoints[4].x, keyPoints[4].y)
            data = Data(arrayOf(keyPoints[7], keyPoints[4]).toList(), path)
            mAnim2.add(data)

            path = Path()
            path.moveTo(keyPoints[8].x, keyPoints[8].y)
            path.lineTo(keyPoints[11].x, keyPoints[11].y)
            data = Data(arrayOf(keyPoints[8], keyPoints[11]).toList(), path)
            mAnim2.add(data)

            path = Path()
            path.moveTo(keyPoints[15].x, keyPoints[15].y)
            path.lineTo(keyPoints[12].x, keyPoints[12].y)
            data = Data(arrayOf(keyPoints[15], keyPoints[12]).toList(), path)
            mAnim2.add(data)

            path = Path()
            path.moveTo(keyPoints[16].x, keyPoints[16].y)
            path.lineTo(keyPoints[19].x, keyPoints[19].y)
            data = Data(arrayOf(keyPoints[16], keyPoints[19]).toList(), path)
            mAnim2.add(data)

            for((index, pointArray) in points.withIndex()) {
                if(index != 0  && index != 2 && index != 3) continue
                path = Path()
                for(point in pointArray) {
                    path.addCircle(point.x, point.y, 8f, Path.Direction.CW)
                }
                data = Data(pointArray, path)
                mAnim3.add(data)
            }
        }
    }

    fun setData(bitmap: Bitmap?) {
        animator?.cancel()
        this.mSource = bitmap
        val maxValue = resources.getDimensionPixelOffset(R.dimen.change_960px)
        scale = maxValue / Math.max(mSource!!.width, mSource!!.height).toFloat()
        scale = scale.coerceAtMost(1.0f)
        rect = Rect(0, 0, mSource!!.width, mSource!!.height)
        rectF = RectF(rect)
        animator = ValueAnimator.ofInt(0, 1000).setDuration(2000)
        isShowScan = true
        animator?.interpolator = LinearInterpolator()
        animator?.addUpdateListener {
            mCurrentProgress = it.animatedValue as Int
            postInvalidate()
        }
        animator?.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                if (mAnim1.isNotEmpty()) {
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

    private fun startAnim() {
        isShowScan = false
        rect = Rect(0, 0, mSource!!.width, mSource!!.height)
        rectF = RectF(rect)

//        mSourceRect = Rect(0,0,mSource!!.width, mSource!!.height)
        animator = ValueAnimator.ofInt(0, 10500).setDuration(5000)
        animator?.interpolator = LinearInterpolator()
        animator?.addUpdateListener {
            mCurrentProgress = it.animatedValue as Int
            postInvalidate()
            listener?.onUpdate(mCurrentProgress)
        }
        animator?.addListener(object : Animator.AnimatorListener {
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
        animator?.start()
    }


    private fun drawPath(canvas: Canvas, points: Array<PointF>) {
        canvas.drawRect(
            points[0].x * mSource!!.width / 192f,
            points[0].y * mSource!!.height / 192f,
            points[1].x * mSource!!.width / 192f,
            points[1].y * mSource!!.height / 192f, mPaint
        );
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
            canvas.drawBitmap(
                it,
                point.x - it.width / 2,
                point.y - it.height / 2,
                mPaint
            )
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (mSource != null) {
            canvas?.let {
                // 计算左边位置
                it.save()
                it.scale(scale, scale)
                it.translate((mWidth - mWidth * scale) / 2.toFloat(),0f)

                val sc = canvas.saveLayer(0f, 0f, width.toFloat(), height.toFloat(), mBitmapPaint, Canvas.ALL_SAVE_FLAG)
                rectF?.let { it1 -> canvas.drawRoundRect(it1, 48f, 48f, mBitmapPaint) }
                canvas.drawBitmap(mSource!!, 0f, 0f, mModePaint)
                canvas.restoreToCount(sc)
                if (isShowScan) { // 扫描
                    mScanDstRect.left = 0
                    mScanDstRect.top = (mSource!!.height / 1000.toFloat() * mCurrentProgress).toInt()
                    mScanDstRect.right = mSource!!.width
                    mScanDstRect.bottom = (mSource!!.height / 1000.toFloat() * mCurrentProgress + mScan.height).toInt()
                    canvas.drawBitmap(mScan, mScanSrcRect, mScanDstRect, mPaintHand)
                } else {
                    if (mCurrentProgress < 3500) {
                        val currentProgress = mCurrentProgress % 1000
                        val currentShowPosition = (mCurrentProgress / 1000)
                        // 0 - 99 第1点
                        // 1000 - 1099 第2点
                        // 2000 - 2099 第3点
                        for ((index, anim) in mAnim1.withIndex()) {
                            showPoint(it, anim, index, currentShowPosition, currentProgress)
                        }
                        mPaint.alpha = 100
                        for (index in 0 until currentShowPosition) {
                            canvas.drawPath(mAnim1[index].path, mPaint)
                        }
                        if (currentShowPosition < mAnim1.size) {
                            if (currentProgress in (100..1000)) {
                                mPathMeasure.setPath(mAnim1[currentShowPosition].path, false)
                                val progress = mPathMeasure.length * (currentProgress - 100) / 900
                                mDst.rewind()
                                mPathMeasure.getSegment(0f, progress, mDst, true)
                                canvas.drawPath(mDst, mPaint)
                            }
                        }
                    } else if (mCurrentProgress < 7000) {
                        val currentProgress = (mCurrentProgress - 3500) % 600
                        val currentShowPosition = ((mCurrentProgress - 3500) / 600)
                        for ((index, anim) in mAnim2.withIndex()) {
                            showPoint(it, anim, index, currentShowPosition, currentProgress * 10 / 6)
                        }
                        mPaint.alpha = 100
                        for (index in 0 until currentShowPosition) {
                            canvas.drawPath(mAnim2[index].path, mPaint)
                        }

                        if (currentShowPosition < mAnim2.size) {
                            if (currentProgress in (100..600)) {
                                mPathMeasure.setPath(mAnim2[currentShowPosition].path, false)
                                val progress = mPathMeasure.length * (currentProgress - 100) / 600
                                mDst.rewind()
                                mPathMeasure.getSegment(0f, progress, mDst, true)
                                canvas.drawPath(mDst, mPaint)
                            }
                        }

                    } else if (mCurrentProgress <= 10500) {
                        val currentProgress = (mCurrentProgress - 7000) % 1000
                        val currentShowPosition = ((mCurrentProgress - 7000) / 1000)
                        mLine?.let { lines ->
//                                rectF?.let { it1 -> canvas.drawRoundRect(it1, 48f, 48f, ) }
                                // mBitmapPaint


                            for (index in 0 until currentShowPosition) {
                                canvas.drawBitmap(lines[index],0f, 0f, mBitmapPaint)
                            }

                            if (currentShowPosition < mAnim3.size) {
                                mPathMeasure.setPath(mAnim3[currentShowPosition].path, false)
                                if (currentProgress in (0..999)) {
                                    val sc = canvas.saveLayer(0f, 0f, width.toFloat(), height.toFloat(), mBitmapPaint, Canvas.ALL_SAVE_FLAG)

                                    var length = mPathMeasure.length
                                    while(mPathMeasure.nextContour()) {
                                        length += mPathMeasure.length
                                    }
                                    mDst.rewind()
                                    mPathMeasure.setPath(mAnim3[currentShowPosition].path, false)
                                    var total = mPathMeasure.length
                                    while(mPathMeasure.nextContour()) {
                                        total += mPathMeasure.length
                                        if(total < length  * currentProgress / 1000) {
                                            mPathMeasure.getSegment(0f, total, mDst, true)
                                        }
                                    }


                                    canvas.drawPath(mDst, mBitmapPaint)
                                    canvas.drawBitmap(lines[currentShowPosition], 0f, 0f, mModePaint)
                                    canvas.restoreToCount(sc)
                                }
                            }

//                                var length = mPathMeasure.length * currentProgress / 1000

                        }
                    }
                }
                // 画布状态回滚
                canvas.restore()
            }
        }
    }

    private fun showPoint(canvas: Canvas, data: Data, index: Int, currIndex: Int, progress: Int) {
        drawPoint(canvas, data.points[0], if (currIndex == index && progress < 100) progress else if (currIndex >= index) 100 else 0)
        drawPoint(canvas, data.points[1], if (currIndex == index && progress < 100) progress else if (currIndex >= index) 100 else 0)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        animator?.cancel()
    }
}
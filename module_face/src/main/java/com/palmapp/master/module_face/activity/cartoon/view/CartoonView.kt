package com.palmapp.master.module_face.activity.cartoon.view

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.graphics.Paint.Join
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.palmapp.master.baselib.utils.LogUtil
import com.palmapp.master.module_face.R


/**
 *    author : liwenjun
 *    date   : 2020/4/8 16:28
 *    desc   :
 */
class CartoonView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private var mCanvas: Canvas? = null
    private var animator: ValueAnimator? = null
    private var rectF: RectF? = null
    private var rect: Rect? = null
    private var mOriginBitmap: Bitmap? = null
    private var mReplaceBitmap: Bitmap? = null

    private val SRC_IN = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    private val DST_IN = PorterDuffXfermode(PorterDuff.Mode.DST_IN)

    private val mBitmapPaint: Paint
    private val mModePaint: Paint = Paint()

    private val mBrushBitmap: ArrayList<Bitmap> = ArrayList()

    private var brushSrcRect: Rect = Rect()
    private var brushDstRect: Rect = Rect()


    private var scale: Float = 0f

    private var isRunning = false

    // max 100
    var progress = 0

    init {
        mModePaint.style = Paint.Style.FILL
        mModePaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        mModePaint.isAntiAlias = true

        mBitmapPaint = Paint()
        mBitmapPaint.isAntiAlias = true
        mBitmapPaint.color = Color.LTGRAY
        mBitmapPaint.style = Paint.Style.FILL
        mBitmapPaint.strokeWidth = 4.0f
    }

    private var sourceBitmap : Bitmap? = null

    fun getImageBitmap(): Bitmap? {
        return sourceBitmap
    }
    private fun replaceBrushBitmap(index: Int, bitmap: Bitmap) {
        mBrushBitmap[index].recycle()
        mBrushBitmap[index] = bitmap
    }

    private fun getSRCINBitmap(origin:Bitmap, bitmap: Bitmap): Bitmap {
        val output = Bitmap.createBitmap(
            bitmap.width, bitmap
                .height, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(output)
        val color = -0xbdbdbe
        val paint = Paint()
        val rect = Rect(0, 0, bitmap.width, bitmap.height)
        val rectF = RectF(rect)
        paint.isAntiAlias = true
        canvas.drawARGB(0, 0, 0, 0)
        paint.color = color
        canvas.drawBitmap(bitmap, 0f, 0f, paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(origin, 0f, 0f, paint)
        return output
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        switchBitmap(sourceBitmap)
    }

    fun switchBitmap(replace: Bitmap?) {
        this.sourceBitmap = replace
        this.mOriginBitmap = mReplaceBitmap
        if(width == 0 || replace == null) return

        val maxValue = width.coerceAtMost(height)
        scale =  maxValue / replace!!.width.coerceAtLeast(replace!!.height).toFloat()
        scale = scale.coerceAtMost(1.0f)

        this.mReplaceBitmap = Bitmap.createScaledBitmap(replace, (replace!!.width * scale).toInt(), (replace.height * scale).toInt(), true).copy(
            Bitmap.Config.ARGB_8888, true)
        for ( bitmap in mBrushBitmap) {
            bitmap.recycle()
        }
        mBrushBitmap.clear()
        mBrushBitmap.add(BitmapFactory.decodeResource(resources, R.mipmap.brush_1))
        mBrushBitmap.add(BitmapFactory.decodeResource(resources, R.mipmap.brush_2))
        mBrushBitmap.add(BitmapFactory.decodeResource(resources, R.mipmap.brush_3))
        mBrushBitmap.add(BitmapFactory.decodeResource(resources, R.mipmap.brush_4))

        if(mOriginBitmap != null) {
            for ((index, bitmap) in mBrushBitmap.withIndex()) {
                replaceBrushBitmap(index, getSRCINBitmap(mOriginBitmap!!, bitmap))
            }
            mCanvas = Canvas(mOriginBitmap!!)
        }
        brushSrcRect.left = 0
        brushSrcRect.top = 0
        brushSrcRect.bottom = mBrushBitmap[0].height
        brushSrcRect.right = mBrushBitmap[0].width

        rect = Rect(0, 0, mReplaceBitmap!!.width, mReplaceBitmap!!.height)
        rectF = RectF(rect)

    }


    fun showAnimation() {
        isRunning = true
        animator = ValueAnimator.ofInt(0, 99).setDuration(2000)
        animator?.interpolator = LinearInterpolator()
        animator?.addUpdateListener {
            progress = it.animatedValue as Int
            postInvalidate()
        }
        animator?.addListener(object : Animator.AnimatorListener{
            override fun onAnimationRepeat(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                mOriginBitmap?.recycle()
                mOriginBitmap = null
                isRunning = false
            }

            override fun onAnimationCancel(animation: Animator?) {
                isRunning = false
            }

            override fun onAnimationStart(animation: Animator?) {
            }

        })
        animator?.start()
    }


    fun stopAnimation() {

        animator?.cancel()
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let {
            it.save()
//            it.scale(scale, scale)
            if(mReplaceBitmap!= null)
            it.translate((width - mReplaceBitmap!!.width) / 2.toFloat(),0f)

            if(mOriginBitmap != null && mReplaceBitmap != null && isRunning) {
                var sc = canvas.saveLayer(
                    0f,
                    0f,
                    width.toFloat(),
                    height.toFloat(),
                    null,
                    Canvas.ALL_SAVE_FLAG
                )
                mModePaint.xfermode = SRC_IN
                rectF?.let { it1 -> canvas.drawRoundRect(it1, 48f, 48f, mBitmapPaint) }
                // 画被替换的在底部
                canvas.drawBitmap(mReplaceBitmap!!, 0f, 0f, mModePaint)

                canvas.restoreToCount(sc)
                Log.d(this.javaClass.simpleName, "width:$measuredWidth")

                val count = canvas.saveLayer(
                    0f,
                    0f,
                    mOriginBitmap!!.width.toFloat(),
                    mOriginBitmap!!.height.toFloat(),
                    null,
                    Canvas.ALL_SAVE_FLAG
                )
                rectF?.let { it1 -> canvas.drawRoundRect(it1, 48f, 48f, mBitmapPaint) }

                mModePaint.xfermode = SRC_IN
                canvas.drawBitmap(mOriginBitmap!!, 0f, 0f, mModePaint)
                canvas.restoreToCount(count)

                val currentProgress = progress % 20
                val currentShowPosition = progress / 20
                LogUtil.d("currentShowPosition:$currentShowPosition")
                val offset = arrayOf(1, 3, 2, 4)
                for (position in 0..currentShowPosition) {
                    if (position == 4) {
                        draw(mCanvas!!, (currentProgress + 1) * 5)
                    } else {
                        draw(
                            mCanvas!!,
                            offset[position],
                            offset[position] - 1,
                            if (currentShowPosition == position) (currentProgress + 1) * 5 else 100,
                            position % 2 == 1,
                            4
                        )
                    }
                }

            } else if(mReplaceBitmap != null) {
                var sc = canvas.saveLayer(
                    0f,
                    0f,
                    width.toFloat(),
                    height.toFloat(),
                    null,
                    Canvas.ALL_SAVE_FLAG
                )
                mModePaint.xfermode = SRC_IN
                rectF?.let { it1 -> canvas.drawRoundRect(it1, 48f, 48f, mBitmapPaint) }
                // 画被替换的在底部
                canvas.drawBitmap(mReplaceBitmap!!, 0f, 0f, mModePaint)

                canvas.restoreToCount(sc)
            }
            // 画布状态回滚
            it.restore()
        }
    }

    private fun draw(canvas: Canvas, progress: Int) {
        Log.d(this.javaClass.simpleName, "progress$progress")
        brushDstRect.left = 0
        brushDstRect.top = 0
        brushDstRect.right =  mOriginBitmap!!.width
        brushDstRect.bottom = (mOriginBitmap!!.height.toFloat() / 100 * (progress)).toInt()
        mModePaint.xfermode =  PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
        canvas.drawRect(brushDstRect, mModePaint)
    }

    private fun draw(canvas: Canvas, offset : Int, brush: Int, progress: Int,leftToRight: Boolean, allCount: Int) {
        val leftWidth = if(leftToRight) 0f else -mOriginBitmap!!.width.toFloat() * (allCount + 1) / allCount
        val rightWidth = if(leftToRight) mOriginBitmap!!.width.toFloat() * (allCount + 1) / allCount else  mOriginBitmap!!.width.toFloat()

        val offsetTop = mOriginBitmap!!.height.toFloat() / 25

//        brushDstRect.left = (leftWidth + if(leftToRight) (mOriginBitmap!!.width + leftWidth) * progress / 100f else 0f).toInt()
//        brushDstRect.top = (mOriginBitmap!!.height.toFloat() / allCount * (offset - 1) - offsetTop).toInt()
//        brushDstRect.right = (rightWidth - if(!leftToRight) (mOriginBitmap!!.width + rightWidth) * progress / 100 else 0f).toInt()
//        brushDstRect.bottom = ((mOriginBitmap!!.height.toFloat() / allCount ) * offset + offsetTop).toInt()
        if(offset == 4) {
            LogUtil.d("offset")
        }
        var save = canvas.save()

        canvas.clipRect((if(leftToRight) 0f else canvas.width / 100f *  (100 - progress)).toInt(),
            0,
            (if(leftToRight) canvas.width / 100f * progress else canvas.width.toFloat()).toInt(),
            height)

        brushDstRect.left = leftWidth.toInt()
        brushDstRect.top = (mOriginBitmap!!.height.toFloat() / allCount * (offset - 1) -  offsetTop).toInt()
        brushDstRect.right = rightWidth.toInt()
        brushDstRect.bottom = ((mOriginBitmap!!.height.toFloat() / allCount)  * offset + offsetTop).toInt()
        mModePaint.xfermode =  PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
        canvas.drawBitmap(mBrushBitmap[brush], brushSrcRect, brushDstRect, mModePaint)
        canvas.restoreToCount(save)



        /*brushDstRect.left = (leftWidth + if(leftToRight) (mOriginBitmap!!.width + leftWidth) * progress / 100f else 0f).toInt()
        brushDstRect.top = (mOriginBitmap!!.height.toFloat() / allCount * (offset - 1) - offsetTop).toInt()
        brushDstRect.right = (rightWidth - if(!leftToRight) (mOriginBitmap!!.width + rightWidth) * progress / 100 else 0f).toInt()
        brushDstRect.bottom = ((mOriginBitmap!!.height.toFloat() / allCount ) * offset + offsetTop).toInt()
        mModePaint.xfermode =  PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
        canvas.drawBitmap(mBrushBitmap[brush], brushSrcRect, brushDstRect, mModePaint)*/
    }

}
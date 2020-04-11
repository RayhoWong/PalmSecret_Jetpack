package com.palmapp.master.baselib.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;

import com.palmapp.master.baselib.R;


/**
 *  Created by huangweihao on 2020/3/20.
 * 类描述：渐变的圆形进度条
 *
 */
public class RoundProgress extends View {
    private static final String TAG = "roundProgress";
    /**
     * 背景圆环画笔
     */
    private Paint bgPaint;

    /**
     * 进度画笔
     */
    private Paint progressPaint;

    /**
     * 背景圆环的颜色
     */
    private int bgColor;

    private int[] progressColor;

    /**
     * 外圆环的宽度
     */
    private float roundWidth;

    //进度圆环的宽度
    private float progressRoundWidth;

    /**
     * 最大进度
     */
    private int max;
    /**
     * 当前进度
     */
    private float progress;
    /**
     * 圆环半径
     */
    private int mRadius;
    private int center;

    private float startAngle = 270f;
    private float currentAngle;

    //动画
    public ValueAnimator valueAnimator;


    public RoundProgress(Context context) {
        this(context, null);
    }

    public RoundProgress(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.RoundProgress);

        //获取自定义属性和默认值
        bgColor = mTypedArray.getColor(R.styleable.RoundProgress_bgColor, Color.parseColor("#2d2d2d"));
        roundWidth = mTypedArray.getDimension(R.styleable.RoundProgress_roundWidth, 5);
        progressRoundWidth = mTypedArray.getDimension(R.styleable.RoundProgress_progressRoundWidth, 5);
        max = mTypedArray.getInteger(R.styleable.RoundProgress_maxProgress, 100);
        progressColor = new int[]{Color.parseColor("#00c88e"), Color.parseColor("#007ed8")};
        mTypedArray.recycle();
        initPaint();
    }

    public RoundProgress(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //测量控件应占的宽高大小，此处非必需，只是为了确保布局中设置的宽高不一致时仍显示完整的圆
        int measureWidth = MeasureSpec.getSize(widthMeasureSpec);
        int measureHeight = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(Math.min(measureWidth, measureHeight), Math.min(measureWidth, measureHeight));
    }


    private void initPaint() {
        bgPaint = new Paint();
        bgPaint.setStyle(Paint.Style.STROKE);
        bgPaint.setAntiAlias(true);
        bgPaint.setColor(bgColor);
        bgPaint.setStrokeWidth(roundWidth);

        progressPaint = new Paint();
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setAntiAlias(true);
        progressPaint.setStrokeWidth(progressRoundWidth);
        progressPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        /**
         * 画最外层的大圆环
         */
        //获取圆心的x坐标
        center = Math.min(getWidth(), getHeight()) / 2;
        // 圆环的半径
        mRadius = (int) (center - roundWidth / 2);

        RectF oval = new RectF(center - mRadius, center - mRadius, center + mRadius, center + mRadius);
        //画背景圆环
        canvas.drawArc(oval, startAngle, 360, false, bgPaint);
        //画进度圆环
        drawProgress(canvas, oval);

        canvas.drawArc(oval, startAngle, currentAngle, false, progressPaint);
    }

    /**
     * 画进度圆环
     *
     * @param canvas
     * @param oval
     */
    private void drawProgress(Canvas canvas, RectF oval) {
        float section = progress / 100;
        currentAngle = section * 360;
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //计算外圆半径 宽，高最小值-填充边距/2
        center = (Math.min(w, h)) / 2;
        mRadius = (int) ((Math.min(w, h)) - roundWidth / 2);

        LinearGradient shader = new LinearGradient(0,0,getWidth(), getHeight(), progressColor, new float[]{0.0f, 1.0f}, Shader.TileMode.CLAMP);
        progressPaint.setShader(shader);
    }


    /**
     * 设置进度
     *
     * @param progressValue
//     * @param useAnima      是否需要动画
     */

    public void setProgress(float progressValue) {
        float percent = progressValue * max / 100;
        if (percent < 0) {
            percent = 0;
        }
        if (percent > 100) {
            percent = 100;
        }
        //使用动画
        valueAnimator = ValueAnimator.ofFloat(0, percent);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                progress = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.setDuration(15000);
        valueAnimator.start();

//        if (useAnima) {
//            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, percent);
//            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//                @Override
//                public void onAnimationUpdate(ValueAnimator animation) {
//                    progress = (float) animation.getAnimatedValue();
//                    invalidate();
//                }
//            });
//            valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
//            valueAnimator.setInterpolator(new LinearInterpolator());
//            valueAnimator.setDuration(1500);
//            valueAnimator.start();
//        } else {
//            this.progress = percent;
//            invalidate();
//        }
    }


    public void stopAnim(){
        if (valueAnimator != null){
            if (valueAnimator.isRunning()){
                valueAnimator.cancel();
            }
        }
    }


    public float getRoundWidth() {
        return roundWidth;
    }

    public void setRoundWidth(float roundWidth) {
        this.roundWidth = roundWidth;
    }

    public int[] getProgressColor() {
        return progressColor;
    }

    public void setProgressColor(int[] progressColor) {
        this.progressColor = progressColor;
        invalidate();
    }

    public int getBgColor() {
        return bgColor;
    }

    public void setBgColor(int bgColor) {
        this.bgColor = bgColor;
    }


    public int getmRadius() {
        return mRadius;
    }

    public void setmRadius(int mRadius) {
        this.mRadius = mRadius;
    }

    public int getCenter() {
        return center;
    }

    public void setCenter(int center) {
        this.center = center;
    }

    public float getStartAngle() {
        return startAngle;
    }

    public void setStartAngle(float startAngle) {
        this.startAngle = startAngle;
    }

}

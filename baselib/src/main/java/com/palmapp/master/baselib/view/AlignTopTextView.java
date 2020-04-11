package com.palmapp.master.baselib.view;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

import com.palmapp.master.baselib.R;


public class AlignTopTextView extends AppCompatTextView {
    private Drawable mDrawable;
    private int mPadding;

    public AlignTopTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.AlignTopTextView);
        int drawable = ta.getResourceId(R.styleable.AlignTopTextView_drawableLeft, -1);
        mPadding = ta.getDimensionPixelOffset(R.styleable.AlignTopTextView_drawablePadding, 0);
        ta.recycle();
        if (drawable == -1) {
            throw new IllegalArgumentException("没有指定drawableLeft");
        }
        mDrawable = context.getResources().getDrawable(drawable);
        setPadding(mPadding + mDrawable.getIntrinsicWidth(), 0, 0, 0);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int top = (int) ((getLineHeight()  - getLineSpacingExtra() - mDrawable.getIntrinsicHeight()));
        mDrawable.setBounds(0, top, mDrawable.getIntrinsicWidth(), top + mDrawable.getIntrinsicHeight());
        invalidate();
    }


    @Override
    protected void dispatchDraw(Canvas canvas) {
        mDrawable.draw(canvas);
        super.dispatchDraw(canvas);
    }
}
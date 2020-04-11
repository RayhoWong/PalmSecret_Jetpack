package com.palmapp.master.baselib.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.viewpager.widget.ViewPager;

public class WrapContentViewPager extends ViewPager {

    public WrapContentViewPager(Context context) {
        super(context);
    }

    public WrapContentViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOverScrollMode( View.OVER_SCROLL_NEVER );
    }



    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int maxHeight = 0;
        int maxWidth = 0;
        //下面遍历所有child的高度
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            measureChild(child,widthMeasureSpec,heightMeasureSpec);
            maxHeight = Math.max(child.getMeasuredHeight(), maxHeight);
            maxWidth = Math.max(child.getMeasuredWidth(), maxWidth);
        }

        heightMeasureSpec = MeasureSpec.makeMeasureSpec(maxHeight,
                MeasureSpec.EXACTLY);
//        widthMeasureSpec = MeasureSpec.makeMeasureSpec(maxWidth,MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
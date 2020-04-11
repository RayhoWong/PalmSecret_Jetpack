package com.palmapp.master.baselib.view;

import android.content.res.TypedArray;
import android.widget.TextView;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatTextView;
import com.palmapp.master.baselib.R;

public class GradientColorTextView extends AppCompatTextView {

    private LinearGradient mLinearGradient;
    private int startColor, endColor;

    public GradientColorTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.GradientColorTextView);
        startColor = ta.getColor(R.styleable.GradientColorTextView_startColor, 0xFFFFFF);
        endColor = ta.getColor(R.styleable.GradientColorTextView_endColor, 0xFFFFFF);
        ta.recycle();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mLinearGradient = new LinearGradient(0, 0, w, 0,
                new int[]{startColor, endColor},
                null, Shader.TileMode.CLAMP);
        getPaint().setShader(mLinearGradient);
    }
}
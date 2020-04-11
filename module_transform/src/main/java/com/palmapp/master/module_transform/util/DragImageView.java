package com.palmapp.master.module_transform.util;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * Created by huangweihao on 2019/12/6.
 * 可拖动的imageview
 */
@SuppressLint("AppCompatCustomView")
public class DragImageView extends ImageView {
    private final static String TAG = "DragImageView";

    private int startX;
    private int startY;
    private int startL;//初始时左上X，相对于父容器
    private int startT;//初始时左上Y
    private int startR;//初始时右下X
    private int startB;//初始时右下X
    private int stopX;
    private int stopY;


    public DragImageView(Context context) {
        super(context);
    }

    public DragImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DragImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public DragImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action){
            case MotionEvent.ACTION_DOWN:
                startX = (int) event.getRawX();//相对于屏幕的x坐标,getX()是相对于组件的坐标
                startY = (int) event.getRawY();
                startL = getLeft();
                startT = getTop();
                startR = getRight();
                startB = getBottom();
                Log.d(TAG,"x:"+startX+",rawX,"+event.getRawX()+",left:"+getLeft());
                break;
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
                stopX = (int) event.getRawX();
                stopY = (int) event.getRawY();
                int deltaX = stopX - startX;//拖动偏移量
                int deltaY = stopY - startY;
                this.moveToPosition(startL+deltaX,startT+deltaY,startR+deltaX,startB+deltaY);
                stopX = (int) event.getRawX();
                stopY = (int) event.getRawY();
                invalidate();
                break;
        }
        return true;
    }

    private void moveToPosition(int leftX,int leftY,int rightX,int bottom){
        this.layout(leftX,leftY,rightX,bottom);
    }

}

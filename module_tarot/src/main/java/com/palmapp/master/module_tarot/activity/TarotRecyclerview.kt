package com.palmapp.master.module_tarot.activity

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by huangweihao on 2019/8/30.
 */
class TarotRecyclerview : RecyclerView {
    private var isIntercepted = false

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle)

    fun setIntercept(isIntercepted: Boolean) {
        this.isIntercepted = isIntercepted
    }

    override fun onInterceptTouchEvent(e: MotionEvent?): Boolean {
//        when (e?.action) {
//            MotionEvent.ACTION_DOWN -> {
//                isIntercepted = isIntercepted
//            }
//            MotionEvent.ACTION_UP -> {
//                isIntercepted = false
//            }
//            MotionEvent.ACTION_CANCEL -> {
//                isIntercepted = false
//            }
//            MotionEvent.ACTION_MOVE -> {
//                isIntercepted = false
//            }
//        }
        return if(isIntercepted){
            isIntercepted
        }else{
            super.onInterceptTouchEvent(e)
        }
    }
}
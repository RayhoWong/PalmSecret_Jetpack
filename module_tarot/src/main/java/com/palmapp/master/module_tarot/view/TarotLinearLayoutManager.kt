package com.palmapp.master.module_tarot.view

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearLayoutManager

/**
 * Created by huangweihao on 2019/8/27.
 * 自定义Manager 禁止Recyclerview滑动
 */
class TarotLinearLayoutManager : LinearLayoutManager {
    private var isScrollEnabled = true

    constructor(context: Context) : super(context)

    constructor(context: Context, orientation: Int, reverseLayout: Boolean) : super(
        context,
        orientation,
        reverseLayout
    )


    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    )


    //禁止水平滑动
    fun setHorizontalScroll(isScrollEnabled: Boolean) {
        this.isScrollEnabled = isScrollEnabled
    }

    override fun canScrollHorizontally(): Boolean {
        return isScrollEnabled && super.canScrollHorizontally()
    }
}

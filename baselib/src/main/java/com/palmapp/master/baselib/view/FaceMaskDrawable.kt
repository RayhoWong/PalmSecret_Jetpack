/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.palmapp.master.baselib.view

import android.graphics.*
import android.graphics.drawable.Drawable
import com.palmapp.master.baselib.GoCommonEnv
import com.palmapp.master.baselib.R

/**
 *
 * @author :     xiemingrui
 * @since :      2020/4/3
 */
class FaceMaskDrawable : Drawable() {
    private val STROKE_WIDTH = 10F
    private val RADIO_SIZE = 30F
    private val rectF = RectF()
    private val paint = Paint()
    private val MAX_SIZE = GoCommonEnv.getApplication().resources.getDimensionPixelSize(R.dimen.change_984px)
    init {
        paint.color = Color.BLACK
        paint.style = Paint.Style.FILL
    }

    override fun getIntrinsicHeight(): Int {
        return MAX_SIZE
    }

    override fun getIntrinsicWidth(): Int {
        return MAX_SIZE
    }

    override fun draw(canvas: Canvas) {
        canvas.drawRoundRect(rectF, RADIO_SIZE, RADIO_SIZE, paint)
    }

    override fun setAlpha(alpha: Int) {
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
    }

    override fun onBoundsChange(bounds: Rect?) {
        super.onBoundsChange(bounds)
        bounds?.let {
            rectF.set(it.left + STROKE_WIDTH, it.top + STROKE_WIDTH, it.right - STROKE_WIDTH, it.bottom - STROKE_WIDTH)
        }
    }
}
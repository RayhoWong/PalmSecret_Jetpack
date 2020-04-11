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
class FaceBorderDrawable : Drawable() {
    private val path = Path()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val STROKE_WIDTH = 10F
    private val START_OFFSET = STROKE_WIDTH / 2
    private val RADIO_SIZE = 30F
    private val BORDER_SIZE = 84F + START_OFFSET
    private val BORDER_OFFSET = BORDER_SIZE - RADIO_SIZE
    private val MAX_SIZE = GoCommonEnv.getApplication().resources.getDimensionPixelSize(R.dimen.change_984px)

    init {
        paint.color = Color.WHITE
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = STROKE_WIDTH
    }

    override fun getIntrinsicHeight(): Int {
        return MAX_SIZE
    }

    override fun getIntrinsicWidth(): Int {
        return MAX_SIZE
    }

    override fun draw(canvas: Canvas) {
        canvas.drawPath(path, paint)
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
        bounds?.let{
            path.reset()
            val rectF = RectF(START_OFFSET, START_OFFSET, RADIO_SIZE * 2, RADIO_SIZE * 2)
            path.moveTo(START_OFFSET, BORDER_SIZE)
            path.rLineTo(0f, -BORDER_OFFSET)
            path.arcTo(rectF, 180f, 90f, true)
            path.rLineTo(BORDER_OFFSET, 0f)

            rectF.offsetTo(it.width() - BORDER_OFFSET, START_OFFSET)
            path.moveTo(it.width() - BORDER_SIZE, START_OFFSET)
            path.rLineTo(BORDER_OFFSET, 0f)
            path.arcTo(rectF, 270f, 90f, true)
            path.rLineTo(0f, BORDER_OFFSET)

            rectF.offsetTo(START_OFFSET, it.height() - BORDER_OFFSET)
            path.moveTo(START_OFFSET, it.height() - BORDER_SIZE)
            path.rLineTo(0f, BORDER_OFFSET)
            path.arcTo(rectF, 180f, -90f, true)
            path.rLineTo(BORDER_OFFSET, 0f)

            rectF.offsetTo(it.height() - BORDER_OFFSET, it.height() - BORDER_OFFSET)
            path.moveTo(it.width() - BORDER_SIZE, it.height() - START_OFFSET)
            path.rLineTo(BORDER_OFFSET, 0f)
            path.arcTo(rectF, 90f, -90f, true)
            path.rLineTo(0f, -BORDER_OFFSET)
        }
    }
}
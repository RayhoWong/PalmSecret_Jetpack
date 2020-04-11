package com.palmapp.master.baselib.view

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.KeyEvent
import com.palmapp.master.baselib.R

/**
 *
 * @author :     xiemingrui
 * @since :      2019/12/9
 */
class LoadingDialog(context: Context) : Dialog(context, R.style.CommonDialog) {
    var mback = true

    constructor(context: Context, back: Boolean) : this(context) {
        mback = back
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.loading_dialog)
        val window = window
        val lp = window!!.attributes
        lp.gravity = Gravity.CENTER
        window.attributes = lp
        setCanceledOnTouchOutside(false)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return mback
        }
        return super.onKeyDown(keyCode, event)
    }
}
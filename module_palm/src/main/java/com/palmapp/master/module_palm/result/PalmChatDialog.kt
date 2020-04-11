package com.palmapp.master.module_palm.result

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.cs.bd.mopub.utils.ScreenUtils
import com.palmapp.master.baselib.utils.AppUtil
import com.palmapp.master.module_palm.R

/**
 *
 * @author :     xiemingrui
 * @since :      2020/3/19
 */
class PalmChatDialog(context: Context,val listener:View.OnClickListener) : Dialog(context, R.style.CommonDialog){
    var content :TextView?=null
    var iv: ImageView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.palm_dialog_expert)
        setCanceledOnTouchOutside(false)
        findViewById<View>(R.id.iv_close).setOnClickListener {
            listener.onClick(it)
        }
        iv = findViewById(R.id.iv_palm_countdown)
        content = findViewById(R.id.tv_palm_countdown)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            listener.onClick(content)
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun show() {
        super.show()
        val lp = window?.attributes
        lp?.width = AppUtil.getScreenW(context)
        window?.attributes = lp
    }
}
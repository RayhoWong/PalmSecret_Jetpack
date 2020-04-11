package com.palmapp.master.infomation;

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.palmapp.master.baselib.BaseMVPActivity
import com.palmapp.master.baselib.constants.RouterConstants
import com.palmapp.master.module_me.R
import kotlinx.android.synthetic.main.me_activity_feedback.*

@Route(path = RouterConstants.ACTIVITY_MEFeedbackActivity)
class FeedbackActivity : BaseMVPActivity<FeedbackView, FeedbackPresenter>(), FeedbackView, View.OnClickListener {
    override fun onClick(v: View?) {
        when (v) {
            mBackIv -> finish()
            mDone -> {
                val temp = me_setting_feedback_edit?.text.toString()
                if (temp != null && temp.length > 0) {
                    mPresenter?.postFeedback(me_setting_feedback_edit?.text.toString())
                } else {
                    Toast.makeText(this, getString(R.string.setting_feedback_toast), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun createPresenter(): FeedbackPresenter {
        return FeedbackPresenter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ARouter.getInstance().inject(this)
        setContentView(R.layout.me_activity_feedback)
        findViewById<TextView>(R.id.tv_titlebar_title).setText(getString(R.string.setting_feedback))

        mBackIv = findViewById(R.id.iv_titlebar_back)
        mBackIv?.let {
            it.setOnClickListener(this)
        }
        mDone = findViewById<TextView>(R.id.tv_titlebar_text)
        mDone?.let {
            it.setText(getString(R.string.done))
            it.setOnClickListener(this)
        }
        me_setting_feedback_edit?.let {
            it.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    mDone?.isEnabled = s != null  && s.length > 0
                }
            })
        }
    }

    private var mBackIv:ImageView? = null
    private var mDone:TextView? = null

    override fun showToast(resId: Int) {
        Toast.makeText(getContext(), getString(resId), Toast.LENGTH_SHORT).show()
        finish()
    }

}
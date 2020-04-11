package com.palmapp.master.infomation;

import android.content.Context
import com.palmapp.master.baselib.BaseMVPActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.alibaba.android.arouter.launcher.ARouter
import com.palmapp.master.R
import com.palmapp.master.baselib.bean.user.GENDER_FEMALE
import com.palmapp.master.baselib.bean.user.GENDER_MALE
import com.palmapp.master.baselib.constants.PreConstants
import com.palmapp.master.baselib.constants.RouterConstants
import com.palmapp.master.baselib.manager.AbTestUserManager
import com.palmapp.master.baselib.manager.GoPrefManager
import com.palmapp.master.baselib.statistics.BaseSeq103OperationStatistic
import kotlinx.android.synthetic.main.app_activity_infomation.*

class InfoActivity : BaseMVPActivity<InfoView, InfoPresenter>(), InfoView, View.OnClickListener {
    override fun onClick(v: View?) {
        when (v) {
            tv_app_next -> {
                mPresenter?.saveUserInfo()
                finish()
            }
            tv_appinfo_male, tv_appinfo_female -> {
                mPresenter?.selectGender(v?.tag as Int)
            }
            et3, et2 -> {
                mPresenter?.selectBirthday(this, et2.text.toString())
                hideSoftInput()
            }
            layout_root -> {
                hideSoftInput()
            }
        }
    }

    override fun showGender(gender: Int) {
        tv_appinfo_male.isSelected = gender == 1
        tv_appinfo_female.isSelected = gender == 2
    }

    override fun showBirthDayAndCnt(birth: String, cnt: String) {
        et2.text = birth
        et3.text = cnt
    }

    override fun showNextOrNot(isShow: Boolean) {
        tv_app_next.isEnabled = isShow
    }

    override fun createPresenter(): InfoPresenter {
        return InfoPresenter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.app_activity_infomation)
        BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(BaseSeq103OperationStatistic.enter_f000, "", "1","")
        tv_appinfo_female.tag = GENDER_FEMALE
        tv_appinfo_male.tag = GENDER_MALE
        tv_app_next.setOnClickListener(this)
        tv_appinfo_female.setOnClickListener(this)
        tv_appinfo_male.setOnClickListener(this)
        et2.setOnClickListener(this)
        et3.setOnClickListener(this)
        et1.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                mPresenter?.saveUserName(s.toString())
            }
        })

        layout_root.setOnClickListener(this)
    }

    private fun hideSoftInput() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(window.decorView.windowToken, 0);
    }

    override fun onBackPressed() {
    }
}
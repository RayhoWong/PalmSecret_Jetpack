package com.palmapp.master.infomation;

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import com.alibaba.android.arouter.facade.annotation.Route
import com.palmapp.master.baselib.BaseMVPActivity
import com.palmapp.master.baselib.GoCommonEnv
import com.palmapp.master.baselib.bean.user.GENDER_FEMALE
import com.palmapp.master.baselib.bean.user.GENDER_MALE
import com.palmapp.master.baselib.bean.user.getConstellationById
import com.palmapp.master.baselib.constants.RouterConstants
import com.palmapp.master.baselib.statistics.BaseSeq101OperationStatistic
import com.palmapp.master.baselib.utils.TimeUtils
import com.palmapp.master.module_me.R
import com.palmapp.master.module_me.infomation.EditInfoPresenter
import kotlinx.android.synthetic.main.me_activity_infomation.*
import java.util.*

/**
 * Created by zhangliang on 19/8/20
 *
 * 编辑个人信息
 *
 * @email: zhangliang@gomo.com
 */

@Route(path = RouterConstants.ACTIVITY_MEEditInfoActivity)
class EditInfoActivity : BaseMVPActivity<EditInfoView, EditInfoPresenter>(), EditInfoView, View.OnClickListener {
    override fun onClick(v: View?) {
        when (v) {
            mBackIv -> finish()
            doneTv -> {
                mPresenter?.saveUserInfo()
                finish()
            }
            me_tv_male, me_tv_female -> {
                BaseSeq101OperationStatistic.uploadOperationStatisticData(
                    BaseSeq101OperationStatistic.info_edit_a000,
                    "",
                    "3"
                )
                mPresenter?.selectGender(v?.tag as Int)
            }
            me_et_birthday -> {
                BaseSeq101OperationStatistic.uploadOperationStatisticData(
                    BaseSeq101OperationStatistic.info_edit_a000,
                    "",
                    "2"
                )
                mPresenter?.selectBirthday(this, me_et_birthday.text.toString())
                hideSoftInput()
            }
            layout_root -> {
                hideSoftInput()
            }
        }
    }

    override fun showGender(gender: Int) {
        me_tv_male.isSelected = gender == 1
        me_tv_female.isSelected = gender == 2
        updateDoneStatus()
    }

    override fun showBirthDayAndCnt(birth: String, cnt: String) {
        me_et_birthday.text = birth
        me_et_constellation.text = cnt
        updateDoneStatus()
    }

    override fun createPresenter(): EditInfoPresenter {
        return EditInfoPresenter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.me_activity_infomation)
        doneTv = findViewById<TextView>(R.id.tv_titlebar_text)
        me_tv_female.tag = GENDER_FEMALE
        me_tv_male.tag = GENDER_MALE
        if (GoCommonEnv.userInfo != null && GoCommonEnv.userInfo?.name ?: "" != "") {
            GoCommonEnv.userInfo?.let {
                me_et_name.setText(it.name)
                me_et_birthday.setText(mPresenter?.birthdayStr(it.birthday))
                me_et_constellation.setText(getConstellationById(it.constellation))
                showGender(it.gender)
            }
        } else {
            me_et_name.setText("")
            me_et_birthday.setText("")
            me_et_constellation.setText("")
        }

        findViewById<TextView>(R.id.tv_titlebar_title).setText(R.string.info_title)
        doneTv?.let {
            it.setText(R.string.done)
            it.setOnClickListener(this)
        }

        mBackIv = findViewById<ImageView>(R.id.iv_titlebar_back)
        mBackIv?.let {
            it.setOnClickListener(this)
        }

        me_tv_male.setOnClickListener(this)
        me_tv_female.setOnClickListener(this)
        me_et_birthday.setOnClickListener(this)

        me_et_name.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                s?.let {
                    val str = it.toString().trim()
                    mPresenter?.saveUserName(str.substring(0, if (str.length > 20) 20 else str.length))
                    updateDoneStatus()
                }
            }
        })

        layout_root.setOnClickListener(this)
        BaseSeq101OperationStatistic.uploadOperationStatisticData(BaseSeq101OperationStatistic.info_edit_f000)
    }

    private fun hideSoftInput() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(window.decorView.windowToken, 0);
    }


    private fun updateDoneStatus() {
        val name = me_et_name?.text?.toString()
        val birth = me_et_birthday?.text?.toString()
        val gender = me_tv_male.isSelected || me_tv_female.isSelected
        doneTv?.isEnabled = (name != null && !name.isEmpty()) && (birth != null && !birth.isEmpty()) && gender
    }

    private var doneTv: TextView? = null
    private var mBackIv: ImageView? = null

}
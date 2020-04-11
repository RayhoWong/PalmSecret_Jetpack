package com.palmapp.master.module_me.infomation;

import android.app.DatePickerDialog
import android.text.TextUtils
import android.widget.DatePicker
import com.palmapp.master.baselib.BasePresenter
import com.palmapp.master.baselib.GoCommonEnv
import com.palmapp.master.baselib.bean.user.Gender
import com.palmapp.master.baselib.bean.user.UserInfo
import com.palmapp.master.baselib.bean.user.getConstellationById
import com.palmapp.master.baselib.utils.TimeUtils
import com.palmapp.master.infomation.EditInfoActivity
import com.palmapp.master.infomation.EditInfoView
import com.palmapp.master.module_me.R
import java.text.SimpleDateFormat
import java.util.*

class EditInfoPresenter : BasePresenter<EditInfoView>(), DatePickerDialog.OnDateSetListener {
    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = 0
        calendar.set(year, month, dayOfMonth, 0, 0, 0)
        mBirthday = calendar.timeInMillis
        mConstellation = TimeUtils.getConstellations(month, dayOfMonth)
        getView()?.showBirthDayAndCnt(
            sdf.format(Date(calendar.timeInMillis)),
            getConstellationById(mConstellation)
        )
    }

    private val sdf: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
    private var userInfo = GoCommonEnv.userInfo
    private var mUserName: String = ""
    private var mBirthday = -1L
    private var mConstellation: Int = -1
    private var mGender: Int = -1

    init {
        sdf.toLocalizedPattern()
    }

    override fun onAttach() {
    }

    override fun onDetach() {

    }

    fun birthdayStr(birthdayL: Long?): String {
        return sdf.format(birthdayL)
    }

    fun saveUserInfo() {
        if (userInfo == null) {
            userInfo = UserInfo()
        }
        if (!mUserName.isEmpty()) {
            userInfo?.name = mUserName
        }
        if (mBirthday != -1L) {
            userInfo?.birthday = mBirthday
        }
        if (mConstellation != -1) {
            userInfo?.constellation = mConstellation
        }
        if (mGender != -1) {
            userInfo?.gender = mGender
        }

        GoCommonEnv.storeUserInfo(userInfo)
    }

    fun saveUserName(name: String) {
        mUserName = name
    }

    fun selectGender(@Gender gender: Int) {
        mGender = gender
        getView()?.showGender(gender)
    }

    fun selectBirthday(activity: EditInfoActivity, defaultDate: String = "") {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = if (TextUtils.isEmpty(defaultDate)) {
            System.currentTimeMillis()
        } else {
            sdf.parse(defaultDate).time
        }

        val dialog = DatePickerDialog(
            activity,
            this,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        dialog.datePicker.maxDate = Date().time
        dialog.show()
    }


}
package com.palmapp.master.infomation;

import android.app.DatePickerDialog
import android.text.TextUtils
import android.widget.DatePicker
import com.palmapp.master.R
import com.palmapp.master.baselib.BasePresenter
import com.palmapp.master.baselib.GoCommonEnv
import com.palmapp.master.baselib.bean.user.Gender
import com.palmapp.master.baselib.bean.user.UserInfo
import com.palmapp.master.baselib.bean.user.getConstellationById
import com.palmapp.master.baselib.utils.TimeUtils
import java.text.SimpleDateFormat
import java.util.*

class InfoPresenter : BasePresenter<InfoView>(), DatePickerDialog.OnDateSetListener {
    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = 0
        calendar.set(year, month, dayOfMonth, 0, 0, 0)
        userInfo.birthday = calendar.timeInMillis
        userInfo.constellation = TimeUtils.getConstellations(month, dayOfMonth)
        getView()?.showBirthDayAndCnt(
            sdf.format(Date(calendar.timeInMillis)),
            getConstellationById(userInfo.constellation)
        )
        checkNext()
    }

    private val sdf: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
    private val userInfo = UserInfo()

    init {
        sdf.toLocalizedPattern()
    }

    override fun onAttach() {
    }

    override fun onDetach() {

    }

    fun saveUserInfo() {
        GoCommonEnv.storeUserInfo(userInfo)
    }

    fun saveUserName(name: String) {
        userInfo.name = name.trim()
        checkNext()
    }

    fun selectGender(@Gender gender: Int) {
        userInfo.gender = gender
        getView()?.showGender(gender)
        checkNext()
    }

    fun selectBirthday(activity: InfoActivity, defaultDate: String = "") {
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

    private fun checkNext() {
        if (userInfo.birthday == 0L || TextUtils.isEmpty(userInfo.name) || userInfo.gender == 0) {
            getView()?.showNextOrNot(false)
        } else {
            getView()?.showNextOrNot(true)
        }
    }
}
package com.palmapp.master.module_psy.heartrate

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.DatePicker
import android.widget.ImageView
import com.palmapp.master.baselib.BaseActivity
import com.palmapp.master.baselib.GoCommonEnv
import com.palmapp.master.baselib.bean.user.UserInfo
import com.palmapp.master.baselib.statistics.BaseSeq103OperationStatistic
import com.palmapp.master.module_psy.R
import kotlinx.android.synthetic.main.psy_activity_brith.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * 生日页
 */
class BirthActivity : BaseActivity(), DatePickerDialog.OnDateSetListener {

    private val sdf: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
    private var mBirthday = System.currentTimeMillis() //当前日期
    private var userInfo = GoCommonEnv.userInfo


    init {
        sdf.toLocalizedPattern()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.psy_activity_brith)
        BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(BaseSeq103OperationStatistic.heart_page_f000,"","3","")

        birthdayStr(mBirthday)

        findViewById<ImageView>(R.id.iv_titlebar_back).setOnClickListener {
            finish()
        }

        tv_birthday.setOnClickListener {
            selectBirthday()
        }

        tv_next.setOnClickListener {
            if (userInfo == null){
                userInfo = UserInfo()
            }
            userInfo?.birthday = mBirthday
            GoCommonEnv.storeUserInfo(userInfo)
            birthdayStr(mBirthday)

            startActivity(Intent(this,HeartRateReportActivity::class.java))
            finish()
        }
    }


    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = 0
        calendar.set(year, month, dayOfMonth, 0, 0, 0)
        mBirthday = calendar.timeInMillis

        birthdayStr(mBirthday)
    }


    private fun selectBirthday(defaultDate: String = "") {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = if (TextUtils.isEmpty(defaultDate)) {
            System.currentTimeMillis()
        } else {
            sdf.parse(defaultDate).time
        }

        val dialog = DatePickerDialog(
            this,
            this,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        dialog.datePicker.maxDate = Date().time
        dialog.show()
    }


    private fun birthdayStr(birthdayL: Long?) {
        var birthday = sdf.format(birthdayL)
        tv_birthday.text = birthday
    }

}



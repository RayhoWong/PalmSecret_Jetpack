package com.palmapp.master.module_psy.heartrate

import android.graphics.Color
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.palmapp.master.baselib.bean.user.getConstellationById
import com.palmapp.master.baselib.utils.AppUtil
import com.palmapp.master.baselib.utils.TimeUtils
import com.palmapp.master.module_psy.R
import kotlinx.android.synthetic.main.psy_fragment_love.*
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class LoveFragment : Fragment() {

    private var birthday: Long? = null


    companion object {
        fun getInstance(): LoveFragment {
            return LoveFragment()
        }
    }


    fun setData(birth: String) {
        this.birthday = birth.toLong()
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.psy_fragment_love, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initView()
    }


    private fun initView() {
        birthday?.let {
            var birth = TimeUtils.timeSeqFormat_2(it)
            //eg: 2020-03-23
            if (!TextUtils.isEmpty(birth)) {
                showCnt(birth)
            }
        }
    }


    private fun showCnt(birth: String) {
        //出生日期
        var year = birth.substring(0, 4)
        var month = birth.substring(5, 7)
        var day = birth.substring(8)

        var str = month.substring(0, 1)
        var str1 = month.substring(1)
        var str2 = day.substring(0, 1)
        var str3 = day.substring(1)

        var month1 = if (str == "0") {
            str1
        } else {
            month
        }
        var day1 = if (str2 == "0") {
            str3
        } else {
            day
        }
        tv_constellation.text = getConstellationById(TimeUtils.getConstellations(month1.toInt() - 1, day1.toInt()))

        showAge(year, month1, day1)
        showBirthday(birth)
        val times = arrayListOf<Date>()
        for (i in 0 until 10) {
            times.add(AppUtil.randomDate("2019-01-01", "2019-12-31"))
        }
        var sb = StringBuilder()
        for (i in 0 until 5) {
            sb.append(getMonth(times.get(i).month)).append(" ")
            sb.append(times.get(i).date)
            sb.append(";")
        }
        sb.deleteCharAt(sb.lastIndex)
        tv_lover_date.text = sb.toString()
        sb.clear()
        for (i in 5 until 10) {
            sb.append(getMonth(times.get(i).month)).append(" ")
            sb.append(times.get(i).date)
            sb.append(";")
        }
        sb.deleteCharAt(sb.lastIndex)
        tv_soul_date.text = sb.toString()
    }


    private fun showAge(year: String, month: String, day: String) {
        var year = year.toInt()
        val calendar = Calendar.getInstance()
        var nowmonth = calendar.get(Calendar.MONTH) + 1
        var nowday = calendar.get(Calendar.DATE)
        var gap = calendar.get(Calendar.YEAR) - year

        var str = month.substring(0, 1)
        var str1 = month.substring(1)
        var str2 = day.substring(0, 1)
        var str3 = day.substring(1)
        var month1 = if (str == "0") {
            str1
        } else {
            month
        }
        var day1 = if (str2 == "0") {
            str3
        } else {
            day
        }

        if (month1.toInt() > nowmonth) {
            gap -= 1
        } else if (month1.toInt() == nowmonth && day1.toInt() > nowday) {
            gap -= 1
        }

        tv_age.text = gap.toString()

        showLefts(year.toString(), month, day)
    }

    private fun getMonth(month: Int): String {
        return when (month) {
            0 -> "January"
            1 -> "February"
            2 -> "March"
            3 -> "April"
            4 -> "May"
            5 -> "June"
            6 -> "July"
            7 -> "August"
            8 -> "September"
            9 -> "October"
            10 -> "November"
            11 -> "December"
            else -> "January"
        }
    }


    private fun showBirthday(birth: String) {
        //出生日期
        var year = birth.substring(0, 4)
        var month = birth.substring(5, 7)
        var day = birth.substring(8)
        month = when (month) {
            "01" -> "January"
            "02" -> "February"
            "03" -> "March"
            "04" -> "April"
            "05" -> "May"
            "06" -> "June"
            "07" -> "July"
            "08" -> "August"
            "09" -> "September"
            "10" -> "October"
            "11" -> "November"
            "12" -> "December"
            else -> "January"
        }
        var birthdayDate = "$month $day $year"
        tv_birth.text = birthdayDate
    }


    private fun showLefts(year: String, month: String, day: String) {
        var birthStr = "$year-$month-$day"
        var birthDate = TimeUtils.strToShortDate(birthStr)
        var nowDate = Date(System.currentTimeMillis())

        var gap = TimeUtils.daysBetween(birthDate,nowDate)
        var days = String.format(resources.getString(R.string.heartrate_result_love_days), gap)
        val gS = String.format("%d", gap)
        try {
            val index1 = days.indexOf(gS)
            val builder = SpannableStringBuilder(days)
            builder.setSpan(
                ForegroundColorSpan(Color.parseColor("#ffbb59")),
                index1,
                index1 + gap.toString().length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            tv_days.text = builder
        } catch (e: Exception) {

        }
    }

}

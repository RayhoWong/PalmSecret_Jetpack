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
import com.palmapp.master.baselib.utils.TimeUtils
import com.palmapp.master.module_psy.R
import kotlinx.android.synthetic.main.psy_fragment_life.*
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class LifeFragment : Fragment() {

    private var birthday: Long? = null

    companion object {
        fun getInstance(): LifeFragment {
            return LifeFragment()
        }
    }

    fun setData(birth: String) {
        this.birthday = birth.toLong()
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.psy_fragment_life, container, false)
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
                showBirthday(birth)
            }
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
        var birthdayDate = " " + month + " ${day},${year}"
        tv_birth.text = birthdayDate

        showDeathDate(year, month)
    }


    private fun showDeathDate(year: String, month: String) {
        var gap = Calendar.getInstance().get(Calendar.YEAR) - year.toInt()
        var death_year: String
        var years: Int
        if (gap >= 75) {
            years = (5..20).random()
            death_year = (years + year.toInt() + gap).toString()
        } else {
            years = when (gap) {
                in 0..20 -> {
                    (75..90).random()
                }
                in 21..30 -> {
                    (60..80).random()
                }
                in 31..40 -> {
                    (50..70).random()
                }
                in 41..60 -> {
                    (40..50).random()
                }
                else -> {
                    (24..36).random()
                }
            }
            death_year = (years + year.toInt() + gap).toString()
        }

        var str = ""
        var str1 = month.substring(0, 1)
        var str2 = month.substring(1)
        if (str1 == "0") {
            //月份为0x格式 随机加1-3
            var random = (1..3).random()
            var str3 = (str2.toInt() + random).toString()
            str = if (str3.length == 2) {
                //相加月份>=10 直接取值
                str3
            } else {
                //相加月份<10 前面一位加0
                var builder = StringBuilder(str3)
                builder.insert(0, "0")
                builder.toString()
            }
        } else if (str1 == "1") {
            //月份为1x格式
            str = when (str2) {
                "0" -> {
                    var random = (1..2).random()
                    var builder = StringBuilder((str2.toInt() + random).toString())
                    builder.insert(0, "1")
                    builder.toString()
                }
                "1" -> {
                    var builder = StringBuilder((str2.toInt() + 1).toString())
                    builder.insert(0, "1")
                    builder.toString()
                }
                else -> "12"
            }
        }
        var death_month = when (str) {
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
            else -> "December"
        }
        var death_day = (1..31).random().toString()
        if (death_day.length < 2) {
            var builder = StringBuilder(death_day)
            builder.insert(0, "0")
            death_day = builder.toString()
        }
        var deathDate = " " + death_month + " ${death_day},${death_year}"
        tv_death.text = deathDate

        showLefts(years, death_month, death_day)
    }


    private fun showLefts(years: Int, death_month: String, death_day: String) {
        var str = when (death_month) {
            "January" -> 1
            "February" -> 2
            "March" -> 3
            "April" -> 4
            "May" -> 5
            "June" -> 6
            "July" -> 7
            "August" -> 8
            "September" -> 9
            "October" -> 10
            "November" -> 11
            "December" -> 12
            else -> 12
        }
        var months = years * 12 + str
        var days = months * 30 + death_day.toInt()

        var lefts = resources.getString(R.string.heartrate_result_life_lives_left, days, months)
        try {
            val dS = String.format("%d", days)
            val mS = String.format("%d", months)
            val index1 = lefts.indexOf(dS)
            val index2 = lefts.indexOf(mS)
            val builder = SpannableStringBuilder(lefts)
            builder.setSpan(
                ForegroundColorSpan(Color.parseColor("#ffbb59")),
                index1,
                index1 + dS.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            builder.setSpan(
                ForegroundColorSpan(Color.parseColor("#ffbb59")),
                index2,
                index2 + mS.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            tv_lefts.text = builder
        } catch (e: Exception) {

        }

        var meals = String.format(resources.getString(R.string.heartrate_result_life_meal), days * 3)
        try {
            val mS = String.format("%d", days * 3)
            val index1 = meals.indexOf(mS)
            val builder = SpannableStringBuilder(meals)
            builder.setSpan(
                ForegroundColorSpan(Color.parseColor("#ffbb59")),
                index1,
                index1 + (days * 3).toString().length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            tv_meals.text = builder
        } catch (e: Exception) {

        }

        var baths = String.format(resources.getString(R.string.heartrate_result_life_bath), days)
        try {
            val bs = String.format("%d", days)
            val index1 = baths.indexOf(bs)
            val builder = SpannableStringBuilder(baths)
            builder.setSpan(
                ForegroundColorSpan(Color.parseColor("#ffbb59")),
                index1,
                index1 + (days).toString().length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            tv_baths.text = builder
        } catch (e: Exception) {

        }

        var sleeps = String.format(resources.getString(R.string.heartrate_result_life_sleep), days)
        try {
            val ss = String.format("%d", days)
            val index1 = sleeps.indexOf(ss)
            val builder = SpannableStringBuilder(sleeps)
            builder.setSpan(
                ForegroundColorSpan(Color.parseColor("#ffbb59")),
                index1,
                index1 + (days).toString().length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            tv_times.text = builder
        } catch (e: Exception) {

        }

        var weeks = String.format(resources.getString(R.string.heartrate_result_life_week), days / 7)
        try {
            val ws = String.format("%d", days / 7)
            val index1 = weeks.indexOf(ws)
            val builder = SpannableStringBuilder(weeks)
            builder.setSpan(
                ForegroundColorSpan(Color.parseColor("#ffbb59")),
                index1,
                index1 + (days / 7).toString().length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            tv_weeks.text = builder
        } catch (e: Exception) {

        }
    }

}

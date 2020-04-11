package com.palmapp.master.infomation;

import com.palmapp.master.baselib.IView
import com.palmapp.master.baselib.bean.user.Gender

interface InfoView : IView {
    fun showGender(@Gender gender:Int)
    fun showBirthDayAndCnt(birth:String,cnt:String)
    fun showNextOrNot(isShow:Boolean)
}
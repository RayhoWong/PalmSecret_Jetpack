package com.palmapp.master.baselib.bean.user

import androidx.annotation.IntDef
import com.google.gson.annotations.SerializedName
import com.palmapp.master.baselib.GoCommonEnv
import com.palmapp.master.baselib.R
import java.io.Serializable
import java.lang.IllegalArgumentException

/**
 *  用户信息类
 * @author :     xiemingrui
 * @since :      2019/8/13
 */
const val GENDER_MALE = 1
const val GENDER_FEMALE = 2

const val ARIES = 1
const val TAURUS = 2
const val GEMINI = 3
const val CANCER = 4
const val LEO = 5
const val VIRGO = 6
const val LIBRA = 7
const val SCORPIO = 8
const val SAGITTARIUS = 9
const val CAPRICORN = 10
const val AQUARIUS = 11
const val PISCES = 12

class UserInfo : Serializable {
    @SerializedName("name")
    var name: String = ""
    @SerializedName("birthday")
    var birthday = 0L
    @Constellation
    @SerializedName("constellation")
    var constellation = -1
    @Gender
    @SerializedName("gender")
    var gender: Int = 0
    @SerializedName("flagMaps")
    var flagMaps = LinkedHashMap<String, String>()
}

@Retention(AnnotationRetention.SOURCE)
@IntDef(GENDER_MALE, GENDER_FEMALE)
annotation class Gender

@Retention(AnnotationRetention.SOURCE)
@IntDef(ARIES, TAURUS, GEMINI, CANCER, LEO, VIRGO, LIBRA, SCORPIO, SAGITTARIUS, CAPRICORN, AQUARIUS, PISCES)
annotation class Constellation

//根据id返回对应字符串
fun getConstellationById(@Constellation id: Int?): String {
    return when (id) {
        ARIES -> GoCommonEnv.getApplication().resources.getString(R.string.cnt_aries)
        TAURUS -> GoCommonEnv.getApplication().resources.getString(R.string.cnt_taurus)
        CANCER -> GoCommonEnv.getApplication().resources.getString(R.string.cnt_cancer)
        GEMINI -> GoCommonEnv.getApplication().resources.getString(R.string.cnt_gemini)
        LEO -> GoCommonEnv.getApplication().resources.getString(R.string.cnt_leo)
        VIRGO -> GoCommonEnv.getApplication().resources.getString(R.string.cnt_virgo)
        LIBRA -> GoCommonEnv.getApplication().resources.getString(R.string.cnt_libra)
        SCORPIO -> GoCommonEnv.getApplication().resources.getString(R.string.cnt_scorpio)
        SAGITTARIUS -> GoCommonEnv.getApplication().resources.getString(R.string.cnt_sagittarius)
        CAPRICORN -> GoCommonEnv.getApplication().resources.getString(R.string.cnt_capricorn)
        AQUARIUS -> GoCommonEnv.getApplication().resources.getString(R.string.cnt_aquarius)
        PISCES -> GoCommonEnv.getApplication().resources.getString(R.string.cnt_pisces)
        else -> ""
    }
}

fun getDateById(@Constellation id: Int?): String {
    return when (id) {
        ARIES -> "3/21 - 4/19"
        TAURUS -> "4/20 - 5/20"
        CANCER -> "6/22 - 7/22"
        GEMINI -> "5/21 - 6/21"
        LEO -> "7/23 - 8/22"
        VIRGO -> "8/23 - 9/22"
        LIBRA -> "9/23 - 10/23"
        SCORPIO -> "10/24 - 11/22"
        SAGITTARIUS -> "11/23 - 12/21"
        CAPRICORN -> "12/22 - 1/19"
        AQUARIUS -> "1/20 - 2/18"
        PISCES -> "2/19 - 3/20"
        else -> ""
    }
}

//获取星座图
fun getCntCover(@Constellation id: Int?): Int {
    return when (id) {
        -1->R.mipmap.ic_cnt_default
        ARIES -> R.mipmap.ic_aries
        TAURUS -> R.mipmap.ic_taurus
        CANCER -> R.mipmap.ic_cancer
        GEMINI -> R.mipmap.ic_gemini
        LEO -> R.mipmap.ic_leo
        VIRGO -> R.mipmap.ic_virgo
        LIBRA -> R.mipmap.ic_libra
        SCORPIO -> R.mipmap.ic_scorpio
        SAGITTARIUS -> R.mipmap.ic_sagittarius
        CAPRICORN -> R.mipmap.ic_capricorn
        AQUARIUS -> R.mipmap.ic_aquarius
        PISCES -> R.mipmap.ic_pisces
        else -> 0
    }
}

//获取背景透明的星座图
fun getCntTranCover(@Constellation id: Int?): Int {
    return when (id) {
        ARIES -> R.mipmap.aries
        TAURUS -> R.mipmap.taurus
        CANCER -> R.mipmap.cancer
        GEMINI -> R.mipmap.gemini
        LEO -> R.mipmap.leo
        VIRGO -> R.mipmap.virgo
        LIBRA -> R.mipmap.libra
        SCORPIO -> R.mipmap.scorpio
        SAGITTARIUS -> R.mipmap.sagittarius
        CAPRICORN -> R.mipmap.capricorn
        AQUARIUS -> R.mipmap.aquarius
        PISCES -> R.mipmap.pisces
        else -> 0
    }
}

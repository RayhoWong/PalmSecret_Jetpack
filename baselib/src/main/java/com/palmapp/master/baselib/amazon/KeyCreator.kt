package com.palmapp.master.baselib.amazon

import com.cs.bd.commerce.util.Machine
import com.palmapp.master.baselib.GoCommonEnv
import com.palmapp.master.baselib.utils.TimeUtils
import java.io.File
import java.util.*

/**
 * @author yangguanxiang
 * 面相亚马逊图片key生成器
 */
class KeyCreator {

    fun createAmazonKey(type: Int): String {
        val sb = StringBuilder()
        sb.append("image")
        sb.append(File.separator)
        // 功能入口function
        val function = when (type) {
            TYPE_FACE_DETECT -> "face"
            TYPE_BABY_REPORT_FATHER, TYPE_BABY_REPORT_MOTHER -> "baby" + File.separator + "report"
            TYPE_OLD_REPORT -> "old" + File.separator + "report"
            TYPE_APPEARANCE_PK -> "appearance_pk"
            TYPE_ETHNICITY_REPORT -> "ethnicity" + File.separator + "report"
            TYPE_GENDER_REPORT -> "gender" + File.separator + "report"
            else -> "face"
        }
        sb.append(function)
        sb.append(File.separator)
        sb.append(TimeUtils.longToStr(System.currentTimeMillis(), "yyyyMMdd", Locale.US))
        sb.append(File.separator)
        sb.append(Machine.getAndroidId(GoCommonEnv.getApplication()))
        sb.append(File.separator)
        if (TYPE_BABY_REPORT_FATHER == type) {
            sb.append("father")
            sb.append(File.separator)
        } else if (TYPE_BABY_REPORT_MOTHER == type) {
            sb.append("mother")
            sb.append(File.separator)
        }
        sb.append(System.currentTimeMillis())
        sb.append(getRandomString(8))
        sb.append(".jpg")
        return sb.toString()
    }

    companion object {

        /**
         * 人脸检测接口
         * image/face/日期yyyyMMdd/did/时间戳+随机数.jpg
         */
        const val TYPE_FACE_DETECT = 0
        /**
         * 宝宝预测接口-父亲
         * image/baby/report/日期yyyyMMdd/did/father/时间戳+随机数.jpg
         */
        const val TYPE_BABY_REPORT_FATHER = 1
        /**
         * 宝宝预测接口-母亲
         * image/baby/report/日期yyyyMMdd/did/mother/时间戳+随机数.jpg
         */
        const val TYPE_BABY_REPORT_MOTHER = 2
        /**
         * 变老接口
         * image/old/report/日期yyyyMMdd/did/时间戳+随机数.jpg
         */
        const val TYPE_OLD_REPORT = 3
        /**
         * 颜值PK
         * image/appearance_pk/日期yyyyMMdd/did/时间戳+随机数.jpg
         */
        const val TYPE_APPEARANCE_PK = 4
        /**
         * 种族预测
         * image/ethnicity/report/日期yyyyMMdd/did/时间戳+随机数.jpg
         */
        const val TYPE_ETHNICITY_REPORT = 5

        /**
         * 变性预测
         * image/gender/report/日期yyyyMMdd/did/时间戳+随机数.jpg
         */
        const val TYPE_GENDER_REPORT = 6

        /**
         * 获取随机字符串
         *
         * @param length 　字符串长度
         * @return
         */
        fun getRandomString(length: Int): String {
            val base = "0123456789AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz"
            val random = Random()
            val builder = StringBuilder()
            val baseLength = base.length
            for (i in 0 until length) {
                builder.append(base[random.nextInt(baseLength)])
            }
            return builder.toString()
        }
    }
}

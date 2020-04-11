package com.palmapp.master.baselib.bean.cnt

import androidx.annotation.StringDef
import com.google.gson.annotations.SerializedName
import com.palmapp.master.baselib.bean.BaseRequest

/**
 *  星座预测接口
 * @author :     xiemingrui
 * @since :      2019/8/7
 */
const val DAY = "day" // 日运势
const val TOMORROW = "tomorrow" // 明日运势
const val WEEK = "week" //周运势
const val MONTH = "month" //月运势
const val YEAR = "year" //年运势

class ForecastRequest(id: Int, @ForecastType vararg type: String) : BaseRequest() {
    @SerializedName("types")
    var types: ArrayList<String> = ArrayList<String>()
    @SerializedName("constellation_id")
    var constellation_id: Int? = null

    init {
        constellation_id = id
        types.addAll(type)
    }
}

@StringDef(
    DAY,
    TOMORROW,
    WEEK,
    MONTH,
    YEAR
)
@Retention(AnnotationRetention.SOURCE)
annotation class ForecastType
package com.palmapp.master.baselib.manager.config

import com.palmapp.master.baselib.manager.config.BaseConfig
import org.json.JSONArray
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 *
 * @author :     xiemingrui
 * @since :      2020/4/8
 */
class HomeConfig : BaseConfig(907) {
    val datas = HashMap<String, ArrayList<HomeConfigBean>>()
    override fun parseConfig(response: JSONArray) {
        datas.clear()
        repeat(response.length()) { index ->
            val jsonObject = response.getJSONObject(index)
            val order = jsonObject.getInt("order")
            val func = jsonObject.getString("func")
            val tab_sort = jsonObject.getString("tab_sort")
            if (!datas.containsKey(tab_sort)) {
                datas.put(tab_sort, ArrayList<HomeConfigBean>())
            }
            datas.get(tab_sort)?.add(HomeConfigBean(order, func))
        }
        datas.get("1")?.sort()
        datas.get("0")?.sort()
    }
}

const val TAB_FACE_OLD = "1"
const val TAB_FACE_BABY = "2"
const val TAB_FACE_MANGA = "3"
const val TAB_FACE_TRANSFORM = "4"
const val TAB_FACE_ANIMAL = "5"
const val TAB_FACE_CARTOON = "6"

const val TAB_PALM_DAILY = "1"
const val TAB_PALM_JUDG = "2"
const val TAB_PALM_MATCH = "3"
const val TAB_PALM_TEST = "4"

class HomeConfigBean(val order: Int, val func: String) : Comparable<HomeConfigBean> {
    override fun compareTo(other: HomeConfigBean): Int {
        return order - other.order
    }
}
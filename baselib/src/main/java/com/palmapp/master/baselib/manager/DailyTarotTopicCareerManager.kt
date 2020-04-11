package com.palmapp.master.baselib.manager

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import com.google.gson.reflect.TypeToken
import com.palmapp.master.baselib.GoCommonEnv
import com.palmapp.master.baselib.bean.tarot.DailyTarotBean
import com.palmapp.master.baselib.bean.tarot.TarotBean
import com.palmapp.master.baselib.bean.tarot.TopicAngleAnswer
import com.palmapp.master.baselib.constants.AppConstants
import com.palmapp.master.baselib.constants.PreConstants
import com.palmapp.master.baselib.utils.GoGson
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*
import kotlin.collections.ArrayList

/**
 *
 * @author :     huangweihao
 * @since :      2019/10/14
 * 职业塔罗牌话题管理
 */
object DailyTarotTopicCareerManager {

    var cacheTarot = arrayListOf<TopicAngleAnswer>()  //缓存的塔罗牌
    var defaultTarot = arrayListOf<TopicAngleAnswer>() //默认第一次请求 获取的4张话题塔罗牌
    var result = "" //结果语
    private val pref = GoPrefManager.getInstance("pre_today", Context.MODE_PRIVATE)


    @SuppressLint("CheckResult")
    fun loadData(listener: OnShowTarotTopicListener) {
        if (cacheTarot.isEmpty()) {
            //获取添加的塔罗牌
            val temp = GoGson.fromJson<ArrayList<TopicAngleAnswer>>(
                pref.getString(PreConstants.Today.KEY_TODAY_CACHE_TAROT_TOPIC_CAREER, ""),
                object : TypeToken<ArrayList<TopicAngleAnswer>>() {}.type
            )
            if (temp != null) {
                cacheTarot = temp
            }
        }

        if (defaultTarot.isEmpty()) {
            //获取第一次获取的塔罗牌
            val temp = GoGson.fromJson<ArrayList<TopicAngleAnswer>>(
                pref.getString(PreConstants.Today.KEY_TODAY_CACHE_TAROT_TOPIC_CAREER_DEFAULT, ""),
                object : TypeToken<ArrayList<TopicAngleAnswer>>() {}.type
            )
            if (temp != null) {
                defaultTarot = temp
            }
        }

        //获取结果语
        result = pref.getString(PreConstants.Today.KEY_TODAY_CACHE_TAROT_TOPIC_CAREER_RESULT, "")

        val cacheDay = pref.getInt(PreConstants.Today.KEY_TODAY_CACHE_TOPIC_CAREER_DAY_OF_YEAR, -1)
        val currentDay = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        if (currentDay != cacheDay) {
            //缓存时间是一天 不同一天，清空缓存
            cacheTarot.clear()
            defaultTarot.clear()
            result = ""
            pref.remove(PreConstants.Today.KEY_TODAY_CACHE_TAROT_TOPIC_CAREER_RESULT)
            pref.remove(PreConstants.Today.KEY_TODAY_CACHE_TAROT_TOPIC_CAREER)
            pref.remove(PreConstants.Today.KEY_TODAY_CACHE_TAROT_TOPIC_CAREER_DEFAULT)
        }
        pref.putInt(PreConstants.Today.KEY_TODAY_CACHE_TOPIC_CAREER_DAY_OF_YEAR, currentDay).apply()
        listener?.showTarot(defaultTarot,cacheTarot, result)
    }


    /**
     * 存储默认塔罗牌
     */
    fun storeDefaultTarotTopic(list: List<TopicAngleAnswer>, result: String) {
        if (list.isNotEmpty()) {
            defaultTarot = list as ArrayList<TopicAngleAnswer>
            pref.putString(
                PreConstants.Today.KEY_TODAY_CACHE_TAROT_TOPIC_CAREER_DEFAULT,
                GoGson.toJson(defaultTarot)
            )
            pref.putString(
                PreConstants.Today.KEY_TODAY_CACHE_TAROT_TOPIC_CAREER_RESULT,
                result
            )
            pref.apply()
        }
    }

    /**
     * 存储增加的塔罗牌
     */
    fun storeCacheTarotTopic(list: List<TopicAngleAnswer>) {
        if (list.isNotEmpty()) {
            cacheTarot = list as ArrayList<TopicAngleAnswer>
            pref.putString(
                PreConstants.Today.KEY_TODAY_CACHE_TAROT_TOPIC_CAREER,
                GoGson.toJson(cacheTarot)
            )
            pref.apply()
        }
    }


    interface OnShowTarotTopicListener {
        /**
         * 返回数据
         * default: 默认第一次请求 获取的5张话题塔罗牌
         * list: 缓存的塔罗牌
         * result: 结果语
         */
        fun showTarot(default: List<TopicAngleAnswer>, list: List<TopicAngleAnswer>,result: String)
    }
}
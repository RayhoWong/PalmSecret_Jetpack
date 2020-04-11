package com.palmapp.master.baselib.manager

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import com.google.gson.reflect.TypeToken
import com.palmapp.master.baselib.GoCommonEnv
import com.palmapp.master.baselib.bean.tarot.DailyTarotBean
import com.palmapp.master.baselib.bean.tarot.TarotBean
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
 * @author :     xiemingrui
 * @since :      2019/8/26
 */
object DailyTarotManager {
    var cacheTarot = arrayListOf<DailyTarotBean>()
    private val pref = GoPrefManager.getInstance("pre_today", Context.MODE_PRIVATE)
    @SuppressLint("CheckResult")
    fun loadData(listener: OnShowTarotListener) {
        if (cacheTarot.isEmpty()) {
            val temp = GoGson.fromJson<ArrayList<DailyTarotBean>>(
                pref.getString(PreConstants.Today.KEY_TODAY_CACHE_TAROT, ""),
                object : TypeToken<ArrayList<DailyTarotBean>>() {}.type
            )
            if (temp != null) {
                cacheTarot = temp
            }

        }
        val cacheDay = pref.getInt(PreConstants.Today.KEY_TODAY_CACHE_DAY_OF_YEAR, -1)
        val lastCnt = pref.getInt(PreConstants.Today.KEY_TODAY_CACHE_CNT, -1)
        val currentDay = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        val currentCnt = GoCommonEnv.userInfo?.constellation ?: 1
        if (lastCnt != currentCnt || currentDay != cacheDay) {
            //不同星座或者不同一天，清空缓存
            cacheTarot.clear()
            pref.remove(PreConstants.Today.KEY_TODAY_CACHE_TAROT_RESULT)
            pref.remove(PreConstants.Today.KEY_TODAY_CACHE_TAROT)
        }

        pref.putInt(PreConstants.Today.KEY_TODAY_CACHE_CNT, currentCnt)
            .putInt(PreConstants.Today.KEY_TODAY_CACHE_DAY_OF_YEAR, currentDay).apply()
        //开始->Daily Tarot
        Observable.create<ArrayList<DailyTarotBean>> {
            if (cacheTarot.isNotEmpty()) {
                it.onNext(cacheTarot)
            }
            it.onComplete()
        }.switchIfEmpty(Observable.create<ArrayList<DailyTarotBean>> {
            val list = GoCommonEnv.tarotList.shuffled().take(3)
            cacheTarot.clear()
            repeat(list.size) { pos ->
                val temp = DailyTarotBean()
                temp.card_key = list.get(pos).card_key
                temp.name = list.get(pos).name
                temp.cover = list.get(pos).cover
                temp.type = when (pos) {
                    0 -> AppConstants.TAROT_CARD_TYPE_LOVE
                    1 -> AppConstants.TAROT_CARD_TYPE_FORTUNE
                    2 -> AppConstants.TAROT_CARD_TYPE_CAREER
                    else -> ""
                }
                cacheTarot.add(temp)
            }
            pref.putString(PreConstants.Today.KEY_TODAY_CACHE_TAROT, GoGson.toJson(cacheTarot)).apply()
            it.onNext(cacheTarot)
            it.onComplete()
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe {
            listener.showTarot(it)
        }
    }

    fun storeCacheTarot() {
        pref.putString(
            PreConstants.Today.KEY_TODAY_CACHE_TAROT,
            GoGson.toJson(cacheTarot)
        ).apply()
    }

    interface OnShowTarotListener {
        fun showTarot(list: List<DailyTarotBean>)
    }
}
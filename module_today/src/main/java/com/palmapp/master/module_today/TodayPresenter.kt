package com.palmapp.master.module_today

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.text.SpannedString
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import com.google.gson.reflect.TypeToken
import com.palmapp.master.baselib.BasePresenter
import com.palmapp.master.baselib.GoCommonEnv
import com.palmapp.master.baselib.Product.MODULE_ID_QUOTE
import com.palmapp.master.baselib.bean.CacheBean
import com.palmapp.master.baselib.bean.TodayBean
import com.palmapp.master.baselib.bean.pay.ModuleConfig
import com.palmapp.master.baselib.bean.tarot.*
import com.palmapp.master.baselib.constants.AppConstants
import com.palmapp.master.baselib.constants.PreConstants
import com.palmapp.master.baselib.manager.DailyTarotManager
import com.palmapp.master.baselib.manager.GoPrefManager
import com.palmapp.master.baselib.manager.ThreadExecutorProxy
import com.palmapp.master.baselib.utils.GoGson
import com.palmapp.master.module_network.HttpClient
import com.palmapp.master.module_network.NetworkSubscriber
import com.palmapp.master.module_network.NetworkTransformer
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function3
import io.reactivex.schedulers.Schedulers
import java.util.*

/**
 *
 * @author :     xiemingrui
 * @since :      2019/7/31
 */
class TodayPresenter : BasePresenter<TodayView>() {
    var cacheConfig: ModuleConfig? = null   //缓存大banner的图片下发
    var cacheColor: IntArray? = null    //缓存幸运颜色
    var cacheNumber: Int = -1   //缓存幸运数字
    private val pref = GoPrefManager.getInstance("pre_today", Context.MODE_PRIVATE)
    override fun onAttach() {
    }

    override fun onDetach() {
    }

    @SuppressLint("CheckResult")
    fun loadData() {
        ThreadExecutorProxy.execute(Runnable {
            if (cacheConfig == null) {
                cacheConfig = GoGson.fromJson<ModuleConfig>(
                    pref.getString(PreConstants.Today.KEY_TODAY_CACHE_QUOTE, ""),
                    ModuleConfig::class.java
                )
            }
            if (cacheColor == null) {
                cacheColor = GoGson.fromJson<IntArray>(
                    pref.getString(PreConstants.Today.KEY_TODAY_CACHE_COLOR, ""),
                    IntArray::class.java
                )
            }

            if (cacheNumber == -1) {
                cacheNumber = pref.getInt(PreConstants.Today.KEY_TODAY_CACHE_NUMBER, -1)
            }
            val cacheDay = pref.getInt(PreConstants.Today.KEY_TODAY_CACHE_DAY_OF_YEAR, -1)
            val lastCnt = pref.getInt(PreConstants.Today.KEY_TODAY_CACHE_CNT, -1)
            val currentDay = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
            val currentCnt = GoCommonEnv.userInfo?.constellation ?: 1
            if (lastCnt != currentCnt || currentDay != cacheDay) {
                //不同星座或者不同一天，清空缓存
                cacheConfig = null
                cacheColor = null
                cacheNumber = -1
                pref.remove(PreConstants.Today.KEY_TODAY_CACHE_QUOTE)
                pref.remove(PreConstants.Today.KEY_TODAY_CACHE_COLOR)
                pref.remove(PreConstants.Today.KEY_TODAY_CACHE_TAROT_RESULT)
                pref.remove(PreConstants.Today.KEY_TODAY_CACHE_TAROT)
                pref.remove(PreConstants.Today.KEY_TODAY_CACHE_NUMBER)
            }
            pref.putInt(PreConstants.Today.KEY_TODAY_CACHE_CNT, currentCnt).apply()

            //开始->名人名言
            Observable.create<ModuleConfig> {
                if (cacheConfig != null) {
                    it.onNext(cacheConfig!!)
                }
                it.onComplete()
            }.switchIfEmpty(HttpClient.getConfRequest().getResource(MODULE_ID_QUOTE)
                .compose(NetworkTransformer.toMainSchedulers())
                .observeOn(Schedulers.io()).map {
                    val ids = pref.getString(PreConstants.Today.KEY_TODAY_QUOTE_IDS, "").split("-")
                    val result = it.contents.filterNot {
                        ids.contains(it.id.toString())
                    }.random()
                    pref.putString(PreConstants.Today.KEY_TODAY_QUOTE_IDS, "${ids}-${result.id}")
                        .putInt(PreConstants.Today.KEY_TODAY_CACHE_DAY_OF_YEAR, currentDay)
                        .putString(PreConstants.Today.KEY_TODAY_CACHE_QUOTE, GoGson.toJson(result)).apply()
                    result
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : NetworkSubscriber<ModuleConfig>() {
                    override fun onNext(t: ModuleConfig) {
                        getView()?.showQuote(t)
                    }
                })
            val h = Random().nextInt(12) * 30f
            //开始->Lucky Color
            Observable.create<IntArray> {
                if (cacheColor != null) {
                    it.onNext(cacheColor!!)
                }
                it.onComplete()
            }.switchIfEmpty(Observable.create<IntArray> {
                val colors = IntArray(3)

                repeat(3) {
                    colors[it] = Color.HSVToColor(floatArrayOf(h, 0.45f + (it * 0.15f), 1f - (it * 0.1f)))
                }
                it.onNext(colors)
                it.onComplete()
            }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe {
                var cloth: String? = null
                if (cacheColor != null) {
                    cloth = pref.getString(PreConstants.Today.KEY_TODAY_CACHE_CLOTHES, "")
                } else {
                    val ta =
                        getView()?.getContext()?.resources?.obtainTypedArray(com.palmapp.master.baselib.R.array.clothes)
                    cloth = ta?.getString((h.toInt() / 30))
                    ta?.recycle()
                }
                cacheColor = it
                pref.putInt(PreConstants.Today.KEY_TODAY_CACHE_DAY_OF_YEAR, currentDay)
                    .putString(PreConstants.Today.KEY_TODAY_CACHE_COLOR, GoGson.toJson(it))
                    .putString(PreConstants.Today.KEY_TODAY_CACHE_CLOTHES, cloth ?: "").apply()
                getView()?.showLuckyColor(it, cloth ?: "")
            }

            //开始->Lucky Number
            Observable.create<Int> {
                if (cacheNumber != -1) {
                    it.onNext(cacheNumber)
                }
                it.onComplete()
            }.switchIfEmpty(Observable.create<Int> {
                val result = Random().nextInt(10)
                pref.putInt(PreConstants.Today.KEY_TODAY_CACHE_DAY_OF_YEAR, currentDay)
                    .putInt(PreConstants.Today.KEY_TODAY_CACHE_NUMBER, result).apply()
                it.onNext(result)
                it.onComplete()
            }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe {
                getView()?.showLuckyNum(it)
            }

            //开始->Daily Tarot
            DailyTarotManager.loadData(object : DailyTarotManager.OnShowTarotListener {
                override fun showTarot(list: List<DailyTarotBean>) {
                    getView()?.showTarot(list)
                    getView()?.showTarotResult()
                }
            })

        })
    }

    //开始翻牌
    fun flipTarot(key: String) {
        repeat(DailyTarotManager.cacheTarot.size) {
            val tarot = DailyTarotManager.cacheTarot.get(it)
            if (TextUtils.equals(tarot.card_key, key)) {
                tarot.isFlip = true
            }
        }
        pref.putString(PreConstants.Today.KEY_TODAY_CACHE_TAROT, GoGson.toJson(DailyTarotManager.cacheTarot)).apply()
    }

    fun showDialog(key: String) {
        var tarotBean: DailyTarotBean? = null
        DailyTarotManager.cacheTarot.forEach {
            if (TextUtils.equals(it.card_key, key)) {
                tarotBean = it
                return@forEach
            }
        }
        val dialog = Dialog(getView()?.getContext(), com.palmapp.master.baselib.R.style.CommonDialog)
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(false)
        val window = dialog.window
        window.setContentView(R.layout.today_layout_tarot)
        window.setBackgroundDrawableResource(R.color.color_d9000000)
        window.findViewById<View>(R.id.iv_today_close).setOnClickListener { dialog.dismiss() }
        window.findViewById<ImageView>(R.id.iv_today_res).setImageResource(GoCommonEnv.tarotMap.get(key)?.cover ?: 0)
        val lp = window.getAttributes()
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.MATCH_PARENT
        window.setAttributes(lp)
        val des = window.findViewById<TextView>(R.id.tv_today_des)
        tarotBean?.let {
            if (!TextUtils.isEmpty(it.result)) {
                des.text = it.result
                window.findViewById<TextView>(R.id.tv_today_quote).text = it.name
                dialog.show()
            } else {
                val request = DailyTarotAnswerRequest()
                request.card_keys.add(it.card_key)
                HttpClient.getTarotRequest().getDailTarotAnswer(request).compose(NetworkTransformer.toMainSchedulers())
                    .subscribe(object : NetworkSubscriber<DailyTarotAnswerResponse>() {
                        override fun onNext(t: DailyTarotAnswerResponse) {
                            t.daily_tarot_answer_map?.let { map ->
                                val temp = map.get(it.card_key)
                                if (temp != null)
                                    it.result = when (it.type) {
                                        AppConstants.TAROT_CARD_TYPE_LOVE -> if (Random().nextBoolean()) temp.love_reversed else temp.love_upright
                                        AppConstants.TAROT_CARD_TYPE_FORTUNE -> if (Random().nextBoolean()) temp.fortune_reversed else temp.fortune_upright
                                        AppConstants.TAROT_CARD_TYPE_CAREER -> if (Random().nextBoolean()) temp.career_reversed else temp.career_upright
                                        else -> ""
                                    }
                                des.text = it.result
                                window.findViewById<TextView>(R.id.tv_today_quote).text = it.name
                                DailyTarotManager.cacheTarot.forEach {
                                    getView()?.showTarotResult()
                                }
                                dialog.show()
                            }
                            pref.putString(
                                PreConstants.Today.KEY_TODAY_CACHE_TAROT,
                                GoGson.toJson(DailyTarotManager.cacheTarot)
                            ).apply()
                        }
                    })
            }
        }
    }

    fun share() {

    }

}
package com.palmapp.master.module_palm.result

import android.content.Context
import com.palmapp.master.baselib.BaseMultiplePresenter
import com.palmapp.master.baselib.GoCommonEnv
import com.palmapp.master.baselib.constants.PreConstants
import com.palmapp.master.baselib.manager.GoPrefManager
import com.palmapp.master.module_palm.R
import com.palmapp.master.module_palm.scan.PalmResultCache
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit

/**
 *
 * @author :     xiemingrui
 * @since :      2020/3/18
 */
class PalmChatPresenter() : BaseMultiplePresenter<PalmChatView>() {
    val datas = ArrayList<PalmDataHolder>()
    var disposable: Disposable? = null
    val pref = GoPrefManager.getInstance("Timer", Context.MODE_PRIVATE)
    var time = 5L
    override fun onAttach(pView: PalmChatView, context: Context) {
        super.onAttach(pView, context)
        time = if (pref.getBoolean(PreConstants.Palm.KEY_PALM_CHAT_SHOW, false)) 0L else 5L
        pref.putBoolean(PreConstants.Palm.KEY_PALM_CHAT_SHOW,true)
    }

    fun start(handleinfos: PalmResultCache?) {
        handleinfos?.let {
            datas.add(PalmDataHolder(0, GoCommonEnv.getApplication().getString(R.string.palm_detail_first_line,it.expert_name), 0, ""))
            datas.add(PalmDataHolder(0, GoCommonEnv.getApplication().getString(R.string.palm_detail_second_line), 0, ""))
            datas.add(PalmDataHolder(0, it.palm_type, 0, ""))
            datas.add(PalmDataHolder(1, it.thumb_length, R.mipmap.palm_analyze_pic1, GoCommonEnv.getApplication().getString(R.string.palm_detail_thumb_line)))
            datas.add(PalmDataHolder(1, it.shizhi_length, R.mipmap.palm_analyze_pic2, GoCommonEnv.getApplication().getString(R.string.palm_detail_shizhi_line)))
            datas.add(PalmDataHolder(1, it.fuck_length, R.mipmap.palm_analyze_pic3, GoCommonEnv.getApplication().getString(R.string.palm_detail_fuck_line)))
            datas.add(PalmDataHolder(1, it.wuming_length, R.mipmap.palm_analyze_pic4, GoCommonEnv.getApplication().getString(R.string.palm_detail_wuming_line)))
            datas.add(PalmDataHolder(1, it.small_length, R.mipmap.palm_analyze_pic5, GoCommonEnv.getApplication().getString(R.string.palm_detail_small_line)))
        }

        if (time == 5L) {
            disposable = Observable.interval(0, time, TimeUnit.SECONDS).take(datas.size.toLong()).observeOn(AndroidSchedulers.mainThread()).subscribe {
                getView()?.addResult(datas.get(it.toInt()))
            }
        } else {
            getView()?.showResult(datas)
        }
    }

    override fun onDetach() {
        super.onDetach()
        disposable?.dispose()
    }
}
package com.palmapp.master.module_home.fragment.psy;

import com.google.gson.reflect.TypeToken
import com.palmapp.master.baselib.BasePresenter
import com.palmapp.master.baselib.bean.CacheBean
import com.palmapp.master.baselib.bean.quiz.QuizListBean
import com.palmapp.master.baselib.bean.quiz.QuizListRequest
import com.palmapp.master.baselib.bean.quiz.QuizListResponse
import com.palmapp.master.baselib.constants.PreConstants
import com.palmapp.master.baselib.manager.GoPrefManager
import com.palmapp.master.baselib.utils.GoGson
import com.palmapp.master.module_network.HttpClient
import com.palmapp.master.module_network.NetworkSubscriber
import com.palmapp.master.module_network.NetworkTransformer
import io.reactivex.Observable

class PsychologyPresenter : BasePresenter<PsychologyView>() {
    val expiredTime = 8 * 60 * 60 * 1000L
    override fun onAttach() {
    }

    override fun onDetach() {
    }

    fun loadData() {
        getView()?.showLoading()
        Observable.create<QuizListResponse> {
            val list = loadLocalData()
            if (list == null) {
                it.onComplete()
            } else {
                it.onNext(list)
                it.onComplete()
            }
        }.switchIfEmpty(loadNetworkData()).subscribe(object : NetworkSubscriber<QuizListResponse>() {
            override fun onNext(t: QuizListResponse) {
                getView()?.showContent(t.quizzes)
                val cacheBean = CacheBean<QuizListResponse>( System.currentTimeMillis(),t)
                GoPrefManager.getDefault().putString(PreConstants.Cache.KEY_CACHE_HOME_PSY, GoGson.toJson(cacheBean))
                    .apply()
            }
        })
    }

    private fun loadLocalData(): QuizListResponse? {
        val bean = GoGson.fromJson<CacheBean<QuizListResponse>>(
            GoPrefManager.getDefault().getString(
                PreConstants.Cache.KEY_CACHE_HOME_PSY,
                ""
            ), object : TypeToken<CacheBean<QuizListResponse>>() {}.type
        )
        if (bean == null || bean.isExpired(expiredTime)) {
            return null
        }
        return bean.data
    }

    private fun loadNetworkData(): Observable<QuizListResponse> {
        val body = QuizListRequest()
        body.category_id = QuizListRequest.QUIZ_CATEGORY_ID
        body.accept_type = listOf("ACCORDING_SCORE","ACCORDING_OPTION","JUMP","COMBINATION")
        return HttpClient.getFaceRequest().getQuizList(body = body)
            .compose(NetworkTransformer.toMainSchedulers())
    }
}
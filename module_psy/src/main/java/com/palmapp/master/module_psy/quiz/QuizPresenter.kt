package com.palmapp.master.module_psy.quiz;

import android.text.TextUtils
import com.palmapp.master.module_network.HttpClient
import com.palmapp.master.baselib.BasePresenter
import com.palmapp.master.baselib.bean.quiz.*
import com.palmapp.master.module_network.NetworkSubscriber
import com.palmapp.master.module_network.NetworkTransformer

class QuizPresenter : BasePresenter<QuizView>() {
    private val result = HashMap<String, String>()
    private var quiz_id = ""
    private var quiz_type = ""
    fun loadData(id: String) {
        val request = QuizContentRequest()
        quiz_id = id
        request.quiz_id = id
        getView()?.showLoading()
        HttpClient.getFaceRequest().getQuizContent(request).compose(NetworkTransformer.toMainSchedulers())
            .subscribe(object : NetworkSubscriber<QuizContentResponse>() {
                override fun onNext(t: QuizContentResponse) {
                    quiz_type = t.quiz_type ?: QuizListRequest.ACCORDING_SCORE
                    t.quiz?.let {
                        getView()?.showContent(it)
                    }
                }

                override fun onNetworkError() {
                    super.onNetworkError()
                    getView()?.showNetworkError()
                }

                override fun onServiceError() {
                    super.onServiceError()
                    getView()?.showServerError()
                }
            })
    }

    fun uploadAnswer() {
        val body = AnswerRequest()
        body.answer_list = ArrayList()
        result.values.forEach {
            body.answer_list?.add(QuizOptionBean(it.toLong()))
        }
        body.quiz_type = quiz_type
        body.question_id = quiz_id.toLong()
        getView()?.showLoading()
        HttpClient.getFaceRequest().getAnswer(body).compose(NetworkTransformer.toMainSchedulers())
            .subscribe(object : NetworkSubscriber<AnswerResponse>() {
                override fun onNext(t: AnswerResponse) {
                    getView()?.showResult(t)
                }
            })
    }

    fun saveAnswer(question_id: String, option_id: String) {
        result.remove(question_id)
        result.put(question_id, option_id)
    }

    fun isSelect(question_id: String, option_id: String): Boolean {
        if (!result.containsKey(question_id))
            return false
        return (TextUtils.equals(result.get(question_id), option_id))
    }

    override fun onAttach() {
    }

    override fun onDetach() {
    }

}
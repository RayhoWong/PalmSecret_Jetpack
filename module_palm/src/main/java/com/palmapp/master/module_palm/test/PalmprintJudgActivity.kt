package com.palmapp.master.module_palm.test

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.palmapp.master.baselib.BaseActivity
import com.palmapp.master.baselib.bean.RESPONSE_SUCCESS
import com.palmapp.master.baselib.bean.quiz.*
import com.palmapp.master.baselib.bean.quiz.QuizListRequest.Companion.ACCORDING_OPTION
import com.palmapp.master.baselib.constants.RouterConstants.ACTIVITY_PALM_QUIZ
import com.palmapp.master.baselib.utils.NetworkUtil
import com.palmapp.master.module_network.HttpClient
import com.palmapp.master.module_network.NetworkSubscriber
import com.palmapp.master.module_network.NetworkTransformer
import com.palmapp.master.module_palm.R
import kotlinx.android.synthetic.main.palm_activity_palmprint_judg.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.lang.ref.WeakReference

/**
 * 手相问答
 */

@Route(path = ACTIVITY_PALM_QUIZ)
class PalmprintJudgActivity : BaseActivity() {
    private lateinit var mAdapter: PalmprintJudgAdapter
    private lateinit var mData: List<QuestionDTO>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.palm_activity_palmprint_judg)

        initView()


        getQuizId()
//        getQuizContent("121")
    }

    private fun initView() {
        var mTvTitle = lt_title_bar.findViewById<TextView>(R.id.tv_titlebar_title)
        var mIvBack = lt_title_bar.findViewById<ImageView>(R.id.iv_titlebar_back)
        mTvTitle.text = getString(R.string.palm_quiz_title)
        mIvBack.setOnClickListener {
            finish()
        }
        rcv.layoutManager = GridLayoutManager(this@PalmprintJudgActivity, 2)
    }


    //获取手相问答的问题id
    private fun getQuizId() {
        //请求消息体
        val body = QuizListRequest()
        body.category_id = QuizListRequest.PSY_CATEGORY_ID
        body.accept_type = listOf("ACCORDING_SCORE", "ACCORDING_OPTION", "JUMP", "COMBINATION")
        if (NetworkUtil.isConnected(this)) {
            HttpClient
                .getFaceRequest()
                .getQuizList(body = body)
                .compose(NetworkTransformer.toMainSchedulers<QuizListResponse>())
                .subscribe(object : NetworkSubscriber<QuizListResponse>() {
                    override fun onNext(t: QuizListResponse) {
                        if (t.status_result?.status_code.equals(RESPONSE_SUCCESS) && t.quizzes!!.isNotEmpty()) {
                            for (item in t.quizzes!!) {
                                //选项类型是ACCORDING_OPTION
                                if (item.type == ACCORDING_OPTION) {
                                    getQuizContent(item.quiz_id)
                                }
                            }
                        } else {
                            Toast.makeText(
                                this@PalmprintJudgActivity,
                                getString(R.string.net_server_error),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                })
        } else {
            Toast.makeText(
                this@PalmprintJudgActivity, getString(R.string.net_error), Toast.LENGTH_SHORT
            ).show()
        }
    }


    /**
     * 测试题内容获取
     * @id 测试题id
     */
    private fun getQuizContent(id: String) {
        palm_loading.visibility = View.VISIBLE
        palm_loading.showNetworkLoadingView()
        var request = QuizContentRequest()
        request.quiz_id = id
        if (NetworkUtil.isConnected(this)) {
            HttpClient
                .getFaceRequest()
                .getQuizContent(request)
                .compose(NetworkTransformer.toMainSchedulers<QuizContentResponse>())
                .subscribe(object : NetworkSubscriber<QuizContentResponse>() {
                    override fun onNext(t: QuizContentResponse) {
                        palm_loading.hideAllView()
                        if (t.status_result?.status_code.equals(RESPONSE_SUCCESS) && t.quiz != null) {
                            if (t.quiz!!.questions.isNotEmpty()) {
                                mData = t.quiz!!.questions
                                mAdapter = PalmprintJudgAdapter(this@PalmprintJudgActivity, mData)
                                rcv.adapter = mAdapter
                            }
                        } else {
                            Toast.makeText(
                                this@PalmprintJudgActivity,
                                getString(R.string.net_server_error),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                })
        } else {
            palm_loading.hideAllView()
            Toast.makeText(
                this@PalmprintJudgActivity, getString(R.string.net_error), Toast.LENGTH_SHORT
            ).show()
        }
    }

}

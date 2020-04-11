package com.palmapp.master.module_palm.test

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.launcher.ARouter
import com.palmapp.master.baselib.BaseActivity
import com.palmapp.master.baselib.bean.quiz.OptionDTO
import com.palmapp.master.baselib.bean.quiz.QuestionDTO
import com.palmapp.master.baselib.bean.quiz.QuziAnswerDTO
import com.palmapp.master.baselib.constants.AppConstants
import com.palmapp.master.baselib.constants.RouterConstants
import com.palmapp.master.baselib.event.SubscribeUpdateEvent
import com.palmapp.master.baselib.manager.RouterServiceManager
import com.palmapp.master.baselib.manager.RouterServiceManager.goHomeAndClearTop
import com.palmapp.master.baselib.proxy.BillingServiceProxy
import com.palmapp.master.baselib.view.CommonDialog
import com.palmapp.master.module_palm.R
import kotlinx.android.synthetic.main.palm_activity_palmprint_judg.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.lang.ref.WeakReference

/**
 * 手相问答详情
 */
class PalmprintJudgDetailActivity : BaseActivity(), CommonDialog.OnClickBottomListener,
    PalmprintJudgDetailAdapter.OnCheckedListener {
    override fun onNegativeClick() {
    }

    private var mTitleBarTitle: String? = null
    private var mOptions: List<OptionDTO> = listOf()
    private var mAnswers: List<QuziAnswerDTO> = listOf()
    private lateinit var mAdapter: PalmprintJudgDetailAdapter

    private var mOption: OptionDTO? = null
    private var mAnswer: QuziAnswerDTO? = null

    private var mActivity: PalmprintJudgDetailActivity = this@PalmprintJudgDetailActivity


    //接受某个问题 from PalmprintJudgAdapter
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onQuestionEvent(event: QuestionDTO) {
        mTitleBarTitle = event.title
        if (event.options != null && event.answers != null) {
            mOptions = event.options
            mAnswers = event.answers
        }
        //将问题的id传给手相问答结果页
        EventBus.getDefault().postSticky(event.question_id)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.palm_activity_palmprint_judg_detail)

        //注册事件
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
        initView()
    }

    private fun initView() {
        var mIvBack = lt_title_bar.findViewById<ImageView>(R.id.iv_titlebar_back)
        var mTvTitle = lt_title_bar.findViewById<TextView>(R.id.tv_titlebar_title)
        mTvTitle.text = mTitleBarTitle
        mIvBack.setOnClickListener {
            exitTip()
        }
        mAdapter = PalmprintJudgDetailAdapter(this, mOptions, mAnswers)
        mAdapter.setCheckedListener(this@PalmprintJudgDetailActivity)
        rcv.layoutManager = LinearLayoutManager(this@PalmprintJudgDetailActivity)
        rcv.adapter = mAdapter
    }

    override fun onBackPressed() {
//        super.onBackPressed()
        exitTip()
    }

    /**
     * 页面退出提醒
     */
    private fun exitTip() {
        //用户未选中 弹出dialog
        if (mAdapter.getSelectPosition() == null) {
            var dialog = CommonDialog(this)
            dialog
                .setTitle(getString(R.string.psy_confirm_quit))
                .setPositive(getString(R.string.yes))
                .setNegative(getString(R.string.no))
                .setOnClickBottomListener(this)
                .show()
        } else {
            finish()
        }
    }

    override fun onPositiveClick() {
        goHomeAndClearTop()
        finish()
    }

    override fun onChecked(itemView: View, position: Int) {
        mOption = mOptions[position]
        mAnswer = mAnswers[position]
        //显示结果页前判断是否订阅用户
        try {
            EventBus.getDefault().postSticky(mOption)
            EventBus.getDefault().postSticky(mAnswer)
            startActivity(Intent(this, PalmprintJudgResultActivity::class.java))
        } catch (e: Exception) {
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

}

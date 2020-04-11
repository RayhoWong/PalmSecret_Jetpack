package com.palmapp.master.module_psy.result;

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.google.gson.reflect.TypeToken
import com.palmapp.master.baselib.BaseMVPActivity
import com.palmapp.master.baselib.EmptyPresenter
import com.palmapp.master.baselib.EmptyView
import com.palmapp.master.baselib.GoCommonEnv
import com.palmapp.master.baselib.bean.CacheBean
import com.palmapp.master.baselib.bean.quiz.QuizListBean
import com.palmapp.master.baselib.bean.quiz.QuizListResponse
import com.palmapp.master.baselib.bean.quiz.Quzi_answer
import com.palmapp.master.baselib.bean.user.UserInfo
import com.palmapp.master.baselib.constants.PreConstants
import com.palmapp.master.baselib.constants.RouterConstants
import com.palmapp.master.baselib.manager.GoPrefManager
import com.palmapp.master.baselib.manager.ThreadExecutorProxy
import com.palmapp.master.baselib.manager.config.ConfigManager
import com.palmapp.master.baselib.manager.config.StarGuideConfig
import com.palmapp.master.baselib.proxy.AdSdkServiceProxy
import com.palmapp.master.baselib.statistics.BaseSeq103OperationStatistic
import com.palmapp.master.baselib.utils.GoGson
import com.palmapp.master.baselib.view.LikeDialog
import com.palmapp.master.module_imageloader.ImageLoaderUtils
import com.palmapp.master.module_psy.R
import kotlinx.android.synthetic.main.psy_activity_quizresult.*

@Route(path = RouterConstants.ACTIVITY_PSY_RESULT)
class QuizResultActivity : BaseMVPActivity<EmptyView, EmptyPresenter>() {
    @JvmField
    @Autowired(name = "answer")
    var response: Quzi_answer? = null

    @JvmField
    @Autowired(name = "id")
    var quiz_id: String? = null

    private lateinit var mIvLike: ImageView
    private lateinit var mIvShare_2: ImageView


    override fun createPresenter(): EmptyPresenter {
        return EmptyPresenter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ARouter.getInstance().inject(this)
        setContentView(R.layout.psy_activity_quizresult)
        AdSdkServiceProxy.showResultAd()
        findViewById<TextView>(R.id.tv_titlebar_title).text = getString(R.string.psy_result)
        findViewById<ImageView>(R.id.iv_titlebar_back).setOnClickListener { finish() }
        response?.let {
            tv_psyquizresult_title.text = it.title
            tv_psyquizresult_content.text = it.description
            if (!TextUtils.isEmpty(it.nature)) {
                var info = GoCommonEnv.userInfo
                if (info == null) {
                    info = UserInfo()
                }
                info.flagMaps.put(quiz_id ?: "", it.nature)
                GoCommonEnv.storeUserInfo(info)
            }
        }

        ThreadExecutorProxy.runOnAsyncThread(Runnable {
            val bean = GoGson.fromJson<CacheBean<QuizListResponse>>(
                GoPrefManager.getDefault().getString(
                    PreConstants.Cache.KEY_CACHE_HOME_PSY,
                    ""
                ), object : TypeToken<CacheBean<QuizListResponse>>() {}.type
            )

            val result = ArrayList<QuizListBean>()
            bean?.data?.quizzes?.let { list ->
                val filter = ArrayList<QuizListBean>(list.filterNot {
                    TextUtils.equals(
                        it.quiz_id,
                        quiz_id
                    )
                })
                result.addAll(filter.shuffled().take(3))
            }
            ThreadExecutorProxy.runOnMainThread(Runnable {
                for (b in result) {
                    val view = LayoutInflater.from(this)
                        .inflate(
                            R.layout.psy_layout_quizresult_recommend,
                            layout_psyquiz_content,
                            false
                        )
                    view.findViewById<TextView>(R.id.tv_psyquizresult_item_title).text = b.title
                    view.findViewById<TextView>(R.id.tv_psyquizresult_item_content).text =
                        b.description
                    ImageLoaderUtils.displayImage(
                        b.overview_img,
                        view.findViewById<ImageView>(R.id.iv_psyquizresult_item),
                        R.mipmap.placeholder_default
                    )
                    view.setOnClickListener {
                        ARouter.getInstance().build(RouterConstants.ACTIVITY_PSY_LIST)
                            .withString("title", b.title)
                            .withString("id", b.quiz_id).navigation()
                        finish()
                    }
                    layout_psyquiz_content.addView(view)
                }
            })
        })

        mIvLike = layout_share.findViewById<ImageView>(R.id.iv_like)
        mIvShare_2 = layout_share.findViewById<ImageView>(R.id.iv_share)
        mIvShare_2.visibility = View.INVISIBLE

        mIvLike.setOnClickListener {
            //帧动画
            val animationDrawable = mIvLike.drawable as AnimationDrawable
            animationDrawable.start()

            ThreadExecutorProxy.runOnMainThread(
                Runnable {
                    BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(BaseSeq103OperationStatistic.thumbs_up_a000)
                    var isLiked = GoPrefManager.getDefault().getBoolean(PreConstants.ScoreGuide.KEY_IS_FIRST_LIKE, false)
                    if (!isLiked){
                        BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
                            BaseSeq103OperationStatistic.rating_f000,
                            "",
                            "5",
                            ""
                        )
                        var dialog = LikeDialog(this, "like")
                        dialog.show()
                        GoPrefManager.getDefault().putBoolean(PreConstants.ScoreGuide.KEY_IS_FIRST_LIKE, true).commit()
                    }

                }, 1000)
        }
    }
}
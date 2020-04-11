package com.palmapp.master.module_psy.quiz;

import android.graphics.drawable.Drawable
import com.palmapp.master.baselib.BaseMVPActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.palmapp.master.baselib.bean.quiz.AnswerResponse
import com.palmapp.master.baselib.bean.quiz.OptionDTO
import com.palmapp.master.baselib.bean.quiz.QuestionDTO
import com.palmapp.master.baselib.bean.quiz.QuizContentBean
import com.palmapp.master.baselib.constants.PreConstants
import com.palmapp.master.baselib.constants.RouterConstants
import com.palmapp.master.baselib.constants.RouterConstants.ACTIVITY_PSY_LIST
import com.palmapp.master.baselib.manager.GoPrefManager
import com.palmapp.master.baselib.view.CommonDialog
import com.palmapp.master.baselib.view.OkDialog
import com.palmapp.master.module_psy.R
import kotlinx.android.synthetic.main.psy_activity_quizlist.*
import org.greenrobot.eventbus.EventBus
import java.lang.Exception

@Route(path = ACTIVITY_PSY_LIST)
class QuizActivity : BaseMVPActivity<QuizView, QuizPresenter>(), QuizView {
    override fun showLoading() {
        layout_psyquiz_previous.visibility = View.GONE
        network.showNetworkLoadingView()
    }

    override fun showNetworkError() {
        layout_psyquiz_previous.visibility = View.GONE
        network.showNetworkErrorView { mPresenter?.loadData(quiz_id ?: "") }
    }

    override fun showServerError() {
        layout_psyquiz_previous.visibility = View.GONE
        network.showServerErrorView { mPresenter?.loadData(quiz_id ?: "") }
    }

    override fun showResult(response: AnswerResponse) {
        ARouter.getInstance().build(RouterConstants.ACTIVITY_PSY_RESULT).withString("id", quiz_id)
            .withSerializable("answer", response.quzi_answer).navigation()
        finish()
    }

    private lateinit var selected: Drawable
    private lateinit var no_select: Drawable
    override fun showContent(quiz: QuizContentBean) {
        network.hideAllView()
        viewpager_psyquiz.adapter = VAdapter(quiz.questions)
        layout_psyquiz_previous.setOnClickListener {
            viewpager_psyquiz.previousPage()
        }
        viewpager_psyquiz.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                layout_psyquiz_previous.visibility = if (position == 0) View.INVISIBLE else View.VISIBLE
            }
        })
    }

    @JvmField
    @Autowired(name = "id")
    var quiz_id: String? = null

    @JvmField
    @Autowired(name = "title")
    var title: String? = null

    override fun createPresenter(): QuizPresenter {
        return QuizPresenter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ARouter.getInstance().inject(this)
        setContentView(R.layout.psy_activity_quizlist)
        findViewById<View>(R.id.iv_titlebar_back).setOnClickListener { onBackPressed() }
        selected = resources.getDrawable(R.drawable.psy_bg_quiz_selected)
        no_select = resources.getDrawable(R.drawable.psy_bg_quiz_no_select)
        findViewById<TextView>(R.id.tv_titlebar_title).text = title ?: ""
        network.bindView(viewpager_psyquiz)
        quiz_id?.let {
            mPresenter?.loadData(it)
            viewpager_psyquiz.pageMargin = resources.getDimensionPixelSize(R.dimen.change_96px)
        }

    }

    private inner class VAdapter(val questions: List<QuestionDTO>) : PagerAdapter() {
        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view == `object`
        }

        override fun getCount(): Int {
            return questions.size
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val view =
                LayoutInflater.from(this@QuizActivity).inflate(R.layout.psy_layout_quizlist_quiz, container, false)
            val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_psyquiz)
            val title = view.findViewById<TextView>(R.id.tv_psyquiz_title)
            val num = view.findViewById<TextView>(R.id.tv_psyquiz_num)

            num.text = "${position + 1}/${questions.size}"
            title.text = questions[position].title

            recyclerView.layoutManager = LinearLayoutManager(container.context)
            recyclerView.adapter = RAdapter(questions[position].question_id, questions[position].options)
            container.addView(view, -1, -1)
            return view
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }

    }

    private inner class RAdapter(val id: String, val list: List<OptionDTO>) : RecyclerView.Adapter<Holder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
            return Holder(LayoutInflater.from(this@QuizActivity).inflate(R.layout.psy_item_quizlist, parent, false))
        }

        override fun getItemCount() = list.size

        override fun onBindViewHolder(holder: Holder, position: Int) {
            val info = list[position]
            try {
                holder.tvTitle.text = info.name
                holder.tvDes.text = info.description
                mPresenter?.let {
                    holder.itemView.isSelected = it.isSelect(id, info.option_id)
                }
                holder.itemView.setOnClickListener {
                    mPresenter?.saveAnswer(id, info.option_id)
                    if (viewpager_psyquiz.isLastPage()) {
                        mPresenter?.uploadAnswer()
                    }
                    viewpager_psyquiz.nextPage()
                    notifyDataSetChanged()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvTitle: TextView = itemView.findViewById(R.id.tv_psyquiz_option_num)
        var tvDes: TextView = itemView.findViewById(R.id.tv_psyquiz_option_des)
    }

    private fun ViewPager.nextPage() {
        currentItem += 1
    }

    private fun ViewPager.previousPage() {
        currentItem -= 1
    }

    private fun ViewPager.isLastPage(): Boolean {
        return (currentItem == adapter?.count?.minus(1))
    }

    override fun onBackPressed() {
        val dialog = CommonDialog(this)
        dialog.show()
        dialog.setOnClickBottomListener(object : CommonDialog.OnClickBottomListener {
            override fun onPositiveClick() {
                finish()
            }

            override fun onNegativeClick() {
            }
        })
    }
}
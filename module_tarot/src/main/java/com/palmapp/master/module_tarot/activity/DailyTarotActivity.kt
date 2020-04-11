package com.palmapp.master.module_tarot.activity

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Dialog
import android.graphics.Point
import android.graphics.Rect
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.palmapp.master.baselib.BaseActivity
import com.palmapp.master.baselib.GoCommonEnv
import com.palmapp.master.baselib.bean.RESPONSE_SUCCESS
import com.palmapp.master.baselib.bean.tarot.*
import com.palmapp.master.baselib.bean.user.getCntCover
import com.palmapp.master.baselib.bean.user.getConstellationById
import com.palmapp.master.baselib.constants.AppConstants
import com.palmapp.master.baselib.constants.RouterConstants
import com.palmapp.master.baselib.manager.DailyTarotManager
import com.palmapp.master.baselib.manager.ShareManager
import com.palmapp.master.baselib.statistics.BaseSeq103OperationStatistic
import com.palmapp.master.baselib.utils.RxTimer
import com.palmapp.master.baselib.utils.StatusBarUtil
import com.palmapp.master.baselib.view.FlipImageView
import com.palmapp.master.module_network.HttpClient
import com.palmapp.master.module_network.NetworkSubscriber
import com.palmapp.master.module_network.NetworkTransformer
import com.palmapp.master.module_tarot.R
import com.palmapp.master.module_tarot.adapter.TarotAdapter
import com.palmapp.master.module_tarot.view.TarotLinearLayoutManager
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.tarot_activity_daily_tarot.*
import java.util.*
import kotlin.math.absoluteValue


/**
 * 每日塔罗牌
 */
@Route(path = RouterConstants.ACTIVITY_TAROT_DAILY)
class DailyTarotActivity : BaseActivity(), TarotAdapter.OnItemClickListener {

    private lateinit var mAdapter: TarotAdapter
    private val mData = arrayListOf<DailyTarotBean>()
    private val mList = GoCommonEnv.tarotList.shuffled()
    private var mCompositeDisposable: CompositeDisposable = CompositeDisposable()

    private lateinit var mLayoutManager: TarotLinearLayoutManager

    private lateinit var rlCover: LinearLayout
    private lateinit var ivReplace1: ImageView
    private lateinit var ivReplace2: ImageView
    private lateinit var ivReplace3: ImageView

    private var card_love_1: DailyTarotBean? = null //抽的第一张牌_爱情
    private var card_fortune_2: DailyTarotBean? = null //抽的第二张牌_运气
    private var card_career_3: DailyTarotBean? = null //抽的第三张牌_职业

    private var selectedCount = 1 //选中卡牌数量最大为3

    private var isShowedCardLove = false //是否显示过爱情卡牌
    private var isShowedCardFortune = false //是否显示过运势卡牌
    private var isShowedCardCareer = false //是否显示过职业卡牌
    private var clickType: Int = 0 //点击某张牌的得到类型


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tarot_activity_daily_tarot)
        StatusBarUtil.setRootViewFitsSystemWindows(this, true)

        initView()
        initCardClick()
        getData()
    }

    private fun initView() {

        rlCover = findViewById(R.id.rl_cover)

        ivReplace1 = findViewById(R.id.iv_replace1)
        ivReplace2 = findViewById(R.id.iv_replace2)
        ivReplace3 = findViewById(R.id.iv_replace3)

        tv_titlebar_title.text = getString(R.string.tarot_daily_title)
        iv_titlebar_back.setOnClickListener {
            finish()
        }
        iv_titlebar_share.isEnabled = false
        iv_titlebar_share.setOnClickListener {
            shareResult()
        }

        iv_close.setOnClickListener {
            when (selectedCount) {
                1 -> recoverAnimation(card_love_1!!)
                2 -> recoverAnimation(card_fortune_2!!)
                3 -> recoverAnimation(card_career_3!!)
            }
        }

        //rcv
        mLayoutManager = TarotLinearLayoutManager(this)
        mLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        rcv.layoutManager = mLayoutManager
        //设置item之间重叠
        rcv.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                super.getItemOffsets(outRect, view, parent, state)
                if (parent.getChildLayoutPosition(view) != (mList.size - 1)) {
                    //塔罗牌的右边重叠
                    outRect.right = -resources.getDimension(R.dimen.change_149px).toInt()
                }
            }
        })
    }


    private fun showDialog(bean: DailyTarotBean) {
        val dialog = Dialog(this)
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(false)
        val window = dialog.window
        //dialog取消标题
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setContentView(R.layout.tarot_layout_tarot)
        window.setBackgroundDrawableResource(R.color.color_d9000000)
        window.findViewById<View>(R.id.iv_today_close).setOnClickListener { dialog.dismiss() }
        window.findViewById<ImageView>(R.id.iv_today_res).setImageResource(bean.cover)
        val lp = window.getAttributes()
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.MATCH_PARENT
        window.findViewById<TextView>(R.id.tv_today_quote).text = bean.name
        window.findViewById<TextView>(R.id.tv_today_des).text = bean.result
        dialog.show()
    }

    /**
     * 点击选中卡牌显示显示详情
     */
    private fun initCardClick() {
        iv_tarot_left.setOnClickListener {
            if (card_fortune_2?.isFlip == false) {
                return@setOnClickListener
            }
            //运势2
            showDialog(card_fortune_2!!)
        }

        iv_tarot_miaddle.setOnClickListener {
            if (card_love_1?.isFlip == false) {
                return@setOnClickListener
            }
            //爱情1
            showDialog(card_love_1!!)
        }

        iv_tarot_right.setOnClickListener {
            if (card_career_3?.isFlip == false) {
                return@setOnClickListener
            }
            //职业3
            showDialog(card_career_3!!)
        }
    }


    /**
     * 获取塔罗牌列表
     */
    private fun getData() {
        DailyTarotManager.loadData(object : DailyTarotManager.OnShowTarotListener {
            override fun showTarot(list: List<DailyTarotBean>) {
                list.forEach {
                    when (it.type) {
                        AppConstants.TAROT_CARD_TYPE_LOVE -> {
                            if (it.isFlip) {
                                iv_tarot_miaddle.setImageResource(it.cover)
                            }
                            card_love_1 = it
                        }
                        AppConstants.TAROT_CARD_TYPE_FORTUNE -> {
                            if (it.isFlip) {
                                iv_tarot_left.setImageResource(it.cover)
                            }
                            card_fortune_2 = it
                        }
                        AppConstants.TAROT_CARD_TYPE_CAREER -> {
                            if (it.isFlip) {
                                iv_tarot_right.setImageResource(it.cover)
                            }
                            card_career_3 = it
                        }
                    }
                }
                mData.clear()
                mData.addAll(list)
                mAdapter = TarotAdapter(this@DailyTarotActivity, mList, list, this@DailyTarotActivity)
                rcv.adapter = mAdapter
                showResult()
            }
        })
    }


    override fun onClick(itemView: View, position: Int) {
        selectedCount = if (card_love_1?.isFlip == false) {
            1
        } else if (card_fortune_2?.isFlip == false) {
            2
        } else if (card_career_3?.isFlip == false) {
            3
        } else {
            4
        }
        if (selectedCount == 4)
            return
        val flipImageView = itemView.findViewById<FlipImageView>(R.id.iv_card)
        val temp: DailyTarotBean = mData[selectedCount - 1]
        flipImageView.setFlippedDrawable(resources.getDrawable(temp.cover))
        flipImageView.onClick(flipImageView)
        temp.isFlip = true
        when (selectedCount) {
            1 -> {//爱情
                temp.type = AppConstants.TAROT_CARD_TYPE_LOVE
                card_love_1 = temp
                getCardContent(temp)
                clickType = 1
                isShowedCardLove = true
                startAnimation(itemView, selectedCount - 1)
            }
            2 -> {//运势
                temp.type = AppConstants.TAROT_CARD_TYPE_FORTUNE
                card_fortune_2 = temp
                getCardContent(temp)
                clickType = 2
                isShowedCardFortune = true
                startAnimation(itemView, selectedCount - 1)
            }
            3 -> {//职业
                temp.type = AppConstants.TAROT_CARD_TYPE_CAREER
                card_career_3 = temp
                getCardContent(temp)
                clickType = 3
                isShowedCardCareer = true
                startAnimation(itemView, selectedCount - 1)
            }
        }
    }


    /**
     * 获取卡牌的解析
     */
    private fun getCardContent(item: DailyTarotBean) {
        var request = DailyTarotAnswerRequest()
        request.card_keys = listOf(item.card_key) as MutableList<String>
        HttpClient
            .getTarotRequest()
            .getDailTarotAnswer(request)
            .compose(NetworkTransformer.toMainSchedulers<DailyTarotAnswerResponse>())
            .subscribe(object : NetworkSubscriber<DailyTarotAnswerResponse>() {

                override fun onDispose(d: Disposable) {
                    //添加该订阅事件的Disposable
                    mCompositeDisposable.add(d)
                }

                override fun onNext(t: DailyTarotAnswerResponse) {
                    if (t.status_result?.status_code.equals(RESPONSE_SUCCESS) && t.daily_tarot_answer_map != null) {
                        if (t.daily_tarot_answer_map!!.isNotEmpty()) {
                            val temp = t.daily_tarot_answer_map?.get(item.card_key)
                            if (temp != null) {
                                item.result = when (item.type) {
                                    AppConstants.TAROT_CARD_TYPE_LOVE -> if (Random().nextBoolean()) temp.love_reversed else temp.love_upright
                                    AppConstants.TAROT_CARD_TYPE_FORTUNE -> if (Random().nextBoolean()) temp.fortune_reversed else temp.fortune_upright
                                    AppConstants.TAROT_CARD_TYPE_CAREER -> if (Random().nextBoolean()) temp.career_reversed else temp.career_upright
                                    else -> ""
                                }
                            }
                            tv_card_name.text = item.name
                            tv_card_content.text = item.result
                            DailyTarotManager.storeCacheTarot()
                        }
                    }
                }
            })

    }


    /**
     * 开始动画
     */
    private fun startAnimation(itemView: View, position: Int) {
        //禁止滑动和点击
        rcv.setIntercept(true)
        mLayoutManager.setHorizontalScroll(false)

        when (mData[position].type) {
            AppConstants.TAROT_CARD_TYPE_FORTUNE -> { //运势2
                ll_name.visibility = View.INVISIBLE
                tv_card_content.visibility = View.GONE
                ivReplace1.visibility = View.GONE
            }
            AppConstants.TAROT_CARD_TYPE_CAREER -> { //职业3
                ll_name.visibility = View.INVISIBLE
                tv_card_content.visibility = View.GONE
                ivReplace2.visibility = View.GONE
            }
        }

        //获取屏幕的宽高
        var defaultDisplay = windowManager.defaultDisplay
        var point = Point()
        defaultDisplay.getSize(point)
        var targetX = (point.x / 2) //屏幕中心点的x坐标
        var targetY = (point.y / 2) //屏幕中心点的y坐标

        var translationX = ObjectAnimator.ofFloat(itemView, "translationX", 0f, 0f)
//         卡牌宽度一半 = resources.getDimension(R.dimen.change_103px)
//         卡牌中心点x坐标=itemView.left+resources.getDimension(R.dimen.change_103px)
//         x轴偏移量=卡牌中心点x坐标 - 屏幕中心点的x坐标
        var dx = itemView.left + resources.getDimension(R.dimen.change_103px) - targetX.toFloat()
        var dy = targetY + resources.getDimension(R.dimen.change_226px)
        when {
            dx > 0 -> {//大于0表示该张牌在屏幕中心点的右边
                translationX = ObjectAnimator.ofFloat(itemView, "translationX", 0f, -dx.absoluteValue)
            }
            dx < 0 -> {//小于0表示该张牌在屏幕中心点的左边
                translationX = ObjectAnimator.ofFloat(itemView, "translationX", 0f, dx.absoluteValue)
            }
        }
        //出牌偏移
        var translationY_1 =
            ObjectAnimator.ofFloat(itemView, "translationY", 0f, -resources.getDimension(R.dimen.change_320px))
        //中心位置偏移
        var translationY_2 =
            ObjectAnimator.ofFloat(itemView, "translationY", -resources.getDimension(R.dimen.change_320px), -dy)
        //x,y轴缩放 x轴倍数=648/206  y轴倍数=1072/312
        var scaleX = ObjectAnimator.ofFloat(itemView, "scaleX", 1f, 3.14f)
        var scaleY = ObjectAnimator.ofFloat(itemView, "scaleY", 1f, 3.44f)
        //卡牌详情布局透明度渐变
        var alpha = ObjectAnimator.ofFloat(rlCover, "alpha", 1f, 0.999f)

        var animatorSet = AnimatorSet()
        animatorSet
            .play(translationY_1)
            .with(alpha)
            .with(translationY_2)
            .with(translationX)
            .with(scaleX)
            .with(scaleY)
        animatorSet.duration = 500
        animatorSet.start()
        RxTimer.timer(150, RxTimer.RxAction() {
            //延迟150ms 显示卡牌详情布局
            rlCover.visibility = View.VISIBLE
        })

        translationX.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {

            }

            override fun onAnimationEnd(animation: Animator?) {
                showCardDetail(itemView, position)
            }

            override fun onAnimationCancel(animation: Animator?) {

            }

            override fun onAnimationStart(animation: Animator?) {
            }

        })

    }


    /**
     * 复原动画
     */
    private fun recoverAnimation(item: DailyTarotBean) {
        var animatorSet = AnimatorSet()
        when (item.type) {
            AppConstants.TAROT_CARD_TYPE_LOVE -> { //爱情1
                isShowedCardLove = false
                //偏移量305 通过目测调试得出..
                var translationY = ObjectAnimator.ofFloat(ivReplace1, "translationY", 0f, -305f)
                var scaleX = ObjectAnimator.ofFloat(ivReplace1, "scaleX", 1f, 0.394f)
                var scaleY = ObjectAnimator.ofFloat(ivReplace1, "scaleY", 1f, 0.394f)
                var alpha = ObjectAnimator.ofFloat(rlCover, "alpha", 0.999f, 0f)
                animatorSet
                    .play(translationY)
                    .with(alpha)
                    .with(scaleX)
                    .with(scaleY)
            }

            AppConstants.TAROT_CARD_TYPE_FORTUNE -> { //运势2
                isShowedCardFortune = false
                //偏移量 通过目测调试得出..
                var dx = ivReplace2.left + resources.getDimension(R.dimen.change_40px)
                var dy = rl_tarot.top + resources.getDimension(R.dimen.change_55px)
                var translationX = ObjectAnimator.ofFloat(ivReplace2, "translationX", 0f, -dx.absoluteValue)
                var translationY = ObjectAnimator.ofFloat(ivReplace2, "translationY", 0f, dy)
                var scaleX = ObjectAnimator.ofFloat(ivReplace2, "scaleX", 1f, 0.394f)
                var scaleY = ObjectAnimator.ofFloat(ivReplace2, "scaleY", 1f, 0.394f)
                var alpha = ObjectAnimator.ofFloat(rlCover, "alpha", 0.999f, 0.5f)
                animatorSet
                    .play(translationY)
                    .with(translationX)
                    .with(alpha)
                    .with(scaleX)
                    .with(scaleY)
            }

            AppConstants.TAROT_CARD_TYPE_CAREER -> { //职业3
                isShowedCardCareer = false
                //偏移量 通过目测调试得出..
                var dx = ivReplace3.left + resources.getDimension(R.dimen.change_40px)
                var dy = rl_tarot.top + resources.getDimension(R.dimen.change_55px)
                var translationX = ObjectAnimator.ofFloat(ivReplace3, "translationX", 0f, dx.absoluteValue)
                var translationY = ObjectAnimator.ofFloat(ivReplace3, "translationY", 0f, dy)
                var scaleX = ObjectAnimator.ofFloat(ivReplace3, "scaleX", 1f, 0.394f)
                var scaleY = ObjectAnimator.ofFloat(ivReplace3, "scaleY", 1f, 0.394f)
                var alpha = ObjectAnimator.ofFloat(rlCover, "alpha", 0.999f, 0.5f)
                animatorSet
                    .play(translationY)
                    .with(translationX)
                    .with(alpha)
                    .with(scaleX)
                    .with(scaleY)
            }
        }
        animatorSet.duration = 150
        animatorSet.start()
        animatorSet.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {}

            override fun onAnimationEnd(animation: Animator?) {
                rlCover.visibility = View.GONE
                iv_titlebar_share.isClickable = true
                iv_titlebar_back.isClickable = true
                for (tarot in GoCommonEnv.tarotList) {
                    if (tarot.card_key == item.card_key) {
                        when (item.type) {
                            AppConstants.TAROT_CARD_TYPE_LOVE -> { //爱情1
                                iv_tarot_miaddle.setImageResource(tarot.cover)
                            }
                            AppConstants.TAROT_CARD_TYPE_FORTUNE -> { //运势2
                                iv_tarot_left.setImageResource(tarot.cover)
                            }
                            AppConstants.TAROT_CARD_TYPE_CAREER -> { //职业3
                                iv_tarot_right.setImageResource(tarot.cover)
                            }
                        }
                    }
                }

                rcv.setIntercept(false)
                mLayoutManager.setHorizontalScroll(true)
                //选完三张牌监听
                if (card_love_1?.isFlip == true && card_fortune_2?.isFlip == true && card_career_3?.isFlip == true) {
                    //全部牌都翻了
                    rcv.setIntercept(true)
                    mLayoutManager.setHorizontalScroll(false)

                    showResult()
                }
            }

            override fun onAnimationCancel(animation: Animator?) {}

            override fun onAnimationStart(animation: Animator?) {}

        })

    }

    /**
     * 显示卡牌详情
     */
    private fun showCardDetail(itemView: View, position: Int) {
        //显示卡牌详情时 禁止点击titlebar按钮
        iv_titlebar_share.isClickable = false
        iv_titlebar_back.isClickable = false
        //隐藏卡牌itemView
        itemView.visibility = View.GONE
        for (item in GoCommonEnv.tarotList) {
            if (item.card_key == mData[position].card_key) {
                when (mData[position].type) {
                    AppConstants.TAROT_CARD_TYPE_LOVE -> { //爱情1
                        //设置卡牌代替的图片1
                        ivReplace1.visibility = View.VISIBLE
                        ivReplace1.setImageResource(item.cover)
                    }
                    AppConstants.TAROT_CARD_TYPE_FORTUNE -> { //运势2
                        //设置卡牌代替的图片2
                        ivReplace2.visibility = View.VISIBLE
                        ivReplace2.setImageResource(item.cover)
                    }
                    AppConstants.TAROT_CARD_TYPE_CAREER -> { //职业3
                        //设置卡牌代替的图片3
                        ivReplace3.visibility = View.VISIBLE
                        ivReplace3.setImageResource(item.cover)
                    }
                }
            }
        }
        RxTimer.timer(500, RxTimer.RxAction {
            iv_close.visibility = View.VISIBLE
            ll_name.visibility = View.VISIBLE
            tv_card_content.visibility = View.VISIBLE
        })
    }


    /**
     * 显示已抽中爱情卡牌的详情
     */
    private fun showCardLoveDetail() {
        if (card_love_1 != null) {
            rlCover.visibility = View.VISIBLE
            ivReplace2.visibility = View.GONE
            ivReplace3.visibility = View.GONE
            ivReplace1.visibility = View.VISIBLE

            var animatorSet = AnimatorSet()
            //偏移量 通过目测调试得出..
            var translationY =
                ObjectAnimator.ofFloat(ivReplace1, "translationY", 0f, resources.getDimension(R.dimen.change_1px))
            var scaleX = ObjectAnimator.ofFloat(ivReplace1, "scaleX", 0.394f, 1f)
            var scaleY = ObjectAnimator.ofFloat(ivReplace1, "scaleY", 0.394f, 1f)
            var alpha = ObjectAnimator.ofFloat(rlCover, "alpha", 0f, 0.999f)
            animatorSet
                .play(translationY)
                .with(alpha)
                .with(scaleX)
                .with(scaleY)
            animatorSet.duration = 150
            animatorSet.start()
            animatorSet.addListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(animation: Animator?) {}

                override fun onAnimationEnd(animation: Animator?) {
                    //显示卡牌详情时 禁止点击titlebar按钮
                    iv_titlebar_share.isClickable = false
                    iv_titlebar_back.isClickable = false

                    tv_card_name.text = card_love_1?.name
                    tv_card_content.text = card_love_1?.result
                }

                override fun onAnimationCancel(animation: Animator?) {}

                override fun onAnimationStart(animation: Animator?) {}

            })
        }
    }


    /**
     * 显示已抽中运势卡牌的详情
     */
    private fun showCardFortuneDetail() {
        if (card_fortune_2 != null) {
            rlCover.visibility = View.VISIBLE
            ivReplace1.visibility = View.GONE
            ivReplace3.visibility = View.GONE
            ivReplace2.visibility = View.VISIBLE

            var animatorSet = AnimatorSet()
            //偏移量 通过目测调试得出..
            var dx = resources.getDimension(R.dimen.change_1px)
            var dy = resources.getDimension(R.dimen.change_1px)
            var translationX = ObjectAnimator.ofFloat(ivReplace2, "translationX", 0f, dx.absoluteValue)
            var translationY = ObjectAnimator.ofFloat(ivReplace2, "translationY", 0f, -dy)

            var scaleX = ObjectAnimator.ofFloat(ivReplace2, "scaleX", 0.394f, 1f)
            var scaleY = ObjectAnimator.ofFloat(ivReplace2, "scaleY", 0.394f, 1f)
            var alpha = ObjectAnimator.ofFloat(rlCover, "alpha", 0f, 0.999f)
            animatorSet
                .play(translationY)
                .with(translationX)
                .with(alpha)
                .with(scaleX)
                .with(scaleY)
            animatorSet.duration = 150
            animatorSet.start()
            animatorSet.addListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(animation: Animator?) {}

                override fun onAnimationEnd(animation: Animator?) {
                    //显示卡牌详情时 禁止点击titlebar按钮
                    iv_titlebar_share.isClickable = false
                    iv_titlebar_back.isClickable = false

                    tv_card_name.text = card_fortune_2?.name
                    tv_card_content.text = card_fortune_2?.result
                }

                override fun onAnimationCancel(animation: Animator?) {}

                override fun onAnimationStart(animation: Animator?) {}

            })
        }
    }


    /**
     * 显示已抽中职业卡牌的详情
     */
    private fun showCardCareerDetail() {
        if (card_career_3 != null) {
            rlCover.visibility = View.VISIBLE
            ivReplace1.visibility = View.GONE
            ivReplace2.visibility = View.GONE
            ivReplace3.visibility = View.VISIBLE

            var animatorSet = AnimatorSet()
            //偏移量 通过目测调试得出..
            var dx = resources.getDimension(R.dimen.change_1px) //1
            var dy = resources.getDimension(R.dimen.change_1px)  //20
            var translationX = ObjectAnimator.ofFloat(ivReplace3, "translationX", 0f, -dx.absoluteValue)
            var translationY = ObjectAnimator.ofFloat(ivReplace3, "translationY", 0f, -dy)

            var scaleX = ObjectAnimator.ofFloat(ivReplace3, "scaleX", 0.394f, 1f)
            var scaleY = ObjectAnimator.ofFloat(ivReplace3, "scaleY", 0.394f, 1f)
            var alpha = ObjectAnimator.ofFloat(rlCover, "alpha", 0f, 0.999f)
            animatorSet
                .play(translationY)
                .with(translationX)
                .with(alpha)
                .with(scaleX)
                .with(scaleY)
            animatorSet.duration = 150
            animatorSet.start()
            animatorSet.addListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(animation: Animator?) {}

                override fun onAnimationEnd(animation: Animator?) {
                    //显示卡牌详情时 禁止点击titlebar按钮
                    iv_titlebar_share.isClickable = false
                    iv_titlebar_back.isClickable = false

                    tv_card_name.text = card_career_3?.name
                    tv_card_content.text = card_career_3?.result
                }

                override fun onAnimationCancel(animation: Animator?) {}

                override fun onAnimationStart(animation: Animator?) {}

            })
        }
    }


    /**
     * 抽完三张牌后 显示解析结果
     */
    private fun showResult() {
        if (card_love_1?.isFlip == false || card_fortune_2?.isFlip == false || card_career_3?.isFlip == false)
            return
        ll_tarot_cards.visibility = View.GONE
        ll_tarot_result.visibility = View.VISIBLE
        iv_titlebar_share.isEnabled = true
        tv_tarot_result_title1.text = getString(R.string.tarot_love)
        tv_tarot_result_title2.text = getString(R.string.tarot_fortune)
        tv_tarot_result_title3.text = getString(R.string.tarot_business)

        tv_tarot_result_content1.text = card_love_1?.result
        tv_tarot_result_content2.text = card_fortune_2?.result
        tv_tarot_result_content3.text = card_career_3?.result
    }


    private fun shareResult() {
        if (card_love_1 != null && card_fortune_2 != null && card_career_3 != null) {
            BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
                BaseSeq103OperationStatistic.share_a000,
                "",
                "11",
                ""
            )

            var result = LayoutInflater.from(this).inflate(R.layout.tarot_layout_share, null, false)
            if (TextUtils.isEmpty(GoCommonEnv.userInfo?.name ?: "")) {
                result.findViewById<View>(R.id.layout_header).visibility = View.GONE
            }
            result.findViewById<ImageView>(R.id.iv_tarot_miaddle).setImageDrawable(iv_tarot_miaddle.drawable)
            result.findViewById<ImageView>(R.id.iv_tarot_left).setImageDrawable(iv_tarot_left.drawable)
            result.findViewById<ImageView>(R.id.iv_tarot_right).setImageDrawable(iv_tarot_right.drawable)
            result.findViewById<ImageView>(R.id.iv_icon)
                .setImageResource(getCntCover(GoCommonEnv.userInfo?.constellation ?: 1))
            result.findViewById<TextView>(R.id.tv_name).text = GoCommonEnv.userInfo?.name ?: "Tom Sawyer"
            result.findViewById<TextView>(R.id.tv_cnt).text =
                getConstellationById(GoCommonEnv.userInfo?.constellation ?: 1)
            ShareManager.share(result, getString(R.string.app_app_name))
        }
    }


    /**
     * 监听返回键动作 是否开启复原动画
     */
    private fun backListener() {
        if (clickType != 0) {
            //点击牌的种类
            when (clickType) {
                1 -> {//爱情1
                    if (isShowedCardLove) {
                        recoverAnimation(card_love_1!!)
                    } else {
                        finish()
                    }
                }
                2 -> {//运势2
                    if (isShowedCardFortune) {
                        recoverAnimation(card_fortune_2!!)
                    } else {
                        finish()
                    }
                }
                3 -> {//职业3
                    if (isShowedCardCareer) {
                        recoverAnimation(card_career_3!!)
                    } else {
                        finish()
                    }
                }
            }
        } else {
            //未点击选中的牌 直接退出
            finish()
        }
    }


    override fun onBackPressed() {
        backListener()
    }


    override fun onDestroy() {
        super.onDestroy()
        mCompositeDisposable.clear()
    }

}

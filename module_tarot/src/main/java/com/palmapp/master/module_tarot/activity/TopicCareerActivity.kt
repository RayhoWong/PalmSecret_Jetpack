package com.palmapp.master.module_tarot.activity

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Dialog
import android.content.Intent
import android.graphics.Point
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.palmapp.master.baselib.BaseActivity
import com.palmapp.master.baselib.GoCommonEnv
import com.palmapp.master.baselib.bean.tarot.*
import com.palmapp.master.baselib.constants.AppConstants
import com.palmapp.master.baselib.constants.RouterConstants
import com.palmapp.master.baselib.event.SubscribeUpdateEvent
import com.palmapp.master.baselib.manager.DailyTarotTopicCareerManager
import com.palmapp.master.baselib.proxy.BillingServiceProxy
import com.palmapp.master.baselib.utils.NetworkUtil
import com.palmapp.master.baselib.utils.RxTimer
import com.palmapp.master.baselib.utils.StatusBarUtil
import com.palmapp.master.baselib.view.CommonDialog
import com.palmapp.master.baselib.view.FlipImageView
import com.palmapp.master.baselib.view.PalmBlurView
import com.palmapp.master.module_network.HttpClient
import com.palmapp.master.module_network.NetworkSubscriber
import com.palmapp.master.module_network.NetworkTransformer
import com.palmapp.master.module_tarot.R
import com.palmapp.master.module_tarot.adapter.TarotAdapter
import com.palmapp.master.module_tarot.view.TarotLinearLayoutManager
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.tarot_activity_topic_career.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import kotlin.math.absoluteValue

/**
 * 塔罗话题 职业
 */
@Route(path = RouterConstants.ACTIVITY_TAROT_TOPIC_CAREER)
class TopicCareerActivity : BaseActivity(), TarotAdapter.OnItemClickListener, CommonDialog.OnClickBottomListener {

    private val mList = GoCommonEnv.tarotList.shuffled()//随机返回存储在本地的塔罗牌列表
    private lateinit var mAdapter: TarotAdapter
    private var mCompositeDisposable: CompositeDisposable = CompositeDisposable()

    private lateinit var mLayoutManager: TarotLinearLayoutManager
    private var tarotBlurView: PalmBlurView? = null //模糊页view

    private var card_1: TopicAngleAnswer? = null
    private var card_2: TopicAngleAnswer? = null
    private var card_3: TopicAngleAnswer? = null
    private var card_4: TopicAngleAnswer? = null

    private var mCacheTarotDefault = arrayListOf<TopicAngleAnswer>()//第一次请求 获取的默认5张话题塔罗牌
    private var mCacheTarotList = arrayListOf<TopicAngleAnswer>()//缓存的卡牌

    private var mResult = "" //结果语
    private var selectedCount = 0 //选中卡牌数量最大为4

    private var isShowedCard_1 = false //是否已经显示卡牌详情
    private var isShowedCard_2 = false
    private var isShowedCard_3 = false
    private var isShowedCard_4 = false
    private var clickType: Int = 0 //当前点击卡牌的类型


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tarot_activity_topic_career)
        StatusBarUtil.setRootViewFitsSystemWindows(this, true)
        EventBus.getDefault().register(this)


        initView()
        initCardClick()
        getCacheTarot()
    }


    private fun initView() {

        tarotBlurView = findViewById(R.id.tarot_blurview)
        //订阅页塔罗牌统计 entrance=5
        tarotBlurView?.entrance = "5"

        tv_titlebar_title.text = getString(R.string.career)

        iv_titlebar_back.setOnClickListener {
            exitTip()
        }

        iv_titlebar_share.setOnClickListener {
            //            shareResult()
        }

        iv_close.setOnClickListener {
            when (selectedCount) {
                1 -> recoverAnimation(card_1!!)
                2 -> recoverAnimation(card_2!!)
                3 -> recoverAnimation(card_3!!)
                4 -> recoverAnimation(card_4!!)
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
        mAdapter = TarotAdapter(this, mList, this)
        rcv.adapter = mAdapter
    }


    /**
     * 点击选中卡牌显示显示详情
     */
    private fun initCardClick() {
        iv_tarot_left.setOnClickListener {
            //点击卡牌位置如果卡牌未被选中 不显示详情
            if (!card_2!!.isFlip) {
                return@setOnClickListener
            }
            showCardDialog(card_2!!)
        }

        iv_tarot_top.setOnClickListener {
            if (!card_4!!.isFlip) {
                return@setOnClickListener
            }
            showCardDialog(card_4!!)
        }

        iv_tarot_miaddle.setOnClickListener {
            if (!card_1!!.isFlip) {
                return@setOnClickListener
            }
            showCardDialog(card_1!!)
        }

        iv_tarot_right.setOnClickListener {
            if (!card_3!!.isFlip) {
                return@setOnClickListener
            }
            showCardDialog(card_3!!)
        }
    }


    override fun onClick(itemview: View, position: Int) {
        //订阅判断
//        if (!BillingServiceProxy.isVip()) {
//            //非订阅用户 显示订阅引导页
//            ARouter.getInstance().build(RouterConstants.ACTIVITY_PAY).withString("entrance", "5")
//                .navigation(this, AppConstants.PAY_REQUEST_CODE)
//            return
//        }

        if (card_1 == null) return
        selectedCount++
        //最多点击4次牌
        if (selectedCount == 5) return

        val flipImageView = itemview.findViewById<FlipImageView>(R.id.iv_card)
        when (selectedCount) {
            1 -> {
                if (card_1!!.isFlip) {
                    //卡牌已被选中 直接显示 不开启动画
                    return
                }
                clickType = 1 //当前点击卡牌的类型是1
                isShowedCard_1 = true //卡牌1已经显示
                card_1?.isFlip = true //卡牌1已经被选中
                mCacheTarotList.add(card_1!!) //添加到缓存的话题塔罗牌集合
                mList.forEach {
                    //遍历本地的塔罗牌列表 根据key设置卡牌的图片和属性
                    if (card_1?.card_key == it.card_key) {
                        flipImageView.setFlippedDrawable(resources.getDrawable(it.cover))
                        flipImageView.onClick(flipImageView) //开启点击事件
                        tv_card_name.text = it.name
                        tv_keywords.text = it.keyword
                    }
                }
                tv_topic.text = card_1?.topic
                tv_card_content.text = card_1?.explanation
                startAnimation(itemview, card_1!!)
            }

            2 -> {
                if (card_2!!.isFlip) {
                    return
                }
                clickType = 2
                isShowedCard_2 = true
                card_2?.isFlip = true
                mCacheTarotList.add(card_2!!)
                mList.forEach {
                    if (card_2?.card_key == it.card_key) {
                        flipImageView.setFlippedDrawable(resources.getDrawable(it.cover))
                        flipImageView.onClick(flipImageView)
                        tv_card_name.text = it.name
                        tv_keywords.text = it.keyword
                    }
                }
                tv_topic.text = card_2?.topic
                tv_card_content.text = card_2?.explanation
                startAnimation(itemview, card_2!!)
            }

            3 -> {
                if (card_3!!.isFlip) {
                    return
                }
                clickType = 3
                isShowedCard_3 = true
                card_3?.isFlip = true
                mCacheTarotList.add(card_3!!)
                mList.forEach {
                    if (card_3?.card_key == it.card_key) {
                        flipImageView.setFlippedDrawable(resources.getDrawable(it.cover))
                        flipImageView.onClick(flipImageView)
                        tv_card_name.text = it.name
                        tv_keywords.text = it.keyword
                    }
                }
                tv_topic.text = card_3?.topic
                tv_card_content.text = card_3?.explanation
                startAnimation(itemview, card_3!!)
            }

            4 -> {
                if (card_4!!.isFlip) {
                    return
                }
                clickType = 4
                isShowedCard_4 = true
                card_4?.isFlip = true
                mCacheTarotList.add(card_4!!)
                mList.forEach {
                    if (card_4?.card_key == it.card_key) {
                        flipImageView.rotation
                        flipImageView.setFlippedDrawable(resources.getDrawable(it.cover))
                        flipImageView.onClick(flipImageView)
                        tv_card_name.text = it.name
                        tv_keywords.text = it.keyword
                    }
                }
                tv_topic.text = card_4?.topic
                tv_card_content.text = card_4?.explanation
                startAnimation(itemview, card_4!!)
            }

        }
    }


    //用户未订阅 订阅页关闭后 显示订阅引导页
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppConstants.PAY_REQUEST_CODE && resultCode == AppConstants.PAY_RESULT_CODE) {
            tarotBlurView?.visibility = View.VISIBLE
        }
    }



    //订阅成功 隐藏订阅引导页
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSubscribeUpdateEvent(event: SubscribeUpdateEvent) {
        if (event.isVip) {//订阅成功
            tarotBlurView?.visibility = View.GONE
        } else { //订阅失败
            tarotBlurView?.visibility = View.VISIBLE
        }
    }


    /**
     * 获取缓存的话题塔罗牌
     */
    private fun getCacheTarot() {
        DailyTarotTopicCareerManager.loadData(object : DailyTarotTopicCareerManager.OnShowTarotTopicListener {
            override fun showTarot(default: List<TopicAngleAnswer>, list: List<TopicAngleAnswer>, result: String) {
                if (list.isNotEmpty()) {
                    list.forEach {
                        when (it.angle_number) {
                            1 -> {
                                card_1 = it
                                mList.forEach { bean ->
                                    if (card_1?.card_key == bean.card_key) {
                                        if (card_1?.card_place_type == AppConstants.TAROT_CARD_PLACE_TYPE_UPRIGHT) {
                                            iv_tarot_miaddle.setImageResource(bean.cover)
                                        } else if (card_1?.card_place_type == AppConstants.TAROT_CARD_PLACE_TYPE_REVERSED) {
                                            iv_tarot_miaddle.setImageResource(bean.cover)
                                            //逆位卡牌 旋转180度(view.setRotation())
                                            iv_tarot_miaddle.rotation = 180f
                                        }
                                    }
                                }
                                //选中卡牌的数量
                                selectedCount = 1
                            }

                            2 -> {
                                card_2 = it
                                mList.forEach { bean ->
                                    if (card_2?.card_key == bean.card_key) {
                                        if (card_2?.card_place_type == AppConstants.TAROT_CARD_PLACE_TYPE_UPRIGHT) {
                                            iv_tarot_left.setImageResource(bean.cover)
                                        } else if (card_2?.card_place_type == AppConstants.TAROT_CARD_PLACE_TYPE_REVERSED) {
                                            iv_tarot_left.setImageResource(bean.cover)
                                            iv_tarot_left.rotation = 180f
                                        }
                                    }
                                }
                                selectedCount = 2
                            }

                            3 -> {
                                card_3 = it
                                mList.forEach { bean ->
                                    if (card_3?.card_key == bean.card_key) {
                                        if (card_3?.card_place_type == AppConstants.TAROT_CARD_PLACE_TYPE_UPRIGHT) {
                                            iv_tarot_right.setImageResource(bean.cover)
                                        } else if (card_3?.card_place_type == AppConstants.TAROT_CARD_PLACE_TYPE_REVERSED) {
                                            iv_tarot_right.setImageResource(bean.cover)
                                            iv_tarot_right.rotation = 180f
                                        }
                                    }
                                }
                                selectedCount = 3
                            }

                            4 -> {
                                card_4 = it
                                mList.forEach { bean ->
                                    if (card_4?.card_key == bean.card_key) {
                                        if (card_4?.card_place_type == AppConstants.TAROT_CARD_PLACE_TYPE_UPRIGHT) {
                                            iv_tarot_top.setImageResource(bean.cover)
                                        } else if (card_4?.card_place_type == AppConstants.TAROT_CARD_PLACE_TYPE_REVERSED) {
                                            iv_tarot_top.setImageResource(bean.cover)
                                            iv_tarot_top.rotation = 180f
                                        }
                                    }
                                }
                                selectedCount = 4
                            }

                        }
                    }
                }
                mCacheTarotDefault = default as ArrayList<TopicAngleAnswer>
                mCacheTarotList = list as ArrayList<TopicAngleAnswer>
                mResult = result //缓存的结果语
                tv_tarot_result.text = mResult

                /*
                已经第一次请求 获取的4张塔罗牌
                以后点击卡牌列表 都是这4张塔罗牌
                * */
                if (mCacheTarotDefault.isNotEmpty()) {
                    for (item in mCacheTarotDefault) {
                        when (item.angle_number) {
                            1 -> {
                                if (card_1 == null) card_1 = item
                            }
                            2 -> {
                                if (card_2 == null) card_2 = item
                            }
                            3 -> {
                                if (card_3 == null) card_3 = item
                            }
                            4 -> {
                                if (card_4 == null) card_4 = item
                            }
                        }
                    }

                    rl_tarot.visibility = View.VISIBLE
                    if (mCacheTarotList.isNotEmpty() && mCacheTarotList.size == 4) {
                        //用户已经选了4张 直接显示结果
                        showResult()
                    } else {
                        //否则显示卡牌列表布局
                        rcv.visibility = View.VISIBLE
                    }

                } else {
                    //如果用户第一次请求 获取话题塔罗牌数据
                    getTopicId()
                }
            }

        })
    }


    /**
     * 获取真爱话题的topic_id
     */
    private fun getTopicId() {
        //显示网络加载的loading view
        palm_loading.visibility = View.VISIBLE
        palm_loading.showNetworkLoadingView()

        var request = TarotTopicInfoRequest()
        request.tarot_card_array_id = arrayListOf(TarotTopicInfoRequest.tarot_card_array_id_career)
        request.topic_kind_ids = arrayListOf(TarotTopicInfoRequest.topic_kind_ids)

        if (NetworkUtil.isConnected(this)) {
            HttpClient
                .getTarotRequest()
                .getTarotTopicList(request)
                .compose(NetworkTransformer.toMainSchedulers<TarotTopicInfoResponse>())
                .subscribe(object : NetworkSubscriber<TarotTopicInfoResponse>() {

                    override fun onDispose(d: Disposable) {
                        mCompositeDisposable.add(d)
                    }

                    override fun onNext(t: TarotTopicInfoResponse) {
                        if (t.status_result!!.isSuccess() && t.topic_info_list.isNotEmpty()) {
                            t.topic_info_list.forEach {
                                //只要职业话题的id
                                if (it.topic_id == AppConstants.TAROT_TOPIC_CAREER) {
                                    getCards(it.topic_id)
                                }
                            }
                        } else {
                            palm_loading.hideAllView()
                            Toast.makeText(
                                this@TopicCareerActivity,
                                getString(R.string.net_server_error),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                })

        } else {
            palm_loading.hideAllView()
            Toast.makeText(
                this, getString(R.string.net_error), Toast.LENGTH_SHORT
            ).show()
        }
    }


    /**
     * 根据id获取真爱话题的塔罗牌(后台随机下发话题)
     */
    private fun getCards(topic_id: Int) {
        var request = TarotTopicInfoDetailRequest()

        if (NetworkUtil.isConnected(this)) {
            HttpClient
                .getTarotRequest()
                .getTarotTopicDetail(topic_id, request)
                .compose(NetworkTransformer.toMainSchedulers<TarotTopicInfoDetailResponse>())
                .subscribe(object : NetworkSubscriber<TarotTopicInfoDetailResponse>() {

                    override fun onDispose(d: Disposable) {
                        mCompositeDisposable.add(d)
                    }

                    override fun onNext(t: TarotTopicInfoDetailResponse) {
                        if (t.status_result!!.isSuccess() && t.topic_answer!!.topic_angle_answer_list!!.isNotEmpty()) {

                            var topic_angle_answer_list = t.topic_answer?.topic_angle_answer_list
                            for (item in topic_angle_answer_list!!) {
                                when (item.angle_number) {
                                    1 -> {
                                        if (card_1 == null) card_1 = item
                                    }
                                    2 -> {
                                        if (card_2 == null) card_2 = item
                                    }
                                    3 -> {
                                        if (card_3 == null) card_3 = item
                                    }
                                    4 -> {
                                        if (card_4 == null) card_4 = item
                                    }
                                }
                            }

                            //获取卡牌的内容后 添加到默认话题塔罗牌
                            if (t.topic_angle_content_dto_list!!.isNotEmpty()) {
                                for (item in t.topic_angle_content_dto_list!!) {
                                    when (item.angle_number) {
                                        1 -> {
                                            if (card_1 != null) {
                                                card_1?.topic = item.content
                                                mCacheTarotDefault.add(card_1!!)
                                            }
                                        }
                                        2 -> {
                                            if (card_2 != null) {
                                                card_2?.topic = item.content
                                                mCacheTarotDefault.add(card_2!!)
                                            }
                                        }
                                        3 -> {
                                            if (card_3 != null) {
                                                card_3?.topic = item.content
                                                mCacheTarotDefault.add(card_3!!)
                                            }
                                        }
                                        4 -> {
                                            if (card_4 != null) {
                                                card_4?.topic = item.content
                                                mCacheTarotDefault.add(card_4!!)
                                            }
                                        }
                                    }
                                }
                            }
                            mResult = t.topic_answer?.explanation!!
                            //将获取的数据存储到默认话题塔罗牌(第一次请求)和结果语 有效时间一天
                            DailyTarotTopicCareerManager.storeDefaultTarotTopic(mCacheTarotDefault, mResult)
                            tv_tarot_result.text = mResult
                            //隐藏loading view 显示塔罗牌布局和牌阵
                            palm_loading.hideAllView()
                            rl_tarot.visibility = View.VISIBLE
                            rcv.visibility = View.VISIBLE

                        } else {
                            palm_loading.hideAllView()
                            Toast.makeText(
                                this@TopicCareerActivity,
                                getString(R.string.net_server_error),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                })

        } else {
            palm_loading.hideAllView()
            Toast.makeText(
                this, getString(R.string.net_error), Toast.LENGTH_SHORT
            ).show()
        }
    }


    /**
     * 开始动画
     */
    private fun startAnimation(itemView: View, card: TopicAngleAnswer) {
        //禁止滑动和点击
        rcv.setIntercept(true)
        mLayoutManager.setHorizontalScroll(false)

        closeReplace(card.angle_number)


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
        var dy = targetY + resources.getDimension(R.dimen.change_126px)
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
        var alpha = ObjectAnimator.ofFloat(rl_cover, "alpha", 1f, 0.999f)
        var rotation = ObjectAnimator.ofFloat(itemView, "rotation", 0f, 180f)

        var animatorSet = AnimatorSet()
        if (card.card_place_type == AppConstants.TAROT_CARD_PLACE_TYPE_REVERSED) {
            //逆位 添加旋转动画
            animatorSet
                .play(translationY_1)
                .with(alpha)
                .with(translationY_2)
                .with(translationX)
                .with(rotation)
                .with(scaleX)
                .with(scaleY)
        } else {
            //正位
            animatorSet
                .play(translationY_1)
                .with(alpha)
                .with(translationY_2)
                .with(translationX)
                .with(scaleX)
                .with(scaleY)
        }
        animatorSet.duration = 500
        animatorSet.start()
        RxTimer.timer(150, RxTimer.RxAction() {
            //延迟150ms 显示卡牌详情布局
            rl_cover.visibility = View.VISIBLE
        })

        translationX.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {

            }

            override fun onAnimationEnd(animation: Animator?) {
                showCardDetail(itemView, card.angle_number)
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
    private fun recoverAnimation(card: TopicAngleAnswer) {
        var animatorSet = AnimatorSet()

        //获取屏幕的宽高
        var defaultDisplay = windowManager.defaultDisplay
        var point = Point()
        defaultDisplay.getSize(point)
        var targetX = (point.x / 2) //屏幕中心点的x坐标

        when (card.angle_number) {
            1 -> { //位置1
                isShowedCard_1 = false
                var translationY = ObjectAnimator.ofFloat(iv_replace1, "translationY", 0f, 150f)
                var scaleX = ObjectAnimator.ofFloat(iv_replace1, "scaleX", 1f, 0.394f)
                var scaleY = ObjectAnimator.ofFloat(iv_replace1, "scaleY", 1f, 0.394f)
                var alpha = ObjectAnimator.ofFloat(rl_cover, "alpha", 0.999f, 0.5f)
                animatorSet
                    .play(translationY)
                    .with(scaleX)
                    .with(scaleY)
                    .with(alpha)
            }

            2 -> { //位置2
                isShowedCard_2 = false
                var dx = targetX - rl_tarot.left - resources.getDimension(R.dimen.change_105px)
                var translationX = ObjectAnimator.ofFloat(iv_replace2, "translationX", 0f, -dx.absoluteValue)
                //偏移量50 通过目测调试得出..
                var translationY = ObjectAnimator.ofFloat(iv_replace2, "translationY", 0f, 150f)
                var scaleX = ObjectAnimator.ofFloat(iv_replace2, "scaleX", 1f, 0.394f)
                var scaleY = ObjectAnimator.ofFloat(iv_replace2, "scaleY", 1f, 0.394f)
                var alpha = ObjectAnimator.ofFloat(rl_cover, "alpha", 0.999f, 0f)
                animatorSet
                    .play(translationY)
                    .with(translationX)
                    .with(alpha)
                    .with(scaleX)
                    .with(scaleY)
            }

            3 -> { //位置3
                isShowedCard_3 = false
                //偏移量 通过目测调试得出..
                var dx = targetX - rl_tarot.left - resources.getDimension(R.dimen.change_105px)
                var translationX = ObjectAnimator.ofFloat(iv_replace3, "translationX", 0f, dx.absoluteValue)
                //偏移量40 通过目测调试得出..
                var translationY = ObjectAnimator.ofFloat(iv_replace3, "translationY", 0f, 150f)
                var scaleX = ObjectAnimator.ofFloat(iv_replace3, "scaleX", 1f, 0.394f)
                var scaleY = ObjectAnimator.ofFloat(iv_replace3, "scaleY", 1f, 0.394f)
                var alpha = ObjectAnimator.ofFloat(rl_cover, "alpha", 0.999f, 0.5f)
                animatorSet
                    .play(translationY)
                    .with(translationX)
                    .with(alpha)
                    .with(scaleX)
                    .with(scaleY)
            }

            4 -> { //位置4
                isShowedCard_4 = false
                //偏移量450 通过目测调试得出..
                var translationY = ObjectAnimator.ofFloat(iv_replace4, "translationY", 0f, -450f)
                var scaleX = ObjectAnimator.ofFloat(iv_replace4, "scaleX", 1f, 0.394f)
                var scaleY = ObjectAnimator.ofFloat(iv_replace4, "scaleY", 1f, 0.394f)
                var alpha = ObjectAnimator.ofFloat(rl_cover, "alpha", 0.999f, 0.5f)
                animatorSet
                    .play(translationY)
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
                rl_cover.visibility = View.GONE
                iv_titlebar_back.isClickable = true
                when (card.angle_number) {
                    1 -> {
                        mList.forEach {
                            if (card_1?.card_key == it.card_key) {
                                if (card_1?.card_place_type == AppConstants.TAROT_CARD_PLACE_TYPE_UPRIGHT) {
                                    iv_tarot_miaddle.setImageResource(it.cover)
                                } else if (card_1?.card_place_type == AppConstants.TAROT_CARD_PLACE_TYPE_REVERSED) {
                                    iv_tarot_miaddle.setImageResource(it.cover)
                                    iv_tarot_miaddle.rotation = 180f
                                }
                            }
                        }
                    }
                    2 -> {
                        mList.forEach {
                            if (card_2?.card_key == it.card_key) {
                                if (card_2?.card_place_type == AppConstants.TAROT_CARD_PLACE_TYPE_UPRIGHT) {
                                    iv_tarot_left.setImageResource(it.cover)
                                } else if (card_2?.card_place_type == AppConstants.TAROT_CARD_PLACE_TYPE_REVERSED) {
                                    iv_tarot_left.setImageResource(it.cover)
                                    iv_tarot_left.rotation = 180f
                                }
                            }
                        }
                    }
                    3 -> {
                        mList.forEach {
                            if (card_3?.card_key == it.card_key) {
                                if (card_3?.card_place_type == AppConstants.TAROT_CARD_PLACE_TYPE_UPRIGHT) {
                                    iv_tarot_right.setImageResource(it.cover)
                                } else if (card_3?.card_place_type == AppConstants.TAROT_CARD_PLACE_TYPE_REVERSED) {
                                    iv_tarot_right.setImageResource(it.cover)
                                    iv_tarot_right.rotation = 180f
                                }
                            }
                        }
                    }
                    4 -> {
                        mList.forEach {
                            if (card_4?.card_key == it.card_key) {
                                if (card_4?.card_place_type == AppConstants.TAROT_CARD_PLACE_TYPE_UPRIGHT) {
                                    iv_tarot_top.setImageResource(it.cover)
                                } else if (card_4?.card_place_type == AppConstants.TAROT_CARD_PLACE_TYPE_REVERSED) {
                                    iv_tarot_top.setImageResource(it.cover)
                                    iv_tarot_top.rotation = 180f
                                }
                            }
                        }
                    }
                }
                rcv.setIntercept(false)
                mLayoutManager.setHorizontalScroll(true)
                //选完5张牌后 显示结果语
                if (selectedCount == 4) {
                    showResult()
                }
            }

            override fun onAnimationCancel(animation: Animator?) {}

            override fun onAnimationStart(animation: Animator?) {}

        })

    }

    /**
     * 隐藏代替的卡牌
     */
    private fun closeReplace(angle_number: Int) {
        if (angle_number == 1) return
        ll_name.visibility = View.INVISIBLE
        tv_topic.visibility = View.INVISIBLE
        tv_card_name.visibility = View.INVISIBLE
        tv_card_content.visibility = View.INVISIBLE
        tv_keywords.visibility = View.INVISIBLE
        when (angle_number) {
            2 -> { //位置2
                iv_replace1.visibility = View.GONE
            }
            3 -> { //位置3
                iv_replace2.visibility = View.GONE
            }
            4 -> { //位置4
                iv_replace3.visibility = View.GONE
            }
        }
    }


    /**
     * 显示卡牌详情
     */
    private fun showCardDetail(itemView: View, angle_number: Int) {
        //显示卡牌详情时 禁止点击titlebar按钮
        iv_titlebar_back.isClickable = false
        //隐藏卡牌itemView
        itemView.visibility = View.GONE
        when (angle_number) {
            1 -> {
                mList.forEach {
                    if (card_1?.card_key == it.card_key) {
                        iv_replace1.visibility = View.VISIBLE
                        if (card_1?.card_place_type == AppConstants.TAROT_CARD_PLACE_TYPE_UPRIGHT) {
                            //正位
                            iv_replace1.setImageResource(it.cover)
                        } else if (card_1?.card_place_type == AppConstants.TAROT_CARD_PLACE_TYPE_REVERSED) {
                            //逆位 180度旋转卡牌
                            iv_replace1.setImageResource(it.cover)
                            iv_replace1.rotation = 180f
                        }
                    }
                }
            }
            2 -> {
                mList.forEach {
                    if (card_2?.card_key == it.card_key) {
                        iv_replace2.visibility = View.VISIBLE
                        if (card_2?.card_place_type == AppConstants.TAROT_CARD_PLACE_TYPE_UPRIGHT) {
                            //正位
                            iv_replace2.setImageResource(it.cover)
                        } else if (card_2?.card_place_type == AppConstants.TAROT_CARD_PLACE_TYPE_REVERSED) {
                            //逆位
                            iv_replace2.setImageResource(it.cover)
                            iv_replace2.rotation = 180f
                        }
                    }
                }
            }
            3 -> {
                mList.forEach {
                    if (card_3?.card_key == it.card_key) {
                        iv_replace3.visibility = View.VISIBLE
                        if (card_3?.card_place_type == AppConstants.TAROT_CARD_PLACE_TYPE_UPRIGHT) {
                            //正位
                            iv_replace3.setImageResource(it.cover)
                        } else if (card_3?.card_place_type == AppConstants.TAROT_CARD_PLACE_TYPE_REVERSED) {
                            //逆位
                            iv_replace3.setImageResource(it.cover)
                            iv_replace3.rotation = 180f
                        }
                    }
                }
            }
            4 -> {
                mList.forEach {
                    if (card_4?.card_key == it.card_key) {
                        iv_replace4.visibility = View.VISIBLE
                        if (card_4?.card_place_type == AppConstants.TAROT_CARD_PLACE_TYPE_UPRIGHT) {
                            //正位
                            iv_replace4.setImageResource(it.cover)
                        } else if (card_4?.card_place_type == AppConstants.TAROT_CARD_PLACE_TYPE_REVERSED) {
                            //逆位
                            iv_replace4.setImageResource(it.cover)
                            iv_replace4.rotation = 180f
                        }
                    }
                }
            }
        }
        RxTimer.timer(500, RxTimer.RxAction {
            iv_close.visibility = View.VISIBLE
            ll_name.visibility = View.VISIBLE
            tv_topic.visibility = View.VISIBLE
            tv_card_name.visibility = View.VISIBLE
            tv_keywords.visibility = View.VISIBLE
            tv_card_content.visibility = View.VISIBLE
        })
    }


    /**
     * 点击选中的卡牌显示详情
     */
    private fun showCardDialog(bean: TopicAngleAnswer) {
        val dialog = Dialog(this)
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(false)
        val window = dialog.window
        //dialog取消标题
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setContentView(R.layout.tarot_layout_tarot_love)
        window.setBackgroundDrawableResource(R.color.color_d9000000)
        window.findViewById<View>(R.id.iv_close).setOnClickListener { dialog.dismiss() }

        var replace = window.findViewById<ImageView>(R.id.iv_replace)
        mList.forEach {
            if (bean.card_key == it.card_key) {
                if (bean.card_place_type == AppConstants.TAROT_CARD_PLACE_TYPE_UPRIGHT) {
                    //正位
                    replace.setImageResource(it.cover)
                } else if (bean.card_place_type == AppConstants.TAROT_CARD_PLACE_TYPE_REVERSED) {
                    //逆位 旋转卡牌180度
                    replace.setImageResource(it.cover)
                    replace.rotation = 180f
                }

                window.findViewById<TextView>(R.id.tv_card_name).text = it.name
                window.findViewById<TextView>(R.id.tv_keywords).text = it.keyword
            }
        }
        window.findViewById<TextView>(R.id.tv_topic).text = bean.topic
        window.findViewById<TextView>(R.id.tv_card_content).text = bean.explanation
        val lp = window.getAttributes()
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.MATCH_PARENT
        dialog.show()
    }


    /**
     * 抽完5张牌后 显示解析结果
     */
    private fun showResult() {
//        iv_titlebar_share.visibility = View.VISIBLE
        rcv.visibility = View.GONE
        ll_tarot_result.visibility = View.VISIBLE
    }


    /**
     * 退出提醒
     */
    private fun exitTip() {
        //选中卡牌少于4张的时候 弹出dialog
        if (selectedCount in 1 until 4) {
            var dialog = CommonDialog(this)
            dialog
                .setTitle(getString(R.string.psy_confirm_quit))
                .setPositive(getString(R.string.yes))
                .setNegative(getString(R.string.no))
                .setOnClickBottomListener(this)
                .show()
        } else {
            //选完4张 保存卡牌
            DailyTarotTopicCareerManager.storeCacheTarotTopic(mCacheTarotList)
            finish()
        }
    }


    override fun onPositiveClick() {
        //选择确定按钮 保存卡牌并退出
        DailyTarotTopicCareerManager.storeCacheTarotTopic(mCacheTarotList)
        finish()
    }


    override fun onNegativeClick() {}


    /**
     * 监听返回键动作 是否开启复原动画
     */
    private fun backListener() {
        if (clickType != 0) {
            //点击牌的种类
            when (clickType) {
                1 -> {//位置1
                    if (isShowedCard_1) {
                        recoverAnimation(card_1!!)
                    } else {
                        exitTip()
                    }
                }
                2 -> {//位置2
                    if (isShowedCard_2) {
                        recoverAnimation(card_2!!)
                    } else {
                        exitTip()
                    }
                }
                3 -> {//位置3
                    if (isShowedCard_3) {
                        recoverAnimation(card_3!!)
                    } else {
                        exitTip()
                    }
                }
                4 -> {//位置4
                    if (isShowedCard_4) {
                        recoverAnimation(card_4!!)
                    } else {
                        exitTip()
                    }
                }
            }
        } else {
            //未点击选中的牌 直接退出
            if (selectedCount < 4) {
                exitTip()
            } else {
                finish()
            }
        }
    }


    override fun onBackPressed() {
        backListener()
    }


    override fun onDestroy() {
        mCompositeDisposable.clear()
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }
}

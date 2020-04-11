package com.palmapp.master.module_cnt.matching

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.palmapp.master.baselib.BaseMVPActivity
import com.palmapp.master.baselib.EmptyPresenter
import com.palmapp.master.baselib.EmptyView
import com.palmapp.master.baselib.GoCommonEnv
import com.palmapp.master.baselib.bean.user.getCntTranCover
import com.palmapp.master.baselib.bean.user.getConstellationById
import com.palmapp.master.baselib.constants.PreConstants
import com.palmapp.master.baselib.constants.RouterConstants
import com.palmapp.master.baselib.manager.GoPrefManager
import com.palmapp.master.baselib.manager.RouterServiceManager
import com.palmapp.master.baselib.proxy.BillingServiceProxy
import com.palmapp.master.baselib.utils.LogUtil
import com.palmapp.master.module_cnt.R
import kotlinx.android.synthetic.main.cnt_activity_selectcnt.*
import kotlinx.android.synthetic.main.cnt_guide_match.*

/**
 *
 * @author :     xiemingrui
 * @since :      2019/8/26
 */
@Route(path = RouterConstants.ACTIVITY_CNT_SELECT)
class CntMatchSelectActivity : BaseMVPActivity<EmptyView, EmptyPresenter>() {
    private var isNeedStopBGM = true
    var pos1 = -1   //当前pos
    var pos2 = -1   //目标pos
    //目标pos回调，-1表示没有目标pos
    var clickTime = 0L

    val first = GoPrefManager.getDefault().isFirstTime(PreConstants.First.KEY_FIRST_RUN_MATCH)

    fun onMatch(toPos: Int) {
        pos2 = toPos
    }

    fun onRelease(viewHolder: RecyclerView.ViewHolder?) {
        val diff = System.currentTimeMillis() - clickTime
        LogUtil.i("onRelease", "$diff")
        if (diff <= 500 && pos1 != -1) {
            isNeedStopBGM = false
            ARouter.getInstance().build(RouterConstants.ACTIVITY_CNT_MATCHING)
                .withInt("pos1", ids.get(pos1)).withInt("pos2", ids.get(pos1))
                .navigation(this@CntMatchSelectActivity)
        } else if (pos1 != -1 && pos2 != -1) {
            isNeedStopBGM = false
            ARouter.getInstance().build(RouterConstants.ACTIVITY_CNT_MATCHING)
                .withInt("pos1", ids[pos1]).withInt("pos2", ids[pos2])
                .navigation(this@CntMatchSelectActivity)
        }
        pos1 = -1
        pos2 = -1
    }

    fun onSelect(fromPos: Int) {
        clickTime = System.currentTimeMillis()
        pos1 = fromPos
        pos2 = -1
    }

    var itemTouchHelper: ItemTouchHelper? = null

    private val ids = arrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12)
    override fun createPresenter(): EmptyPresenter {
        return EmptyPresenter()
    }

    override fun onResume() {
        super.onResume()
        isNeedStopBGM = true
        GoCommonEnv.startBGM()
    }

    override fun onPause() {
        super.onPause()
        if (isNeedStopBGM)
            GoCommonEnv.stopBGM()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.cnt_activity_selectcnt)
        findViewById<TextView>(R.id.tv_titlebar_title).text = getString(R.string.constellation_selection)
        findViewById<View>(R.id.iv_titlebar_back).setOnClickListener { finish() }
        rv.layoutManager = GridLayoutManager(this, 3)
        rv.adapter = MyAdapter()
        val callback = CntMatchItemCallback()
        itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper?.attachToRecyclerView(rv)
        if (first)
            rv.post {
                val w = rv.getChildAt(0).width
                val h = rv.getChildAt(0).height
                viewStub.inflate()
                val iv = findViewById<View>(R.id.iv_cntmatching)
                val lp = iv.layoutParams
                lp.width = w
                lp.height = h
                iv.layoutParams = lp
                val hand = findViewById<View>(R.id.iv_hand)
                val t1 = ObjectAnimator.ofFloat(hand, "translationX", 0f, resources.getDimension(R.dimen.change_294px))
                t1.duration = 500

                val a1 = ObjectAnimator.ofFloat(hand, "alpha", 0f, 1f)
                a1.duration = 500

                val a2 = ObjectAnimator.ofFloat(hand, "alpha", 1f, 0f)
                a1.duration = 500

                val a0 = ObjectAnimator.ofFloat(iv, "alpha", 0f, 1f)
                a0.duration = 500

                val animatorSet = AnimatorSet()
                animatorSet.play(a1).before(t1).before(a2)
                animatorSet.play(t1).after(250)
                animatorSet.play(a2).after(750)
                animatorSet.addListener(object : Animator.AnimatorListener {
                    override fun onAnimationRepeat(animation: Animator?) {
                    }

                    override fun onAnimationEnd(animation: Animator?) {
                        animatorSet.start()
                    }

                    override fun onAnimationCancel(animation: Animator?) {
                    }

                    override fun onAnimationStart(animation: Animator?) {
                    }
                })
                val result = AnimatorSet()
                result.playTogether(a0, animatorSet)
                result.start()
                layout_guide.setOnClickListener {
                    layout_guide.visibility = View.GONE
                }
            }
    }

    private inner class MyAdapter : RecyclerView.Adapter<MyViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            return MyViewHolder(
                LayoutInflater.from(this@CntMatchSelectActivity).inflate(
                    R.layout.cnt_item_match_select,
                    parent,
                    false
                )
            )
        }

        override fun getItemCount(): Int {
            return ids.size
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.ivCnt.setImageResource(getCntTranCover(ids[position]))
            holder.tvCnt.text = getConstellationById(ids[position])
            holder.itemView.setOnClickListener {

            }
            holder.itemView.setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    itemTouchHelper?.startDrag(holder)
                }
                false
            }
        }

    }

    private inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var ivCnt: ImageView = itemView.findViewById(R.id.iv_cntselect_cnt)
        var tvCnt: TextView = itemView.findViewById(R.id.tv_cntselect_title)
        var ivBg: ImageView = itemView.findViewById(R.id.iv_cntselect_bg)

        init {
            val lp = itemView.layoutParams
            lp.height = rv.height / 4
            itemView.layoutParams = lp
        }
    }

    //Item Drag CallBack
    private inner class CntMatchItemCallback() : ItemTouchHelper.Callback() {
        private var lastViewHolder: MyViewHolder? = null
        private val MAX_DISTENCE: Int =
            GoCommonEnv.getApplication().resources.getDimensionPixelSize(R.dimen.change_114px)

        override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
            return makeFlag(
                ItemTouchHelper.ACTION_STATE_DRAG,
                ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            )
        }

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return true
        }

        override fun onMoved(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            fromPos: Int,
            target: RecyclerView.ViewHolder,
            toPos: Int,
            x: Int,
            y: Int
        ) {
            Log.i("onMoved", "$fromPos $toPos $x $y")
            lastViewHolder?.ivBg?.visibility = View.GONE
            lastViewHolder = target as MyViewHolder
            lastViewHolder?.ivBg?.visibility = View.VISIBLE
            onMatch(toPos)
        }

        override fun onChildDraw(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean
        ) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }

        override fun isLongPressDragEnabled(): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        }

        override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
            //判断选中状态
            if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
                if (viewHolder is MyViewHolder) {
                    viewHolder.ivBg.visibility = View.VISIBLE
                }
                onSelect(viewHolder?.adapterPosition ?: 0)
            }
            super.onSelectedChanged(viewHolder, actionState);
        }

        override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
            super.clearView(recyclerView, viewHolder)
            lastViewHolder?.ivBg?.visibility = View.GONE
            lastViewHolder = null
            if (viewHolder is MyViewHolder) {
                viewHolder.ivBg.visibility = View.GONE
                onRelease(viewHolder)
            }
        }

        override fun chooseDropTarget(
            selected: RecyclerView.ViewHolder,
            dropTargets: MutableList<RecyclerView.ViewHolder>,
            curX: Int,
            curY: Int
        ): RecyclerView.ViewHolder? {
            val viewHolder = getMatchTarget(selected, dropTargets, curX, curY)
            if (viewHolder == null) {
                lastViewHolder?.ivBg?.visibility = View.GONE
                lastViewHolder = null
                onMatch(-1)
            }
            return viewHolder
        }

        //获取目标ViewHolder
        private fun getMatchTarget(
            selected: RecyclerView.ViewHolder,
            dropTargets: MutableList<RecyclerView.ViewHolder>,
            curX: Int,
            curY: Int
        ): RecyclerView.ViewHolder? {
            val centerX = curX + selected.itemView.width / 2
            val centerY = curY + (selected as MyViewHolder).ivCnt.height / 2
            var winner: RecyclerView.ViewHolder? = null
            var winnerScore = -1
            val targetsSize = dropTargets.size
            for (i in 0 until targetsSize) {
                val target = dropTargets.get(i) as MyViewHolder
                val targetX = target.itemView.left + target.itemView.width / 2
                val targetY = target.itemView.top + target.ivCnt.height / 2
                val score = getDistance(centerX, targetX, centerY, targetY)
                if (winnerScore == -1 || score < winnerScore) {
                    winnerScore = score
                    winner = target
                }
            }
            if (winnerScore > MAX_DISTENCE) {
                winner = null
            }
            return winner
        }

        //获取两点距离
        private fun getDistance(x1: Int, x2: Int, y1: Int, y2: Int): Int {
            val value = (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2)
            return Math.abs(Math.sqrt(value.toDouble())).toInt()
        }
    }
}
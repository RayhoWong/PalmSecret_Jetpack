package com.palmapp.master.module_home.fragment


import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.palmapp.master.baselib.event.UnlockEvent
import com.palmapp.master.baselib.manager.TIMER_OLD
import com.palmapp.master.baselib.manager.TimerManager
import com.palmapp.master.baselib.manager.config.*
import com.palmapp.master.module_home.R
import com.palmapp.master.module_home.adapter.CommonTabAdapter
import kotlinx.android.synthetic.main.home_fragment_face.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


/**
 * 面相
 *
 */
class FaceFragment : Fragment() {

    private lateinit var mActivity: Activity
    private lateinit var mAdapter: CommonTabAdapter

    companion object {
        fun newInstance(): FaceFragment {
            return FaceFragment()
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mActivity = context!! as Activity
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        EventBus.getDefault().register(this)
        return inflater.inflate(R.layout.home_fragment_face, container, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        EventBus.getDefault().unregister(this)
        TimerManager.unregisterTimerListener(TIMER_OLD, mAdapter)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initView()
    }


    private fun initView() {
        var homeConfig = ConfigManager.getConfig(HomeConfig::class.java)?.datas?.get("0")
        var titles: ArrayList<String> = ArrayList()
        if (homeConfig != null) {
            for (config in homeConfig) {
                titles.add(config.func)
            }
            titles.add("ad")
        } else {
            titles.addAll(getDefaultOrder())
        }
        mAdapter = CommonTabAdapter(titles.toMutableList(), mActivity, getString(R.string.home_face_title), 0)
        val time = ConfigManager.getConfig(TimeConfig::class.java)?.old_delay ?: 1000L
        TimerManager.registerTimerListener(TIMER_OLD, mAdapter)
        rcv_face.layoutManager = LinearLayoutManager(mActivity)
        rcv_face.adapter = mAdapter
        TimerManager.startTimer(TIMER_OLD, time)
    }

    private fun getDefaultOrder() =
        listOf(TAB_FACE_OLD, TAB_FACE_BABY, TAB_FACE_MANGA, TAB_FACE_TRANSFORM, TAB_FACE_ANIMAL, TAB_FACE_CARTOON, "ad")

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onUnlockEvent(event: UnlockEvent) {
        mAdapter.notifyDataSetChanged()
    }
}

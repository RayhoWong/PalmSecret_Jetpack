package com.palmapp.master.module_home.fragment


import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.palmapp.master.module_home.R
import com.palmapp.master.module_home.adapter.CommonTabAdapter
import kotlinx.android.synthetic.main.home_fragment_tarot.*


/**
 * 塔罗
 */
class TarotFragment : Fragment() {

    private lateinit var mActivity: Activity
    private lateinit var mAdapter: CommonTabAdapter

    companion object{
        fun newInstance(): TarotFragment{
            return TarotFragment()
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mActivity = context!! as Activity
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.home_fragment_tarot, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initView()
    }

    private fun initView(){
        var titles: List<String> = listOf("0","1","2","ad","3")
        mAdapter = CommonTabAdapter(titles.toMutableList(),mActivity,getString(R.string.home_tarot_title),4)
        rcv_tarot.layoutManager = LinearLayoutManager(mActivity)
        rcv_tarot.adapter = mAdapter

    }

}

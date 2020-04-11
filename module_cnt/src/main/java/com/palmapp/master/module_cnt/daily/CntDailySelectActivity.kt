package com.palmapp.master.module_cnt.daily

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.palmapp.master.baselib.BaseMVPActivity
import com.palmapp.master.baselib.EmptyPresenter
import com.palmapp.master.baselib.EmptyView
import com.palmapp.master.baselib.GoCommonEnv
import com.palmapp.master.baselib.bean.user.getCntTranCover
import com.palmapp.master.baselib.bean.user.getConstellationById
import com.palmapp.master.baselib.bean.user.getDateById
import com.palmapp.master.module_cnt.R
import kotlinx.android.synthetic.main.cnt_activity_selectcnt.*

/**
 *
 * @author :     xiemingrui
 * @since :      2019/8/20
 */
class CntDailySelectActivity : BaseMVPActivity<EmptyView, EmptyPresenter>(), EmptyView {
    private val ids = arrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12)
    private val isFirst = GoCommonEnv.userInfo?.constellation ?: -1 == -1
    override fun createPresenter(): EmptyPresenter {
        return EmptyPresenter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.cnt_activity_selectcnt)
        findViewById<TextView>(R.id.tv_titlebar_title).text = getString(R.string.constellation_selection)
        findViewById<View>(R.id.iv_titlebar_back).setOnClickListener {
            val intent = Intent()
            intent.putExtra("id", -1)
            setResult(100, intent)
            finish()
        }
        tv_first.visibility = if (isFirst) {
            View.VISIBLE
        } else {
            View.GONE
        }
        rv.layoutManager = GridLayoutManager(this, 3)
        rv.adapter = MyAdapter()
    }

    override fun onBackPressed() {
        val intent = Intent()
        intent.putExtra("id", -1)
        setResult(100, intent)
        finish()
    }

    private inner class MyAdapter : RecyclerView.Adapter<MyViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            return MyViewHolder(
                LayoutInflater.from(this@CntDailySelectActivity).inflate(
                    R.layout.cnt_item_daily_select,
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
            holder.tvDate.text = "(${getDateById(ids[position])})"
            holder.itemView.setOnClickListener {
                val intent = Intent()
                intent.putExtra("id", ids[position])
                setResult(100, intent)
                finish()
            }
        }

    }

    private inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var ivCnt: ImageView = itemView.findViewById(R.id.iv_cntselect_cnt)
        var tvCnt: TextView = itemView.findViewById(R.id.tv_cntselect_title)
        var tvDate: TextView = itemView.findViewById(R.id.tv_cntselect_date)
    }
}
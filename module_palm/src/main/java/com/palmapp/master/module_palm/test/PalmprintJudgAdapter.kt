package com.palmapp.master.module_palm.test

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.palmapp.master.baselib.bean.quiz.QuestionDTO
import com.palmapp.master.baselib.utils.GlideUtil
import com.palmapp.master.module_palm.R
import org.greenrobot.eventbus.EventBus

/**
 * Created by huangweihao on 2019/8/13.
 * 手相问答题列表
 */
class PalmprintJudgAdapter : RecyclerView.Adapter<PalmprintJudgAdapter.MyViewHolder> {
    private var mContext: Context
    private var mData: List<QuestionDTO>


    constructor(mContext: Context, mData: List<QuestionDTO>) : super() {
        this.mContext = mContext
        this.mData = mData
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        var view = LayoutInflater.from(mContext).inflate(R.layout.palm_palmprint_judg_item, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int = mData.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.mTvSummary.text = mData[position].title
        GlideUtil.loadImage(mContext, mData[position].img, holder.mIvCover)
        holder.itemView.setOnClickListener {
            EventBus.getDefault().postSticky(mData[position])
            mContext.startActivity(Intent(mContext, PalmprintJudgDetailActivity::class.java))
        }
    }


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mIvCover = itemView.findViewById<ImageView>(R.id.iv_cover)
        val mTvSummary = itemView.findViewById<TextView>(R.id.tv_title)
    }

}
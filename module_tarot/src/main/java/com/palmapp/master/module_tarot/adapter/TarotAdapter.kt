package com.palmapp.master.module_tarot.adapter

import android.content.Context
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.palmapp.master.baselib.GoCommonEnv
import com.palmapp.master.baselib.bean.tarot.DailyTarotBean
import com.palmapp.master.baselib.bean.tarot.TarotBean
import com.palmapp.master.baselib.bean.tarot.TarotListBean
import com.palmapp.master.baselib.view.FlipImageView
import com.palmapp.master.module_tarot.R

/**
 * Created by huangweihao on 2019/8/19.
 * 塔罗牌item
 */
class TarotAdapter : RecyclerView.Adapter<TarotAdapter.MyViewHolder> {
    private var mContext: Context
    private var mData: List<TarotBean>
    private var mRemove: List<DailyTarotBean> = arrayListOf()
    private var mClickListener: OnItemClickListener

    constructor(context: Context, data: List<TarotBean>, onItemClickListener: OnItemClickListener) {
        mContext = context
        mData = data
        mClickListener = onItemClickListener
    }

    constructor(
        context: Context,
        data: List<TarotBean>,
        remove: List<DailyTarotBean>,
        onItemClickListener: OnItemClickListener
    ) {
        mContext = context
        mData = data
        mRemove = remove
        mClickListener = onItemClickListener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        var view = LayoutInflater.from(mContext).inflate(R.layout.tarot_card_item, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int = mData.size


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.mIvCard.isClickable = false
        holder.itemView.setOnClickListener {
            if (mClickListener != null) {
                holder.mIvCard.setFlippedDrawable(mContext.resources.getDrawable(mData[position].cover))
                mClickListener.onClick(holder.itemView, position)
            }
        }
//        val index = mRemove.indexOfFirst { TextUtils.equals(it.card_key, mData[position].card_key) && it.isFlip }
//        Log.d("tarot", "$index")
        holder.itemView.visibility = View.VISIBLE
    }


    interface OnItemClickListener {
        fun onClick(itemview: View, position: Int)
    }


    class MyViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        val mIvCard: FlipImageView = itemview.findViewById(R.id.iv_card)
    }
}
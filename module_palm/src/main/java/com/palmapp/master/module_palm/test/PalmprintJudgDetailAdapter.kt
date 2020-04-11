package com.palmapp.master.module_palm.test

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.palmapp.master.baselib.bean.quiz.OptionDTO
import com.palmapp.master.baselib.bean.quiz.QuziAnswerDTO
import com.palmapp.master.baselib.constants.RouterConstants
import com.palmapp.master.baselib.manager.RouterServiceManager
import com.palmapp.master.baselib.proxy.BillingServiceProxy
import com.palmapp.master.baselib.utils.GlideUtil
import com.palmapp.master.module_palm.R
import org.greenrobot.eventbus.EventBus

/**
 * Created by huangweihao on 2019/8/15.
 * 手相问答题选项
 */
class PalmprintJudgDetailAdapter : RecyclerView.Adapter<PalmprintJudgDetailAdapter.MyViewHolder> {
    private var mContext: Context
    private var mOption: List<OptionDTO>
    private var mAnswer: List<QuziAnswerDTO>
    private var isBind: Boolean? = null //判断rcv是否滑动(计算布局)的标记
    private var selectPosition: Int? = null //选中的位置

    private var mOnCheckedListener: OnCheckedListener? = null


    constructor(mContext: Context, mOption: List<OptionDTO>, mAnswer: List<QuziAnswerDTO>) : super() {
        this.mContext = mContext
        this.mOption = mOption
        this.mAnswer = mAnswer
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        var view = LayoutInflater.from(mContext).inflate(R.layout.palm_palmprint_judg_detail_item, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int = mOption.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
//        LogUtil.d("PalmOnBindView", "--onBindViewHolder-----" + mOption[position].name)
        isBind = true
        holder.mTvSDescription.text = mOption[position].description
        GlideUtil.loadImage(mContext, mOption[position].picture, holder.mIvPicture)
        //单选
        holder.mRbOption.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                //当rcv没有滑动的时候 更新界面
                if (isBind == false) {
                    selectPosition = position
                    notifyDataSetChanged()

                    mOnCheckedListener?.let {
                        mOnCheckedListener!!.onChecked(holder.itemView,position)
                    }
                }
            }
        }
        holder.mRbOption.isChecked = position == selectPosition
        isBind = false
    }

    //返回选中项的位置
    fun getSelectPosition(): Int? {
        return selectPosition
    }

    fun setCheckedListener(onCheckedListener: OnCheckedListener) {
        mOnCheckedListener = onCheckedListener
    }

    interface OnCheckedListener {
        fun onChecked(itemView: View, position: Int)
    }


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mRbOption = itemView.findViewById<RadioButton>(R.id.rb_option)
        val mIvPicture = itemView.findViewById<ImageView>(R.id.iv_picture)
        val mTvSDescription = itemView.findViewById<TextView>(R.id.tv_description)
    }

}
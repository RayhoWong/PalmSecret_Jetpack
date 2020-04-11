package com.palmapp.master.baselib.view

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.palmapp.master.baselib.R

/**
 *  网络状态切换view
 * @author :     xiemingrui
 * @since :      2019/8/27
 */
class NetworkStateView(context: Context, attributeSet: AttributeSet) : FrameLayout(context, attributeSet) {
    private var view: View? = null
    private val border = GradientBorderDrawable(
        resources.getDimension(R.dimen.change_3px), resources.getDimension(R.dimen.change_84px),
        Color.parseColor("#0bc88c"), Color.parseColor("#187edc")
    )

    //服务器错误
    fun showServerErrorView(retry: () -> Unit) {
        removeAllViewsInLayout()
        visibility = View.VISIBLE
        view?.visibility = View.INVISIBLE
        val root = LayoutInflater.from(context).inflate(R.layout.layout_state_net_error, this)
        val btn = root.findViewById<View>(R.id.btn_netstate_again)
        btn.background = border
        btn.setOnClickListener {
            retry()
        }
    }

    //网络超时
    fun showNetworkErrorView(retry: () -> Unit) {
        removeAllViewsInLayout()
        visibility = View.VISIBLE
        view?.visibility = View.INVISIBLE
        val root = LayoutInflater.from(context).inflate(R.layout.layout_state_net_timeout, this)
        val btn = root.findViewById<View>(R.id.btn_netstate_again)
        btn.background = border
        btn.setOnClickListener {
            retry()
        }
    }

    //加载页
    fun showNetworkLoadingView() {
        removeAllViewsInLayout()
        visibility = View.VISIBLE
        view?.visibility = View.INVISIBLE
        LayoutInflater.from(context).inflate(R.layout.layout_state_net_loading, this)
    }

    //隐藏所有视图
    fun hideAllView() {
        removeAllViewsInLayout()
        visibility = View.GONE
        view?.visibility = View.VISIBLE
    }

    //绑定目标view（目的是为了显示和隐藏目标View）
    fun bindView(v: View) {
        view = v
    }
}
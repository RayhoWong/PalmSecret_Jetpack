package com.palmapp.master.module_ad

import com.palmapp.master.baselib.IView

/**
 *
 * @author :     xiemingrui
 * @since :      2020/3/12
 */
interface IRewardVideoView :IView{
    /**
     * 激励视频播放完后该展示的界面，一般是用来隐藏假结果页
     */
    fun showRewardVipView()
}
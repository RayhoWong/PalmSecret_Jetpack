package com.palmapp.master.module_me

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.palmapp.master.baselib.BasePresenter
import com.palmapp.master.baselib.GoCommonEnv
import com.palmapp.master.baselib.bean.user.getCntCover
import com.palmapp.master.baselib.bean.user.getConstellationById
import com.palmapp.master.baselib.event.SubscribeUpdateEvent
import com.palmapp.master.baselib.manager.ShareManager
import com.palmapp.master.baselib.statistics.BaseSeq103OperationStatistic
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.text.SimpleDateFormat


/**
 *
 * @author :     xiemingrui
 * @since :      2019/7/31
 */
class MePresenter : BasePresenter<MeView>() {
    override fun onAttach() {
        EventBus.getDefault().register(this)
    }

    override fun onDetach() {
        EventBus.getDefault().unregister(this)
    }

    //接受某个选项的名称
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSubscribeUpdateEvent(event: SubscribeUpdateEvent) {
        getView()?.toUpdateUIWithVip(event.isVip)
    }

    private val sdf: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
    fun birthdayStr(birthdayL:Long?):String {
        return sdf.format(birthdayL)
    }

    fun share(activity:Context?, shareBitmap:Bitmap?) {
        BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
            BaseSeq103OperationStatistic.share_a000,
            "",
            "13",
            ""
        )
        val view = LayoutInflater.from(activity).inflate(R.layout.me_fragment_share,null,false)
        if (view != null) {
            val uinfo = GoCommonEnv.userInfo
            uinfo?.let {
                view.findViewById<ImageView>(R.id.me_share).setImageBitmap(shareBitmap)
                view.findViewById<ImageView>(R.id.iv_me).setImageResource(getCntCover(it.constellation))
                view.findViewById<TextView>(R.id.tv_user_name).setText(it.name)
                view.findViewById<TextView>(R.id.tv_user_cnt).setText(getConstellationById(it.constellation))
            }
            if (TextUtils.isEmpty(GoCommonEnv.userInfo?.name ?: "")) {
                view.findViewById<View>(R.id.layout_header).visibility = View.GONE
            }
            ShareManager.share(view, getView()?.getContext()?.getString(R.string.app_app_name)?:"")
        }
    }

    fun createShareBitmap(v: View): Bitmap {
        val bmp = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888)
        val c = Canvas(bmp)
        v.draw(c)
        return bmp
    }
}
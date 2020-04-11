package com.palmapp.master.module_me

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.alibaba.android.arouter.launcher.ARouter
import com.palmapp.master.baselib.BaseFragment
import com.palmapp.master.baselib.GoCommonEnv
import com.palmapp.master.baselib.bean.user.GENDER_FEMALE
import com.palmapp.master.baselib.bean.user.GENDER_MALE
import com.palmapp.master.baselib.bean.user.getCntCover
import com.palmapp.master.baselib.bean.user.getConstellationById
import com.palmapp.master.baselib.constants.RouterConstants
import com.palmapp.master.baselib.event.SubscribeUpdateEvent
import com.palmapp.master.baselib.proxy.BillingServiceProxy
import com.palmapp.master.baselib.utils.StatusBarUtil
import com.palmapp.master.infomation.EditInfoActivity
import kotlinx.android.synthetic.main.me_fragment_me.*
import org.greenrobot.eventbus.EventBus


/**
 *
 * @author :     xiemingrui
 * @since :      2019/7/31
 * 我的
 */
class MeFragment : BaseFragment<MeView, MePresenter>(), MeView, View.OnClickListener {
    override fun onClick(v: View?) {

        when (v?.id) {
            R.id.me_edit_btn -> {
                startActivity(Intent(activity, EditInfoActivity::class.java))
//                EventBus.getDefault().post(SubscribeUpdateEvent(true))
            }
            R.id.me_tovip -> ARouter.getInstance().build(RouterConstants.ACTIVITY_PAY).withString(
                "entrance",
                "0"
            ).navigation()
            R.id.me_setting_btn -> ARouter.getInstance().build(RouterConstants.ACTIVITY_MESettingsActivity).navigation(
                activity
            )
            R.id.me_share -> {
                val share = mPresenter?.createShareBitmap(me_share_view)
                mPresenter?.share(context, share)
            }
        }
    }

    override fun createPresenter(): MePresenter {
        return MePresenter()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.me_fragment_me, container, false)
    }

    companion object {
        fun newInstance(): MeFragment {
            return MeFragment()
        }
    }

    override fun onResume() {
        super.onResume()
        val info = GoCommonEnv.userInfo
        info?.let {

            me_name?.setText(it.name)
            me_constellation?.setText(getConstellationById(it.constellation))
            me_date?.setText(mPresenter?.birthdayStr(it.birthday))
            when (it.gender) {
                GENDER_MALE -> me_gender_image?.setImageResource(R.mipmap.me_pic_boy)
                GENDER_FEMALE -> me_gender_image?.setImageResource(R.mipmap.me_pic_girl)
                else -> {
                }
            }
            me_constellation_img.setImageResource(getCntCover(it.constellation))
            it.flagMaps.values.let { items ->
                me_circle_layout.setLabelItem(items)
                me_flag_no_flag?.visibility = if (items.size > 0) View.INVISIBLE else View.VISIBLE
            }
        }

        toUpdateUIWithVip(BillingServiceProxy.isVip())
        if (TextUtils.isEmpty(GoCommonEnv.userInfo?.name ?: "")) {
            layout_info.visibility = View.GONE
            tv_no_message.visibility = View.VISIBLE
        } else {
            layout_info.visibility = View.VISIBLE
            tv_no_message.visibility = View.GONE
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        me_edit_btn.setOnClickListener(this)
        me_tovip.setOnClickListener(this)
        me_setting_btn.setOnClickListener(this)
        me_share.setOnClickListener(this)
        StatusBarUtil.setStatusBarHeight(rootView)
    }

    override fun toUpdateUIWithVip(isVip: Boolean) {
        me_vip_img.visibility = if (isVip) View.VISIBLE else View.INVISIBLE
        me_share.visibility = if (isVip) View.VISIBLE else View.INVISIBLE
        me_circle_layout.visibility = if (isVip) View.VISIBLE else View.INVISIBLE
        me_blur_view.visibility = if (isVip) View.INVISIBLE else View.VISIBLE

        if (GoCommonEnv.userInfo?.flagMaps?.size ?: 0 == 0) {
            me_share.visibility = View.INVISIBLE
        }
    }

}
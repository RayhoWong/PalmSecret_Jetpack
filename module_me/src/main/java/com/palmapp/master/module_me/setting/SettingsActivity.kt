package com.palmapp.master.infomation;

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.palmapp.master.baselib.BaseMVPActivity
import com.palmapp.master.baselib.constants.RouterConstants
import com.palmapp.master.baselib.statistics.BaseSeq101OperationStatistic
import com.palmapp.master.baselib.utils.MachineUtil
import com.palmapp.master.module_me.R
import kotlinx.android.synthetic.main.me_activity_setting.*

/**
 * Created by zhangliang on 19/8/20
 *
 * 设置页面
 *
 * @email: zhangliang@gomo.com
 */
@Route(path = RouterConstants.ACTIVITY_MESettingsActivity)
class SettingsActivity : BaseMVPActivity<PolicyView, PolicyPresenter>(), PolicyView, View.OnClickListener {

    val PrivacyURL = "http://d2prafqgniatg5.cloudfront.net/palmhoroscope/palmsecret_p2.html"
    val TemaOfService = "http://d2prafqgniatg5.cloudfront.net/palmhoroscope/palmsecret_p1.html"

    override fun onClick(v: View?) {
        when (v) {
            mBackIv -> finish()
            me_setting_privacy -> {
                BaseSeq101OperationStatistic.uploadOperationStatisticData(BaseSeq101OperationStatistic.setting_a000, "", "1")
                ARouter.getInstance().build(RouterConstants.ACTIVITY_MEPolicyActivity).withString("url", PrivacyURL).navigation()
            }
            me_setting_terms -> {
                BaseSeq101OperationStatistic.uploadOperationStatisticData(BaseSeq101OperationStatistic.setting_a000, "", "2")
                ARouter.getInstance().build(RouterConstants.ACTIVITY_MEPolicyActivity).withString("url", TemaOfService).navigation()
            }
            me_setting_feedback -> {
                BaseSeq101OperationStatistic.uploadOperationStatisticData(BaseSeq101OperationStatistic.setting_a000, "", "3")
                ARouter.getInstance().build(RouterConstants.ACTIVITY_MEFeedbackActivity).navigation()
            }
        }
    }

    override fun createPresenter(): PolicyPresenter {
        return PolicyPresenter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.me_activity_setting)

        mBackIv = findViewById(R.id.iv_titlebar_back)
        mBackIv?.let {
            it.setOnClickListener(this)
        }

        mTitlebarTv = findViewById(R.id.tv_titlebar_title)
        mTitlebarTv?.let {
            it.setText(getString(R.string.setting_title))
        }

        me_setting_privacy?.setOnClickListener(this)
        me_setting_terms?.setOnClickListener(this)
        me_setting_feedback?.setOnClickListener(this)
        BaseSeq101OperationStatistic.uploadOperationStatisticData(BaseSeq101OperationStatistic.setting_f000)
    }

    private var mBackIv:ImageView? = null
    private var mTitlebarTv:TextView? = null
}
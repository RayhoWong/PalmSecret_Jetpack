package com.palmapp.master.module_cnt

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.android.arouter.launcher.ARouter
import com.palmapp.master.baselib.bean.cnt.*
import com.palmapp.master.baselib.constants.RouterConstants
import com.palmapp.master.module_cnt.R
import com.palmapp.master.module_cnt.daily.CntDailyActivity
import com.palmapp.master.module_network.HttpClient
import com.palmapp.master.module_network.NetworkSubscriber
import com.palmapp.master.module_network.NetworkTransformer

/**
 *
 * @author :     xiemingrui
 * @since :      2019/8/13
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.cnt_activity_main)
        findViewById<View>(R.id.btn_daily).setOnClickListener {
            ARouter.getInstance().build(RouterConstants.ACTIVITY_CNT_DAILY).navigation()
        }

        findViewById<View>(R.id.btn_matching).setOnClickListener {
            ARouter.getInstance().build(RouterConstants.ACTIVITY_CNT_SELECT).navigation()
        }
    }
}
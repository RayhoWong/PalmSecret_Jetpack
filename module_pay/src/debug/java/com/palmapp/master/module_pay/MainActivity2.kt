package com.palmapp.master.module_pay

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.cs.bd.subscribe.SubscribeHelper
import com.palmapp.master.baselib.GoCommonEnv
import com.palmapp.master.baselib.constants.AppConstants
import com.palmapp.master.baselib.constants.RouterConstants
import com.palmapp.master.module_network.HttpClient
import com.palmapp.master.module_network.NetworkTransformer
import io.reactivex.schedulers.Schedulers

@Route(path = "/temp/t2")
class MainActivity2 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pay_activity_main2)
    }
}

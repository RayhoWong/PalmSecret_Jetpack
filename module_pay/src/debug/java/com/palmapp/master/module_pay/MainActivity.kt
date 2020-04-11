package com.palmapp.master.module_pay

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import com.alibaba.android.arouter.launcher.ARouter
import com.cpcphone.abtestcenter.cache.NetCache
import com.cs.bd.ad.abtest.ABTestManager
import com.cs.bd.subscribe.SubscribeHelper
import com.palmapp.master.baselib.GoCommonEnv
import com.palmapp.master.baselib.Product
import com.palmapp.master.baselib.buychannel.BuyChannelProxy
import com.palmapp.master.baselib.constants.AppConstants
import com.palmapp.master.baselib.constants.PreConstants
import com.palmapp.master.baselib.constants.RouterConstants
import com.palmapp.master.baselib.manager.AbTestUserManager
import com.palmapp.master.baselib.manager.GoPrefManager
import com.palmapp.master.baselib.manager.PayGuideManager
import com.palmapp.master.baselib.manager.RouterServiceManager
import com.palmapp.master.baselib.manager.config.*
import com.palmapp.master.module_network.HttpClient
import com.palmapp.master.module_network.NetworkTransformer

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pay_activity_main)
        ConfigManager.addConfig(PayConfig()).addConfig(AbUserConfig()).addConfig(AdConfig())
            .addConfig(
                StarGuideConfig()
            )
        ConfigManager.init()
        PayGuideManager.loadCacheConfig()
        findViewById<View>(R.id.btn_pay1).setOnClickListener {
            val et = findViewById<EditText>(R.id.et_campaign)
            BuyChannelProxy.campaign = et.text.toString()
            BuyChannelProxy.setBuyChannelUser()
            val sp = getSharedPreferences("ab_net_cache", Context.MODE_PRIVATE)
            sp.edit().clear().commit()
            GoPrefManager.getDefault().putString(PreConstants.First.KEY_FIRST_TEST_USER, "")
            PayGuideManager.clearPaymentGuideConfig()
            ConfigManager.requestAbConfig()
            HttpClient.getConfRequest().getResource(Product.MODULE_ID_PAY_GUIDE)
                .compose(NetworkTransformer.toMainSchedulers()).subscribe {
                    GoCommonEnv.storePaymentGuideConfig(it)
                }
        }
        findViewById<View>(R.id.btn_start12).setOnClickListener {
            val config = PayGuideManager.payGuideConfig.get(PayGuideManager.FIRST_LAUNCHER)!!
            ARouter.getInstance().build(RouterConstants.ACTIVITY_PAY).withString("entrance", config.scen).withString("newPlan",config.plan)
                .navigation(this)
        }
        findViewById<View>(R.id.btn_start13).setOnClickListener {
            val config = PayGuideManager.payGuideConfig.get(PayGuideManager.NOT_FIRST_LAUNCHER)!!
            ARouter.getInstance().build(RouterConstants.ACTIVITY_PAY).withString("entrance", config.scen).withString("newPlan",config.plan)
                .navigation(this)
        }
        findViewById<View>(R.id.btn_pay).setOnClickListener {
            ConfigManager.requestAbConfig()
            HttpClient.getConfRequest().getResource(Product.MODULE_ID_PAY_GUIDE)
                .compose(NetworkTransformer.toMainSchedulers()).subscribe {
                    GoCommonEnv.storePaymentGuideConfig(it)
                }
        }
        findViewById<View>(R.id.btn_clear).setOnClickListener {
            val sp = getSharedPreferences("ab_net_cache", Context.MODE_PRIVATE)
            sp.edit().clear().commit()
            GoPrefManager.getDefault().putString(PreConstants.First.KEY_FIRST_TEST_USER, "")
            PayGuideManager.clearPaymentGuideConfig()
            ConfigManager.requestAbConfig()
            HttpClient.getConfRequest().getResource(Product.MODULE_ID_PAY_GUIDE)
                .compose(NetworkTransformer.toMainSchedulers()).subscribe {
                    GoCommonEnv.storePaymentGuideConfig(it)
                }
        }
        findViewById<View>(R.id.btn_start).setOnClickListener {
            ARouter.getInstance().build(RouterConstants.ACTIVITY_PAY).withString("entrance", "1")
                .navigation(this)
        }
        findViewById<View>(R.id.btn_start5).setOnClickListener {
            ARouter.getInstance().build(RouterConstants.ACTIVITY_PAY).withString("entrance", "1")
                .navigation(this)
        }
        findViewById<View>(R.id.btn_start6).setOnClickListener {
            ARouter.getInstance().build(RouterConstants.ACTIVITY_PAY).withString("entrance", "1")
                .navigation(this)
        }

        findViewById<View>(R.id.btn_start7).setOnClickListener {
            ARouter.getInstance().build(RouterConstants.ACTIVITY_PAY).withString("entrance", "7")
                .navigation(this)
        }

        findViewById<View>(R.id.btn_start2).setOnClickListener {
            ARouter.getInstance().build(RouterConstants.ACTIVITY_PAY).withString("entrance", "1")
                .withString("type", "3").navigation(this)
        }

        findViewById<View>(R.id.btn_start3).setOnClickListener {
            ARouter.getInstance().build(RouterConstants.ACTIVITY_PAY).withString("entrance", "1")
                .withString("type", "6").navigation(this)
        }

        findViewById<View>(R.id.btn_start8).setOnClickListener {
            ARouter.getInstance().build(RouterConstants.ACTIVITY_PAY).withString("entrance", "6")
                .withString("type", "9").navigation(this)
        }

        findViewById<View>(R.id.btn_start9).setOnClickListener {
            ARouter.getInstance().build(RouterConstants.ACTIVITY_PAY).withString("entrance", "6")
                .withString("type", "10").navigation(this)
        }
        findViewById<View>(R.id.btn_start10).setOnClickListener {
            ARouter.getInstance().build(RouterConstants.ACTIVITY_PAY).withString("entrance", "6")
                .withString("type", "11").navigation(this)
        }
        findViewById<View>(R.id.btn_start11).setOnClickListener {
            ARouter.getInstance().build(RouterConstants.ACTIVITY_PAY).withString("entrance", "6")
                .withString("type", "12").navigation(this)
        }
    }
}

package com.palmapp.master.module_ad

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import com.cs.bd.daemon.util.LogUtils
import com.google.android.gms.ads.AdView
import com.palmapp.master.baselib.manager.config.ConfigManager
import com.palmapp.master.baselib.view.AdsWatchDialog
import com.palmapp.master.module_ad.manager.BootIntersetAdManager
import com.palmapp.master.module_ad.manager.LaunchAdManager
import com.palmapp.master.module_ad.manager.RewardAdManager
import com.palmapp.master.module_ad.manager.TabBottomAdManager



class MainActivity : AppCompatActivity() {

    private lateinit var container: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ad_activity_main)
        findViewById<View>(R.id.loadConfig).setOnClickListener {
            BootIntersetAdManager.loadAd(this)
        }

        findViewById<View>(R.id.show).setOnClickListener {
            BootIntersetAdManager.showAd()
        }

        findViewById<View>(R.id.showDialog).setOnClickListener {
//            AdsWatchDialog(this, RewardAdManager.getConfig()?.show_count ?: 0).show()

            LogUtils.d("展示次数：${ BootIntersetAdManager.getConfig()?.show_count}"
                    +"\n"+"开关：${BootIntersetAdManager.getConfig()?.switch}")
        }

        container = findViewById(R.id.ad_container)

        loadAd()
    }

    @Suppress("MISSING_DEPENDENCY_CLASS")
    private fun loadAd() {
        TabBottomAdManager.loadAd(this,object : TabBottomAdManager.OnAdListener{
            override fun getBannerAdView(adView: AdView) {
                adView?.let {
                    var view = it as View
                    container.addView(view)
                }
            }
        })
        TabBottomAdManager.showAd()
    }
}

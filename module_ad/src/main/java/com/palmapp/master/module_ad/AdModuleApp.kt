package com.palmapp.master.module_ad

import android.app.Application
import com.cs.bd.ad.AdSdkApi
import com.cs.bd.ad.params.ClientParams
import com.cs.bd.buychannel.BuyChannelApi
import com.palmapp.master.baselib.BaseModuleApp
import com.palmapp.master.baselib.buychannel.BuyChannelProxy
import com.palmapp.master.baselib.event.GoogleAdvertisingIdEvent
import com.palmapp.master.baselib.event.OnGoogleGoogleAdvertisingIdListener
import com.palmapp.master.baselib.utils.AppUtil
import com.palmapp.master.baselib.utils.VersionController
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

/**
 *
 * @author :     xiemingrui
 * @since :      2019/8/1
 */
class AdModuleApp(application: Application) : BaseModuleApp(application),
    OnGoogleGoogleAdvertisingIdListener {
    @Subscribe
    override fun onGoogleAdvertisingIdEvent(event: GoogleAdvertisingIdEvent) {
        AdSdkApi.setGoogleAdvertisingId(application, event.googleId)
    }

    override fun onCreate() {
        EventBus.getDefault().register(this)
        AdSdkProxy.initAdSDK()

    }

    override fun onTerminate() {
        EventBus.getDefault().unregister(this)
    }

    override fun attachBaseContext() {
    }

    override fun getPriority(): Int {
        return 1
    }

    override fun onBuyChannelUpdate() {
        val buyChannel = BuyChannelProxy.getBuyChannelBean().buyChannel
        val clientParams = ClientParams(
            buyChannel, AppUtil.getInstalledTime(
                application, application
                    .getPackageName()
            ), !VersionController.isNewUser
        )
        clientParams.useFrom = BuyChannelApi.getBuyChannelBean(application).secondUserType.toString()
        AdSdkApi.setClientParams(application, clientParams)
    }

}
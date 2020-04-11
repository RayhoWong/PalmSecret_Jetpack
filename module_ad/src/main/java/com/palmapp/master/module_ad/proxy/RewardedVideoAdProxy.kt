package com.palmapp.master.module_ad.proxy

import com.google.android.gms.ads.reward.RewardedVideoAd

/**
 *
 * @author :     xiemingrui
 * @since :      2020/1/2
 */
class RewardedVideoAdProxy(val ad: RewardedVideoAd) : IAd {
    override fun getAd(): Any = ad

    override fun show() {
        ad.show()
    }

    override fun destroy() {
        ad.destroy()
    }

    override fun isLoadSuccess(): Boolean {
        return ad.isLoaded
    }

}
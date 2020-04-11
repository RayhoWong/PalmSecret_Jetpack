package com.palmapp.master.baselib.buychannel

import android.text.TextUtils
import com.cs.bd.buychannel.BuyChannelApi
import com.cs.bd.buychannel.BuySdkInitParams
import com.cs.bd.buychannel.buyChannel.bean.BuyChannelBean
import com.cs.bd.commerce.util.LogUtils
import com.palmapp.master.baselib.BuildConfig
import com.palmapp.master.baselib.GoCommonEnv
import com.palmapp.master.baselib.Product
import com.palmapp.master.baselib.constants.PreConstants
import com.palmapp.master.baselib.manager.DebugProxy
import com.palmapp.master.baselib.manager.GoPrefManager
import com.palmapp.master.baselib.manager.ModuleLifeManger
import com.palmapp.master.baselib.manager.config.ConfigManager
import com.palmapp.master.baselib.manager.config.PayConfig
import com.palmapp.master.baselib.statistics.BaseSeq45OperationStatistic
import com.palmapp.master.baselib.utils.AppUtil
import com.palmapp.master.baselib.utils.LogUtil
import com.palmapp.master.baselib.utils.VersionController

/**
 *
 * @author :     xiemingrui
 * @since :      2019/8/1
 */
object BuyChannelProxy {
    const val TAG = "BuyChannelProxy"
    fun initBuyChannelApi() {
        val productKey = "Z8V3B7LJR2NYHVWPCTC99KQ6"
        val accessKey = "79FJ6VTO03QULVOH2P70ANUAO0YLR083"
        //协议相关参数
        val p45FunId = BaseSeq45OperationStatistic.FUNCTION_ID
        //2.初始化买量SDK需要的相关参数
        val builder = BuySdkInitParams.Builder(
            GoCommonEnv.innerChannel, p45FunId,
            "38", BuySdkInitParams.IProtocal19Handler { }, false, productKey, accessKey
        )
        BuyChannelApi.registerBuyChannelListener(GoCommonEnv.getApplication()) {
            LogUtil.d(TAG, "onBuyChannelUpdate=> $it")
            GoCommonEnv.storeOuterChannel(it)
            ModuleLifeManger.onBuyChannelUpdate()
            ConfigManager.getConfig(PayConfig::class.java)?.onBuyChannelUpdate()
        }
        //3.初始化买量SDK
        if (!TextUtils.isEmpty(AppUtil.getCurProcessName(GoCommonEnv.getApplication()))) {
            //进程没被销毁时初始化否则报android.os.DeadObjectException
            LogUtil.d(TAG, "初始化买量sdk")
            BuyChannelApi.init(GoCommonEnv.getApplication(), builder.build())
        }
    }

    /**
     * 判断是否是买量用户
     *
     * @return
     */
    fun isBuyUser(): Boolean {
        try {
            return getBuyChannelBean().isUserBuy
        } catch (e: Exception) {

        }

        return false
    }

    private var isBuyUser = false

    fun setBuyChannelUser() {
        isBuyUser = true
    }

    fun getBuyChannelBean(): BuyChannelBean {
        if (BuildConfig.DEBUG) {
            if (isBuyUser) {
                return getUserBuyChannelBean()
            }
        }
        return BuyChannelApi.getBuyChannelBean(GoCommonEnv.getApplication())
    }

    //写死买量身份，线上包不能调用！
    private fun getUserBuyChannelBean(): BuyChannelBean {
        val buyChannelBean = BuyChannelBean()
        buyChannelBean.setFirstUserType("userbuy")
        buyChannelBean.setSecondUserType(1)
        buyChannelBean.setBuyChannel("appflood")
        buyChannelBean.setSuccessCheck(true)
        buyChannelBean.setChannelFrom("from_ga")
        buyChannelBean.campaign = campaign
        return buyChannelBean
    }

    var campaign = ""
}
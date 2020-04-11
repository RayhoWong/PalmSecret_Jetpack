package com.palmapp.master.baselib.bean

import com.palmapp.master.baselib.GoCommonEnv
import com.palmapp.master.baselib.constants.AppConstants
import com.palmapp.master.baselib.manager.DebugProxy
import com.palmapp.master.baselib.utils.AppUtil
import com.palmapp.master.baselib.utils.MachineUtil
import com.palmapp.master.baselib.utils.VersionController
import java.io.Serializable
import java.util.*
import kotlin.collections.HashMap

/**
 *
 * @author :     xiemingrui
 * @since :      2019/8/2
 */

class Device :Serializable{
    var pkgname: String? = ""
    var country: String? = ""
    var lang: String? = ""
    var cversion: Int? = 0
    var zone_id: String? = ""
    var zone: String? = ""
    var platform: Int = 1
    var did: String? = ""

    companion object {
        fun create(): Device {
            val device = Device()
            device.pkgname = "com.palmsecret.horoscope"
            device.country = MachineUtil.getCountry(GoCommonEnv.getApplication()).toUpperCase()
            device.lang = MachineUtil.getLanguage(GoCommonEnv.getApplication())
            device.cversion = VersionController.getVersionCode()
            device.zone_id = TimeZone.getDefault().getDisplayName(false, TimeZone.SHORT)
            device.zone = device.zone_id
            device.platform = 1
            device.did = MachineUtil.getAndroidId(GoCommonEnv.getApplication())

            return device
        }
    }
}
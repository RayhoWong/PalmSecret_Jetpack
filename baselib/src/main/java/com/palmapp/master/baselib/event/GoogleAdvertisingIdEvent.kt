package com.palmapp.master.baselib.event

import org.greenrobot.eventbus.Subscribe

/**
 *
 * @author :     xiemingrui
 * @since :      2019/8/1
 */
data class GoogleAdvertisingIdEvent(val googleId: String)

interface OnGoogleGoogleAdvertisingIdListener {
    fun onGoogleAdvertisingIdEvent(event: GoogleAdvertisingIdEvent)
}
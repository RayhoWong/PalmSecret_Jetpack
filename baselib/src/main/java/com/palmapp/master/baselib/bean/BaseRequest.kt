package com.palmapp.master.baselib.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 *
 * @author :     xiemingrui
 * @since :      2019/8/2
 */
open class BaseRequest : Serializable {
    var device: Device = Device.create()
}
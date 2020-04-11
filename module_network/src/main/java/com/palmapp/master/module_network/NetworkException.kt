package com.palmapp.master.module_network

import java.lang.Exception

/**
 *
 * @author :     xiemingrui
 * @since :      2019/8/5
 */
const val CODE_TIMEOUT = 1
const val CODE_SERVER_ERROR = 2
const val CODE_UNKNOWN = 3
const val CODE_CUSTOM = 4

class NetworkException(val code:Int,val msg:String) :Exception()
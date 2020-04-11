package com.palmapp.master.baselib.bean

/**
 *
 * @author :     xiemingrui
 * @since :      2019/8/16
 */

class CacheBean<T>(val cacheTime: Long, val data: T) {

    fun isExpired(time: Long) = data == null || System.currentTimeMillis() - cacheTime > time
}
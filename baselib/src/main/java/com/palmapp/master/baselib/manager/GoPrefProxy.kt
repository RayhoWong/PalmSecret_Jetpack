package com.palmapp.master.baselib.manager

import android.content.SharedPreferences
import com.tencent.mmkv.MMKV

/**
 *
 * @ClassName:      GoPrefProxy
 * @Description:
 * @Author:         xiemingrui
 * @CreateDate:     2019/7/23
 */
class GoPrefProxy constructor(name: String, mode: Int) {
    var mMode = mode
    private var mPref: MMKV? = MMKV.mmkvWithID(name, mode)
    private var mEditor: SharedPreferences.Editor? = mPref?.edit()


    fun remove(key: String) {
        mEditor?.remove(key)
    }

    operator fun contains(key: String): Boolean {
        return mPref?.contains(key) ?: false
    }

    fun getBoolean(key: String, defValue: Boolean): Boolean {
        return mPref?.getBoolean(key, defValue) ?: defValue
    }

    fun getFloat(key: String, defValue: Float): Float {
        return mPref?.getFloat(key, defValue) ?: defValue
    }

    fun getInt(key: String, defValue: Int): Int {
        return mPref?.getInt(key, defValue) ?: defValue
    }

    fun getLong(key: String, defValue: Long): Long {
        return mPref?.getLong(key, defValue) ?: defValue
    }

    fun getString(key: String, defValue: String): String {
        return mPref?.getString(key, defValue) ?: defValue
    }

    fun putBoolean(key: String, b: Boolean): GoPrefProxy {
        mEditor?.putBoolean(key, b)
        return this
    }

    fun putInt(key: String, i: Int): GoPrefProxy {
        mEditor?.putInt(key, i)
        return this
    }

    fun putFloat(key: String, f: Float): GoPrefProxy {
        mEditor?.putFloat(key, f)
        return this
    }

    fun putLong(key: String, l: Long): GoPrefProxy {
        mEditor?.putLong(key, l)
        return this
    }

    fun putString(key: String, s: String): GoPrefProxy {
        mEditor?.putString(key, s)
        return this
    }

    fun isFirstTime(key: String): Boolean {
        val result = mPref?.getBoolean(key, true) ?: true
        mEditor?.putBoolean(key, false)?.apply()
        return result
    }

    fun addTimes(key: String, defValue: Int) {
        var value = mPref?.getInt(key, defValue) ?: defValue
        value += 1
        mEditor?.putInt(key, value)?.apply()
    }

    fun commit(): Boolean {
        return mEditor?.commit() ?: false
    }

    fun apply() {
        mEditor?.apply()
    }
}
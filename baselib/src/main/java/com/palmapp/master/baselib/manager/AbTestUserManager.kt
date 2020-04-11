package com.palmapp.master.baselib.manager

import android.text.TextUtils
import com.palmapp.master.baselib.constants.PreConstants
import com.palmapp.master.baselib.utils.LogUtil
import com.palmapp.master.baselib.utils.VersionController
import java.util.*
import kotlin.math.log

/**
 *
 * @author :     xiemingrui
 * @since :      2019/9/20
 */

object AbTestUserManager {
    const val TAG = "AbTestUserManager"
    const val USER_A = "a"
    private const val AB_VERSION_CODE = 23   //新AB开始的版本号
    private val users = arrayListOf(USER_A)
    private var user = ""

    init {
        LogUtil.d("AbTestUserManager",VersionController.getVersionCode().toString())
        if (!isABTestNull()) {
            val version = GoPrefManager.getDefault().getInt(PreConstants.First.KEY_FIRST_TEST_USER_VERSION, 0)
            if (version < AB_VERSION_CODE) {
                GoPrefManager.getDefault().putString(PreConstants.First.KEY_FIRST_TEST_USER, "")
                GoPrefManager.getDefault().putInt(PreConstants.First.KEY_FIRST_TEST_USER_VERSION, VersionController.getVersionCode())
            }
        }
    }

    @Synchronized
    fun init() {
        if (!TextUtils.isEmpty(user)) {
            return
        }
        user = GoPrefManager.getDefault().getString(PreConstants.First.KEY_FIRST_TEST_USER, "")
        if (TextUtils.isEmpty(user)) {
            user = users.shuffled().first()
            GoPrefManager.getDefault().putString(PreConstants.First.KEY_FIRST_TEST_USER, user)
        }
        LogUtil.d(TAG, "使用本地AB,方案标示为：$user")
    }

    fun getTestUser(): String {
        if (TextUtils.isEmpty(user)) {
            init()
        }
        LogUtil.d(TAG, "User == $user")
        return user
    }

    fun isTestUser(u: String): Boolean {
        if (TextUtils.isEmpty(getTestUser())) {
            init()
        }

        return TextUtils.equals(getTestUser(), u)
    }

    //是否分配了AB方案
    fun isABTestNull(): Boolean {
        val user = GoPrefManager.getDefault().getString(PreConstants.First.KEY_FIRST_TEST_USER, "")
        return TextUtils.isEmpty(user)
    }
}
package com.palmapp.master.baselib.manager

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import com.palmapp.master.baselib.utils.LogUtil
import io.reactivex.internal.util.LinkedArrayList
import java.util.*

object ForegroundManager {
    var activityCount = 0
    var blocks = ArrayDeque<() -> Unit>()
    fun runOnForeground(block: () -> Unit) {
        if (isForeground()) {
            block()
        } else {
            addObserver(block)
        }
    }

    private fun addObserver(block: () -> Unit) {
        blocks.add(block)
    }

    private fun notifyObserver() {
        LogUtil.d("ForegroundManager","切换到前台")
        while (blocks.peek() != null) {
            blocks.poll()()
        }
    }

    fun init(application: Application) {
        application.registerActivityLifecycleCallbacks(object :
            Application.ActivityLifecycleCallbacks {
            override fun onActivityPaused(activity: Activity?) {
            }

            override fun onActivityResumed(activity: Activity?) {
            }

            override fun onActivityStarted(activity: Activity?) {
                if (!isForeground()) {
                    notifyObserver()
                }
                activityCount++
            }

            override fun onActivityDestroyed(activity: Activity?) {
            }

            override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
            }

            override fun onActivityStopped(activity: Activity?) {
                activityCount--
            }

            override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
            }

        })
    }

    fun isForeground() = activityCount != 0
}
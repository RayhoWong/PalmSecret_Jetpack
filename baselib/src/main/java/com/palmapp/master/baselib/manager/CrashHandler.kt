package com.palmapp.master.baselib.manager

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageInfo
import android.os.Build
import android.os.Looper
import android.os.Process
import android.util.DisplayMetrics
import android.view.WindowManager
import android.widget.Toast
import com.palmapp.master.baselib.GoCommonEnv
import com.palmapp.master.baselib.utils.AppUtil
import com.palmapp.master.baselib.utils.MachineUtil
import com.palmapp.master.baselib.utils.VersionController
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.LinkedHashMap

/**
 *
 * @author :     xiemingrui
 * @since :      2019/9/19
 */
@SuppressLint("StaticFieldLeak")
class CrashHandler : Thread.UncaughtExceptionHandler {
    private val VERSION_NAME_KEY = "VersionName"
    private val VERSION_CODE_KEY = "VersionCode"
    private val LAST_VERSION_CODE_KEY = "LastVersionCode"
    private val PACKAGE_NAME_KEY = "PackageName"
    private val SVN_CODE = "SVN"
    private val PHONE_MODEL_KEY = "PhoneModel"
    private val ANDROID_VERSION_KEY = "AndroidVersion"
    private val CURRENT_LOCALE = "Locale"
    private val ANDROID_ID = "AndroidId"
    private val DISPLAY = "Display"

    private val NAME_SUFFIX = ".txt"
    private var mDefaultCrashHandler: Thread.UncaughtExceptionHandler? = null
    private lateinit var mContext: Context
    private val mCrashProperties = LinkedHashMap<String, String>()
    fun init(context: Context) {
        //正式包就算了
        mDefaultCrashHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler(this)
        mContext = context
    }

    override fun uncaughtException(t: Thread?, e: Throwable?) {
        e?.printStackTrace()
        val path = mContext.externalCacheDir?.absolutePath?.plus(File.separator).plus("crash")
        val dirs = File(path)
        if (!dirs.exists()) {
            dirs.mkdirs()
        }
        retrieveCrashData(mContext)
        val time = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date().time)
        val file = File(path.plus(File.separator).plus(time).plus(NAME_SUFFIX))
        file.createNewFile()
        try {
            val pw = PrintWriter(BufferedWriter(FileWriter(file)))
            pw.println(time)
            pw.println()
            pw.println("===============设备信息===============")
            mCrashProperties.entries.forEach {
                pw.println(it.key.plus(" = ").plus(it.value))
            }
            pw.println()
            pw.println("===============异常信息===============")
            e?.printStackTrace(pw)
            pw.close()

        } catch (e: Exception) {

        }
        if(DebugProxy.isDebug()) {
            Thread {
                Looper.prepare()
                Toast.makeText(mContext, "崩溃了，日志已经保存：".plus(file.absolutePath), Toast.LENGTH_LONG)
                    .show()
                Looper.loop()
            }.start()
        }
        Thread.sleep(3000)
        if (mDefaultCrashHandler != null) {
            mDefaultCrashHandler?.uncaughtException(t, e)
        } else {
            Process.killProcess(Process.myPid())
        }

    }

    private fun retrieveCrashData(context: Context) {
        mCrashProperties.clear()
        try {
            mCrashProperties.put(PACKAGE_NAME_KEY, context.packageName)
            mCrashProperties.put(VERSION_NAME_KEY, VersionController.getVersionName())
            mCrashProperties.put(VERSION_CODE_KEY, VersionController.getVersionCode().toString())
            mCrashProperties.put(LAST_VERSION_CODE_KEY, VersionController.lastVersionCode.toString())
            mCrashProperties.put(SVN_CODE, GoCommonEnv.svn)
            val locale = mContext.resources.configuration.locale
            if (locale != null) {
                mCrashProperties.put(CURRENT_LOCALE, locale.toString())
            }

            // Device model
            mCrashProperties.put(PHONE_MODEL_KEY, Build.MODEL)
            // Android version
            mCrashProperties.put(ANDROID_VERSION_KEY, Build.VERSION.RELEASE)
            // Android build data
            mCrashProperties.put(ANDROID_ID, MachineUtil.getAndroidId(context))

            val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val dm = DisplayMetrics()
            wm.defaultDisplay.getMetrics(dm)
            mCrashProperties.put(
                DISPLAY,
                "屏幕宽度:${dm.widthPixels} 屏幕高度:${dm.heightPixels} 屏幕密度:${dm.density} 屏幕密度DPI:${dm.densityDpi}"
            )


        } catch (t: Throwable) {
        }

    }
}
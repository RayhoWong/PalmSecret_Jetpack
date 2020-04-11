/***
 * This is free and unencumbered software released into the public domain.
 * <p/>
 * Anyone is free to copy, modify, publish, use, compile, sell, or
 * distribute this software, either in source code form or as a compiled
 * binary, for any purpose, commercial or non-commercial, and by any
 * means.
 * <p/>
 * For more information, please refer to <http://unlicense.org/>
 */

package com.palmapp.master.baselib.utils;

import android.os.SystemClock;
import android.util.Log;
import com.palmapp.master.baselib.BuildConfig;
import com.palmapp.master.baselib.manager.DebugProxy;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;


/**
 * @author zhangliang
 * <p/>
 * Create a simple and more understandable Android logs.
 */

public class LogUtil {

    static String sTAG = "GOPalm";

    static String sClassName;

    static String sMethodName;

    static int sLineNumber;

    static HashMap<String, Long> sTimeTag = new HashMap<>();

    static long sStartTime = 0;

    static String sLog;
    private static boolean sIsDebug = BuildConfig.DEBUG;

    public static final String TAG_XMR = "xmr";
    public static final String TAG_YXQ = "yxq";

    private LogUtil() {
        /* Protect from instantiations */

    }

    private static boolean isDebuggable() {
        //研发人员请勿将修改的值上传至服务器
        return sIsDebug;
    }

    public static void setIsDebug(boolean isDebug) {
        Log.d(TAG_XMR, "isDebug ==" + isDebug);
        sIsDebug = isDebug;
    }

    private static String createLog(String log) {

        return Thread.currentThread().getName() + "[" + sClassName + ":" + sMethodName + ":" + sLineNumber + "]" + log;
    }

    private static void getMethodNames(StackTraceElement[] sElements) {
        sClassName = sElements[1].getFileName();
        sMethodName = sElements[1].getMethodName();
        sLineNumber = sElements[1].getLineNumber();
    }

    public static void e(String message) {
        if (!isDebuggable()) {
            return;
        }

        // Throwable sInstance must be created before any methods
        getMethodNames(new Throwable().getStackTrace());
        Log.e(sTAG, createLog(message));
    }

    public static void e(String tag, String message) {
        if (!isDebuggable()) {
            return;
        }

        // Throwable sInstance must be created before any methods
        getMethodNames(new Throwable().getStackTrace());
        Log.e(tag, createLog(message));
    }

    public static void e(String tag, String msg, Exception e) {
        if (!isDebuggable()) {
            return;
        }
        Log.e(tag, msg, e);
    }

    public static void i(String message) {
        if (!isDebuggable()) {
            return;
        }

        getMethodNames(new Throwable().getStackTrace());
        Log.i(sTAG, createLog(message));
    }

    public static void i(String tag, String message) {
        if (!isDebuggable()) {
            return;
        }

        getMethodNames(new Throwable().getStackTrace());
        Log.i(tag, createLog(message));
    }

    public static void d(String message) {
        if (!isDebuggable()) {
            return;
        }

        getMethodNames(new Throwable().getStackTrace());
        Log.d(sTAG, createLog(message));
    }

    public static void d(String tag, String message) {
        if (!isDebuggable()) {
            return;
        }
        getMethodNames(new Throwable().getStackTrace());
        Log.d(tag, createLog(message));
    }

    public static void v(String message) {
        if (!isDebuggable()) {
            return;
        }

        getMethodNames(new Throwable().getStackTrace());
        Log.v(sTAG, createLog(message));
    }

    public static void w(String message) {
        if (!isDebuggable()) {
            return;
        }

        getMethodNames(new Throwable().getStackTrace());
        Log.w(sTAG, createLog(message));
    }

    public static void wtf(String message) {
        if (!isDebuggable()) {
            return;
        }

        getMethodNames(new Throwable().getStackTrace());
        Log.wtf(sTAG, createLog(message));
    }

    public static void timelog(String message) {
        if (!isDebuggable()) {
            return;
        }
        getMethodNames(new Throwable().getStackTrace());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yy-MM-dd HH:mm:ss.sss");
        Log.d(sTAG, message + " : " + simpleDateFormat.format(new Date()));
    }

    public static void startMoniter() {
        if (!DebugProxy.INSTANCE.isOpenLog())
            return;
        if (sTimeTag.size() > 0) {
            sTimeTag.clear();
        }
        sStartTime = SystemClock.uptimeMillis();
    }

    public static long recodingTime(String tag) {
        if (!DebugProxy.INSTANCE.isOpenLog())
            return -1;
        sTimeTag.remove(tag);
        long time = SystemClock.uptimeMillis() - sStartTime;
        sTimeTag.put(tag, time);
        return time;
    }

    public static void showTimeData(String tag) {
        if (!DebugProxy.INSTANCE.isOpenLog())
            return;
        if (sTimeTag.isEmpty()) {
            d(tag, "time log is empty");
            return;
        }
        Iterator<String> iterator = sTimeTag.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            d(tag, key + " : " + sTimeTag.get(key));
        }
    }
}

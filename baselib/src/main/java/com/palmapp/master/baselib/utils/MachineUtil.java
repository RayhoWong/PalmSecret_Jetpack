package com.palmapp.master.baselib.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import com.palmapp.master.baselib.GoCommonEnv;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * a
 *
 * @author jiangxuwen
 */
// CHECKSTYLE:OFF
public class MachineUtil {

    /**
     * <br>
     * 功能简述:获取Android ID的方法 <br>
     * 功能详细描述: <br>
     * 注意:
     *
     * @return
     */
    public static String getAndroidId(Context context) {
        String androidId = null;
        if (context != null) {
            androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        return androidId;
    }

    /**
     * 获取国家
     *
     * @param context
     * @return
     */
    public static String getCountry(Context context) {
        String ret = null;

        try {
            TelephonyManager telManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (telManager != null) {
                ret = telManager.getSimCountryIso().toLowerCase();

                if (telManager.getSimState() == TelephonyManager.SIM_STATE_ABSENT) {
                    ret = null;
                }

            }
        } catch (Throwable e) {
            // e.printStackTrace();
        }
        if (ret == null || ret.equals("")) {
            Locale locale = context.getResources().getConfiguration().locale;
            ret = locale.getCountry().toLowerCase();
        }
        return TextUtils.isEmpty(ret) ? "us" : ret;
    }


    /**
     * 获取当前的语言
     *
     * @param context
     * @return
     * @author zhoujun
     */
    public static String getLanguage(Context context) {
        String language = context.getResources().getConfiguration().locale.getLanguage().toLowerCase();
        if (TextUtils.equals(language, "zh") && !TextUtils.equals(getCountry(context), "cn")) {
            return language + "-TW";
        }
        return language;
    }

    /**
     * 获取当前的语言
     *
     * @param context
     * @return
     * @author zhoujun
     */
    public static String getLanguageConf(Context context) {
        String language = context.getResources().getConfiguration().locale.getLanguage().toLowerCase();
        if (TextUtils.equals(language, "zh") && !TextUtils.equals(getCountry(context), "cn")) {
            return language + "_TW";
        }
        return language;
    }

    /**
     * 获取当前的语言
     *
     * @param context
     * @return
     * @author zhoujun
     */
    public static String getLanguageOldCnt(Context context) {
        String language = context.getResources().getConfiguration().locale.getLanguage().toUpperCase();
        return language;
    }


    /**
     * 判断当前网络是否可以使用
     *
     * @param context
     * @return
     * @author huyong
     */
    public static boolean isNetworkOK(Context context) {
        boolean result = false;
        if (context != null) {
            try {
                ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                if (cm != null) {
                    NetworkInfo networkInfo = cm.getActiveNetworkInfo();
                    if (networkInfo != null && networkInfo.isConnected()) {
                        result = true;
                    }
                }
            } catch (NoSuchFieldError e) {
                e.printStackTrace();
            }
        }

        return result;
    }
}

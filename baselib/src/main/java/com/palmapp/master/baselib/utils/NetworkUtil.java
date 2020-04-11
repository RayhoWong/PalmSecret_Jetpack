package com.palmapp.master.baselib.utils;

/**
 * @创建者 Demon
 * @创建时间 2016/12/12
 * @描述 ${网络判断工具类}
 *
 */

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import java.util.Date;
import java.util.Random;

import static android.content.Context.TELEPHONY_SERVICE;

public class NetworkUtil {

    private NetworkUtil() {
        throw new UnsupportedOperationException("u can't fuck me...");
    }

    public static final int NETWORK_WIFI    = 1;    // wifi network
    public static final int NETWORK_4G      = 4;    // "4G" networks
    public static final int NETWORK_3G      = 3;    // "3G" networks
    public static final int NETWORK_2G      = 2;    // "2G" networks
    public static final int NETWORK_UNKNOWN = 5;    // unknown network
    public static final int NETWORK_NO      = -1;   // no network

    private static final int NETWORK_TYPE_GSM      = 16;
    private static final int NETWORK_TYPE_TD_SCDMA = 17;
    private static final int NETWORK_TYPE_IWLAN    = 18;

    /**
     * 打开网络设置界面
     * <p>3.0以下打开设置界面</p>
     *
     * @param context 上下文
     */
    public static void openWirelessSettings(Context context) {
        if (Build.VERSION.SDK_INT > 10) {
            context.startActivity(new Intent(Settings.ACTION_SETTINGS));
        } else {
            context.startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
        }
    }

    /**
     * 获取活动网络信息
     *
     * @param context 上下文
     * @return NetworkInfo
     */
    private static NetworkInfo getActiveNetworkInfo(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo();
    }

    /**
     * 判断网络是否可用
     * <p>需添加权限 {@code <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>}</p>
     *
     * @param context 上下文
     * @return {@code true}: 可用<br>{@code false}: 不可用
     */
    public static boolean isAvailable(Context context) {
        NetworkInfo info = getActiveNetworkInfo(context);
        return info != null && info.isAvailable();
    }

    /**
     * 判断网络是否连接
     * <p>需添加权限 {@code <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>}</p>
     *
     * @param context 上下文
     * @return {@code true}: 是<br>{@code false}: 否
     */
    public static boolean isConnected(Context context) {
        NetworkInfo info = getActiveNetworkInfo(context);
        return info != null && info.isConnected();
    }

    /**
     * 判断网络是否是4G
     * <p>需添加权限 {@code <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>}</p>
     *
     * @param context 上下文
     * @return {@code true}: 是<br>{@code false}: 不是
     */
    public static boolean is4G(Context context) {
        NetworkInfo info = getActiveNetworkInfo(context);
        return info != null && info.isAvailable() && info.getSubtype() == TelephonyManager.NETWORK_TYPE_LTE;
    }

    /**
     * 判断wifi是否连接状态
     * <p>需添加权限 {@code <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>}</p>
     *
     * @param context 上下文
     * @return {@code true}: 连接<br>{@code false}: 未连接
     */
    public static boolean isWifiConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm != null && cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo()
                                                                    .getType() == ConnectivityManager.TYPE_WIFI;
    }

    /**
     * 获取移动网络运营商名称
     * <p>如中国联通、中国移动、中国电信</p>
     *
     * @param context 上下文
     * @return 移动网络运营商名称
     */
    public static String getNetworkOperatorName(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
        return tm != null
               ? tm.getNetworkOperatorName()
               : null;
    }

    /**
     * 获取移动终端类型
     *
     * @param context 上下文
     * @return 手机制式
     * <ul>
     * <li>{@link TelephonyManager#PHONE_TYPE_NONE } : 0 手机制式未知</li>
     * <li>{@link TelephonyManager#PHONE_TYPE_GSM  } : 1 手机制式为GSM，移动和联通</li>
     * <li>{@link TelephonyManager#PHONE_TYPE_CDMA } : 2 手机制式为CDMA，电信</li>
     * <li>{@link TelephonyManager#PHONE_TYPE_SIP  } : 3</li>
     * </ul>
     */
    public static int getPhoneType(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
        return tm != null
               ? tm.getPhoneType()
               : -1;
    }


    /**
     * 获取当前的网络类型(WIFI,2G,3G,4G)
     * <p>需添加权限 {@code <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>}</p>
     *
     * @param context 上下文
     * @return 网络类型
     * <ul>
     * <li>{@link #NETWORK_WIFI   } = 1;</li>
     * <li>{@link #NETWORK_4G     } = 4;</li>
     * <li>{@link #NETWORK_3G     } = 3;</li>
     * <li>{@link #NETWORK_2G     } = 2;</li>
     * <li>{@link #NETWORK_UNKNOWN} = 5;</li>
     * <li>{@link #NETWORK_NO     } = -1;</li>
     * </ul>
     */
    public static int getNetWorkType(Context context) {
        int         netType = NETWORK_NO;
        NetworkInfo info    = getActiveNetworkInfo(context);
        if (info != null && info.isAvailable()) {

            if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                netType = NETWORK_WIFI;
            } else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                switch (info.getSubtype()) {

                    case NETWORK_TYPE_GSM:
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                    case TelephonyManager.NETWORK_TYPE_IDEN:
                        netType = NETWORK_2G;
                        break;

                    case NETWORK_TYPE_TD_SCDMA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B:
                    case TelephonyManager.NETWORK_TYPE_EHRPD:
                    case TelephonyManager.NETWORK_TYPE_HSPAP:
                        netType = NETWORK_3G;
                        break;

                    case NETWORK_TYPE_IWLAN:
                    case TelephonyManager.NETWORK_TYPE_LTE:
                        netType = NETWORK_4G;
                        break;
                    default:

                        String subtypeName = info.getSubtypeName();
                        if (subtypeName.equalsIgnoreCase("TD-SCDMA") || subtypeName.equalsIgnoreCase(
                                "WCDMA") || subtypeName.equalsIgnoreCase("CDMA2000"))
                        {
                            netType = NETWORK_3G;
                        } else {
                            netType = NETWORK_UNKNOWN;
                        }
                        break;
                }
            } else {
                netType = NETWORK_UNKNOWN;
            }
        }
        return netType;
    }

    /**
     * 获取当前的网络类型(WIFI,2G,3G,4G)
     * <p>依赖上面的方法</p>
     *
     * @param context 上下文
     * @return 网络类型名称
     * <ul>
     * <li>NETWORK_WIFI   </li>
     * <li>NETWORK_4G     </li>
     * <li>NETWORK_3G     </li>
     * <li>NETWORK_2G     </li>
     * <li>NETWORK_UNKNOWN</li>
     * <li>NETWORK_NO     </li>
     * </ul>
     */
    public static String getNetWorkTypeName(Context context) {
        switch (getNetWorkType(context)) {
            case NETWORK_WIFI:
                return "NETWORK_WIFI";
            case NETWORK_4G:
                return "NETWORK_4G";
            case NETWORK_3G:
                return "NETWORK_3G";
            case NETWORK_2G:
                return "NETWORK_2G";
            case NETWORK_NO:
                return "NETWORK_NO";
            default:
                return "NETWORK_UNKNOWN";
        }
    }

    /**
     * 获取当前时间戳,精确到毫秒
     * @return
     */
    public static String getCurrentTimestamp() {
        return String.valueOf(new Date().getTime());
    }

    /**
     * 唯一的设备标识
     * @return
     */
    //    public static String getMobileDeviceId() {
    //
    //
    //        String m_szImei = getImei_ID();
    //        String m_szDevIDShort = getUnique_ID();
    ////        String m_szWLANMAC = getWLAN_MAC();
    //        String m_szAndroidID = getAndroidID();
    //        String m_szBTMAC = getBT_MAC();
    //        String m_szLongID = m_szImei + m_szDevIDShort + m_szAndroidID + m_szWLANMAC + m_szBTMAC;
    //
    //        // compute md5
    //        MessageDigest m = null;
    //        try {
    //            m = MessageDigest.getInstance("MD5");
    //        } catch (NoSuchAlgorithmException e) {
    //            e.printStackTrace();
    //        }
    //        m.update(m_szLongID.getBytes(), 0, m_szLongID.length());
    //        // get md5 bytes
    //        byte p_md5Data[] = m.digest();
    //        // create a hex string
    //        String m_szUniqueID = new String();
    //        for (int i = 0; i < p_md5Data.length; i++) {
    //            int b = (0xFF & p_md5Data[i]);
    //            // if it is a single digit, make sure it have 0 in front (proper padding)
    //            if (b <= 0xF) { m_szUniqueID += "0"; }
    //            // add number to string
    //            m_szUniqueID += Integer.toHexString(b);
    //        }   // hex string to uppercase
    //        m_szUniqueID = m_szUniqueID.toUpperCase();
    //        return m_szUniqueID;
    //
    //    }

//    /**
//     * 获取蓝牙地址
//     * @return
//     */
//    private static String getBT_MAC() {
//        BluetoothAdapter m_BluetoothAdapter = null; // Local Bluetooth adapter
//        m_BluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//        if (m_BluetoothAdapter == null) {
//            return "";
//        }
//        String m_szBTMAC = m_BluetoothAdapter.getAddress();
//        return m_szBTMAC;
//    }

    /**
     * 获取Android ID
     * @return
     */
    /*private static String getAndroidID() {
        String m_szAndroidID = Settings.Secure.getString(App.getInstance()
                                                               .getContentResolver(),
                                                         Settings.Secure.ANDROID_ID);
        return m_szAndroidID;
    }*/

    //    /**
    //     * 获取WLAN MAC地址
    //     * @return
    //     */
    //    private static String getWLAN_MAC() {
    //        WifiManager wm = (WifiManager) UiUtil.getContext()
    //                                             .getSystemService(Context.WIFI_SERVICE);
    //        String m_szWLANMAC = wm.getConnectionInfo()
    //                               .getMacAddress();
    //        return m_szWLANMAC;
    //    }

    /**
     * 获取Pseudo-Unique ID, 这个在任何Android手机中都有效
     * @return
     */
    private static String getUnique_ID() {
        String m_szDevIDShort = "35" + //we make this look like a valid IMEI

                Build.BOARD.length() % 10 + Build.BRAND.length() % 10 + Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10 + Build.DISPLAY.length() % 10 + Build.HOST.length() % 10 + Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10 + Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10 + Build.TAGS.length() % 10 + Build.TYPE.length() % 10 + Build.USER.length() % 10; //13 digits
        return m_szDevIDShort;
    }

    /**
     * 获取设备的The IMEI: 仅仅只对Android手机有效:
     * @return
     */
    /*private static String getImei_ID() {
        TelephonyManager TelephonyMgr = (TelephonyManager) App.getInstance()
                                                                 .getSystemService(TELEPHONY_SERVICE);
        String szImei = TelephonyMgr.getDeviceId();
        return szImei;
    }*/

    /**
     * 请求参数签名
     * @param
     * @param
     * @param
     * @param md5json
     */
    //    public static String getSignature(String md5json, String timestamp, String random) {
    //        StringBuffer sb2 = new StringBuffer();
    //        //B.把时间戳，随机数，客户端唯一标示，token字符串，
    //        // 已经A中获取的字符串一次从左至右拼接成一个字符串
    //        String mobileDeviceId = getMobileDeviceId();
    //        String signToken = SpTools.getString("SignToken", "");
    //        String s2 = sb2.append(timestamp)
    //                       .append(random)
    //                       .append(mobileDeviceId)
    //                       .append(signToken)
    //                       .append(md5json)
    //                       .toString()
    //                       .replace("{", "")
    //                       .replace("\"","")
    //                       .replace("}", "")
    //                       .replace(",", "")
    //                       .replace("\\", "")
    //                       .replace(":", "");
    //        //C.	把B中字符串升序排列成另一个字符串。
    //        StringBuffer sb3 = new StringBuffer();
    //        char[] array = s2.toCharArray();
    //        for (int i = 0; i < array.length; i++) {
    //            for (int j = 0; j < i; j++) {
    //                if (array[i] < array[j]) {
    //                    char temp = array[i];
    //                    array[i] = array[j];
    //                    array[j] = temp;
    //                }
    //            }
    //        }
    //        //array排序升序完成,
    //        for (int i = 0; i < array.length; i++) {
    //            sb3.append(array[i]);
    //        }
    //        String s3 = sb3.toString();
    //        System.out.println("设备号是" + mobileDeviceId);
    //        System.out.println("随机数是" + random);
    //        System.out.println("时间戳是" + timestamp);
    //        System.out.println("token是" + signToken);
    //        System.out.println("升序排列" + s3);
    //        System.out.println("排序前的" + s2);
    //        System.out.println("json是" + md5json);
    //        //D.把C中字符串转成UTF8编码的二进制。
    //        //        String binaryUTF8 = StringUtils.getBinaryUTF8(s3);
    //        //        System.out.println("生成的MD5是"+binaryUTF8);
    //        //E.把D中二进制MD5加密,并全部转换为大写32为字符串。
    //        String md5Encode = "";
    //        try {
    //            md5Encode = Md5Utils.MD5(s3);
    //        } catch (Exception e) {
    //            e.printStackTrace();
    //        }
    //        //此时map是已经排序好的
    //        System.out.println("digest生成" + md5Encode);
    //        System.out.println("md5:" + md5Encode);
    //        return md5Encode;
    //    }

    /**
     * 获取随机数
     * @return
     */
    public static int getRandom() {
        int random = new Random().nextInt();
        int abs    = Math.abs(random);
        return abs;
    }

}
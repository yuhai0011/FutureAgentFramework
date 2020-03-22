package com.futureagent.lib.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

/**
 * @author skywalker
 * @date 15/12/18
 * @description
 * @Email: yuhai833@126.com
 */
public class NetWorkUtil {

    // 常量
    public static final String TYPE_WIFI = "wifi";
    public static final String TYPE_2G = "2g";
    public static final String TYPE_3G = "3g";
    public static final String TYPE_4G = "4g";
    public static final String TYPE_UNKNOWN = "unknown";

    /**
     * 获取网络连接类型
     *
     * @param context
     * @return 默认为 TYPE_UNKNOWN
     */
    public static String getConnectType(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context
                .CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info == null || info.isAvailable()) {
            return TYPE_UNKNOWN;
        } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {
            return TYPE_WIFI;
        } else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
            int sub = info.getSubtype();
            switch (sub) {
                case TelephonyManager.NETWORK_TYPE_GPRS:
                case TelephonyManager.NETWORK_TYPE_EDGE:
                case TelephonyManager.NETWORK_TYPE_CDMA:
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                case TelephonyManager.NETWORK_TYPE_IDEN:

                    //以上的都是2G网络
                    return TYPE_2G;

                case TelephonyManager.NETWORK_TYPE_UMTS:
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                case TelephonyManager.NETWORK_TYPE_HSPA:
                case TelephonyManager.NETWORK_TYPE_EVDO_B:
                case TelephonyManager.NETWORK_TYPE_EHRPD:
                case TelephonyManager.NETWORK_TYPE_HSPAP:

                    //以上的都是3G网络
                    return TYPE_3G;

                case TelephonyManager.NETWORK_TYPE_LTE:
                    return TYPE_4G;

                default:
                    return TYPE_UNKNOWN;
            }
        } else {
            return TYPE_UNKNOWN;
        }
    }


    public static boolean isMobileNetWork(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context
                .CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();

        if (info != null && info.getType() == ConnectivityManager.TYPE_MOBILE) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取运营商
     *
     * @param context
     * @return
     */
    public static String getMNO(Context context) {
        TelephonyManager telManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        String operator = telManager.getSimOperator();

        if (operator != null) {

            if (operator.equals("46000") || operator.equals("46002") || operator.equals("46007")) {
                return "移动";
            } else if (operator.equals("46001")) {
                return "联通";
            } else if (operator.equals("46003")) {
                return "电信";
            } else {
                return "unknown";
            }
        } else {
            return "unknown";
        }
    }

    /**
     * 判断网络是否连接
     *
     * @param context
     * @return
     */
    public static boolean isConnected(Context context) {

        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (null != connectivity) {

            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (null != info && info.isConnected()) {
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断是否是wifi连接
     *
     * @param context
     * @return
     */
    public static boolean isWifi(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm == null)
            return false;
        return cm.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_WIFI;

    }

    /**
     * 打开网络设置界面
     *
     * @param activity
     */
    public static void openSetting(Activity activity) {
        Intent intent = new Intent("/");
        ComponentName cm = new ComponentName("com.android.settings",
                "com.android.settings.WirelessSettings");
        intent.setComponent(cm);
        intent.setAction("android.intent.action.VIEW");
        activity.startActivityForResult(intent, 0);
    }
    
}

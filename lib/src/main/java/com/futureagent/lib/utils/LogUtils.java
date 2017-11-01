
package com.futureagent.lib.utils;

import android.util.Log;

/**
 * Log统一管理类
 */
public class LogUtils {
    private static String TAG = "Future-Agent";
    private static boolean DEBUG = true;// 是否需要打印bug，可以在application的onCreate函数里面初始化

    private LogUtils()
    {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    public static void setTag(String tag) {
        TAG = tag;
    }

    public static void setDebug(boolean debug) {
        DEBUG = debug;
    }

    // 下面四个是默认tag的函数
    public static void i(String msg)
    {
        if (DEBUG)
            Log.i(TAG, msg);
    }

    public static void d(String msg)
    {
        if (DEBUG)
            Log.i(TAG, msg);
    }

    public static void e(String msg)
    {
        if (DEBUG)
            Log.e(TAG, msg);
    }

    public static void v(String msg)
    {
        if (DEBUG)
            Log.v(TAG, msg);
    }

    // 下面是传入自定义tag的函数
    public static void i(String tag, String msg)
    {
        if (DEBUG) {
            Log.i(TAG, getLogMsg(tag, msg));
        }
    }

    public static void d(String tag, String msg)
    {
        if (DEBUG) {
            Log.i(TAG, getLogMsg(tag, msg));
        }
    }

    public static void w(String tag, String msg)
    {
        if (DEBUG) {
            Log.w(TAG, getLogMsg(tag, msg));
        }
    }

    public static void w(String tag, String msg, Exception e)
    {
        if (DEBUG) {
            Log.w(TAG, getLogMsg(tag, msg + e.getMessage()));
        }
    }

    public static void e(String tag, String msg)
    {
        if (DEBUG) {
            Log.e(TAG, getLogMsg(tag, msg));
        }
    }

    public static void e(String tag, String msg, Exception e)
    {
        if (DEBUG) {
            Log.e(TAG, getLogMsg(tag, msg + e.getMessage()));
        }
    }

    public static void v(String tag, String msg)
    {
        if (DEBUG) {
            Log.i(TAG, getLogMsg(tag, msg));
        }
    }

    private static String getLogMsg(String tag, String msg) {
        return "[" + tag + "] " + msg;
    }
}

package com.futureagent.lib.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * @author skywalker
 * @date 15/12/18
 * @description
 * @Email: yuhai833@126.com
 */
public class PrefManager {

    public static void putString(Context mContext, String key, String values) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        sp.edit().putString(key, values).apply();
    }

    public static String getString(Context mContext, String key) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        return sp.getString(key, "");
    }

    public static String getString(Context mContext, String key, String defaultValue) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        return sp.getString(key, defaultValue);
    }

    public static void putBoolean(Context mContext, String key, Boolean values) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        sp.edit().putBoolean(key, values).apply();

    }

    public static void putFloat(Context mContext, String key, float values) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        sp.edit().putFloat(key, values).apply();

    }

    public static float getFloat(Context mContext, String key, float values) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        return sp.getFloat(key, values);
    }

    public static boolean getBoolean(Context mContext, String key) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        return sp.getBoolean(key, false);
    }

    public static boolean getBoolean(Context mContext, String key, boolean defaultValue) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        return sp.getBoolean(key, defaultValue);
    }


    public static void putLong(Context context, String key, long value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putLong(key, value).apply();
    }

    public static long getLong(Context context, String key) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getLong(key, 0L);
    }

    public static long getLong(Context context, String key, long defaultValue) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getLong(key, defaultValue);
    }

    public static void putInt(Context context, String key, int value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putInt(key, value).apply();
    }

    public static int getInt(Context context, String key) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getInt(key, 0);
    }

    public static int getInt(Context context, String key, int defaultValue) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getInt(key, defaultValue);
    }
}

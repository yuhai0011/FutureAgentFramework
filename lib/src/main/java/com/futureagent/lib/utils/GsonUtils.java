package com.futureagent.lib.utils;

import com.google.gson.Gson;

/**
 * 获取gson对象单例
 */
public class GsonUtils {

    private static final Gson gson = new Gson();

    private GsonUtils() {
    }

    public static final Gson getGsonInstance() {
        return gson;
    }
}

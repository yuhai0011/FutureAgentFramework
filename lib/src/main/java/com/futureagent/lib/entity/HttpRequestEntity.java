package com.futureagent.lib.entity;

import android.content.Context;

import java.util.Date;
import java.util.HashMap;


/**
 * Created by dangt on 15/12/4.
 * <p/>
 * http请求的输入参数类
 */
public class HttpRequestEntity {

    // 固定要传的4个参数
    private String timestamp;
    private String sign;

    // 固定参数的常量key
    private final static String KEY_TIMESTAMP = "_timestamp";
    private HashMap<String, String> paramsMap;

    /**
     * @param context
     */
    public HttpRequestEntity(Context context, HashMap hashMap) {
        if (hashMap == null) {
            paramsMap = new HashMap<>();
        } else {
            paramsMap = hashMap;
        }
        timestamp = String.valueOf(new Date().getTime());

        paramsMap.put(KEY_TIMESTAMP, timestamp);
    }

    /**
     * 生成http 参数
     *
     * @return
     */
    public HashMap getParams() {
        return paramsMap;
    }

}

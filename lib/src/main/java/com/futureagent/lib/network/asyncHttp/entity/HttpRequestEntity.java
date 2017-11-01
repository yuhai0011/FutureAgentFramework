package com.futureagent.lib.network.asyncHttp.entity;

import android.content.Context;

import com.futureagent.lib.utils.IdManager;
import com.futureagent.lib.utils.LogHelper;
import com.futureagent.lib.utils.Md5Manager;
import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.HashMap;


/**
 * Created by dangt on 15/12/4.
 * <p>
 * http请求的输入参数类
 */
public class HttpRequestEntity {

    // 固定要传的4个参数
    private String timestamp;
    private String token = "";
    private String sign;
    private String data;
    private String client;
    private String vno;
    private String deviceid;
    private String channel;

    // 固定参数的常量key
    private final static String KEY_TIMESTAMP = "timestamp";
    private final static String KEY_TOKEN = "token";
    private final static String KEY_SIGN = "sign";
    private final static String KEY_DATA = "param";
    private final static String KEY_CLIENT = "client";
    private final static String KEY_VNO = "vno";
    private final static String KEY_DEVICEID = "deviceid";
    private final static String KEY_CHANNEL = "channel";

    // 可选
    private final static String KEY_FILE = "file";

    // 构造方法

    /**
     * @param context
     */
    public HttpRequestEntity(Context context, HashMap hashMap) {
        String key = "12345678";
        String account = "";
        timestamp = String.valueOf(new Date().getTime());
        sign = getSign(key, account, token, timestamp);
        data = new Gson().toJson(hashMap);
        client = "android";
        vno = "1.3.0";
        deviceid = IdManager.getDeviceId(context);
        channel = "test";
    }

    /**
     * 生成http 参数
     *
     * @return
     */
    public RequestParams getParams() {
        RequestParams params = new RequestParams();

        // 固定的4个参数
        params.put(KEY_TIMESTAMP, timestamp);
        params.put(KEY_TOKEN, token);
        params.put(KEY_SIGN, sign);
        params.put(KEY_DATA, data);
        params.put(KEY_CLIENT, client);
//        params.put(KEY_VNO, vno);
        params.put(KEY_DEVICEID, deviceid);
        params.put(KEY_CHANNEL, channel);

        return params;
    }

    /**
     * 生成http 参数（带file的）
     *
     * @param file
     * @return
     */
    public RequestParams getParams(File... file) {
        RequestParams params = new RequestParams();

        // 固定的4个参数
        params.put(KEY_TIMESTAMP, timestamp);
        params.put(KEY_TOKEN, token);
        params.put(KEY_SIGN, sign);
        params.put(KEY_DATA, data);
        params.put(KEY_CLIENT, client);
        params.put(KEY_VNO, vno);
        params.put(KEY_DEVICEID, deviceid);
        params.put(KEY_CHANNEL, channel);
        try {
            if (file.length == 1) {
                params.put(KEY_FILE, file[0]);
            } else {
                params.put(KEY_FILE, file);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            LogHelper.e("", "头像文件为空");
        }

        return params;
    }

    /**
     * 生成sign
     *
     * @param key
     * @param account
     * @param token
     * @param timestamp
     * @return
     */
    private String getSign(String key, String account, String token, String timestamp) {
        return Md5Manager.MD5(key + account + token + timestamp);
    }
}

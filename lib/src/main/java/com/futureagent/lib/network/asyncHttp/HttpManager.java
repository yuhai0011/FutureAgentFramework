package com.futureagent.lib.network.asyncHttp;

import android.content.Context;
import android.text.TextUtils;

import com.futureagent.lib.config.URLConfig;
import com.futureagent.lib.network.asyncHttp.Interface.IHttp;
import com.futureagent.lib.network.asyncHttp.entity.HttpRequestEntity;
import com.futureagent.lib.network.asyncHttp.handler.BaseGsonHttpResondHandler;
import com.futureagent.lib.network.asyncHttp.handler.GsonHttpResponseHandler;
import com.futureagent.lib.utils.LogUtils;
import com.futureagent.lib.utils.NetWorkUtil;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

import java.io.File;
import java.util.HashMap;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * @author skywalker
 * @date 15/12/18
 * @description
 * @Email: yuhai833@126.com
 */
public class HttpManager implements IHttp {
    private static final String TAG = "HttpManager";

    private static final int MAX_CONNECT_TIME_OUT = 8000;
    private static final int MAX_RESPOND_TIME_OUT = 8000;

    // 变量
    private AsyncHttpClient asyncHttpClient;
    private SyncHttpClient httpClient;

    private HttpManager() {
    }

    public static final HttpManager getInstance() {
        return HttpManagerHolder.INSTANCE;
    }

    // get/set 方法
    public AsyncHttpClient getAsyncHttpClient() {
        if (asyncHttpClient != null) {
            return asyncHttpClient;
        }
        asyncHttpClient = new AsyncHttpClient(true, 80, 443);
        asyncHttpClient.setTimeout(MAX_CONNECT_TIME_OUT);
        asyncHttpClient.setResponseTimeout(MAX_RESPOND_TIME_OUT);
        return asyncHttpClient;
    }

    public SyncHttpClient getHttpClient() {
        if (httpClient == null) {
            httpClient = new SyncHttpClient(true, 80, 443);
            httpClient.setTimeout(MAX_CONNECT_TIME_OUT);
        }
        return httpClient;
    }

    /**
     * 获取封装好的参数对象
     *
     * @param hashMap
     * @return
     */
    private RequestParams getParams(Context context, HashMap hashMap) {
        return new HttpRequestEntity(context, hashMap).getParams();
    }

    /**
     * 获取封装好的参数对象（带file的）
     *
     * @param context
     * @param hashMap
     * @param file
     * @return
     */
    private RequestParams getParams(Context context, HashMap hashMap, File... file) {
        return new HttpRequestEntity(context, hashMap).getParams(file);
    }

    /**
     * 获取http请求的url（拼接上前缀）
     *
     * @param url
     * @return
     */
    private String getUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            url = "";
        }
        String urlStr = URLConfig.getUrlHostSsl() + url;
        LogUtils.e(TAG, "getUrl urlStr:" + urlStr);
        return urlStr;
    }

    /**
     * IHttp interface
     *
     * @param context
     * @param url
     * @param params
     * @param callback
     * @return
     */
    @Override
    public RequestHandle httpPost(Context context, String url, HashMap params,
                                  GsonHttpResponseHandler callback) {

        // 参数检查
        if (context == null) {
            LogUtils.e(TAG, "httpPost context is null");
            return null;
        }

        // 判断网络是否连接
        if (NetWorkUtil.isConnected(context)) {
            RequestParams requestParams = getParams(context, params);
            Gson gson = new Gson();
            String paramJson = gson.toJson(requestParams, RequestParams.class);
            HttpEntity httpEntity = new StringEntity(paramJson, "utf-8");
            return getAsyncHttpClient().post(
                    context,
                    getUrl(url),
                    httpEntity,
                    "application/x-www-form-urlencoded",
                    callback
            );
        } else {
            LogUtils.e(TAG, "httpPost NetWorkUtil is not connect");
            if (callback != null) {
                callback.onNoConnect();
            }
            return null;
        }
    }


    // 接口实现

    /**
     * http 上传单文件
     *
     * @param context
     * @param url
     * @param params
     * @param file
     * @param callback
     * @return
     */
    public RequestHandle httpPost(Context context, String url, HashMap params, File file,
                                  GsonHttpResponseHandler callback) {
        return httpPost(context, url, params, callback, file);
    }

    /**
     * 上传多个文件
     *
     * @param context
     * @param url
     * @param params
     * @param file
     * @param callback
     * @return
     */
    public RequestHandle httpPost(Context context, String url, HashMap params, File[] file,
                                  GsonHttpResponseHandler callback) {
        return httpPost(context, url, params, callback, file);
    }

    /**
     * 上传文件（单文件或者文件数组）
     *
     * @param context
     * @param url
     * @param params
     * @param callback
     * @param file
     * @return
     */
    private RequestHandle httpPost(Context context, String url, HashMap params,
                                   GsonHttpResponseHandler callback, File... file) {
        // 参数检查
        if (context == null) {
            return null;
        }

        // 判断网络是否连接
        if (NetWorkUtil.isConnected(context)) {

            return getAsyncHttpClient().post(
                    context,
                    getUrl(url),
                    getParams(context, params, file),
                    callback
            );
        } else {
            if (callback != null) {
                callback.onNoConnect();
            }
            return null;
        }
    }

    /**
     * http get
     *
     * @param context
     * @param url
     * @param params
     * @param callback
     * @return
     */
    public RequestHandle httpGet(Context context, String url, HashMap<String, String> params,
                                 BaseGsonHttpResondHandler callback) {
        // 参数检查
        if (context == null) {
            return null;
        }

        StringBuffer buffer = new StringBuffer(url);
        if (params != null) {
            int i = 0;
            for (String key : params.keySet()) {
                if (i == 0) {
                    buffer.append("?").append(key).append("=").append(params.get(key));
                } else {
                    buffer.append("&").append(key).append("=").append(params.get(key));
                }
                i++;

            }
        }
        // 判断网络是否连接
        if (NetWorkUtil.isConnected(context)) {
            return getAsyncHttpClient().get(
                    context, getUrl(buffer.toString()), callback);
        } else {
            if (callback != null) {
                callback.onNoConnect();
            }
            return null;
        }
    }

    // 单例模式
    private static class HttpManagerHolder {
        private static final HttpManager INSTANCE = new HttpManager();
    }
}

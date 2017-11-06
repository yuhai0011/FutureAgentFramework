package com.futureagent.lib.network;

import android.content.Context;
import android.support.annotation.NonNull;

import com.futureagent.lib.BuildConfig;
import com.futureagent.lib.config.URLConstant;
import com.futureagent.lib.entity.HttpRequestEntity;
import com.futureagent.lib.network.handler.IGsonHttpResonsedHandler;
import com.futureagent.lib.utils.LogUtils;
import com.futureagent.lib.utils.NetWorkUtil;
import com.futureagent.lib.utils.ThreadUtils;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by skywalker on 2017/5/9.
 * Email: skywalker@thecover.co
 * Description:
 */

public class HttpManager {
    private static final String TAG = "HttpManager";

    private HttpManager() {
    }

    public static final HttpManager getInstance() {
        return HttpManagerHolder.INSTANCE;
    }

    /**
     * 获取封装好的参数对象
     *
     * @param hashMap
     * @return
     */
    private HashMap getBodyParams(Context context, HashMap hashMap) {
        return new HttpRequestEntity(context, hashMap).getParams();
    }

    private HashMap getUrlParams(HashMap<String, Object> hashMap) {
        HashMap<String, Object> urlParamMap = new HashMap<>();
        urlParamMap.put("vno", BuildConfig.VERSION_NAME);
        if (hashMap != null && hashMap.size() > 0) {
            for (String key : hashMap.keySet()) {
                urlParamMap.put(key, hashMap.get(key));
            }
        }
        return urlParamMap;
    }

    private boolean isLogin() {
        return false;
    }

    private String getLoginToken() {
        try {
            return "";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 统一http post请求
     *
     * @param context 上下文
     * @param url     接口
     * @param params  接口参数map
     * @param handler 请求回调
     */
    public Call<ResponseBody> httpPost(@NonNull Context context, @NonNull String url, HashMap params, @NonNull final IGsonHttpResonsedHandler handler) {

        // 手机没有网络
        if (!NetWorkUtil.isConnected(context)) {
            if (handler != null) {
                handler.onNoConnect();
            }
            return null;
        }

        // 手机有网络
        else {
            ThreadUtils.getInstance().runInUIThread(new Runnable() {
                @Override
                public void run() {
                    if (handler != null) {
                        handler.onStart();
                    }
                }
            });

            ApiService repo = getApiService();
            Call<ResponseBody> call;
            if (isLogin()) {
                call = repo.httpPost(url, getLoginToken(), getUrlParams(null), getBodyParams(context, params));
            } else {
                call = repo.httpPost(url, getUrlParams(null), getBodyParams(context, params));
            }
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response) {
                    dealResponse(handler, response);
                }

                @Override
                public void onFailure(Call<ResponseBody> call, final Throwable t) {
                    LogUtils.e(TAG, "Exception:" + t.getMessage());
                    dealFailure(handler, t);
                }
            });

            return call;
        }
    }

    /**
     * 统一http get请求
     *
     * @param context 上下文
     * @param url     接口
     * @param params  接口参数map
     * @param handler 请求回调
     */
    public Call<ResponseBody> httpGet(@NonNull Context context, @NonNull String url, HashMap params, @NonNull final IGsonHttpResonsedHandler handler) {

        // 手机没有网络
        if (!NetWorkUtil.isConnected(context)) {
            handler.onNoConnect();
            return null;
        } else {// 手机有网络
            ThreadUtils.getInstance().runInUIThread(new Runnable() {
                @Override
                public void run() {
                    if (handler != null) {
                        handler.onStart();
                    }
                }
            });

            ApiService repo = getApiService();
            Call<ResponseBody> call;
            if (isLogin()) {
                call = repo.httpGet(url, getLoginToken(), getUrlParams(getBodyParams(context, params)));
            } else {
                call = repo.httpGet(url, getUrlParams(getBodyParams(context, params)));
            }
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response) {
                    dealResponse(handler, response);
                }

                @Override
                public void onFailure(Call<ResponseBody> call, final Throwable t) {
                    LogUtils.e(TAG, "Exception:" + t.getMessage());
                    dealFailure(handler, t);
                }
            });

            return call;
        }
    }

    /**
     * 获取网络请求通用接口
     *
     * @return
     */
    private ApiService getApiService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URLConstant.getUrlHostSsl())
                .build();
        return retrofit.create(ApiService.class);
    }

    /**
     * 处理服务端返回
     *
     * @param handler
     * @param response
     */
    private void dealResponse(final IGsonHttpResonsedHandler handler, final Response<ResponseBody> response) {
        ThreadUtils.getInstance().runInUIThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (handler != null) {
                        if (response == null) {
                            Throwable throwable = new Throwable("response is null");
                            handler.onFailure(404, throwable);
                        } else {
                            handler.onSuccess(response.code(), response.toString(), handler.parseResponse(response.body().string(), !response.isSuccessful()));
                        }
                    }
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
                if (handler != null) {
                    handler.onFinish();
                }
            }
        });
    }

    /**
     * 处理异常
     *
     * @param handler
     * @param t
     */
    private void dealFailure(final IGsonHttpResonsedHandler handler, final Throwable t) {
        ThreadUtils.getInstance().runInUIThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (handler != null) {
                        handler.onFailure(IGsonHttpResonsedHandler.STATUS_NET_ERROR, t);
                    }
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
                if (handler != null) {
                    handler.onFinish();
                }
            }
        });
    }

    // 单例模式
    private static class HttpManagerHolder {
        private static final HttpManager INSTANCE = new HttpManager();
    }
}

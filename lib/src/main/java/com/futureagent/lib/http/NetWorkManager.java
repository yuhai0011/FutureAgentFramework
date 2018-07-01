package com.futureagent.lib.http;

import android.content.Context;
import android.support.annotation.NonNull;

import com.futureagent.lib.config.ConstantsConfig;
import com.futureagent.lib.entity.HttpRequestEntity;
import com.futureagent.lib.http.handler.IHttpResonsedHandler;
import com.futureagent.lib.network.FaSSLSocketFactory;
import com.futureagent.lib.utils.NetWorkUtil;
import com.futureagent.lib.utils.ThreadUtils;

import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * @author skywalker on 2018/7/1.
 * Email: skywalker@thecover.co
 * Description:
 */
public class NetWorkManager {
    private static final String TAG = "NetWorkManager";

    private NetWorkManager() {
    }

    public static final NetWorkManager getInstance() {
        return NetWorkManagerHolder.INSTANCE;
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

    public HashMap getUrlParams(HashMap<String, Object> hashMap) {
        HashMap<String, Object> urlParamMap = new HashMap<>();
        if (hashMap != null && hashMap.size() > 0) {
            for (String key : hashMap.keySet()) {
                urlParamMap.put(key, hashMap.get(key));
            }
        }
        return urlParamMap;
    }

    /**
     * 统一http post请求
     *
     * @param context 上下文
     * @param baseUrl 域名
     * @param method  接口
     * @param params  接口参数map
     * @param handler 请求回调
     */
    public Call<ResponseBody> httpPost(@NonNull Context context, @NonNull String baseUrl, @NonNull String method, JSONObject params, @NonNull final IHttpResonsedHandler handler) {

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

            NetApiService repo = getApiService(baseUrl);
            Call<ResponseBody> call;
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), params.toString());

            call = repo.httpPost(method, getUrlParams(null), requestBody);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response) {
                    dealResponse(handler, response);
                }

                @Override
                public void onFailure(Call<ResponseBody> call, final Throwable t) {
                    dealFailure(handler, t);
                }
            });
            if (handler != null) {
                handler.onCall(call);
            }
            return call;
        }
    }

    /**
     * 统一http get请求
     *
     * @param context 上下文
     * @param baseUrl 域名
     * @param method  接口
     * @param params  接口参数map
     * @param handler 请求回调
     */
    public Call<ResponseBody> httpGet(@NonNull Context context, @NonNull String baseUrl, @NonNull String method, HashMap params, @NonNull final IHttpResonsedHandler handler) {

        // 手机没有网络
        if (!NetWorkUtil.isConnected(context)) {
            handler.onNoConnect();
            return null;
        }
        // 手机有网络
        ThreadUtils.getInstance().runInUIThread(new Runnable() {
            @Override
            public void run() {
                if (handler != null) {
                    handler.onStart();
                }
            }
        });

        NetApiService repo = getApiService(baseUrl);
        Call<ResponseBody> call;
        call = repo.httpGet(method, getUrlParams(getBodyParams(context, params)));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response) {
                dealResponse(handler, response);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, final Throwable t) {
                dealFailure(handler, t);
            }
        });
        if (handler != null) {
            handler.onCall(call);
        }
        return call;
    }

    /**
     * 获取网络请求通用接口
     *
     * @return
     */
    private NetApiService getApiService(String host) {
        OkHttpClient okhttpclient = new OkHttpClient.Builder()
                .sslSocketFactory(FaSSLSocketFactory.createSSLSocketFactory(), FaSSLSocketFactory.createTrustAllManager())
                .hostnameVerifier(new FaSSLSocketFactory.TrustAllHostnameVerifier())
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(host)
                .client(okhttpclient)
                .build();
        return retrofit.create(NetApiService.class);
    }

    /**
     * 处理服务端返回
     *
     * @param handler
     * @param response
     */
    private void dealResponse(final IHttpResonsedHandler handler,
                              final Response<ResponseBody> response) {
        ThreadUtils.getInstance().runInUIThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (handler != null) {
                        if (response == null || response.body() == null) {
                            Throwable throwable = new Throwable("response is null");
                            handler.onFailure(ConstantsConfig.HTTP_STATUS_NO_FOUND, throwable, "network error");
                        } else if (response.code() == ConstantsConfig.HTTP_STATUS_OK) {
                            handler.onSuccess(response.code(), response.toString());
                        } else {
                            Throwable throwable = new Throwable("server error");
                            handler.onFailure(response.code(), throwable, response.toString());
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
    private void dealFailure(final IHttpResonsedHandler handler, final Throwable t) {
        ThreadUtils.getInstance().runInUIThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (handler != null) {
                        handler.onFailure(IHttpResonsedHandler.STATUS_NET_ERROR, t, "network error");
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
     * 单例模式
     */
    private static class NetWorkManagerHolder {
        private static final NetWorkManager INSTANCE = new NetWorkManager();
    }
}

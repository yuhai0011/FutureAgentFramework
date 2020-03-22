package com.futureagent.lib.network;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;

import com.futureagent.lib.config.URLConstant;
import com.futureagent.lib.entity.HttpRequestEntity;
import com.futureagent.lib.network.body.FileRequestBody;
import com.futureagent.lib.network.body.FileResponseBody;
import com.futureagent.lib.network.callback.RetrofitCallback;
import com.futureagent.lib.network.handler.IGsonHttpResonsedHandler;
import com.futureagent.lib.utils.IdManager;
import com.futureagent.lib.utils.LogUtils;
import com.futureagent.lib.utils.NetWorkUtil;
import com.futureagent.lib.utils.ThreadUtils;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by skywalker on 2017/5/9.
 * Email: skywalker@thecover.co
 * Description:
 */

public class HttpManager {
    private static final String TAG = "HttpManager";

    private final static String KEY_TIMESTAMP = "timestamp";
    private final static String KEY_SIGN = "sign";
    private final static String KEY_DATA = "data";
    private final static String KEY_CLIENT = "client";
    private final static String KEY_VC = "vc";
    private final static String KEY_VN = "vn";
    private final static String KEY_DEVICEID = "deviceid";
    private final static String KEY_CHANNEL = "channel";

    private static HttpManager sInstance;

    private Context mContext;
    private int vc = 0;
    private String vn = "";
    private String deviceId = "";

    private HttpManager(Context context) {
        mContext = context.getApplicationContext();

        try {
            PackageInfo packageInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
            vc = packageInfo.versionCode;
            vn = packageInfo.versionName;
            deviceId = IdManager.getDeviceId(mContext);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            vc = -1;
            vn = "0.0";
            deviceId = "deviceId";
        }
    }

    public static HttpManager getInstance(Context context) {
        if (sInstance == null) {
            synchronized (HttpManager.class) {
                if (sInstance == null) {
                    sInstance = new HttpManager(context);
                }
            }
        }
        return sInstance;
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
        urlParamMap.put(KEY_VC, vc);
        urlParamMap.put(KEY_VN, vn);
        urlParamMap.put(KEY_DEVICEID, deviceId);
        urlParamMap.put(KEY_CLIENT, "android");
        urlParamMap.put(KEY_CHANNEL, "test");
        urlParamMap.put(KEY_TIMESTAMP, System.currentTimeMillis());

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
    public Call<ResponseBody> httpPost(@NonNull Context context, @NonNull String url, JSONObject params, @NonNull final IGsonHttpResonsedHandler handler) {

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
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), params.toString());
            if (isLogin()) {
                call = repo.httpPost(url, getLoginToken(), getUrlParams(null), requestBody);
            } else {

                call = repo.httpPost(url, getUrlParams(null), requestBody);
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
            if (handler != null) {
                handler.onCall(call);
            }
            return call;
        }
    }

    /**
     * 统一http post请求上传文件
     *
     * @param context 上下文
     * @param url     接口
     * @param files    文件列表
     * @param handler 请求回调
     */
    public void httpUploadFile(@NonNull Context context, @NonNull String url, File[] files, @NonNull final IGsonHttpResonsedHandler handler) {

        // 手机没有网络
        if (!NetWorkUtil.isConnected(context)) {
            if (handler != null) {
                handler.onNoConnect();
            }
            return;
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

            for (File file : files) {
                RetrofitCallback<ResponseBody> callback = new RetrofitCallback<ResponseBody>() {
                    @Override
                    public void onSuccess(Call<ResponseBody> call, Response<ResponseBody> response) {
                        dealResponse(handler, response);
                        //进度更新结束
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        LogUtils.e(TAG, "Exception:" + t.getMessage());
                        dealFailure(handler, t);
                    }

                    @Override
                    public void onLoading(long total, long progress) {
                        //此处进行进度更新
                        if (handler != null) {
                            handler.onLoading(total, progress);
                        }
                    }
                };
                RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                MultipartBody.Part bodyPart =
                        MultipartBody.Part.createFormData("file", file.getName(), requestBody);
                //通过该行代码将RequestBody转换成特定的FileRequestBody
                FileRequestBody body = new FileRequestBody(requestBody, callback);
                Call<ResponseBody> call = repo.httpUploadFile(url, body, bodyPart);
                call.enqueue(callback);

                if (handler != null) {
                    handler.onCall(call);
                }
            }
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
            if (handler != null) {
                handler.onCall(call);
            }
            return call;
        }
    }

    /**
     * 统一http 下载文件
     *
     * @param context 上下文
     * @param fileUrl 接口
     * @param handler 请求回调
     */
    public Call<ResponseBody> httpDownFile(@NonNull Context context, @NonNull String fileUrl, @NonNull final File localFile, @NonNull final IGsonHttpResonsedHandler handler) {

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
            RetrofitCallback<ResponseBody> callback = new RetrofitCallback<ResponseBody>() {
                @Override
                public void onSuccess(Call<ResponseBody> call, Response<ResponseBody> response) {
                    final boolean result = writeResponseBodyToDisk(response.body(), localFile);
                    ThreadUtils.getInstance().runInUIThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (result) {
                                    handler.onSuccess(200, localFile);
                                } else {
                                    Throwable throwable = new Throwable("response error");
                                    handler.onFailure(-1, throwable, localFile);
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
                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    LogUtils.e(TAG, "Exception:" + t.getMessage());
                    dealFailure(handler, t);
                }

                @Override
                public void onLoading(long total, long progress){
                    //更新下载进度
                    if (handler != null) {
                        handler.onLoading(total, progress);
                    }
                }

            };

            Call<ResponseBody> call = getRetrofitService(callback).httpDownFile(fileUrl);
            call.enqueue(callback);
            if (handler != null) {
                handler.onCall(call);
            }
            return call;
        }
    }

    private <T> ApiService getRetrofitService(final RetrofitCallback<T> callback) {
        OkHttpClient okhttpclient = new OkHttpClient.Builder()
                .sslSocketFactory(FaSSLSocketFactory.createSSLSocketFactory(), FaSSLSocketFactory.createTrustAllManager())
                .hostnameVerifier(new FaSSLSocketFactory.TrustAllHostnameVerifier())
                .addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        okhttp3.Response response = chain.proceed(chain.request());
                        //将ResponseBody转换成我们需要的FileResponseBody
                        return response.newBuilder().body(new FileResponseBody<T>(response.body(), callback)).build();
                    }
                })
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getHostUrl())
                .client(okhttpclient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(ApiService.class);
    }

    /**
     * 获取网络请求通用接口
     *
     * @return
     */
    private ApiService getApiService() {
        OkHttpClient okhttpclient = new OkHttpClient.Builder()
                .sslSocketFactory(FaSSLSocketFactory.createSSLSocketFactory(), FaSSLSocketFactory.createTrustAllManager())
                .hostnameVerifier(new FaSSLSocketFactory.TrustAllHostnameVerifier())
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getHostUrl())
                .client(okhttpclient)
                .build();
        return retrofit.create(ApiService.class);
    }

    private boolean writeResponseBodyToDisk(ResponseBody body, File file) {
        try {
            InputStream inputStream = null;
            OutputStream outputStream = null;
            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(file);

                while (true) {
                    int read = inputStream.read(fileReader);
                    if (read == -1) {
                        break;
                    }
                    outputStream.write(fileReader, 0, read);
                    fileSizeDownloaded += read;
                    LogUtils.d(TAG, "file download: " + fileSizeDownloaded + " of " + fileSize);
                }
                outputStream.flush();
                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                    inputStream = null;
                }
                if (outputStream != null) {
                    outputStream.close();
                    outputStream = null;
                }
            }
        } catch (Exception e) {
            return false;
        }
    }

    protected String getHostUrl() {
        return URLConstant.getUrlHostSsl();
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
                        if (response == null || response.body() == null) {
                            Throwable throwable = new Throwable("response is null");
                            handler.onFailure(404, throwable, "network error", null);
                        } else if (response.code() == 200) {
                            handler.onSuccess(response.code(), response.toString(), handler.parseResponse(response.body().string(), !response.isSuccessful()));
                        } else {
                            Throwable throwable = new Throwable("server error");
                            handler.onFailure(response.code(), throwable, response.toString(), handler.parseResponse(response.body().string(), true));
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
                        handler.onFailure(IGsonHttpResonsedHandler.STATUS_NET_ERROR, t, "network error", null);
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
}

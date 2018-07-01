package com.futureagent.lib.http.handler;

import com.futureagent.lib.protocol.INoConnect;

import okhttp3.ResponseBody;
import retrofit2.Call;

public abstract class IHttpResonsedHandler<T>
        implements
        INoConnect {

    public static final int STATUS_NO_CONNECT = -111222333;
    public static final int STATUS_NET_ERROR = -111222334;


    // 构造方法
    public IHttpResonsedHandler() {

    }

    @Override
    public void onNoConnect() {

    }

    public void onStart() {

    }

    public void onSuccess(int statusCode, String rawJsonResponse) {

    }

    public void onFailure(int statusCode, Throwable throwable, String rawJsonData) {

    }

    public void onCancel() {

    }

    public void onFinish() {

    }

    public void onCall(Call<ResponseBody> call){

    }
}

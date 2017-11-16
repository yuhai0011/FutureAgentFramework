package com.futureagent.lib.network.handler;

import com.futureagent.lib.entity.HttpResponseEntity;
import com.futureagent.lib.protocol.INoConnect;

import java.io.File;
import java.lang.reflect.ParameterizedType;

public abstract class IGsonHttpResonsedHandler<T>
        implements
        INoConnect {

    public static final int STATUS_NO_CONNECT = -111222333;
    public static final int STATUS_NET_ERROR = -111222334;

    Class<T> type;

    // 构造方法
    public IGsonHttpResonsedHandler() {
        try {
            type = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass())
                    .getActualTypeArguments()[0];
        } catch (Exception e) {
            type = null;
        }
    }

    @Override
    public void onNoConnect() {

    }

    public void onStart() {

    }

    public void onSuccess(int statusCode, String rawJsonResponse,
                          HttpResponseEntity<T> response) {

    }

    public void onSuccess(int statusCode, File file) {

    }

    public void onFailure(int statusCode, Throwable throwable, String rawJsonData, HttpResponseEntity<T> response) {

    }

    public void onFailure(int statusCode, Throwable throwable, File file) {

    }

    public void onCancel() {

    }

    public void onFinish() {

    }

    public HttpResponseEntity<T> parseResponse(String rawJsonData, boolean isFailure) throws
            Throwable {

        HttpResponseEntity httpResponseEntity = new HttpResponseEntity();
        httpResponseEntity.setData(rawJsonData);
        httpResponseEntity.parseData(type);

        return httpResponseEntity;
    }
}

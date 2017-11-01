package com.futureagent.lib.network.asyncHttp.handler;

import com.futureagent.lib.network.asyncHttp.Interface.INoConnect;
import com.futureagent.lib.network.asyncHttp.entity.HttpResponseEntity;
import com.loopj.android.http.BaseJsonHttpResponseHandler;

import java.lang.reflect.ParameterizedType;

import cz.msebera.android.httpclient.Header;

/**
 * @author bo_siu
 * @date 16/1/29
 * @description
 * @Email: zhibt_com@163.com
 */
public class BaseGsonHttpResondHandler<T> extends BaseJsonHttpResponseHandler<HttpResponseEntity<T>>
        implements
        INoConnect {

    Class<T> type;

    // 构造方法
    public BaseGsonHttpResondHandler() {
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

    @Override
    public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse,
                          HttpResponseEntity<T> response) {

    }

    @Override
    public void onFailure(int statusCode, Header[] headers, Throwable throwable, String
            rawJsonData, HttpResponseEntity<T> errorResponse) {

    }

    @Override
    protected HttpResponseEntity<T> parseResponse(String rawJsonData, boolean isFailure) throws
            Throwable {

        HttpResponseEntity httpResponseEntity = new HttpResponseEntity();
        httpResponseEntity.setData(rawJsonData);
        httpResponseEntity.parseData(type);

        return httpResponseEntity;

    }
}

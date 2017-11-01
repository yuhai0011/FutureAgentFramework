package com.futureagent.lib.network.asyncHttp.handler;

/**
 * Created by dangt on 15/12/4.
 * <p>
 * http的回调处理类
 */

import android.content.Context;

import com.futureagent.lib.network.asyncHttp.Interface.INoConnect;
import com.futureagent.lib.network.asyncHttp.entity.HttpResponseEntity;
import com.loopj.android.http.BaseJsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.ParameterizedType;

import cz.msebera.android.httpclient.Header;


public class GsonHttpResponseHandler<T> extends BaseJsonHttpResponseHandler<HttpResponseEntity<T>>
        implements INoConnect {

    private static final int STATUS_NO_CONNECT = -111222333;
    /**
     * 变量
     */
    //

    private Context context;

    Class<T> type;

    // 构造方法
    public GsonHttpResponseHandler(Context context) {
        this.context = context;
        try {
            type = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass())
                    .getActualTypeArguments()[0];
        } catch (Exception e) {
            type = null;
        }
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
        LogUtils.d("skywalker", "GsonHttpResponseHandler parseResponse rawJsonData:" + rawJsonData);
        HttpResponseEntity httpResponseEntity = new HttpResponseEntity();
        try {
            JSONObject jsonObject = new JSONObject(rawJsonData);
            int status = jsonObject.getInt("status");
            String message = jsonObject.getString("message");
            String data = jsonObject.optString("data", "");

            httpResponseEntity.setStatus(status);
            httpResponseEntity.setMessage(message);
            httpResponseEntity.setData(data);

            httpResponseEntity.parseData(type);
        } catch (JSONException e) {
            LogUtils.w("GsonHttpResponseHandler", "GsonHttpResponseHandler parseResponse json解析失败");
        }
        return httpResponseEntity;
    }

    @Override
    public void onNoConnect() {

        onFailure(STATUS_NO_CONNECT, null, null, null, null);
        onFinish();
    }
}

package com.futureagent.lib.network.handler;

import android.content.Context;

import com.futureagent.lib.R;
import com.futureagent.lib.entity.HttpResponseEntity;
import com.futureagent.lib.protocol.INoConnect;
import com.futureagent.lib.utils.DialogUtils;
import com.futureagent.lib.utils.LogUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.ParameterizedType;

/**
 * @author  by skywalker
 *
 * http的回调处理类
 */
public class GsonHttpResonsedHandler<T> extends IGsonHttpResonsedHandler<T>
        implements INoConnect {

    private static final String TAG = "GsonHttpResonsedHandler";
    /**
     * 变量
     */
    //

    private Context context;

    private Class<T> type;

    // 构造方法
    public GsonHttpResonsedHandler(Context context) {
        this.context = context;
        try {
            type = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass())
                    .getActualTypeArguments()[0];
        } catch (Exception e) {
            type = null;
        }
    }

    @Override
    public void onSuccess(int statusCode, String rawJsonResponse,
                          HttpResponseEntity<T> response) {
        LogUtils.e(TAG, "request onSuccess");
        LogUtils.e(TAG, rawJsonResponse);

        if (response == null && context != null) {
            DialogUtils.showToast(context, context.getString(R.string.http_server_fail));
        }
    }

    @Override
    public void onFailure(int statusCode, Throwable throwable, String rawJsonData, HttpResponseEntity<T> response) {
        LogUtils.e(TAG, "request onFailure");

        if (statusCode != STATUS_NET_ERROR && context != null) {
            DialogUtils.showToast(context, context.getString(R.string.http_connect_fail));
        }
    }

    @Override
    public void onFinish() {
        super.onFinish();

        LogUtils.d("request", "onFinish");
    }

    @Override
    public HttpResponseEntity<T> parseResponse(String rawJsonData, boolean isFailure) throws
            Throwable {

        HttpResponseEntity httpResponseEntity = new HttpResponseEntity();

        try {

            JSONObject jsonObject = new JSONObject(rawJsonData);

            int status = jsonObject.getInt("status");
            String message = jsonObject.getString("msg");
            String data = jsonObject.optString("data", "");

            httpResponseEntity.setStatus(status);
            httpResponseEntity.setMessage(message);
            httpResponseEntity.setData(data);

            httpResponseEntity.parseData(type);

            return httpResponseEntity;
        } catch (JSONException e) {
            LogUtils.e("GsonHttpResonsedHandler parseResponse json解析失败");
        } catch (Exception e) {
            LogUtils.e("GsonHttpResonsedHandler parseResponse:" + e.getMessage());
        } finally {
            return httpResponseEntity;
        }
    }

    @Override
    public void onNoConnect() {
        onStart();

        if (context != null) {
            DialogUtils.showToast(context, context.getString(R.string.http_no_connect));
        }

        onFailure(STATUS_NO_CONNECT, null, "", null);
        onFinish();
    }

    @Override
    public void onCancel() {
        super.onCancel();

        onFinish();
    }
}

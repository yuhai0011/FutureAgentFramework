package com.futureagent.lib.http.handler;

import android.content.Context;
import android.text.TextUtils;

import com.futureagent.lib.R;
import com.futureagent.lib.protocol.INoConnect;
import com.futureagent.lib.utils.DialogUtils;
import com.futureagent.lib.utils.LogUtils;

/**
 * @author skywalker
 * <p>
 * http的回调处理类
 */
public class HttpResonsedHandler<T> extends IHttpResonsedHandler<T>
        implements INoConnect {

    private static final String TAG = "HttpResonsedHandler";
    private Context context;

    /**
     * 构造方法
     *
     * @param context
     */
    public HttpResonsedHandler(Context context) {
        this.context = context;
    }

    @Override
    public void onSuccess(int statusCode, String rawJsonResponse) {
        LogUtils.e(TAG, "request onSuccess");
        LogUtils.e(TAG, rawJsonResponse);

        if (TextUtils.isEmpty(rawJsonResponse) && context != null) {
            DialogUtils.showToast(context, context.getString(R.string.http_server_fail));
        }
    }

    @Override
    public void onFailure(int statusCode, Throwable throwable, String rawJsonData) {
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
    public void onNoConnect() {
        onStart();
        if (context != null) {
            DialogUtils.showToast(context, context.getString(R.string.http_no_connect));
        }
        onFailure(STATUS_NO_CONNECT, null, "");
        onFinish();
    }

    @Override
    public void onCancel() {
        super.onCancel();
        onFinish();
    }
}

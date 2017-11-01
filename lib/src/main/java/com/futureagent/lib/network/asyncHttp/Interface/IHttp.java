package com.futureagent.lib.network.asyncHttp.Interface;

import android.content.Context;

import com.futureagent.lib.network.asyncHttp.handler.GsonHttpResponseHandler;
import com.loopj.android.http.RequestHandle;

import java.util.HashMap;

/**
 * @author skywalker
 * @date 15/12/18
 * @description
 * @Email: yuhai833@126.com
 */
public interface IHttp {
    RequestHandle httpPost(Context context, String url, HashMap params, GsonHttpResponseHandler
            callback);

}

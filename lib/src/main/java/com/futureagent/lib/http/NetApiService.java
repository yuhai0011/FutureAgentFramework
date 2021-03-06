package com.futureagent.lib.http;


import java.util.HashMap;
import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

/**
 * @author skywalker on 2017/5/9.
 * Email: skywalker@thecover.co
 * Description:
 */
public interface NetApiService {
    @GET("{url}")
    @Headers({"Content-type:application/json;charset=UTF-8"})
    Call<ResponseBody> httpGet(
            @Path("url") String url,
            @QueryMap Map<String, String> maps);

    @POST("{url}")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<ResponseBody> httpPost(
            @Path("url") String url,
            @QueryMap HashMap<String, Object> param,
            @Body RequestBody route);
}

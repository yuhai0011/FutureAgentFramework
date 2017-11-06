package com.futureagent.lib.network;

import com.squareup.okhttp.ResponseBody;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

/**
 * Created by skywalker on 2017/5/9.
 * Email: skywalker@thecover.co
 * Description:
 */
public interface ApiService {
    @GET("{url}")
    @Headers({"Content-type:application/x-www-form-urlencoded;charset=UTF-8"})
    Call<ResponseBody> httpGet(
            @Path("url") String url,
            @Header("Authorization") String authorization,
            @QueryMap Map<String, String> maps);

    @GET("{url}")
    @Headers({"Content-type:application/x-www-form-urlencoded;charset=UTF-8"})
    Call<ResponseBody> httpGet(
            @Path("url") String url,
            @QueryMap Map<String, String> maps);

    @POST("{url}")
    @FormUrlEncoded
    @Headers({"Content-type:application/x-www-form-urlencoded;charset=UTF-8"})
    Call<ResponseBody> httpPost(
            @Path("url") String url,
            @QueryMap HashMap<String, Object> param,
            @FieldMap HashMap<String, Object> entity);

    @POST("{url}")
    @FormUrlEncoded
    @Headers({"Content-type:application/x-www-form-urlencoded;charset=UTF-8"})
    Call<ResponseBody> httpPost(
            @Path("url") String url,
            @Header("Authorization") String authorization,
            @QueryMap HashMap<String, Object> param,
            @FieldMap HashMap<String, Object> entity);
}

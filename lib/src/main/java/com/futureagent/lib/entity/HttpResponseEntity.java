package com.futureagent.lib.entity;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.lang.reflect.Type;

/**
 * Created by dangt on 15/12/4.
 * <p/>
 * http的返回对象
 */
public class HttpResponseEntity<T> {

    public static final Gson gson = new Gson();
    // 返回的描述
    private int status;
    // 返回码
    private String message;
    // 返回的数据
    private String data;
    // data对应的object
    private T object;

    public HttpResponseEntity(int status, String message, String data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public HttpResponseEntity() {
    }

    // get & set 方法
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public T getObject() {
        return object;
    }

    public void setObject(T object) {
        this.object = object;
    }

    /**
     * 把data 解析成 object
     *
     * @param type
     * @return
     */
    public void parseData(Type type) {
        try {
            if (type == null) {
                object = null;
            } else {
                object = gson.fromJson(data, type);
            }
        } catch (JsonSyntaxException e) {
            object = null;
        }
    }
}

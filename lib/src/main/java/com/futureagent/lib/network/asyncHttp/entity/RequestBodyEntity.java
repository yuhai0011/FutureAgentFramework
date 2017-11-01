package com.futureagent.lib.network.asyncHttp.entity;

public class RequestBodyEntity {

    private String param;
    /*********
     * 客户的请求体
     ************/
    private String method = "";// 客户端的请求api

    private RequestBodyEntity data;
    // 请求body
    private long newsId;//最新id

    private long count;//请求个数

    private int channelType;//频道id

    // 请求body

    /*********
     * 客户的请求体
     ************/

    public void setMethod(String method) {
        this.method = method;
    }

    public String getMethod() {
        return this.method;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public String getParam() {
        return this.param;
    }

    public void setData(RequestBodyEntity data) {
        this.data = data;
    }

    public RequestBodyEntity getData() {
        return this.data;
    }

    public void setNewsId(long newsId) {
        this.newsId = newsId;
    }

    public long getNewsId() {
        return this.newsId;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public int getChannelType() {
        return channelType;
    }

    public void setChannelType(int channelType) {
        this.channelType = channelType;
    }
}

package com.futureagent.framework.constant;

import com.futureagent.lib.config.URLConstant;

/**
 * Created by skywalker on 2018/4/7.
 * Email: skywalker@thecover.co
 * Description:
 */
public class URLConfig implements URLConstant.IUrlEnvInterface {

    public static final String URL_ADD = "add";

    @Override
    public String getUrlHostSsl() {
        return "http://ff.91root.cn:8000/script/";
    }
}

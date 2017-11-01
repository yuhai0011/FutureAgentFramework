package com.futureagent.lib.config;

/**
 * @author skywalker
 * @date 15/12/18
 * @description
 * @Email: yuhai833@126.com
 */
public class URLConfig {

    private static IUrlEnvInterface mUrlEnvInterface;

    public static void setDevEnv(IUrlEnvInterface urlEnvInterface) {
        mUrlEnvInterface = urlEnvInterface;
    }

    public static String getUrlHostSsl() {
        if (mUrlEnvInterface == null) {
            throw new RuntimeException("getUrlHostSsl mUrlEnvInterface is null");
        }
        return mUrlEnvInterface.getUrlHostSsl();
    }

    public interface IUrlEnvInterface {
        String getUrlHostSsl();
    }
}

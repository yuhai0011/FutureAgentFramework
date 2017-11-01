
package com.futureagent.lib.network.urlConnHttp;


import com.futureagent.lib.config.LibConfigs;
import com.futureagent.lib.utils.LogUtils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

/**
 * Http请求的工具类
 *
 * @author skywalker
 */
public class HttpUtils {

    private static final boolean DEBUG = LibConfigs.DEBUG_LOG;
    private static final String TAG = "HttpUtils";

    private static final int TIMEOUT_IN_MILLIONS = 5000;

    public interface CallBack {
        void onRequestComplete(String result);
    }

    /**
     * 异步的Get请求
     *
     * @param urlStr
     * @param callBack
     */
    public static void doGetAsync(final String urlStr, final Map<String, String> params, final CallBack callBack) {
        new Thread() {
            public void run() {
                try {
                    String result = doGet(urlStr, params);
                    if (callBack != null) {
                        callBack.onRequestComplete(result);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            ;
        }.start();
    }

    /**
     * 异步的Post请求
     *
     * @param urlStr
     * @param params
     * @param callBack
     * @throws Exception
     */
    public static void doPostAsync(final String urlStr, final String params,
                                   final CallBack callBack) throws Exception {
        new Thread() {
            public void run() {
                try {
                    String result = doPost(urlStr, params);
                    if (callBack != null) {
                        callBack.onRequestComplete(result);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            ;
        }.start();

    }

    /**
     * Get请求，获得返回数据
     *
     * @param urlStr
     * @return
     * @throws Exception
     */
    public static String doGet(String urlStr, Map<String, String> paramsMap) {
        urlStr += parseGetParamMap(paramsMap);
        if (DEBUG) {
            LogUtils.d(TAG, "doGet url:" + urlStr);
        }
        URL url;
        HttpURLConnection conn = null;
        InputStream is = null;
        ByteArrayOutputStream byteArrayOutputStream = null;
        try {
            url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(TIMEOUT_IN_MILLIONS);
            conn.setConnectTimeout(TIMEOUT_IN_MILLIONS);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            if (DEBUG) {
                LogUtils.d(TAG, "doGet conn.getResponseCode:" + conn.getResponseCode());
            }
            if (conn.getResponseCode() == 200) {
                is = conn.getInputStream();
                byteArrayOutputStream = new ByteArrayOutputStream();
                int len;
                byte[] buf = new byte[128];

                while ((len = is.read(buf)) != -1) {
                    byteArrayOutputStream.write(buf, 0, len);
                }
                byteArrayOutputStream.flush();
                return byteArrayOutputStream.toString();
            } else {
                LogUtils.e(TAG, "responseCode code:" + conn.getResponseCode());
            }
        } catch (Exception e) {
            if (DEBUG) {
                LogUtils.e(TAG, "doGet Exception", e);
            }
        } finally {
            try {
                if (is != null)
                    is.close();
            } catch (IOException e) {
                if (DEBUG) {
                    LogUtils.e(TAG, "doGet IOException", e);
                }
            }
            try {
                if (byteArrayOutputStream != null)
                    byteArrayOutputStream.close();
            } catch (IOException e) {
                if (DEBUG) {
                    LogUtils.e(TAG, "doGet IOException", e);
                }
            }
            if (conn != null) {
                conn.disconnect();
            }

        }
        return null;
    }

    private static String parseGetParamMap(Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        if (params != null && params.size() != 0) {
            sb.append("?");
            for (Map.Entry<String, String> entry : params.entrySet()) {
                // 如果请求参数中有中文，需要进行URLEncoder编码 gbk/utf8
                try {
                    sb.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(), "utf-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                sb.append("&");
            }
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url   发送请求的 URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     * @throws Exception
     */
    public static String doPost(String url, String param) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        if (DEBUG) {
            LogUtils.d(TAG, "doPost url:" + url + ", param:" + param);
        }
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            HttpURLConnection conn = (HttpURLConnection) realUrl
                    .openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            conn.setRequestProperty("charset", "utf-8");
            conn.setUseCaches(false);
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setReadTimeout(TIMEOUT_IN_MILLIONS);
            conn.setConnectTimeout(TIMEOUT_IN_MILLIONS);

            if (param != null && !param.trim().equals("")) {
                // 获取URLConnection对象对应的输出流
                out = new PrintWriter(conn.getOutputStream());
                // 发送请求参数
                out.print(param);
                // flush输出流的缓冲
                out.flush();
            }
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 使用finally块来关闭输出流、输入流
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        if (DEBUG) {
            LogUtils.d(TAG, "doPost result:" + result);
        }
        return result;
    }
}

package com.futureagent.lib.utils;

import android.content.Context;
import android.text.TextUtils;

import com.futureagent.lib.config.LibConfigs;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * 异常处理
 *
 * @author skywalker
 * @date 15/12/18
 * @description
 * @Email: yuhai833@126.com
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private static final String TAG = "CrashHandler";
    private static final boolean DEBUG = LibConfigs.DEBUG_LOG;

    private Thread.UncaughtExceptionHandler mDefaultHandler;
    private Context mContext;

    public static CrashHandler newInstance(Context context) {
        return new CrashHandler(context);
    }

    private CrashHandler(Context context) {
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        mContext = context.getApplicationContext();
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (null == ex) {
            return;
        }
        if (DEBUG) {
            LogHelper.e(TAG, "uncaughtException", ex);
        }

        recordCrashInfo(ex);
        // 弹出程序crash的对话框
        mDefaultHandler.uncaughtException(thread, ex);
    }

    private void recordCrashInfo(Throwable e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        pw.flush();

        String stackTrace = sw.toString();
        String cause = e.getMessage();
        Throwable rootTr = e;
        while (e.getCause() != null) {
            e = e.getCause();
            if (e.getStackTrace() != null && e.getStackTrace().length > 0)
                rootTr = e;
            String msg = e.getMessage();
            if (!TextUtils.isEmpty(msg))
                cause = msg;
        }

        String exceptionType = rootTr.getClass().getName();

        String throwClassName;
        String throwMethodName;
        int throwLineNumber;

        if (rootTr.getStackTrace().length > 0) {
            StackTraceElement trace = rootTr.getStackTrace()[0];
            throwClassName = trace.getClassName();
            throwMethodName = trace.getMethodName();
            throwLineNumber = trace.getLineNumber();
        } else {
            throwClassName = "unknown";
            throwMethodName = "unknown";
            throwLineNumber = 0;
        }

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(stackTrace + "\n");
        stringBuilder.append(cause + "\n");
        stringBuilder.append(exceptionType + "\n");

        LogHelper.e(TAG, stringBuilder.toString());
        //TODO save to file
    }

    public void register() {
        Thread.setDefaultUncaughtExceptionHandler(this);
    }
}

package com.futureagent.lib.utils;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author skywalker
 * @date 16-11-7
 * @description
 * @Email: yuhai833@126.com
 */
public class ThreadUtils {

    private static ThreadUtils mInstance = new ThreadUtils();
    private ExecutorService mExecutor = Executors.newCachedThreadPool();

    private ThreadUtils() {
    }

    public static ThreadUtils getInstance() {
        return mInstance;
    }

    public synchronized void runInThread(Runnable runnable) {
        if (mExecutor != null && !mExecutor.isShutdown()) {
            mExecutor.execute(runnable);
        }
    }

    public synchronized void runInUIThread(Runnable runnable) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(runnable);
    }

    public synchronized void runInUIThreadPost(Runnable runnable, long delay) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(runnable, delay);
    }

    public synchronized void destroy() {
        if (mExecutor != null && !mExecutor.isShutdown()) {
            mExecutor.shutdown();
        }
    }
}

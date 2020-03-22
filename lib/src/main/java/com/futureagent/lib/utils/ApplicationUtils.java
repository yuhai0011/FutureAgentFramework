
package com.futureagent.lib.utils;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

public class ApplicationUtils {
    private static Application sApplication;
    private static Handler sHandler;

    public static void initApplication(Application app) {
        sApplication = app;
        sHandler = new Handler();
    }

    public static Context getApplicationContext() {
        return sApplication;
    }

    public static void post(Runnable r) {
        sHandler.post(r);
    }

    public static void postDelayed(Runnable r, long delayMillis) {
        sHandler.postDelayed(r, delayMillis);
    }

    public static void toast(final CharSequence msg, final int duration) {
        post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), msg, duration).show();
            }
        });
    }
}

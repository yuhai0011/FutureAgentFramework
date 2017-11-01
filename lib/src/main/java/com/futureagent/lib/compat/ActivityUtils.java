
package com.futureagent.lib.compat;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.ActivityManager.AppTask;
import android.app.ActivityManager.RecentTaskInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.util.Pair;

import com.futureagent.lib.config.LibConfigs;
import com.futureagent.lib.utils.Constants;
import com.futureagent.lib.utils.LogHelper;
import com.futureagent.lib.utils.SysManagerUtils;

import java.util.List;

public class ActivityUtils {

    private static final String TAG = "ActivityUtils";
    private static final boolean DEBUG = LibConfigs.DEBUG_LOG;

    public static ComponentName getTopActivity(ActivityManager am) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return null; // no permission to visit running tasks
        }

        List<RunningTaskInfo> taskList = null;
        try {
            taskList = am.getRunningTasks(1);
            if (taskList != null && taskList.size() > 0) {
                ComponentName cname = taskList.get(0).topActivity;
                return cname;
            }
        } catch (Exception e) {
            // should not be here, but in some system, the getRunningTasks will
            // fail with crash...
        }
        return null;
    }

    public static boolean isTopActivity(String className, Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName componentName = getTopActivity(am);
        if (componentName != null) {
            return componentName.getClassName().contains(className);
        }

        return false;
    }

    private static ComponentName getTopActivityV20(ActivityManager am) {
        try {
            List<RunningTaskInfo> taskList = am.getRunningTasks(1);
            if (taskList != null && taskList.size() > 0) {
                return taskList.get(0).topActivity;
            } else {
                if (DEBUG) {
                    LogHelper.d(TAG, "getTopActivityV20 taskList is null or empty");
                }
            }
        } catch (Exception e) {
            // should not be here, but in some system, the getRunningTasks will
            // fail with crash...
            if (DEBUG) {
                LogHelper.d(TAG, "getTopActivityV20 exception:" + e.getMessage());
            }
        }
        return null;
    }

    private static String getTopApplicationV21(ActivityManager am) {
        List<RunningAppProcessInfo> runningApps = SysManagerUtils.getRunningAppProcesses(am);
        for (RunningAppProcessInfo procInfo : runningApps) {
            if (procInfo.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                if (procInfo.pkgList != null && procInfo.pkgList.length > 0) {
                    for (int i = 0; i < procInfo.pkgList.length; i++) {
                        if (procInfo.processName.equals(procInfo.pkgList[i])) {
                            return procInfo.processName;
                        }
                    }
                    return procInfo.pkgList[0]; // default to the first package
                }
                return procInfo.processName; // default to process name
            }
        }
        return null;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static String getMyTopActivity(ActivityManager am) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            List<AppTask> tasks = am.getAppTasks();
            if (tasks != null && tasks.size() > 0) {
                // the first one is the top activity if running
                RecentTaskInfo taskInfo = tasks.get(0).getTaskInfo();
                if (taskInfo.id != -1) {
                    return taskInfo.baseIntent.getComponent().getClassName();
                }
            }
        } else {
            if (DEBUG) {
                throw new RuntimeException("cannot invoke this method before Lollipop");
            }
        }
        return null;
    }

    /**
     * Get package name of the top application.
     * @return null may be returned
     */
    public static String getTopApplication(ActivityManager am) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return getTopApplicationV21(am);
        } else {
            ComponentName cn = getTopActivityV20(am);
            return (cn != null ? cn.getPackageName() : null);
        }
    }

    /**
     * Get package name of the top application and class name of the top
     * activity if possible.
     * @return Never be null. Value of {@link android.util.Pair#first} is the
     * package name and value of {@link android.util.Pair#second} is the
     * class name.
     */
    public static Pair<String, String> getTopApplicationAndActivity(ActivityManager am) {
        String pkgName = null;
        String className = null;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            ComponentName cn = getTopActivityV20(am);
            if (cn != null) {
                pkgName = cn.getPackageName();
                className = cn.getClassName();
            }
        } else {
            pkgName = getTopApplicationV21(am);
            if (pkgName != null && pkgName.equals(Constants.REAL_PACKAGE_NAME)) {
                className = getMyTopActivity(am);
            }
        }

        return new Pair<String, String>(pkgName, className);
    }
}

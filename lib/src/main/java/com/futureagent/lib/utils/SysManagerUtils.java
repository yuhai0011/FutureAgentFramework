
package com.futureagent.lib.utils;

import android.app.ActivityManager;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.util.List;

public class SysManagerUtils {
    public static List<ActivityManager.RunningServiceInfo> getRunningServices(ActivityManager am,
                                                                              int maxNum) {
        List<ActivityManager.RunningServiceInfo> runServiceList = null;
        try {
            runServiceList = am.getRunningServices(maxNum);
        } catch (Exception e) {
            // NullPointerException or IndexOutOfBoundsException may be thrown
            // on some devices
            // in the implementation of ActivityManager#getRunningServices().
        }
        return runServiceList;
    }

    public static List<ActivityManager.RunningAppProcessInfo> getRunningAppProcesses(
            ActivityManager am) {
        List<ActivityManager.RunningAppProcessInfo> runProcessList = null;
        try {
            runProcessList = am.getRunningAppProcesses();
        } catch (Exception e) {
            // NullPointerException or IndexOutOfBoundsException may be thrown
            // on some devices
            // in the implementation of
            // ActivityManager#getRunningAppProcesses().
        }
        return runProcessList;
    }

    public static List<PackageInfo> getInstalledPackages(PackageManager pm, int flags) {
        List<PackageInfo> installedApps = null;
        try {
            installedApps = pm.getInstalledPackages(flags);
        } catch (Exception e) {
            // IndexOutOfBoundsException may be thrown on some devices
            // in the implementation of PackageManager#getInstalledPackages().
        }
        return installedApps;
    }
}

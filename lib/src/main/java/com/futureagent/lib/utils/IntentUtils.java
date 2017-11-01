
package com.futureagent.lib.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;

import java.util.List;

public class IntentUtils {
    public static boolean isActivityAvailable(Context cxt, Intent intent) {
        List<ResolveInfo> list = cxt.getPackageManager().queryIntentActivities(intent, 0);
        return list != null && list.size() > 0;
    }

    public static boolean hasLauncherEntry(Context cxt, String pkgName) {
        return cxt.getPackageManager().getLaunchIntentForPackage(pkgName) != null;
    }

    public static List<ResolveInfo> getActivityInfo(Context cxt, Intent intent) {
        return cxt.getPackageManager().queryIntentActivities(intent, 0);
    }
}

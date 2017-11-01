
package com.futureagent.lib.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.os.SystemClock;

import com.futureagent.lib.config.LibConfigs;

import java.util.List;

public class BatteryUtils {

    private static final boolean DEBUG = LibConfigs.DEBUG_LOG;
    private static final String TAG = "BatteryUtils";

    /**
     * 获取指定应用可增加续航时间
     * 
     * @param ctx
     * @param pkgName
     * @return 单位：分
     */
    public static int getExtendTimeOfApp(Context ctx, String pkgName) {
        long startTime = System.currentTimeMillis();
        ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningList = SysManagerUtils.getRunningServices(
                am, 100);
        long time = 0;
        for (ActivityManager.RunningServiceInfo si : runningList) {
            if (si.service.getPackageName().equals(pkgName)) {
                if (time > si.activeSince || time == 0) {
                    time = si.activeSince;
                }
            }
        }
        // 5~7m 随机 + 耗电时长(每 2 小时加 1 分钟,进行取余),最长为15分钟
        int extendTime = (int) (5 + Math.random() * 2);
        if (time != 0) {
            long interval = SystemClock.elapsedRealtime() - time;
            int addTime = (int) (interval / (Constants.HOUR_MS * 2));
            extendTime += addTime;
        }
        if (extendTime > 15) {
            extendTime = 15;
        }
        return extendTime;
    }
}

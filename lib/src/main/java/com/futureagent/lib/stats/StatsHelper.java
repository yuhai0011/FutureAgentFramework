package com.futureagent.lib.stats;

import android.content.Context;

import org.json.JSONObject;

/**
 * author：yuhai on 15-12-31 13:42
 * email：yuhai833@126.com
 */
public class StatsHelper {
    private static StatsHelper sInstance;
    private StatsDB mStatsDB;

    private StatsHelper(Context ctx) {
        mStatsDB = new StatsDB(ctx);
    }

    public static StatsHelper getInstance(Context context) {
        synchronized (StatsHelper.class) {
            if (sInstance == null) {
                sInstance = new StatsHelper(context);
            }
        }
        return sInstance;
    }

    public void reportAlive() {
        mStatsDB.reportAlive();
    }

    public void reportInfo(String key, String contentKey, int contentValue) {
        mStatsDB.reportInfo(key, contentKey, contentValue);
    }

    public void reportInfo(String key, JSONObject data) {
        mStatsDB.reportInfo(key, data);
    }
}

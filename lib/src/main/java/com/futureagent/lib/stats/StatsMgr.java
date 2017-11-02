package com.futureagent.lib.stats;

import android.content.Context;

import com.futureagent.lib.config.LibConfigs;
import com.futureagent.lib.utils.Constants;
import com.futureagent.lib.utils.HttpUtils;
import com.futureagent.lib.utils.LogUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by skywalker on 16-2-28.
 * Email：yuhai833@126.com
 */
public class StatsMgr {

    private static final boolean DEBUG = LibConfigs.DEBUG_LOG;
    private static final String TAG = "StatsMgr";

    private static Context sContext;
    private static volatile StatsMgr sInstance;
    private StatsDB mStatsDB;

    private static final String HTTP_RETURN_STATUS_OK = "0";

    private StatsMgr(Context ctx) {
        sContext = ctx.getApplicationContext();
        mStatsDB = new StatsDB(sContext);
    }

    public StatsMgr getInstance(Context ctx) {
        if (sInstance == null) {
            synchronized (StatsMgr.class) {
                if (sInstance == null) {
                    sInstance = new StatsMgr(ctx);
                }
            }
        }
        return sInstance;
    }

    public void uploadStatsInfo() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                uploadAppAlive();
                uploadAppEvent();
                uploadAppJson();
            }
        }).start();
    }

    private void uploadAppAlive() {
        ArrayList<AppAliveInfo> appAliveInfoArrayList = mStatsDB.getAliveInfo();
        for (AppAliveInfo appAliveInfo : appAliveInfoArrayList) {
            JSONObject jsonObject = AppAliveInfo.convertToJson(appAliveInfo);
            String result = HttpUtils.doPost(Constants.APP_REPORT_URL, jsonObject.toString());
            try {
                JSONObject jsonResult = new JSONObject(result);
                String status = jsonResult.getString("status");
                String msg = jsonResult.getString("msg");
                if (HTTP_RETURN_STATUS_OK.equals(status)) {
                    mStatsDB.deleAliveInfo(appAliveInfo.mId);
                } else {
                    if (DEBUG) {
                        LogUtils.d(TAG, "uploadAppAlive failed, msg:" + msg);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadAppEvent() {

    }

    private void uploadAppJson() {

    }
}

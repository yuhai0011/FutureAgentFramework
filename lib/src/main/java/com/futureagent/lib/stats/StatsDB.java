package com.futureagent.lib.stats;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.futureagent.lib.config.LibConfigs;
import com.futureagent.lib.utils.DatabaseWrapper;
import com.futureagent.lib.utils.LogHelper;
import com.futureagent.lib.utils.TimeUtils;
import com.futureagent.lib.utils.TokenManager;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by skywalker on 16-2-28.
 * Email：yuhai833@126.com
 */
class StatsDB {
    private static final String TAG = "StatsDB";
    private static final boolean DEBUG = LibConfigs.DEBUG_LOG;

    private Context mContext;
    private DatabaseWrapper mDatabaseWrapper;

    private static final String DB_NAME = "stat_db";
    private static final int DB_VERSION = 1;
    private static final String TABLE_CREATE_ALIVE =
            "create table alive(id INTEGER PRIMARY KEY AUTOINCREMENT, pkgName TEXT, token TEXT, time TEXT)";
    private static final String TABLE_CREATE_EVENT =
            "create table event(id INTEGER PRIMARY KEY AUTOINCREMENT, pkgName TEXT, token TEXT, key TEXT, value TEXT, count INTEGER, time TEXT)";
    private static final String TABLE_CREATE_JSON =
            "create table json(id INTEGER PRIMARY KEY AUTOINCREMENT, pkgName TEXT, token TEXT, key TEXT, value TEXT, time TEXT)";

    private static final String TABLE_NAME_ALIVE = "alive";
    private static final String TABLE_NAME_EVENT = "event";
    private static final String TABLE_NAME_JSON = "json";


    public StatsDB(Context ctx) {
        mContext = ctx.getApplicationContext();
        mDatabaseWrapper = new DatabaseWrapper(mContext);
        mDatabaseWrapper.initDb(DB_NAME, DB_VERSION, getTableCreateSQL());
    }

    private ArrayList<String> getTableCreateSQL() {
        ArrayList<String> list = new ArrayList<>();
        list.add(TABLE_CREATE_ALIVE);
        list.add(TABLE_CREATE_EVENT);
        list.add(TABLE_CREATE_JSON);
        return list;
    }

    public void reportAlive() {
        AppAliveInfo appAliveInfo = new AppAliveInfo(mContext.getPackageName(),
                TokenManager.getToken(mContext), TimeUtils.getCurrentTimeInString());
        mDatabaseWrapper.insert(TABLE_NAME_ALIVE, null, AppAliveInfo.convertToContentValues(appAliveInfo));
    }

    public ArrayList<AppAliveInfo> getAliveInfo() {
        Cursor cursor = mDatabaseWrapper.query(TABLE_NAME_ALIVE,
                new String[]{"id", "pkgName", "token", "time"},
                null, null, null, null, null);
        ArrayList<AppAliveInfo> appAliveInfoList = new ArrayList<>();
        if (cursor == null || cursor.getCount() <= 0) {
            return appAliveInfoList;
        }

        while (cursor.moveToFirst()) {
            AppAliveInfo appAliveInfo = AppAliveInfo.convertFromCursor(cursor);
            appAliveInfoList.add(appAliveInfo);
        }
        return appAliveInfoList;
    }

    public boolean deleAliveInfo(int id) {
        int count = mDatabaseWrapper.delete(TABLE_NAME_ALIVE, "id=?", new String[]{String.valueOf(id)});
        return count > 0;
    }

    public void reportInfo(String key, String contentKey, int contentValue) {
        if (DEBUG) {
            LogHelper.d(TAG, "reportInfo key:" + key + ", contentKey:" + contentKey);
        }
        ContentValues values = new ContentValues();
        values.put("token", TokenManager.getToken(mContext));
        values.put("key", key);
        values.put("value", contentKey);
        values.put("count", contentValue);
        values.put("time", TimeUtils.getCurrentTimeInString());
        mDatabaseWrapper.insert(TABLE_NAME_EVENT, null, values);
    }

    public void reportInfo(String key, JSONObject data) {
        if (DEBUG) {
            LogHelper.d(TAG, "reportInfo key:" + key + ", data:" + data.toString());
        }
        ContentValues values = new ContentValues();
        values.put("token", TokenManager.getToken(mContext));
        values.put("key", key);
        values.put("value", data.toString());
        values.put("time", TimeUtils.getCurrentTimeInString());
        mDatabaseWrapper.insert(TABLE_NAME_JSON, null, values);
    }
}

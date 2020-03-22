package com.futureagent.lib.db.topical;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.futureagent.lib.config.LibConfigs;
import com.futureagent.lib.utils.LogUtils;

import java.util.HashMap;

public class SQLiteDbMgr {
    private static final String TAG = "SQLiteDbMgr";
    private static final boolean DEBUG = LibConfigs.DEBUG_LOG && false;

    private static class DbInfo {
        SQLiteDatabase db;
        int referenceCount;
    }

    private Context mAppContext;
    private HashMap<Class<? extends SQLiteDbCreator>, DbInfo> mOpenHelpers
            = new HashMap<Class<? extends SQLiteDbCreator>, DbInfo>();

    private static volatile SQLiteDbMgr sInstance;

    private SQLiteDbMgr(Context cxt) {
        mAppContext = cxt.getApplicationContext();
    }

    private static SQLiteDbMgr getInstance(Context cxt) {
        if (sInstance == null) {
            synchronized (SQLiteDbMgr.class) {
                if (sInstance == null) {
                    sInstance = new SQLiteDbMgr(cxt);
                }
            }
        }
        return sInstance;
    }

    private SQLiteDatabase acquireDatabase(Class<? extends SQLiteDbCreator> dbInfoClass) {
        if (DEBUG) LogUtils.d(TAG, "acquire DB: " + dbInfoClass.getName());
        SQLiteDatabase db;
        synchronized (SQLiteDbMgr.class) {
            DbInfo info = mOpenHelpers.get(dbInfoClass);
            if (info == null) {
                try {
                    if (DEBUG) LogUtils.d(TAG, "create DB: " + dbInfoClass.getName());
                    SQLiteDbCreator helper = dbInfoClass.newInstance();
                    info = new DbInfo();
                    info.db = helper.createDb(mAppContext);
                    info.referenceCount = 0;
                    mOpenHelpers.put(dbInfoClass, info);
                } catch (Exception e) {
                    throw new RuntimeException("failed to create SQLiteOpenHelper instance", e);
                }
            }
            info.referenceCount++;
            db = info.db;
            if (DEBUG) LogUtils.d(TAG, "acquireDatabase referenceCount: " + info.referenceCount);
        }
        return db;
    }

    private void releaseDatabase(Class<? extends SQLiteDbCreator> dbInfoClass) {
        if (DEBUG) LogUtils.d(TAG, "release DB: " + dbInfoClass.getName());
        synchronized (SQLiteDbMgr.class) {
            DbInfo info = mOpenHelpers.get(dbInfoClass);
            if (info != null) {
                info.referenceCount--;
                if (DEBUG) LogUtils.d(TAG, "releaseDatabase referenceCount: " + info.referenceCount);
                if (info.referenceCount == 0) {
                    if (DEBUG) LogUtils.d(TAG, "close DB: " + dbInfoClass.getName());
                    if (info.db != null) {
                        info.db.close();
                        info.db = null;
                    }
                    mOpenHelpers.remove(dbInfoClass);
                }
            }
        }
    }

    private SQLiteDatabase acquireDatabaseByName(String dbClassName) {
        if (DEBUG) LogUtils.d(TAG, "acquireDatabaseByName className: " + dbClassName);
        if (TextUtils.isEmpty(dbClassName)) {
            throw new RuntimeException("init sqlite db info error !");
        }
        Class dbInfoClass;
        try {
            dbInfoClass = Class.forName(dbClassName);
        } catch (Exception exception) {
            throw new RuntimeException("instance sqlite class object error. " + exception);
        }
        return acquireDatabase(dbInfoClass);
    }

    private void releaseDatabase(String dbClassName) {
        if (DEBUG) LogUtils.d(TAG, "releaseDatabase DB: " + dbClassName);
        if (TextUtils.isEmpty(dbClassName)) {
            throw new RuntimeException("init sqlite db info error !");
        }
        Class dbInfoClass;
        try {
            dbInfoClass = Class.forName(dbClassName);
        } catch (Exception exception) {
            throw new RuntimeException("instance sqlite class object error. " + exception);
        }
        releaseDatabase(dbInfoClass);
    }

    public static void releaseDatabase(Context cxt, String dbClassName) {
        getInstance(cxt).releaseDatabase(dbClassName);
    }

    public static SQLiteDatabase acquireDatabase(Context cxt, String dbClassName) {
        return getInstance(cxt).acquireDatabaseByName(dbClassName);
    }

}

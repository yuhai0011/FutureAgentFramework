package com.futureagent.lib.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.futureagent.lib.db.activeorm.Cache;
import com.futureagent.lib.db.activeorm.Configuration;

/**
 * Created by skywalker on 16/9/18.
 * Email: skywalker@thecover.cn
 * Description:
 * 数据库操作ORM
 */
public final class ActiveOrm {

    public static void initialize(Context context) {
        initialize(new Configuration.Builder(context).create());
    }

    public static void initialize(Configuration configuration) {
        initialize(configuration, false);
    }

    public static void initialize(Context context, boolean loggingEnabled) {
        initialize(new Configuration.Builder(context).create(), loggingEnabled);
    }

    public static void initialize(Configuration configuration, boolean loggingEnabled) {
        // Set logging enabled first
        Cache.initialize(configuration);
    }

    public static void clearCache() {
        Cache.clear();
    }

    public static void dispose() {
        Cache.dispose();
    }

    public static SQLiteDatabase getDatabase() {
        return Cache.openDatabase();
    }

    public static void beginTransaction() {
        Cache.openDatabase().beginTransaction();
    }

    public static void endTransaction() {
        Cache.openDatabase().endTransaction();
    }

    public static void setTransactionSuccessful() {
        Cache.openDatabase().setTransactionSuccessful();
    }

    public static boolean inTransaction() {
        return Cache.openDatabase().inTransaction();
    }

    public static void execSQL(String sql) {
        Cache.openDatabase().execSQL(sql);
    }

    public static void execSQL(String sql, Object[] bindArgs) {
        Cache.openDatabase().execSQL(sql, bindArgs);
    }
}

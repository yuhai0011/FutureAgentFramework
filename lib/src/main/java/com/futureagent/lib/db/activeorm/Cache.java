package com.futureagent.lib.db.activeorm;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.util.LruCache;

import com.futureagent.lib.config.LibConfigs;
import com.futureagent.lib.db.activeorm.serializer.TypeSerializer;
import com.futureagent.lib.utils.LogUtils;

import java.util.Collection;

/**
 * Created by skywalker on 16/9/18.
 * Email: skywalker@thecover.cn
 * Description:
 * 数据库操作缓存
 */
public final class Cache {
    private static final boolean DEBUG = LibConfigs.DEBUG_LOG;
    private static final String TAG = "Cache";
    /**
     * PUBLIC CONSTANTS
     */

    public static final int DEFAULT_CACHE_SIZE = 1024;

    /**
     * PRIVATE MEMBERS
     */

    private static Context sContext;

    private static ModelInfo sModelInfo;
    private static DatabaseHelper sDatabaseHelper;

    private static LruCache<String, Model> sEntities;

    private static boolean sIsInitialized = false;

    //////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    //////////////////////////////////////////////////////////////////////////////////////

    private Cache() {
    }

    //////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC METHODS
    //////////////////////////////////////////////////////////////////////////////////////

    public static synchronized void initialize(Configuration configuration) {
        if (sIsInitialized) {
            if (DEBUG) {
                LogUtils.d(TAG, "ActiveOrm already initialized.");
            }
            return;
        }

        sContext = configuration.getContext();
        sModelInfo = new ModelInfo(configuration);
        sDatabaseHelper = new DatabaseHelper(configuration);

        // TODO: It would be nice to override sizeOf here and calculate the memory
        // actually used, however at this point it seems like the reflection
        // required would be too costly to be of any benefit. We'll just set a max
        // object size instead.
        sEntities = new LruCache<String, Model>(configuration.getCacheSize());

        openDatabase();

        sIsInitialized = true;

        if (DEBUG) {
            LogUtils.d(TAG, "ActiveAndroid initialized successfully.");
        }
    }

    public static synchronized void clear() {
        sEntities.evictAll();
        if (DEBUG) {
            LogUtils.d(TAG, "Cache cleared.");
        }
    }

    public static synchronized void dispose() {
        closeDatabase();

        sEntities = null;
        sModelInfo = null;
        sDatabaseHelper = null;

        sIsInitialized = false;

        if (DEBUG) {
            LogUtils.d(TAG, "ActiveAndroid disposed. Call initialize to use library.");
        }
    }

    // Database access

    public static boolean isInitialized() {
        return sIsInitialized;
    }

    public static synchronized SQLiteDatabase openDatabase() {
        return sDatabaseHelper.getWritableDatabase();
    }

    public static synchronized void closeDatabase() {
        sDatabaseHelper.close();
    }

    // Context access

    public static Context getContext() {
        return sContext;
    }

    // Entity cache

    public static String getIdentifier(Class<? extends Model> type, Long id) {
        return getTableName(type) + "@" + id;
    }

    public static String getIdentifier(Model entity) {
        return getIdentifier(entity.getClass(), entity.getId());
    }

    public static synchronized void addEntity(Model entity) {
        sEntities.put(getIdentifier(entity), entity);
    }

    public static synchronized Model getEntity(Class<? extends Model> type, long id) {
        return sEntities.get(getIdentifier(type, id));
    }

    public static synchronized void removeEntity(Model entity) {
        sEntities.remove(getIdentifier(entity));
    }

    // Model cache

    public static synchronized Collection<TableInfo> getTableInfos() {
        return sModelInfo.getTableInfos();
    }

    public static synchronized TableInfo getTableInfo(Class<? extends Model> type) {
        return sModelInfo.getTableInfo(type);
    }

    public static synchronized TypeSerializer getParserForType(Class<?> type) {
        return sModelInfo.getTypeSerializer(type);
    }

    public static synchronized String getTableName(Class<? extends Model> type) {
        return sModelInfo.getTableInfo(type).getTableName();
    }
}

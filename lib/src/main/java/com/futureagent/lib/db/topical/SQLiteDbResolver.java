package com.futureagent.lib.db.topical;

import android.annotation.TargetApi;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Build;
import android.os.RemoteException;
import android.util.Pair;

import com.futureagent.lib.config.LibConfigs;
import com.futureagent.lib.utils.LogHelper;

import java.util.ArrayList;

/**
 * Created by skywalker on 15-6-9.
 */
public class SQLiteDbResolver {

    private static final String TAG = "SQLiteDbResolver";
    private static final boolean DEBUG = LibConfigs.DEBUG_LOG && false;

    public static volatile SQLiteDbResolver sInstance;
    private Context mAppContext;
    private ContentResolver mContentResolver;

    private static final String BACK_PROCESS_NAME = "cn.opda.a.phonoalbumshoushou:dxopt";
    private boolean sIsBackProcess;


    private SQLiteDbResolver(Context cxt) {
        mAppContext = cxt.getApplicationContext();
        mContentResolver = mAppContext.getContentResolver();
    }

    public static SQLiteDbResolver getInstance(Context cxt) {
        if (sInstance == null) {
            synchronized (SQLiteDbResolver.class) {
                if (sInstance == null) {
                    sInstance = new SQLiteDbResolver(cxt);
                }
            }
        }
        return sInstance;
    }

    public void setBackProcess() {
        sIsBackProcess = true;
    }

    /**
     * Acquires a reference to the object.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void acquireDatabase(Uri uri, String dbInfoName) {
        if (DEBUG) LogHelper.d(TAG, "acquireDatabase");
        if (uri == null || dbInfoName == null) {
            return;
        }
        if (sIsBackProcess) {
            SQLiteDbMgr.acquireDatabase(mAppContext, dbInfoName);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                mAppContext.getContentResolver().call(uri, SQLiteDbConstants.OPER_DB_ACQUIRE, dbInfoName, null);
            } else {
                ContentValues values = new ContentValues();
                values.put(SQLiteDbConstants.OPER_TYPE, SQLiteDbConstants.OPER_DB_ACQUIRE);
                mAppContext.getContentResolver().insert(uri, values);
            }
        }
    }

    /**
     * Releases a reference to the object, closing the object if the last reference
     * was released.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void releaseDataBase(Uri uri, String dbInfoName) {
        if (DEBUG) LogHelper.d(TAG, "releaseDataBase ");
        if (uri == null || dbInfoName == null) {
            return;
        }
        if (sIsBackProcess) {
            SQLiteDbMgr.releaseDatabase(mAppContext, dbInfoName);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                mAppContext.getContentResolver().call(uri, SQLiteDbConstants.OPER_DB_RELEASE, dbInfoName, null);
            } else {
                ContentValues values = new ContentValues();
                values.put(SQLiteDbConstants.OPER_TYPE, SQLiteDbConstants.OPER_DB_RELEASE);
                mAppContext.getContentResolver().insert(uri, values);
            }
        }

    }

    /**
     * Query the given table, returning a {@link Cursor} over the result set.
     * It does not currently support groupBy and having criteria query.
     *
     * @param uri The URI to query. This will be the full URI sent by the client;
     *      if the client is requesting a specific record, the URI will end in a record number
     *      that the implementation should parse and add to a WHERE or HAVING clause, specifying
     *      that _id value.
     * @param projection The list of columns to put into the cursor. If
     *      {@code null} all columns are included.
     * @param selection A filter declaring which rows to return, formatted as an
     *            SQL WHERE clause (excluding the WHERE itself). Passing null
     *            will return all rows for the given table.
     * @param selectionArgs You may include ?s in selection, which will be
     *         replaced by the values from selectionArgs, in order that they
     *         appear in the selection. The values will be bound as Strings.
     * @param orderBy How to order the rows, formatted as an SQL ORDER BY clause
     *            (excluding the ORDER BY itself). Passing null will use the
     *            default sort order, which may be unordered.
     * @return A {@link Cursor} object, which is positioned before the first entry. Note that
     * {@link Cursor}s are not synchronized, see the documentation for more details.
     * @see Cursor
     */
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String orderBy) {
        if (DEBUG) LogHelper.d(TAG, "query");
        Cursor cursor = null;
        if (uri == null) {
            return cursor;
        }
        if (sIsBackProcess) {
            Pair<String, String> dbInfo = SQLiteDbUtils.getDbInfo(uri);
            if (dbInfo == null) {
                return cursor;
            }
            SQLiteDatabase db = SQLiteDbMgr.acquireDatabase(mAppContext, dbInfo.first);
            try {
                if (db != null) {
                    cursor = db.query(dbInfo.second, projection, selection, selectionArgs, null, null, orderBy);
                }
            } catch (Exception exception) {
                throw new SQLiteException(exception.getMessage());
            } finally {
                SQLiteDbMgr.releaseDatabase(mAppContext, dbInfo.first);
            }
        } else {
            cursor = mContentResolver.query(uri, projection, selection, selectionArgs, orderBy);
        }
        return cursor;
    }

    /**
     * Implement this to handle requests to insert a new row.
     * As a courtesy, call {@link ContentResolver#notifyChange(android.net.Uri ,android.database.ContentObserver) notifyChange()}
     * after inserting.
     * @param uri The content:// URI of the insertion request. This must not be {@code null}.
     * @param values A set of column_name/value pairs to add to the database.
     *     This must not be {@code null}.
     * @return The URI for the newly inserted item.
     */
    public void insert(Uri uri, ContentValues values) {
        if (DEBUG) LogHelper.d(TAG, "insert");
        if (uri == null) {
            return;
        }
        if (sIsBackProcess) {
            Pair<String, String> dbInfo = SQLiteDbUtils.getDbInfo(uri);
            if (dbInfo == null) {
                return;
            }
            SQLiteDatabase db = SQLiteDbMgr.acquireDatabase(mAppContext, dbInfo.first);
            try {
                if (db != null) {
                    db.insert(dbInfo.second, null, values);
                }
            } catch (Exception exception) {
                throw new SQLiteException(exception.getMessage());
            } finally {
                SQLiteDbMgr.releaseDatabase(mAppContext, dbInfo.first);
            }
        } else {
            mContentResolver.insert(uri, values);
        }
    }

    /**
     * Implement this to handle requests to delete one or more rows.
     * The implementation should apply the selection clause when performing
     * deletion, allowing the operation to affect multiple rows in a directory.
     * As a courtesy, call {@link ContentResolver#notifyChange(android.net.Uri ,android.database.ContentObserver) notifyChange()}
     * after deleting.
     * @param uri The full URI to query, including a row ID (if a specific record is requested).
     * @param selection An optional restriction to apply to rows when deleting.
     * @return The number of rows affected.
     * @throws SQLException
     */
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        if (DEBUG) LogHelper.d(TAG, "delete");
        int rowId = -1;
        if (uri == null) {
            return rowId;
        }
        if (sIsBackProcess) {
            Pair<String, String> dbInfo = SQLiteDbUtils.getDbInfo(uri);
            if (dbInfo == null) {
                return rowId;
            }
            SQLiteDatabase db = SQLiteDbMgr.acquireDatabase(mAppContext, dbInfo.first);
            try {
                if (db != null) {
                    rowId = db.delete(dbInfo.second, selection, selectionArgs);
                }
            } catch (Exception exception) {
                throw new SQLiteException(exception.getMessage());
            } finally {
                SQLiteDbMgr.releaseDatabase(mAppContext, dbInfo.first);
            }
        } else {
            rowId = mContentResolver.delete(uri, selection, selectionArgs);
        }
        return rowId;
    }

    /**
     * Implement this to handle requests to update one or more rows.
     * The implementation should update all rows matching the selection
     * to set the columns according to the provided values map.
     * @param uri The URI to query. This can potentially have a record ID if this
     * is an update request for a specific record.
     * @param values A set of column_name/value pairs to update in the database.
     *     This must not be {@code null}.
     * @param selection An optional filter to match rows to update.
     * @return the number of rows affected.
     */
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (DEBUG) LogHelper.d(TAG, "update");
        int rowId = -1;
        if (uri == null) {
            return rowId;
        }
        if (sIsBackProcess) {
            Pair<String, String> dbInfo = SQLiteDbUtils.getDbInfo(uri);
            if (dbInfo == null) {
                return rowId;
            }
            SQLiteDatabase db = SQLiteDbMgr.acquireDatabase(mAppContext, dbInfo.first);
            try {
                if (db != null) {
                    rowId = db.update(dbInfo.second, values, selection, selectionArgs);
                }
            } catch (Exception exception) {
                throw new SQLiteException(exception.getMessage());
            } finally {
                SQLiteDbMgr.releaseDatabase(mAppContext, dbInfo.first);
            }
        } else {
            rowId = mContentResolver.update(uri, values, selection, selectionArgs);
        }
        return rowId;
    }



    /**
     * Applies each of the ContentProviderOperation objects and returns an array
     * of their results. Passes through OperationApplicationException, which may be thrown
     * by the call to ContentProviderOperation#apply}.
     * If all the applications succeed then a {@link ContentProviderResult} array with the
     * same number of elements as the operations will be returned. It is implementation-specific
     * how many, if any, operations will have been successfully applied if a call to
     * apply results in a OperationApplicationException}.
     * @param authority the authority of the DBContentProvider to which this batch should be applied
     * @param operations the operations to apply
     * @return the results of the applications
     * @throws OperationApplicationException thrown if an application fails.
     * See {@link ContentProviderOperation#apply} for more information.
     * @throws RemoteException thrown if a RemoteException is encountered while attempting
     *   to communicate with a remote provider.
     */
    public ContentProviderResult[] applyBatch(String authority,
                                              ArrayList<ContentProviderOperation> operations)
            throws RemoteException, OperationApplicationException {
        if (DEBUG) LogHelper.d(TAG, "applyBatch");
        ContentProviderResult[] results = null;
        if (authority == null) {
            throw new IllegalArgumentException("Unknown authority " + authority);
        }
        if (operations == null) {
            return results;
        }
        try {
            results = mContentResolver.applyBatch(authority, operations);
        } catch (Exception exception) {
            throw new SQLiteException(exception.getMessage());
        }
        return results;
    }

}

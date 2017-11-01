package com.futureagent.lib.db.topical;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Bundle;
import android.util.Pair;

import com.futureagent.lib.utils.LogHelper;

import java.util.ArrayList;

/**
 * author：yuhai on 15-12-31 14:11
 * email：yuhai04@baidu.com
 */
public class SQLiteDbProvider extends ContentProvider {
    private static final String TAG = "SQLiteDbProvider";
    private Context mCxt;

    @Override
    public boolean onCreate() {
        LogHelper.d(TAG, "SQLiteDbProvider.oncreate");
        mCxt = getContext();
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        LogHelper.d(TAG, "SQLiteDbProvider.query");
        Cursor cursor = null;
        Pair<String, String> dbInfo = SQLiteDbUtils.getDbInfo(uri);
        if (dbInfo == null) {
            return cursor;
        }
        SQLiteDatabase db = SQLiteDbMgr.acquireDatabase(mCxt, dbInfo.first);
        try {
            cursor = db.query(dbInfo.second, projection, selection, selectionArgs,
                    null, null, sortOrder);
        } catch (Exception exception) {
            throw new SQLiteException(exception.getMessage());
        } finally {
            SQLiteDbMgr.releaseDatabase(mCxt, dbInfo.first);
        }
        return cursor;
    }


    @Override
    public Uri insert(Uri uri, ContentValues values) {
        LogHelper.d(TAG, "SQLiteDbProvider.insert uri = " + uri.toString());

        if (uri == null) {
            return null;
        }
        Pair<String, String> dbInfo = SQLiteDbUtils.getDbInfo(uri);
        if (dbInfo == null) {
            return null;
        }
        String operName = values.getAsString(SQLiteDbConstants.OPER_TYPE);
        if (operName == null) {
            SQLiteDatabase db = SQLiteDbMgr.acquireDatabase(mCxt, dbInfo.first);
            long rowId = 0;
            try {
                rowId = db.insert(dbInfo.second, null, values);
            } catch (Exception exception) {
                throw new SQLiteException(exception.getMessage());
            } finally {
                SQLiteDbMgr.releaseDatabase(mCxt, dbInfo.first);
            }
            return ContentUris.withAppendedId(uri, rowId);
        } else {

            LogHelper.d(TAG, "SQLiteDbProvider.insert method = " + operName);

            if (SQLiteDbConstants.OPER_DB_ACQUIRE.equals(operName)) {
                SQLiteDbMgr.acquireDatabase(mCxt, dbInfo.first);
            } else if (SQLiteDbConstants.OPER_DB_RELEASE.equals(operName)) {
                SQLiteDbMgr.releaseDatabase(mCxt, dbInfo.first);
            }
            return null;
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        LogHelper.d(TAG, "SQLiteDbProvider.delete");
        int rowId = 0;
        if (uri == null) {
            return rowId;
        }
        Pair<String, String> dbInfo = SQLiteDbUtils.getDbInfo(uri);
        if (dbInfo == null) {
            return rowId;
        }
        SQLiteDatabase db = SQLiteDbMgr.acquireDatabase(mCxt, dbInfo.first);
        try {
            rowId = db.delete(dbInfo.second, selection, selectionArgs);
        } catch (Exception exception) {
            throw new SQLiteException(exception.getMessage());
        } finally {
            SQLiteDbMgr.releaseDatabase(mCxt, dbInfo.first);
        }
        return rowId;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        LogHelper.d(TAG, "SQLiteDbProvider.update");
        int rowId = 0;
        if (uri == null) {
            return rowId;
        }
        Pair<String, String> dbInfo = SQLiteDbUtils.getDbInfo(uri);
        if (dbInfo == null) {
            return rowId;
        }
        SQLiteDatabase db = SQLiteDbMgr.acquireDatabase(mCxt, dbInfo.first);
        try {
            rowId = db.update(dbInfo.second, values, selection, selectionArgs);
        } catch (Exception exception) {
            throw new SQLiteException(exception.getMessage());
        } finally {
            SQLiteDbMgr.releaseDatabase(mCxt, dbInfo.first);
        }
        return rowId;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        return super.bulkInsert(uri, values);
    }

    @Override
    public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations)
            throws OperationApplicationException {
        LogHelper.d(TAG, "BaseProvider applyBatch");
        if (operations == null || operations.size() == 0) {
            return new ContentProviderResult[0];
        }

        Uri uri = operations.get(0).getUri();

        if (uri == null) {
            return null;
        }
        Pair<String, String> dbInfo = SQLiteDbUtils.getDbInfo(uri);
        if (dbInfo == null) {
            return null;
        }
        SQLiteDatabase db = SQLiteDbMgr.acquireDatabase(mCxt, dbInfo.first);
        db.beginTransaction();
        try {
            ContentProviderResult[] results = super.applyBatch(operations);
            db.setTransactionSuccessful();
            return results;
        } catch (Exception exception) {
            throw new SQLiteException(exception.getMessage());
        } finally {
            db.endTransaction();
            SQLiteDbMgr.releaseDatabase(mCxt, dbInfo.first);
        }
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Bundle call(String method, String arg, Bundle extras) {
        if (method == null || arg == null) {
            return null;
        }
        if (SQLiteDbConstants.OPER_DB_ACQUIRE.equals(method)) {
            SQLiteDbMgr.acquireDatabase(mCxt, arg);
        } else if (SQLiteDbConstants.OPER_DB_RELEASE.equals(method)) {
            SQLiteDbMgr.releaseDatabase(mCxt, arg);
        }
        return null;
    }
}
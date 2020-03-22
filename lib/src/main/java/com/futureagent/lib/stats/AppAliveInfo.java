package com.futureagent.lib.stats;

import android.content.ContentValues;
import android.database.Cursor;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by skywalker on 16-2-28.
 * Email：yuhai833@126.com
 */
public class AppAliveInfo {

    public int mId;
    public String mPkgName;
    public String mToken;
    public String mTime;

    public AppAliveInfo(int id, String pkgName, String token, String time) {
        mId = id;
        mPkgName = pkgName;
        mToken = token;
        mTime = time;
    }

    public AppAliveInfo(String pkgName, String token, String time) {
        mId = -1;
        mPkgName = pkgName;
        mToken = token;
        mTime = time;
    }

    public static ContentValues convertToContentValues(AppAliveInfo appAliveInfo) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("pkgName", appAliveInfo.mPkgName);
        contentValues.put("token", appAliveInfo.mToken);
        contentValues.put("time", appAliveInfo.mTime);
        return contentValues;
    }

    public static JSONObject convertToJson(AppAliveInfo appAliveInfo) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("pkgName", appAliveInfo.mPkgName);
            jsonObject.put("token", appAliveInfo.mToken);
            jsonObject.put("time", appAliveInfo.mTime);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static AppAliveInfo convertFromCursor(Cursor cursor) {
        return new AppAliveInfo(cursor.getInt(cursor.getColumnIndex("id")),
                cursor.getString(cursor.getColumnIndex("pkgName")),
                cursor.getString(cursor.getColumnIndex("token")),
                cursor.getString(cursor.getColumnIndex("time")));
    }
}

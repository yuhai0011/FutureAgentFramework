package com.futureagent.lib.db.topical;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Pair;

import java.util.List;

/**
 * Created by skywalker on 15-6-11.
 */

public class SQLiteDbUtils {

    public static Pair<String, String> getDbInfo(Uri uri) {

        List<String> segments = uri.getPathSegments();
        if (segments == null) {
            return null;
        }
        int size = segments.size();
        if (size < 2){
            return null;
        }
        String dbClassName = segments.get(0);
        String tableName = segments.get(1);
        if (TextUtils.isEmpty(dbClassName) ||
                TextUtils.isEmpty(tableName)) {
            return null;
        }

        return new Pair<>(dbClassName, tableName);
    }
}

package com.futureagent.lib.db.topical;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public interface SQLiteDbCreator {
    SQLiteDatabase createDb(Context cxt);
}

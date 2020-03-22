package com.futureagent.lib.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by yuhai on 16-2-27.
 */
public class DatabaseWrapper {

    private static class DatabaseHelper extends SQLiteOpenHelper {
        /**
         * 在SQLiteOpenHelper的子类当中，必须有该构造函数
         *
         * @param context 上下文对象
         * @param name    数据库名称
         * @param factory
         * @param version 当前数据库的版本，值必须是整数并且是递增的状态
         */
        public ArrayList<String> mCreateTableSQLList;

        public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                              int version) {
            //必须通过super调用父类当中的构造函数
            super(context, name, factory, version);
        }

        public DatabaseHelper(Context context, String name, int version) {
            this(context, name, null, version);
        }

        //该函数是在第一次创建的时候执行，实际上是第一次得到SQLiteDatabase对象的时候才会调用这个方法
        @Override
        public void onCreate(SQLiteDatabase db) {
            //execSQL用于执行SQL语句
            if (mCreateTableSQLList == null || mCreateTableSQLList.size() == 0) {
                return;
            }
            for (String sql : mCreateTableSQLList) {
                db.execSQL(sql);
            }
            //"create table user(id int,name varchar(20))"
        }

        @Override
        public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
            // TODO Auto-generated method stub
        }
    }

    private DatabaseHelper mDBHelper;
    private Context mContext;
    private SQLiteDatabase mSQLiteDatabase;

    public DatabaseWrapper(Context context) {
        mContext = context.getApplicationContext();
    }

    public void initDb(String dbName, int version, ArrayList<String> createTableSQL) {
        mDBHelper = new DatabaseHelper(mContext, dbName, version);
        mDBHelper.mCreateTableSQLList = (ArrayList<String>) createTableSQL.clone();
    }

    private boolean getSQLiteDatabase() {
        if (mSQLiteDatabase == null) {
            mSQLiteDatabase = mDBHelper.getWritableDatabase();
        }
        return mSQLiteDatabase != null;
    }

    // 创建ContentValues对象
    // ContentValues values = new ContentValues();
    // 向该对象中插入键值对，其中键是列名，值是希望插入到这一列的值，值必须和数据库当中的数据类型一致
    // values.put("id", 1);
    // values.put("name", "skywalker");
    // 创建DatabaseHelper对象
    // 调用insert方法，就可以将数据插入到数据库当中
    // 第一个参数:表名称
    // 第二个参数：SQl不允许一个空列，如果ContentValues是空的，那么这一列被明确的指明为NULL值
    // 第三个参数：ContentValues对象
    // insert("user", null, values);
    public long insert(String table, String nullColumnHack, ContentValues values) {
        if (!getSQLiteDatabase()) {
            return -1;
        }
        return mSQLiteDatabase.insert(table, nullColumnHack, values);
    }

    // 创建一个ContentValues对象
    // ContentValues values = new ContentValues();
    // values.put("name", "zhangsan");
    // 调用update方法
    // 第一个参数String：表名
    // 第二个参数ContentValues：ContentValues对象
    // 第三个参数String：where字句，相当于sql语句where后面的语句，？号是占位符
    // 第四个参数String[]：占位符的值
    // update("user", values, "id=?", new String[] { "1" });
    public int update(String table, ContentValues values, String whereClause, String[] whereArgs) {
        if (!getSQLiteDatabase()) {
            return -1;
        }
        return mSQLiteDatabase.update(table, values, whereClause, whereArgs);
    }

    // 第一个参数String：表名
    // 第二个参数String[]:要查询的列名
    // 第三个参数String：查询条件
    // 第四个参数String[]：查询条件的参数
    // 第五个参数String:对查询的结果进行分组
    // 第六个参数String：对分组的结果进行限制
    // 第七个参数String：对查询的结果进行排序
    // query("user", new String[] { "id",
    //  "name" }, "id=?", new String[] { "1" }, null, null, null);
    public Cursor query(String table, String[] columns, String selection,
                        String[] selectionArgs, String groupBy, String having,
                        String orderBy) {
        if (!getSQLiteDatabase()) {
            return null;
        }
        return mSQLiteDatabase.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
    }

    //调用SQLiteDatabase对象的delete方法进行删除操作
    //第一个参数String：表名
    //第二个参数String：条件语句
    //第三个参数String[]：条件值
    //delete("user", "id=?", new String[]{"1"});
    public int delete(String table, String whereClause, String[] whereArgs) {
        if (!getSQLiteDatabase()) {
            return -1;
        }
        return mSQLiteDatabase.delete(table, whereClause, whereArgs);
    }

    //调用SQLiteDatabase对象执行sql语句
    //第一个参数String：sql语句
    public void execSQL(String sql) {
        if (!getSQLiteDatabase()) {
            return;
        }
        mSQLiteDatabase.execSQL(sql);
    }
}

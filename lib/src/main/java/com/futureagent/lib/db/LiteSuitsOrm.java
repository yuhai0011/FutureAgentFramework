package com.futureagent.lib.db;

import android.content.Context;

import com.litesuits.orm.LiteOrm;
import com.litesuits.orm.db.DataBaseConfig;
import com.litesuits.orm.db.assit.QueryBuilder;

import java.util.List;

/**
 * Created by skywalker on 16/9/19.
 * Email: skywalker@thecover.cn
 * Description:
 */
public final class LiteSuitsOrm {
    private static LiteOrm liteOrm;
    private static volatile LiteSuitsOrm ourInstance;
    private static final String DB_NAME = "future_blog.db";
    private static final int DB_VER = 1;

    /**
     * 说明
     * http://blog.csdn.net/u014099894/article/details/51586500
     */
    private LiteSuitsOrm(Context context) {
        liteOrm = LiteOrm.newSingleInstance(getDataBaseConfig(context));//非级联操作，保持当前对象，高效率
        //liteOrm = LiteOrm.newCascadeInstance(getDataBaseConfig(context));//级联操作：保存[当前对象]，以及该对象所有的[关联对象]以及它们的[映射关系]
    }

    private DataBaseConfig getDataBaseConfig(Context context) {
        DataBaseConfig dataBaseConfig = new DataBaseConfig(context);
        dataBaseConfig.context = context;
        dataBaseConfig.dbName = DB_NAME;
        dataBaseConfig.dbVersion = DB_VER;
        dataBaseConfig.onUpdateListener = null;
        return dataBaseConfig;
    }

    public static LiteSuitsOrm getInstance(Context context) {
        if (ourInstance == null) {
            synchronized (LiteSuitsOrm.class) {
                if (ourInstance == null) {
                    ourInstance = new LiteSuitsOrm(context);
                }
            }
        }
        return ourInstance;
    }

    /**
     * 插入一条记录
     *
     * @param t
     */
    public <T> long insert(T t) {
        return liteOrm.save(t);
    }

    /**
     * 插入所有记录
     *
     * @param list
     */
    public <T> void insertAll(List<T> list) {
        liteOrm.save(list);
    }

    /**
     * 查询所有
     *
     * @param cla
     * @return
     */
    public <T> List<T> getQueryAll(Class<T> cla) {
        return liteOrm.query(cla);
    }

    /**
     * 查询  某字段 等于 Value的值
     *
     * @param builder
     * @return
     */
    public <T> List<T> getQueryByWhere(QueryBuilder builder) {
        return liteOrm.<T>query(builder);
    }

    /**
     * 查询  某字段 等于 Value的值
     *
     * @param cla
     * @param field
     * @param value
     * @return
     */
    public <T> List<T> getQueryByWhere(Class<T> cla, String field, Object[] value) {
        return liteOrm.<T>query(new QueryBuilder(cla).where(field + "=?", value));
    }

    /**
     * 查询  某字段 等于 Value的值  可以指定从1-20，就是分页
     *
     * @param cla
     * @param field
     * @param value
     * @param start
     * @param length
     * @return
     */
    public <T> List<T> getQueryByWhereLength(Class<T> cla, String field, Object[] value, int start, int length) {
        return liteOrm.<T>query(new QueryBuilder(cla).where(field + "=?", value).limit(start, length));
    }

    /**
     * 删除一个数据
     *
     * @param t
     * @param <T>
     */
    public <T> void delete(T t) {
        liteOrm.delete(t);
    }

    /**
     * 删除一个表
     *
     * @param cla
     * @param <T>
     */
    public <T> void delete(Class<T> cla) {
        liteOrm.delete(cla);
    }

    /**
     * 删除集合中的数据
     *
     * @param list
     * @param <T>
     */
    public <T> void deleteList(List<T> list) {
        liteOrm.delete(list);
    }

    /**
     * 删除数据库
     */
    public void deleteDatabase() {
        liteOrm.deleteDatabase();
    }
}

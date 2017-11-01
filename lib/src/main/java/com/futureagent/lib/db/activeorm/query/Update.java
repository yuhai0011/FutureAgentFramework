package com.futureagent.lib.db.activeorm.query;

import com.futureagent.lib.db.activeorm.Cache;
import com.futureagent.lib.db.activeorm.Model;

public final class Update implements Sqlable {
    private Class<? extends Model> mType;

    public Update(Class<? extends Model> table) {
        mType = table;
    }

    public Set set(String set) {
        return new Set(this, set);
    }

    public Set set(String set, Object... args) {
        return new Set(this, set, args);
    }

    Class<? extends Model> getType() {
        return mType;
    }

    @Override
    public String toSql() {
        StringBuilder sql = new StringBuilder();
        sql.append("UPDATE ");
        sql.append(Cache.getTableName(mType));
        sql.append(" ");

        return sql.toString();
    }
}

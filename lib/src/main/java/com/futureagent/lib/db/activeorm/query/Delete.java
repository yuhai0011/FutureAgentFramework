package com.futureagent.lib.db.activeorm.query;

import com.futureagent.lib.db.activeorm.Model;

public final class Delete implements Sqlable {
    public Delete() {
    }

    public From from(Class<? extends Model> table) {
        return new From(table, this);
    }

    @Override
    public String toSql() {
        return "DELETE ";
    }
}
package com.futureagent.lib.db.activeorm;

import android.text.TextUtils;

import com.futureagent.lib.config.LibConfigs;
import com.futureagent.lib.db.activeorm.annotation.Column;
import com.futureagent.lib.db.activeorm.annotation.Table;
import com.futureagent.lib.utils.LogHelper;
import com.futureagent.lib.utils.ReflectUtil;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by skywalker on 16/9/18.
 * Email: skywalker@thecover.cn
 * Description:
 */
public final class TableInfo {
    private static final boolean DEBUG = LibConfigs.DEBUG_LOG;
    private static final String TAG = "TableInfo";
    //////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE MEMBERS
    //////////////////////////////////////////////////////////////////////////////////////

    private Class<? extends Model> mType;
    private String mTableName;
    private String mIdName = Table.DEFAULT_ID_NAME;

    private Map<Field, String> mColumnNames = new LinkedHashMap<Field, String>();

    //////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    //////////////////////////////////////////////////////////////////////////////////////

    public TableInfo(Class<? extends Model> type) {
        mType = type;

        final Table tableAnnotation = type.getAnnotation(Table.class);

        if (tableAnnotation != null) {
            mTableName = tableAnnotation.name();
            mIdName = tableAnnotation.id();
        } else {
            mTableName = type.getSimpleName();
        }

        // Manually add the id column since it is not declared like the other columns.
        Field idField = getIdField(type);
        mColumnNames.put(idField, mIdName);

        List<Field> fields = new LinkedList<>(ReflectUtil.getDeclaredColumnFields(type));
        Collections.reverse(fields);

        for (Field field : fields) {
            if (field.isAnnotationPresent(Column.class)) {
                final Column columnAnnotation = field.getAnnotation(Column.class);
                String columnName = columnAnnotation.name();
                if (TextUtils.isEmpty(columnName)) {
                    columnName = field.getName();
                }

                mColumnNames.put(field, columnName);
            }
        }

    }

    //////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC METHODS
    //////////////////////////////////////////////////////////////////////////////////////

    public Class<? extends Model> getType() {
        return mType;
    }

    public String getTableName() {
        return mTableName;
    }

    public String getIdName() {
        return mIdName;
    }

    public Collection<Field> getFields() {
        return mColumnNames.keySet();
    }

    public String getColumnName(Field field) {
        return mColumnNames.get(field);
    }


    private Field getIdField(Class<?> type) {
        if (type.equals(Model.class)) {
            try {
                return type.getDeclaredField("mId");
            } catch (NoSuchFieldException e) {
                if (DEBUG) {
                    LogHelper.e(TAG, "Impossible!", e);
                }
            }
        } else if (type.getSuperclass() != null) {
            return getIdField(type.getSuperclass());
        }

        return null;
    }
}


package com.futureagent.lib.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.futureagent.lib.config.LibConfigs;
import com.futureagent.lib.db.activeorm.Model;
import com.futureagent.lib.db.activeorm.annotation.Column;
import com.futureagent.lib.db.activeorm.serializer.TypeSerializer;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 反射调用公共方法
 */
public class ReflectUtil {
    private static final boolean DEBUG = LibConfigs.DEBUG_LOG;
    private static final String TAG = "ReflectUtil";

    public static Class getClass(String name) {
        try {
            return Class.forName(name);
        } catch (Exception e) {
        }
        return null;
    }

    public static Method getMethod(Class clazz, String name, Class[] args) {
        Method ret = null;
        try {
            ret = clazz.getMethod(name, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    public static Constructor getConstructor(Class clazz, Class[] args) {
        Constructor ret = null;
        try {
            ret = clazz.getDeclaredConstructor(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    public static Object getStaticFieldValue(Class clazz, String name) {
        return getFieldValue(clazz, name, null);
    }

    public static Object getFieldValue(Class clazz, String name, Object obj) {
        Object ret = null;
        if (clazz != null && name != null) {
            try {
                Field field = clazz.getDeclaredField(name);
                field.setAccessible(true);
                ret = field.get(obj);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    /*
     * // 设置字段值 // 如：username // 字段,setUsername(String // username)
     */
    @SuppressWarnings("unchecked")
    public static void setFieldValue(Object target, String fname, Class ftype, Object fvalue) {
        if (target == null || fname == null || "".equals(fname)
                || (fvalue != null && !ftype.isAssignableFrom(fvalue.getClass()))) {// 如果类型不匹配，直接退出
            return;
        }
        Class clazz = target.getClass();
        try { // 先通过setXxx()方法设置类属性值
            String methodname = "set" + Character.toUpperCase(fname.charAt(0)) + fname.substring(1);
            Method method = clazz.getDeclaredMethod(methodname, ftype); // 获取定义的方法
            if (!Modifier.isPublic(method.getModifiers())) { // 设置非共有方法权限
                method.setAccessible(true);
            }
            method.invoke(target, fvalue); // 执行方法回调
        } catch (Exception me) {// 如果set方法不存在，则直接设置类属性值
            me.printStackTrace();
            try {
                Field field = clazz.getDeclaredField(fname); // 获取定义的类属性
                if (!Modifier.isPublic(field.getModifiers())) { // 设置非共有类属性权限
                    field.setAccessible(true);
                }
                field.set(target, fvalue); // 设置类属性值
            } catch (Exception fe) {
                fe.printStackTrace();
            }
        }
    }

    public static Object callStaticMethod(Method m, Object... args) {
        return callMethod(m, null, args);
    }

    public static Object callMethod(Method m, Object obj, Object... args) {
        Object ret = null;
        try {
            ret = m.invoke(obj, args);
        } catch (Exception e) {
            if (DEBUG) {
                LogUtils.e(TAG, "callMethod", e);
            }
            e.printStackTrace();
            throw new RuntimeException("callMethod failure", e);
        }
        return ret;
    }

    public static Object callConstructor(Constructor m, Object... args) {
        Object ret = null;
        try {
            ret = m.newInstance(args);
        } catch (Exception e) {
            if (DEBUG) {
                LogUtils.e(TAG, "callConstructor", e);
            }
            throw new RuntimeException("callConstructor failure", e);
        }
        return ret;
    }

    public static boolean isModel(Class<?> type) {
        return isSubclassOf(type, Model.class) && (!Modifier.isAbstract(type.getModifiers()));
    }

    public static boolean isTypeSerializer(Class<?> type) {
        return isSubclassOf(type, TypeSerializer.class);
    }

    // Meta-data

    @SuppressWarnings("unchecked")
    public static <T> T getMetaData(Context context, String name) {
        try {
            final ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(),
                    PackageManager.GET_META_DATA);

            if (ai.metaData != null) {
                return (T) ai.metaData.get(name);
            }
        } catch (Exception e) {
            if (DEBUG) {
                LogUtils.e(TAG, "getMetaData", e);
            }
        }

        return null;
    }

    public static Set<Field> getDeclaredColumnFields(Class<?> type) {
        Set<Field> declaredColumnFields = Collections.emptySet();

        if (isSubclassOf(type, Model.class) || Model.class.equals(type)) {
            declaredColumnFields = new LinkedHashSet<>();

            Field[] fields = type.getDeclaredFields();
            Arrays.sort(fields, new Comparator<Field>() {
                @Override
                public int compare(Field field1, Field field2) {
                    return field2.getName().compareTo(field1.getName());
                }
            });
            for (Field field : fields) {
                if (field.isAnnotationPresent(Column.class)) {
                    declaredColumnFields.add(field);
                }
            }

            Class<?> parentType = type.getSuperclass();
            if (parentType != null) {
                declaredColumnFields.addAll(getDeclaredColumnFields(parentType));
            }
        }

        return declaredColumnFields;
    }

    //////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE METHODS
    //////////////////////////////////////////////////////////////////////////////////////

    public static boolean isSubclassOf(Class<?> type, Class<?> superClass) {
        if (type.getSuperclass() != null) {
            if (type.getSuperclass().equals(superClass)) {
                return true;
            }
            return isSubclassOf(type.getSuperclass(), superClass);
        }
        return false;
    }
}

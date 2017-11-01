package com.futureagent.lib.db.activeorm;

import android.content.Context;

import com.futureagent.lib.config.LibConfigs;
import com.futureagent.lib.db.activeorm.serializer.CalendarSerializer;
import com.futureagent.lib.db.activeorm.serializer.FileSerializer;
import com.futureagent.lib.db.activeorm.serializer.SqlDateSerializer;
import com.futureagent.lib.db.activeorm.serializer.TypeSerializer;
import com.futureagent.lib.db.activeorm.serializer.UtilDateSerializer;
import com.futureagent.lib.utils.LogHelper;
import com.futureagent.lib.utils.ReflectUtil;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dalvik.system.DexFile;

/**
 * Created by skywalker on 16/9/18.
 * Email: skywalker@thecover.cn
 * Description:
 */
final class ModelInfo {
    private static final boolean DEBUG = LibConfigs.DEBUG_LOG;
    private static final String TAG = "ModelInfo";
    //////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE METHODS
    //////////////////////////////////////////////////////////////////////////////////////

    private Map<Class<? extends Model>, TableInfo> mTableInfos = new HashMap<Class<? extends Model>, TableInfo>();
    private Map<Class<?>, TypeSerializer> mTypeSerializers = new HashMap<Class<?>, TypeSerializer>() {
        {
            put(Calendar.class, new CalendarSerializer());
            put(java.sql.Date.class, new SqlDateSerializer());
            put(java.util.Date.class, new UtilDateSerializer());
            put(java.io.File.class, new FileSerializer());
        }
    };

    //////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    //////////////////////////////////////////////////////////////////////////////////////

    public ModelInfo(Configuration configuration) {
        if (!loadModelFromMetaData(configuration)) {
            try {
                scanForModel(configuration.getContext());
            } catch (IOException e) {
                if (DEBUG) {
                    LogHelper.e(TAG, "Couldn't open source path.", e);
                }
            }
        }

        if (DEBUG) {
            LogHelper.d(TAG, "ModelInfo loaded.");
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC METHODS
    //////////////////////////////////////////////////////////////////////////////////////

    public Collection<TableInfo> getTableInfos() {
        return mTableInfos.values();
    }

    public TableInfo getTableInfo(Class<? extends Model> type) {
        scanClassTableInfo(type);
        return mTableInfos.get(type);
    }

    public TypeSerializer getTypeSerializer(Class<?> type) {
        return mTypeSerializers.get(type);
    }

    //////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE METHODS
    //////////////////////////////////////////////////////////////////////////////////////

    private boolean loadModelFromMetaData(Configuration configuration) {
        if (!configuration.isValid()) {
            return false;
        }

        final List<Class<? extends Model>> models = configuration.getModelClasses();
        if (models != null) {
            for (Class<? extends Model> model : models) {
                mTableInfos.put(model, new TableInfo(model));
            }
        }

        final List<Class<? extends TypeSerializer>> typeSerializers = configuration.getTypeSerializers();
        if (typeSerializers != null) {
            for (Class<? extends TypeSerializer> typeSerializer : typeSerializers) {
                try {
                    TypeSerializer instance = typeSerializer.newInstance();
                    mTypeSerializers.put(instance.getDeserializedType(), instance);
                } catch (InstantiationException e) {
                    if (DEBUG) {
                        LogHelper.e(TAG, "Couldn't instantiate TypeSerializer.", e);
                    }
                } catch (IllegalAccessException e) {
                    if (DEBUG) {
                        LogHelper.e(TAG, "IllegalAccessException", e);
                    }
                }
            }
        }

        return true;
    }

    private void scanForModel(Context context) throws IOException {
        LogHelper.d("skywalker", "scanForModel begin");
        String packageName = context.getPackageName();
        String sourcePath = context.getApplicationInfo().sourceDir;
        List<String> paths = new ArrayList<>();

        if (sourcePath != null && !(new File(sourcePath).isDirectory())) {
            DexFile dexfile = new DexFile(sourcePath);
            Enumeration<String> entries = dexfile.entries();

            while (entries.hasMoreElements()) {
                paths.add(entries.nextElement());
            }
        }
        // Robolectric fallback
        else {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            Enumeration<URL> resources = classLoader.getResources("");

            while (resources.hasMoreElements()) {
                String path = resources.nextElement().getFile();
                if (path.contains("bin") || path.contains("classes")) {
                    paths.add(path);
                }
            }
        }

        for (String path : paths) {
            File file = new File(path);
            scanForModelClasses(file, packageName, context.getClassLoader());
        }
        LogHelper.d("skywalker", "scanForModel end");
    }

    private void scanForModelClasses(File path, String packageName, ClassLoader classLoader) {
        if (path.isDirectory()) {
            for (File file : path.listFiles()) {
                scanForModelClasses(file, packageName, classLoader);
            }
        } else {
            String className = path.getName();

            // Robolectric fallback
            if (!path.getPath().equals(className)) {
                className = path.getPath();

                if (className.endsWith(".class")) {
                    className = className.substring(0, className.length() - 6);
                } else {
                    return;
                }

                className = className.replace(System.getProperty("file.separator"), ".");

                int packageNameIndex = className.lastIndexOf(packageName);
                if (packageNameIndex < 0) {
                    return;
                }

                className = className.substring(packageNameIndex);
            }

            try {
                Class<?> discoveredClass = Class.forName(className, false, classLoader);
                scanClassTableInfo(discoveredClass);
            } catch (ClassNotFoundException e) {
                if (DEBUG) {
                    LogHelper.e(TAG, "Couldn't create class.", e);
                }
            }
        }
    }

    private void scanClassTableInfo(Class<?> discoveredClass) {
        if (mTableInfos.containsKey(discoveredClass)) {
            return;
        }
        try {
            if (ReflectUtil.isModel(discoveredClass)) {
                @SuppressWarnings("unchecked")
                Class<? extends Model> modelClass = (Class<? extends Model>) discoveredClass;
                mTableInfos.put(modelClass, new TableInfo(modelClass));
            } else if (ReflectUtil.isTypeSerializer(discoveredClass)) {
                TypeSerializer instance = (TypeSerializer) discoveredClass.newInstance();
                mTypeSerializers.put(instance.getDeserializedType(), instance);
            }
        } catch (InstantiationException e) {
            if (DEBUG) {
                LogHelper.e(TAG, "Couldn't instantiate TypeSerializer.", e);
            }
        } catch (IllegalAccessException e) {
            if (DEBUG) {
                LogHelper.e(TAG, "IllegalAccessException", e);
            }
        }
    }
}

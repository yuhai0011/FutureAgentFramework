
package com.futureagent.lib.compat;

import android.os.IBinder;

import com.futureagent.lib.config.LibConfigs;
import com.futureagent.lib.utils.LogUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ServiceManagerUtils {

    private static final String TAG = "ServiceManagerUtils";
    private static final boolean DEBUG = LibConfigs.DEBUG_LOG;

    private static Class<?> sClass_ServiceManager;

    private static Method sMtd_getService;
    private static Method sMtd_checkService;
    private static Method sMtd_addService;
    private static Method sMtd_listServices;

    static {
        try {
            sClass_ServiceManager = Class.forName("android.os.ServiceManager", false,
                    Thread.currentThread().getContextClassLoader());
        } catch (ClassNotFoundException e) {
            if (DEBUG)
                LogUtils.w(TAG, "class not found", e);
        }
    }

    private ServiceManagerUtils() {
        // nothing to do
    }

    private static void reflect_getService() {
        if (sMtd_getService != null || sClass_ServiceManager == null) {
            return;
        }

        try {
            // public static IBinder getService(String name)
            sMtd_getService = sClass_ServiceManager.getMethod("getService", String.class);
        } catch (NoSuchMethodException e) {
            if (DEBUG)
                LogUtils.w(TAG, "method not found", e);
        }
    }

    /**
     * Returns a reference to a service with the given name.
     * <p>
     * Important: May block the calling thread!
     * </p>
     * 
     * @param name the name of the service to get
     * @return a reference to the service, or <code>null</code> if the service
     *         doesn't exist
     */
    public static IBinder getService(String name) {
        reflect_getService();
        if (sMtd_getService != null) {
            try {
                return (IBinder) sMtd_getService.invoke(null, name);
            } catch (IllegalAccessException e) {
                if (DEBUG)
                    LogUtils.w(TAG, "Failed to invoke #listServices()", e);
            } catch (InvocationTargetException e) {
                if (DEBUG)
                    LogUtils.w(TAG, "Failed to invoke #listServices()", e);
            }
        } else {
            if (DEBUG)
                LogUtils.w(TAG, "#getService() not available");
        }
        return null;
    }

    private static void reflect_checkService() {
        if (sMtd_checkService != null || sClass_ServiceManager == null) {
            return;
        }

        try {
            // public static IBinder checkService(String name)
            sMtd_checkService = sClass_ServiceManager.getMethod("checkService", String.class);
        } catch (NoSuchMethodException e) {
            if (DEBUG)
                LogUtils.w(TAG, "method not found", e);
        }
    }

    /**
     * Retrieve an existing service called @a name from the service manager.
     * Non-blocking.
     */
    public static IBinder checkService(String name) {
        reflect_checkService();
        if (sMtd_checkService != null) {
            try {
                return (IBinder) sMtd_checkService.invoke(null, name);
            } catch (IllegalAccessException e) {
                if (DEBUG)
                    LogUtils.w(TAG, "Failed to invoke #listServices()", e);
            } catch (InvocationTargetException e) {
                if (DEBUG)
                    LogUtils.w(TAG, "Failed to invoke #listServices()", e);
            }
        } else {
            if (DEBUG)
                LogUtils.w(TAG, "#checkService() not available");
        }
        return null;
    }

    private static void reflect_addService() {
        if (sMtd_addService != null || sClass_ServiceManager == null) {
            return;
        }

        try {
            // public static void addService(String name, IBinder service)
            sMtd_addService = sClass_ServiceManager.getMethod("addService",
                    String.class, IBinder.class);
        } catch (NoSuchMethodException e) {
            if (DEBUG)
                LogUtils.w(TAG, "method not found", e);
        }
    }

    /**
     * Place a new @a service called @a name into the service manager.
     * 
     * @param name the name of the new service
     * @param service the service object
     */
    public static void addService(String name, IBinder service) {
        reflect_addService();
        if (sMtd_addService != null) {
            try {
                sMtd_addService.invoke(null, name, service);
            } catch (IllegalAccessException e) {
                if (DEBUG)
                    LogUtils.w(TAG, "Failed to invoke #listServices()", e);
            } catch (InvocationTargetException e) {
                if (DEBUG)
                    LogUtils.w(TAG, "Failed to invoke #listServices()", e);
            }
        } else {
            if (DEBUG)
                LogUtils.w(TAG, "#addService() not available");
        }
    }

    private static void reflect_listServices() {
        if (sMtd_listServices != null || sClass_ServiceManager == null) {
            return;
        }

        try {
            // public static String[] listServices() throws RemoteException
            sMtd_listServices = sClass_ServiceManager.getMethod("listServices");
        } catch (NoSuchMethodException e) {
            if (DEBUG)
                LogUtils.w(TAG, "method not found", e);
        }
    }

    /**
     * Return a list of all currently running services.
     */
    public static String[] listServices() {
        reflect_listServices();
        if (sMtd_listServices != null) {
            try {
                return (String[]) sMtd_listServices.invoke(null);
            } catch (IllegalAccessException e) {
                if (DEBUG)
                    LogUtils.w(TAG, "Failed to invoke #listServices()", e);
            } catch (InvocationTargetException e) {
                if (DEBUG)
                    LogUtils.w(TAG, "Failed to invoke #listServices()", e);
            }
        } else {
            if (DEBUG)
                LogUtils.w(TAG, "#listServices() not available");
        }
        return null;
    }

}

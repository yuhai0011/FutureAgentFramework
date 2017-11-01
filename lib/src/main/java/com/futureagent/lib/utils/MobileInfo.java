package com.futureagent.lib.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Point;
import android.hardware.Camera;
import android.os.Build;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by skywalker on 15-6-30.
 */
public class MobileInfo {
    private final static String TAG = "MobileInfo";
    private static int idle2;
    private static int idle1;
    private static int cpuAll1;
    private static int cpuAll2;
    private static final String DEVICE_CPU_MIN_FREQ = "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_min_freq";
    private static final String DEVICE_CPU_MAX_FREQ = "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq";
    private static final String DEVICE_CPU_DIR = "/sys/devices/system/cpu/";

    private static final String DEVICE_LOW_MEMORY_KILLER = "/sys/module/lowmemorykiller/parameters/minfree";

    private static int sCpuCores = -1;
    public static final int ICE_CREAM_SANDWICH = 14;

    public static float getUsedROM(Context context) {
        float totalRom = StorageUtils.getInternalStorageTotalSize();
        float availabRom = StorageUtils.getInternalStorageAvailableSize();
        return (totalRom - availabRom) / totalRom;
    }

    public static float getUsedSD(Context context) {
        if (StorageUtils.externalStorageAvailable()) {
            float totalSD = StorageUtils.getExternalStorageTotalSize();
            float availabSD = StorageUtils.getExternalStorageAvailableSize();
            return (totalSD - availabSD) / totalSD;
        } else {
            return -1;
        }
    }

    public static int getCamerapixel() {
        try {
            Camera camera = Camera.open();
            int pixel = getCameraPixel(camera);
            if (camera != null) {
                camera.release();
            }
            return pixel;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int getCamerapixelFront() {
        try {
            int cameraNum = Camera.getNumberOfCameras();
            Camera.CameraInfo info = new Camera.CameraInfo();
            int cameraId = 0;
            for (; cameraId < cameraNum; cameraId++) {
                Camera.getCameraInfo(cameraId, info);
                if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    break;
                }
            }
            if (cameraId >= cameraNum) {
                return 0;
            }
            Camera camera = Camera.open(cameraId);
            int pixel = getCameraPixel(camera);
            if (camera != null) {
                camera.release();
            }
            return pixel;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private static int getCameraPixel(Camera camera) {
        if (camera != null) {
            Camera.Parameters parameters = camera.getParameters();
            List<Camera.Size> supportedPictureSizes = parameters.getSupportedPictureSizes();
            int maxHeight = supportedPictureSizes.get(0).height;
            int maxWidth = supportedPictureSizes.get(0).width;
            for (int i = 1; i < supportedPictureSizes.size(); i++) {
                if (maxHeight < supportedPictureSizes.get(i).height) {
                    maxHeight = supportedPictureSizes.get(i).height;
                    maxWidth = supportedPictureSizes.get(i).width;
                }
            }
            return maxHeight * maxWidth / 10000;
        } else {
            return 0;
        }
    }

    public static float getBigUsedSD(Context context) {
        if (StorageUtils.extraSDCardAvailable()) {
            float totalSD = StorageUtils.getExtraSDCardTotalSize();
            float availabSD = StorageUtils.getExtraSDCardAvailableSize();
            return (totalSD - availabSD) / totalSD;
        } else {
            return -1;
        }
    }

    public static int getDisplayDensity(Context cx) {
        DisplayMetrics dm = new DisplayMetrics();
        dm = cx.getResources().getDisplayMetrics();
        return dm.densityDpi;
    }

    public static DisplayMetrics getScreenMetrics(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        return dm;
    }

    public static int getScreenOrientation(Context context) {
        Configuration configuration = context.getResources().getConfiguration();
        return configuration.orientation;
    }

    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    public static Point getScreenHardwareSize(Context cxt) {
        WindowManager wm = (WindowManager) cxt.getSystemService(Context.WINDOW_SERVICE);
        Display screen = wm.getDefaultDisplay();
        Point pt = new Point();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            try {
                Method getRealSizeMethod = Display.class.getMethod("getRealSize",
                        Point.class);
                getRealSizeMethod.invoke(screen, pt);
            } catch (Exception e) {
                LogUtils.w(TAG, "Unexpected exception: ", e);
                screen.getSize(pt); // exclude window decor size (eg, statusbar)
            }
        } else {
            pt.x = screen.getWidth();
            pt.y = screen.getHeight();
        }
        return pt;
    }

    public static float dp2px(Context cxt, float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                cxt.getResources().getDisplayMetrics());
    }

    private static String cutBlank(String str) {
        str = str.trim();
        while (str.startsWith(" ")) {
            str = str.substring(1, str.length()).trim();
        }
        while (str.endsWith(" ")) {
            str = str.substring(0, str.length() - 1).trim();
        }
        return str;

    }

    public static String getModel() {
        return Build.MODEL;
    }

    public static boolean isMiOnePlus() {
        return "Xiaomi".equals(Build.MANUFACTURER) && "mione_plus".equals(Build.PRODUCT);
    }

    public static boolean isMiuiV5() {
        if (!"Xiaomi".equals(Build.MANUFACTURER)) {
            return false;
        }
        try {
            Class<?> spClazz = Class.forName("android.os.SystemProperties");
            Method method = spClazz.getDeclaredMethod("get", String.class, String.class);
            String property = (String) method.invoke(spClazz, "ro.miui.ui.version.name", null);
            if (!TextUtils.isEmpty(property) && "V5".equals(property)) {
                return true;
            }
            // May be we can use logic.
            /*int value = Integer.parseInt(property.toLowerCase().substring(1));
            if (value >= 5) {
                return true;
            }*/

            // This logic work well too.
           /* try {
                Process p = Runtime.getRuntime().exec("getprop ro.miui.ui.version.name");
                BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line = input.readLine();
                if (!TextUtils.isEmpty(line) && "V5".equals(line))) {
                    return true;
                }
                input.close();
            } catch (IOException e) {
                e.printStackTrace();
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 判断miui v5中悬浮窗是否开启
     * @return
     */
    public static boolean getMiuiFloatWindowPermission(Context ctx, String pkgName) {
        if (!MobileInfo.isMiuiV5()) {
            return true;
        }
        try {
            ApplicationInfo applicationInfo = ctx.getPackageManager().getApplicationInfo(
                    pkgName, 0);
            // 判断标志位0x8000000：标志位标志为1时可以显示悬浮窗，为0时不能显示
            if (((applicationInfo.flags & 0x8000000) == 0)) {
                return false;// 未开启悬浮窗
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static String getCPUModel() {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec("cat /proc/cpuinfo");
        } catch (IOException e) {
            e.printStackTrace();
        }
        InputStream outs = process.getInputStream();
        InputStreamReader isrout = new InputStreamReader(outs);

        BufferedReader brout = new BufferedReader(isrout, 8 * 1024);
        String line;
        String cpu_model = null;
        try {
            while ((line = brout.readLine()) != null) {
                if (line.startsWith("Processor")) {
                    cpu_model = line.substring(line.indexOf(":") + 1, line.length());
                    cpu_model = cpu_model.trim();
                    if (!TextUtils.isDigitsOnly(cpu_model)) {
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cpu_model;
    }

    // Numbers of Cpu core
    public static int getCpuNumber() {
        if (sCpuCores < 0) {
            int cpu = 0;
            for (int i = 0; i < 10; i++) {
                File f = new File(DEVICE_CPU_DIR, "cpu" + i);
                if (f.exists() && f.isDirectory()) {
                    cpu++;
                } else {
                    break;
                }
            }
            sCpuCores = cpu;
        }
        return sCpuCores;
    }

    /**
     * Get the maximum low memory threshold of the system default config
     * @return In MB
     */
    private static int getDefaultLowMemoryKillerThreshold() {
        // unit of the following values is "4KB"
        // In order: foreground, visible, background, invisible, provider, empty
        // GB: 2048,3072,4096,6144,7168,8192
        // HC: 8192,10240,12288,14336,16384,20480
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            return 32;
        } else {
            return 80;
        }
    }
}

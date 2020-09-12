package com.futureagent.lib.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.futureagent.lib.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author skywalker
 * @date 15/12/18
 * @description
 * @Email: yuhai833@126.com
 */
public class IdManager {
    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

    private static String mDeviceId = "";

    /**
     * Generate a value suitable for use in setId().
     * This value will not collide with ID values generated at build release_datetime by aapt for R.id.
     *
     * @return a generated ID value
     */
    public static int generateViewId() {
        for (;;) {
            final int result = sNextGeneratedId.get();
            // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
            if (sNextGeneratedId.compareAndSet(result, newValue)) {
                return result;
            }
        }
    }

    /**
     * 新的设备id方法
     * @param context
     * @return
     */
    @SuppressLint("HardwareIds")
    public synchronized static String getDeviceId(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && !TextUtils.isEmpty(getFileDeviceId(context))) {
                mDeviceId = getAndCheckFileDeviceId(context);
                LogUtils.i("getDeviceId", "NowDeviceId:" + mDeviceId);
                return mDeviceId;
            }
        } else {
            if (!TextUtils.isEmpty(getFileDeviceId(context))) {
                mDeviceId = getAndCheckFileDeviceId(context);
                LogUtils.i("getDeviceId", "NowDeviceId:" + mDeviceId);
                return mDeviceId;
            }
        }
        mDeviceId = getPrefDeviceId(context);
        if (TextUtils.isEmpty(mDeviceId)) {
            String serial = "";
            String android_id = "";
            String uuid = "";
            String m_szDevIDShort = "future_agent" + Build.BOARD.length() % 10 + Build.BRAND.length() % 10 +
                    Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10 +
                    Build.DISPLAY.length() % 10 + Build.HOST.length() % 10 +
                    Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10 +
                    Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10 +
                    Build.TAGS.length() % 10 + Build.TYPE.length() % 10 +
                    Build.USER.length() % 10;
            try {
                serial = android.os.Build.class.getField("SERIAL").get(null).toString();
                android_id = "" + Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
                uuid = new UUID(m_szDevIDShort.hashCode(), serial.hashCode() | android_id.hashCode()).toString();
            } catch (Exception exception) {
                //serial需要一个初始化
                serial = "future_agent_serial"; // 给一个初始化值
                android_id = "" + Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
                uuid = new UUID(m_szDevIDShort.hashCode(), serial.hashCode() | android_id.hashCode()).toString();
            }
            //生成的deviceId加密一次
            mDeviceId = SignFactory.getFinalDeviceId(uuid);
            saveStoreDeviceId(context, mDeviceId);
        } else {
            if (mDeviceId.length() != 32) {
                mDeviceId = getAndCheckFileDeviceId(context);
                savePrefDeviceId(context, mDeviceId);
            }
            saveFileDeviceId(context, mDeviceId);
        }
        LogUtils.i("getDeviceId", "NowDeviceId:" + mDeviceId);
        return mDeviceId;
    }

    /**
     * 获取和校验文件中的deviceid
     * @param context
     * @return
     */
    private synchronized static String getAndCheckFileDeviceId(Context context) {
        String deviceId = getFileDeviceId(context);
        if (deviceId.length() != 32) {
            //生成的deviceId加密一次
            deviceId = SignFactory.getFinalDeviceId(deviceId);
            saveStoreDeviceId(context, deviceId);
        }
        LogUtils.i("getAndCheckFileDeviceId", "deviceId:" + deviceId);
        return deviceId;
    }

    private static String getStoreDeviceId(Context context) {
        String deviceId = getFileDeviceId(context);
        if (!TextUtils.isEmpty(deviceId)) {
            savePrefDeviceId(context, deviceId);
            return deviceId;
        }
        deviceId = getPrefDeviceId(context);
        saveFileDeviceId(context, deviceId);
        return deviceId;
    }

    private static void saveStoreDeviceId(Context context, String deviceId) {
        saveFileDeviceId(context, deviceId);
        savePrefDeviceId(context, deviceId);
    }

    private static String getFileDeviceId(Context context) {
        String fileName = "id";
        String deviceId = "";
        if (FileUtils.isExternalStorageAvailable()) {
            File idFile = new File(FileUtils.getPublicIdDir(context), fileName);

            if (idFile.exists()) {
                // 把数据读出来
                try {
                    int length = (int) idFile.length();
                    byte[] bytes = new byte[length];
                    FileInputStream in = new FileInputStream(idFile);
                    try {
                        in.read(bytes);
                    } finally {
                        in.close();
                    }
                    deviceId = new String(bytes);
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                } catch (IOException e2) {
                    e2.printStackTrace();
                } catch (Exception e3) {
                    e3.printStackTrace();
                }
            }
        }
        return deviceId;
    }

    private static boolean saveFileDeviceId(Context context, String deviceId) {
        String fileName = "id";
        if (FileUtils.isExternalStorageAvailable()) {
            try {
                File idFile = new File(FileUtils.getPublicIdDir(context), fileName);
                if (idFile.exists()) {
                    idFile.delete();
                }
                idFile.createNewFile();
                FileOutputStream fileOutputStream = new FileOutputStream(idFile);
                fileOutputStream.write(deviceId.getBytes());
                fileOutputStream.close();
                return true;
            } catch (Exception e1) {
                e1.printStackTrace();
                return false;
            }
        }
        return false;
    }

    private static String getPrefDeviceId(Context context) {
        return PrefManager.getString(context, context.getString(R.string.preference_device_id), "");
    }

    private static void savePrefDeviceId(Context context, String deviceId) {
        // 存在pref里面
        PrefManager.putString(context, context.getString(R.string.preference_device_id), deviceId);
    }

    /**
     * 获取IMEI
     *
     * @param context
     * @return
     */
    public static String getIMEI(Context context) {
        final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (tm != null) {
            try {
                return tm.getDeviceId();
            } catch (SecurityException e) {
                return "no permission";
            }
        } else {
            return "";
        }
    }
}

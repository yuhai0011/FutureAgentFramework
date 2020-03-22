package com.futureagent.lib.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.text.TextUtils;

import com.futureagent.lib.config.ShareConfig;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;

/**
 * @author skywalker on 2018/6/22.
 * Email: skywalker@thecover.co
 * Description:
 */
public class DeviceUtil {
    private static final String TAG = "DeviceUtil";
    private static final String marshmallowMacAddress = "02:00:00:00:00:00";
    private static final String fileAddressMac = "/sys/class/net/wlan0/address";


    private static String mDeviceId;
    /**
     * 获取基站信息
     *
     * @return
     */
    public static String getCellInfo(Context context) {
        TelephonyManager mTelNet = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (mTelNet == null) {
            return "";
        }
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return "";
        } else {
            try {


                GsmCellLocation location = (GsmCellLocation) mTelNet.getCellLocation();
                if (location == null) {
                    return "";
                }

                String operator = mTelNet.getNetworkOperator();
                int mcc = Integer.parseInt(operator.substring(0, 3));
                int mnc = Integer.parseInt(operator.substring(3));
                int cid = location.getCid();
                int lac = location.getLac();
                return mcc + ":" + mnc + ":" + cid + ":" + lac;
            } catch (Exception e) {
                return "";
            }
        }
    }

    /**
     * 获取device id
     *
     * @return
     */
    public static String getDeviceId(Context context) {
        if (TextUtils.isEmpty(mDeviceId)) {

            final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            final String fileName = "id";
            try {
                // 旧版本生成device_id
                final String tmDevice, tmSerial, androidId;
                tmDevice = "" + tm.getDeviceId();
                tmSerial = "" + tm.getSimSerialNumber();
                androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

                UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
                String deviceId = deviceUuid.toString();

                LogUtils.d("deviceId", deviceId);
                mDeviceId = deviceId;
            } catch (Exception e) {

                // 兼容无权限的情况
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
                            mDeviceId = new String(bytes);
                        } catch (FileNotFoundException e1) {
                            e1.printStackTrace();
                        } catch (IOException e2) {
                            e2.printStackTrace();
                        } catch (Exception e3) {
                            e3.printStackTrace();
                        }
                    }
                }
            }

            // 如果上述操作都失败了，则重新生成
            if (TextUtils.isEmpty(mDeviceId)) {
                String deviceId = PrefManager.getString(context, ShareConfig.SHARE_KEY_DEVICE_ID, "");
                if (!TextUtils.isEmpty(deviceId)) {
                    mDeviceId = deviceId;
                } else {
                    mDeviceId = UUID.randomUUID().toString();
                    // 把数据存起来
                    try {
                        File idFile = new File(FileUtils.getPublicIdDir(context), fileName);
                        idFile.createNewFile();
                        FileOutputStream fileOutputStream = new FileOutputStream(idFile);
                        fileOutputStream.write(mDeviceId.getBytes());
                        fileOutputStream.close();

                    } catch (Exception e1) {
                        e1.printStackTrace();

                        // 存在pref里面
                        PrefManager.putString(context, ShareConfig.SHARE_KEY_DEVICE_ID, mDeviceId);
                    }
                }
            }
        }
        return mDeviceId;
    }

    /**
     * 获取wifi ssid
     *
     * @param context
     * @return
     */
    public static String getWifiSSID(Context context) {
        if (!NetWorkUtil.TYPE_WIFI.equals(NetWorkUtil.getConnectType(context))) {
            return "";
        }
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager == null) {
            return "";
        }
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo == null) {
            return "";
        }
        return wifiInfo.getSSID();
    }

    /**
     * 获取wifi bssid
     *
     * @param context
     * @return
     */
    public static String getWifiBSSID(Context context) {
        if (!NetWorkUtil.TYPE_WIFI.equals(NetWorkUtil.getConnectType(context))) {
            return "";
        }
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager == null) {
            return "";
        }
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo == null) {
            return "";
        }
        return wifiInfo.getBSSID();
    }

    public static String getWifiMacAddress(Context context) {
        if (!NetWorkUtil.TYPE_WIFI.equals(NetWorkUtil.getConnectType(context))) {
            return "";
        }
        WifiManager wifiMan = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiMan == null) {
            return "";
        }
        WifiInfo wifiInf = wifiMan.getConnectionInfo();

        if (wifiInf != null && marshmallowMacAddress.equals(wifiInf.getMacAddress())) {
            String result;
            try {
                result = getAddressMacByInterface();
                if (result != null) {
                    return result;
                } else {
                    result = getAddressMacByFile(wifiMan);
                    return result;
                }
            } catch (IOException e) {
                LogUtils.e(TAG, "Erreur lecture propriete Adresse MAC");
            } catch (Exception e) {
                LogUtils.e(TAG, "Erreur lecture propriete Adresse MAC ");
            }
        } else {
            if (wifiInf != null && wifiInf.getMacAddress() != null) {
                return wifiInf.getMacAddress();
            } else {
                return "";
            }
        }
        return marshmallowMacAddress;
    }

    private static String getAddressMacByInterface() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (nif.getName().equalsIgnoreCase("wlan0")) {
                    byte[] macBytes = nif.getHardwareAddress();
                    if (macBytes == null) {
                        return "";
                    }

                    StringBuilder res1 = new StringBuilder();
                    for (byte b : macBytes) {
                        res1.append(String.format("%02X:", b));
                    }

                    if (res1.length() > 0) {
                        res1.deleteCharAt(res1.length() - 1);
                    }
                    return res1.toString();
                }
            }

        } catch (Exception e) {
            LogUtils.e(TAG, "Erreur lecture propriete Adresse MAC ");
        }
        return null;
    }

    private static String getAddressMacByFile(WifiManager wifiMan) throws Exception {
        String ret;
        int wifiState = wifiMan.getWifiState();

        wifiMan.setWifiEnabled(true);
        File fl = new File(fileAddressMac);
        FileInputStream fin = new FileInputStream(fl);
        ret = crunchifyGetStringFromStream(fin);
        fin.close();

        boolean enabled = WifiManager.WIFI_STATE_ENABLED == wifiState;
        wifiMan.setWifiEnabled(enabled);
        return ret;
    }

    private static String crunchifyGetStringFromStream(InputStream crunchifyStream) throws IOException {
        if (crunchifyStream != null) {
            Writer crunchifyWriter = new StringWriter();

            char[] crunchifyBuffer = new char[2048];
            try {
                Reader crunchifyReader = new BufferedReader(new InputStreamReader(crunchifyStream, "UTF-8"));
                int counter;
                while ((counter = crunchifyReader.read(crunchifyBuffer)) != -1) {
                    crunchifyWriter.write(crunchifyBuffer, 0, counter);
                }
            } finally {
                crunchifyStream.close();
            }
            return crunchifyWriter.toString();
        } else {
            return "";
        }
    }

    public static String getIpAddress(Context context) {
        ConnectivityManager connectivityManager = ((ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE));
        if (connectivityManager == null) {
            return "0.0.0.0";
        }
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            // 3/4g网络
            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                try {
                    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                        NetworkInterface intf = en.nextElement();
                        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                            InetAddress inetAddress = enumIpAddr.nextElement();
                            if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                                return inetAddress.getHostAddress();
                            }
                        }
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                }

            } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                //  wifi网络
                WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                if (wifiManager == null) {
                    return "0.0.0.0";
                }
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                return intIP2StringIP(wifiInfo.getIpAddress());
            } else if (info.getType() == ConnectivityManager.TYPE_ETHERNET) {
                // 有限网络
                return getLocalIp();
            }
        }
        return "0.0.0.0";
    }

    private static String intIP2StringIP(int ip) {
        return (ip & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                (ip >> 24 & 0xFF);
    }


    // 获取有限网IP
    private static String getLocalIp() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()
                            && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
        return "0.0.0.0";
    }

    public static String getPhoneMacAddress(Context context) {
        String mac_s = "";
        StringBuilder buf = new StringBuilder();
        try {
            byte[] mac;
            NetworkInterface ne = NetworkInterface.getByInetAddress(InetAddress.getByName(getIpAddress(context)));
            mac = ne.getHardwareAddress();
            for (byte b : mac) {
                buf.append(String.format("%02X:", b));
            }
            if (buf.length() > 0) {
                buf.deleteCharAt(buf.length() - 1);
            }
            mac_s = buf.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mac_s;
    }
}

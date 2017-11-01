
package com.futureagent.lib.utils;

import android.annotation.TargetApi;
import android.os.Build;
import android.util.Base64;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DigestEncodingUtils {
    private static final String TAG = "DigestEncodingUtils";

    private static final char[] HEXCHARS = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
    };

    /**
     * Encode the bytes with hex string (lower case)
     * 
     * @param data
     * @return
     */
    public static String encodeHexString(byte[] data) {
        StringBuilder sb = new StringBuilder(data.length * 2);
        for (int i = 0; i < data.length; i++) {
            sb.append(HEXCHARS[(data[i] & 0xf0) >>> 4]);
            sb.append(HEXCHARS[data[i] & 0x0f]);
        }
        return sb.toString();
    }

    /**
     * Encode the bytes with Base64 (Standard encode & no wrap)</p> Important:
     * Only supported by Android 2.2 and higher versions!</p>
     * 
     * @param data
     * @return
     */
    @TargetApi(Build.VERSION_CODES.FROYO)
    public static String encodeBase64String(byte[] data) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO) {
            LogHelper.e(TAG, "This API is not supported on platforms older than Froyo");
            return null;
        }
        return Base64.encodeToString(data, Base64.NO_WRAP);
    }

    /**
     * Compute MD5 digest with hex encoding (lower case)
     * 
     * @return null may be returned if unexpected exceptions happen
     */
    public static String computeMd5HexString(byte[] data) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(data);
            return encodeHexString(md5.digest());
        } catch (NoSuchAlgorithmException e) {
            LogHelper.w(TAG, "Should never happen!", e);
        }
        return null;
    }

    /**
     * Compute MD5 digest with hex encoding (lower case)</p> Using
     * {@link String#getBytes(String)} with "UTF-8" to convert String to
     * byte[]</p>
     * 
     * @return null may be returned if unexpected exceptions happen
     */
    public static String computeMd5HexString(String data) {
        try {
            return computeMd5HexString(data.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            LogHelper.w(TAG, "should never happen!", e);
        }
        return null;
    }

    /**
     * Compute MD5 digest with hex encoding (lower case)
     * 
     * @return null may be returned if unexpected exceptions happen
     */
    public static String computeMd5HexString(InputStream is) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            int len;
            byte buffer[] = new byte[32 * 1024]; // 32 KB
            while ((len = is.read(buffer)) != -1) {
                messageDigest.update(buffer, 0, len);
            }
            return encodeHexString(messageDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            LogHelper.w(TAG, "Should never happen!", e);
        } catch (IOException e) {
            LogHelper.w(TAG, "unexpected exception happened", e);
        }

        return null;
    }

    /**
     * Compute file MD5 digest with hex encoding (lower case)
     * 
     * @return null may be returned if the file not found or other unexpected
     *         exceptions happen
     */
    public static String computeFileMd5HexString(String filepath) {
        File apkFile = new File(filepath);
        if (!apkFile.exists()) {
            return null;
        }
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(apkFile);
            return computeMd5HexString(fis);
        } catch (FileNotFoundException e) {
            LogHelper.w(TAG, "should not happen", e);
        } finally {
            FileUtils.close(fis);
        }
        return null;
    }

}

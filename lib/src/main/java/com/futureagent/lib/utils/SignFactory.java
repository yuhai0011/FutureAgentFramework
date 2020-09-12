package com.futureagent.lib.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SignFactory {

    /**
     * md5加密deviceId
     * @param orgDeviceId
     * @return
     */
    public static String getFinalDeviceId(String orgDeviceId) {
        return shut(orgDeviceId);
    }

    /**
     * MD5加密字符串
     *
     * @param val
     * @return
     * @throws java.security.NoSuchAlgorithmException
     */
    public static String shut(String val) {
        return shut(val, "MD5");
    }

    /**
     * MD5加密字符串
     *
     * @param val
     * @return
     * @throws java.security.NoSuchAlgorithmException
     */
    public static String shut(String val, String type) {
        byte[] hash;
        try {
            hash = MessageDigest.getInstance(type).digest(val.getBytes("UTF-8"));

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("shut should not be supported!", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UTF-8 should not be supported!", e);
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10) {
                hex.append("0");
            }
            hex.append(Integer.toHexString(b & 0xFF));
        }

        return hex.toString().toUpperCase();
    }
}

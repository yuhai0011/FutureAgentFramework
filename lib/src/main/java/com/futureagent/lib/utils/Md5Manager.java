package com.futureagent.lib.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author skywalker
 * @date 15/12/18
 * @description
 * @Email: yuhai833@126.com
 */
public class Md5Manager {

    private Md5Manager() {}

    /**
     * MD5加密字符串
     * @param val
     * @return
     * @throws java.security.NoSuchAlgorithmException
     */
    public static String MD5(String val)  {
        byte[] hash;

        try {
            hash = MessageDigest.getInstance("MD5").digest(val.getBytes("UTF-8"));

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 should not be supported!", e);
        }catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UTF-8 should not be supported!", e);
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if((b & 0xFF) < 0x10) hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }

        return hex.toString().toUpperCase();
    }
}

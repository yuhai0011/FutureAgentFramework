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
public class Sha1Manager {

    private Sha1Manager() {}

    private static String convertToHex(byte[] data) {
        StringBuilder buf = new StringBuilder();
        for (byte b : data) {
            int halfbyte = (b >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                buf.append((0 <= halfbyte) && (halfbyte <= 9) ? (char) ('0' + halfbyte) : (char) ('a' + (halfbyte - 10)));
                halfbyte = b & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }

    public static String SHA1(String text) {

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(text.getBytes("iso-8859-1"), 0, text.length());
            byte[] sha1hash = md.digest();
            return convertToHex(sha1hash);

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA1 should not be supported!", e);
        }catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UTF-8 should not be supported!", e);
        }
    }
}

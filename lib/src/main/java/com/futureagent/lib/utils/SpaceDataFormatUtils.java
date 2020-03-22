
package com.futureagent.lib.utils;

import java.text.DecimalFormat;

public class SpaceDataFormatUtils {
    public static String spaceFormatBytes(long size, boolean hasByte) {
        if (size >= 1048051712l) { // 1048051712l=1024*1024*999.5
            // in GB
            float gbSize = size / (1024 * 1024 * 1024f);
            DecimalFormat formatter = getFormat(gbSize);
            return formatter.format(gbSize) + "G" + (hasByte ? "B" : "");
        } else if (size >= 1023488l) { // 1023488=1024*999.5
            // in MB
            float mbSize = size / (1024 * 1024f);
            DecimalFormat formatter = getFormat(mbSize);
            return formatter.format(mbSize) + "M" + (hasByte ? "B" : "");
        } else if (size >= 1000) {
            // in KB
            float kbSize = size / 1014f;
            DecimalFormat formatter = getFormat(kbSize);
            return formatter.format(kbSize) + "K" + (hasByte ? "B" : "");
        } else {
            return size + "B";
        }
    }

    public static String[] getSpaceFormatSizeAndUnit(long size, boolean hasByte) {
        if (size < 0) {
            size = Math.abs(size);
        }
        if (size >= 1048051712l) { // 1048051712l=1024*1024*999.5
            // in GB
            float gbSize = size / (1024 * 1024 * 1024f);
            DecimalFormat formatter = getFormat(gbSize);
            return new String[] {
                    formatter.format(gbSize), "G" + (hasByte ? "B" : "")
            };
        } else if (size >= 1023488l) { // 1023488=1024*999.5
            // in MB
            float mbSize = size / (1024 * 1024f);
            DecimalFormat formatter = getFormat(mbSize);
            return new String[] {
                    formatter.format(mbSize), "M" + (hasByte ? "B" : "")
            };
        } else if (size >= 1000) {
            // in KB
            float kbSize = size / 1014f;
            DecimalFormat formatter = getFormat(kbSize);
            return new String[] {
                    formatter.format(kbSize), "K" + (hasByte ? "B" : "")
            };
        } else {
            return new String[] {
                    String.valueOf(size), "B"
            };
        }
    }

    /*
     * size must less than 1000
     */
    private static DecimalFormat getFormat(float size) {
        DecimalFormat decimal;
        if (size < 10) {
            decimal = new DecimalFormat("#0.00");
        } else if (size < 100) {
            decimal = new DecimalFormat("#0.0");
        } else {
            decimal = new DecimalFormat("#0");
        }
        return decimal;
    }
}

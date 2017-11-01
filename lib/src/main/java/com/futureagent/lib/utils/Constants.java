
package com.futureagent.lib.utils;

import com.futureagent.lib.config.LibConfigs;

public class Constants {
    private static final boolean ON_LINE = LibConfigs.ON_LINE;
    public static final long MILLISECOND = 1000l;
    public static final long MINUTE_MS = MILLISECOND * 60;
    public static final long HOUR_MS = MINUTE_MS * 60;
    public static final long HALF_DAY_MS = HOUR_MS * 12;
    public static final long DAY_MS = HOUR_MS * 24;
    public static final long WEEK_MS = DAY_MS * 7;
    public static final long TWO_MINUTE_MS = MINUTE_MS * 2;
    public static final long FIVE_MINUTES_MS = MINUTE_MS * 5;
    public static final long TEN_MINUTES_MS = MINUTE_MS * 10;
    public static final long HALF_HOUR_MS = MINUTE_MS * 30;
    public static final long FOUR_HOUR_MS = MINUTE_MS * 60 * 4;

    public static final String REAL_PACKAGE_NAME = "com.futureagent.phoneacc";

    public static String APP_REPORT_URL;

    static {
        if (ON_LINE) {
            APP_REPORT_URL = "http://www.futureagent.com";
        } else {
            APP_REPORT_URL = "http://www.futureagent.com";
        }
    }


}

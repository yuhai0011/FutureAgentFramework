package com.futureagent.lib.utils;

import java.util.List;

/**
 * @author skywalker
 * @date 15/12/18
 * @description
 * @Email: yuhai833@126.com
 */
public class ListUtils {

    public static boolean isEmpty(List list) {
        if (list == null || list.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isEmpty(String val) {
        if (val == null || val.matches("\\s") || val.length() == 0
                || "null".equalsIgnoreCase(val)) {
            return true;
        }
        return false;
    }
}

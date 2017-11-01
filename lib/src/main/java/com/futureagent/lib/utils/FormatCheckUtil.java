package com.futureagent.lib.utils;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author skywalker
 * @date 15/12/18
 * @description
 * @Email: yuhai833@126.com
 */
public class FormatCheckUtil {

    /**
     * 判断手机是否合法
     *
     * @param mobiles
     * @return
     */
    public static boolean isMobileNO(String mobiles) {
        if (TextUtils.isEmpty(mobiles)) {
            return false;
        }
        if (mobiles.trim().length() != 11) {
            return false;
        } else {
            return true;
        }
//        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
//        Matcher m = p.matcher(mobiles);
//        return m.matches();
    }

    /**
     * 判断邮箱是否合法
     *
     * @param email
     * @return
     */
    public static boolean isEmail(String email) {
        if (TextUtils.isEmpty(email)) {
            return false;
        }
        Pattern p = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
        Matcher m = p.matcher(email);
        return m.matches();
    }


    /**
     * 密码合法检测
     *
     * @param pwd
     * @return
     */
    public static boolean isValidPwd(String pwd) {
        if (TextUtils.isEmpty(pwd)) {
            return false;
        }
        return !(pwd.length() < 6 || pwd.length() > 16);
    }

}

package com.futureagent.lib.utils;

import android.annotation.SuppressLint;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


/**
 * @author skywalker
 * @date 15/12/18
 * @description
 * @Email: yuhai833@126.com
 */
@SuppressLint("SimpleDateFormat")
public class DateUtil {

    /**
     * riqi
     */
    private static Calendar sCalendar = Calendar.getInstance();

    private static SimpleDateFormat mDefaultFormat = new SimpleDateFormat();

    /**
     * 如：2012-12-12 12:12:12
     */
    private static SimpleDateFormat commonDateFormat = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss");
    /**
     * 如：20121212121212 场景:时间生成序列号
     */
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
    /**
     * 如：2012-12-12
     */
    private static SimpleDateFormat ymdDateFormat = new SimpleDateFormat(
            "yyyy-MM-dd");
    /**
     * 如：2012-12
     */
    private static SimpleDateFormat ymDateFormat = new SimpleDateFormat(
            "yyyy-MM");

    /**
     * 如：2012年12月12日
     */
    private static SimpleDateFormat ymdCHDateFormat = new SimpleDateFormat(
            "yyyy年MM月dd日");

    /**
     * 如：2012年12月
     */
    private static SimpleDateFormat ymCHDateFormat = new SimpleDateFormat(
            "yyyy年MM月");
    /**
     * 如：2012年12月
     */
    private static SimpleDateFormat ymCHSpaceDateFormat = new SimpleDateFormat(
            "yyyy 年 MM 月 ");

    /**
     * 如：12月30日
     */
    private static SimpleDateFormat mdCHDateFormat = new SimpleDateFormat(
            "MM月dd日");

    /**
     * 如：12:12 场景：12点12分
     */
    private static SimpleDateFormat hmDateFormat = new SimpleDateFormat("HH:mm");
    /**
     * 日期前加0
     */
    private static DecimalFormat mDayFormat = new DecimalFormat("00");


    /**
     * Cover
     * 获取封面新闻规定的时间
     */
    public static String getCoverFormatTime(long timestamp) {

        long currentTimestamp = new Date().getTime();
        long timeRelative = currentTimestamp - timestamp;

        final long ONE_SECOND = 1000;
        final long ONE_MINUTE = ONE_SECOND * 60;
        final long ONE_HOUR = ONE_MINUTE * 60;
        final long ONE_DAY = ONE_HOUR * 24;
        final long ONE_YEAR = ONE_DAY * 365;

        if (timeRelative <= ONE_SECOND) {
            return "1秒前";
        }
        // 一分钟内
        else if (timeRelative < ONE_MINUTE) {
            return (int)(timeRelative / 1000) + "秒前";
        }
        // 一小时内
        else if (timeRelative < ONE_HOUR) {
            return (int)(timeRelative / 1000 / 60) + "分钟前";
        }
        // 一天内
        else if (timeRelative < ONE_DAY) {
            return (int)(timeRelative / 1000 / 60 / 60) + "小时前";
        }
        // 一年内
        else if (timeRelative < ONE_YEAR) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd HH:mm");
            return simpleDateFormat.format(new Date(timestamp));
        }
        // 超过一年
        else {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            return simpleDateFormat.format(new Date(timestamp));
        }
    }

    /**
     * Cover
     * 获取当前年份
     * @return
     */
    public static int currentYear() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }

    /**
     * Cover
     * 给定year，monthOfYear，dayOfMonth
     * 获取对应的timestamp
     * @param year
     * @param monthOfYear
     * @param dayOfMonth
     * @return
     */
    public static long timestampFromYearMonthDay(int year, int monthOfYear, int dayOfMonth) {
        return new GregorianCalendar(year, monthOfYear, dayOfMonth).getTimeInMillis();
    }

    /**
     * Cover
     * 从生日时间获取对应的年份
     * @param birthday
     * @return
     */
    public static int yearFromBirthday(long birthday) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(birthday);
        return calendar.get(Calendar.YEAR);
    }

    /**
     * Cover
     * 从生日时间获取对应的月份
     * @param birthday
     * @return
     */
    public static int monthOfYearFromBirthday(long birthday) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(birthday);
        return calendar.get(Calendar.MONTH);
    }

    /**
     * Cover
     * 从生日时间获取对应的天
     * @param birthday
     * @return
     */
    public static int dayOfMonthFromBirthday(long birthday) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(birthday);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 返回自定义格式的时间
     *
     * @param pattern
     * @param date
     * @return
     */
    public static String format(String pattern, Date date) {
        if (date == null) {
            return null;
        }
        mDefaultFormat.applyPattern(pattern);
        return mDefaultFormat.format(date);
    }

    /**
     * 返回自定义格式的时间
     *
     * @param pattern
     * @param date
     * @return
     */
    public static Date parse(String pattern, String date) {
        if (TextUtils.isEmpty(date)) {
            return null;
        }
        mDefaultFormat.applyPattern(pattern);
        try {
            return mDefaultFormat.parse(date);
        } catch (ParseException e) {
        }
        return null;
    }

    /**
     * 根据字符串返回字符串日期
     *
     * @param curPattern 当前字符串格式类型
     * @param tarPattern 目标字符串格式类型
     * @param date
     * @return

     */
    public static String parseFormat(String curPattern, String tarPattern,
                                     String date) {
        return format(tarPattern, parse(curPattern, date));
    }

    /**
     * 日期转换成字符
     *
     * @param date
     * @return

     */
    public static String format(Date date) {
        return commonDateFormat.format(date);
    }

    /**
     * 构造年月日日期字符串
     *
     * @param date
     * @return

     */
    public static String formatYMD(Date date) {
        return ymdDateFormat.format(date);
    }

    /**
     * 构造年月日期字符串
     *
     * @param date
     * @return
     */
    public static String formatYM(Date date) {
        return ymDateFormat.format(date);
    }

    /**
     * 单位日期前加0
     *
     * @param number
     * @return
     */
    public static String formatDateNum(int number) {
        return mDayFormat.format(number);
    }

    /**
     * 可扩展的日期格式函数
     *
     * @param date
     * @param dateFormat
     * @return
     */
    public static String format(Date date, SimpleDateFormat dateFormat) {
        return dateFormat.format(date);
    }

    /**
     * 字符转换成日期
     *
     * @param date
     * @return
     * @throws ParseException

     */
    public static Date parse(String date) throws ParseException {
        return commonDateFormat.parse(date);
    }

    /**
     * 字符转换成年月日日期
     *
     * @param date
     * @return
     * @throws ParseException
     */
    public static Date parseYMD(String date) throws ParseException {
        return ymdDateFormat.parse(date + " 00:00:00");
    }

    /**
     * 获取系统时间
     *
     * @return

     */
    public static String getSysDate() {
        return format(new Date(), commonDateFormat);
    }

    /**
     * 获取不带分隔符的系统时间
     *
     * @return
     */
    public static String getSysDateVersion() {
        return format(new Date(), sdf);
    }

    /**
     * 获取不带分隔符的系统时间
     *
     * @return

     */
    public static String getDateVersion(Date date) {
        return format(date, sdf);
    }

    /**
     * 计算当前日期与规定日期的时间差
     *
     * @param dateString datePoint 2014-01-23 12:22:00
     */
    public static int getLeftDays(String dateString, String datePoint) {
        if (TextUtils.isEmpty(dateString)) {
            return 0;
        }
        long days = 0;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Calendar cal1 = Calendar.getInstance();
            cal1.setTime(sdf.parse(dateString));
            Calendar cal2 = Calendar.getInstance();
            cal2.setTime(sdf.parse(datePoint));
            long l = cal1.getTimeInMillis() - cal2.getTimeInMillis();
            days = Long.valueOf(l / (1000 * 60 * 60 * 24));
        } catch (ParseException e) {
            // e.printStackTrace();
        }

        return (int) days;
    }

    /**
     * yyyy-MM-dd转化为yyyy年MM月dd日 add 20151214 by xiayundong
     *
     * @param date
     * @return
     */
    public static String ymdFormatCH(String date) {
        try {
            return ymdCHDateFormat.format(ymdDateFormat.parse(date));
        } catch (ParseException e) {
            // e.printStackTrace();
        }
        return date;
    }


    /**
     * 带下划线的年月，转化为中文年月
     *
     * @param date
     * @return

     */
    public static String ymFormatCH(String date) {
        try {
            return ymCHDateFormat.format(ymDateFormat.parse(date));
        } catch (ParseException e) {
            // e.printStackTrace();
        }
        return date;
    }

    /**
     * yyyy-MM-dd转化为MM月dd日
     *
     * @param date
     * @return
     */
    public static String mdFormatCH(String date) {
        try {
            return mdCHDateFormat.format(ymdDateFormat.parse(date));
        } catch (ParseException e) {
            // e.printStackTrace();
        }
        return date;
    }

    /**
     * 带下划线的年月，转化为中文年月 获取附带样式的年月标题
     *
     * @param date
     * @return

     */
    public static Spannable ymSpaceFormatCH(String date, int numberSize,
                                            int wordSize) {
        try {
            Spannable word = new SpannableString(
                    ymCHSpaceDateFormat.format(ymDateFormat.parse(date)));
            word.setSpan(new AbsoluteSizeSpan(numberSize), 0, 4,
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            word.setSpan(new AbsoluteSizeSpan(wordSize), 4, 6,
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            word.setSpan(new AbsoluteSizeSpan(numberSize), 6, 9, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            word.setSpan(new AbsoluteSizeSpan(wordSize), 9, 10, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            return word;
        } catch (Exception e) {
        }
        return new SpannableString(date);
    }


    public static String addDay(String date, int i) {
        try {
            Date date1 = ymdDateFormat.parse(date);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date1);
            calendar.add(Calendar.DAY_OF_MONTH, i);
            return ymdDateFormat.format(calendar.getTime());

        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 返回与今天的对比值 大于0表示未来 小于0表示过去
     *
     * @param date
     * @return
     */
    public static long compareToady(String date) {
        try {
            Date date1 = ymdDateFormat.parse(date);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date1);
            long l = calendar.getTimeInMillis();
            String date2 = ymdDateFormat.format(new Date());
            calendar.setTime(ymdDateFormat.parse(date2));
            l = l - calendar.getTimeInMillis();
            return l / (1000L * 60 * 60 * 24);
        } catch (Exception e) {
            return Long.MAX_VALUE;
        }
    }

    /**
     * 返回下个月的当日
     */

    public static String getNextDate(String date) {
        try {
            Date date1 = ymdDateFormat.parse(date);
            sCalendar.setTime(date1);
            int month = sCalendar.get(Calendar.MONTH) + 1;
            int bit = month / 12;
            if (month > 11) {
                month = month - 12;
            }
            int year = sCalendar.get(Calendar.YEAR) + bit;
            int day = sCalendar.get(Calendar.DAY_OF_MONTH);
            sCalendar.set(year, month, 1);
            int day1 = sCalendar.getMaximum(Calendar.DAY_OF_MONTH);
            day = day1 < day ? day1 : day;
            sCalendar.set(year, month, day);
            return ymdDateFormat.format(sCalendar.getTime());
        } catch (Exception e) {
            return date;
        }
    }

    ;

    /**
     * 全日期格式化成yyyy-MM-dd
     *
     * @return

     */
    public static String ymdFormat(String date) {
        if (TextUtils.isEmpty(date)) {
            return null;
        }
        try {
            return ymdDateFormat.format(commonDateFormat.parse(date));
        } catch (ParseException e) {
            // e.printStackTrace();
        }
        return date;
    }

    /**
     * 从日期字符串中获取日
     *
     * @param dateString 2014-01-23 12:22:00
     */
    public static String getStringDateOfDay(String dateString) {
        String day = "";
        if (dateString == null) {
            return day;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Calendar cal1 = Calendar.getInstance();
            cal1.setTime(sdf.parse(dateString));
            Date date = cal1.getTime();
            day = getDay(date);
        } catch (ParseException e) {

        }
        return day;
    }


    /**
     * 从日期字符串中获取日小写
     *
     * @param dateString 2014-01-23 12:22:00
     */
    public static String getNStringDateOfDay(String dateString) {
        String day = "";
        if (dateString == null) {
            return day;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Calendar cal1 = Calendar.getInstance();
            cal1.setTime(sdf.parse(dateString));
            Date date = cal1.getTime();
            day = getDay(date);
        } catch (ParseException e) {

        }
        if (day != null && day.length() == 2) {
            if (day.charAt(0) == '0') {
                day = day.substring(1);
            }
        }
        return day;
    }

    /**
     * 得到2013年01月 格式
     *
     * @param dateString
     * @return
     */
    public static String parseYYMMdate(String dateString) {
        String date = "";
        if (dateString == null) {
            return dateString;
        }
        try {
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy年MM月");
            Date dataDate = commonDateFormat.parse(dateString);
            date = sdf2.format(dataDate);
            return date;
        } catch (ParseException e) {
            return date;
        }
    }

    /**
     * 获取年
     *
     * @param date
     * @return
     */
    public static String getYear(Date date) {
        if (sCalendar == null) {
            sCalendar = Calendar.getInstance();
        }
        sCalendar.setTime(date);
        return String.valueOf(sCalendar.get(Calendar.YEAR));

    }

    /**
     * 获取月
     *
     * @param date
     * @return
     */
    public static String getMonth(Date date) {
        if (sCalendar == null) {
            sCalendar = Calendar.getInstance();
        }
        sCalendar.setTime(date);
        return String.valueOf(sCalendar.get(Calendar.MONTH) + 1);
    }

    /**
     * 获取日期中的日
     *
     * @param date
     * @return
     */
    public static String getDay(Date date) {
        if (sCalendar == null) {
            sCalendar = Calendar.getInstance();
        }
        sCalendar.setTime(date);
        return formatDateNum(sCalendar.get(Calendar.DAY_OF_MONTH));
    }

    /**
     * 判断后面时间是否在前面时间之后
     *
     * @param startTime 2010-10-10
     * @param endTime   2010-10-12
     * @return true
     */
    public static boolean isAfterDate(String startTime, String endTime) {
        try {
            Date startDate = ymdDateFormat.parse(startTime);
            Date enDate = ymdDateFormat.parse(endTime);
            return !enDate.before(startDate);
        } catch (ParseException e) {
        }
        return false;
    }

      
    public static String formatTime(long time, String mould) {
        return new SimpleDateFormat(mould).format(new Date(time));
    }
}

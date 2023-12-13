package com.csgo.util;

import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 日期工具类
 *
 * @author admin
 * @create 2019/10/11
 */
@Slf4j
public class DateUtils {

    public static final String DATE_AND_TIME_FULL_FORMAT = "yyyyMMddHHmmss"; //完整日期加时间格式
    public static final SimpleDateFormat DATE_FORMAT_YEAR = new SimpleDateFormat("yyyy");//日期格式
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");//日期格式
    public static final SimpleDateFormat DATE_FORMAT_ = new SimpleDateFormat("yyyy-MM-dd");//日期格式
    public static final SimpleDateFormat DATE_AND_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//完整日期加时间格式

    public static String dateFormat(Date date, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(date);
    }

    /**
     * 将日期转换为指定格式的日期的形式
     * string-->yyyy-MM-dd
     *
     * @param date
     * @return
     */
    public static String toStringDate(Date date) {
        return DATE_FORMAT_.format(date);
    }

    /**
     * 日期类型转化为字符串
     * yyyy-MM-dd HH:mm:ss -->string
     *
     * @param date
     * @return
     */
    public static String dateToString(Date date) {
        try {
            return DATE_AND_TIME_FORMAT.format(date);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return date.toString();
    }

    /**
     * yyyy-MM-dd HH:mm:ss --->yyyyMMdd
     *
     * @param date
     * @return
     */
    public static String timeToDate(Date date) {
        return DATE_FORMAT.format(date);
    }

    /**
     * 字符串转换为日期类型
     *
     * @param dateString
     * @return
     */
    public static Date stringToDate(String dateString) {
        try {
            return DATE_FORMAT_.parse(dateString);
        } catch (ParseException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    public static Date stringConnectionToDate(String dateString) {
        try {
            return DATE_FORMAT.parse(dateString);
        } catch (ParseException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }


    public static Date now() {
        Date time = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(time);
        cal.set(Calendar.MILLISECOND, 0); //毫秒
        return cal.getTime();
    }


    /**
     * 获取下周一的日期
     */
    public static Date getNextMon(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int i = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (i == 0) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        } else {
            calendar.add(Calendar.DAY_OF_MONTH, 8 - i);
        }

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }


    public static Date addDate(Date date, int days, int field) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(field, days);
        return calendar.getTime();
    }

    /**
     * 返回两个日期之间的所有日期集合
     *
     * @param start
     * @param end
     * @return
     */
    public static List<Date> getBetweenDates(Date start, Date end) {
        List<Date> result = new ArrayList<Date>();
        Calendar tempStart = Calendar.getInstance();
        tempStart.setTime(start);
        tempStart.add(Calendar.DAY_OF_YEAR, 1);

        Calendar tempEnd = Calendar.getInstance();
        tempEnd.setTime(end);
        result.add(start);
        while (tempStart.before(tempEnd)) {
            result.add(tempStart.getTime());
            tempStart.add(Calendar.DAY_OF_YEAR, 1);
        }
        result.add(end);
        return result;
    }

}

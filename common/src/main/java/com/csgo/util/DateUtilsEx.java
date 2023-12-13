package com.csgo.util;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import org.apache.commons.lang3.time.DateUtils;
import org.joda.time.DateTime;

/**
 * @author admin
 */
public final class DateUtilsEx {

    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_SECOND_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss.S";
    public static final String DATE_TIME_NEW_FORMAT = "yyyyMMddHHmmss";
    public static final String DATE_MONTH_FORMAT = "yyyy-MM";
    public static final String DATE_DAY_FORMAT = "yyyy-MM-dd";
    public static final String DATE_DAY_NEW_FORMAT = "yyyyMMdd";


    private DateUtilsEx() {
    }

    public static Date monthlyFirstDay(int year, int month) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        return DateUtils.truncate(cal.getTime(), Calendar.DAY_OF_MONTH);
    }

    public static Date monthlyLastDay(int year, int month) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, 0);
        return DateUtils.truncate(cal.getTime(), Calendar.DAY_OF_MONTH);
    }

    public static Date truncateWeek() {
        Date date = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_WEEK_IN_MONTH, Calendar.MONDAY);
        return cal.getTime();
    }

    public static Date toDayStart(Date date) {
        DateTime dateTime = new DateTime(date);
        return dateTime.withMillisOfDay(0).toDate();
    }

    public static Date toDayEnd(Date date) {
        DateTime dateTime = new DateTime(date);
        return dateTime.withMillisOfDay(24 * 60 * 60 * 1000 - 1).toDate();
    }

    public static Date calDateByYearToStartTime(Date date, int value) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.YEAR, value);
        return getYearFirst(calendar.get(Calendar.YEAR));
    }

    public static Date calDateByMonthToStartTime(Date date, int value) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, value);
        return getFirstDayOfMonthSynYear(calendar, calendar.get(Calendar.MONTH));
    }

    public static Date calDateByDayToStartTime(Date date, int value) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, value);
        return toDayStart(calendar.getTime());
    }

    public static Date calDateByDay(Date date, int value) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, value);
        return calendar.getTime();
    }

    public static Date getYearFirst(int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.YEAR, year);
        return calendar.getTime();
    }

    public static Date getFirstDayOfMonth(int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        //将小时至0
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        //将分钟至0
        calendar.set(Calendar.MINUTE, 0);
        //将秒至0
        calendar.set(Calendar.SECOND, 0);
        //将毫秒至0
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static Date getFirstDayOfMonthSynYear(Calendar calendar, int month) {
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        //将小时至0
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        //将分钟至0
        calendar.set(Calendar.MINUTE, 0);
        //将秒至0
        calendar.set(Calendar.SECOND, 0);
        //将毫秒至0
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static Date getFirstDayOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        //将小时至0
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        //将分钟至0
        calendar.set(Calendar.MINUTE, 0);
        //将秒至0
        calendar.set(Calendar.SECOND, 0);
        //将毫秒至0
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static Date getLastDayOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        //将小时至23
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        //将分钟至59
        calendar.set(Calendar.MINUTE, 59);
        //将秒至59
        calendar.set(Calendar.SECOND, 59);
        //将毫秒至999
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    public static int getDateDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    public static Date setDateDay(Date date, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        return toDayStart(calendar.getTime());
    }

    public static int getDateMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MONTH);
    }

    public static int getDateYear(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR);
    }

    public static BigDecimal getDifferDay(Date startTime, Date endTime) {
        BigDecimal beginTime = BigDecimal.valueOf(startTime.getTime());
        BigDecimal stopTime = BigDecimal.valueOf(endTime.getTime());
        BigDecimal day = stopTime.subtract(beginTime).divide(BigDecimal.valueOf(1000 * 60 * 60 * 24), 2, BigDecimal.ROUND_HALF_UP);
        return day;
    }

    public static BigDecimal getDifferHour(Date startTime, Date endTime) {
        BigDecimal beginTime = BigDecimal.valueOf(startTime.getTime());
        BigDecimal stopTime = BigDecimal.valueOf(endTime.getTime());
        BigDecimal hour = stopTime.subtract(beginTime).divide(BigDecimal.valueOf(1000 * 60 * 60), 2, BigDecimal.ROUND_HALF_UP);
        return hour;
    }

    public static BigDecimal getDifferMinute(Date startTime, Date endTime) {
        BigDecimal beginTime = BigDecimal.valueOf(startTime.getTime());
        BigDecimal stopTime = BigDecimal.valueOf(endTime.getTime());
        BigDecimal hour = stopTime.subtract(beginTime).divide(BigDecimal.valueOf(1000 * 60), 2, BigDecimal.ROUND_HALF_UP);
        return hour;
    }

    public static BigDecimal getDifferSecond(Date startTime, Date endTime) {
        BigDecimal beginTime = BigDecimal.valueOf(startTime.getTime());
        BigDecimal stopTime = BigDecimal.valueOf(endTime.getTime());
        BigDecimal hour = stopTime.subtract(beginTime).divide(BigDecimal.valueOf(1000), 2, BigDecimal.ROUND_HALF_UP);
        return hour;
    }

    public static String toDateStr(Date date, String pattern) {
        String dateStr = new SimpleDateFormat(pattern).format(date);
        return dateStr;
    }

    public static String getDateStr(Date value) {
        return toString(value, DATE_TIME_FORMAT);
    }

    public static String getDateNewStr(Date value) {
        return toString(value, DATE_TIME_NEW_FORMAT);
    }

    public static String getDateMonthStr(Date value) {
        return toString(value, DATE_MONTH_FORMAT);
    }

    public static String getDateDayStr(Date value) {
        return toString(value, DATE_DAY_FORMAT);
    }

    public static String getDateDayNewStr(Date value) {
        return toString(value, DATE_DAY_NEW_FORMAT);
    }

    public static Date getDate(String value, String pattern) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);//注意月份是MM
            return simpleDateFormat.parse(value);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Date getSecondDate(String value) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_SECOND_TIME_FORMAT);
        return simpleDateFormat.parse(value);
    }

    public static Date calDateByHour(Date date, int hour) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR_OF_DAY, hour);
        return calendar.getTime();
    }

    public static Date calDateByMonth(Date date, int month) {
        if (month == 0) {
            return date;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, month);
        return calendar.getTime();
    }

    public static Date calDateBySecond(Date date, int second) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.SECOND, second);
        return calendar.getTime();
    }

    public static Date calDateByMinute(Date date, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, minute);
        return calendar.getTime();
    }

    public static String toString(Date date, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(date);
    }

    public static Date getStartTimeOfWeek(int i) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        calendar.add(Calendar.DAY_OF_MONTH, i * -7);

        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        //将小时至0
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        //将分钟至0
        calendar.set(Calendar.MINUTE, 0);
        //将秒至0
        calendar.set(Calendar.SECOND, 0);
        //将毫秒至0
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static Date getFirstDayOfMonthAndI(int i) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, i * -1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        //将小时至0
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        //将分钟至0
        calendar.set(Calendar.MINUTE, 0);
        //将秒至0
        calendar.set(Calendar.SECOND, 0);
        //将毫秒至0
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }
}

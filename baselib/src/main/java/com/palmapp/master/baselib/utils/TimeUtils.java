package com.palmapp.master.baselib.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static com.palmapp.master.baselib.bean.user.UserInfoKt.*;

/**
 * <br>类描述:
 * <br>功能详细描述:
 *
 * @author rongjinsong
 * @date [2014年11月4日]
 */
public class TimeUtils {
    public final static String SYS_DATE_FORMATE = "yyyy-MM-dd HH:mm:ss";
    public final static String LONGEST_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    public final static String LONG_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public final static String SHORT_FORMAT = "yyyy-MM-dd";
    public final static String TIME_FORMAT = "HH:mm:ss";

    private static SimpleDateFormat sFormatter = new SimpleDateFormat();

    // ///////////////////////////////////////////////////////////////

    /**
     * 获取现在时间
     *
     * @return 返回时间类型 yyyy-MM-dd HH:mm:ss.SSS
     */
    public static Date getNowDateLongest() {
        return getNowDate(LONGEST_FORMAT);
    }

    /**
     * 获取现在时间
     *
     * @return 返回时间类型 yyyy-MM-dd HH:mm:ss
     */
    public static Date getNowDate() {
        return getNowDate(LONG_FORMAT);
    }

    /**
     * 获取现在时间
     *
     * @return 返回短时间字符串格式yyyy-MM-dd
     */
    public static Date getNowDateShort() {
        return getNowDate(SHORT_FORMAT);
    }

    /**
     * 获取时间 小时:分;秒 HH:mm:ss
     *
     * @return
     */
    public static Date getNowTimeShort() {
        return getNowDate(TIME_FORMAT);
    }

    /**
     * 获取现在时间
     *
     * @param timeFormat 返回时间格式
     */
    public static Date getNowDate(String timeFormat) {
        Date currentTime = new Date();
        Date currentTime_2 = null;
        synchronized (sFormatter) {
            sFormatter.applyPattern(timeFormat);
            String dateString = sFormatter.format(currentTime);
            ParsePosition pos = new ParsePosition(0);
            currentTime_2 = sFormatter.parse(dateString, pos);
        }
        return currentTime_2;
    }

    // /////////////////////////////////////////////////////////////////////////////

    /**
     * 获取现在时间
     *
     * @return 返回字符串格式 yyyy-MM-dd HH:mm:ss.SSS
     */
    public static String getStringDateLongest() {
        return getStringDate(LONGEST_FORMAT);
    }

    /**
     * 获取现在时间
     *
     * @return 返回字符串格式 yyyy-MM-dd HH:mm:ss
     */
    public static String getStringDate() {
        return getStringDate(LONG_FORMAT);
    }

    /**
     * 获取现在时间
     *
     * @return 返回短时间字符串格式yyyy-MM-dd
     */
    public static String getStringDateShort() {
        return getStringDate(SHORT_FORMAT);
    }

    /**
     * 获取时间 小时:分;秒 HH:mm:ss
     *
     * @return
     */
    public static String getTimeShort() {
        return getStringDate(TIME_FORMAT);
    }

    /**
     * 获取现在时间
     */
    public static String getStringDate(String timeFormat) {
        Date currentTime = new Date();
        String dateString = null;
        synchronized (sFormatter) {
            sFormatter.applyPattern(timeFormat);
            dateString = sFormatter.format(currentTime);
        }
        return dateString;
    }

    // //////////////////////////////////////////////////////////////////////////////

    /**
     * 将长时间格式字符串转换为时间 yyyy-MM-dd HH:mm:ss.SSS
     *
     * @param strDate
     * @return
     */
    public static Date strToLongDateLongest(String strDate) {
        return strToDate(strDate, LONGEST_FORMAT);
    }

    /**
     * 将长时间格式字符串转换为时间 yyyy-MM-dd HH:mm:ss
     *
     * @param strDate
     * @return
     */
    public static Date strToLongDate(String strDate) {
        return strToDate(strDate, LONG_FORMAT);
    }

    /**
     * 将短时间格式字符串转换为时间 yyyy-MM-dd
     *
     * @param strDate
     * @return
     */
    public static Date strToShortDate(String strDate) {
        return strToDate(strDate, SHORT_FORMAT);
    }

    /**
     * 将时间格式字符串转换为时间 HH:mm:ss
     *
     * @param strDate
     * @return
     */
    public static Date strToTimeDate(String strDate) {
        return strToDate(strDate, TIME_FORMAT);
    }

    /**
     * 按指定的时间格式字符串转换为时间
     *
     * @param strDate
     * @param timeFormat
     * @return
     */
    public static Date strToDate(String strDate, String timeFormat) {
        Date strtodate = null;
        synchronized (sFormatter) {
            sFormatter.applyPattern(timeFormat);
            ParsePosition pos = new ParsePosition(0);
            strtodate = sFormatter.parse(strDate, pos);
        }
        return strtodate;
    }

    // ///////////////////////////////////////////////////////////////////////////////

    /**
     * 将长时间格式时间转换为字符串 yyyy-MM-dd HH:mm:ss.SSS
     *
     * @param dateDate
     * @return
     */
    public static String dateToLongestStr(Date dateDate) {
        return dateToStr(dateDate, LONGEST_FORMAT);
    }

    /**
     * 将长时间格式时间转换为字符串 yyyy-MM-dd HH:mm:ss
     *
     * @param dateDate
     * @return
     */
    public static String dateToLongStr(Date dateDate) {
        return dateToStr(dateDate, LONG_FORMAT);
    }

    /**
     * 将短时间格式字符串转换为时间 yyyy-MM-dd
     *
     * @param dateDate
     * @return
     */
    public static String dateToShortStr(Date dateDate) {
        return dateToStr(dateDate, SHORT_FORMAT);
    }

    /**
     * 将时间格式字符串转换为时间 HH:mm:ss
     *
     * @param dateDate
     * @return
     */
    public static String dateToTimeStr(Date dateDate) {
        return dateToStr(dateDate, TIME_FORMAT);
    }

    /**
     * 按指定的时间格式时间转换为字符串
     *
     * @param dateDate
     * @param timeFormat
     * @return
     */
    public static String dateToStr(Date dateDate, String timeFormat) {
        String dateString = null;
        synchronized (sFormatter) {
            sFormatter.applyPattern(timeFormat);
            dateString = sFormatter.format(dateDate);
        }
        return dateString;
    }

    public static String dateToStr(Date dateDate, String timeFormat, Locale locale) {
        SimpleDateFormat format = new SimpleDateFormat(timeFormat, locale);
        return format.format(dateDate);
    }

    public static String longToStr(long m, String timeFormat) {
        String dateString = null;
        synchronized (sFormatter) {
            sFormatter.applyPattern(timeFormat);
            dateString = sFormatter.format(new Date(m));
        }
        return dateString;
    }

    public static String longToStr(long m, String timeFormat, Locale locale) {
        SimpleDateFormat format = new SimpleDateFormat(timeFormat, locale);
        return format.format(new Date(m));
    }

    public static boolean isTimeValid(String pattern, String startTime, String endTime) {
        return isTimeValid(System.currentTimeMillis(), pattern, startTime, endTime);
    }

    public static boolean isTimeValid(long datetime, String pattern, String startTime,
                                      String endTime) {
        DateFormat df = new SimpleDateFormat(pattern);
        df.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        try {
            Date before = df.parse(startTime);
            Date now = df.parse(df.format(new Date(datetime)));
            Date after = df.parse(endTime);
            if (now.before(after) && now.after(before)) {
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ////////////////////////////////////////////////////////////////////////////////

    public static String transferLongToDate(String dateFormat, Long millSec) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        Date date = new Date(millSec);
        return sdf.format(date);
    }


    public static String timeSeqFormat(long timeseq) {
        return timeSeqFormat(timeseq, SYS_DATE_FORMATE);
    }


    public static String timeSeqFormat_2(long timeseq) {
        return timeSeqFormat(timeseq, SHORT_FORMAT);
    }


    public static String timeSeqFormat(long timeseq, String format) {
        Date date = new Date(timeseq);
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        String time = formatter.format(date);
        return time;
    }

    /**
     * 得到程序中标准时间到秒
     *
     * @return yyyy-MM-dd HH:mm:ss 格式的时间
     */
    public static String getCurrentTimeToSecond() {
        String time = "";
        Date dNow = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        time = formatter.format(dNow);
        return time;
    }


    /**
     * 得出2个时间相差多少小时
     *
     * @param now      时间参数 1 格式：2011-11-11 11:11:11
     * @param callTime 时间参数 2 格式：2011-01-01 12:00:00
     * @return boolean
     */
    public static Long getDistanceHour(String now, String callTime) {
        long hour = -1;
        long time1 = toSystemTimeLong(now);
        long time2 = toSystemTimeLong(callTime);
        hour = ((time1 - time2) / (60*60));
        return hour;
    }

    /**
     * 得出2个时间相差多少分钟
     *
     * @param now      时间参数 1 格式：2011-11-11 11:11:11
     * @param callTime 时间参数 2 格式：2011-01-01 12:00:00
     * @return boolean
     */
    public static Long getDistanceMin(String now, String callTime) {
        long min = -1;
        long time1 = toSystemTimeLong(now);
        long time2 = toSystemTimeLong(callTime);
        min = ((time1 - time2) / (60));
        return min;
    }

    /**
     * 得出2个时间相差多少秒
     *
     * @return boolean
     */
    public static Long getDistanceSec(String now, String callTime) {
        long second = -1;
        long time1 = toSystemTimeLong(now);
        long time2 = toSystemTimeLong(callTime);
        second = (time1 - time2);
        return second;
    }

    /**
     * 将时间字符串转化为秒值（相对 1970年１月１日）
     *
     * @param time 时间字符串
     * @return　　　失败返回　０
     */
    public static long toSystemTimeLong(String time) {
        long nRet = 0;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(SYS_DATE_FORMATE);
            Date de = dateFormat.parse(time);
            nRet = de.getTime() / 1000;
        } catch (Exception e) {/*DISCARD EXCEPTION*/
        }
        return nRet;
    }

    /**
     * 根据月日返回对应星座ID
     * @param month
     * @param dayOfMonth
     * @return
     */
    public static int getConstellations(int month, int dayOfMonth) {
        int[] mDate = {20, 19, 21, 20, 21, 22, 23, 23, 23, 24, 23, 22};
        int[] mStar1 = {CAPRICORN, AQUARIUS, PISCES, ARIES, TAURUS, GEMINI, CANCER, LEO, VIRGO, LIBRA, SCORPIO, SAGITTARIUS};
        int[] mStar2 = {AQUARIUS, PISCES, ARIES, TAURUS, GEMINI, CANCER, LEO, VIRGO, LIBRA, SCORPIO, SAGITTARIUS, CAPRICORN};

        int day = mDate[month];
        if (dayOfMonth >= day) {
            return mStar2[month];
        } else {
            return mStar1[month];
        }
    }


    /**
     * 通过时间秒毫秒数判断两个时间的间隔
     * @param date1
     * @param date2
     * @return
     */
    public static int differentDaysByMillisecond(Date date1,Date date2)
    {
        int days = (int) ((date2.getTime() - date1.getTime()) / (1000*3600*24));
        return days;
    }


    /**
     * date2比date1多的天数
     * @param date1
     * @param date2
     * @return
     */
    public static int differentDays(Date date1,Date date2)
    {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        int day1= cal1.get(Calendar.DAY_OF_YEAR);
        int day2 = cal2.get(Calendar.DAY_OF_YEAR);

        int year1 = cal1.get(Calendar.YEAR);
        int year2 = cal2.get(Calendar.YEAR);
        if(year1 != year2)   //同一年
        {
            int timeDistance = 0 ;
            for(int i = year1 ; i < year2 ; i ++)
            {
                if(i%4==0 && i%100!=0 || i%400==0)    //闰年
                {
                    timeDistance += 366;
                }
                else    //不是闰年
                {
                    timeDistance += 365;
                }
            }

            return timeDistance + (day2-day1) ;
        }
        else    //不同年
        {
            System.out.println("判断day2 - day1 : " + (day2-day1));
            return day2-day1;
        }
    }


    /**
     * 计算两个日期之间相差的天数
     * @param smdate 较小的时间
     * @param bdate  较大的时间
     * @return 相差天数
     * @throws ParseException
     */
    public static int daysBetween(Date smdate,Date bdate) throws ParseException
    {
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        smdate=sdf.parse(sdf.format(smdate));
        bdate=sdf.parse(sdf.format(bdate));
        Calendar cal = Calendar.getInstance();
        cal.setTime(smdate);
        long time1 = cal.getTimeInMillis();
        cal.setTime(bdate);
        long time2 = cal.getTimeInMillis();
        long between_days=(time2-time1)/(1000*3600*24);

        return Integer.parseInt(String.valueOf(between_days));
    }


}
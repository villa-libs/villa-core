package com.villa.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * 时间相关的工具类
 */
public class TimeUtil {
    /**
     * 指定日期加指定天数
     * @param date	 时间源
     * @param addDay 添加的天数
     */
    public static Date addDate(Date date, int addDay) {
        long time = date.getTime(); // 得到指定日期的毫秒数
        time+=addDay*24*60*60*1000L; //要加上的天数转换成毫秒数 相加得到新的毫秒数
        return new Date(time); // 将毫秒数转换成日期
    }

    /**
     * 给定一个Date实例  得到这个时间中对应月的最大天数
     */
    public static int getMaxDayOfMonth(Date date) throws ParseException {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.setTime(date);
        return calendar.getActualMaximum(Calendar.DATE);
    }
    /**
     * 取两个时间的小时差  得到的差值保留一位小数
     */
    public static String timeDifference(Date beginTime,Date endTime) {
        double time = endTime.getTime()-beginTime.getTime();
        String h= (time/(1000*60*60))+"";
        int index = h.indexOf(".");
        if (index!=-1) {
            h.substring(0,index+1);
        }
        return h;
    }
    /**
     * 时间格式转换为yyyy-MM-dd
     */
    public static String formatYMD(Date date) {
        try {
            SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
            return s.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    /**
     * 时间格式转换为yyyy-MM-dd HH:mm:ss
     */
    public static String formatYMDHmS(Date date) {
        try {
            SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return s.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static Date parseYMDHmS(String date) {
        try {
            SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return s.parse(date);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    /**
     * 将一个不是当天00:00:00的时间戳 转换为当天的00:00:00
     * 可与coverEndTime组合时间 转换结束时间
     * @param time 类似与2023-04-15 14:08:05 这样的时间的时间戳
     * @return 得到类似2023-04-15 00:00:00时间的时间戳
     */
    public static Long coverBeginTime(Long time){
        Date beginTime = new Date(time);
        String beginTimeFormat = formatYMD(beginTime)+" 00:00:00";
        return parseYMDHmS(beginTimeFormat).getTime();
    }
    public static Long coverEndTime(Long time){
        Date endTime = new Date(time);
        String endTimeFormat = formatYMD(endTime)+" 23:59:59";
        return parseYMDHmS(endTimeFormat).getTime();
    }

    /**
     * 获取星期天是一周第一天的起始时间和结束时间
     * @return
     */
    public static long[] getCurrentWeekTimeOnSunday() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        //start of the week
        calendar.add(Calendar.DAY_OF_WEEK, -(calendar.get(Calendar.DAY_OF_WEEK) - 1));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        long startTime = calendar.getTimeInMillis();
        //end of the week
        calendar.add(Calendar.DAY_OF_WEEK, 6);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        long endTime = calendar.getTimeInMillis();
        return new long[]{startTime, endTime};
    }
    /**
     * 获取星期一作为一周的第一天的起始时间和结束时间
     */
    public static long[] getCurrentWeekTimeOnMonday() {
        Calendar calendar = Calendar.getInstance();
        if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            calendar.add(Calendar.DAY_OF_YEAR,-1);
        }
        calendar.add(Calendar.DAY_OF_WEEK, -(calendar.get(Calendar.DAY_OF_WEEK) - 2));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        long startTime = calendar.getTimeInMillis();
        //end of the week
        calendar.add(Calendar.DAY_OF_WEEK, 6);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        long endTime = calendar.getTimeInMillis();
        return new long[]{startTime, endTime};
    }
}

package com.villa.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
}

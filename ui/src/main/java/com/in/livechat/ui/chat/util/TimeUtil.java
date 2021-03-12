package com.in.livechat.ui.chat.util;

import android.os.SystemClock;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期工具类
 * @author Darren
 * Created by Darren on 2015/2/28.
 */
public class TimeUtil {

    private static String[] dayNameArr = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
    private static long appLaunchSystemTimestamp;
    private static long appLaunchServiceTimestamp;

    public static void initTime(long serviceTimestamp) {
        appLaunchSystemTimestamp = SystemClock.elapsedRealtime();
        appLaunchServiceTimestamp = serviceTimestamp;
    }

    /**
     * 得到当前时间
     *
     * @param dateFormat 时间格式
     * @return 转换后的时间格式
     */
    public static String getCurrentTime(String dateFormat) {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        return formatter.format(currentTime);
    }

    public static String getCurrentTime() {
        return getCurrentTime("yyyy-MM-dd HH:mm:ss");
    }

    public static long getCurrentTimestamp() {
        return System.currentTimeMillis();
    }

    /**
     * 获取服务器时间戳
     */
    public static long getServerTimestamp() {
        long currentSystemTimestamp = SystemClock.elapsedRealtime();
        return appLaunchServiceTimestamp + currentSystemTimestamp - appLaunchSystemTimestamp;
    }

    /**
     * 时间戳转字符串日期
     */
    public static String timestampToStr(long timeStamp, String timeFormat) {
        SimpleDateFormat format = new SimpleDateFormat(timeFormat);
        return format.format(new Date(timeStamp));
    }

    public static String timestampToStr(long timeStamp) {
        return timestampToStr(timeStamp, "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * Date转字符串日期
     */
    public static String dateToStr(Date date, String dateFormat) {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        return formatter.format(date);
    }

    public static String dateToStr(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.format(date);
    }

    /**
     * 字符串日期转Date
     *
     * @param dateStr    字符串日期
     * @param dateFormat 日期格式
     */
    public static Date strToDate(String dateStr, String dateFormat) {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        try {
            return formatter.parse(dateStr);
        } catch (ParseException e) {
            return null;
        }
    }

    public static Date strToDate(String dateStr) {
        return strToDate(dateStr, "yyyy-MM-dd HH:mm:ss");
    }

    public static Date timestampToDate(long timestamp) {
        return strToDate(timestampToStr(timestamp));
    }

    /**
     * yyyy-MM-dd HH:mm:ss字符串日期转新格式字符串日期
     */
    public static String strToStr(String dateStr, String newDateFormat) {
        Date date = strToDate(dateStr);
        if (date != null) {
            return dateToStr(date, newDateFormat);
        } else {
            return null;
        }
    }

    /**
     * 两个时间点的间隔时长（天数）
     * 得到的差值是微秒级别
     *
     * @param before 开始时间
     * @param after  结束时间
     * @return 两个时间点的间隔时长（天数）
     */
    public static long calDaysDiff(Date before, Date after) {
        if (before == null || after == null) {
            return 0;
        }
        long dif = 0;
        if (after.getTime() >= before.getTime()) {
            dif = after.getTime() - before.getTime();
        } else if (after.getTime() < before.getTime()) {
            dif = after.getTime() + 86400000 - before.getTime();
        }
        dif = Math.abs(dif);
        return dif / (1000 * 60 * 60 * 24);
    }

    public static long calDaysDiff(long beforeTimestamp, long afterTimestamp) {
        return calDaysDiff(timestampToDate(beforeTimestamp), timestampToDate(afterTimestamp));
    }

    /**
     * 获取指定时间间隔分钟后的时间
     *
     * @param date 指定的时间
     * @param min  间隔分钟数
     * @return 间隔分钟数后的时间
     */
    public static Date addMinutes(Date date, int min) {
        if (date == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, min);
        return calendar.getTime();
    }

    public static String formatTimeStr(long durationSecond) {
        int minute = (int) durationSecond / 60;
        int second = (int) durationSecond - (minute * 60);
        String minuteStr;
        String secondStr;
        if (minute < 10) {
            minuteStr = "0" + minute;
        } else {
            minuteStr = String.valueOf(minute);
        }
        if (second < 10) {
            secondStr = "0" + second;
        } else {
            secondStr = String.valueOf(second);
        }
        return minuteStr + ":" + secondStr;
    }

    public static boolean isSameDay(long firstTimestamp, long secondTimestamp) {
        Calendar firstCalendar = Calendar.getInstance();
        Calendar secondCalendar = Calendar.getInstance();
        firstCalendar.setTimeInMillis(firstTimestamp);
        secondCalendar.setTimeInMillis(secondTimestamp);
        return firstCalendar.get(Calendar.DAY_OF_YEAR) - secondCalendar.get(Calendar.DAY_OF_YEAR) == 0;
    }

    public static String getFormatDate(long inputTimestamp) {
        Calendar currentCalendar = Calendar.getInstance();
        Calendar inputCalendar = Calendar.getInstance();
        currentCalendar.setTimeInMillis(getServerTimestamp());
        inputCalendar.setTimeInMillis(inputTimestamp);
        int currentDay = currentCalendar.get(Calendar.DAY_OF_YEAR);
        int inputDay = inputCalendar.get(Calendar.DAY_OF_YEAR);
        if (currentDay - inputDay == 0) {
            //当天
            return "今天";
        } else if (currentDay - inputDay == 1) {
            //昨天
            return "昨天";
        } else {
            return timestampToStr(inputTimestamp, "M月d日");
        }
    }

    //格式化時間格式仿微信顯示
    public static String getWeChatTime(long currentShowTime) {
        String formatResult = "";
        try {
            formatResult = formatWeChatTime(currentShowTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return formatResult;
    }

    private static String formatWeChatTime(long timestamp) {
        String result;
        Calendar todayCalendar = Calendar.getInstance();
        Calendar otherCalendar = Calendar.getInstance();
        otherCalendar.setTimeInMillis(timestamp);
        String timeFormat;
        String yearTimeFormat;
        String am_pm;
        int hour = otherCalendar.get(Calendar.HOUR_OF_DAY);
        if (hour >= 0 && hour < 6) {
            am_pm = "凌晨";
        } else if (hour >= 6 && hour < 12) {
            am_pm = "早上";
        } else if (hour == 12) {
            am_pm = "中午";
        } else if (hour > 12 && hour < 18) {
            am_pm = "下午";
        } else {
            am_pm = "晚上";
        }
        timeFormat = "M月d日 " + am_pm + "HH:mm";
        yearTimeFormat = "yyyy年M月d日 " + am_pm + "HH:mm";
        boolean yearTemp = todayCalendar.get(Calendar.YEAR) == otherCalendar.get(Calendar.YEAR);
        if (yearTemp) {
            int todayMonth = todayCalendar.get(Calendar.MONTH);
            int otherMonth = otherCalendar.get(Calendar.MONTH);
            if (todayMonth == otherMonth) {//表示是同一个月
                int temp = todayCalendar.get(Calendar.DATE) - otherCalendar.get(Calendar.DATE);
                switch (temp) {
                    case 0:
                        result = getHourAndMin(timestamp);
                        break;
                    case 1:
                        result = "昨天 " + getHourAndMin(timestamp);
                        break;
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                        int dayOfMonth = otherCalendar.get(Calendar.WEEK_OF_MONTH);
                        int todayOfMonth = todayCalendar.get(Calendar.WEEK_OF_MONTH);
                        if (dayOfMonth == todayOfMonth) {//表示是同一周
                            int dayOfWeek = otherCalendar.get(Calendar.DAY_OF_WEEK);
                            if (dayOfWeek != 1) {//判断当前是不是星期日   如想显示为：周日 12:09 可去掉此判断
                                result = dayNameArr[otherCalendar.get(Calendar.DAY_OF_WEEK) - 1] + getHourAndMin(timestamp);
                            } else {
                                result = getTime(timestamp, timeFormat);
                            }
                        } else {
                            result = getTime(timestamp, timeFormat);
                        }
                        break;
                    default:
                        result = getTime(timestamp, timeFormat);
                        break;
                }
            } else if (todayCalendar.get(Calendar.DAY_OF_YEAR) - otherCalendar.get(Calendar.DAY_OF_YEAR) == 1) {
                result = "昨天 " + getHourAndMin(timestamp);
            } else {
                result = getTime(timestamp, timeFormat);
            }
        } else {
            result = getYearTime(timestamp, yearTimeFormat);
        }
        return result;
    }

    public static String getConversation(long timestamp) {
        String result;
        Calendar todayCalendar = Calendar.getInstance();
        Calendar otherCalendar = Calendar.getInstance();
        otherCalendar.setTimeInMillis(timestamp);
        String timeFormat;
        String yearTimeFormat;
        timeFormat = "yy/M/d";
        yearTimeFormat = "yy/M/d";
        boolean yearTemp = todayCalendar.get(Calendar.YEAR) == otherCalendar.get(Calendar.YEAR);
        if (yearTemp) {
            int todayMonth = todayCalendar.get(Calendar.MONTH);
            int otherMonth = otherCalendar.get(Calendar.MONTH);
            if (todayMonth == otherMonth) {//表示是同一个月
                int temp = todayCalendar.get(Calendar.DATE) - otherCalendar.get(Calendar.DATE);
                switch (temp) {
                    case 0:
                        result = getHourAndMin(timestamp);
                        break;
                    case 1:
                        result = "昨天";
                        break;
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                        int dayOfMonth = otherCalendar.get(Calendar.WEEK_OF_MONTH);
                        int todayOfMonth = todayCalendar.get(Calendar.WEEK_OF_MONTH);
                        if (dayOfMonth == todayOfMonth) {//表示是同一周
                            int dayOfWeek = otherCalendar.get(Calendar.DAY_OF_WEEK);
                            if (dayOfWeek != 1) {//判断当前是不是星期日   如想显示为：周日 12:09 可去掉此判断
                                result = dayNameArr[otherCalendar.get(Calendar.DAY_OF_WEEK) - 1];
                            } else {
                                result = getTime(timestamp, timeFormat);
                            }
                        } else {
                            result = getTime(timestamp, timeFormat);
                        }
                        break;
                    default:
                        result = getTime(timestamp, timeFormat);
                        break;
                }
            } else if (todayCalendar.get(Calendar.DAY_OF_YEAR) - otherCalendar.get(Calendar.DAY_OF_YEAR) == 1) {
                result = "昨天 ";
            } else {
                result = getTime(timestamp, timeFormat);
            }
        } else {
            result = getYearTime(timestamp, yearTimeFormat);
        }
        return result;
    }

    /**
     * 当天的显示时间格式
     */
    public static String getHourAndMin(long time) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        return format.format(new Date(time));
    }

    /**
     * 不同一周的显示时间格式
     */
    public static String getTime(long time, String timeFormat) {
        SimpleDateFormat format = new SimpleDateFormat(timeFormat);
        return format.format(new Date(time));
    }

    /**
     * 不同年的显示时间格式
     * @return
     */
    public static String getYearTime(long time, String yearTimeFormat) {
        SimpleDateFormat format = new SimpleDateFormat(yearTimeFormat);
        return format.format(new Date(time));
    }

}

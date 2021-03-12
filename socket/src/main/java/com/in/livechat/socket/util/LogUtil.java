package com.in.livechat.socket.util;

import android.util.Log;
public class LogUtil {
    // 输出日志类型，v:输出所有信息
    private static char LOG_TYPE = 'v';
    private static final String TAG_LOG = "Socket";
    private static boolean isDebug = true;

    public void setIsDebug(boolean isDebug) {
        this.isDebug = isDebug;
    }

    public static void v(Object obj) {
        log("--x--" + getCurrentClassName() + "--:" + obj, 'v');
    }

    public static void v(String title, Object obj) {
        log("--x--" + getCurrentClassName() + "--:" + title + ":" + obj, 'v');
    }

    public static void d(Object obj) {
        log("--x--" + getCurrentClassName() + "--:" + obj, 'd');
    }

    public static void d(String title, Object obj) {
        log("--x--" + getCurrentClassName() + "--:" + title + ":" + obj, 'd');
    }

    public static void i(Object obj) {
        log("--x--" + getCurrentClassName() + "--:" + obj, 'i');
    }

    public static void i(String title, Object obj) {
        log("--x--" + getCurrentClassName() + "--:" + title + ":" + obj, 'i');
    }

    public static void w(Object obj) {
        log("--x--" + getCurrentClassName() + "--:" + obj, 'w');
    }

    public static void w(String title, Object obj) {
        log("--x--" + getCurrentClassName() + "--:" + title + ":" + obj, 'w');
    }

    public static void e(Object obj) {
        log("--x--" + getCurrentClassName() + "--:" + obj, 'e');
    }

    public static void e(String title, Object obj) {
        log("--x--" + getCurrentClassName() + "--:" + title + ":" + obj, 'e');
    }

    /**
     * 根据tag, msg和等级，输出日志
     */
    private static void log(String logStr, char level) {
        if (isDebug) {
            if ('e' == level && ('e' == LOG_TYPE || 'v' == LOG_TYPE)) {
                Log.e(TAG_LOG, logStr);
            } else if ('w' == level && ('w' == LOG_TYPE || 'v' == LOG_TYPE)) {
                Log.w(TAG_LOG, logStr);
            } else if ('i' == level && ('i' == LOG_TYPE || 'v' == LOG_TYPE)) {
                Log.e(TAG_LOG, logStr);
            } else if ('d' == level && ('d' == LOG_TYPE || 'v' == LOG_TYPE)) {
                Log.d(TAG_LOG, logStr);
            } else {
                Log.v(TAG_LOG, logStr);
            }
        }
    }


    private static String getCurrentClassName() {
        int level = 2;
        StackTraceElement[] stacks = new Throwable().getStackTrace();
        String className = stacks[level].getClassName();
        return className.substring(className.lastIndexOf(".") + 1, className.length());
    }

}

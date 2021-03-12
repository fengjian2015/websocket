package com.in.livechat.ui.common.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.in.livechat.socket.util.LogUtil;


/**
 * @author Darren
 * Created by Darren on 2018/12/13.
 */
public class SpCons {
    private static final String FILE_NAME = "LiveChat";

    /**
     * 存储字符串sp
     * @param key
     * @param value
     */
    public static void setString(Context context, String key, String value) {
        if(context==null){
            return;
        }
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, 0);

        SharedPreferences.Editor edit = sp.edit();

        edit.putString(key, value);

        edit.commit();

    }

    /**
     * 取出字符串sp
     * @param value
     * @return
     */
    public static String getString(Context context, String value) {
        if(context==null){
            return "";
        }
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, 0);

        String s = sp.getString(value, "");

        return s;

    }

    /**
     * 用户user
     */
    public static void setUser(Context context, String user) {
        if (context == null) {
            return;
        }
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("loginUser", user);
        editor.apply();
    }

    public static String getUser(Context context) {
        if (context == null) {
            return "";
        }
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, 0);
        return sp.getString("loginUser", "");
    }

    /**
     * 保存键盘高度
     */
    public static void setKeyboardHeight(Context context, int height) {
        if (context == null) {
            return;
        }
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("keyboardHeight", height);
        editor.apply();
    }

    public static int getKeyboardHeight(Context context) {
        if (context == null) {
            return 0;
        }
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, 0);
        return sp.getInt("keyboardHeight", 0);
    }

    /**
     * 保存屏幕宽度
     */
    public static void setWindowWidth(Context context, int width) {
        if (context == null) {
            return;
        }
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("windowWidth", width);
        editor.apply();
    }

    public static int getWindowWidth(Context context) {
        if (context == null) {
            return 0;
        }
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, 0);
        return sp.getInt("windowWidth", 0);
    }


}

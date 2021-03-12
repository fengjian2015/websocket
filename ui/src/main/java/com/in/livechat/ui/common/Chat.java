package com.in.livechat.ui.common;

import android.app.Application;

import com.in.livechat.ui.emotion.util.EmoticonUtil;

import org.litepal.LitePal;

/**
 * description ： TODO:类的作用
 * author : Fickle
 * date : 2021/3/11 18:19
 */
public class Chat {
    private static String mFileProvider;
    private static Application application;

    public static void init(Application application, String fileProvider){
        mFileProvider=fileProvider;
        LitePal.initialize(application);
        EmoticonUtil.init(application);

    }

    public static Application getApp() {
        return application;
    }

    public static String getFileProvider() {
        return mFileProvider;
    }
}

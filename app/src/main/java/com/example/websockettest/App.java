package com.example.websockettest;

import android.app.Application;

import com.in.livechat.ui.common.Chat;
import com.in.livechat.ui.emotion.util.EmoticonUtil;

/**
 * description ： TODO:类的作用
 * author : Fickle
 * date : 2021/3/10 13:18
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Chat.init(this,"om.example.websockettest.provider");
    }
}

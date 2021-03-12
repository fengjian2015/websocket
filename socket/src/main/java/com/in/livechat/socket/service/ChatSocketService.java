package com.in.livechat.socket.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import com.in.livechat.socket.ChatSocket;
import com.in.livechat.socket.util.LogUtil;
import com.in.livechat.socket.util.NetworkUtil;
import com.in.livechat.socket.util.ServiceUtil;

import static com.in.livechat.socket.util.Cons.TYPE_RECONNECT_FAILURE;
import static com.in.livechat.socket.util.Cons.TYPE_RECONNECT_NOT;


public class ChatSocketService extends IntentService {

    public void startChatService(Context context){
        if (ServiceUtil.isServiceRunning(context, getClass().getCanonicalName())) {
            LogUtil.d("start:存在");
            return;
        }
        Intent intent = new Intent(context, ChatSocketService.class);
        context.startService(intent);
    }

    public void stopChatService(Context context){
        Intent intent = new Intent(context, ChatSocketService.class);
        context.stopService(intent);
    }

    public ChatSocketService() {
        super("ChatSocketService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.d("onCreate");
        ChatSocket.getInstance().connect(getApplicationContext());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        while (!ChatSocket.getInstance().isActiveShutdown()) {
            try {
                Thread.sleep(5 * 1000);
                checkSocketState();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    private void checkSocketState() {
        if (!NetworkUtil.isNetworkConnected(this)){
            ChatSocket.getInstance().getReconnectType();
            LogUtil.d("checkSocketState : 无网络");
            return;
        }
        if ((ChatSocket.getInstance().getReconnectType() == TYPE_RECONNECT_NOT
                || ChatSocket.getInstance().getReconnectType() == TYPE_RECONNECT_FAILURE)
                &&!ChatSocket.getInstance().isActiveShutdown()) {
            ChatSocket.getInstance().reconnect();
            LogUtil.d("checkSocketState : reconnect");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.d("onDestroy : close");
    }
}

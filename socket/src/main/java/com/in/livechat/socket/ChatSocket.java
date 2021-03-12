package com.in.livechat.socket;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import com.in.livechat.socket.listener.SocketReceiveMessageListener;
import com.in.livechat.socket.listener.ChatWebSocketListener;
import com.in.livechat.socket.service.ChatSocketService;
import com.in.livechat.socket.util.Cons;
import com.in.livechat.socket.util.LogUtil;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;

import static com.in.livechat.socket.util.Cons.INTERVAL_HEART;
import static com.in.livechat.socket.util.Cons.TYPE_RECONNECT_NOT;
import static com.in.livechat.socket.util.Cons.TYPE_RECONNECT_SUCCESS;


public class ChatSocket {
    private Context mContext;
    private static ChatSocket chatSocket;
    private OkHttpClient okHttpClient;
    private ChatWebSocketListener webSocketListener;
    private Request request;
    private boolean isActiveShutdown = false;
    private SocketReceiveMessageListener mSocketReceiveMessageListener;

    private ChatSocket() {
    }

    public static ChatSocket getInstance() {
        if (chatSocket == null) {
            synchronized (ChatSocket.class) {
                if (chatSocket == null) {
                    chatSocket = new ChatSocket();
                }
            }
        }
        return chatSocket;
    }

    /**
     * 开始
     *
     * @param context
     */
    public void start(Context context) {
        new ChatSocketService().startChatService(context);
    }


    /**
     * 停止
     */
    public void stop() {
        if (webSocketListener != null && webSocketListener.getOkHttpWebSocket() != null) {
            webSocketListener.getOkHttpWebSocket().close(Cons.CODE_CLOSE_NORMALLY, "stop");
            webSocketListener.setOkHttpWebSocket(null);
        }
        new ChatSocketService().stopChatService(mContext.getApplicationContext());
        okHttpClient = null;
        request = null;
        isActiveShutdown = true;
    }

    /**
     * 发送消息
     *
     * @param text
     * @return true发送中，false失败
     */
    public boolean send(String text) {
        if (webSocketListener != null && webSocketListener.getOkHttpWebSocket() != null) {
            webSocketListener.getOkHttpWebSocket().send(text);
            return true;
        } else {
            LogUtil.d("send：未连接");
            return false;
        }
    }

    /**
     * 当前是否连接成功
     *
     * @return
     */
    public boolean isConnectState() {
        if (webSocketListener != null) {
            return webSocketListener.getReconnectType() == TYPE_RECONNECT_SUCCESS;
        }
        return false;
    }

    /**
     * 获取当前连接状态
     *
     * @return
     */
    public int getReconnectType() {
        if (webSocketListener != null) {
            return webSocketListener.getReconnectType();
        }
        return TYPE_RECONNECT_NOT;
    }

    /**
     * 是否主动关闭
     *
     * @return
     */
    public boolean isActiveShutdown() {
        return isActiveShutdown;
    }

    /**
     * 链接
     */
    public void connect(Context context) {
        this.mContext = context;
        isActiveShutdown = false;
        if (okHttpClient == null) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.retryOnConnectionFailure(false);
            builder.pingInterval(INTERVAL_HEART, TimeUnit.SECONDS);
            okHttpClient = builder.build();
        }
        request = new Request.Builder().url(Cons.wsUrl).build();
        if (webSocketListener == null) {
            webSocketListener = new ChatWebSocketListener(mContext);
            webSocketListener.setSocketReceiveMessageListener(mSocketReceiveMessageListener);
        }
        webSocketListener.setReconnectType(Cons.TYPE_RECONNECTING);
        okHttpClient.newWebSocket(request, webSocketListener);
    }

    /**
     * 重连
     */
    public void reconnect() {
        if (webSocketListener != null && webSocketListener.getOkHttpWebSocket() != null) {
            webSocketListener.getOkHttpWebSocket().close(Cons.CODE_CLOSE_NORMALLY, "stop");
            webSocketListener.setOkHttpWebSocket(null);
            okHttpClient = null;
        }
        connect(mContext);
        if (webSocketListener != null) {
            webSocketListener.setReconnectType(Cons.TYPE_RECONNECTING);
        }
    }

    /**
     * 设置消息回执
     *
     * @param socketReceiveMessageListener
     */
    public void setSocketReceiveMessageListener(SocketReceiveMessageListener socketReceiveMessageListener) {
        mSocketReceiveMessageListener = socketReceiveMessageListener;
        if (webSocketListener != null) {
            webSocketListener.setSocketReceiveMessageListener(socketReceiveMessageListener);
        }
    }

    public void unSocketReceiveMessageListener() {
        mSocketReceiveMessageListener = null;
        if (webSocketListener != null) {
            webSocketListener.unSocketReceiveMessageListener();
        }
    }

    /**
     * 注册状态监听广播
     *
     * @param context
     * @param localReceiver
     */
    public void registerReceiverReconnectTypeManager(Context context, BroadcastReceiver localReceiver) {
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(context);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Cons.ACTION_TYPE_RECONNECT_NOT);
        intentFilter.addAction(Cons.ACTION_TYPE_RECONNECTING);
        intentFilter.addAction(Cons.ACTION_TYPE_RECONNECT_SUCCESS);
        intentFilter.addAction(Cons.ACTION_TYPE_RECONNECT_FAILURE);
        intentFilter.addAction(Cons.ACTION_TYPE_RECONNECT_CLOSE);
        localBroadcastManager.registerReceiver(localReceiver, intentFilter);
    }

    /**
     * 取消注册状态监听广播
     *
     * @param context
     * @param localReceiver
     */
    public void unregisterReceiverReconnectTypeManager(Context context, BroadcastReceiver localReceiver) {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(localReceiver);
    }


}

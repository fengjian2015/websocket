package com.in.livechat.socket.listener;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.in.livechat.socket.ChatSocket;
import com.in.livechat.socket.util.LogUtil;

import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

import static com.in.livechat.socket.util.Cons.ACTION_TYPE_RECONNECTING;
import static com.in.livechat.socket.util.Cons.ACTION_TYPE_RECONNECT_CLOSE;
import static com.in.livechat.socket.util.Cons.ACTION_TYPE_RECONNECT_FAILURE;
import static com.in.livechat.socket.util.Cons.ACTION_TYPE_RECONNECT_NOT;
import static com.in.livechat.socket.util.Cons.ACTION_TYPE_RECONNECT_SUCCESS;
import static com.in.livechat.socket.util.Cons.CODE_CLOSE_NORMALLY;
import static com.in.livechat.socket.util.Cons.TYPE_RECONNECTING;
import static com.in.livechat.socket.util.Cons.TYPE_RECONNECT_CLOSE;
import static com.in.livechat.socket.util.Cons.TYPE_RECONNECT_FAILURE;
import static com.in.livechat.socket.util.Cons.TYPE_RECONNECT_NOT;
import static com.in.livechat.socket.util.Cons.TYPE_RECONNECT_SUCCESS;


public class ChatWebSocketListener extends WebSocketListener {
    private int reconnectType = TYPE_RECONNECT_NOT;//当前链接类型
    private WebSocket okHttpWebSocket;
    private SocketReceiveMessageListener mSocketReceiveMessageListener;
    private Context mContext;

    public ChatWebSocketListener(Context context) {
        mContext = context;
    }

    public void setSocketReceiveMessageListener(SocketReceiveMessageListener socketReceiveMessageListener) {
        mSocketReceiveMessageListener = socketReceiveMessageListener;
    }

    public void unSocketReceiveMessageListener() {
        mSocketReceiveMessageListener = null;
    }

    public WebSocket getOkHttpWebSocket() {
        return okHttpWebSocket;
    }

    public void setOkHttpWebSocket(WebSocket okHttpWebSocket) {
        this.okHttpWebSocket = okHttpWebSocket;
    }

    public int getReconnectType() {
        return reconnectType;
    }

    public void setReconnectType(int reconnectType) {
        this.reconnectType = reconnectType;
        String action;
        switch (reconnectType) {
            case TYPE_RECONNECTING:
                action = ACTION_TYPE_RECONNECTING;
                break;
            case TYPE_RECONNECT_SUCCESS:
                action = ACTION_TYPE_RECONNECT_SUCCESS;
                break;
            case TYPE_RECONNECT_FAILURE:
                action = ACTION_TYPE_RECONNECT_FAILURE;
                break;
            case TYPE_RECONNECT_CLOSE:
                action = ACTION_TYPE_RECONNECT_CLOSE;
                break;
            default:
                action = ACTION_TYPE_RECONNECT_NOT;
                break;
        }
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent(action));
    }


    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        super.onOpen(webSocket, response);
        LogUtil.d("webSocket onOpen");
        okHttpWebSocket = webSocket;
        if (ChatSocket.getInstance().isActiveShutdown()) {
            ChatSocket.getInstance().stop();
            LogUtil.d("webSocket:close，连接过程中主动关闭情况");
        }
        setReconnectType(TYPE_RECONNECT_SUCCESS);
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        super.onMessage(webSocket, text);
        LogUtil.d("webSocket onMessage", text);
        if (mSocketReceiveMessageListener != null) {
            mSocketReceiveMessageListener.receiveText(text);
        }
    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        super.onClosing(webSocket, code, reason);
        LogUtil.d("webSocket onClosing", "code: " + code + ", reason: " + reason);
        if (code != CODE_CLOSE_NORMALLY) {
            setReconnectType(TYPE_RECONNECT_FAILURE);
        } else {
            setReconnectType(TYPE_RECONNECT_CLOSE);
        }
    }

    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
        super.onClosed(webSocket, code, reason);
        LogUtil.d("webSocket onClosed", "code: " + code + ", reason: " + reason);
        if (code != CODE_CLOSE_NORMALLY) {
            setReconnectType(TYPE_RECONNECT_FAILURE);
        } else {
            setReconnectType(TYPE_RECONNECT_CLOSE);
        }
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        super.onFailure(webSocket, t, response);
        LogUtil.d("webSocket onFailure", "t: " + t);
        setReconnectType(TYPE_RECONNECT_FAILURE);
    }
}

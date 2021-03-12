package com.in.livechat.socket.util;

public class Cons {
    /****服务器地址**/
    public final static String wsUrl="ws://echo.websocket.org";
//    public final static String wsUrl="ws://121.40.165.18:8800";

    public static final int INTERVAL_HEART = 10;//心跳间隔
    public static final int CODE_CLOSE_NORMALLY = 1001;//关闭code
    /***链接状态**/
    public static final int TYPE_RECONNECT_NOT = 0;//未进行连
    public static final int TYPE_RECONNECTING = 1;//重连中
    public static final int TYPE_RECONNECT_SUCCESS = 2;//连接成功
    public static final int TYPE_RECONNECT_FAILURE = 3;//连接失败
    public static final int TYPE_RECONNECT_CLOSE = 4;//主动关闭
    /***广播发送链接状态**/
    public static final String ACTION_TYPE_RECONNECT_NOT = "action_type_reconnect_not";//未进行重连
    public static final String ACTION_TYPE_RECONNECTING = "action_type_reconnecting";//重连中
    public static final String ACTION_TYPE_RECONNECT_SUCCESS = "action_type_reconnect_success";//连接成功
    public static final String ACTION_TYPE_RECONNECT_FAILURE = "action_type_reconnect_failure";//连接失败
    public static final String ACTION_TYPE_RECONNECT_CLOSE = "action_type_reconnect_close";//主动关闭
}

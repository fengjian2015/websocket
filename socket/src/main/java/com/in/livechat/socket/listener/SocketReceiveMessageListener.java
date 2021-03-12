package com.in.livechat.socket.listener;

/**
 * description ： 接收消息，消息类型分类处理
 * author : Fickle
 * date : 2021/3/8 14:03
 */
public interface SocketReceiveMessageListener {
    /**
     * 接收text文本
     * @param text
     */
    void receiveText(String text);
}

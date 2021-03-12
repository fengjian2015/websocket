package com.in.livechat.ui.chat.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.in.livechat.socket.ChatSocket;
import com.in.livechat.socket.listener.SocketReceiveMessageListener;
import com.in.livechat.ui.R;
import com.in.livechat.ui.chat.impl.ISocketReceiveMessageListener;
import com.in.livechat.ui.chat.model.ChatMsg;
import com.in.livechat.ui.chat.model.FileInfo;
import com.in.livechat.ui.common.callback.LubanCallBack;
import com.in.livechat.ui.common.util.ImgUtil;
import com.in.livechat.ui.common.util.SpCons;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.util.List;

import static com.in.livechat.socket.util.Cons.ACTION_TYPE_RECONNECTING;
import static com.in.livechat.socket.util.Cons.ACTION_TYPE_RECONNECT_FAILURE;
import static com.in.livechat.socket.util.Cons.ACTION_TYPE_RECONNECT_SUCCESS;
import static com.in.livechat.ui.chat.model.ChatMsg.TYPE_CONTENT_TEXT;
import static com.in.livechat.ui.chat.model.ChatMsg.TYPE_SEND_FAIL;

/**
 * 聊天数据相关处理
 */
public class ChatActivity extends BaseChatUiActivity implements SocketReceiveMessageListener {
    private LocalBroadcastReceiver localBroadcastReceiver;

    public static void startChat(Context context, String user) {
        SpCons.setUser(context, user);
        context.startActivity(new Intent(context, ChatActivity.class));
    }

    @Override
    protected void init() {
        super.init();
        ChatSocket.getInstance().start(this);
        ChatSocket.getInstance().setSocketReceiveMessageListener(this);
        localBroadcastReceiver = new LocalBroadcastReceiver();
        test();
    }

    private void test() {
        // TODO: 2021/3/12 根据具体规则编写数据读取方式，roomId确定
        addAllMsgDate(getDataList(System.currentTimeMillis()), false);
    }

    private List<ChatMsg> getDataList(long time) {
        return DataSupport.where("roomId = ? and createTimestamp < ?", SpCons.getUser(getHostActivity()), Long.toString(time)).order("createTimestamp desc").limit(NUMBER_LIMIT).find(ChatMsg.class);
    }

    @Override
    protected void setListener() {
        super.setListener();
        ChatSocket.getInstance().registerReceiverReconnectTypeManager(this, localBroadcastReceiver);

    }

    @Override
    protected void compressImg(final String filePath) {
        ImgUtil.compress(getHostActivity(), filePath, new LubanCallBack() {
            @Override
            public void onSuccess(File outputFile) {
                FileInfo fileInfo = new FileInfo();
                fileInfo.setFileName(outputFile.getName());
                fileInfo.setFileType(FileInfo.TYPE_IMG);
                fileInfo.setFileLength(outputFile.length());
                fileInfo.setOriginPath(filePath);
                fileInfo.setCompressPath(outputFile.getAbsolutePath());
                // TODO: 2021/3/11 上传图片发送
            }
        });
    }

    @Override
    protected void baseImgLongClick(int position, View view) {

    }

    @Override
    protected void baseChatSendClick(String text) {
        // TODO: 2021/3/12 roomId需要确定，UserId确定 ，消息体需要确认
        ChatMsg chatMsg = new ChatMsg(SpCons.getUser(getHostActivity()), SpCons.getUser(getHostActivity()), TYPE_CONTENT_TEXT, text, System.currentTimeMillis());
        chatMsg.save();
        if (!ChatSocket.getInstance().send(text+";"+chatMsg.getLocalMsgId())) {
            chatMsg.setSendState(TYPE_SEND_FAIL);
            chatMsg.save();
        }
        addMsgDate(chatMsg);
    }

    @Override
    protected void baseLoadData() {
        addAllMsgDate(getDataList(mChatMsgList.get(0).getCreateTimestamp()), true);
    }

    /**
     * 接收socket文本消息
     * @param text
     */
    @Override
    public void receiveText(String text) {
        // TODO: 2021/3/12 需要确认消息结构
        String[] split = text.split(";");
        List<ChatMsg> chatMsgs = DataSupport.where("localMsgId = ? ", split[1]).find(ChatMsg.class);
        if (chatMsgs!=null&&chatMsgs.size()>0){
            ChatMsg chatMsg = chatMsgs.get(0);
            chatMsg.setSendState(ChatMsg.TYPE_SEND_SUCCESS);
            chatMsg.save();
            updateMsg(chatMsg);
        }
        // TODO: 2021/3/12 模拟客服消息
        ChatMsg chatMsg = new ChatMsg(SpCons.getUser(getHostActivity()), "kefu", TYPE_CONTENT_TEXT, "客服消息:"+text, System.currentTimeMillis());
        chatMsg.save();
        updateMsg(chatMsg);
    }

    public class LocalBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case ACTION_TYPE_RECONNECT_SUCCESS:
                    //链接成功
                    tvConversationChatCenterStatus.setVisibility(View.GONE);
                    tvConversationChatCenterStatus.setText(null);
                    break;
                case ACTION_TYPE_RECONNECT_FAILURE:
                case ACTION_TYPE_RECONNECTING:
                    //重连
                    //链接失败
                    tvConversationChatCenterStatus.setVisibility(View.VISIBLE);
                    tvConversationChatCenterStatus.setText(getResources().getString(R.string.live_socket_connecting));
                    break;
                default:
                    //未连接//主动关闭
                    tvConversationChatCenterStatus.setVisibility(View.VISIBLE);
                    tvConversationChatCenterStatus.setText(getResources().getString(R.string.live_socket_not_connected));
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ChatSocket.getInstance().unregisterReceiverReconnectTypeManager(this, localBroadcastReceiver);
    }
}

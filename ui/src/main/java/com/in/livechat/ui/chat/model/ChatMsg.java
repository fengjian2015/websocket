package com.in.livechat.ui.chat.model;

import com.in.livechat.ui.common.util.UUIDUtil;

import org.litepal.crud.DataSupport;

import java.io.Serializable;
import java.util.Comparator;
import java.util.UUID;

/**
 * 消息
 * @author Darren
 * Created by Darren on 2018/12/17.
 */
public class ChatMsg extends DataSupport implements Serializable {

    public static final int TYPE_CONTENT_TEXT = 0;//文字内容
    public static final int TYPE_CONTENT_FILE = 2;//文件图片

    public static final int TYPE_SEND_FAIL = -1;//发送失败
    public static final int TYPE_SENDING = 0;//发送中
    public static final int TYPE_SEND_SUCCESS = 1;//发送成功

    public static final int TYPE_READ_TAPE = 1;//设置录音已读
    public static final int TYPE_READ_NORMAL = 2;//设置消息已读

    private String msgId;//消息id
    private String localMsgId;//本地唯一id
    private String roomId;//房间id
    private String roomType;//friend/group/temporary
    private String senderId;//发送者id
    private String receiverId;//接收者id
    private int contentType;//0文本; 1文件图片;
    private String content;//消息内容
    private String contentDesc;//版本不支持时的提示内容
    private long createTimestamp;//消息生成时间戳
    private int deleteMark;//记录是否本地删除 0未删除 1删除
    private int sendState;//发送状态 -1发送失败； 0发送中；1发送成功；
    private int emoMark;//是否包含表情
    private int phoneMark;//是否包含手机号
    private int urlMark;//是否包含网址
    private int showMark;//是否显示
    private FileInfo fileInfo;//文件信息

    public ChatMsg(String roomId, String senderId, int contentType, String content, long createTimestamp) {
        this.roomId = roomId;
        this.senderId = senderId;
        this.contentType = contentType;
        this.content = content;
        this.createTimestamp = createTimestamp;
        localMsgId= UUIDUtil.get32UUID();
    }

    public ChatMsg(String roomId, String roomType, String senderId, int contentType, String content, long createTimestamp) {
        this.roomId = roomId;
        this.roomType = roomType;
        this.senderId = senderId;
        this.contentType = contentType;
        this.content = content;
        this.createTimestamp = createTimestamp;
        localMsgId= UUIDUtil.get32UUID();
    }

    //按createTimestamp正序排列
    public static class TimeRiseComparator implements Comparator<ChatMsg> {
        @Override
        public int compare(ChatMsg o1, ChatMsg o2) {
            if (o1.getCreateTimestamp() - o2.getCreateTimestamp() < 0) {
                return -1;
            } else if (o1.getCreateTimestamp() - o2.getCreateTimestamp() > 0) {
                return 1;
            } else {
                return 0;
            }
        }
    }



    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getLocalMsgId() {
        return localMsgId;
    }

    public void setLocalMsgId(String localMsgId) {
        this.localMsgId = localMsgId;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public int getContentType() {
        return contentType;
    }

    public void setContentType(int contentType) {
        this.contentType = contentType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContentDesc() {
        return contentDesc;
    }

    public void setContentDesc(String contentDesc) {
        this.contentDesc = contentDesc;
    }

    public long getCreateTimestamp() {
        return createTimestamp;
    }

    public void setCreateTimestamp(long createTimestamp) {
        this.createTimestamp = createTimestamp;
    }

    public int getDeleteMark() {
        return deleteMark;
    }

    public void setDeleteMark(int deleteMark) {
        this.deleteMark = deleteMark;
    }

    public int getSendState() {
        return sendState;
    }

    public void setSendState(int sendState) {
        this.sendState = sendState;
    }

    public int getEmoMark() {
        return emoMark;
    }

    public void setEmoMark(int emoMark) {
        this.emoMark = emoMark;
    }

    public int getPhoneMark() {
        return phoneMark;
    }

    public void setPhoneMark(int phoneMark) {
        this.phoneMark = phoneMark;
    }

    public int getUrlMark() {
        return urlMark;
    }

    public void setUrlMark(int urlMark) {
        this.urlMark = urlMark;
    }

    public int getShowMark() {
        return showMark;
    }

    public void setShowMark(int showMark) {
        this.showMark = showMark;
    }

    public FileInfo getFileInfo() {
        return fileInfo;
    }

    public void setFileInfo(FileInfo fileInfo) {
        this.fileInfo = fileInfo;
    }

    @Override
    public String toString() {
        return "ChatMsg{" +
                ", msgId='" + msgId + '\'' +
                ", localMsgId='" + localMsgId + '\'' +
                ", roomId='" + roomId + '\'' +
                ", roomType='" + roomType + '\'' +
                ", senderId='" + senderId + '\'' +
                ", receiverId='" + receiverId + '\'' +
                ", contentType=" + contentType +
                ", content='" + content + '\'' +
                ", contentDesc='" + contentDesc + '\'' +
                ", createTimestamp=" + createTimestamp +
                ", deleteMark=" + deleteMark +
                ", sendState=" + sendState +
                ", emoMark=" + emoMark +
                ", phoneMark=" + phoneMark +
                ", urlMark=" + urlMark +
                ", showMark=" + showMark +
                ", fileInfo=" + fileInfo +
                '}';
    }
}

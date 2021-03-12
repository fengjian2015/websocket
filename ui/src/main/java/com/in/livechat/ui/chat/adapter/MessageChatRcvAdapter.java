package com.in.livechat.ui.chat.adapter;

import android.content.Context;
import android.text.TextUtils;
import com.in.livechat.ui.R;
import com.in.livechat.ui.chat.model.ChatMsg;
import com.in.livechat.ui.chat.model.FileInfo;
import com.in.livechat.ui.chat.util.HyperLinkUtil;
import com.in.livechat.ui.common.util.ImgUtil;
import com.in.livechat.ui.chat.widget.CustomLinkMovementMethod;
import java.util.List;

/**
 * Created by Darren on 2018/12/17.
 */
public class MessageChatRcvAdapter extends BaseMessageChatRcvAdapter {

    private Context context;
    private List<ChatMsg> msgList;

    public MessageChatRcvAdapter(Context context, List<ChatMsg> msgList) {
        super(context, msgList);
        this.context = context;
        this.msgList = msgList;
    }

    //右侧文字
    @Override
    protected void setRightTextHolder(RightTextHolder rightTextHolder, int position) {
        if (TextUtils.isEmpty(msgList.get(position).getContent())) {
            return;
        }
        rightTextHolder.contentTv.setMovementMethod(CustomLinkMovementMethod.getInstance());
        HyperLinkUtil.setSpanStr(context,rightTextHolder.contentTv,msgList.get(position),msgList.get(position).getContent(),true);

    }

    //右侧图片
    @Override
    protected void setRightImgHolder(RightImgHolder rightImgHolder, int position) {
        FileInfo fileInfo = msgList.get(position).getFileInfo();
        String filePath = "";
        if (!TextUtils.isEmpty(fileInfo.getOriginPath())) {
            filePath = fileInfo.getOriginPath();
        } else if (!TextUtils.isEmpty(fileInfo.getSavePath())) {
            filePath = fileInfo.getSavePath();
        } else if (!TextUtils.isEmpty(fileInfo.getDownloadPath())) {
            filePath = fileInfo.getDownloadPath();
        }
        ImgUtil.load(context, filePath, rightImgHolder.imgIv, R.drawable.live_con_img_vertical);
    }

    //左侧文字
    @Override
    protected void setLeftTextHolder(LeftTextHolder leftTextHolder, int position) {
        if (TextUtils.isEmpty(msgList.get(position).getContent())) {
            return;
        }
        leftTextHolder.contentTv.setMovementMethod(CustomLinkMovementMethod.getInstance());
        HyperLinkUtil.setSpanStr(context,leftTextHolder.contentTv,msgList.get(position),msgList.get(position).getContent(),true);
    }

    //左侧图片
    @Override
    protected void setLeftImgHolder(LeftImgHolder leftImgHolder, int position) {
        FileInfo fileInfo = msgList.get(position).getFileInfo();
        String filePath = "";
        if (!TextUtils.isEmpty(fileInfo.getOriginPath())) {
            filePath = fileInfo.getOriginPath();
        } else if (!TextUtils.isEmpty(fileInfo.getSavePath())) {
            filePath = fileInfo.getSavePath();
        } else if (!TextUtils.isEmpty(fileInfo.getDownloadPath())) {
            filePath = fileInfo.getDownloadPath();
        }
        ImgUtil.load(context, filePath, leftImgHolder.imgIv, R.drawable.live_con_img_vertical);
    }

}

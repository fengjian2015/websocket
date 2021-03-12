package com.in.livechat.ui.chat.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.in.livechat.ui.R;
import com.in.livechat.ui.chat.model.ChatMsg;
import com.in.livechat.ui.chat.model.FileInfo;
import com.in.livechat.ui.common.util.ImgUtil;
import com.in.livechat.ui.chat.util.TimeUtil;
import com.in.livechat.ui.common.util.SpCons;

import java.util.List;

/**
 * Created by Darren on 2018/12/17.
 */
public abstract class BaseMessageChatRcvAdapter extends BaseRcvTopLoadAdapter {

    private Context context;
    private List<ChatMsg> msgList;
    private OnMsgClickListener msgClickListener;
    private OmMsgLongClickListener mOmMsgLongClickListener;
    private String friendAvatarUrl;

    //右侧，己方
    private final int TYPE_RIGHT_VERSION_NOT = 10;//版本不支持
    private final int TYPE_RIGHT_TEXT_AT = 11;//文本+@
    private final int TYPE_RIGHT_FILE_IMG = 12;//图片

    //左侧，对方
    private final int TYPE_LEFT_VERSION_NOT = 100;//版本不支持
    private final int TYPE_LEFT_TEXT_AT = 101;//文本
    private final int TYPE_LEFT_FILE_IMG = 102;//图片


    public BaseMessageChatRcvAdapter(Context context, List<ChatMsg> msgList) {
        this.context = context;
        this.msgList = msgList;
    }

    //右侧自己 文字+@
    protected abstract void setRightTextHolder(RightTextHolder rightTextHolder, int position);

    //右侧自己 图片
    protected abstract void setRightImgHolder(RightImgHolder rightImgHolder, int position);

    //左侧对方 文字+@
    protected abstract void setLeftTextHolder(LeftTextHolder leftTextHolder, int position);

    //左侧对方 图片
    protected abstract void setLeftImgHolder(LeftImgHolder leftImgHolder, int position);

    public interface OnMsgClickListener {
        void imgClick(int position);//点击图片
    }

    public void setOnMsgClickListener(OnMsgClickListener msgClickListener) {
        this.msgClickListener = msgClickListener;
    }

    public interface OmMsgLongClickListener {

        /**
         * 图片长按事件
         *
         * @param position
         * @param view
         */
        void imgLongClick(int position, View view);
    }

    public void setOmMsgLongClickListener(OmMsgLongClickListener omMsgLongClickListener) {
        this.mOmMsgLongClickListener = omMsgLongClickListener;
    }

    @Override
    protected Context getContext() {
        return context;
    }

    @Override
    protected List getObjectList() {
        return msgList;
    }

    @Override
    protected RecyclerView.ViewHolder getItemViewHolder(int viewType) {
        switch (viewType) {
            //右边
            case TYPE_RIGHT_TEXT_AT:
            case TYPE_RIGHT_VERSION_NOT:
                return new RightTextHolder(View.inflate(context, R.layout.live_layout_item_message_text_right, null));
            case TYPE_RIGHT_FILE_IMG:
                return new RightImgHolder(View.inflate(context, R.layout.live_layout_item_message_img_right, null));
            //左边
            case TYPE_LEFT_TEXT_AT:
                return new LeftTextHolder(View.inflate(context, R.layout.live_layout_item_message_text_left, null));
            case TYPE_LEFT_FILE_IMG:
                return new LeftImgHolder(View.inflate(context, R.layout.live_layout_item_message_img_left, null));
            default:
                return new LeftTextHolder(View.inflate(context, R.layout.live_layout_item_message_text_left, null));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (!getTopPullViewVisible()) {
            return getViewType(position);
        } else if (getTopPullViewVisible() && position != 0) {
            return getViewType(position - 1);
        }
        return super.getItemViewType(position);
    }

    @Override
    protected void bindViewData(final RecyclerView.ViewHolder holder, final int position) {
//        long startTime = System.currentTimeMillis();
        if (holder instanceof RightViewHolder) {
            //右侧
            if (msgList.get(position) == null) {
                return;
            }
            RightViewHolder rightHolder = (RightViewHolder) holder;
            //右侧 己方 消息发送时间
            if (rightHolder.timeTv != null) {
                rightHolder.timeTv.setText(TimeUtil.timestampToStr(msgList
                        .get(position).getCreateTimestamp(), "HH:mm"));
            }
            //右侧 日期
            long createTimestamp = msgList.get(position).getCreateTimestamp();
            if (position == 0) {
                if (!getTopPullViewVisible()) {
                    rightHolder.dateTv.setVisibility(View.VISIBLE);
                    String dateStr = TimeUtil.getFormatDate(msgList.get(position).getCreateTimestamp());
                    rightHolder.dateTv.setText(dateStr);
                } else {
                    rightHolder.dateTv.setVisibility(View.GONE);
                }
            } else {
                long lastTimestamp = msgList.get(position - 1).getCreateTimestamp();
                if (TimeUtil.isSameDay(createTimestamp, lastTimestamp)) {
                    rightHolder.dateTv.setVisibility(View.GONE);
                } else {
                    rightHolder.dateTv.setVisibility(View.VISIBLE);
                    String dateStr = TimeUtil.getFormatDate(msgList.get(position).getCreateTimestamp());
                    rightHolder.dateTv.setText(dateStr);
                }
            }

            //右侧 消息发送状态
            if (msgList.get(position).getSendState() == ChatMsg.TYPE_SEND_FAIL) {
                rightHolder.failTopIv.setVisibility(View.INVISIBLE);
                rightHolder.failCenterIv.setVisibility(View.VISIBLE);
                rightHolder.sendingIv.setVisibility(View.INVISIBLE);

            } else if (msgList.get(position).getSendState() == ChatMsg.TYPE_SENDING) {
                rightHolder.failTopIv.setVisibility(View.INVISIBLE);
                rightHolder.failCenterIv.setVisibility(View.INVISIBLE);
                rightHolder.sendingIv.setVisibility(View.VISIBLE);
                rightHolder.sendingIv.startAnimation(AnimationUtils
                        .loadAnimation(context, R.anim.live_rotate360_anim));

            } else if (msgList.get(position).getSendState() == ChatMsg.TYPE_SEND_SUCCESS) {
                rightHolder.failTopIv.setVisibility(View.INVISIBLE);
                rightHolder.failCenterIv.setVisibility(View.INVISIBLE);
                rightHolder.sendingIv.setVisibility(View.INVISIBLE);
            }

            if (holder instanceof RightTextHolder) {
                //右侧 己方 text + @
                final RightTextHolder textHolder = (RightTextHolder) holder;
                setRightTextHolder(textHolder, position);

                //右侧 发送失败
                if (msgList.get(position).getSendState() == ChatMsg.TYPE_SEND_FAIL) {
                    textHolder.failTopIv.setVisibility(View.VISIBLE);
                    textHolder.failCenterIv.setVisibility(View.INVISIBLE);
                }

            } else if (holder instanceof RightImgHolder) {
                //右侧 图片
                if (msgList.get(position).getFileInfo() == null) {
                    return;
                }
                setRightImgHolder((RightImgHolder) holder, position);
                ((RightImgHolder) holder).imgIv.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (mOmMsgLongClickListener != null) {
                            mOmMsgLongClickListener.imgLongClick(position, ((RightImgHolder) holder).imgIv);
                        }
                        return true;
                    }
                });
                ((RightImgHolder) holder).imgIv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (msgClickListener != null) {
                            msgClickListener.imgClick(position);
                        }
                    }
                });

            }
        } else if (holder instanceof LeftViewHolder) {
            //左侧
            if (msgList.get(position) == null) {
                return;
            }
            final LeftViewHolder leftHolder = (LeftViewHolder) holder;
            //左侧 日期
            long currentTimestamp = msgList.get(position).getCreateTimestamp();
            if (position == 0) {
                if (!getTopPullViewVisible()) {
                    leftHolder.dateTv.setVisibility(View.VISIBLE);
                    String dateStr = TimeUtil.getFormatDate(msgList.get(position).getCreateTimestamp());
                    leftHolder.dateTv.setText(dateStr);
                } else {
                    leftHolder.dateTv.setVisibility(View.GONE);
                }
            } else {
                long lastTimestamp = msgList.get(position - 1).getCreateTimestamp();
                if (TimeUtil.isSameDay(currentTimestamp, lastTimestamp)) {
                    leftHolder.dateTv.setVisibility(View.GONE);
                } else {
                    leftHolder.dateTv.setVisibility(View.VISIBLE);
                    String dateStr = TimeUtil.getFormatDate(msgList.get(position).getCreateTimestamp());
                    leftHolder.dateTv.setText(dateStr);
                }
            }
            //左侧名字
            leftHolder.nameTv.setVisibility(View.VISIBLE);
            leftHolder.nameTv.setText("客服");

            //左侧 头像
            // TODO: 2021/3/10 为空
            ImgUtil.loadCircle(context, friendAvatarUrl, leftHolder.avatarIv);

            //左侧 消息发送时间
            if (leftHolder.timeTv != null) {
                leftHolder.timeTv.setText(TimeUtil.timestampToStr(msgList.get(position)
                        .getCreateTimestamp(), "HH:mm"));
            }

            if (holder instanceof LeftTextHolder) {
                //左侧 文字+@
                setLeftTextHolder((LeftTextHolder) holder, position);
            } else if (holder instanceof LeftImgHolder) {
                //左侧 图片
                if (msgList.get(position).getFileInfo() == null) {
                    return;
                }
                setLeftImgHolder((LeftImgHolder) holder, position);
                ((LeftImgHolder) holder).imgIv.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (mOmMsgLongClickListener != null) {
                            mOmMsgLongClickListener.imgLongClick(position, ((LeftImgHolder) holder).imgIv);
                        }
                        return true;
                    }
                });
                ((LeftImgHolder) holder).imgIv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (msgClickListener != null) {
                            msgClickListener.imgClick(position);
                        }
                    }
                });
            }
        }
    }

    private int getViewType(int position) {
        //消息是否自己发送
        boolean selfState = false;
        if (SpCons.getUser(context).equals(msgList.get(position).getSenderId())) {
            selfState = true;
        }
        switch (msgList.get(position).getContentType()) {
            case ChatMsg.TYPE_CONTENT_TEXT:
                if (selfState) {
                    return TYPE_RIGHT_TEXT_AT;
                } else {
                    return TYPE_LEFT_TEXT_AT;
                }
            case ChatMsg.TYPE_CONTENT_FILE:
                if (msgList.get(position) != null
                        && msgList.get(position).getFileInfo() != null) {
                    switch (msgList.get(position).getFileInfo().getFileType()) {
                        case FileInfo.TYPE_IMG:
                            if (selfState) {
                                return TYPE_RIGHT_FILE_IMG;
                            } else {
                                return TYPE_LEFT_FILE_IMG;
                            }
                        default:
                            if (selfState) {
                                return TYPE_RIGHT_VERSION_NOT;
                            } else {
                                return TYPE_LEFT_VERSION_NOT;
                            }
                    }
                }
            default:
                if (selfState) {
                    return TYPE_RIGHT_VERSION_NOT;
                } else {
                    return TYPE_LEFT_VERSION_NOT;
                }
        }
    }


    protected static class RightTextHolder extends RightViewHolder {
        public TextView contentTv;

        private RightTextHolder(View itemView) {
            super(itemView);
            contentTv = itemView.findViewById(R.id.tv_message_chat_right_content);
        }
    }

    protected static class RightImgHolder extends RightViewHolder {
        public ImageView imgIv;

        private RightImgHolder(View itemView) {
            super(itemView);
            imgIv = itemView.findViewById(R.id.iv_message_chat_right_img);
        }
    }

    protected static class LeftTextHolder extends LeftViewHolder {
        public TextView contentTv;

        private LeftTextHolder(View itemView) {
            super(itemView);
            contentTv = itemView.findViewById(R.id.tv_conversation_chat_left_content);
        }
    }


    protected static class LeftImgHolder extends LeftViewHolder {
        public ImageView imgIv;

        private LeftImgHolder(View itemView) {
            super(itemView);
            imgIv = itemView.findViewById(R.id.iv_message_chat_left_img);
        }
    }

    private static class RightViewHolder extends RecyclerView.ViewHolder {
        private TextView timeTv, dateTv;
        private ImageView sendingIv;
        ImageView failTopIv, failCenterIv;

        private RightViewHolder(View itemView) {
            super(itemView);
            timeTv = itemView.findViewById(R.id.tv_conversation_chat_time_right);
            failTopIv = itemView.findViewById(R.id.iv_chat_send_fail_top_right);
            failCenterIv = itemView.findViewById(R.id.iv_chat_send_fail_center_right);
            sendingIv = itemView.findViewById(R.id.iv_chat_text_msg_sending_right);
            dateTv = itemView.findViewById(R.id.tv_message_chat_date);
        }
    }

    private static class LeftViewHolder extends RecyclerView.ViewHolder {
        ImageView avatarIv, redPointIv, loadingIv;
        private TextView timeTv, dateTv, nameTv;

        private LeftViewHolder(View itemView) {
            super(itemView);
            avatarIv = itemView.findViewById(R.id.civ_message_chat_left_avatar);
            redPointIv = itemView.findViewById(R.id.iv_conversation_unread_red_point_left);
            loadingIv = itemView.findViewById(R.id.iv_chat_text_msg_loading_left);
            timeTv = itemView.findViewById(R.id.tv_conversation_chat_time_left);
            dateTv = itemView.findViewById(R.id.tv_message_chat_date);
            nameTv = itemView.findViewById(R.id.tv_conversation_chat_left_name);
        }
    }

}

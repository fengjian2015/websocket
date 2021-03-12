package com.in.livechat.ui.chat.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.in.livechat.ui.R;
import com.in.livechat.ui.album.AlbumChoiceActivity;
import com.in.livechat.ui.album.ImgShowActivity;
import com.in.livechat.ui.album.model.ImgUrl;
import com.in.livechat.ui.album.utils.AlbumCons;
import com.in.livechat.ui.chat.adapter.BaseMessageChatRcvAdapter;
import com.in.livechat.ui.chat.adapter.BaseRcvTopLoadAdapter;
import com.in.livechat.ui.chat.adapter.MessageChatRcvAdapter;
import com.in.livechat.ui.chat.impl.CustomTextWatcher;
import com.in.livechat.ui.chat.model.ChatMsg;
import com.in.livechat.ui.chat.model.FileInfo;
import com.in.livechat.ui.chat.widget.KeyboardRelativeLayout;
import com.in.livechat.ui.chat.widget.MyLinearLayoutManager;
import com.in.livechat.ui.common.base.BaseChatActivity;
import com.in.livechat.ui.common.util.ImgUtil;
import com.in.livechat.ui.common.util.SpCons;
import com.in.livechat.ui.common.util.SystemUtil;
import com.in.livechat.ui.emotion.impl.IEmotionSelectedListener;
import com.in.livechat.ui.emotion.model.Emoticon;
import com.in.livechat.ui.emotion.model.FeaturesModel;
import com.in.livechat.ui.emotion.util.EmoticonUtil;
import com.in.livechat.ui.emotion.widget.EmotionLayout;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * description ： 聊天ui
 * author : Fickle
 * date : 2021/3/10 17:45
 */
public abstract class BaseChatUiActivity extends BaseChatActivity {
    protected RecyclerView rcvConversationChatContainer;
    protected KeyboardRelativeLayout krlMessageChatRootLayout;
    protected EditText etConversationChatContentInput;
    protected EmotionLayout conversationChatExpressionContainerEmoji;
    protected EmotionLayout conversationChatExpressionContainerFeatures;
    protected ImageView ivConversationChatEmoticon, ivConversationChatMore;
    protected TextView tvConversationChatCenterStatus;
    protected List<FeaturesModel> mFeaturesModelList = new ArrayList<>();//功能列表数据

    protected final int NUMBER_LIMIT = 10; //分页消息数量

    protected MessageChatRcvAdapter rcvAdapter;//消息
    protected List<ChatMsg> mChatMsgList = new ArrayList<>();//消息列表数据
    protected int topLoadLastOffset = 0;//下拉加载消息偏移量
    protected ArrayList<String> emList = new ArrayList<>();//表情保存
    protected boolean isEmoticonActive = false;//键盘/表情切换
    protected int mKeyboardHeight = 0;//键盘高度
    protected MyLinearLayoutManager layoutManager;
    //删除位置记录
    protected int delIndex = -1;
    protected int delLength = 0;

    /**
     * 处理图片后进行上传发送
     *
     * @param filePath
     */
    protected abstract void compressImg(final String filePath);

    /**
     * 图片长安
     *
     * @param position
     * @param view
     */
    protected abstract void baseImgLongClick(int position, View view);

    /**
     * 发送文本消息
     *
     * @param text
     */
    protected abstract void baseChatSendClick(String text);

    /**
     * 下拉刷新
     */
    protected abstract void baseLoadData();


    @Override
    protected void initView() {
        rcvConversationChatContainer = findViewById(R.id.rcv_conversation_chat_container);
        krlMessageChatRootLayout = findViewById(R.id.krl_message_chat_root_layout);
        etConversationChatContentInput = findViewById(R.id.et_conversation_chat_content_input);
        conversationChatExpressionContainerEmoji = findViewById(R.id.conversation_chat_expression_container_emoji);
        ivConversationChatEmoticon = findViewById(R.id.iv_conversation_chat_emoticon);
        tvConversationChatCenterStatus = findViewById(R.id.tv_conversation_chat_center_status);
        ivConversationChatMore = findViewById(R.id.iv_conversation_chat_more);
        conversationChatExpressionContainerFeatures = findViewById(R.id.conversation_chat_expression_container_features);
        etConversationChatContentInput.setFocusable(true);
        etConversationChatContentInput.setFocusableInTouchMode(true);
        etConversationChatContentInput.requestFocus();
    }

    @Override
    protected void init() {
        mKeyboardHeight = SpCons.getKeyboardHeight(getHostActivity());
        initRecyclerView();
        // 起初的布局可自动调整大小
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
//                | WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.live_activity_chat;
    }

    private void initRecyclerView() {
        layoutManager = new MyLinearLayoutManager(getHostActivity());
        rcvAdapter = new MessageChatRcvAdapter(this, mChatMsgList);
        layoutManager = new MyLinearLayoutManager(getHostActivity());
        rcvConversationChatContainer.setLayoutManager(layoutManager);
        rcvConversationChatContainer.setAdapter(rcvAdapter);
        rcvAdapter.setTopPullViewVisible(false);
    }


    @Override
    protected void setListener() {
        rcvConversationChatContainer.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (rcvAdapter.getItemCount() > 2) {
                    //获取可视的第一个view
                    View topView = layoutManager.getChildAt(1);
                    if (topView != null) {
                        //获取与该view的顶部的偏移量
                        topLoadLastOffset = topView.getTop();
                    }
                }
            }
        });

        krlMessageChatRootLayout.setOnKeyboardLayoutListener(new KeyboardRelativeLayout.OnKeyboardLayoutListener() {
            @Override
            public void keyboardStateChanged(boolean isKeyboardActive, int keyboardHeight) {
                if (isKeyboardActive) {
                    mKeyboardHeight = keyboardHeight;
                    krlMessageChatRootLayout.unKeyboardLayoutListener();
                }
            }
        });

        rcvAdapter.setOnLoadDataListener(new BaseRcvTopLoadAdapter.OnLoadDataListener() {
            @Override
            public void loadData() {
                baseLoadData();
            }
        });
        //长按
        rcvAdapter.setOmMsgLongClickListener(new BaseMessageChatRcvAdapter.OmMsgLongClickListener() {
            @Override
            public void imgLongClick(int position, View view) {
                baseImgLongClick(position, view);
            }
        });

        //点击
        rcvAdapter.setOnMsgClickListener(new BaseMessageChatRcvAdapter.OnMsgClickListener() {
            @Override
            public void imgClick(int position) {
                clickImage(position);
            }
        });


        /**
         * 输入框监听
         */
        etConversationChatContentInput.addTextChangedListener(new CustomTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (delIndex >= 0 && delLength > 0) {
                    int temp1 = delIndex;
                    int temp2 = delLength;
                    delIndex = -1;
                    delLength = 0;
                    s.delete(temp1 - temp2, temp1);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //说明，是删除
                if (count > 0 && after == 0) {
                    //检查是不是表情
                    checkIsEmoticon(start);
                }
            }
        });
        conversationChatExpressionContainerEmoji.setEmotionSelectedListener(new IEmotionSelectedListener() {
            @Override
            public void onEmotionSelected(int position) {
                Emoticon emoticon = EmoticonUtil.getEmoticonList().get(position);
                int cursor = etConversationChatContentInput.getSelectionStart();
                String tag = emoticon.getTag();
                SpannableString spannableString = new SpannableString(tag);
                int resId = getResources().getIdentifier(emoticon.getId(), "drawable",
                        getPackageName());
                // 通过上面匹配得到的字符串来生成图片资源id
                if (resId != 0) {
                    Drawable drawable = getResources().getDrawable(resId);
                    drawable.setBounds(0, 0, SystemUtil.dp2px(getHostActivity(), 20),
                            SystemUtil.dp2px(getHostActivity(), 20));
                    // 通过图片资源id来得到bitmap，用一个ImageSpan来包装
                    ImageSpan imageSpan = new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM);
                    // 计算该图片名字的长度，也就是要替换的字符串的长度
                    // 将该图片替换字符串中规定的位置中
                    spannableString.setSpan(imageSpan, 0, tag.length(),
                            Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                    etConversationChatContentInput.getText().insert(cursor, spannableString);
                    emList.add(tag);
                }
            }
        });

        conversationChatExpressionContainerFeatures.setEmotionSelectedListener(new IEmotionSelectedListener() {
            @Override
            public void onEmotionSelected(int position) {
                switch (position) {
                    case 0:
                        ImgUtil.openCamera(getHostActivity());
                        break;
                    case 1:
                        Intent intent = new Intent(getHostActivity(), AlbumChoiceActivity.class);
                        intent.putExtra(AlbumCons.ALBUM_OPEN_KEY, -1);
                        startActivityForResult(intent, AlbumCons.REQUEST_ALBUM);
                        break;
                }

            }
        });
    }

    /**
     * 表情点击
     *
     * @param view
     */
    public void chatEmoticonClick(View view) {
        showEmoticonGv();
    }

    /**
     * 发送消息
     *
     * @param view
     */
    public void chatSendClick(View view) {
        if (etConversationChatContentInput.getText() != null && !TextUtils.isEmpty(etConversationChatContentInput.getText().toString())) {
            baseChatSendClick(etConversationChatContentInput.getText().toString());
            etConversationChatContentInput.setText("");
        }
    }

    /**
     * 返回
     *
     * @param view
     */
    public void chatBackClick(View view) {
        getHostActivity().finish();
    }

    /**
     * 更多
     *
     * @param view
     */
    public void chatMoreClick(View view) {
        if (conversationChatExpressionContainerFeatures.isShown()) {
            conversationChatExpressionContainerEmoji.setVisibility(View.GONE);
            conversationChatExpressionContainerFeatures.setVisibility(View.GONE);
            SystemUtil.hideKeyboard(getHostActivity(), etConversationChatContentInput);
        } else {

            //初始化
            if (mKeyboardHeight > 0) {
                ViewGroup.LayoutParams params = conversationChatExpressionContainerEmoji.getLayoutParams();
                params.height = mKeyboardHeight;
                ViewGroup.LayoutParams featuresLayoutParams = conversationChatExpressionContainerFeatures.getLayoutParams();
                featuresLayoutParams.height = mKeyboardHeight;
            }
            SystemUtil.hideKeyboard(getHostActivity(), etConversationChatContentInput);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
            conversationChatExpressionContainerEmoji.setVisibility(View.GONE);
            conversationChatExpressionContainerFeatures.setVisibility(View.VISIBLE);
            layoutManager.scrollToPositionWithOffset(rcvAdapter.getItemCount() - 1, 0);
        }

    }

    /**
     * 点击查看大图
     *
     * @param position
     */
    private void clickImage(int position) {
        if (mChatMsgList.get(position).getFileInfo() == null) {
            return;
        }
        List<ImgUrl> imgUrlList = new ArrayList<>();
        int imgClickPosition = 0;
        for (ChatMsg chatMsg : mChatMsgList) {
            if (chatMsg.getFileInfo() != null
                    && !TextUtils.isEmpty(chatMsg.getFileInfo().getDownloadPath())
                    && chatMsg.getFileInfo().getFileType() == FileInfo.TYPE_IMG) {

                ImgUrl imgUrl = new ImgUrl(chatMsg.getFileInfo().getDownloadPath());
                imgUrl.setFileName(chatMsg.getFileInfo().getFileName());
                imgUrlList.add(imgUrl);
                if (chatMsg.getMsgId().equals(mChatMsgList.get(position).getMsgId())) {
                    imgClickPosition = imgUrlList.indexOf(imgUrl);
                }
            }
        }
        Intent intent = new Intent(getHostActivity(), ImgShowActivity.class);
        intent.putExtra(AlbumCons.IMG_DONWLOAD, true);
        intent.putExtra(AlbumCons.IMG_CLICK_POSITION, imgClickPosition);
        intent.putExtra(AlbumCons.IMG_LIST_KEY, (Serializable) imgUrlList);
        startActivity(intent);
    }

    /**
     * 输入法点击
     *
     * @param view
     */
    public void chatInputClick(View view) {
        //延迟一段时间，等待输入法完全弹出
        krlMessageChatRootLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                //输入法弹出之后，重新调整
                layoutManager.scrollToPositionWithOffset(rcvAdapter.getItemCount() - 1, 0);
                conversationChatExpressionContainerEmoji.setVisibility(View.GONE);
                conversationChatExpressionContainerFeatures.setVisibility(View.GONE);
                ivConversationChatEmoticon.setImageResource(R.drawable.live_con_emoticon_03);
                isEmoticonActive = false;
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            }
        }, 100);
    }

    /**
     * 显示表情
     */
    private void showEmoticonGv() {
        if (isEmoticonActive) {
            SystemUtil.showKeyboard(getHostActivity(), etConversationChatContentInput);
            layoutManager.scrollToPositionWithOffset(rcvAdapter.getItemCount() - 1, 0);
            //延迟一段时间，等待输入法完全弹出
            krlMessageChatRootLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //输入法弹出之后，重新调整
                    layoutManager.scrollToPositionWithOffset(rcvAdapter.getItemCount() - 1, 0);
                    conversationChatExpressionContainerEmoji.setVisibility(View.GONE);
                    conversationChatExpressionContainerFeatures.setVisibility(View.GONE);
                    ivConversationChatEmoticon.setImageResource(R.drawable.live_con_emoticon_03);
                    isEmoticonActive = false;
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                }
            }, 100);

        } else {
            SystemUtil.hideKeyboard(getHostActivity(), etConversationChatContentInput);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
            conversationChatExpressionContainerEmoji.setVisibility(View.VISIBLE);
            conversationChatExpressionContainerFeatures.setVisibility(View.GONE);
            ivConversationChatEmoticon.setImageResource(R.drawable.live_con_character_04);
            isEmoticonActive = true;
            layoutManager.scrollToPositionWithOffset(rcvAdapter.getItemCount() - 1, 0);
            if (mKeyboardHeight > 0) {
                ViewGroup.LayoutParams params = conversationChatExpressionContainerEmoji.getLayoutParams();
                params.height = mKeyboardHeight;
                ViewGroup.LayoutParams featuresLayoutParams = conversationChatExpressionContainerFeatures.getLayoutParams();
                featuresLayoutParams.height = mKeyboardHeight;
            }
        }
    }


    /**
     * 添加消息
     *
     * @param chatMsgList
     * @param isLoad      是否为下拉刷新
     */
    protected void addAllMsgDate(List<ChatMsg> chatMsgList, boolean isLoad) {
        if (rcvAdapter == null) {
            return;
        }
        Collections.sort(chatMsgList, new ChatMsg.TimeRiseComparator());
        if (!isLoad) {
            mChatMsgList.clear();
            mChatMsgList.addAll(chatMsgList);
        } else {
            mChatMsgList.addAll(0, chatMsgList);
        }
        rcvAdapter.notifyDataSetChanged();
        if (isLoad) {
            if (rcvAdapter.getTopPullViewVisible()) {
                scrollToPositionWithOffset(chatMsgList.size() + 1, topLoadLastOffset);
            } else {
                scrollToPositionWithOffset(chatMsgList.size(), topLoadLastOffset);
            }
        } else {
            if (rcvAdapter.getTopPullViewVisible()) {
                scrollToPositionWithOffset(mChatMsgList.size(), 0);
            } else {
                scrollToPositionWithOffset(mChatMsgList.size() - 1, 0);
            }
        }
        if (chatMsgList.size() == NUMBER_LIMIT) {
            rcvAdapter.setTopPullViewVisible(true);
        } else {
            rcvAdapter.setTopPullViewVisible(false);
        }
    }

    /**
     * 添加一条消息
     */
    protected void addMsgDate(ChatMsg chatMsg) {
        if (rcvAdapter == null) {
            return;
        }
        mChatMsgList.add(chatMsg);
        rcvAdapter.notifyDataSetChanged();
        if (rcvConversationChatContainer.canScrollVertically(1) && SpCons.getUser(this).equals(chatMsg.getSenderId())) {
            scrollToPositionWithOffset(rcvAdapter.getItemCount() - 1, 0);
        } else if (!rcvConversationChatContainer.canScrollVertically(1)) {
            scrollToPositionWithOffset(rcvAdapter.getItemCount() - 1, 0);
        }
    }

    /**
     * 更新某一条数据
     * @param chatMsg
     */
    protected void updateMsg(final ChatMsg chatMsg){
        rcvConversationChatContainer.post(new Runnable() {
            @Override
            public void run() {
                boolean removeMark = false;
                //遍历删除,除去重复消息
                Iterator<ChatMsg> iterator = mChatMsgList.iterator();
                while (iterator.hasNext()) {
                    ChatMsg cm = iterator.next();
                    if (cm.getLocalMsgId().equals(chatMsg.getLocalMsgId())) {
                        iterator.remove();//使用迭代器的删除方法删除
                        removeMark = true;
                    }
                }
                mChatMsgList.add(chatMsg);
                if (removeMark) {
                    rcvAdapter.notifyDataSetChanged();
                } else {
                    //这里+1是由于适配器重写固定增加底部加载，所以获取到的少一个
                    rcvAdapter.notifyItemInserted(mChatMsgList.indexOf(chatMsg) + 1);
                }
                if (rcvConversationChatContainer.canScrollVertically(1) && SpCons.getUser(getHostActivity()).equals(chatMsg.getSenderId())) {
                    scrollToPositionWithOffset(rcvAdapter.getItemCount() - 1, 0);
                } else if (!rcvConversationChatContainer.canScrollVertically(1)) {
                    scrollToPositionWithOffset(rcvAdapter.getItemCount() - 1, 0);
                }
            }
        });
    }

    /**
     * 这里加上延时处理一遍，以防处理表情时，控件高度变化导致无法定位准确
     *
     * @param position
     * @param offset
     */
    private void scrollToPositionWithOffset(final int position, final int offset) {
        layoutManager.scrollToPositionWithOffset(position, offset);
    }

    /**
     * 检测是不是表情，并记录删除位置
     *
     * @param index
     */
    private void checkIsEmoticon(int index) {
        CharSequence temp = etConversationChatContentInput.getText();
        if (temp.charAt(index) != ']') {
            //检查是不是表情
            return;
        }
        if (index <= 0) {
            return;
        }
        String sub = etConversationChatContentInput.getText().toString().substring(0, index);
        //如果子串是空，返回
        if (sub.length() == 0) {
            return;
        }
        int atIndex = sub.lastIndexOf("face[");
        if (atIndex < 0) {
            return;
        }
        if (index - atIndex <= 1) {
            return;
        }
        String name = sub.substring(atIndex, index) + "]";
        for (String value : emList) {
            if (value.equals(name)) {
                delIndex = index;
                delLength = name.length() - 1;
                return;
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            int[] locationArr = new int[2];
            etConversationChatContentInput.getLocationInWindow(locationArr);
            if (ev.getY() < locationArr[1]) {
                conversationChatExpressionContainerEmoji.setVisibility(View.GONE);
                conversationChatExpressionContainerFeatures.setVisibility(View.GONE);
                ivConversationChatEmoticon.setImageResource(R.drawable.live_con_emoticon_03);
                isEmoticonActive = false;
                SystemUtil.hideKeyboard(getHostActivity(), etConversationChatContentInput);
            }
        }
        return super.dispatchTouchEvent(ev);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == AlbumCons.REQUEST_ALBUM) {
                List<String> dataList = data.getStringArrayListExtra(AlbumCons.IMG_SELECT_PATH_LIST);
                if (dataList == null || dataList.isEmpty()) {
                    return;
                }
                for (String path : dataList) {
                    compressImg(path);
                }
            } else if (requestCode == AlbumCons.REQUEST_CAMERA) {
                compressImg(ImgUtil.cameraOutputPath);
            }
        }
    }


}

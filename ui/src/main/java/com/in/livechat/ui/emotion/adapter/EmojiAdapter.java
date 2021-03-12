package com.in.livechat.ui.emotion.adapter;


import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.in.livechat.ui.emotion.util.EmoticonUtil;
import com.in.livechat.ui.common.util.ImgUtil;
import com.in.livechat.ui.common.util.SystemUtil;

import static com.in.livechat.ui.emotion.widget.EmotionLayout.EMOJI_COLUMNS;
import static com.in.livechat.ui.emotion.widget.EmotionLayout.EMOJI_PER_PAGE;
import static com.in.livechat.ui.emotion.widget.EmotionLayout.EMOJI_ROWS;

public class EmojiAdapter extends BaseAdapter {

    private Context mContext;
    private int mStartIndex;
    private int mEmotionLayoutWidth;
    private int mEmotionLayoutHeight;
    private float mPerWidth;
    private float mPerHeight;
    private float mIvSize;

    public EmojiAdapter(Context context, int emotionLayoutWidth, int emotionLayoutHeight, int startIndex) {
        mContext = context;
        mStartIndex = startIndex;
        mEmotionLayoutWidth = emotionLayoutWidth;
        mEmotionLayoutHeight = emotionLayoutHeight - SystemUtil.dp2px(mContext, 35 + 26 + 50);//减去底部的tab高度、小圆点的高度才是viewpager的高度，再减少30dp是让表情整体的顶部和底部有个外间距
        mPerWidth = mEmotionLayoutWidth * 1f / EMOJI_COLUMNS;
        mPerHeight = mEmotionLayoutHeight * 1f / EMOJI_ROWS;
        float ivWidth = mPerWidth * .6f;
        float ivHeight = mPerHeight * .6f;
        mIvSize = Math.min(ivWidth, ivHeight);
    }

    @Override
    public int getCount() {
        int count = EmoticonUtil.getEmoticonList().size() - mStartIndex ;
        count = Math.min(count, EMOJI_PER_PAGE );
        return count;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return mStartIndex + position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RelativeLayout rl = new RelativeLayout(mContext);
        rl.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, (int) mPerHeight));
        ImageView emojiThumb = new ImageView(mContext);
        int count = EmoticonUtil.getEmoticonList().size();
        int index = mStartIndex + position;
        if (index < count) {
            int resId = mContext.getResources().getIdentifier(EmoticonUtil.getEmoticonList().get(index).getId(),
                    "drawable", mContext.getPackageName());
            ImgUtil.loadEmoticon(mContext, resId, emojiThumb);
        }

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        if (mIvSize == 0) {
            mIvSize = getIvSize();
        }
        layoutParams.width = (int) mIvSize;
        layoutParams.height = (int) mIvSize;
        emojiThumb.setLayoutParams(layoutParams);

        rl.setGravity(Gravity.CENTER);
        rl.addView(emojiThumb);

        return rl;
    }

    public float getmPerHeight() {
        return mPerHeight;
    }

    private int getIvSize() {
        return (int) mIvSize;
    }
}

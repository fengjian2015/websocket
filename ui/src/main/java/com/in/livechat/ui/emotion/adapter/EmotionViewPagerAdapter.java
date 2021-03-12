package com.in.livechat.ui.emotion.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.RelativeLayout;

import com.in.livechat.ui.emotion.impl.IEmotionSelectedListener;
import com.in.livechat.ui.emotion.model.FeaturesModel;
import com.in.livechat.ui.emotion.util.EmoticonUtil;

import java.util.List;

import static com.in.livechat.ui.emotion.widget.EmotionLayout.EMOJI_COLUMNS;
import static com.in.livechat.ui.emotion.widget.EmotionLayout.EMOJI_PER_PAGE;
import static com.in.livechat.ui.emotion.widget.EmotionLayout.EMOJI_ROWS;
import static com.in.livechat.ui.emotion.widget.EmotionLayout.FEATURES_COLUMNS;
import static com.in.livechat.ui.emotion.widget.EmotionLayout.FEATURES_PER_PAGE;
import static com.in.livechat.ui.emotion.widget.EmotionLayout.FEATURES_ROWS;
import static com.in.livechat.ui.emotion.widget.EmotionLayout.TYPE_EMOJI;

/**
 * 表情控件的ViewPager适配器(emoji )
 */

public class EmotionViewPagerAdapter extends PagerAdapter {
    int mPageCount = 0;
    private IEmotionSelectedListener listener;
    private int mEmotionLayoutWidth;
    private int mEmotionLayoutHeight;
    private int mSelectType;
    private List<FeaturesModel> mFeaturesModelList;
    private FeaturesAdapter featuresAdapter;

    public EmotionViewPagerAdapter(int selectType, int emotionLayoutWidth, int emotionLayoutHeight, IEmotionSelectedListener listener, List<FeaturesModel> featuresModelList) {
        this.listener = listener;
        mSelectType = selectType;
        mEmotionLayoutWidth = emotionLayoutWidth;
        mEmotionLayoutHeight = emotionLayoutHeight;
        mFeaturesModelList = featuresModelList;
        if (selectType == TYPE_EMOJI) {
            mPageCount = (int) Math.ceil(EmoticonUtil.getEmoticonList().size() / (float) EMOJI_PER_PAGE);
        } else {
            mPageCount = (int) Math.ceil(mFeaturesModelList.size() / (float) FEATURES_PER_PAGE);
        }
    }

    @Override
    public int getCount() {
        return mPageCount == 0 ? 1 : mPageCount;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Context context = container.getContext();
        RelativeLayout rl = new RelativeLayout(context);
        rl.setGravity(Gravity.CENTER);
        GridView gridView = new GridView(context);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        if (mSelectType == TYPE_EMOJI) {
            EmojiAdapter emojiAdapter = new EmojiAdapter(context, mEmotionLayoutWidth, mEmotionLayoutHeight, position * EMOJI_PER_PAGE);
            gridView.setAdapter(emojiAdapter);
            params.height = (int) (emojiAdapter.getmPerHeight() * EMOJI_ROWS);
            gridView.setNumColumns(EMOJI_COLUMNS);
        } else {
            featuresAdapter = new FeaturesAdapter(context, mEmotionLayoutWidth, mEmotionLayoutHeight, position * FEATURES_PER_PAGE, mFeaturesModelList);
            gridView.setAdapter(featuresAdapter);
            params.height = (int) (featuresAdapter.getmPerHeight() * FEATURES_ROWS);
            gridView.setNumColumns(FEATURES_COLUMNS);
        }
        gridView.setLayoutParams(params);
        gridView.setGravity(Gravity.CENTER);
        gridView.setTag(position);//标记自己是第几页
        gridView.setOnItemClickListener(emojiListener);
        rl.addView(gridView);
        container.addView(rl);
        return rl;

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    public AdapterView.OnItemClickListener emojiListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            int index;
            if (mSelectType == TYPE_EMOJI) {
                index = position + (Integer) parent.getTag() * EMOJI_PER_PAGE;
            } else {
                index = position + (Integer) parent.getTag() * FEATURES_PER_PAGE;
            }
            int count = EmoticonUtil.getEmoticonList().size();
            if (listener != null) {
                if (index <= count) {
                    listener.onEmotionSelected(index);
                }
            }
        }
    };

    public void featuresNotifyDataSetChanged() {
        featuresAdapter.notifyDataSetChanged();
    }
}

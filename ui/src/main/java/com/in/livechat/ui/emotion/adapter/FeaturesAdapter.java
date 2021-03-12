package com.in.livechat.ui.emotion.adapter;


import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.in.livechat.ui.emotion.model.FeaturesModel;
import com.in.livechat.ui.common.util.ImgUtil;
import com.in.livechat.ui.common.util.SystemUtil;

import java.util.List;

import static com.in.livechat.ui.emotion.widget.EmotionLayout.FEATURES_COLUMNS;
import static com.in.livechat.ui.emotion.widget.EmotionLayout.FEATURES_PER_PAGE;
import static com.in.livechat.ui.emotion.widget.EmotionLayout.FEATURES_ROWS;

public class FeaturesAdapter extends BaseAdapter {

    private Context mContext;
    private int mStartIndex;
    private int mEmotionLayoutWidth;
    private int mEmotionLayoutHeight;
    private float mPerWidth;
    private float mPerHeight;
    private float mIvSize;
    private List<FeaturesModel> mFeaturesModelList;

    public FeaturesAdapter(Context context, int emotionLayoutWidth, int emotionLayoutHeight, int startIndex, List<FeaturesModel> featuresModelList) {
        mContext = context;
        mStartIndex = startIndex;
        mFeaturesModelList = featuresModelList;
        mEmotionLayoutWidth = emotionLayoutWidth;
        mEmotionLayoutHeight = emotionLayoutHeight - SystemUtil.dp2px(mContext, 35 + 26 + 50);//减去底部的tab高度、小圆点的高度才是viewpager的高度，再减少30dp是让表情整体的顶部和底部有个外间距
        mPerWidth = mEmotionLayoutWidth * 1f / FEATURES_COLUMNS;
        mPerHeight = mEmotionLayoutHeight * 1f / FEATURES_ROWS;
        float ivWidth = mPerWidth * .6f;
        float ivHeight = mPerHeight * .6f;
        mIvSize = Math.min(ivWidth, ivHeight);
    }

    private int getListSize() {
        return mFeaturesModelList.size();
    }

    @Override
    public int getCount() {
        int count = getListSize() - mStartIndex;
        count = Math.min(count, FEATURES_PER_PAGE);
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
        LinearLayout rl = new LinearLayout(mContext);
        rl.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, (int) mPerHeight));
        rl.setOrientation(LinearLayout.VERTICAL);
        TextView textView = new TextView(mContext);
        textView.setTextColor(Color.BLACK);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);

        ImageView emojiThumb = new ImageView(mContext);
        int count = getListSize();
        int index = mStartIndex + position;
        if (index < count) {
            String url = mFeaturesModelList.get(index).getUrl();
            if (TextUtils.isEmpty(url)) {
                int resId = mFeaturesModelList.get(index).getResourcesId();
                ImgUtil.load(mContext, resId, emojiThumb);
            } else {
                ImgUtil.load(mContext, url, emojiThumb);
            }
            textView.setText(mFeaturesModelList.get(index).getName());
        }


        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);

        if (mIvSize == 0) {
            mIvSize = getIvSize();
        }
        layoutParams.width = (int) mIvSize;
        layoutParams.height = (int) mIvSize;
        emojiThumb.setLayoutParams(layoutParams);

        RelativeLayout.LayoutParams textLayout = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        textLayout.topMargin = SystemUtil.dp2px(mContext, 5);
        textLayout.addRule(RelativeLayout.CENTER_HORIZONTAL);
        textView.setLayoutParams(textLayout);
        rl.setGravity(Gravity.CENTER);
        rl.addView(emojiThumb);
        rl.addView(textView);
        return rl;
    }

    public float getmPerHeight() {
        return mPerHeight;
    }

    private int getIvSize() {
        return (int) mIvSize;
    }
}

package com.in.livechat.ui.emotion.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.in.livechat.ui.R;
import com.in.livechat.ui.emotion.adapter.EmotionViewPagerAdapter;
import com.in.livechat.ui.emotion.impl.IEmotionSelectedListener;
import com.in.livechat.ui.emotion.model.FeaturesModel;
import com.in.livechat.ui.emotion.util.EmoticonUtil;
import com.in.livechat.ui.common.util.SystemUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * 表情布局/功能布局
 */
public class EmotionLayout extends LinearLayout {
    public static final int EMOJI_COLUMNS = 7;
    public static final int EMOJI_ROWS = 3;
    public static final int EMOJI_PER_PAGE = EMOJI_COLUMNS * EMOJI_ROWS;

    public static final int FEATURES_COLUMNS = 4;
    public static final int FEATURES_ROWS = 2;
    public static final int FEATURES_PER_PAGE = FEATURES_COLUMNS * FEATURES_ROWS;
    private List<FeaturesModel> mFeaturesModelList = new ArrayList<>();

    public static final int TYPE_EMOJI = 1;
    public static final int TYPE_FEATURES = 2;

    private int mMeasuredWidth;
    private int mMeasuredHeight;

    private Context mContext;
    private ViewPager mVpEmotioin;
    private LinearLayout mLlPageNumber;
    private int selectType = TYPE_EMOJI;
    private EmotionViewPagerAdapter adapter;


    private IEmotionSelectedListener mEmotionSelectedListener;

    public EmotionLayout(Context context) {
        this(context, null);
    }

    public EmotionLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EmotionLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.EmotionView);
        selectType = a.getInt(R.styleable.EmotionView_type, TYPE_EMOJI);
        a.recycle();
    }

    public void setEmotionSelectedListener(IEmotionSelectedListener emotionSelectedListener) {
        if (emotionSelectedListener != null) {
            this.mEmotionSelectedListener = emotionSelectedListener;
        } else {
            Log.i("CSDN_LQR", "IEmotionSelectedListener is null");
        }
    }

    public void setFeaturesModelList(List featuresModelList) {
        mFeaturesModelList.clear();
        mFeaturesModelList.addAll(featuresModelList);
        if (adapter == null) return;
        adapter.featuresNotifyDataSetChanged();
        setCurPageCommon(0);
    }

    public List<FeaturesModel> getFeaturesModelList() {
        return mFeaturesModelList;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        init();
        initListener();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mMeasuredWidth = measureWidth(widthMeasureSpec);
        mMeasuredHeight = measureHeight(heightMeasureSpec);
        setMeasuredDimension(mMeasuredWidth, mMeasuredHeight);
    }


    private int measureWidth(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = SystemUtil.dp2px(mContext, 200);
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    private int measureHeight(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = SystemUtil.dp2px(mContext, 200);
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    private void init() {
        if (selectType == TYPE_FEATURES){
            // TODO: 2021/3/11 模拟数据
            FeaturesModel featuresModel=new FeaturesModel();
            featuresModel.setName(getResources().getString(R.string.live_photo_camera));
            featuresModel.setResourcesId(R.drawable.live_img_avatar_default);
            FeaturesModel featuresModel1=new FeaturesModel();
            featuresModel1.setName(getResources().getString(R.string.live_photo_album));
            featuresModel1.setResourcesId(R.drawable.live_img_avatar_default);
            mFeaturesModelList.add(featuresModel);
            mFeaturesModelList.add(featuresModel1);
        }
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.live_emotion_layout, this);
        mVpEmotioin = (ViewPager) findViewById(R.id.vpEmotioin);
        mLlPageNumber = (LinearLayout) findViewById(R.id.llPageNumber);
        fillVpEmotion();
    }

    private void initListener() {
        mVpEmotioin.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                setCurPageCommon(position);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void setCurPageCommon(int position) {
        if (selectType == TYPE_EMOJI) {
            setCurPage(position, (int) Math.ceil(EmoticonUtil.getEmoticonList().size() / (float) EmotionLayout.EMOJI_PER_PAGE));
        } else {
            setCurPage(position, (int) Math.ceil(mFeaturesModelList.size() / (float) EmotionLayout.FEATURES_PER_PAGE));
        }
    }

    private void fillVpEmotion() {
        adapter = new EmotionViewPagerAdapter(selectType, mMeasuredWidth, mMeasuredHeight, mEmotionSelectedListener, mFeaturesModelList);
        mVpEmotioin.setAdapter(adapter);
        mLlPageNumber.removeAllViews();
        setCurPageCommon(0);
    }


    private void setCurPage(int page, int pageCount) {
        int hasCount = mLlPageNumber.getChildCount();
        int forMax = Math.max(hasCount, pageCount);
        if (pageCount == 1) return;
        ImageView ivCur = null;
        for (int i = 0; i < forMax; i++) {
            if (pageCount <= hasCount) {
                if (i >= pageCount) {
                    mLlPageNumber.getChildAt(i).setVisibility(View.GONE);
                    continue;
                } else {
                    ivCur = (ImageView) mLlPageNumber.getChildAt(i);
                }
            } else {
                if (i < hasCount) {
                    ivCur = (ImageView) mLlPageNumber.getChildAt(i);
                } else {
                    ivCur = new ImageView(mContext);
                    ivCur.setBackgroundResource(R.drawable.live_selector_view_pager_indicator);
                    LayoutParams params = new LayoutParams(SystemUtil.dp2px(mContext, 8), SystemUtil.dp2px(mContext, 8));
                    ivCur.setLayoutParams(params);
                    params.leftMargin = SystemUtil.dp2px(mContext, 3);
                    params.rightMargin = SystemUtil.dp2px(mContext, 3);
                    mLlPageNumber.addView(ivCur);
                }
            }

            ivCur.setId(i);
            ivCur.setSelected(i == page);
            ivCur.setVisibility(View.VISIBLE);
        }
    }
}

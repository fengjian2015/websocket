package com.in.livechat.ui.chat.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.in.livechat.ui.common.util.SpCons;

/**
 * Created by Darren on 2019/1/28
 */
public class KeyboardRelativeLayout extends RelativeLayout {
    private KeyboardOnGlobalChangeListener keyboardOnGlobalChangeListener;
    private OnKeyboardLayoutListener mListener;
    //输入法是否激活
    private boolean mIsKeyboardActive = false;
    //输入法高度
    private int mKeyboardHeight = 0;
    //记录原始窗口
    private int mWindowHeight = 0;
    private int mWindowWidth = 0;

    public KeyboardRelativeLayout(Context context) {
        this(context, null, 0);
    }

    public KeyboardRelativeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public KeyboardRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // 监听布局变化
        keyboardOnGlobalChangeListener = new KeyboardOnGlobalChangeListener();
    }

    public interface OnKeyboardLayoutListener {
        void keyboardStateChanged(boolean isKeyboardActive, int keyboardHeight);
    }

    public void setOnKeyboardLayoutListener(OnKeyboardLayoutListener listener) {
        mListener = listener;
        getViewTreeObserver().addOnGlobalLayoutListener(keyboardOnGlobalChangeListener);
    }

    public void unKeyboardLayoutListener() {
        mListener = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            getViewTreeObserver().removeOnGlobalLayoutListener(keyboardOnGlobalChangeListener);
        }
    }

    public OnKeyboardLayoutListener getKeyboardListener() {
        return mListener;
    }

    public int getmKeyboardHeight() {
        return mKeyboardHeight;
    }

    public int getWindowWidth() {
        return mWindowWidth;
    }

    private class KeyboardOnGlobalChangeListener implements ViewTreeObserver.OnGlobalLayoutListener {

        int mScreenHeight = 0;

        private int getScreenHeight() {
            if (mScreenHeight > 0) {
                return mScreenHeight;
            }
            mScreenHeight = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE))
                    .getDefaultDisplay().getHeight();
            return mScreenHeight;
        }

        @Override
        public void onGlobalLayout() {
            Rect r = new Rect();
            //获取当前窗口实际的可见区域
            ((Activity) getContext()).getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
            int height = r.height();
            int width = r.width();
            if (mWindowHeight == 0) {
                //一般情况下，这是原始的窗口高度
                mWindowHeight = height;
                mWindowWidth = width;
                SpCons.setWindowWidth(getContext(), mWindowWidth);
            } else {
                boolean isActive;
                if (mWindowHeight != height) {
                    isActive = true;
                    //两次窗口高度相减，就是软键盘高度
                    mKeyboardHeight = mWindowHeight - height;
                    System.out.println("SoftKeyboard height = " + mKeyboardHeight);
                    SpCons.setKeyboardHeight(getContext(), mKeyboardHeight);
                    if (mListener != null) {
                        mListener.keyboardStateChanged(isActive, mKeyboardHeight);
                    }
                }
            }
        }
    }


}

package com.in.livechat.ui.common.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.in.livechat.ui.common.util.SystemUtil;

import java.lang.reflect.Field;

/**
 * description ：
 * author : Fickle
 * date : 2021/3/10 12:30
 */
public abstract class BaseChatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        initView();
        init();
        setListener();
    }

    protected abstract void initView();
    protected abstract void init();
    protected abstract void setListener();

    protected abstract int getLayoutId();

    protected Activity getHostActivity() {
        return this;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //点击空白位置 隐藏软键盘
        SystemUtil.hideKeyboard(getHostActivity());
        return super.onTouchEvent(event);
    }



    @Override
    protected void onPause() {
        super.onPause();
        //隐藏软键盘
        SystemUtil.hideKeyboard(getHostActivity());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        fixInputMethod(this);
    }

    /**
     * 解决输入法导致的内存泄漏
     */
    public static void fixInputMethod(Context context) {
        if (context == null) {
            return;
        }
        InputMethodManager inputMethodManager = null;
        try {
            inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        } catch (Throwable th) {
            th.printStackTrace();
        }
        if (inputMethodManager == null) {
            return;
        }
        Field[] declaredFields = inputMethodManager.getClass().getDeclaredFields();
        for (Field declaredField : declaredFields) {
            try {
                if (!declaredField.isAccessible()) {
                    declaredField.setAccessible(true);
                }
                Object obj = declaredField.get(inputMethodManager);
                if (!(obj instanceof View)) {
                    continue;
                }
                View view = (View) obj;
                if (view.getContext() == context) {
                    declaredField.set(inputMethodManager, null);
                } else {
                    return;
                }
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }
    }
}

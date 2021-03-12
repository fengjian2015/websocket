package com.in.livechat.ui.common.callback;

import com.in.livechat.socket.util.LogUtil;

import java.io.File;

import top.zibin.luban.OnCompressListener;

public class LubanCallBack implements OnCompressListener {


    @Override
    public void onStart() {

    }

    @Override
    public void onSuccess(File outputFile) {

    }

    @Override
    public void onError(Throwable e) {
        LogUtil.i(e.getMessage());
        e.printStackTrace();
    }
}

package com.in.livechat.ui.album.permission;

import java.util.HashMap;

/**
 * 回调方法
 */
public interface PermissionCallback {
    //确认回调
    void onGranted();
    //取消回调
    void onDenied(HashMap<String, Boolean> permissions);


}

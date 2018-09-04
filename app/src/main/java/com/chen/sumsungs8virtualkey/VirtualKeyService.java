package com.chen.sumsungs8virtualkey;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.TargetApi;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.widget.Toast;

import java.util.Iterator;
import java.util.List;

public class VirtualKeyService extends AccessibilityService {


    private static VirtualKeyService mVirtualService;

    private static final String PACKAGENAME = "com.tencent.mm";

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        mVirtualService = this;
        //发送广播，已经连接上了
        Toast.makeText(this, "成功开启辅助功能权限", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

        Log.i("VirtualKeyService", "shoudao");

    }

    @Override
    public void onInterrupt() {
        Toast.makeText(this, "断开辅助功能", Toast.LENGTH_SHORT).show();

    }


    /**
     * 判断当前服务是否正在运行
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static boolean isRunning() {
        if (mVirtualService == null) {
            return false;
        }
        AccessibilityManager accessibilityManager = (AccessibilityManager) mVirtualService.getSystemService(Context.ACCESSIBILITY_SERVICE);
        AccessibilityServiceInfo info = mVirtualService.getServiceInfo();
        if (info == null) {
            return false;
        }
        List<AccessibilityServiceInfo> list = accessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC);
        Iterator<AccessibilityServiceInfo> iterator = list.iterator();

        boolean isConnect = false;
        while (iterator.hasNext()) {
            AccessibilityServiceInfo i = iterator.next();
            if (i.getId().equals(info.getId())) {
                isConnect = true;
                break;
            }
        }

        if (!isConnect) {
            return false;
        }
        return true;
    }


    public static VirtualKeyService getService() {
        return mVirtualService;
    }

    public boolean clickBackKey() {
        try {
            return performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean clickRecentKey() {
        try {
            return performGlobalAction(AccessibilityService.GLOBAL_ACTION_RECENTS);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean clickHomeKey() {
        try {
            return performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME);
        } catch (Exception e) {
            return false;
        }
    }
}

package com.chen.sumsungs8virtualkey;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;

import com.chen.sumsungs8virtualkey.utils.LogUtils;

import java.util.List;

/**
 * Created by CHEN on 2018/1/9.
 */

public class CUtils {
    // 判断是否打开了通知监听权限
    public static boolean isEnabled(Context context) {
        String pkgName = context.getPackageName();
        final String flat = Settings.Secure.getString(context.getContentResolver(), "enabled_notification_listeners");
        if (!TextUtils.isEmpty(flat)) {
            final String[] names = flat.split(":");
            for (int i = 0; i < names.length; i++) {
                final ComponentName cn = ComponentName.unflattenFromString(names[i]);
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.getPackageName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    public static boolean isServiceRunning(Context mContext, String className) {

        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager
                .getRunningServices(30);

        if (!(serviceList.size() > 0)) {
            return false;
        }

        for (int i = 0; i < serviceList.size(); i++) {

            LogUtils.e(serviceList.get(i).service.getClassName());

            if (serviceList.get(i).service.getClassName().equals(className)) {

                isRunning = true;
                break;
            }
        }
        return isRunning;

    }

}

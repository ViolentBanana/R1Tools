package com.chen.sumsungs8virtualkey.service

import android.annotation.TargetApi
import android.app.Notification
import android.os.Build
import android.os.Bundle
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log

/**
 * Created by CHEN on 2018/1/9.
 */

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
class NotificationMonitorService : NotificationListenerService() {
    // 在收到消息时触发
    @TargetApi(Build.VERSION_CODES.KITKAT)
    override fun onNotificationPosted(sbn: StatusBarNotification) {
        // TODO Auto-generated method stub
        //        Bundle extras = sbn.getNotification().extras;
        //        // 获取接收消息APP的包名
        //        String notificationPkg = sbn.getPackageName();
        //        // 获取接收消息的抬头
        //        String notificationTitle = extras.getString(Notification.EXTRA_TITLE);
        //        // 获取接收消息的内容
        //        String notificationText = extras.getString(Notification.EXTRA_TEXT);
        //        Log.i("XSL_Test", "Notification posted " + notificationTitle + " & " + notificationText);
    }

    // 在删除消息时触发
    @TargetApi(Build.VERSION_CODES.KITKAT)
    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        // TODO Auto-generated method stub
        //        Bundle extras = sbn.getNotification().extras;
        //        // 获取接收消息APP的包名
        //        String notificationPkg = sbn.getPackageName();
        //        // 获取接收消息的抬头
        //        String notificationTitle = extras.getString(Notification.EXTRA_TITLE);
        //        // 获取接收消息的内容
        //        String notificationText = extras.getString(Notification.EXTRA_TEXT);
        //        Log.i("XSL_Test", "Notification removed " + notificationTitle + " & " + notificationText);
    }

}

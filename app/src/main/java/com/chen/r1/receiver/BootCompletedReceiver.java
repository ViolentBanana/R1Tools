package com.chen.r1.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.chen.r1.service.VirtualKeyService;
import com.chen.r1.utils.LogUtils;

/**
 * Create by CHEN ON 2019/4/8
 */
public class BootCompletedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            //example:启动程序
            LogUtils.INSTANCE.e("开机广播！！");
//            VirtualKeyService.Companion.getService().createView();
//            VirtualKeyService.Companion.getService().createRightFloatView();
        }
    }
}
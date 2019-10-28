package com.chen.r1.acessbility;

import android.view.accessibility.AccessibilityEvent;

import com.chen.r1.service.VirtualKeyService;
import com.chen.r1.service.VirtualKeyService;

/**
 * Created by CHEN on 2016/12/19.
 */
public interface AccessbilityJob {

    //根据包名区分处理事件
    String getTargetPackageName();

    //连接到服务
    void onCreateJob(VirtualKeyService service);

    //接收到更新
    void onReceiveJob(AccessibilityEvent event);

    //服务断开连接
    void onStopJob();

    //此服务是否可用
    boolean isEnable();
}

package com.chen.r1.acessbility;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.widget.Toast;

import com.chen.r1.utils.LogUtils;

import java.util.Iterator;
import java.util.List;

/**
 * Created by CHEN on 2016/12/19.
 */
public class BaseAccessibilityService extends AccessibilityService {


    private static final String WECHAT_PACKAGENAME = "com.tencent.mm";

    //检测包名
    private String[] PACKAGES = {"com.tencent.mm", "com.qc.grabmoney",
            "com.android.packageinstaller", "com.lenovo.security",
            "com.samsung.android.packageinstaller", "com.miui.securitycenter"};
    //回调处理不同的辅助功能类
    private static final Class[] ACCESSBILITY_JOBS = {
//            LenovoPhoneAccessibility.class, AutoAttentWechatAccessbility.class,
// XiaomiAccessibility.class
    };

    private static BaseAccessibilityService service;


    private List<AccessbilityJob> mAccessbilityJobs;


    private PackageInfo mWechatPackageInfo = null;


    @Override
    public void onCreate() {
        super.onCreate();

    }



    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        service = this;
        //发送广播，已经连接上了
        Toast.makeText(this, "已连接智能管家服务", Toast.LENGTH_SHORT).show();

    }


    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

    }

    @Override
    public void onInterrupt() {
        LogUtils.INSTANCE.d("grabmoney service interrupt");
        Toast.makeText(this, "中断抢红包服务", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.INSTANCE.d("grabmoney service destory");
        if (mAccessbilityJobs != null && !mAccessbilityJobs.isEmpty()) {
            for (AccessbilityJob job : mAccessbilityJobs) {
                job.onStopJob();
            }
            mAccessbilityJobs.clear();
        }
        service = null;
        mAccessbilityJobs = null;

    }


    /**
     * 判断当前服务是否正在运行
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static boolean isRunning() {
        if (service == null) {
            return false;
        }
        AccessibilityManager accessibilityManager = (AccessibilityManager) service.getSystemService(Context.ACCESSIBILITY_SERVICE);
        AccessibilityServiceInfo info = service.getServiceInfo();
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
}

package com.chen.sumsungs8virtualkey;

import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chen.sumsungs8virtualkey.utils.LogUtils;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int REQUEST_OVERLAY = 100;

    private static final String TAG = "MainActivityTouch";

    private TextView mAccessStatus;
    private Button mAccessBtn;

    private TextView mNotificationStatus;
    private Button mNotificationBtn;

    private TextView mWindowsStatus;
    private Button mWindowsBtn;

    private TextView mAutoStartStatus;
    private Button mAutoStartBtn;


    private TextView mBgColorCommit;
    private TextView mColor_transparent;
    private TextView mBackViewStatus;


    private Button mBackViewCreate, mBackViewDestory;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initPermission();
        initView();
    }

    private void initView() {


        mWindowsBtn = findViewById(R.id.window_open);
        mWindowsBtn.setOnClickListener(this);
        mWindowsStatus = findViewById(R.id.window_open_status);

        mNotificationBtn = findViewById(R.id.notification_btn);
        mNotificationBtn.setOnClickListener(this);
        mNotificationStatus = findViewById(R.id.notification_status);

        mAccessBtn = findViewById(R.id.access_open);
        mAccessBtn.setOnClickListener(this);
        mAccessStatus = findViewById(R.id.access_status);


        mAutoStartBtn = findViewById(R.id.auto_start_btn);
        mAutoStartBtn.setOnClickListener(this);
        mAutoStartStatus = findViewById(R.id.auto_start_status);

        mBgColorCommit = findViewById(R.id.color_bg_commit);
        mBgColorCommit.setOnClickListener(this);

        mColor_transparent = findViewById(R.id.color_transparent);
        mColor_transparent.setOnClickListener(this);

        mBackViewStatus = findViewById(R.id.color_bg_status);


        mBackViewCreate = findViewById(R.id.color_bg_create);
        mBackViewCreate.setOnClickListener(this);
        mBackViewDestory = findViewById(R.id.color_bg_destory);
        mBackViewDestory.setOnClickListener(this);

    }

    public void openAppDetail() {
        Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
        String pkg = "com.android.settings";
        String cls = "com.android.settings.applications.InstalledAppDetails";
        intent.setComponent(new ComponentName(pkg, cls));
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {


            case R.id.window_open:
                initPermission();
                break;

            case R.id.access_open:
                openAccessibilityServiceSettings();
                break;

            case R.id.color_bg_create:
                if (VirtualKeyService.isRunning())
                    VirtualKeyService.getService().createFloatView();
                else
                    Toast.makeText(getApplicationContext(), R.string.please_try_again, Toast.LENGTH_SHORT).show();
                checkAllpermiss();
                break;
            case R.id.color_bg_destory:
                if (VirtualKeyService.isRunning())
                    VirtualKeyService.getService().destoryFlowView();
                else
                    Toast.makeText(getApplicationContext(), R.string.please_try_again, Toast.LENGTH_SHORT).show();
                checkAllpermiss();
                break;


            case R.id.color_bg_commit:
                //设置灰色
                if (VirtualKeyService.isRunning())
                    VirtualKeyService.getService().setBgGray();
                else
                    Toast.makeText(getApplicationContext(), R.string.please_try_again, Toast.LENGTH_SHORT).show();
                break;

            case R.id.color_transparent:
                //设置透明
                if (VirtualKeyService.isRunning())
                    VirtualKeyService.getService().setBgTran();
                else
                    Toast.makeText(getApplicationContext(), R.string.please_try_again, Toast.LENGTH_SHORT).show();
                break;


            case R.id.notification_btn:
                //获取通知权限
                startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
                break;


            case R.id.auto_start_btn:
                //自动启动
                openAppDetail();
                break;
        }
    }


    /**
     * 获取窗口显示权限
     */
    private void initPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(getApplicationContext())) {
                //启动Activity让用户授权
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, REQUEST_OVERLAY);
            } else {
//                createFloatView();
            }
        } else {
//            createFloatView();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_OVERLAY) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(this)) {
                    if (VirtualKeyService.isRunning())
                        VirtualKeyService.getService().createFloatView();
                    else
                        Toast.makeText(getApplicationContext(), R.string.please_try_again, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "ACTION_MANAGE_OVERLAY_PERMISSION权限已被拒绝", Toast
                            .LENGTH_SHORT).show();
                }
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        checkAllpermiss();

        LogUtils.e("是否运行中：" + VirtualKeyService.isRunning());


    }


    public void checkAllpermiss() {


        //辅助功能
        if (VirtualKeyService.isRunning()) {
            mAccessStatus.setText(R.string.yes);


            if (VirtualKeyService.getService().getmFloatView() == null) {
                mBackViewStatus.setText(R.string.no);
            } else {
                mBackViewStatus.setText(R.string.yes);
            }

            //通知权限
            if (CUtils.isEnabled(getApplicationContext())) {
                mNotificationStatus.setText(R.string.yes);
            } else {
                mNotificationStatus.setText(R.string.no);
            }

            //窗口显示权限
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(getApplicationContext())) {
                    mWindowsStatus.setText(R.string.yes);
                } else {
                    mWindowsStatus.setText(R.string.no);
                }
            } else {
                mWindowsStatus.setText(R.string.yes);
            }

        } else {
            mAccessStatus.setText(R.string.no);
            if (VirtualKeyService.getService() != null)
                if (VirtualKeyService.getService().getmFloatView() != null)
                    VirtualKeyService.getService().destoryFlowView();
        }
    }

    /**
     * 打开辅助服务的设置
     */
    private void openAccessibilityServiceSettings() {
        try {
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivity(intent);
            Toast.makeText(this, "请开启s8手势返回服务", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

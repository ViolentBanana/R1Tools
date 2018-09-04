package com.chen.sumsungs8virtualkey;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnTouchListener,
        IHandleMessage, View.OnClickListener {

    public static final int REQUEST_OVERLAY = 100;

    private static final String TAG = "MainActivityTouch";

    private static final String HALF_BLACK = "#55000000";
    private static final String TRANSLATE = "#00000000";

    //定义浮动窗口布局
    private RelativeLayout mFloatLayout;
    private WindowManager.LayoutParams wmParams;
    //创建浮动窗口设置布局参数的对象
    private WindowManager mWindowManager;

    private TextView mFloatView;

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

        mHander = new WeakRefHandler<>(this);

        isServiceWork(getApplicationContext(), "com.chen.sumsungs8virtualkey.VirtualKeyService");

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
                createFloatView();
                checkAllpermiss();
                break;
            case R.id.color_bg_destory:
                destoryFlowView();
                checkAllpermiss();
                break;


            case R.id.color_bg_commit:
                //设置灰色
                if (mFloatView != null)
                    mFloatView.setBackgroundColor(Color.parseColor(HALF_BLACK));
                else
                    Toast.makeText(getApplicationContext(), getString(R.string.get_permiss_first)
                            , Toast.LENGTH_SHORT)
                            .show();
                break;

            case R.id.color_transparent:
                //设置透明
                if (mFloatView != null)
                    mFloatView.setBackgroundColor(Color.parseColor(TRANSLATE));
                else
                    Toast.makeText(getApplicationContext(), getString(R.string.get_permiss_first)
                            , Toast.LENGTH_SHORT)
                            .show();
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
                createFloatView();
            }
        } else {
            createFloatView();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_OVERLAY) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(this)) {
                    createFloatView();
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

    }


    public void checkAllpermiss() {

        if (mFloatView == null) {
            mBackViewStatus.setText(R.string.no);
        } else {
            mBackViewStatus.setText(R.string.yes);
        }
        //辅助功能
        if (VirtualKeyService.isRunning()) {
            mAccessStatus.setText(R.string.yes);
        } else {
            mAccessStatus.setText(R.string.no);
        }

        //通知权限
        if (CUtils.isEnabled(getApplicationContext())) {
            mNotificationStatus.setText(R.string.yes);
        } else {
            mNotificationStatus.setText(R.string.no);
        }

        //窗口显示权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays
                (getApplicationContext())) {
            mWindowsStatus.setText(R.string.yes);
            createFloatView();
        } else {
            mWindowsStatus.setText(R.string.no);
        }

    }


    private void destoryFlowView() {
        if (mFloatLayout != null) {
            //移除悬浮窗口
            mWindowManager.removeView(mFloatLayout);
            mFloatLayout = null;
        }
    }


    /**
     * 打开辅助服务的设置
     */
    private void openAccessibilityServiceSettings() {
        try {
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivity(intent);
            Toast.makeText(this, "开启", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    boolean isClick;
    private boolean mIsMove;
    private boolean mTouchTimeOut = false;


    private long mDownTime;
    private long mMoveTime;

    private int mDownX;
    private int mMoveX;


    private WeakRefHandler mHander;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                Log.e(TAG, "==========START================");
                mIsMove = true;
//                        mFloatView.setBackgroundResource(R.drawable.circle_red);
                mDownTime = System.currentTimeMillis() / 1000;
                mDownX = (int) event.getRawX()
                        - mFloatView.getMeasuredWidth() / 2;
                isClick = false;


                if (!mTouchTimeOut)
                    mHander.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (mIsMove)
                                mTouchTimeOut = true;
                        }
                    }, 500);

                break;
            case MotionEvent.ACTION_MOVE:
                // getRawX是触摸位置相对于屏幕的坐标，getX是相对于按钮的坐标
                wmParams.x = (int) event.getRawX()
                        - mFloatView.getMeasuredWidth() / 2;
                // 减25为状态栏的高度
                wmParams.y = (int) event.getRawY()
                        - mFloatView.getMeasuredHeight() / 2 - 75;


                mMoveX = wmParams.x;
                // 刷新
//                Log.e("touch:", "X:" + wmParams.x + "/Y:" + wmParams.y);

                boolean isMoveEnough = (mMoveX - mDownX) > 40;

                Log.e("isMoveEnough:", "DownX:" + mDownX + "moveX:" + mMoveX);

                if (mIsMove && isMoveEnough && !mTouchTimeOut) {
                    isClick = true;
                    mIsMove = false;
                    VirtualKeyService.getService().clickBackKey();
                    return true;
                } else {
                    Log.e(TAG, "mIsMove" + mIsMove + "/isMoveEnough" + isMoveEnough +
                            "/mTouchTimeOut" + mTouchTimeOut);
                    return false;
                }

            case MotionEvent.ACTION_UP:
                mMoveX = 0;
                mDownX = 0;
                mTouchTimeOut = false;
                Log.e(TAG, "==========STOP================");

                mIsMove = false;
//                        mFloatView.setBackgroundResource(R.drawable.circle_cyan);
                return isClick;// 此处返回false则属于移动事件，返回true则释放事件，可以出发点击否。

            default:
                break;
        }
        return false;
    }

    @Override
    public void onHandleMessage(Message msg) {
        mTouchTimeOut = true;
        Log.e(TAG, "============超时================");
    }

    @SuppressLint("ClickableViewAccessibility")
    private void createFloatView() {

        if (!VirtualKeyService.isRunning())
            return;

        if (mFloatLayout == null) {

            wmParams = new WindowManager.LayoutParams();
            //通过getApplication获取的是WindowManagerImpl.CompatModeWrapper
            mWindowManager = (WindowManager) getApplication().getSystemService(WINDOW_SERVICE);
            //设置window type
            wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
            //设置图片格式，效果为背景透明
            wmParams.format = PixelFormat.RGBA_8888;
            //设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
            wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            //调整悬浮窗显示的停靠位置为左侧置顶
            wmParams.gravity = Gravity.LEFT | Gravity.TOP;
//        wmParams.gravity = Gravity.BOTTOM;
            // 以屏幕左上角为原点，设置x、y初始值，相对于gravity
            wmParams.x = 0;
            wmParams.y = 0;

            //设置悬浮窗口长宽数据
            wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
            wmParams.height = WindowManager.LayoutParams.MATCH_PARENT;

            LayoutInflater inflater = LayoutInflater.from(this);
            //获取浮动窗口视图所在布局
            mFloatLayout = (RelativeLayout) inflater.inflate(R.layout.alert_window_menu, null);
            //添加mFloatLayout
            mWindowManager.addView(mFloatLayout, wmParams);
            //浮动窗口按钮
            mFloatView = mFloatLayout.findViewById(R.id.alert_window_imagebtn);

            mFloatLayout.measure(View.MeasureSpec.makeMeasureSpec(0,
                    View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
                    .makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));


            //设置监听浮动窗口的触摸移动
            mFloatView.setOnTouchListener(this);

            mFloatView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
//                VirtualKeyService.getService().clickBackKey();
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "已经创建完毕", Toast.LENGTH_SHORT).show();
        }
    }


    public boolean isServiceWork(Context mContext, String serviceName) {
        boolean isWork = false;
        ActivityManager myAM = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> myList = myAM.getRunningServices(40);
        if (myList.size() <= 0) {
            return false;
        }
        for (int i = 0; i < myList.size(); i++) {
            String mName = myList.get(i).service.getClassName().toString();
            if (mName.equals(serviceName)) {
                isWork = true;
                break;
            }
        }
        return isWork;
    }

}

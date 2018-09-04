package com.chen.sumsungs8virtualkey;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chen.sumsungs8virtualkey.utils.LogUtils;

import java.util.Iterator;
import java.util.List;

public class VirtualKeyService extends AccessibilityService implements IHandleMessage{


    private static VirtualKeyService mVirtualService;

    private static final String PACKAGENAME = "com.tencent.mm";

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        mVirtualService = this;
        LogUtils.e("mVirtualService:"+ mVirtualService.getServiceInfo().getId());
        //发送广播，已经连接上了
        Toast.makeText(this, "成功开启辅助功能权限", Toast.LENGTH_SHORT).show();
        mHander = new WeakRefHandler<>(this);
        createFloatView();

    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

        Log.i("VirtualKeyService", "shoudao");

    }

    @Override
    public void onInterrupt() {
        destoryFlowView();
        Toast.makeText(this, "断开辅助功能", Toast.LENGTH_SHORT).show();
    }


    //定义浮动窗口布局
    private RelativeLayout mFloatLayout;
    private WindowManager.LayoutParams wmParams;
    //创建浮动窗口设置布局参数的对象
    private WindowManager mWindowManager;

    private TextView mFloatView;

    private static final String HALF_BLACK = "#55000000";
    private static final String TRANSLATE = "#00000000";



    public TextView getmFloatView(){
        if(mFloatView==null){
            return null;
        }
        return mFloatView;
    }


    public void setBgTran(){
        if (mFloatView != null)
            mFloatView.setBackgroundColor(Color.parseColor(TRANSLATE));
        else
            Toast.makeText(getApplicationContext(), getString(R.string.get_permiss_first)
                    , Toast.LENGTH_SHORT)
                    .show();
    }


    public void setBgGray(){
        if (mFloatView != null)
            mFloatView.setBackgroundColor(Color.parseColor(HALF_BLACK));
        else
            Toast.makeText(getApplicationContext(), getString(R.string.get_permiss_first)
                    , Toast.LENGTH_SHORT)
                    .show();
    }

    @SuppressLint("ClickableViewAccessibility")
    public void createFloatView() {

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
            mFloatView.setOnTouchListener(new onClick());
            mFloatView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

        } else {
            Toast.makeText(getApplicationContext(), "已经创建完毕", Toast.LENGTH_SHORT).show();
        }
    }


    public void destoryFlowView() {
        if (mFloatLayout != null) {
            //移除悬浮窗口
            mWindowManager.removeView(mFloatLayout);
            mFloatLayout = null;
        }
    }

    private static final String TAG = VirtualKeyService.class.getSimpleName();

    boolean isClick;
    private boolean mIsMove;
    private boolean mTouchTimeOut = false;


    private long mDownTime;
    private long mMoveTime;

    private int mDownX;
    private int mMoveX;

    private WeakRefHandler mHander;

    @Override
    public void onHandleMessage(Message msg) {

    }

    private class  onClick implements View.OnTouchListener{

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
                        Log.e(TAG, "mIsMove:" + mIsMove + "/isMoveEnough" + isMoveEnough +
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

            LogUtils.e("mVirtualService:"+ i.getId());

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

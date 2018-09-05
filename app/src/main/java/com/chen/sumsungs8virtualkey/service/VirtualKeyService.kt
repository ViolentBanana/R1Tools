package com.chen.sumsungs8virtualkey.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Build
import android.os.Message
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityManager
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import com.chen.sumsungs8virtualkey.R
import com.chen.sumsungs8virtualkey.utils.IHandleMessage

import com.chen.sumsungs8virtualkey.utils.LogUtils
import com.chen.sumsungs8virtualkey.utils.WeakRefHandler

class VirtualKeyService : AccessibilityService(), IHandleMessage {


    //定义浮动窗口布局
    private var mFloatLayout: RelativeLayout? = null
    private var wmParams: WindowManager.LayoutParams? = null
    //创建浮动窗口设置布局参数的对象
    private var mWindowManager: WindowManager? = null

    private var mFloatView: TextView? = null

    internal var isClick: Boolean = false
    private var mIsMove: Boolean = false
    private var mTouchTimeOut = false


    private var mDownTime: Long = 0
    private val mMoveTime: Long = 0

    private var mDownX: Int = 0
    private var mMoveX: Int = 0

    private var mHander: WeakRefHandler<*>? = null

    override fun onServiceConnected() {
        super.onServiceConnected()
        service = this
        LogUtils.e("mVirtualService:" + service!!.serviceInfo.id)
        //发送广播，已经连接上了
        Toast.makeText(this, "成功开启辅助功能权限", Toast.LENGTH_SHORT).show()
        mHander = WeakRefHandler(this)
        createFloatView()

    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {

        Log.i("VirtualKeyService", "shoudao")

    }

    override fun onInterrupt() {
        destoryFlowView()
        Toast.makeText(this, "断开辅助功能", Toast.LENGTH_SHORT).show()
    }


    fun getmFloatView(): TextView? {
        return if (mFloatView == null) {
            null
        } else mFloatView
    }


    fun setBgTran() {
        if (mFloatView != null)
            mFloatView!!.setBackgroundColor(Color.parseColor(TRANSLATE))
        else
            Toast.makeText(applicationContext, getString(R.string.get_permiss_first), Toast.LENGTH_SHORT)
                    .show()
    }


    fun setBgGray() {
        if (mFloatView != null)
            mFloatView!!.setBackgroundColor(Color.parseColor(HALF_BLACK))
        else
            Toast.makeText(applicationContext, getString(R.string.get_permiss_first), Toast.LENGTH_SHORT)
                    .show()
    }

    @SuppressLint("ClickableViewAccessibility")
    fun createFloatView() {

        if (!isRunning)
            return

        if (mFloatLayout == null) {

            wmParams = WindowManager.LayoutParams()
            //通过getApplication获取的是WindowManagerImpl.CompatModeWrapper
            mWindowManager = application.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            //设置window type
            wmParams!!.type = WindowManager.LayoutParams.TYPE_PHONE
            //设置图片格式，效果为背景透明
            wmParams!!.format = PixelFormat.RGBA_8888
            //设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
            wmParams!!.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            //调整悬浮窗显示的停靠位置为左侧置顶
            wmParams!!.gravity = Gravity.LEFT or Gravity.TOP
            //        wmParams.gravity = Gravity.BOTTOM;
            // 以屏幕左上角为原点，设置x、y初始值，相对于gravity
            wmParams!!.x = 0
            wmParams!!.y = 0

            //设置悬浮窗口长宽数据
            wmParams!!.width = WindowManager.LayoutParams.WRAP_CONTENT
            wmParams!!.height = WindowManager.LayoutParams.MATCH_PARENT

            val inflater = LayoutInflater.from(this)
            //获取浮动窗口视图所在布局
            mFloatLayout = inflater.inflate(R.layout.alert_window_menu, null) as RelativeLayout
            //添加mFloatLayout
            mWindowManager!!.addView(mFloatLayout, wmParams)
            //浮动窗口按钮
            mFloatView = mFloatLayout!!.findViewById(R.id.alert_window_imagebtn)

            mFloatLayout!!.measure(View.MeasureSpec.makeMeasureSpec(0,
                    View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
                    .makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED))


            //设置监听浮动窗口的触摸移动
            mFloatView!!.setOnTouchListener(onClick())
            mFloatView!!.setOnClickListener { }

        } else {
            Toast.makeText(applicationContext, "已经创建完毕", Toast.LENGTH_SHORT).show()
        }
    }


    fun destoryFlowView() {
        if (mFloatLayout != null) {
            //移除悬浮窗口
            mWindowManager!!.removeView(mFloatLayout)
            mFloatLayout = null
        }
    }

    override fun onHandleMessage(msg: Message) {

    }

    private inner class onClick : View.OnTouchListener {

        override fun onTouch(v: View, event: MotionEvent): Boolean {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {

                    Log.e(TAG, "==========START================")
                    mIsMove = true
                    //                        mFloatView.setBackgroundResource(R.drawable.circle_red);
                    mDownTime = System.currentTimeMillis() / 1000
                    mDownX = event.rawX.toInt() - mFloatView!!.measuredWidth / 2
                    isClick = false


                    if (!mTouchTimeOut)
                        mHander!!.postDelayed({
                            if (mIsMove)
                                mTouchTimeOut = true
                        }, 500)
                }
                MotionEvent.ACTION_MOVE -> {
                    // getRawX是触摸位置相对于屏幕的坐标，getX是相对于按钮的坐标
                    wmParams!!.x = event.rawX.toInt() - mFloatView!!.measuredWidth / 2
                    // 减25为状态栏的高度
                    wmParams!!.y = (event.rawY.toInt()
                            - mFloatView!!.measuredHeight / 2 - 75)


                    mMoveX = wmParams!!.x
                    // 刷新
                    //                Log.e("touch:", "X:" + wmParams.x + "/Y:" + wmParams.y);

                    val isMoveEnough = mMoveX - mDownX > 40

                    Log.e("isMoveEnough:", "DownX:" + mDownX + "moveX:" + mMoveX)

                    if (mIsMove && isMoveEnough && !mTouchTimeOut) {
                        isClick = true
                        mIsMove = false
                        service!!.clickBackKey()
                        return true
                    } else {
                        Log.e(TAG, "mIsMove:" + mIsMove + "/isMoveEnough" + isMoveEnough +
                                "/mTouchTimeOut" + mTouchTimeOut)
                        return false
                    }
                }

                MotionEvent.ACTION_UP -> {
                    mMoveX = 0
                    mDownX = 0
                    mTouchTimeOut = false
                    Log.e(TAG, "==========STOP================")

                    mIsMove = false
                    //                        mFloatView.setBackgroundResource(R.drawable.circle_cyan);
                    return isClick// 此处返回false则属于移动事件，返回true则释放事件，可以出发点击否。
                }

                else -> {
                }
            }
            return false
        }
    }

    fun clickBackKey(): Boolean {
        try {
            return performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK)
        } catch (e: Exception) {
            return false
        }

    }

    fun clickRecentKey(): Boolean {
        try {
            return performGlobalAction(AccessibilityService.GLOBAL_ACTION_RECENTS)
        } catch (e: Exception) {
            return false
        }

    }

    fun clickHomeKey(): Boolean {
        try {
            return performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME)
        } catch (e: Exception) {
            return false
        }

    }

    companion object {


        var service: VirtualKeyService? = null
            private set

        private val PACKAGENAME = "com.tencent.mm"

        private val HALF_BLACK = "#55000000"
        private val TRANSLATE = "#00000000"

        private val TAG = VirtualKeyService::class.java.simpleName

        /**
         * 判断当前服务是否正在运行
         */
        val isRunning: Boolean
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            get() {
                if (service == null) {
                    return false
                }
                val accessibilityManager = service!!.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
                val info = service!!.serviceInfo ?: return false
                val list = accessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC)
                val iterator = list.iterator()

                var isConnect = false
                while (iterator.hasNext()) {
                    val i = iterator.next()

                    LogUtils.e("mVirtualService:" + i.id)

                    if (i.id == info.id) {
                        isConnect = true
                        break
                    }
                }

                return if (!isConnect) {
                    false
                } else true
            }
    }
}

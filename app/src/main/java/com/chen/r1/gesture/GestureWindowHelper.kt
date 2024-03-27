package com.chen.r1.gesture

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Build
import android.os.Message
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import com.chen.r1.R
import com.chen.r1.acessbility.BaseAccessibilityService
import com.chen.r1.app.App
import com.chen.r1.utils.CUtils
import com.chen.r1.utils.IHandleMessage
import com.chen.r1.utils.LogUtils
import com.chen.r1.utils.SharedPreferencesHelper
import com.chen.r1.utils.Utils
import com.chen.r1.utils.WeakRefHandler

@SuppressLint("StaticFieldLeak")
object GestureWindowHelper : IHandleMessage {


    private val TAG = GestureWindowHelper::class.simpleName

    private const val HALF_BLACK = "#55000000"
    private const val TRANSLATE = "#00000000"


    //定义浮动窗口布局
    private var mFloatLayout: RelativeLayout? = null
    private var wmParams: WindowManager.LayoutParams? = null

    //创建浮动窗口设置布局参数的对象
    private var mWindowManager: WindowManager? = null

    private var mFloatView: TextView? = null


    //定义浮动窗口布局
    private var mFloatLayoutRight: RelativeLayout? = null
    private var wmParamsRight: WindowManager.LayoutParams? = null

    //创建浮动窗口设置布局参数的对象
    private var mWindowManagerRight: WindowManager? = null

    private var mFloatViewRight: TextView? = null

    private var mFloatViewBottom: TextView? = null

    internal var isClick: Boolean = false
    private var mIsMove: Boolean = false
    private var mTouchTimeOut = false


    private var mDownTime: Long = 0
    private val mMoveTime: Long = 0

    private var mDownX: Int = 0
    private var mMoveX: Int = 0


    private var mDownY = 0
    private var mMoveY = 0

    private var mHander: WeakRefHandler<*>? = null

    private lateinit var mContext: Context
    private lateinit var mService: BaseAccessibilityService


    fun initContext(con: Context, service: BaseAccessibilityService) {
        mContext = con
        mHander = WeakRefHandler(this)
        mService = service
    }


    override fun onHandleMessage(msg: Message) {
        TODO("Not yet implemented")
    }

    public fun createView() {
        createLeftFloatView()
//        createBottomFloatView()
    }

    @SuppressLint("ClickableViewAccessibility")
    fun createLeftFloatView() {

        if (!CUtils.isServiceRunning(mContext))
            return

        if (mFloatLayout == null) {

            wmParams = WindowManager.LayoutParams()
            //通过getApplication获取的是WindowManagerImpl.CompatModeWrapper
            mWindowManager =
                App.instance!!.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            //设置window type
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                wmParams!!.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                wmParams!!.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
            }
            //设置图片格式，效果为背景透明
            wmParams!!.format = PixelFormat.RGBA_8888
            //设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
            wmParams!!.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            //调整悬浮窗显示的停靠位置为左侧置顶
//            wmParams!!.gravity = Gravity.LEFT or Gravity.TOP
            wmParams!!.gravity = Gravity.LEFT or Gravity.BOTTOM
            // 以屏幕左上角为原点，设置x、y初始值，相对于gravity
            wmParams!!.x = 0
            wmParams!!.y = 0

            //设置悬浮窗口长宽数据
            wmParams!!.width = getLeftWidth()
            wmParams!!.height = WindowManager.LayoutParams.WRAP_CONTENT


            val inflater = LayoutInflater.from(mContext)
            //获取浮动窗口视图所在布局
            mFloatLayout = inflater.inflate(R.layout.alert_window_menu, null) as RelativeLayout

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(mContext)) {
                Toast.makeText(
                    mContext, "ACTION_MANAGE_OVERLAY_PERMISSION权限已被拒绝", Toast
                        .LENGTH_SHORT
                ).show()
                return
            } else {
                //添加mFloatLayout
                mWindowManager!!.addView(mFloatLayout, wmParams)
                //浮动窗口按钮
                mFloatView = mFloatLayout!!.findViewById(R.id.alert_window_imagebtn)
                mFloatView!!.visibility = View.VISIBLE

                if (getViewTranslate()) {
                    mFloatView?.setBackgroundColor(Color.parseColor(GestureWindowHelper.TRANSLATE))
                } else {
                    mFloatView?.setBackgroundColor(Color.parseColor(GestureWindowHelper.HALF_BLACK))
                }
                val layp = mFloatView!!.layoutParams as RelativeLayout.LayoutParams
                layp.setMargins(0, getLeftMarginTopHeight(), 0, getLeftMarginBottomHeight())
                layp.height = RelativeLayout.LayoutParams.MATCH_PARENT
                layp.width = getLeftWidth()

                //设置监听浮动窗口的触摸移动
                mFloatView!!.setOnTouchListener(onClick())


            }
        } else {
            Toast.makeText(mContext, "已经创建完毕", Toast.LENGTH_SHORT).show()
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    fun createRightFloatView() {

        if (!CUtils.isServiceRunning(mContext))
            return

        if (mFloatLayoutRight == null) {

            wmParamsRight = WindowManager.LayoutParams()
            //通过getApplication获取的是WindowManagerImpl.CompatModeWrapper
            mWindowManagerRight =
                App.instance!!.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            //设置window type
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                wmParamsRight!!.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                wmParamsRight!!.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
            }
            //设置图片格式，效果为背景透明
            wmParamsRight!!.format = PixelFormat.RGBA_8888
            //设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
            wmParamsRight!!.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            //调整悬浮窗显示的停靠位置为左侧置顶
//            wmParams!!.gravity = Gravity.LEFT or Gravity.TOP
            wmParamsRight!!.gravity = Gravity.RIGHT
            // 以屏幕左上角为原点，设置x、y初始值，相对于gravity
            wmParamsRight!!.x = 0
            wmParamsRight!!.y = 0

            //设置悬浮窗口长宽数据
//            wmParamsRight!!.width = Utils.dp2px(resources, 10f).toInt()
            wmParamsRight!!.width = getRightWidth()
            wmParamsRight!!.height = WindowManager.LayoutParams.WRAP_CONTENT

            val inflater = LayoutInflater.from(mContext)
            //获取浮动窗口视图所在布局
            mFloatLayoutRight = inflater.inflate(R.layout.alert_window_menu, null) as RelativeLayout

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(mContext)) {
                Toast.makeText(
                    mContext, "ACTION_MANAGE_OVERLAY_PERMISSION权限已被拒绝", Toast
                        .LENGTH_SHORT
                ).show()
            } else {
                //添加mFloatLayout
                mWindowManagerRight!!.addView(mFloatLayoutRight, wmParamsRight)
                //浮动窗口按钮
                mFloatViewRight = mFloatLayoutRight!!.findViewById(R.id.alert_window_imagebtn_right)
                mFloatViewRight!!.visibility = View.VISIBLE

                if (getViewTranslate()) {
                    mFloatViewRight!!.setBackgroundColor(Color.parseColor(GestureWindowHelper.TRANSLATE))
                } else {
                    mFloatViewRight?.setBackgroundColor(Color.parseColor(GestureWindowHelper.HALF_BLACK))
                }

                val layp = mFloatViewRight!!.layoutParams as RelativeLayout.LayoutParams
                layp.setMargins(0, getRightMarginTopHeight(), 0, getRightMarginBottomHeight())
                layp.height = RelativeLayout.LayoutParams.MATCH_PARENT
                layp.width = getRightWidth()

                //设置监听浮动窗口的触摸移动
                mFloatViewRight!!.setOnTouchListener(onClick())
            }
        } else {
            Toast.makeText(mContext, "已经创建完毕", Toast.LENGTH_SHORT).show()
        }
    }


    fun createBottomFloatView() {

        if (!CUtils.isServiceRunning(mContext))
            return

        if (mFloatLayout == null) {

            wmParams = WindowManager.LayoutParams()
            //通过getApplication获取的是WindowManagerImpl.CompatModeWrapper
            mWindowManager =
                App.instance!!.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            //设置window type
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                wmParams!!.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                wmParams!!.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
            }
            //设置图片格式，效果为背景透明
            wmParams!!.format = PixelFormat.RGBA_8888
            //设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
            wmParams!!.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            //调整悬浮窗显示的停靠位置为左侧置顶
//            wmParams!!.gravity = Gravity.LEFT or Gravity.TOP
            wmParams!!.gravity = Gravity.BOTTOM
            // 以屏幕左上角为原点，设置x、y初始值，相对于gravity
            wmParams!!.x = 0
            wmParams!!.y = 0

            //设置悬浮窗口长宽数据
            wmParams!!.width = WindowManager.LayoutParams.MATCH_PARENT
//            wmParams!!.height = WindowManager.LayoutParams.WRAP_CONTENT
            wmParams!!.height = Utils.dp2px(mContext.resources, 10f).toInt()

            val inflater = LayoutInflater.from(mContext)
            //获取浮动窗口视图所在布局
            mFloatLayout = inflater.inflate(R.layout.alert_window_menu, null) as RelativeLayout

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                if (Settings.canDrawOverlays(mContext)) {
                    //添加mFloatLayout
                    mWindowManager!!.addView(mFloatLayout, wmParams)
                    //浮动窗口按钮
                    mFloatViewBottom = mFloatLayout!!.findViewById(R.id.alert_window_home)

//                    mFloatLayout!!.measure(View.MeasureSpec.makeMeasureSpec(0,
//                            View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
//                            .makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED))


                    //设置监听浮动窗口的触摸移动
                    mFloatViewBottom!!.setOnTouchListener(onClick())
                    mFloatViewBottom!!.setOnClickListener {
                        Toast.makeText(
                            mContext, "全屏了。。。", Toast
                                .LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        mContext, "ACTION_MANAGE_OVERLAY_PERMISSION权限已被拒绝", Toast
                            .LENGTH_SHORT
                    ).show()
                }
        } else {
            Toast.makeText(mContext, "已经创建完毕", Toast.LENGTH_SHORT).show()
        }
    }

    fun destoryFlowView() {
        if (mFloatLayout != null) {
            //移除悬浮窗口
            mWindowManager!!.removeView(mFloatLayout)
            mFloatLayout = null
            mFloatView = null
            mWindowManager = null
        }

        if (mFloatLayoutRight != null) {
            //移除悬浮窗口
            mWindowManagerRight!!.removeView(mFloatLayoutRight)
            mFloatLayoutRight = null
            mFloatViewRight = null
            mWindowManagerRight = null
        }
    }


    fun setBgTran() {

        if (mFloatView != null || mFloatViewRight != null) {

            if (mFloatView != null) {
                mFloatView!!.setBackgroundColor(Color.parseColor(GestureWindowHelper.TRANSLATE))
            }

            if (mFloatViewRight != null) {
                mFloatViewRight!!.setBackgroundColor(Color.parseColor(GestureWindowHelper.TRANSLATE))
            }

        } else {
            Toast.makeText(
                mContext,
                mContext.getString(R.string.get_permiss_first),
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }


    fun setBgGray() {
        if (mFloatView != null || mFloatViewRight != null) {

            if (mFloatView != null) {
                mFloatView!!.setBackgroundColor(Color.parseColor(GestureWindowHelper.HALF_BLACK))
            }

            if (mFloatViewRight != null) {
                mFloatViewRight!!.setBackgroundColor(Color.parseColor(GestureWindowHelper.HALF_BLACK))
            }

        } else {
            Toast.makeText(
                mContext,
                mContext.getString(R.string.get_permiss_first),
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }

    fun setBottomBgGray() {
        if (mFloatViewBottom != null)
            mFloatViewBottom!!.setBackgroundColor(Color.parseColor(GestureWindowHelper.HALF_BLACK))
        else
            Toast.makeText(
                mContext,
                mContext.getString(R.string.get_permiss_first),
                Toast.LENGTH_SHORT
            )
                .show()
    }

    fun getLeftWidth(): Int {
        return SharedPreferencesHelper.INSTANCE.getInt(
            App.instance!!,
            SharedPreferencesHelper.INSTANCE.LEFT_WIDTH,
            30
        )
    }

    fun getLeftMarginTopHeight(): Int {
        return SharedPreferencesHelper.INSTANCE.getInt(
            App.instance!!,
            SharedPreferencesHelper.INSTANCE.LEFT_MARGIN_TOP,
            200
        )
    }

    fun getLeftMarginBottomHeight(): Int {
        return SharedPreferencesHelper.INSTANCE.getInt(
            App.instance!!,
            SharedPreferencesHelper.INSTANCE.LEFT_MARGIN_BOTTOM,
            400
        )
    }

    fun getRightWidth(): Int {
        return SharedPreferencesHelper.INSTANCE.getInt(
            App.instance!!,
            SharedPreferencesHelper.INSTANCE.RIGHT_WIDTH,
            30
        )
    }

    fun getRightMarginTopHeight(): Int {
        return SharedPreferencesHelper.INSTANCE.getInt(
            App.instance!!,
            SharedPreferencesHelper.INSTANCE.RIGHT_MARGIN_TOP,
            200
        )
    }

    fun getRightMarginBottomHeight(): Int {
        return SharedPreferencesHelper.INSTANCE.getInt(
            App.instance!!,
            SharedPreferencesHelper.INSTANCE.RIGHT_MARGIN_BOTTOM,
            400
        )
    }

    fun getViewTranslate(): Boolean {
        return SharedPreferencesHelper.INSTANCE.getBoolean(
            App.instance!!,
            SharedPreferencesHelper.INSTANCE.TRANSLATE
        )
    }

    fun getmFloatView(): TextView? {
        return if (mFloatView == null) {
            null
        } else mFloatView
    }


    fun getmFloatViewRight(): TextView? {
        return if (mFloatViewRight == null) {
            null
        } else mFloatViewRight
    }


    private class onClick : View.OnTouchListener {

        override fun onTouch(v: View, event: MotionEvent): Boolean {

            when (v.id) {

                R.id.alert_window_imagebtn -> {

                    LogUtils.e("touch " + v.id + "viewgroup:" + mFloatLayout!!.id)

                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {

                            Log.e(GestureWindowHelper.TAG, "==========START================")
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
                                mService.clickBackKey()
                                return true
                            } else {
                                Log.e(
                                    GestureWindowHelper.TAG,
                                    "mIsMove:" + mIsMove + "/isMoveEnough" + isMoveEnough +
                                            "/mTouchTimeOut" + mTouchTimeOut
                                )
                                return false
                            }
                        }

                        MotionEvent.ACTION_UP -> {
                            mMoveX = 0
                            mDownX = 0
                            mTouchTimeOut = false
                            Log.e(GestureWindowHelper.TAG, "==========STOP================")

                            mIsMove = false
                            //                        mFloatView.setBackgroundResource(R.drawable.circle_cyan);
                            return isClick// 此处返回false则属于移动事件，返回true则释放事件，可以出发点击否。
                        }

                        else -> {
                        }
                    }
                    return false
                }


                R.id.alert_window_imagebtn_right -> {
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {

                            Log.e(GestureWindowHelper.TAG, "==========START================")
                            mIsMove = true
                            //                        mFloatView.setBackgroundResource(R.drawable.circle_red);
                            mDownTime = System.currentTimeMillis() / 1000
                            mDownX = event.rawX.toInt() - mFloatViewRight!!.measuredWidth / 2
                            isClick = false


                            if (!mTouchTimeOut)
                                mHander!!.postDelayed({
                                    if (mIsMove)
                                        mTouchTimeOut = true
                                }, 500)
                        }

                        MotionEvent.ACTION_MOVE -> {
                            // getRawX是触摸位置相对于屏幕的坐标，getX是相对于按钮的坐标
                            wmParamsRight!!.x =
                                event.rawX.toInt() - mFloatViewRight!!.measuredWidth / 2
                            // 减25为状态栏的高度
                            wmParamsRight!!.y = (event.rawY.toInt()
                                    - mFloatViewRight!!.measuredHeight / 2 - 75)


                            mMoveX = wmParamsRight!!.x
                            // 刷新
                            //                Log.e("touch:", "X:" + wmParams.x + "/Y:" + wmParams.y);

                            val isMoveEnough = mDownX - mMoveX > 40

                            Log.e("isMoveEnough:", "DownX:" + mDownX + "moveX:" + mMoveX)

                            if (mIsMove && isMoveEnough && !mTouchTimeOut) {
                                isClick = true
                                mIsMove = false
                                mService.clickBackKey()
                                return true
                            } else {
                                Log.e(
                                    GestureWindowHelper.TAG,
                                    "mIsMove:" + mIsMove + "/isMoveEnough" + isMoveEnough +
                                            "/mTouchTimeOut" + mTouchTimeOut
                                )
                                return false
                            }
                        }

                        MotionEvent.ACTION_UP -> {
                            mMoveX = 0
                            mDownX = 0
                            mTouchTimeOut = false
                            Log.e(GestureWindowHelper.TAG, "==========STOP================")

                            mIsMove = false
                            //                        mFloatView.setBackgroundResource(R.drawable.circle_cyan);
                            return isClick// 此处返回false则属于移动事件，返回true则释放事件，可以出发点击否。
                        }

                        else -> {
                        }
                    }
                    return false
                }


                R.id.alert_window_home -> {
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {

                            Log.e(GestureWindowHelper.TAG, "==========HOME START================")
                            mIsMove = true
                            mDownTime = System.currentTimeMillis() / 1000
//                            mDownY = event.rawY.toInt() - mFloatViewBottom!!.measuredHeight / 2
                            mDownX = event.rawX.toInt()
                            mDownY = event.rawY.toInt()
                            isClick = false


                            if (!mTouchTimeOut)
                                mHander!!.postDelayed({
                                    if (mIsMove)
                                        mTouchTimeOut = true
                                }, 500)
                        }

                        MotionEvent.ACTION_MOVE -> {
                            // getRawX是触摸位置相对于屏幕的坐标，getX是相对于按钮的坐标
                            wmParams!!.x = event.rawX.toInt() - mFloatViewBottom!!.measuredWidth / 2
                            // 减25为状态栏的高度
                            wmParams!!.y = (event.rawY.toInt())


                            mMoveX = wmParams!!.x
                            mMoveY = wmParams!!.y
                            // 刷新

                            val isMoveEnough = Math.abs(mMoveY - mDownY) > 40

                            Log.e("isMoveEnough:", "DownY:" + mDownY + "moveX:" + mMoveY)
                            Log.e(
                                "isMoveEnough:",
                                "DownX:" + mDownX + "measuredWidth:" + mFloatViewBottom!!.measuredWidth
                            )
                            Log.e("isMoveEnough:", "mTouchTimeOut" + mTouchTimeOut)
                            Log.e("isMoveEnough:", "mIsMove:" + mIsMove)

                            if (mIsMove && isMoveEnough && !mTouchTimeOut) {
                                isClick = true
                                mIsMove = false


                                if (mDownX < mFloatViewBottom!!.measuredWidth / 3) {
                                    mService.clickRecentKey()

                                } else if (mDownX < (mFloatViewBottom!!.measuredWidth / 3) * 2) {
                                    mService.clickHomeKey()

                                } else {
                                    mService.clickBackKey()
                                }

                                return true
                            } else {
                                Log.e(
                                    GestureWindowHelper.TAG,
                                    "mIsMove:" + mIsMove + "/isMoveEnough" + isMoveEnough +
                                            "/mTouchTimeOut" + mTouchTimeOut
                                )
                                return false
                            }
                        }

                        MotionEvent.ACTION_UP -> {
                            mMoveY = 0
                            mDownY = 0
                            mDownX = 0
                            mTouchTimeOut = false
                            Log.e(GestureWindowHelper.TAG, "==========HOME STOP================")
                            mIsMove = false
                            //                        mFloatView.setBackgroundResource(R.drawable.circle_cyan);
                            return isClick// 此处返回false则属于移动事件，返回true则释放事件，可以出发点击否。
                        }

                        else -> {
                        }
                    }
                    return true
                }
            }
            return false
        }
    }


}
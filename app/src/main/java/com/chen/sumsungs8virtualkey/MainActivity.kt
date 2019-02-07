package com.chen.sumsungs8virtualkey

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.chen.sumsungs8virtualkey.service.VirtualKeyService

import com.chen.sumsungs8virtualkey.utils.CUtils
import com.chen.sumsungs8virtualkey.utils.LogUtils
import android.graphics.PixelFormat
import android.support.v4.content.ContextCompat.startActivity
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.RelativeLayout
import org.jetbrains.annotations.NotNull
import kotlin.jvm.internal.Intrinsics
import android.app.UiModeManager
import android.content.SharedPreferences
import android.graphics.Color
import kotlin.TypeCastException
import android.os.Build.VERSION
import android.preference.PreferenceManager
import com.chen.sumsungs8virtualkey.utils.Utils


class MainActivity : AppCompatActivity(), View.OnClickListener {

    private var mAccessStatus: TextView? = null
    private var mAccessBtn: Button? = null

    private var mNotificationStatus: TextView? = null
    private var mNotificationBtn: Button? = null

    private var mWindowsStatus: TextView? = null
    private var mWindowsBtn: Button? = null

    private var mAutoStartStatus: TextView? = null
    private var mAutoStartBtn: Button? = null


    private var mBgColorCommit: TextView? = null
    private var mColor_transparent: TextView? = null
    private var mBackViewStatus: TextView? = null
    private var mBackRightViewStatus: TextView? = null


    private var mBackViewCreate: Button? = null
    private var mBackViewRightCreate: Button? = null
    private var mBackViewDestory: Button? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(applicationContext)) {
                //启动Activity让用户授权
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                intent.data = Uri.parse("package:$packageName")
                startActivityForResult(intent, 100)
            }
        }
    }

    private fun initView() {


        mWindowsBtn = findViewById(R.id.window_open)
        mWindowsBtn!!.setOnClickListener(this)
        mWindowsStatus = findViewById(R.id.window_open_status)

        mNotificationBtn = findViewById(R.id.notification_btn)
        mNotificationBtn!!.setOnClickListener(this)
        mNotificationStatus = findViewById(R.id.notification_status)

        mAccessBtn = findViewById(R.id.access_open)
        mAccessBtn!!.setOnClickListener(this)
        mAccessStatus = findViewById(R.id.access_status)


        mAutoStartBtn = findViewById(R.id.auto_start_btn)
        mAutoStartBtn!!.setOnClickListener(this)
        mAutoStartStatus = findViewById(R.id.auto_start_status)

        mBgColorCommit = findViewById(R.id.color_bg_commit)
        mBgColorCommit!!.setOnClickListener(this)

        mColor_transparent = findViewById(R.id.color_transparent)
        mColor_transparent!!.setOnClickListener(this)

        mBackViewStatus = findViewById(R.id.color_bg_status)
        mBackRightViewStatus = findViewById(R.id.color_bg_right_status)


        mBackViewCreate = findViewById(R.id.color_bg_create)
        mBackViewCreate!!.setOnClickListener(this)

        mBackViewRightCreate = findViewById(R.id.color_bg_create_right)
        mBackViewRightCreate!!.setOnClickListener(this)

        mBackViewDestory = findViewById(R.id.color_bg_destory)
        mBackViewDestory!!.setOnClickListener(this)


//        LogUtils.e("getNavBarHeight:" + forceTouchWizNavEnabled(applicationContext))
//        LogUtils.e("getNavBarHeight:" + getNavBarHeight(applicationContext))

//        forceNavBlack(applicationContext)
//        Utils.clearBlackNav(applicationContext)
//        Utils.execRootCmd("wm overscan 0,0,0,0")
    }


    fun forceNavBlack(context: Context) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("navigationbar_color",
                Settings.Global.getString(context.getContentResolver(), "navigationbar_color")).putString("navigationbar_current_color",
                Settings.Global.getString(context.getContentResolver(), "navigationbar_current_color")).putString("navigationbar_use_theme_default",
                Settings.Global.getString(context.getContentResolver(),
                        "navigationbar_use_theme_default")).apply();
        val argb: Int = Color.argb(255, 0, 0, 0);
        Settings.Global.putInt(context.getContentResolver(), "navigationbar_color", argb);
        Settings.Global.putInt(context.getContentResolver(), "navigationbar_current_color", argb);
        Settings.Global.putInt(context.getContentResolver(), "navigationbar_use_theme_default", 0);
    }


    fun openAppDetail() {
        val intent = Intent("android.settings.APPLICATION_DETAILS_SETTINGS")
        val pkg = "com.android.settings"
        val cls = "com.android.settings.applications.InstalledAppDetails"
        intent.component = ComponentName(pkg, cls)
        intent.data = Uri.parse("package:$packageName")
        startActivity(intent)
    }

    override fun onClick(v: View) {
        when (v.id) {


            R.id.window_open -> {
                initPermission()
                Toast.makeText(applicationContext, "打开窗口", Toast.LENGTH_SHORT).show()
            }

            R.id.access_open -> openAccessibilityServiceSettings()

            R.id.color_bg_create -> {
                if (VirtualKeyService.isRunning) {
//                    VirtualKeyService.service!!.createFloatView()
                    VirtualKeyService.service!!.createView()
                } else
                    Toast.makeText(applicationContext, R.string.please_try_again, Toast.LENGTH_SHORT).show()
                checkAllpermiss()
            }
            R.id.color_bg_create_right -> {
                if (VirtualKeyService.isRunning) {
//                    VirtualKeyService.service!!.createFloatView()
                    VirtualKeyService.service!!.createRightFloatView()
                } else
                    Toast.makeText(applicationContext, R.string.please_try_again, Toast.LENGTH_SHORT).show()
                checkAllpermiss()
            }
            R.id.color_bg_destory -> {
                if (VirtualKeyService.isRunning)
                    VirtualKeyService.service!!.destoryFlowView()
                else
                    Toast.makeText(applicationContext, R.string.please_try_again, Toast.LENGTH_SHORT).show()
                checkAllpermiss()
            }


            R.id.color_bg_commit ->
                //设置灰色
                if (VirtualKeyService.isRunning)
                    VirtualKeyService.service!!.setBgGray()
                else
                    Toast.makeText(applicationContext, R.string.please_try_again, Toast.LENGTH_SHORT).show()

            R.id.color_transparent ->
                //设置透明
                if (VirtualKeyService.isRunning)
                    VirtualKeyService.service!!.setBgTran()
                else
                    Toast.makeText(applicationContext, R.string.please_try_again, Toast.LENGTH_SHORT).show()


            R.id.notification_btn ->
                //获取通知权限
                startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"))


            R.id.auto_start_btn ->
                //自动启动
                openAppDetail()
        }
    }


    fun forceTouchWizNavEnabled(context: Context): Boolean {
        Intrinsics.checkParameterIsNotNull(context, "context");
        return Settings.Global.putInt(context.getContentResolver(), "navigationbar_hide_bar_enabled", 1);
    }

    fun getNavBarHeight(context: Context): Int {
        Intrinsics.checkParameterIsNotNull(context, "context");
        var systemService: UiModeManager = context!!.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
        if (systemService == null) {
            throw  TypeCastException("null cannot be cast to non-null type android.app.UiModeManager");
        } else if ((systemService)!!.getCurrentModeType() == 3) {
            return context.getResources().getDimensionPixelSize(context.getResources().getIdentifier("navigation_bar_height_car_mode", "dimen", "android"));
        } else {
            return context.getResources().getDimensionPixelSize(context.getResources().getIdentifier("navigation_bar_height", "dimen", "android"));
        }
    }

    /**
     * 获取窗口显示权限
     */
    private fun initPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(applicationContext)) {
                //启动Activity让用户授权
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                intent.data = Uri.parse("package:$packageName")
                startActivityForResult(intent, REQUEST_OVERLAY)
            } else {
                Toast.makeText(applicationContext, "已经获取浮窗权限", Toast.LENGTH_SHORT).show()
                //                createFloatView();
            }
        } else {
            //            createFloatView();
            Toast.makeText(applicationContext, "已经获取浮窗权限", Toast.LENGTH_SHORT).show()

        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_OVERLAY) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(this)) {
                    if (VirtualKeyService.isRunning)
//                        VirtualKeyService.service!!.createView()
                    else
                        Toast.makeText(applicationContext, R.string.please_try_again, Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "ACTION_MANAGE_OVERLAY_PERMISSION权限已被拒绝", Toast
                            .LENGTH_SHORT).show()
                }
            }
        }
    }


    override fun onResume() {
        super.onResume()
        checkAllpermiss()

        LogUtils.e("是否运行中：" + VirtualKeyService.isRunning)


    }


    fun checkAllpermiss() {

        //辅助功能
        if (VirtualKeyService.isRunning) {
            mAccessStatus!!.setText(R.string.yes)

            if (VirtualKeyService.service!!.getmFloatView() == null) {
                mBackViewStatus!!.setText(R.string.no)
            } else {
                mBackViewStatus!!.setText(R.string.yes)
            }


            if (VirtualKeyService.service!!.getmFloatViewRight() == null) {
                mBackRightViewStatus!!.setText(R.string.no)
            } else {
                mBackRightViewStatus!!.setText(R.string.yes)
            }


            //通知权限
            if (CUtils.isEnabled(applicationContext)) {
                mNotificationStatus!!.setText(R.string.yes)
            } else {
                mNotificationStatus!!.setText(R.string.no)
            }


        } else {
            mAccessStatus!!.setText(R.string.no)
            if (VirtualKeyService.service != null)
                if (VirtualKeyService.service!!.getmFloatView() != null)
                    VirtualKeyService.service!!.destoryFlowView()
        }


        //窗口显示权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.canDrawOverlays(applicationContext)) {
                mWindowsStatus!!.setText(R.string.yes)
            } else {
                mWindowsStatus!!.setText(R.string.no)
            }
        } else {
            mWindowsStatus!!.setText(R.string.yes)
        }
    }

    /**
     * 打开辅助服务的设置
     */
    private fun openAccessibilityServiceSettings() {
        try {
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            startActivity(intent)
            Toast.makeText(this, "请开启r1手势返回服务", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    companion object {

        val REQUEST_OVERLAY = 100

        private val TAG = "MainActivityTouch"
    }
}

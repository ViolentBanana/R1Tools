package com.chen.sumsungs8virtualkey

import android.app.Dialog
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.chen.sumsungs8virtualkey.service.VirtualKeyService

import com.chen.sumsungs8virtualkey.utils.CUtils
import com.chen.sumsungs8virtualkey.utils.LogUtils
import android.graphics.PixelFormat
import android.support.v4.content.ContextCompat.startActivity
import android.view.LayoutInflater
import android.view.WindowManager
import org.jetbrains.annotations.NotNull
import kotlin.jvm.internal.Intrinsics
import android.app.UiModeManager
import android.content.*
import android.graphics.Color
import kotlin.TypeCastException
import android.os.Build.VERSION
import android.preference.PreferenceManager
import android.support.v7.app.AlertDialog
import android.widget.*
import com.chen.sumsungs8virtualkey.utils.SharedPreferencesHelper
import com.chen.sumsungs8virtualkey.utils.Utils
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), View.OnClickListener {

    private var access_status: TextView? = null
    private var access_open: Button? = null

    private var notification_status: TextView? = null
    private var notification_btn: Button? = null

    private var window_open_status: TextView? = null

    private var auto_start_status: TextView? = null
    private var auto_start_btn: Button? = null


    private var color_bg_commit: TextView? = null
    private var color_transparent: TextView? = null
    private var color_bg_status: TextView? = null
    private var color_bg_right_status: TextView? = null


    private var color_bg_create: Button? = null
    private var color_bg_create_right: Button? = null
    private var color_bg_destory: Button? = null


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


        window_open.setOnClickListener(this)
        notification_btn!!.setOnClickListener(this)

        access_open!!.setOnClickListener(this)


        auto_start_btn!!.setOnClickListener(this)

        color_bg_commit!!.setOnClickListener(this)

        color_transparent!!.setOnClickListener(this)

        color_bg_create!!.setOnClickListener(this)

        color_bg_create_right!!.setOnClickListener(this)

        color_bg_destory!!.setOnClickListener(this)

        setting_vibrator.setOnClickListener(this)

//        LogUtils.e("getNavBarHeight:" + forceTouchWizNavEnabled(applicationContext))
//        LogUtils.e("getNavBarHeight:" + getNavBarHeight(applicationContext))

//        forceNavBlack(applicationContext)
//        Utils.clearBlackNav(applicationContext)
//        Utils.execRootCmd("wm overscan 0,0,0,0")
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

            R.id.setting_vibrator -> {
                setVibratorStrength()

            }

        }
    }


    fun setVibratorStrength() {
        var et = EditText(this);
        AlertDialog.Builder(this).setTitle("请输入消息")
                .setIcon(android.R.drawable.sym_def_app_icon)
                .setView(et)
                .setPositiveButton("确定") { dialog, which ->
                    Toast.makeText(getApplicationContext(), et.getText().toString(), Toast.LENGTH_LONG).show();
                }.setNegativeButton("取消", null).show();
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
            access_status!!.setText(R.string.yes)

            if (VirtualKeyService.service!!.getmFloatView() == null) {
                color_bg_status!!.setText(R.string.no)
            } else {
                color_bg_status!!.setText(R.string.yes)
            }


            if (VirtualKeyService.service!!.getmFloatViewRight() == null) {
                color_bg_right_status!!.setText(R.string.no)
            } else {
                color_bg_right_status!!.setText(R.string.yes)
            }


            //通知权限
            if (CUtils.isEnabled(applicationContext)) {
                notification_status!!.setText(R.string.yes)
            } else {
                notification_status!!.setText(R.string.no)
            }


        } else {
            access_status!!.setText(R.string.no)
            if (VirtualKeyService.service != null)
                if (VirtualKeyService.service!!.getmFloatView() != null)
                    VirtualKeyService.service!!.destoryFlowView()
        }


        //窗口显示权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.canDrawOverlays(applicationContext)) {
                window_open_status!!.setText(R.string.yes)
            } else {
                window_open_status!!.setText(R.string.no)
            }
        } else {
            window_open_status!!.setText(R.string.yes)
        }


        auto_start_vibrator.text = SharedPreferencesHelper.INSTANCE.getInt(this, SharedPreferencesHelper.INSTANCE.VIBRATOR_STRENGTH, 0).toString()

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

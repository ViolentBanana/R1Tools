package com.chen.sumsungs8virtualkey.gesture

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.text.InputFilter
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast

import com.chen.sumsungs8virtualkey.R
import com.chen.sumsungs8virtualkey.REQUEST_OVERLAY
import com.chen.sumsungs8virtualkey.app.App
import com.chen.sumsungs8virtualkey.base.BaseFragment
import com.chen.sumsungs8virtualkey.service.VirtualKeyService
import com.chen.sumsungs8virtualkey.utils.CUtils
import com.chen.sumsungs8virtualkey.utils.LogUtils
import com.chen.sumsungs8virtualkey.utils.SharedPreferencesHelper
import kotlinx.android.synthetic.main.fragment_gesture.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain activity fragment must implement the
 * [GestureFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [GestureFragment.newInstance] factory method to
 * create an instance of activity fragment.
 *
 */
class GestureFragment : BaseFragment(), View.OnClickListener {

    override fun getTitleText(): Int {
        return R.string.gesture_fragment_title
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for activity fragment
        return inflater.inflate(R.layout.fragment_gesture, container, false)
    }


    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(activity)) {
                //启动Activity让用户授权
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                intent.data = Uri.parse("package:${activity.packageName}")
                startActivityForResult(intent, 100)
            }
        }
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
            if (CUtils.isEnabled(activity)) {
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
            if (Settings.canDrawOverlays(activity)) {
                window_open_status!!.setText(R.string.yes)
            } else {
                window_open_status!!.setText(R.string.no)
            }
        } else {
            window_open_status!!.setText(R.string.yes)
        }


        val st = SharedPreferencesHelper.INSTANCE.getInt(activity, SharedPreferencesHelper.INSTANCE.VIBRATOR_STRENGTH, 0)
        if (st == 0) {
            auto_start_vibrator.text = "无震动"
        } else {
            auto_start_vibrator.text = st.toString()
        }

    }

    override fun onResume() {
        super.onResume()
        checkAllpermiss()

        LogUtils.e("是否运行中：" + VirtualKeyService.isRunning)


    }


    fun openAppDetail() {
        val intent = Intent("android.settings.APPLICATION_DETAILS_SETTINGS")
        val pkg = "com.android.settings"
        val cls = "com.android.settings.applications.InstalledAppDetails"
        intent.component = ComponentName(pkg, cls)
        intent.data = Uri.parse("package:${activity.packageName}")
        startActivity(intent)
    }

    override fun onClick(v: View) {
        when (v.id) {


            R.id.window_open -> {
                initPermission()
                Toast.makeText(activity, "打开窗口", Toast.LENGTH_SHORT).show()
            }

            R.id.access_open -> openAccessibilityServiceSettings()

            R.id.color_bg_create -> {
                if (VirtualKeyService.isRunning) {
//                    VirtualKeyService.service!!.createFloatView()
                    VirtualKeyService.service!!.createView()
                } else
                    Toast.makeText(activity, R.string.please_try_again, Toast.LENGTH_SHORT).show()
                checkAllpermiss()
            }

            R.id.color_bg_create_right -> {
                if (VirtualKeyService.isRunning) {
//                    VirtualKeyService.service!!.createFloatView()
                    VirtualKeyService.service!!.createRightFloatView()
                } else
                    Toast.makeText(activity, R.string.please_try_again, Toast.LENGTH_SHORT).show()
                checkAllpermiss()
            }
            R.id.color_bg_destory -> {
                if (VirtualKeyService.isRunning)
                    VirtualKeyService.service!!.destoryFlowView()
                else
                    Toast.makeText(activity, R.string.please_try_again, Toast.LENGTH_SHORT).show()
                checkAllpermiss()
            }


            R.id.color_bg_commit ->
                //设置灰色
                if (VirtualKeyService.isRunning)
                    VirtualKeyService.service!!.setBgGray()
                else
                    Toast.makeText(activity, R.string.please_try_again, Toast.LENGTH_SHORT).show()

            R.id.color_transparent ->
                //设置透明
                if (VirtualKeyService.isRunning)
                    VirtualKeyService.service!!.setBgTran()
                else
                    Toast.makeText(activity, R.string.please_try_again, Toast.LENGTH_SHORT).show()


            R.id.notification_btn ->
                //获取通知权限
                startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"))


            R.id.auto_start_btn ->
                //自动启动
                openAppDetail()

            R.id.setting_vibrator -> {
                setVibratorStrength()
            }


            R.id.color_bg_create_right_width -> {
                setRightWidth()
            }

            R.id.color_bg_create_right_height_top ->{
                setRightMarginTopHeight()
            }

            R.id.color_bg_create_right_height_bottom ->{
                setRightMarginBottomHeight()
            }

            R.id.color_bg_create_width -> {
                setLeftWidth()
            }

            R.id.color_bg_create_height_bottom ->{
                setLeftMarginBottomHeight()
            }
            R.id.color_bg_create_height_top ->{
                setLeftMarginTopHeight()
            }
        }
    }

    fun setLeftWidth() {
        val et = EditText(activity)

        et.inputType = InputType.TYPE_TEXT_VARIATION_LONG_MESSAGE

        et.setText(SharedPreferencesHelper.INSTANCE.getInt(App.instance!!,
                SharedPreferencesHelper.INSTANCE.LEFT_WIDTH, 30).toString())

        AlertDialog.Builder(activity).setTitle("请输入左侧返回条宽度默认30")
                .setIcon(android.R.drawable.sym_def_app_icon)
                .setView(et)
                .setPositiveButton("确定") { dialog, which ->

                    SharedPreferencesHelper.INSTANCE.putInt(activity, SharedPreferencesHelper.INSTANCE.LEFT_WIDTH, et.text.toString().toInt())
                    reCreateView()

                }.setNegativeButton("取消", null).show()
    }

    fun setLeftMarginTopHeight() {
        val et = EditText(activity)

        et.inputType = InputType.TYPE_TEXT_VARIATION_LONG_MESSAGE

        et.setText(SharedPreferencesHelper.INSTANCE.getInt(App.instance!!,
                SharedPreferencesHelper.INSTANCE.LEFT_MARGIN_TOP, 400).toString())

        AlertDialog.Builder(activity).setTitle("请输入左侧返回条距离顶部距离0-2240，默认400")
                .setIcon(android.R.drawable.sym_def_app_icon)
                .setView(et)
                .setPositiveButton("确定") { dialog, which ->

                    SharedPreferencesHelper.INSTANCE.putInt(activity, SharedPreferencesHelper.INSTANCE.LEFT_MARGIN_TOP, et.text.toString().toInt())
                    reCreateView()

                }.setNegativeButton("取消", null).show()
    }

    fun setLeftMarginBottomHeight() {
        val et = EditText(activity)

        et.inputType = InputType.TYPE_TEXT_VARIATION_LONG_MESSAGE

        et.setText(SharedPreferencesHelper.INSTANCE.getInt(App.instance!!,
                SharedPreferencesHelper.INSTANCE.LEFT_MARGIN_BOTTOM, 200).toString())

        AlertDialog.Builder(activity).setTitle("请输入左侧返回条距离底部0-2240，默认200")
                .setIcon(android.R.drawable.sym_def_app_icon)
                .setView(et)
                .setPositiveButton("确定") { dialog, which ->

                    SharedPreferencesHelper.INSTANCE.putInt(activity, SharedPreferencesHelper.INSTANCE.LEFT_MARGIN_BOTTOM, et.text.toString().toInt())
                    reCreateView()

                }.setNegativeButton("取消", null).show()
    }

    fun setRightWidth() {
        val et = EditText(activity)

        et.inputType = InputType.TYPE_TEXT_VARIATION_LONG_MESSAGE

        et.setText(SharedPreferencesHelper.INSTANCE.getInt(App.instance!!,
                SharedPreferencesHelper.INSTANCE.RIGHT_WIDTH, 30).toString())

        AlertDialog.Builder(activity).setTitle("请输入右侧返回条宽度默认30")
                .setIcon(android.R.drawable.sym_def_app_icon)
                .setView(et)
                .setPositiveButton("确定") { dialog, which ->

                    SharedPreferencesHelper.INSTANCE.putInt(activity, SharedPreferencesHelper.INSTANCE.RIGHT_WIDTH, et.text.toString().toInt())
                    reCreateView()

                }.setNegativeButton("取消", null).show()
    }
    fun setRightMarginTopHeight() {
        val et = EditText(activity)

        et.inputType = InputType.TYPE_TEXT_VARIATION_LONG_MESSAGE

        et.setText(SharedPreferencesHelper.INSTANCE.getInt(App.instance!!,
                SharedPreferencesHelper.INSTANCE.RIGHT_MARGIN_TOP, 400).toString())

        AlertDialog.Builder(activity).setTitle("请输入右侧返回条距离顶部 0-2240 默认400")
                .setIcon(android.R.drawable.sym_def_app_icon)
                .setView(et)
                .setPositiveButton("确定") { dialog, which ->

                    SharedPreferencesHelper.INSTANCE.putInt(activity, SharedPreferencesHelper.INSTANCE.RIGHT_MARGIN_TOP, et.text.toString().toInt())
                    reCreateView()

                }.setNegativeButton("取消", null).show()
    }

    fun setRightMarginBottomHeight() {
        val et = EditText(activity)

        et.inputType = InputType.TYPE_TEXT_VARIATION_LONG_MESSAGE

        et.setText(SharedPreferencesHelper.INSTANCE.getInt(App.instance!!,
                SharedPreferencesHelper.INSTANCE.RIGHT_MARGIN_BOTTOM, 200).toString())

        AlertDialog.Builder(activity).setTitle("请输入右侧返回条距离底部 0-2240 默认200")
                .setIcon(android.R.drawable.sym_def_app_icon)
                .setView(et)
                .setPositiveButton("确定") { dialog, which ->

                    SharedPreferencesHelper.INSTANCE.putInt(activity, SharedPreferencesHelper.INSTANCE.RIGHT_MARGIN_BOTTOM, et.text.toString().toInt())
                    reCreateView()

                }.setNegativeButton("取消", null).show()
    }


    fun reCreateView() {


        if (VirtualKeyService.isRunning)
            VirtualKeyService.service!!.destoryFlowView()
        else
            Toast.makeText(activity, R.string.please_try_again, Toast.LENGTH_SHORT).show()
        if (VirtualKeyService.isRunning) {
            VirtualKeyService.service!!.createView()

            VirtualKeyService.service!!.createRightFloatView()
        } else
            Toast.makeText(activity, R.string.please_try_again, Toast.LENGTH_SHORT).show()
        checkAllpermiss()
    }


    /**
     * 设置震感
     */
    fun setVibratorStrength() {

        val et = EditText(activity)

        et.inputType = InputType.TYPE_TEXT_VARIATION_LONG_MESSAGE

        et.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(1))

        et.setText(SharedPreferencesHelper.INSTANCE.getInt(activity,
                SharedPreferencesHelper.INSTANCE.VIBRATOR_STRENGTH, 0).toString())

        AlertDialog.Builder(activity).setTitle("请输入0-9的震动强度")
                .setIcon(android.R.drawable.sym_def_app_icon)
                .setView(et)
                .setPositiveButton("确定") { dialog, which ->

                    SharedPreferencesHelper.INSTANCE.putInt(activity, SharedPreferencesHelper.INSTANCE.VIBRATOR_STRENGTH, et.text.toString().toInt())
                    checkAllpermiss()

                }.setNegativeButton("取消", null).show()
    }

    /**
     * 获取窗口显示权限
     */
    private fun initPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(activity)) {
                //启动Activity让用户授权
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                intent.data = Uri.parse("package:${activity.packageName}")
                startActivityForResult(intent, REQUEST_OVERLAY)
            } else {
                Toast.makeText(activity, "已经获取浮窗权限", Toast.LENGTH_SHORT).show()
                //                createFloatView();
            }
        } else {
            //            createFloatView();
            Toast.makeText(activity, "已经获取浮窗权限", Toast.LENGTH_SHORT).show()

        }
    }

    /**
     * 打开辅助服务的设置
     */
    private fun openAccessibilityServiceSettings() {
        try {
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            startActivity(intent)
            Toast.makeText(activity, "请开启r1手势返回服务", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            e.printStackTrace()
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


        color_bg_create_right_width.setOnClickListener(this)
        color_bg_create_width.setOnClickListener(this)

        color_bg_create_height_bottom.setOnClickListener(this)

        color_bg_create_height_top.setOnClickListener(this)

        color_bg_create_right_height_top.setOnClickListener(this)
        color_bg_create_right_height_bottom.setOnClickListener(this)


//        LogUtils.e("getNavBarHeight:" + forceTouchWizNavEnabled(activity))
//        LogUtils.e("getNavBarHeight:" + getNavBarHeight(activity))

//        forceNavBlack(activity)
//        Utils.clearBlackNav(activity)
//        Utils.execRootCmd("wm overscan 0,0,0,0")
    }


    companion object {

        @JvmStatic
        fun newInstance() =
                GestureFragment()
    }
}

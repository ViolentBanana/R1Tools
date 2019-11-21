package com.chen.r1.acessbility

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.annotation.TargetApi
import android.content.Context
import android.content.pm.PackageInfo
import android.os.Build
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityManager
import android.widget.Toast
import com.chen.r1.BuildConfig
import com.chen.r1.acessbility.AccessbilityJob
import com.chen.r1.app.App
import com.chen.r1.utils.LogUtils
import com.chen.r1.utils.SharedPreferencesHelper
import com.chen.r1.utils.Utils


import java.util.ArrayList

/**
 * Created by CHEN on 2016/12/19.
 */
class BaseAccessibilityService : AccessibilityService() {

    //检测包名
    private val PACKAGES = arrayOf("com.tencent.mm", "com.qc.grabmoney", "com.android.packageinstaller", "com.lenovo.security", "com.samsung.android.packageinstaller", "com.miui.securitycenter")


    private var mAccessbilityJobs: MutableList<AccessbilityJob>? = null


    private val mWechatPackageInfo: PackageInfo? = null


    override fun onCreate() {
        super.onCreate()
        init()
    }


    private fun init() {

        mAccessbilityJobs = ArrayList()

        //初始化辅助插件工作
        for (clazz in ACCESSBILITY_JOBS) {
            try {
                val `object` = clazz.newInstance()
                if (`object` is AccessbilityJob) {
                    `object`.onCreateJob(this)
                    mAccessbilityJobs!!.add(`object`)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }





    override fun onServiceConnected() {
        super.onServiceConnected()
        mBaseService = this
        //发送广播，已经连接上了
        Toast.makeText(this, "已连接智能管家服务", Toast.LENGTH_SHORT).show()

    }


    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        if (BuildConfig.DEBUG) {
            val eventType = event.eventType
            var eventText = ""
            LogUtils.i("==============Start====================")
            when (eventType) {
                AccessibilityEvent.TYPE_VIEW_CLICKED -> eventText = "TYPE_VIEW_CLICKED"
                AccessibilityEvent.TYPE_VIEW_FOCUSED -> eventText = "TYPE_VIEW_FOCUSED"
                AccessibilityEvent.TYPE_VIEW_LONG_CLICKED -> eventText = "TYPE_VIEW_LONG_CLICKED"
                AccessibilityEvent.TYPE_VIEW_SELECTED -> eventText = "TYPE_VIEW_SELECTED"
                AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED -> eventText = "TYPE_VIEW_TEXT_CHANGED"
                AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> eventText = "TYPE_WINDOW_STATE_CHANGED"
                AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED -> eventText = "TYPE_NOTIFICATION_STATE_CHANGED"
                AccessibilityEvent.TYPE_TOUCH_EXPLORATION_GESTURE_END -> eventText = "TYPE_TOUCH_EXPLORATION_GESTURE_END"
                AccessibilityEvent.TYPE_ANNOUNCEMENT -> eventText = "TYPE_ANNOUNCEMENT"
                AccessibilityEvent.TYPE_TOUCH_EXPLORATION_GESTURE_START -> eventText = "TYPE_TOUCH_EXPLORATION_GESTURE_START"
                AccessibilityEvent.TYPE_VIEW_HOVER_ENTER -> eventText = "TYPE_VIEW_HOVER_ENTER"
                AccessibilityEvent.TYPE_VIEW_HOVER_EXIT -> eventText = "TYPE_VIEW_HOVER_EXIT"
                AccessibilityEvent.TYPE_VIEW_SCROLLED -> eventText = "TYPE_VIEW_SCROLLED"
                AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED -> eventText = "TYPE_VIEW_TEXT_SELECTION_CHANGED"
                AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED -> eventText = "TYPE_WINDOW_CONTENT_CHANGED"
            }
            eventText = "$eventText:$eventType"
            LogUtils.i(event.packageName.toString() + "")
            LogUtils.i("界面名字:" + event.className)
            //            LogUtils.e(event+"");
            //            LogUtils.e(event.toString());
            LogUtils.i(eventText)
            LogUtils.i("=============END=====================")
        }


        val pkn = event.packageName.toString()

        LogUtils.e("长度：" + mAccessbilityJobs!!.size)
        if (mAccessbilityJobs != null && !mAccessbilityJobs!!.isEmpty()) {
            for (job in mAccessbilityJobs!!) {
                //                LogUtils.e("开始分发：" + job.isEnable() + "/" + pkn + "/" + job.getTargetPackageName());

                if (pkn == job.getTargetPackageName() && job.isEnable()) {
                    job.onReceiveJob(event)
                }
            }
        }
    }

    override fun onInterrupt() {
        LogUtils.d("grabmoney service interrupt")
        Toast.makeText(this, "中断抢红包服务", Toast.LENGTH_SHORT).show()
    }


    override fun onDestroy() {
        super.onDestroy()
        LogUtils.d("grabmoney service destory")
        if (mAccessbilityJobs != null && !mAccessbilityJobs!!.isEmpty()) {
            for (job in mAccessbilityJobs!!) {
                job.onStopJob()
            }
            mAccessbilityJobs!!.clear()
        }
        mBaseService = null
        mAccessbilityJobs = null

    }
    fun clickBackKey(): Boolean {
        try {
            Utils.vibrator(App.instance!!, SharedPreferencesHelper.INSTANCE.getInt(App.instance!!, SharedPreferencesHelper.INSTANCE.VIBRATOR_STRENGTH, 0))
            return mBaseService!!.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK)
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }

    }

    fun clickRecentKey(): Boolean {
        try {
            Utils.vibrator(App.instance!!, SharedPreferencesHelper.INSTANCE.getInt(App.instance!!, SharedPreferencesHelper.INSTANCE.VIBRATOR_STRENGTH, 0))
            return mBaseService!!.performGlobalAction(AccessibilityService.GLOBAL_ACTION_RECENTS)
        } catch (e: Exception) {
            return false
        }
    }

    fun clickHomeKey(): Boolean {
        try {
            Utils.vibrator(App.instance!!, SharedPreferencesHelper.INSTANCE.getInt(App.instance!!, SharedPreferencesHelper.INSTANCE.VIBRATOR_STRENGTH, 0))
            return mBaseService!!.performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME)
        } catch (e: Exception) {
            return false
        }

    }

    /**
     * 判断当前服务是否正在运行
     */
    val isRunning: Boolean
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        get() {
            if (mBaseService == null) {
                return false
            }
            val accessibilityManager = mBaseService!!.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
            val info = mBaseService!!.serviceInfo ?: return false
            val list = accessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC)
            val iterator = list.iterator()

            var isConnect = false
            while (iterator.hasNext()) {
                val i = iterator.next()
                if (i.id == info.id) {
                    isConnect = true
                    break
                }
            }

            return if (!isConnect) {
                false
            } else true
        }

    companion object {


        private val WECHAT_PACKAGENAME = "com.tencent.mm"
        //回调处理不同的辅助功能类
        private val ACCESSBILITY_JOBS = arrayOf<Class<*>>()//            LenovoPhoneAccessibility.class, AutoAttentWechatAccessbility.class,
        // XiaomiAccessibility.class

        private var mBaseService: BaseAccessibilityService? = null



    }
}

package com.chen.r1.utils

import android.app.ActivityManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.text.TextUtils
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.chen.r1.R

/**
 * Created by CHEN on 2018/1/9.
 */

object CUtils {
    // 判断是否打开了通知监听权限
    fun isEnabled(context: Context): Boolean {
        val pkgName = context.packageName
        val flat = Settings.Secure.getString(context.contentResolver, "enabled_notification_listeners")
        if (!TextUtils.isEmpty(flat)) {
            val names = flat.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            for (i in names.indices) {
                val cn = ComponentName.unflattenFromString(names[i])
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.packageName)) {
                        return true
                    }
                }
            }
        }
        return false
    }

    /**
     * 打开辅助服务的设置
     */
    fun openAccessibilityServiceSettings(activity: FragmentActivity) {
        try {
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            activity.startActivity(intent)
            Toast.makeText(activity,
                activity.getString(R.string.open_accessibility_tips), Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun isServiceRunning(mContext: Context):Boolean{
        return isServiceRunning(mContext,"com.chen.r1.acessbility.BaseAccessibilityService")
    }


    fun isServiceRunning(mContext: Context, className: String): Boolean {

        var isRunning = false
        val activityManager = mContext
                .getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val serviceList = activityManager
                .getRunningServices(30)

        if (serviceList.size <= 0) {
            return false
        }

        for (i in serviceList.indices) {

            LogUtils.e(serviceList[i].service.className)

            if (serviceList[i].service.className == className) {

                isRunning = true
                break
            }
        }
        return isRunning

    }

}

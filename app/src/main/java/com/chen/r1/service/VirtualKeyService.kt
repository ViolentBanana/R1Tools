package com.chen.r1.service

import android.os.Message
import android.view.accessibility.AccessibilityEvent
import com.chen.r1.acessbility.AccessbilityJob
import com.chen.r1.acessbility.BaseAccessbilityJob
import com.chen.r1.acessbility.BaseAccessibilityService
import com.chen.r1.gesture.GestureWindowHelper
import com.chen.r1.utils.*
import java.util.ArrayList

class VirtualKeyService : BaseAccessbilityJob(), IHandleMessage {
    override fun getTargetPackageName(): String {
        return PACKAGENAME
    }

    override fun onReceiveJob(event: AccessibilityEvent?) {
    }

    override fun onStopJob() {
        GestureWindowHelper.destoryFlowView()
    }

    override fun isEnable(): Boolean {
        return true
    }

    override fun refreshView() {
        // TODO: 处理View
    }

    override fun onCreateJob(service: BaseAccessibilityService?) {
        super.onCreateJob(service)
        LogUtils.e("onCreateJob:" + service!!.serviceInfo.id)
    }



    //检测包名
    private val PACKAGES = arrayOf(
        "com.tencent.mm",
        "com.qc.grabmoney",
        "com.android.packageinstaller",
        "com.lenovo.security",
        "com.samsung.android.packageinstaller",
        "com.miui.securitycenter"
    )

    //回调处理不同的辅助功能类
    private val ACCESSBILITY_JOBS =
        arrayOf<Class<*>>()//            LenovoPhoneAccessibility.class, AutoAttentWechatAccessbility.class,

    // XiaomiAccessibility.class
    private var mAccessbilityJobs: ArrayList<AccessbilityJob> = ArrayList()


    override fun onHandleMessage(msg: Message) {

    }





    fun isRunning(): Boolean {
        return false
    }

    companion object {
//        var service: VirtualKeyService? = null
//            private set
        private val PACKAGENAME = ""


        private val TAG = VirtualKeyService::class.simpleName

    }
}

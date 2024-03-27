package com.chen.r1.app

import android.app.Application
import com.chen.r1.gesture.GestureWindowHelper

/**
 * Created by CHEN on 2018/1/9.
 */

class App : Application() {

    override fun onCreate() {

        super.onCreate()

        instance = this
        GestureWindowHelper.initContext(instance!!)
    }

    companion object {


        var instance: App? = null
            private set
    }
}

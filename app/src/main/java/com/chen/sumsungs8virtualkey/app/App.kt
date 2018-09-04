package com.chen.sumsungs8virtualkey.app

import android.app.Application

/**
 * Created by CHEN on 2018/1/9.
 */

class App : Application() {

    override fun onCreate() {

        super.onCreate()

        instance = this
    }

    companion object {


        var instance: App? = null
            private set
    }
}

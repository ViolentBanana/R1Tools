package com.chen.sumsungs8virtualkey;

import android.app.Application;

/**
 * Created by CHEN on 2018/1/9.
 */

public class App extends Application {


    private static App mInstance;

    @Override
    public void onCreate() {

        super.onCreate();

        mInstance = this;
    }

    public static App getInstance() {
        return mInstance;
    }
}

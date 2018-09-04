package com.chen.sumsungs8virtualkey;

import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 * Created by CHEN on 2017/12/13.
 */
public class WeakRefHandler<T extends IHandleMessage> extends Handler {

    private final WeakReference<T> mTarget;

    WeakRefHandler(T t) {
        mTarget = new WeakReference<T>(t);
    }

    @Override
    public void handleMessage(Message msg) {
        T target = mTarget.get();
        if (target != null) {
            target.onHandleMessage(msg);
        }
    }

    /**
     * 处理请求
     *
     * @param what
     * @param object
     */
    public void handleHttpResult(int what, Object object) {
        Message msg = obtainMessage();
        msg.what = what;
        msg.obj = object;
        sendMessage(msg);
    }
}

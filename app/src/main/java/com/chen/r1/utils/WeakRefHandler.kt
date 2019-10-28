package com.chen.r1.utils

import android.os.Handler
import android.os.Message

import java.lang.ref.WeakReference

/**
 * Created by CHEN on 2017/12/13.
 */
class WeakRefHandler<T : IHandleMessage> internal constructor(t: T) : Handler() {

    private val mTarget: WeakReference<T> = WeakReference(t)

    override fun handleMessage(msg: Message) {
        val target = mTarget.get()
        target?.onHandleMessage(msg)
    }
}

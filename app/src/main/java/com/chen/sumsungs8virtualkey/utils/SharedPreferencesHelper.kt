package com.chen.sumsungs8virtualkey.utils

import android.content.Context

/**
 * Create by CHEN ON 2019/2/11
 */
enum class SharedPreferencesHelper {

    INSTANCE;


    //是否开启震动
    val VIBRATOR = "VIBRATOR"
    //震动强度
    val VIBRATOR_STRENGTH = "VIBRATOR_STRENGTH"
    val LEFT_WIDTH = "LEFT_WIDTH"
    val RIGHT_WIDTH = "RIGHT_WIDTH"


    val NAME = "config"

    //键 值
    fun putInt(mContext: Context, key: String, value: Int): Boolean {
        val sp = mContext.getSharedPreferences(NAME, Context.MODE_PRIVATE)
        return sp.edit().putInt(key, value).commit()
    }


    fun getInt(mContext: Context, key: String, defValue: Int): Int {
        val sp = mContext.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        return sp.getInt(key, defValue);
    }


    //键 值
    fun putBoolean(mContext: Context, key: String, value: Boolean): Boolean {
        val sp = mContext.getSharedPreferences(NAME, Context.MODE_PRIVATE)
        return sp.edit().putBoolean(key, value).commit()
    }


    fun getBoolean(mContext: Context, key: String): Boolean {
        val sp = mContext.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        return sp.getBoolean(key, false);
    }


    //删除单个
    fun deleShare(mContext: Context, key: String): Boolean {
        val sp = mContext.getSharedPreferences(NAME, Context.MODE_PRIVATE)
        return sp.edit().remove(key).commit()
    }

    //删除全部键值对信息
    fun deleAll(mContext: Context): Boolean {
        val sp = mContext.getSharedPreferences(NAME, Context.MODE_PRIVATE)
        return sp.edit().clear().commit()
    }
}
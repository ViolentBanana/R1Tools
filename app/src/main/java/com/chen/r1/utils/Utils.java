package com.chen.r1.utils;

import android.app.Service;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.provider.Settings.Global;
import android.util.Log;

import com.chen.r1.R;
import com.chen.r1.app.App;

import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import kotlin.jvm.internal.Intrinsics;

/**
 * Create by CHEN ON 2018/9/21
 */
public class Utils {

    public static float dp2px(Resources resources, float dp) {
        final float scale = resources.getDisplayMetrics().density;
        return dp * scale + 0.5f;
    }

    public static void vibrator(Context context, int milliseconds) {
        if (milliseconds > 0) {

            Vibrator vib = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
            if (vib != null) {
                vib.vibrate(milliseconds);
            }

        }
    }

    public static boolean clearBlackNav(@NotNull Context context) {
        Intrinsics.checkParameterIsNotNull(context, "context");

        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return Global.putString(context.getContentResolver(), "navigationbar_use_theme_default", defaultSharedPreferences.getString("navigationbar_use_theme_default", null)) | (Global.putString(context.getContentResolver(), "navigationbar_color", defaultSharedPreferences.getString("navigationbar_color", null)) | Global.putString(context.getContentResolver(), "navigationbar_current_color", defaultSharedPreferences.getString("navigationbar_current_color", null)));
    }

    public static boolean forceTouchWizNavEnabled(@NotNull Context context) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        return Settings.Global.putInt(context.getContentResolver(), "navigationbar_hide_bar_enabled", 1);
    }


    public static String execRootCmd(String cmd) {
        String result = "";
        DataOutputStream dos = null;
        DataInputStream dis = null;

        try {
            Process p = Runtime.getRuntime().exec("sh");// 经过Root处理的android系统即有su命令
            dos = new DataOutputStream(p.getOutputStream());
            dis = new DataInputStream(p.getInputStream());

            dos.writeBytes(cmd + "\n");
            dos.flush();
            dos.writeBytes("exit\n");
            dos.flush();
            String line = null;
            while ((line = dis.readLine()) != null) {
                Log.d("result", line);
                LogUtils.INSTANCE.e("result" + line);
                result += line;
            }
            LogUtils.INSTANCE.e("result" + line);
            p.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (dos != null) {
                try {
                    dos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (dis != null) {
                try {
                    dis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

}

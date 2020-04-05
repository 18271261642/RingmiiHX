package com.guider.libbase.util;

import com.guider.libbase.BuildConfig;

public class Log {
    public static void v(String tag, String msg) {
        if (BuildConfig.DEBUG)
            android.util.Log.v(tag, msg);
    }

    public static void d(String tag, String msg) {
        if (BuildConfig.DEBUG)
            android.util.Log.d(tag, msg);
    }

    public static void i(String tag, String msg) {
        android.util.Log.d(tag, msg);
    }

    public static void w(String tag, String msg) {
        android.util.Log.d(tag, msg);
    }

    public static void e(String tag, String msg) {
        android.util.Log.d(tag, msg);
    }
}

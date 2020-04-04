package com.guider.libbase.util;

import android.content.Context;

public class AppPath {
    public static String getPath(Context context, String childPath) {
        return context.getCacheDir().getPath() + childPath;
    }
}

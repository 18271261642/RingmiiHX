package com.tc.keyboard;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class MyViewUtils {
    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);

        Configuration mConfiguration = context.getResources().getConfiguration(); //获取设置的配置信息
        int ori = mConfiguration.orientation; //获取屏幕方向

        return outMetrics.widthPixels + ori == mConfiguration.ORIENTATION_LANDSCAPE ? getNavigationBarHeight(context) : 0;
    }

    public static int getNavigationBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId=resources.getIdentifier("navigation_bar_height","dimen","android");
        int height = resources.getDimensionPixelSize(resourceId);
        return height;
    }
}

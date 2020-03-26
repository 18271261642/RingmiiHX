package com.guider.healthring;

public class CustomMade {
    public static int  getSmallLogo() {
        int id = 0;
        if (BuildConfig.FLAVOR.equals("app_vigori_mate")) {
            id = R.drawable.ic_vigori_icon_small;
        }

        return id;
    }

    public static int  getBigLogo() {
        int id = 0;
        if (BuildConfig.FLAVOR.equals("app_vigori_mate")) {
            id = R.drawable.ic_vigori_icon;
        }

        return id;
    }
}

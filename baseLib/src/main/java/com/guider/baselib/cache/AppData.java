package com.guider.baselib.cache;

import com.raizlabs.android.dbflow.annotation.Database;

@Database(name = AppData.NAME,version = AppData.VERSION)
public class AppData {
    public static final String NAME="DataTmp";
    public static final int VERSION=1;
}

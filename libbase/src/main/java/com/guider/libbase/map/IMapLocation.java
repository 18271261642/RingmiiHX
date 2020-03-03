package com.guider.libbase.map;

import android.content.Context;

public interface IMapLocation {
    boolean start(Context context, int maxTryCnt, IOnLocation iOnLocation);
    void stop();

    /**
     * 获得当前经度
     * @return
     */
    double getCurrLng();

    /**
     * 获得当前维度
     * @return
     */
    double getCurrLat();

    String getAddr();
}

package com.guider.libbase.map;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 跑步
 */
public interface IMapRun extends IMapBase {
    // Google地图专用
    void init(Activity context, int rcId, FragmentManager fragmentManager, ISpportMapView iSpportMapView);
    // 高德地图专用
    void init(Activity context, View view, ISpportMapView iSpportMapView);
    void initMap();

    void start();
    boolean isStart();
    void stop();
    void onDestroy();
    void onSaveInstanceState(Bundle outState);

    void conn();
    void disconn();
    /**
     * 处理最后一个点
     */
    void handleLastPoint();

    /**
     * 点记录数量
     * @return
     */
    int getPointSize();

    /**
     * 获取总距离
     * @return
     */
    double getDistance();
}

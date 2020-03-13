package com.guider.libbase.map;

import java.util.Date;

public interface ISpportMapView {
    void setDistance(double distance);
    void setTime(long time);

    /**
     * 卡路里
     */
    void setCal(double distance);
    /**
     * 速度
     */
    void setSpeed(double distance);

    /**
     * 配速
     */
    void setPace(float pace);

    /**
     * 定位是否开始
     * @return
     */
    boolean isStart();

    void putTime(String name, Date date, boolean isFirst);

    /**
     * 添加一次经纬度
     * @param lng
     * @param lat
     */
    void addPoint(double lng, double lat);

    boolean requestPermission();
}

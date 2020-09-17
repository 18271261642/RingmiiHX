package com.guider.baselib.utils;

/**
 * Created by apple on 2017/11/8.
 */

public class MapPosition {

    private double lat;
    private double lon;

    public MapPosition(double lat, double lon) {
        setLat(lat);
        setLon(lon);
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    @Override
    public String toString() {
        return lat + "," + lon;
    }

}

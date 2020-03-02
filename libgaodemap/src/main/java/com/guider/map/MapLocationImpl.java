package com.guider.map;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.guider.health.common.utils.StringUtil;
import com.guider.libbase.map.IMapLocation;
import com.guider.libbase.map.IOnLocation;

public class MapLocationImpl implements IMapLocation {
    private final String TAG = "MapLocationImpl";
    private Context mContext;
    // 声明mlocationClient对象
    public AMapLocationClient mlocationClient;
    // 声明mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;

    private double mLng;
    private double mLat;
    private String mAddr;
    private int mMaxTryCnt = 1;
    private int mTryCnt;

    private IOnLocation mIOnLocation;

    @Override
    public boolean start(Context context, int maxTryCnt, IOnLocation iOnLocation) {
        mContext = context;
        mIOnLocation = iOnLocation;
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }

        mlocationClient = new AMapLocationClient(mContext);
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位监听
        mlocationClient.setLocationListener(aMapLocationListener);

        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(3 * 1000);
        //设置定位参数
        mlocationClient.setLocationOption(mLocationOption);
        // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
        // 注意设置合适的定位时间的间隔（最小间隔支持为1000ms），并且在合适时间调用stopLocation()方法来取消定位请求
        // 在定位结束后，在合适的生命周期调用onDestroy()方法
        // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
        //启动定位
        mlocationClient.startLocation();

        Log.i(TAG, "startLocation");
        mTryCnt = 0;
        mMaxTryCnt = maxTryCnt;
        return true;
    }

    AMapLocationListener aMapLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation amapLocation) {
            Log.i(TAG, "onLocationChanged" + (amapLocation == null));
            if (amapLocation == null) return;
            if (amapLocation.getErrorCode() != 0) {
                // 显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e("AmapError", "location Error, ErrCode:" + amapLocation.getErrorCode() + ", errInfo:" + amapLocation.getErrorInfo());
            }

            if (mTryCnt++ >= mMaxTryCnt - 1) {
                mTryCnt = 0;
                stop();
            }

            double latitude = amapLocation.getLatitude();
            double longitude = amapLocation.getLongitude();

            String dizhi = handle(amapLocation.getCountry())//国
                    + " " + handle(amapLocation.getProvince())//省
                    + " " + handle(amapLocation.getCity())//城
                    + " " + handle(amapLocation.getDistrict())//区
                    + " " + handle(amapLocation.getStreet())//街
                    + " " + handle(amapLocation.getStreetNum());//街道门牌号信息
            if (StringUtil.isEmpty(dizhi)) dizhi = amapLocation.getAddress();

            mAddr = dizhi;
            mLng = longitude;
            mLat = latitude;
            mTryCnt++;

            Log.i(TAG, dizhi);
            if (mIOnLocation != null) mIOnLocation.onLocation(mLng, mLat, mAddr);
        }
    };

    @Override
    public void stop() {
        if (mlocationClient != null) {
            Log.i(TAG, "stop");
            mTryCnt = 0;
            mlocationClient.stopLocation();//停止定位后，本地定位服务并不会被销毁
            mlocationClient.onDestroy();//销毁定位客户端，同时销毁本地定位服务。
            mlocationClient = null;
        }
    }

    private String handle(String addr) {
        return addr == null ? "" : addr;
    }

    @Override
    public double getCurrLng() {
        return mLng;
    }

    @Override
    public double getCurrLat() {
        return mLat;
    }

    @Override
    public String getAddr() {
        return mAddr;
    }
}

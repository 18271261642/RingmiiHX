package com.guider.map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.FragmentManager;
import android.util.Log;
import android.view.View;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.CoordinateConverter;
import com.amap.api.location.DPoint;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.PolylineOptions;
import com.guider.libbase.map.IMapRun;
import com.guider.libbase.map.ISpportMapView;
import com.guider.libgaodemap.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MapRunImpl extends MapBaseImpl implements IMapRun, AMapLocationListener, LocationSource {
    private final String TAG = "MapRunImpl";

    private AMapLocationClientOption mLocationOption = null;
    private MyLocationStyle myLocationStyle;
    private ArrayList<LatLng> mLocationList = new ArrayList<>();// 定位坐标集合
    private AMapLocationClient mLocationClient = null;
    private OnLocationChangedListener mListener;

    // 以前的定位点
    private LatLng oldLatLng;
    // 现在定位点
    private LatLng curLatLng;
    // 是否是第一次定位
    private boolean isFirstLatLng = true;
    private double mLocatinLat;
    private double mLocationLon;

    // 开始结束标志
    private Marker starPerth, endPerth;

    private ISpportMapView mISpportMapView;

    // 一堆中间变量
    private double gpsDistance = 0.0;//距离
    private boolean mOver = false;
    private double currLength;
    private double mBestLat;
    private double mBestLon;
    private long lastTime = 0;
    private long currTime = 0;
    private long minusTime;
    private int errorCnt = 0;
    private LatLng currLa;
    private LatLng lastLa = new LatLng(0, 0);
    private LatLng overLa = new LatLng(0, 0);

    @Deprecated
    @Override
    public void init(Activity context, int rcId, FragmentManager fragmentManager, ISpportMapView iSpportMapView) {

    }

    @Override
    public void init(Activity context, View view, ISpportMapView iSpportMapView) {
        super.init(context, view);
        mISpportMapView = iSpportMapView;
    }

    public void initMap() {
        // 定位
        mAMap.setLocationSource(this);// 设置定位监听
        UiSettings mUiSettings = mAMap.getUiSettings();
        mUiSettings.setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
        myLocationStyle = new MyLocationStyle();
        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.ic_gps_point);
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW_NO_CENTER)//定位、但不会移动到地图中心点，并且会跟随设备移动。
                .showMyLocation(true)//是否显示小蓝点
                .strokeColor(Color.TRANSPARENT)
                .radiusFillColor(Color.TRANSPARENT)
                .radiusFillColor(Color.parseColor("#3641b692"))
                .myLocationIcon(bitmap)
                .interval(1000);//定位时间间隔1秒
        mAMap.setMyLocationStyle(myLocationStyle);

        //画线
        // 缩放级别（zoom）：地图缩放级别范围为【4-20级】，值越大地图越详细
        mAMap.moveCamera(CameraUpdateFactory.zoomTo(18));
        //使用 mAMap.setMapTextZIndex(2) 可以将地图底图文字设置在添加的覆盖物之上
        mAMap.setMapTextZIndex(2);
        AMapOptions options = new AMapOptions();
        options.scrollGesturesEnabled(true);
        mAMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
    }

    @Override
    public boolean isStart() {
        return mLocationClient != null && mLocationClient.isStarted();
    }

    @Override
    public void start() {
        if (mLocationClient == null) return;
        mLocationClient.startLocation();
    }

    @Override
    public void stop() {
        if (mLocationClient == null) return;
        mLocationClient.stopLocation();
    }

    @Override
    public void onDestroy() {
        if (mMapView != null) {
            // 在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
            mMapView.onDestroy();
        }
        if (null != mLocationClient) {
            // 销毁定位客户端，同时销毁本地定位服务。
            mLocationClient.onDestroy();
        }
        if (mMapView != null) {
            mMapView = null;
        }
        if (starPerth != null) {
            starPerth.remove();
            starPerth = null;
        }
        if (endPerth != null) {
            endPerth.remove();
            endPerth = null;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void conn() {

    }

    @Override
    public void disconn() {

    }

    @Override
    public void handleLastPoint() {
        if (mLocationList.get(mLocationList.size() - 1).longitude != 0 && mLocationList.get(mLocationList.size() - 1).latitude != 0) {
            addPointLins(mLocationList.get(mLocationList.size() - 2), mLocationList.get(mLocationList.size() - 1));
            endDisplayPerth(mLocationList.get(mLocationList.size() - 1));// 添加结束点
        }
    }

    @Override
    public int getPointSize() {
        return mLocationList == null ? 0 : mLocationList.size();
    }

    @Override
    public double getDistance() {
        return gpsDistance;
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                // 可在其中解析amapLocation获取相应内容。
                if (mListener != null) {
                    mListener.onLocationChanged(aMapLocation);// 显示系统小蓝点
                }
                mLocatinLat = aMapLocation.getLatitude();
                mLocationLon = aMapLocation.getLongitude();

                // 设置显示地图时间
                mISpportMapView.setTime(aMapLocation.getTime());

                if (mISpportMapView.isStart()) {
                    // 判断是否是第一次定位
                    if (isFirstLatLng) {
                        isFirstLatLng = false;
                        //移动到定位点中心，并且缩放级别为18
                        mAMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLocatinLat, mLocationLon), 18));
                        if (mLocationClient.isStarted()) {
                            // 添加起点
                            startDisplayPerth(new LatLng(mLocatinLat, mLocationLon));
                            mISpportMapView.addPoint(mLocationLon, mLocatinLat);
                        }
                        mLocationList.add(new LatLng(mLocatinLat, mLocationLon));

                    } else {
                        if (oldLatLng == null) {
                            oldLatLng = new LatLng(mLocatinLat, mLocationLon);
                        } else {
                            float speed = aMapLocation.getSpeed();
                            if (mISpportMapView.isStart()) {
                                findBest(speed);
                            }
                        }
                    }
                } else {
                    mAMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLocatinLat, mLocationLon), 18));
                }
            } else {
                // 定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                Log.e(TAG, "location Error, ErrCode:" + aMapLocation.getErrorCode() + ", errInfo:" + aMapLocation.getErrorInfo());
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void findBest(float speed) {
        currLa = new LatLng(mLocatinLat, mLocationLon);
        currTime = System.currentTimeMillis();
        // .e(TAG, "test walk la is" + currLa + "");
        // Log.e(TAG, "test walk last is" + lastLa + "");
        currLength = AMapUtils.calculateLineDistance(lastLa, currLa);

        if (!lastLa.equals(currLa)) {
            minusTime = currTime - lastTime;
            if (currLength < ((minusTime + 1) / 1000) * 5) {
                errorCnt = 0;
                lastLa = currLa;
                lastTime = currTime;

                mBestLat = mLocatinLat;
                mBestLon = mLocationLon;
                curLatLng = new LatLng(mBestLat, mBestLon);
                //Log.e(TAG, "发>>>>>>>>>" + currLength);
                mLocationList.add(curLatLng);
                if (mLocationList.size() >= 2) {
                    DPoint dPoint1 = new DPoint(mLocationList.get(mLocationList.size() - 2).latitude, mLocationList.get(mLocationList.size() - 2).longitude);
                    DPoint dPoint2 = new DPoint(mLocationList.get(mLocationList.size() - 1).latitude, mLocationList.get(mLocationList.size() - 1).longitude);
                    gpsDistance += CoordinateConverter.calculateLineDistance(dPoint1, dPoint2) * 0.001;
                    //添加线
                    addPointLins(mLocationList.get(mLocationList.size() - 2), mLocationList.get(mLocationList.size() - 1));

                    mISpportMapView.addPoint(mLocationLon, mLocatinLat);
                }
                DecimalFormat decimalFormat = new DecimalFormat("######0.00");
                // 距离
                mISpportMapView.setDistance(gpsDistance);
                // 时速
                mISpportMapView.setSpeed(gpsDistance);
                // 配速
                mISpportMapView.setPace(speed);
                // 卡路里
                mISpportMapView.setCal(gpsDistance);
            } else if (minusTime >= 20000) {
                if (mOver) {

                    if (!overLa.equals(currLa)) {
                        errorCnt = 0;
                        lastLa = currLa;
                        lastTime = currTime;
                        //Log.e(TAG, " 确定大于距离>>>>>>>>>" + currLength);

                        mBestLat = mLocatinLat;
                        mBestLon = mLocationLon;

                        curLatLng = new LatLng(mBestLat, mBestLon);
                        mLocationList.add(curLatLng);
                        mOver = false;
                    } else {
                        errorCnt = 0;
                        lastTime = currTime;
                        mOver = false;
                    }
                } else {
                    if (currLength > ((minusTime + 1) / 1000) * 5) {
                        mOver = true;
                        overLa = currLa;
                        // Log.e(TAG, " 第一次大于距离" + currLength);
                    }
                }
            } else {
                errorCnt++;
                // Log.e(TAG, " +++++++++++++++++++++++++++++++++++++++++++++距离太大，是漂移，不发" + currLength + "定位是" + mLocatinLat + "^^^" + mLocationLon);
            }
        } else {
            // Log.e(TAG, " -------------------------------------------------距离太小，没有移动，不发");
            lastTime = currTime;
        }
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
        if (mLocationClient == null) {
            mLocationClient = new AMapLocationClient(mContext);
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mLocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置定位参数
            mLocationClient.setLocationOption(mLocationOption);
            mLocationOption.setOnceLocation(false);
            /**
             * 设置是否优先返回GPS定位结果，如果30秒内GPS没有返回定位结果则进行网络定位
             * 注意：只有在高精度模式下的单次定位有效，其他方式无效
             */
            mLocationOption.setGpsFirst(true);
            if (mLocationClient != null) {
                mLocationClient.startLocation();

            }
        }
    }

    @Override
    public void deactivate() {

    }

    /**
     * 绘制线 两点一线
     *
     * @param oldPointLatLng
     * @param newPointLatLng
     */
    void addPointLins(LatLng oldPointLatLng, LatLng newPointLatLng) {
        mAMap.addPolyline(new PolylineOptions()
                .color(Color.RED)
                .width(10f)
                .geodesic(true)
                .add(oldPointLatLng, newPointLatLng));
    }

    /**
     * 添加 开始 标记
     */
    private void startDisplayPerth(LatLng latLng) {
        // 每一次打点第一个的时候就是定位开始的时候
        // 定位开始的时间
        try {
            // 得到开始时间
            mISpportMapView.putTime("startTime", Calendar.getInstance().getTime(), true);
        } catch (Exception E) {
            E.printStackTrace();
        }

        if (starPerth == null) {
            BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.ic_start_pistion);
            starPerth = mAMap.addMarker(new MarkerOptions().draggable(false).icon(bitmap).position(latLng));//.title("start")
            starPerth.setDraggable(false); //设置不可移动
        }
    }

    /**
     * 添加 结束标记
     */
    private void endDisplayPerth(LatLng latLng) {
        if (endPerth == null) {
            BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.ic_end_pistion);
            endPerth = mAMap.addMarker(new MarkerOptions().draggable(false).icon(bitmap).position(latLng));//.title("end")
            endPerth.setDraggable(false); //设置不可移动
        }
    }
}

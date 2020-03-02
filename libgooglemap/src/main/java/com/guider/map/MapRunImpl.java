package com.guider.map;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;
import com.guider.libbase.map.IMapRun;
import com.guider.libbase.map.ISpportMapView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MapRunImpl extends MapBaseImpl implements IMapRun, OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleMap.OnMarkerDragListener,
        GoogleApiClient.OnConnectionFailedListener {
    private final String TAG = "MapRunImpl";

    private ArrayList<LatLng> mLocationList = new ArrayList<>();// 定位坐标集合
    private GoogleApiClient mGoogleApiClient;
    // private AMapLocationClient mLocationClient = null;
    // private OnLocationChangedListener mListener;

    private Location mLastLocation;
    private AddressResultReceiver mResultReceiver;
    /**
     * 用来判断用户在连接上Google Play services之前是否有请求地址的操作
     */
    private boolean mAddressRequested = false;
    private LocationRequest mLocationRequest;

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

    private LatLng perthLatLng;

    private boolean isStart = false;

    // Google地图专用
    public void init(Activity context, int rcId, FragmentManager fragmentManager, ISpportMapView iSpportMapView) {
        // super.init(context, null);
        mContext = context;
        mISpportMapView = iSpportMapView;

        // 接收FetchAddressIntentService返回的结果
        mResultReceiver = new AddressResultReceiver(new Handler());
        // 创建GoogleAPIClient实例
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        // 取得SupportMapFragment,并在地图准备好后调用onMapReady
        SupportMapFragment mapFragment = (SupportMapFragment) fragmentManager.findFragmentById(rcId);
        mapFragment.getMapAsync(this);
    }

    @Deprecated
    @Override
    public void init(Activity context, View view, ISpportMapView iSpportMapView) {

    }

    @Override
    public void initMap() {
    }

    @Override
    public boolean isStart() {
        return isStart;
    }

    @Override
    public void start() {
        isStart = true;
        startLocationUpdates();
    }

    /**
     * 启动地址搜索Service
     */
    protected void startIntentService(LatLng latLng) {
        Intent intent = new Intent(mContext, FetchAddressIntentService.class);
        intent.putExtra(FetchAddressIntentService.RECEIVER, mResultReceiver);
        intent.putExtra(FetchAddressIntentService.LATLNG_DATA_EXTRA, latLng);
        mContext.startService(intent);
    }

    @Override
    public void stop() {
        isStart = false;
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, locationListener);
    }

    @Override
    public void onDestroy() {
        if (mMapView != null) {
            // 在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
            mMapView.onDestroy();
            mMapView = null;
        }
        if (mGoogleMap != null) {
            mGoogleMap.clear();
            mGoogleMap = null;
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
        if (mMapView != null)
            mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void conn() {
        if (mGoogleApiClient != null) mGoogleApiClient.connect();// 连接Google Play服务
    }

    @Override
    public void disconn() {
        if (mGoogleApiClient != null) mGoogleApiClient.disconnect();// 断开连接Google Play服务
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

    @SuppressLint("SetTextI18n")
    private void findBest(float speed) {
        currLa = new LatLng(mLocatinLat, mLocationLon);
        currTime = System.currentTimeMillis();
        // Log.e(TAG, "test walk la is" + currLa + "");
        // Log.e(TAG, "test walk last is" + lastLa + "");
        currLength = calculateLineDistance(lastLa, currLa);

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
                if (mLocationList == null) mLocationList = new ArrayList<>();
                mLocationList.add(curLatLng);
                if (mLocationList.size() >= 2) {
                    gpsDistance += SphericalUtil.computeDistanceBetween(mLocationList.get(mLocationList.size() - 2), mLocationList.get(mLocationList.size() - 1)) * 0.001;
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

    /**
     * 地图两点之间的距离的算法
     *
     * @param var0
     * @param var1
     * @return
     */
    public float calculateLineDistance(LatLng var0, LatLng var1) {
        if (var0 != null && var1 != null) {
            try {
                double var2 = 0.01745329251994329D;
                double var4 = var0.longitude;
                double var6 = var0.latitude;
                double var8 = var1.longitude;
                double var10 = var1.latitude;
                var4 *= 0.01745329251994329D;
                var6 *= 0.01745329251994329D;
                var8 *= 0.01745329251994329D;
                var10 *= 0.01745329251994329D;
                double var12 = Math.sin(var4);
                double var14 = Math.sin(var6);
                double var16 = Math.cos(var4);
                double var18 = Math.cos(var6);
                double var20 = Math.sin(var8);
                double var22 = Math.sin(var10);
                double var24 = Math.cos(var8);
                double var26 = Math.cos(var10);
                double[] var28 = new double[3];
                double[] var29 = new double[3];
                var28[0] = var18 * var16;
                var28[1] = var18 * var12;
                var28[2] = var14;
                var29[0] = var26 * var24;
                var29[1] = var26 * var20;
                var29[2] = var22;
                double var30 = Math.sqrt((var28[0] - var29[0]) * (var28[0] - var29[0]) + (var28[1] - var29[1]) * (var28[1] - var29[1]) + (var28[2] - var29[2]) * (var28[2] - var29[2]));
                return (float) (Math.asin(var30 / 2.0D) * 1.27420015798544E7D);
            } catch (Throwable var32) {
                var32.printStackTrace();
                return 0.0F;
            }
        } else {
            try {
                throw new Exception("非法坐标值");
            } catch (Exception var33) {
                var33.printStackTrace();
                return 0.0F;
            }
        }
    }

    /**
     * 绘制线 两点一线
     *
     * @param oldPointLatLng
     * @param newPointLatLng
     */
    void addPointLins(LatLng oldPointLatLng, LatLng newPointLatLng) {
        mGoogleMap.addPolyline(new PolylineOptions()
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
            starPerth = mGoogleMap.addMarker(new MarkerOptions().draggable(false).icon(bitmap).position(latLng));//.title("start")
            starPerth.setDraggable(false); //设置不可移动
        }
    }

    /**
     * 添加 结束标记
     */
    private void endDisplayPerth(LatLng latLng) {
        if (endPerth == null) {
            BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.ic_end_pistion);
            endPerth = mGoogleMap.addMarker(new MarkerOptions().draggable(false).icon(bitmap).position(latLng));
            endPerth.setDraggable(false); //设置不可移动
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (!mISpportMapView.requestPermission())
            return;
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        //Log.i(TA G, "--onConnected-经纬度-" + mLastLocation.toString());
        if (mLastLocation != null) {
            initCamera(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));//将地图视角切换到定位的位置
            if (!Geocoder.isPresent()) {
                // Toast.makeText(this, "No geocoder available", Toast.LENGTH_LONG).show();
                return;
            }
            // 启动位置更新
            startLocationUpdates();
            if (!mAddressRequested) {
                startIntentService(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
                mAddressRequested = true;
            }
        }
    }

    /**
     * 将地图视角切换到定位的位置
     */
    private void initCamera(final LatLng sydney) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(800);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mGoogleMap != null)
                            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 13.0f));
                    }
                });
            }
        }).start();
    }

    /**
     * 启用定位更新
     */
    protected void startLocationUpdates() {
        if (!mISpportMapView.requestPermission())
            return;
        if (mGoogleApiClient.isConnected())
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, locationListener);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        perthLatLng = marker.getPosition();
        startIntentService(perthLatLng);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                Log.i(TAG, "GoogleMap loadesd");
            }
        });
        mGoogleMap.setOnMarkerDragListener(this);
        UiSettings uiSettings = mGoogleMap.getUiSettings();
        uiSettings.setMyLocationButtonEnabled(false);//不显示定位按钮
        uiSettings.setCompassEnabled(false);//设置是否显示指南针
        mGoogleMap.setTrafficEnabled(true);
        mGoogleMap.setMinZoomPreference(6f);
        mGoogleMap.setMaxZoomPreference(15f);
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        enableMyLocation();
    }

    /**
     * 如果取得了权限,显示地图定位层
     */
    private void enableMyLocation() {
        if (!mISpportMapView.requestPermission())
            return;
        if (mGoogleMap != null) {
            // Access to the location has been granted to the app.
            mGoogleMap.setMyLocationEnabled(true);
        }
    }

    class AddressResultReceiver extends ResultReceiver {
        private String mAddressOutput;

        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            mAddressOutput = resultData.getString(FetchAddressIntentService.RESULT_DATA_KEY);
            if (resultCode == FetchAddressIntentService.SUCCESS_RESULT) {
            }
        }
    }

    /**
     * 位置监听
     */
    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            try {
                // 精确度越小越准，单位：米
                if (location.getAccuracy() > 100) {
                    return;
                }
                mLastLocation = location;
                updateUI();//更新UI
            } catch (Error error) {

            }
        }
    };

    /**
     * 更新UI
     */
    @SuppressLint("SetTextI18n")
    private void updateUI() {
        if (mISpportMapView.isStart()) {
            if (mLastLocation == null) return;
            mLocatinLat = mLastLocation.getLatitude();
            mLocationLon = mLastLocation.getLongitude();

            // 设置显示地图时间
            mISpportMapView.setTime(mLastLocation.getTime());

            if (mISpportMapView.isStart()) {
                // 判断是否是第一次定位
                if (isFirstLatLng) {
                    isFirstLatLng = false;
                    // 移动到定位点中心，并且缩放级别为18
                    initCamera(new LatLng(mLocatinLat, mLocationLon));
                    // 添加起点
                    startDisplayPerth(new LatLng(mLocatinLat, mLocationLon));
                    mLocationList.add(new LatLng(mLocatinLat, mLocationLon));

                    mISpportMapView.addPoint(mLocationLon, mLocatinLat);
                } else {
                    if (oldLatLng == null) {
                        oldLatLng = new LatLng(mLocatinLat, mLocationLon);
                    } else {
                        // Log.e(TAG, mLastLocation.getLatitude() + "====" + mLastLocation.getLongitude());
                        float speed = mLastLocation.getSpeed();
                        // 移动到定位点中心，并且缩放级别为18
                        initCamera(new LatLng(mLocatinLat, mLocationLon));
                        if (mISpportMapView.isStart()) {
                            findBest(speed);
                        }
                    }
                }
            } else {
                initCamera(new LatLng(mLocatinLat, mLocationLon));
            }
        }

        // 获取地图返回的时间戳转换成时间设置显示
        mISpportMapView.setTime(mLastLocation.getTime());
    }
}

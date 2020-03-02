package com.guider.healthring.bzlmaps;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afa.tourism.greendao.gen.SportMapsDao;
import com.google.gson.Gson;
import com.guider.healthring.Commont;
import com.guider.healthring.MyApp;
import com.guider.healthring.R;
import com.guider.healthring.bleutil.MyCommandManager;
import com.guider.healthring.bzlmaps.mapdb.LatLonBean;
import com.guider.healthring.bzlmaps.mapdb.SportMaps;
import com.guider.healthring.siswatch.utils.WatchUtils;
import com.guider.healthring.util.SharedPreferencesUtils;
import com.guider.healthring.util.ToastUtil;
import com.guider.libbase.map.IMapRun;
import com.guider.libbase.map.ISpportMapView;
import com.guider.map.MapRunImpl;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SportsMapActivity extends FragmentActivity implements ISpportMapView, ActivityCompat.OnRequestPermissionsResultCallback,
        Chronometer.OnChronometerTickListener, BzlDragView.BzlDragViewListenter {
    private final String TAG = "-------GPS>>>";

    private final int MY_PERMISSION_REQUEST_CODE = 1;
    private boolean isPermit = true;//是否允许权限

    private TextView textTimeData = null;
    private Chronometer sportTime = null;
    private TextView sportDistance = null;//运动距离---KM
    private TextView sportShisu = null;//运动时速---KM/H
    private TextView sportSpeed = null;//运动配速
    private TextView sportKcal = null;//运动卡路里
    private TextView timeDowln = null;//倒计时
    private FrameLayout timr_frame;
    private int times = 0;//运动计时 s
    protected BzlDragView bzlDragView;
    private TextView sport_distance_unti, util_shisu;//单位  km--ft=====km/h---FT/h

    // 成员变量mToast
    private Toast mToast;

    List gpsMaps = new ArrayList();
    com.alibaba.fastjson.JSONObject gpsJSon;
    SportMaps sportMaps = new SportMaps();
    LatLonBean latLonBean = new LatLonBean();

    MyCountTimer myCountTimer = null;

    protected IMapRun mIMapRun;

    protected int getLayoutRcId() {
        return 0;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPermission();
        if (getLayoutRcId() > 0)
            setContentView(getLayoutRcId());

        mIMapRun = new MapRunImpl();
        // 取得SupportMapFragment,并在地图准备好后调用onMapReady
        Fragment mapFragment = (Fragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mIMapRun.init(this, R.id.map, getSupportFragmentManager(), this);
        } else {
            mIMapRun.init(this, findViewById(R.id.map), this);
        }

        initView();
    }

    private void initView() {
        bzlDragView = new BzlDragView(SportsMapActivity.this, getResources().getString(R.string.star));
        bzlDragView.setBzlDragViewListenter(this);
        bzlDragView.setIsDrag(false);
        textTimeData = findViewById(R.id.text_time_data);
        sportTime = findViewById(R.id.sport_times);

        sportDistance = findViewById(R.id.sport_distance);
        sportShisu = findViewById(R.id.sport_shisu);
        sportSpeed = findViewById(R.id.sport_speed);
        sportKcal = findViewById(R.id.sport_kcal);
        timeDowln = findViewById(R.id.time_dowln);
        timr_frame = findViewById(R.id.timr_frame);
        sport_distance_unti = findViewById(R.id.sport_distance_unti);
        util_shisu = findViewById(R.id.util_shisu);
        boolean w30sunit = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISSystem, true);//是否为公制
        int h9_step_util = (int) SharedPreferencesUtils.getParam(MyApp.getContext(), "H9_UTIT", 0);
        if ((!TextUtils.isEmpty(MyCommandManager.DEVICENAME) && MyCommandManager.DEVICENAME.equals("H9")) ||
                !TextUtils.isEmpty(MyCommandManager.DEVICENAME) && MyCommandManager.DEVICENAME.equals("W06X")) {
            // 0位公制 1为英制
            if (h9_step_util == 0) {
                util_shisu.setText("km/h");
                sport_distance_unti.setText("KM");
            } else {
                util_shisu.setText("ft/h");
                sport_distance_unti.setText("MI");
            }

        } else {
            if (w30sunit) {
                util_shisu.setText("km/h");
                sport_distance_unti.setText("KM");
            } else {
                util_shisu.setText("ft/h");
                sport_distance_unti.setText("FT");
            }
        }
        if (myCountTimer == null) {
            myCountTimer = new MyCountTimer(4000, 1000, timeDowln, getResources().getString(R.string.string_sport_run));
            // 倒计时监听
            myCountTimer.setmCompletionTime(completionTime);
        }
    }


    /***
     * *****************************************************************权限
     */
    private void checkPermission() {
        /**
         * 第 1 步: 检查是否有相应的权限
         */
        checkPermissionAllGranted(
                new String[]{
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                }
        );
        // 如果这些权限全都拥有;

    }

    /**
     * 检查是否拥有指定的所有权限
     *
     * @param permissions
     * @return
     */
    private void checkPermissionAllGranted(String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                // 只要有一个权限没有被授予, 则直接返回 false
                isPermit = false;
                //进入到这里代表没有权限.
                ActivityCompat.requestPermissions(this, new String[]{permission}, MY_PERMISSION_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    for (int grantResult :
                            grantResults) {
                        if (grantResult != PackageManager.PERMISSION_GRANTED) {
                            ToastUtil.showShort(SportsMapActivity.this, getResources().getString(R.string.shouquanshib));
                            isPermit = false;
                        } else {
                            ToastUtil.showShort(SportsMapActivity.this, getResources().getString(R.string.shouquancg));
                            isPermit = true;
                        }
                        return;
                    }
                }
                break;
        }
    }


    /**
     * 初始化
     */
    private void init() {
        // 地图初始化
        mIMapRun.initMap();

        gpsJSon = new com.alibaba.fastjson.JSONObject();
        /**查询当前的运动类型*/
        try {
            int type = 1;
            if (!TextUtils.isEmpty((String) SharedPreferencesUtils.readObject(SportsMapActivity.this, "type"))) {
                if ("0".equals(SharedPreferencesUtils.readObject(SportsMapActivity.this, "type"))) {
                    type = 0;
                }
                gpsJSon.put("type", type);
                sportMaps.setType(type);
                latLonBean.setType(type);
            } else {
                gpsJSon.put("type", 0);
                sportMaps.setType(0);
            }
        } catch (Exception E) {
            E.printStackTrace();
        }
    }


    private boolean isStart = false;// 是否开始定位
    protected boolean isEnd = false;// 是否结束定位

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mIMapRun.onResume();
        if (isPermit) {
            init();
        }
        mIMapRun.start();

        if (bzlDragView != null) {
            if (!isEnd) bzlDragView.showDragCallView();
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        /*
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }

         */
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mIMapRun.onPause();
        if (bzlDragView != null) {
            bzlDragView.hideDragCallView();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        // mMapView.onSaveInstanceState(outState);
        mIMapRun.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mIMapRun.onDestroy();

        if (gpsMaps != null) {
            gpsMaps.clear();
            gpsMaps = null;
        }
        if (gpsJSon != null) {
            gpsJSon.clear();
            gpsJSon = null;
        }
    }

    /**
     * 计时监听
     *
     * @param chronometer
     */
    @Override
    public void onChronometerTick(Chronometer chronometer) {
        if (mIMapRun.isStart()) {
            times++;
            if (sportTime != null) sportTime.setText(FormatMiss(times));
        }
        try {//得到时间
            gpsJSon.put("timeLen", FormatMiss(times));
            sportMaps.setTimeLen(FormatMiss(times));
        } catch (Exception E) {
            E.printStackTrace();
        }
    }

    /**
     * 上传经纬度数据
     */
    public void upDataGPS() {
        try {
            // 路程
            String discanceStr = sportDistance.getText().toString().trim();
            // 速度
            // String shisuStr = sportShisu.getText().toString().trim();
            // 取得时速
            String speedSStr = "0.0";
            if (sportDistance != null) {
                speedSStr = sportShisu.getText().toString().trim();
            }

            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            DecimalFormat decimalFormat = new DecimalFormat("######0.00");

            /****  保存数据库  ***/
            @SuppressLint("SimpleDateFormat")
            DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sportMaps.setRtc(df.format(new Date()));
            sportMaps.setSaveTime(df1.format(new Date()));
            sportMaps.setSpeed(speedSStr.equals("00'00") || TextUtils.isEmpty(speedSStr) ? "0.0" : "" + Double.valueOf(speedSStr));
            if (sportDistance != null) {
                sportMaps.setDistance(discanceStr.equals("0.00") ? "0.00" : "" + Double.valueOf(Double.parseDouble(sportDistance.getText().toString())));
            } else {
                sportMaps.setDistance("0.00");
            }

            String bleMac = (String) SharedPreferencesUtils.readObject(SportsMapActivity.this, Commont.BLEMAC);
            String userId = (String) SharedPreferencesUtils.readObject(SportsMapActivity.this, "userId");
            if (WatchUtils.isEmpty(bleMac)) return;
            sportMaps.setUserId(WatchUtils.isEmpty(userId) ? "null" : userId);
            sportMaps.setCalories(Double.valueOf(decimalFormat.format(mIMapRun.getDistance() * 65.4)) + "");
            sportMaps.setMac(bleMac);
            sportMaps.setLatLons(new Gson().toJson(gpsMaps));

            List<SportMaps> sportMapsList = MyApp.getDBManager().getDaoSession().getSportMapsDao().loadAll();
            if (sportMapsList == null || sportMapsList.size() <= 0) {
                MyApp.getDBManager().getDaoSession().getSportMapsDao().insert(sportMaps);
            } else {
                SportMaps timedata = MyApp.getDBManager().getDaoSession().getSportMapsDao().queryBuilder()
                        .where(SportMapsDao.Properties.SaveTime.eq(df1.format(new Date()))).unique();
                if (timedata == null) {
                    MyApp.getDBManager().getDaoSession().getSportMapsDao().insert(sportMaps);
                }
            }

            latLonBean.setUserId(TextUtils.isEmpty(userId) ? "null" : userId);
            latLonBean.setRtc(df.format(new Date()));
            latLonBean.setSaveTime(df1.format(new Date()));
            latLonBean.setMac(bleMac);
            latLonBean.setLatLons(new Gson().toJson(gpsMaps));

            MyApp.getDBManager().getDaoSession().getLatLonBeanDao().insert(latLonBean);
        } catch (Exception E) {
            E.printStackTrace();
        }
    }

    /**
     * 按钮监听------包含开始暂停----下滑结束（下滑必须超出屏幕才会执行）
     */
    @Override
    public void OnBzlDragViewListenter() {
        int cnt = mIMapRun.getPointSize();
        if (cnt <= 0)
            return;
        else if (cnt >= 2) {
            if (sportDistance != null) {
                String discanceStr = sportDistance.getText().toString().trim();
                if (TextUtils.isEmpty(discanceStr) || discanceStr.equals("0.0") || discanceStr.equals("0.00") || discanceStr.equals("0.000")) {
                    // 显示Toast
                    if (mToast == null) {
                        mToast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.string_no_sport), Toast.LENGTH_SHORT);
                    } else {
                        mToast.setText(getResources().getString(R.string.string_no_sport));
                        mToast.setDuration(Toast.LENGTH_SHORT);
                    }
                    mToast.show();
                    if (mHandler != null) mHandler.sendEmptyMessageAtTime(0x01, 5000);
                    return;
                }
            }

            new CommomDialog(SportsMapActivity.this, R.style.dialog, getResources().getString(R.string.save_record) + "?", new CommomDialog.OnCloseListener() {
                @Override
                public void onClick(Dialog dialog, boolean confirm) {
                    if (confirm) {
                        mIMapRun.handleLastPoint();
                        /*
                        if (mLocationList.get(mLocationList.size() - 1).longitude != 0 && mLocationList.get(mLocationList.size() - 1).latitude != 0) {
                            addPointLins(mLocationList.get(mLocationList.size() - 2), mLocationList.get(mLocationList.size() - 1));
                            endDisplayPerth(mLocationList.get(mLocationList.size() - 1));//添加结束点
                        }
                        */

                        if (mHandler != null) {
                            mHandler.removeCallbacks(mRunnable);
                            mRunnable = null;
                        }

                        //停止定位后，本地定位服务并不会被销毁
                        mIMapRun.stop();
                        // mLocationClient.stopLocation();
                        isStart = false;
                        isEnd = true;

                        // 上传数据
                        upDataGPS();

                        bzlDragView.hideDragCallView();
                    }
                    dialog.dismiss();
                    bzlDragView.setDraging(false);
                }
            }).setTitle(getResources().getString(R.string.prompt)).show();
        } else {
            // 显示Toast
            if (mToast == null) {
                mToast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.string_no_sport), Toast.LENGTH_SHORT);
            } else {
                mToast.setText(getResources().getString(R.string.string_no_sport));
                mToast.setDuration(Toast.LENGTH_SHORT);
            }
            mToast.show();

            mIMapRun.stop();
            /*
            if (mLocationClient != null) {
                mLocationClient.startLocation();
            }
            */
            if (mHandler != null) mHandler.sendEmptyMessageAtTime(0x01, 5000);
        }
    }

    /**
     * 开始按钮处理
     */
    @Override
    public void OnClickBzlDragViewListenter() {
        if (!isStart) {// 没开始时候开始
            isStart = true;
            if (timeDowln == null || timr_frame == null) return;
            timeDowln.setEnabled(false);
            timr_frame.setEnabled(false);
            timr_frame.setVisibility(View.VISIBLE);
            timeDowln.setVisibility(View.VISIBLE);//隐藏倒计时控件

            // 三秒倒计时
            if (myCountTimer == null) {
                myCountTimer = new MyCountTimer(4000, 1000, timeDowln, getResources().getString(R.string.string_sport_run));
                //倒计时监听
                myCountTimer.setmCompletionTime(completionTime);
            }
            myCountTimer.start();
        } else {
            ToastUtil.showShort(SportsMapActivity.this, getResources().getString(R.string.string_stop_run_dowln));
        }
    }


    /**
     * 倒计时监听,
     */
    MyCountTimer.CompletionTime completionTime = new MyCountTimer.CompletionTime() {
        @Override
        public void OnCompletionTime() {
            // 倒计时完成时执行开始定位
            timeDowln.setVisibility(View.GONE);//隐藏倒计时控件
            timr_frame.setVisibility(View.GONE);

            if (mRunnable == null) {
                mRunnable = new MyRunnable();
            }
            if (mHandler != null) mHandler.postDelayed(mRunnable, 0);

            // 开始定位更新
            if (!mIMapRun.isStart())
                mIMapRun.start();
            // if (mLocationClient != null) mLocationClient.startLocation();

            if (bzlDragView != null) {
                bzlDragView.setIsDrag(true);
                bzlDragView.setText(getResources().getString(R.string.stop));
            }
        }
    };

    /**
     * 监听Back键按下事件
     * 注意:
     * 返回值表示:是否能完全处理该事件
     * 在此处返回false,所以会继续传播该事件.
     * 在具体项目中此处的返回值视情况而定.
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            if (isStart) {
                new CommomDialog(SportsMapActivity.this, R.style.dialog,
                        getResources().getString(R.string.string_out_gps), new CommomDialog.OnCloseListener() {
                    @Override
                    public void onClick(Dialog dialog, boolean confirm) {
                        if (confirm && mIMapRun.isStart()) {
                            isEnd = true;
                            if (mHandler != null) mHandler.removeCallbacks(mRunnable);
                            mRunnable = null;

                            //停止定位更新
                            mIMapRun.stop();
                            // mLocationClient.stopLocation();
                            isStart = false;
                            bzlDragView.hideDragCallView();
                            bzlDragView.setDraging(false);
                            Intent intent = new Intent();
                            setResult(1001, intent);
                        }
                        dialog.dismiss();
                    }
                }).setTitle(getResources().getString(R.string.prompt)).show();
                return true;
            }
            return super.onKeyDown(keyCode, event);
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }


    MyRunnable mRunnable = null;
    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0x01) {
                if (bzlDragView != null) bzlDragView.setDraging(false);
            }
        }
    };

    // 总距离
    @Override
    public void setDistance(double distance) {
        if (sportDistance == null) return;
        DecimalFormat decimalFormat = new DecimalFormat("######0.00");
        boolean w30sunit = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISSystem, true);//是否为公制
        int h9_step_util = (int) SharedPreferencesUtils.getParam(MyApp.getContext(), "H9_UTIT", 0);
        if ((!TextUtils.isEmpty(MyCommandManager.DEVICENAME) && MyCommandManager.DEVICENAME.equals("H9")) ||
                !TextUtils.isEmpty(MyCommandManager.DEVICENAME) && MyCommandManager.DEVICENAME.equals("W06X")) {
            // 0位公制 1为英制
            if (h9_step_util == 0) {
                sportDistance.setText(String.valueOf(decimalFormat.format(distance)));
                sport_distance_unti.setText("KM");
            } else {
                //总运动距离
                sportDistance.setText(String.valueOf(decimalFormat.format(distance * 0.0006214)));
                sport_distance_unti.setText("MI");
            }
        } else {
            //总公里数
            if (w30sunit) {
                sportDistance.setText(String.valueOf(decimalFormat.format(distance)));
                sport_distance_unti.setText("KM");
            } else {
                //总运动距离
                sportDistance.setText(String.valueOf(decimalFormat.format(distance * 3.28)));
                sport_distance_unti.setText("FT");
            }
        }
    }

    // 显式时间
    @Override
    public void setTime(long time) {
        if (textTimeData != null) {
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            textTimeData.setVisibility(View.VISIBLE);
            textTimeData.setText(format.format(new Date(time)).trim());
        }
    }

    // 卡路里
    @Override
    public void setCal(double distance) {
        if (sportKcal != null) {
            DecimalFormat decimalFormat = new DecimalFormat("######0.00");
            sportKcal.setText(String.valueOf(decimalFormat.format(distance * 65.4)) + "kcal");
        }
    }

    // 速度
    @Override
    public void setSpeed(double distance) {
        if (sportShisu == null) return;
        DecimalFormat decimalFormat = new DecimalFormat("######0.00");

        try {
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat dff = new SimpleDateFormat("HH:mm:ss");
            Date dates = dff.parse(sportTime.getText().toString());
            Calendar c = Calendar.getInstance();
            c.setTime(dates);

            boolean w30sunit = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISSystem, true);//是否为公制
            int h9_step_util = (int) SharedPreferencesUtils.getParam(MyApp.getContext(), "H9_UTIT", 0);
            if ((!TextUtils.isEmpty(MyCommandManager.DEVICENAME) && MyCommandManager.DEVICENAME.equals("H9")) ||
                    !TextUtils.isEmpty(MyCommandManager.DEVICENAME) && MyCommandManager.DEVICENAME.equals("W06X")) {
                // 0位公制 1为英制
                if (h9_step_util == 0) {
                    if (distance <= 0.0) {
                        sportShisu.setText("0.0");
                    } else {
                        sportShisu.setText("" + String.valueOf(decimalFormat.format(Double.valueOf(decimalFormat.format(distance)) / (c.get(Calendar.HOUR) * 360 + c.get(Calendar.MINUTE) * 60 + c.get(Calendar.SECOND)) * 60)));
                    }
                    util_shisu.setText("km/h");
                } else {
                    //总运动距离
                    if (distance <= 0.0) {
                        sportShisu.setText("0.0");
                    } else {
                        sportShisu.setText("" + String.valueOf(decimalFormat.format(Double.valueOf(decimalFormat.format(distance * 0.0006214)) / (c.get(Calendar.HOUR) * 360 + c.get(Calendar.MINUTE) * 60 + c.get(Calendar.SECOND)) * 60)));
                    }
                    util_shisu.setText("mi/h");
                }
            } else {
                if (w30sunit) {
                    if (distance <= 0.0) {
                        sportShisu.setText("0.0");
                    } else {
                        sportShisu.setText("" + String.valueOf(decimalFormat.format(Double.valueOf(decimalFormat.format(distance)) / (c.get(Calendar.HOUR) * 360 + c.get(Calendar.MINUTE) * 60 + c.get(Calendar.SECOND)) * 60)));
                    }
                    util_shisu.setText("km/h");
                } else {
                    //总运动距离
                    if (distance <= 0.0) {
                        sportShisu.setText("0.0");
                    } else {
                        sportShisu.setText("" + String.valueOf(decimalFormat.format(Double.valueOf(decimalFormat.format(distance * 3.28)) / (c.get(Calendar.HOUR) * 360 + c.get(Calendar.MINUTE) * 60 + c.get(Calendar.SECOND)) * 60)));
                    }
                    util_shisu.setText("ft/h");
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    // 配速
    @Override
    public void setPace(float pace) {
        if (sportSpeed != null) {
            sportSpeed.setText(String.valueOf(pace));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mIMapRun.conn();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mIMapRun.disconn();
    }

    @Override
    public boolean isStart() {
        return isStart;
    }

    @Override
    public void addPoint(double lng, double lat) {
        try {
            com.alibaba.fastjson.JSONObject beanList = new com.alibaba.fastjson.JSONObject();
            beanList.put("lat", lat);
            beanList.put("lon", lng);
            gpsMaps.add(beanList);
        } catch (Exception E) {
            E.printStackTrace();
        }
    }

    @Override
    public boolean requestPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Toast.makeText(BzlGoogleMapsActivity.this, "没定位权限", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public void putTime(String name, Date date, boolean isFirst) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd  HH:mm");
        try {
            gpsJSon.put(name, format.format(date));
            if (isFirst)
                sportMaps.setStartTime(format.format(date));
        } catch (Exception E) {
            E.printStackTrace();
        }
    }

    private class MyRunnable implements Runnable {
        @Override
        public void run() {
            times++;
            if (times == 86401) times = 0;
            if (sportTime != null) sportTime.setText(FormatMiss(times));
            try {//得到时间
                Log.d("===========", FormatMiss(times));
                gpsJSon.put("timeLen", FormatMiss(times));
                sportMaps.setTimeLen(FormatMiss(times));
            } catch (Exception E) {
                E.printStackTrace();
            }
            if (mHandler != null) mHandler.postDelayed(this, 1000);
        }
    }


    // 将秒转化成小时分钟秒
    public String FormatMiss(int miss) {
        String hh = miss / 3600 > 9 ? miss / 3600 + "" : "0" + miss / 3600;
        String mm = (miss % 3600) / 60 > 9 ? (miss % 3600) / 60 + "" : "0" + (miss % 3600) / 60;
        String ss = (miss % 3600) % 60 > 9 ? (miss % 3600) % 60 + "" : "0" + (miss % 3600) % 60;
        return hh + ":" + mm + ":" + ss;
    }

}

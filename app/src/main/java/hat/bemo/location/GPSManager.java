package hat.bemo.location;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import java.util.List;
import java.util.Locale;

import hat.bemo.BaseActivity;
import hat.bemo.MyApplication;
import hat.bemo.UpdateAlarmLoc;
import hat.bemo.setting.ChildThread;
import hat.bemo.setting.SharedPreferences_status;

/**
 * Created by apple on 2017/11/8.
 */

public class GPSManager implements LocationListener {
    public static LocationManager lms;
    public static String bestProvider = LocationManager.GPS_PROVIDER;    //最佳資訊提供者
    private boolean getService = false;        //是否已開啟定位服務

    private Location finalLocation;//2015-09-22更改定位需求
    public static Location location;
    public static String pr_type_gps = "0";
    private Double longitude = 0.0;
    private Double latitude = 0.0;
    private float accuracy = 0.0f;
    private static String address = "";
    private String gps_status = "1";
    private static String LAT = "";
    private static String LONG = "";
    private String type;
    private String gpstype;
    private static MyCount mc;


    //baidu
    public LocationClient mLocationClient;
    public MyLocationListener mMyLocationListener;

    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation BDlocation) {
            if (mListener != null) {
                Log.e("GPSManager", "百度定位模式");
                try {
                    MapPosition point = MapPositionUtil.bd09_To_Gps84(BDlocation.getLatitude(), BDlocation.getLongitude());

                    String address = BDlocation.getAddrStr();
                    if (address == null) {
                        address = "";
                    }

                    stopBaiduClient();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

//        @Override
//        public void onConnectHotSpotMessage(String s, int i) {
//
//        }
    }

    //baidu初始化
    private void initBaiduLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可選，默認高精度，設置定位模式，高精度，低功耗，僅設備
        option.setCoorType("bd09ll");//可選，默認gcj02，設置返回的定位結果坐標系，

        option.setScanSpan(0);//可選，默認0，即僅定位一次，設置發起定位請求的間隔需要大於等於1000ms才是有效的
        option.setIsNeedAddress(true);//可選，設置是否需要地址信息，默認不需要
        option.setOpenGps(true);//可選，默認false,設置是否使用gps
//        option.setLocationNotify(false);//可選，默認false，設置是否當gps有效時按照1S1次頻率輸出GPS結果
        option.setIgnoreKillProcess(false);//可選，默認true，定位SDK內部是一個SERVICE，並放到了獨立進程，設置是否在stop的時候殺死這個進程，默認不殺死

        mLocationClient.setLocOption(option);
    }

    //呼叫定位
    public void startBaiduLocation() {
        if (mLocationClient != null) {
            if (mLocationClient.isStarted()) {
                mLocationClient.requestLocation();
            } else {
                mLocationClient.start();
            }
        }
    }

    //關閉
    public void stopBaiduClient() {
        mLocationClient.stop();
    }
    /////

    public interface OnGPSListeners {
        void onGPSParam(String lat, String Long, String Address, String GPS_status, String accuracy);
    }

    public static OnGPSListeners mListener;


    public void setOnGPSListeners(OnGPSListeners ongpslisteners) {
        mListener = ongpslisteners;
    }

    public void Check_GPS_Status() {
        @SuppressWarnings("static-access")
        LocationManager status = (LocationManager) (MyApplication.context.getSystemService(MyApplication.context.LOCATION_SERVICE));
        if (status.isProviderEnabled(LocationManager.GPS_PROVIDER) || status.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            System.out.println("Mark 有開吧有開吧~!!");
        } else {
            //開啟設定頁面
            if (android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
                turnGPSOn(MyApplication.context);
            } else {
//                Toast.makeText(MyApplication.context, R.string.location_services, Toast.LENGTH_LONG).show();
                MyApplication.context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
            System.out.println("Mark 沒開嗎~!!?");
        }
    }

    public GPSManager(String type1, String type2) {
        try {
            if (type1.equals("1") & type2.equals("1")) {
                Log.e("GPS開關", "GPS開啟");
                Log.e("GPS開關", "GPS_type1= " + type1);
                Log.e("GPS開關", "GPS_type2= " + type2);
                Log.e("GPS開關", "GPS_type_1= "+SharedPreferences_status.getGps_status(MyApplication.context));
                GpsController.setGpsStatus(MyApplication.context, true);
                GpsSwitch();
            } else if (type1.equals("2") & type2.equals("2")) {
                Log.e("GPS開關", "GPS開啟");
                Log.e("GPS開關", "GPS_type11= " + type1);
                Log.e("GPS開關", "GPS_type22= " + type2);
                Log.e("GPS開關", "GPS_type_2= "+SharedPreferences_status.getGps_status(MyApplication.context));
                GpsController.setGpsStatus(MyApplication.context, true);
                GpsSwitch();
            } else if (type1.equals("0") & type2.equals("0")) {
                if(SharedPreferences_status.getGps_status(MyApplication.context).equals("0x29")){
                Log.e("GPS開關", "GPS開啟");
                Log.e("GPS開關", "GPS_type0= " + type1);
                Log.e("GPS開關", "GPS_type0= " + type2);
                    Log.e("GPS開關", "GPS_type_0= "+SharedPreferences_status.getGps_status(MyApplication.context));
                GpsController.setGpsStatus(MyApplication.context, true);
                GpsSwitch();
                }else{
                    Log.e("GPS開關", "GPS關閉");
                    Log.e("GPS開關", "GPS_type= "+SharedPreferences_status.getGps_status(MyApplication.context));
                    GpsController.setGpsStatus(MyApplication.context, false);
                }
            } else if (type1.equals("1") & type2.equals("0")) {
                Log.e("GPS開關", "GPS開啟");
                Log.e("GPS開關", "GPS_type1_1= " + type1);
                Log.e("GPS開關", "GPS_type1_1= " + type2);
                Log.e("GPS開關", "GPS_type1_1= "+SharedPreferences_status.getGps_status(MyApplication.context));
                GpsController.setGpsStatus(MyApplication.context, true);
                GpsSwitch();
            } else if (type1.equals("0") & type2.equals("1")) {
                Log.e("GPS開關", "GPS開啟");
                Log.e("GPS開關", "GPS_type2_2= " + type1);
                Log.e("GPS開關", "GPS_type2_2= " + type2);
                Log.e("GPS開關", "GPS_type2_2= "+SharedPreferences_status.getGps_status(MyApplication.context));
                GpsController.setGpsStatus(MyApplication.context, true);
                GpsSwitch();
            } else if (type1.equals("SOS") & type2.equals("SOS")) {
                Log.e("GPS開關", "GPS開啟");
                Log.e("GPS開關", "GPS_type2_2= " + type1);
                Log.e("GPS開關", "GPS_type2_2= " + type2);
                Log.e("GPS開關", "GPS_type2_2= "+SharedPreferences_status.getGps_status(MyApplication.context));
                mLocationClient = new LocationClient(MyApplication.context);
                mMyLocationListener = new MyLocationListener();
                mLocationClient.registerLocationListener(mMyLocationListener);
                initBaiduLocation();
//				if(GpsController.getGpsStatus(MyApplication.context)){
//
//				}
                GpsController.setGpsStatus(MyApplication.context, true);
            } else if (type1.equals("false") & type2.equals("false")) {
                Log.e("GPS開關", "true");
            } else if (type1.equals("5") & type2.equals("5")) {
                GpsController.setGpsStatus(MyApplication.context, true);
            } else if (type1.equals("baidu") && type2.equals("baidu")) {
                mLocationClient = new LocationClient(MyApplication.context);
                mMyLocationListener = new MyLocationListener();
                mLocationClient.registerLocationListener(mMyLocationListener);
                initBaiduLocation();
            } else {
                Log.e("GPS開關", "GPS關閉3");
                GpsController.setGpsStatus(MyApplication.context, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("static-access")
    public void testLocationProvider(String Type) {
        //取得系統定位服務
        type = Type;
        LocationManager status = (LocationManager) (MyApplication.context.getSystemService(MyApplication.context.LOCATION_SERVICE));
        getService = true;    //確認開啟定位服務
        locationServiceInitial(pr_type_gps);
        if (status.isProviderEnabled(LocationManager.GPS_PROVIDER) || status.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            //如果GPS或網路定位開啟，呼叫locationServiceInitial()更新位置
//        	getService = true;	//確認開啟定位服務
//        	locationServiceInitial();
        } else {
            if (Type.equals("0x28")) {
                //開啟設定頁面
                if (android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    turnGPSOn(MyApplication.context);
                } else {
//                    Toast.makeText(MyApplication.context, R.string.location_services, Toast.LENGTH_LONG).show();
                    MyApplication.context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                }
            }
        }
    }

    private void GpsSwitch() {
        gpstype = SharedPreferences_status.getGps_status(MyApplication.context);

        if ("0x29".equals(gpstype)) {
            try {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        if (mc != null) mc.cancel();
                        Log.i("GPS開關_0x29", "GPS開啟_0x29");
                        GpsController.setGpsStatus(MyApplication.context, true);
                        Looper.loop();
                    }
                }).start();
                return;
            } catch (Exception e) {
            }
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Looper.prepare();
                    //Mark 改10秒後關閉GPS
                    if (mc != null) mc.cancel();
                    mc = new MyCount(1000 * 10, 1000);
                    mc.start();
                    Looper.loop();
                }
            }).start();
        }
    }


    class MyCount extends CountDownTimer {
        public MyCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            try {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        Log.i("GPS開關", "GPS關閉");
                        GpsController.setGpsStatus(MyApplication.context, false);
                        Looper.loop();
                    }
                }).start();
            } catch (Exception e) {
            }
        }

        @Override
        public void onTick(long millisUntilFinished) {
            System.out.println("GPS關閉排程: " + millisUntilFinished / 1000 + "S");
        }
    }

    @SuppressWarnings("static-access")
    public void locationServiceInitial(String status) {

        lms = (LocationManager) MyApplication.context.getSystemService(MyApplication.context.LOCATION_SERVICE);//取得系統定位服務
        getService = true;
        /**
         *@deprecated
         *@date 2015-09-22
         */
        Criteria criteria = new Criteria();    //資訊提供者選取標準
        bestProvider = lms.getBestProvider(criteria, true);    //選擇精準度最高的提供者
        if (ActivityCompat.checkSelfPermission(MyApplication.context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MyApplication.context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        location = lms.getLastKnownLocation(bestProvider);
        System.out.println("Mark 最後定位 = " + location);
        Check_GPS_Status();
        if (getService) {
            try {
                /**
                 *@deprecated
                 *@date 2015-09-22
                 */
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        System.out.println("Mark 確定有取得定位服務 = " + bestProvider);
                        //服務提供者、更新頻率60000毫秒=1分鐘、最短距離、地點改變時呼叫物件
                        if (ActivityCompat.checkSelfPermission(MyApplication.context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MyApplication.context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        lms.requestLocationUpdates(bestProvider, 1000 * 60 * 60, 1000, GPSManager.this);

                        getLocation(location);
                        Looper.loop();
                    }
                }).start();
                /**
                 *SOS判斷進入
                 *@date 2015-09-22
                 */
                if(status.equals("0")) {
                    locationStatus(UpdateAlarmLoc.GPStype);
                }
                else
                    locationStatus("SOS");
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     * 最後位子
     *@param location
     *@date 2015-09-22
     */
    private String longs = "0";
    private String lats = "0";
    private void getLocation(Location location) {
        System.out.println("Mark google在這裡 loc = " + location);

        if(location != null){
            finalLocation = location;
            onLocationChanged(finalLocation);
            finalLocation = location;
            System.out.println("Mark loc = " + finalLocation);
            latitude = finalLocation.getLatitude();
            longitude = finalLocation.getLongitude();
            accuracy = finalLocation.getAccuracy();

            address = getAddressByLocation(finalLocation);
            LAT = String.valueOf(latitude);
            LONG = String.valueOf(longitude);

            gps_status = "0";

            Log.e("LAT", "LAT"+": "+LAT);
            Log.e("LONG", "LONG"+": "+LONG);
            Log.e("Provider", "Provider"+": "+finalLocation.getProvider());

            if(finalLocation.getProvider().equals("gps")){
                Log.e("GPS", "GPS定位模式");
                lats = SharedPreferences_status.GetLat(MyApplication.context);
                longs = SharedPreferences_status.GetLong(MyApplication.context);
                Log.e("LAT", LAT+":"+lats);
                Log.e("LONG", LONG+":"+longs);
                if(LAT.equals(lats) & LONG.equals(longs)){
//					室內
                    Log.e("network", "室內狀態");
                    ChildThread mhandler = new ChildThread(2000, 1000);

                    mhandler.setOnHandlerListeners(new ChildThread.OnHandlerListeners() {

                        @Override
                        public void onHandlerParam() {
                            Message m = new Message();
                            m.obj = "General";
                            BaseActivity.mdHandler.sendMessage(m);
                            getLocation(MyApplication.gps_parameters.location);
                        }
                    });
                }
                else{
                    Log.e("network", "室外狀態");
                    mListener.onGPSParam(LAT, LONG, address, gps_status, String.valueOf((int)(Math.round(accuracy))));
                    SharedPreferences_status.SetLat(MyApplication.context, LAT);
                    SharedPreferences_status.SetLong(MyApplication.context, LONG);
                }
            }
            else if(finalLocation.getProvider().equals("network")){
                Log.e("network", "網路定位模式");
                mListener.onGPSParam(LAT, LONG, address, "4", String.valueOf((int)(Math.round(accuracy))));
            }
        }
        else{
            Log.e("getLocation", "google網路定位失敗!!!!!");
            /**
             * 客製化功能 定位模式
             * */

                Log.e("定位模式", "Skyhook");
                mListener.onGPSParam(LAT, LONG, address, "1", "0");

        }
    }

    /**
     * 判斷是否緊急求救
     *@param mStatus
     *@date 2015-09-22
     */
    private void locationStatus(String mStatus){
        Message m = new Message();
        m.obj = mStatus;
        BaseActivity.mdHandler.sendMessage(m);
        if (mStatus.equals("SOS")) {
            ChildThread mhandler = new ChildThread(1000, 1000);
            mhandler.setOnHandlerListeners(new ChildThread.OnHandlerListeners() {
                @Override
                public void onHandlerParam() {
                    getLocation(MyApplication.gps_parameters.location);
                }
            });
        } else {
            ChildThread mhandler = new ChildThread(1000, 1000);
            mhandler.setOnHandlerListeners(new ChildThread.OnHandlerListeners() {
                @Override
                public void onHandlerParam() {
                    getLocation(MyApplication.gps_parameters.location);
                }
            });
        }
    }

    /**
     *@category 選擇低精準度定位
     *@date 2015-09-22
     */
    @SuppressLint("InlinedApi")
    public static Criteria createCoarseCriteria() {
        Criteria c = new Criteria();
        c.setAccuracy(Criteria.ACCURACY_COARSE);
        c.setAltitudeRequired(false);
        c.setBearingRequired(false);
        c.setSpeedRequired(false);
        c.setCostAllowed(true);
        c.setPowerRequirement(Criteria.POWER_LOW);
        return c;
    }
    /**
     * @category 選擇高精準度定位
     *@date 2015-09-22
     */
    public static Criteria createFineCriteria() {
        Criteria c = new Criteria();
        c.setAccuracy(Criteria.ACCURACY_FINE);
        c.setAltitudeRequired(false);
        c.setBearingRequired(false);
        c.setSpeedRequired(false);
        c.setCostAllowed(true);
        c.setPowerRequirement(Criteria.POWER_LOW);
        return c;
    }

    /**
     * 清掉LocationManager
     *@date 2015-09-22
     *@author luman
     */
    private void Cancel(){
        lms.removeUpdates(this);
        finalLocation = null;
        location = null;
    }


    /**
     * 打开GPS
     *@date 2014-2-18 上午10:01:21
     *@author luman
     */
    public void turnGPSOn(Context ctx){
        Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
        intent.putExtra("enabled", true);
        ctx.sendBroadcast(intent);


        String provider = Settings.Secure.getString(ctx.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if(!provider.contains("gps")){ //if gps is disabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            ctx.sendBroadcast(poke);
        }
    }

    /**
     * 关闭GPS
     *@date 2014-2-18 上午10:01:21
     *@author luman
     */
    public void turnGPSOff(Context ctx){
        Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
        intent.putExtra("enabled", false);
        ctx.sendBroadcast(intent);

        String provider = Settings.Secure.getString(ctx.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if(!provider.contains("gps")){ //if gps is disabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("1"));
            ctx.sendBroadcast(poke);
        }
    }

    public String getAddressByLocation(Location location) {
        String returnAddress = "";
        try {
            if (location != null) {
                //建立Geocoder物件: Android 8 以上模疑器測式會失敗
                Geocoder gc = new Geocoder(MyApplication.context, Locale.TRADITIONAL_CHINESE);	//地區:台灣
                //自經緯度取得地址
                List<Address> lstAddress = gc.getFromLocation(latitude, longitude, 1);

                returnAddress = lstAddress.get(0).getAddressLine(0);
//                Log.e("returnAddress", returnAddress);
            }else{
                Log.e("returnAddress", "無法定位");
            }

            if(returnAddress == null){
                returnAddress = "";
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return returnAddress;
    }

    public static void Markloc(){
        mListener.onGPSParam(LAT, LONG, address, "1", "0");
    }


    @Override
    public void onProviderDisabled(String arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onLocationChanged(Location locat) {
        // TODO Auto-generated method stub


        System.out.println("Mark 這邊這邊啦 loc = " + locat.getLatitude() + "," + locat.getLongitude());
        locat = location;
        finalLocation = location;
    }

}

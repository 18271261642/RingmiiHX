package com.guider.healthring.bzlmaps;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.google.gson.Gson;
import com.guider.healthring.Commont;
import com.guider.healthring.MyApp;
import com.guider.healthring.R;
import com.guider.healthring.bzlmaps.baidulocal.LocationService;
import com.guider.healthring.bzlmaps.sos.GPSGaoDeUtils;
import com.guider.healthring.bzlmaps.sos.GPSGoogleUtils;
import com.guider.healthring.siswatch.utils.PhoneUtils;
import com.guider.healthring.siswatch.utils.WatchUtils;
import com.guider.healthring.util.OkHttpTool;
import com.guider.healthring.util.SharedPreferencesUtils;
import com.guider.healthring.util.ToastUtil;
import com.guider.healthring.util.VerifyUtil;
import com.veepoo.protocol.listener.data.IDeviceControlPhone;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PhoneSosOrDisPhone implements IDeviceControlPhone {


    private LocationService locationService;


    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case 0x01:
                    handler.removeMessages(0x01);
                    String stringpersonOne = (String) SharedPreferencesUtils.getParam(MyApp.getContext(), "personOne", "");
                    String stringpersonTwo = (String) SharedPreferencesUtils.getParam(MyApp.getContext(), "personTwo", "");
                    String stringpersonThree = (String) SharedPreferencesUtils.getParam(MyApp.getContext(), "personThree", "");

                    if (!TextUtils.isEmpty(stringpersonOne)) {
                        call(stringpersonOne);
                    } else {
                        if (!TextUtils.isEmpty(stringpersonTwo)) {
                            call(stringpersonTwo);
                        } else {
                            if (!TextUtils.isEmpty(stringpersonThree)) {
                                call(stringpersonThree);
                            }
                        }
                    }
                    break;
            }
            return false;
        }
    });

    @Override
    public void rejectPhone() {
        try {
//                    TelephonyManager tm = (TelephonyManager) MyApp.getContext()
//                            .getSystemService(Service.TELEPHONY_SERVICE);
//                    PhoneUtils.endPhone(MyApp.getContext(),tm);
            PhoneUtils.dPhone();
            PhoneUtils.endCall(MyApp.getContext());
            //PhoneUtils.endcall();
            Log.d("call---", "rejectPhone: " + "电话被挂断了");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void cliencePhone() {
        //正常模式静音
        if (WatchUtils.getPhoneStatus() == AudioManager.RINGER_MODE_NORMAL) {
            SharedPreferencesUtils.setParam(MyApp.getContext(), "phone_status", true);
            AudioManager audioManager = (AudioManager) MyApp.getInstance().getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
            if (audioManager != null) {
                audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                audioManager.getStreamVolume(AudioManager.STREAM_RING);
                Log.d("call---", "RINGING 已被静音");
            }
        }
    }

    @Override
    public void knocknotify(int i) {
    }

    int count = 0;

    //启动了SOS功能
    @Override
    public void sos() {
        count ++;
        Log.e("SOS","-------sos="+count);
        if (!Commont.isSosOpen) {
            locationService = MyApp.getApplication().locationService;
            locationService.registerListener(bdAbstractLocationListener);
            locationService.start();




//            Commont.isSosOpen = true;
//            //Toast.makeText(this,"SOS 执行了 进入",Toast.LENGTH_SHORT).show();
//            boolean isSos = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISHelpe, false);//sos
//            String stringpersonOne = (String) SharedPreferencesUtils.getParam(MyApp.getInstance(), "personOne", "");
//            String stringpersonTwo = (String) SharedPreferencesUtils.getParam(MyApp.getInstance(), "personTwo", "");
//            String stringpersonThree = (String) SharedPreferencesUtils.getParam(MyApp.getInstance(), "personThree", "");
//            if ((!TextUtils.isEmpty(stringpersonOne)
//                    || !TextUtils.isEmpty(stringpersonTwo)
//                    || !TextUtils.isEmpty(stringpersonThree))
//                    && isSos) {
//                //Toast.makeText(this,"SOS 执行了 电话和条件允许",Toast.LENGTH_SHORT).show();
//                Commont.COUNTNUMBER = 0;
//                Commont.GPSCOUNT = 0;
////                Commont.isGPSed = true;
//                Log.e("===", "======开始定位");
//                if (initPermission(MyApp.getInstance())) {
//                    getGps();
//
//                    Log.e("===", "======5 秒后打电话");
//                    handler.sendEmptyMessageAtTime(0x01, 5000);
//                } else {
//                    Commont.isSosOpen = false;
//                    ToastUtil.showShort(MyApp.getContext(), MyApp.getInstance().getResources().getString(R.string.string_sos_prmiss));
//                }
//                //handler.sendEmptyMessageAtTime(0x02, 1000 * 60 * 3);
//            } else {
//                Commont.isSosOpen = false;
//                ToastUtil.showShort(MyApp.getContext(), MyApp.getInstance().getResources().getString(R.string.string_sos_tip));
//            }

        }
    }


    private BDAbstractLocationListener bdAbstractLocationListener = new BDAbstractLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {

            if(bdLocation == null)
                return;
            if(bdLocation.getLocType() == BDLocation.TypeOffLineLocationFail){
                ToastUtil.showToast(MyApp.getApplication(),"定位失败,请打开GPS开关!");
                return;
            }
            if(bdLocation.getLocType() ==  BDLocation.TypeServerError)
                return;
            //纬度
            double latitude = bdLocation.getLatitude();
            //经度
            double longitude = bdLocation.getLongitude();
            if(String.valueOf(latitude).contains("e") || String.valueOf(longitude).equals("e"))
                return;

            //详细地址
            String detailStr = bdLocation.getAddrStr();


            Log.e("SOS", "-------sos=" + detailStr + "--" + latitude + "-=" + longitude);
            updateLocalMsgToGuiderServer(detailStr, latitude, longitude);

        }
    };




    //上传经纬度到盖德平台

    /**
     *  {
     *    long accountId : 用户账号id
     *    String addr: 详细地址
     *    DateTime testTime: 测量时间，ISO8601,
     *    double lng: 经度,
     *    double lat: 维度
     *  }
     */
    private void updateLocalMsgToGuiderServer(String address,double lat,double lng){
        try {
            long accountId = (long) SharedPreferencesUtils.getParam(MyApp.getContext(),"accountIdGD",0L);
            if(accountId == 0)
                return;
            String bleMac = MyApp.getInstance().getMacAddress();
            Map<String,Object> params = new HashMap<>();
            params.put("accountId",accountId);
            params.put("addr",address);
            params.put("testTime",WatchUtils.getISO8601Timestamp(new Date()));
            params.put("deviceCode",bleMac == null ? "00" : bleMac);
            params.put("lng",lng);
            params.put("lat",lat);
            String url = "http://api.guiderhealth.com/api/v1/sos/position";
            String parStr = new Gson().toJson(params);
            Log.e("位置","----------参数="+parStr);
            OkHttpTool.getInstance().doRequest(url, parStr, null, new OkHttpTool.HttpResult() {
                @Override
                public void onResult(String result) {
                    Log.e("位置","---------位置上传返回="+result);
                    if(locationService != null)
                        locationService.stop();
                }
            });


        }catch (Exception e){
            e.printStackTrace();
        }
    }




    GPSGoogleUtils instance;

    /**
     * 获取定位以及发送短信
     */
    void getGps() {

        boolean zh = VerifyUtil.isZh(MyApp.getInstance());
        if (zh) {
            Boolean zhonTW = MyApp.getInstance().getResources().getConfiguration().locale.getCountry().equals("TW");
            Log.e("======", zh + "====" + zhonTW);
            if (zhonTW) {
                instance = GPSGoogleUtils.getInstance(MyApp.getInstance());
                getGpsGoogle();
            } else {
                GPSGaoDeUtils.getInstance(MyApp.getInstance());
            }
        } else {
            instance = GPSGoogleUtils.getInstance(MyApp.getInstance());
            getGpsGoogle();
        }

    }


    void getGpsGoogle() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean b = instance.startLocationUpdates(MyApp.getInstance());
                if (!b) {
                    getGpsGoogle();
                }
            }
        }, 3000);
    }


    /**
     * 打电话
     *
     * @param tel
     */
    //点击事件调用的类
    protected void call(final String tel) {

        try {
            Uri uri = Uri.parse("tel:" + tel);
            Intent intent = new Intent(Intent.ACTION_CALL, uri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            if (ActivityCompat.checkSelfPermission(MyApp.getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
//                return;
//            }
            MyApp.getContext().startActivity(intent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /**
     * 判断权限集合
     * permissions 权限数组
     * return true-表示没有改权限 false-表示权限已开启
     */
    List<String> mPermissionList = null;

    //4、权限判断和申请
    private boolean initPermission(Context mContexts) {
        boolean isOk = false;
        if (mPermissionList == null) {
            mPermissionList = new ArrayList<>();
        } else mPermissionList.clear();//清空已经允许的没有通过的权限
        //逐个判断是否还有未通过的权限
        for (int i = 0; i < permissionsREAD.length; i++) {
            if (ContextCompat.checkSelfPermission(mContexts, permissionsREAD[i]) !=
                    PackageManager.PERMISSION_GRANTED) {
                mPermissionList.add(permissionsREAD[i]);//添加还未授予的权限到mPermissionList中
            }
        }
        //申请权限
        if (mPermissionList.size() > 0) {//有权限没有通过，需要申请
            isOk = false;
        } else {
            //权限已经都通过了，可以将程序继续打开了
            Log.e("=======", "权限请求完成A");
            isOk = true;
        }
        return isOk;
    }


    /**
     * 读写权限 自己可以添加需要判断的权限
     */
    public static String[] permissionsREAD = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.SEND_SMS,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_PHONE_STATE,//
            Manifest.permission.READ_CONTACTS,//
            Manifest.permission.READ_CALL_LOG,//
            Manifest.permission.USE_SIP
    };

}

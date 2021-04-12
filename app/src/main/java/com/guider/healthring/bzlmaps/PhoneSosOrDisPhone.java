package com.guider.healthring.bzlmaps;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import androidx.core.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import com.google.gson.Gson;
import com.guider.healthring.BuildConfig;
import com.guider.healthring.Commont;
import com.guider.healthring.MyApp;
import com.guider.healthring.siswatch.utils.PhoneUtils;
import com.guider.healthring.siswatch.utils.WatchUtils;
import com.guider.healthring.util.OkHttpTool;
import com.guider.healthring.util.SharedPreferencesUtils;
import com.guider.libbase.map.IMapLocation;
import com.guider.libbase.map.IOnLocation;
import com.guider.map.MapLocationImpl;
import com.veepoo.protocol.listener.data.IDeviceControlPhoneModelState;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PhoneSosOrDisPhone implements IDeviceControlPhoneModelState, IOnLocation {

    private static long lastUploadTime = 0;
    // private LocationService locationService;

    // 定位
    private IMapLocation mIMapLocation;

    private Handler handler = new Handler(new Handler.Callback() {
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
            PhoneUtils.dPhone();
            PhoneUtils.endCall(MyApp.getContext());
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
        long now = System.currentTimeMillis();
        if (now - lastUploadTime < 30 * 1000)
            return;
        lastUploadTime = now;
        if (mIMapLocation == null) {
            mIMapLocation = new MapLocationImpl();
        }
        count ++;
        Log.e("SOS","-------sos="+count + ", Commont.isSosOpen : " + Commont.isSosOpen);
        if (!Commont.isSosOpen) {
            mIMapLocation.start(MyApp.getContext(), 1, this);
            // locationService = MyApp.getApplication().locationService;
            // locationService.registerListener(bdAbstractLocationListener);
            // locationService.start();
        }
    }

    @Override
    public void onLocation(double lng, double lat, String addr) {
        updateLocalMsgToGuiderServer(addr, lat, lng);
    }

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
            if (BuildConfig.GOOGLEPLAY){
                params.put("pointType",PhoneSosMapPointType.BASE);
            }else  {
                params.put("pointType",PhoneSosMapPointType.AMAP);
            }
            params.put("signalType",PhoneSosSingalType.GPS);
            params.put("warnType",PhoneSosWarnType.SOS);
            String url = com.guider.health.apilib.BuildConfig.APIURL + "api/v1/sos/position"; // http://api.guiderhealth.com/
            String parStr = new Gson().toJson(params);
            Log.e("位置","----------参数="+parStr);
            OkHttpTool.getInstance().doRequest(url, parStr, null, result -> {
                Log.e("位置","---------位置上传返回="+result);
                if(mIMapLocation != null)
                    mIMapLocation.stop();
            });


        }catch (Exception e){
            e.printStackTrace();
        }
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
//            MyApp.getContext().startActivity(intent);
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

    //音乐控制 下一首
    @Override
    public void nextMusic() {

    }

    //音乐控制 上一首
    @Override
    public void previousMusic() {

    }

    //音乐控制 播放/暂停
    @Override
    public void pauseAndPlayMusic() {

    }

    @Override
    public void pauseMusic() {

    }

    @Override
    public void playMusic() {

    }

    @Override
    public void voiceUp() {

    }

    @Override
    public void voiceDown() {

    }

    @Override
    public void oprateMusicSuccess() {

    }

    @Override
    public void oprateMusicFail() {

    }

    @Override
    public void inPttModel() {

    }

    @Override
    public void outPttModel() {

    }
}

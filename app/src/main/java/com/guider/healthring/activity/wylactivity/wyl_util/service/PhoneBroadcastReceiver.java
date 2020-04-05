package com.guider.healthring.activity.wylactivity.wyl_util.service;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.guider.healthring.BuildConfig;
import com.guider.healthring.Commont;
import com.guider.healthring.MyApp;
import com.guider.healthring.bleutil.MyCommandManager;
import com.guider.healthring.siswatch.utils.WatchUtils;
import com.guider.healthring.util.SharedPreferencesUtils;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
import com.veepoo.protocol.model.enums.ESocailMsg;
import com.veepoo.protocol.model.settings.ContentPhoneSetting;
import com.veepoo.protocol.model.settings.ContentSetting;
import com.yanzhenjie.permission.AndPermission;

import static android.content.Context.TELEPHONY_SERVICE;

/**
 * Created by admin on 2017/5/14.\
 * 6.0 广播接收来电
 */

public class PhoneBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "PhoneBroadcastReceiver";


    OnCallPhoneListener onClallListener;

    public void setOnClallListener(OnCallPhoneListener onClallListener) {
        this.onClallListener = onClallListener;
    }


    String phoneNumber = "";
    private String bleName ;

    public PhoneBroadcastReceiver() {
        super();
        bleName = (String) SharedPreferencesUtils.readObject(MyApp.getApplication().getApplicationContext(), "mylanya");


        if (!WatchUtils.isEmpty(bleName) && WatchUtils.isVPBleDevice(bleName)) {
            //设置维亿魄系列的来电静音和挂断电话功能
//            setVPPhoneSetting();
            MyApp.getInstance().getVpOperateManager().settingDeviceControlPhone(MyApp.phoneSosOrDisPhone);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        if (action == null)
            return;
        //未连接设备的状态
        if(MyCommandManager.DEVICENAME == null)
            return;
        //呼入电话
        if (action.equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED) || action.equals("android.intent.action.PHONE_STATE")) {
            Log.e(TAG, "---------action---" + action);
            doReceivePhone(context, intent);
        }

    }

    /**
     * 处理电话广播.
     *
     * @param context
     * @param intent
     */
    public void doReceivePhone(Context context, Intent intent) {
        phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);

        if (WatchUtils.isEmpty(phoneNumber))
            return;
        Log.d(TAG, "---phoneNumber----" + phoneNumber);
        TelephonyManager telephony = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
        if (telephony == null)
            return;
        int state = telephony.getCallState();
        Log.d(TAG, "-----state-----" + state);
        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING://"[Broadcast]等待接电话="

                if (!WatchUtils.isEmpty(phoneNumber) && !WatchUtils.isEmpty(bleName)) {
                    Log.d(TAG, "------收到了来电广播---" + phoneNumber);
                    if (onClallListener != null) {
                        onClallListener.callPhoneAlert(phoneNumber);
                    }

                    //维亿魄系列
                    if (WatchUtils.isVPBleDevice(bleName)) {   //B30手环
                        sendPhoneAlertData(phoneNumber, "B30");
                    }
                }

                break;
            case TelephonyManager.CALL_STATE_IDLE:// "[Broadcast]挂断电话
                Log.d(TAG, "------挂断电话--");
            case TelephonyManager.CALL_STATE_OFFHOOK://"[Broadcast]通话中="
                Log.d(TAG, "------通话中--");
                if (!WatchUtils.isEmpty(bleName)) {
                    if (WatchUtils.isVPBleDevice(bleName)) {
                        setB30DisPhone();
                    }
                }
                break;
        }
    }

    // 发送电话号码
    private void sendPhoneAlertData(String phoneNumber, String tag) {
        if (BuildConfig.GOOGLEPLAY)
            getPhoneContacts(phoneNumber, tag);
        else {
            //判断是否有读取联系人和通讯录的权限
            if (!AndPermission.hasPermissions(MyApp.getContext(), Manifest.permission.READ_CONTACTS, Manifest.permission.READ_CALL_LOG)) {
                AndPermission.with(MyApp.getContext()).runtime().permission(Manifest.permission.READ_CONTACTS,
                        Manifest.permission.READ_CALL_LOG).start();
            } else {
                getPhoneContacts(phoneNumber, tag);
            }
        }
    }

    public interface OnCallPhoneListener {
        void callPhoneAlert(String phoneTag);
    }

    private static final String[] PHONES_PROJECTION = new String[]{
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Photo.PHOTO_ID, ContactsContract.CommonDataKinds.Phone.CONTACT_ID};

    /**
     * 联系人显示名称
     **/
    private static final int PHONES_DISPLAY_NAME_INDEX = 0;

    /**
     * 电话号码
     **/
    private static final int PHONES_NUMBER_INDEX = 1;

    /**
     * 头像ID
     **/
    private static final int PHONES_PHOTO_ID_INDEX = 2;

    /**
     * 联系人的ID
     **/
    private static final int PHONES_CONTACT_ID_INDEX = 3;
    boolean isPhone = true;

    /**
     * 得到手机通讯录联系人信息
     **/
    private void getPhoneContacts(String pName, String tag) {
        try {
            //        Log.e(TAG,"----pname-="+pName);
            ContentResolver resolver = MyApp.getInstance().getContentResolver();
            if (resolver == null) return;
            // 获取手机联系人
            Cursor phoneCursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PHONES_PROJECTION, null, null, null);

            if (phoneCursor != null) {
//            Log.e(TAG,"---phoneCursor.size="+phoneCursor.getCount());
                while (phoneCursor.moveToNext()) {
                    //得到手机号码
                    String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
//                Log.e(TAG,"----ph--="+phoneNumber);
                    //当手机号码为空的或者为空字段 跳过当前循环
                    if (!WatchUtils.isEmpty(phoneNumber)) {
                        if (phoneNumber.contains("-")) {
                            phoneNumber = phoneNumber.replace("-", "");
                        }
                        if (phoneNumber.contains(" ")) {
                            phoneNumber = phoneNumber.replace(" ", "");
                        }

                        if (pName.equals(phoneNumber)) {
                            isPhone = false;
                            String contactNames = phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX) + "";
                            if (WatchUtils.isEmpty(contactNames)) contactNames = "null";
//                        Log.e(TAG,"-----相等---="+contactNames);
                            if (!WatchUtils.isEmpty(tag)
                                    && (tag.equals("B30")
                                    || tag.equals("B36"))
                                    || tag.equals("B31")
                                    || tag.equals("Ringmii")
                                    || tag.equals("B31S")
                                    || tag.equals("500S")) {
                                setB30PhoneMsg(contactNames);
                            }

                            return;
                        }
                    }
//                Log.e(TAG,"-----phoneNum-="+phoneNumber);
                    //得到联系人名称
                    String contactName = phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX);
//                Log.e(TAG,"----contactName--="+contactName);

                }
                if (isPhone) {
                    if (!WatchUtils.isEmpty(tag)
                            && (tag.equals("B30")
                            || tag.equals("B36"))
                            || tag.equals("B31")
                            || tag.equals("Ringmii")
                            || tag.equals("B31S")
                            || tag.equals("500S")) {
                        setB30PhoneMsg("");
                    }

                }

                phoneCursor.close();
            } else {
                if (!WatchUtils.isEmpty(tag)
                        && (tag.equals("B30")
                        || tag.equals("B36"))
                        || tag.equals("B31")
                        || tag.equals("Ringmii")
                        || tag.equals("B31S")
                        || tag.equals("500S")) {
                    setB30PhoneMsg("");
                }
                // MyApp.getmW30SBLEManage().notifacePhone(pName,0x01);
            }
        } catch (Exception error) {
            error.printStackTrace();
        }

    }

    //发送来电指令
    private void setB30PhoneMsg(String peopleName) {
        if (MyCommandManager.DEVICENAME != null) {

            boolean callPhone = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISCallPhone, true);//来电
            if (!callPhone)
                return;
            ContentSetting contentSetting = new ContentPhoneSetting(ESocailMsg.PHONE, peopleName, phoneNumber);
            MyApp.getInstance().getVpOperateManager().sendSocialMsgContent(iBleWriteResponse, contentSetting);
        }

    }

    /**
     * B30电话挂断
     */
    private void setB30DisPhone() {
        if (MyCommandManager.DEVICENAME != null) {
            MyApp.getInstance().getVpOperateManager().offhookOrIdlePhone(iBleWriteResponse);
        }
        boolean isTrue = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), "phone_status", false);
        if (isTrue) {
            AudioManager audioManager = (AudioManager) MyApp.getInstance().getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
            if (audioManager != null) {
                audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                // audioManager.getStreamVolume(AudioManager.STREAM_RING);
                Log.d("SilentListenerService", "RINGING 取消静音");
            }
        }


    }

    private IBleWriteResponse iBleWriteResponse = new IBleWriteResponse() {
        @Override
        public void onResponse(int i) {

        }
    };


}
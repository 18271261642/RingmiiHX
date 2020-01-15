package com.guider.healthring.activity.wylactivity.wyl_util.service;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.telephony.SmsMessage;
import android.util.Log;
import com.guider.healthring.Commont;
import com.guider.healthring.MyApp;
import com.guider.healthring.bleutil.MyCommandManager;
import com.guider.healthring.siswatch.utils.WatchUtils;
import com.guider.healthring.util.SharedPreferencesUtils;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
import com.veepoo.protocol.model.enums.ESocailMsg;
import com.veepoo.protocol.model.settings.ContentSetting;
import com.veepoo.protocol.model.settings.ContentSmsSetting;


/**
 * 短信接收广播
 * Created by Admin
 * Date 2019/6/26
 */
public class NewSmsBroadCastReceiver extends BroadcastReceiver {

    private static final String TAG = "NewSmsBroadCastReceiver";


    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String msgStr = (String) msg.obj;
            Log.e(TAG,"------msgStr="+msgStr);
            sendMsgToDevice(msgStr);
        }
    };

    private void sendMsgToDevice(String msgStr) {
        String saveBleName = (String) SharedPreferencesUtils.readObject(MyApp.getContext(), Commont.BLENAME);
        if(WatchUtils.isEmpty(saveBleName))
            return;
        if(MyCommandManager.DEVICENAME == null)
            return;

        //华为和荣耀手机接收短信使用广播
        String deviceBrad = Build.BRAND;
        if(!WatchUtils.isEmpty(deviceBrad) && (deviceBrad.toLowerCase().equals("huawei")|| deviceBrad.equals("HONOR"))){

            //维亿魄
            if (WatchUtils.isVPBleDevice(saveBleName)) {  //B30,B31,B36
                boolean isMSG = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISMsm, true);
                if (isMSG) sendB30Mesage(ESocailMsg.SMS, "MMS", msgStr);
            }
        }

    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            Bundle bundle = intent.getExtras();
            String format = intent.getStringExtra("format");
            if(bundle == null)
                return;

            Object[] object=(Object[]) bundle.get("pdus");
            if(object == null)
                return;
            StringBuilder sb=new StringBuilder();

            for(Object obs : object){
                byte[] pusMsg = (byte[]) obs;
                SmsMessage sms = SmsMessage.createFromPdu(pusMsg,format == null ? "":format);
                String mobile = sms.getOriginatingAddress();//发送短信的手机号
                String content = sms.getMessageBody();//短信内容
                sb.append(mobile +  content);

            }

            Message message = handler.obtainMessage();
            message.obj = sb.toString();
            message.what = 1001;
            handler.sendMessage(message);
        }catch (Exception e){
            e.printStackTrace();
        }

    }



    //B30的短信
    private void sendB30Mesage(ESocailMsg b30msg, String appName, String context) {
        if (MyCommandManager.DEVICENAME != null) {
            ContentSetting msgConn = new ContentSmsSetting(b30msg, appName, context);
            MyApp.getInstance().getVpOperateManager().sendSocialMsgContent(iBleWriteResponse, msgConn);
        }

    }

    private IBleWriteResponse iBleWriteResponse = new IBleWriteResponse() {
        @Override
        public void onResponse(int i) {

        }
    };


}

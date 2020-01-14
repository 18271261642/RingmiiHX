package com.guider.healthring.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;

import com.android.internal.telephony.ITelephony;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by An on 2018/9/19.
 */

public class PhoneUtile {

    /**
     * 从TelephonyManager中实例化ITelephony,并返回
     */
    static public ITelephony getITelephony(TelephonyManager telMgr)
            throws Exception {
        Method getITelephonyMethod = telMgr.getClass().getDeclaredMethod(
                "getITelephony");
        getITelephonyMethod.setAccessible(true);// 私有化函数也能使用
        return (ITelephony) getITelephonyMethod.invoke(telMgr);
    }

    //自动挂断
    public static void endPhone(TelephonyManager tm) {
        try {
            ITelephony iTelephony;
            Method getITelephonyMethod = TelephonyManager.class
                    .getDeclaredMethod("getITelephony", (Class[]) null);
            getITelephonyMethod.setAccessible(true);
            iTelephony = (ITelephony) getITelephonyMethod.invoke(tm,
                    (Object[]) null);
            // 挂断电话
            iTelephony.endCall();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void endCall() {
        try {
            Class<?> clazz = Class.forName("android.os.ServiceManager");
            Method method = clazz.getMethod("getService", String.class);
            IBinder ibinder = (IBinder) method.invoke(null, Context.TELEPHONY_SERVICE);
            ITelephony iTelephony = ITelephony.Stub.asInterface(ibinder);
            iTelephony.endCall();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 挂断电话
     *
     * @param context
     */
    public static void endCall(Context context) {
        try {
            Object telephonyObject = getTelephonyObject(context);
            if (null != telephonyObject) {
                Class telephonyClass = telephonyObject.getClass();

                Method endCallMethod = telephonyClass.getMethod("endCall");
                endCallMethod.setAccessible(true);

                endCallMethod.invoke(telephonyObject);
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

    }

    private static Object getTelephonyObject(Context context) {
        Object telephonyObject = null;
        try {
            // 初始化iTelephony
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            // Will be used to invoke hidden methods with reflection
            // Get the current object implementing ITelephony interface
            Class telManager = telephonyManager.getClass();
            Method getITelephony = telManager.getDeclaredMethod("getITelephony");
            getITelephony.setAccessible(true);
            telephonyObject = getITelephony.invoke(telephonyManager);
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return telephonyObject;
    }


    /**
     * 发送短信
     *
     * @param phoneNumber 电话
     * @param message     内容
     */
    public static synchronized void sendMsg(String phoneNumber, String message) {

        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNumber, null, message, null, null);
//        if (message.length() > 120) {
//            ArrayList<String> msgs = smsManager.divideMessage(message);
//            for (int i = 0; i < msgs.size(); i++) {
//                smsManager.sendTextMessage(phoneNumber, null, msgs.get(i), null, null);
//            }
//        } else {
//            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
//        }

    }


    //---sends an SMS message to another device---
//    public static void sendSMS(String phoneNumber, String message) {
    public static void sendSMS(String phoneNumber, String message) {

//        synchronized (MyApp.getInstance()) {
//            String SENT = "SMS_SENT";
//            String DELIVERED = "SMS_DELIVERED";
//
//            PendingIntent sentPI = PendingIntent.getBroadcast(MyApp.getInstance(), 0,
//                    new Intent(SENT), 0);
//
//            PendingIntent deliveredPI = PendingIntent.getBroadcast(MyApp.getInstance(), 0,
//                    new Intent(DELIVERED), 0);
//
//            //---什么时候发送短信---
//            registerReceiver(new BroadcastReceiver() {
//                @Override
//                public void onReceive(Context arg0, Intent arg1) {
//                    switch (getResultCode()) {
//                        case Activity.RESULT_OK:
////                            Toast.makeText(MyApp.getInstance(), "SMS sent",
////                                    Toast.LENGTH_SHORT).show();
//                            Log.d("--------短信", "SMS sent");
//                            break;
//                        case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
////                            Toast.makeText(MyApp.getInstance(), "Generic failure",
////                                    Toast.LENGTH_SHORT).show();
//                            Log.d("--------短信", "Generic failure");
//                            break;
//                        case SmsManager.RESULT_ERROR_NO_SERVICE:
////                            Toast.makeText(MyApp.getInstance(), "No service",
////                                    Toast.LENGTH_SHORT).show();
//                            Log.d("--------短信", "No service");
//                            break;
//                        case SmsManager.RESULT_ERROR_NULL_PDU:
////                            Toast.makeText(MyApp.getInstance(), "Null PDU",
////                                    Toast.LENGTH_SHORT).show();
//                            Log.d("--------短信", "Null PDU");
//                            break;
//                        case SmsManager.RESULT_ERROR_RADIO_OFF:
////                            Toast.makeText(MyApp.getInstance(), "Radio off",
////                                    Toast.LENGTH_SHORT).show();
//                            Log.d("--------短信", "Radio off");
//                            break;
//                    }
//                }
//            }, new IntentFilter(SENT));
//
//            //---什么时候发送短信---
//            registerReceiver(new BroadcastReceiver() {
//                @Override
//                public void onReceive(Context arg0, Intent arg1) {
//                    switch (getResultCode()) {
//                        case Activity.RESULT_OK:
////                            Toast.makeText(MyApp.getInstance(), "SMS delivered",
////                                    Toast.LENGTH_SHORT).show();
//                            Log.d("--------短信", "SMS delivered");
//                            break;
//                        case Activity.RESULT_CANCELED:
////                            Toast.makeText(MyApp.getInstance(), "SMS not delivered",
////                                    Toast.LENGTH_SHORT).show();
//                            Log.d("--------短信", "SMS not delivered");
//                            break;
//                    }
//                }
//            }, new IntentFilter(DELIVERED));
//
//            SmsManager sms = SmsManager.getDefault();
//            sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
//        }
            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(phoneNumber, null, message, null, null);
    }


    public static int SIMTYPE(Context context) {
        int simType = 0; //移动，联通
        TelephonyManager telManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        /** 获取SIM卡的IMSI码
         * SIM卡唯一标识：IMSI 国际移动用户识别码（IMSI：International Mobile Subscriber Identification Number）是区别移动用户的标志，
         * 储存在SIM卡中，可用于区别移动用户的有效信息。IMSI由MCC、MNC、MSIN组成，其中MCC为移动国家号码，由3位数字组成，
         * 唯一地识别移动客户所属的国家，我国为460；MNC为网络id，由2位数字组成，
         * 用于识别移动客户所归属的移动网络，中国移动为00，中国联通为01,中国电信为03；MSIN为移动客户识别码，采用等长11位数字构成。
         * 唯一地识别国内GSM移动通信网中移动客户。所以要区分是移动还是联通，只需取得SIM卡中的MNC字段即可
         */
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            return 0;
        }
        String imsi = telManager.getSubscriberId();
        if (imsi != null) {
            if (imsi.startsWith("46000") || imsi.startsWith("46002") || imsi.startsWith("46004") || imsi.startsWith("46007")) {//因为移动网络编号46000下的IMSI已经用完，所以虚拟了一个46002编号，134/159号段使用了此编号
                //中国移动
                simType = 0;
            } else if (imsi.startsWith("46001") || imsi.startsWith("46006") || imsi.startsWith("46009")) {
                //中国联通
                simType = 0;
            }
//            else if (imsi.startsWith("46003") || imsi.startsWith("46005") || imsi.startsWith("46011")) {
//                //中国电信
//                simType = 1;
//            }
            else {//中国电信 和其他
                simType = 1;
            }
        }
        return simType;
    }

}

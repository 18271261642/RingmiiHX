package com.guider.healthring.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 获取手机系统内部信息工具类
 * Created by yangbin on 3/2/2017.
 */

public class GetSystemInfoUtil {


    /**
     * 获取当前手机系统版本号
     *
     * @return 系统版本号
     */
    public static String getSystemVersion() {
        return Build.DISPLAY;
        //return android.os.Build.VERSION.RELEASE;

    }


    /**
     * 获取手机型号
     *
     * @return 手机型号
     */
    public static String getSystemModel() {
        return Build.MODEL;
    }

    /**
     * 获取手机厂商
     *
     * @return 手机厂商
     */
    public static String getDeviceBrand() {
        return Build.BRAND;
    }


    /**
     * 获取SN
     *
     * @return
     */
    public static String getSn(Context ctx) {
        String serial = null;
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);
            serial = (String) get.invoke(c, "ro.serialno");

        } catch (Exception ignored) {

        }

        return serial;
    }

    /**
     * 系统4.0的时候
     * 获取手机IMEI 或者Meid
     *
     * @return 手机IMEI
     */
    public static String getImeiOrMeid(Context ctx) {
        TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Activity.TELEPHONY_SERVICE);
        if (tm != null) {
            if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return null;
            }
            return tm.getDeviceId();
        }


        return null;
    }

    /**
     * 拿到imei或者meid后判断是有多少位数
     *
     * @param ctx
     * @return
     */
    public static int getNumber(Context ctx) {
        int count = 0;
        long number = Long.parseLong(getImeiOrMeid(ctx).trim());

        while (number > 0) {
            number = number / 10;
            count++;
        }
        return count;
    }


    /**
     * Flyme 说 5.0 6.0统一使用这个获取IMEI IMEI2 MEID
     *
     * @param ctx
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public static Map getImeiAndMeid(Context ctx) {
        Map<String, String> map = new HashMap<String, String>();
        TelephonyManager mTelephonyManager = (TelephonyManager) ctx.getSystemService(Activity.TELEPHONY_SERVICE);
        Class<?> clazz = null;
        Method method = null;//(int slotId)
        if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;
        }
        try {
            clazz = Class.forName("android.os.SystemProperties");
            method = clazz.getMethod("get", String.class, String.class);
            String gsm = (String) method.invoke(null, "ril.gsm.imei", "");


            String meid = (String) method.invoke(null, "ril.cdma.meid", "");
            map.put("meid", meid);
            if (!TextUtils.isEmpty(gsm)) {
                //the value of gsm like:xxxxxx,xxxxxx
                String imeiArray[] = gsm.split(",");
                if (imeiArray != null && imeiArray.length > 0) {
                    map.put("imei1", imeiArray[0]);

                    if (imeiArray.length > 1) {
                        map.put("imei2", imeiArray[1]);
                    } else {

                        map.put("imei2", mTelephonyManager.getDeviceId(1));
                    }
                } else {
                    map.put("imei1", mTelephonyManager.getDeviceId(0));
                    map.put("imei2", mTelephonyManager.getDeviceId(1));
                }
            } else {
                map.put("imei1", mTelephonyManager.getDeviceId(0));
                map.put("imei2", mTelephonyManager.getDeviceId(1));

            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return map;
    }


    //获取手机的唯一标识
    public String getPhoneSign(Context ctx) {
        StringBuilder deviceId = new StringBuilder();
        // 渠道标志
        deviceId.append("a");
        try {
            //IMEI（imei）
            TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);

            if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return null;
            }
            String imei = tm.getDeviceId();
            if (!TextUtils.isEmpty(imei)) {
                deviceId.append("imei");
                deviceId.append(imei);
                return deviceId.toString();
            }
            //序列号（sn）
            String sn = tm.getSimSerialNumber();
            if (!TextUtils.isEmpty(sn)) {
                deviceId.append("sn");
                deviceId.append(sn);
                return deviceId.toString();
            }
            //如果上面都没有， 则生成一个id：随机码
            String uuid = getUUID(ctx);
            if (!TextUtils.isEmpty(uuid)) {
                deviceId.append("id");
                deviceId.append(uuid);
                return deviceId.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            deviceId.append("id").append(getUUID(ctx));
        }
        return deviceId.toString();
    }

    /**
     * 得到全局唯一UUID
     */
    private String uuid;

    public String getUUID(Context ctx) {
        SharedPreferences mShare = ctx.getSharedPreferences("uuid", Context.MODE_PRIVATE);
        if (mShare != null) {
            uuid = mShare.getString("uuid", "");
        }
        if (TextUtils.isEmpty(uuid)) {
            uuid = UUID.randomUUID().toString();
            mShare.edit().putString("uuid", uuid).commit();
        }
        return uuid;
    }
}

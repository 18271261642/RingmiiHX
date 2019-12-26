package hat.bemo.setting;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by apple on 2017/8/21.
 */

public class SharedPreferences_status {
    private static SharedPreferences sp;
    private static Context context;

    public SharedPreferences_status(Context context){
        this.context = context;
    }

    //Mark 手機寬
    public static void SaveWidth(Context context, int value) {
        try{
            getInstances(context).edit().putInt("phonewidth", value).commit();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    //Mark 手機寬
    public static int GetWidth(Context context) {
        return getInstances(context).getInt("phonewidth", 0);
    }

    //Mark 手機高
    public static void SaveHeight(Context context, int value) {
        try{
            getInstances(context).edit().putInt("phoneheight", value).commit();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    //Mark 手機高
    public static int GetHeight(Context context) {
        return getInstances(context).getInt("phoneheight", 0);
    }

    private static SharedPreferences sp2;
    public static final String PreferenceName = "Gcare800";

    private static SharedPreferences getInstances(Context context){
        if(sp2 == null){
            sp2 = context.getSharedPreferences(PreferenceName, context.MODE_PRIVATE);
        }
        return sp2;
    }

    //Mark 定位用
    //set wifi開關排程
    public static void setWifi_status(Context context, String value) {
        try{
            getInstances(context).edit().putString("Wifi_status", value).commit();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    //get wifi開關排程
    public static String getWifi_status(Context context) {
        return getInstances(context).getString("Wifi_status", "");
    }

    //set gps開關排程
    public static void setGps_status(Context context, String value) {
        try{
            getInstances(context).edit().putString("Gps_status", value).commit();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    //get gps開關排程
    public static String getGps_status(Context context) {
        return getInstances(context).getString("Gps_status", "");
    }

    //set 室內判斷GPS
    public static void SetLat(Context context, String value) {
        try{
            getInstances(context).edit().putString("Lat", value).commit();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    //get 室內判斷GPS
    public static String GetLat(Context context) {
        return getInstances(context).getString("Lat", "0");
    }

    //set 室內判斷GPS
    public static void SetLong(Context context, String value) {
        try{
            getInstances(context).edit().putString("Long", value).commit();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    //get 室內判斷GPS
    public static String GetLong(Context context) {
        return getInstances(context).getString("Long", "0");
    }

    //Mark 時間
    //get時間格式
    @SuppressWarnings("static-access")
    public static String Get_TimeFormat(String filename, String key) {
        String value = "TIME_0";
        sp = context.getSharedPreferences(filename, context.MODE_PRIVATE);
        value = sp.getString(key, value);
        return value;
    }

    //set時間格式
    @SuppressWarnings("static-access")
    public static void save_TimeFormat(String filename, String key, String value) {
        try{
            sp = context.getSharedPreferences(filename, context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(key, value);
            editor.commit();
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    //set日期格式
    @SuppressWarnings("static-access")
    public static void save_DateFormat(String filename, String key, String value) {
        try{
            sp = context.getSharedPreferences(filename, context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(key, value);
            editor.commit();
        }catch(Exception e){}

    }

    //get日期格式
    @SuppressWarnings("static-access")
    public static String Get_DateFormat(String filename, String key) {
        String value = "DATE_1";
        sp = context.getSharedPreferences(filename, context.MODE_PRIVATE);
        value = sp.getString(key, value);
        return value;
    }

    //Mark IMEI
    //set IMEI
    @SuppressWarnings("static-access")
    public static void save_IMEI(Context context, String value) {
        try{
            getInstances(context).edit().putString("IMEI", value).commit();
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    //get IMEI
    @SuppressWarnings("static-access")
    public static String Get_IMEI(Context context) {
        return getInstances(context).getString("IMEI", "864198028820949");
//        return getInstances(context).getString("IMEI", "865717050001315");
    }

}

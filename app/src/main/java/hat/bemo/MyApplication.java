package hat.bemo;

import android.content.Context;
import android.content.Intent;
import android.view.WindowManager;

import com.guider.healthring.MyApp;
import com.guider.healthring.util.SharedPreferencesUtils;

import hat.bemo.APIupload.Controller;
import hat.bemo.BlueTooth.blegatt.baseService.BLEScanService;
import hat.bemo.location.GPSParameters;
import hat.bemo.setting.SharedPreferences_status;

/**
 * Created by apple on 2017/8/17.
 */

public class MyApplication extends MyApp {

    public static Context context;
    public static GPSParameters gps_parameters;
    public static Controller cro = new Controller();

    public static String imei = "";

    public final static String SEND_OK_MESSAGE = "send.ok.message";
    public static final String PARAMETERS_KEY = "KEY";


    private WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();

    public WindowManager.LayoutParams getMywmParams() {
        return wmParams;
    }

    @SuppressWarnings({"static-access"})
    public void onCreate() {
        super.onCreate();

        MyApplication.context = getApplicationContext();
        SharedPreferences_status.save_IMEI(MyApplication.context, "");
        gps_parameters = new GPSParameters();


        boolean isBemo = (boolean) SharedPreferencesUtils.getParam(getApplicationContext(),
                "bemo_switch",false);
        if(isBemo){
            try {
                Intent mIntent = new Intent(this, BLEScanService.class);
                startService(mIntent);

                Intent Intent2 = new Intent(this, MyService.class);
                startService(Intent2);

            }catch (Exception e){
                e.printStackTrace();
            }
        }


//        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);//取得本機UUID服務
//        imei = telephonyManager.getDeviceId();
//        imei = "864198028829494";

//        String IMEI;
//        IMEI = SharedPreferences_status.Get_IMEI(MyApplication.context);
//        System.out.println("Mark IMEI = " + IMEI);

//        try {
//            Intent mIntent = new Intent(this, BLEScanService.class);
//            startService(mIntent);
//
//            Intent Intent2 = new Intent(this, MyService.class);
//            startService(Intent2);
//        } catch (Error error) {
//
//        }

//        bindService(new Intent(this, BLEScanService.class), mConnection, Service.BIND_AUTO_CREATE);
//
//        bindService(new Intent(this, MyService.class), mConnection, Service.BIND_AUTO_CREATE);

    }

//    private ServiceConnection mConnection = new ServiceConnection() {
//
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//        }
//
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
//        }
//    };

    static class BatteryInformation {
        static int Voltage;
        static float VoltagePercent;
    }

}

package hat.bemo;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.RequiresApi;
import android.util.Log;

import hat.bemo.APIupload.ChangeDateFormat;
import hat.bemo.APIupload.Controller;
import hat.bemo.APIupload.Update;
import hat.bemo.DataBase.Insert;
import hat.bemo.VO.VO_0x30;
import hat.bemo.location.GPSManager;
import hat.bemo.location.GSM_CellLocation;
import hat.bemo.location.GpsController;
import hat.bemo.location.WifiAdminManager;
import hat.bemo.setting.ChildThread;

/**
 * Created by apple on 2017/10/17.
 */

public class UpdateAlarmLoc {

    public static String GPStype="General";

    private GSM_CellLocation gsm;
    private WifiAdminManager wifi;
    private GPSManager gps;
    private MarkLocation mgps;

    private String MCC="0";
    private String MNC="0";
    private String LAC="0";
    private String CELL_ID="0";
    private String RSSI="-79";

    private String WIFI_MAC="0";
    private String WIFI_Signal_dB="0";
    private String WIFI_Channel="0";

    private String gps_status = "1";
    private String LAT="";
    private String LONG="";
    private String address="";
    private String Accuracy = "0";
    private String createdate;

    private Insert insert = new Insert();
    private VO_0x30 vo0x30 = new VO_0x30();
    private Update up = new Update();
//    private static Get0x30 get0x30;
    private String Emg_Date;
    private String[] ITEMNO = {"1"};
    private Handler Markhandler;


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public UpdateAlarmLoc(Context context) {

        get0x20();

//        getGSM();
//        getWIFI();
//        getGPS();

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void get0x20(){
        Intent batteryIntent = MyApplication.context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        MyApplication.BatteryInformation.Voltage = batteryIntent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0);
        MyApplication.BatteryInformation.VoltagePercent = (((float)level / (float)scale) * 100.0f);
        System.out.println("Mark 0x20 get?");
        Emg_Date = ChangeDateFormat.CreateDate();
        createdate = ChangeDateFormat.CreateDate();
        Log.e("get0x20", "get0x20");
//        getGSM();
//        getGPS();
        getMsg();
        setGPS();
    }

    public void getMsg() {
        createdate = ChangeDateFormat.CreateDate();
        wifi = new WifiAdminManager();
    }

    private void setGPS() {
        //Mark 先測試只有GPS 固定值!!
        mgps = new MarkLocation(3);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void getGSM(){

        gsm = new GSM_CellLocation();
        gsm.setOnGSMListeners(new GSM_CellLocation.OnGSMListeners() {

            @Override
            public void onGsmParam(String mcc, String mnc, String lac, String cell_id, String rssi) {
                MCC = mcc;
                MNC = mnc;
                LAC = lac;
                CELL_ID = cell_id;
                RSSI = rssi;

                System.out.println("Mark MCC = " + MCC + ", MNC = " + MNC + ", LAC = " + LAC + ", CELL_ID = " + CELL_ID + ", RSSI = " + RSSI);
            }
        });
        gsm.GsmLocation();

    }

    private void getWIFI(){

        wifi = new WifiAdminManager();
        wifi.setOnWIFIListeners(new WifiAdminManager.OnWIFIListeners() {

            @Override
            public void onWifiParam(String wifi_mac, String wifi_signal_dB, String wifi_channel) {
                WIFI_MAC = wifi_mac;
                WIFI_Signal_dB = wifi_signal_dB;
                WIFI_Channel = wifi_channel;
                System.out.println("Mark Wifi_MAC = " + WIFI_MAC + ", WIFI_dB = " + WIFI_Signal_dB + ", WIFI_Chan = " + WIFI_Channel);
            }
        });
        wifi.WifiScan();

    }

    private void getGPS(){

        gps = new GPSManager("SOS", "SOS");
        gps.setOnGPSListeners(new GPSManager.OnGPSListeners() {

            @Override
            public void onGPSParam(String lat, String Long, String Address, String GPS_status, String accuracy) {
                LAT = lat;
                LONG = Long;
                address = Address;
                gps_status = GPS_status;
                Accuracy = accuracy;
                /**
                 *@date 2015-09-22
                 */
                if(GPStype.equals("SOS")){
                    ChildThread mhandler = new ChildThread(30*1000, 1000);

                    mhandler.setOnHandlerListeners(new ChildThread.OnHandlerListeners() {

                        @Override
                        public void onHandlerParam() {
                            vo0x30.setEMG_DATE(Emg_Date);
                            vo0x30.setGPS_LAT(LAT);
                            vo0x30.setGPS_LNG(LONG);
                            vo0x30.setGPS_ADDRESS(address);
                            vo0x30.setGPS_ACCURACY(Accuracy);
                            vo0x30.setGPS_STATUS(gps_status);
                            vo0x30.setMCC(MCC);
                            vo0x30.setMNC(MNC);
                            vo0x30.setLAC(LAC);
                            vo0x30.setCELL_ID(CELL_ID);
                            vo0x30.setRSSI(RSSI);
                            vo0x30.setWIFI_MAC(WIFI_MAC);
                            vo0x30.setWIFI_Signal_dB(WIFI_Signal_dB);
                            vo0x30.setWIFI_Channel(WIFI_Channel);
                            vo0x30.setCREATE_DATE(createdate);
                            insert.insert_0x30(MyApplication.context, vo0x30);
                            MyApplication.cro.LoData(MyApplication.context, Controller.type_0x30);
                            GpsController.setGpsStatus(MyApplication.context, false);
                            GPStype = "General";

                            System.out.println("Mark 0x30 up?");

                        }
                    });
                }
                else{
                    ChildThread mhandler = new ChildThread(30*1000, 1000);

                    mhandler.setOnHandlerListeners(new ChildThread.OnHandlerListeners() {

                        @Override
                        public void onHandlerParam() {
                            up.up_0x20(MyApplication.context, ITEMNO, Emg_Date, LAT, LONG, address, String.valueOf(Accuracy),
                                    gps_status, MCC, MNC, LAC, CELL_ID, RSSI, WIFI_MAC, WIFI_Signal_dB,
                                    WIFI_Channel, createdate);
                            MyApplication.cro.LoData(MyApplication.context, Controller.type_0x20);
                            GPStype = "SOS";
                            Message m = new Message();
                            m.obj = "SOS";
                            BaseActivity.mdHandler.sendMessage(m);

                            System.out.println("Mark 0x20 up?");

                        }
                    });
                }
            }
        });
        mgetGPS();

    }

    private void mgetGPS(){
        /**
         * 客製化功能 定位模式
         * */
            gps.testLocationProvider("0x00");

//            gps.startBaiduLocation();


    }

    public static GPSManager.OnGPSListeners mListener;

    public void setOnGPSListeners(GPSManager.OnGPSListeners ongpslisteners) {
        mListener = ongpslisteners;
    }

}

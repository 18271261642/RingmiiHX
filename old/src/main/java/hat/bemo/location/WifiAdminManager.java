package hat.bemo.location;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.CountDownTimer;
import android.os.Looper;

import java.util.List;

import hat.bemo.MyApplication;

/**
 * Created by apple on 2017/10/23.
 */

public class WifiAdminManager {


    private WifiAdmin wifiAdmin;
    private List<ScanResult> wifiResult;
    private String WIFI_MAC="0";
    private String WIFI_Signal_dB="0";
    private String WIFI_Channel="0";
    private int level = 0;
    private int level_Sequence;
    private WifiManager mWifiManager ;
    private static MyCount mc;
    private String wifitype;

    public interface OnWIFIListeners {
        void onWifiParam(String wifi_mac, String wifi_signal_dB, String wifi_channel);
    }

    public OnWIFIListeners mListener;

    public void setOnWIFIListeners(OnWIFIListeners onwifilisteners){
        mListener = onwifilisteners;
    }

    @SuppressWarnings("static-access")
    public WifiAdminManager(){
        wifiAdmin = new WifiAdmin(MyApplication.context);
        mWifiManager = (WifiManager) MyApplication.context.getSystemService(MyApplication.context.WIFI_SERVICE);
        mWifiManager.setWifiEnabled(true);
//		WifiSwitch();
    }

    class MyCount extends CountDownTimer {
        public MyCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            new Thread(new Runnable(){
                @Override
                public void run() {
                    Looper.prepare();
                    mWifiManager.setWifiEnabled(false);
                    Looper.loop();
                }
            }).start();
        }

        @Override
        public void onTick(long millisUntilFinished) {
//        	System.out.println("WIFI開關排程: "+millisUntilFinished / 1000+"S");
        }
    }

    public void WifiScan(){
        try{
            wifiResult = wifiAdmin.startScan2();
            if(wifiResult != null) {
                StringBuffer MAC_List = new StringBuffer();
                StringBuffer WIFI_Signal_dB_List = new StringBuffer();
                StringBuffer WIFI_Channel_List = new StringBuffer();
                int[] Level_Array = new int[wifiResult.size()];
                ScanResult result;
                int i, j, temp;

                for (int k=0; k<wifiResult.size(); k++) {
                    result = wifiResult.get(k);

                    WIFI_MAC = result.BSSID;
                    System.out.println(WIFI_MAC);
                    if(WIFI_MAC == null | WIFI_MAC.equals("")){
                        WIFI_MAC = "00:00:00:00:00:00";
                    }

                    if(WIFI_MAC.equals("00:00:00:00:00:00")){
                        WIFI_MAC = "";
                    }

                    level = result.level;
//					System.out.println(level);
                    Level_Array[k] = level;

                    WIFI_Channel = String.valueOf((result.frequency));
//					System.out.println(WIFI_Channel);
                    if(WIFI_Channel == null | WIFI_Channel.equals("")){
                        WIFI_Channel = "0";
                    }
                    WIFI_Channel_List.append(WIFI_Channel).append(";");
                    MAC_List.append(WIFI_MAC).append(";");
                }

                for(i=Level_Array.length-1; i>=0; i=i-1){
                    for (j=0; j<i; j=j+1){
                        if (Level_Array[j] > Level_Array[i]){
                            temp = Level_Array[j];
                            Level_Array[j] = Level_Array[i];
                            Level_Array[i] = temp;
                        }
                    }
                }

                for(i=0; i<Level_Array.length; i=i+1){
                    int lev = Level_Array[i];
                    level_Sequence = Level_Array[i] = lev;
                    //				Log.e("level_Sequence", level_Sequence+"");
                    WIFI_Signal_dB = String.valueOf(level_Sequence);
                    WIFI_Signal_dB_List.append(WIFI_Signal_dB).append(";");
                }

                WIFI_MAC = MAC_List.toString();
                WIFI_Signal_dB = WIFI_Signal_dB_List.toString();
                WIFI_Channel = WIFI_Channel_List.toString();

                //			mListener.onWifiParam(WIFI_MAC, WIFI_Signal_dB, WIFI_Channel);
                mListener.onWifiParam(WIFI_MAC, WIFI_Signal_dB, WIFI_Channel);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }


}

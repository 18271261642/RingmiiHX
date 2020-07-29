package cn.wuweikang;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.wehealth.ecg.EcgDataParser;

import bluetooth.ClassicBluetoothClient;


public class WuweikangData implements EcgDataParser.EcgDataGetListener {

    private GetEcgDataCallback callback;
    public void setGetEcgData(GetEcgDataCallback listener) {
        this.callback = listener;
    }
    public interface GetEcgDataCallback {
        void getEcgData(int[] ints, int i, boolean[] booleans, boolean b);
    }

    @Override
    public void GetEcgData(int[] ints, int i, boolean[] booleans, boolean b) {
        if (callback != null) {
            callback.getEcgData(ints, i, booleans, b);
        }
    }

    protected WuweikangData() {}
    private volatile static WuweikangData instance;
    public static WuweikangData getInstance() {
        if (instance != null) {
            return instance;
        }
        if (instance == null) {
            synchronized (WuweikangData.class) {
                if (instance == null) {
                    instance = new WuweikangData();
                }
            }
        }
        return instance;
    }

    BTConnectStreamThread thread;
    public void connect(final Context context , final BluetoothDevice device, final Handler handler) {
        if (thread != null) {
            thread.stopBlueTooth();
        }
        ClassicBluetoothClient.boud(context, device );
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                thread = new BTConnectStreamThread(context, device, WuweikangData.this);
                thread.setHandler(handler);
                thread.start();
            }
        }, 3000);
    }

    public void disconnect() {
        if (thread != null) {
            thread.stopBlueTooth();
        }
    }
}

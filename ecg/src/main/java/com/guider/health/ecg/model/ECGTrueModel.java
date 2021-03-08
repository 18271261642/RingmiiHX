package com.guider.health.ecg.model;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;
import com.tencent.bugly.crashreport.BuglyLog;
import com.tencent.bugly.crashreport.CrashReport;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.net.ssl.HttpsURLConnection;

import static android.content.Context.BLUETOOTH_SERVICE;

public class ECGTrueModel {


    private static Activity context;

    private static ECGTrueModel.BT_Back listener;


    /////廠商//////
    private final static String bluetooth_Tag = "haix";
    private static final UUID CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR_UUID = UUID.fromString("49535343-FE7D-4AE5-8FA9-9FAFD205E455");
    private static final String UUID_CHAR6 = "49535343-1E4D-4BD9-BA61-23C647249616";
    private BluetoothGattCharacteristic gattCharacteristic_char6;
    static BluetoothGatt mBluetoothGatt;

    //儲存藍芽
    static ArrayList<BluetoothDevice> newBluetoothDevice = new ArrayList<BluetoothDevice>();
    static BluetoothDevice last_device = null;  //最後連線設備
    static BluetoothDevice temp_device = null;  //最後連線設備
    static OutputStream outStream = null;
    static InputStream inStream = null;

    /////BLE/////

    public static void setStream(OutputStream out, InputStream in) {
        outStream = out;
        inStream = in;
    }

    static BluetoothSocket btSocket = null;


    //判斷封包長度  7 or 515
    static int datalength = 7;


    private static BluetoothManager mBluetoothManager;
    public static BluetoothAdapter mBluetoothAdapter;


    //傳輸檔案使用
    static int filesize = 0;
    static int file_index = 0;
    static ArrayList<Byte> file_temp = new ArrayList<Byte>();
    public static ArrayList<Byte> file_data = new ArrayList<Byte>();


    //判斷是否連線
    public static boolean isconnect = false;
    static boolean ischecklifefinish = true;
    static boolean canchecklife = true;
    static long checklifetime = 0;

    //上船數量
    static int list_count = 0;


    //////畫心電圖使用///////
    public static ArrayList<Entry> chartSet1Entries = new ArrayList<Entry>();

    public static LineDataSet chartSet1 = new LineDataSet(null, "");


    static double[] mX = {0.0, 0.0, 0.0, 0.0, 0.0};

    static double[] mY = {0.0, 0.0, 0.0, 0.0, 0.0};

    static int[] mStreamBuf = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20};

    static double[] mAcoef = {0.00001347408952448771,
            0.00005389635809795083,
            0.00008084453714692624,
            0.00005389635809795083,
            0.00001347408952448771};

    static double[] mBcoef = {1.00000000000000000000,
            -3.67172908916193470000,
            5.06799838673418980000,
            -3.11596692520174570000,
            0.71991032729187143000};

    static long startTime;
    static int countsix = 6;
    static int count30 = 0;

    static boolean isStop = false;//是否主動暫停

    public static boolean alreadyscan = false;

    public static String connectaddress = "";


    //////畫心電圖使用///////

    public interface BT_Back {
        void postfinish(String message);
    }

    public static void setOnPostBack(ECGTrueModel.BT_Back l) {
        listener = l;
    }

    public static void Bluetooth_init(Activity contextin) {
        context = contextin;
    }


    public static void startScan() {

        alreadyscan = true;

        isconnect = false;

        newBluetoothDevice.clear();
        mBluetoothManager = (BluetoothManager) context.getSystemService(BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();

        int postms = 1000;

        context.registerReceiver(mReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));

        if (!mBluetoothAdapter.isEnabled()) {
            Log.d(bluetooth_Tag, "enable  bluetooth");

            mBluetoothAdapter.enable();

            postms = 3000;
        } else {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);


            new android.os.Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    Log.d(bluetooth_Tag, "startScanstartScanAAA");
                    mBluetoothAdapter.startLeScan(mLeScanCallback);//開始搜尋BLE設備

//                    checkgps();

                }
            }, postms);
        }


    }

    @SuppressLint("NewApi")
    private static BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {
            context.runOnUiThread(new Runnable() { //使用runOnUiThread方法，其功能等同於WorkThread透過Handler將資訊傳到MainThread(UiThread)中，
                //詳細可進到runOnUiThread中觀察
                @Override
                public void run() {

                    if (device.getName() != null) {


                        if (!newBluetoothDevice.contains(device) && (device.getName().toString().contains("CmateH") || device.getName().toString().contains("ChakraECG") || device.getName().toString().contains("Dual-SPP"))) {


                            Log.d(bluetooth_Tag, "搜尋到CmateH設備    " + device.getName() + "    " + device.getAddress());


//                            cansearch = 1;


                            mBluetoothAdapter.stopLeScan(mLeScanCallback);
//                            mBluetoothAdapter.cancelDiscovery();

                            newBluetoothDevice.add(device);

                            checkconnect();

                        }


                    }


                }
            });
        }

    };

    public static void checkconnect() {

        Log.d("checkconnect", "checkconnect");

        if (newBluetoothDevice.size() > 0) {
            Set<BluetoothDevice> BondDevices = mBluetoothAdapter.getBondedDevices();
            Iterator<BluetoothDevice> IterDevice = BondDevices.iterator();

            int deviceindex = -1;

            while (IterDevice.hasNext()) {
                BluetoothDevice btDevice = IterDevice.next();
                if (btDevice.getName().contains("CmateH") ||
                        btDevice.getName().contains("ChakraECG") ||
                        btDevice.getName().contains("Dual-SPP")) {

                    for (int i = 0; i < newBluetoothDevice.size(); i++) {
                        if (newBluetoothDevice.get(i).getAddress().equals(btDevice.getAddress())) {
                            if (deviceindex == -1) {
                                deviceindex = i;

                            }
                        }
                    }

                    Log.d(bluetooth_Tag, "deviceindex = " + deviceindex);
                }

            }


            //都沒配對過
            if (deviceindex == -1) {
                Log.d(bluetooth_Tag, "都沒配對過");

                if (!connectaddress.equals(newBluetoothDevice.get(0).getAddress())) {

                    connectaddress = newBluetoothDevice.get(0).getAddress();

                    Log.d(bluetooth_Tag, "都沒配對過,嘗試配對");

                    mBluetoothGatt = newBluetoothDevice.get(0).connectGatt(context, false, gattCallback);

                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, "都沒配對過,嘗試配對", Toast.LENGTH_LONG).show();
                        }
                    });
                } else {

                    connectaddress = "";

                    Log.d(bluetooth_Tag, "都沒配對過,嘗試配對一次,跳出管理畫面");

                    close();

                    final Intent intent = new Intent(Intent.ACTION_MAIN, null);
                    intent.addCategory(Intent.CATEGORY_LAUNCHER);
                    final ComponentName cn = new ComponentName("com.android.settings", "com.android.settings.bluetooth.BluetoothSettings");
                    intent.setComponent(cn);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }


            } else {

                Log.d(bluetooth_Tag, "已經配對過，設備在線上");
                temp_device = newBluetoothDevice.get(deviceindex);

                last_device = temp_device;

                connectBLE();

                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "發現設備，嘗試配對中...", Toast.LENGTH_LONG).show();
                    }
                });


            }


        } else {
            Log.d(bluetooth_Tag, "未查搜尋到裝置");
            last_device = null;
        }


    }//checkconnect

    private static void connectBLE() {
        new Thread(new Runnable() {
            @Override
            public void run() {


                try {
                    final UUID SERIAL_PORT_SERVICE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

                    btSocket = temp_device.createRfcommSocketToServiceRecord(SERIAL_PORT_SERVICE_UUID);

                    btSocket.connect();


                    outStream = btSocket.getOutputStream();
                    inStream = btSocket.getInputStream();


                    int bytesAvailable = inStream.available();


                    byte[] packetBytes = new byte[bytesAvailable];
                    inStream.read(packetBytes);


                    isconnect = true;


                    listener.postfinish("connect success");


                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, "配對成功", Toast.LENGTH_LONG).show();
                        }
                    });


                } catch (IOException e) {

                    Log.d(bluetooth_Tag, "error reconnect ");

//                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
//
//
                    try {
                        btSocket.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
//
//
//                    isconnect = false;
//
//                    last_device = null;
//                    newBluetoothDevice.clear();

                    close();
                    Log.d("startScan", "startScanstartScanDDD");
                    mBluetoothAdapter.startLeScan(mLeScanCallback);//開始搜尋BLE設備


                }

            }
        }).start();
    }

    @SuppressLint("NewApi")
    private static final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            Log.d(bluetooth_Tag, "STATE_DISCONNECTED");

//            isconnect = false;
//
//            last_device = null;
//            newBluetoothDevice.clear();

            close();
            Log.d("startScan", "startScanstartScanCCC");
            mBluetoothAdapter.startLeScan(mLeScanCallback);//開始搜尋BLE設備

        }
    };


    private static final BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);

                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        //Indicates the local ECGTrueModel adapter is off.
                        Log.d(bluetooth_Tag, "STATE_OFF");
                        close();
                        break;

                    case BluetoothAdapter.STATE_TURNING_ON:
                        //Indicates the local ECGTrueModel adapter is turning on. However local clients should wait for STATE_ON before attempting to use the adapter.
                        Log.d(bluetooth_Tag, "STATE_TURNING_ON");
                        break;

                    case BluetoothAdapter.STATE_ON:
                        //Indicates the local ECGTrueModel adapter is on, and ready for use.
                        Log.d(bluetooth_Tag, "STATE_ON   scan");
                        startScan();
                        break;

                    case BluetoothAdapter.STATE_TURNING_OFF:
                        //Indicates the local ECGTrueModel adapter is turning off. Local clients should immediately attempt graceful disconnection of any remote links.
                        Log.d(bluetooth_Tag, "STATE_TURNING_OFF");
                        break;
                }
            }
        }
    };


    public static void close() {

        alreadyscan = false;
        canchecklife = false;
//        mBluetoothAdapter.stopLeScan(mLeScanCallback);


        if (btSocket != null) {
            try {

                btSocket.close();

                mBluetoothAdapter.stopLeScan(mLeScanCallback);

            } catch (IOException e) {
                e.printStackTrace();

            }
        }

        isconnect = false;
        last_device = null;
//        newBluetoothDevice.clear();

    }


    //////////////////////////刪除所有數據/////////////////////////////////////////

    private static android.os.Handler mhandler;

    ///讀取總共有幾筆
    public static void read_upload_list(final android.os.Handler read_handler) {
        mhandler = read_handler;
        new Thread(new Runnable() {
            @Override
            public void run() {


                int status = 0;


                byte[] getbyte = send_BTCommand(new byte[]{(byte) 0xaa, 0x20, 0, 0, 0, 0, 0x00});


                if (getbyte.length == 1) {
                    Log.d(bluetooth_Tag, "錯誤");
                } else {
                    list_count = byteArrayToInt(new byte[]{getbyte[2]});


                    Log.d(bluetooth_Tag, "上傳數量 = " + list_count + "");


                    ArrayList<Byte> updelist = new ArrayList<Byte>();

                    for (int i = 0; i < list_count; i++) {
                        updelist.add((byte) (i & 0xFF));
                    }


                    if (delete(updelist))
                        status = 1;

                }

                Message msg = new Message();

                msg.arg1 = status;

                read_handler.sendMessage(msg);


            }
        }).start();
    }


    public static void readbattery(final android.os.Handler handlerxx) {
        mhandler = handlerxx;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!ischecklifefinish) {
                    SystemClock.sleep(20);
                }

                while (true) {
                    byte[] ret = send_BTCommand(new byte[]{(byte) 0xaa, 0x10, 0x01, 0x01, 0x00, 0x00, 0x00});

                    if (ret.length == 1) {
                        Log.d("eeeecgpp", "停止---成功");
                        break;
                    } else if (ret[1] == 0x10) {
                        Log.d("eeeecgpp", "停止---失敗");
                        break;
                    }
                }

                int value = -1;
                byte[] getbyte = send_BTCommand(new byte[]{(byte) 0xaa, 0x03, 0, 0, 0, 0, 0x00});

                if (getbyte.length == 1) {
                    Log.d("eeeecgpp", "錯誤");
                } else {
                    value = byteArrayToInt(new byte[]{getbyte[3], getbyte[2]});
                    Log.d("eeeecgpp", "batteryPower = " + value);

                    if (value >= 1990) {
                        Log.d("eeeecgpp", "battery100");
                        value = 100;
                    } else if (value >= 1955 && value < 1990) {
                        Log.d("eeeecgpp", "battery75");
                        value = 75;
                    } else if (value >= 1920 && value < 1955) {
                        Log.d("eeeecgpp", "battery50");
                        value = 50;
                    } else if (value >= 1880 && value < 1920) {
                        Log.d("eeeecgpp", "battery25");
                        value = 25;
                    } else {
                        Log.d("eeeecgpp", "battery0");
                        value = 0;
                    }
                }

                Message msg = new Message();
                if (value <= 25) {
                    msg.arg1 = 5;
                } else {
                    msg.arg1 = 4;
                }

                msg.obj = value;
                handlerxx.sendMessage(msg);
            }
        }).start();
    }

    ///刪除列表
    public static boolean delete(ArrayList<Byte> del_index) {
        final int count = del_index.size();
        int index = 0;
        boolean ret = true;

        while (count > index) {
            byte[] send_del = send_BTCommand(new byte[]{(byte) 0xaa, 0x26, del_index.get(index), 0, 0, 0, 0});

            if (send_del.length == 1) {
                ret = false;
                break;
            }
            index++;
            final int finalIndex = index;
        }

        return ret;

    }

    //////////////////////////刪除所有數據/////////////////////////////////////////


    public static byte[] send_BTCommand(final byte[] commandByte) {
        if (outStream == null) { // 還沒開port
            Log.d(bluetooth_Tag, "尚未配對連線");
            close();
        } else {
            checklifetime = System.currentTimeMillis() + 1000;
            byte[] outBuf = commandByte;
            byte outBufSum = 0;
            for (int i = 0; i < 6; i++)
                outBufSum += outBuf[i];

            outBuf[6] = outBufSum;

            String result = "";
            for (int i = 0; i < outBuf.length; i++)
                result += "  " + Integer.toString((outBuf[i] & 0xff) + 0x100, 16).substring(1);

            //Log.d(bluetooth_Tag, "發指令 = " + result + "");

            try {
                boolean ECGrvStart = false;    // true: ECG data receiving can start
                int bytesAvailable;

                outStream.write(outBuf);

                int waitRetryN = 0;
                int waitRetryMax = 333;
                int commResentMax = 10;
                int commRetryMax = 3;

                while (!ECGrvStart) {
                    waitRetryN++;
                    if (waitRetryN % waitRetryMax == waitRetryMax - 1
                            && waitRetryN < waitRetryMax * commResentMax
                            && inStream.available() == 0) {
                        // Output the command
                        Log.d(bluetooth_Tag, "Resend Command");
                        outStream.write(outBuf);
                    }

                    if (waitRetryN > waitRetryMax * commRetryMax) {
                        Log.d(bluetooth_Tag, "Command Time Out");
                        ECGrvStart = true;

                        Message msg = new Message();
                        msg.arg1 = 2222;

                        mhandler.sendMessage(msg);

                        Log.d(bluetooth_Tag, "startScanstartScanFFF");
                        close();

                        return new byte[]{0x00};
                    }

                    bytesAvailable = inStream.available();

                    if (bytesAvailable >= datalength) {
                        datalength = 7;

                        byte[] packetBytes = new byte[bytesAvailable];
                        inStream.read(packetBytes);

                        String receive = "";

                        for (int i = 0; i < packetBytes.length; i++)
                            receive += "  " + Integer.toString((packetBytes[i] & 0xff) + 0x100, 16).substring(1);

                        ECGrvStart = true;

                        return packetBytes;
                    }
                    SystemClock.sleep(3);
                }
            } catch (IOException ex) {
                Log.d(bluetooth_Tag, "Disconnect, re_connecting");
                close();
                Log.d(bluetooth_Tag, "startScanstartScanGGG");
            }
        }
        datalength = 7;
        return new byte[]{0x00};
    }//send command

    ///////////////////////////測量/////////////////////////////////////////

    //跑波30秒
    public static void wave30(final android.os.Handler read_handler) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int status = -1;

                while (!ischecklifefinish) {
                    SystemClock.sleep(20);
                }

                byte[] outBuf = {-86, 0x10, 0x02, 0x02, 0, 0, -68};

                byte outBufSum = 0;
                for (int i = 0; i < 6; i++)
                    outBufSum += outBuf[i];

                outBuf[6] = outBufSum;

                try {
                    Log.e("VtControlTask", "start recording in app...");
                    // ECG data receiving loop
                    int bytesAvailable;
                    send_BTCommand(outBuf);

                    int outcount = 0;
                    startTime = System.currentTimeMillis();
                    count30 = 30;

                    while (true) {
                        try {
                            bytesAvailable = inStream.available();
                        } catch (IOException e) {
                            e.printStackTrace();
                            break;
                        }
                        Log.d("bytesAvailable", "bytesAvailable = " + bytesAvailable);

                        if (bytesAvailable >= 7) {  // Wait until ECG data income
                            outcount = 0;

                            final byte[] result = new byte[7];
                            inStream.read(result);

                            double ch2 = byteArrayToInt(new byte[]{result[4]}) * 128 + byteArrayToInt(new byte[]{result[5]});
                            int ch4 = getStreamLP((int) ch2);
                            if (ch4 >= 2500) {
                                ch4 = 2500;
                            } else if (ch4 <= 1500) {
                                ch4 = 1500;
                            }

                            Message msg = new Message();
                            msg.arg1 = 101;
                            msg.obj = ch4;
                            read_handler.sendMessage(msg);

                            // 测量时间
                            if ((startTime + 1000) < System.currentTimeMillis()) {
                                startTime = System.currentTimeMillis();
                                count30--;
                                Message msg1 = new Message();
                                msg1.arg1 = 102;
                                msg1.obj = count30;
                                read_handler.sendMessage(msg1);
                                //timeCount_TV.setText(""+count30+"秒");
                            }
                        }

                        if (count30 == 0) {
                            while (true) {
                                byte[] ret = send_BTCommand(new byte[]{(byte) 0xaa, 0x10, 0x01, 0x01, 0x00, 0x00, 0x00});
                                if (ret.length == 1) {
                                    break;
                                } else if (ret[1] == 0x10) {
                                    break;
                                }
                            }
                            outcount = 500;
                            break;
                        }
                        outcount++;

                        if (outcount == 150) {
//                            bytesAvailable = inStream.available();
                            final byte[] result = new byte[bytesAvailable];
                            inStream.read(result);
                            break;
                        }
                        checklifetime = System.currentTimeMillis() + 1000;
                        SystemClock.sleep(5);
                    }

                    ////清除
                    int clearcount = 0;

                    while (true) {
                        final byte[] result = new byte[bytesAvailable = inStream.available()];
                        inStream.read(result);

                        clearcount++;
                        if (clearcount == 60)
                            break;

                        SystemClock.sleep(10);
                    }

                    if (outcount >= 500) {
                        isStop = false;
                        final byte[] result = new byte[bytesAvailable];
                        inStream.read(result);
                        status = 2;
                        Log.d("sssss", "closecloseclosecloseclose");
                    } else if (isStop) {
                        isStop = false;
                        status = 100;
                    } else {
                        final byte[] result = new byte[bytesAvailable];
                        inStream.read(result);
                        Log.d("sssss", "timeouttimeout");
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                    Log.d("sssss", "oops, try end the connection due to exception");
                }

                Message msg = new Message();
                msg.arg1 = status;

                read_handler.sendMessage(msg);
            }
        }).start();

    }

    //停止量測
    public static void stopMeasure() {
        isStop = true;
        Log.d(bluetooth_Tag, "停止量測");
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!ischecklifefinish) {
                    SystemClock.sleep(20);
                }
                while (true) {
                    byte[] ret = send_BTCommand(new byte[]{(byte) 0xaa, 0x10, 0x01, 0x01, 0x00, 0x00, 0x00});

                    if (ret.length == 1) {
                        Log.d(bluetooth_Tag, "停止---失敗");
                        break;
                    } else if (ret[1] == 0x10) {
                        Log.d(bluetooth_Tag, "停止---成功");
                        break;
                    }
                }
            }
        }).start();
    }

    ///////////////////////////測量/////////////////////////////////////////


    /////////////////////////讀取上傳資料//////////////////////////////////

    public static void read_upload(final android.os.Handler read_handler) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int status = -1;
                byte[] getbyte = send_BTCommand(new byte[]{(byte) 0xaa, 0x20, 0, 0, 0, 0, 0x00});//讀取檔案上傳數量

                if (getbyte.length == 1) {
                    Log.d(bluetooth_Tag, "錯誤 -- 讀取檔案上傳數量");
                } else {
                    byte index = 0;
                    if (true) {
                        byte[] getbyte0 = send_BTCommand(new byte[]{(byte) 0xaa, 0x21, index, 0, 0, 0, 0});//讀取檔案大小

                        if (getbyte0.length == 1) {
                            Log.d(bluetooth_Tag, "讀取檔案大小--失敗");
                        } else {
                            filesize = byteArrayToInt(new byte[]{getbyte0[5], getbyte0[4], getbyte0[3], getbyte0[2]});

                            Log.d(bluetooth_Tag, "size = " + filesize);

                            byte[] getbyte1 = send_BTCommand(new byte[]{(byte) 0xaa, 0x24, index, 1, 0, 0, 0});//打開檔案

                            if (getbyte1.length == 1) {
                                Log.d(bluetooth_Tag, "打開檔案--失敗");
                            } else {
                                file_index = 0;
                                file_temp.clear();
                                file_data.clear();
                                boolean success = read_ble_upload_data(read_handler);//讀取上傳資料

                                if (success) {
                                    //讀取成功
                                    status = 10009;

                                } else {
                                    //讀取失敗
                                }
                            }
                        }
                    } else {
                        Log.d(bluetooth_Tag, "錯誤");
                    }
                }//

                Message msg = new Message();
                msg.arg1 = status;

                read_handler.sendMessage(msg);

            }
        }).start();

    }

    public static boolean read_ble_upload_data(final android.os.Handler read_handler) {
        if (file_index < (int) ((double) (filesize) / 512.0 + 0.9999) * 2) {

//            Log.d(bluetooth_Tag,"file_index = " + file_index + "    index = " + (int)((double)(filesize) / 512.0 + 0.9999) * 2);

            int x1 = 0;
            int x2 = 0;


            if (file_index < 256) {
                x1 = file_index;

                x2 = 0;
            } else {
                x1 = file_index - 256;

                x2 = 1;
            }

//            Log.d("uploadfile","x1 = " + x1 + "    x2 = " + x2);


            datalength = 515;
            byte[] getbyte = send_BTCommand(new byte[]{(byte) 0xaa, 0x25, 0x00, (byte) x1, (byte) x2, 0, 0x00});

            if (getbyte.length == 1) {
                Log.d(bluetooth_Tag, "讀取上傳資料錯誤");

                return false;
            } else // 515
            {


                for (int i = 2; i < getbyte.length - 1; i++) {
                    if (file_data.size() < filesize)
                        file_data.add(getbyte[i]);
                }


                final double percent = (double) (file_data.size()) / (double) (filesize);

                Log.d(bluetooth_Tag, "儲存資料 515  percent = " + percent);

                //((Activity)context).runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {

                //percent_TV.setText((int)(percent*100) + " %");
                Message msg = new Message();
                msg.arg1 = 10001;

                msg.obj = (int) (percent * 100);
                read_handler.sendMessage(msg);

//                    }
//                });


                file_index = file_index + 2;

                return read_ble_upload_data(read_handler);
            }


        } else {

            Log.d(bluetooth_Tag, "完成讀取檔案  file_data.size() = " + file_data.size());


//            ((Activity)context).runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
            //percent_TV.setText("100%");
            Message msg = new Message();
            msg.arg1 = 10002;

            msg.obj = 100;
            read_handler.sendMessage(msg);
//                }
//            });

            return true;
        }

    }


    /////////////////////////讀取上傳資料//////////////////////////////////


    public static int getStreamLP(int NewSample) {
        int tmp = 0;

        for (int array = 4; array >= 1; array--) {
            mX[array] = mX[array - 1];
            mY[array] = mY[array - 1];
        }

        mX[0] = (double) (NewSample);
        mY[0] = mAcoef[0] * mX[0];

        for (int i = 1; i <= 4; i++) {
            mY[0] += mAcoef[i] * mX[i] - mBcoef[i] * mY[i];
        }

        for (int array = 20; array >= 1; array--) {
            mStreamBuf[array] = mStreamBuf[array - 1];
        }

        mStreamBuf[0] = NewSample;

        tmp = mStreamBuf[20] + (2000 - (int) (mY[0]));
        return tmp;
    }


    public String getAPItoken() {
        Log.i("haix", "进入APItoken");

        String token = null;
        String jsonString = "Null--";
        try {
            // API url
            String strURL = "https://api.cmatecare.com/ECGAppApi/api/Device/Login";

            // Parameters
            HashMap mapPara = new HashMap();

            mapPara.put("Id", "TwMac00001");
            mapPara.put("Password", "Mac00001");

            // 網路呼叫以取得JSON
            jsonString = performPostCall(strURL, mapPara);
            //jsonString 为接口返回的结果

            Log.i("haix", "获取token: " + jsonString);
            BuglyLog.e("EcgTokenJson", TextUtils.isEmpty(jsonString) ? "Token=空" : jsonString);

            // For debug
            //Log.i(MY_LOG_TAG, "API return: " + jsonString);

            JSONObject jsonObj = new JSONObject(jsonString);

            String code = jsonObj.getString("StatusCode");
            token = jsonObj.getString("Token");

//            // For debug
//            Log.i(MY_LOG_TAG, "API return: Status code: " + APIstatCode + ", Token: " + strAPItoken +
//                    ", Name: " + strUserName);
        } catch (Exception e) {
            e.printStackTrace();
            CrashReport.putUserData(context, "tokenJson", TextUtils.isEmpty(jsonString) ? "Token=空" : jsonString);
            Exception je = new Exception(jsonString);
            CrashReport.postCatchedException(je);
        }

        return token;

    }


    public interface MeasureResult {
        void result(String result);
    }


    String upload_lp4_file_pattern = "{\"ProjectId\":\"4\",\"SubUserNo\":\"SubUserNo#\",\"FileName\":\"FileName#\",\"Token\":\"Token#\",\"FileBase64\":\"FileBase64#\"}";

    public void lp4Upload(String token, MeasureResult measureResult) {
        try {
            // API url
            String strURL = "https://api.cmatecare.com/ECGAppApi/api/Upload/UploadLp4";


            byte[] bytes = new byte[ECGTrueModel.file_data.size()];
            Log.i("haix", "上传数据大小: " + ECGTrueModel.file_data.size());
            for (int i = 0; i < bytes.length; i++) {
                bytes[i] = (byte) ECGTrueModel.file_data.get(i);
//                receive = receive+", "+  (bytes[i] & 0xFF) ;
            }

            String strEncFile = Base64.encodeToString(bytes, Base64.NO_WRAP);
            String fileName = getFileName();
            strEncFile = strEncFile.replaceAll("\r\n", "")
                    .replaceAll("\n", "");

            String[] patternin = new String[]{"26", fileName, token, strEncFile};

            String pattern = upload_lp4_file_pattern
                    .replace("SubUserNo#", patternin[0])
                    .replace("FileName#", patternin[1])
                    .replace("Token#", patternin[2])
                    .replace("FileBase64#", patternin[3]);

//            LP4LP4 = {"ProjectId":"4","SubUserNo":"26","FileName":"J6QE1911.lp4","Token":"64a3703a-428d-4ae8-be04-08da67dc6530","FileBase64":"xwHxCDcGNwY4B...

            Log.d("haix", "LP4LP4 = " + pattern);

            httprequest(strURL, pattern, measureResult);

        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    public void httprequest(final String PostUrl, final String pattern, final MeasureResult measureResult) {

        new Thread(new Runnable() {

            @Override
            public void run() {


                String response = "";
                BufferedReader reader = null;
                HttpURLConnection conn = null;


                try {

                    URL urlObj = new URL(PostUrl);

                    conn = (HttpURLConnection) urlObj.openConnection();
                    conn.setRequestMethod("POST");
                    //Type = "POST";
                    conn.setConnectTimeout(20000);
                    conn.setReadTimeout(20000);
                    conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    conn.setRequestProperty("Accept", "application/json");

                    conn.setDoOutput(true);


                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());


                    wr.write(pattern);
                    wr.flush();
                    wr.close();

                    int responseCode = conn.getResponseCode();


                    reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line = null;

                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }

                    response = sb.toString();
                    Log.d("haix", "upload success" + response);

                    measureResult.result(response);
                    reader.close();


//                    final String finalResponse = response;

//                    ((Activity)context).runOnUiThread(new Runnable() { //使用runOnUiThread方法，其功能等同於WorkThread透過Handler將資訊傳到MainThread(UiThread)中，
//                        //詳細可進到runOnUiThread中觀察
//                        @Override
//                        public void run() {
//
//                            content_tv.setText(finalResponse);
//                        }
//                    });


                } catch (Exception e) {

                    Log.d("error", e.toString());

                    //callbackString = "Api_Error_Code_networkerror";

                } finally {

                    if (conn != null) {
                        conn.disconnect();
                        Log.d("pp", "close");
                    }


                }

            }
        }).start();

    }


    public String performPostCall(String requestURL,
                                  HashMap<String, String> postDataParams) throws Exception {

        URL url;
        String response = "";
        try {
            url = new URL(requestURL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(60000);
            conn.setConnectTimeout(60000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            //Log.i("haix", "接口请求的数据: "+getPostDataString(postDataParams));
            writer.write(getPostDataString(postDataParams));

            writer.flush();
            writer.close();
            os.close();
            int responseCode = conn.getResponseCode();

            // For debug
            //Log.i(MY_LOG_TAG, "API HTTP return code: " + responseCode);

            Log.i("haix", "请求responseCode= " + responseCode);
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = br.readLine()) != null) {
                    response += line;

                    // For debug
                    //Log.i(MY_LOG_TAG, "API HTTP return: " + line);
                }
            } else {
                response = responseCode + "";

            }

        } catch (Exception e) {
            throw e;
        }

        return response;
    }

    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    public static int byteArrayToInt(byte[] b) {
        if (b.length == 4)
            return b[0] << 24 | (b[1] & 0xff) << 16 | (b[2] & 0xff) << 8 | (b[3] & 0xff);
        else if (b.length == 2)
            return 0x00 << 24 | 0x00 << 16 | (b[0] & 0xff) << 8 | (b[1] & 0xff);
        else if (b.length == 1)
            return 0x00 << 24 | 0x00 << 16 | (0x00 & 0xff) << 8 | (b[0] & 0xff);

        return 0;
    }


    //tired
    public static String lfhftovalue(double lfhf) {

        double nvalue = 0.0;

        if (lfhf >= 0.25 && lfhf <= 4) //green 0.25 ~ 4  --> 32 ~ 68
        {

            nvalue = ((lfhf - 0.25) / 3.75) * 36 + 32;
            return "正常";
        } else if (lfhf < 0.02 || lfhf > 50) // red

        {


            if (lfhf < 0.02) // 靠下紅
            {
                nvalue = 6.5;
            } else {
                nvalue = 93.5;
            }

            return "过劳";

        } else //yellow
        {


            if (lfhf >= 5) //靠上黃 5~50 --> 74 ~ 82
            {
                nvalue = ((lfhf - 5) / 45) * 8 + 74;
            } else // 靠下黃  0.02 ~ 0.24 --> 18 ~ 26
            {
                nvalue = ((lfhf - 0.02) / 0.22) * 8 + 18;
            }


            return "轻松";
        }


    }


    //preasure
    public static String sdnntovalue(double sdnn) {
//        let sdnn = 15.0

        double newvalue = 0.0;

        if (sdnn < 15) // red 0~15   --> 67 ~ 100
        {
            newvalue = (sdnn / 15) * 33 + 67;
            return "不佳";
        } else if (sdnn > 40 && sdnn <= 100) // green 41 ~ 100 --> 1 ~ 33
        {
            newvalue = ((sdnn - 41) / 59) * 32 + 1;

            return "正常";
        } else // yellow
        {
            if (sdnn <= 40) // 15 ~ 40  --> 34 ~ 66
            {
                newvalue = ((sdnn - 15) / 25) * 32 + 34;
            } else // > 100  --> 34 ~ 66
            {
                newvalue = (double) (((int) (sdnn) % 32)) + 34;
            }

            return "尚可";
        }

    }


    //health
    public static String pnn50tovalue(double pnn50, String sick) {


        int newint = 0;

        if (pnn50 > 0.75) {
            newint = (int) (pnn50 * 100) % 7 + 15;

            return "不佳";
        } else {
            if (sick.equals("1")) // 黃
            {
                newint = (int) (pnn50 * 100) % 7 + 8;

                return "注意";
            } else // 綠
            {
                newint = (int) (pnn50 * 100) % 7 + 1;

                return "良好";
            }
        }

    }

    public void tired(double tired) {
        if (tired >= 32 && tired <= 68) //green
        {


        } else if (tired == 6.5 || tired == 93.5) //red
        {


        } else {

        }
    }

    //9 green  29 yellow  46 red
    public void preasure(double preasure) {
        if (preasure <= 33) {

        } else if (preasure >= 67) {

        } else {

        }
    }

    //0.6good ~ -0.3bad   0.1 normal
    public void health(int health) {
        if (health <= 7) {

        } else if (health >= 8 && health <= 14) {

        } else {

        }
    }


    private String getFileName() {
        Calendar EcgFileCal = Calendar.getInstance();

        for (int fi = 0; fi < 2; fi++)
            EcgFileCal.add(Calendar.MINUTE, -1);

        int secRST = EcgFileCal.get(Calendar.SECOND);
        int minRST = EcgFileCal.get(Calendar.MINUTE);
        int hourRST = EcgFileCal.get(Calendar.HOUR_OF_DAY);
        int dayRST = EcgFileCal.get(Calendar.DATE);
        int monthRST = EcgFileCal.get(Calendar.MONTH);
        int yearRST = EcgFileCal.get(Calendar.YEAR);

        int secCUR = secRST;
        int minCUR = minRST;
        int hourCUR = hourRST;
        int dayCUR = dayRST;
        int monthCUR = monthRST;
        int yearCUR = yearRST;


        // Determine the file name of ECG output file according to recording time
        char ECGoutFN[] = {'0', '0', '0', '0', '0', '0', '0', '0', '.', 'l', 'p', '4'};  // The initial value of the file name of ECG output file
        Integer tempByteHigh, tempByteLow, tempOneByte;
        Integer yearOvrN;
        boolean yearOver = false;

        tempOneByte = yearRST - 2000;
        yearOvrN = tempOneByte / 36;
        if (yearOvrN > 0)
            yearOver = true;
        yearOvrN = yearOvrN % 36;

        tempOneByte = tempOneByte % 36;        // Will not cross 'Z'

        switch (tempOneByte) {
            case 0:
                ECGoutFN[0] = '0';
                break;
            case 1:
                ECGoutFN[0] = '1';
                break;
            case 2:
                ECGoutFN[0] = '2';
                break;
            case 3:
                ECGoutFN[0] = '3';
                break;
            case 4:
                ECGoutFN[0] = '4';
                break;
            case 5:
                ECGoutFN[0] = '5';
                break;
            case 6:
                ECGoutFN[0] = '6';
                break;
            case 7:
                ECGoutFN[0] = '7';
                break;
            case 8:
                ECGoutFN[0] = '8';
                break;
            case 9:
                ECGoutFN[0] = '9';
                break;
            case 10:
                ECGoutFN[0] = 'A';
                break;
            case 11:
                ECGoutFN[0] = 'B';
                break;
            case 12:
                ECGoutFN[0] = 'C';
                break;
            case 13:
                ECGoutFN[0] = 'D';
                break;
            case 14:
                ECGoutFN[0] = 'E';
                break;
            case 15:
                ECGoutFN[0] = 'F';
                break;
            case 16:
                ECGoutFN[0] = 'G';
                break;
            case 17:
                ECGoutFN[0] = 'H';
                break;
            case 18:
                ECGoutFN[0] = 'I';
                break;
            case 19:
                ECGoutFN[0] = 'J';
                break;
            case 20:
                ECGoutFN[0] = 'K';
                break;
            case 21:
                ECGoutFN[0] = 'L';
                break;
            case 22:
                ECGoutFN[0] = 'M';
                break;
            case 23:
                ECGoutFN[0] = 'N';
                break;
            case 24:
                ECGoutFN[0] = 'O';
                break;
            case 25:
                ECGoutFN[0] = 'P';
                break;
            case 26:
                ECGoutFN[0] = 'Q';
                break;
            case 27:
                ECGoutFN[0] = 'R';
                break;
            case 28:
                ECGoutFN[0] = 'S';
                break;
            case 29:
                ECGoutFN[0] = 'T';
                break;
            case 30:
                ECGoutFN[0] = 'U';
                break;
            case 31:
                ECGoutFN[0] = 'V';
                break;
            case 32:
                ECGoutFN[0] = 'W';
                break;
            case 33:
                ECGoutFN[0] = 'X';
                break;
            case 34:
                ECGoutFN[0] = 'Y';
                break;
            case 35:
                ECGoutFN[0] = 'Z';
                break;
        }

        tempOneByte = monthRST + 1;
        switch (tempOneByte) {
            case 0:
                ECGoutFN[1] = '0';
                break;
            case 1:
                ECGoutFN[1] = '1';
                break;
            case 2:
                ECGoutFN[1] = '2';
                break;
            case 3:
                ECGoutFN[1] = '3';
                break;
            case 4:
                ECGoutFN[1] = '4';
                break;
            case 5:
                ECGoutFN[1] = '5';
                break;
            case 6:
                ECGoutFN[1] = '6';
                break;
            case 7:
                ECGoutFN[1] = '7';
                break;
            case 8:
                ECGoutFN[1] = '8';
                break;
            case 9:
                ECGoutFN[1] = '9';
                break;
            case 10:
                ECGoutFN[1] = 'A';
                break;
            case 11:
                ECGoutFN[1] = 'B';
                break;
            case 12:
                ECGoutFN[1] = 'C';
                break;
        }

        tempOneByte = dayRST;
        switch (tempOneByte) {
            case 0:
                ECGoutFN[2] = '0';
                break;
            case 1:
                ECGoutFN[2] = '1';
                break;
            case 2:
                ECGoutFN[2] = '2';
                break;
            case 3:
                ECGoutFN[2] = '3';
                break;
            case 4:
                ECGoutFN[2] = '4';
                break;
            case 5:
                ECGoutFN[2] = '5';
                break;
            case 6:
                ECGoutFN[2] = '6';
                break;
            case 7:
                ECGoutFN[2] = '7';
                break;
            case 8:
                ECGoutFN[2] = '8';
                break;
            case 9:
                ECGoutFN[2] = '9';
                break;
            case 10:
                ECGoutFN[2] = 'A';
                break;
            case 11:
                ECGoutFN[2] = 'B';
                break;
            case 12:
                ECGoutFN[2] = 'C';
                break;
            case 13:
                ECGoutFN[2] = 'D';
                break;
            case 14:
                ECGoutFN[2] = 'E';
                break;
            case 15:
                ECGoutFN[2] = 'F';
                break;
            case 16:
                ECGoutFN[2] = 'G';
                break;
            case 17:
                ECGoutFN[2] = 'H';
                break;
            case 18:
                ECGoutFN[2] = 'I';
                break;
            case 19:
                ECGoutFN[2] = 'J';
                break;
            case 20:
                ECGoutFN[2] = 'K';
                break;
            case 21:
                ECGoutFN[2] = 'L';
                break;
            case 22:
                ECGoutFN[2] = 'M';
                break;
            case 23:
                ECGoutFN[2] = 'N';
                break;
            case 24:
                ECGoutFN[2] = 'O';
                break;
            case 25:
                ECGoutFN[2] = 'P';
                break;
            case 26:
                ECGoutFN[2] = 'Q';
                break;
            case 27:
                ECGoutFN[2] = 'R';
                break;
            case 28:
                ECGoutFN[2] = 'S';
                break;
            case 29:
                ECGoutFN[2] = 'T';
                break;
            case 30:
                ECGoutFN[2] = 'U';
                break;
            case 31:
                ECGoutFN[2] = 'V';
                break;
        }

        tempOneByte = hourRST;
        switch (tempOneByte) {
            case 0:
                ECGoutFN[3] = '0';
                break;
            case 1:
                ECGoutFN[3] = '1';
                break;
            case 2:
                ECGoutFN[3] = '2';
                break;
            case 3:
                ECGoutFN[3] = '3';
                break;
            case 4:
                ECGoutFN[3] = '4';
                break;
            case 5:
                ECGoutFN[3] = '5';
                break;
            case 6:
                ECGoutFN[3] = '6';
                break;
            case 7:
                ECGoutFN[3] = '7';
                break;
            case 8:
                ECGoutFN[3] = '8';
                break;
            case 9:
                ECGoutFN[3] = '9';
                break;
            case 10:
                ECGoutFN[3] = 'A';
                break;
            case 11:
                ECGoutFN[3] = 'B';
                break;
            case 12:
                ECGoutFN[3] = 'C';
                break;
            case 13:
                ECGoutFN[3] = 'D';
                break;
            case 14:
                ECGoutFN[3] = 'E';
                break;
            case 15:
                ECGoutFN[3] = 'F';
                break;
            case 16:
                ECGoutFN[3] = 'G';
                break;
            case 17:
                ECGoutFN[3] = 'H';
                break;
            case 18:
                ECGoutFN[3] = 'I';
                break;
            case 19:
                ECGoutFN[3] = 'J';
                break;
            case 20:
                ECGoutFN[3] = 'K';
                break;
            case 21:
                ECGoutFN[3] = 'L';
                break;
            case 22:
                ECGoutFN[3] = 'M';
                break;
            case 23:
                ECGoutFN[3] = 'N';
                break;
        }


        tempByteHigh = minRST / 10;
        tempByteLow = minRST % 10;
        switch (tempByteHigh) {
            case 0:
                ECGoutFN[4] = '0';
                break;
            case 1:
                ECGoutFN[4] = '1';
                break;
            case 2:
                ECGoutFN[4] = '2';
                break;
            case 3:
                ECGoutFN[4] = '3';
                break;
            case 4:
                ECGoutFN[4] = '4';
                break;
            case 5:
                ECGoutFN[4] = '5';
                break;
            case 6:
                ECGoutFN[4] = '6';
                break;
            case 7:
                ECGoutFN[4] = '7';
                break;
            case 8:
                ECGoutFN[4] = '8';
                break;
            case 9:
                ECGoutFN[4] = '9';
                break;
        }
        switch (tempByteLow) {
            case 0:
                ECGoutFN[5] = '0';
                break;
            case 1:
                ECGoutFN[5] = '1';
                break;
            case 2:
                ECGoutFN[5] = '2';
                break;
            case 3:
                ECGoutFN[5] = '3';
                break;
            case 4:
                ECGoutFN[5] = '4';
                break;
            case 5:
                ECGoutFN[5] = '5';
                break;
            case 6:
                ECGoutFN[5] = '6';
                break;
            case 7:
                ECGoutFN[5] = '7';
                break;
            case 8:
                ECGoutFN[5] = '8';
                break;
            case 9:
                ECGoutFN[5] = '9';
                break;
        }

        tempByteHigh = secRST / 10;
        tempByteLow = secRST % 10;
        switch (tempByteHigh) {
            case 0:
                ECGoutFN[6] = '0';
                break;
            case 1:
                ECGoutFN[6] = '1';
                break;
            case 2:
                ECGoutFN[6] = '2';
                break;
            case 3:
                ECGoutFN[6] = '3';
                break;
            case 4:
                ECGoutFN[6] = '4';
                break;
            case 5:
                ECGoutFN[6] = '5';
                break;
            case 6:
                ECGoutFN[6] = '6';
                break;
            case 7:
                ECGoutFN[6] = '7';
                break;
            case 8:
                ECGoutFN[6] = '8';
                break;
            case 9:
                ECGoutFN[6] = '9';
                break;
        }
        switch (tempByteLow) {
            case 0:
                ECGoutFN[7] = '0';
                break;
            case 1:
                ECGoutFN[7] = '1';
                break;
            case 2:
                ECGoutFN[7] = '2';
                break;
            case 3:
                ECGoutFN[7] = '3';
                break;
            case 4:
                ECGoutFN[7] = '4';
                break;
            case 5:
                ECGoutFN[7] = '5';
                break;
            case 6:
                ECGoutFN[7] = '6';
                break;
            case 7:
                ECGoutFN[7] = '7';
                break;
            case 8:
                ECGoutFN[7] = '8';
                break;
            case 9:
                ECGoutFN[7] = '9';
                break;
        }

        String ECGoutFNStr = "";

        if (yearOver) {    // Added a char in file name to represent the year number
            char yearOverFN[] = {'0', '0', '0', '0', '0', '0', '0', '0', '0', '.', 'l', 'p', '4'};  // The initial value of the file name of ECG output file
            for (int fi = 0; fi < 8; fi++)
                yearOverFN[fi] = ECGoutFN[fi];
            switch (yearOvrN) {
                case 0:
                    yearOverFN[8] = '0';
                    break;
                case 1:
                    yearOverFN[8] = '1';
                    break;
                case 2:
                    yearOverFN[8] = '2';
                    break;
                case 3:
                    yearOverFN[8] = '3';
                    break;
                case 4:
                    yearOverFN[8] = '4';
                    break;
                case 5:
                    yearOverFN[8] = '5';
                    break;
                case 6:
                    yearOverFN[8] = '6';
                    break;
                case 7:
                    yearOverFN[8] = '7';
                    break;
                case 8:
                    yearOverFN[8] = '8';
                    break;
                case 9:
                    yearOverFN[8] = '9';
                    break;
                case 10:
                    yearOverFN[8] = 'A';
                    break;
                case 11:
                    yearOverFN[8] = 'B';
                    break;
                case 12:
                    yearOverFN[8] = 'C';
                    break;
                case 13:
                    yearOverFN[8] = 'D';
                    break;
                case 14:
                    yearOverFN[8] = 'E';
                    break;
                case 15:
                    yearOverFN[8] = 'F';
                    break;
                case 16:
                    yearOverFN[8] = 'G';
                    break;
                case 17:
                    yearOverFN[8] = 'H';
                    break;
                case 18:
                    yearOverFN[8] = 'I';
                    break;
                case 19:
                    yearOverFN[8] = 'J';
                    break;
                case 20:
                    yearOverFN[8] = 'K';
                    break;
                case 21:
                    yearOverFN[8] = 'L';
                    break;
                case 22:
                    yearOverFN[8] = 'M';
                    break;
                case 23:
                    yearOverFN[8] = 'N';
                    break;
                case 24:
                    yearOverFN[8] = 'O';
                    break;
                case 25:
                    yearOverFN[8] = 'P';
                    break;
                case 26:
                    yearOverFN[8] = 'Q';
                    break;
                case 27:
                    yearOverFN[8] = 'R';
                    break;
                case 28:
                    yearOverFN[8] = 'S';
                    break;
                case 29:
                    yearOverFN[8] = 'T';
                    break;
                case 30:
                    yearOverFN[8] = 'U';
                    break;
                case 31:
                    yearOverFN[8] = 'V';
                    break;
                case 32:
                    yearOverFN[8] = 'W';
                    break;
                case 33:
                    yearOverFN[8] = 'X';
                    break;
                case 34:
                    yearOverFN[8] = 'Y';
                    break;
                case 35:
                    yearOverFN[8] = 'Z';
                    break;
            }
            ECGoutFNStr = new String(yearOverFN);
        } else
            ECGoutFNStr = new String(ECGoutFN);

        return ECGoutFNStr;
    }

}

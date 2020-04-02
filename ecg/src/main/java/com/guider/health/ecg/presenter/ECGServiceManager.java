package com.guider.health.ecg.presenter;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.guider.health.bluetooth.core.BleBluetooth;
import com.guider.health.bluetooth.core.Bluetooth;
import com.guider.health.bluetooth.core.ClientThread;
import com.guider.health.common.core.HearRate;
import com.guider.health.common.core.MyUtils;
import com.guider.health.ecg.R;
import com.guider.health.ecg.model.ECGTrueModel;
import com.guider.health.ecg.view.ECGViewInterface;
import com.tencent.bugly.crashreport.BuglyLog;
import com.tencent.bugly.crashreport.CrashReport;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import tw.com.jchang.geniiecgbt.decompNDK;


/**
 * Created by haix on 2019/6/13.
 */

public class ECGServiceManager {
    private Handler truehandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int status = msg.arg1;
            switch (status) {
                case 1:
                    // 开始测量
                    if (measureViewDataWeakReference.get() != null) {
                        measureViewDataWeakReference.get().connectSuccess();
                    }
                    break;
                case 2:
                    //测量结束
                    if (measureViewDataWeakReference.get() != null) {
                        measureViewDataWeakReference.get().measureComplete();
                    }
                    break;
                case 3:
                    break;
                case 4:
                    // 电量检查通过
                    startMeasure();
                    break;
                case 5:
                    // 电量低
                    if (measureViewDataWeakReference.get() != null) {
                        measureViewDataWeakReference.get().powerLow(msg.obj + "");
                    }
                    startMeasure();
                    break;
                case 101:
                    //波形
                    if (measureViewDataWeakReference.get() != null) {
                        measureViewDataWeakReference.get().measureWare((int) msg.obj);
                    }
                    break;

                case 102:
                    //测量时间
                    if (measureViewDataWeakReference.get() != null) {
                        measureViewDataWeakReference.get().measureTime((int) msg.obj);
                    }
                    break;
                case 10001:
                    //拉取数据时间
                    if (measureViewDataWeakReference.get() != null) {
                        measureViewDataWeakReference.get().onAnalysisTime((int) msg.obj);
                    }
                    break;
                case 10002:
                    //
                    if (measureViewDataWeakReference.get() != null) {
                        measureViewDataWeakReference.get().onAnalysisTime((int) msg.obj);
                    }
                    break;
                case 10009:
                    //拉取数据结束
                    if (measureViewDataWeakReference.get() != null) {
                        measureViewDataWeakReference.get().onAnalysisEnd();
                        saveFile();
                        startGetToken();
                    }
                    break;
                case 2222:
                    //超时
                    if (measureViewDataWeakReference.get() != null) {
                        measureViewDataWeakReference.get().measureTimeOut("连接超时" , true);
                    }
                    break;
            }
        }

    };


    private ECGServiceManager() {
    }

    private static ECGServiceManager serviceManager = new ECGServiceManager();

    public static ECGServiceManager getInstance() {

        return serviceManager;
    }

    public BluetoothDevice mDevice;
    private ECGTrueModel measureModel = new ECGTrueModel();


    Handler handler = new Handler(Looper.getMainLooper());
    ExecutorService executor = Executors.newFixedThreadPool(3);


    private WeakReference<ECGViewInterface> measureViewDataWeakReference;

    public void setViewObject(ECGViewInterface measureViewData) {
        measureViewDataWeakReference = new WeakReference<ECGViewInterface>(measureViewData);
    }



    BluetoothSocket bluetoothSocket;
    private OutputStream outputStream;
    private InputStream inputStream;
    public void toSocketConnect(final BluetoothDevice btDevice) {
        ClientThread.toSocketConnectDevice(btDevice, new ClientThread.SocketConnectListener() {
            @Override
            public void connectsuccess(BluetoothSocket socket, final BluetoothDevice device, OutputStream out, InputStream in) {
                outputStream = out;
                inputStream = in;
                bluetoothSocket = socket;
                ECGTrueModel.setStream(outputStream, inputStream);
                ECGTrueModel.readbattery(truehandler);
            }

            @Override
            public void connectfaile(BluetoothDevice device) {
                if (measureViewDataWeakReference.get() != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            measureViewDataWeakReference.get().connectFaile(1);
                        }
                    });
                }
            }
        });
    }

    private void startMeasure() {
        ECGTrueModel.read_upload_list(truehandler);
    }

    public String getTired(double lfhf) {
        return ECGTrueModel.lfhftovalue(lfhf);
    }

    public String getPreasure(double sdnn) {
        return ECGTrueModel.sdnntovalue(sdnn);
    }

    public String getHealth(int pnn50) {
        String str = HearRate.getInstance().getPredictedSymptoms();
        if (!"".equals(str)) {
            str = "1";
        }
        return ECGTrueModel.pnn50tovalue(pnn50, str);
    }


    public void startResultAnalysis() {
        ECGTrueModel.read_upload(truehandler);
    }

    public void saveFile() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 写入数据到文件
                String path = Environment.getExternalStorageDirectory().getAbsolutePath()
                        + File.separator+"ECG"+File.separator;
                ecpEcgFile(path , ECGTrueModel.file_data);
                // new decompNDK().decpEcgFile(path + "temp.lp4");
            }
        }).start();
    }

    public void startGetToken() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                // 提交数据到服务器
                String token = measureModel.getAPItoken();
                if (token != null) {
                    measureModel.lp4Upload(token, new ECGTrueModel.MeasureResult() {
                        @Override
                        public void result(String result) {
                            Log.i("haix_ecg", "lp41+++++++++++++++++++++: " + result);
                            JSONObject jsonObject = JSONObject.parseObject(result);
                            String statusCode = null;
                            statusCode = jsonObject.getString("StatusCode");
                            if ("01".equals(statusCode)) {


//                                {"ThisCost":0,"RemainPoint":0,"FileId":17877,"HeartRate":91.79,"HeartRateLight":"yellow","HealthLight":"yellow","HealthLightOriginal":"yellow","DiaDescribe":"Above or below the average HR. Potential cardiac risks. Have a professional ECG report if you see yellow frequently.","SDNN":48.11,"LFHF":3.096774193548387,"NN50":3.0,"PNN50":0.11538,"StressLight":"green","NervousSystemBalanceLight":"yellow","PredictedSymptoms":"","StatusCode":"01"}
                                HearRate.getInstance().setHeartRate(jsonObject.getString("HeartRate"));
                                HearRate.getInstance().setHeartRateLight(jsonObject.getString("HeartRateLight"));
                                HearRate.getInstance().setHealthLight(jsonObject.getString("HealthLight"));
                                HearRate.getInstance().setHealthLightOriginal(jsonObject.getString("HealthLightOriginal"));
                                HearRate.getInstance().setDiaDescribe(jsonObject.getString("DiaDescribe"));
                                HearRate.getInstance().setSDNN(jsonObject.getString("SDNN"));
                                HearRate.getInstance().setLFHF(jsonObject.getString("LFHF"));
                                HearRate.getInstance().setNN50(jsonObject.getString("NN50"));
                                HearRate.getInstance().setPNN50(jsonObject.getString("PNN50"));
                                HearRate.getInstance().setPredictedSymptoms(jsonObject.getString("PredictedSymptoms"));
                                HearRate.getInstance().setPervousSystemBalanceLight(jsonObject.getString("NervousSystemBalanceLight"));
                                HearRate.getInstance().setStressLight(jsonObject.getString("StressLight"));
                                if (measureViewDataWeakReference.get() != null) {

                                    measureViewDataWeakReference.get().onAnalysisResult(result);

                                }
                            } else {
                                String msg = measureViewDataWeakReference.get().getViewContext().getResources().getString(R.string.ecg_abnormal); // "心电异常 , 请重新测量";
                                BuglyLog.e("EcgStatusCode" , TextUtils.isEmpty(statusCode)?"Null--Status" : statusCode);
                                if (statusCode == null) {

                                } else {
                                    switch (statusCode) {
                                        case "03":
                                            msg = measureViewDataWeakReference.get().getViewContext().getResources().getString(R.string.info_error);
                                            // msg = "输入资料验证失败";
                                            break;
                                        case "04":
                                            msg = measureViewDataWeakReference.get().getViewContext().getResources().getString(R.string.poor_singal);
                                            // msg = "讯号不佳，请重新测量";
                                            break;
                                        case "05":
                                            msg = measureViewDataWeakReference.get().getViewContext().getResources().getString(R.string.token_expired);
                                            // msg = "Token 过期(身分验证失败)";
                                            break;
                                        case "06":
                                            msg = measureViewDataWeakReference.get().getViewContext().getResources().getString(R.string.other_error);
                                            // msg = "其他错误";
                                            break;
                                        case "29":
                                            msg = measureViewDataWeakReference.get().getViewContext().getResources().getString(R.string.poor_singal);
                                            // msg = "讯号不佳，请重新测量";
                                            break;
                                        case "34":
                                            msg = measureViewDataWeakReference.get().getViewContext().getResources().getString(R.string.ecg_abnormal);
                                            // msg = "副档名不为 Lp4";
                                            break;
                                        case "35":
                                            // msg = "讯号不佳，请重新测量";
                                            msg = measureViewDataWeakReference.get().getViewContext().getResources().getString(R.string.poor_singal);
                                            break;
                                        case "36":
                                            // msg = "讯号不佳，请重新测量";
                                            msg = measureViewDataWeakReference.get().getViewContext().getResources().getString(R.string.poor_singal);
                                            break;
                                        case "37":
                                            // msg = "讯号不佳，请重新测量";
                                            msg = measureViewDataWeakReference.get().getViewContext().getResources().getString(R.string.poor_singal);
                                            break;
                                        case "38":
                                            // msg = "讯号不佳，请重新测量";
                                            msg = measureViewDataWeakReference.get().getViewContext().getResources().getString(R.string.poor_singal);
                                            break;
                                    }
                                }
                                if (measureViewDataWeakReference.get() != null) {
                                    measureViewDataWeakReference.get().measureTimeOut(msg, true);
                                    CrashReport.putUserData(measureViewDataWeakReference.get().getViewContext(),
                                            "EcgStatusCode",
                                            TextUtils.isEmpty(statusCode)?"Null--Status" : statusCode);
                                    Exception je = new Exception(TextUtils.isEmpty(statusCode)?"Null--Status" : statusCode);
                                    CrashReport.postCatchedException(je);
                                }

                            }


                        }
                    });
                } else {
                    if (measureViewDataWeakReference.get() != null) {
                        measureViewDataWeakReference.get().measureTimeOut(measureViewDataWeakReference.get().getViewContext().getResources().getString(R.string.token_get_error) , false);
                    }
                }
            }
        }).start();

    }

    private void ecpEcgFile(String path, ArrayList<Byte> datalist) {
        byte[] array = new byte[datalist.size()];
        for (int i = 0; i < array.length; i++) {
            array[i] = (byte) datalist.get(i);
        }

        try {
            File file = new File(path + "temp.lp4");
            File floder_file = new File(path);

            if (!floder_file.exists()) {
                floder_file.mkdirs();
            }

            if (!file.exists()) {
                file.createNewFile();
            }


            FileOutputStream fos = new FileOutputStream(file);
            fos.write(array);
            fos.close();

            Log.d("tag","writeToFile  success");
        } catch (Exception e) {
            Log.d("tag", "sssddd = " + e.getMessage());
        }
    }

    public void startDeviceMessure() {
        ECGTrueModel.wave30(truehandler);
    }

    public void startScanBlueToothDevice(boolean isClearBluetooth) {
        toScanDevice(isClearBluetooth);
    }

    private void toScanDevice(boolean isClearBluetooth) {
        mDevice = null;

        setListner();
        connect(isClearBluetooth);
    }

    private void toPair() {
        setListner();
        BleBluetooth.getInstance().nowStopScan();
    }

    public void stopDeviceConnect() {
        Bluetooth.getInstance().cancelDiscovery();
        ECGTrueModel.stopMeasure();

        try {
            if (bluetoothSocket != null){
                if (outputStream != null){
                    outputStream.close();
                }
                if (inputStream != null){
                    inputStream.close();
                }
                bluetoothSocket.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeRecerver(Context context) {
        Bluetooth.getInstance().unReceiver(context);
    }

    private void connect(boolean isClearBluetooth) {
        if (measureViewDataWeakReference.get() != null) {
            Bluetooth.getInstance().initBluetooth(measureViewDataWeakReference.get().getViewContext());
        }

        if (!isClearBluetooth) {
            List<BluetoothDevice> deviceLists = new ArrayList();
            Bluetooth.getInstance().getBondDevices(deviceLists);
            for (BluetoothDevice device : deviceLists) {
                if ("CmateH".equals(device.getName())) {
                    mDevice = device;
                    HearRate.getInstance().setDeviceAddress(MyUtils.getMacAddress());
                }
            }
        }

        if (mDevice == null) {
            if (measureViewDataWeakReference.get() != null) {
                Bluetooth.getInstance().searchBluetoothDevices(measureViewDataWeakReference.get().getViewContext());
            }
        } else {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    toSocketConnect(mDevice);
                }
            });
        }
    }

    private void setListner() {
        Bluetooth.getInstance().setMakePairBlueToothListener(new Bluetooth.MakePairBlueToothListener() {
            @Override
            public void foundDevice(BluetoothDevice btDevice) {
                Log.i("haix", "扫描到设备: " + btDevice.getName() + " 地址: " + btDevice.getAddress());
                if ("CmateH".equals(btDevice.getName())) {
                    Log.i("haix", "匹配成功------------------");
                    if (mDevice == null) {
                        mDevice = btDevice;
                        //toConnect(mDevice);
                        HearRate.getInstance().setDeviceAddress(MyUtils.getMacAddress());
                        executor.execute(new Runnable() {
                            @Override
                            public void run() {
                                toSocketConnect(mDevice);
                            }
                        });
                    }
                }
            }
            @Override
            public void pairing(BluetoothDevice btDevice) {

            }

            @Override
            public void pairingSuccess(BluetoothDevice btDevice) {

            }

            @Override
            public void scanFinish(final BluetoothDevice btDevice) {
                if (mDevice == null) {
                    if (measureViewDataWeakReference.get() != null) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                measureViewDataWeakReference.get().scanFailed();
                            }
                        });
                    }
                }
            }
        });
    }
}

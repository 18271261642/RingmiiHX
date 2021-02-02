//package com.guider.health.ybglu;
//
//import android.bluetooth.BluetoothGatt;
//import android.bluetooth.BluetoothGattCharacteristic;
//import android.util.Log;
//import com.guider.health.foraglu.BleScanAndConnectBluetooth;
//import com.guider.health.foraglu.BleVIewInterface;
//import com.guider.health.foraglu.IBleServiceManager;
//import com.tiancheng.util.ByteUtil;
//import com.tiancheng.util.DateUtil;
//import org.greenrobot.eventbus.EventBus;
//import java.lang.ref.WeakReference;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
///**
// * @Package: com.guider.health.beneCheck
// * @ClassName: BeneCheckModelImpl
// * @Description:
// * @Author: hjr
// * @CreateDate: 2021/1/12 16:31
// * Copyright (C), 1998-2021, GuiderTechnology
// */
//public class BeneCheckModelImpl implements IBleServiceManager {
//    private BleScanAndConnectBluetooth bpScanAndConnectBluetooth;
//    private WeakReference<BleVIewInterface> bpViewData;
//    private ExecutorService executor = Executors.newFixedThreadPool(10);
//    private String[] ma_deviceInfo = new String[]{"百捷BKM13-1 BENECHECK TC-B DONGLE",
//            "blood_sygar|ua|chol", "1"};
//    private long mLastTimeMills = 0L;
//
//    private final int m_intTimeout = 5000;
//
//    private String m_lastData = null;
//
//    private BeneCheckModelImpl() {
//    }
//
//    private static BeneCheckModelImpl serviceManager = new BeneCheckModelImpl();
//
//    public static BeneCheckModelImpl getInstance() {
//        return serviceManager;
//    }
//
//    public void setViewObject(BleVIewInterface bpView) {
//        bpViewData = new WeakReference<>(bpView);
//    }
//
//    public void startMeasure() {
//        if (bpScanAndConnectBluetooth == null) {
//            bpScanAndConnectBluetooth = new BleScanAndConnectBluetooth(this,
//                    new YBGluDeviceUUID(),ma_deviceInfo[0]
//            );
//        }
//        bpScanAndConnectBluetooth.run();
//    }
//
//    @Override
//    public void scanAndconnectFailed() {
//        if (bpViewData.get() != null) {
//            bpViewData.get().connectNotSuccess();
//        }
//    }
//
//    @Override
//    public void generateData(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
//        synchronized (this) {
//            byte[] data = characteristic.getValue();
//            Log.d("BeneCheckModelImpl", data.toString());
//            if ((data != null) && (data.length == 20) &&
//                    (checkSum(data)))
//                getResult(data);
//        }
//    }
//
//    public void stopDeviceConnect() {
//        if (bpScanAndConnectBluetooth != null)
//            bpScanAndConnectBluetooth.stop();
//    }
//
//
//    private void getResult(byte[] data) {
//        String msg = null;
//        String event = null;
//        String uploadDate = DateUtil.dateToString(DateUtil.utcNow());
//        switch (data[4]) {
//            case 65:
//                int temp = ((data[18] & 0xFF) << 8) + (data[17] & 0xFF);
//                double glu = temp / 18.0D;
//                double gluResult = ByteUtil.getDoubleMixture(glu, 2);
//                Log.d("BeneCheckModelImpl", "血糖的值是" + gluResult);
//                String[] tempData = {uploadDate, String.valueOf(gluResult)};
//
//                this.ma_deviceInfo[1] = "blood_sygar";
//                msg = gluResult > 0.0D ? SplitUtil.combin(this.ma_deviceInfo, tempData) : null;
//                event = "bloodGlucose";
//                break;
//            case 81:
//                int temp1 = ((data[18] & 0xFF) << 8) + (data[17] & 0xFF);
//                double ua = temp1 * 0.1D / 16.809999999999999D * 1000.0D;
//                double uaResult = ByteUtil.getDoubleMixture(ua, 1);
//                Log.d("BeneCheckModelImpl", "尿酸的值是" + uaResult);
//                String[] tempData1 = {uploadDate, String.valueOf(uaResult)};
//                this.ma_deviceInfo[1] = "ua";
//                msg = uaResult > 0.0D ? SplitUtil.combin(this.ma_deviceInfo, tempData1) : null;
//                event = "ua";
//                break;
//            case 97:
//                int temp2 = ((data[18] & 0xFF) << 8) + (data[17] & 0xFF);
//                double chol = temp2 / 38.659999999999997D;
//                double cholResult = ByteUtil.getDoubleMixture(chol, 2);
//                Log.d("BeneCheckModelImpl", "胆固醇的值是" + cholResult);
//                String[] tempData2 = {uploadDate, String.valueOf(cholResult)};
//                this.ma_deviceInfo[1] = "chol";
//                msg = cholResult > 0.0D ? SplitUtil.combin(this.ma_deviceInfo, tempData2) : null;
//                event = "chol";
//                break;
//            default:
//                Log.d("BeneCheckModelImpl", "不是指定数据类型");
//        }
//        if ((null != msg) && (System.currentTimeMillis() - this.mLastTimeMills > 5000L)) {
//            EventBus.getDefault().post(msg, event);
//        }
//        this.mLastTimeMills = System.currentTimeMillis();
//        this.m_lastData = data.toString();
//    }
//
//    private boolean checkSum(byte[] data) {
//        if (null == data)
//            return false;
//        byte tmpData = 0;
//
//        for (int i = 1; i < 19; i++) {
//            tmpData = (byte) (tmpData + data[i]);
//        }
//        return data[19] == tmpData;
//    }
//
//}

package com.gaider.proton.presenter;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.gaider.proton.PermissionUtils;
import com.gaider.proton.Protocol;
import com.guider.health.common.core.MyUtils;
import com.guider.health.common.core.UserManager;
import com.orhanobut.logger.Logger;
import com.proton.ecgcard.algorithm.bean.AlgorithmResult;
import com.proton.ecgcard.algorithm.bean.RealECGData;
import com.proton.ecgcard.algorithm.callback.AlgorithmResultListener;
import com.proton.ecgcard.connector.EcgCardManager;
import com.proton.ecgcard.connector.callback.DataListener;
import com.wms.ble.bean.ScanResult;
import com.wms.ble.callback.OnConnectListener;
import com.wms.ble.callback.OnScanListener;

import java.util.List;

public class MeasurePresnter implements Protocol.IMeasurePresenter {

    private Protocol.IMeasureView view;
    private String TAG = "MeasurePresnter";
    private String currentDeviceMac;
    private EcgCardManager manager;
    private String heart, power, signal;
    private ProtonEcgTheTimer countDownTimer;

    @Override
    public void init(final Protocol.IMeasureView view) {
        this.view = view;
        countDownTimer = new ProtonEcgTheTimer(60, new ProtonEcgTheTimer.OnTimingCallback() {
            @Override
            void onTick(int time) {
                super.onTick(time);
                view.onTick(time);
            }

            @Override
            void onFinish() {
                super.onFinish();
                // 年龄 性别01 胆固醇 高度蛋白 是否吸烟01 收缩压 是否降压药01 身高cm 体重kg
                manager.getAnalysisResult(
                        MyUtils.getAgeFromBirthTime(UserManager.getInstance().getBirth()),
                        "MAN".equals(UserManager.getInstance().getSex()) ? 0 : 1,
                        170,
                        55,
                        0,
                        125,
                        0,
                        UserManager.getInstance().getHeight(),
                        UserManager.getInstance().getWeight(), new AlgorithmResultListener() {
                            @Override
                            public void receiveAlgorithmResult(final AlgorithmResult algorithmResult) {
                                super.receiveAlgorithmResult(algorithmResult);
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        view.onAnalysisResult(algorithmResult);
                                    }
                                });
                            }
                        });
            }
        });
    }

    @Override
    public void start() {
        searchDevice();
    }

    /**
     * 开始搜索设备
     */
    void searchDevice() {
        PermissionUtils.getLocationPermission(view.getMyContext());
        EcgCardManager.scanDevice(new OnScanListener() {
            @Override
            public void onScanStart() {
                Log.i(TAG, "onScanStart...");
                currentDeviceMac = "";
                view.onStartSearch();
            }

            @Override
            public void onDeviceFound(ScanResult scanResult) {
                Log.w(TAG, "onDeviceFound: " + scanResult.getDevice().getAddress());
                currentDeviceMac = scanResult.getDevice().getAddress();
                EcgCardManager.stopScan();
            }

            @Override
            public void onScanStopped() {
                Log.i(TAG, "onScanStopped...");
                // 检查是不是找到了可用的设备
                startMeasure();
            }

            @Override
            public void onScanCanceled() {
                Log.i(TAG, "onScanCanceled...");
                startMeasure();
            }
        });
    }

    /**
     * 开始测量
     */
    void startMeasure() {
        // 检查是否搜索到了设备
        if (TextUtils.isEmpty(currentDeviceMac)) {
            view.notFindDevice();
            return;
        }
        Log.i(TAG, "开始初始化...");
        // 初始化实例
        initManager();
        //开始连接
        manager.connectEcgCard(new OnConnectListener() {
            @Override
            public void onConnectSuccess() {
                Logger.w("连接成功");
            }

            @Override
            public void onConnectFaild() {
                Logger.w("连接失败");
                view.onConnectFail();
            }

            @Override
            public void onDisconnect() {
                Logger.w("连接断开");
                view.onConnectFail();
            }
        });
    }

    /**
     * 初始化管理器
     */
    private void initManager() {
        manager = EcgCardManager.getInstance(currentDeviceMac);
        manager.setDataListener(new DataListener() {
            @Override
            public void receiveTouchMode(int mode) {
                super.receiveTouchMode(mode);
                //mode = 0代表双手没有触摸 mode = 1代表双手触摸
                Log.w(TAG, "receiveTouchMode: mode = " + mode);
                if (mode == 0) {
                    view.onStatusChange(Protocol.CODE_HAND_GONE);
                    countDownTimer.stop();
                } else {
                    view.onStatusChange(Protocol.CODE_HAND_ON);
                    countDownTimer.start();
                }
            }

            @Override
            public void receiveEcgFilterData(RealECGData currentData, int section) {
                super.receiveEcgFilterData(currentData, section);
                //currentData.ecgData 用来绘制心电图
                Log.w(TAG, "接收心电数据用来绘制实时心电图" + currentData.ecgData.size());
                view.onDeviceMeasure(currentData);
            }

            @Override
            public void receiverHeartRate(int rate) {
                super.receiverHeartRate(rate);
                Log.w(TAG, "当前心跳:" + rate);
                heart = String.valueOf(rate);
                view.hasDeviceInfo(heart, power, signal);
            }

            @Override
            public void receiveEcgSourceData(List<Float> sourceData) {
                //这个是心电卡采集的原始数据，没有经过滤波算法
                super.receiveEcgSourceData(sourceData);
            }

            @Override
            public void signalInterference(int signalQualityIndex) {
                super.signalInterference(signalQualityIndex);
                Log.w(TAG, "信号强度:" + signalQualityIndex);
                switch (signalQualityIndex) {
                    case 0:
                        signal = "强";
                        break;
                    case 1:
                        signal = "弱";
                        break;
                    case 2:
                        signal = "极弱";
                        view.onWarning();
                        break;
                }
                view.hasDeviceInfo(heart, power, signal);
            }

            @Override
            public void receiveBFR(Integer bfr) {
                super.receiveBFR(bfr);
                //心电卡采集到的体脂电阻
            }

            @Override
            public void receiveBattery(Integer battery) {
                super.receiveBattery(battery);
                Log.w(TAG, "电量:" + battery);
                power = String.valueOf(battery);
                view.hasDeviceInfo(heart, power, signal);
            }

            @Override
            public void receiveSerial(String serial) {
                super.receiveSerial(serial);
                Log.w(TAG, "序列号:" + serial);
            }

            @Override
            public void receiveHardVersion(String hardVersion) {
                super.receiveHardVersion(hardVersion);
                Log.w(TAG, "固件版本:" + hardVersion);
            }
        });
    }

    @Override
    public void restart() {
        manager.disConnect(currentDeviceMac);
        start();
    }

    @Override
    public void finish() {
        view = new NullView();
        manager.disConnect(currentDeviceMac);
    }


    static class NullView implements Protocol.IMeasureView {

        @Override
        public void onStartSearch() {

        }

        @Override
        public void notFindDevice() {

        }

        @Override
        public void onConnectFail() {

        }

        @Override
        public void onTick(int time) {

        }

        @Override
        public void onWarning() {

        }

        @Override
        public void onDeviceMeasure(RealECGData currentData) {

        }

        @Override
        public void hasDeviceInfo(String heart, String power, String signal) {

        }

        @Override
        public void onStatusChange(int code) {

        }

        @Override
        public void onAnalysisResult(AlgorithmResult algorithmResult) {

        }

        @Override
        public Activity getMyContext() {
            return null;
        }

    }
}

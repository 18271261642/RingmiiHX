package com.guider.health.bp.view.avebp;

import android.bluetooth.BluetoothGatt;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.guider.health.bp.R;
import com.guider.health.bp.view.BPFragment;
import com.guider.health.bp.view.TipTitleView;
import com.guider.health.common.core.Config;
import com.guider.health.common.core.HeartPressAve;
import com.guider.health.common.core.HeartPressBp;
import com.guider.health.common.core.MyUtils;
import com.guider.health.common.device.DeviceInit;
import com.guider.health.common.utils.SkipClick;
import com.sdk.core.OnBPxxActionListener;
import com.sdk.healthkits.BloodPressureLite.BPxx;

import java.text.SimpleDateFormat;
import java.util.Date;

import ble.BleClient;
import ble.SimpleDevice;
import ble.callback.SimpleCallback;

/**
 * 这是动脉硬化款的血压仪四川云峰的
 * Created by haix on 2019/6/25.
 */

public class BPOperaterReminder1 extends BPFragment {

    private View view;
    private Button research;
    private TextView textView;
    private BPxx mBPxx = null;
    private BluetoothGatt bluetoothGatt;
    private long startTime;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.bp_ave_device_connect, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            return;
        }
        Bundle arguments = getArguments();
        startTime = arguments.getLong("startTime");

        initView();

        initBle();
    }

    private void initView() {
        setHomeEvent(view.findViewById(R.id.home), Config.HOME_DEVICE);
        ((TextView) view.findViewById(R.id.title)).setText("蓝牙连接");
        view.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pop();
            }
        });
        view.findViewById(R.id.skip).setVisibility(View.VISIBLE);
        view.findViewById(R.id.skip).setOnClickListener(new SkipClick(this , DeviceInit.DEV_BP_AVE));
        TipTitleView tips = view.findViewById(R.id.tips);
        tips.setTips("动脉硬化测量", "操作指南", "蓝牙连接", "测量结果");
        tips.toTip(2);

        textView = view.findViewById(R.id.glu_operat_reminders);
        research = view.findViewById(R.id.glu_button_failed);
        research.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 连接失败后的按钮
                BleClient.instance().disconnect(BleClient.instance().popStagingDevice());
                BleClient.instance().searchDevice(1000 * 99999,bleCallback);
            }
        });
    }

    private void initBle() {
        BleClient.init(_mActivity);
        BleClient.instance().searchDevice(1000 * 300 , bleCallback);
        mBPxx = new BPxx("8888");
        mBPxx.SetOnActionListener(new OnBPxxActionListener() {
            @Override
            public void OnBleDiscovered(String Addr) {
                Log.i("aaaaaaa", "OnBleDiscovered  " + Addr);
            }

            @Override
            public void OnBleOffline() {
                // 设备下线
                Log.i("aaaaaaa", "OnBleOffline");
                if (bluetoothGatt != null) {
                    bluetoothGatt.disconnect();
                }
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        notFindData();
                    }
                });
            }

            @Override
            public void OnBleOnline() {
                // 设备上线
                Log.i("aaaaaaa", "OnBleOnline");
            }

            @Override
            public void OnBleRead(String BPxxStrData) {
                // 这里可以获取到用户的出生日期 例:2019/11/11
//                UserManager.getInstance().setBirth(BPxxStrData.replace("/" , "-"));
                Log.i("aaaaaaa", "OnBleRead " + BPxxStrData);
            }

            @Override
            public void OnBleScanTimeout() {
                Log.i("aaaaaaa", "OnBleScanTimeout");
            }

            @Override
            public void OnBleServicesDiscovered() {
                // 服务被发现
                Log.i("aaaaaaa", "OnBleServicesDiscovered");
            }

            @Override
            public void OnBleWrite() {
                Log.i("aaaaaaa", "OnBleWrite");
            }

            /**
             * " 用户:" + split[0] +
             * " 高压:" + split[1] +
             * " 低压:" + split[2] +
             * " 脉搏:" + split[3] +
             * " AVI:" + split[4] +
             * " API:" + split[5]);
             */
            @Override
            public void OnDataReceiveComplete() {
                int count = mBPxx.GetMeasurementDataCount();
//                #0,0,高压 120,低压 69, 脉搏 86,170,310,1752583810, API 31
//                  #1,0,114,68,92,AVI 220,API 270,1752585482,30
                // 0? 1高压 2低压 3脉搏 4AVI 5API 6? 7气温
                if (count == 0) {
                    notFindData();
                    return;
                }
                for (int i = 0; i < count; i++) {
                    String s = mBPxx.GetMeasurementData(i);
                    Log.i("aaaaaaa", "OnDataReceiveComplete----" + s);
                }
                String s = mBPxx.GetMeasurementData(count - 1);
                final String[] split = s.split(",");
                Log.i("aaaaaaa", "OnDataReceiveComplete----" + count);

                mBPxx.GetUserInfo(Integer.valueOf(split[0]));
                mBPxx.SetOnActionListener(null);
                BleClient.instance().disconnect(BleClient.instance().popStagingDevice());
                toNext(split);
            }

            @Override
            public void OnReady() {
                Log.i("aaaaaaa", "OnReady");
                textView.setText("获取测量数据...");
                research.setVisibility(View.GONE);
                startTimeout(TYPE_GET_DATA);

                final byte[] byteTime = getByteTime(startTime);
                Log.i("aaaaaaa","删选时间 : "+ startTime);
                StringBuffer buffer = new StringBuffer();
                for (int i = 0; i < byteTime.length; i++) {
                    buffer.append(byteTime[i] + " : ");
                }
                boolean b = mBPxx.SetDateTimeFilter(byteTime);
                Log.i("aaaaaaa", b + " : "+buffer.toString());
                mBPxx.GetMeasurement();
            }

            @Override
            public void OnSetPasswordComplete() {
                Log.i("aaaaaaa", "OnSetPasswordComplete");
            }

            @Override
            public void OnUpdating() {
                Log.i("aaaaaaa", "OnUpdating");
            }
        });
    }

    SimpleCallback bleCallback = new SimpleCallback() {

        @Override
        public void onSearchStarted() {
            super.onSearchStarted();
            textView.setText("正在连接蓝牙...");
            research.setVisibility(View.GONE);
        }

        @Override
        protected SimpleDevice onFindDevice(final SimpleDevice device) {
            if ("AVE-2000".equals(device.getName())) {
                BleClient.instance().stopSearching();
                BleClient.instance().recordDevice(device);
                HeartPressBp.getInstance().setDeviceAddress(MyUtils.getMacAddress());
                mBPxx.StartKeepAlive();
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        bluetoothGatt = device.deviceInfo.device.connectGatt(getContext(), false, mBPxx);
                        startTimeout(TYPE_CONNECT);
                    }
                }, 3000);
            }
            return null;
        }



        @Override
        protected void onSearchFinish() {
            notFindDevice();
        }

    };

    /**
     * 时间字符串用byte[] 封装
     *
     * @param date
     * @return
     */
    public byte[] getByteTime(long date) {
//        byte[] byteNum = new byte[4];
//        for (int ix = 0; ix < 4; ++ix) {
//            int offset = 64 - (ix + 1) * 8;
//            byteNum[3 - ix] = (byte) ((date >> offset) & 0xff);
//        }
//        return byteNum;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-M-d-H-m-s");
        String format1 = format.format(new Date(date));
        Log.i("aaaaaa", "时间转换" + format1);
        String[] split = format1.split("-");

        byte[] time = new byte[7];
        //year[0][1]
        time[0] = (byte) (Integer.valueOf(split[0]) & 0x0FF);
        time[1] = ((byte) ((Integer.valueOf(split[0]) & 0x0FF00) >> 8));
        //month[2]
        time[2] = (byte) (Integer.valueOf(split[1]) & 0x0FF);
        //date[3]
        time[3] = (byte) (Integer.valueOf(split[2]) & 0x0FF);
        //hours[4]
        time[4] = (byte) (Integer.valueOf(split[3]) & 0x0FF);
        //min[5]
        time[5] = (byte) (Integer.valueOf(split[4]) & 0x0FF);
        //sec[6]
        time[6] = (byte) (Integer.valueOf(split[5]) & 0x0FF);
        return time;
    }

    Handler handler = new Handler();
    final int TYPE_CONNECT = 1;
    final int TYPE_GET_DATA = 2;
    /**
     * 在连接成功的时候
     */
    void startTimeout(int type) {
        handler.removeMessages(0);
        switch (type) {
            case TYPE_CONNECT:
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        connectFail();
                    }
                } , 10 * 1000);
                break;
            case TYPE_GET_DATA:
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        notFindData();
                    }
                } , 10 * 1000);
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (bluetoothGatt != null) {
            bluetoothGatt.disconnect();
        }
        handler.removeMessages(0);
    }

    private void notFindDevice() {
        textView.setText("未发现设备.\n1. 请确认设备蓝牙已开启.\n2. 可以尝试长按设备\"时间\"键手动开启蓝牙.");
        research.setVisibility(View.VISIBLE);
    }

    private void notFindData() {
        textView.setText("未发现新的测量数据.\n请尝试重新测量.");
        research.setVisibility(View.VISIBLE);
    }

    private void connectFail() {
        textView.setText("连接失败.\n1. 请确认设备蓝牙已开启.\n2. 可以尝试长按设备\"时间\"键手动开启蓝牙.");
        research.setVisibility(View.VISIBLE);
    }

    private void toNext(String[] split) {
        handler.removeMessages(0);
        if (bluetoothGatt != null) {
            bluetoothGatt.disconnect();
        }
        startWithPop(new BPOperaterReminderResult());
        Log.i("aaaaaaa", "跳转\n");
        HeartPressAve.getInstance().setSbp(split[2]);
        HeartPressAve.getInstance().setDbp(split[3]);
        HeartPressAve.getInstance().setHeart(split[4]);
        HeartPressAve.getInstance().setAvi(Integer.valueOf(split[5]) / 10 + "");
        HeartPressAve.getInstance().setApi(Integer.valueOf(split[6]) / 10 + "");
    }

}
//                                629113593-000        946656000
//        0,0, 97, 70,86, 190,200,628355051,29
//        0,0, 92, 54,103,160,220,628355564,31
//        0,0, 98, 64,101,190,240,628356001,32
//        0,0, 100,74,81, 150,230,628357394,28
//        0,0, 104,77,68, 190,190,628361737,30
//        0,0, 101,71,74, 170,200,628362281,31
//        0,0, 94, 67,102,140,220,628362982,29
//        0,0, 114,76,78, 220,300,628363635,32
//        0,0, 100,64,95, 130,230,628363895,33
//        0,0, 101,54,74, 170,270,628364055,33
//        0,0, 96, 50,73, 180,250,628364288,33
//        0,0, 113,78,89, 190,250,628595075,19 ****
//        2,0, 103,65,84, 120,240,627991051,21
//        2,0, 129,80,78, 140,290,628019111,26
//        2,0, 97, 77,89, 150,180,628357829,31 ..


//                               628596273   ----  10:02
//        0,0,97,70,86,  190,200,628355051,29
//        0,0,92,54,103, 160,220,628355564,31
//        0,0,98,64,101, 190,240,628356001,32
//        0,0,100,74,81, 150,230,628357394,28
//        0,0,104,77,68, 190,190,628361737,30
//        0,0,101,71,74, 170,200,628362281,31
//        0,0,94,67,102, 140,220,628362982,29
//        0,0,114,76,78, 220,300,628363635,32
//        0,0,100,64,95, 130,230,628363895,33
//        0,0,101,54,74, 170,270,628364055,33
//        0,0,96,50,73,  180,250,628364288,33
//        0,0,113,78,89, 190,250,628595075,19
//        0,0,133,79,89, 170,280,628596158,22 ****
//        2,0,103,65,84, 120,240,627991051,21
//        2,0,129,80,78, 140,290,628019111,26
//        2,0,97,77,89,  150,180,628357829,31 ..
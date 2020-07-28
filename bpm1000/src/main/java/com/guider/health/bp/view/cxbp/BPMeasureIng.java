package com.guider.health.bp.view.cxbp;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ftdi.j2xx.D2xxManager;
import com.ftdi.j2xx.FT_Device;
import com.guider.health.bp.R;
import com.guider.health.bp.view.BPFragment;
import com.guider.health.common.core.Config;
import com.guider.health.common.core.HeartPressCx;
import com.guider.health.common.core.Judgement;
import com.guider.health.common.core.ParamHealthRangeAnlysis;
import com.guider.health.common.core.UserManager;
import com.guider.health.common.device.DeviceInit;
import com.guider.health.common.utils.SkipClick;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * 这是连线款的血压仪
 * Created by haix on 2019/6/25.
 */

public class BPMeasureIng extends BPFragment {

    // original ///////////////////////////////
    D2xxManager d2xxManager;
    FT_Device ftDev = null;
    int DevCount = -1;
    int currentIndex = -1;
    int openIndex = 0;


    /*graphical objects*/
    EditText writeText;

    Button readEnButton;
    static int iEnableReadFlag = 1;

    String ourString = "";
    /*local variables*/
    int baudRate; /*baud rate*/
    byte stopBit; /*1:1stop bits, 2:2 stop bits*/
    byte dataBit; /*8:8bit, 7: 7bit*/
    byte parity;  /* 0: none, 1: odd, 2: even, 3: mark, 4: space*/
    byte flowControl; /*0:none, 1: flow control(CTS,RTS)*/
    int portNumber; /*port number*/


    public static final int readLength = 512;
    public int readcount = 0;
    public int iavailable = 0;
    byte[] readData;
    char[] readDataToText;
    public boolean bReadThreadGoing = false;
    public readThread read_thread;

    boolean uart_configured = false;
    View view;

    private long openTime = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        try {
            d2xxManager = D2xxManager.getInstance(getContext());
        } catch (D2xxManager.D2xxException e) {
            e.printStackTrace();
        }
        view = inflater.inflate(R.layout.bp_cx_meassure, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            return;
        }
        setHomeEvent(view.findViewById(R.id.home), Config.HOME_DEVICE);
        ((TextView) view.findViewById(R.id.title)).setText("设备测量");
        view.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startWithPop(new BPFirstOperaterReminder());
            }
        });
        view.findViewById(R.id.skip).setVisibility(View.VISIBLE);
        view.findViewById(R.id.skip).setOnClickListener(new SkipClick(this , DeviceInit.DEV_BP_CX));

        readData = new byte[readLength];
        readDataToText = new char[readLength];

        ourString = "";

        try {
            d2xxManager = D2xxManager.getInstance(getContext());
        } catch (D2xxManager.D2xxException e) {
            e.printStackTrace();
        }

        /* by default it is 9600 */
        baudRate = 2400;

        /* default is stop bit 1 */
        stopBit = 1;

        /* default data bit is 8 bit */
        dataBit = 8;

        /* default is none */
        parity = 2;

        /* default flow control is is none */
        flowControl = 3;

        portNumber = 1;

        // todotemp  openIndex = PreferenceConstant.getBpPost(getContext());

        if (DevCount <= 0) {
            createDeviceList();
        } else {
            connectFunction();
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        createDeviceList();
    }

    @Override
    public void onStop() {
        disconnectFunction();
        super.onStop();
    }

    public void createDeviceList() {
        int tempDevCount = d2xxManager.createDeviceInfoList(_mActivity);

        if (tempDevCount > 0) {
            if (DevCount != tempDevCount) {
                DevCount = tempDevCount;
//                updatePortNumberSelector();
            }
        } else {
            DevCount = -1;
            currentIndex = -1;
        }
    }

    public void disconnectFunction() {
        DevCount = -1;
        currentIndex = -1;
        bReadThreadGoing = false;
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (ftDev != null) {
            synchronized (ftDev) {
                if (ftDev.isOpen()) {
                    ftDev.close();
                }
            }
        }
    }

    public void connectFunction() {
        int tmpProtNumber = openIndex + 1;

        if (currentIndex != openIndex) {
            if (null == ftDev) {
                ftDev = d2xxManager.openByIndex(_mActivity, openIndex);
            } else {
                synchronized (ftDev) {
                    ftDev = d2xxManager.openByIndex(_mActivity, openIndex);
                }
            }
            uart_configured = false;
        } else {
//            Toast.makeText(DeviceUARTContext, "Device port " + tmpProtNumber + " is already opened", Toast.LENGTH_LONG).show();
            return;
        }

        if (ftDev == null) {
//            Toast.makeText(DeviceUARTContext, "open device port(" + tmpProtNumber + ") NG, ftDev == null", Toast.LENGTH_LONG).show();
            return;
        }

        if (true == ftDev.isOpen()) {
            currentIndex = openIndex;
//            Toast.makeText(DeviceUARTContext, "open device port(" + tmpProtNumber + ") OK", Toast.LENGTH_SHORT).show();

            if (false == bReadThreadGoing) {
                read_thread = new readThread(handler);
                read_thread.start();
                openTime = System.currentTimeMillis();
                bReadThreadGoing = true;
            }
        } else {
//            Toast.makeText(DeviceUARTContext, "open device port(" + tmpProtNumber + ") NG", Toast.LENGTH_LONG).show();
            //Toast.makeText(DeviceUARTContext, "Need to get permission!", Toast.LENGTH_SHORT).show();
        }
    }

    public void SetConfig(int baud, byte dataBits, byte stopBits, byte parity, byte flowControl) {
        if (ftDev.isOpen() == false) {
            Log.e("j2xx", "SetConfig: device not open");
            return;
        }

        // configure our port
        // reset to UART mode for 232 devices
        ftDev.setBitMode((byte) 0, D2xxManager.FT_BITMODE_RESET);

        ftDev.setBaudRate(baud);

        switch (dataBits) {
            case 7:
                dataBits = D2xxManager.FT_DATA_BITS_7;
                break;
            case 8:
                dataBits = D2xxManager.FT_DATA_BITS_8;
                break;
            default:
                dataBits = D2xxManager.FT_DATA_BITS_8;
                break;
        }

        switch (stopBits) {
            case 1:
                stopBits = D2xxManager.FT_STOP_BITS_1;
                break;
            case 2:
                stopBits = D2xxManager.FT_STOP_BITS_2;
                break;
            default:
                stopBits = D2xxManager.FT_STOP_BITS_1;
                break;
        }

        switch (parity) {
            case 0:
                parity = D2xxManager.FT_PARITY_NONE;
                break;
            case 1:
                parity = D2xxManager.FT_PARITY_ODD;
                break;
            case 2:
                parity = D2xxManager.FT_PARITY_EVEN;
                break;
            case 3:
                parity = D2xxManager.FT_PARITY_MARK;
                break;
            case 4:
                parity = D2xxManager.FT_PARITY_SPACE;
                break;
            default:
                parity = D2xxManager.FT_PARITY_NONE;
                break;
        }

        ftDev.setDataCharacteristics(dataBits, stopBits, parity);

        short flowCtrlSetting;
        switch (flowControl) {
            case 0:
                flowCtrlSetting = D2xxManager.FT_FLOW_NONE;
                break;
            case 1:
                flowCtrlSetting = D2xxManager.FT_FLOW_RTS_CTS;
                break;
            case 2:
                flowCtrlSetting = D2xxManager.FT_FLOW_DTR_DSR;
                break;
            case 3:
                flowCtrlSetting = D2xxManager.FT_FLOW_XON_XOFF;
                break;
            default:
                flowCtrlSetting = D2xxManager.FT_FLOW_NONE;
                break;
        }

        // TODO : flow ctrl: XOFF/XOM
        // TODO : flow ctrl: XOFF/XOM
        ftDev.setFlowControl(flowCtrlSetting, (byte) 0x0b, (byte) 0x0d);

        uart_configured = true;
//        Toast.makeText(DeviceUARTContext, "Config done", Toast.LENGTH_SHORT).show();
    }

    public void EnableRead() {
        iEnableReadFlag = (iEnableReadFlag + 1) % 2;

        if (iEnableReadFlag == 1) {
            ftDev.purge((byte) (D2xxManager.FT_PURGE_TX));
            ftDev.restartInTask();
            readEnButton.setText("Read Enabled");
        } else {
            ftDev.stopInTask();
            readEnButton.setText("Read Disabled");
        }
    }

    final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (iavailable > 0) {

                // todo 这里需要判断一下时间
                if (System.currentTimeMillis() - openTime <= 1000 * 2) {
                    return;
                }

                ourString = ourString + (String.copyValueOf(readDataToText, 0, iavailable));
                Log.d(TAG, "ourString" + ourString);
                if (ourString.length() > 58) {
                    String Serial = ourString;
                    ourString = "";

                    Serial = Serial.substring(Serial.length() - 58, Serial.length());
                    Log.d(TAG, "Serial" + Serial.substring(Serial.length() - 58, Serial.length()));
                    Log.d(TAG, "收縮壓" + Serial.substring(Serial.length() - 18, Serial.length() - 15));
                    Log.d(TAG, "舒張壓" + Serial.substring(Serial.length() - 10, Serial.length() - 7));
                    Log.d(TAG, "脈搏" + Serial.substring(Serial.length() - 6, Serial.length() - 3));


                    String StringSbp = Serial.substring(Serial.length() - 18, Serial.length() - 15);
                    StringSbp = StringSbp.replaceAll("^(0+)", "");

                    String StringDbp = Serial.substring(Serial.length() - 10, Serial.length() - 7);
                    StringDbp = StringDbp.replaceAll("^(0+)", "");

                    String StringHeart = Serial.substring(Serial.length() - 6, Serial.length() - 3);
                    StringHeart = StringHeart.replaceAll("^(0+)", "");

                    String bpValue = StringDbp + "/" + StringSbp;
                    // todo 重要 拿到值了  bpValue, "血壓", StringDbp, StringSbp, StringHeart
                    HeartPressCx.getInstance().setSbp(StringSbp);
                    HeartPressCx.getInstance().setDbp(StringDbp);
                    HeartPressCx.getInstance().setHeart(StringHeart);


                    List<ParamHealthRangeAnlysis> list = new ArrayList<>();

                    ParamHealthRangeAnlysis paramHealthRangeAnlysis1 = new ParamHealthRangeAnlysis();
                    paramHealthRangeAnlysis1.setType(ParamHealthRangeAnlysis.BLOODPRESSURE);
                    paramHealthRangeAnlysis1.setValue1(HeartPressCx.getInstance().getSbp());
                    paramHealthRangeAnlysis1.setValue2(HeartPressCx.getInstance().getDbp());
                    String birth = UserManager.getInstance().getBirth();
                    StringBuffer stringBuffer = new StringBuffer();
                    stringBuffer.append(birth.substring(0, 4));
                    stringBuffer.append(birth.substring(5, 7));
                    stringBuffer.append(birth.substring(8, 10));
                    paramHealthRangeAnlysis1.setYear(Integer.parseInt(stringBuffer.toString()));
                    list.add(paramHealthRangeAnlysis1);


                    List<String> results = Judgement.healthDataAnlysis(list);
                    if (results != null) {
                        HeartPressCx.getInstance().setCardShowStr(results.get(0));
                    }


                    try {
                        bReadThreadGoing = false;
                        ftDev.close();
                    } catch (Exception e) {

                    }finally {
                        startWithPop(new BPMeasureResult());
                    }
                }
            }
        }
    };

    private class readThread extends Thread {
        Handler mHandler;

        readThread(Handler h) {
            mHandler = h;
            this.setPriority(Thread.MIN_PRIORITY);
        }

        @Override
        public void run() {
            int i;

            while (true == bReadThreadGoing) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                }

                synchronized (ftDev) {
                    iavailable = ftDev.getQueueStatus();
                    if (iavailable > 0) {

                        if (iavailable > readLength) {
                            iavailable = readLength;
                        }

                        ftDev.read(readData, iavailable);
                        for (i = 0; i < iavailable; i++) {
                            readDataToText[i] = (char) readData[i];
                        }
                        Message msg = mHandler.obtainMessage();
                        mHandler.sendMessage(msg);
                    }
                }
            }
        }

    }

    /**
     * Hot plug for plug in solution
     * This is workaround before android 4.2 . Because BroadcastReceiver can not
     * receive ACTION_USB_DEVICE_ATTACHED broadcast
     */

    @Override
    public void onResume() {
        super.onResume();
        DevCount = 0;
        createDeviceList();
        if (DevCount > 0) {
            connectFunction();
            SetConfig(baudRate, dataBit, stopBit, parity, flowControl);
        }

    }


//    public void updatePortNumberSelector() {
//        //Toast.makeText(DeviceUARTContext, "updatePortNumberSelector:" + DevCount, Toast.LENGTH_SHORT).show();
//
//        if (DevCount == 2) {
//
////           mParentActivity
//            //portSpinner.setOnItemSelectedListener(new MyOnPortSelectedListener());
//        } else if (DevCount == 4) {
////            Toast.makeText(DeviceUARTContext, "4 port device attached", Toast.LENGTH_SHORT).show();
//            //portSpinner.setOnItemSelectedListener(new MyOnPortSelectedListener());
//        } else {
//
////            Toast.makeText(DeviceUARTContext, "1 port device attached", Toast.LENGTH_SHORT).show();
//            //portSpinner.setOnItemSelectedListener(new MyOnPortSelectedListener());
//        }
//
//    }


//    public void SendMessage() {
//        if (ftDev.isOpen() == false) {
//            Log.e("j2xx", "SendMessage: device not open");
//            return;
//        }
//
//        ftDev.setLatencyTimer((byte) 16);
////		ftDev.purge((byte) (D2xxManager.FT_PURGE_TX | D2xxManager.FT_PURGE_RX));
//
//        String writeData = writeText.getText().toString();
//        byte[] OutData = writeData.getBytes();
//        ftDev.write(OutData, writeData.length());
//    }


//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//
////        view.findViewById(R.id.bt_n).setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                start(new BPOperaterReminder());
////            }
////        });
//    }
}

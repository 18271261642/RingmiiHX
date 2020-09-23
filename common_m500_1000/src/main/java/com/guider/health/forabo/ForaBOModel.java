package com.guider.health.forabo;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.guider.health.bluetooth.core.BleBluetooth;
import com.guider.health.common.core.ForaBO;
import com.guider.health.foraglu.FORA;

import java.util.ArrayList;

public class ForaBOModel extends FORA {
    private static final String TAG = "ForaBOModel";

    public static final String CHARACTERISTIC_WRITE = "00001524-1212-efde-1523-785feabcd123";
    public static final String READ_FINAL_DATA = "512600000000a31a";
    public static final String READ_WEIGHT_FINAL_DATA = "5171020000a367";
    public static final String READ_DEVICE_SERIAL_NUMBER1 = "512700000000a31b";
    public static final String READ_DEVICE_SERIAL_NUMBER2 = "512800000000a31c";
    public static final String READ_DEVICE_CLOCK_TIME = "512300000000a317";
    public static final String TRUN_OFF_DEVICE = "515000000000a344";

    public static final String SET_CLOCK_CMD = "5133";
    public static final String END = "a3";

    private String serialNumber1;
    private String serialNumber2;
    private String year;
    private String month;
    private String day;
    private String hour;
    private String min;
    private String spo2;
    private String bpm;

    public void writeReadDeviceSerial() {
        write(null, READ_DEVICE_SERIAL_NUMBER1);
        //目前发现血氧仪只支持写入一次指令，再次写入系统报蓝牙写入数据失败
//        write(null, READ_FINAL_DATA);
    }

    public void write(BluetoothGatt gatt, String command) {
        BleBluetooth.getInstance().writeBuffer(getHexBytes(command));
    }

    public byte[] getHexBytes(String message) {
        int len = message.length() / 2;
        char[] chars = message.toCharArray();
        String[] hexStr = new String[len];
        byte[] bytes = new byte[len];
        for (int i = 0, j = 0; j < len; i += 2, j++) {
            hexStr[j] = "" + chars[i] + chars[i + 1];
            bytes[j] = (byte) Integer.parseInt(hexStr[j], 16);
        }
        return bytes;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void deviceSerialNumber1DateFormat(ArrayList<Byte> characteristicValues,
                                              BluetoothGattCharacteristic characteristic,
                                              BluetoothGatt gatt) {
        if (characteristic.getValue() != null & characteristicValues.size() == 6) {
            String value1 = hexString(intParse(characteristic, 2));
            String value2 = hexString(intParse(characteristic, 3));
            String value3 = hexString(intParse(characteristic, 4));
            String value4 = hexString(intParse(characteristic, 5));
            System.out.println("Mark 血氧跑來這");
            System.out.println("Mark 到底 = " + characteristic);
            System.out.println("Mark 到底2 = " + characteristicValues);
            serialNumber2 = value4 + value3 + value2 + value1;
            System.out.println("Mark 數值? = " + value1 + "," + value2 + "," + value3 + "," + value4 + "," + serialNumber2);
            // tool.writeDeviceSerialNumber2(gatt);
            write(gatt, READ_DEVICE_SERIAL_NUMBER2);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void deviceSerialNumber2DateFormat(ArrayList<Byte> characteristicValues,
                                              BluetoothGattCharacteristic characteristic,
                                              BluetoothGatt gatt) {
        if (characteristic.getValue() != null & characteristicValues.size() == 8 |
                characteristic.getValue() != null & characteristicValues.size() == 4) {
            // Log.i(TAG, "准备设置时钟");
            String value1 = hexString(intParse(characteristic, 2));
            String value2 = hexString(intParse(characteristic, 3));
            String value3 = hexString(intParse(characteristic, 4));
            String value4 = hexString(intParse(characteristic, 5));

            serialNumber1 = value4 + value3 + value2 + value1;
            String clockTime = getClockTime();
            String datetime = CalculateCheckSum(getHexBytes(SET_CLOCK_CMD + clockTime + END));
            String fallZero = "";
            int writeDateTime = 0;

            if (!isNumeric(datetime)) {
                System.out.println("if");
                fallZero = fillZero(datetime);
                if (datetime.length() > 1)
                    datetime = datetime.substring(datetime.length() - 2);
                write(gatt, SET_CLOCK_CMD + clockTime + END + fallZero + datetime);
            } else {
                System.out.println("else");
                writeDateTime = Integer.valueOf(datetime);
                if (writeDateTime >= 100)
                    writeDateTime -= 100;
                fallZero = String.valueOf(fillZero(writeDateTime));
                write(gatt, SET_CLOCK_CMD + clockTime + END + fallZero + writeDateTime);
            }
        }
    }

    @Override
    public void writeClockDateTime(ArrayList<Byte> characteristicValues, BluetoothGattCharacteristic characteristic,
                                   BluetoothGatt gatt) {
        // Log.i(TAG, "获取时钟");
        write(gatt, READ_DEVICE_CLOCK_TIME);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void readClockTimeDateFormat(ArrayList<Byte> characteristicValues,
                                        BluetoothGattCharacteristic characteristic,
                                        BluetoothGatt gatt) {
        if (characteristic.getValue() != null & characteristicValues.size() == 7 | characteristicValues.size() == 8) {
            int md = intParse(characteristic, 2);
            int yea = intParse(characteristic, 3);
            int min = intParse(characteristic, 4);
            int hour = intParse(characteristic, 5);

            String Yearbinary = String.valueOf(Integer.toBinaryString(yea));//轉二進位
            String YearLength = fillLength(8, Yearbinary);//長度
            int year = decimalFormat(0,
                    YearLength.length() - 1,
                    YearLength) + 2000;//轉十進位 Year(7-bit)計算

            String MDbinary = String.valueOf(Integer.toBinaryString(md));
            String monthDay = fillLength(8, MDbinary);
            int month = decimalFormat(0, 3, monthDay);//Month (4-bit)
            int day = decimalFormat(3, 8, monthDay);//Day (5-bit)

            setYear(String.valueOf(year));
            setMonth(String.valueOf(month));
            setDay(String.valueOf(day));
            setHour(String.valueOf(hour));
            setMin(String.valueOf(min));
            // Log.i(TAG, "准备获取血氧数据");
            write(gatt, READ_FINAL_DATA);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void measureDataFormat(ArrayList<Byte> characteristicValues,
                                  BluetoothGattCharacteristic characteristic,
                                  BluetoothGatt gatt) {
        // Log.i(TAG, "收取到数据");
        if (characteristic.getValue() != null & characteristicValues.size() == 6) {
            int sop2 = intParse(characteristic, 2);
            int bpm = intParse(characteristic, 5);
            setSpo2(String.valueOf(sop2));
            setBpm(String.valueOf(bpm));

            // 数据
            ForaBO.getForaBOInstance().setValue(sop2);
            ForaBO.getForaBOInstance().setHeartBeat(bpm);

            Log.i(TAG, "血氧 " + sop2 + "心率 " + bpm + ", 准备关机");
            write(gatt, TRUN_OFF_DEVICE);
            //目前发现血氧仪只支持写入一次指令，再次写入系统报蓝牙写入数据失败,所以关机指令也无法执行，
            // 只能是断开蓝牙连接
//            closeBluetooth(gatt);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void trunOffDevice(ArrayList<Byte> characteristicValues,
                              BluetoothGattCharacteristic characteristic,
                              BluetoothGatt gatt) {
        if (characteristic.getValue() != null & characteristicValues.size() == 4) {
             Log.i(TAG, "准备断开链接");
             System.out.println("Mark 這邊6");
            closeBluetooth(gatt);
        }
    }

    private void closeBluetooth(BluetoothGatt gatt) {
        try {
            gatt.disconnect();
            gatt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getHttpParams(String... params) {
        final StringBuilder httpParams = new StringBuilder();
        return httpParams.toString();
    }

    public String getSerialNumber() {
        return serialNumber1 + serialNumber2;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getMin() {
        return min;
    }

    public void setMin(String min) {
        this.min = min;
    }

    public String getSpo2() {
        return spo2;
    }

    public void setSpo2(String spo2) {
        this.spo2 = spo2;
    }

    public String getBpm() {
        return bpm;
    }

    public void setBpm(String bpm) {
        this.bpm = bpm;
    }

    public String CalculateCheckSum(byte[] bytes) {
        short CheckSum = 0, i = 0;
        for (i = 0; i < bytes.length; i++) {
            CheckSum = (short) ((short) CheckSum + (short) bytes[i]);
        }
        return Integer.toHexString(CheckSum);
    }
}
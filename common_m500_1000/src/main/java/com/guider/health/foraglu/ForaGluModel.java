package com.guider.health.foraglu;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Build;

import androidx.annotation.RequiresApi;

import android.util.Log;

import com.guider.health.bluetooth.core.BleBluetooth;
import com.guider.health.common.core.ForaGlucose;
import com.guider.health.common.core.MyUtils;

import java.util.ArrayList;
import java.util.Arrays;

public class ForaGluModel extends FORA {
    private String serialNumber1;
    private String serialNumber2;
    private String year;
    private String month;
    private String day;
    private String hour;
    private String min;
    private String bg;

    public static final String SET_CLOCK_CMD = "5133";
    public static final String END = "a3";
    public static final String READ_FINAL_DATA = "512600000000a31a";
    public static final String READ_WEIGHT_FINAL_DATA = "5171020000a367";
    public static final String READ_DEVICE_SERIAL_NUMBER1 = "512700000000a31b";
    public static final String READ_DEVICE_SERIAL_NUMBER2 = "512800000000a31c";
    public static final String READ_DEVICE_CLOCK_TIME = "512300000000a317";
    public static final String TRUN_OFF_DEVICE = "515000000000a344";

    public void write(BluetoothGatt gatt, String command) {
        BleBluetooth.getInstance().writeBuffer(getHexBytes(command));
    }

    public void writeReadDeviceSerial() {
        BleBluetooth.getInstance().writeBuffer(getHexBytes(READ_DEVICE_SERIAL_NUMBER1));
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

    public void deviceSerialNumber1DateFormat(ArrayList<Byte> characteristicValues,
                                              BluetoothGattCharacteristic characteristic,
                                              BluetoothGatt gatt) {
        System.out.println("Mark 有到這嗎?");
        //得到十六进制的数
        String value1 = hexString(intParse(characteristic, 2));
        String value2 = hexString(intParse(characteristic, 3));
        String value3 = hexString(intParse(characteristic, 4));
        String value4 = hexString(intParse(characteristic, 5));
        System.out.println("Mark 到底 = " + characteristic);
        System.out.println("Mark 到底2 = " + characteristicValues);
        serialNumber2 = value4 + value3 + value2 + value1;
        System.out.println("Mark 數值? = " + value1 + "," + value2 + "," + value3 + "," + value4 + "," + serialNumber2);
        // tool.writeDeviceSerialNumber2(gatt);
        write(gatt, READ_DEVICE_SERIAL_NUMBER2);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void deviceSerialNumber2DateFormat(ArrayList<Byte> characteristicValues,
                                              BluetoothGattCharacteristic characteristic,
                                              BluetoothGatt gatt) {
        if (characteristic.getValue() != null & characteristicValues.size() == 8 |
                characteristic.getValue() != null & characteristicValues.size() == 4) {
            String value1 = hexString(intParse(characteristic, 2));
            String value2 = hexString(intParse(characteristic, 3));
            String value3 = hexString(intParse(characteristic, 4));
            String value4 = hexString(intParse(characteristic, 5));

            serialNumber1 = value4 + value3 + value2 + value1;
            System.out.println("Mark 數值? = " + value1 + "," + value2 + "," + value3 + "," + value4 + "," + serialNumber2);
            //得到的是十六进制的日期
            String clockTime = getClockTime();
            // SET_CLOCK_CMD + clockTime + END  十六进制 转10进制数组
            //十进制数组转16进制
            System.out.println("拼接的时间" + SET_CLOCK_CMD + clockTime + END + "-------" + Arrays.toString(getHexBytes(SET_CLOCK_CMD + clockTime + END)) );
            String datetime = CalculateCheckSum(getHexBytes(SET_CLOCK_CMD + clockTime + END));
            System.out.println("当前时间" + clockTime + "-------" + datetime);
            String fallZero = "";
            int writeDateTime = 0;
            if (!isNumeric(datetime)) {
                System.out.println("if");
                fallZero = fillZero(datetime);
                if (datetime.length() > 1)
                    datetime = datetime.substring(datetime.length() - 2);
                System.out.println("发给服务器的时间命令" + clockTime + "---" + fallZero + "-------" + datetime);
                write(gatt, SET_CLOCK_CMD + clockTime + END + fallZero + datetime);
            } else {
                System.out.println("else");
                writeDateTime = Integer.parseInt(datetime);
                if (writeDateTime >= 100)
                    writeDateTime -= 100;
                fallZero = String.valueOf(fillZero(writeDateTime));
                System.out.println("发给服务器的时间命令" + clockTime + "---" + fallZero + "-------" + writeDateTime);
                write(gatt, SET_CLOCK_CMD + clockTime + END + fallZero + writeDateTime);
            }
        }
    }

    @Override
    public void writeClockDateTime(ArrayList<Byte> characteristicValues, BluetoothGattCharacteristic characteristic,
                                   BluetoothGatt gatt) {
        // tool.readClockTimeDate(gatt);
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
            int year = Integer.valueOf(decimalFormat(0, YearLength.length() - 1, YearLength)) + 2000;//轉十進位 Year(7-bit)計算
            String substring = YearLength.substring(YearLength.length() - 1);
            String MDbinary = Integer.toBinaryString(md);
            String monthDay = fillLength(8, MDbinary);
            String monthDayValue = substring + monthDay;
            int month = decimalFormat(0, 4, monthDayValue);//Month (4-bit)
            int day = decimalFormat(4, monthDayValue.length(), monthDayValue);//Day (5-bit)

            setYear(String.valueOf(year));
            setMonth(String.valueOf(month));
            setDay(String.valueOf(day));
            setHour(String.valueOf(hour));
            setMin(String.valueOf(min));
            System.out.println("设备返回的时间为" + year + "---" + month + "---"+ day + "---"+ hour + "---"+ min + "---");

            // tool.writeMeasureData(gatt);
            write(gatt, READ_FINAL_DATA);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void measureDataFormat(ArrayList<Byte> characteristicValues,
                                  BluetoothGattCharacteristic characteristic,
                                  BluetoothGatt gatt) {
        if (characteristic.getValue() != null & characteristicValues.size() == 7 |
                characteristic.getValue() != null & characteristicValues.size() == 6 |
                characteristic.getValue() != null & characteristicValues.size() == 4) {
            int bg = intParse(characteristic, 2);
            float fBg = bg / 18;
            setBg(String.valueOf(fBg));
            // 获得数据
            ForaGlucose.getForaGluInstance().setGlucose(fBg); // 设置血糖值
            ForaGlucose.getForaGluInstance().setDeviceAddress(MyUtils.getMacAddress());
            Log.i("haix", "Fora血糖值 ; " + fBg);//146

            // tool.writeTrunOffDevice(gatt);
            write(gatt, TRUN_OFF_DEVICE);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void trunOffDevice(ArrayList<Byte> characteristicValues,
                              BluetoothGattCharacteristic characteristic,
                              BluetoothGatt gatt) {
        if (characteristic.getValue() != null & characteristicValues.size() == 4) {
            // tool.disconnect(gatt);
            turnOffDevice(characteristicValues, characteristic, gatt);
        }
    }

    @Override
    public String getHttpParams(String... params) {
        final StringBuilder httpParams = new StringBuilder();
        return httpParams.toString();
    }


    @SuppressLint("DefaultLocale")
    public String hexString(int value) {
        String hexStr = Integer.toHexString(value);
        if (!isNumeric(hexStr))
            return fillZero(value) + hexStr.toUpperCase();
        else
            return fillZero(value) + hexStr;
    }

    public void turnOffDevice(ArrayList<Byte> characteristicValues, BluetoothGattCharacteristic characteristic,
                              BluetoothGatt gatt) {
        if (characteristic.getValue() != null & characteristicValues.size() == 4) {
            System.out.println("Mark 這邊6");
            try {
                gatt.disconnect();
                gatt.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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

    public String getBg() {
        return bg;
    }

    public void setBg(String bg) {
        this.bg = bg;
    }

    public String CalculateCheckSum(byte[] bytes) {
        short CheckSum = 0, i = 0;
        for (i = 0; i < bytes.length; i++) {
            CheckSum = (short) ((short) CheckSum + (short) bytes[i]);
        }
        return Integer.toHexString(CheckSum);
    }
}

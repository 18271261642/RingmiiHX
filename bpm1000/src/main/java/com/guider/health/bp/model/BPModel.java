package com.guider.health.bp.model;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.util.Log;

import com.guider.health.bluetooth.core.BleBluetooth;
import com.guider.health.common.core.HeartPressBp;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by haix on 2019/6/10.
 */

public class BPModel {

    public void sendDataToDevice() {
        sendDataToDevice("512700000000a31b");
    }

    public void sendDataToDevice(String command) {

        BleBluetooth.getInstance().writeBuffer(getHexBytes(command));

    }

    public void deviceSerialNumber1DateFormat(ArrayList<Byte> characteristicValues, BluetoothGattCharacteristic characteristic,
                                              BluetoothGatt gatt) {
        if (characteristic.getValue() != null & characteristicValues.size() == 7 |
                characteristic.getValue() != null & characteristicValues.size() == 8 |
                characteristic.getValue() != null & characteristicValues.size() == 6 |
                characteristic.getValue() != null & characteristicValues.size() == 5) {
            String value1 = hexString(intParse(characteristic, 2));
            String value2 = hexString(intParse(characteristic, 3));
            String value3 = hexString(intParse(characteristic, 4));
            String value4 = hexString(intParse(characteristic, 5));

            Log.i("haix", "设备第一次返回的值: "+ value1 + " : "+ value2 + " : "+ value3 + " : "+ value4);

            sendDataToDevice("512800000000a31c");
        }
    }

    public void deviceSerialNumber2DateFormat(ArrayList<Byte> characteristicValues, BluetoothGattCharacteristic characteristic,
                                              BluetoothGatt gatt) {
        if (characteristic.getValue() != null & characteristicValues.size() == 8) {
            String value1 = hexString(intParse(characteristic, 2));
            String value2 = hexString(intParse(characteristic, 3));
            String value3 = hexString(intParse(characteristic, 4));
            String value4 = hexString(intParse(characteristic, 5));

            Log.i("haix", "设备第二次返回的值: "+ value1 + " : "+ value2 + " : "+ value3 + " : "+ value4);
            writeClockTimeDate(gatt);
        }
    }

    public void writeClockTimeDate(BluetoothGatt gatt) {
        String SET_CLOCK_CMD = "5133";
        String END = "a3";
        String clockTime = getClockTime();
        String datetime = CalculateCheckSum(getHexBytes(SET_CLOCK_CMD + clockTime + END));
        //System.out.println(datetime);
        int writeDateTime = 0;
        String fallZero = "";
        if (!isNumeric(datetime)) {
            //System.out.println("if");
            fallZero = fillZero(datetime);
            if (datetime.length() > 1)
                datetime = datetime.substring(datetime.length() - 2, datetime.length());

            sendDataToDevice(SET_CLOCK_CMD + clockTime + END + fallZero + datetime);
        } else {
            //System.out.println("else");
            writeDateTime = Integer.valueOf(datetime);
            if (writeDateTime >= 100)
                writeDateTime -= 100;
            fallZero = String.valueOf(fillZero(writeDateTime));

            sendDataToDevice(SET_CLOCK_CMD + clockTime + END + fallZero + String.valueOf(writeDateTime));
        }
    }

    public static String CalculateCheckSum(byte[] bytes) {
        short CheckSum = 0, i = 0;
        for (i = 0; i < bytes.length; i++) {
            CheckSum = (short) ((short) CheckSum + (short) bytes[i]);
        }
        return Integer.toHexString(CheckSum);
    }


    public String getClockTime() {
        String yearBinary = Integer.toBinaryString(Integer.valueOf(ChangeDateFormat.getYear()) - 2000);
        String year = fillLength(7, yearBinary);

        String monthBinary = Integer.toBinaryString(Integer.valueOf(ChangeDateFormat.getMM()));
        String month = fillLength(4, monthBinary);

        String dayBinary = Integer.toBinaryString(Integer.valueOf(ChangeDateFormat.getDD()));
        String day = fillLength(5, dayBinary);

        String binaryTotal = year + month + day;

        int decimalYear = decimalFormat(0, binaryTotal.length() - 8, binaryTotal);
        String hexadecimalYear = hexString(decimalYear);
        int decimalMonthDay = decimalFormat(binaryTotal.length() - 8, binaryTotal.length(), binaryTotal);
        String hexadecimalYearMonthDay = hexIndex(hexString(decimalMonthDay));
        Log.e("year", "year:" + hexadecimalYear);
        Log.e("MonthDay", "MonthDay:" + hexadecimalYearMonthDay);

//		18:29 18:37
        String hexadecimalmm = hexIndex(hexString(Integer.valueOf(ChangeDateFormat.getmm())));
        String hexadecimalhh = hexIndex(hexString(Integer.valueOf(ChangeDateFormat.getHH())));

        Log.e("hexadecimalmm", "hexadecimalmm:" + hexadecimalmm);
        Log.e("hexadecimalhh", "hexadecimalhh:" + hexadecimalhh);

        String resault = hexadecimalYearMonthDay + hexadecimalYear + hexadecimalmm + hexadecimalhh;
        return resault;
    }

    public String fillLength(int length, String value) {
        NumberFormat nf = NumberFormat.getInstance();
        nf.setGroupingUsed(false);//設定數字是否會以逗號分組(ex:123,456,789)
        nf.setMinimumIntegerDigits(length);//設定數字最少幾位數
        return nf.format(Integer.valueOf(value));
    }

    public int decimalFormat(int positionStart, int positionEnd, String mLength) {
        return Integer.valueOf(mLength.substring(positionStart, positionEnd), 2);
    }

    public String hexIndex(String hex) {
        if (hex.length() == 1)
            return "0" + hex;
        else
            return hex;
    }

    public void writeClockDateTime(ArrayList<Byte> characteristicValues, BluetoothGattCharacteristic characteristic,
                                   BluetoothGatt gatt) {

        System.out.println("Mark 這邊3");


        sendDataToDevice("512300000000a317");
    }


    public void readClockTimeDateFormat(ArrayList<Byte> characteristicValues, BluetoothGattCharacteristic characteristic,
                                        BluetoothGatt gatt) {
        if (characteristic.getValue() != null & characteristicValues.size() == 8) {
            int md = intParse(characteristic, 2);
            int yea = intParse(characteristic, 3);
            int min = intParse(characteristic, 4);//fen
            int hour = intParse(characteristic, 5);//shi

            String Yearbinary = String.valueOf(Integer.toBinaryString(yea));//轉二進位
            String YearLength = fillLength(8, Yearbinary);//二進位長度
            int year = Integer.valueOf(decimalFormat(0, YearLength.length() - 1, YearLength)) + 2000;//轉十進位 Year(7-bit)計算

            String MDbinary = String.valueOf(Integer.toBinaryString(md));
            String monthDay = fillLength(8, MDbinary);
            int month = decimalFormat(0, 3, monthDay);//Month (4-bit)
            int day = decimalFormat(3, 8, monthDay);//Day (5-bit)

            System.out.println("Mark 這邊4");


            sendDataToDevice("512600000000a31a");
        }
    }


    public void measureDataFormat(ArrayList<Byte> characteristicValues, BluetoothGattCharacteristic characteristic,
                                  BluetoothGatt gatt) {
        if (characteristic.getValue() != null & characteristicValues.size() == 8 |
                characteristic.getValue() != null & characteristicValues.size() == 7) {

            System.out.println("Mark 這邊5");

            HeartPressBp.getInstance().setSbp(String.valueOf(intParse(characteristic, 2)));
            HeartPressBp.getInstance().setDbp(String.valueOf(intParse(characteristic, 4)));
            HeartPressBp.getInstance().setHeart(String.valueOf(intParse(characteristic, 5)));

            Log.i("haix", "血压值1; "+ HeartPressBp.getInstance().getSbp());//146
            Log.i("haix", "血压值2; "+ HeartPressBp.getInstance().getDbp());//96
            Log.i("haix", "血压值3; "+ HeartPressBp.getInstance().getHeart());//71
            sendDataToDevice("515000000000a344");
        }
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

    public String hexString(int value) {
        String hexStr = Integer.toHexString(value);
        if (!isNumeric(hexStr))
            return fillZero(value) + hexStr.toUpperCase();
        else
            return fillZero(value) + hexStr;
    }

    public boolean isNumeric(String str) {
        for (int i = str.length(); --i >= 0; ) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public String fillZero(int value) {
        String fill = "";
        if (value < 10) fill = "0";
        return fill;
    }

    public String fillZero(String value) {
        String fill = "";
        if (value.length() == 1) fill = "0";
        return fill;
    }

    public int intParse(BluetoothGattCharacteristic characteristic, int position) {
        return (characteristic.getValue()[position] <= 0 ? byteToInt(characteristic.getValue()[position]) :
                characteristic.getValue()[position]);
    }


    public int byteToInt(byte b) {
        return b & 0xFF;
    }


    public static byte[] getHexBytes(String message) {
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


    public static class ChangeDateFormat {

        public static String CreateDate() {
            SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS 'GMT'Z");
            String format_Date = dateformat.format(new Date());
            return format_Date;
        }


        public static String BsTime() {
            SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String format_Date = dateformat.format(new Date());
            return format_Date;
        }


        public static String UpdataDate() {
            SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String format_Date = dateformat.format(new Date());
            return format_Date;
        }


        public static String getYear() {
            SimpleDateFormat dateformat = new SimpleDateFormat("yyyy");
            String format_Date = dateformat.format(new Date());
            return format_Date;
        }


        public static String getMM() {
            SimpleDateFormat dateformat = new SimpleDateFormat("MM");
            String format_Date = dateformat.format(new Date());
            return format_Date;
        }


        public static String getDD() {
            SimpleDateFormat dateformat = new SimpleDateFormat("dd");
            String format_Date = dateformat.format(new Date());
            return format_Date;
        }


        public static String getHH() {
            SimpleDateFormat dateformat = new SimpleDateFormat("HH");
            String format_Date = dateformat.format(new Date());
            return format_Date;
        }


        public static String getmm() {
            SimpleDateFormat dateformat = new SimpleDateFormat("mm");
            String format_Date = dateformat.format(new Date());
            return format_Date;
        }


        public static String getss() {
            SimpleDateFormat dateformat = new SimpleDateFormat("ss");
            String format_Date = dateformat.format(new Date());
            return format_Date;
        }


        public static String TimeStamp() {
            return String.valueOf(new Date().getTime());
        }


        public static Date getDatebirt(String dateString) {
            try {
                SimpleDateFormat dateformat = null;
                dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS 'GMT'Z");
                Date date = dateformat.parse(dateString);
                return date;
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}

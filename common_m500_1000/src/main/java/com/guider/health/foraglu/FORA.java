package com.guider.health.foraglu;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Build;
import androidx.annotation.RequiresApi;
import android.util.Log;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public abstract class FORA {
 public abstract void deviceSerialNumber1DateFormat(ArrayList<Byte> characteristicValues, 
		 											BluetoothGattCharacteristic characteristic, 
													BluetoothGatt gatt);
 public abstract void deviceSerialNumber2DateFormat(ArrayList<Byte> characteristicValues, 
													BluetoothGattCharacteristic characteristic, 
													BluetoothGatt gatt);
 public abstract void writeClockDateTime(ArrayList<Byte> characteristicValues, 
										 BluetoothGattCharacteristic characteristic, 
										 BluetoothGatt gatt);
 public abstract void readClockTimeDateFormat(ArrayList<Byte> characteristicValues, 
											  BluetoothGattCharacteristic characteristic, 
											  BluetoothGatt gatt);
 public abstract void measureDataFormat(ArrayList<Byte> characteristicValues, 
										BluetoothGattCharacteristic characteristic, 
										BluetoothGatt gatt);
 public abstract void trunOffDevice(ArrayList<Byte> characteristicValues, 
									BluetoothGattCharacteristic characteristic, 
									BluetoothGatt gatt);
 public abstract String getHttpParams(String... params);
 
 	public String getDeviceYear() {
		return new SimpleDateFormat("yyyy").format(new Date());
	}
 
 	public String getDeviceMonth() {
		return new SimpleDateFormat("MM").format(new Date());
	}
 
 	public String getDeviceDay() {
		return new SimpleDateFormat("dd").format(new Date());
	}
 	
	@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
	public int intParse(BluetoothGattCharacteristic characteristic, int position) {
		return (characteristic.getValue()[position] <= 0 ? byteToInt(characteristic.getValue()[position]) : 
         												             characteristic.getValue()[position]);	
	}
	
	public int intParse(ArrayList<Byte> characteristicValues, int position) {		 
		return 0;	
	}
	
	public int decimalFormat(int positionStart, int positionEnd, String mLength) {
 		//截取的字符串从一个二进制的数转为10进制的数
		String substring = mLength.substring(positionStart, positionEnd);
		return Integer.valueOf(mLength.substring(positionStart, positionEnd), 2);
	}
	
	public int byteToInt(byte b) {  	   
	    return b & 0xFF;  
	}
	
	@SuppressLint("DefaultLocale")
	public String hexString(int value) {
 		//转成十六进制
		String hexStr = Integer.toHexString(value);	
		if (!isNumeric(hexStr))
			return fillZero(value) + hexStr.toUpperCase();
		else
			return fillZero(value) + hexStr;				
	}
	
	public boolean isNumeric(String str) {
		for (int i = str.length(); --i >= 0;) {
			//判断字符是否为数字
			if (!Character.isDigit(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	public String fillZero(int value) {
		String fill = "";
		if(value < 10) fill = "0";	
		return fill;	
	}
 
	public String fillZero(String value) {
		String fill = "";
		if(value.length() == 1) fill = "0";	
		return fill;	
	}
	
	public String hexIndex(String hex) {
		if (hex.length() == 1)
			return "0" + hex;
		else
			return hex;
	}
	
	public String fillLength(int length, String value) {
		NumberFormat nf = NumberFormat.getInstance();
		nf.setGroupingUsed(false);//設定數字是否會以逗號分組(ex:123,456,789)
		nf.setMinimumIntegerDigits(length);//設定數字最少幾位數
		return nf.format(Integer.valueOf(value));
	}

	public StringBuffer getStringBuffer() {
		return new StringBuffer();
	}
	
	public String getClockTime() {
 		//十进制转换为二进制输出
		String yearBinary = Integer.toBinaryString(Integer.valueOf(ChangeDateFormat.getYear()) - 2000);
		Log.e("currentYear", "currentYear:" + ChangeDateFormat.getYear());
		String year = fillLength(7, yearBinary);

		String monthBinary = Integer.toBinaryString(Integer.valueOf(ChangeDateFormat.getMM()));
		Log.e("currentMonth", "currentMonth:" + ChangeDateFormat.getMM());
		String month = fillLength(4, monthBinary);
		
		String dayBinary = Integer.toBinaryString(Integer.valueOf(ChangeDateFormat.getDD()));
		Log.e("currentDay", "currentDay:" + ChangeDateFormat.getDD());
		String day = fillLength(5, dayBinary);
		 
		String binaryTotal = year + month + day;
		Log.e("currentYear", "year:" + year+"month:" + month+"day:" + day);
		//二进制转十六进制
		int decimalYear = decimalFormat(0, binaryTotal.length() -8, binaryTotal);
		Log.e("decimalYear", decimalYear+"");
		//转成16进制
		String hexadecimalYear = hexString(decimalYear);
		int decimalMonthDay = decimalFormat(binaryTotal.length() - 8, binaryTotal.length(), binaryTotal);
		Log.e("decimalMonthDay", decimalMonthDay+"");
		//转成16进制
		String hexadecimalYearMonthDay = hexIndex(hexString(decimalMonthDay));
		Log.e("year", "year:" + hexadecimalYear);
		Log.e("MonthDay", "MonthDay:" + hexadecimalYearMonthDay);
		
//		18:29 18:37
		//转成16进制  16 --20
		String hexadecimalmm = hexIndex(hexString(Integer.valueOf(ChangeDateFormat.getmm())));
		String hexadecimalhh = hexIndex(hexString(Integer.valueOf(ChangeDateFormat.getHH())));
		Log.e("getmm", hexString(Integer.valueOf(ChangeDateFormat.getmm())));
		Log.e("hexadecimalmm", "hexadecimalmm:" + hexadecimalmm);
		Log.e("getHH", hexString(Integer.valueOf(ChangeDateFormat.getHH())));
		Log.e("hexadecimalhh", "hexadecimalhh:" + hexadecimalhh);
		//十六进制的拼接
		String resault = hexadecimalYearMonthDay + hexadecimalYear + hexadecimalmm + hexadecimalhh;	
		return resault;
	}
}
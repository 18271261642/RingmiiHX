package com.guider.health.foraet;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Build;
import androidx.annotation.RequiresApi;
import android.util.Log;

import com.guider.health.bluetooth.core.BleBluetooth;
import com.guider.health.common.core.ForaET;
import com.guider.health.common.core.MyUtils;
import com.guider.health.foraglu.FORA;

import java.util.ArrayList;


public class ForaETModel extends FORA {
	public static final String CHARACTERISTIC_WRITE = "00001524-1212-efde-1523-785feabcd123";
	public static final String READ_FINAL_DATA = "512600000000a31a";
	public static final String READ_WEIGHT_FINAL_DATA = "5171020000a367";
	public static final String READ_DEVICE_SERIAL_NUMBER1 = "512700000000a31b";
	public static final String READ_DEVICE_SERIAL_NUMBER2 = "512800000000a31c";
	public static final String READ_DEVICE_CLOCK_TIME = "512300000000a317";
	public static final String TRUN_OFF_DEVICE = "515000000000a344";

	public static final String SET_CLOCK_CMD = "5133";
	public static final String END = "a3";

	// public EarThermometerTool tool;
	private String serialNumber1;
	private String serialNumber2;	
	private String year;
	private String month;
	private String day;			
	private String hour;	
	private String min;
	private String temperature;

	public void writeReadDeviceSerial() {
		write(null, READ_DEVICE_SERIAL_NUMBER1);
	}

	@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
	@Override
	public void deviceSerialNumber1DateFormat(ArrayList<Byte> characteristicValues, 
											  BluetoothGattCharacteristic characteristic, 
											  BluetoothGatt gatt) {
		if (characteristic.getValue() != null & characteristicValues.size() == 7 | 
			characteristic.getValue() != null & characteristicValues.size() == 8 |
			characteristic.getValue() != null & characteristicValues.size() == 6) {
			String value1 = hexString(intParse(characteristic, 2));
			String value2 = hexString(intParse(characteristic, 3));
			String value3 = hexString(intParse(characteristic, 4));
			String value4 = hexString(intParse(characteristic, 5));
			
			System.out.println("Mark 耳溫跑來這");
			System.out.println("Mark 到底 = " + characteristic);
			System.out.println("Mark 到底2 = " + characteristicValues);
			serialNumber2 = value4 + value3 + value2 + value1;
			System.out.println("Mark 數值? = " + value1 + "," + value2 + "," + value3 + "," + value4 + "," + serialNumber2);
			write(gatt, READ_DEVICE_SERIAL_NUMBER2);
		}
	}

	@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
	@Override
	public void deviceSerialNumber2DateFormat(ArrayList<Byte> characteristicValues, 
											  BluetoothGattCharacteristic characteristic, 
			  								  BluetoothGatt gatt) {
		
		if (characteristic.getValue() != null & characteristicValues.size() == 8) {				 			 
			String value1 = hexString(intParse(characteristic, 2));
			String value2 = hexString(intParse(characteristic, 3));
			String value3 = hexString(intParse(characteristic, 4));
			String value4 = hexString(intParse(characteristic, 5));
				
			serialNumber1 = value4 + value3 + value2 + value1;	
			System.out.println("Mark 數值? = " + value1 + "," + value2 + "," + value3 + "," + value4 + "," + serialNumber2);
			String clockTime = getClockTime();
			String datetime = CalculateCheckSum(getHexBytes(SET_CLOCK_CMD + clockTime + END));
			String fallZero = "";
			int writeDateTime = 0;

			if(!isNumeric(datetime)) {
				System.out.println("if");
				fallZero = fillZero(datetime);
				if (datetime.length() > 1)
					datetime = datetime.substring(datetime.length() - 2, datetime.length());
				write(gatt, SET_CLOCK_CMD + clockTime + END + fallZero + datetime);
			} else {
				System.out.println("else");
				writeDateTime = Integer.valueOf(datetime);
				if (writeDateTime >= 100)
					writeDateTime -= 100;
				fallZero = String.valueOf(fillZero(writeDateTime));
				write(gatt, SET_CLOCK_CMD + clockTime + END + fallZero + String.valueOf(writeDateTime));
			}
		}
	}
	
	@Override
	public void writeClockDateTime(ArrayList<Byte> characteristicValues, BluetoothGattCharacteristic characteristic, BluetoothGatt gatt) {
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
			write(gatt, READ_FINAL_DATA);
		}
	}

	@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
	@Override
	public void measureDataFormat(ArrayList<Byte> characteristicValues, 
								  BluetoothGattCharacteristic characteristic, 
								  BluetoothGatt gatt) {
		
		if (characteristic.getValue() != null & characteristicValues.size() == 7 | 
			characteristic.getValue() != null & characteristicValues.size() == 8) {				 
			int temperature = intParse(characteristic, 2);	
			double equation1 = temperature + 256;
			double equation2  = equation1 / 10;
			Log.e("temp", equation2+"");
			setTemperature(String.valueOf(equation2));
			ForaET.getForaETInstance().setValue((float) equation2); // 设置耳温值
			ForaET.getForaETInstance().setDeviceAddress(MyUtils.getMacAddress());

			// 耳温值
			write(gatt, TRUN_OFF_DEVICE);
		}
	}

	@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
	@Override
	public void trunOffDevice(ArrayList<Byte> characteristicValues, 
							  BluetoothGattCharacteristic characteristic, 
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

	public String getTemperature() {
		return temperature;
	}

	public void setTemperature(String temperature) {
		this.temperature = temperature;
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

	public void write(BluetoothGatt gatt, String command) {
		BleBluetooth.getInstance().writeBuffer(getHexBytes(command));
	}

	public String CalculateCheckSum(byte[] bytes) {
		short CheckSum = 0, i = 0;
		for (i = 0; i < bytes.length; i++) {
			CheckSum = (short) ((short) CheckSum + (short) bytes[i]);
		}
		return Integer.toHexString(CheckSum);
	}
}
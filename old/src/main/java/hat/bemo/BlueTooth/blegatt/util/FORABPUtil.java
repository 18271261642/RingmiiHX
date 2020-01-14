package hat.bemo.BlueTooth.blegatt.util;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Build;
import android.support.annotation.RequiresApi;

import java.util.ArrayList;

import hat.bemo.BlueTooth.blegatt.baseService.BLEScanService;
import hat.bemo.BlueTooth.blegatt.label.FORA;


public class FORABPUtil extends FORA {
	public BPTool tool;
	private String serialNumber1;
	private String serialNumber2;	
	private String year;
	private String month;
	private String day;			
	private String hour;	
	private String min;
	private String dbp;
	private String sbp;
	private String heart;
	
	public FORABPUtil() {
		
	}
	
	public interface BPTool {
		public void writeClockTimeDate(BluetoothGatt gatt);
		public void writeDeviceSerialNumber2(BluetoothGatt gatt);
		public void readClockTimeDate(BluetoothGatt gatt);
		public void writeMeasureData(BluetoothGatt gatt);
		public void writeTrunOffDevice(BluetoothGatt gatt);
		public void disconnect(BluetoothGatt gatt);
	}
	
	public void setOnFORADateFormatTool(BPTool tool){
		this.tool = tool;
	}

	@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
	@Override
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
			
			System.out.println("Mark 這邊1");

			serialNumber2 = value4 + value3 + value2 + value1;
			System.out.println("Mark 數值deviceSerialNumber1DateFormat = " + value1 + "," + value2 + "," + value3 + "," + value4 + "," + serialNumber2);
			tool.writeDeviceSerialNumber2(gatt);
		}	 
	}

	@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
	@Override
	public void deviceSerialNumber2DateFormat(ArrayList<Byte> characteristicValues, BluetoothGattCharacteristic characteristic, 
				 																	BluetoothGatt gatt) {
		if (characteristic.getValue() != null & characteristicValues.size() == 8 ) {				 			 
			String value1 = hexString(intParse(characteristic, 2));
			String value2 = hexString(intParse(characteristic, 3));
			String value3 = hexString(intParse(characteristic, 4));
			String value4 = hexString(intParse(characteristic, 5));
			
			System.out.println("Mark 這邊2");
				
			serialNumber1 = value4 + value3 + value2 + value1;
			System.out.println("Mark 數值deviceSerialNumber2DateFormat = " + value1 + "," + value2 + "," + value3 + "," + value4 + "," + serialNumber1);
			tool.writeClockTimeDate(gatt);
		}
	}

	@Override
	public void writeClockDateTime(ArrayList<Byte> characteristicValues, BluetoothGattCharacteristic characteristic, 
																		 BluetoothGatt gatt) {	
		
		System.out.println("Mark 這邊3");
		
		tool.readClockTimeDate(gatt);		
	}

	@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
	@Override
	public void readClockTimeDateFormat(ArrayList<Byte> characteristicValues, BluetoothGattCharacteristic characteristic, 
																			  BluetoothGatt gatt) {
		if (characteristic.getValue() != null & characteristicValues.size() == 8) {		
			int md = intParse(characteristic, 2);
			int yea = intParse(characteristic, 3);
			int min = intParse(characteristic, 4);
			int hour = intParse(characteristic, 5);
			
			String Yearbinary = String.valueOf(Integer.toBinaryString(yea));//轉二進位
			String YearLength = fillLength(8, Yearbinary);//二進位長度
			int year = Integer.valueOf(decimalFormat(0, YearLength.length() - 1, YearLength)) + 2000;//轉十進位 Year(7-bit)計算
			
			String MDbinary = String.valueOf(Integer.toBinaryString(md));
			String monthDay = fillLength(8, MDbinary);
			int month = decimalFormat(0, 3, monthDay);//Month (4-bit)
			int day = decimalFormat(3, 8, monthDay);//Day (5-bit)
			
			System.out.println("Mark 這邊4");
			
			setYear(String.valueOf(year));
			setMonth(String.valueOf(month));
			setDay(String.valueOf(day));
			setHour(String.valueOf(hour));
			setMin(String.valueOf(min));
			tool.writeMeasureData(gatt);
		}
	}

	@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
	@Override
	public void measureDataFormat(ArrayList<Byte> characteristicValues, BluetoothGattCharacteristic characteristic, 
																	    BluetoothGatt gatt) {	
		if (characteristic.getValue() != null & characteristicValues.size() == 8 |
			characteristic.getValue() != null & characteristicValues.size() == 7) {		
			
			System.out.println("Mark 這邊5");
			
			//Mark 改成7個值也可以唷
			int dbp = intParse(characteristic, 2);
			int sbp = intParse(characteristic, 4);
			int heart = intParse(characteristic, 5);			
			setDbp(String.valueOf(dbp));
			setSbp(String.valueOf(sbp));
			setHeart(String.valueOf(heart));
			tool.writeTrunOffDevice(gatt);
		}
	}

	@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
	@Override
	public void trunOffDevice(ArrayList<Byte> characteristicValues, BluetoothGattCharacteristic characteristic, 
				     												BluetoothGatt gatt) {	
		if (characteristic.getValue() != null & characteristicValues.size() == 4) {	
			
			System.out.println("Mark 這邊6");
			
			tool.disconnect(gatt);
		}
	}

	@Override
	public String getHttpParams(String... params) {
		
		System.out.println("Mark 這邊有經過嗎?");
		
        final StringBuilder httpParams = new StringBuilder();
        String separator = "\r\n";
        httpParams.append("FunctionName=").append("AddNewData").append(separator);
		httpParams.append("GatewayType=").append("9022").append(separator);
		httpParams.append("GatewayID=").append(BLEScanService.getImei()).append(separator);
		httpParams.append("DeviceType=").append(params[1].substring(0,4)).append(separator);
		httpParams.append("DeviceID=").append(params[1]).append(separator);
		httpParams.append("ExtensionID=").append("0").append(separator);
//		httpParams.append("GroupID=").append("TAI01").append(separator);
//		httpParams.append("PatientID=").append("001234568").append(separator);
//		httpParams.append("Year=").append(params[2]).append(separator);
//		httpParams.append("Month=").append(params[3]).append(separator);
//		httpParams.append("Day=").append(params[4]).append(separator);
		httpParams.append("Year=").append(getDeviceYear()).append(separator);
		httpParams.append("Month=").append(getDeviceMonth()).append(separator);
		httpParams.append("Day=").append(getDeviceDay()).append(separator);
		httpParams.append("Hour=").append(params[5]).append(separator);
		httpParams.append("Minute=").append(params[6]).append(separator);
//		httpParams.append("Second=").append(ChangeDateFormat.getss()).append(separator);
		httpParams.append("DataType=").append("2").append(separator);
		httpParams.append("Value1=").append(params[7]).append(separator);
		httpParams.append("Value2=").append(params[8]).append(separator);
		httpParams.append("Value3=").append(params[9]).append(separator);
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

	public String getDbp() {
		return dbp;
	}

	public void setDbp(String dbp) {
		this.dbp = dbp;
	}

	public String getSbp() {
		return sbp;
	}

	public void setSbp(String sbp) {
		this.sbp = sbp;
	}

	public String getHeart() {
		return heart;
	}

	public void setHeart(String heart) {
		this.heart = heart;
	}
}
package hat.bemo.BlueTooth.blegatt.baseService;

import java.util.ArrayList;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.os.Build;
import androidx.annotation.RequiresApi;

public abstract class BaseDevice {	
	public static final String CHARACTERISTIC_CD01 = "00001800-0000-1000-8000-00805f9b34fb";
	public static final String CHARACTERISTIC_CD02 = "00001801-0000-1000-8000-00805f9b34fb";
	public static final String CHARACTERISTIC_CD03 = "00001810-0000-1000-8000-00805f9b34fb";
	public static final String CHARACTERISTIC_CD04 = "0000180f-0000-1000-8000-00805f9b34fb";
	public static final String CHARACTERISTIC_CD05 = "0000180a-0000-1000-8000-00805f9b34fb";
	public static final String SERVICE_UUID_PREFIX = "00001523-1212-efde-1523-785feabcd123";
	public static final String CHARACTERISTIC_WRITE = "00001524-1212-efde-1523-785feabcd123";
	public static final String USER_APP_UUID = "00002902-0000-1000-8000-00805f9b34fb";	
	public static final String SET_CLOCK_CMD = "5133";
	public static final String END = "a3";
	public static final String READ_FINAL_DATA = "512600000000a31a";
	public static final String READ_WEIGHT_FINAL_DATA = "5171020000a367";
	public static final String READ_DEVICE_SERIAL_NUMBER1 = "512700000000a31b";
	public static final String READ_DEVICE_SERIAL_NUMBER2 = "512800000000a31c";  
	public static final String READ_DEVICE_CLOCK_TIME = "512300000000a317";	
	public static final String TRUN_OFF_DEVICE = "515000000000a344";
	
	public static final String PARAMETERS_KEY = "KEY";
	
	public BluetoothGattService gattServiceOut; 
	public BluetoothAdapter mBluetoothAdapter;
	public BluetoothManager mBluetoothManager;
	 	 
	public abstract void getCharacteristic(BluetoothGatt gatt);	
	public abstract void setBluetoothGattCharacteristic(BluetoothGatt mBluetoothGatt, String uuid);
	public abstract void setCharacteristicNotification(BluetoothGatt gatt, 
													   BluetoothGattCharacteristic characteristic, 
													   boolean enabled);
	public abstract void write(BluetoothGatt gatt, String command);
	public abstract void close(BluetoothGatt gatt);
	public abstract void stopService();
	public abstract void startService();
	
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
	public int intParse(BluetoothGattCharacteristic characteristic, int position) {
		return (characteristic.getValue()[position] <= 0 ? byteToInt(characteristic.getValue()[position]) : 
            												         characteristic.getValue()[position]);	
	}
 
	public int intParse(ArrayList<Byte> characteristicValues, int position) {		 
		return 0;	
	}
	
	public int byteToInt(byte b) {  	   
	    return b & 0xFF;  
	}
	
	public String CalculateCheckSum(byte[] bytes) {		
		short CheckSum = 0, i = 0;
		for (i = 0; i < bytes.length; i++) {
			CheckSum = (short) ((short) CheckSum + (short) bytes[i]);
		}
		return Integer.toHexString(CheckSum);
	}
}
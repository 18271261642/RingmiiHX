package hat.bemo.measure.service;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.os.Build;
import androidx.annotation.RequiresApi;
import android.util.Log;

import java.util.UUID;

import hat.bemo.measure.set.Bluetooth4DeviceInterface;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class ChoicemmedPluseOximeterService implements Bluetooth4DeviceInterface{
	public static final String SERVICE_UUID_PREFIX = "ba11f08c-5f14-0b0d-1080";
	public static final String CHARACTERISTIC_CD01 = "0000cd01-0000-1000-8000-00805f9b34fb";
	public static final String CHARACTERISTIC_CD02 = "0000cd02-0000-1000-8000-00805f9b34fb";
	public static final String CHARACTERISTIC_CD03 = "0000cd03-0000-1000-8000-00805f9b34fb";
	public static final String CHARACTERISTIC_CD04 = "0000cd04-0000-1000-8000-00805f9b34fb";
	public static final String CHARACTERISTIC_WRITE = "0000cd20-0000-1000-8000-00805f9b34fb";
	public static final String USER_APP_UUID = "00002902-0000-1000-8000-00805f9b34fb";
	private BluetoothGattService gattServiceOut;
	BluetoothGatt gattOut;
	
	@Override
	public BluetoothGattCallback getCallbackObject() {
		return mGattCallback;
	}
	
	final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
		@Override
		public void onConnectionStateChange(BluetoothGatt gatt, int status,int newState) {
			if (newState == BluetoothProfile.STATE_CONNECTED) {
				Log.e("starlin", "Attempting to start service discovery:"+ gatt.discoverServices());
			}
		}
		
		@Override
		public void onServicesDiscovered(BluetoothGatt gatt, int status) {
			gattOut = gatt;
			if (status == BluetoothGatt.GATT_SUCCESS) {
				Log.e("starlin", "in service "+gatt.getServices().size());
				
	    		for(BluetoothGattService gattService : gatt.getServices()){
	    			if(gattService.getUuid().toString().contains(SERVICE_UUID_PREFIX)){
	    				gattServiceOut = gattService;
	    				BluetoothGattCharacteristic mCharacteristicCD01 = gattService.getCharacteristic(UUID.fromString(CHARACTERISTIC_CD01));
	    				setCharacteristicNotification(gatt, mCharacteristicCD01, true);
	    				break;
	    			}
	    		}
			}
		}
		
		@Override
		public void onCharacteristicRead(BluetoothGatt gatt,BluetoothGattCharacteristic characteristic, int status) {
		}
		
		@Override
		public void onCharacteristicWrite(BluetoothGatt gatt,BluetoothGattCharacteristic characteristic, int status) {
		};
		
		@Override
		public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
			if (characteristic.getValue() != null) {
				Log.e("starlin ",characteristic.getUuid().toString());
				Log.e("starlin", "onCharacteristicChanged "+characteristic.getValue()[4]);				
			}
			if(characteristic.getValue() != null && characteristic.getUuid().equals(UUID.fromString(CHARACTERISTIC_CD04)) ){
				Log.e("starlin", "血氧: "+characteristic.getValue()[3]);
				Log.e("starlin", "心率: "+characteristic.getValue()[4]);

				BluetoothBaseActivity.result.ResultData(characteristic.getValue());
			}
		}
		
		@Override
		public void onDescriptorWrite(BluetoothGatt gatt,BluetoothGattDescriptor descriptor, int status) {
			Log.e("starlin","onDescriptorWriteonDescriptorWrite" );
			UUID uuid = descriptor.getCharacteristic().getUuid();
			
			if (uuid.equals(UUID.fromString(CHARACTERISTIC_CD01))){
				executeCd02();
				Log.e("starlin","executeCd02");
			}
			else if (uuid.equals(UUID.fromString(CHARACTERISTIC_CD02))){
				executeCd03();
				Log.e("starlin","executeCd03");
			}
			else if (uuid.equals(UUID.fromString(CHARACTERISTIC_CD03))){
				executeCd04();
				Log.e("starlin","executeCd04");
			}
			else if (uuid.equals(UUID.fromString(CHARACTERISTIC_CD04))){
				BluetoothGattCharacteristic write = gattServiceOut.getCharacteristic(UUID.fromString(CHARACTERISTIC_WRITE));
	    		write.setValue(getHexBytes("AA5504B10000B5"));
	    		gatt.writeCharacteristic(write);
	    		Log.e("starlin","end");
			}
		}
		
		private void executeCd02(){
			BluetoothGattCharacteristic mCharacteristicCD02 = gattServiceOut.getCharacteristic(UUID.fromString(CHARACTERISTIC_CD02));
			setCharacteristicNotification(gattOut, mCharacteristicCD02, true);
		}
		
		private void executeCd03(){
			BluetoothGattCharacteristic mCharacteristicCD03 = gattServiceOut.getCharacteristic(UUID.fromString(CHARACTERISTIC_CD03));
			setCharacteristicNotification(gattOut, mCharacteristicCD03, true);
		}
		
		private void executeCd04(){
			BluetoothGattCharacteristic mCharacteristicCD04 = gattServiceOut.getCharacteristic(UUID.fromString(CHARACTERISTIC_CD04));
			setCharacteristicNotification(gattOut, mCharacteristicCD04, true);
		}
				
		private void setCharacteristicNotification(BluetoothGatt gatt,BluetoothGattCharacteristic characteristic, boolean enabled) {
			gatt.setCharacteristicNotification(characteristic, enabled);
			BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString(USER_APP_UUID));
			if (descriptor != null) {
				Log.e("starlin", "setCharacteristicNotification");
				descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
				gatt.writeDescriptor(descriptor);
			}
		}
		
		private byte[] getHexBytes(String message) {
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
	};
}
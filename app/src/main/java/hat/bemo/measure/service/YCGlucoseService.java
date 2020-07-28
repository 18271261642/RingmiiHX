package hat.bemo.measure.service;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.os.Build;
import android.os.Handler;
import androidx.annotation.RequiresApi;
import android.util.Log;

import java.util.UUID;

import hat.bemo.measure.set.Bluetooth4DeviceInterface;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class YCGlucoseService implements Bluetooth4DeviceInterface{
	public Handler mhanlder;
	public boolean mstatus = true;
	
	public YCGlucoseService(Handler mhanlder){
		 this.mhanlder = mhanlder;
	}
	
	@Override
	public BluetoothGattCallback getCallbackObject() {
		return mGattCallback;
	}
 
	final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
		@Override
		public void onConnectionStateChange(BluetoothGatt gatt, int status,int newState) {
			if (newState == BluetoothProfile.STATE_CONNECTED) {
				Log.i("starlin", "Attempting to start service discovery:"+ gatt.discoverServices());
			}
		}
		
		@Override
		public void onServicesDiscovered(BluetoothGatt gatt, int status) {
			if (status == BluetoothGatt.GATT_SUCCESS) {
				Log.e("starlin", "in service "+gatt.getServices().size());
				
	    		for(BluetoothGattService gattService : gatt.getServices()){
	    			if(gattService.getUuid().toString().contains("00001808-0000-1000-8000-00805f9b34fb")){
	    				BluetoothGattCharacteristic glucoseMeasurement = gattService.getCharacteristic(UUID.fromString("00002A18-0000-1000-8000-00805f9b34fb"));
	    				setCharacteristicNotification(gatt, glucoseMeasurement, true);
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
		public void onCharacteristicChanged(BluetoothGatt gatt,BluetoothGattCharacteristic characteristic) {
			if(characteristic.getValue() != null && characteristic.getUuid().equals(UUID.fromString("00002A18-0000-1000-8000-00805f9b34fb")) ){
				float glucoseConcentration = characteristic.getFloatValue(50, 10).floatValue();
				if(mstatus){
					mstatus = false;
					Log.e("starlin", "血糖: "+glucoseConcentration);
					BluetoothBaseActivity.result.ResultData(characteristic.getValue());
				}			
			}
		}
		
		@Override
		public void onDescriptorWrite(BluetoothGatt gatt,BluetoothGattDescriptor descriptor, int status) {
			Log.e("starlin","onDescriptorWriteonDescriptorWrite");		
			mhanlder.sendEmptyMessage(0);
			mstatus = true;
		}
						
		private void setCharacteristicNotification(BluetoothGatt gatt,BluetoothGattCharacteristic characteristic, boolean enabled) {
			gatt.setCharacteristicNotification(characteristic, enabled);
			BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"));
			if (descriptor != null) {
				Log.e("starlin", "setCharacteristicNotification");
				descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
				gatt.writeDescriptor(descriptor);
			}
		}
	};
}
package hat.bemo.BlueTooth.blegatt.service;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.util.UUID;

import hat.bemo.BlueTooth.blegatt.GattCallbackInterface;
import hat.bemo.BlueTooth.blegatt.baseService.BLEScanService;
import hat.bemo.BlueTooth.blegatt.baseService.BaseDevice;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class ChoicemmedPluseOximeterService extends BaseDevice implements GattCallbackInterface {
	public static final String SERVICE_UUID_PREFIX = "ba11f08c-5f14-0b0d-1080";
	public static final String CHARACTERISTIC_CD01 = "0000cd01-0000-1000-8000-00805f9b34fb";
	public static final String CHARACTERISTIC_CD02 = "0000cd02-0000-1000-8000-00805f9b34fb";
	public static final String CHARACTERISTIC_CD03 = "0000cd03-0000-1000-8000-00805f9b34fb";
	public static final String CHARACTERISTIC_CD04 = "0000cd04-0000-1000-8000-00805f9b34fb";
	public static final String CHARACTERISTIC_WRITE = "0000cd20-0000-1000-8000-00805f9b34fb";
	public static final String USER_APP_UUID = "00002902-0000-1000-8000-00805f9b34fb";	
	public static final String GET_DATA = "AA5504B10000B5";
	public Context mContext;
	
	public ChoicemmedPluseOximeterService(Context mContext) {
		this.mContext = mContext;
	}

	@Override
	public BluetoothGattCallback getBluetoothGattCallback() {
		return mGattCallback;
	}
	
    public final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {		
		/**
		 * 設備連接狀態發生更改時的監聽
         * 我們要在這裏進行discoverServices()
		 **/
		@Override
		public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
			switch (newState) {
			case BluetoothProfile.STATE_CONNECTED:
				System.out.println("STATE_CONNECTED");
				Log.i("mGattCallback", "Attempting to start service discovery:" + gatt.discoverServices());
				break;
			case BluetoothProfile.STATE_DISCONNECTED:
				System.out.println("STATE_DISCONNECTED");
				stopService();
				break;			 
			}			 
		}
		
		/**
		 * 遠端設備中的服務可用時的監聽
		 **/
		@Override
		public void onServicesDiscovered(BluetoothGatt gatt, int status) {
			System.out.println("onServicesDiscovered");
			switch (status) {
			case BluetoothGatt.GATT_SUCCESS://寫入成功
				Log.d("onServicesDiscovered", "write data success");
				Log.e("superJim", "in service "+gatt.getServices().size());					
				getCharacteristic(gatt);
				break;
			case BluetoothGatt.GATT_FAILURE://寫入失敗
				Log.d("onCharacteristicWrite", "write data failed");
				break;
			case BluetoothGatt.GATT_WRITE_NOT_PERMITTED://沒有寫入的權限
				Log.d("onCharacteristicWrite", "write not permitted");
				break;
			}			 
		}
		
		/**
		 * 寫入Characteristic成功與否的監聽
		 **/	
		@Override
		public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
			super.onCharacteristicWrite(gatt, characteristic, status);		
			System.out.println("onCharacteristicWrite");			
		}
		
		/**
		 * 某Characteristic的狀態為可讀時的回調	 
		 **/	
		@Override
		public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
			System.out.println("onCharacteristicRead");
		}
		
		/**
		 * 訂閱了遠端設備的Characteristic信息後，
		 * 當遠端設備的Characteristic信息發生改變後,回調此方法
		 **/
		@Override
		public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
			System.out.println("onCharacteristicChanged");
			if (characteristic.getValue() != null) {
				Log.e("superJim", characteristic.getUuid().toString());
			}
			if (characteristic.getValue() != null && characteristic.getUuid().equals(UUID.fromString(CHARACTERISTIC_CD04))) {
				Log.e("superJim", "血氧: " + characteristic.getValue()[3]);
				Log.e("superJim", "心率: " + characteristic.getValue()[4]); 
				close(gatt);
				stopService();		
				
//				MainActivity.ScreenOn(String.valueOf(characteristic.getValue()[3])+"\n"+String.valueOf(characteristic.getValue()[4]));
			}
		}

		@Override
		public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
			Log.e("superJim","onDescriptorWrite");			
			UUID uuid = descriptor.getCharacteristic().getUuid();			
			characteristic mCharacteristic = characteristic.ByStr(uuid.toString()); 
			switch (mCharacteristic) {
			case CHARACTERISTIC_CD01:
				setBluetoothGattCharacteristic(gatt, CHARACTERISTIC_CD02);
				break;
			case CHARACTERISTIC_CD02:
				setBluetoothGattCharacteristic(gatt, CHARACTERISTIC_CD03);
				break;
			case CHARACTERISTIC_CD03:
				setBluetoothGattCharacteristic(gatt, CHARACTERISTIC_CD04);
				break;
			case CHARACTERISTIC_CD04:
				write(gatt, GET_DATA);
				break;
			default:
				break;
			}			 
		}
	};

	@Override
	public void getCharacteristic(BluetoothGatt gatt) {
		for (BluetoothGattService gattService : gatt.getServices()) {
			System.out.println(gattService.getUuid().toString());
			if (gattService.getUuid().toString().contains(SERVICE_UUID_PREFIX)) {
				gattServiceOut = gattService;
				setBluetoothGattCharacteristic(gatt, CHARACTERISTIC_CD01);
				break;
			}
		}
	}

	@Override
	public void setBluetoothGattCharacteristic(BluetoothGatt mBluetoothGatt, String uuid) {
		BluetoothGattCharacteristic mCharacteristic = gattServiceOut.getCharacteristic(UUID.fromString(uuid));
		setCharacteristicNotification(mBluetoothGatt, mCharacteristic, true);
	}
	 
	@Override
	public void setCharacteristicNotification(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, boolean enabled) {
		gatt.setCharacteristicNotification(characteristic, enabled);//啟用或禁止使用通知/指定的特徵。
		BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString(USER_APP_UUID));
		if (descriptor != null) {
			descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
			gatt.writeDescriptor(descriptor);//設置特性
		}
	}

	@Override
	public void write(BluetoothGatt gatt, String command) {
		BluetoothGattCharacteristic write = gattServiceOut.getCharacteristic(UUID.fromString(CHARACTERISTIC_WRITE));
		write.setValue(getHexBytes(command));
		gatt.writeCharacteristic(write);
	}
	
	@Override
	public void close(BluetoothGatt gatt) {
		gatt.disconnect();
		gatt.close();
	}

	@Override
	public void stopService() {
		Intent service = new Intent(mContext, BLEScanService.class);
		mContext.stopService(service);
	}
	
	@Override
	public void startService() {
		Intent service = new Intent(mContext, BLEScanService.class);	       
		mContext.startService(service);
	}
	
	public enum characteristic {
		CHARACTERISTIC_CD01("0000cd01-0000-1000-8000-00805f9b34fb"),
		CHARACTERISTIC_CD02("0000cd02-0000-1000-8000-00805f9b34fb"),
		CHARACTERISTIC_CD03("0000cd03-0000-1000-8000-00805f9b34fb"),
		CHARACTERISTIC_CD04("0000cd04-0000-1000-8000-00805f9b34fb"),	 
		err("");

		private String mCharacteristic;

		characteristic(String mCharacteristic) {
			this.mCharacteristic = mCharacteristic;
		}

		public static characteristic ByStr(final String val) {
			for (characteristic mCharacteristic : characteristic.values()) {
				if (mCharacteristic.mCharacteristic.equals(val)) {
					return mCharacteristic;
				}
			}
			return err;
		}
	}
}
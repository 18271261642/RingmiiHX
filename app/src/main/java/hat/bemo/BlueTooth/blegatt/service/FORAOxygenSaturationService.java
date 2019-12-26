package hat.bemo.BlueTooth.blegatt.service;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.guider.healthring.siswatch.utils.WatchUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import hat.bemo.BlueTooth.blegatt.GattCallbackInterface;
import hat.bemo.BlueTooth.blegatt.api.HttpPostData;
import hat.bemo.BlueTooth.blegatt.api.Network;
import hat.bemo.BlueTooth.blegatt.baseService.BLEScanService;
import hat.bemo.BlueTooth.blegatt.baseService.BaseDevice;
import hat.bemo.BlueTooth.blegatt.util.FORAOxygenSaturationUtil;
import hat.bemo.MyApplication;
import hat.bemo.measure.database.Insert;
import hat.bemo.measure.database.VOMeasureRecord;
import hat.bemo.setting.SharedPreferences_status;
import hat.bemo.updata.UpdateGuiderData;


@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class FORAOxygenSaturationService extends BaseDevice implements GattCallbackInterface {
	public Context mContext;
	private FORAOxygenSaturationUtil mFORAOxygenSaturationUtil;
	
	public FORAOxygenSaturationService(Context mContext){
		this.mContext = mContext;
		initFORAOxygenSaturationUtil();
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
//				MainActivity.ScreenOn("連線");		
				Log.i("mGattCallback", "Attempting to start service discovery:" + gatt.discoverServices());
				break;
			case BluetoothProfile.STATE_DISCONNECTED:
				System.out.println("STATE_DISCONNECTED");
				close(gatt);
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
			Log.e("superJim", characteristic.getUuid().toString());	
			ArrayList<Byte> characteristicValues = new ArrayList<Byte>();
			for(int i = 0 ; i < characteristic.getValue().length ; i++){
				if (characteristic.getValue()[i] != 0){
					characteristicValues.add(characteristic.getValue()[i]);
					System.out.println(intParse(characteristic, i)); 
				}
			}
			
			switch (characteristic.getValue()[1]) {
			case 39:
				mFORAOxygenSaturationUtil.deviceSerialNumber1DateFormat(characteristicValues, characteristic, gatt);					
				break;
			case 40:
				mFORAOxygenSaturationUtil.deviceSerialNumber2DateFormat(characteristicValues, characteristic, gatt);				
				break;
			case 51:
				mFORAOxygenSaturationUtil.writeClockDateTime(characteristicValues, characteristic, gatt);				
				break;
			case 35:
				mFORAOxygenSaturationUtil.readClockTimeDateFormat(characteristicValues, characteristic, gatt);			
				break;
			case 38:
				mFORAOxygenSaturationUtil.measureDataFormat(characteristicValues, characteristic, gatt);
				break;		
			case 80:
				mFORAOxygenSaturationUtil.trunOffDevice(characteristicValues, characteristic, gatt);				
				break;
			default:
				break;
			}			
		}
 
		@Override
		public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
			Log.e("superJim","onDescriptorWrite");			
			UUID uuid = descriptor.getCharacteristic().getUuid();
			Log.e("superJim", uuid.toString());			
			characteristic mCharacteristic = characteristic.ByStr(uuid.toString()); 
			switch (mCharacteristic) {
			case CHARACTERISTIC_WRITE:	
				write(gatt, READ_DEVICE_SERIAL_NUMBER1);
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
				System.out.println("getCharacteristic");
				gattServiceOut = gattService;
				setBluetoothGattCharacteristic(gatt, CHARACTERISTIC_WRITE);
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
			System.out.println("setCharacteristicNotification");
			descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
			gatt.writeDescriptor(descriptor);//設置特性
		}	
	}

	private void initFORAOxygenSaturationUtil() {
		mFORAOxygenSaturationUtil = new FORAOxygenSaturationUtil();
		mFORAOxygenSaturationUtil.setOnFORADateFormatTool(new FORAOxygenSaturationUtil.OxygenSaturationTool() {
			
			@Override
			public void writeTrunOffDevice(BluetoothGatt gatt) {
				Log.e("writeTrunOffDevice", "TrunOffDevice");
				sendMessage();
				write(gatt, TRUN_OFF_DEVICE);						
			}
 
			@Override
			public void disconnect(BluetoothGatt gatt) {
				Log.e("disconnect", "disconnect");
				close(gatt);
				stopService();
			}
			
			@Override
			public void writeMeasureData(BluetoothGatt gatt) {
				write(gatt, READ_FINAL_DATA);				
			}
			
			@Override
			public void writeDeviceSerialNumber2(BluetoothGatt gatt) {
				write(gatt, READ_DEVICE_SERIAL_NUMBER2);	
			}
			
			@Override
			public void writeClockTimeDate(BluetoothGatt gatt) {
				String clockTime = mFORAOxygenSaturationUtil.getClockTime();
				String datetime = CalculateCheckSum(getHexBytes(SET_CLOCK_CMD + clockTime + END));
				String fallZero = "";
				int writeDateTime = 0;		
				
				if(!mFORAOxygenSaturationUtil.isNumeric(datetime)) {
					System.out.println("if");						 
					fallZero = mFORAOxygenSaturationUtil.fillZero(datetime);				
					if (datetime.length() > 1)
						datetime = datetime.substring(datetime.length() - 2, datetime.length());							
					write(gatt, SET_CLOCK_CMD + clockTime + END + fallZero + datetime);
				}
				else{				 		
					System.out.println("else");
					writeDateTime = Integer.valueOf(datetime);
					if (writeDateTime >= 100)
						writeDateTime -= 100;
					fallZero = String.valueOf(mFORAOxygenSaturationUtil.fillZero(writeDateTime));
					write(gatt, SET_CLOCK_CMD + clockTime + END + fallZero + String.valueOf(writeDateTime));					 
				}
			}

			@Override
			public void readClockTimeDate(BluetoothGatt gatt) {
				write(gatt, READ_DEVICE_CLOCK_TIME);	
			}
		});
	}
	
	private void sendMessage() {
		String serialNumber = mFORAOxygenSaturationUtil.getSerialNumber();
		String year = mFORAOxygenSaturationUtil.getYear();
		String month = mFORAOxygenSaturationUtil.getMonth();
		String day = mFORAOxygenSaturationUtil.getDay();
		String hour = mFORAOxygenSaturationUtil.getHour();
		String min = mFORAOxygenSaturationUtil.getMin();
		String sop2 = mFORAOxygenSaturationUtil.getSpo2();
		String bpm = mFORAOxygenSaturationUtil.getBpm();
		final String httpParams = mFORAOxygenSaturationUtil.getHttpParams(serialNumber, year, month, day, hour, min, sop2, bpm);
		Log.e("superJim", "序號:" + serialNumber);
		Log.e("superJim", "年:" + year);
		Log.e("superJim", "月:" + month);
		Log.e("superJim", "日:" + day);
		Log.e("superJim", "時:" + hour);
		Log.e("superJim", "分:" + min);
		Log.e("superJim", "血氧飽和度:" + sop2);
		Log.e("superJim", "脈搏:" + bpm);


		SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Calendar mCalendar ;
		mCalendar = Calendar.getInstance();
		String IMEI;
		IMEI = SharedPreferences_status.Get_IMEI(MyApplication.context);
		System.out.println("Mark IMEI = " + IMEI);

		Network.UploadNetwork(mContext,
				"血壓",
				IMEI,
				mSimpleDateFormat.format(mCalendar.getTime()),
				sop2);

		UpdateGuiderData.uploadOxygenData(sop2,IMEI,WatchUtils.getISO8601Timestamp(mCalendar.getTime()));
//		MainActivity.ScreenOn("序號:" + serialNumber + "\n" + 
//							  "年:" + year + "\n" + 
//							  "月:" + month + "\n" + 
//							  "日:" + day + "\n" + 
//							  "時:" + hour + "\n" + 
//							  "分:" + min + "\n" + 
//							  "血氧飽和度:" + sop2+ "\n" + 
//							  "脈搏:" + bpm);
							  
		String SEND_OK_MESSAGE = "send.ok.message";  
		Intent intent = new Intent(SEND_OK_MESSAGE);  
		intent.putExtra(PARAMETERS_KEY,   sop2 +" SpO2"+ "\n" + 
				  						  bpm + " %");
		Bundle mBundle = new Bundle();
	    mBundle.putInt("TYPES", 2);
	 	intent.putExtras(mBundle);
        mContext.sendBroadcast(intent); 
        insertData(sop2 + "/" + bpm);
        
		new Thread() {
			public void run() {
				try {
					new HttpPostData(httpParams);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();	
	}

	@SuppressLint({ "SimpleDateFormat" })
	public void insertData(String value) {
		Insert mInsert = new Insert();
		VOMeasureRecord mVOMeasureRecord = new VOMeasureRecord();
		mVOMeasureRecord.setValue(value);
		mVOMeasureRecord.setMeasureType("BO");
		mVOMeasureRecord.setCreateDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + "/" + new SimpleDateFormat("HH:mm").format(new Date()));
		mInsert.insertMeasureRecord(this.mContext, mVOMeasureRecord);
	}
	@Override
	public void write(BluetoothGatt gatt, String command) {
		Log.e("superJim", "write"); 
		Log.e("superJim", "command: "+command);
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
		CHARACTERISTIC_CD01("00001800-0000-1000-8000-00805f9b34fb"),
		CHARACTERISTIC_CD02("00001801-0000-1000-8000-00805f9b34fb"),
		CHARACTERISTIC_CD03("00001810-0000-1000-8000-00805f9b34fb"),
		CHARACTERISTIC_CD04("0000180f-0000-1000-8000-00805f9b34fb"),
		CHARACTERISTIC_CD05("0000180a-0000-1000-8000-00805f9b34fb"),
		CHARACTERISTIC_WRITE("00001524-1212-efde-1523-785feabcd123"),
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
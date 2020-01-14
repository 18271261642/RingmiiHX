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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import hat.bemo.BlueTooth.blegatt.GattCallbackInterface;
import hat.bemo.BlueTooth.blegatt.api.HttpPostData;
import hat.bemo.BlueTooth.blegatt.baseService.BLEScanService;
import hat.bemo.BlueTooth.blegatt.baseService.BaseDevice;
import hat.bemo.BlueTooth.blegatt.util.FORABodyWeightScaleUtil;
import hat.bemo.measure.database.Insert;
import hat.bemo.measure.database.VOMeasureRecord;


@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class FORABodyWeightScaleService extends BaseDevice implements GattCallbackInterface {
	public Context mContext;
	private FORABodyWeightScaleUtil mFORABodyWeightScaleUtil;
	private ArrayList<Byte> characteristicValues;	 

	public FORABodyWeightScaleService(Context mContext){
		this.mContext = mContext;
		initFORABloodGlucoseUtil();
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
			InitCharacteristicParameter();
			for(int i = 0 ; i < characteristic.getValue().length ; i++) { 
					characteristicValues.add(characteristic.getValue()[i]);					
					System.out.println(intParse(characteristic, i) +" p:"+ i); 			
				if (intParse(characteristic, i) == 165) {										
					Controller(gatt, characteristic);
					return;
				}
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

	private void initFORABloodGlucoseUtil() {
		mFORABodyWeightScaleUtil = new FORABodyWeightScaleUtil();
		mFORABodyWeightScaleUtil.setOnFORADateFormatTool(new FORABodyWeightScaleUtil.BodyWeightScaleTool() {
			
			@Override
			public void writeTrunOffDevice(BluetoothGatt gatt) {
				sendMessage();
				write(gatt, TRUN_OFF_DEVICE);						
			}

			@Override
			public void disconnect(BluetoothGatt gatt) {
				close(gatt);
				stopService();
			}
			
			@Override
			public void writeMeasureData(BluetoothGatt gatt) {
				write(gatt, READ_WEIGHT_FINAL_DATA);				
			}
			
			@Override
			public void writeDeviceSerialNumber2(BluetoothGatt gatt) {
				write(gatt, READ_DEVICE_SERIAL_NUMBER2);	
			}
			
			@Override
			public void writeClockTimeDate(BluetoothGatt gatt) {
				String clockTime = mFORABodyWeightScaleUtil.getClockTime();
				String datetime = CalculateCheckSum(getHexBytes(SET_CLOCK_CMD + clockTime + END));
				String fallZero = "";
				int writeDateTime = 0;		
				
				if(!mFORABodyWeightScaleUtil.isNumeric(datetime)) {
					System.out.println("if");						 
					fallZero = mFORABodyWeightScaleUtil.fillZero(datetime);				
					if (datetime.length() > 1)
						datetime = datetime.substring(datetime.length() - 2, datetime.length());							
					write(gatt, SET_CLOCK_CMD + clockTime + END + fallZero + datetime);
				}
				else{				 		
					System.out.println("else");
					writeDateTime = Integer.valueOf(datetime);
					if (writeDateTime >= 100)
						writeDateTime -= 100;
					fallZero = String.valueOf(mFORABodyWeightScaleUtil.fillZero(writeDateTime));
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
		String serialNumber = mFORABodyWeightScaleUtil.getSerialNumber();
		String year = mFORABodyWeightScaleUtil.getYear();
		String month = mFORABodyWeightScaleUtil.getMonth();
		String day = mFORABodyWeightScaleUtil.getDay();
		String hour = mFORABodyWeightScaleUtil.getHour();
		String min = mFORABodyWeightScaleUtil.getMin();
		String weight = mFORABodyWeightScaleUtil.getWeight();
		String bmi = mFORABodyWeightScaleUtil.getBmi();
//		String fat = mFORABodyWeightScaleUtil.getFat();
		
		final String httpParams = mFORABodyWeightScaleUtil.getHttpParams(serialNumber, year, month, day, hour, min, weight, bmi);
		
		Log.e("superJim", "序號:" + serialNumber);
		Log.e("superJim", "年:" + year);
		Log.e("superJim", "月:" + month);
		Log.e("superJim", "日:" + day);
		Log.e("superJim", "時:" + hour);
		Log.e("superJim", "分:" + min);
		Log.e("superJim", "體重:" + weight);
		Log.e("superJim", "BMI:" + bmi);
//		Log.e("superJim", "體脂:" + fat);
		
//		MainActivity.ScreenOn("序號:" + serialNumber + "\n" + 
//							  "年:" + year + "\n" + 
//							  "月:" + month + "\n" + 
//							  "日:" + day + "\n" + 
//							  "時:" + hour + "\n" + 
//							  "分:" + min + "\n" + 
//							  "體重:" + weight);
////							  "體脂:" + fat+ "\n" +
////							  "BMI:" + bmi);
							  
		String SEND_OK_MESSAGE = "send.ok.message";  
		Intent intent = new Intent(SEND_OK_MESSAGE);  
		intent.putExtra(PARAMETERS_KEY, weight + " KG");   
		Bundle mBundle = new Bundle();
	    mBundle.putInt("TYPES", 2);
	 	intent.putExtras(mBundle);
        mContext.sendBroadcast(intent); 
        insertData(weight);
        
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
		mVOMeasureRecord.setMeasureType("WT");
		mVOMeasureRecord.setCreateDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + "/" + new SimpleDateFormat("HH:mm").format(new Date()));
		mInsert.insertMeasureRecord(this.mContext, mVOMeasureRecord);
	}
	
	private void Controller(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {	
		for(int i = 0 ; i < characteristicValues.size() ; i++) { 
			switch (intParse(characteristicValues, i)) {
			case 39:
				mFORABodyWeightScaleUtil.deviceSerialNumber1DateFormat(characteristicValues, characteristic, gatt);					
				break;
			case 40:
				mFORABodyWeightScaleUtil.deviceSerialNumber2DateFormat(characteristicValues, characteristic, gatt);				
				break;
			case 51:
				mFORABodyWeightScaleUtil.writeClockDateTime(characteristicValues, characteristic, gatt);				
				break;
			case 35:
				mFORABodyWeightScaleUtil.readClockTimeDateFormat(characteristicValues, characteristic, gatt);			
				break;
			case 113:
				mFORABodyWeightScaleUtil.measureDataFormat(characteristicValues, characteristic, gatt);
				break;		
			case 80:
				mFORABodyWeightScaleUtil.trunOffDevice(characteristicValues, characteristic, gatt);				
				break;
			}	
		}		
		ClearCharacteristicParameter();
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

	@Override
	public int intParse(ArrayList<Byte> characteristicValues, int position) {		 
		return (characteristicValues.get(position) <= 0 ? byteToInt(characteristicValues.get(position)) : 
																	characteristicValues.get(position));	
	}
	
	private void InitCharacteristicParameter() {
		if (characteristicValues == null)
			characteristicValues = new ArrayList<Byte>();		
	}
	
	private void ClearCharacteristicParameter() {
		characteristicValues = null;
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
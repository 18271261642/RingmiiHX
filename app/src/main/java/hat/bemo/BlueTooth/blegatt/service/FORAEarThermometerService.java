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
import androidx.annotation.RequiresApi;
import android.util.Log;

import com.guider.healthring.siswatch.utils.WatchUtils;

import org.apache.http.NameValuePair;

import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import hat.bemo.BlueTooth.blegatt.GattCallbackInterface;
import hat.bemo.BlueTooth.blegatt.api.ChangeDateFormat;
import hat.bemo.BlueTooth.blegatt.api.Network;
import hat.bemo.BlueTooth.blegatt.baseService.BLEScanService;
import hat.bemo.BlueTooth.blegatt.baseService.BaseDevice;
import hat.bemo.BlueTooth.blegatt.util.FORAEarThermometerUtil;
import hat.bemo.MyApplication;
import hat.bemo.measure.database.Insert;
import hat.bemo.measure.database.VOMeasureRecord;
import hat.bemo.measure.set.MeasureFieldName;
import hat.bemo.measure.set.MeasureJsonFormat;
import hat.bemo.setting.SharedPreferences_status;
import hat.bemo.updata.UpdateGuiderData;


@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class FORAEarThermometerService extends BaseDevice implements GattCallbackInterface {
	public Context mContext;
	public static String HTTP_PARAMS = "";
	private FORAEarThermometerUtil mFORAEarThermometerUtil;
	String URL;	 
	String TYPE;
	int btnum;
	List<NameValuePair> ValuePair;
	
	public FORAEarThermometerService(Context mContext, int BTnum){
		this.mContext = mContext;
		btnum = BTnum;
		initFORABPUtil();
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
					System.out.println("Mark"+intParse(characteristic, i)); 
				}
			}
			
			switch (characteristic.getValue()[1]) {
			case 39:
				mFORAEarThermometerUtil.deviceSerialNumber1DateFormat(characteristicValues, characteristic, gatt);					
				break;
			case 40:
				mFORAEarThermometerUtil.deviceSerialNumber2DateFormat(characteristicValues, characteristic, gatt);				
				break;
			case 51:
				mFORAEarThermometerUtil.writeClockDateTime(characteristicValues, characteristic, gatt);				
				break;
			case 35:
				mFORAEarThermometerUtil.readClockTimeDateFormat(characteristicValues, characteristic, gatt);			
				break;
			case 38:
				mFORAEarThermometerUtil.measureDataFormat(characteristicValues, characteristic, gatt);
				break;		
			case 80:
				mFORAEarThermometerUtil.trunOffDevice(characteristicValues, characteristic, gatt);				
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
			System.out.println("Mark" +gattService.getUuid().toString());
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

	private void initFORABPUtil() {
		mFORAEarThermometerUtil = new FORAEarThermometerUtil();
		mFORAEarThermometerUtil.setOnFORADateFormatTool(new FORAEarThermometerUtil.EarThermometerTool() {
			
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
				write(gatt, READ_FINAL_DATA);				
				System.out.println("Mark 接資料?");
			}
			
			@Override
			public void writeDeviceSerialNumber2(BluetoothGatt gatt) {
				write(gatt, READ_DEVICE_SERIAL_NUMBER2);	
			}
			
			@Override
			public void writeClockTimeDate(BluetoothGatt gatt) {
				String clockTime = mFORAEarThermometerUtil.getClockTime();
				String datetime = CalculateCheckSum(getHexBytes(SET_CLOCK_CMD + clockTime + END));
				String fallZero = "";
				int writeDateTime = 0;		
				
				if(!mFORAEarThermometerUtil.isNumeric(datetime)) {
					System.out.println("if");						 
					fallZero = mFORAEarThermometerUtil.fillZero(datetime);				
					if (datetime.length() > 1)
						datetime = datetime.substring(datetime.length() - 2, datetime.length());							
					write(gatt, SET_CLOCK_CMD + clockTime + END + fallZero + datetime);
				}
				else{				 		
					System.out.println("else");
					writeDateTime = Integer.valueOf(datetime);
					if (writeDateTime >= 100)
						writeDateTime -= 100;
					fallZero = String.valueOf(mFORAEarThermometerUtil.fillZero(writeDateTime));
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
		String serialNumber = mFORAEarThermometerUtil.getSerialNumber();
		String year = mFORAEarThermometerUtil.getYear();
		String month = mFORAEarThermometerUtil.getMonth();
		String day = mFORAEarThermometerUtil.getDay();
		String hour = mFORAEarThermometerUtil.getHour();
		String min = mFORAEarThermometerUtil.getMin();
		final String temperature = mFORAEarThermometerUtil.getTemperature();
		HTTP_PARAMS = mFORAEarThermometerUtil.getHttpParams(serialNumber, year, month, day, hour, min, temperature);
		Log.e("superJim", "序號:" + serialNumber);
		Log.e("superJim", "年:" + year);
		Log.e("superJim", "月:" + month);
		Log.e("superJim", "日:" + day);
		Log.e("superJim", "時:" + hour);
		Log.e("superJim", "分:" + min);
		Log.e("superJim", "體溫:" + temperature);

//		MainActivity.ScreenOn("序號:" + serialNumber + "\n" + 
//							  "年:" + year + "\n" + 
//							  "月:" + month + "\n" + 
//							  "日:" + day + "\n" + 
//							  "時:" + hour + "\n" + 
//							  "分:" + min + "\n" + 
//							  "體溫:" + temperature);
		
		
		
//		String SEND_OK_MESSAGE = "send.ok.message";  
//		Intent intent = new Intent(SEND_OK_MESSAGE);  
//        intent.putExtra(PARAMETERS_KEY, temperature + " C");         
//        Bundle mBundle = new Bundle();
//	    mBundle.putInt("TYPES", 2);
//	 	intent.putExtras(mBundle);
//        mContext.sendBroadcast(intent); 
//        insertData(temperature);
//        
//		new Thread() {
//			public void run() {
//				try {
//					new HttpPostData(HTTP_PARAMS);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		}.start();	
		
		final String DataJosn;
		String IMEI;
		IMEI = SharedPreferences_status.Get_IMEI(MyApplication.context);
		System.out.println("Mark IMEI = " + IMEI);
//		IMEI = "864198028829494";
		MeasureJsonFormat json = new MeasureJsonFormat();
		double temp = Double.parseDouble(temperature);
		String uptemp = String.valueOf(temp*10);
		System.out.println("Mark temp = " + uptemp);

		SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Calendar mCalendar ;
		mCalendar = Calendar.getInstance();

		Network.UploadNetwork(mContext,
				"體溫",
				IMEI,
				mSimpleDateFormat.format(mCalendar.getTime()),
				temperature);


		UpdateGuiderData.uploadBodyTemp(temperature,WatchUtils.getISO8601Timestamp(mCalendar.getTime()),IMEI);

		DataJosn = json.jsonformat("0x39",  
				   IMEI, 
				   ChangeDateFormat.CreateDate(),
				   "177.5",  
				   "0", 
				   "0",
				   uptemp,
				   "33.1",
				   "24.1",
				   "2058",
				   "3",
				   "2.4",
				   "1928",
				   "12",
				   "30",
				   "37",
				   "65.1",
				   "42.1",
				   "650",
				   ChangeDateFormat.CreateDate(),
				   ChangeDateFormat.CreateDate()
				   );

		System.out.println("Mark 有嗎3?");
		String SEND_OK_MESSAGE = "send.ok.message";
		Intent intent = new Intent(SEND_OK_MESSAGE);
//		intent.putExtra(PARAMETERS_KEY, "Bloodglucose:" + bg); 
		intent.putExtra(PARAMETERS_KEY, temperature + "ºC");
		Bundle mBundle = new Bundle();
	    mBundle.putInt("TYPES", 2);
	 	intent.putExtras(mBundle);
        mContext.sendBroadcast(intent);
        insertData(temperature);

		 
        final String HTTP = "http://";
		//API/AppUploadBloodOxygenData
		//APP/GcareUploadBloodOxygenData.html
		final String SERVER_HOST = "/APP/GcareUploadWeightData.html";
		final String type_0x39 = "0x39";
		
		String DataHash;
		String Gkey = "AAAAAAAAZZZZZZZZZZZZ999999999";
		String GkeyId = "1";
		String TimeStamp;
		
		MeasureFieldName fn;
		fn = new MeasureFieldName(); 
		
		Long tsLong = System.currentTimeMillis()/1000;
		TimeStamp = tsLong.toString();
		
		URL = HTTP+"13.229.65.12:8080/angelcare"+SERVER_HOST;
		System.out.println("Mark URL = " + URL);
		TYPE = type_0x39;
		DataHash = encrypt(Gkey, TimeStamp, DataJosn);			 
		ValuePair =	fn.NameValuePair(DataHash, GkeyId, TimeStamp, DataJosn);
		
		System.out.println(URL);
        
		new Thread() {
			public void run() {
				try {
//					new MeasureHttpPostData(URL, TYPE, ValuePair, mContext);
					ValuePair = null;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();	
	}
	
	public static String encrypt(String account, String pwd, String timeStamp){
		//String timeStamp = getTimesgetTimes(); //加密還不需%20取代空格
    	String dataStructure = account + pwd + timeStamp;
    	Log.e("dataStructure", dataStructure);
    	MessageDigest shaCode = null;
    	try {
    		shaCode = MessageDigest.getInstance("SHA-256");
			shaCode.update(dataStructure.getBytes());
			//System.out.println("dataStructure="+dataStructure);
    	}
    	catch(Exception e) {
    		e.printStackTrace();
    		return "";
    	}
    	return byte2Hex(shaCode.digest());
	}
	
	private static String byte2Hex(byte[] data) {
		String hexString = "";
		String stmp = "";
		
		for(int i = 0; i < data.length; i++) {
			stmp = Integer.toHexString(data[i] & 0XFF);
			
			if(stmp.length() == 1) {
				hexString = hexString + "0" + stmp;
			}
			else {
				hexString = hexString + stmp;
			}
		}
		return hexString.toUpperCase();
	}
	
	@SuppressLint("SimpleDateFormat")
	public void insertData(String value) {
		Insert mInsert = new Insert();
		VOMeasureRecord mVOMeasureRecord = new VOMeasureRecord();
		mVOMeasureRecord.setValue(value);
		mVOMeasureRecord.setMeasureType("TEMP");
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
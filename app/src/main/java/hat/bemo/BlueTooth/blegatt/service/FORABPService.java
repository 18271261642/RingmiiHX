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
import java.util.Locale;
import java.util.UUID;

import hat.bemo.BlueTooth.blegatt.GattCallbackInterface;
import hat.bemo.BlueTooth.blegatt.api.ChangeDateFormat;
import hat.bemo.BlueTooth.blegatt.api.Network;
import hat.bemo.BlueTooth.blegatt.baseService.BLEScanService;
import hat.bemo.BlueTooth.blegatt.baseService.BaseDevice;
import hat.bemo.BlueTooth.blegatt.util.FORABPUtil;
import hat.bemo.MyApplication;
import hat.bemo.measure.database.Insert;
import hat.bemo.measure.database.VOMeasureRecord;
import hat.bemo.measure.set.MeasureFieldName;
import hat.bemo.measure.set.MeasureHttpPostData;
import hat.bemo.measure.set.MeasureJsonFormat;
import hat.bemo.setting.SharedPreferences_status;
import hat.bemo.updata.UpdateGuiderData;


@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class FORABPService extends BaseDevice implements GattCallbackInterface {
	public Context mContext;
	public String deviceName;
	private FORABPUtil mFORABPUtil;
	String URL;	 
	String TYPE;
	List<NameValuePair> ValuePair;
	
	public FORABPService(Context mContext, String deviceName){
		this.mContext = mContext;
		this.deviceName = deviceName;
		initFORABPUtil();
		System.out.print("Mark1");
	}

	@Override
	public BluetoothGattCallback getBluetoothGattCallback() {
		System.out.print("Mark2 call back");
		return mGattCallback;
	}

    public final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {		
		/**
		 * 設備連接狀態發生更改時的監聽
         * 我們要在這裏進行discoverServices()
		 **/
		@Override
		public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
			System.out.print("Mark3 state change");
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
			System.out.print("Mark4 discovered");
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
			System.out.print("Mark5 character write");
		}
		
		/**
		 * 某Characteristic的狀態為可讀時的回調	 
		 **/	
		@Override
		public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
			System.out.println("onCharacteristicRead");
			System.out.print("Mark6 character read");
		}
		
		/**
		 * 訂閱了遠端設備的Characteristic信息後，
		 * 當遠端設備的Characteristic信息發生改變後,回調此方法
		 **/
		@Override
		public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
			System.out.println("onCharacteristicChanged");
			System.out.print("Mark6 character change");
			Log.e("superJim", characteristic.getUuid().toString());	
			System.out.println("Mark 這邊 characteristic = " + characteristic.toString()); 
			ArrayList<Byte> characteristicValues = new ArrayList<Byte>();
			for(int i = 0 ; i < characteristic.getValue().length ; i++){
				if (characteristic.getValue()[i] != 0){
					characteristicValues.add(characteristic.getValue()[i]);
					System.out.println("Mark 這邊  = "+ i + " , " +intParse(characteristic, i)); 
				}
			}
			
			switch (characteristic.getValue()[1]) {
			case 39:
				mFORABPUtil.deviceSerialNumber1DateFormat(characteristicValues, characteristic, gatt);					
				break;
			case 40:
				mFORABPUtil.deviceSerialNumber2DateFormat(characteristicValues, characteristic, gatt);				
				break;
			case 51:
				mFORABPUtil.writeClockDateTime(characteristicValues, characteristic, gatt);				
				break;
			case 35:
				mFORABPUtil.readClockTimeDateFormat(characteristicValues, characteristic, gatt);			
				break;
			case 38:
				mFORABPUtil.measureDataFormat(characteristicValues, characteristic, gatt);
				break;		
			case 80:
				mFORABPUtil.trunOffDevice(characteristicValues, characteristic, gatt);				
				break;
			default:
				break;
			}			
		}
 
		@Override
		public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
			Log.e("superJim","onDescriptorWrite");			
			System.out.print("Mark7 descriptor write");
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
		System.out.print("Mark8 get character");
		for (BluetoothGattService gattService : gatt.getServices()) {
			System.out.println(gattService.getUuid().toString());
			if (gattService.getUuid().toString().contains(SERVICE_UUID_PREFIX)) {
				System.out.println("mark ppp getCharacteristic getType = " + gattService.getType() + 
						", getCharacteristics " + gattService.getCharacteristics() + ", get getCharacteristics tostring" +
						gattService.getCharacteristics().toString() + ", get UUID = " +
						gattService.getUuid() + ", getdevice = " + gatt.getDevice() +
						 ", get what = " + gatt.GATT_INSUFFICIENT_ENCRYPTION + 
						 ", get services = " + gatt.getServices().toString());
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
		System.out.print("Mark10 set BT character");
	}

	@Override
	public void setCharacteristicNotification(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, boolean enabled) {
		gatt.setCharacteristicNotification(characteristic, enabled);//啟用或禁止使用通知/指定的特徵。
		BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString(USER_APP_UUID));
		System.out.print("Mark9 set character noti");
		if (descriptor != null) {
			System.out.println("setCharacteristicNotification");
			descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
			gatt.writeDescriptor(descriptor);//設置特性
		}
	}
 
	private void initFORABPUtil() {
		mFORABPUtil = new FORABPUtil();
		mFORABPUtil.setOnFORADateFormatTool(new FORABPUtil.BPTool() {
			
					
			@Override
			public void writeClockTimeDate(BluetoothGatt gatt) {
				System.out.print("Mark11 initFORABPUtil");
				String clockTime = mFORABPUtil.getClockTime();
				String datetime = CalculateCheckSum(getHexBytes(SET_CLOCK_CMD + clockTime + END));
				System.out.println(datetime);
				int writeDateTime = 0;				 
				String fallZero = "";
				
				if(!mFORABPUtil.isNumeric(datetime)) {
					System.out.println("if");						 
					fallZero = mFORABPUtil.fillZero(datetime);				
					if (datetime.length() > 1)
						datetime = datetime.substring(datetime.length() - 2, datetime.length());							
					write(gatt, SET_CLOCK_CMD + clockTime + END + fallZero + datetime);
				}
				else{				 		
					System.out.println("else");
					writeDateTime = Integer.valueOf(datetime);
					if (writeDateTime >= 100)
						writeDateTime -= 100;
					fallZero = String.valueOf(mFORABPUtil.fillZero(writeDateTime));
					write(gatt, SET_CLOCK_CMD + clockTime + END + fallZero + String.valueOf(writeDateTime));					 
				}	
			}
			
			@Override
			public void writeTrunOffDevice(BluetoothGatt gatt) {
				System.out.print("Mark12 turn off");
				sendMessage();
				write(gatt, TRUN_OFF_DEVICE);						
			}

			@Override
			public void disconnect(BluetoothGatt gatt) {
				System.out.print("Mark13 disconnect");
				close(gatt);
				stopService();
			}
			
			@Override
			public void writeMeasureData(BluetoothGatt gatt) {
				System.out.print("Mark14 writemeasure");
				write(gatt, READ_FINAL_DATA);				
			}
			
			@Override
			public void writeDeviceSerialNumber2(BluetoothGatt gatt) {
				System.out.print("Mark15 write number2");
				write(gatt, READ_DEVICE_SERIAL_NUMBER2);	
			}
			
			@Override
			public void readClockTimeDate(BluetoothGatt gatt) {
				System.out.print("Mark16 readclocktime");
				write(gatt, READ_DEVICE_CLOCK_TIME);	
			}		
		});
	}
	
	
	
	private void sendMessage() {
		String serialNumber = mFORABPUtil.getSerialNumber();
		String year = mFORABPUtil.getYear();
		String month = mFORABPUtil.getMonth();
		String day = mFORABPUtil.getDay();
		String hour = mFORABPUtil.getHour();
		String min = mFORABPUtil.getMin();
		String dbp = mFORABPUtil.getDbp();
		String sbp = mFORABPUtil.getSbp();
		String heart = mFORABPUtil.getHeart();
		//final String httpParams = mFORABPUtil.getHttpParams(deviceName, serialNumber, year, month, day, hour, min, dbp, sbp, heart);
		final String httpParams = mFORABPUtil.getHttpParams(deviceName, serialNumber, year, month, day, hour, min, dbp, sbp, heart);
		
		final String DataJosn;
		String IMEI;
		IMEI = SharedPreferences_status.Get_IMEI(MyApplication.context);
		System.out.println("Mark IMEI = " + IMEI);
//		IMEI = "864198028829494";
		MeasureJsonFormat json = new MeasureJsonFormat();
		
		System.out.println("Mark BP Time = " + ChangeDateFormat.CreateDate());
		
		DataJosn = json.jsonformat("0x36",  
				   IMEI, 
				   "N",        
				   "N", 
				   "1",  
				   ChangeDateFormat.CreateDate(), 
				   ChangeDateFormat.CreateDate(), 
				   "1",
				   "1", 
				   dbp, 
				   sbp,
				   "1", 
				   heart, 
				   "Y", 
				   "1", 
				   "1",
				   year);

		SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss",Locale.CHINA);
		Calendar mCalendar ;
		mCalendar = Calendar.getInstance();


		Network.UploadNetwork(mContext,
				"血壓",
				IMEI,
				mSimpleDateFormat.format(mCalendar.getTime()),
				sbp,
				dbp,
				heart);

		//血压
		UpdateGuiderData.uploadBloodData(sbp,dbp,WatchUtils.getISO8601Timestamp(mCalendar.getTime()),IMEI);

		
		Log.e("superJim", "序號:" + serialNumber);
		Log.e("superJim", "年:" + year);
		Log.e("superJim", "月:" + month);
		Log.e("superJim", "日:" + day);
		Log.e("superJim", "時:" + hour);
		Log.e("superJim", "分:" + min);
		Log.e("superJim", "收縮壓:" + dbp);
		Log.e("superJim", "舒張壓:" + sbp);
		Log.e("superJim", "脈搏:" + heart);
//		"\n"+String.valueOf(sbp)+"\n"+String.valueOf(heart)
//		MainActivity.ScreenOn("序號:" + serialNumber + "\n" + 
//							  "年:" + year + "\n" + 
//							  "月:" + month + "\n" +  
//							  "日:" + day + "\n" + 
//							  "時:" + hour + "\n" + 
//							  "分:" + min + "\n" + 
//							  "收縮壓:" + dbp + "\n" + 
//							  "舒張壓:" + sbp + "\n" + 
//							  "脈搏:" + heart);	
		
//		String SEND_OK_MESSAGE = "send.ok.message";  
//		Intent intent = new Intent(SEND_OK_MESSAGE);  
//      intent.putExtra(PARAMETERS_KEY, "Systolic:" + dbp + "\n" + 
//									    "Diastolic:" + sbp + "\n" + 
//									    "Pulse:" + heart);  
		
		StringBuffer mStringBuffer = mFORABPUtil.getStringBuffer();
		String SEND_OK_MESSAGE = "send.ok.message";  
		Intent intent = new Intent(SEND_OK_MESSAGE);  
		mStringBuffer.append(dbp).append("/").append(sbp).append("/").append(heart);
        intent.putExtra(PARAMETERS_KEY, mStringBuffer.toString()); 
        Bundle mBundle = new Bundle();
        mBundle.putInt("TYPES", 2);
 		intent.putExtras(mBundle);
        mContext.sendBroadcast(intent); 
        insertData(dbp + "/" + sbp + "/" + heart);
        
       
		final String HTTP = "http://";
		//API/AppUploadBloodOxygenData
		//APP/GcareUploadBloodOxygenData.html
		final String SERVER_HOST = "/APP/GcareUploadBloodPressureData.html";
		final String type_0x36 = "0x36";
		
		String DataHash;
		String Gkey = "AAAAAAAAZZZZZZZZZZZZ999999999";
		String GkeyId = "1";
		String TimeStamp;
		
		MeasureFieldName fn;
		fn = new MeasureFieldName(); 
		
		Long tsLong = System.currentTimeMillis()/1000;
		TimeStamp = tsLong.toString();
		System.out.print("Mark Timestamp = " + TimeStamp);
		
		URL = HTTP+"13.229.65.12:8080/angelcare"+SERVER_HOST;
		System.out.println("Mark URL = " + URL);
		TYPE = type_0x36;
		DataHash = encrypt(Gkey, TimeStamp, DataJosn);			 
		ValuePair =	fn.NameValuePair(DataHash, GkeyId, TimeStamp, DataJosn);
		
		System.out.println("length:"+httpParams.length());
		System.out.println(URL);
		System.out.println("Mark 上傳" + httpParams);
		
        
		new Thread() {
			public void run() {
				try {
					Log.e("URL", URL);
					new MeasureHttpPostData(URL, TYPE, ValuePair, mContext);
					ValuePair = null;
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();	
		//Intent service = new Intent(mContext, BLEScanService.class);	
		
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

	@SuppressLint({ "SimpleDateFormat" })
	public void insertData(String value) {
		Insert mInsert = new Insert();
		VOMeasureRecord mVOMeasureRecord = new VOMeasureRecord();
		mVOMeasureRecord.setValue(value);
		mVOMeasureRecord.setMeasureType("BP");
		mVOMeasureRecord.setCreateDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + "/" + new SimpleDateFormat("HH:mm").format(new Date()));
		mInsert.insertMeasureRecord(this.mContext, mVOMeasureRecord);
	}
	
	@Override
	public void write(BluetoothGatt gatt, String command) {
		System.out.print("Mark17 write");
		Log.e("superJim", "write"); 
		Log.e("superJim", "command: "+command);
		BluetoothGattCharacteristic write = gattServiceOut.getCharacteristic(UUID.fromString(CHARACTERISTIC_WRITE));
		write.setValue(getHexBytes(command));
		gatt.writeCharacteristic(write);
		
		System.out.print("Mark YOYO command = " + command + ", write = " + write);
		
	}
 
	@Override
	public void close(BluetoothGatt gatt) {
		System.out.print("Mark18 close");
		gatt.disconnect();
		gatt.close();
	}

	@Override
	public void stopService() {
		System.out.print("Mark19 stop");
		Intent service = new Intent(mContext, BLEScanService.class);
		mContext.stopService(service);
	}

	@Override 
	public void startService() {
		System.out.print("Mark20 start");
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
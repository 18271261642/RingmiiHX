package hat.bemo.BlueTooth.blegatt.baseService;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import hat.bemo.BlueTooth.blegatt.AlarmManagerTool;
import hat.bemo.BlueTooth.blegatt.GattCallbackInterface;
import hat.bemo.BlueTooth.blegatt.service.ChoicemmedPluseOximeterService;
import hat.bemo.BlueTooth.blegatt.service.FORABPService;
import hat.bemo.BlueTooth.blegatt.service.FORABloodGlucoseService;
import hat.bemo.BlueTooth.blegatt.service.FORABodyWeightScaleService;
import hat.bemo.BlueTooth.blegatt.service.FORAEarThermometerService;
import hat.bemo.BlueTooth.blegatt.service.FORAOxygenSaturationService;
import hat.bemo.MyApplication;
import hat.bemo.measure.database.Insert;
import hat.bemo.measure.database.MeasureBLEDAO;
import hat.bemo.measure.database.VOMeasureDevice;

public class BLEScanService extends Service {
  public static final String TAG = "BLEScanService";
  public BluetoothGatt mBluetoothGatt;
  public BluetoothGattService gattServiceOut;
  public BluetoothAdapter mBluetoothAdapter;
  public BluetoothManager mBluetoothManager;
  private BroadcastReceiver mReceiver;
  private static BLEScanService mBLEScanService;
  private String[] mName;
  private String[] mAddress;
  private String mDeviceName;
  private ArrayList<VOMeasureDevice> getmeasure;
  
  @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
  public void onCreate() {
	  if(getAndroidSDKVersion() > 18){
		  Log.e("BLEScanService", "onCreate!!!");
		  init();
	  }
  }
  
  public int onStartCommand(Intent intent, int flags, int startId) {
	  if(getAndroidSDKVersion() > 18){
	    stop();
	    start();
	    flags = 1;
	  }
    return super.onStartCommand(intent, flags, startId);
  }
  
  public IBinder onBind(Intent intent) {
    return null;
  }
  
  @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
  private void init() {
    Log.e("BLEScanService", initialize()+"");
    ServiceContext();
    start();
    startTimer();
    mBroadcastReceiver();
    mUnregisterReceiver();
  }
  
  @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
  public boolean initialize() {
    if (this.mBluetoothManager == null) {
      this.mBluetoothManager = ((BluetoothManager)getSystemService("bluetooth"));
    }
    if (this.mBluetoothManager == null) {
      return false;
    }
    this.mBluetoothAdapter = this.mBluetoothManager.getAdapter();
    if (this.mBluetoothAdapter == null) {
      return false;
    }
    return true;
  }
  
  private void startTimer() {
    AlarmManagerTool tool = new AlarmManagerTool(getApplicationContext());
    tool.start();
  }
  
  private void mBroadcastReceiver() {
    this.mReceiver = new BroadcastReceiver() {
      @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
      public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(action == null)
          return;
        if ("android.bluetooth.device.action.FOUND".equals(action)) {
          BluetoothDevice device = (BluetoothDevice)intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
          BLEScanService.this.deviceFound(device);
        }
        else {
          "android.bluetooth.adapter.action.DISCOVERY_FINISHED".equals(action);
        }
      }
    };
  }
  
  private void mUnregisterReceiver() {
    IntentFilter filter = new IntentFilter();
    filter.addAction("android.bluetooth.device.action.FOUND");
    filter.addAction("android.bluetooth.adapter.action.DISCOVERY_FINISHED");
    registerReceiver(this.mReceiver, filter);
    filter.addAction("hat.bemo.BlueTooth.blegatt.device");
    this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    this.mBluetoothAdapter.startDiscovery();
  }
  
  @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
  private void deviceFound(BluetoothDevice device){
    if ( (device != null) && (device.getBondState() != 12)) {
    	System.out.println("Mark D40 = 12");
      scanResault(device);
    }
  }
  
  @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
  @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
  private void scanResault(BluetoothDevice device) {

//    System.out.println("Mark BT nname = " + device.getName() + ", address = " + device.getAddress());

    if (device.getName() == null){
      return;
    }

    System.out.println("Mark getmeasure = " + this.getmeasure);
    System.out.println("Mark BT yyy = " + device.getName());

    if(device.getName().contains("FORA")) {

      //Mark 加上判斷
      if (this.getmeasure == null) {
        System.out.println("Mark Add BT");

        Insert mInsert = new Insert();
        VOMeasureDevice mVOMeasureDevice = new VOMeasureDevice();
        mVOMeasureDevice.setDevice(device.getName() + "/" + device.getAddress());
        mVOMeasureDevice.setCreateDate(new SimpleDateFormat("yyyy-MM-dd HH:mm",Locale.CHINA).format(new Date()));
        mInsert.insertMeasureDevice(MyApplication.context, mVOMeasureDevice);
////      infoList.remove(position);
//
////        Intent mIntent = new Intent(MyApplication.context, BLEScanService.class);
////        MyApplication.context.startService(mIntent);
//      }
      }

    }
    getPairDevice(device.getName() + "/" + device.getAddress());
//    System.out.println("Mark D40 = " + getmeasure);
    if (this.getmeasure == null) {
      return;
    }
    deviceName name = deviceName.ByStr(this.mName[0]);
    String deviceN = name.toString();

    //发送广播
    Intent intent = new Intent();
    intent.setAction("hat.bemo.BlueTooth.blegatt.device");
    intent.putExtra("guiderName",device.getName());
    intent.putExtra("guiderMac",device.getAddress());
    sendBroadcast(intent);
    
    /*
    switch (name) {
    case FORAP60: 
      startDeviceService(device, new ChoicemmedPluseOximeterService(this));
      break;
    case FORA_GD40: 
      startDeviceService(device, new FORABPService(this, "3128"));
      break;
    case FORA_IR20:   
      startDeviceService(device, new FORAEarThermometerService(this));
      break;
    case FORA_P30_PLUS: 
      startDeviceService(device, new FORABPService(this, "3129"));
      break;
    case ICHOICE: 
      startDeviceService(device, new FORAOxygenSaturationService(this));
      break;
    case TAIDOC_TD2551: 
      startDeviceService(device, new FORABloodGlucoseService(this));
      break;
    case TNG_SPO2: 
      startDeviceService(device, new FORABodyWeightScaleService(this));
      break;
    }
    */
    
//    if(SharedPreferences_status.GetBT(this).equals("1")){
//    	if (deviceN.indexOf("FORA_IR") > -1){
//        	startDeviceService(device, new FORAEarThermometerService(this, 1));
//        }
//    	 else if (deviceN.indexOf("TAIDOC_TD12") > -1){
//    	    	startDeviceService(device, new FORAEarThermometerService(this, 1));
//    	   }
//    }
//    else{
    
    if(deviceN.indexOf("FORAP60") > -1){
    	startDeviceService(device,(GattCallbackInterface) new ChoicemmedPluseOximeterService(this));
    	System.out.println("Mark 量測1");
    }
    else if (deviceN.indexOf("FORA_GD") > -1){
    	System.out.println("Mark 血糖咧?");
    	startDeviceService(device,(GattCallbackInterface) new FORABloodGlucoseService(this));
    }
    else if (deviceN.indexOf("FORA_IR") > -1){
    	startDeviceService(device,(GattCallbackInterface) new FORAEarThermometerService(this, 0));
    }
    else if (deviceN.indexOf("TAIDOC_TD12") > -1){
    	startDeviceService(device,(GattCallbackInterface) new FORAEarThermometerService(this, 0));
   }
    else if (deviceN.indexOf("FORA_P30_PLUS") > -1){
    	startDeviceService(device,(GattCallbackInterface) new FORABPService(this, "3129"));
    }
    else if (deviceN.indexOf("FORAP100") > -1){
    	startDeviceService(device,(GattCallbackInterface) new FORABPService(this, "3129"));
    }
    else if (deviceN.indexOf("TAIDOC_TD31") > -1){
    	startDeviceService(device, (GattCallbackInterface) new FORABPService(this, "3129"));
    }
    else if (deviceN.indexOf("ICHOICE") > -1){
    	startDeviceService(device, (GattCallbackInterface) new FORAOxygenSaturationService(this));
    }
    else if (deviceN.indexOf("TAIDOC_TD82") > -1){
    	startDeviceService(device, (GattCallbackInterface) new FORAOxygenSaturationService(this));
    }
    else if (deviceN.indexOf("TAIDOC_TD25") > -1){
    	startDeviceService(device, (GattCallbackInterface) new FORABodyWeightScaleService(this));
    }
    else if (deviceN.indexOf("TAIDOC_TD42") > -1){
    	startDeviceService(device, (GattCallbackInterface) new FORABloodGlucoseService(this));
    }
    else if (deviceN.indexOf("TAIDOC_TD32") > -1){
    	startDeviceService(device,(GattCallbackInterface) new FORABPService(this, "3129"));
    	startDeviceService(device,(GattCallbackInterface) new FORABloodGlucoseService(this));
    }
    else if (deviceN.indexOf("DIAMOND_MO") > -1){
    	startDeviceService(device,(GattCallbackInterface) new FORABloodGlucoseService(this));
    }
    else if (deviceN.indexOf("TNG_SPO2") > -1){
    	startDeviceService(device,(GattCallbackInterface) new FORABodyWeightScaleService(this));
    }
    else if (deviceN.indexOf("FORA_D") > -1){
    	startDeviceService(device,(GattCallbackInterface) new FORABPService(this, "3129"));
    	//startDeviceService(device, new FORABloodGlucoseService(this));
    }
    else if (deviceN.indexOf("FORA_6_CON") > -1){
    	startDeviceService(device,(GattCallbackInterface) new FORABloodGlucoseService(this));
    }
    else if (deviceN.indexOf("mtest") > -1){
    	System.out.println("Mark 這邊會先有嗎~?");
    }
//    }
    
  }
  
  @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
  private void startDeviceService(BluetoothDevice device, GattCallbackInterface mGattCallbackInterface) {
    stop();
    Log.e("connectGatt", connect(device.getAddress(), mGattCallbackInterface)+"");
  }
  
  private void start() {
    this.mBluetoothAdapter.startDiscovery();
  }
  
  private void stop() {
    this.mBluetoothAdapter.cancelDiscovery();
  }
  
  @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
  public boolean connect(String address, GattCallbackInterface mService) {
    BluetoothDevice device = this.mBluetoothAdapter.getRemoteDevice(address);
    if (device == null) {
      return false;
    }
    device.connectGatt(this, false, mService.getBluetoothGattCallback());
    return true;
  }
  
  public void ServiceContext() {
    mBLEScanService = this;
  }
  
  public static String getImei() {
    TelephonyManager telephonyManager = (TelephonyManager)mBLEScanService.getSystemService("phone");
    @SuppressLint("MissingPermission") String imei = telephonyManager.getDeviceId();
    Log.e("imei", imei);
    return imei;
  }
  
  private void getPairDevice(String deviceName) {
    Log.e("BLEScanService", "Data:" + deviceName);


      //发送广播
      Intent intent = new Intent();
      intent.setAction("hat.bemo.BlueTooth.blegatt.device");
      intent.putExtra("guiderName",deviceName);
      //intent.putExtra("guiderMac",device.getAddress());
      sendBroadcast(intent);






    MeasureBLEDAO dao = new MeasureBLEDAO();
    this.getmeasure = dao.getMeasureDevice(this, deviceName);
    System.out.println("Mark D40 yo = " + this.getmeasure);
    if (this.getmeasure != null) {
      for (int i = 0; i < this.getmeasure.size(); i++)
      {
        VOMeasureDevice data = (VOMeasureDevice)this.getmeasure.get(i);
        Log.e("Table_name", "Table_name=measuredevice");
        System.out.println("getItemno=" + data.getItemno());
        System.out.println("getDevice=" + data.getDevice());
        System.out.println("getCreateDate=" + data.getCreateDate());
        System.out.println("---------------------------");
        this.mDeviceName = data.getDevice();
        this.mName = this.mDeviceName.split("/");
        this.mAddress = this.mDeviceName.split("/");
      }
    }
  }
  
  public static enum deviceName {
	  //Fora 6 connect
    ICHOICE("ichoice"),  
    FORAP60("FORA P60"),  
    FORAP100("FORA P100"),
    FORA_P30_PLUS("FORA P30 PLUS"),  
    FORA_IR20("FORA IR20"),  
    FORA_IR21("FORA IR21"),
    FORA_IR40("FORA IR40"),
    TNG_SPO2("TNG SPO2"),  
    FORA_GD40("FORA GD40"),  
    FORA_D40("FORA D40"),
    FORA_6_CONNECT("FORA 6 CONNECT"),
    TAIDOC_TD2551("TAIDOC TD2551"),  
    TAIDOC_TD2555("TAIDOC TD2555"),
    TAIDOC_TD4257("TAIDOC TD4257"),  
    TAIDOC_TD4277("TAIDOC TD4277"),
    TAIDOC_TD4279("TAIDOC TD4279"),
    TAIDOC_TD1241("TAIDOC TD1241"),
    TAIDOC_TD1261("TAIDOC TD1261"),
    TAIDOC_TD3128("TAIDOC TD3128"),
    TAIDOC_TD3140("TAIDOC TD3140"),
    TAIDOC_TD3130("TAIDOC TD3130"),
    TAIDOC_TD3223("TAIDOC TD3223"),
    TAIDOC_TD3261("TAIDOC TD3261"),
    TAIDOC_TD8255("TAIDOC TD8255"),
    TAIDOC_TD8201("TAIDOC TD8201"),
    DIAMOND_MOBILE("DIAMOND MOBILE"),
    err("");
    
    private String name;
    
    private deviceName(String name) {
      this.name = name;
    }
    
    public static deviceName ByStr(String val) {
      deviceName[] arrayOfdeviceName;
      int j = (arrayOfdeviceName = values()).length;
      for (int i = 0; i < j; i++) {
        deviceName name = arrayOfdeviceName[i];
        if (name.name.equals(val)) {
          return name;
        }
      }
      return err;
    }
  }
  
	@SuppressWarnings("deprecation")
	public static int getAndroidSDKVersion() {
		int version = 0;
		try {
			version = Integer.valueOf(Build.VERSION.SDK);
		} catch (NumberFormatException e) {
			Log.e("", e.toString());
		}
		return version;
	}
  
  public void onDestroy() {
    super.onDestroy();
    unregisterReceiver(this.mReceiver);
    Log.e("BLEScanService", "onDestroy!!!");
  }
}
package hat.bemo.measure.service;
/**
 * 描述：此類為藍牙調試助手主界面
 *  
 * 作用：接收發送藍牙消息、建立藍牙通信連接、打印藍牙消息
 */

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.RequiresApi;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.guider.healthring.R;

import java.lang.reflect.Method;
import java.util.ArrayList;


import hat.bemo.measure.set.Bluetooth4DeviceInterface;
import hat.bemo.measure.set.Command;
import hat.bemo.measure.setting_db.HRDataInfoVO;
import hat.bemo.measure.setting_db.MeasureDAO;
import hat.bemo.measure.setting_db.TABLE_BG;
import hat.bemo.measure.setting_db.TABLE_BP;
import hat.bemo.measure.setting_db.VO_BG;
import hat.bemo.measure.setting_db.VO_BO;
import hat.bemo.measure.setting_db.VO_BP;


public class BluetoothChatActivity extends BluetoothBaseActivity {
    // 調試
    private static final String TAG = "BluetoothChat";
    private static final boolean D = true;
    
    private static BluetoothChatActivity bluetoothChatActivity;

    //動畫跑圖
    private ImageView btimage;
    private ImageView imageanimation;
    private int bt_type;
    private String language;	 	 
    
    private Handler measuringCountdownTimer;
    private TextView timer;
    private int countBackwards = 60;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(D) Log.e(TAG, "+++ ON CREATE +++");
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.gd800_measure_bluetoothchatactivity);

        bluetoothChatActivity = this;
//        language = MyApplication.getLanguageEnv();
        // 獲取本地藍牙適配器
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();       		
//      LoData_HR();
        // 判斷藍牙是否可用
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "藍牙是不可用的", Toast.LENGTH_LONG).show();
            finish();
            return;
        }        
        setImagetype();          
    } 

    private void setImagetype(){
    	 timer = (TextView)findViewById(R.id.timer);
    	 btimage = (ImageView)findViewById(R.id.btimage);
    	 imageanimation = (ImageView)findViewById(R.id.imageanimation);
    	 
    	 btimage.setEnabled(true);
    	 Bundle bundle0311 =this.getIntent().getExtras();
         bt_type = bundle0311.getInt("BT_TYPE");

//         if(bt_type == R.id.btn_item01){
//        	Log.e("量測模式", "血壓模式");
//        	LoData_BP();
//        	BTtype(R.id.btn_item01);
//
//         	if(language.equals("en")){
//
//	   	 	}
//	   	 	else if(language.equals("zh-CN")){
//	   	 		imageanimation.setImageResource(R.drawable.a_animation);
//	   	 	}
//	   	 	else if(language.equals("zh-TW")){
//	   	 		imageanimation.setImageResource(R.drawable.a_animation_tv);
//	   	 	}
//     	 }
//     	 else if(bt_type == R.id.btn_item02){
//     		Log.e("量測模式", "血氧模式");
//     		LoData_BO();
//     		BTtype(R.id.btn_item02);
//     		if(language.equals("en")){
//
//	   	 	}
//	   	 	else if(language.equals("zh-CN")){
//	   	 		imageanimation.setImageResource(R.drawable.c_animation);
//	   	 	}
//	   	 	else if(language.equals("zh-TW")){
//	   	 		imageanimation.setImageResource(R.drawable.c_animation_tv);
//	   	 	}
//     	 }
     	
//     	 if(bt_type == R.id.btn_item03){
//     		System.out.println("Mark 藍芽這邊66?");
//     		Log.e("量測模式", "血糖模式");
//     		if(BluetoothBaseActivity.result.bg_map != null) BluetoothBaseActivity.result.bg_map.clear();
//     		LoData_BG();
//     		BTtype(R.id.btn_item01);
//
//     		if(language.equals("en")){
//
//	   	 	}
//	   	 	else if(language.equals("zh-CN")){
//	   	 		imageanimation.setImageResource(R.drawable.b_animation);
//	   	 	}
//	   	 	else if(language.equals("zh-TW")){
//	   	 		imageanimation.setImageResource(R.drawable.b_animation_tv);
//	   	 	}
//     	 }
     	 
//     	 else if(bt_type == R.id.btn_item04){
//     		BTtype(R.id.btn_item04);
//
//     		if(language.equals("en")){
//
//	   	 	}
//	   	 	else if(language.equals("zh-CN")){
//	   	 		imageanimation.setImageResource(R.drawable.d_animation);
//	   	 	}
//	   	 	else if(language.equals("zh-TW")){
//	   	 		imageanimation.setImageResource(R.drawable.d_animation_tv);
//	   	 	}
//     	 }
         
//         btimage.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) { 
//				// TODO Auto-generated method stub
//				try{	
////					btimage.setEnabled(false);
//						
//			 		sendMsg("MEASURE_RESULT_CMD");			 		 
//				}catch(Exception e){
//					e.printStackTrace();
//					Toast.makeText(BluetoothChatActivity.this, "裝置無配對", Toast.LENGTH_LONG).show();	
//					BluetoothChatActivity.this.finish();
//				}		 		 					             				              
//			}
//		});       
    } 
    
    private void AnimType(){
//    	 if(bt_type == R.id.btn_item01){
//    		 if(language.equals("en")){
//    			 imageanimation.setImageResource(R.drawable.anim_bluetooth_a_en);
// 	   	 	 }
// 	   	 	 else if(language.equals("zh-CN")){
// 	   	 		 imageanimation.setImageResource(R.drawable.anim_bluetooth_a);
// 	   	 	 }
// 	   	 	 else if(language.equals("zh-TW")){
// 	   	 		 imageanimation.setImageResource(R.drawable.anim_bluetooth_a_tv);
// 	   	 	 }
//      	 }
//      	 else if(bt_type == R.id.btn_item02){
//      		 if(language.equals("en")){
//      			imageanimation.setImageResource(R.drawable.anim_bluetooth_c_en);
//	   	 	 }
//	   	 	 else if(language.equals("zh-CN")){
//	   	 		imageanimation.setImageResource(R.drawable.anim_bluetooth_c);
//	   	 	 }
//	   	 	 else if(language.equals("zh-TW")){
//	   	 		imageanimation.setImageResource(R.drawable.anim_bluetooth_c_tv);
//	   	 	 }
//      	 }
//      	 else if(bt_type == R.id.btn_item03){
//      		 if(language.equals("en")){
//      			imageanimation.setImageResource(R.drawable.anim_bluetooth_b_en);
//	   	 	 }
//	   	 	 else if(language.equals("zh-CN")){
//	   	 		imageanimation.setImageResource(R.drawable.anim_bluetooth_b);
//	   	 	 }
//	   	 	 else if(language.equals("zh-TW")){
//	   	 		imageanimation.setImageResource(R.drawable.anim_bluetooth_b_tv);
//	   	 	 }
//      	 }
//      	 else if(bt_type == R.id.btn_item04){
//      		 if(language.equals("en")){
//      			imageanimation.setImageResource(R.drawable.anim_bluetooth_d_en);
//	   	 	 }
//	   	 	 else if(language.equals("zh-CN")){
//	   	 		imageanimation.setImageResource(R.drawable.anim_bluetooth_d);
//	   	 	 }
//	   	 	 else if(language.equals("zh-TW")){
//	   	 		imageanimation.setImageResource(R.drawable.anim_bluetooth_d_tv);
//	   	 	 }
//      	 }
    }
    
    private String[] name;
    private String[] address;
    private String device_name;
    private void LoData_BP(){
    	MeasureDAO dao = new  MeasureDAO();
		ArrayList<VO_BP> getmeasure = dao.getdata_BP(this, "connected", MEAUSE_DEVICE_ON);
		if(getmeasure != null){
			for(int i=0; i<getmeasure.size(); i++){
				VO_BP data = getmeasure.get(i);
				Log.e("Table_name","Table_name="+ TABLE_BP.TABLE_NAME);
				System.out.println("getITEMNO="+data.getITEMNO());
				System.out.println("getDEVICE_NAME="+data.getDEVICE_NAME());
				System.out.println("getCREATE_DATE="+data.getCREATE_DATE());	
				System.out.println("getSWITCH="+data.getSWITCH());	
				System.out.println("---------------------------");
				device_name = data.getDEVICE_NAME();
				name = device_name.split(relation);	
				address = device_name.split(relation);			
			}
		}else{

		}
    }
    
    private void LoData_BG(){
    	MeasureDAO dao = new  MeasureDAO();
		ArrayList<VO_BG> getmeasure = dao.getdata_BG(this, "connected", MEAUSE_DEVICE_ON);
		if(getmeasure != null){
			for(int i=0; i<getmeasure.size(); i++){
				VO_BG data = getmeasure.get(i);
				Log.e("Table_name","Table_name="+ TABLE_BG.TABLE_NAME);
				System.out.println("getITEMNO="+data.getITEMNO());
				System.out.println("getDEVICE_NAME="+data.getDEVICE_NAME());
				System.out.println("getCREATE_DATE="+data.getCREATE_DATE());	
				System.out.println("getSWITCH="+data.getSWITCH());	
				System.out.println("---------------------------");
				device_name = data.getDEVICE_NAME();
				name = device_name.split(relation);	
				address = device_name.split(relation);			
			}
		}else{

		}
    }
    
    private void LoData_BO(){
    	MeasureDAO dao = new  MeasureDAO();
		ArrayList<VO_BO> getmeasure = dao.getdata_BO(this, "connected", MEAUSE_DEVICE_ON);
		if(getmeasure != null){
			for(int i=0; i<getmeasure.size(); i++){
				VO_BO data = getmeasure.get(i);
				Log.e("Table_name","Table_name="+TABLE_BP.TABLE_NAME);
				System.out.println("getITEMNO="+data.getITEMNO());
				System.out.println("getDEVICE_NAME="+data.getDEVICE_NAME());
				System.out.println("getCREATE_DATE="+data.getCREATE_DATE());	
				System.out.println("getSWITCH="+data.getSWITCH());	
				System.out.println("---------------------------");
				device_name = data.getDEVICE_NAME();
				name = device_name.split(relation);	
				address = device_name.split(relation);			
			}
		}else{

		}
    }
    
    private void LoData_HR(){
    	MeasureDAO dao = new  MeasureDAO();
		ArrayList<HRDataInfoVO> getmeasure = dao.getHRDataSheet(this);
		if(getmeasure != null){
			for(int i=0; i<getmeasure.size(); i++){
				HRDataInfoVO data = getmeasure.get(i);
				System.out.println("getHRDataNumber="+data.getHRDataNumber());
				System.out.println("getHeartRate="+data.getHeartRate());
				System.out.println("getPrValue="+data.getPrValue());	
				System.out.println("getRelaxDegree="+data.getRelaxDegree());				
				System.out.println("getBreaThing="+data.getBreaThing());
				System.out.println("getHeartage="+data.getHeartage());
				System.out.println("getFiveHa="+data.getFiveHa());	
				System.out.println("getBsTime="+data.getBsTime());				
				System.out.println("getCreateDate="+data.getCreateDate());
				System.out.println("getRowData="+data.getRowData());
				System.out.println("getCreateDateDetails="+data.getCreateDateDetails());	
				System.out.println("getDetailNo="+data.getDetailNo());	
				System.out.println("---------------------------");	
			}
		}else{

		}
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public static boolean removeBond(Class btClass, BluetoothDevice btDevice) throws Exception{  
        Method removeBondMethod = btClass.getMethod("removeBond");  
        Boolean returnValue = (Boolean) removeBondMethod.invoke(btDevice);  
        return returnValue.booleanValue();  
    }  
    
    public Handler mHandler = new Handler() {
    	@Override
    	public void handleMessage(Message msg) {   		
    		super.handleMessage(msg);
    		measuringCountdownTimer.postDelayed(mRunnable, 1000);
    	}
    };
      
    @Override
    public void onStart() { 
        super.onStart();
        if(D) Log.e(TAG, "++ ON START ++");

        // 判斷藍牙是否打開.
        // setupChat在onActivityResult()將被調用
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, BluetoothBaseActivity.REQUEST_ENABLE_BT);
        } else {	 		
	 		try{
	 			result.ClearData();
	 			AnimType();	
				anim = (AnimationDrawable) imageanimation.getDrawable();
				anim.start();
				
				System.out.println("Mark 藍芽裝置 = " + name[0]);
				 
				if(name[0].equals(ICHOICE_HEART_DEVICE_NAME) | name[0].equals(YC_GLUCOSE_DEVICE_NAME)){
					getMeasurementFromBLEDevice(address[1]);
				}
				else if(name[0].equals(Command.DEVICENAME.ITONDM)){
					timer.setText("");
					device = mBluetoothAdapter.getRemoteDevice(address[1]);	
					measuringCountdownTimer = new Handler();
					service.setHandler(mHandler);					
			     	mChatService.connect(device); 		          	
//			     	裝置TimeOut
			     	if(bluetimeout != null) bluetimeout.cancel();	    
				 	bluetimeout = new SocketTimeout(40000, 1000);
				 	bluetimeout.start();
				}
				else{
					device = mBluetoothAdapter.getRemoteDevice(address[1]);	
								 			
			     	mChatService.connect(device); 		          	
//			     	裝置TimeOut
			     	if(bluetimeout != null) bluetimeout.cancel();	    
				 	bluetimeout = new SocketTimeout(40000, 1000);
				 	bluetimeout.start(); 					
				}	 
			}catch(Exception e){
				e.printStackTrace();
				Toast.makeText(BluetoothChatActivity.this, getString(R.string.device_not_pair), Toast.LENGTH_LONG).show();	
				BluetoothChatActivity.this.finish();
			}
        }
    }

    @Override
    public synchronized void onResume() {
        super.onResume();
        if(D) Log.e(TAG, "+ ON RESUME +");
//        if (mChatService != null) {
//            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
//              mChatService.start();
//            }
//        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
	@Override
    public void onDestroy() {    	
    	super.onDestroy();
    	earlyEndTiming();
    }
    
    public static void Activityfinish(){
    	try{  		 
			bluetoothChatActivity.finish();								
    	}catch(Exception e){
    		
    	}	
    }
    
    private void ensureDiscoverable() {
        if(D) Log.d(TAG, "ensure discoverable");
        if (mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }
     
    public static final String ICHOICE_HEART_DEVICE_NAME = "ichoice";
	public static final String YC_GLUCOSE_DEVICE_NAME = "BJYC-5D-8B B4"; 	
	private BluetoothManager bluetoothManager;

	public void getMeasurementFromBLEDevice(String address){		
//		if(UserInfoFragment.getAndroidSDKVersion() > 17){
//			bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
//			BluetoothDevice device = bluetoothManager.getAdapter().getRemoteDevice(address);
//			connectToBLEDevice(device);
//		}
//		else{
			finish(); 
			Toast.makeText(this, "Inadequate version 4.4!!", Toast.LENGTH_LONG).show();
//		}
    }
	
	@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
	private void connectToBLEDevice(BluetoothDevice device){
		Bluetooth4DeviceInterface service = choiceBLEDeviceService(device);
		connect(service,device);
	}
	
	@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
	private Bluetooth4DeviceInterface choiceBLEDeviceService(BluetoothDevice device){
		Bluetooth4DeviceInterface service = null;
		if(device==null){
			Log.e("starlin","device not find...");
		}
		else if(ICHOICE_HEART_DEVICE_NAME.equals(device.getName())){
			service = new ChoicemmedPluseOximeterService();
		}
		else if(YC_GLUCOSE_DEVICE_NAME.equals(device.getName())){
			service = new YCGlucoseService(GetBloodHandler);				 
		}
		return service;
	}

    public Handler GetBloodHandler = new Handler() {
    	@Override
    	public void handleMessage(Message msg) {   		
    		super.handleMessage(msg);
    		anim.stop();
//    		imageanimation.setImageResource(R.drawable.getblood);
    	}
    };
  
    private Runnable mRunnable = new Runnable() {
		
		@Override
		public void run() {			 
			timingResults();   	
		}
	};
   
    private void timingResults(){
    	measuringCountdownTimer.postDelayed(mRunnable, 1000);
    	countBackwards--;
    	endTiming();
		timer.setText(countBackwards+"");		
    }
    
    private void earlyEndTiming(){
    	if(measuringCountdownTimer != null)
    		measuringCountdownTimer.removeCallbacks(mRunnable);
    } 
    
    private void endTiming(){
    	if(countBackwards == 0)
    		measuringCountdownTimer.removeCallbacks(mRunnable);
    }
    
	@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
	private void connect(Bluetooth4DeviceInterface service, BluetoothDevice device){
		if(service!=null){
			Log.e("MainDeviceControl", "connectGatt");
			gatt = device.connectGatt(this, false, service.getCallbackObject());
		}
	}
}
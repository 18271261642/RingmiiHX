package hat.bemo.measure.service;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.RequiresApi;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.guider.healthring.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import hat.bemo.BaseActivity;
import hat.bemo.measure.set.Command;
import hat.bemo.measure.set.ParametersResult;
import hat.bemo.measure.setting_db.Insert;
import hat.bemo.measure.setting_db.MeasureDAO;
import hat.bemo.measure.setting_db.TABLE_BG;
import hat.bemo.measure.setting_db.TABLE_BO;
import hat.bemo.measure.setting_db.TABLE_BP;
import hat.bemo.measure.setting_db.VO_BG;
import hat.bemo.measure.setting_db.VO_BO;
import hat.bemo.measure.setting_db.VO_BP;


public class BluetoothBaseActivity extends BaseActivity {
	private static final String TAG = "BlueToothBaseActivity";
	private static final boolean D = true;
	public static final String TYPE = "BT_TYPE";
	public static final int REQUEST_CONNECT_DEVICE = 1;
	public static final int REQUEST_ENABLE_BT = 2;
	
    //從BluetoothChatService發送處理程序的消息類型
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    //連接設備的名稱
    private String mConnectedDeviceName = null;
    //參數回傳
    public static ParametersResult result = new ParametersResult();
    //字符
    public static final String relation = "@relation@";
    
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";
    public static String EXTRA_DEVICE_NAME = "device_name";
    public static String EXTRA_DEVICE_ADDRESS = "device_address";
    public static String MEAUSE_DEVICE_ON = "ON";
    public static String MEAUSE_DEVICE_OFF = "OFF";
    // 成員對象聊天服務
    public BluetoothAdapter mBluetoothAdapter;
    public BluetoothGatt gatt;
    public BluetoothDevice device;
    public static Dialog dialog;
    public static String dialogswitch = "ON";
    public static AnimationDrawable anim = null;
    public static Context bluetoothcontext;   	
    
    // 此Handler處理BluetoothChatService傳來的消息
    @SuppressLint("HandlerLeak")
	public final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MESSAGE_STATE_CHANGE:
                Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                switch (msg.arg1) {
                case BluetoothChatService.STATE_CONNECTED:
                	Log.e(TAG, getString(R.string.title_connected_to) + mConnectedDeviceName);
        			new Handler().postDelayed(new Runnable() {
        				
        				@Override
        				public void run() {
        					sendMsg("MEASURE_RESULT_CMD");		
        				}
        			}, 3000);
                	TimeOutStop();
                    break;
                case BluetoothChatService.STATE_CONNECTING:
                	Log.e(TAG, getString(R.string.title_connecting));
                    break;
                case BluetoothChatService.STATE_LISTEN:
                	
                	
                case BluetoothChatService.STATE_NONE:
//                	 Dialog_Disconnection();		
                	 Log.e(TAG, "STATE_NONE:"+getString(R.string.title_not_connected));
                    break;
                }
                break;
            case MESSAGE_WRITE:

                break;
            case MESSAGE_READ:
                byte[] readBuf = (byte[]) msg.obj;    
                result.ResultData(readBuf);
                break;
            case MESSAGE_DEVICE_NAME:
                // 保存連接設備的名字
                mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                
                break;
            case MESSAGE_TOAST:
            	if(dialogswitch.equals("ON")){
//            		  Dialog_Disconnection();
//                    Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST), Toast.LENGTH_SHORT).show();
            	}else{
            		dialogswitch = "ON";
            	}         	
                break;
            }
        }
    };
    
    /**
     * 發送消息
     * @param message  發送的內容
     */
    public void sendMsg(String message) {
        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

        if (message.length() > 0) { 
//          byte[] send = message.getBytes(); 
        	bluetimeout = new SocketTimeout(6000, 1000);
        	bluetimeout.start(); 	 
        	
//        	 if(Id == R.id.btn_item01){
//              	LoData_BP();
//            	if(name[0].equals(Command.DEVICENAME.TaidocDevice) | name[0].equals(Command.DEVICENAME.Taidoctd3128) |
//            	   name[0].equals(Command.DEVICENAME.FORAP60)      | name[0].equals(Command.DEVICENAME.FORAD40)){
//            		if(message.equals("MEASURE_RESULT_CMD")){
//            			Log.e("Connent", "BP-FORA--->Connent");
//            			mChatService.write(Command.FORA.MEASURE_RESULT_CMD);
//            		}
//            		else if(message.equals("TURN_OFF_CMD")){
//            			Log.e("Connent", "TURN_OFF_CMD");
//            			mChatService.write(Command.FORA.TURN_OFF_CMD);
//            		}
//            	}
//            	else if(name[0].equals(Command.DEVICENAME.ML103D)){
//            		BluetoothBaseActivity.TimeOutStop();
//            	}
//            	else if(name[0].equals(Command.DEVICENAME.BP)){
//            		if(message.equals("MEASURE_RESULT_CMD")){
//            			Log.e("Connent", "BP-FORA--->BP");
//                		mChatService.write(Command.BP.MEASURE_RESULT_CMD);
//            		}
//            		else if(message.equals("TURN_OFF_CMD")){
//            			Log.e("Connent", "TURN_OFF_CMD");
//            			mChatService.write(Command.BP.TURN_OFF_CMD);
//            			BluetoothBaseActivity.TimeOutStop();
//            		}
//            	}
//           	 }
//           	 else if(Id == R.id.btn_item02){
//
//           	 }
           	 
           	
           		LoData_BG();
           		if(name[0].equals(Command.DEVICENAME.TaidocTD4279)){
            		if(message.equals("MEASURE_RESULT_CMD")){
            			Log.e("Connent", "TaidocTD4279--->Connent");
            			mChatService.write(Command.FORA.MEASURE_RESULT_CMD);
            		}
            		else if(message.equals("TURN_OFF_CMD")){
            			Log.e("Connent", "TURN_OFF_CMD");
            			mChatService.write(Command.FORA.TURN_OFF_CMD);
            		} 
           		} 
           		else if(name[0].equals(Command.DEVICENAME.ITONDM)){
           			new Handler().postDelayed(new Runnable() {
						
						@Override
						public void run() {																			
							//用戶狀態			
							Log.e("Connent", "ITONDM");							 
							result.NonInvasive(service);
							service.setBluetoothChatService(mChatService);
							service.start();
							bluetimeout.cancel();
						}
					}, 1000);      			
           		}
           	 
           	 
//           	 else if(Id == R.id.btn_item04){
//
//           	 }
           	 
        }
    }
    
    public static int Id;
    public static void BTtype(int id){
    	Id = id;
    }
    
    public BluetoothChatService mChatService = new BluetoothChatService(this, mHandler);
    public NonInvasiveBloodGlucoseService service = new NonInvasiveBloodGlucoseService(this);
	public void Rotary_Information(int Id){	
		final Intent intent =new Intent(this, BluetoothShowDataActivity.class);
		Bundle b = new Bundle();
		b.putInt("type", Id);
		intent.putExtras(b);
		BluetoothChatActivity.Activityfinish();
		NonInvasiveBloodGlucoseActivity.Activityfinish();
		new Handler().postAtTime(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Log.e("BluetoothBaseActivity", "startActivity");
				startActivity(intent);	
			}
		}, 1000);	 		
	}
 
	public void Dialog_Disconnection(){	
		try{
			if(anim != null) anim.stop();
			if (mChatService != null) mChatService.stop();
		    DisplayMetrics metrics = bluetoothcontext.getResources().getDisplayMetrics();

			dialog = new Dialog(bluetoothcontext, R.style.AppTheme);
			dialog.setContentView(R.layout.gd800_measure_dialog_disconnection); 
			
			LinearLayout lin = (LinearLayout)dialog.findViewById(R.id.lins);			 		
			
			lin.getLayoutParams().width = metrics.widthPixels; 
			lin.getLayoutParams().height = metrics.heightPixels; 
		
			lin.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					dialog.cancel();
				}
			});
			
			dialog.show();
			dialog.setOnDismissListener(new OnDismissListener() {
				
				@Override
				public void onDismiss(DialogInterface dialog) {
					BluetoothChatActivity.Activityfinish();				
				}
			});
		}catch(Exception e){
			e.printStackTrace();
		}			
	}
	
	@SuppressLint("SimpleDateFormat")
	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	Log.e("LauncherActivity", "requestCode:"+requestCode);
        switch (requestCode) {
        case REQUEST_CONNECT_DEVICE:
            // 當DeviceListActivity返回與設備連接的消息
            if (resultCode == Activity.RESULT_OK) {
            	Log.e("", "onActivityResult > 連接");
                // 得到鏈接設備的MAC
            	String name = data.getExtras().getString(EXTRA_DEVICE_NAME);
            	String address = data.getExtras().getString(EXTRA_DEVICE_ADDRESS);
            	
            	MeasureDAO dao = new  MeasureDAO();
            	if(name.equals(Command.DEVICENAME.TaidocDevice) | name.equals(Command.DEVICENAME.Taidoctd3128) | 
            	   name.equals(Command.DEVICENAME.ML103D)		| name.equals(Command.DEVICENAME.FORAP60)| 
            	   name.equals(Command.DEVICENAME.FORAD40) 		| name.equals(Command.DEVICENAME.BP)){
            		ArrayList<VO_BP> getmeasure = dao.getdata_BP(this, "exist", name+relation+address);
            		if(getmeasure != null){
                		for(int i=0; i<getmeasure.size(); i++){
                			VO_BP mdata = getmeasure.get(i);
                			Log.e("Table_name","Table_name="+ TABLE_BP.TABLE_NAME);
                			System.out.println("getITEMNO="+mdata.getITEMNO());
                			System.out.println("getDEVICE_NAME="+mdata.getDEVICE_NAME());
                			System.out.println("getCREATE_DATE="+mdata.getCREATE_DATE());	
                			System.out.println("getSWITCH="+mdata.getSWITCH());	
                			System.out.println("---------------------------");
                		}
                		Toast.makeText(this, getString(R.string.prevent_errors1), Toast.LENGTH_LONG).show();
            		}else{
            			getmeasure = dao.getdata_BP(this, "", "");
            			VO_BP vo = new VO_BP();
            			Insert insert = new Insert();
            			if(getmeasure == null){    				
            				vo.setSWITCH(MEAUSE_DEVICE_ON);
                		}else{
                    		vo.setSWITCH(MEAUSE_DEVICE_OFF);
                		} 
            			vo.setDEVICE_NAME(name+relation+address);
                		vo.setCREATE_DATE(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS 'GMT'Z").format(new Date()));            		
                		insert.insert_BP(this, vo);
                		Toast.makeText(this, getString(R.string.prevent_errors2), Toast.LENGTH_LONG).show();
            		} 
            	}
    			else if(name.equals(Command.DEVICENAME.TaidocTD4279) | name.equals(Command.DEVICENAME.ITONDM) |
    					name.equals(Command.DEVICENAME.BJYC)){
    				ArrayList<VO_BG> getmeasure = dao.getdata_BG(this, "exist", name+relation+address);
            		if(getmeasure != null){
                		for(int i=0; i<getmeasure.size(); i++){
                			VO_BG mdata = getmeasure.get(i);
                			Log.e("Table_name","Table_name="+ TABLE_BG.TABLE_NAME);
                			System.out.println("getITEMNO="+mdata.getITEMNO());
                			System.out.println("getDEVICE_NAME="+mdata.getDEVICE_NAME());
                			System.out.println("getCREATE_DATE="+mdata.getCREATE_DATE());	
                			System.out.println("getSWITCH="+mdata.getSWITCH());	
                			System.out.println("---------------------------");
                		}
                		Toast.makeText(this, getString(R.string.prevent_errors1), Toast.LENGTH_LONG).show();
            		}else{
            			getmeasure = dao.getdata_BG(this, "", "");
            			VO_BG vo = new VO_BG();
            			Insert insert = new Insert();
            			if(getmeasure == null){    				
            				vo.setSWITCH(MEAUSE_DEVICE_ON);
                		}else{
                    		vo.setSWITCH(MEAUSE_DEVICE_OFF);
                		} 
            			vo.setDEVICE_NAME(name+relation+address);
                		vo.setCREATE_DATE(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS 'GMT'Z").format(new Date()));            		
                		insert.insert_BG(this, vo);
                		Toast.makeText(this, getString(R.string.prevent_errors2), Toast.LENGTH_LONG).show();
            		} 
    			}   
    			else if(name.equals(Command.DEVICENAME.ichoice)){
    				ArrayList<VO_BO> getmeasure = dao.getdata_BO(this, "exist", name+relation+address);
            		if(getmeasure != null){
                		for(int i=0; i<getmeasure.size(); i++){
                			VO_BO mdata = getmeasure.get(i);
                			Log.e("Table_name","Table_name="+ TABLE_BO.TABLE_NAME);
                			System.out.println("getITEMNO="+mdata.getITEMNO());
                			System.out.println("getDEVICE_NAME="+mdata.getDEVICE_NAME());
                			System.out.println("getCREATE_DATE="+mdata.getCREATE_DATE());	
                			System.out.println("getSWITCH="+mdata.getSWITCH());	
                			System.out.println("---------------------------");
                		}
                		Toast.makeText(this, getString(R.string.prevent_errors1), Toast.LENGTH_LONG).show();
            		}else{
            			getmeasure = dao.getdata_BO(this, "", "");
            			VO_BO vo = new VO_BO();
            			Insert insert = new Insert();
            			if(getmeasure == null){    				
            				vo.setSWITCH(MEAUSE_DEVICE_ON);
                		}else{
                    		vo.setSWITCH(MEAUSE_DEVICE_OFF);
                		} 
            			vo.setDEVICE_NAME(name+relation+address);
                		vo.setCREATE_DATE(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS 'GMT'Z").format(new Date()));            		
                		insert.insert_BO(this, vo);
                		Toast.makeText(this, getString(R.string.prevent_errors2), Toast.LENGTH_LONG).show();
            		} 
    			}
            }
            break;
        case REQUEST_ENABLE_BT:
            // 判斷藍牙是否啓用
            if (resultCode == Activity.RESULT_OK) {
                // 建立連接
//            	bluetoothConnect();
            } else {
              Log.e(TAG, "藍牙未啓用");
              Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
            }
        }
    }
    	
    private String[] name;
    private String device_name;
    private void LoData_BP(){
    	MeasureDAO dao = new  MeasureDAO();
		ArrayList<VO_BP> getmeasure = dao.getdata_BP(BluetoothBaseActivity.bluetoothcontext, 
													 "connected", BluetoothBaseActivity.MEAUSE_DEVICE_ON);
		if(getmeasure != null){
			for(int i=0; i<getmeasure.size(); i++){
				VO_BP data = getmeasure.get(i);
				device_name = data.getDEVICE_NAME();
				name = device_name.split(BluetoothBaseActivity.relation);								
			}
		} 
    }
	
    private void LoData_BG(){
    	MeasureDAO dao = new  MeasureDAO();
		ArrayList<VO_BG> getmeasure = dao.getdata_BG(BluetoothBaseActivity.bluetoothcontext, 
													 "connected", BluetoothBaseActivity.MEAUSE_DEVICE_ON);
		if(getmeasure != null){
			for(int i=0; i<getmeasure.size(); i++){
				VO_BG data = getmeasure.get(i);
				device_name = data.getDEVICE_NAME();
				name = device_name.split(BluetoothBaseActivity.relation);								
			}
		} 
    }
    
    public void ServiceStop(){
    	if (mChatService != null) mChatService.stop();
    }
    
    public static SocketTimeout bluetimeout;
    public class SocketTimeout extends CountDownTimer {     
        public SocketTimeout(long millisInFuture, long countDownInterval) {     
            super(millisInFuture, countDownInterval);     
        }     
        	       
		@Override     
        public void onFinish() {	
			Log.e("", "藍牙停止...重新連線");
			if (mChatService != null) mChatService.stop();
//			mChatService.connect(device); 	 
			new Handler().postDelayed(new Runnable() {
				
				@Override
				public void run() {
					Dialog_Disconnection();				
				}
			}, 2000);			
        }
        
        @Override     
        public void onTick(long millisUntilFinished) {     	        	              
        	Log.e("SocketTimeout", "SocketTimeout:"+millisUntilFinished/1000);	
        }    
    } 
    
	public static void TimeOutStop(){
		if(BluetoothBaseActivity.bluetimeout != null) BluetoothBaseActivity.bluetimeout.cancel();	
	}
	
	
	public static Context getAppContext() {
	    return bluetoothcontext;
	}
	
    @Override
    protected void onStart() {
    	super.onStart();
    	bluetoothcontext = BluetoothBaseActivity.this;
//    	if (mChatService != null) mChatService.stop();
    	if(D) Log.e(TAG, "- ON START -");       
    }
    
    @Override
    public synchronized void onPause() {
        super.onPause();
        if(D) Log.e(TAG, "- ON PAUSE -"); 
    }
    
    @Override
    public void onStop() {
        super.onStop();
        if(D) Log.e(TAG, "-- ON STOP --");
    }
    
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
	@Override
    public void onDestroy() {
        super.onDestroy();
        // 停止藍牙
        BluetoothBaseActivity.TimeOutStop();
        if (mChatService != null) mChatService.stop();
        if (gatt != null){
        	Log.e(TAG, "--- gatt close ---");
        	gatt.disconnect();
        	gatt.close();
        } 
        if(D) Log.e(TAG, "--- ON DESTROY ---");
    }
}
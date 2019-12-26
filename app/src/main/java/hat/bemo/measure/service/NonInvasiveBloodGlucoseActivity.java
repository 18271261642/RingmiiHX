package hat.bemo.measure.service;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import com.guider.healthring.R;
import hat.bemo.measure.setting_db.MeasureDAO;
import hat.bemo.measure.setting_db.TABLE_BG;
import hat.bemo.measure.setting_db.VO_BG;

public class NonInvasiveBloodGlucoseActivity extends BluetoothBaseActivity {
    private static final String TAG = "NonInvasiveBloodGlucoseActivity";
    private static final boolean D = true;  
    private static NonInvasiveBloodGlucoseActivity bluetoothChatActivity; 	 
    private TextView text;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if(D) Log.e(TAG, "+++ ON CREATE +++");
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.gd800_measure_noinvasivebloodglucoseactivity);

        bluetoothChatActivity = this; 	 
        // 獲取本地藍牙適配器
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();       		
        
        // 判斷藍牙是否可用
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "藍牙是不可用的", Toast.LENGTH_LONG).show();
            finish();
            return;
        }        
        init();          
    } 

    private void init(){
    	text = (TextView)findViewById(R.id.text);
    	
    	if(BluetoothBaseActivity.result.bg_map != null) BluetoothBaseActivity.result.bg_map.clear();
    	BTtype(R.id.btn_item03);
    	service.setHandler(noIBhandler);
 		LoData_BG();      
    } 
    
    private String[] name;
    private String[] address;
    private String device_name;
   
    private void LoData_BG(){
    	MeasureDAO dao = new  MeasureDAO();
		ArrayList<VO_BG> getmeasure = dao.getdata_BG(this, "connected", MEAUSE_DEVICE_ON);
		if(getmeasure != null){
			for(int i=0; i<getmeasure.size(); i++){
				VO_BG data = getmeasure.get(i);
				Log.e("Table_name","Table_name="+TABLE_BG.TABLE_NAME);
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
 
    @Override
    public void onStart() { 
        super.onStart();
//        if(D) Log.e(TAG, "++ ON START ++");

        // 判斷藍牙是否打開.
        // setupChat在onActivityResult()將被調用
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, BluetoothBaseActivity.REQUEST_ENABLE_BT);
        } else {	 		
	 		try{
	 			result.ClearData();	 			
				device = mBluetoothAdapter.getRemoteDevice(address[1]);							 			
			    mChatService.connect(device); 		          	
//			   	  裝置TimeOut
			    if(bluetimeout != null) bluetimeout.cancel();	    
				bluetimeout = new SocketTimeout(40000, 1000);
				bluetimeout.start(); 										 
			}catch(Exception e){
				e.printStackTrace();
				Toast.makeText(NonInvasiveBloodGlucoseActivity.this, getString(R.string.device_not_pair), Toast.LENGTH_LONG).show();	
				NonInvasiveBloodGlucoseActivity.this.finish();
			}
        }
    }

    @Override
    public synchronized void onResume() {
        super.onResume();
//        if(D) Log.e(TAG, "+ ON RESUME +");
    }
    
    Handler noIBhandler = new Handler() {
    	@Override
    	public void handleMessage(Message msg) {   		
    		super.handleMessage(msg);
    		text.setText(msg.obj.toString());
    	}
    };
    
    public static void Activityfinish(){
    	try{  		 
			bluetoothChatActivity.finish();								
    	}catch(Exception e){
    		
    	}	
    }
}
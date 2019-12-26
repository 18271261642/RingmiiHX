package hat.bemo.measure.service;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;

import hat.bemo.measure.set.Command;
import hat.bemo.measure.setting_db.MeasureDAO;
import hat.bemo.measure.setting_db.TABLE_BG;
import hat.bemo.measure.setting_db.TABLE_BP;
import hat.bemo.measure.setting_db.VO_BG;
import hat.bemo.measure.setting_db.VO_BP;

public class PairingRequest extends BroadcastReceiver {  
	private final static String TaidocDevice_ping = "111111";  
	private final static String ML103D_ping = "4103"; 
    final String ACTION_PAIRING_REQUEST = "android.bluetooth.device.action.PAIRING_REQUEST";  
    static BluetoothDevice remoteDevice = null;  
    private Context context;
    
    @Override   
    public void onReceive(Context context, Intent intent) {
    	this.context = context;
    	LoData_BP();
    	
        if (intent.getAction().equals(ACTION_PAIRING_REQUEST)) {  
  
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);  
            if (device.getBondState() != BluetoothDevice.BOND_BONDED) {  
                try {  
                	/*
               	 if(BluetoothBaseActivity.Id == R.id.btn_item01){  
	                   	LoData_BP();
	                    if(name[0].equals(Command.DEVICENAME.TaidocDevice) | name[0].equals(Command.DEVICENAME.Taidoctd3128)| 
	                       name[0].equals(Command.DEVICENAME.FORAP60) 	   | name[0].equals(Command.DEVICENAME.FORAD40)){
	                    	 Log.e("PIN", "FORA bp pin...");
	                         ClsUtils.setPin(device.getClass(), device, TaidocDevice_ping);
	                     }
	                     else if(name[0].equals(Command.DEVICENAME.ML103D)){
	                    	 Log.e("PIN", "microlife pin...");
	                         ClsUtils.setPin(device.getClass(), device, ML103D_ping);
	                     }  
                  }
                  else if(BluetoothBaseActivity.Id == R.id.btn_item02){		

                  }
                  else if(BluetoothBaseActivity.Id == R.id.btn_item03){	
                  */
	                	LoData_BG();
	                	if(name[0].equals(Command.DEVICENAME.TaidocTD4279)){
	                		Log.e("PIN", "FORA bg pin...");
	                		ClsUtils.setPin(device.getClass(), device, TaidocDevice_ping);
	                	}
	                	else if(name[0].equals(Command.DEVICENAME.ITONDM)){
	                		Log.e("PIN", "無創 bg pin...");
	                		ClsUtils.setPin(device.getClass(), device, "");
	                	}
	                	/*
                  }
                  else if(BluetoothBaseActivity.Id == R.id.btn_item04){

                  }
                  */
                  // ClsUtils.cancelPairingUserInput(device.getClass(),  
                  // device); //一般调用不成功，前言里面讲解过了  
                } catch (Exception e) {  
                    e.printStackTrace(); 
                }  
            }  
            // */  
            // pair(device.getAddress(),strPsw);  
        }  
    }
    
    private String[] name;
    private String device_name;
    private void LoData_BP(){
    	MeasureDAO dao = new  MeasureDAO();
		ArrayList<VO_BP> getmeasure = dao.getdata_BP(context, "connected", BluetoothBaseActivity.MEAUSE_DEVICE_ON);
		if(getmeasure != null){
			for(int i=0; i<getmeasure.size(); i++){
				VO_BP data = getmeasure.get(i);
				Log.e("Table_name","Table_name="+TABLE_BP.TABLE_NAME);
				System.out.println("getITEMNO="+data.getITEMNO());
				System.out.println("getDEVICE_NAME="+data.getDEVICE_NAME());
				System.out.println("getCREATE_DATE="+data.getCREATE_DATE());	
				System.out.println("getSWITCH="+data.getSWITCH());	
				System.out.println("---------------------------");
				device_name = data.getDEVICE_NAME();
				name = device_name.split(BluetoothBaseActivity.relation);				 
			}
		} 
    }
    
    private void LoData_BG(){
    	MeasureDAO dao = new  MeasureDAO();
		ArrayList<VO_BG> getmeasure = dao.getdata_BG(context, "connected", BluetoothBaseActivity.MEAUSE_DEVICE_ON);
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
				name = device_name.split(BluetoothBaseActivity.relation);				 
			}
		} 
    }
}  
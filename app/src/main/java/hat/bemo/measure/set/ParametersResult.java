package hat.bemo.measure.set;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

import hat.bemo.measure.service.BluetoothBaseActivity;
import hat.bemo.measure.service.NonInvasiveBloodGlucoseService;
import hat.bemo.measure.setting_db.MeasureDAO;
import hat.bemo.measure.setting_db.VO_BG;
import hat.bemo.measure.setting_db.VO_BO;
import hat.bemo.measure.setting_db.VO_BP;

public class ParametersResult {
//	量測數據長度
    public static ArrayList<String> meaDataList = new ArrayList<String>();
//  血壓參數
    private String dbp;
    private String sbp;
//  心率  
    private String heart;
//  血糖參數
    private String bg;
//  超思血氧
    private String oxygen;
//  無創血糖參數解析後的結果
    public HashMap<String, String> bg_map = new HashMap<String, String>();	
    private ArrayList<String> bg_list;
    private StringBuffer bg_buf;	 
    
    private NonInvasiveBloodGlucoseService service;
    
    public void ClearData(){
    	meaDataList.clear();
    }
    
	public void ResultData(byte[] readBuf){
        for(byte b: readBuf){                   		 
    		if(b != 0){
    			if(b <= 0){   							
    				meaDataList.add(String.valueOf(byteToInt(b)));
    				Log.e("meaDataList", byteToInt(b)+"");
    			}
    			else{
    				meaDataList.add(String.valueOf(b));  
        			Log.e("meaDataList", b+"");
    			}			
    		} 
    	}       
          	 
//        if(BluetoothBaseActivity.Id == R.id.btn_item01){
//	           	LoData_BP();
//	    		/**
//	    		 * FORA血壓參數分析
//	    		 */
//	    		if(name[0].equals(Command.DEVICENAME.TaidocDevice) | name[0].equals(Command.DEVICENAME.Taidoctd3128) |
//	    		   name[0].equals(Command.DEVICENAME.FORAP60)      | name[0].equals(Command.DEVICENAME.FORAD40)){
//	                if(meaDataList.size() == 4){
//	                    if(meaDataList.get(0).equals("81") & meaDataList.get(1).equals("84")){
//	                    	meaDataList.clear();
//	                    }
//	                }
//	                Log.e("size", meaDataList.size()+"");
//	                if(meaDataList.size() == 4){
//	                    if(meaDataList.get(0).equals("81") & meaDataList.get(1).equals("80") & meaDataList.get(2).equals("165") &
//	                       meaDataList.get(3).equals("70")){
//	                    	meaDataList.clear();
//	                    }
//	                }
//
//	                if(meaDataList.size() >= 7){
//	                	 Log.e("size", ">= 7");
//	                    if(meaDataList.get(0).equals("81") & meaDataList.get(1).equals("38")){
//	                    	dbp = meaDataList.get(2);
//	                    	sbp = meaDataList.get(3);
//	                    	heart = meaDataList.get(4);
//
//	                    	if(name[0].equals(Command.DEVICENAME.FORAP60)){
//	                    		Log.e("size", "P60");
//	                    		dbp = meaDataList.get(2);
//		                    	sbp = meaDataList.get(4);
//		                    	heart = meaDataList.get(5);
//	                    	}
//
//	                    	meaDataList.clear();
//	                 		FORA_TURN_OFF_BP();
//	                 		BluetoothBaseActivity.TimeOutStop();
//	                    }else{
//	                    	 Log.e("size", "get(0):"+meaDataList.get(0));
//	                    	 Log.e("size", "get(1):"+meaDataList.get(1));
//	                    	 Log.e("size", "dbp:"+dbp);
//	                    	 Log.e("size", "sbp:"+sbp);
//	                    	 Log.e("size", "heart:"+heart);
//	                    }
//	                }
//	    		}
//	    		/**
//	    		 * microlife血壓參數分析
//	    		 */
//	    		else if(name[0].equals(Command.DEVICENAME.ML103D)){
//	    			 if(meaDataList.size() == 11){
//	    				 if(meaDataList.get(3).equals("69") & meaDataList.get(4).equals("114") &
//	    					meaDataList.get(5).equals("66")){
//	    	            	  Toast.makeText(BluetoothChatActivity.getAppContext(), "請更換電池!!", Toast.LENGTH_LONG).show();
//	    	            	  BluetoothBaseActivity.TimeOutStop();
//	    	            	  ((BluetoothBaseActivity)BluetoothBaseActivity.getAppContext()).Dialog_Disconnection();
//	 	    	         }
//	    				 else if(meaDataList.get(0).equals("77") & meaDataList.get(1).equals("76") &
//	    						 meaDataList.get(2).equals("16")){
//	    	            	  dbp = meaDataList.get(3);
//	    	                  sbp = meaDataList.get(4);
//	    	                  heart = meaDataList.get(5);
//
//	    	                  meaDataList.clear();
//	    	          		((BluetoothBaseActivity)BluetoothBaseActivity.getAppContext()).Rotary_Information(R.id.btn_item01);
//	    	          		BluetoothBaseActivity.TimeOutStop();
//	    	              }
//	    	         }
//	    		}
//	    		/**
//	    		 * 倍泰血壓參數分析
//	    		 */
//	    		else if(name[0].equals(Command.DEVICENAME.BP)){
//	    			Log.e("meaDataList", "size:"+meaDataList.size());
//	    			if(meaDataList.size() >= 16){
//	    				if(meaDataList.get(1).equals("5") & meaDataList.get(2).equals("34") & meaDataList.get(3).equals("1")){
//	    					Log.e("meaDataList", "指令驗證成功!!!!!!!!");
//	    					if(meaDataList.get(15).equals("187")){
//	    					   Log.e("meaDataList_if", "倍泰血壓參數分析!!!!!!!!");
//	    					   dbp = meaDataList.get(11);
//	  	    				   sbp = meaDataList.get(12);
//	  	    				   heart = meaDataList.get(13);
//	  	    				   BP_TURN_OFF_BP();
//	  	    				   BluetoothBaseActivity.TimeOutStop();
//	  	    				   meaDataList.clear();
//	    					}
//	    					else{
//	    					   Log.e("meaDataList_else", "倍泰血壓參數分析!!!!!!!!");
//	    					   dbp = meaDataList.get(10);
//		  	    			   sbp = meaDataList.get(11);
//		  	    			   heart = meaDataList.get(12);
//		  	    			   BP_TURN_OFF_BP();
//		  	    			   BluetoothBaseActivity.TimeOutStop();
//		  	    			   meaDataList.clear();
//	    					}
//	    		    	}
//	    			}
//	    		}
//          }
//          else if(BluetoothBaseActivity.Id == R.id.btn_item02){
//        	  	LoData_BO();
//        	  	/**
//	    		 * 超思血氧參數分析
//	    		 */
//        	  	if(name[0].equals(Command.DEVICENAME.ichoice)){
//        	  		Log.e("size", "size:"+meaDataList.size());
//        	  		oxygen = meaDataList.get(3);
//        	  		heart = meaDataList.get(4);
//        	  		IC_TURN_OFF_BO();
//        	  		meaDataList.clear();
//        	  	}
//          }
//          else if(BluetoothBaseActivity.Id == R.id.btn_item03){
//	        	LoData_BG();
//	        	/**
//	    		 * FORA血糖參數分析
//	    		 */
//	        	if(name[0].equals(Command.DEVICENAME.TaidocTD4279)){
//		        	if(meaDataList.get(0).equals("81") & meaDataList.get(1).equals("38")){
//		        		float f = Float.valueOf(String.valueOf(meaDataList.get(2))) / 18;
//		        		bg = new DecimalFormat("0.0").format(f);
//		        		meaDataList.clear();
//		        		FORA_TURN_OFF_BG();
//		        		BluetoothBaseActivity.TimeOutStop();
//		        	}
//		        	/**
//		    		 * FORA血糖關機回傳的參數分析
//		    		 */
//		        	else if(meaDataList.get(0).equals("81") & meaDataList.get(1).equals("80")){
//		        		Log.e("", "BG TURN_OFF");
//		        		meaDataList.clear();
//		        	}
//	        	}
//	        	/**
//	    		 * 怡成血糖參數分析
//	    		 */
//	        	else if(name[0].equals(Command.DEVICENAME.BJYC)) {
//	        		Log.e("size", "size:"+meaDataList.size());
//	        		if(meaDataList.size() >= 11){
//	        			bg = meaDataList.get(8);
//	        		}
//	        		else{
//	        			bg = meaDataList.get(7);
//	        		}
//	        		float g = Float.parseFloat(bg) / 10;
//	        		bg = String.valueOf(g);
//	        		Log.e("bg", "bg:"+g);
//	        		BJYC_TURN_OFF_BG();
//	        		meaDataList.clear();
//	        	}
//	        	/**
//	    		 * 無創血糖回傳參數 --> Bg_DataFormat
//	    		 */
//	        	else if(name[0].equals(Command.DEVICENAME.ITONDM)) {
//	        		char cv;
//	        		byte b;
//	        		bg_buf = new StringBuffer();
//    				bg_list = new ArrayList<String>();
//    				BluetoothBaseActivity.TimeOutStop();
//	        		for(String value : meaDataList){
//	        			b = (byte)((int)Integer.valueOf(value));
//	        			cv = (char) b;
//	        			bg_list.add(String.valueOf(cv));
//	        			bg_buf.append(String.valueOf(cv));
//	        			if(String.valueOf(cv).equals("$")){
////	        				Bg_DataFormat(bg_list, bg_buf);
//	        				service.getByte(bg_list, bg_buf);
//	        				bg_buf = new StringBuffer();
//	        				bg_list = new ArrayList<String>();
//	        				meaDataList = new ArrayList<String>();
//	            			Log.e("value", "==============分隔線===============");
//	        			}
//	        		}
//	        	}
//	        	else{
//	        		Log.e("size", "get(0):"+meaDataList.get(0));
//	               	Log.e("size", "get(1):"+meaDataList.get(1));
//	               	Log.e("size", "bg:"+bg);
//	        	}
//	      }
//          else if(BluetoothBaseActivity.Id == R.id.btn_item04){
//
//          }
	}
	/**
	 * FORA血壓關機
	 */
	private void FORA_TURN_OFF_BP(){
 		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				((BluetoothBaseActivity)BluetoothBaseActivity.getAppContext()).sendMsg("TURN_OFF_CMD");
//				((BluetoothBaseActivity)BluetoothBaseActivity.getAppContext()).Rotary_Information(R.id.btn_item01);
		 		new Handler().postDelayed(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub						
						
					}
				}, 2000);
			}
		}, 1000);
	}
	/**
	 * 倍泰血壓關機
	 */
	private void BP_TURN_OFF_BP(){	
 		new Handler().postDelayed(new Runnable() { 
			
			@Override
			public void run() {
				// TODO Auto-generated method stub			
				((BluetoothBaseActivity)BluetoothBaseActivity.getAppContext()).sendMsg("TURN_OFF_CMD");
//				((BluetoothBaseActivity)BluetoothBaseActivity.getAppContext()).ServiceStop();
//				((BluetoothBaseActivity)BluetoothBaseActivity.getAppContext()).Rotary_Information(R.id.btn_item01);
		 		new Handler().postDelayed(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub											
					}
				}, 5000);
			}
		}, 1000);
	}
	/**
	 * FORA血糖關機
	 */
	private void FORA_TURN_OFF_BG(){
 		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				((BluetoothBaseActivity)BluetoothBaseActivity.getAppContext()).sendMsg("TURN_OFF_CMD");
		 		new Handler().postDelayed(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
//						((BluetoothBaseActivity)BluetoothBaseActivity.getAppContext()).Rotary_Information(R.id.btn_item03);
					}
				}, 2000);
			}
		}, 1000);
	}
	/**
	 * 怡成血糖關機
	 */
	private void BJYC_TURN_OFF_BG(){
		new Thread(new Runnable(){
		    @Override
		    public void run() {						        
		        Looper.prepare();
//		        	((BluetoothBaseActivity)BluetoothBaseActivity.getAppContext()).Rotary_Information(R.id.btn_item03);
		        Looper.loop();
		    }
		}).start();	 		 
	}
	/**
	 * 超思血氧關機
	 */
	private void IC_TURN_OFF_BO(){
		new Thread(new Runnable(){
		    @Override
		    public void run() {						        
		        Looper.prepare();
//		        	((BluetoothBaseActivity)BluetoothBaseActivity.getAppContext()).Rotary_Information(R.id.btn_item02);
		        Looper.loop();
		    }
		}).start();	 		 
	}
	
	public void NonInvasive(NonInvasiveBloodGlucoseService service){
		this.service = service;
	}
	
	public String Getbp_DBP(){
//      Log.e("舒張壓", dbp);
		return dbp;
	}		
	public String Getbp_SBP(){
//      Log.e("收縮壓", sbp);
		return sbp;
	}		
	public String Getbp_HEART(){
//      Log.e("脈搏", heart);
		return heart;
	}
	
	public String Getbg_BG(){
		return bg;
	}
	
	public void setbg_BG2(HashMap<String, String> bg_map){
		this.bg_map = bg_map;
	}
	public HashMap<String, String> Getbg_BG2(){
		return bg_map;
	}
	
	public String Getbo_oxy(){
		return oxygen;
	}
	
	public String Getbo_heart(){
		return heart;
	}

	public static byte intToByte(int x) {  
	    return (byte) x;  
	}
	
	public static int byteToInt(byte b) {  
	    //Java 总是把 byte 当做有符处理；我们可以通过将其和 0xFF 进行二进制与得到它的无符值  
	    return b & 0xFF;  
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
    
    private void LoData_BO(){
    	MeasureDAO dao = new  MeasureDAO();
		ArrayList<VO_BO> getmeasure = dao.getdata_BO(BluetoothBaseActivity.bluetoothcontext, 
													 "connected", BluetoothBaseActivity.MEAUSE_DEVICE_ON);
		if(getmeasure != null){
			for(int i=0; i<getmeasure.size(); i++){
				VO_BO data = getmeasure.get(i);
				device_name = data.getDEVICE_NAME();
				name = device_name.split(BluetoothBaseActivity.relation);			
			}
		} 
    }
}
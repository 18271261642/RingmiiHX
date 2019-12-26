package hat.bemo.measure.service;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import com.guider.healthring.R;
import hat.bemo.measure.set.Command.ITONDM;
import hat.bemo.measure.set.Command.ITONDM.Commands;


public class NonInvasiveBloodGlucoseService{
	private BluetoothChatService mChatService;
	private Context context;
	private Handler mhandler;
//  無創血糖參數解析後的結果
    public HashMap<String, String> bg_map = new HashMap<String, String>();		 
    
	public NonInvasiveBloodGlucoseService(Context context){	
		this.context = context;
	}
	
	public void setBluetoothChatService(BluetoothChatService mChatService){
		this.mChatService = mChatService;
	}
	
	public void setHandler(Handler mhandler){
	 	this.mhandler = mhandler;
	}
	
	void start(){
		Message msg = new Message();
		msg.obj = context.getString(R.string.mea_Environmental_Testing);
//		mhandler.sendMessage(msg);
		commands();		
		requestLog("HJJC", "環境檢測");		
		ccCommand("HJJC");		
	}
	
	public void getByte(ArrayList<String> bg_list, StringBuffer bg_buf){		
		Bg_DataFormat(bg_list, bg_buf);
	}
	
	/**
	 * 無創血糖回傳的參數分析
	 */
	private void Bg_DataFormat(ArrayList<String> bg_list, StringBuffer bg_buf){
		Log.e("list_total", ""+bg_list.toString());
		Log.e("bg_total", ""+bg_buf.toString());
		Log.e("list_size", ""+bg_list.size());
		Log.e("buf length", ""+bg_buf.length());
		String mtype;
		String mvalue;
		if(bg_list.size()-1 == 0){
			
		}
		else if(bg_list.get(0).equals("D") & bg_list.get(1).equals("D") & bg_list.get(bg_list.size()-1).equals("$") |
				bg_list.get(0).equals("C") & bg_list.get(1).equals("C") & bg_list.get(bg_list.size()-1).equals("$")){
			mtype = bg_buf.toString().substring(2, 6);
			mvalue = ITONDM.DataAnalysis(bg_buf.toString().substring(2, 6), bg_buf.toString().substring(6, bg_buf.length()-1));
			
			bg_buf.delete(0, bg_buf.length());
			bg_list.clear();
			
			Log.e("bufmtype", mtype);	        			
			Log.e("bufmvalue", mvalue);			
		} 
		else{
			Toast.makeText(context, "數據不穩定，請檢查設備或重啟", Toast.LENGTH_LONG).show();
			BluetoothChatActivity.Activityfinish();
			NonInvasiveBloodGlucoseActivity.Activityfinish();		
			Log.e("Bg_DataFormat", "error!!!");	     
		}
		bg_buf.delete(0, bg_buf.length());
		bg_list.clear();
	}
	
	private void commands(){
		new ITONDM().setCommandListeners(new Commands() {
			
			@Override
			public void getJSOK() {
//				requestLog("ZJKS",  "電路板自我檢測");
//				ccCommend("ZJKS");						
			}

			@Override
			public void getGLUS(String command, String value) {
				Log.e("血糖值", value);
				resaultData(command, value);
			}

			@Override
			public void getXYBH(String command, String value) {
				Log.e("血氧饱和度", value);
//				resaultData(command, value);
			}

			@Override
			public void getXHDB(String command, String value) {
				Log.e("血红蛋白浓度", value);
				resaultData(command, value);
			}

			@Override
			public void getXLSD(String command, String value) {
				Log.e("血流速度", value);
				resaultData(command, value);
			}

			@Override
			public void getHJWD(String value) {
				Log.e("環境温度", value);
			}

			@Override
			public void getHJSD(String value) {
				Log.e("環境温度", value);
			}

			@Override
			public void getTBWD(String value) {
				Log.e("體表温度", value);
			}

			@Override
			public void getTBSD(String value) {
				Log.e("體表湿度", value);
			}

			@Override
			public void getXTMB(String command, String value) {
				Log.e("脈搏數", value);
//				ccCommand("CSSJ");	 
//				resaultData(command, value);
			}
			
			/**
			0代表失败
			1代表成功
			**/	
			@Override
			public void getZJJG(String value) {						
				Log.e("自检结果", value);
			}

			/**
			0正常
			1手指温度过冷
			2手指温度过热	
			**/	
			@Override
			public void getSZER(String value) {						
				Message msg = new Message();
				switch (Integer.valueOf(value)) {
				case 0:					
					msg.obj = context.getString(R.string.mea_finger_normal);						 
					break;
				case 1:					
					msg.obj = context.getString(R.string.mea_finger_temperature);				 
					break;
				case 2:
					msg.obj = context.getString(R.string.mean_finger_overheating);				
					break;				 
				default:
					break;
				}
//				mhandler.sendMessage(msg);	
			}

			/**
			 * 0:代表环境符合要求
			 * 1:代表环境温度过冷
			 * 2:代表环境温度过热
			 * 3:代表环境湿度过干燥
			 * 4:代表环境湿度过湿润	
			 * **/	
			@Override
			public void getHJER(String value) {		
				Message msg = new Message();
				switch (Integer.valueOf(value)) {
				case 0:					
					msg.obj = context.getString(R.string.mea_Meets_the_requirements);					 
					ccCommand("STAT");	
					break;
				case 1:					
					msg.obj = context.getString(R.string.mea_Supercooling);	
					ccCommand("STAT");	
					break;
				case 2:
					msg.obj = context.getString(R.string.mea_overheat);		
					ccCommand("STAT");	
					break;
				case 3:
					msg.obj = context.getString(R.string.mea_dry);	
					ccCommand("STAT");	
					break;
				case 4:
					msg.obj = context.getString(R.string.mea_Moist);
					ccCommand("STAT");	
					break;
				default:
					break;
				}
//				mhandler.sendMessage(msg);	
			}

			@Override
			public void getFRSZ() {								
				Toast.makeText(context, context.getString(R.string.mea_fingers_into), Toast.LENGTH_LONG).show();
				Message msg = new Message();
				msg.obj = context.getString(R.string.mea_fingers_into);				
				mhandler.sendMessage(msg);	
			}

			@Override
			public void getERRO() {
				Toast.makeText(context, context.getString(R.string.mea_dps_error), Toast.LENGTH_LONG).show();	
				BluetoothChatActivity.Activityfinish();
			} 

			@Override
			public void getSTOP() {

			}

			@Override
			public void getSJJS() {

			}

			@Override
			public void getBCKS() {

			}

			@Override
			public void getBCJS() {

			}

			@Override
			public void getERTT() {

			}

			@Override
			public void getERAD() {
				Toast.makeText(context, "採集數據有問題，請檢查接口", Toast.LENGTH_LONG).show();
				BluetoothChatActivity.Activityfinish();
				NonInvasiveBloodGlucoseActivity.Activityfinish();		
				Log.e("Bg_DataFormat", "error!!!");	 
			}

			@Override
			public void getERBW() {
				Toast.makeText(context, "採集數據有問題，請檢查接口", Toast.LENGTH_LONG).show();
				BluetoothChatActivity.Activityfinish();
				NonInvasiveBloodGlucoseActivity.Activityfinish();		
				Log.e("Bg_DataFormat", "error!!!");	 
			}

			@Override
			public void getERWD() {
	
			}

			@Override
			public void getZJWC() {
			
			}

			@Override
			public void getWCWC() {
		
			}

			@Override
			public void getINOK() {
			
			}

			@Override
			public void getCWHM() {
				Log.e("CWHM", "錯誤代碼");		
			}
		});
	}
	
	private void ccCommand(String type){
		String cmd = "CC"+type+"$";
		mChatService.write(cmd.getBytes()); 	
	}
	
	private void ddCommand(String type){
		String cmd = "DD"+type+"$";
		mChatService.write(type.getBytes()); 	
	}
	
	private void resaultData(String mtype, String mvalue){		
		bg_map.put(mtype, mvalue);
		Log.e("map length", bg_map.size()+"");
		if(Float.valueOf(mvalue) == 0.0) {
			return;
		}
		if(bg_map.size() == 3) {	        
			BluetoothBaseActivity.result.setbg_BG2(bg_map);
//			((BluetoothBaseActivity)BluetoothBaseActivity.getAppContext()).Rotary_Information(R.id.btn_item01);
		}
	}
	
	public static void requestLog(String type, String value){
		Log.e(type, "Request:"+value);
	}
	
	public static void responsesLog(String type, String value){
		Log.e(type, "Responses:"+value);
	}
}
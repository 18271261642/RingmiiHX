package hat.bemo.measure.set;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

import org.apache.http.NameValuePair;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

import hat.bemo.APIupload.Controller;
import hat.bemo.BlueTooth.blegatt.api.ChangeDateFormat;
import hat.bemo.MyApplication;
import hat.bemo.measure.setting_db.MeasureDAO;
import hat.bemo.measure.setting_db.VO_BG_DATA;
import hat.bemo.measure.setting_db.VO_BO_DATA;
import hat.bemo.measure.setting_db.VO_BP_DATA;
import hat.bemo.measure.setting_db.VO_BW_DATA;
import hat.bemo.measure.setting_db.VO_Temp_DATA;

public class MeasureController {
	public final static String HTTP = "http://";	
	public final static String SERVER_HOST_0x36 = "/APP/GcareUploadBloodPressureData.html";
	public final static String SERVER_HOST_0x37 = "/APP/GcareUploadBloodSugarData.html";
	public final static String SERVER_HOST_0x38 = "/APP/GcareUploadBloodOxygenData.html";
	public final static String SERVER_HOST_0x39 = "/APP/GcareUploadWeightData.html";
	public final static String SERVER_HOST_0x3A = "/APP/GcareUploadTemperatureData.html";
	
	public final static String type_0x36 = "0x36";
	public final static String type_0x37 = "0x37";
	public final static String type_0x38 = "0x38";
	public final static String type_0x39 = "0x39";
	public final static String type_0x3A = "0x3A";
	
	private static String CreateDate;
	private static String TimeStamp;
	private static MeasureDAO dao;
	private List<NameValuePair> ValuePair;
	private MeasureFieldName fn;
	private String DataJosn;
	private String DataHash;
	private MeasureJsonFormat json;
	private String URL; 
	private String TYPE;
	private static VO_BP_DATA vobpdata;
	private static VO_BG_DATA vobgdata;
	private static VO_BO_DATA vobodata;
	private static VO_BW_DATA vobwdata;
	private static VO_Temp_DATA votempdata;
	public static Context context;
	private TelephonyManager telephonyManager;
	public static String IMEI;
	private String Gkey = "AAAAAAAAZZZZZZZZZZZZ999999999";
	private String GkeyId = "1";
	private static ArrayList<VO_BP_DATA> vo_801203;
	private static ArrayList<VO_BG_DATA> vo_801204;
	private static ArrayList<VO_BO_DATA> vo_801205;
	private static ArrayList<VO_BW_DATA> vo_801206;
	private static ArrayList<VO_Temp_DATA> vo_801207;
	
	@SuppressWarnings("static-access")
	public void LoData(final Context context, String type) {
		this.context = context;
		dao = new MeasureDAO();

		CreateDate = ChangeDateFormat.CreateDate();
		TimeStamp = ChangeDateFormat.TimeStamp();

		telephonyManager = (TelephonyManager)context.getSystemService(context.TELEPHONY_SERVICE);
		IMEI = telephonyManager.getDeviceId();
		
		if (json == null)
			json = new MeasureJsonFormat();
		if (fn == null)
			fn = new MeasureFieldName();

		ESettings_type mSetting_type = ESettings_type.ByStr(type);

		switch (mSetting_type) {
		case type_0x36:
			vo_801203 = dao.Get_BP_MeasurementRecord(context);
			if (vo_801203 != null) {
				for (int i = 0; i < vo_801203.size(); i++) {
					vobpdata = vo_801203.get(i);
				}
				
				DataJosn = json.jsonformat(type_0x36,  
										   vobpdata.getAccount(), 
										   vobpdata.getAFIB(),        
										   vobpdata.getAM(), 
										   vobpdata.getARR(),  
										   vobpdata.getBS_TIME(), 
										   ChangeDateFormat.CreateDate(), 
										   vobpdata.getDATA_TYPE(),
										   vobpdata.getDIAGNOSTIC_MODE(), 
										   vobpdata.getHIGH_PRESSURE(), 
										   vobpdata.getLOW_PRESSURE(),
										   vobpdata.getMODEL(), 
										   vobpdata.getPLUS(), 
										   vobpdata.getPM(), 
										   vobpdata.getRES(), 
										   vobpdata.getUSUAL_MODE(),
										   vobpdata.getYEAR());
										 
				URL = HTTP+ Controller.Check_IP()+SERVER_HOST_0x36;
				TYPE = type_0x36;
				DataHash = encrypt(Gkey, TimeStamp, DataJosn);			 
				ValuePair =	fn.NameValuePair(DataHash, GkeyId, TimeStamp, DataJosn);
				PostData();
				Log.e(TYPE, "上傳血壓記錄");
			}
			else{
				Log.e(TYPE, "血壓記錄上傳完畢");
			}
			break;
		case type_0x37:
			vo_801204 = dao.Get_BG_MeasurementRecord(context);
			if (vo_801204 != null) {
				for (int i = 0; i < vo_801204.size(); i++) {
					vobgdata = vo_801204.get(i);
				}
//				new	
				DataJosn = json.jsonformat(type_0x37,  
										   vobgdata.getAccount(),
//										   "799999999999999",
										   vobgdata.getUnit(),        
										   vobgdata.getGlu(), 
										   vobgdata.getBsTime(),  
										   vobgdata.getHemoglobin(), 														  
										   vobgdata.getBloodFlowVelocity());
//				old						 
//				DataJosn = json.jsonformat(type_0x37,  
//										   vobgdata.getAccount(), 
//										   vobgdata.getUnit(),        
//										   vobgdata.getGlu(), 
//										   ChangeDateFormat.CreateDate(),  
//										   "1", 														  
//										   ChangeDateFormat.CreateDate());
				
				URL = HTTP+Controller.Check_IP()+SERVER_HOST_0x37;
				TYPE = type_0x37;
				DataHash = encrypt(Gkey, TimeStamp, DataJosn);			 
				ValuePair =	fn.NameValuePair(DataHash, GkeyId, TimeStamp, DataJosn);
				PostData();
				Log.e(TYPE, "上傳血糖記錄");
			}
			else{
				Log.e(TYPE, "血糖記錄上傳完畢");
			}
			break;
		case type_0x38:
			vo_801205 = dao.Get_BO_MeasurementRecord(context);
			if (vo_801205 != null) {
				for (int i = 0; i < vo_801205.size(); i++) {
					vobodata = vo_801205.get(i);
				}
									
				DataJosn = json.jsonformat(type_0x38,  
										   vobodata.getAccount(), 
										   vobodata.getOXY_TIME(),        
										   vobodata.getOXY_COUNT(), 
										   vobodata.getPLUS(),  
										   vobodata.getSPO2(), 										  
										   ChangeDateFormat.CreateDate());
										 
				URL = HTTP+Controller.Check_IP()+SERVER_HOST_0x38;
				TYPE = type_0x38;
				DataHash = encrypt(Gkey, TimeStamp, DataJosn);			 
				ValuePair =	fn.NameValuePair(DataHash, GkeyId, TimeStamp, DataJosn);
				PostData();
				Log.e(TYPE, "上傳血氧記錄");
			}
			else{
				Log.e(TYPE, "血氧記錄上傳完畢");
			}
			break;
			
		case type_0x39:
			vo_801206 = dao.Get_BW_MeasurementRecord(context);
			if (vo_801206 != null) {
				for (int i = 0; i < vo_801206.size(); i++) {
					vobwdata = vo_801206.get(i);
				}
									
				DataJosn = json.jsonformat(type_0x39,  
										   vobwdata.getAccount(), 
										   vobwdata.getBFTime(),        
										   vobwdata.getHeight(), 
										   vobwdata.getSex(),  
										   vobwdata.getUnit(), 		
										   vobwdata.getBw(), 
										   vobwdata.getBmi(), 
										   vobwdata.getRecycle(), 
										   vobwdata.getOrganFat(), 
										   vobwdata.getBone(), 
										   vobwdata.getBirthYear(), 
										   vobwdata.getBirthMonth(), 
										   vobwdata.getBirthDay(), 
										   vobwdata.getAge(), 
										   vobwdata.getMuscle(), 
										   vobwdata.getResister(), 
										   vobwdata.getAqua(), 
										   ChangeDateFormat.CreateDate());
										 
				URL = HTTP+Controller.Check_IP()+SERVER_HOST_0x39;
				TYPE = type_0x39;
				DataHash = encrypt(Gkey, TimeStamp, DataJosn);			 
				ValuePair =	fn.NameValuePair(DataHash, GkeyId, TimeStamp, DataJosn);
				PostData();
				Log.e(TYPE, "上傳體重記錄");
			}
			else{
				Log.e(TYPE, "體重記錄上傳完畢");
			}
			break;
			
		case type_0x3A:
			vo_801207 = dao.Get_TEMP_MeasurementRecord(context);
			if (vo_801207 != null) {
				for (int i = 0; i < vo_801207.size(); i++) {
					votempdata = vo_801207.get(i);
				}
									
				DataJosn = json.jsonformat(type_0x3A,  
							votempdata.getAccount(), 
							votempdata.getBS_TIME(),        
							votempdata.getTYPE_T(), 
							votempdata.getUINT(),  
							votempdata.getTEMP(), 										  
							ChangeDateFormat.CreateDate());
										 
				URL = HTTP+Controller.Check_IP()+SERVER_HOST_0x3A;
				TYPE = type_0x3A;
				DataHash = encrypt(Gkey, TimeStamp, DataJosn);			 
				ValuePair =	fn.NameValuePair(DataHash, GkeyId, TimeStamp, DataJosn);
				PostData();
				Log.e(TYPE, "上傳耳溫記錄");
			}
			else{
				Log.e(TYPE, "耳溫記錄上傳完畢");
			}
			break;
		}
		
	}
	

	public static void delete0x36(){
		for (int i = 0; i < vo_801203.size(); i++) {
			vobpdata = vo_801203.get(i);
		}
		vo_801203.clear(); 
		MyApplication.cro.LoData(MyApplication.context, MeasureController.type_0x36);
	}
	
	public static void delete0x37(){
		for (int i = 0; i < vo_801204.size(); i++) {
			vobgdata = vo_801204.get(i);
		}
		vo_801204.clear(); 
		MyApplication.cro.LoData(MyApplication.context, MeasureController.type_0x37);
	}
	
	public static void delete0x38(){
		for (int i = 0; i < vo_801205.size(); i++) {
			vobodata = vo_801205.get(i);
		}
		vo_801205.clear(); 
		MyApplication.cro.LoData(MyApplication.context, MeasureController.type_0x38);
	}
	
	public static void delete0x39(){
		for (int i = 0; i < vo_801206.size(); i++) {
			vobwdata = vo_801206.get(i);
		}
		vo_801206.clear(); 
		MyApplication.cro.LoData(MyApplication.context, MeasureController.type_0x39);
	}
	
	public static void delete0x3A(){
		for (int i = 0; i < vo_801207.size(); i++) {
			votempdata = vo_801207.get(i);
		}
		vo_801207.clear(); 
		MyApplication.cro.LoData(MyApplication.context, MeasureController.type_0x3A);
	}
	
	private void PostData() {
		new Thread() {
			public void run() {
				try {
					Log.e("URL", URL);
					new MeasureHttpPostData(URL, TYPE, ValuePair, context);
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
    
    public static String encrypt2(String account){
		//String timeStamp = getTimesgetTimes(); //加密還不需%20取代空格 	 
    	MessageDigest shaCode = null;
    	try {
    		shaCode = MessageDigest.getInstance("SHA-256");
			shaCode.update(account.getBytes());
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
	
    public enum ESettings_type {
   	 type_0x36("0x36"),
   	 type_0x37("0x37"),
   	 type_0x38("0x38"),
   	type_0x39("0x39"),
   	type_0x3A("0x3A"),
   	 type_err ("");
   	 
   	private String type;
   	ESettings_type(String str){
   		 this.type = str;
   	 }
   	 public static ESettings_type ByStr(final String val)
   	    {
   	        for (ESettings_type type : ESettings_type.values())
   	        {
   	            if (type.type.equals(val))
   	            {
   	                return type;
   	            }
   	        }
   	        return type_err; //Never return null
   	    }
     }
}
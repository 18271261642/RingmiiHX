package hat.bemo.measure.set;

import android.annotation.SuppressLint;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class MeasureJsonFormat {
	@SuppressLint("SdCardPath")
	private JSONObject job, job2, job3, job4;
	private JSONArray ary, ary1, ary2;
	public String jsonformat(String type, String... prams){
		
		MeasureController.ESettings_type mSetting_type = MeasureController.ESettings_type.ByStr(type);
		
		switch(mSetting_type){		
			case type_0x36:
				try {
					job = new JSONObject();
					job.put("account", prams[0]);
					job.put("AFIB", prams[1]);
					job.put("AM", prams[2]);
					job.put("ARR", prams[3]);					 							 
					job.put("BS_TIME", prams[4]);
					job.put("CREATE_DATE", prams[5]);
					job.put("DATA_TYPE", prams[6]);	
					job.put("DIAGNOSTIC_MODE", prams[7]);
					job.put("HIGH_PRESSURE", prams[8]);
					job.put("LOW_PRESSURE", prams[9]);	
					job.put("MODEL", prams[10]);
					job.put("PLUS", prams[11]);
					job.put("PM", prams[12]);	
					job.put("RES", prams[13]);
					job.put("USUAL_MODE", prams[14]);
					job.put("YEAR", prams[15]);
				} catch (Exception e) {				 
					e.printStackTrace();
				}
			break;
			case type_0x37:
				try {
					job = new JSONObject();
					job2 = new JSONObject();
					ary = new JSONArray();
//					new
					job.put("account", prams[0]);
					job.put("unit", prams[1]);				
					job2.put("glu", prams[2]);
					job2.put("bsTime", prams[3]);	
					job2.put("hemoglobin", prams[4]);	
					job2.put("bloodFlowVelocity", prams[5]);	
					ary.put(job2);
					job.put("Data", ary);	
					
//					old
//					job.put("account", prams[0]);
//					job.put("UNIT", prams[1]);				
//					ary.put(prams[2]);
//					job.put("GLU", ary);	
//					job.put("BS_TIME", prams[3]);	
//					job.put("BS_COUNT", prams[4]);	
//					job.put("CREATE_DATE", prams[5]);	  
				} catch (Exception e) {
					e.printStackTrace();
				}	
			break;
			case type_0x38:			
				try {
					
					 job = new JSONObject();
					job2 = new JSONObject();
					ary = new JSONArray();
					job.put("account", prams[0]);
					job.put("OXY_TIME", prams[1]);
					job.put("OXY_COUNT", prams[2]);
					
					job2.put("PLUS", prams[3]);
					job2.put("SPO2", prams[4]);				
					ary.put(job2);
					job.put("DATA", ary);
					
					job.put("CREATE_DATE", prams[5]);
					 
					/*
					job = new JSONObject();
					job2 = new JSONObject();
					ary = new JSONArray();
					job.put("account", prams[0]);
					job.put("time", prams[1]);
					
					job2.put("heartbeat", prams[2]);
					job2.put("oxygen", prams[3]);				
					ary.put(job2);
					job.put("DATA", ary);
					*/
				} catch (Exception e) {
					e.printStackTrace();
				}			 
			break;
			
			case type_0x3A:
				try {
					job = new JSONObject();
					
					job.put("account", prams[0]);
					job.put("BS_TIME", prams[1]);	
					job.put("TYPE_T", prams[2]);
					job.put("UNIT", prams[3]);
					job.put("TEMP", prams[4]);
					job.put("CREATE_DATE", prams[5]);
					
					 
				} catch (Exception e) {
					e.printStackTrace();
				}	
			break;
			
			case type_0x39:
				try {
					job = new JSONObject();
					
					job.put("account", prams[0]);
					job.put("BF_TIME", prams[1]);	
					job.put("HEIGHT", prams[2]);
					job.put("SEX", prams[3]);
					job.put("UNIT", prams[4]);
					job.put("BW", prams[5]);
					job.put("BF_RATE", prams[6]);
					job.put("BMI", prams[7]);
					job.put("RECYCLE", prams[8]);
					job.put("ORGAN_FAT", prams[9]);
					job.put("BONE", prams[10]);
					job.put("BIRTH_YEAR", prams[11]);
					job.put("BIRTH_MONTH", prams[12]);
					job.put("BIRTH_DAY", prams[13]);
					job.put("AGE", prams[14]);
					job.put("MUSCLE", prams[15]);
					job.put("RESISTER", prams[16]);
					job.put("AQUA", prams[17]);
					job.put("CREATE_DATE", prams[18]);
					
					 
				} catch (Exception e) {
					e.printStackTrace();
				}	
			break;
		}
		Log.e("job", job.toString());	
		return job.toString();
	}
	

	public static int Size;
	public static void JsonSize(int size){
		 Size = size;
	}
	
	public static String[] split(String prams){
		String[] detail = prams.split(";");
		return detail;
	}
	
	public static boolean saveTxt(String type, String job, String filename, String path){  
	    //sd卡检测  
//		String sdStatus = Environment.getExternalStorageState();  
//		if(!sdStatus.equals(Environment.MEDIA_MOUNTED)){  
//		    Toast.makeText(context, "SD 卡不可用", Toast.LENGTH_SHORT).show();  
//		    return false;  
//		}  
		//检测文件夹是否存在  
		File file = new File(path);  
		file.exists();  
		file.mkdirs();  
		String p = path+File.separator+filename+".txt";  
		FileOutputStream outputStream = null;  
		try {  
		    //创建文件，并写入内容  
		    outputStream = new FileOutputStream(new File(p));  
		    String msg = new String(type+":"+job);  
		    outputStream.write(msg.getBytes("UTF-8"));  
		} catch (FileNotFoundException e) {  
		   e.printStackTrace();  
		   return false;  
		} catch (UnsupportedEncodingException e) {  
		   e.printStackTrace();  
		} catch (IOException e) {  
		   e.printStackTrace();  
		}finally{  
		   if(outputStream!=null){  
		       try {  
		           outputStream.flush();  
		       } catch (IOException e) {  
		           e.printStackTrace();  
		       }  
		       try {  
		           outputStream.close();  
		       } catch (IOException e) {  
		           e.printStackTrace();  
		       }  
		   }  
		}  
		return true;  
	}   
}
package hat.bemo.BlueTooth.blegatt.preferences;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesStatus {
	private static SharedPreferences sp; 
	public static final String PreferenceName = "gcare";
	
	@SuppressWarnings({ "static-access" })
	private static SharedPreferences getInstances(Context context){
		if(sp == null){
			sp = context.getSharedPreferences(PreferenceName, context.MODE_PRIVATE);
		}
		return sp;
	}
	
    public static void setAddress(Context context, String value) {
    	try{
    		getInstances(context).edit().putString("Address", value).commit();
    	}catch(Exception e){
    		e.printStackTrace();
    	}   				
	}

	public static String getAddress(Context context) {  
	     return getInstances(context).getString("Address", "");   
    }
}
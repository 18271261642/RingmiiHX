package hat.bemo.measure.set;

import android.content.Context;
import android.content.SharedPreferences;

public class Measure_SharedPreferences {	 
    private static SharedPreferences sp2; 
	public static final String PreferenceName = "Gcare800";
	
	@SuppressWarnings("static-access")
	private static SharedPreferences getInstances(Context context){
		if(sp2 == null){
			sp2 = context.getSharedPreferences(PreferenceName, context.MODE_PRIVATE);
		}
		return sp2;
	}

    public static void setValue_Page1(Context context, int value) {
    	try{
    		getInstances(context).edit().putInt("Page1", value).commit();
    	}catch(Exception e){
    		e.printStackTrace();
    	}   				
	}

	public static int getValue_Page1(Context context) {  
	     return getInstances(context).getInt("Page1", 4);   
    }
	
	public static void setValue_Page2(Context context, int value) {
		try {
			getInstances(context).edit().putInt("Page2", value).commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static int getValue_Page2(Context context) {
		return getInstances(context).getInt("Page2", 3);
	}
	
	public static void setValue_Page3(Context context, int value) {
		try {
			getInstances(context).edit().putInt("Page3", value).commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static int getValue_Page3(Context context) {
		return getInstances(context).getInt("Page3", 2);
	}
	
	public static void setValue_Page4(Context context, int value) {
		try {
			getInstances(context).edit().putInt("Page4", value).commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static int getValue_Page4(Context context) {
		return getInstances(context).getInt("Page4", 2);
	}
	
	public static void setValue_Page5(Context context, int value) {
		try {
			getInstances(context).edit().putInt("Page5", value).commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static int getValue_Page5(Context context) {
		return getInstances(context).getInt("Page5", 2);
	}
	
	public static void setValue_Setting(Context context, int value) {
		try {
			getInstances(context).edit().putInt("Setting", value).commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 0 --> 已設定 
	 * , 1 --> 初次設定
	 **/
	public static int getValue_Setting(Context context) {
		return getInstances(context).getInt("Setting", 1);
	}
}
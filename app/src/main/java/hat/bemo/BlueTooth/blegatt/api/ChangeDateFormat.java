package hat.bemo.BlueTooth.blegatt.api;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ChangeDateFormat {
	
	@SuppressLint("SimpleDateFormat")
	public static String CreateDate(){		
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS 'GMT'Z");			 				 
		String format_Date = dateformat.format(new Date());		 		 	
		return format_Date;		 
	}
	
	@SuppressLint("SimpleDateFormat")
	public static String BsTime(){		
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");			 				 
		String format_Date = dateformat.format(new Date());		 		 	
		return format_Date;		 
	}
	
	@SuppressLint("SimpleDateFormat")
	public static String UpdataDate(){		
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm");			 				 
		String format_Date = dateformat.format(new Date());		 		 	
		return format_Date;		 
	}
	
	@SuppressLint("SimpleDateFormat")
	public static String getYear(){		
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyy");			 				 
		String format_Date = dateformat.format(new Date());		 		 	
		return format_Date;		 
	}
	
	@SuppressLint("SimpleDateFormat")
	public static String getMM(){		
		SimpleDateFormat dateformat = new SimpleDateFormat("MM");			 				 
		String format_Date = dateformat.format(new Date());		 		 	
		return format_Date;		 
	}
	
	@SuppressLint("SimpleDateFormat")
	public static String getDD(){		
		SimpleDateFormat dateformat = new SimpleDateFormat("dd");			 				 
		String format_Date = dateformat.format(new Date());		 		 	
		return format_Date;		 
	}
	
	@SuppressLint("SimpleDateFormat")
	public static String getHH(){		
		SimpleDateFormat dateformat = new SimpleDateFormat("HH");			 				 
		String format_Date = dateformat.format(new Date());		 		 	
		return format_Date;		 
	}
	
	@SuppressLint("SimpleDateFormat")
	public static String getmm(){		
		SimpleDateFormat dateformat = new SimpleDateFormat("mm");			 				 
		String format_Date = dateformat.format(new Date());		 		 	
		return format_Date;		 
	}
	
	@SuppressLint("SimpleDateFormat")
	public static String getss(){		
		SimpleDateFormat dateformat = new SimpleDateFormat("ss");			 				 
		String format_Date = dateformat.format(new Date());		 		 	
		return format_Date;		 
	}
	
	@SuppressLint("SimpleDateFormat")
	public static String TimeStamp(){				 		 			 		 		 	
		return String.valueOf(new Date().getTime());		 
	}

	@SuppressLint("SimpleDateFormat")
	public static Date getDatebirt(String dateString){		
		try {
			SimpleDateFormat dateformat = null; 
			dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS 'GMT'Z");	
			Date date = dateformat.parse(dateString);
			return date;	
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;	
	}
}
package hat.bemo.measure.setting_db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class Update {
	private Measure_Create_Table dbtc = null;
	private SQLiteDatabase db = null;
	
	public int up_BP(Context context, String ITEMNO[], String DEVICE_NAME, String CREATE_DATE, String SWITCH){	
		dbtc = new Measure_Create_Table(context);
		db = dbtc.getWritableDatabase();
	    ContentValues cv = new ContentValues();  
	    cv.put(TABLE_BP.DEVICE_NAME, DEVICE_NAME);
	    cv.put(TABLE_BP.CREATE_DATE, CREATE_DATE);
	    cv.put(TABLE_BP.SWITCH, SWITCH);
	    
	    int update = db.update(TABLE_BP.TABLE_NAME, cv, TABLE_BP.ITEMNO+ "= ?", ITEMNO);
	    return update;   
	}
	
	public int up_BO(Context context, String ITEMNO[], String DEVICE_NAME, String CREATE_DATE, String SWITCH){	
		dbtc = new Measure_Create_Table(context);
		db = dbtc.getWritableDatabase();
	    ContentValues cv = new ContentValues();  
	    cv.put(TABLE_BO.DEVICE_NAME, DEVICE_NAME);
	    cv.put(TABLE_BO.CREATE_DATE, CREATE_DATE);
	    cv.put(TABLE_BO.SWITCH, SWITCH);
	    
	    int update = db.update(TABLE_BO.TABLE_NAME, cv, TABLE_BO.ITEMNO+ "= ?", ITEMNO);
	    return update;   
	}
	
	public int up_BG(Context context, String ITEMNO[], String DEVICE_NAME, String CREATE_DATE, String SWITCH){	
		dbtc = new Measure_Create_Table(context);
		db = dbtc.getWritableDatabase();
	    ContentValues cv = new ContentValues();  
	    cv.put(TABLE_BG.DEVICE_NAME, DEVICE_NAME);
	    cv.put(TABLE_BG.CREATE_DATE, CREATE_DATE);
	    cv.put(TABLE_BG.SWITCH, SWITCH);
	    
	    int update = db.update(TABLE_BG.TABLE_NAME, cv, TABLE_BG.ITEMNO+ "= ?", ITEMNO);
	    return update;   
	}
	
	public int up_WT(Context context, String ITEMNO[], String DEVICE_NAME, String CREATE_DATE, String SWITCH){	
		dbtc = new Measure_Create_Table(context);
		db = dbtc.getWritableDatabase();
	    ContentValues cv = new ContentValues();  
	    cv.put(TABLE_WT.DEVICE_NAME, DEVICE_NAME);
	    cv.put(TABLE_WT.CREATE_DATE, CREATE_DATE);
	    cv.put(TABLE_WT.SWITCH, SWITCH);
	    
	    int update = db.update(TABLE_WT.TABLE_NAME, cv, TABLE_WT.ITEMNO+ "= ?", ITEMNO);
	    return update;   
	}
}
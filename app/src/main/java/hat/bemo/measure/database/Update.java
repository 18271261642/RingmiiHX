package hat.bemo.measure.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class Update {
	private MeasureCreateTable dbtc = null;
	private SQLiteDatabase db = null;
	
	public int updateMeasureDevice(Context context, String itemno[], String device, String createDate){	
		dbtc = new MeasureCreateTable(context);
		db = dbtc.getWritableDatabase();
	    ContentValues cv = new ContentValues();  
	    cv.put(MeasureDevice.device, device);
	    cv.put(MeasureDevice.createDate, createDate);
	    
	    int update = db.update(MeasureDevice.TABLE_NAME, cv, MeasureDevice.itemno+ "= ?", itemno);
	    return update;   
	}
	
	public int updateMeasureRecord(Context context, String itemno[], String measureType, String value ,String createDate){	
		dbtc = new MeasureCreateTable(context);
		db = dbtc.getWritableDatabase();
	    ContentValues cv = new ContentValues();  
	    cv.put(MeasureRecord.measureType, measureType);
	    cv.put(MeasureRecord.value, value);
	    cv.put(MeasureRecord.createDate, createDate);
	    
	    int update = db.update(MeasureRecord.TABLE_NAME, cv, MeasureRecord.itemno+ "= ?", itemno);
	    return update;   
	}
}
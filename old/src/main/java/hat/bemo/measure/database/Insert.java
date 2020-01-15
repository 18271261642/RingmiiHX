package hat.bemo.measure.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class Insert {
	private MeasureCreateTable dbtc = null;
	private SQLiteDatabase db = null;
	
	public long insertMeasureRecord(Context context, VOMeasureRecord vo){	
		dbtc = new MeasureCreateTable(context);
		db = dbtc.getWritableDatabase();
		
		ContentValues values = new ContentValues();				
		values.put(MeasureRecord.measureType, vo.getMeasureType());
		values.put(MeasureRecord.value, vo.getValue());
		values.put(MeasureRecord.createDate, vo.getCreateDate());
		
		long insert = db.insert(MeasureRecord.TABLE_NAME, null, values);
		
        db.close();
        return insert;   		
	}

	public long insertMeasureDevice(Context context, VOMeasureDevice vo){	
		dbtc = new MeasureCreateTable(context);
		db = dbtc.getWritableDatabase();
		
		ContentValues values = new ContentValues();				
		values.put(MeasureDevice.device, vo.getDevice());
		values.put(MeasureDevice.createDate, vo.getCreateDate());
		 
		long insert = db.insert(MeasureDevice.TABLE_NAME, null, values);
		
        db.close();
        return insert;   		
	}
}
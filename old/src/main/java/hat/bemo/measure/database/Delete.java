package hat.bemo.measure.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class Delete {
	private MeasureCreateTable dbtc = null;
	private SQLiteDatabase db = null;
	
	public int deleteMeasureRecord(Context context, String Itemno){
		dbtc = new MeasureCreateTable(context);
		db = dbtc.getWritableDatabase();
        int delete = db.delete(MeasureRecord.TABLE_NAME, MeasureRecord.itemno + "=" + Itemno,null);
        db.close();
        return delete;   
	}
	
	public int deleteMeasureDevice(Context context, String Itemno){
		dbtc = new MeasureCreateTable(context);
		db = dbtc.getWritableDatabase();
        int delete = db.delete(MeasureDevice.TABLE_NAME, MeasureDevice.itemno + "=" + Itemno,null);
        db.close();
        return delete;   
	}
}
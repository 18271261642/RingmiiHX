package hat.bemo.measure.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import hat.bemo.DataBase.Create_Table;
import hat.bemo.measure.Table.Blood_Table;

public class Blood_Delete {
	private Create_Table dbtc = null;
	private SQLiteDatabase db = null;
	
	public int Delete_Time(Context context, String imei){
		
		dbtc = new Create_Table(context);
		db = dbtc.getWritableDatabase();
	    int delete = db.delete(Blood_Table.TABLE_NAME, Blood_Table.IMEI + "=" + imei, null);
	    db.close();
	    return delete;   		
	}
}

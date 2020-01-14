package hat.bemo.measure.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import hat.bemo.DataBase.Create_Table;
import hat.bemo.measure.Table.Blood_Table;


public class Blood_Update {
	private Create_Table dbtc = null;
	private SQLiteDatabase db = null;
	
public int update_Blood(Context context, String create_date[], String h_pressure, String l_pressure,
							   String plus, String bs_time, String date, String imei){
		System.out.println("UpDate");
		dbtc = new Create_Table(context);
		db = dbtc.getWritableDatabase();
        ContentValues cv = new ContentValues();  
        cv.put(Blood_Table.H_PRESSURE, h_pressure);
        cv.put(Blood_Table.L_PRESSURE, l_pressure);
        cv.put(Blood_Table.PLUS, plus);
        cv.put(Blood_Table.BS_TIME, bs_time);
//        cv.put(Blood_Table.CREATE_DATE, date);
        cv.put(Blood_Table.IMEI, imei);
        
        int update = db.update(Blood_Table.TABLE_NAME, cv, Blood_Table.CREATE_DATE+ "= ?", create_date);
        return update;   
		
	}
}

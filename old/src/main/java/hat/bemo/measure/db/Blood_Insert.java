package hat.bemo.measure.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import hat.bemo.DataBase.Create_Table;
import hat.bemo.measure.Table.Blood_Table;
import hat.bemo.measure.VO.BloodVO;


public class Blood_Insert {
	
	private Create_Table dbtc = null;
	private SQLiteDatabase db = null;
	private static Context context;
	
	public Blood_Insert(Context context){
		this.context = context;
	}
	
	public long insert_Blood(BloodVO bloodVO){
			
			dbtc = new Create_Table(context);
			db = dbtc.getWritableDatabase();
			
			ContentValues values = new ContentValues();		
			values.put(Blood_Table.H_PRESSURE, bloodVO.getH_PRESSURE());
			values.put(Blood_Table.L_PRESSURE, bloodVO.getL_PRESSURE());
			values.put(Blood_Table.PLUS, bloodVO.getPLUS());		
			values.put(Blood_Table.BS_TIME, bloodVO.getBS_TIME());				
			values.put(Blood_Table.CREATE_DATE, bloodVO.getCREATE_DATE());
			values.put(Blood_Table.UPDATE_DATE, bloodVO.getUPDATE_DATE());
			values.put(Blood_Table.DATETIME, bloodVO.getDATETIME());
			values.put(Blood_Table.BLOOD_NO, bloodVO.getBLOOD_NO());
			values.put(Blood_Table.IMEI, bloodVO.getIMEI());
			
			long insert = db.insert(Blood_Table.TABLE_NAME, null, values);
	        db.close();
	        return insert;   		
	}
}
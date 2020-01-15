package hat.bemo.measure.DAO;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import hat.bemo.DataBase.Create_Table;
import hat.bemo.measure.Table.Blood_Table;
import hat.bemo.measure.VO.BloodVO;

public class Blood_DAO {
	private Create_Table dbtc = null;
	private SQLiteDatabase db = null;
	
	public ArrayList<BloodVO> getBlood(Context context){
		dbtc = new Create_Table(context);
		db = dbtc.getWritableDatabase();
		Cursor c=null;
		try {
			c = db.query(Blood_Table.TABLE_NAME,
					null,	// select * 
					null, 
					null, 
					null, 
					null, 
					null
					);
			
			if(c.moveToFirst()){
				
				ArrayList<BloodVO> list = new ArrayList<BloodVO>();
				do{
					BloodVO data = new BloodVO();
					data.setH_PRESSURE(c.getString(c.getColumnIndex(Blood_Table.H_PRESSURE)));
					data.setL_PRESSURE(c.getString(c.getColumnIndex(Blood_Table.L_PRESSURE)));
					data.setPLUS(c.getString(c.getColumnIndex(Blood_Table.PLUS)));
					data.setBS_TIME(c.getString(c.getColumnIndex(Blood_Table.BS_TIME)));
					data.setCREATE_DATE(c.getString(c.getColumnIndex(Blood_Table.CREATE_DATE)));
					data.setDATETIME(c.getString(c.getColumnIndex(Blood_Table.DATETIME)));
					data.setBLOOD_NO(c.getString(c.getColumnIndex(Blood_Table.BLOOD_NO)));
					data.setIMEI(c.getString(c.getColumnIndex(Blood_Table.IMEI)));
					list.add(data);
	
				}while(c.moveToNext());
				return list;
			}
			
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}finally{
			if(c!=null)
				c.close();
				db.close();
		}
	}
}

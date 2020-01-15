package hat.bemo.measure.setting_db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class Delete {
	private Measure_Create_Table dbtc = null;
	private SQLiteDatabase db = null;
	
	public int Delete_BP(Context context, String Itemno){
		dbtc = new Measure_Create_Table(context);
		db = dbtc.getWritableDatabase();
        int delete = db.delete(TABLE_BP.TABLE_NAME, TABLE_BP.ITEMNO + "=" + Itemno,null);
        db.close();
        return delete;   
	}
	
	public int Delete_BO(Context context, String Itemno){
		dbtc = new Measure_Create_Table(context);
		db = dbtc.getWritableDatabase();
        int delete = db.delete(TABLE_BO.TABLE_NAME, TABLE_BO.ITEMNO + "=" + Itemno,null);
        db.close();
        return delete;   
	}
	
	public int Delete_BG(Context context, String Itemno){
		dbtc = new Measure_Create_Table(context);
		db = dbtc.getWritableDatabase();
        int delete = db.delete(TABLE_BG.TABLE_NAME, TABLE_BG.ITEMNO + "=" + Itemno,null);
        db.close();
        return delete;   
	}
	
	public int Delete_WT(Context context, String Itemno){
		dbtc = new Measure_Create_Table(context);
		db = dbtc.getWritableDatabase();
        int delete = db.delete(TABLE_WT.TABLE_NAME, TABLE_WT.ITEMNO + "=" + Itemno,null);
        db.close();
        return delete;   
	}
	
	public int Delete_BP_DATA(Context context, String Itemno){
		dbtc = new Measure_Create_Table(context);
		db = dbtc.getWritableDatabase();
        int delete = db.delete(TABLE_BP_DATA.TABLE_NAME, TABLE_BP_DATA.ITEMNO + "=" + Itemno,null);
        db.close();
        return delete;   
	}
	
	public int Delete_BO_DATA(Context context, String Itemno){
		dbtc = new Measure_Create_Table(context);
		db = dbtc.getWritableDatabase();
        int delete = db.delete(TABLE_BO_DATA.TABLE_NAME, TABLE_BO_DATA.ITEMNO + "=" + Itemno,null);
        db.close();
        return delete;   
	}
	
	public int Delete_BG_DATA(Context context, String Itemno){
		dbtc = new Measure_Create_Table(context);
		db = dbtc.getWritableDatabase();
        int delete = db.delete(TABLE_BG_DATA.TABLE_NAME, TABLE_BG_DATA.ITEMNO + "=" + Itemno,null);
        db.close();
        return delete;   
	}
	
	public int deleteHRDataSheet(Context context, String HRDataNo){
		dbtc = new Measure_Create_Table(context);
		db = dbtc.getWritableDatabase();
        int delete = db.delete(HRDataSheet.TABLE_NAME, HRDataSheet.HRDataNumber + "=" + HRDataNo,null);
        db.close();
        return delete;   
	}
}
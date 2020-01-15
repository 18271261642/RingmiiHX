package hat.bemo.measure.setting_db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

import hat.bemo.measure.service.BluetoothBaseActivity;
import hat.bemo.measure.set.Command;

public class Measure_Create_Table extends SQLiteOpenHelper{
	private static final String DB_NAME = "Measure"; 
	private static final int DB_VERSION =  1003;
	
	public Measure_Create_Table(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}
		 
	public int getDbVersion(){
		return DB_VERSION;
	}
	
	public Measure_Create_Table(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(TABLE_BP.SQL_CREATE_TABLE);
		db.execSQL(TABLE_BO.SQL_CREATE_TABLE);
		db.execSQL(TABLE_BG.SQL_CREATE_TABLE);
		db.execSQL(TABLE_WT.SQL_CREATE_TABLE);
		db.execSQL(HRDataSheet.SQL_CREATE_TABLE);
		
		db.execSQL(TABLE_BP_DATA.SQL_CREATE_TABLE);
		db.execSQL(TABLE_BO_DATA.SQL_CREATE_TABLE);
		db.execSQL(TABLE_BG_DATA.SQL_CREATE_TABLE);	
		try{
//			TABLE_BP(db);
//			TABLE_BO(db);	
//			TABLE_BG(db);	
//			TABLE_WT(db);	
//			setHRDataSheet(db);
		}catch(SQLException e){
				db.execSQL("DROP TABLE IF EXISTS "+ TABLE_BP.SQL_CREATE_TABLE);	
				db.execSQL("DROP TABLE IF EXISTS "+ TABLE_BO.SQL_CREATE_TABLE);		
				db.execSQL("DROP TABLE IF EXISTS "+ TABLE_BG.SQL_CREATE_TABLE);		
				db.execSQL("DROP TABLE IF EXISTS "+ TABLE_WT.SQL_CREATE_TABLE);		
				db.execSQL("DROP TABLE IF EXISTS "+ TABLE_BP_DATA.SQL_CREATE_TABLE);		
				db.execSQL("DROP TABLE IF EXISTS "+ TABLE_BO_DATA.SQL_CREATE_TABLE);
				db.execSQL("DROP TABLE IF EXISTS "+ TABLE_BG_DATA.SQL_CREATE_TABLE);	
				Log.e("SQLite","SQLite error");
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		Log.e("", "oldVersion:"+oldVersion+"---->"+"newVersion:"+newVersion);
		/**
	     * GC850.CN.GAP.V03.00.04
	     * DB- 1 > 1001
	     * Insert TABLE_BP_DATA 
	     */	
		if(oldVersion == 1 & newVersion == 1001){
			if(!(tabIsExist(db, TABLE_BP_DATA.TABLE_NAME))){ 
				db.execSQL(TABLE_BP_DATA.SQL_CREATE_TABLE);	
			}			
		}
		/**
	     * GC850.EP.GAP.V05.00.11
	     * DB- 1001 > 1002
	     * Insert TABLE_BO_DATA 
	     * Insert TABLE_BG_DATA 
	     */	
		else if(oldVersion == 1001 & newVersion == 1002){
			if(!(tabIsExist(db, TABLE_BO_DATA.TABLE_NAME))){ 
				db.execSQL(TABLE_BO_DATA.SQL_CREATE_TABLE);	
			}
			if(!(tabIsExist(db, TABLE_BG_DATA.TABLE_NAME))){ 
				db.execSQL(TABLE_BG_DATA.SQL_CREATE_TABLE);	
			}
		}
		/**
	     * GC850.CN.GAP.V03.00.09
	     * DB- 1002 > 1003
	     * Insert HRDataSheet 	      
	     */	
		else if(oldVersion == 1002 & newVersion == 1003){
			if(!(tabIsExist(db, HRDataSheet.TABLE_NAME))){ 
				db.execSQL(HRDataSheet.SQL_CREATE_TABLE);	
			}			 
		}
	}
	
	/**
     * 檢查資料表是否存在
     * @param tabName 表名
     * @return
     */
	public boolean tabIsExist(SQLiteDatabase db, String tabName) {
		boolean result = false;
		Cursor cursor = null;
		try {
			String sql = "select * from Sqlite_master where type ='table' and name ='"+ tabName + "'";
			cursor = db.rawQuery(sql, null);
			if (cursor.moveToNext()) {
				int count = cursor.getInt(0);
				if(count > 0){
                    result = true;
				}
				result = true;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return result;
	}
	
	@SuppressLint("SimpleDateFormat")
	private void TABLE_BP(SQLiteDatabase db){
		ContentValues[] values = new ContentValues[2];
		System.out.println("table_create");
		for(int i=0; i<values.length; i++)
			values[i] = new ContentValues();		
			values[0].put(TABLE_BP.DEVICE_NAME, "Taidoc-Device1"+BluetoothBaseActivity.relation+"00:12:A1:B0:8A:F8");
			values[0].put(TABLE_BP.SWITCH, "OFF");
			values[0].put(TABLE_BP.CREATE_DATE, 
						  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS 'GMT'Z").format(new Date()));
			
			values[1].put(TABLE_BP.DEVICE_NAME, "Taidoc-Device2"+BluetoothBaseActivity.relation+"00:12:A1:B0:8A:F9");
			values[1].put(TABLE_BP.SWITCH, "OFF");
			values[1].put(TABLE_BP.CREATE_DATE, 
						  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS 'GMT'Z").format(new Date()));	
			
		for(ContentValues row : values){
			db.insert(TABLE_BP.TABLE_NAME, null, row);
		}	
	}
	
	@SuppressLint("SimpleDateFormat")
	private void TABLE_BO(SQLiteDatabase db){
		ContentValues[] values = new ContentValues[2];
		System.out.println("table_create");
		for(int i=0; i<values.length; i++)
			values[i] = new ContentValues();		
			values[0].put(TABLE_BO.DEVICE_NAME, "Taidoc-Device1"+BluetoothBaseActivity.relation+"00:12:A1:B0:8A:F8");
			values[0].put(TABLE_BO.SWITCH, "OFF");
			values[0].put(TABLE_BO.CREATE_DATE, 
						  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS 'GMT'Z").format(new Date()));
			
			values[1].put(TABLE_BO.DEVICE_NAME, "Taidoc-Device2"+BluetoothBaseActivity.relation+"00:12:A1:B0:8A:F9");
			values[1].put(TABLE_BO.SWITCH, "OFF");
			values[1].put(TABLE_BO.CREATE_DATE, 
						  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS 'GMT'Z").format(new Date()));	
			
		for(ContentValues row : values){
			db.insert(TABLE_BO.TABLE_NAME, null, row);
		}	
	}
	
	@SuppressLint("SimpleDateFormat")
	private void TABLE_BG(SQLiteDatabase db){
		ContentValues[] values = new ContentValues[2];
		System.out.println("table_create");
		for(int i=0; i<values.length; i++)
			values[i] = new ContentValues();		
			values[0].put(TABLE_BG.DEVICE_NAME, Command.DEVICENAME.ITONDM+BluetoothBaseActivity.relation+"00:12:A1:B0:8A:F8");
			values[0].put(TABLE_BG.SWITCH, "ON");
			values[0].put(TABLE_BG.CREATE_DATE, 
						  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS 'GMT'Z").format(new Date()));
			
			values[1].put(TABLE_BG.DEVICE_NAME, "Taidoc-Device2"+BluetoothBaseActivity.relation+"00:12:A1:B0:8A:F9");
			values[1].put(TABLE_BG.SWITCH, "OFF");
			values[1].put(TABLE_BG.CREATE_DATE, 
						  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS 'GMT'Z").format(new Date()));	
			
		for(ContentValues row : values){
			db.insert(TABLE_BG.TABLE_NAME, null, row);
		}	
	}
	
	@SuppressLint("SimpleDateFormat")
	private void TABLE_WT(SQLiteDatabase db){
		ContentValues[] values = new ContentValues[2];
		System.out.println("table_create");
		for(int i=0; i<values.length; i++)
			values[i] = new ContentValues();		
			values[0].put(TABLE_WT.DEVICE_NAME, "Taidoc-Device1"+BluetoothBaseActivity.relation+"00:12:A1:B0:8A:F8");
			values[0].put(TABLE_WT.SWITCH, "OFF");
			values[0].put(TABLE_WT.CREATE_DATE, 
						  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS 'GMT'Z").format(new Date()));
			
			values[1].put(TABLE_WT.DEVICE_NAME, "Taidoc-Device2"+BluetoothBaseActivity.relation+"00:12:A1:B0:8A:F9");
			values[1].put(TABLE_WT.SWITCH, "OFF");
			values[1].put(TABLE_WT.CREATE_DATE, 
						  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS 'GMT'Z").format(new Date()));	
			
		for(ContentValues row : values){
			db.insert(TABLE_WT.TABLE_NAME, null, row);
		}	
	}
	
	@SuppressLint("SimpleDateFormat")
	private void setHRDataSheet(SQLiteDatabase db){
		ContentValues[] values = new ContentValues[1];
		for(int i=0; i<values.length; i++)
			values[i] = new ContentValues();
			values[0].put(HRDataSheet.HeartRate, "1");
			values[0].put(HRDataSheet.PrValue, "2");
			values[0].put(HRDataSheet.RelaxDegree, "3");
			values[0].put(HRDataSheet.BreaThing, "4");
			values[0].put(HRDataSheet.Heartage, "5");			
			values[0].put(HRDataSheet.FiveHa, "6");
			values[0].put(HRDataSheet.BsTime, "7");
			values[0].put(HRDataSheet.CreateDate, "8");
			values[0].put(HRDataSheet.RowData, "9");
			values[0].put(HRDataSheet.CreateDateDetails, "10");
			values[0].put(HRDataSheet.DetailNo, "11");				
		for(ContentValues row : values){
			db.insert(HRDataSheet.TABLE_NAME, null, row);
		}	
	}
}
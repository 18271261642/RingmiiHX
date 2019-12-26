package hat.bemo.measure.database;

import java.text.SimpleDateFormat;
import java.util.Date;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

@SuppressLint("SimpleDateFormat")
public class MeasureCreateTable extends SQLiteOpenHelper{
	private static final String DB_NAME = "Measure"; 
	private static final int DB_VERSION =  1001;
	
	public MeasureCreateTable(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}
		 
	public int getDbVersion(){
		return DB_VERSION;
	}
	
	public MeasureCreateTable(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(MeasureDevice.SQL_CREATE_TABLE);		
		db.execSQL(MeasureRecord.SQL_CREATE_TABLE);		
		try{
//			measureRecordFalseData(db);
//			measureDeviceFalseData(db);
		}catch(SQLException e){
				db.execSQL("DROP TABLE IF EXISTS "+ MeasureDevice.SQL_CREATE_TABLE);		
				db.execSQL("DROP TABLE IF EXISTS "+ MeasureRecord.SQL_CREATE_TABLE);		
				Log.e("SQLite","SQLite error");
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		Log.e("", "oldVersion:"+oldVersion+"---->"+"newVersion:"+newVersion);
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
	private void measureRecordFalseData(SQLiteDatabase db){
		ContentValues[] values = new ContentValues[2];
		System.out.println("table_create");
		for(int i=0; i<values.length; i++)
			values[i] = new ContentValues();		
			values[0].put(MeasureRecord.measureType, "BP");
			values[0].put(MeasureRecord.value, "133/121/68");
			values[0].put(MeasureRecord.itemno, "1");
			values[0].put(MeasureRecord.createDate, new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()));
			
			values[1].put(MeasureRecord.measureType, "BG");
			values[1].put(MeasureRecord.value, "65");
			values[1].put(MeasureRecord.itemno, "2");
			values[1].put(MeasureRecord.createDate, new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()));	
			
		for(ContentValues row : values){
			db.insert(MeasureRecord.TABLE_NAME, null, row);
		}	
	}
	
	@SuppressLint("SimpleDateFormat")
	private void measureDeviceFalseData(SQLiteDatabase db){
		ContentValues[] values = new ContentValues[2];
		System.out.println("table_create");
		for(int i=0; i<values.length; i++)
			values[i] = new ContentValues();		
			values[0].put(MeasureDevice.device, "Taidoc-Device1"+"/"+"00:12:A1:B0:8A:F8");
			values[0].put(MeasureDevice.itemno, "1");
			values[0].put(MeasureDevice.createDate, new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()));
			
			values[1].put(MeasureDevice.device, "Taidoc-Device2"+"/"+"00:12:A1:B0:8A:F9");
			values[1].put(MeasureDevice.itemno, "2");
			values[1].put(MeasureDevice.createDate, new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()));	
			
		for(ContentValues row : values){
			db.insert(MeasureDevice.TABLE_NAME, null, row);
		}	
	}
}
package hat.bemo.measure.setting_db;

public class TABLE_BO_DATA {
	public static final String TABLE_NAME = "TABLE_BO_DATA";
	public static final String ITEMNO = "ITEMNO";
	public static final String account = "account";
	public static final String OXY_TIME = "OXY_TIME";
	public static final String OXY_COUNT = "OXY_COUNT";
	public static final String PLUS = "PLUS";
	public static final String SPO2 = "SPO2";
	public static final String CREATE_DATE = "CREATE_DATE";
	
	public static final String SQL_CREATE_TABLE = "create table if not exists " + TABLE_NAME + " ("
			+ account + " text not null, "	 	
			+ ITEMNO + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ OXY_TIME + " text not null, "
			+ OXY_COUNT + " text not null, "
			+ PLUS + " text not null, "
			+ SPO2 + " text not null, "			
			+ CREATE_DATE + " text not null "	
			+ ");";
}
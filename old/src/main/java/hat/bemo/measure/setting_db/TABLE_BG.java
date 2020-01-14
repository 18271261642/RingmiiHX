package hat.bemo.measure.setting_db;

public class TABLE_BG {
	public static final String TABLE_NAME = "TABLE_BG";
	public static final String DEVICE_NAME = "DEVICE_NAME";
	public static final String CREATE_DATE = "CREATE_DATE";
	public static final String SWITCH = "SWITCH";
	public static final String ITEMNO = "ITEMNO";
	
	public static final String SQL_CREATE_TABLE = "create table if not exists " + TABLE_NAME + " ("
			+ DEVICE_NAME + " text not null, "	 	
			+ ITEMNO + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ SWITCH + " text not null, "
			+ CREATE_DATE + " text not null "	
			+ ");";
}

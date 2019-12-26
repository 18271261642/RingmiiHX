package hat.bemo.measure.setting_db;

public class TABLE_BP_DATA {
	public static final String TABLE_NAME = "TABLE_BP_DATA";
	public static final String account = "account";
	public static final String ITEMNO = "ITEMNO";
	public static final String BS_TIME = "BS_TIME";
	public static final String HIGH_PRESSURE = "HIGH_PRESSURE";
	public static final String LOW_PRESSURE = "LOW_PRESSURE";
	public static final String PLUS = "PLUS";
	public static final String MODEL = "MODEL";
	public static final String DATA_TYPE = "DATA_TYPE";
	public static final String YEAR = "YEAR";
	public static final String ARR = "ARR";
	public static final String USUAL_MODE = "USUAL_MODE";
	public static final String DIAGNOSTIC_MODE = "DIAGNOSTIC_MODE";
	public static final String RES = "RES";
	public static final String AM = "AM";
	public static final String PM = "PM";
	public static final String AFIB = "AFIB";
	public static final String CREATE_DATE = "CREATE_DATE";
	
	public static final String SQL_CREATE_TABLE = "create table if not exists " + TABLE_NAME + " ("
			+ account + " text not null, "	 	
			+ ITEMNO + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ BS_TIME + " text not null, "
			+ HIGH_PRESSURE + " text not null, "
			+ LOW_PRESSURE + " text not null, "
			+ PLUS + " text not null, "
			+ MODEL + " text not null, "
			+ DATA_TYPE + " text not null, "
			+ YEAR + " text not null, "
			+ ARR + " text not null, "
			+ USUAL_MODE + " text not null, "
			+ DIAGNOSTIC_MODE + " text not null, "
			+ RES + " text not null, "
			+ AM + " text not null, "
			+ PM + " text not null, "
			+ AFIB + " text not null, "
			+ CREATE_DATE + " text not null "	
			+ ");";
}
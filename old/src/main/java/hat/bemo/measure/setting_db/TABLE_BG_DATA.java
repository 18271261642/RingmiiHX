package hat.bemo.measure.setting_db;

public class TABLE_BG_DATA {
	public static final String TABLE_NAME = "TABLE_BG_DATA";
	public static final String account = "account";
	public static final String ITEMNO = "ITEMNO";	
	public static final String unit = "unit";	
	public static final String bsTime = "bsTime";
	public static final String glu = "glu";
	public static final String hemoglobin = "hemoglobin";
	public static final String bloodFlowVelocity = "bloodFlowVelocity";
	
	public static final String SQL_CREATE_TABLE = "create table if not exists " + TABLE_NAME + " ("
			+ account + " text not null, "	 	
			+ ITEMNO + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ unit + " text not null, "
			+ bsTime + " text not null, "
			+ glu + " text not null, "
			+ hemoglobin + " text not null, "	
			+ bloodFlowVelocity + " text not null "
			+ ");";
}
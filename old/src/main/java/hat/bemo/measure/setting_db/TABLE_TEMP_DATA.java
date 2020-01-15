package hat.bemo.measure.setting_db;

public class TABLE_TEMP_DATA {
	
	public static final String TABLE_NAME = "TABLE_TEMP_DATA";
	public static final String account = "account";
	public static final String BS_TIME = "BS_TIME";	
	public static final String TYPE_T = "TYPE_T";	
	public static final String UINT = "UINT";
	public static final String TEMP = "TEMP";
	public static final String CREATE_DATE = "CREATE_DATE";
	
	public static final String SQL_CREATE_TABLE = "create table if not exists " + TABLE_NAME + " ("
			+ account + " text not null, "	 	
			+ BS_TIME + " text not null, "
			+ TYPE_T + " text not null, "
			+ UINT + " text not null, "
			+ TEMP + " text not null, "
			+ CREATE_DATE + " text not null, "	
			+ ");";

}

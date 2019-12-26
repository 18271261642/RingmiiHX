package hat.bemo.measure.Table;

import android.provider.BaseColumns;

public class Blood_Table implements BaseColumns{
	public static final String TABLE_NAME = "Blood";
	public static final String H_PRESSURE = "H_PRESSURE";
	public static final String L_PRESSURE = "L_PRESSURE";
	public static final String PLUS = "PLUS";
	public static final String BS_TIME = "BS_TIME";
	public static final String CREATE_DATE = "CREATEDATE";
	public static final String UPDATE_DATE = "UPDATEDATE";
	public static final String IMEI = "IMEI";
	public static final String DATETIME = "DATETIME";
	public static final String BLOOD_NO="BLOOD_NO";
	
	public static final String SQL_CREATE_TABLE = "create table if not exists " + TABLE_NAME + " ("
			+ H_PRESSURE + " text not null default '1', "
			+ L_PRESSURE + " text not null, "
			+ PLUS + " text not null, "
			+ BS_TIME + " text not null,"
			+ CREATE_DATE + " text not null, "
			+ UPDATE_DATE + " text not null, "
			+ DATETIME + " text not null, "
			+ BLOOD_NO + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ IMEI + " text not null"
			+ ");";
}

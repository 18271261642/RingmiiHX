package hat.bemo.measure.database;

public class MeasureDevice {
	public static final String TABLE_NAME = "measuredevice";
	public static final String device = "device";
	public static final String createDate = "createDate";
	public static final String itemno = "itemno";
	
	public static final String SQL_CREATE_TABLE = "create table if not exists " + TABLE_NAME + " ("
			+ device + " text not null, "	 	
			+ itemno + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ createDate + " text not null "			
			+ ");";
}
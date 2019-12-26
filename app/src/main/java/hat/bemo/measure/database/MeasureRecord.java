package hat.bemo.measure.database;

public class MeasureRecord {
	public static final String TABLE_NAME = "measurerecord";
	public static final String value = "value";
	public static final String createDate = "createDate";
	public static final String measureType = "measureType";
	public static final String itemno = "itemno";
	
	public static final String SQL_CREATE_TABLE = "create table if not exists " + TABLE_NAME + " ("
			+ value + " text not null, "	 	
			+ itemno + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ measureType + " text not null, "
			+ createDate + " text not null "	
			+ ");";
}
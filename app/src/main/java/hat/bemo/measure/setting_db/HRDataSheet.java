package hat.bemo.measure.setting_db;

public class HRDataSheet {
	public static final String TABLE_NAME = "HRDataSheet";
	public static final String HRDataNumber = "HRDataNumber";
	public static final String HeartRate = "HeartRate";
	public static final String PrValue = "PrValue";
	public static final String RelaxDegree = "RelaxDegree";
	public static final String BreaThing = "BreaThing";
	public static final String Heartage = "Heartage";
	public static final String FiveHa = "FiveHa";
	public static final String BsTime = "BsTime";
	public static final String CreateDate = "CreateDate";
	public static final String RowData = "RowData";
	public static final String CreateDateDetails = "CreateDateDetails";
	public static final String DetailNo = "DetailNo";
	
	public static final String SQL_CREATE_TABLE = "create table if not exists " + TABLE_NAME + " ("
			+ HRDataNumber + " INTEGER PRIMARY KEY AUTOINCREMENT, "	 	
			+ HeartRate + " text not null, "
			+ PrValue + " text not null, "
			+ RelaxDegree + " text not null, "			
			+ BreaThing + " text not null, "
			+ Heartage + " text not null, "
			+ FiveHa + " text not null, "	
			+ BsTime + " text not null, "
			+ CreateDate + " text not null, "
			+ RowData + " text not null, "	
			+ CreateDateDetails + " text not null, "
			+ DetailNo + " text not null "			 
			+ ");";
}
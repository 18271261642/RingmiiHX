package hat.bemo.measure.setting_db;

public class TABLE_BW_DATA {
	
	public static final String TABLE_NAME = "TABLE_BW_DATA";
	public static final String account = "account";
	public static final String BF_TIME = "BF_TIME";
	public static final String HEIGHT = "HEIGHT";
	public static final String SEX = "SEX";
	public static final String UNIT = "UNIT";
	public static final String BW = "BW";
	public static final String BF_RATE = "BF_RATE";
	public static final String BMI = "BMI";
	public static final String RECYCLE = "RECYCLE";
	public static final String ORGAN_FAT = "ORGAN_FAT";
	public static final String BONE = "BONE";
	public static final String BIRTH_YEAR = "BIRTH_YEAR";
	public static final String BIRTH_MONTH = "BIRTH_MONTH";
	public static final String BIRTH_DAY = "BIRTH_DAY";
	public static final String AGE = "AGE";
	public static final String MUSCLE = "MUSCLE";
	public static final String RESISTER = "RESISTER";
	public static final String AQUA = "AQUA";
	public static final String CREATE_DATE = "CREATE_DATE";
	
	public static final String SQL_CREATE_TABLE = "create table if not exists " + TABLE_NAME + " ("
			+ account + " text not null, "	 	
			+ BF_TIME + " text not null, "
			+ HEIGHT + " text not null, "
			+ SEX + " text not null, "
			+ UNIT + " text not null, "
			+ BW + " text not null, "
			+ BF_RATE + " text not null, "
			+ BMI + " text not null, "
			+ RECYCLE + " text not null, "
			+ ORGAN_FAT + " text not null, "
			+ BONE + " text not null, "
			+ BIRTH_YEAR + " text not null, "
			+ BIRTH_MONTH + " text not null, "
			+ BIRTH_DAY + " text not null, "
			+ AGE + " text not null, "
			+ MUSCLE + " text not null, "
			+ RESISTER + " text not null, "
			+ AQUA + " text not null, "
			+ CREATE_DATE + " text not null, "	
			+ ");";

}

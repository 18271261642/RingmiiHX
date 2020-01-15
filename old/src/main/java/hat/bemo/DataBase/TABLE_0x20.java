package hat.bemo.DataBase;

/**
 * Created by apple on 2017/11/10.
 */

public class TABLE_0x20 {
    public static final String TABLE_NAME = "TABLE_0x20";
    public static final String EMG_DATE = "EMG_DATE";
    public static final String GPS_LAT = "GPS_LAT";
    public static final String GPS_LNG = "GPS_LNG";
    public static final String GPS_ADDRESS = "GPS_ADDRESS";
    public static final String GPS_ACCURACY = "GPS_ACCURACY";
    public static final String GPS_STATUS = "GPS_STATUS";
    public static final String MCC = "MCC";
    public static final String MNC = "MNC";
    public static final String LAC = "LAC";
    public static final String CELL_ID = "CELL_ID";
    public static final String RSSI = "RSSI";
    public static final String WIFI_MAC = "WIFI_MAC";
    public static final String WIFI_Signal_dB = "WIFI_Signal_dB";
    public static final String WIFI_Channel = "WIFI_Channel";
    public static final String CREATE_DATE = "CREATE_DATE";
    public static final String ITEMNO = "ITEMNO";

    public static final String SQL_CREATE_TABLE = "create table if not exists " + TABLE_NAME + " ("
            + EMG_DATE + " text not null, "
            + GPS_LAT + " text not null, "
            + GPS_LNG + " text not null, "
            + GPS_ADDRESS + " text not null, "
            + GPS_ACCURACY + " text not null, "
            + GPS_STATUS + " text not null, "
            + MCC + " text not null, "
            + MNC + " text not null, "
            + LAC + " text not null, "
            + CELL_ID + " text not null, "
            + RSSI + " text not null, "
            + WIFI_MAC + " text not null, "
            + WIFI_Signal_dB + " text not null, "
            + WIFI_Channel + " text not null, "
            + ITEMNO + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + CREATE_DATE + " text not null "
            + ");";

}

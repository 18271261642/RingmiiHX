package hat.bemo.APIupload;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import hat.bemo.DataBase.Create_Table;
import hat.bemo.DataBase.TABLE_0x20;
import hat.bemo.DataBase.TABLE_0x30;

/**
 * Created by apple on 2017/11/10.
 */

public class Update {

    private Create_Table dbtc = null;
    private SQLiteDatabase db = null;

//    public int up_0x01(Context context, String ITEMNO[], String PR_COUNT, String PR_DATE, String VOLTAGE,
//                       String VOLTAGE_PERCENT, String LAC, String MNC, String RSSI, String CELL_ID,
//                       String MCC, String GSensor_AVG, String GSensor_MAX, String PPM, String GPS_STATUS,
//                       String GPS_LAT, String GPS_LNG, String GPS_ADDRESS, String GPS_ACCURACY, String WIFI_MAC,
//                       String WIFI_Signal_dB, String WIFI_Channel){
//
//        dbtc = new Create_Table(context);
//        db = dbtc.getWritableDatabase();
//        ContentValues cv = new ContentValues();
//        cv.put(TABLE_0x01.PR_COUNT, PR_COUNT);
//        cv.put(TABLE_0x01.PR_DATE, PR_DATE);
//        cv.put(TABLE_0x01.VOLTAGE, VOLTAGE);
//        cv.put(TABLE_0x01.VOLTAGE_PERCENT, VOLTAGE_PERCENT);
//        cv.put(TABLE_0x01.LAC, LAC);
//        cv.put(TABLE_0x01.MNC, MNC);
//        cv.put(TABLE_0x01.RSSI, RSSI);
//        cv.put(TABLE_0x01.CELL_ID, CELL_ID);
//        cv.put(TABLE_0x01.MCC, MCC);
//        cv.put(TABLE_0x01.GSensor_AVG, GSensor_AVG);
//        cv.put(TABLE_0x01.GSensor_MAX, GSensor_MAX);
//        cv.put(TABLE_0x01.PPM, PPM);
//        cv.put(TABLE_0x01.GPS_STATUS, GPS_STATUS);
//        cv.put(TABLE_0x01.GPS_LAT, GPS_LAT);
//        cv.put(TABLE_0x01.GPS_LNG, GPS_LNG);
//        cv.put(TABLE_0x01.GPS_ADDRESS, GPS_ADDRESS);
//        cv.put(TABLE_0x01.GPS_ACCURACY, GPS_ACCURACY);
//        cv.put(TABLE_0x01.WIFI_MAC, WIFI_MAC);
//        cv.put(TABLE_0x01.WIFI_Signal_dB, WIFI_Signal_dB);
//        cv.put(TABLE_0x01.WIFI_Channel, WIFI_Channel);
//
//        int update = db.update(TABLE_0x01.TABLE_NAME, cv, TABLE_0x01.ITEMNO+ "= ?", ITEMNO);
//        return update;
//    }

//    public int up_0x10(Context context,               String ITEMNO[],          String AUTOREAD,         String CHECKSUM,
//                       String DIALOUT_LIMIT_STARTDAY, String ECHO_GPS_T,        String ECHO_PR_T,        String PHONE_LIMIT_BY_CUSTOMER,
//                       String PHONE_LIMIT_MODE,       String PHONE_LIMIT_ONOFF, String PHONE_LIMIT_TIME, String POWERON_LOGO,
//                       String SERVER_DNS,  			  String SERVER_IP_PORT ){
//
//        dbtc = new Create_Table(context);
//        db = dbtc.getWritableDatabase();
//        ContentValues cv = new ContentValues();
//        cv.put(TABLE_0x10.AUTOREAD, AUTOREAD);
//        cv.put(TABLE_0x10.CHECKSUM, CHECKSUM);
//        cv.put(TABLE_0x10.DIALOUT_LIMIT_STARTDAY, DIALOUT_LIMIT_STARTDAY);
//        cv.put(TABLE_0x10.ECHO_GPS_T, ECHO_GPS_T);
//        cv.put(TABLE_0x10.ECHO_PR_T, ECHO_PR_T);
//        cv.put(TABLE_0x10.PHONE_LIMIT_BY_CUSTOMER, PHONE_LIMIT_BY_CUSTOMER);
//        cv.put(TABLE_0x10.PHONE_LIMIT_MODE, PHONE_LIMIT_MODE);
//        cv.put(TABLE_0x10.PHONE_LIMIT_ONOFF, PHONE_LIMIT_ONOFF);
//        cv.put(TABLE_0x10.PHONE_LIMIT_TIME, PHONE_LIMIT_TIME);
//        cv.put(TABLE_0x10.POWERON_LOGO, POWERON_LOGO);
//        cv.put(TABLE_0x10.SERVER_DNS, SERVER_DNS);
//        cv.put(TABLE_0x10.SERVER_IP_PORT, SERVER_IP_PORT);
//
//        int update = db.update(TABLE_0x10.TABLE_NAME, cv, TABLE_0x10.ITEMNO+ "= ?", ITEMNO);
//        return update;
//    }

//    public int up_0x11(Context context,           String ITEMNO[],       	String CHECKSUM,         String FALL_DETECT,
//                       String FALL_PHONE_NAME1,   String FALL_PHONE_NAME2,  String FALL_PHONE_NAME3, String FALL_PHONE_NO1,
//                       String FALL_PHONE_NO2,     String FALL_PHONE_NO3, 	String FALL_SMS,   	     String FAMILY_PHONE_NAME1,
//                       String FAMILY_PHONE_NAME2, String FAMILY_PHONE_NAME3,String FAMILY_PHONE_NO1, String FAMILY_PHONE_NO2,
//                       String FAMILY_PHONE_NO3,   String FMLY_KEY_DELAY, 	String MED_TIME_1, 	     String SWITCH,
//                       String TIME,               String WEEK1,             String WEEK2,            String WEEK3,
//                       String WEEK4,			  String WEEK5, 			String WEEK6,			 String WEEK7,
//                       String MED_TIME_2, 		  String MED_TIME_3,        String MED_TIME_4,       String MED_TIME_5,
//                       String NON_DISTRUB, 		  String PERSONAL_INFO,  	String ACCOUNTID ,       String AGE,
//                       String BIRTHDATE_DD,       String BIRTHDATE_MM,      String BIRTHDATE_YY,   	 String HEIGHT,
//                       String IDEAL_WEIGHT, 	  String NAME, 				String SEX,				 String STEP,
//                       String UNIT,           	  String WEIGHT,            String RETURN_DT_1, 	 String DAY,
//                       String MONTH, 			  String YEAR,			    String SET_LANG, 	     String SOS_KEY_DELAY,
//                       String SOS_PHONE_NAME1,    String SOS_PHONE_NAME2,   String SOS_PHONE_NAME3,  String SOS_PHONE_NO1,
//                       String SOS_PHONE_NO2,  	  String SOS_PHONE_NO3,     String SOS_SMS, 		 String TIMEZONE,
//                       String WHITE_LIST_ON,	  String ELECTRIC_FENCE,	String PHONE){
//
//        dbtc = new Create_Table(context);
//        db = dbtc.getWritableDatabase();
//        ContentValues cv = new ContentValues();
//        cv.put(TABLE_0x11.CHECKSUM, CHECKSUM);
//        cv.put(TABLE_0x11.FALL_DETECT, FALL_DETECT);
//        cv.put(TABLE_0x11.FALL_PHONE_NAME1, FALL_PHONE_NAME1);
//        cv.put(TABLE_0x11.FALL_PHONE_NAME2, FALL_PHONE_NAME2);
//        cv.put(TABLE_0x11.FALL_PHONE_NAME3, FALL_PHONE_NAME3);
//        cv.put(TABLE_0x11.FALL_PHONE_NO1, FALL_PHONE_NO1);
//        cv.put(TABLE_0x11.FALL_PHONE_NO2, FALL_PHONE_NO2);
//        cv.put(TABLE_0x11.FALL_PHONE_NO3, FALL_PHONE_NO3);
//        cv.put(TABLE_0x11.FALL_SMS, FALL_SMS);
//        cv.put(TABLE_0x11.FAMILY_PHONE_NAME1, FAMILY_PHONE_NAME1);
//        cv.put(TABLE_0x11.FAMILY_PHONE_NAME2, FAMILY_PHONE_NAME2);
//        cv.put(TABLE_0x11.FAMILY_PHONE_NAME3, FAMILY_PHONE_NAME3);
//        cv.put(TABLE_0x11.FAMILY_PHONE_NO1, FAMILY_PHONE_NO1);
//        cv.put(TABLE_0x11.FAMILY_PHONE_NO2, FAMILY_PHONE_NO2);
//        cv.put(TABLE_0x11.FAMILY_PHONE_NO3, FAMILY_PHONE_NO3);
//        cv.put(TABLE_0x11.FMLY_KEY_DELAY, FMLY_KEY_DELAY);
//        cv.put(TABLE_0x11.MED_TIME_1, MED_TIME_1);
//        cv.put(TABLE_0x11.SWITCH, SWITCH);
//        cv.put(TABLE_0x11.TIME, TIME);
//        cv.put(TABLE_0x11.WEEK1, WEEK1);
//        cv.put(TABLE_0x11.WEEK2, WEEK2);
//        cv.put(TABLE_0x11.WEEK3, WEEK3);
//        cv.put(TABLE_0x11.WEEK4, WEEK4);
//        cv.put(TABLE_0x11.WEEK5, WEEK5);
//        cv.put(TABLE_0x11.WEEK6, WEEK6);
//        cv.put(TABLE_0x11.WEEK7, WEEK7);
//        cv.put(TABLE_0x11.MED_TIME_2, MED_TIME_2);
//        cv.put(TABLE_0x11.MED_TIME_3, MED_TIME_3);
//        cv.put(TABLE_0x11.MED_TIME_4, MED_TIME_4);
//        cv.put(TABLE_0x11.MED_TIME_5, MED_TIME_5);
//        cv.put(TABLE_0x11.NON_DISTRUB, NON_DISTRUB);
//        cv.put(TABLE_0x11.PERSONAL_INFO, PERSONAL_INFO);
//        cv.put(TABLE_0x11.ACCOUNTID, ACCOUNTID);
//        cv.put(TABLE_0x11.AGE, AGE);
//        cv.put(TABLE_0x11.BIRTHDATE_DD, BIRTHDATE_DD);
//        cv.put(TABLE_0x11.BIRTHDATE_MM, BIRTHDATE_MM);
//        cv.put(TABLE_0x11.BIRTHDATE_YY, BIRTHDATE_YY);
//        cv.put(TABLE_0x11.HEIGHT, HEIGHT);
//        cv.put(TABLE_0x11.IDEAL_WEIGHT, IDEAL_WEIGHT);
//        cv.put(TABLE_0x11.NAME, NAME);
//        cv.put(TABLE_0x11.SEX, SEX);
//        cv.put(TABLE_0x11.STEP, STEP);
//        cv.put(TABLE_0x11.UNIT, UNIT);
//        cv.put(TABLE_0x11.WEIGHT, WEIGHT);
//        cv.put(TABLE_0x11.RETURN_DT_1, RETURN_DT_1);
//        cv.put(TABLE_0x11.DAY, DAY);
//        cv.put(TABLE_0x11.MONTH, MONTH);
//        cv.put(TABLE_0x11.YEAR, YEAR);
//        cv.put(TABLE_0x11.SET_LANG, SET_LANG);
//        cv.put(TABLE_0x11.SOS_KEY_DELAY, SOS_KEY_DELAY);
//        cv.put(TABLE_0x11.SOS_PHONE_NAME1, SOS_PHONE_NAME1);
//        cv.put(TABLE_0x11.SOS_PHONE_NAME2, SOS_PHONE_NAME2);
//        cv.put(TABLE_0x11.SOS_PHONE_NAME3, SOS_PHONE_NAME3);
//        cv.put(TABLE_0x11.SOS_PHONE_NO1, SOS_PHONE_NO1);
//        cv.put(TABLE_0x11.SOS_PHONE_NO2, SOS_PHONE_NO2);
//        cv.put(TABLE_0x11.SOS_PHONE_NO3, SOS_PHONE_NO3);
//        cv.put(TABLE_0x11.SOS_SMS, SOS_SMS);
//        cv.put(TABLE_0x11.TIMEZONE, TIMEZONE);
//        cv.put(TABLE_0x11.WHITE_LIST_ON, WHITE_LIST_ON);
////			 cv.put(TABLE_0x11.ITEMNO, ITEMNO);
//        cv.put(TABLE_0x11.ELECTRIC_FENCE, ELECTRIC_FENCE);
//        cv.put(TABLE_0x11.PHONE, PHONE);
//
//        int update = db.update(TABLE_0x11.TABLE_NAME, cv, TABLE_0x11.ITEMNO+ "= ?", ITEMNO);
//        return update;
//    }

    public int up_0x20(Context context,    String ITEMNO[],        String EMG_DATE,     String GPS_LAT,     String GPS_LNG,
                       String GPS_ADDRESS, String GPS_ACCURACY,    String GPS_STATUS,   String MCC,
                       String MNC,         String LAC, 	           String CELL_ID,   	String RSSI,
                       String WIFI_MAC,    String WIFI_Signal_dB , String WIFI_Channel, String CREATE_DATE){

        dbtc = new Create_Table(context);
        db = dbtc.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(TABLE_0x20.EMG_DATE, EMG_DATE);
        cv.put(TABLE_0x20.GPS_LAT, GPS_LAT);
        cv.put(TABLE_0x20.GPS_LNG, GPS_LNG);
        cv.put(TABLE_0x20.GPS_ADDRESS, GPS_ADDRESS);
        cv.put(TABLE_0x20.GPS_ACCURACY, GPS_ACCURACY);
        cv.put(TABLE_0x20.GPS_STATUS, GPS_STATUS);
        cv.put(TABLE_0x20.MCC, MCC);
        cv.put(TABLE_0x20.MNC, MNC);
        cv.put(TABLE_0x20.LAC, LAC);
        cv.put(TABLE_0x20.CELL_ID, CELL_ID);
        cv.put(TABLE_0x20.RSSI, RSSI);
        cv.put(TABLE_0x20.WIFI_MAC, WIFI_MAC);
        cv.put(TABLE_0x20.WIFI_Signal_dB, WIFI_Signal_dB);
        cv.put(TABLE_0x20.WIFI_Channel, WIFI_Channel);
        cv.put(TABLE_0x20.CREATE_DATE, CREATE_DATE);

        int update = db.update(TABLE_0x20.TABLE_NAME, cv, TABLE_0x20.ITEMNO+ "= ?", ITEMNO);
        return update;
    }

    public int up_0x30(Context context,    String ITEMNO[],        String EMG_DATE,     String GPS_LAT,     String GPS_LNG,
                       String GPS_ADDRESS, String GPS_ACCURACY,    String GPS_STATUS,   String MCC,
                       String MNC,         String LAC, 	           String CELL_ID,   	String RSSI,
                       String WIFI_MAC,    String WIFI_Signal_dB , String WIFI_Channel, String CREATE_DATE){

        dbtc = new Create_Table(context);
        db = dbtc.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(TABLE_0x30.EMG_DATE, EMG_DATE);
        cv.put(TABLE_0x30.MCC, MCC);
        cv.put(TABLE_0x30.MNC, MNC);
        cv.put(TABLE_0x30.LAC, LAC);
        cv.put(TABLE_0x30.CELL_ID, CELL_ID);
        cv.put(TABLE_0x30.RSSI, RSSI);
        cv.put(TABLE_0x30.GPS_STATUS, GPS_STATUS);
        cv.put(TABLE_0x30.GPS_LAT, GPS_LAT);
        cv.put(TABLE_0x30.GPS_LNG, GPS_LNG);
        cv.put(TABLE_0x30.GPS_ACCURACY, GPS_ACCURACY);
        cv.put(TABLE_0x30.GPS_ADDRESS, GPS_ADDRESS);
        cv.put(TABLE_0x30.WIFI_MAC, WIFI_MAC);
        cv.put(TABLE_0x30.WIFI_Signal_dB, WIFI_Signal_dB);
        cv.put(TABLE_0x30.WIFI_Channel, WIFI_Channel);
        cv.put(TABLE_0x30.CREATE_DATE, CREATE_DATE);

        int update = db.update(TABLE_0x30.TABLE_NAME, cv, TABLE_0x30.ITEMNO+ "= ?", ITEMNO);
        return update;
    }
//
//    public int up_0x28(Context context,    String ITEMNO[],        String EMG_DATE,      String GPS_LAT,      String GPS_LNG,
//                       String GPS_ADDRESS, String GPS_ACCURACY,    String GPS_STATUS,   String MCC,
//                       String MNC,         String LAC, 	           String CELL_ID,   	String RSSI,
//                       String WIFI_MAC,    String WIFI_Signal_dB , String WIFI_Channel, String CREATE_DATE){
//
//        dbtc = new Create_Table(context);
//        db = dbtc.getWritableDatabase();
//        ContentValues cv = new ContentValues();
//        cv.put(TABLE_0x28.EMG_DATE, EMG_DATE);
//        cv.put(TABLE_0x28.GPS_LAT, GPS_LAT);
//        cv.put(TABLE_0x28.GPS_LNG, GPS_LNG);
//        cv.put(TABLE_0x28.GPS_ADDRESS, GPS_ADDRESS);
//        cv.put(TABLE_0x28.GPS_ACCURACY, GPS_ACCURACY);
//        cv.put(TABLE_0x28.GPS_STATUS, GPS_STATUS);
//        cv.put(TABLE_0x28.MCC, MCC);
//        cv.put(TABLE_0x28.MNC, MNC);
//        cv.put(TABLE_0x28.LAC, LAC);
//        cv.put(TABLE_0x28.CELL_ID, CELL_ID);
//        cv.put(TABLE_0x28.RSSI, RSSI);
//        cv.put(TABLE_0x28.WIFI_MAC, WIFI_MAC);
//        cv.put(TABLE_0x28.WIFI_Signal_dB, WIFI_Signal_dB);
//        cv.put(TABLE_0x28.WIFI_Channel, WIFI_Channel);
//        cv.put(TABLE_0x28.CREATE_DATE, CREATE_DATE);
//
//        int update = db.update(TABLE_0x28.TABLE_NAME, cv, TABLE_0x28.ITEMNO+ "= ?", ITEMNO);
//        return update;
//    }
//
//    public int up_0x29(Context context,       String ITEMNO[],       String VOLTAGE,    String VOLTAGE_PERCENT,      String LAC,
//                       String MNC,            String RSSI,           String CELL_ID,    String MCC,                  String GSensor_AVG,
//                       String GSensor_MAX,    String TEMP,           String PPM,        String CELLER_LAC ,          String CELLER_MNC,
//                       String CELLER_RSSI, 	  String CELLER_CELL_ID, String CELLER_MCC, String GPS_STATUS,           String GPS_LAT,
//                       String GPS_LNG,     	  String GPS_ACCURACY,   String GPS_ADDRESS,String ELECTRIC_FENCE_ITEMNO,String WIFI_MAC,
//                       String WIFI_Signal_dB, String WIFI_Channel,   String CREATE_DATE,String EF_COUNT, String EF_DATE){
//
//        dbtc = new Create_Table(context);
//        db = dbtc.getWritableDatabase();
//        ContentValues cv = new ContentValues();
//        cv.put(TABLE_0x29.EF_COUNT, EF_COUNT);
//        cv.put(TABLE_0x29.EF_DATE, EF_DATE);
//        cv.put(TABLE_0x29.VOLTAGE, VOLTAGE);
//        cv.put(TABLE_0x29.VOLTAGE_PERCENT, VOLTAGE_PERCENT);
//        cv.put(TABLE_0x29.LAC, LAC);
//        cv.put(TABLE_0x29.MNC, MNC);
//        cv.put(TABLE_0x29.RSSI, RSSI);
//        cv.put(TABLE_0x29.CELL_ID, CELL_ID);
//        cv.put(TABLE_0x29.MCC, MCC);
//        cv.put(TABLE_0x29.GSensor_AVG, GSensor_AVG);
//        cv.put(TABLE_0x29.GSensor_MAX, GSensor_MAX);
//        cv.put(TABLE_0x29.TEMP, TEMP);
//        cv.put(TABLE_0x29.PPM, PPM);
//        cv.put(TABLE_0x29.CELLER_LAC, CELLER_LAC);
//        cv.put(TABLE_0x29.CELLER_MNC, CELLER_MNC);
//        cv.put(TABLE_0x29.CELLER_RSSI, CELLER_RSSI);
//        cv.put(TABLE_0x29.CELLER_CELL_ID, CELLER_CELL_ID);
//        cv.put(TABLE_0x29.CELLER_MCC, CELLER_MCC);
//        cv.put(TABLE_0x29.GPS_STATUS, GPS_STATUS);
//        cv.put(TABLE_0x29.GPS_LAT, GPS_LAT);
//        cv.put(TABLE_0x29.GPS_LNG, GPS_LNG);
//        cv.put(TABLE_0x29.GPS_ACCURACY, GPS_ACCURACY);
//        cv.put(TABLE_0x29.GPS_ADDRESS, GPS_ADDRESS);
//        cv.put(TABLE_0x29.ELECTRIC_FENCE_ITEMNO, ELECTRIC_FENCE_ITEMNO);
//        cv.put(TABLE_0x29.WIFI_MAC, WIFI_MAC);
//        cv.put(TABLE_0x29.WIFI_Signal_dB, WIFI_Signal_dB);
//        cv.put(TABLE_0x29.WIFI_Channel, WIFI_Channel);
//        cv.put(TABLE_0x29.CREATE_DATE, CREATE_DATE);
//
//
//        int update = db.update(TABLE_0x29.TABLE_NAME, cv, TABLE_0x29.ITEMNO+ "= ?", ITEMNO);
//        return update;
//    }
//
//    public int up_0x2A(Context context, String ITEMNO[], String DEVICE_TOKEN, String SIGNATURE, String CREATE_DATE){
//
//        dbtc = new Create_Table(context);
//        db = dbtc.getWritableDatabase();
//        ContentValues cv = new ContentValues();
//        cv.put(TABLE_0x2A.DEVICE_TOKEN, DEVICE_TOKEN);
//        cv.put(TABLE_0x2A.SIGNATURE, SIGNATURE);
//        cv.put(TABLE_0x2A.CREATE_DATE, CREATE_DATE);
//
//        int update = db.update(TABLE_0x2A.TABLE_NAME, cv, TABLE_0x2A.ITEMNO+ "= ?", ITEMNO);
//        return update;
//    }
//
//    public int up_0x2B(Context context, String ITEMNO[], String ALERT_TYPE){
//
//        dbtc = new Create_Table(context);
//        db = dbtc.getWritableDatabase();
//        ContentValues cv = new ContentValues();
//        cv.put(TABLE_0x2B.ALERT_TYPE, ALERT_TYPE);
//
//        int update = db.update(TABLE_0x2B.TABLE_NAME, cv, TABLE_0x2B.ITEMNO+ "= ?", ITEMNO);
//        return update;
//    }
//
//    public int up_0x2E(Context context, String ITEMNO[],        String PWR_ON_DATE, String FW_BUILD_DATE,
//                       String VOLTAGE,  String VOLTAGE_PERCENT, String MCC,         String MNC,
//                       String LAC,      String CELL_ID, 	    String RSSI,   	    String CREATE_DATE){
//
//        dbtc = new Create_Table(context);
//        db = dbtc.getWritableDatabase();
//        ContentValues cv = new ContentValues();
//        cv.put(TABLE_0x2E.PWR_ON_DATE, PWR_ON_DATE);
//        cv.put(TABLE_0x2E.FW_BUILD_DATE, FW_BUILD_DATE);
//        cv.put(TABLE_0x2E.VOLTAGE, VOLTAGE);
//        cv.put(TABLE_0x2E.VOLTAGE_PERCENT, VOLTAGE_PERCENT);
//        cv.put(TABLE_0x2E.MCC, MCC);
//        cv.put(TABLE_0x2E.MNC, MNC);
//        cv.put(TABLE_0x2E.LAC, LAC);
//        cv.put(TABLE_0x2E.CELL_ID, CELL_ID);
//        cv.put(TABLE_0x2E.RSSI, RSSI);
//        cv.put(TABLE_0x2E.CREATE_DATE, CREATE_DATE);
//
//        int update = db.update(TABLE_0x2E.TABLE_NAME, cv, TABLE_0x2E.ITEMNO+ "= ?", ITEMNO);
//        return update;
//    }
//
//    public int up_0x2F(Context context, 	String ITEMNO[],        String PWR_ON_DATE, String FW_BUILD_DATE,
//                       String VOLTAGE,  	String VOLTAGE_PERCENT, String MCC,         String MNC,
//                       String LAC,      	String CELL_ID, 	    String RSSI,   	    String CREATE_DATE,
//                       String GPS_ACCURACY, String GPS_LAT, 		String GPS_LNG,     String GPS_STATUS,
//                       String PWR_OFF_DATE, String PWR_OFF_TYPE, 	String account){
//
//        dbtc = new Create_Table(context);
//        db = dbtc.getWritableDatabase();
//        ContentValues cv = new ContentValues();
//        cv.put(TABLE_0x2F.PWR_ON_DATE, PWR_ON_DATE);
//        cv.put(TABLE_0x2F.FW_BUILD_DATE, FW_BUILD_DATE);
//        cv.put(TABLE_0x2F.VOLTAGE, VOLTAGE);
//        cv.put(TABLE_0x2F.VOLTAGE_PERCENT, VOLTAGE_PERCENT);
//        cv.put(TABLE_0x2F.MCC, MCC);
//        cv.put(TABLE_0x2F.MNC, MNC);
//        cv.put(TABLE_0x2F.LAC, LAC);
//        cv.put(TABLE_0x2F.CELL_ID, CELL_ID);
//        cv.put(TABLE_0x2F.RSSI, RSSI);
//        cv.put(TABLE_0x2F.CREATE_DATE, CREATE_DATE);
//        cv.put(TABLE_0x2F.GPS_ACCURACY, GPS_ACCURACY);
//        cv.put(TABLE_0x2F.GPS_LAT, GPS_LAT);
//        cv.put(TABLE_0x2F.GPS_LNG, GPS_LNG);
//        cv.put(TABLE_0x2F.GPS_STATUS, GPS_STATUS);
//        cv.put(TABLE_0x2F.PWR_OFF_DATE, PWR_OFF_DATE);
//        cv.put(TABLE_0x2F.PWR_OFF_TYPE, PWR_OFF_TYPE);
//        cv.put(TABLE_0x2F.account, account);
//
//        int update = db.update(TABLE_0x2F.TABLE_NAME, cv, TABLE_0x2F.ITEMNO+ "= ?", ITEMNO);
//        return update;
//    }
//
//    public int up_0x3F(Context context,  String ITEMNO[], String PHONE, String START_TIME, String END_TIME,
//                       String CALL_TYPE, String CREATE_DATE){
//
//        dbtc = new Create_Table(context);
//        db = dbtc.getWritableDatabase();
//        ContentValues cv = new ContentValues();
//        cv.put(TABLE_0x3F.PHONE, PHONE);
//        cv.put(TABLE_0x3F.START_TIME, START_TIME);
//        cv.put(TABLE_0x3F.END_TIME, END_TIME);
//        cv.put(TABLE_0x3F.CALL_TYPE, CALL_TYPE);
//        cv.put(TABLE_0x3F.CREATE_DATE, CREATE_DATE);
//
//        int update = db.update(TABLE_0x3F.TABLE_NAME, cv, TABLE_0x3F.ITEMNO+ "= ?", ITEMNO);
//        return update;
//    }
//
//    public int up_0x40(Context context, String ITEMNO[], String SERVER_IP_PORT ,String SERVER_DNS){
//
//        dbtc = new Create_Table(context);
//        db = dbtc.getWritableDatabase();
//        ContentValues cv = new ContentValues();
//        cv.put(TABLE_0x40.SERVER_IP_PORT, SERVER_IP_PORT);
//        cv.put(TABLE_0x40.SERVER_DNS, SERVER_DNS);
//
//        int update = db.update(TABLE_0x40.TABLE_NAME, cv, TABLE_0x40.ITEMNO+ "= ?", ITEMNO);
//        return update;
//    }
//
//    public int up_0x4B(Context context, String ITEMNO[], String URL, String FAMILY_PHOTO){
//
//        dbtc = new Create_Table(context);
//        db = dbtc.getWritableDatabase();
//        ContentValues cv = new ContentValues();
////			 cv.put(TABLE_0x4B.ITEMNO, ITEMNO);
//        cv.put(TABLE_0x4B.URL, URL);
//        cv.put(TABLE_0x4B.FAMILY_PHOTO, FAMILY_PHOTO);
//
//        int update = db.update(TABLE_0x4B.TABLE_NAME, cv, TABLE_0x4B.ITEMNO+ "= ?", ITEMNO);
//        return update;
//    }
//
//    public int up_0x4C(Context context, String ITEMNO[], String URL, String DISPLAY_PHOTO){
//
//        dbtc = new Create_Table(context);
//        db = dbtc.getWritableDatabase();
//        ContentValues cv = new ContentValues();
////		 cv.put(TABLE_0x4C.ITEMNO, ITEMNO);
//        cv.put(TABLE_0x4C.URL, URL);
//        cv.put(TABLE_0x4C.DISPLAY_PHOTO, DISPLAY_PHOTO);
//
//        int update = db.update(TABLE_0x4C.TABLE_NAME, cv, TABLE_0x4C.ITEMNO+ "= ?", ITEMNO);
//        return update;
//    }
//
//    public int up_0x4D(Context context, String ITEMNO[], String GROUP_ID, String NAME, String PHONE, String PHOTO_URL,
//                       String PHONE_LIST){
//
//        dbtc = new Create_Table(context);
//        db = dbtc.getWritableDatabase();
//        ContentValues cv = new ContentValues();
//        cv.put(TABLE_0x4D.GROUP_ID, GROUP_ID);
////			 cv.put(TABLE_0x4D.ITEMNO, ITEMNO);
//        cv.put(TABLE_0x4D.NAME, NAME);
//        cv.put(TABLE_0x4D.PHONE, PHONE);
//        cv.put(TABLE_0x4D.PHOTO_URL, PHOTO_URL);
//        cv.put(TABLE_0x4D.PHONE_LIST, PHONE_LIST);
//
//        int update = db.update(TABLE_0x4D.TABLE_NAME, cv, TABLE_0x4D.ITEMNO+ "= ?", ITEMNO);
//        return update;
//    }
//
//    public int up_0x4W(Context context,         String ITEMNO[],        String dressing_Suggestions, String rain_Probability,
//                       String temperature_High, String temperature_Low, String weather_Code,         String PHONE_LIST){
//
//        dbtc = new Create_Table(context);
//        db = dbtc.getWritableDatabase();
//        ContentValues cv = new ContentValues();
//        cv.put(TABLE_0x4W.dressing_Suggestions, dressing_Suggestions);
//        cv.put(TABLE_0x4W.rain_Probability, rain_Probability);
//        cv.put(TABLE_0x4W.temperature_High, temperature_High);
//        cv.put(TABLE_0x4W.temperature_Low, temperature_Low);
//        cv.put(TABLE_0x4W.weather_Code, weather_Code);
//
//        int update = db.update(TABLE_0x4W.TABLE_NAME, cv, TABLE_0x4W.ITEMNO+ "= ?", ITEMNO);
//        return update;
//    }
//
//    public int up_0x7F(Context context, String ITEMNO[], String ECHO_TYPE, String CHECKSUM_10, String CHECKSUM_11){
//
//        dbtc = new Create_Table(context);
//        db = dbtc.getWritableDatabase();
//        ContentValues cv = new ContentValues();
//        cv.put(TABLE_0x7F.ECHO_TYPE, ECHO_TYPE);
//        cv.put(TABLE_0x7F.CHECKSUM_10, CHECKSUM_10);
//        cv.put(TABLE_0x7F.CHECKSUM_11, CHECKSUM_11);
//
//        int update = db.update(TABLE_0x7F.TABLE_NAME, cv, TABLE_0x7F.ITEMNO+ "= ?", ITEMNO);
//        return update;
//    }
//
//    public int up_801603(Context context,     String NO[],              String birthday,       String broadcaset_account,
//                         String contact_name, String contact_tel,       String contact_type,   String event_time,
//                         String imei, 	      String missing_person_no,	String missing_status, String name,
//                         String photo_url,    String updatatime){
//
//        dbtc = new Create_Table(context);
//        db = dbtc.getWritableDatabase();
//        ContentValues cv = new ContentValues();
//        cv.put(TABLE_801603.birthday, birthday);
//        cv.put(TABLE_801603.broadcaset_account, broadcaset_account);
//        cv.put(TABLE_801603.contact_name, contact_name);
//        cv.put(TABLE_801603.contact_tel, contact_tel);
//        cv.put(TABLE_801603.contact_type, contact_type);
//        cv.put(TABLE_801603.event_time, event_time);
//        cv.put(TABLE_801603.imei, imei);
//        cv.put(TABLE_801603.missing_person_no, missing_person_no);
//        cv.put(TABLE_801603.missing_status, missing_status);
//        cv.put(TABLE_801603.name, name);
//        cv.put(TABLE_801603.photo_url, photo_url);
//        cv.put(TABLE_801603.updatatime, updatatime);
//
//        int update = db.update(TABLE_801603.TABLE_NAME, cv, TABLE_801603.missing_person_no+ "= ?", NO);
//        return update;
//    }
//
//    public int up_0x18(Context context,     String NO[],    String account,       String TRACKING_DATE,
//                       String GPS_LAT,      String GPS_LNG, String GPS_ADDRESS,   String GPS_ACCURACY,
//                       String GPS_STATUS,   String MCC,     String MNC,           String LAC,
//                       String CELL_ID,      String RSSI,    String WIFI_MAC,      String WIFI_Signal_dB,
//                       String WIFI_Channel, String CREATE_DATE){
//
//        dbtc = new Create_Table(context);
//        db = dbtc.getWritableDatabase();
//        ContentValues cv = new ContentValues();
//        cv.put(TABLE_0x18.account, account);
//        cv.put(TABLE_0x18.TRACKING_DATE, TRACKING_DATE);
//        cv.put(TABLE_0x18.GPS_LAT, GPS_LAT);
//        cv.put(TABLE_0x18.GPS_LNG, GPS_LNG);
//        cv.put(TABLE_0x18.GPS_ADDRESS, GPS_ADDRESS);
//        cv.put(TABLE_0x18.GPS_ACCURACY, GPS_ACCURACY);
//        cv.put(TABLE_0x18.GPS_STATUS, GPS_STATUS);
//        cv.put(TABLE_0x18.MCC, MCC);
//        cv.put(TABLE_0x18.MNC, MNC);
//        cv.put(TABLE_0x18.LAC, LAC);
//        cv.put(TABLE_0x18.CELL_ID, CELL_ID);
//        cv.put(TABLE_0x18.RSSI, RSSI);
//        cv.put(TABLE_0x18.WIFI_MAC, WIFI_MAC);
//        cv.put(TABLE_0x18.WIFI_Signal_dB, WIFI_Signal_dB);
//        cv.put(TABLE_0x18.WIFI_Channel, WIFI_Channel);
//        cv.put(TABLE_0x18.CREATE_DATE, CREATE_DATE);
//
//        int update = db.update(TABLE_0x18.TABLE_NAME, cv, TABLE_0x18.NO+ "= ?", NO);
//        return update;
//    }
//
//    public int up_851001(Context context, String NUMBER[], String account,
//                         String fallDetectSwitch, String delayTimer, String serviceIP,
//                         String fromtime, String totime, String week, String enable,
//                         String phone1, String phone2, String phone3, String CREATE_DATE) {
//
//        dbtc = new Create_Table(context);
//        db = dbtc.getWritableDatabase();
//        ContentValues cv = new ContentValues();
//        cv.put(TABLE_851001.account, account);
//        cv.put(TABLE_851001.fallDetectSwitch, fallDetectSwitch);
//        cv.put(TABLE_851001.delayTimer, delayTimer);
//        cv.put(TABLE_851001.serviceIP, serviceIP);
//        cv.put(TABLE_851001.fromtime, fromtime);
//        cv.put(TABLE_851001.totime, totime);
//        cv.put(TABLE_851001.week, week);
//        cv.put(TABLE_851001.enable, enable);
//        cv.put(TABLE_851001.phone1, phone1);
//        cv.put(TABLE_851001.phone2, phone2);
//        cv.put(TABLE_851001.phone3, phone3);
//        cv.put(TABLE_851001.CREATE_DATE, CREATE_DATE);
//
//        int update = db.update(TABLE_851001.TABLE_NAME, cv, TABLE_851001.NUMBER
//                + "= ?", NUMBER);
//        return update;
//    }
//
//    public int up_851301(Context context, String NUMBER[], String account,
//                         String EMG_DATE, String GPS_LAT, String GPS_LNG,
//                         String GPS_ADDRESS, String GPS_ACCURACY, String GPS_STATUS,
//                         String MCC, String MNC, String LAC, String CELL_ID, String RSSI,
//                         String WIFI_MAC, String WIFI_Signal_dB, String WIFI_Channel,
//                         String CREATE_DATE) {
//
//        dbtc = new Create_Table(context);
//        db = dbtc.getWritableDatabase();
//        ContentValues cv = new ContentValues();
//        cv.put(TABLE_851301.account, account);
//        cv.put(TABLE_851301.EMG_DATE, EMG_DATE);
//        cv.put(TABLE_851301.GPS_LAT, GPS_LAT);
//        cv.put(TABLE_851301.GPS_LNG, GPS_LNG);
//        cv.put(TABLE_851301.GPS_ADDRESS, GPS_ADDRESS);
//        cv.put(TABLE_851301.GPS_ACCURACY, GPS_ACCURACY);
//        cv.put(TABLE_851301.GPS_STATUS, GPS_STATUS);
//        cv.put(TABLE_851301.MCC, MCC);
//        cv.put(TABLE_851301.MNC, MNC);
//        cv.put(TABLE_851301.LAC, LAC);
//        cv.put(TABLE_851301.CELL_ID, CELL_ID);
//        cv.put(TABLE_851301.RSSI, RSSI);
//        cv.put(TABLE_851301.WIFI_MAC, WIFI_MAC);
//        cv.put(TABLE_851301.WIFI_Signal_dB, WIFI_Signal_dB);
//        cv.put(TABLE_851301.WIFI_Channel, WIFI_Channel);
//        cv.put(TABLE_851301.CREATE_DATE, CREATE_DATE);
//
//        int update = db.update(TABLE_851301.TABLE_NAME, cv, TABLE_851301.NUMBER
//                + "= ?", NUMBER);
//        return update;
//    }
//
//    public int up_851302(Context context, String NUMBER[], String account,
//                         String EMG_DATE, String GPS_LAT, String GPS_LNG,
//                         String GPS_ADDRESS, String GPS_ACCURACY, String GPS_STATUS,
//                         String MCC, String MNC, String LAC, String CELL_ID, String RSSI,
//                         String WIFI_MAC, String WIFI_Signal_dB, String WIFI_Channel,
//                         String CREATE_DATE) {
//
//        dbtc = new Create_Table(context);
//        db = dbtc.getWritableDatabase();
//        ContentValues cv = new ContentValues();
//        cv.put(TABLE_851302.account, account);
//        cv.put(TABLE_851302.EMG_DATE, EMG_DATE);
//        cv.put(TABLE_851302.GPS_LAT, GPS_LAT);
//        cv.put(TABLE_851302.GPS_LNG, GPS_LNG);
//        cv.put(TABLE_851302.GPS_ADDRESS, GPS_ADDRESS);
//        cv.put(TABLE_851302.GPS_ACCURACY, GPS_ACCURACY);
//        cv.put(TABLE_851302.GPS_STATUS, GPS_STATUS);
//        cv.put(TABLE_851302.MCC, MCC);
//        cv.put(TABLE_851302.MNC, MNC);
//        cv.put(TABLE_851302.LAC, LAC);
//        cv.put(TABLE_851302.CELL_ID, CELL_ID);
//        cv.put(TABLE_851302.RSSI, RSSI);
//        cv.put(TABLE_851302.WIFI_MAC, WIFI_MAC);
//        cv.put(TABLE_851302.WIFI_Signal_dB, WIFI_Signal_dB);
//        cv.put(TABLE_851302.WIFI_Channel, WIFI_Channel);
//        cv.put(TABLE_851302.CREATE_DATE, CREATE_DATE);
//
//        int update = db.update(TABLE_851302.TABLE_NAME, cv, TABLE_851302.NUMBER
//                + "= ?", NUMBER);
//        return update;
//    }
//
//    public int up_851303(Context context, String NUMBER[], String account,
//                         String FD_DATE,  String imei,     String ACTIVE_DATE, String CREATE_DATE) {
//        dbtc = new Create_Table(context);
//        db = dbtc.getWritableDatabase();
//        ContentValues cv = new ContentValues();
//        cv.put(TABLE_851303.account, account);
//        cv.put(TABLE_851303.FD_DATE, FD_DATE);
//        cv.put(TABLE_851303.imei, imei);
//        cv.put(TABLE_851303.ACTIVE_DATE, ACTIVE_DATE);
//        cv.put(TABLE_851303.CREATE_DATE, CREATE_DATE);
//
//        int update = db.update(TABLE_851303.TABLE_NAME, cv, TABLE_851303.NUMBER
//                + "= ?", NUMBER);
//        return update;
//    }

}

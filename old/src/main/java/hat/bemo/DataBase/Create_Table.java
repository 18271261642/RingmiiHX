package hat.bemo.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by apple on 2017/11/10.
 */

public class Create_Table extends SQLiteOpenHelper {
    private static final String DB_NAME = "Jianbudevice";
    private static final int DB_VERSION =  1001;

    public Create_Table(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public int getDbVersion(){
        return DB_VERSION;
    }

    public Create_Table(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
//        db.execSQL(TABLE_0x01.SQL_CREATE_TABLE);
//        db.execSQL(TABLE_0x10.SQL_CREATE_TABLE);
//        db.execSQL(TABLE_0x11.SQL_CREATE_TABLE);
//        db.execSQL(TABLE_0x18.SQL_CREATE_TABLE);
        db.execSQL(TABLE_0x20.SQL_CREATE_TABLE);
//        db.execSQL(TABLE_0x28.SQL_CREATE_TABLE);
//        db.execSQL(TABLE_0x29.SQL_CREATE_TABLE);
//        db.execSQL(TABLE_0x2A.SQL_CREATE_TABLE);
//        db.execSQL(TABLE_0x2B.SQL_CREATE_TABLE);
//        db.execSQL(TABLE_0x2E.SQL_CREATE_TABLE);
//        db.execSQL(TABLE_0x2F.SQL_CREATE_TABLE);
        db.execSQL(TABLE_0x30.SQL_CREATE_TABLE);
//        db.execSQL(TABLE_0x3F.SQL_CREATE_TABLE);
//        db.execSQL(TABLE_0x40.SQL_CREATE_TABLE);
//        db.execSQL(TABLE_0x4B.SQL_CREATE_TABLE);
//        db.execSQL(TABLE_0x4C.SQL_CREATE_TABLE);
//        db.execSQL(TABLE_0x4D.SQL_CREATE_TABLE);
//        db.execSQL(TABLE_0x4W.SQL_CREATE_TABLE);
//        db.execSQL(TABLE_0x7F.SQL_CREATE_TABLE);
//        db.execSQL(TABLE_851001.SQL_CREATE_TABLE);
//        db.execSQL(TABLE_851301.SQL_CREATE_TABLE);
//        db.execSQL(TABLE_851302.SQL_CREATE_TABLE);
//        db.execSQL(TABLE_851303.SQL_CREATE_TABLE);
//        db.execSQL(TABLE_0x01_Insert.SQL_CREATE_TABLE);
//        db.execSQL(TABLE_0x29_Insert.SQL_CREATE_TABLE);
//        //血壓APP
//        db.execSQL(Blood_Table.SQL_CREATE_TABLE);
//        //健步器
//        db.execSQL(Jianbudevice_Table.SQL_CREATE_TABLE);
//        db.execSQL(Time_Table.SQL_CREATE_TABLE);
//        db.execSQL(SettingPedometerTable.SQL_CREATE_TABLE);
//        //Missing Program
//        db.execSQL(TABLE_801603.SQL_CREATE_TABLE);

        try{
//            TABLE_0x01(db);
//            TABLE_0x10(db);
//            TABLE_0x11(db);
//            TABLE_0x18(db);
            TABLE_0x20(db);
//            TABLE_0x28(db);
//            TABLE_0x29(db);
//            TABLE_0x2A(db);
//            TABLE_0x2B(db);
//            TABLE_0x2E(db);
//            TABLE_0x2F(db);
			TABLE_0x30(db);
//            TABLE_0x3F(db);
//            TABLE_0x40(db);
//            TABLE_0x4B(db);
//            TABLE_0x4C(db);
//            TABLE_0x4D(db);
//            TABLE_0x4W(db);
//            TABLE_0x7F(db);
//            TABLE_851001(db);
//            TABLE_851301(db);
//            TABLE_851302(db);
//            TABLE_851303(db);
//            insert_SettingPedometer(db);
////			insert_Blood(db);
        }catch(SQLException e){
//            db.execSQL("DROP TABLE IF EXISTS "+ TABLE_0x01.SQL_CREATE_TABLE);
//            db.execSQL("DROP TABLE IF EXISTS "+ TABLE_0x10.SQL_CREATE_TABLE);
//            db.execSQL("DROP TABLE IF EXISTS "+ TABLE_0x11.SQL_CREATE_TABLE);
            db.execSQL("DROP TABLE IF EXISTS "+ TABLE_0x20.SQL_CREATE_TABLE);
//            db.execSQL("DROP TABLE IF EXISTS "+ TABLE_0x28.SQL_CREATE_TABLE);
//            db.execSQL("DROP TABLE IF EXISTS "+ TABLE_0x29.SQL_CREATE_TABLE);
//            db.execSQL("DROP TABLE IF EXISTS "+ TABLE_0x2A.SQL_CREATE_TABLE);
//            db.execSQL("DROP TABLE IF EXISTS "+ TABLE_0x2B.SQL_CREATE_TABLE);
//            db.execSQL("DROP TABLE IF EXISTS "+ TABLE_0x2E.SQL_CREATE_TABLE);
//            db.execSQL("DROP TABLE IF EXISTS "+ TABLE_0x2F.SQL_CREATE_TABLE);
            db.execSQL("DROP TABLE IF EXISTS "+ TABLE_0x30.SQL_CREATE_TABLE);
//            db.execSQL("DROP TABLE IF EXISTS "+ TABLE_0x3F.SQL_CREATE_TABLE);
//            db.execSQL("DROP TABLE IF EXISTS "+ TABLE_0x40.SQL_CREATE_TABLE);
//            db.execSQL("DROP TABLE IF EXISTS "+ TABLE_0x4B.SQL_CREATE_TABLE);
//            db.execSQL("DROP TABLE IF EXISTS "+ TABLE_0x4C.SQL_CREATE_TABLE);
//            db.execSQL("DROP TABLE IF EXISTS "+ TABLE_0x4D.SQL_CREATE_TABLE);
//            db.execSQL("DROP TABLE IF EXISTS "+ TABLE_0x4W.SQL_CREATE_TABLE);
//            db.execSQL("DROP TABLE IF EXISTS "+ TABLE_0x7F.SQL_CREATE_TABLE);
//            db.execSQL("DROP TABLE IF EXISTS "+Mon_Table.TABLE_NAME);
//            db.execSQL("DROP TABLE IF EXISTS "+Mon_Location_Table.TABLE_NAME);
//            db.execSQL("DROP TABLE IF EXISTS "+Mon_GPS_Table.TABLE_NAME);
//            db.execSQL("DROP TABLE IF EXISTS "+Blood_Table.TABLE_NAME);
            Log.e("SQLite","SQLite error");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        Log.e("", "oldVersion:"+oldVersion+"---->"+"newVersion:"+newVersion);
        /**
         * GC850.CN.GAP.V03.00.04
         * DB->1001
         * Insert Fall Table
         */
//        if(oldVersion == 1 & newVersion == 1001){
//            if(!(tabIsExist(db, TABLE_851001.TABLE_NAME))){
//                db.execSQL(TABLE_851001.SQL_CREATE_TABLE);
//                TABLE_851001(db);
//            }
//            if(!(tabIsExist(db, TABLE_851301.TABLE_NAME))){
//                db.execSQL(TABLE_851301.SQL_CREATE_TABLE);
//                TABLE_851301(db);
//            }
//            if(!(tabIsExist(db, TABLE_851302.TABLE_NAME))){
//                db.execSQL(TABLE_851302.SQL_CREATE_TABLE);
//                TABLE_851302(db);
//            }
//            if(!(tabIsExist(db, TABLE_851303.TABLE_NAME))){
//                db.execSQL(TABLE_851303.SQL_CREATE_TABLE);
//                TABLE_851303(db);
//            }
//            if(!(tabIsExist(db, TABLE_851303.TABLE_NAME))){
//                db.execSQL(TABLE_851303.SQL_CREATE_TABLE);
//                TABLE_851303(db);
//            }
//        }
    }

    /**
     * 檢查資料表是否存在
     * @param tabName 表名
     * @return
     */
    public boolean tabIsExist(SQLiteDatabase db, String tabName) {
        boolean result = false;
        Cursor cursor = null;
        try {
            String sql = "select * from Sqlite_master where type ='table' and name ='"+ tabName + "'";
            cursor = db.rawQuery(sql, null);
            if (cursor.moveToNext()) {
                int count = cursor.getInt(0);
                if(count > 0){
                    result = true;
                }
                result = true;
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return result;
    }

//    private void TABLE_0x01(SQLiteDatabase db){
//        ContentValues[] values = new ContentValues[1];
//        System.out.println("table_create");
//        for(int i=0; i<values.length; i++)
//            values[i] = new ContentValues();
//        values[0].put(TABLE_0x01.PR_COUNT, " ");
//        values[0].put(TABLE_0x01.PR_DATE, " ");
//        values[0].put(TABLE_0x01.VOLTAGE, " ");
//        values[0].put(TABLE_0x01.VOLTAGE_PERCENT, " ");
//        values[0].put(TABLE_0x01.LAC, " ");
//        values[0].put(TABLE_0x01.MNC, " ");
//        values[0].put(TABLE_0x01.RSSI, " ");
//        values[0].put(TABLE_0x01.CELL_ID, " ");
//        values[0].put(TABLE_0x01.MCC, " ");
//        values[0].put(TABLE_0x01.GSensor_AVG, " ");
//        values[0].put(TABLE_0x01.GSensor_MAX, " ");
//        values[0].put(TABLE_0x01.PPM, " ");
//        values[0].put(TABLE_0x01.GPS_STATUS, " ");
//        values[0].put(TABLE_0x01.GPS_LAT, " ");
//        values[0].put(TABLE_0x01.GPS_LNG, " ");
//        values[0].put(TABLE_0x01.GPS_ADDRESS, " ");
//        values[0].put(TABLE_0x01.GPS_ACCURACY, " ");
//        values[0].put(TABLE_0x01.WIFI_MAC, " ");
//        values[0].put(TABLE_0x01.WIFI_Signal_dB, " ");
//        values[0].put(TABLE_0x01.WIFI_Channel, " ");
//
//        for(ContentValues row : values){
//            db.insert(TABLE_0x01.TABLE_NAME, null, row);
//        }
//    }

//    private void TABLE_0x10(SQLiteDatabase db){
//        ContentValues[] values = new ContentValues[1];
//        for(int i=0; i<values.length; i++)
//            values[i] = new ContentValues();
////			values[0].put(TABLE_0x10.TIME_NO, "1");
//        values[0].put(TABLE_0x10.AUTOREAD, " ");
//        values[0].put(TABLE_0x10.CHECKSUM, " ");
//        values[0].put(TABLE_0x10.DIALOUT_LIMIT_STARTDAY, " ");
//        values[0].put(TABLE_0x10.ECHO_GPS_T, " ");
//        values[0].put(TABLE_0x10.ECHO_PR_T, " ");
//        values[0].put(TABLE_0x10.PHONE_LIMIT_BY_CUSTOMER, " ");
//        values[0].put(TABLE_0x10.PHONE_LIMIT_MODE, " ");
//        values[0].put(TABLE_0x10.PHONE_LIMIT_ONOFF, " ");
//        values[0].put(TABLE_0x10.PHONE_LIMIT_TIME, " ");
//        values[0].put(TABLE_0x10.POWERON_LOGO, " ");
//        values[0].put(TABLE_0x10.SERVER_DNS, " ");
//        values[0].put(TABLE_0x10.SERVER_IP_PORT, " ");
//
//        for(ContentValues row : values){
//            db.insert(TABLE_0x10.TABLE_NAME, null, row);
//        }
//    }

//    private void TABLE_0x11(SQLiteDatabase db){
//        ContentValues[] values = new ContentValues[1];
//        for(int i=0; i<values.length; i++)
//            values[i] = new ContentValues();
//        values[0].put(TABLE_0x11.CHECKSUM, " ");
//        values[0].put(TABLE_0x11.FALL_DETECT, " ");
//        values[0].put(TABLE_0x11.FALL_PHONE_NAME1, " ");
//        values[0].put(TABLE_0x11.FALL_PHONE_NAME2, " ");
//        values[0].put(TABLE_0x11.FALL_PHONE_NAME3, " ");
//        values[0].put(TABLE_0x11.FALL_PHONE_NO1, " ");
//        values[0].put(TABLE_0x11.FALL_PHONE_NO2, " ");
//        values[0].put(TABLE_0x11.FALL_PHONE_NO3, " ");
//        values[0].put(TABLE_0x11.FALL_SMS, " ");
//        values[0].put(TABLE_0x11.FAMILY_PHONE_NAME1, " ");
//        values[0].put(TABLE_0x11.FAMILY_PHONE_NAME2, " ");
//        values[0].put(TABLE_0x11.FAMILY_PHONE_NAME3, " ");
//        values[0].put(TABLE_0x11.FAMILY_PHONE_NO1, " ");
//        values[0].put(TABLE_0x11.FAMILY_PHONE_NO2, " ");
//        values[0].put(TABLE_0x11.FAMILY_PHONE_NO3, " ");
//        values[0].put(TABLE_0x11.FMLY_KEY_DELAY, " ");
//        values[0].put(TABLE_0x11.MED_TIME_1, " ");
//        values[0].put(TABLE_0x11.SWITCH, " ");
//        values[0].put(TABLE_0x11.TIME, " ");
//        values[0].put(TABLE_0x11.WEEK1, " ");
//        values[0].put(TABLE_0x11.WEEK2, " ");
//        values[0].put(TABLE_0x11.WEEK3, " ");
//        values[0].put(TABLE_0x11.WEEK4, " ");
//        values[0].put(TABLE_0x11.WEEK5, " ");
//        values[0].put(TABLE_0x11.WEEK6, " ");
//        values[0].put(TABLE_0x11.WEEK7, " ");
//        values[0].put(TABLE_0x11.MED_TIME_2, " ");
//        values[0].put(TABLE_0x11.MED_TIME_3, " ");
//        values[0].put(TABLE_0x11.MED_TIME_4, " ");
//        values[0].put(TABLE_0x11.MED_TIME_5, " ");
//        values[0].put(TABLE_0x11.NON_DISTRUB, " ");
//        values[0].put(TABLE_0x11.PERSONAL_INFO, " ");
//        values[0].put(TABLE_0x11.ACCOUNTID, " ");
//        values[0].put(TABLE_0x11.AGE, "0");
//        values[0].put(TABLE_0x11.BIRTHDATE_DD, " ");
//        values[0].put(TABLE_0x11.BIRTHDATE_MM, " ");
//        values[0].put(TABLE_0x11.BIRTHDATE_YY, " ");
//        values[0].put(TABLE_0x11.HEIGHT, " ");
//        values[0].put(TABLE_0x11.IDEAL_WEIGHT, " ");
//        values[0].put(TABLE_0x11.NAME, " ");
//        values[0].put(TABLE_0x11.SEX, " ");
//        values[0].put(TABLE_0x11.STEP, " ");
//        values[0].put(TABLE_0x11.UNIT, " ");
//        values[0].put(TABLE_0x11.WEIGHT, " ");
//        values[0].put(TABLE_0x11.RETURN_DT_1, " ");
//        values[0].put(TABLE_0x11.DAY, " ");
//        values[0].put(TABLE_0x11.MONTH, " ");
//        values[0].put(TABLE_0x11.YEAR, " ");
//        values[0].put(TABLE_0x11.SET_LANG, " ");
//        values[0].put(TABLE_0x11.SOS_KEY_DELAY, " ");
//        values[0].put(TABLE_0x11.SOS_PHONE_NAME1, " ");
//        values[0].put(TABLE_0x11.SOS_PHONE_NAME2, " ");
//        values[0].put(TABLE_0x11.SOS_PHONE_NAME3, " ");
//        values[0].put(TABLE_0x11.SOS_PHONE_NO1, "112");
//        values[0].put(TABLE_0x11.SOS_PHONE_NO2, "112");
//        values[0].put(TABLE_0x11.SOS_PHONE_NO3, "112");
//        values[0].put(TABLE_0x11.SOS_SMS, " ");
//        values[0].put(TABLE_0x11.TIMEZONE, " ");
//        values[0].put(TABLE_0x11.WHITE_LIST_ON, " ");
//        values[0].put(TABLE_0x11.ELECTRIC_FENCE, " ");
//        values[0].put(TABLE_0x11.PHONE, " ");
//
//        for(ContentValues row : values){
//            db.insert(TABLE_0x11.TABLE_NAME, null, row);
//        }
//    }

//    private void TABLE_0x18(SQLiteDatabase db){
//        ContentValues[] values = new ContentValues[1];
//        for(int i=0; i<values.length; i++)
//            values[i] = new ContentValues();
//        values[0].put(TABLE_0x18.account, " ");
//        values[0].put(TABLE_0x18.TRACKING_DATE, " ");
//        values[0].put(TABLE_0x18.GPS_LAT, " ");
//        values[0].put(TABLE_0x18.GPS_LNG, " ");
//        values[0].put(TABLE_0x18.GPS_ADDRESS, " ");
//        values[0].put(TABLE_0x18.GPS_ACCURACY, " ");
//        values[0].put(TABLE_0x18.GPS_STATUS, " ");
//        values[0].put(TABLE_0x18.MCC, " ");
//        values[0].put(TABLE_0x18.MNC, " ");
//        values[0].put(TABLE_0x18.LAC, " ");
//        values[0].put(TABLE_0x18.CELL_ID, " ");
//        values[0].put(TABLE_0x18.RSSI, " ");
//        values[0].put(TABLE_0x18.WIFI_MAC, " ");
//        values[0].put(TABLE_0x18.WIFI_Signal_dB, " ");
//        values[0].put(TABLE_0x18.WIFI_Channel, " ");
//        values[0].put(TABLE_0x18.CREATE_DATE, " ");
//        for(ContentValues row : values){
//            db.insert(TABLE_0x18.TABLE_NAME, null, row);
//        }
//    }

    private void TABLE_0x20(SQLiteDatabase db){
        ContentValues[] values = new ContentValues[1];
        for(int i=0; i<values.length; i++)
            values[i] = new ContentValues();
        values[0].put(TABLE_0x20.EMG_DATE, " ");
        values[0].put(TABLE_0x20.GPS_LAT, " ");
        values[0].put(TABLE_0x20.GPS_LNG, " ");
        values[0].put(TABLE_0x20.GPS_ADDRESS, " ");
        values[0].put(TABLE_0x20.GPS_ACCURACY, " ");
        values[0].put(TABLE_0x20.GPS_STATUS, " ");
        values[0].put(TABLE_0x20.MCC, " ");
        values[0].put(TABLE_0x20.MNC, " ");
        values[0].put(TABLE_0x20.LAC, " ");
        values[0].put(TABLE_0x20.CELL_ID, " ");
        values[0].put(TABLE_0x20.RSSI, " ");
        values[0].put(TABLE_0x20.WIFI_MAC, " ");
        values[0].put(TABLE_0x20.WIFI_Signal_dB, " ");
        values[0].put(TABLE_0x20.WIFI_Channel, " ");
        values[0].put(TABLE_0x20.CREATE_DATE, " ");

        for(ContentValues row : values){
            db.insert(TABLE_0x20.TABLE_NAME, null, row);
        }
    }

//    private void TABLE_0x28(SQLiteDatabase db){
//        ContentValues[] values = new ContentValues[1];
//        for(int i=0; i<values.length; i++)
//            values[i] = new ContentValues();
//        values[0].put(TABLE_0x28.EMG_DATE, " ");
//        values[0].put(TABLE_0x28.MCC, " ");
//        values[0].put(TABLE_0x28.MNC, " ");
//        values[0].put(TABLE_0x28.LAC, " ");
//        values[0].put(TABLE_0x28.CELL_ID, " ");
//        values[0].put(TABLE_0x28.RSSI, " ");
//        values[0].put(TABLE_0x28.GPS_STATUS, " ");
//        values[0].put(TABLE_0x28.GPS_LAT, " ");
//        values[0].put(TABLE_0x28.GPS_LNG, " ");
//        values[0].put(TABLE_0x28.GPS_ACCURACY, " ");
//        values[0].put(TABLE_0x28.GPS_ADDRESS, " ");
//        values[0].put(TABLE_0x28.WIFI_MAC, " ");
//        values[0].put(TABLE_0x28.WIFI_Signal_dB, " ");
//        values[0].put(TABLE_0x28.WIFI_Channel, " ");
//        values[0].put(TABLE_0x28.CREATE_DATE, " ");
//
//        for(ContentValues row : values){
//            db.insert(TABLE_0x28.TABLE_NAME, null, row);
//        }
//    }

//    private void TABLE_0x29(SQLiteDatabase db){
//        ContentValues[] values = new ContentValues[1];
//        for(int i=0; i<values.length; i++)
//            values[i] = new ContentValues();
//        values[0].put(TABLE_0x29.EF_COUNT, " ");
//        values[0].put(TABLE_0x29.EF_DATE, " ");
//        values[0].put(TABLE_0x29.VOLTAGE, " ");
//        values[0].put(TABLE_0x29.VOLTAGE_PERCENT, " ");
//        values[0].put(TABLE_0x29.LAC, " ");
//        values[0].put(TABLE_0x29.MNC, " ");
//        values[0].put(TABLE_0x29.RSSI, " ");
//        values[0].put(TABLE_0x29.CELL_ID, " ");
//        values[0].put(TABLE_0x29.MCC, " ");
//        values[0].put(TABLE_0x29.GSensor_AVG, " ");
//        values[0].put(TABLE_0x29.GSensor_MAX, " ");
//        values[0].put(TABLE_0x29.TEMP, " ");
//        values[0].put(TABLE_0x29.PPM, " ");
//        values[0].put(TABLE_0x29.CELLER_LAC, " ");
//        values[0].put(TABLE_0x29.CELLER_MNC, " ");
//        values[0].put(TABLE_0x29.CELLER_RSSI, " ");
//        values[0].put(TABLE_0x29.CELLER_CELL_ID, " ");
//        values[0].put(TABLE_0x29.CELLER_MCC, " ");
//        values[0].put(TABLE_0x29.GPS_STATUS, " ");
//        values[0].put(TABLE_0x29.GPS_LAT, " ");
//        values[0].put(TABLE_0x29.GPS_LNG, " ");
//        values[0].put(TABLE_0x29.GPS_ACCURACY, " ");
//        values[0].put(TABLE_0x29.GPS_ADDRESS, " ");
//        values[0].put(TABLE_0x29.ELECTRIC_FENCE_ITEMNO, " ");
//        values[0].put(TABLE_0x29.WIFI_MAC, " ");
//        values[0].put(TABLE_0x29.WIFI_Signal_dB, " ");
//        values[0].put(TABLE_0x29.WIFI_Channel, " ");
//        values[0].put(TABLE_0x29.CREATE_DATE, " ");
//        for(ContentValues row : values){
//            db.insert(TABLE_0x29.TABLE_NAME, null, row);
//        }
//    }

//    private void TABLE_0x2A(SQLiteDatabase db){
//        ContentValues[] values = new ContentValues[1];
//        for(int i=0; i<values.length; i++)
//            values[i] = new ContentValues();
//        values[0].put(TABLE_0x2A.DEVICE_TOKEN, "APA91bENHRzhxTHQ6f5C7Id0lUNc3W7iHLLZ09oU6YYOoPkUiGG5Oqs8nRkZMMinqyuaAezjujmAS--A2P1asdY6rwJGWstJWTpdsFGCbp1NaG75tL9eozplMfI5InAHkTOFLXYTQHshE0AxsyAeKt_Oj_gPuX-mWg");
//        values[0].put(TABLE_0x2A.SIGNATURE, "APA91bENHRzhxTHQ6f5C7Id0lUNc3W7iHLLZ09oU6YYOoPkUiGG5Oqs8nRkZMMinqyuaAezjujmAS--A2P1asdY6rwJGWstJWTpdsFGCbp1NaG75tL9eozplMfI5InAHkTOFLXYTQHshE0AxsyAeKt_Oj_gPuX-mWg");
//        values[0].put(TABLE_0x2A.CREATE_DATE, " ");
//
//
//        for(ContentValues row : values){
//            db.insert(TABLE_0x2A.TABLE_NAME, null, row);
//        }
//    }

//    private void TABLE_0x2B(SQLiteDatabase db){
//        ContentValues[] values = new ContentValues[1];
//        for(int i=0; i<values.length; i++)
//            values[i] = new ContentValues();
//        values[0].put(TABLE_0x2B.ALERT_TYPE, " ");
//
//        for(ContentValues row : values){
//            db.insert(TABLE_0x2B.TABLE_NAME, null, row);
//        }
//    }

//    private void TABLE_0x2E(SQLiteDatabase db){
//        ContentValues[] values = new ContentValues[1];
//        for(int i=0; i<values.length; i++)
//            values[i] = new ContentValues();
//        values[0].put(TABLE_0x2E.PWR_ON_DATE, " ");
//        values[0].put(TABLE_0x2E.FW_BUILD_DATE, " ");
//        values[0].put(TABLE_0x2E.VOLTAGE, " ");
//        values[0].put(TABLE_0x2E.VOLTAGE_PERCENT, " ");
//        values[0].put(TABLE_0x2E.MCC, " ");
//        values[0].put(TABLE_0x2E.MNC, " ");
//        values[0].put(TABLE_0x2E.LAC, " ");
//        values[0].put(TABLE_0x2E.CELL_ID, " ");
//        values[0].put(TABLE_0x2E.RSSI, " ");
//        values[0].put(TABLE_0x2E.CREATE_DATE, " ");
//
//        for(ContentValues row : values){
//            db.insert(TABLE_0x2E.TABLE_NAME, null, row);
//        }
//    }

//    private void TABLE_0x2F(SQLiteDatabase db){
//        ContentValues[] values = new ContentValues[1];
//        for(int i=0; i<values.length; i++)
//            values[i] = new ContentValues();
//        values[0].put(TABLE_0x2F.PWR_ON_DATE, "0");
//        values[0].put(TABLE_0x2F.FW_BUILD_DATE, "0");
//        values[0].put(TABLE_0x2F.VOLTAGE, "0");
//        values[0].put(TABLE_0x2F.VOLTAGE_PERCENT, "0");
//        values[0].put(TABLE_0x2F.MCC, "0");
//        values[0].put(TABLE_0x2F.MNC, "0");
//        values[0].put(TABLE_0x2F.LAC, "0");
//        values[0].put(TABLE_0x2F.CELL_ID, "0");
//        values[0].put(TABLE_0x2F.RSSI, "0");
//        values[0].put(TABLE_0x2F.CREATE_DATE, "0");
//        values[0].put(TABLE_0x2F.GPS_ACCURACY, "0");
//        values[0].put(TABLE_0x2F.GPS_LAT, "0");
//        values[0].put(TABLE_0x2F.GPS_LNG, "0");
//        values[0].put(TABLE_0x2F.GPS_STATUS, "0");
//        values[0].put(TABLE_0x2F.PWR_OFF_DATE, "0");
//        values[0].put(TABLE_0x2F.PWR_OFF_TYPE, "0");
//        values[0].put(TABLE_0x2F.account, "0");
//
//        for(ContentValues row : values){
//            db.insert(TABLE_0x2F.TABLE_NAME, null, row);
//        }
//    }

    private void TABLE_0x30(SQLiteDatabase db){
        ContentValues[] values = new ContentValues[1];
        for(int i=0; i<values.length; i++)
            values[i] = new ContentValues();
        values[0].put(TABLE_0x30.EMG_DATE, " ");
        values[0].put(TABLE_0x30.MCC, " ");
        values[0].put(TABLE_0x30.MNC, " ");
        values[0].put(TABLE_0x30.LAC, " ");
        values[0].put(TABLE_0x30.CELL_ID, " ");
        values[0].put(TABLE_0x30.RSSI, " ");
        values[0].put(TABLE_0x30.GPS_STATUS, " ");
        values[0].put(TABLE_0x30.GPS_LAT, " ");
        values[0].put(TABLE_0x30.GPS_LNG, " ");
        values[0].put(TABLE_0x30.GPS_ACCURACY, " ");
        values[0].put(TABLE_0x30.GPS_ADDRESS, " ");
        values[0].put(TABLE_0x30.WIFI_MAC, " ");
        values[0].put(TABLE_0x30.WIFI_Signal_dB, " ");
        values[0].put(TABLE_0x30.WIFI_Channel, " ");
        values[0].put(TABLE_0x30.CREATE_DATE, " ");

        for(ContentValues row : values){
            db.insert(TABLE_0x30.TABLE_NAME, null, row);
        }
    }

//    private void TABLE_0x3F(SQLiteDatabase db){
//        ContentValues[] values = new ContentValues[1];
//        for(int i=0; i<values.length; i++)
//            values[i] = new ContentValues();
//        values[0].put(TABLE_0x3F.PHONE, " ");
//        values[0].put(TABLE_0x3F.START_TIME, " ");
//        values[0].put(TABLE_0x3F.END_TIME, " ");
//        values[0].put(TABLE_0x3F.CALL_TYPE, " ");
//        values[0].put(TABLE_0x3F.CREATE_DATE, " ");
//
//        for(ContentValues row : values){
//            db.insert(TABLE_0x3F.TABLE_NAME, null, row);
//        }
//    }

//    private void TABLE_0x40(SQLiteDatabase db){
//        ContentValues[] values = new ContentValues[1];
//        for(int i=0; i<values.length; i++)
//            values[i] = new ContentValues();
//        values[0].put(TABLE_0x40.SERVER_IP_PORT, " ");
//        values[0].put(TABLE_0x40.SERVER_DNS, " ");
//
//        for(ContentValues row : values){
//            db.insert(TABLE_0x40.TABLE_NAME, null, row);
//        }
//    }

//    private void TABLE_0x4B(SQLiteDatabase db){
//        ContentValues[] values = new ContentValues[1];
//        for(int i=0; i<values.length; i++)
//            values[i] = new ContentValues();
//        values[0].put(TABLE_0x4B.URL, "0x00;0x00;0x00");
//        values[0].put(TABLE_0x4B.FAMILY_PHOTO, "0x00;0x00;0x00");
//
//        for(ContentValues row : values){
//            db.insert(TABLE_0x4B.TABLE_NAME, null, row);
//        }
//    }

//    private void TABLE_0x4C(SQLiteDatabase db){
//        ContentValues[] values = new ContentValues[1];
//        for(int i=0; i<values.length; i++)
//            values[i] = new ContentValues();
//        values[0].put(TABLE_0x4C.URL, "0x00;0x00;0x00");
//        values[0].put(TABLE_0x4C.DISPLAY_PHOTO, "0x00;0x00;0x00");
//
//        for(ContentValues row : values){
//            db.insert(TABLE_0x4C.TABLE_NAME, null, row);
//        }
//    }

//    private void TABLE_0x4D(SQLiteDatabase db){
//        ContentValues[] values = new ContentValues[1];
//        for(int i=0; i<values.length; i++)
//            values[i] = new ContentValues();
//        values[0].put(TABLE_0x4D.GROUP_ID, " ");
//        values[0].put(TABLE_0x4D.NAME, " ");
//        values[0].put(TABLE_0x4D.PHONE, " ");
//        values[0].put(TABLE_0x4D.PHOTO_URL, " ");
//        values[0].put(TABLE_0x4D.PHONE_LIST, " ");
//
//        for(ContentValues row : values){
//            db.insert(TABLE_0x4D.TABLE_NAME, null, row);
//        }
//    }

//    private void TABLE_0x4W(SQLiteDatabase db){
//        ContentValues[] values = new ContentValues[1];
//        for(int i=0; i<values.length; i++)
//            values[i] = new ContentValues();
//        values[0].put(TABLE_0x4W.dressing_Suggestions, " ");
//        values[0].put(TABLE_0x4W.rain_Probability, " ");
//        values[0].put(TABLE_0x4W.temperature_High, " ");
//        values[0].put(TABLE_0x4W.temperature_Low, " ");
//        values[0].put(TABLE_0x4W.weather_Code, " ");
//
//        for(ContentValues row : values){
//            db.insert(TABLE_0x4W.TABLE_NAME, null, row);
//        }
//    }

//    private void TABLE_0x7F(SQLiteDatabase db){
//        ContentValues[] values = new ContentValues[1];
//        for(int i=0; i<values.length; i++)
//            values[i] = new ContentValues();
//        values[0].put(TABLE_0x7F.CHECKSUM_10, "0");
//        values[0].put(TABLE_0x7F.CHECKSUM_11, "0");
//        values[0].put(TABLE_0x7F.ECHO_TYPE, "0");
//
//        for(ContentValues row : values){
//            db.insert(TABLE_0x7F.TABLE_NAME, null, row);
//        }
//    }
    //運動
//    private void insert_Mon(SQLiteDatabase db){
//        ContentValues[] values = new ContentValues[1];
//        for(int i=0; i<values.length; i++)
//            values[i] = new ContentValues();
//        values[0].put(Mon_Table.OVERVIEW_NO, "0");
//        values[0].put(Mon_Table.RUN_NO, "11");
//        values[0].put(Mon_Table.MODEL, "0");
//        values[0].put(Mon_Table.IMEI, "0");
//        values[0].put(Mon_Table.S_TIME, "12:00:00");
//        values[0].put(Mon_Table.E_TIME, "13:00:00");
//        values[0].put(Mon_Table.SNO, "0");
//        values[0].put(Mon_Table.WEIGHT, "0");
//        values[0].put(Mon_Table.CREATE_DATE, "00:00");
//        values[0].put(Mon_Table.SETTING_TIME, "0");
//        values[0].put(Mon_Table.SETTING_KM, "0");
//        values[0].put(Mon_Table.SETTING_CAL, "00:00");
//        values[0].put(Mon_Table.DATETIME, "2014/12/31 13:00:00");
//        values[0].put(Mon_Table.HZ, "0");
//
//        for(ContentValues row : values){
//            db.insert(Mon_Table.TABLE_NAME, null, row);
//        }
//    }

//    private void insert_Mon_Location_Table(SQLiteDatabase db){
//        ContentValues[] values = new ContentValues[1];
//        for(int i=0; i<values.length; i++)
//            values[i] = new ContentValues();
//        values[0].put(Mon_Location_Table.OVERVIEW_NO, "0");
//        values[0].put(Mon_Location_Table.IMEI, "0");
//        values[0].put(Mon_Location_Table.ITEMNO, "11");
//        values[0].put(Mon_Location_Table.GPS_STATUS, "0");
//        values[0].put(Mon_Location_Table.MY_GPS_LAT, "0");
//        values[0].put(Mon_Location_Table.MY_GPS_LNG, "0");
//        values[0].put(Mon_Location_Table.SPEED, "0");
//        values[0].put(Mon_Location_Table.CAL, "0");
//        values[0].put(Mon_Location_Table.DISTANCE, "0");
//        values[0].put(Mon_Location_Table.STEPS, "0");
//        values[0].put(Mon_Location_Table.TOTAL_STEPS, "0");
//        values[0].put(Mon_Location_Table.DIFFERENCE_OF_HEIGHT, "0");
//        values[0].put(Mon_Location_Table.HPE, "0");
//        values[0].put(Mon_Location_Table.GPS_TIME, "0");
//        values[0].put(Mon_Location_Table.DATETIME, "2014/12/31 13:00:00");
//        values[0].put(Mon_Location_Table.CREATE_DATE, "0");
//        values[0].put(Mon_Location_Table.UPDATE_DATE, "0");
//
//        for(ContentValues row : values){
//            db.insert(Mon_Location_Table.TABLE_NAME, null, row);
//        }
//    }
    //血壓
//    private void insert_Blood(SQLiteDatabase db){
//        ContentValues[] values = new ContentValues[7];
//        for(int i=0; i<values.length; i++)
//            values[i] = new ContentValues();
//        System.out.println("table_Create");
//        values[0].put(Blood_Table.H_PRESSURE, "137");
//        values[0].put(Blood_Table.L_PRESSURE, "125");
//        values[0].put(Blood_Table.PLUS, "79");
//        values[0].put(Blood_Table.BS_TIME, "2014/09/12");
//        values[0].put(Blood_Table.CREATE_DATE, "2014/09/12");
//        values[0].put(Blood_Table.UPDATE_DATE, "2014/09/12 12:22:11");
//        values[0].put(Blood_Table.DATETIME, "2014/09/12 12:22:11");
//        values[0].put(Blood_Table.IMEI, "1000");
//
//        values[1].put(Blood_Table.H_PRESSURE, "120");
//        values[1].put(Blood_Table.L_PRESSURE, "100");
//        values[1].put(Blood_Table.PLUS, "80");
//        values[1].put(Blood_Table.BS_TIME, "2014/09/12");
//        values[1].put(Blood_Table.CREATE_DATE, "2014/09/12");
//        values[1].put(Blood_Table.UPDATE_DATE, "2014/09/12 12:22:11");
//        values[1].put(Blood_Table.DATETIME, "2014/09/12 12:22:11");
//        values[1].put(Blood_Table.IMEI, "1000");
//
//        values[2].put(Blood_Table.H_PRESSURE, "139");
//        values[2].put(Blood_Table.L_PRESSURE, "130");
//        values[2].put(Blood_Table.PLUS, "101");
//        values[2].put(Blood_Table.BS_TIME, "2014/09/12");
//        values[2].put(Blood_Table.CREATE_DATE, "2014/09/12");
//        values[2].put(Blood_Table.UPDATE_DATE, "2014/09/12 12:22:11");
//        values[2].put(Blood_Table.DATETIME, "2014/09/12 12:22:11");
//        values[2].put(Blood_Table.IMEI, "1000");
//
//        values[3].put(Blood_Table.H_PRESSURE, "110");
//        values[3].put(Blood_Table.L_PRESSURE, "123");
//        values[3].put(Blood_Table.PLUS, "90");
//        values[3].put(Blood_Table.BS_TIME, "2014/09/12");
//        values[3].put(Blood_Table.CREATE_DATE, "2014/09/12");
//        values[3].put(Blood_Table.UPDATE_DATE, "2014/09/12 12:22:11");
//        values[3].put(Blood_Table.DATETIME, "2014/09/12 12:22:11");
//        values[3].put(Blood_Table.IMEI, "1000");
//
//        values[4].put(Blood_Table.H_PRESSURE, "134");
//        values[4].put(Blood_Table.L_PRESSURE, "115");
//        values[4].put(Blood_Table.PLUS, "110");
//        values[4].put(Blood_Table.BS_TIME, "2014/09/12");
//        values[4].put(Blood_Table.CREATE_DATE, "2014/09/12");
//        values[4].put(Blood_Table.UPDATE_DATE, "2014/09/12 12:22:11");
//        values[4].put(Blood_Table.DATETIME, "2014/09/12 12:22:11");
//        values[4].put(Blood_Table.IMEI, "1000");
//
//        values[5].put(Blood_Table.H_PRESSURE, "110");
//        values[5].put(Blood_Table.L_PRESSURE, "135");
//        values[5].put(Blood_Table.PLUS, "80");
//        values[5].put(Blood_Table.BS_TIME, "2014/09/12");
//        values[5].put(Blood_Table.CREATE_DATE, "2014/09/12");
//        values[5].put(Blood_Table.UPDATE_DATE, "2014/09/12 12:22:11");
//        values[5].put(Blood_Table.DATETIME, "2014/09/12 12:22:11");
//        values[5].put(Blood_Table.IMEI, "1000");
//
//        values[6].put(Blood_Table.H_PRESSURE, "120");
//        values[6].put(Blood_Table.L_PRESSURE, "110");
//        values[6].put(Blood_Table.PLUS, "110");
//        values[6].put(Blood_Table.BS_TIME, "2014/09/12");
//        values[6].put(Blood_Table.CREATE_DATE, "2014/09/12");
//        values[6].put(Blood_Table.UPDATE_DATE, "2014/09/12 12:22:11");
//        values[6].put(Blood_Table.DATETIME, "2014/09/12 12:22:11");
//        values[6].put(Blood_Table.IMEI, "1000");
//
//        for(ContentValues row : values){
//            db.insert(Blood_Table.TABLE_NAME, null, row);
//        }
//    }

//    private void insert_SettingPedometer(SQLiteDatabase db){
//        ContentValues[] values = new ContentValues[1];
//        for(int i=0; i<values.length; i++)
//            values[i] = new ContentValues();
//        values[0].put(SettingPedometerTable.OVERVIEW_NO, "1");
//        values[0].put(SettingPedometerTable.userAccount, "0");
//        values[0].put(SettingPedometerTable.account, "0");
//        values[0].put(SettingPedometerTable.timeStamp, "0");
//        values[0].put(SettingPedometerTable.data, "0");
//        values[0].put(SettingPedometerTable.StepRange, "60.0");
//        values[0].put(SettingPedometerTable.WeightRange, "60.0");
//        values[0].put(SettingPedometerTable.Distance, "0");
//        values[0].put(SettingPedometerTable.StepCount, "5000");
//        values[0].put(SettingPedometerTable.role, "0");
//        values[0].put(SettingPedometerTable.DBrole, "0");
//
//        for(ContentValues row : values){
//            db.insert(SettingPedometerTable.TABLE_NAME, null, row);
//        }
//    }

//    private void TABLE_851001(SQLiteDatabase db){
//        ContentValues[] values = new ContentValues[1];
//        for(int i=0; i<values.length; i++)
//            values[i] = new ContentValues();
//        values[0].put(TABLE_851001.account, "");
//        values[0].put(TABLE_851001.fallDetectSwitch, "0");
//        values[0].put(TABLE_851001.delayTimer, "15");
//        values[0].put(TABLE_851001.serviceIP, "");
//        values[0].put(TABLE_851001.fromtime, "00:00");
//        values[0].put(TABLE_851001.totime, "23:59");
//        values[0].put(TABLE_851001.week, "1,2,3,4,5,6,7");
//        values[0].put(TABLE_851001.enable, "1");
//        values[0].put(TABLE_851001.phone1, "0912345678");
//        values[0].put(TABLE_851001.phone2, "0912345678");
//        values[0].put(TABLE_851001.phone3, "0912345678");
//        values[0].put(TABLE_851001.CREATE_DATE, ChangeDateFormat.CreateDate());
//        for(ContentValues row : values){
//            db.insert(TABLE_851001.TABLE_NAME, null, row);
//        }
//    }

//    private void TABLE_851301(SQLiteDatabase db){
//        ContentValues[] values = new ContentValues[1];
//        for(int i=0; i<values.length; i++)
//            values[i] = new ContentValues();
//        values[0].put(TABLE_851301.account, "");
//        values[0].put(TABLE_851301.EMG_DATE, ChangeDateFormat.CreateDate());
//        values[0].put(TABLE_851301.GPS_LAT, "");
//        values[0].put(TABLE_851301.GPS_LNG, "");
//        values[0].put(TABLE_851301.GPS_ADDRESS, "");
//        values[0].put(TABLE_851301.GPS_ACCURACY, "");
//        values[0].put(TABLE_851301.GPS_STATUS, "");
//        values[0].put(TABLE_851301.MCC, "");
//        values[0].put(TABLE_851301.MNC, "");
//        values[0].put(TABLE_851301.LAC, "");
//        values[0].put(TABLE_851301.CELL_ID, "");
//        values[0].put(TABLE_851301.RSSI, "");
//        values[0].put(TABLE_851301.WIFI_MAC, "");
//        values[0].put(TABLE_851301.WIFI_Signal_dB, "");
//        values[0].put(TABLE_851301.WIFI_Channel, "");
//        values[0].put(TABLE_851301.CREATE_DATE, ChangeDateFormat.CreateDate());
//        for(ContentValues row : values){
//            db.insert(TABLE_851301.TABLE_NAME, null, row);
//        }
//    }

//    private void TABLE_851302(SQLiteDatabase db){
//        ContentValues[] values = new ContentValues[1];
//        for(int i=0; i<values.length; i++)
//            values[i] = new ContentValues();
//        values[0].put(TABLE_851302.account, "");
//        values[0].put(TABLE_851302.EMG_DATE, ChangeDateFormat.CreateDate());
//        values[0].put(TABLE_851302.GPS_LAT, "");
//        values[0].put(TABLE_851302.GPS_LNG, "");
//        values[0].put(TABLE_851302.GPS_ADDRESS, "");
//        values[0].put(TABLE_851302.GPS_ACCURACY, "");
//        values[0].put(TABLE_851302.GPS_STATUS, "");
//        values[0].put(TABLE_851302.MCC, "");
//        values[0].put(TABLE_851302.MNC, "");
//        values[0].put(TABLE_851302.LAC, "");
//        values[0].put(TABLE_851302.CELL_ID, "");
//        values[0].put(TABLE_851302.RSSI, "");
//        values[0].put(TABLE_851302.WIFI_MAC, "");
//        values[0].put(TABLE_851302.WIFI_Signal_dB, "");
//        values[0].put(TABLE_851302.WIFI_Channel, "");
//        values[0].put(TABLE_851302.CREATE_DATE, ChangeDateFormat.CreateDate());
//        for(ContentValues row : values){
//            db.insert(TABLE_851302.TABLE_NAME, null, row);
//        }
//    }

//    private void TABLE_851303(SQLiteDatabase db){
//        ContentValues[] values = new ContentValues[1];
//        for(int i=0; i<values.length; i++)
//            values[i] = new ContentValues();
//        values[0].put(TABLE_851303.account, "");
//        values[0].put(TABLE_851303.FD_DATE, ChangeDateFormat.CreateDate());
//        values[0].put(TABLE_851303.imei, "");
//        values[0].put(TABLE_851303.status, "");
//        values[0].put(TABLE_851303.ACTIVE_DATE, new SimpleDateFormat("yyMMddHH").format(new Date().getTime()));
//        values[0].put(TABLE_851303.CREATE_DATE, ChangeDateFormat.CreateDate());
//        for(ContentValues row : values){
//            db.insert(TABLE_851303.TABLE_NAME, null, row);
//        }
//    }

}

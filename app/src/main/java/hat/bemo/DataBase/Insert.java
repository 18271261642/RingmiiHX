package hat.bemo.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import hat.bemo.VO.VO_0x30;

/**
 * Created by apple on 2017/11/10.
 */

public class Insert {
    private Create_Table dbtc = null;
    private SQLiteDatabase db = null;

//    public long insert_0x01(Context context, VO_0x01_Insert vo_0x01_Insert){
//        dbtc = new Create_Table(context);
//        db = dbtc.getWritableDatabase();
//
//        ContentValues values = new ContentValues();
//        values.put(TABLE_0x01_Insert.PR_COUNT, vo_0x01_Insert.getPR_COUNT());
//        values.put(TABLE_0x01_Insert.PR_DATE, vo_0x01_Insert.getPR_DATE());
//        values.put(TABLE_0x01_Insert.VOLTAGE, vo_0x01_Insert.getVOLTAGE());
//        values.put(TABLE_0x01_Insert.VOLTAGE_PERCENT, vo_0x01_Insert.getVOLTAGE_PERCENT());
//        values.put(TABLE_0x01_Insert.LAC, vo_0x01_Insert.getLAC());
//        values.put(TABLE_0x01_Insert.MNC, vo_0x01_Insert.getMNC());
//        values.put(TABLE_0x01_Insert.RSSI, vo_0x01_Insert.getRSSI());
//        values.put(TABLE_0x01_Insert.CELL_ID, vo_0x01_Insert.getCELL_ID());
//        values.put(TABLE_0x01_Insert.MCC, vo_0x01_Insert.getMCC());
//        values.put(TABLE_0x01_Insert.GSensor_AVG, vo_0x01_Insert.getGSensor_AVG());
//        values.put(TABLE_0x01_Insert.GSensor_MAX, vo_0x01_Insert.getGSensor_MAX());
//        values.put(TABLE_0x01_Insert.PPM, vo_0x01_Insert.getPPM());
//        values.put(TABLE_0x01_Insert.GPS_STATUS, vo_0x01_Insert.getGPS_STATUS());
//        values.put(TABLE_0x01_Insert.GPS_LAT, vo_0x01_Insert.getGPS_LAT());
//        values.put(TABLE_0x01_Insert.GPS_LNG, vo_0x01_Insert.getGPS_LNG());
//        values.put(TABLE_0x01_Insert.GPS_ADDRESS, vo_0x01_Insert.getGPS_ADDRESS());
//        values.put(TABLE_0x01_Insert.GPS_ACCURACY, vo_0x01_Insert.getGPS_ACCURACY());
//        values.put(TABLE_0x01_Insert.WIFI_MAC, vo_0x01_Insert.getWIFI_MAC());
//        values.put(TABLE_0x01_Insert.WIFI_Signal_dB, vo_0x01_Insert.getWIFI_Signal_dB());
//        values.put(TABLE_0x01_Insert.WIFI_Channel, vo_0x01_Insert.getWIFI_Channel());
//
//        long insert = db.insert(TABLE_0x01_Insert.TABLE_NAME, null, values);
//
//        db.close();
//        return insert;
//    }
//
//    public long insert_0x29(Context context, VO_0x29_Insert vo_0x29_Insert){
//
//        dbtc = new Create_Table(context);
//        db = dbtc.getWritableDatabase();
//
//        ContentValues values = new ContentValues();
//        values.put(TABLE_0x29_Insert.EF_COUNT, vo_0x29_Insert.getEF_COUNT());
//        values.put(TABLE_0x29_Insert.EF_DATE, vo_0x29_Insert.getEF_DATE());
//        values.put(TABLE_0x29_Insert.VOLTAGE, vo_0x29_Insert.getVOLTAGE());
//        values.put(TABLE_0x29_Insert.VOLTAGE_PERCENT, vo_0x29_Insert.getVOLTAGE_PERCENT());
//        values.put(TABLE_0x29_Insert.LAC, vo_0x29_Insert.getLAC());
//        values.put(TABLE_0x29_Insert.MNC, vo_0x29_Insert.getMNC());
//        values.put(TABLE_0x29_Insert.RSSI, vo_0x29_Insert.getRSSI());
//        values.put(TABLE_0x29_Insert.CELL_ID, vo_0x29_Insert.getCELL_ID());
//        values.put(TABLE_0x29_Insert.MCC, vo_0x29_Insert.getMCC());
//        values.put(TABLE_0x29_Insert.GSensor_AVG, vo_0x29_Insert.getGSensor_AVG());
//        values.put(TABLE_0x29_Insert.GSensor_MAX, vo_0x29_Insert.getGSensor_MAX());
//        values.put(TABLE_0x29_Insert.TEMP, vo_0x29_Insert.getTEMP());
//        values.put(TABLE_0x29_Insert.PPM, vo_0x29_Insert.getPPM());
//        values.put(TABLE_0x29_Insert.CELLER_LAC, vo_0x29_Insert.getCELLER_LAC());
//        values.put(TABLE_0x29_Insert.CELLER_MNC, vo_0x29_Insert.getCELLER_MNC());
//        values.put(TABLE_0x29_Insert.CELLER_RSSI, vo_0x29_Insert.getCELLER_RSSI());
//        values.put(TABLE_0x29_Insert.CELLER_CELL_ID, vo_0x29_Insert.getCELLER_CELL_ID());
//        values.put(TABLE_0x29_Insert.CELLER_MCC, vo_0x29_Insert.getCELLER_MCC());
//        values.put(TABLE_0x29_Insert.GPS_STATUS, vo_0x29_Insert.getGPS_STATUS());
//        values.put(TABLE_0x29_Insert.GPS_LAT, vo_0x29_Insert.getGPS_LAT());
//        values.put(TABLE_0x29_Insert.GPS_LNG, vo_0x29_Insert.getGPS_LNG());
//        values.put(TABLE_0x29_Insert.GPS_ACCURACY, vo_0x29_Insert.getGPS_ACCURACY());
//        values.put(TABLE_0x29_Insert.GPS_ADDRESS, vo_0x29_Insert.getGPS_ADDRESS());
//        values.put(TABLE_0x29_Insert.ELECTRIC_FENCE_ITEMNO, vo_0x29_Insert.getELECTRIC_FENCE_ITEMNO());
//        values.put(TABLE_0x29_Insert.WIFI_MAC, vo_0x29_Insert.getWIFI_MAC());
//        values.put(TABLE_0x29_Insert.WIFI_Signal_dB, vo_0x29_Insert.getWIFI_Signal_dB());
//        values.put(TABLE_0x29_Insert.WIFI_Channel, vo_0x29_Insert.getWIFI_Channel());
//        values.put(TABLE_0x29_Insert.CREATE_DATE, vo_0x29_Insert.getCREATE_DATE());
//
//        long insert = db.insert(TABLE_0x29_Insert.TABLE_NAME, null, values);
//
//        db.close();
//        return insert;
//    }

    public long insert_0x30(Context context, VO_0x30 vo_0x30){

        dbtc = new Create_Table(context);
        db = dbtc.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TABLE_0x30.EMG_DATE, vo_0x30.getEMG_DATE());
        values.put(TABLE_0x30.MCC, vo_0x30.getMCC());
        values.put(TABLE_0x30.MNC, vo_0x30.getMNC());
        values.put(TABLE_0x30.LAC, vo_0x30.getLAC());
        values.put(TABLE_0x30.CELL_ID, vo_0x30.getCELL_ID());
        values.put(TABLE_0x30.RSSI, vo_0x30.getRSSI());
        values.put(TABLE_0x30.GPS_STATUS, vo_0x30.getGPS_STATUS());
        values.put(TABLE_0x30.GPS_LAT, vo_0x30.getGPS_LAT());
        values.put(TABLE_0x30.GPS_LNG, vo_0x30.getGPS_LNG());
        values.put(TABLE_0x30.GPS_ACCURACY, vo_0x30.getGPS_ACCURACY());
        values.put(TABLE_0x30.GPS_ADDRESS, vo_0x30.getGPS_ADDRESS());
        values.put(TABLE_0x30.WIFI_MAC, vo_0x30.getWIFI_MAC());
        values.put(TABLE_0x30.WIFI_Signal_dB, vo_0x30.getWIFI_Signal_dB());
        values.put(TABLE_0x30.WIFI_Channel, vo_0x30.getWIFI_Channel());
        values.put(TABLE_0x30.CREATE_DATE, vo_0x30.getCREATE_DATE());

        long insert = db.insert(TABLE_0x30.TABLE_NAME, null, values);

        db.close();
        return insert;
    }

//    public long insert_801603(Context context, VO_801603 vo__801603){
//
//        dbtc = new Create_Table(context);
//        db = dbtc.getWritableDatabase();
//
//        ContentValues values = new ContentValues();
//        values.put(TABLE_801603.birthday, vo__801603.getBirthday());
//        values.put(TABLE_801603.broadcaset_account, vo__801603.getBroadcaset_account());
//        values.put(TABLE_801603.contact_name, vo__801603.getContact_name());
//        values.put(TABLE_801603.contact_tel, vo__801603.getContact_tel());
//        values.put(TABLE_801603.contact_type, vo__801603.getContact_type());
//        values.put(TABLE_801603.event_time, vo__801603.getEvent_time());
//        values.put(TABLE_801603.imei, vo__801603.getImei());
//        values.put(TABLE_801603.missing_person_no, vo__801603.getMissing_person_no());
//        values.put(TABLE_801603.missing_status, vo__801603.getMissing_status());
//        values.put(TABLE_801603.name, vo__801603.getName());
//        values.put(TABLE_801603.photo_url, vo__801603.getPhoto_url());
//        values.put(TABLE_801603.updatatime, vo__801603.getUpdatatime());
//
//        long insert = db.insert(TABLE_801603.TABLE_NAME, null, values);
//
//        db.close();
//        return insert;
//    }

}

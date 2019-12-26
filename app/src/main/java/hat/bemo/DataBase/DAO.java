package hat.bemo.DataBase;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import hat.bemo.VO.VO_0x20;
import hat.bemo.VO.VO_0x30;

/**
 * Created by apple on 2017/11/10.
 */

public class DAO {

    private Create_Table dbtc = null;
    private SQLiteDatabase db = null;

//    public ArrayList<VO_0x01> getdata_0x01(Context context){
//        dbtc = new Create_Table(context);
//        db = dbtc.getWritableDatabase();
//        Cursor c=null;
//        String sql = null;
//        sql = "SELECT * FROM "+TABLE_0x01.TABLE_NAME+" ORDER BY ITEMNO LIMIT 0,3 ";
//        try {
////			c = db.query(TABLE_0x01.TABLE_NAME,
////					null,	// select *
////					null,
////					null,
////					null,
////					null,
////					null
////					);
//            c = db.rawQuery(sql, null);
//
//            if(c.moveToFirst()){
//
//                ArrayList<VO_0x01> list = new ArrayList<VO_0x01>();
//                do{
//                    VO_0x01 data = new VO_0x01();
//                    data.setPR_COUNT(c.getString(c.getColumnIndex(TABLE_0x01.PR_COUNT)));
//                    data.setPR_DATE(c.getString(c.getColumnIndex(TABLE_0x01.PR_DATE)));
//                    data.setVOLTAGE(c.getString(c.getColumnIndex(TABLE_0x01.VOLTAGE)));
//                    data.setVOLTAGE_PERCENT(c.getString(c.getColumnIndex(TABLE_0x01.VOLTAGE_PERCENT)));
//                    data.setLAC(c.getString(c.getColumnIndex(TABLE_0x01.LAC)));
//                    data.setMNC(c.getString(c.getColumnIndex(TABLE_0x01.MNC)));
//                    data.setRSSI(c.getString(c.getColumnIndex(TABLE_0x01.RSSI)));
//                    data.setCELL_ID(c.getString(c.getColumnIndex(TABLE_0x01.CELL_ID)));
//                    data.setMCC(c.getString(c.getColumnIndex(TABLE_0x01.MCC)));
//                    data.setGSensor_AVG(c.getString(c.getColumnIndex(TABLE_0x01.GSensor_AVG)));
//                    data.setGSensor_MAX(c.getString(c.getColumnIndex(TABLE_0x01.GSensor_MAX)));
//                    data.setPPM(c.getString(c.getColumnIndex(TABLE_0x01.PPM)));
//                    data.setGPS_STATUS(c.getString(c.getColumnIndex(TABLE_0x01.GPS_STATUS)));
//                    data.setGPS_LAT(c.getString(c.getColumnIndex(TABLE_0x01.GPS_LAT)));
//                    data.setGPS_LNG(c.getString(c.getColumnIndex(TABLE_0x01.GPS_LNG)));
//                    data.setGPS_ADDRESS(c.getString(c.getColumnIndex(TABLE_0x01.GPS_ADDRESS)));
//                    data.setGPS_ACCURACY(c.getString(c.getColumnIndex(TABLE_0x01.GPS_ACCURACY)));
//                    data.setWIFI_MAC(c.getString(c.getColumnIndex(TABLE_0x01.WIFI_MAC)));
//                    data.setWIFI_Signal_dB(c.getString(c.getColumnIndex(TABLE_0x01.WIFI_Signal_dB)));
//                    data.setITEMNO(c.getString(c.getColumnIndex(TABLE_0x01.ITEMNO)));
//                    data.setWIFI_Channel(c.getString(c.getColumnIndex(TABLE_0x01.WIFI_Channel)));
//
//                    list.add(data);
//
//                }while(c.moveToNext());
//                return list;
//            }
//
//            return null;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }finally{
//            if(c!=null)
//                c.close();
//            db.close();
//        }
//    }

//    public ArrayList<VO_0x10> getdata_0x10(Context context){
//        dbtc = new Create_Table(context);
//        db = dbtc.getWritableDatabase();
//        Cursor c=null;
//        try {
//            c = db.query(TABLE_0x10.TABLE_NAME,
//                    null,	// select *
//                    null,
//                    null,
//                    null,
//                    null,
//                    null
//            );
//
//            if(c.moveToFirst()){
//
//                ArrayList<VO_0x10> list = new ArrayList<VO_0x10>();
//                do{
//                    VO_0x10 data = new VO_0x10();
//                    data.setAUTOREAD(c.getString(c.getColumnIndex(TABLE_0x10.AUTOREAD)));
//                    data.setCHECKSUM(c.getString(c.getColumnIndex(TABLE_0x10.CHECKSUM)));
//                    data.setDIALOUT_LIMIT_STARTDAY(c.getString(c.getColumnIndex(TABLE_0x10.DIALOUT_LIMIT_STARTDAY)));
//                    data.setECHO_GPS_T(c.getString(c.getColumnIndex(TABLE_0x10.ECHO_GPS_T)));
//                    data.setECHO_PR_T(c.getString(c.getColumnIndex(TABLE_0x10.ECHO_PR_T)));
//                    data.setPHONE_LIMIT_BY_CUSTOMER(c.getString(c.getColumnIndex(TABLE_0x10.PHONE_LIMIT_BY_CUSTOMER)));
//                    data.setPHONE_LIMIT_MODE(c.getString(c.getColumnIndex(TABLE_0x10.PHONE_LIMIT_MODE)));
//                    data.setPHONE_LIMIT_ONOFF(c.getString(c.getColumnIndex(TABLE_0x10.PHONE_LIMIT_ONOFF)));
//                    data.setPHONE_LIMIT_TIME(c.getString(c.getColumnIndex(TABLE_0x10.PHONE_LIMIT_TIME)));
//                    data.setPOWERON_LOGO(c.getString(c.getColumnIndex(TABLE_0x10.POWERON_LOGO)));
//                    data.setSERVER_DNS(c.getString(c.getColumnIndex(TABLE_0x10.SERVER_DNS)));
//                    data.setITEMNO(c.getString(c.getColumnIndex(TABLE_0x10.ITEMNO)));
//                    data.setSERVER_IP_PORT(c.getString(c.getColumnIndex(TABLE_0x10.SERVER_IP_PORT)));
//
//                    list.add(data);
//
//                }while(c.moveToNext());
//                return list;
//            }
//
//            return null;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }finally{
//            if(c!=null)
//                c.close();
//            db.close();
//        }
//    }

//    public ArrayList<VO_0x11> getdata_0x11(Context context){
//        dbtc = new Create_Table(context);
//        db = dbtc.getWritableDatabase();
//        Cursor c=null;
//        try {
//            c = db.query(TABLE_0x11.TABLE_NAME,
//                    null,	// select *
//                    null,
//                    null,
//                    null,
//                    null,
//                    null
//            );
//
//            if(c.moveToFirst()){
//
//                ArrayList<VO_0x11> list = new ArrayList<VO_0x11>();
//                do{
//                    VO_0x11 data = new VO_0x11();
//                    data.setCHECKSUM(c.getString(c.getColumnIndex(TABLE_0x11.CHECKSUM)));
//                    data.setFALL_DETECT(c.getString(c.getColumnIndex(TABLE_0x11.FALL_DETECT)));
//                    data.setFALL_PHONE_NAME1(c.getString(c.getColumnIndex(TABLE_0x11.FALL_PHONE_NAME1)));
//                    data.setFALL_PHONE_NAME2(c.getString(c.getColumnIndex(TABLE_0x11.FALL_PHONE_NAME2)));
//                    data.setFALL_PHONE_NAME3(c.getString(c.getColumnIndex(TABLE_0x11.FALL_PHONE_NAME3)));
//                    data.setFALL_PHONE_NO1(c.getString(c.getColumnIndex(TABLE_0x11.FALL_PHONE_NO1)));
//                    data.setFALL_PHONE_NO2(c.getString(c.getColumnIndex(TABLE_0x11.FALL_PHONE_NO2)));
//                    data.setFALL_PHONE_NO3(c.getString(c.getColumnIndex(TABLE_0x11.FALL_PHONE_NO3)));
//                    data.setFALL_SMS(c.getString(c.getColumnIndex(TABLE_0x11.FALL_SMS)));
//                    data.setFAMILY_PHONE_NAME1(c.getString(c.getColumnIndex(TABLE_0x11.FAMILY_PHONE_NAME1)));
//                    data.setFAMILY_PHONE_NAME2(c.getString(c.getColumnIndex(TABLE_0x11.FAMILY_PHONE_NAME2)));
//                    data.setFAMILY_PHONE_NAME3(c.getString(c.getColumnIndex(TABLE_0x11.FAMILY_PHONE_NAME3)));
//                    data.setFAMILY_PHONE_NO1(c.getString(c.getColumnIndex(TABLE_0x11.FAMILY_PHONE_NO1)));
//                    data.setFAMILY_PHONE_NO2(c.getString(c.getColumnIndex(TABLE_0x11.FAMILY_PHONE_NO2)));
//                    data.setFAMILY_PHONE_NO3(c.getString(c.getColumnIndex(TABLE_0x11.FAMILY_PHONE_NO3)));
//                    data.setFMLY_KEY_DELAY(c.getString(c.getColumnIndex(TABLE_0x11.FMLY_KEY_DELAY)));
//                    data.setMED_TIME_1(c.getString(c.getColumnIndex(TABLE_0x11.MED_TIME_1)));
//                    data.setSWITCH(c.getString(c.getColumnIndex(TABLE_0x11.SWITCH)));
//                    data.setTIME(c.getString(c.getColumnIndex(TABLE_0x11.TIME)));
//                    data.setWEEK1(c.getString(c.getColumnIndex(TABLE_0x11.WEEK1)));
//                    data.setWEEK2(c.getString(c.getColumnIndex(TABLE_0x11.WEEK2)));
//                    data.setWEEK3(c.getString(c.getColumnIndex(TABLE_0x11.WEEK3)));
//                    data.setWEEK4(c.getString(c.getColumnIndex(TABLE_0x11.WEEK4)));
//                    data.setWEEK5(c.getString(c.getColumnIndex(TABLE_0x11.WEEK5)));
//                    data.setWEEK6(c.getString(c.getColumnIndex(TABLE_0x11.WEEK6)));
//                    data.setWEEK7(c.getString(c.getColumnIndex(TABLE_0x11.WEEK7)));
//                    data.setMED_TIME_2(c.getString(c.getColumnIndex(TABLE_0x11.MED_TIME_2)));
//                    data.setMED_TIME_3(c.getString(c.getColumnIndex(TABLE_0x11.MED_TIME_3)));
//                    data.setMED_TIME_4(c.getString(c.getColumnIndex(TABLE_0x11.MED_TIME_4)));
//                    data.setMED_TIME_5(c.getString(c.getColumnIndex(TABLE_0x11.MED_TIME_5)));
//                    data.setNON_DISTRUB(c.getString(c.getColumnIndex(TABLE_0x11.NON_DISTRUB)));
//                    data.setPERSONAL_INFO(c.getString(c.getColumnIndex(TABLE_0x11.PERSONAL_INFO)));
//                    data.setACCOUNTID(c.getString(c.getColumnIndex(TABLE_0x11.ACCOUNTID)));
//                    data.setAGE(c.getString(c.getColumnIndex(TABLE_0x11.AGE)));
//                    data.setBIRTHDATE_DD(c.getString(c.getColumnIndex(TABLE_0x11.BIRTHDATE_DD)));
//                    data.setBIRTHDATE_MM(c.getString(c.getColumnIndex(TABLE_0x11.BIRTHDATE_MM)));
//                    data.setBIRTHDATE_YY(c.getString(c.getColumnIndex(TABLE_0x11.BIRTHDATE_YY)));
//                    data.setHEIGHT(c.getString(c.getColumnIndex(TABLE_0x11.HEIGHT)));
//                    data.setIDEAL_WEIGHT(c.getString(c.getColumnIndex(TABLE_0x11.IDEAL_WEIGHT)));
//                    data.setNAME(c.getString(c.getColumnIndex(TABLE_0x11.NAME)));
//                    data.setSEX(c.getString(c.getColumnIndex(TABLE_0x11.SEX)));
//                    data.setSTEP(c.getString(c.getColumnIndex(TABLE_0x11.STEP)));
//                    data.setUNIT(c.getString(c.getColumnIndex(TABLE_0x11.UNIT)));
//                    data.setWEIGHT(c.getString(c.getColumnIndex(TABLE_0x11.WEIGHT)));
//                    data.setRETURN_DT_1(c.getString(c.getColumnIndex(TABLE_0x11.RETURN_DT_1)));
//                    data.setDAY(c.getString(c.getColumnIndex(TABLE_0x11.DAY)));
//                    data.setMONTH(c.getString(c.getColumnIndex(TABLE_0x11.MONTH)));
//                    data.setYEAR(c.getString(c.getColumnIndex(TABLE_0x11.YEAR)));
//                    data.setSET_LANG(c.getString(c.getColumnIndex(TABLE_0x11.SET_LANG)));
//                    data.setSOS_KEY_DELAY(c.getString(c.getColumnIndex(TABLE_0x11.SOS_KEY_DELAY)));
//                    data.setSOS_PHONE_NAME1(c.getString(c.getColumnIndex(TABLE_0x11.SOS_PHONE_NAME1)));
//                    data.setSOS_PHONE_NAME2(c.getString(c.getColumnIndex(TABLE_0x11.SOS_PHONE_NAME2)));
//                    data.setSOS_PHONE_NAME3(c.getString(c.getColumnIndex(TABLE_0x11.SOS_PHONE_NAME3)));
//                    data.setSOS_PHONE_NO1(c.getString(c.getColumnIndex(TABLE_0x11.SOS_PHONE_NO1)));
//                    data.setSOS_PHONE_NO2(c.getString(c.getColumnIndex(TABLE_0x11.SOS_PHONE_NO2)));
//                    data.setSOS_PHONE_NO3(c.getString(c.getColumnIndex(TABLE_0x11.SOS_PHONE_NO3)));
//                    data.setSOS_SMS(c.getString(c.getColumnIndex(TABLE_0x11.SOS_SMS)));
//                    data.setTIMEZONE(c.getString(c.getColumnIndex(TABLE_0x11.TIMEZONE)));
//                    data.setWHITE_LIST_ON(c.getString(c.getColumnIndex(TABLE_0x11.WHITE_LIST_ON)));
//                    data.setITEMNO(c.getString(c.getColumnIndex(TABLE_0x11.ITEMNO)));
//                    data.setELECTRIC_FENCE(c.getString(c.getColumnIndex(TABLE_0x11.ELECTRIC_FENCE)));
//                    data.setPHONE(c.getString(c.getColumnIndex(TABLE_0x11.PHONE)));
//                    list.add(data);
//
//                }while(c.moveToNext());
//                return list;
//            }
//
//            return null;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }finally{
//            if(c!=null)
//                c.close();
//            db.close();
//        }
//    }

    public ArrayList<VO_0x20> getdata_0x20(Context context){
        dbtc = new Create_Table(context);
        db = dbtc.getWritableDatabase();
        Cursor c=null;
        try {
            c = db.query(TABLE_0x20.TABLE_NAME,
                    null,	// select *
                    null,
                    null,
                    null,
                    null,
                    null
            );

            if(c.moveToFirst()){

                ArrayList<VO_0x20> list = new ArrayList<VO_0x20>();
                do{
                    VO_0x20 data = new VO_0x20();
                    data.setEMG_DATE(c.getString(c.getColumnIndex(TABLE_0x20.EMG_DATE)));
                    data.setGPS_LAT(c.getString(c.getColumnIndex(TABLE_0x20.GPS_LAT)));
                    data.setGPS_LNG(c.getString(c.getColumnIndex(TABLE_0x20.GPS_LNG)));
                    data.setGPS_ADDRESS(c.getString(c.getColumnIndex(TABLE_0x20.GPS_ADDRESS)));
                    data.setGPS_ACCURACY(c.getString(c.getColumnIndex(TABLE_0x20.GPS_ACCURACY)));
                    data.setGPS_STATUS(c.getString(c.getColumnIndex(TABLE_0x20.GPS_STATUS)));
                    data.setMCC(c.getString(c.getColumnIndex(TABLE_0x20.MCC)));
                    data.setMNC(c.getString(c.getColumnIndex(TABLE_0x20.MNC)));
                    data.setLAC(c.getString(c.getColumnIndex(TABLE_0x20.LAC)));
                    data.setCELL_ID(c.getString(c.getColumnIndex(TABLE_0x20.CELL_ID)));
                    data.setRSSI(c.getString(c.getColumnIndex(TABLE_0x20.RSSI)));
                    data.setWIFI_MAC(c.getString(c.getColumnIndex(TABLE_0x20.WIFI_MAC)));
                    data.setWIFI_Signal_dB(c.getString(c.getColumnIndex(TABLE_0x20.WIFI_Signal_dB)));
                    data.setWIFI_Channel(c.getString(c.getColumnIndex(TABLE_0x20.WIFI_Channel)));
                    data.setITEMNO(c.getString(c.getColumnIndex(TABLE_0x20.ITEMNO)));
                    data.setCREATE_DATE(c.getString(c.getColumnIndex(TABLE_0x20.CREATE_DATE)));
                    list.add(data);

                }while(c.moveToNext());
                return list;
            }

            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }finally{
            if(c!=null)
                c.close();
            db.close();
        }
    }

//    public ArrayList<VO_0x28> getdata_0x28(Context context){
//        dbtc = new Create_Table(context);
//        db = dbtc.getWritableDatabase();
//        Cursor c=null;
//        try {
//            c = db.query(TABLE_0x28.TABLE_NAME,
//                    null,	// select *
//                    null,
//                    null,
//                    null,
//                    null,
//                    null
//            );
//
//            if(c.moveToFirst()){
//
//                ArrayList<VO_0x28> list = new ArrayList<VO_0x28>();
//                do{
//                    VO_0x28 data = new VO_0x28();
//                    data.setEMG_DATE(c.getString(c.getColumnIndex(TABLE_0x28.EMG_DATE)));
//                    data.setGPS_LAT(c.getString(c.getColumnIndex(TABLE_0x28.GPS_LAT)));
//                    data.setGPS_LNG(c.getString(c.getColumnIndex(TABLE_0x28.GPS_LNG)));
//                    data.setGPS_ADDRESS(c.getString(c.getColumnIndex(TABLE_0x28.GPS_ADDRESS)));
//                    data.setGPS_ACCURACY(c.getString(c.getColumnIndex(TABLE_0x28.GPS_ACCURACY)));
//                    data.setGPS_STATUS(c.getString(c.getColumnIndex(TABLE_0x28.GPS_STATUS)));
//                    data.setMCC(c.getString(c.getColumnIndex(TABLE_0x28.MCC)));
//                    data.setMNC(c.getString(c.getColumnIndex(TABLE_0x28.MNC)));
//                    data.setLAC(c.getString(c.getColumnIndex(TABLE_0x28.LAC)));
//                    data.setCELL_ID(c.getString(c.getColumnIndex(TABLE_0x28.CELL_ID)));
//                    data.setRSSI(c.getString(c.getColumnIndex(TABLE_0x28.RSSI)));
//                    data.setWIFI_MAC(c.getString(c.getColumnIndex(TABLE_0x28.WIFI_MAC)));
//                    data.setWIFI_Signal_dB(c.getString(c.getColumnIndex(TABLE_0x28.WIFI_Signal_dB)));
//                    data.setWIFI_Channel(c.getString(c.getColumnIndex(TABLE_0x28.WIFI_Channel)));
//                    data.setITEMNO(c.getString(c.getColumnIndex(TABLE_0x28.ITEMNO)));
//                    data.setCREATE_DATE(c.getString(c.getColumnIndex(TABLE_0x28.CREATE_DATE)));
//                    list.add(data);
//
//                }while(c.moveToNext());
//                return list;
//            }
//
//            return null;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }finally{
//            if(c!=null)
//                c.close();
//            db.close();
//        }
//    }

//    public ArrayList<VO_0x29> getdata_0x29(Context context){
//        dbtc = new Create_Table(context);
//        db = dbtc.getWritableDatabase();
//        Cursor c=null;
//        try {
//            c = db.query(TABLE_0x29.TABLE_NAME,
//                    null,	// select *
//                    null,
//                    null,
//                    null,
//                    null,
//                    null
//            );
//
//            if(c.moveToFirst()){
//
//                ArrayList<VO_0x29> list = new ArrayList<VO_0x29>();
//                do{
//                    VO_0x29 data = new VO_0x29();
//                    data.setITEMNO(c.getString(c.getColumnIndex(TABLE_0x29.ITEMNO)));
//                    data.setEF_COUNT(c.getString(c.getColumnIndex(TABLE_0x29.EF_COUNT)));
//                    data.setEF_DATE(c.getString(c.getColumnIndex(TABLE_0x29.EF_DATE)));
//                    data.setVOLTAGE(c.getString(c.getColumnIndex(TABLE_0x29.VOLTAGE)));
//                    data.setVOLTAGE_PERCENT(c.getString(c.getColumnIndex(TABLE_0x29.VOLTAGE_PERCENT)));
//                    data.setLAC(c.getString(c.getColumnIndex(TABLE_0x29.LAC)));
//                    data.setMNC(c.getString(c.getColumnIndex(TABLE_0x29.MNC)));
//                    data.setRSSI(c.getString(c.getColumnIndex(TABLE_0x29.RSSI)));
//                    data.setCELL_ID(c.getString(c.getColumnIndex(TABLE_0x29.CELL_ID)));
//                    data.setMCC(c.getString(c.getColumnIndex(TABLE_0x29.MCC)));
//                    data.setGSensor_AVG(c.getString(c.getColumnIndex(TABLE_0x29.GSensor_AVG)));
//                    data.setGSensor_MAX(c.getString(c.getColumnIndex(TABLE_0x29.GSensor_MAX)));
//                    data.setTEMP(c.getString(c.getColumnIndex(TABLE_0x29.TEMP)));
//                    data.setPPM(c.getString(c.getColumnIndex(TABLE_0x29.PPM)));
//                    data.setCELLER_LAC(c.getString(c.getColumnIndex(TABLE_0x29.CELLER_LAC)));
//                    data.setCELLER_MNC(c.getString(c.getColumnIndex(TABLE_0x29.CELLER_MNC)));
//                    data.setCELLER_RSSI(c.getString(c.getColumnIndex(TABLE_0x29.CELLER_RSSI)));
//                    data.setCELLER_CELL_ID(c.getString(c.getColumnIndex(TABLE_0x29.CELLER_CELL_ID)));
//                    data.setCELLER_MCC(c.getString(c.getColumnIndex(TABLE_0x29.CELLER_MCC)));
//                    data.setGPS_STATUS(c.getString(c.getColumnIndex(TABLE_0x29.GPS_STATUS)));
//                    data.setGPS_LAT(c.getString(c.getColumnIndex(TABLE_0x29.GPS_LAT)));
//                    data.setGPS_LNG(c.getString(c.getColumnIndex(TABLE_0x29.GPS_LNG)));
//                    data.setGPS_ACCURACY(c.getString(c.getColumnIndex(TABLE_0x29.GPS_ACCURACY)));
//                    data.setGPS_ADDRESS(c.getString(c.getColumnIndex(TABLE_0x29.GPS_ADDRESS)));
//                    data.setELECTRIC_FENCE_ITEMNO(c.getString(c.getColumnIndex(TABLE_0x29.ELECTRIC_FENCE_ITEMNO)));
//                    data.setWIFI_MAC(c.getString(c.getColumnIndex(TABLE_0x29.WIFI_MAC)));
//                    data.setWIFI_Signal_dB(c.getString(c.getColumnIndex(TABLE_0x29.WIFI_Signal_dB)));
//                    data.setWIFI_Channel(c.getString(c.getColumnIndex(TABLE_0x29.WIFI_Channel)));
//                    data.setCREATE_DATE(c.getString(c.getColumnIndex(TABLE_0x29.CREATE_DATE)));
//                    list.add(data);
//
//                }while(c.moveToNext());
//                return list;
//            }
//
//            return null;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }finally{
//            if(c!=null)
//                c.close();
//            db.close();
//        }
//    }

//    public ArrayList<VO_0x2A> getdata_0x2A(Context context){
//        dbtc = new Create_Table(context);
//        db = dbtc.getWritableDatabase();
//        Cursor c=null;
//        try {
//            c = db.query(TABLE_0x2A.TABLE_NAME,
//                    null,	// select *
//                    null,
//                    null,
//                    null,
//                    null,
//                    null
//            );
//
//            if(c.moveToFirst()){
//
//                ArrayList<VO_0x2A> list = new ArrayList<VO_0x2A>();
//                do{
//                    VO_0x2A data = new VO_0x2A();
//                    data.setDEVICE_TOKEN(c.getString(c.getColumnIndex(TABLE_0x2A.DEVICE_TOKEN)));
//                    data.setSIGNATURE(c.getString(c.getColumnIndex(TABLE_0x2A.SIGNATURE)));
//                    data.setITEMNO(c.getString(c.getColumnIndex(TABLE_0x2A.ITEMNO)));
//                    data.setCREATE_DATE(c.getString(c.getColumnIndex(TABLE_0x2A.CREATE_DATE)));
//                    list.add(data);
//
//                }while(c.moveToNext());
//                return list;
//            }
//
//            return null;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }finally{
//            if(c!=null)
//                c.close();
//            db.close();
//        }
//    }

//    public ArrayList<VO_0x2B> getdata_0x2B(Context context){
//        dbtc = new Create_Table(context);
//        db = dbtc.getWritableDatabase();
//        Cursor c=null;
//        try {
//            c = db.query(TABLE_0x2B.TABLE_NAME,
//                    null,	// select *
//                    null,
//                    null,
//                    null,
//                    null,
//                    null
//            );
//
//            if(c.moveToFirst()){
//
//                ArrayList<VO_0x2B> list = new ArrayList<VO_0x2B>();
//                do{
//                    VO_0x2B data = new VO_0x2B();
//                    data.setITEMNO(c.getString(c.getColumnIndex(TABLE_0x2B.ITEMNO)));
//                    data.setALERT_TYPE(c.getString(c.getColumnIndex(TABLE_0x2B.ALERT_TYPE)));
//                    list.add(data);
//
//                }while(c.moveToNext());
//                return list;
//            }
//
//            return null;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }finally{
//            if(c!=null)
//                c.close();
//            db.close();
//        }
//    }

//    public ArrayList<VO_0x2E> getdata_0x2E(Context context){
//        dbtc = new Create_Table(context);
//        db = dbtc.getWritableDatabase();
//        Cursor c=null;
//        try {
//            c = db.query(TABLE_0x2E.TABLE_NAME,
//                    null,	// select *
//                    null,
//                    null,
//                    null,
//                    null,
//                    null
//            );
//
//            if(c.moveToFirst()){
//
//                ArrayList<VO_0x2E> list = new ArrayList<VO_0x2E>();
//                do{
//                    VO_0x2E data = new VO_0x2E();
//                    data.setPWR_ON_DATE(c.getString(c.getColumnIndex(TABLE_0x2E.PWR_ON_DATE)));
//                    data.setFW_BUILD_DATE(c.getString(c.getColumnIndex(TABLE_0x2E.FW_BUILD_DATE)));
//                    data.setVOLTAGE(c.getString(c.getColumnIndex(TABLE_0x2E.VOLTAGE)));
//                    data.setVOLTAGE_PERCENT(c.getString(c.getColumnIndex(TABLE_0x2E.VOLTAGE_PERCENT)));
//                    data.setMCC(c.getString(c.getColumnIndex(TABLE_0x2E.MCC)));
//                    data.setMNC(c.getString(c.getColumnIndex(TABLE_0x2E.MNC)));
//                    data.setLAC(c.getString(c.getColumnIndex(TABLE_0x2E.LAC)));
//                    data.setCELL_ID(c.getString(c.getColumnIndex(TABLE_0x2E.CELL_ID)));
//                    data.setRSSI(c.getString(c.getColumnIndex(TABLE_0x2E.RSSI)));
//                    data.setITEMNO(c.getString(c.getColumnIndex(TABLE_0x2E.ITEMNO)));
//                    data.setCREATE_DATE(c.getString(c.getColumnIndex(TABLE_0x2E.CREATE_DATE)));
//                    list.add(data);
//
//                }while(c.moveToNext());
//                return list;
//            }
//
//            return null;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }finally{
//            if(c!=null)
//                c.close();
//            db.close();
//        }
//    }

//    public ArrayList<VO_0x2F> getdata_0x2F(Context context){
//        dbtc = new Create_Table(context);
//        db = dbtc.getWritableDatabase();
//        Cursor c=null;
//        try {
//            c = db.query(TABLE_0x2F.TABLE_NAME,
//                    null,	// select *
//                    null,
//                    null,
//                    null,
//                    null,
//                    null
//            );
//
//            if(c.moveToFirst()){
//
//                ArrayList<VO_0x2F> list = new ArrayList<VO_0x2F>();
//                do{
//                    VO_0x2F data = new VO_0x2F();
//                    data.setPWR_ON_DATE(c.getString(c.getColumnIndex(TABLE_0x2F.PWR_ON_DATE)));
//                    data.setFW_BUILD_DATE(c.getString(c.getColumnIndex(TABLE_0x2F.FW_BUILD_DATE)));
//                    data.setVOLTAGE(c.getString(c.getColumnIndex(TABLE_0x2F.VOLTAGE)));
//                    data.setVOLTAGE_PERCENT(c.getString(c.getColumnIndex(TABLE_0x2F.VOLTAGE_PERCENT)));
//                    data.setMCC(c.getString(c.getColumnIndex(TABLE_0x2F.MCC)));
//                    data.setMNC(c.getString(c.getColumnIndex(TABLE_0x2F.MNC)));
//                    data.setLAC(c.getString(c.getColumnIndex(TABLE_0x2F.LAC)));
//                    data.setCELL_ID(c.getString(c.getColumnIndex(TABLE_0x2F.CELL_ID)));
//                    data.setRSSI(c.getString(c.getColumnIndex(TABLE_0x2F.RSSI)));
//                    data.setITEMNO(c.getString(c.getColumnIndex(TABLE_0x2F.ITEMNO)));
//                    data.setCREATE_DATE(c.getString(c.getColumnIndex(TABLE_0x2F.CREATE_DATE)));
//                    data.setGPS_ACCURACY(c.getString(c.getColumnIndex(TABLE_0x2F.GPS_ACCURACY)));
//                    data.setGPS_LAT(c.getString(c.getColumnIndex(TABLE_0x2F.GPS_LAT)));
//                    data.setGPS_LNG(c.getString(c.getColumnIndex(TABLE_0x2F.GPS_LNG)));
//                    data.setGPS_STATUS(c.getString(c.getColumnIndex(TABLE_0x2F.GPS_STATUS)));
//                    data.setPWR_OFF_DATE(c.getString(c.getColumnIndex(TABLE_0x2F.PWR_OFF_DATE)));
//                    data.setPWR_OFF_TYPE(c.getString(c.getColumnIndex(TABLE_0x2F.PWR_OFF_TYPE)));
//                    data.setAccount(c.getString(c.getColumnIndex(TABLE_0x2F.account)));
//                    list.add(data);
//
//                }while(c.moveToNext());
//                return list;
//            }
//
//            return null;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }finally{
//            if(c!=null)
//                c.close();
//            db.close();
//        }
//    }

    public ArrayList<VO_0x30> getdata_0x30(Context context){
        dbtc = new Create_Table(context);
        db = dbtc.getWritableDatabase();
        Cursor c=null;
        try {
            c = db.query(TABLE_0x30.TABLE_NAME,
                    null,	// select *
                    null,
                    null,
                    null,
                    null,
                    null
            );

            if(c.moveToFirst()){

                ArrayList<VO_0x30> list = new ArrayList<VO_0x30>();
                do{
                    VO_0x30 data = new VO_0x30();
                    data.setEMG_DATE(c.getString(c.getColumnIndex(TABLE_0x30.EMG_DATE)));
                    data.setMCC(c.getString(c.getColumnIndex(TABLE_0x30.MCC)));
                    data.setMNC(c.getString(c.getColumnIndex(TABLE_0x30.MNC)));
                    data.setLAC(c.getString(c.getColumnIndex(TABLE_0x30.LAC)));
                    data.setCELL_ID(c.getString(c.getColumnIndex(TABLE_0x30.CELL_ID)));
                    data.setRSSI(c.getString(c.getColumnIndex(TABLE_0x30.RSSI)));
                    data.setGPS_STATUS(c.getString(c.getColumnIndex(TABLE_0x30.GPS_STATUS)));
                    data.setGPS_LAT(c.getString(c.getColumnIndex(TABLE_0x30.GPS_LAT)));
                    data.setGPS_LNG(c.getString(c.getColumnIndex(TABLE_0x30.GPS_LNG)));
                    data.setGPS_ACCURACY(c.getString(c.getColumnIndex(TABLE_0x30.GPS_ACCURACY)));
                    data.setGPS_ADDRESS(c.getString(c.getColumnIndex(TABLE_0x30.GPS_ADDRESS)));
                    data.setWIFI_MAC(c.getString(c.getColumnIndex(TABLE_0x30.WIFI_MAC)));
                    data.setWIFI_Signal_dB(c.getString(c.getColumnIndex(TABLE_0x30.WIFI_Signal_dB)));
                    data.setWIFI_Channel(c.getString(c.getColumnIndex(TABLE_0x30.WIFI_Channel)));
                    data.setITEMNO(c.getString(c.getColumnIndex(TABLE_0x30.ITEMNO)));
                    data.setCREATE_DATE(c.getString(c.getColumnIndex(TABLE_0x30.CREATE_DATE)));

                    list.add(data);

                }while(c.moveToNext());
                return list;
            }

            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }finally{
            if(c!=null)
                c.close();
            db.close();
        }
    }

//    public ArrayList<VO_0x3F> getdata_0x3F(Context context){
//        dbtc = new Create_Table(context);
//        db = dbtc.getWritableDatabase();
//        Cursor c=null;
//        try {
//            c = db.query(TABLE_0x3F.TABLE_NAME,
//                    null,	// select *
//                    null,
//                    null,
//                    null,
//                    null,
//                    null
//            );
//
//            if(c.moveToFirst()){
//
//                ArrayList<VO_0x3F> list = new ArrayList<VO_0x3F>();
//                do{
//                    VO_0x3F data = new VO_0x3F();
//                    data.setPHONE(c.getString(c.getColumnIndex(TABLE_0x3F.PHONE)));
//                    data.setSTART_TIME(c.getString(c.getColumnIndex(TABLE_0x3F.START_TIME)));
//                    data.setEND_TIME(c.getString(c.getColumnIndex(TABLE_0x3F.END_TIME)));
//                    data.setCALL_TYPE(c.getString(c.getColumnIndex(TABLE_0x3F.CALL_TYPE)));
//                    data.setITEMNO(c.getString(c.getColumnIndex(TABLE_0x3F.ITEMNO)));
//                    data.setCREATE_DATE(c.getString(c.getColumnIndex(TABLE_0x3F.CREATE_DATE)));
//                    list.add(data);
//
//                }while(c.moveToNext());
//                return list;
//            }
//
//            return null;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }finally{
//            if(c!=null)
//                c.close();
//            db.close();
//        }
//    }

//    public ArrayList<VO_0x40> getdata_0x40(Context context){
//        dbtc = new Create_Table(context);
//        db = dbtc.getWritableDatabase();
//        Cursor c=null;
//        try {
//            c = db.query(TABLE_0x40.TABLE_NAME,
//                    null,	// select *
//                    null,
//                    null,
//                    null,
//                    null,
//                    null
//            );
//
//            if(c.moveToFirst()){
//
//                ArrayList<VO_0x40> list = new ArrayList<VO_0x40>();
//                do{
//                    VO_0x40 data = new VO_0x40();
//                    data.setSERVER_IP_PORT(c.getString(c.getColumnIndex(TABLE_0x40.SERVER_IP_PORT)));
//                    data.setITEMNO(c.getString(c.getColumnIndex(TABLE_0x40.ITEMNO)));
//                    data.setSERVER_DNS(c.getString(c.getColumnIndex(TABLE_0x40.SERVER_DNS)));
//                    list.add(data);
//
//                }while(c.moveToNext());
//                return list;
//            }
//
//            return null;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }finally{
//            if(c!=null)
//                c.close();
//            db.close();
//        }
//    }

//    public ArrayList<VO_0x4B> getdata_0x4B(Context context){
//        dbtc = new Create_Table(context);
//        db = dbtc.getWritableDatabase();
//        Cursor c=null;
//        try {
//            c = db.query(TABLE_0x4B.TABLE_NAME,
//                    null,	// select *
//                    null,
//                    null,
//                    null,
//                    null,
//                    null
//            );
//
//            if(c.moveToFirst()){
//
//                ArrayList<VO_0x4B> list = new ArrayList<VO_0x4B>();
//                do{
//                    VO_0x4B data = new VO_0x4B();
//                    data.setITEMNO(c.getString(c.getColumnIndex(TABLE_0x4B.ITEMNO)));
//                    data.setURL(c.getString(c.getColumnIndex(TABLE_0x4B.URL)));
//                    data.setFAMILY_PHOTO(c.getString(c.getColumnIndex(TABLE_0x4B.FAMILY_PHOTO)));
//                    list.add(data);
//
//                }while(c.moveToNext());
//                return list;
//            }
//
//            return null;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }finally{
//            if(c!=null)
//                c.close();
//            db.close();
//        }
//    }

//    public ArrayList<VO_0x4C> getdata_0x4C(Context context){
//        dbtc = new Create_Table(context);
//        db = dbtc.getWritableDatabase();
//        Cursor c=null;
//        try {
//            c = db.query(TABLE_0x4C.TABLE_NAME,
//                    null,	// select *
//                    null,
//                    null,
//                    null,
//                    null,
//                    null
//            );
//
//            if(c.moveToFirst()){
//
//                ArrayList<VO_0x4C> list = new ArrayList<VO_0x4C>();
//                do{
//                    VO_0x4C data = new VO_0x4C();
//                    data.setITEMNO(c.getString(c.getColumnIndex(TABLE_0x4C.ITEMNO)));
//                    data.setURL(c.getString(c.getColumnIndex(TABLE_0x4C.URL)));
//                    data.setDISPLAY_PHOTO(c.getString(c.getColumnIndex(TABLE_0x4C.DISPLAY_PHOTO)));
//                    list.add(data);
//
//                }while(c.moveToNext());
//                return list;
//            }
//
//            return null;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }finally{
//            if(c!=null)
//                c.close();
//            db.close();
//        }
//    }

//    public ArrayList<VO_0x4D> getdata_0x4D(Context context){
//        dbtc = new Create_Table(context);
//        db = dbtc.getWritableDatabase();
//        Cursor c=null;
//        try {
//            c = db.query(TABLE_0x4D.TABLE_NAME,
//                    null,	// select *
//                    null,
//                    null,
//                    null,
//                    null,
//                    null
//            );
//
//            if(c.moveToFirst()){
//
//                ArrayList<VO_0x4D> list = new ArrayList<VO_0x4D>();
//                do{
//                    VO_0x4D data = new VO_0x4D();
//                    data.setGROUP_ID(c.getString(c.getColumnIndex(TABLE_0x4D.GROUP_ID)));
//                    data.setITEMNO(c.getString(c.getColumnIndex(TABLE_0x4D.ITEMNO)));
//                    data.setNAME(c.getString(c.getColumnIndex(TABLE_0x4D.NAME)));
//                    data.setPHONE(c.getString(c.getColumnIndex(TABLE_0x4D.PHONE)));
//                    data.setPHOTO_URL(c.getString(c.getColumnIndex(TABLE_0x4D.PHOTO_URL)));
//                    data.setPHONE_LIST(c.getString(c.getColumnIndex(TABLE_0x4D.PHONE_LIST)));
//                    list.add(data);
//
//                }while(c.moveToNext());
//                return list;
//            }
//
//            return null;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }finally{
//            if(c!=null)
//                c.close();
//            db.close();
//        }
//    }

//    public ArrayList<VO_0x4W> getdata_0x4W(Context context){
//        dbtc = new Create_Table(context);
//        db = dbtc.getWritableDatabase();
//        Cursor c=null;
//        try {
//            c = db.query(TABLE_0x4W.TABLE_NAME,
//                    null,	// select *
//                    null,
//                    null,
//                    null,
//                    null,
//                    null
//            );
//
//            if(c.moveToFirst()){
//
//                ArrayList<VO_0x4W> list = new ArrayList<VO_0x4W>();
//                do{
//                    VO_0x4W data = new VO_0x4W();
//                    data.setDressing_Suggestions(c.getString(c.getColumnIndex(TABLE_0x4W.dressing_Suggestions)));
//                    data.setRain_Probability(c.getString(c.getColumnIndex(TABLE_0x4W.rain_Probability)));
//                    data.setTemperature_High(c.getString(c.getColumnIndex(TABLE_0x4W.temperature_High)));
//                    data.setTemperature_Low(c.getString(c.getColumnIndex(TABLE_0x4W.temperature_Low)));
//                    data.setITEMNO(c.getString(c.getColumnIndex(TABLE_0x4W.ITEMNO)));
//                    data.setWeather_Code(c.getString(c.getColumnIndex(TABLE_0x4W.weather_Code)));
//                    list.add(data);
//
//                }while(c.moveToNext());
//                return list;
//            }
//
//            return null;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }finally{
//            if(c!=null)
//                c.close();
//            db.close();
//        }
//    }

//    public ArrayList<VO_0x7F> getdata_0x7F(Context context){
//        dbtc = new Create_Table(context);
//        db = dbtc.getWritableDatabase();
//        Cursor c=null;
//        try {
//            c = db.query(TABLE_0x7F.TABLE_NAME,
//                    null,	// select *
//                    null,
//                    null,
//                    null,
//                    null,
//                    null
//            );
//
//            if(c.moveToFirst()){
//
//                ArrayList<VO_0x7F> list = new ArrayList<VO_0x7F>();
//                do{
//                    VO_0x7F data = new VO_0x7F();
//                    data.setCHECKSUM_10(c.getString(c.getColumnIndex(TABLE_0x7F.CHECKSUM_10)));
//                    data.setCHECKSUM_11(c.getString(c.getColumnIndex(TABLE_0x7F.CHECKSUM_11)));
//                    data.setITEMNO(c.getString(c.getColumnIndex(TABLE_0x7F.ITEMNO)));
//                    data.setECHO_TYPE(c.getString(c.getColumnIndex(TABLE_0x7F.ECHO_TYPE)));
//                    list.add(data);
//
//                }while(c.moveToNext());
//                return list;
//            }
//
//            return null;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }finally{
//            if(c!=null)
//                c.close();
//            db.close();
//        }
//    }

//    public ArrayList<VO_0x01_Insert> getdata_0x01_Insert(Context context, String type){
//        dbtc = new Create_Table(context);
//        db = dbtc.getWritableDatabase();
//        Cursor c=null;
//        String sql = null;
//        if(type.equals("getall")){
//            sql = "SELECT * FROM "+TABLE_0x01_Insert.TABLE_NAME;
//        }else{
//            sql = "SELECT * FROM "+TABLE_0x01_Insert.TABLE_NAME+" ORDER BY ITEMNO DESC LIMIT 3 ";
//        }
//
//        try {
////			c = db.query(TABLE_0x01_Insert.TABLE_NAME,
////					null,	// select *
////					null,
////					null,
////					null,
////					null,
////					null
////					);
//            c = db.rawQuery(sql, null);
//
//            if(c.moveToFirst()){
//
//                ArrayList<VO_0x01_Insert> list = new ArrayList<VO_0x01_Insert>();
//                do{
//                    VO_0x01_Insert data = new VO_0x01_Insert();
//                    data.setPR_COUNT(c.getString(c.getColumnIndex(TABLE_0x01_Insert.PR_COUNT)));
//                    data.setPR_DATE(c.getString(c.getColumnIndex(TABLE_0x01_Insert.PR_DATE)));
//                    data.setVOLTAGE(c.getString(c.getColumnIndex(TABLE_0x01_Insert.VOLTAGE)));
//                    data.setVOLTAGE_PERCENT(c.getString(c.getColumnIndex(TABLE_0x01_Insert.VOLTAGE_PERCENT)));
//                    data.setLAC(c.getString(c.getColumnIndex(TABLE_0x01_Insert.LAC)));
//                    data.setMNC(c.getString(c.getColumnIndex(TABLE_0x01_Insert.MNC)));
//                    data.setRSSI(c.getString(c.getColumnIndex(TABLE_0x01_Insert.RSSI)));
//                    data.setCELL_ID(c.getString(c.getColumnIndex(TABLE_0x01_Insert.CELL_ID)));
//                    data.setMCC(c.getString(c.getColumnIndex(TABLE_0x01_Insert.MCC)));
//                    data.setGSensor_AVG(c.getString(c.getColumnIndex(TABLE_0x01_Insert.GSensor_AVG)));
//                    data.setGSensor_MAX(c.getString(c.getColumnIndex(TABLE_0x01_Insert.GSensor_MAX)));
//                    data.setPPM(c.getString(c.getColumnIndex(TABLE_0x01_Insert.PPM)));
//                    data.setGPS_STATUS(c.getString(c.getColumnIndex(TABLE_0x01_Insert.GPS_STATUS)));
//                    data.setGPS_LAT(c.getString(c.getColumnIndex(TABLE_0x01_Insert.GPS_LAT)));
//                    data.setGPS_LNG(c.getString(c.getColumnIndex(TABLE_0x01_Insert.GPS_LNG)));
//                    data.setGPS_ADDRESS(c.getString(c.getColumnIndex(TABLE_0x01_Insert.GPS_ADDRESS)));
//                    data.setGPS_ACCURACY(c.getString(c.getColumnIndex(TABLE_0x01_Insert.GPS_ACCURACY)));
//                    data.setWIFI_MAC(c.getString(c.getColumnIndex(TABLE_0x01_Insert.WIFI_MAC)));
//                    data.setWIFI_Signal_dB(c.getString(c.getColumnIndex(TABLE_0x01_Insert.WIFI_Signal_dB)));
//                    data.setITEMNO(c.getString(c.getColumnIndex(TABLE_0x01_Insert.ITEMNO)));
//                    data.setWIFI_Channel(c.getString(c.getColumnIndex(TABLE_0x01_Insert.WIFI_Channel)));
//
//                    list.add(data);
//
//                }while(c.moveToNext());
//                return list;
//            }
//
//            return null;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }finally{
//            if(c!=null)
//                c.close();
//            db.close();
//        }
//    }

//    public ArrayList<VO_0x29_Insert> getdata_0x29_Insert(Context context, String type){
//        dbtc = new Create_Table(context);
//        db = dbtc.getWritableDatabase();
//        Cursor c = null;
//        String sql = null;
//        if(type.equals("getall")){
//            sql = "SELECT * FROM "+TABLE_0x29_Insert.TABLE_NAME;
//        }else{
//            sql = "SELECT * FROM "+TABLE_0x29_Insert.TABLE_NAME+" ORDER BY ITEMNO LIMIT 0,3 ";
//        }
//        try {
////			c = db.query(TABLE_0x29_Insert.TABLE_NAME,
////					null,	// select *
////					null,
////					null,
////					null,
////					null,
////					null
////					);
//            c = db.rawQuery(sql, null);
//
//            if(c.moveToFirst()){
//
//                ArrayList<VO_0x29_Insert> list = new ArrayList<VO_0x29_Insert>();
//                do{
//                    VO_0x29_Insert data = new VO_0x29_Insert();
//                    data.setITEMNO(c.getString(c.getColumnIndex(TABLE_0x29_Insert.ITEMNO)));
//                    data.setEF_COUNT(c.getString(c.getColumnIndex(TABLE_0x29_Insert.EF_COUNT)));
//                    data.setEF_DATE(c.getString(c.getColumnIndex(TABLE_0x29_Insert.EF_DATE)));
//                    data.setVOLTAGE(c.getString(c.getColumnIndex(TABLE_0x29_Insert.VOLTAGE)));
//                    data.setVOLTAGE_PERCENT(c.getString(c.getColumnIndex(TABLE_0x29_Insert.VOLTAGE_PERCENT)));
//                    data.setLAC(c.getString(c.getColumnIndex(TABLE_0x29_Insert.LAC)));
//                    data.setMNC(c.getString(c.getColumnIndex(TABLE_0x29_Insert.MNC)));
//                    data.setRSSI(c.getString(c.getColumnIndex(TABLE_0x29_Insert.RSSI)));
//                    data.setCELL_ID(c.getString(c.getColumnIndex(TABLE_0x29_Insert.CELL_ID)));
//                    data.setMCC(c.getString(c.getColumnIndex(TABLE_0x29_Insert.MCC)));
//                    data.setGSensor_AVG(c.getString(c.getColumnIndex(TABLE_0x29_Insert.GSensor_AVG)));
//                    data.setGSensor_MAX(c.getString(c.getColumnIndex(TABLE_0x29_Insert.GSensor_MAX)));
//                    data.setTEMP(c.getString(c.getColumnIndex(TABLE_0x29_Insert.TEMP)));
//                    data.setPPM(c.getString(c.getColumnIndex(TABLE_0x29_Insert.PPM)));
//                    data.setCELLER_LAC(c.getString(c.getColumnIndex(TABLE_0x29_Insert.CELLER_LAC)));
//                    data.setCELLER_MNC(c.getString(c.getColumnIndex(TABLE_0x29_Insert.CELLER_MNC)));
//                    data.setCELLER_RSSI(c.getString(c.getColumnIndex(TABLE_0x29_Insert.CELLER_RSSI)));
//                    data.setCELLER_CELL_ID(c.getString(c.getColumnIndex(TABLE_0x29_Insert.CELLER_CELL_ID)));
//                    data.setCELLER_MCC(c.getString(c.getColumnIndex(TABLE_0x29_Insert.CELLER_MCC)));
//                    data.setGPS_STATUS(c.getString(c.getColumnIndex(TABLE_0x29_Insert.GPS_STATUS)));
//                    data.setGPS_LAT(c.getString(c.getColumnIndex(TABLE_0x29_Insert.GPS_LAT)));
//                    data.setGPS_LNG(c.getString(c.getColumnIndex(TABLE_0x29_Insert.GPS_LNG)));
//                    data.setGPS_ACCURACY(c.getString(c.getColumnIndex(TABLE_0x29_Insert.GPS_ACCURACY)));
//                    data.setGPS_ADDRESS(c.getString(c.getColumnIndex(TABLE_0x29_Insert.GPS_ADDRESS)));
//                    data.setELECTRIC_FENCE_ITEMNO(c.getString(c.getColumnIndex(TABLE_0x29_Insert.ELECTRIC_FENCE_ITEMNO)));
//                    data.setWIFI_MAC(c.getString(c.getColumnIndex(TABLE_0x29_Insert.WIFI_MAC)));
//                    data.setWIFI_Signal_dB(c.getString(c.getColumnIndex(TABLE_0x29_Insert.WIFI_Signal_dB)));
//                    data.setWIFI_Channel(c.getString(c.getColumnIndex(TABLE_0x29_Insert.WIFI_Channel)));
//                    data.setCREATE_DATE(c.getString(c.getColumnIndex(TABLE_0x29_Insert.CREATE_DATE)));
//                    list.add(data);
//
//                }while(c.moveToNext());
//                return list;
//            }
//
//            return null;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }finally{
//            if(c!=null)
//                c.close();
//            db.close();
//        }
//    }

//    public ArrayList<Mon_VO> getmon(Context context){
//        dbtc = new Create_Table(context);
//        db = dbtc.getWritableDatabase();
//        Cursor c=null;
//        try {
//            c = db.query(Mon_Table.TABLE_NAME,
//                    null,	// select *
//                    null,
//                    null,
//                    null,
//                    null,
//                    null
//            );
//
//            if(c.moveToFirst()){
//
//                ArrayList<Mon_VO> list = new ArrayList<Mon_VO>();
//                do{
//                    Mon_VO data = new Mon_VO();
//                    data.setOverview_no(c.getString(c.getColumnIndex(Mon_Table.OVERVIEW_NO)));
//                    data.setRUN_NO(c.getString(c.getColumnIndex(Mon_Table.RUN_NO)));
//                    data.setMODEL(c.getString(c.getColumnIndex(Mon_Table.MODEL)));
//                    data.setIMEI(c.getString(c.getColumnIndex(Mon_Table.IMEI)));
//                    data.setS_TIME(c.getString(c.getColumnIndex(Mon_Table.S_TIME)));
//                    data.setE_TIME(c.getString(c.getColumnIndex(Mon_Table.E_TIME)));
//                    data.setSNO(c.getString(c.getColumnIndex(Mon_Table.SNO)));
//                    data.setWEIGHT(c.getString(c.getColumnIndex(Mon_Table.WEIGHT)));
//                    data.setCREATE_DATE(c.getString(c.getColumnIndex(Mon_Table.CREATE_DATE)));
//                    data.setSETTING_TIME(c.getString(c.getColumnIndex(Mon_Table.SETTING_TIME)));
//                    data.setSETTING_KM(c.getString(c.getColumnIndex(Mon_Table.SETTING_KM)));
//                    data.setSETTING_CAL(c.getString(c.getColumnIndex(Mon_Table.SETTING_CAL)));
//                    data.setDATETIME(c.getString(c.getColumnIndex(Mon_Table.DATETIME)));
//                    data.setHZ(c.getString(c.getColumnIndex(Mon_Table.HZ)));
//                    list.add(data);
//
//                }while(c.moveToNext());
//                return list;
//            }
//
//            return null;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }finally{
//            if(c!=null)
//                c.close();
//            db.close();
//        }
//    }

//    public ArrayList<Mon_Location_VO> getmon_location(Context context){
//        dbtc = new Create_Table(context);
//        db = dbtc.getWritableDatabase();
//        Cursor c=null;
//        try {
//            c = db.query(Mon_Location_Table.TABLE_NAME,
//                    null,	// select *
//                    null,
//                    null,
//                    null,
//                    null,
//                    null
//            );
//
//            if(c.moveToFirst()){
//
//                ArrayList<Mon_Location_VO> list = new ArrayList<Mon_Location_VO>();
//                do{
//                    Mon_Location_VO data = new Mon_Location_VO();
//
//                    data.setOverview_no(c.getString(c.getColumnIndex(Mon_Location_Table.OVERVIEW_NO)));
//                    data.setIMEI(c.getString(c.getColumnIndex(Mon_Location_Table.IMEI)));
//                    data.setITEMNO(c.getString(c.getColumnIndex(Mon_Location_Table.ITEMNO)));
//                    data.setGPS_STATUS(c.getString(c.getColumnIndex(Mon_Location_Table.GPS_STATUS)));
//                    data.setMY_GPS_LAT(c.getString(c.getColumnIndex(Mon_Location_Table.MY_GPS_LAT)));
//                    data.setMY_GPS_LNG(c.getString(c.getColumnIndex(Mon_Location_Table.MY_GPS_LNG)));
//                    data.setSPEED(c.getString(c.getColumnIndex(Mon_Location_Table.SPEED)));
//                    data.setCAL(c.getString(c.getColumnIndex(Mon_Location_Table.CAL)));
//                    data.setDISTANCE(c.getString(c.getColumnIndex(Mon_Location_Table.DISTANCE)));
//                    data.setSTEPS(c.getString(c.getColumnIndex(Mon_Location_Table.STEPS)));
//                    data.setTOTAL_STEPS(c.getString(c.getColumnIndex(Mon_Location_Table.TOTAL_STEPS)));
//                    data.setDIFFERENCE_OF_HEIGHT(c.getString(c.getColumnIndex(Mon_Location_Table.DIFFERENCE_OF_HEIGHT)));
//                    data.setHPE(c.getString(c.getColumnIndex(Mon_Location_Table.HPE)));
//                    data.setGPS_TIME(c.getString(c.getColumnIndex(Mon_Location_Table.GPS_TIME)));
//                    data.setDATETIME(c.getString(c.getColumnIndex(Mon_Location_Table.DATETIME)));
//                    data.setCREATE_DATE(c.getString(c.getColumnIndex(Mon_Location_Table.CREATE_DATE)));
//                    data.setUPDATE_DATE(c.getString(c.getColumnIndex(Mon_Location_Table.UPDATE_DATE)));
//                    list.add(data);
//
//                }while(c.moveToNext());
//                return list;
//            }
//
//            return null;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }finally{
//            if(c!=null)
//                c.close();
//            db.close();
//        }
//    }

//    public ArrayList<Mon_GPS_VO> getGPS(Context context){
//        dbtc = new Create_Table(context);
//        db = dbtc.getWritableDatabase();
//        Cursor c=null;
//        try {
//            c = db.query(Mon_GPS_Table.TABLE_NAME,
//                    null,	// select *
//                    null,
//                    null,
//                    null,
//                    null,
//                    null
//            );
//
//            if(c.moveToFirst()){
//
//                ArrayList<Mon_GPS_VO> list = new ArrayList<Mon_GPS_VO>();
//                do{
//                    Mon_GPS_VO data = new Mon_GPS_VO();
//                    data.setOverview_no(c.getString(c.getColumnIndex(Mon_GPS_Table.OVERVIEW_NO)));
//                    data.setIMEI(c.getString(c.getColumnIndex(Mon_GPS_Table.IMEI)));
//                    data.setITEMNO(c.getString(c.getColumnIndex(Mon_GPS_Table.ITEMNO)));
//                    data.setGPS_STATUS(c.getString(c.getColumnIndex(Mon_GPS_Table.GPS_STATUS)));
//                    data.setMY_GPS_LAT(c.getString(c.getColumnIndex(Mon_GPS_Table.MY_GPS_LAT)));
//                    data.setMY_GPS_LNG(c.getString(c.getColumnIndex(Mon_GPS_Table.MY_GPS_LNG)));
//                    data.setSPEED(c.getString(c.getColumnIndex(Mon_GPS_Table.SPEED)));
//                    data.setCAL(c.getString(c.getColumnIndex(Mon_GPS_Table.CAL)));
//                    data.setDISTANCE(c.getString(c.getColumnIndex(Mon_GPS_Table.DISTANCE)));
//                    data.setSTEPS(c.getString(c.getColumnIndex(Mon_GPS_Table.STEPS)));
//                    data.setTOTAL_STEPS(c.getString(c.getColumnIndex(Mon_GPS_Table.TOTAL_STEPS)));
//                    data.setDIFFERENCE_OF_HEIGHT(c.getString(c.getColumnIndex(Mon_GPS_Table.DIFFERENCE_OF_HEIGHT)));
//                    data.setHPE(c.getString(c.getColumnIndex(Mon_GPS_Table.HPE)));
//                    data.setGPS_TIME(c.getString(c.getColumnIndex(Mon_GPS_Table.GPS_TIME)));
//                    data.setDATETIME(c.getString(c.getColumnIndex(Mon_GPS_Table.DATETIME)));
//                    data.setCREATE_DATE(c.getString(c.getColumnIndex(Mon_GPS_Table.CREATE_DATE)));
//                    data.setUPDATE_DATE(c.getString(c.getColumnIndex(Mon_GPS_Table.UPDATE_DATE)));
//                    list.add(data);
//
//                }while(c.moveToNext());
//                return list;
//            }
//
//            return null;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }finally{
//            if(c!=null)
//                c.close();
//            db.close();
//        }
//    }

//    public ArrayList<VO_801603> getData_801603(Context context, String no){
//        dbtc = new Create_Table(context);
//        db = dbtc.getWritableDatabase();
//        Cursor c = null;
//        String sql = null;
//
//        try {
//            if(!(no.equals(""))){
//                sql = "SELECT * FROM "+ TABLE_801603.TABLE_NAME+" WHERE missing_person_no = " +
//                        no +" ORDER BY missing_person_no desc";
//            }else if(no.equals("")){
//                sql = "SELECT * FROM "+TABLE_801603.TABLE_NAME+" ORDER BY missing_person_no desc LIMIT 0,10 ";
//            }
//            c = db.rawQuery(sql, null);
////			c = db.query(TABLE_801603.TABLE_NAME,
////					null,	// select *
////					null,
////					null,
////					null,
////					null,
////					null
////					);
//            if(c.moveToFirst()){
//
//                ArrayList<VO_801603> list = new ArrayList<VO_801603>();
//                do{
//                    VO_801603 data = new VO_801603();
//                    data.setBirthday(c.getString(c.getColumnIndex(TABLE_801603.birthday)));
//                    data.setBroadcaset_account(c.getString(c.getColumnIndex(TABLE_801603.broadcaset_account)));
//                    data.setContact_name(c.getString(c.getColumnIndex(TABLE_801603.contact_name)));
//                    data.setContact_tel(c.getString(c.getColumnIndex(TABLE_801603.contact_tel)));
//                    data.setContact_type(c.getString(c.getColumnIndex(TABLE_801603.contact_type)));
//                    data.setEvent_time(c.getString(c.getColumnIndex(TABLE_801603.event_time)));
//                    data.setImei(c.getString(c.getColumnIndex(TABLE_801603.imei)));
//                    data.setMissing_person_no(c.getString(c.getColumnIndex(TABLE_801603.missing_person_no)));
//                    data.setMissing_status(c.getString(c.getColumnIndex(TABLE_801603.missing_status)));
//                    data.setName(c.getString(c.getColumnIndex(TABLE_801603.name)));
//                    data.setPhoto_url(c.getString(c.getColumnIndex(TABLE_801603.photo_url)));
//                    data.setUpdatatime(c.getString(c.getColumnIndex(TABLE_801603.updatatime)));
//                    list.add(data);
//
//                }while(c.moveToNext());
//                return list;
//            }
//            return null;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }finally{
//            if(c!=null)
//                c.close();
//            db.close();
//        }
//    }

//    public ArrayList<VO_0x18> getData_0x18(Context context){
//        dbtc = new Create_Table(context);
//        db = dbtc.getWritableDatabase();
//        Cursor c=null;
//        try {
//            c = db.query(TABLE_0x18.TABLE_NAME,
//                    null,	// select *
//                    null,
//                    null,
//                    null,
//                    null,
//                    null
//            );
//
//            if(c.moveToFirst()){
//
//                ArrayList<VO_0x18> list = new ArrayList<VO_0x18>();
//                do{
//                    VO_0x18 data = new VO_0x18();
//                    data.setAccount(c.getString(c.getColumnIndex(TABLE_0x18.account)));
//                    data.setNO(c.getString(c.getColumnIndex(TABLE_0x18.NO)));
//                    data.setTRACKING_DATE(c.getString(c.getColumnIndex(TABLE_0x18.TRACKING_DATE)));
//                    data.setGPS_LAT(c.getString(c.getColumnIndex(TABLE_0x18.GPS_LAT)));
//                    data.setGPS_LNG(c.getString(c.getColumnIndex(TABLE_0x18.GPS_LNG)));
//                    data.setGPS_ADDRESS(c.getString(c.getColumnIndex(TABLE_0x18.GPS_ADDRESS)));
//                    data.setGPS_ACCURACY(c.getString(c.getColumnIndex(TABLE_0x18.GPS_ACCURACY)));
//                    data.setGPS_STATUS(c.getString(c.getColumnIndex(TABLE_0x18.GPS_STATUS)));
//                    data.setMCC(c.getString(c.getColumnIndex(TABLE_0x18.MCC)));
//                    data.setMNC(c.getString(c.getColumnIndex(TABLE_0x18.MNC)));
//                    data.setLAC(c.getString(c.getColumnIndex(TABLE_0x18.LAC)));
//                    data.setCELL_ID(c.getString(c.getColumnIndex(TABLE_0x18.CELL_ID)));
//                    data.setRSSI(c.getString(c.getColumnIndex(TABLE_0x18.RSSI)));
//                    data.setWIFI_MAC(c.getString(c.getColumnIndex(TABLE_0x18.WIFI_MAC)));
//                    data.setWIFI_Signal_dB(c.getString(c.getColumnIndex(TABLE_0x18.WIFI_Signal_dB)));
//                    data.setWIFI_Channel(c.getString(c.getColumnIndex(TABLE_0x18.WIFI_Channel)));
//                    data.setCREATE_DATE(c.getString(c.getColumnIndex(TABLE_0x18.CREATE_DATE)));
//                    list.add(data);
//
//                }while(c.moveToNext());
//                return list;
//            }
//            return null;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }finally{
//            if(c!=null)
//                c.close();
//            db.close();
//        }
//    }
    //跌倒
//    public ArrayList<VO_851001> Getdata_851001(Context context){
//        dbtc = new Create_Table(context);
//        db = dbtc.getWritableDatabase();
//        Cursor c=null;
//        try {
//            c = db.query(TABLE_851001.TABLE_NAME,
//                    null,	// select *
//                    null,
//                    null,
//                    null,
//                    null,
//                    null
//            );
//
//            if(c.moveToFirst()){
//
//                ArrayList<VO_851001> list = new ArrayList<VO_851001>();
//                do{
//                    VO_851001 data = new VO_851001();
//                    data.setAccount(c.getString(c.getColumnIndex(TABLE_851001.account)));
//                    data.setFallDetectSwitch(c.getString(c.getColumnIndex(TABLE_851001.fallDetectSwitch)));
//                    data.setDelayTimer(c.getString(c.getColumnIndex(TABLE_851001.delayTimer)));
//                    data.setServiceIP(c.getString(c.getColumnIndex(TABLE_851001.serviceIP)));
//                    data.setFromtime(c.getString(c.getColumnIndex(TABLE_851001.fromtime)));
//                    data.setTotime(c.getString(c.getColumnIndex(TABLE_851001.totime)));
//                    data.setWeek(c.getString(c.getColumnIndex(TABLE_851001.week)));
//                    data.setEnable(c.getString(c.getColumnIndex(TABLE_851001.enable)));
//                    data.setPhone1(c.getString(c.getColumnIndex(TABLE_851001.phone1)));
//                    data.setPhone2(c.getString(c.getColumnIndex(TABLE_851001.phone2)));
//                    data.setPhone3(c.getString(c.getColumnIndex(TABLE_851001.phone3)));
//                    data.setCREATE_DATE(c.getString(c.getColumnIndex(TABLE_851001.CREATE_DATE)));
//                    data.setNUMBER(c.getString(c.getColumnIndex(TABLE_851001.NUMBER)));
//                    list.add(data);
//                }while(c.moveToNext());
//                return list;
//            }
//
//            return null;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }finally{
//            if(c!=null)
//                c.close();
//            db.close();
//        }
//    }

//    public ArrayList<VO_851301> Getdata_851301(Context context){
//        dbtc = new Create_Table(context);
//        db = dbtc.getWritableDatabase();
//        Cursor c=null;
//        try {
//            c = db.query(TABLE_851301.TABLE_NAME,
//                    null,	// select *
//                    null,
//                    null,
//                    null,
//                    null,
//                    null
//            );
//
//            if(c.moveToFirst()){
//
//                ArrayList<VO_851301> list = new ArrayList<VO_851301>();
//                do{
//                    VO_851301 data = new VO_851301();
//                    data.setAccount(c.getString(c.getColumnIndex(TABLE_851301.account)));
//                    data.setEMG_DATE(c.getString(c.getColumnIndex(TABLE_851301.EMG_DATE)));
//                    data.setGPS_LAT(c.getString(c.getColumnIndex(TABLE_851301.GPS_LAT)));
//                    data.setGPS_LNG(c.getString(c.getColumnIndex(TABLE_851301.GPS_LNG)));
//                    data.setGPS_ADDRESS(c.getString(c.getColumnIndex(TABLE_851301.GPS_ADDRESS)));
//                    data.setGPS_ACCURACY(c.getString(c.getColumnIndex(TABLE_851301.GPS_ACCURACY)));
//                    data.setGPS_STATUS(c.getString(c.getColumnIndex(TABLE_851301.GPS_STATUS)));
//                    data.setMCC(c.getString(c.getColumnIndex(TABLE_851301.MCC)));
//                    data.setMNC(c.getString(c.getColumnIndex(TABLE_851301.MNC)));
//                    data.setLAC(c.getString(c.getColumnIndex(TABLE_851301.LAC)));
//                    data.setCELL_ID(c.getString(c.getColumnIndex(TABLE_851301.CELL_ID)));
//                    data.setRSSI(c.getString(c.getColumnIndex(TABLE_851301.RSSI)));
//                    data.setWIFI_MAC(c.getString(c.getColumnIndex(TABLE_851301.WIFI_MAC)));
//                    data.setWIFI_Signal_dB(c.getString(c.getColumnIndex(TABLE_851301.WIFI_Signal_dB)));
//                    data.setWIFI_Channel(c.getString(c.getColumnIndex(TABLE_851301.WIFI_Channel)));
//                    data.setCREATE_DATE(c.getString(c.getColumnIndex(TABLE_851301.CREATE_DATE)));
//                    data.setNUMBER(c.getString(c.getColumnIndex(TABLE_851301.NUMBER)));
//                    list.add(data);
//                }while(c.moveToNext());
//                return list;
//            }
//
//            return null;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }finally{
//            if(c!=null)
//                c.close();
//            db.close();
//        }
//    }

//    public ArrayList<VO_851302> Getdata_851302(Context context){
//        dbtc = new Create_Table(context);
//        db = dbtc.getWritableDatabase();
//        Cursor c=null;
//        try {
//            c = db.query(TABLE_851302.TABLE_NAME,
//                    null,	// select *
//                    null,
//                    null,
//                    null,
//                    null,
//                    null
//            );
//
//            if(c.moveToFirst()){
//
//                ArrayList<VO_851302> list = new ArrayList<VO_851302>();
//                do{
//                    VO_851302 data = new VO_851302();
//                    data.setAccount(c.getString(c.getColumnIndex(TABLE_851302.account)));
//                    data.setEMG_DATE(c.getString(c.getColumnIndex(TABLE_851302.EMG_DATE)));
//                    data.setGPS_LAT(c.getString(c.getColumnIndex(TABLE_851302.GPS_LAT)));
//                    data.setGPS_LNG(c.getString(c.getColumnIndex(TABLE_851302.GPS_LNG)));
//                    data.setGPS_ADDRESS(c.getString(c.getColumnIndex(TABLE_851302.GPS_ADDRESS)));
//                    data.setGPS_ACCURACY(c.getString(c.getColumnIndex(TABLE_851302.GPS_ACCURACY)));
//                    data.setGPS_STATUS(c.getString(c.getColumnIndex(TABLE_851302.GPS_STATUS)));
//                    data.setMCC(c.getString(c.getColumnIndex(TABLE_851302.MCC)));
//                    data.setMNC(c.getString(c.getColumnIndex(TABLE_851302.MNC)));
//                    data.setLAC(c.getString(c.getColumnIndex(TABLE_851302.LAC)));
//                    data.setCELL_ID(c.getString(c.getColumnIndex(TABLE_851302.CELL_ID)));
//                    data.setRSSI(c.getString(c.getColumnIndex(TABLE_851302.RSSI)));
//                    data.setWIFI_MAC(c.getString(c.getColumnIndex(TABLE_851302.WIFI_MAC)));
//                    data.setWIFI_Signal_dB(c.getString(c.getColumnIndex(TABLE_851302.WIFI_Signal_dB)));
//                    data.setWIFI_Channel(c.getString(c.getColumnIndex(TABLE_851302.WIFI_Channel)));
//                    data.setCREATE_DATE(c.getString(c.getColumnIndex(TABLE_851302.CREATE_DATE)));
//                    data.setNUMBER(c.getString(c.getColumnIndex(TABLE_851302.NUMBER)));
//                    list.add(data);
//                }while(c.moveToNext());
//                return list;
//            }
//
//            return null;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }finally{
//            if(c!=null)
//                c.close();
//            db.close();
//        }
//    }

//    public ArrayList<VO_851303> Getdata_851303(Context context){
//        dbtc = new Create_Table(context);
//        db = dbtc.getWritableDatabase();
//        Cursor c=null;
//        try {
//            c = db.query(TABLE_851303.TABLE_NAME,
//                    null,	// select *
//                    null,
//                    null,
//                    null,
//                    null,
//                    null
//            );
//
//            if(c.moveToFirst()){
//
//                ArrayList<VO_851303> list = new ArrayList<VO_851303>();
//                do{
//                    VO_851303 data = new VO_851303();
//                    data.setAccount(c.getString(c.getColumnIndex(TABLE_851303.account)));
//                    data.setFD_DATE(c.getString(c.getColumnIndex(TABLE_851303.FD_DATE)));
//                    data.setImei(c.getString(c.getColumnIndex(TABLE_851303.imei)));
//                    data.setStatus(c.getString(c.getColumnIndex(TABLE_851303.status)));
//                    data.setACTIVE_DATE(c.getString(c.getColumnIndex(TABLE_851303.ACTIVE_DATE)));
//                    data.setCREATE_DATE(c.getString(c.getColumnIndex(TABLE_851303.CREATE_DATE)));
//                    data.setNUMBER(c.getString(c.getColumnIndex(TABLE_851303.NUMBER)));
//                    list.add(data);
//                }while(c.moveToNext());
//                return list;
//            }
//
//            return null;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }finally{
//            if(c!=null)
//                c.close();
//            db.close();
//        }
//    }

}

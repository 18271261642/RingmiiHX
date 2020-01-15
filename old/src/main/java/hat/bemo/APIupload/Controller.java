package hat.bemo.APIupload;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

import org.apache.http.NameValuePair;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

import hat.bemo.DataBase.DAO;
import hat.bemo.DataBase.Delete;
import hat.bemo.MyApplication;
import hat.bemo.VO.VO_0x20;
import hat.bemo.VO.VO_0x30;
import hat.bemo.setting.ChildThread;
import hat.bemo.setting.SharedPreferences_status;

/**
 * Created by apple on 2017/11/10.
 */

public class Controller {
//    public final static String type_0x01 = "0x01";
//    public final static String type_0x10 = "0x10";
//    public final static String type_0x11 = "0x11";
//    public final static String type_0x18 = "0x18";
    public final static String type_0x20 = "0x20";
//    public final static String type_0x28 = "0x28";
//    public final static String type_0x29 = "0x29";
//    public final static String type_0x2A = "0x2A";
//    public final static String type_0x2B = "0x2B";
//    public final static String type_0x2E = "0x2E";
//    public final static String type_0x2F = "0x2F";
    public final static String type_0x30 = "0x30";
//    public final static String type_0x3F = "0x3F";
//    public final static String type_0x40 = "0x40";
//    public final static String type_0x4B = "0x4B";
//    public final static String type_0x4C = "0x4C";
//    public final static String type_0x4D = "0x4D";
//    public final static String type_0x4W = "0x4W";
//    public final static String type_0x7F = "0x7F";
//    public final static String type_0x01_insert = "0x01_insert";
//    public final static String type_0x29_insert = "0x29_insert";
//    public final static String type_800721 = "800721";
//    public final static String type_801002 = "801002";
//    public final static String type_800908 = "800908";
//    public final static String type_801610 = "801610";
//    public final static String type_801611 = "801611";

    private String GkeyId = "1";
    private String Gkey = "AAAAAAAAZZZZZZZZZZZZ999999999";
    private SharedPreferences_status sh;
    private String DataHash;
    private String imei;

    private List<NameValuePair> ValuePair;
    private FieldName fn;
    public static String CreateDate;

    private String DataJosn;
    private JsonFormat json;
    private String URL;
    private String TYPE;
    private static Context context;
    public static ProgressDialog progressDialog;
    private String[] Settings_type;
    private String[] Settings;
    private String System_Settings_Item;
    private String System_Settings_Value;

//    private VO_0x2A data_2A;
//    private VO_0x2E data_2E;
//    private VO_0x2F data_2F;
//    private VO_0x01 data_01;
//    private VO_0x10 data_10;
//    private VO_0x11 data_11;
//    private VO_0x18 data_18;
    private VO_0x20 data_20;
//    private VO_0x28 data_28;
//    private VO_0x29 data_29;
//    private VO_0x2B data_2B;
    private static VO_0x30 data_30;
//    private VO_0x3F data_3F;
//    private static VO_0x40 data_40;
//    private VO_0x4B data_4B;
//    private VO_0x4C data_4C;
//    private VO_0x4D data_4D;
//    private VO_0x4W data_4W;
//    private VO_0x7F data_7F;
//    public static VO_0x01_Insert data_01_Insert;
//    public static VO_0x29_Insert data_29_Insert;
//    //防呆
//    public ArrayList<VO_0x01_Insert> get0x01_insert;
//    public ArrayList<VO_0x29_Insert> get0x29_insert;

    private ArrayList<VO_0x30> get0x30;
//    private ArrayList<VO_0x29> get0x29;
//    private ArrayList<VO_0x01> get0x01;

    private String getgoal,getStep,getWeight;
//    private Get_DB getdb;
//    private SettingPedometerVO vo;
    public static String TimeStamp;
    private static Delete delete = new Delete();
    private static DAO dao;
//    private ArrayList<SettingPedometerVO> volist = new ArrayList<SettingPedometerVO>();

    @SuppressWarnings("static-access")
    public void LoData(Context context, String type){
        try{
            dao = new DAO();

            CreateDate = ChangeDateFormat.CreateDate();
            TimeStamp = ChangeDateFormat.TimeStamp();
            sh = new SharedPreferences_status(context);
            imei = sh.Get_IMEI(MyApplication.context);
            Settings_type = type.split(";");
            Log.e("Settings_type0", Settings_type[0]);

            this.context = context;

            if(json == null)
                json = new JsonFormat();
            if(fn == null)
                fn = new FieldName();

//            Loaddata(type_0x2A);

            ESettings_type mSetting_type = ESettings_type.ByStr(Settings_type[0]);
            switch(mSetting_type){
//                case type_0x2A:
//                    //jim 手錶登入 /APP/GcareWatchLogin device參數 請填3    (1:android app , 3:watch android,0:ios app) 15MW
////					DataJosn = json.jsonformat(type_0x2A, data_2A.getDEVICE_TOKEN(), "3", "15", imei);
//                    DataJosn = json.jsonformat(type_0x2A, "APA91bENHRzhxTHQ6f5C7Id0lUNc3W7iHLLZ09oU6YYOoPkUiGG5Oqs8nRkZMMinqyuaAezjujmAS--A2P1asdY6rwJGWstJWTpdsFGCbp1NaG75tL9eozplMfI5InAHkTOFLXYTQHshE0AxsyAeKt_Oj_gPuX-mWg", "3", "15", imei);
//
//                    URL = HTTP+Check_IP()+SERVER_HOST_0x2A;
//                    System.out.println("Mark 手錶登入" + URL);
//                    TYPE = type_0x2A;
//                    DataHash = encrypt(Gkey, TimeStamp, DataJosn);
//                    ValuePair =	fn.NameValuePair(DataHash, GkeyId, TimeStamp, DataJosn);
//                    PostData();
//                    Log.e(type_0x2A, "設備登陸");
//                    break;
//
//                case type_0x2B:
//                    Loaddata(type_0x2B);
//                    DataJosn = json.jsonformat(type_0x2B, imei, data_2B.getALERT_TYPE());
//                    URL = HTTP+Check_IP()+SERVER_HOST_0x2B;
//                    TYPE = type_0x2B;
//                    DataHash = encrypt(Gkey, TimeStamp, DataJosn);
//                    ValuePair =	fn.NameValuePair(DataHash, GkeyId, TimeStamp, DataJosn);
//                    PostData();
//                    Log.e(type_0x2B, "異常告警");
//                    break;
//
//                case type_0x2E:
//                    Log.e(type_0x2E, "開機封包驗證");
//                    Loaddata(type_0x2E);
//
//                    CreateDate = new FW_Build_Date(MyApplication.context).Get_Version_Date("FW BUILD DATE", "FW BUILD DATE");
//
//                    if(API.Version.equals("G800.v1") && CreateDate.equals("")){
//                        Log.e("版本", "版本更新G800.v1");
//                        CreateDate = ChangeDateFormat.CreateDate();
//                        new FW_Build_Date(MyApplication.context).set_Version_Date("FW BUILD DATE", "FW BUILD DATE", CreateDate);
//                    }
//
//                    CreateDate = new FW_Build_Date(MyApplication.context).Get_Version_Date("FW BUILD DATE", "FW BUILD DATE");
//
//                    DataJosn = json.jsonformat(type_0x2E, ChangeDateFormat.CreateDate(),Build.DISPLAY,      data_2E.getVOLTAGE(),
//                            data_2E.getVOLTAGE_PERCENT(), data_2E.getMCC(),     data_2E.getMNC(),
//                            data_2E.getLAC(),             data_2E.getCELL_ID(), data_2E.getRSSI(),
//                            ChangeDateFormat.CreateDate(), imei);
//
//                    URL = HTTP+Check_IP()+SERVER_HOST_0x2E;
//                    System.out.println("Mark 開機封包" + URL);
//                    TYPE = type_0x2E;
//                    DataHash = encrypt(Gkey, TimeStamp, DataJosn);
////					Log.e("DataHash", DataHash);
//                    ValuePair =	fn.NameValuePair(DataHash, GkeyId, TimeStamp, DataJosn);
//                    PostData();
//                    break;
//
//                case type_0x2F:
//                    Loaddata(type_0x2F);
//
//                    DataJosn = json.jsonformat(type_0x2F, ChangeDateFormat.CreateDate(), data_2F.getPWR_OFF_TYPE(),
//                            data_2F.getVOLTAGE(),
//                            data_2F.getVOLTAGE_PERCENT(),
//                            data_2F.getGPS_ACCURACY(),
//                            data_2F.getGPS_LAT(),
//                            data_2F.getGPS_LNG(),
//                            data_2F.getGPS_STATUS(),
//                            data_2F.getMCC(),
//                            data_2F.getMNC(),
//                            data_2F.getLAC(),
//                            data_2F.getCELL_ID(),
//                            data_2F.getRSSI(),
//                            ChangeDateFormat.CreateDate(), imei);
//
//                    URL = HTTP+Check_IP()+SERVER_HOST_0x2F;
//                    TYPE = type_0x2F;
//                    DataHash = encrypt(Gkey, TimeStamp, DataJosn);
//                    //				Log.e("DataHash", DataHash);
//                    ValuePair =	fn.NameValuePair(DataHash, GkeyId, TimeStamp, DataJosn);
//                    PostData();
//                    Log.e(type_0x2F, "關機封包");
//                    break;
//
//
//                case type_0x01:
//                    System.out.println("Mark 這邊又是啥?");
//                    Loaddata(type_0x01);
//                    JsonFormat.JsonSize(get0x01.size());
//                    DataJosn = json.jsonformat(type_0x01, "1", CreateDate, data_01.getVOLTAGE(),       data_01.getVOLTAGE_PERCENT(),
//                            data_01.getLAC(),            data_01.getMNC(),           data_01.getRSSI(),
//                            data_01.getCELL_ID(),        data_01.getMCC(),           data_01.getGSensor_AVG(),
//                            data_01.getGSensor_MAX(),    data_01.getPPM(),           data_01.getGPS_STATUS(),
//                            data_01.getGPS_LAT(),        data_01.getGPS_LNG(),       data_01.getGPS_ADDRESS(),
//                            data_01.getGPS_ACCURACY(),   data_01.getGSensor_AVG(),   data_01.getWIFI_MAC(),
//                            data_01.getWIFI_Signal_dB(), data_01.getWIFI_Channel(), imei);
//
//                    URL = HTTP+Check_IP()+SERVER_HOST_0x01;
//                    TYPE = type_0x01;
//                    DataHash = encrypt(Gkey, TimeStamp, DataJosn);
//                    ValuePair =	fn.NameValuePair(DataHash, GkeyId, TimeStamp, DataJosn);
//                    PostData();
//                    Log.e(type_0x01, "定期封包上傳");
//                    break;
//
//                case type_0x7F:
//                    Loaddata(type_0x10);
//                    Loaddata(type_0x11);
//                    String checksum_10 = data_10.getCHECKSUM();
//                    String checksum_11 = data_11.getCHECKSUM();
//
//                    if(checksum_10 == null){
//                        checksum_10 = " ";
//                    }
//                    if(checksum_11 == null){
//                        checksum_11 = " ";
//                    }
//                    DataJosn = json.jsonformat(type_0x7F, checksum_10, checksum_11, imei);
//                    URL = HTTP+Check_IP()+SERVER_HOST_0x7F;
//                    TYPE = type_0x7F;
//                    DataHash = encrypt(Gkey, TimeStamp, DataJosn);
//                    ValuePair =	fn.NameValuePair(DataHash, GkeyId, TimeStamp, DataJosn);
//                    PostData();
//                    Log.e(type_0x7F, "查詢更新項目...");
//                    break;
//
//                case type_0x10:
//                    Loaddata(type_0x10);
//                    DataJosn = json.jsonformat(type_0x10, imei);
//                    URL = HTTP+Check_IP()+SERVER_HOST_0x10;
//                    TYPE = type_0x10;
//                    DataHash = encrypt(Gkey, TimeStamp, DataJosn);
//                    ValuePair =	fn.NameValuePair(DataHash, GkeyId, TimeStamp, DataJosn);
//                    PostData();
//                    Log.e(type_0x10, "獲取更新資料0x10...");
//                    break;
//
//                case type_0x11:
//                    Loaddata(type_0x11);
//                    DataJosn = json.jsonformat(type_0x11, imei);
//                    URL = HTTP+Check_IP()+SERVER_HOST_0x11;
//                    TYPE = type_0x11;
//                    DataHash = encrypt(Gkey, TimeStamp, DataJosn);
//                    ValuePair =	fn.NameValuePair(DataHash, GkeyId, TimeStamp, DataJosn);
//                    PostData();
//                    Log.e(type_0x11, "獲取更新資料0x11...");
//                    break;
//
//                case type_0x18:
//                    Loaddata(type_0x18);
//                    DataJosn = json.jsonformat(type_0x18,                 data_18.getLAC(),         data_18.getMNC(),
//                            data_18.getRSSI(),         data_18.getCELL_ID(),     data_18.getMCC(),
//                            data_18.getGPS_STATUS(),   data_18.getGPS_ACCURACY(),
//                            data_18.getGPS_ADDRESS(),  data_18.getGPS_LNG(),     data_18.getGPS_LAT(),
//                            data_18.getTRACKING_DATE(),data_18.getWIFI_MAC(),    data_18.getWIFI_Signal_dB(),
//                            data_18.getWIFI_Channel(), data_18.getCREATE_DATE(), data_18.getAccount());
//                    URL = HTTP+Check_IP()+SERVER_HOST_0x18;
//                    TYPE = type_0x18;
//                    DataHash = encrypt(Gkey, TimeStamp, DataJosn);
//                    ValuePair =	fn.NameValuePair(DataHash, GkeyId, TimeStamp, DataJosn);
//                    PostData();
//                    Log.e(type_0x18, "獲取資料0x18...");
//                    break;
//
//                case type_0x40:
//                    if(SharedPreferences_status.Get_Status0x40(MyApplication.context).equals("1")){
//                        Loaddata(type_0x40);
//                        DataJosn = json.jsonformat(type_0x40, imei);
//                        URL = SERVER_HOST_0x40;
//                        TYPE = type_0x40;
//                        DataHash = encrypt(Gkey, TimeStamp, DataJosn);
//                        ValuePair =	fn.NameValuePair(DataHash, GkeyId, TimeStamp, DataJosn);
//                        PostData();
//                        Log.e(type_0x40, "獲取更新資料0x40...");
//                    }else{
//                        Log.e(type_0x40, "0x40已更新過...");
//                    }
//                    break;
//
//                case type_0x4B:
//                    Loaddata(type_0x4B);
//                    DataJosn = json.jsonformat(type_0x40, imei);
//                    URL = HTTP+Check_IP()+SERVER_HOST_0x4B;
//                    TYPE = type_0x4B;
//                    DataHash = encrypt(Gkey, TimeStamp, DataJosn);
//                    ValuePair =	fn.NameValuePair(DataHash, GkeyId, TimeStamp, DataJosn);
//                    PostData();
//                    Log.e(type_0x40, "獲取更新資料0x4B...");
//                    break;
//
//                case type_0x4C:
//                    Loaddata(type_0x4C);
//                    DataJosn = json.jsonformat(type_0x40, imei);
//
//                    URL = HTTP+Check_IP()+SERVER_HOST_0x4C;
//                    TYPE = type_0x4C;
//                    DataHash = encrypt(Gkey, TimeStamp, DataJosn);
//                    ValuePair =	fn.NameValuePair(DataHash, GkeyId, TimeStamp, DataJosn);
//                    PostData();
//                    Log.e(type_0x4C, "獲取更新資料0x4C...");
//                    break;
//
//                case type_0x4D:
//                    Loaddata(type_0x4D);
//                    DataJosn = json.jsonformat(type_0x40, imei);
//
//                    URL = HTTP+Check_IP()+SERVER_HOST_0x4D;
//                    TYPE = type_0x40;
//                    DataHash = encrypt(Gkey, TimeStamp, DataJosn);
//                    ValuePair =	fn.NameValuePair(DataHash, GkeyId, TimeStamp, DataJosn);
//                    PostData();
//                    Log.e(type_0x40, "獲取更新資料0x4D...");
//                    break;
//
//                case type_0x28:
//                    Loaddata(type_0x28);
//                    DataJosn = json.jsonformat(type_0x28, data_28.getCELL_ID(), data_28.getLAC(), data_28.getMCC(), data_28.getMNC(),
//                            data_28.getRSSI(), CreateDate, CreateDate,
//                            data_28.getGPS_ACCURACY(),   data_28.getGPS_ADDRESS(), data_28.getGPS_LAT(),
//                            data_28.getGPS_LNG(),        data_28.getGPS_STATUS(),  data_28.getWIFI_MAC(),
//                            data_28.getWIFI_Signal_dB(), data_28.getWIFI_Channel(),data_28.getCELL_ID(),
//                            data_28.getLAC(),            data_28.getMCC(),         data_28.getMNC(),
//                            data_28.getRSSI(), imei);
//
//                    URL = HTTP+Check_IP()+SERVER_HOST_0x28;
//                    TYPE = type_0x28;
//                    DataHash = encrypt(Gkey, TimeStamp, DataJosn);
//                    ValuePair =	fn.NameValuePair(DataHash, GkeyId, TimeStamp, DataJosn);
//                    PostData();
//                    Log.e(type_0x28, "立即定位上傳");
//                    break;

                case type_0x20:
                    /**
                     * 客製化功能 定位模式
                     * */
                        Loaddata(type_0x20);
                        DataJosn = json.jsonformat(type_0x20,                  data_20.getCELL_ID(),     data_20.getLAC(),
                                data_20.getMCC(),           data_20.getMNC(),         data_20.getRSSI(),
                                data_20.getCREATE_DATE(),   data_20.getEMG_DATE(),    data_20.getGPS_ACCURACY(),
                                data_20.getGPS_ADDRESS(),   data_20.getGPS_LAT(),     data_20.getGPS_LNG(),
                                data_20.getGPS_STATUS(),    data_20.getWIFI_Channel(),data_20.getWIFI_MAC(),
                                data_20.getWIFI_Signal_dB(),data_20.getCELL_ID(),     data_20.getLAC(),
                                data_20.getMCC(),		   data_20.getMNC(),         data_20.getRSSI(), imei);

                        URL = HTTP+Check_IP()+SERVER_HOST_0x20;
                        TYPE = type_0x20;
                        DataHash = encrypt(Gkey, TimeStamp, DataJosn);
                        ValuePair =	fn.NameValuePair(DataHash, GkeyId, TimeStamp, DataJosn);
                        PostData();
                        Log.e(type_0x20, "緊急求救x20");

                    break;

                case type_0x30:
                    /**
                     * 客製化功能 定位模式
                     * */

                        Loaddata(type_0x30);
                        if(get0x30 != null){
                            DataJosn = json.jsonformat(type_0x30,                  data_30.getCELL_ID(),     data_30.getLAC(),
                                    data_30.getMCC(),           data_30.getMNC(),         data_30.getRSSI(),
                                    data_30.getCREATE_DATE(),   data_30.getEMG_DATE(),    data_30.getGPS_ACCURACY(),
                                    data_30.getGPS_ADDRESS(),   data_30.getGPS_LAT(),     data_30.getGPS_LNG(),
                                    data_30.getGPS_STATUS(),    data_30.getWIFI_Channel(),data_30.getWIFI_MAC(),
                                    data_30.getWIFI_Signal_dB(),data_30.getCELL_ID(),     data_30.getLAC(),
                                    data_30.getMCC(),		   data_30.getMNC(),         data_30.getRSSI(), imei);

                            URL = HTTP+Check_IP()+SERVER_HOST_0x30;
                            TYPE = type_0x30;
                            DataHash = encrypt(Gkey, TimeStamp, DataJosn);
                            ValuePair =	fn.NameValuePair(DataHash, GkeyId, TimeStamp, DataJosn);
                            PostData();
                            Log.e(type_0x30, "緊急求救x30");
                        }else{
                            Log.e(type_0x30, "沒有SOS定位資料");
                        }

                    break;

//                case type_0x29:
//                    Loaddata(type_0x29);
//                    JsonFormat.JsonSize(get0x29.size());
//                    DataJosn = json.jsonformat(type_0x29,                  data_29.getCREATE_DATE(), data_29.getCELL_ID(),
//                            data_29.getLAC(),           data_29.getMCC(),         data_29.getMNC(),
//                            data_29.getRSSI(),          CreateDate,               data_29.getELECTRIC_FENCE_ITEMNO(),
//                            data_29.getGPS_ACCURACY(),  data_29.getGPS_ADDRESS(), data_29.getGPS_LAT(),
//                            data_29.getGPS_LNG(),       data_29.getGPS_STATUS(),  data_29.getGSensor_AVG(),
//                            data_29.getGSensor_MAX(),   data_29.getWIFI_Channel(),data_29.getWIFI_MAC(),
//                            data_29.getWIFI_Signal_dB(),data_29.getCELL_ID(),     data_29.getLAC(),
//                            data_29.getMCC(), 		   data_29.getMNC(), 		 data_29.getRSSI(),
//                            data_29.getPPM(), 		   data_29.getTEMP(), 		 data_29.getVOLTAGE(),
//                            data_29.getVOLTAGE_PERCENT(), "1", imei
//                    );
//                    URL = HTTP+Check_IP()+SERVER_HOST_0x29;
//                    TYPE = type_0x29;
//                    DataHash = encrypt(Gkey, TimeStamp, DataJosn);
//                    ValuePair =	fn.NameValuePair(DataHash, GkeyId, TimeStamp, DataJosn);
//                    PostData();
//                    Log.e(type_0x29, "圍籬訊息x29");
//                    break;
//
//                case type_0x3F:
//                    Loaddata(type_0x3F);
//                    DataJosn = json.jsonformat(type_0x3F, data_3F.getPHONE(), data_3F.getSTART_TIME(), data_3F.getEND_TIME(),
//                            data_3F.getCALL_TYPE(), data_3F.getCREATE_DATE(), imei);
//                    URL = HTTP+Check_IP()+SERVER_HOST_0x3F;
//                    TYPE = type_0x3F;
//                    DataHash = encrypt(Gkey, TimeStamp, DataJosn);
//                    ValuePair =	fn.NameValuePair(DataHash, GkeyId, TimeStamp, DataJosn);
//                    PostData();
//                    Log.e(type_0x3F, "電話紀錄x3F");
//                    break;
//
//                case type_0x01_insert:
//                    try {
//
//                        System.out.println("Mark 這邊有定位吧?");
//                        Loaddata(type_0x2A);
//                        get0x01_insert = dao.getdata_0x01_Insert(context, "limit 3");
//                        if(get0x01_insert != null){
//                            /**
//                             * 客製化功能 定位模式
//                             * */
//                            if (UserInfoFragment.verSion.equals("TD")) {
//                                StringBuilder httpParams = null;
//                                for(int i=0; i<get0x01_insert.size(); i++) {
//                                    data_01_Insert = get0x01_insert.get(i);
//                                    httpParams = new StringBuilder();
//                                    String separator = "\r\n";
//                                    httpParams.append("FunctionName=").append("AddNewData").append(separator);
//                                    httpParams.append("GatewayType=").append("9022").append(separator);
//                                    httpParams.append("GatewayID=").append("9022"+getImei()).append(separator);
//                                    httpParams.append("DeviceType=").append("9022").append(separator);
//                                    httpParams.append("DeviceID=").append("9022"+getImei()).append(separator);
//                                    httpParams.append("ExtensionID=").append("0").append(separator);
//                                    httpParams.append("Year=").append(ChangeDateFormat.Year()).append(separator);
//                                    httpParams.append("Month=").append(ChangeDateFormat.getMonth()).append(separator);
//                                    httpParams.append("Day=").append(ChangeDateFormat.getDay()).append(separator);
//                                    httpParams.append("Hour=").append(ChangeDateFormat.getHour()).append(separator);
//                                    httpParams.append("Minute=").append(ChangeDateFormat.getMinute()).append(separator);
//                                    httpParams.append("DataType=").append("43").append(separator);
//                                    httpParams.append("Value1=").append(data_01_Insert.getGPS_LAT()).append(separator);
//                                    httpParams.append("Value2=").append(data_01_Insert.getGPS_LNG()).append(separator);
//                                    httpParams.append("Value3=").append(data_01_Insert.getGPS_ACCURACY()).append(separator);
//                                    httpParams.append("Value4=").append("0").append(separator);
//                                    Log.e("FORA_TYPE_43", "43上傳資料");
//                                }
//                                new FORAHttpPostData(httpParams.toString());
//                            }
//                            else{
//                                System.out.println("Mark 定位了吧1?");
//                                for(int i=0; i<get0x01_insert.size(); i++){
//                                    data_01_Insert = get0x01_insert.get(i);
//                                    System.out.println(data_01_Insert.getITEMNO());
//                                    System.out.println("Mark 這是啥 " + data_01_Insert.getPR_DATE());
//                                    try {
//                                        JSONObject job = new JSONObject();
//                                        JSONObject job2 = null;
//                                        JSONObject job3 = null;
//                                        JSONObject job4 = null;
//                                        JSONObject job5 = null;
//                                        JSONArray ary = new JSONArray();
//                                        JSONArray ary2 = null;
//                                        JSONArray ary3 = null;
//                                        String[] detail1 = null;
//                                        String[] detail2 = null;
//                                        String[] detail3 = null;
//                                        String[] detail4 = null;
//                                        String[] detail5 = null;
//
//                                        detail1 = JsonFormat.split(data_01_Insert.getLAC());
//                                        detail2 = JsonFormat.split(data_01_Insert.getMNC());
//                                        detail3 = JsonFormat.split(data_01_Insert.getRSSI());
//                                        detail4 = JsonFormat.split(data_01_Insert.getCELL_ID());
//                                        detail5 = JsonFormat.split(data_01_Insert.getMCC());
//
//                                        for(int k = 0; k < get0x01_insert.size(); k++){
//                                            job2 = new JSONObject();
//                                            job4 = new JSONObject();
//                                            ary2 = new JSONArray();
//                                            ary3 = new JSONArray();
//                                            data_01_Insert = get0x01_insert.get(k);
//                                            job2.put("PR_DATE", data_01_Insert.getPR_DATE());
//                                            job2.put("VOLTAGE", data_01_Insert.getVOLTAGE());
//                                            job2.put("VOLTAGE_PERCENT", data_01_Insert.getVOLTAGE_PERCENT());
//
//                                            for(int j=0; j<detail1.length; j++){
//
//                                                job3 = new JSONObject();
//                                                detail1 = JsonFormat.split(data_01_Insert.getLAC());
//                                                String lac = detail1[j];
//                                                if(lac == null || lac.equals("")){
//                                                    lac = "0";
//                                                }
//                                                job3.put("LAC", lac);
//
//                                                detail2 = JsonFormat.split(data_01_Insert.getMNC());
//                                                String mnc = detail2[j];
//                                                if(mnc == null || mnc.equals("")){
//                                                    mnc = "0";
//                                                }
//                                                job3.put("MNC", mnc);
//
//                                                detail3 = JsonFormat.split(data_01_Insert.getRSSI());
//                                                String rssi = detail3[j];
//                                                if(rssi == null || rssi.equals("")){
//                                                    rssi = "0";
//                                                }
//                                                job3.put("RSSI", rssi);
//
//                                                detail4 = JsonFormat.split(data_01_Insert.getCELL_ID());
//                                                String cell_ids = detail4[j];
//                                                if(cell_ids == null || cell_ids.equals("")){
//                                                    cell_ids = "0";
//                                                }
//                                                job3.put("CELL_ID", cell_ids);
//
//                                                detail5 = JsonFormat.split(data_01_Insert.getMCC());
//                                                String mcc = detail5[j];
//                                                if(mcc == null || mcc.equals("")){
//                                                    mcc = "0";
//                                                }
//                                                job3.put("MCC", mcc);
//                                                ary2.put(job3);
//                                            }
//                                            job2.put("MUTLICELL", ary2);
//
//                                            job2.put("GSensor_AVG", data_01_Insert.getGSensor_AVG());
//                                            job2.put("GSensor_MAX", data_01_Insert.getGSensor_MAX());
//                                            job2.put("PPM", data_01_Insert.getPPM());
//
//                                            detail1 = JsonFormat.split(data_01_Insert.getLAC());
//                                            String lac = detail1[0];
//                                            if(lac == null || lac.equals("")){
//                                                lac = "0";
//                                            }
//                                            job4.put("LAC", lac);
//
//                                            detail2 = JsonFormat.split(data_01_Insert.getMNC());
//                                            String mnc = detail2[0];
//                                            if(mnc == null || mnc.equals("")){
//                                                mnc = "0";
//                                            }
//                                            job4.put("MNC", mnc);
//
//                                            detail3 = JsonFormat.split(data_01_Insert.getRSSI());
//                                            String rssi = detail3[0];
//                                            if(rssi == null || rssi.equals("")){
//                                                rssi = "0";
//                                            }
//                                            job4.put("RSSI", rssi);
//
//                                            detail4 = JsonFormat.split(data_01_Insert.getCELL_ID());
//                                            String cell_id = detail4[0];
//                                            if(cell_id == null || cell_id.equals("")){
//                                                cell_id = "0";
//                                            }
//                                            job4.put("CELL_ID", cell_id);
//
//                                            detail5 = JsonFormat.split(data_01_Insert.getMCC());
//                                            String mcc = detail5[0];
//                                            if(mcc == null || mcc.equals("")){
//                                                mcc = "0";
//                                            }
//                                            job4.put("MCC", mcc);
//
//                                            job2.put("CELLER", job4);
//                                            job2.put("GPS_STATUS", data_01_Insert.getGPS_STATUS());
//                                            job2.put("GPS_LAT", data_01_Insert.getGPS_LAT());
//                                            job2.put("GPS_LNG",  data_01_Insert.getGPS_LNG());
//                                            job2.put("GPS_ADDRESS", data_01_Insert.getGPS_ADDRESS());
//                                            job2.put("GPS_ACCURACY", data_01_Insert.getGPS_ACCURACY());
//
//                                            detail1 = null;
//                                            detail2 = null;
//                                            detail3 = null;
//
//                                            detail1 = JsonFormat.split(data_01_Insert.getWIFI_MAC());
//                                            detail2 = JsonFormat.split(data_01_Insert.getWIFI_Signal_dB());
//                                            detail3 = JsonFormat.split(data_01_Insert.getWIFI_Channel());
//                                            for(int j=0; j<detail1.length; j++){
//                                                if(j <= 9){
//                                                    job5 = new JSONObject();
//
//                                                    detail1 = JsonFormat.split(data_01_Insert.getWIFI_MAC());
//                                                    String wifi_mac = detail1[j];
//                                                    job5.put("WIFI_MAC", wifi_mac);
//
//                                                    detail2 = JsonFormat.split(data_01_Insert.getWIFI_Signal_dB());
//                                                    String wifi_dB = detail2[j];
//                                                    job5.put("WIFI_Signal_dB", wifi_dB);
//
//                                                    detail3 = JsonFormat.split(data_01_Insert.getWIFI_Channel());
//                                                    String wifi_channel = detail3[j];
//                                                    job5.put("WIFI_Channel", wifi_channel);
//                                                    ary3.put(job5);
//                                                }
//                                            }
//                                            job2.put("MULTIWIFI", ary3);
//                                            ary.put(job2);
//                                        }
//                                        job.put("PRS", ary);
//                                        job.put("PR_COUNT", get0x01_insert.size());
//                                        job.put("CREATE_DATE", CreateDate);
//                                        job.put("account", imei);
//
//                                        DataJosn = job.toString();
//                                        Log.e("job", DataJosn);
//                                        //								new Thread(){
//                                        //									public void run(){
//                                        //										Looper.prepare();
//                                        //										try {
//                                        //											JsonFormat.saveTxt("0x01", DataJosn.toString(), "0x01_"+Controller.CreateDate, ImgDownload.ALBUM_PATH_SD);
//                                        //										} catch (Exception e) {
//                                        //											e.printStackTrace();
//                                        //										}
//                                        //										Looper.loop();
//                                        //									}
//                                        //								}.start();
//
//                                    } catch (Exception e) {
//                                        e.printStackTrace();
//                                    }
//
//                                    URL = HTTP+Check_IP()+SERVER_HOST_0x01;
//                                    TYPE = type_0x01;
//                                    DataHash = encrypt(Gkey, TimeStamp, DataJosn);
//                                    ValuePair =	fn.NameValuePair(DataHash, GkeyId, TimeStamp, DataJosn);
//                                    Thread.sleep(500);
//                                }
//                                PostData();
//                                Log.e(type_0x01_insert, "0x01上傳資料");
//                            }
//
//                        }else{
//                            Log.e(type_0x01_insert, "0x01上傳完畢");
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    break;
//
//                case type_0x29_insert:
//                    try{
//                        get0x29_insert = dao.getdata_0x29_Insert(context, "limt0,3");
//                        if(get0x29_insert != null){
//                            JsonFormat.JsonSize(get0x29_insert.size());
//
//                            for(int i=0; i<get0x29_insert.size(); i++){
//                                data_29_Insert = get0x29_insert.get(i);
//                                Loaddata(type_0x2A);
//
//                                JSONObject job = new JSONObject();
//                                JSONObject job2 = null;
//                                JSONObject job3 = null;
//                                JSONObject job4 = null;
//                                JSONObject job5 = null;
//                                JSONArray ary = new JSONArray();
//                                JSONArray ary2 = null;
//                                JSONArray ary3 = null;
//                                String[] detail1 = null;
//                                String[] detail2 = null;
//                                String[] detail3 = null;
//                                String[] detail4 = null;
//                                String[] detail5 = null;
//
//                                try {
//                                    job.put("CREATE_DATE", data_29_Insert.getCREATE_DATE());
//
//                                    detail1 = JsonFormat.split(data_29_Insert.getMCC());
//                                    String mcc = detail1[0];
//                                    if(mcc == null || mcc.equals("")){
//                                        mcc = "0";
//                                    }
//
//                                    detail2 = JsonFormat.split(data_29_Insert.getMNC());
//                                    String mnc = detail2[0];
//                                    if(mnc == null || mnc.equals("")){
//                                        mnc = "0";
//                                    }
//
//                                    detail3 = JsonFormat.split(data_29_Insert.getLAC());
//                                    String lac = detail3[0];
//                                    if(lac == null || lac.equals("")){
//                                        lac = "0";
//                                    }
//
//                                    detail4 = JsonFormat.split(data_29_Insert.getCELL_ID());
//                                    String cell_id = detail4[0];
//                                    if(cell_id == null || cell_id.equals("")){
//                                        cell_id = "0";
//                                    }
//
//                                    detail5 = JsonFormat.split(data_29_Insert.getRSSI());
//                                    String rssi = detail5[0];
//                                    if(rssi == null || rssi.equals("")){
//                                        rssi = "0";
//                                    }
//
//                                    for(int k = 0; k < get0x29_insert.size(); k++){
//                                        data_29_Insert = get0x29_insert.get(k);
//                                        ary2 = new JSONArray();
//                                        ary3 = new JSONArray();
//                                        job2 = new JSONObject();
//                                        job3 = new JSONObject();
//
//                                        job3.put("CELL_ID", cell_id);
//                                        job3.put("LAC", lac);
//                                        job3.put("MCC", mcc);
//                                        job3.put("MNC", mnc);
//                                        job3.put("RSSI", rssi);
//                                        job2.put("CELLER", job3);
//
//                                        job2.put("EF_DATE", data_29_Insert.getEF_DATE());
//                                        job2.put("ELECTRIC_FENCE_ITEMNO", data_29_Insert.getELECTRIC_FENCE_ITEMNO());
//                                        job2.put("GPS_ACCURACY", data_29_Insert.getGPS_ACCURACY());
//                                        job2.put("GPS_ADDRESS", data_29_Insert.getGPS_ADDRESS());
//                                        job2.put("GPS_LAT", data_29_Insert.getGPS_LAT());
//                                        job2.put("GPS_LNG", data_29_Insert.getGPS_LNG());
//                                        job2.put("GPS_STATUS", data_29_Insert.getGPS_STATUS());
//                                        job2.put("GSensor_AVG", data_29_Insert.getGSensor_AVG());
//                                        job2.put("GSensor_MAX", data_29_Insert.getGSensor_MAX());
//
//                                        detail1 = null;
//                                        detail2 = null;
//                                        detail3 = null;
//
//                                        detail1 = JsonFormat.split(data_29_Insert.getWIFI_Channel());
//                                        detail2 = JsonFormat.split(data_29_Insert.getWIFI_MAC());
//                                        detail3 = JsonFormat.split(data_29_Insert.getWIFI_Signal_dB());
//
//                                        for(int j=0; j<detail1.length; j++){
//                                            if(j <= 9){
//                                                job4 = new JSONObject();
//                                                detail1 = JsonFormat.split(data_29_Insert.getWIFI_Channel());
//                                                String wifi_channel = detail1[j];
//                                                job4.put("WIFI_Channel", wifi_channel);
//
//                                                detail2 = JsonFormat.split(data_29_Insert.getWIFI_MAC());
//                                                String wifi_mac = detail2[j];
//                                                job4.put("WIFI_MAC", wifi_mac);
//
//                                                detail3 = JsonFormat.split(data_29_Insert.getWIFI_Signal_dB());
//                                                String wifi_dB = detail3[j];
//                                                job4.put("WIFI_Signal_dB", wifi_dB);
//                                                ary2.put(job4);
//                                            }
//                                        }
//                                        job2.put("MULTIWIFI", ary2);
//
//                                        detail1 = JsonFormat.split(data_29_Insert.getMCC());
//                                        detail2 = JsonFormat.split(data_29_Insert.getMNC());
//                                        detail3 = JsonFormat.split(data_29_Insert.getLAC());
//                                        detail4 = JsonFormat.split(data_29_Insert.getCELL_ID());
//                                        detail5 = JsonFormat.split(data_29_Insert.getRSSI());
//
//                                        for(int j=0; j<detail1.length; j++){
//
//                                            job5 = new JSONObject();
//
//                                            detail1 = JsonFormat.split(data_29_Insert.getMCC());
//                                            String mccs = detail1[j];
//                                            if(mccs == null || mccs.equals("")){
//                                                mccs = "0";
//                                            }
//                                            job5.put("MCC", mccs);
//
//                                            detail2 = JsonFormat.split(data_29_Insert.getMNC());
//                                            String mncs = detail2[j];
//                                            if(mncs == null || mncs.equals("")){
//                                                mncs = "0";
//                                            }
//                                            job5.put("MNC", mncs);
//
//                                            detail3 = JsonFormat.split(data_29_Insert.getLAC());
//                                            String lccs = detail3[j];
//                                            if(lccs == null || lccs.equals("")){
//                                                lccs = "0";
//                                            }
//                                            job5.put("LAC", lccs);
//
//                                            detail4 = JsonFormat.split(data_29_Insert.getCELL_ID());
//                                            String cell_ids = detail4[j];
//                                            if(cell_ids == null || cell_ids.equals("")){
//                                                cell_ids = "0";
//                                            }
//                                            job5.put("CELL_ID", cell_ids);
//
//                                            detail5 = JsonFormat.split(data_29_Insert.getRSSI());
//                                            String rssis = detail5[j];
//                                            if(rssis == null || rssis.equals("")){
//                                                rssis = "0";
//                                            }
//                                            job5.put("RSSI", rssis);
//                                            ary3.put(job5);
//                                        }
//                                        job2.put("MUTLICELL", ary3);
//
//                                        job2.put("PPM",  data_29_Insert.getPPM());
//                                        job2.put("TEMP", data_29_Insert.getTEMP());
//                                        job2.put("VOLTAGE", data_29_Insert.getVOLTAGE());
//                                        job2.put("VOLTAGE_PERCENT", data_29_Insert.getVOLTAGE_PERCENT());
//                                        ary.put(job2);
//                                    }
//
//                                    job.put("EFS", ary);
//                                    job.put("EF_COUNT", get0x29_insert.size());
//                                    job.put("account", imei);
//
//                                    DataJosn = job.toString();
//                                    Log.e("job", DataJosn);
//                                    //							new Thread(){
//                                    //								public void run(){
//                                    //									Looper.prepare();
//                                    //									try {
//                                    //										JsonFormat.saveTxt("0x29", DataJosn, "0x29_"+Controller.CreateDate, ImgDownload.ALBUM_PATH_SD);
//                                    //									} catch (Exception e) {
//                                    //										e.printStackTrace();
//                                    //									}
//                                    //									Looper.loop();
//                                    //								}
//                                    //							}.start();
//
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
//                                URL = HTTP+Check_IP()+SERVER_HOST_0x29;
//                                TYPE = type_0x29;
//                                DataHash = encrypt(Gkey, TimeStamp, DataJosn);
//                                ValuePair =	fn.NameValuePair(DataHash, GkeyId, TimeStamp, DataJosn);
//                                Thread.sleep(500);
//                            }
//                            PostData();
//                            Log.e(type_0x29_insert, "0x29上傳資料");
//                        }else{
//                            Log.e(type_0x29_insert, "0x29上傳完畢");
//                        }
//                    }catch(Exception e){
//                        e.printStackTrace();
//                    }
//                    break;
//
//                case type_800721:
//                    DataJosn = json.jsonformat(type_800721, imei);
//                    URL = HTTP+Check_IP()+SERVER_HOST_800721;
//                    TYPE = type_800721;
//
//                    DataHash = encrypt(Gkey, TimeStamp, DataJosn);
//                    ValuePair =	fn.NameValuePair(DataHash, GkeyId, TimeStamp, DataJosn);
//                    PostData();
//                    Log.e(type_800721, "取得動作設定_800721");
//                    break;
//
//                case type_801002:
//                    if(Settings_type[1].equals("4")){
//                        Settings = SharedPreferences_status.getHardwareSetting(MyApplication.context).split(";");
//                        System_Settings_Item = Settings[0];
//                        System_Settings_Value = Settings[1];
//                    } else if(Settings_type[1].equals("7")){
//                        System_Settings_Item = Settings_type[1];
//                        System_Settings_Value = Settings_type[2];
//                    }
//
//                    DataJosn = json.jsonformat(type_801002, imei, System_Settings_Item, System_Settings_Value);
//                    URL = HTTP+Check_IP()+SERVER_HOST_801002;
//                    TYPE = type_801002;
//
//                    DataHash = encrypt(Gkey, TimeStamp, DataJosn);
//                    ValuePair =	fn.NameValuePair(DataHash, GkeyId, TimeStamp, DataJosn);
//                    PostData();
//                    Log.e(type_801002, "儲存硬體設定_801002");
//                    break;
//
//                case type_800908:
//                    if(Settings_type[1].equals("8")){
//                        Settings = SharedPreferences_status.Get_Whitelist(MyApplication.context).split(";");
//                        System_Settings_Item = Settings[0];
//                        System_Settings_Value = Settings[1];
//                    }
//
//                    DataJosn = json.jsonformat(type_800908, imei, System_Settings_Value);
//                    URL = HTTP+Check_IP()+SERVER_HOST_800908;
//                    TYPE = type_800908;
//
//                    DataHash = encrypt(Gkey, TimeStamp, DataJosn);
//                    ValuePair =	fn.NameValuePair(DataHash, GkeyId, TimeStamp, DataJosn);
//                    PostData();
//                    Log.e(type_800908, "儲存白名單設定_800908");
//                    break;
//
//                case type_801610:
//                    getdb= new Get_DB(context, "");
//                    volist = getdb.getSettings();
//                    for(int i=0; i< volist.size(); i++){
//                        vo = volist.get(i);
//                        getStep = vo.getStepRange();
//                        getWeight = vo.getWeightRange();
//                        getgoal = vo.getStepCount();
//                    }
//
//                    DataJosn = json.jsonformat(type_801610, imei, getWeight, getStep, getgoal,
//                            SharedPreferences_status.GetClouds_status(context));
//                    URL = HTTP+Check_IP()+SERVER_HOST_801610;
//                    TYPE = type_801610;
//
//                    DataHash = encrypt(Gkey, TimeStamp, DataJosn);
//                    ValuePair =	fn.NameValuePair(DataHash, GkeyId, TimeStamp, DataJosn);
//                    PostData();
//                    Log.e(type_801610, "取得健步器設定_801610");
//                    break;
//
//                case type_801611:
//                    getdb= new Get_DB(context, "");
//                    volist = getdb.getSettings();
//                    for(int i=0; i< volist.size(); i++){
//                        vo = volist.get(i);
//                        getStep = vo.getStepRange();
//                        getWeight = vo.getWeightRange();
//                        getgoal = vo.getStepCount();
//                    }
//                    DataJosn = json.jsonformat(type_801611, imei, getWeight, getStep, getgoal,
//                            SharedPreferences_status.GetClouds_status(context));
//                    URL = HTTP+Check_IP()+SERVER_HOST_801611;
//                    TYPE = type_801611;
//
//                    DataHash = encrypt(Gkey, TimeStamp, DataJosn);
//                    ValuePair =	fn.NameValuePair(DataHash, GkeyId, TimeStamp, DataJosn);
//                    PostData();
//                    Log.e(type_801611, "儲存健步器設定_801611");
//                    break;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void PostData(){
        new Thread(){
            public void run(){
                try{
//	    			Log.d("檢查驗證碼", signature);
//    				ProgressLodingSetting();
                    new Http_PostData(URL, TYPE, ValuePair);
                    ValuePair = null;
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }.start();
    }

//    public void delete_0x01(){
//        if(get0x01_insert != null) {
//            for(int i=0; i<get0x01_insert.size(); i++){
//                data_01_Insert = get0x01_insert.get(i);
//                delete.Delete_0x01(context, data_01_Insert.getITEMNO());
//            }
//            get0x01_insert.clear();
//
//            /**
//             *@date 2015-09-22
//             */
//            ChildThread mhandler = new ChildThread(3000, 1000);
//
//            mhandler.setOnHandlerListeners(new OnHandlerListeners() {
//
//                @Override
//                public void onHandlerParam() {
//                    MyApplication.cro.LoData(MyApplication.context, Controller.type_0x01_insert);
//                }
//            });
//        }
//    }

//    public void delete_0x29(){
//        for(int i=0; i<get0x29_insert.size(); i++){
//            data_29_Insert = get0x29_insert.get(i);
//            delete.Delete_0x29(context, data_29_Insert.getITEMNO());
//        }
//        get0x29_insert.clear();
//
//        /**
//         *@date 2015-09-22
//         */
//        ChildThread mhandler = new ChildThread(3000, 1000);
//
//        mhandler.setOnHandlerListeners(new OnHandlerListeners() {
//
//            @Override
//            public void onHandlerParam() {
//                MyApplication.cro.LoData(MyApplication.context, Controller.type_0x29_insert);
//            }
//        });
//    }

    public void delete_0x30(){
        try{
            data_30 = get0x30.get(0);
            delete.Delete_0x30(context, data_30.getITEMNO());

            ChildThread mhandler = new ChildThread(3000, 1000);
            mhandler.setOnHandlerListeners(new ChildThread.OnHandlerListeners() {

                @Override
                public void onHandlerParam() {
                    get0x30 = dao.getdata_0x30(context);
                    if(get0x30 != null){
                        get0x30.clear();
                        MyApplication.cro.LoData(MyApplication.context, Controller.type_0x30);
                    }else{
                        Log.e("delete_0x30", "沒有SOS定位資料");
                    }
                }
            });
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public final static String HTTP = "http://";
    public static String IP = "210.242.50.125:8000";
    //Gkey
    public final static String SERVER_HOST_0x2A = "/APP/GcareWatchLogin.html";
    public final static String SERVER_HOST_0x2B = "/APP/GcarePushAbnormaAlarmData.html";
    public final static String SERVER_HOST_0x2E = "/APP/GcareUploadDeviceBootData.html";
    public final static String SERVER_HOST_0x2F = "/APP/GcareUploadDeviceShutdownData.html";
    public final static String SERVER_HOST_0x20 = "/APP/GcareUploadSosData.html";
    public final static String SERVER_HOST_0x30 = "/APP/GcareUploadSosWithGpsData.html";
    public final static String SERVER_HOST_0x28 = "/APP/GcareUploadInstantPositionData.html";
    public final static String SERVER_HOST_0x29 = "/APP/GcareUploadGeofenceData.html";
    public final static String SERVER_HOST_0x3F = "/APP/GcareUploadCallRecordsData.html";
    public final static String SERVER_HOST_0x01 = "/APP/GcareUploadActiveRegionData.html";
    public final static String SERVER_HOST_0x10 = "/APP/GcareGetSystemSettingData.html";
    public final static String SERVER_HOST_0x11 = "/APP/GcareGetPersonalSettingData.html";
    public final static String SERVER_HOST_0x18 = "/APP/GcareUploadSosTrackingData.html";
    public final static String SERVER_HOST_0x7F = "/APP/GcareGetEchoUpdateListData.html";
    public final static String SERVER_HOST_0x40 = "http://equ.guidercare.com:7000/GEquipment/APP/GcareGetDNSandIpListData.html";
    public final static String SERVER_HOST_0x4B = "/APP/GcareGetFamilyPhotoListData.html";
    public final static String SERVER_HOST_0x4C = "/APP/GcareDisplayPhotoListData.html";
    public final static String SERVER_HOST_0x4D = "/APP/GcareGetPhoneBookData.html";
    //取得無動作設定
    public final static String SERVER_HOST_800721 = "/APP/GcareGetSettingNonmovement.html";
    //儲存硬體設定
    public final static String SERVER_HOST_801002 ="/APP/GcareSetSettingHardware.html";
    public final static String SERVER_HOST_800908 ="/APP/GcareEditWhiteStatus.html";
    //取得儲存健步設定
    public final static String SERVER_HOST_801611 ="/APP/GcareCustSetActivityWalkSetting.html";
    public final static String SERVER_HOST_801610 ="/APP/GcareCustGetActivityWalkSetting.html";

    @SuppressWarnings({ "unused" })
    public static String Check_IP(){
        DAO dao = new DAO();
//        ArrayList<VO_0x40> get0x40 = dao.getdata_0x40(MyApplication.context);
		/*
		for(int i=0; i<get0x40.size(); i++){
			data_40 = get0x40.get(i);
			try {
				String ip = "210.242.50.125:8000/mcarewatch";
				String ip_port = data_40.getSERVER_IP_PORT().trim();
				String server_dns = data_40.getSERVER_DNS().trim();
				JSONArray ip_array = new JSONArray(ip_port);
				Log.e("server_dns", "server_dns:"+server_dns);

				for(int j=0; j<ip_array.length(); j++){
					ip = ip_array.getJSONObject(j).getString("SERVER_IP_PORT");
//					Log.e("ip_port_length", ip_array.length()+"");
//					Log.e("ip_port", ip);
					if(server_dns != null && !(server_dns.trim().equals(""))){
						Log.e("SERVER_DNS", server_dns+"");
						return IP = server_dns;
					}else{
						if(ip != null && !(ip.trim().equals(""))){
							Log.e("SERVER_IP_PORT", ip);
							return IP = ip;
						}else{
							Log.e("無IP", "No IP Address");
							return IP = "210.242.50.125:8000/safetywatch";
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		*/
        //ap1.guidercare.com:8080/angelcare
        //http://13.228.173.84:8080/angelcare/
        return "13.229.65.12:8080/angelcare";
    }

    private void Loaddata(String type){
        ESettings_type mSetting_type = ESettings_type.ByStr(type);
        switch(mSetting_type){
//            case type_0x2A:
//                try{
//                    ArrayList<VO_0x2A> get0x2A = dao.getdata_0x2A(context);
//                    for(int i=0; i<get0x2A.size(); i++){
//                        data_2A = get0x2A.get(i);
//                    }
//                }catch(Exception e){
//                    e.printStackTrace();
//                }
//                break;
//
//            case type_0x2B:
//                try{
//                    ArrayList<VO_0x2B> get0x2B = dao.getdata_0x2B(context);
//                    for(int i=0; i<get0x2B.size(); i++){
//                        data_2B = get0x2B.get(i);
//                    }
//                }catch(Exception e){
//                    e.printStackTrace();
//                }
//                break;
//
//            case type_0x2E:
//                try{
//                    ArrayList<VO_0x2E> get0x2E = dao.getdata_0x2E(context);
//                    for(int i=0; i<get0x2E.size(); i++){
//                        data_2E = get0x2E.get(i);
//                    }
//                }catch(Exception e){
//                    e.printStackTrace();
//                }
//                break;
//
//            case type_0x2F:
//                try{
//                    ArrayList<VO_0x2F> get0x2F = dao.getdata_0x2F(context);
//                    for(int i=0; i<get0x2F.size(); i++){
//                        data_2F = get0x2F.get(i);
//                    }
//                }catch(Exception e){
//                    e.printStackTrace();
//                }
//                break;
//
//            case type_0x01:
//                try{
//                    get0x01 = dao.getdata_0x01(context);
//                    for(int i=0; i<get0x01.size(); i++){
//                        data_01 = get0x01.get(i);
//                    }
//                }catch(Exception e){
//                    e.printStackTrace();
//                }
//                break;
//
//            case type_0x10:
//                try{
//                    ArrayList<VO_0x10> get0x10 = dao.getdata_0x10(context);
//                    for(int i=0; i<get0x10.size(); i++){
//                        data_10 = get0x10.get(i);
//                    }
//                }catch(Exception e){
//                    e.printStackTrace();
//                }
//                break;
//
//            case type_0x11:
//                try{
//                    ArrayList<VO_0x11> get0x11 = dao.getdata_0x11(context);
//                    for(int i=0; i<get0x11.size(); i++){
//                        data_11 = get0x11.get(i);
//                    }
//                }catch(Exception e){
//                    e.printStackTrace();
//                }
//                break;
//
//            case type_0x18:
//                try{
//                    ArrayList<VO_0x18> get0x18 = dao.getData_0x18(context);
//                    for(int i=0; i<get0x18.size(); i++){
//                        data_18 = get0x18.get(i);
//                    }
//                }catch(Exception e){
//                    e.printStackTrace();
//                }
//                break;
//
//            case type_0x40:
//                try{
//                    ArrayList<VO_0x40> get0x40 = dao.getdata_0x40(context);
//                    for(int i=0; i<get0x40.size(); i++){
//                        data_40 = get0x40.get(i);
//                    }
//                }catch(Exception e){
//                    e.printStackTrace();
//                }
//                break;
//
//            case type_0x4B:
//                try{
//                    ArrayList<VO_0x4B> get0x4B = dao.getdata_0x4B(context);
//                    for(int i=0; i<get0x4B.size(); i++){
//                        data_4B = get0x4B.get(i);
//                    }
//                }catch(Exception e){
//                    e.printStackTrace();
//                }
//                break;
//
//            case type_0x4C:
//                try{
//                    ArrayList<VO_0x4C> get0x4C = dao.getdata_0x4C(context);
//                    for(int i=0; i<get0x4C.size(); i++){
//                        data_4C = get0x4C.get(i);
//                    }
//                }catch(Exception e){
//                    e.printStackTrace();
//                }
//                break;
//
//            case type_0x4D:
//                try{
//                    ArrayList<VO_0x4D> get0x4D = dao.getdata_0x4D(context);
//                    for(int i=0; i<get0x4D.size(); i++){
//                        data_4D = get0x4D.get(i);
//                    }
//                }catch(Exception e){
//                    e.printStackTrace();
//                }
//                break;
//
//            case type_0x28:
//                try{
//                    ArrayList<VO_0x28> get0x28 = dao.getdata_0x28(context);
//                    for(int i=0; i<get0x28.size(); i++){
//                        data_28 = get0x28.get(i);
//                    }
//                }catch(Exception e){
//                    e.printStackTrace();
//                }
//                break;

            case type_0x20:
                try{
                    ArrayList<VO_0x20> get0x20 = dao.getdata_0x20(context);
                    for(int i=0; i<get0x20.size(); i++){
                        data_20 = get0x20.get(i);
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
                break;

            case type_0x30:
                try{
                    get0x30 = dao.getdata_0x30(context);
                    if(get0x30 != null){
                        for(int i=0; i<get0x30.size(); i++){
                            data_30 = get0x30.get(i);
                        }
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
                break;

//            case type_0x29:
//                try{
//                    get0x29 = dao.getdata_0x29(context);
//                    for(int i=0; i<get0x29.size(); i++){
//                        data_29 = get0x29.get(i);
//                    }
//                }catch(Exception e){
//                    e.printStackTrace();
//                }
//                break;
//
//            case type_0x3F:
//                try{
//                    ArrayList<VO_0x3F> get0x3F = dao.getdata_0x3F(context);
//                    for(int i=0; i<get0x3F.size(); i++){
//                        data_3F = get0x3F.get(i);
//                    }
//                }catch(Exception e){
//                    e.printStackTrace();
//                }
//                break;
//
//            case type_0x7F:
//                ArrayList<VO_0x7F> get0x7F = dao.getdata_0x7F(context);
//                for(int i=0; i<get0x7F.size(); i++){
//                    data_7F = get0x7F.get(i);
//                }
//                break;
//
//            case type_0x01_insert:
//                try{
//                    get0x01_insert = dao.getdata_0x01_Insert(context, "limt0,3");
//                    for(int i=0; i<get0x01_insert.size(); i++){
//                        data_01_Insert = get0x01_insert.get(i);
//                    }
//                }catch(Exception e){
//                    e.printStackTrace();
//                }
//                break;
//
//            case type_0x29_insert:
//                try{
//                    get0x29_insert = dao.getdata_0x29_Insert(context, "limt0,3");
//                    for(int i=0; i<get0x29_insert.size(); i++){
//                        data_29_Insert = get0x29_insert.get(i);
//                    }
//                }catch(Exception e){
//                    e.printStackTrace();
//                }
//                break;
        }
    }

    /**
     *@date 2015-09-22
     */
//    public static void ProgressLodingSetting_Cancel(String type){
//        if(Controller.type_801002.equals(type) | Controller.type_0x11.equals(type)){
//            try{
//                Message m = new Message();
//                m.obj = "Progress";
//                BaseActivity.mdHandler.sendMessage(m);
//            }catch(Exception e){
//                e.printStackTrace();
//            }
//        }
//    }

    public static String encrypt(String Gkey, String timeStamp, String DataJson){
//		String timeStamp = getTimesgetTimes(); //加密還不需%20取代空格
        String dataStructure = Gkey + timeStamp + DataJson;
//    	Log.e("dataStructure", dataStructure);
        MessageDigest shaCode = null;
        try {
            shaCode = MessageDigest.getInstance("SHA-256");
            shaCode.update(dataStructure.getBytes("UTF-8"));
            //System.out.println("dataStructure="+dataStructure);
        }
        catch(Exception e) {
            e.printStackTrace();
            return "";
        }
        return byte2Hex(shaCode.digest());
    }

    @SuppressLint("DefaultLocale")
    private static String byte2Hex(byte[] data) {
        String hexString = "";
        String stmp = "";

        for(int i = 0; i < data.length; i++) {
            stmp = Integer.toHexString(data[i] & 0XFF);

            if(stmp.length() == 1) {
                hexString = hexString + "0" + stmp;
            }
            else {
                hexString = hexString + stmp;
            }
        }
        return hexString.toUpperCase();
    }

    private String getImei() {
        @SuppressWarnings("static-access")
        TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(context.TELEPHONY_SERVICE);
        String imei = telephonyManager.getDeviceId().substring(3, telephonyManager.getDeviceId().length());
        Log.e("imei", imei);
        return imei;
    }

    public enum ESettings_type {
        type_0x01("0x01"),
        type_0x10 ("0x10"),
        type_0x11 ("0x11"),
        type_0x18 ("0x18"),
        type_0x20 ("0x20"),
        type_0x28 ("0x28"),
        type_0x29 ("0x29"),
        type_0x2A ("0x2A"),
        type_0x2B ("0x2B"),
        type_0x2E ("0x2E"),
        type_0x2F ("0x2F"),
        type_0x30 ("0x30"),
        type_0x3F ("0x3F"),
        type_0x40 ("0x40"),
        type_0x4B ("0x4B"),
        type_0x4C ("0x4C"),
        type_0x4D ("0x4D"),
        type_0x4W ("0x4W"),
        type_0x7F ("0x7F"),
        type_0x01_insert ("0x01_insert"),
        type_0x29_insert ("0x29_insert"),
        type_800721 ("800721"),
        type_801002 ("801002"),
        type_800908 ("800908"),
        type_801610 ("801610"),
        type_801611 ("801611"),
        type_err ("");

        private String type;
        ESettings_type(String str){
            this.type = str;
        }
        public static ESettings_type ByStr(final String val)
        {
            for (ESettings_type type : ESettings_type.values())
            {
                if (type.type.equals(val))
                {
                    return type;
                }
            }
            return type_err; //Never return null
        }
    }

}

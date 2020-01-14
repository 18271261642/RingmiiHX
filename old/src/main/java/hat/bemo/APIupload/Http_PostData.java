package hat.bemo.APIupload;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import hat.bemo.MyApplication;
import hat.bemo.setting.EnumSettings;

/**
 * Created by apple on 2017/11/10.
 */

public class Http_PostData {

    private static final int REQUEST_TIMEOUT = 20*1000;//设置请求超时10秒钟
    private static final int SO_TIMEOUT = 20*1000;  //设置等待数据超时时间10秒钟
    private List<NameValuePair> reqParam;
    private String URL;
    private Connection connection;
    private String type;
    private static String Status="";
    private Intent intent;
    private Bundle bundle;
    public static ProgressDialog progressDialog;
    public static Handler Urgent_0x29;
    public static Handler Frequency_x01;
    public static Handler handler;
    public static Handler mhandler1;
    public static Handler mhandler2;
    public static Handler mhandler3;
    public static Handler mhandler4;
    public static Handler mhandler5;
    //語系切換參數
    private String[] mLangArray = {"zh", "zh", "en"};
    private String[] mLocateArray = {"TW", "CN", "US"};
    private String language_type = "0";
    private int checkedItem;

    public Http_PostData(String URL, String type, List<NameValuePair> reqParam){
        this.URL = URL;
        this.reqParam = reqParam;
        this.type = type;
        connection = new Connection();
        connection.execute();
    }

    class Connection extends AsyncTask<String, String, List<String>> {

        @Override
        protected List<String> doInBackground(String... params) {
            try {
                BasicHttpParams httpParams = new BasicHttpParams();
                //連線逾時
                HttpConnectionParams.setConnectionTimeout(httpParams, REQUEST_TIMEOUT);
                HttpConnectionParams.setSoTimeout(httpParams, SO_TIMEOUT);
                HttpClient client = new DefaultHttpClient(httpParams);
                HttpPost post = new HttpPost(URL);
                try {
                    post.setEntity(new UrlEncodedFormEntity(reqParam, HTTP.UTF_8));
                    Log.e("reqParam", reqParam.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String entity = "";

                HttpResponse response = client.execute(post);
                int statusCode = response.getStatusLine().getStatusCode();
                Log.i("statusCode", statusCode+"");
                if (statusCode == 200) {
                    HttpEntity responseEntity = response.getEntity();
                    entity = EntityUtils.toString(responseEntity);
                }

                try {
                    jsonObject = new JSONObject();
                    jsonArray = new JSONArray(entity);
                    jsonObject = jsonArray.getJSONObject(0);
                    Data =  jsonObject.getString("msg");
                    Log.i("TYPE", Data);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.i("entity", "entity:"+entity);
                Update_data(Data, entity);
                System.gc();
            } catch (Exception e) {
//                DataSync.DataSync_Connectiontimeout();
//                Controller.ProgressLodingSetting_Cancel(Controller.type_0x11);
//				SocketException(type);
//				JsonFormat.saveTxt("", getErrorInfoFromException(e), Controller.CreateDate, ImgDownload.ALBUM_PATH_SD);
                e.printStackTrace();
            }
            return null;
        }
    }

//	private void SocketException(String type){
//		try{
//			Thread.sleep(1000);
////			switch(type){
////			case Controller.type_0x2A:
////				MyApplication.cro.LoData(MyApplication.context, Controller.type_0x2A);
////				break;
////			case Controller.type_0x10:
////				MyApplication.cro.LoData(MyApplication.context, Controller.type_0x10);
////				break;
////			case Controller.type_0x11:
////				MyApplication.cro.LoData(MyApplication.context, Controller.type_0x11);
////				break;
////			case Controller.type_0x01:
//////				Frequency_x01.sendMessage(Frequency_x01.obtainMessage());
//////				new Frequency_x01("").Urgent_0x01();
////				Log.e("網路不穩定", "=================0x01=================");
//////				MyApplication.cro.LoData(MyApplication.context, Controller.type_0x01);
////				break;
////			case Controller.type_0x20:
////				MyApplication.cro.LoData(MyApplication.context, Controller.type_0x20);
////				break;
////			case Controller.type_0x28:
////				MyApplication.cro.LoData(MyApplication.context, Controller.type_0x28);
////				break;
////			case Controller.type_0x29:
//////				Urgent_0x29.sendMessage(Urgent_0x29.obtainMessage());
//////				new FenceMessage_x29("").Urgent_0x29();
////				Log.e("網路不穩定", "=================0x29=================");
//////				MyApplication.cro.LoData(MyApplication.context, Controller.type_0x29);
////				break;
////			case Controller.type_0x2B:
////				MyApplication.cro.LoData(MyApplication.context, Controller.type_0x2B);
////				break;
////			case Controller.type_0x2E:
////				MyApplication.cro.LoData(MyApplication.context, Controller.type_0x2E);
////				break;
////			case Controller.type_0x30:
////				MyApplication.cro.LoData(MyApplication.context, Controller.type_0x30);
////				break;
////			case Controller.type_0x3F:
////				MyApplication.cro.LoData(MyApplication.context, Controller.type_0x3F);
////				break;
////			case Controller.type_0x40:
////				MyApplication.cro.LoData(MyApplication.context, Controller.type_0x40);
////				break;
////			case Controller.type_0x4B:
////				MyApplication.cro.LoData(MyApplication.context, Controller.type_0x4B);
////				break;
////			case Controller.type_0x4C:
////				MyApplication.cro.LoData(MyApplication.context, Controller.type_0x4C);
////				break;
////			case Controller.type_0x4D:
////				MyApplication.cro.LoData(MyApplication.context, Controller.type_0x4D);
////				break;
////			case Controller.type_0x4W:
////				MyApplication.cro.LoData(MyApplication.context, Controller.type_0x4W);
////				break;
////			case Controller.type_0x7F:
////				MyApplication.cro.LoData(MyApplication.context, Controller.type_0x7F);
////				break;
////			}
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//	}

    private JSONObject jsonObject;
    private JSONArray jsonArray;
    private String Data;
    private int count = 0;
    private void Update_data(String Type, String entity){

        Update up = new Update();
        String[] ITEMNO = {"1"};

        EnumSettings.Http_PostData_Enum Etype = EnumSettings.Http_PostData_Enum.ByStr(Type);

        switch(Etype){

//            case type80010100:
//                Log.e(Controller.type_0x2A, "封包:0x2A完成!!!!!!!!!!");
//                MyApplication.cro.LoData(MyApplication.context, Controller.type_0x2E);
//                break;
//
//            case type80060100:
//                Log.e(Controller.type_0x2B, "封包:0x2B完成!!!!!!!!!!");
//                break;
//
//            case type80150200:
//                Log.e(Controller.type_0x2E, "封包:0x2E完成!!!!!!!!!!");
//                /**
//                 *@date 2015-09-22
//                 */
//                ChildThread mhandler = new ChildThread(45000, 1000);
//
//                mhandler.setOnHandlerListeners(new OnHandlerListeners() {
//
//                    @Override
//                    public void onHandlerParam() {
//                        MyApplication.cro.LoData(MyApplication.context, Controller.type_0x01_insert);
////						MyApplication.cro.LoData(MyApplication.context, Controller.type_0x01);
//                    }
//                });
//                break;
//
//            case type80150300:
//                Log.e(Controller.type_0x2F, "封包:0x2F完成!!!!!!!!!!");
//                break;
//
//            case type80130700:
//                Log.e(Controller.type_0x01, "封包:0x01完成!!!!!!!!!!");
//                try {
//                    MyApplication.cro.delete_0x01();
//
//                    up.up_0x2A(MyApplication.getAppContext(), ITEMNO, "", "", ChangeDateFormat.CreateDate());
//                    SharedPreferences_status.SetSyncTime(MyApplication.context, ChangeDateFormat.CreateDate());
//                    Receiver();
//                    Statuscancel();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                MyApplication.cro.LoData(MyApplication.context, Controller.type_0x7F);
//                break;
//
//            case type80090200:
//                try {
//                    JSONObject echo_objt;
//                    jsonArray = new JSONArray(entity);
//                    echo_objt = jsonArray.getJSONObject(0);
//                    String echos = echo_objt.getString("ECHOS");
//                    Log.d("ECHOS", echos);
//                    jsonArray = new JSONArray(echos);
//                    for(int i = 0; i<jsonArray.length(); i++){
//                        String echo = jsonArray.getJSONObject(i).getString("ECHO_TYPE");
//                        Log.e("更新項目", "更新項目:"+echo);
//
//                        EnumSettings.Http_PostData_Enum echotype = EnumSettings.Http_PostData_Enum.ByStr(echo);
//
//                        //更新項目
//                        switch(echotype){
//                            case type10:
//                                MyApplication.cro.LoData(MyApplication.context, Controller.type_0x10);
//                                break;
//                            case type11:
//                                MyApplication.cro.LoData(MyApplication.context, Controller.type_0x11);
//                                break;
//                            case type4B:
//                                MyApplication.cro.LoData(MyApplication.context, Controller.type_0x4B);
//                                break;
//                            case type4C:
//                                MyApplication.cro.LoData(MyApplication.context, Controller.type_0x4C);
//                                break;
//                            case type4D:
//                                MyApplication.cro.LoData(MyApplication.context, Controller.type_0x4D);
//                                break;
//                        }
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                DataSync.DataSync_success();
//                Controller.ProgressLodingSetting_Cancel(Controller.type_0x11);
//                Log.e(Controller.type_0x7F, "封包:0x7F完成!!!!!!!!!!");
//                MyApplication.cro.LoData(MyApplication.context, Controller.type_0x40);
//                MyApplication.cro.LoData(MyApplication.context, Controller.type_801610);
//
//                ChildThread mhandler2 = new ChildThread(2000, 1000);
//
//                mhandler2.setOnHandlerListeners(new OnHandlerListeners() {
//
//                    @Override
//                    public void onHandlerParam() {
//                        MyApplication.cro.LoData(MyApplication.context, Controller.type_800721);
//                    }
//                });
//                break;
//
//            case type80110100:
//                try {
//                    jsonArray = new JSONArray(entity);
//                    jsonObject = new JSONObject();
//                    for(int i = 0; i<jsonArray.length(); i++){
//                        jsonObject = jsonArray.getJSONObject(i);
//                    }
//                    String AUTOREAD = jsonObject.getString("AUTOREAD");
//                    String CHECKSUM = jsonObject.getString("CHECKSUM");
//                    String DIALOUT_LIMIT_STARTDAY = jsonObject.getString("DIALOUT_LIMIT_STARTDAY");
//                    String ECHO_GPS_T = jsonObject.getString("ECHO_GPS_T");
//                    String ECHO_PR_T = jsonObject.getString("ECHO_PR_T");
//                    String PHONE_LIMIT_BY_CUSTOMER = jsonObject.getString("PHONE_LIMIT_BY_CUSTOMER");
//                    String PHONE_LIMIT_MODE = jsonObject.getString("PHONE_LIMIT_MODE");
//                    String PHONE_LIMIT_ONOFF = jsonObject.getString("PHONE_LIMIT_ONOFF");
//                    String PHONE_LIMIT_TIME = jsonObject.getString("PHONE_LIMIT_TIME");
//                    String POWERON_LOGO = jsonObject.getString("POWERON_LOGO");
//                    String SERVER_DNS = jsonObject.getString("SERVER_DNS");
//                    String SERVER_IP_PORT = jsonObject.getString("SERVER_IP_PORT");
//
//                    //Mark 定位時間修改
//
//                    if(ECHO_GPS_T.equals("0")){
//                        String gps_status = SharedPreferences_status.getGps_status(MyApplication.context);
//                        if(gps_status.equals("0x00") || gps_status.trim().equals("") || gps_status == null){
//                            GpsController.setGpsStatus(MyApplication.context, false);
//                        }
//                    }
//                    System.out.println("Mark 抓定位隔多久時間的值 = " + ECHO_PR_T);
//                    if(ECHO_PR_T.equals("1")){
//                        System.out.println("Mark 抓定位0");
//                        //MyApplication.loctime(0);
//                    }
//                    else{
//                        System.out.println("Mark 抓定位1");
//                        //MyApplication.loctime(1);
//                    }
//
//                    up.up_0x10(MyApplication.context, ITEMNO, AUTOREAD, CHECKSUM, DIALOUT_LIMIT_STARTDAY, ECHO_GPS_T, ECHO_PR_T,
//                            PHONE_LIMIT_BY_CUSTOMER, PHONE_LIMIT_MODE, PHONE_LIMIT_ONOFF, PHONE_LIMIT_TIME, POWERON_LOGO,
//                            SERVER_DNS, SERVER_IP_PORT);
//
//                    String gps_status = SharedPreferences_status.getGps_status(MyApplication.context);
//                    if(gps_status.equals("0x00") || gps_status.trim().equals("") || gps_status == null){
//                        if(ECHO_GPS_T.equals("0")){
//                            if("SOS".equals(Emergency_0x20_0x30.GPStype)){
//                                Log.e("0x29_3", "求救狀態暫時不關GPS");
//                            }else{
//                                Log.e("0x10", "GPS_false");
//                                new GPSManager("3", "3");
//                            }
//                        }else{
//                            Log.e("0x10", "GPS_true");
//                            new GPSManager("5", "5");
//                        }
//                    }
////					 MainFragment.Scheduling.sendMessage(MainFragment.Scheduling.obtainMessage());
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
////				new Frequency_x01();
//                Log.e(Controller.type_0x10, "封包:0x10完成!!!!!!!!!!");
//
////				new SendFrequence_x01("");
//
////				AlarmManager alarmManager1 = (AlarmManager) MyApplication.context.getSystemService(Context.ALARM_SERVICE);
////				Intent intent1 = new Intent(BaseActivity.context, Frequency_x01_AlarmManager.class);
////				PendingIntent pendingIntent1 = PendingIntent.getBroadcast(MyApplication.context, 0,intent1, 0);
////				if(alarmManager1 != null) alarmManager1.cancel(pendingIntent1);
////				long triggerAtTime1 = new Date().getTime() +1000;
////				alarmManager1.set(AlarmManager.RTC_WAKEUP, triggerAtTime1, pendingIntent1);
//                break;
//
//            case type80100500:
//                try {
//                    String WHITE_PHONE_ITEMNO;
//                    String WHITE_PHONE_PHONE;
//                    jsonArray = new JSONArray(entity);
//                    jsonObject = new JSONObject();
//                    for(int i = 0; i<jsonArray.length(); i++){
//                        jsonObject =  jsonArray.getJSONObject(i);
//                    }
////					 Log.i("Data", "");
//
//                    String CHECKSUM = jsonObject.getString("CHECKSUM");
//                    String ELECTRIC_FENCE = jsonObject.getString("ELECTRIC_FENCE");
//                    String FALL_DETECT = jsonObject.getString("FALL_DETECT");
//                    String FALL_PHONE_NAME1 = jsonObject.getString("FALL_PHONE_NAME1");
//                    String FALL_PHONE_NAME2 = jsonObject.getString("FALL_PHONE_NAME2");
//                    String FALL_PHONE_NAME3 = jsonObject.getString("FALL_PHONE_NAME3");
//                    String FALL_PHONE_NO1 = jsonObject.getString("FALL_PHONE_NO1");
//                    String FALL_PHONE_NO2 = jsonObject.getString("FALL_PHONE_NO2");
//                    String FALL_PHONE_NO3 = jsonObject.getString("FALL_PHONE_NO3");
//                    String FALL_SMS = jsonObject.getString("FALL_SMS");
//                    String FAMILY_PHONE_NAME1 = jsonObject.getString("FAMILY_PHONE_NAME1");
//                    String FAMILY_PHONE_NAME2 = jsonObject.getString("FAMILY_PHONE_NAME2");
//                    String FAMILY_PHONE_NAME3 = jsonObject.getString("FAMILY_PHONE_NAME3");
//                    String FAMILY_PHONE_NO1 = jsonObject.getString("FAMILY_PHONE_NO1");
//                    String FAMILY_PHONE_NO2 = jsonObject.getString("FAMILY_PHONE_NO2");
//                    String FAMILY_PHONE_NO3 = jsonObject.getString("FAMILY_PHONE_NO3");
//                    String FMLY_KEY_DELAY = jsonObject.getString("FMLY_KEY_DELAY");
//
//                    SharedPreferences_status.SaveFamiName1(MyApplication.context, jsonObject.getString("FAMILY_PHONE_NAME1"));
//                    SharedPreferences_status.SaveFamiName2(MyApplication.context, jsonObject.getString("FAMILY_PHONE_NAME2"));
//                    SharedPreferences_status.SaveFamiName3(MyApplication.context, jsonObject.getString("FAMILY_PHONE_NAME3"));
//                    SharedPreferences_status.SaveFamiPhone1(MyApplication.context, jsonObject.getString("FAMILY_PHONE_NO1"));
//                    SharedPreferences_status.SaveFamiPhone2(MyApplication.context, jsonObject.getString("FAMILY_PHONE_NO2"));
//                    SharedPreferences_status.SaveFamiPhone3(MyApplication.context, jsonObject.getString("FAMILY_PHONE_NO3"));
//
//                    String MED_TIME_1 = jsonObject.getString("MED_TIME_1");
//                    String SWITCH_1= new JSONObject(MED_TIME_1).getString("SWITCH");
//                    String TIME_1 =  new JSONObject(MED_TIME_1).getString("TIME");
//                    String WEEK1_1 = new JSONObject(MED_TIME_1).getString("WEEK1");
//                    String WEEK2_1 = new JSONObject(MED_TIME_1).getString("WEEK2");
//                    String WEEK3_1 = new JSONObject(MED_TIME_1).getString("WEEK3");
//                    String WEEK4_1 = new JSONObject(MED_TIME_1).getString("WEEK4");
//                    String WEEK5_1 = new JSONObject(MED_TIME_1).getString("WEEK5");
//                    String WEEK6_1 = new JSONObject(MED_TIME_1).getString("WEEK6");
//                    String WEEK7_1 = new JSONObject(MED_TIME_1).getString("WEEK7");
//
//                    String MED_TIME_2 = jsonObject.getString("MED_TIME_2");
//                    String SWITCH_2= new JSONObject(MED_TIME_2).getString("SWITCH");
//                    String TIME_2 =  new JSONObject(MED_TIME_2).getString("TIME");
//                    String WEEK1_2 = new JSONObject(MED_TIME_2).getString("WEEK1");
//                    String WEEK2_2 = new JSONObject(MED_TIME_2).getString("WEEK2");
//                    String WEEK3_2 = new JSONObject(MED_TIME_2).getString("WEEK3");
//                    String WEEK4_2 = new JSONObject(MED_TIME_2).getString("WEEK4");
//                    String WEEK5_2 = new JSONObject(MED_TIME_2).getString("WEEK5");
//                    String WEEK6_2 = new JSONObject(MED_TIME_2).getString("WEEK6");
//                    String WEEK7_2 = new JSONObject(MED_TIME_2).getString("WEEK7");
//
//                    String MED_TIME_3 = jsonObject.getString("MED_TIME_3");
//                    String SWITCH_3= new JSONObject(MED_TIME_3).getString("SWITCH");
//                    String TIME_3 =  new JSONObject(MED_TIME_3).getString("TIME");
//                    String WEEK1_3 = new JSONObject(MED_TIME_3).getString("WEEK1");
//                    String WEEK2_3 = new JSONObject(MED_TIME_3).getString("WEEK2");
//                    String WEEK3_3 = new JSONObject(MED_TIME_3).getString("WEEK3");
//                    String WEEK4_3 = new JSONObject(MED_TIME_3).getString("WEEK4");
//                    String WEEK5_3 = new JSONObject(MED_TIME_3).getString("WEEK5");
//                    String WEEK6_3 = new JSONObject(MED_TIME_3).getString("WEEK6");
//                    String WEEK7_3 = new JSONObject(MED_TIME_3).getString("WEEK7");
//
//                    String MED_TIME_4 = jsonObject.getString("MED_TIME_4");
//                    String SWITCH_4= new JSONObject(MED_TIME_4).getString("SWITCH");
//                    String TIME_4 =  new JSONObject(MED_TIME_4).getString("TIME");
//                    String WEEK1_4 = new JSONObject(MED_TIME_4).getString("WEEK1");
//                    String WEEK2_4 = new JSONObject(MED_TIME_4).getString("WEEK2");
//                    String WEEK3_4 = new JSONObject(MED_TIME_4).getString("WEEK3");
//                    String WEEK4_4 = new JSONObject(MED_TIME_4).getString("WEEK4");
//                    String WEEK5_4 = new JSONObject(MED_TIME_4).getString("WEEK5");
//                    String WEEK6_4 = new JSONObject(MED_TIME_4).getString("WEEK6");
//                    String WEEK7_4 = new JSONObject(MED_TIME_4).getString("WEEK7");
//
//                    String MED_TIME_5 = jsonObject.getString("MED_TIME_5");
//                    String SWITCH_5= new JSONObject(MED_TIME_5).getString("SWITCH");
//                    String TIME_5 =  new JSONObject(MED_TIME_5).getString("TIME");
//                    String WEEK1_5 = new JSONObject(MED_TIME_5).getString("WEEK1");
//                    String WEEK2_5 = new JSONObject(MED_TIME_5).getString("WEEK2");
//                    String WEEK3_5 = new JSONObject(MED_TIME_5).getString("WEEK3");
//                    String WEEK4_5 = new JSONObject(MED_TIME_5).getString("WEEK4");
//                    String WEEK5_5 = new JSONObject(MED_TIME_5).getString("WEEK5");
//                    String WEEK6_5 = new JSONObject(MED_TIME_5).getString("WEEK6");
//                    String WEEK7_5 = new JSONObject(MED_TIME_5).getString("WEEK7");
//
//                    StringBuffer switch_sbf = new StringBuffer();
//                    StringBuffer time_sbf   = new StringBuffer();
//                    StringBuffer week1_sbf  = new StringBuffer();
//                    StringBuffer week2_sbf  = new StringBuffer();
//                    StringBuffer week3_sbf  = new StringBuffer();
//                    StringBuffer week4_sbf  = new StringBuffer();
//                    StringBuffer week5_sbf  = new StringBuffer();
//
//                    String Switch = switch_sbf.append(SWITCH_1).append(";").append(SWITCH_2).append(";").append(SWITCH_3).
//                            append(";").append(SWITCH_4).append(";").append(SWITCH_5).append(";").toString();
//
//                    String TIME = time_sbf.append(TIME_1).append(";").append(TIME_2).append(";").append(TIME_3).append(";").
//                            append(TIME_4).append(";").append(TIME_5).append(";").toString();
//
//                    String WEEK1 = week1_sbf.append(WEEK1_1).append(";").append(WEEK2_1).append(";").append(WEEK3_1).append(";").
//                            append(WEEK4_1).append(";").append(WEEK5_1).append(";").append(WEEK6_1).append(";").
//                            append(WEEK7_1).append(";").toString();
//
//                    String WEEK2 = week2_sbf.append(WEEK1_2).append(";").append(WEEK2_2).append(";").append(WEEK3_2).append(";").
//                            append(WEEK4_2).append(";").append(WEEK5_2).append(";").append(WEEK6_2).append(";").
//                            append(WEEK7_2).append(";").toString();
//
//                    String WEEK3 = week3_sbf.append(WEEK1_3).append(";").append(WEEK2_3).append(";").append(WEEK3_3).append(";").
//                            append(WEEK4_3).append(";").append(WEEK5_3).append(";").append(WEEK6_3).append(";").
//                            append(WEEK7_3).append(";").toString();
//
//                    String WEEK4 = week4_sbf.append(WEEK1_4).append(";").append(WEEK2_4).append(";").append(WEEK3_4).append(";").
//                            append(WEEK4_4).append(";").append(WEEK5_4).append(";").append(WEEK6_4).append(";").
//                            append(WEEK7_4).append(";").toString();
//
//                    String WEEK5 = week5_sbf.append(WEEK1_5).append(";").append(WEEK2_5).append(";").append(WEEK3_5).append(";").
//                            append(WEEK4_5).append(";").append(WEEK5_5).append(";").append(WEEK6_5).append(";").
//                            append(WEEK7_5).append(";").toString();
//
//
//                    String NON_DISTRUB = jsonObject.getString("NON_DISTRUB");
//
//                    String PERSONAL_INFO = jsonObject.getString("PERSONAL_INFO");
//                    String ACCOUNTID = jsonObject.getString("REAL_NAME");
//                    String AGE = new JSONObject(PERSONAL_INFO).getString("AGE");
//                    String BIRTHDATE_DD = new JSONObject(PERSONAL_INFO).getString("BIRTHDATE_DD");
//                    String BIRTHDATE_MM = new JSONObject(PERSONAL_INFO).getString("BIRTHDATE_MM");
//                    String BIRTHDATE_YY = new JSONObject(PERSONAL_INFO).getString("BIRTHDATE_YY");
//                    String HEIGHT = new JSONObject(PERSONAL_INFO).getString("HEIGHT");
//                    String IDEAL_WEIGHT = new JSONObject(PERSONAL_INFO).getString("IDEAL_WEIGHT");
//                    String NAME = new JSONObject(PERSONAL_INFO).getString("NAME");
//                    String SEX = new JSONObject(PERSONAL_INFO).getString("SEX");
//                    String STEP = new JSONObject(PERSONAL_INFO).getString("STEP");
//                    String UNIT = new JSONObject(PERSONAL_INFO).getString("UNIT");
//                    String WEIGHT = new JSONObject(PERSONAL_INFO).getString("WEIGHT");
//
//                    String RETURN_DT_1 = jsonObject.getString("RETURN_DT_1");
//                    String DAY_DT1 = new JSONObject(RETURN_DT_1).getString("DAY");
//                    String MONTH_DT1 = new JSONObject(RETURN_DT_1).getString("MONTH");
//                    String SWITCH_DT1 = new JSONObject(RETURN_DT_1).getString("SWITCH");
//                    String TIME_DT1 = new JSONObject(RETURN_DT_1).getString("TIME");
//                    String YEAR_DT1 = new JSONObject(RETURN_DT_1).getString("YEAR");
//
//                    String RETURN_DT_2 = jsonObject.getString("RETURN_DT_2");
//                    String DAY_DT2 = new JSONObject(RETURN_DT_2).getString("DAY");
//                    String MONTH_DT2 = new JSONObject(RETURN_DT_2).getString("MONTH");
//                    String SWITCH_DT2 = new JSONObject(RETURN_DT_2).getString("SWITCH");
//                    String TIME_DT2 = new JSONObject(RETURN_DT_2).getString("TIME");
//                    String YEAR_DT2 = new JSONObject(RETURN_DT_2).getString("YEAR");
//
//                    String RETURN_DT = new StringBuffer().append(RETURN_DT_1).append(";").append(RETURN_DT_2).toString();
//
//                    StringBuffer day_buf = new StringBuffer();
//                    StringBuffer month_buf = new StringBuffer();
//                    StringBuffer year_buf = new StringBuffer();
//                    String DAY = day_buf.append(DAY_DT1).append(";").append(DAY_DT2).append(";").toString();
//                    String MONTH = month_buf.append(MONTH_DT1).append(";").append(MONTH_DT2).append(";").toString();
//                    String YEAR = year_buf.append(YEAR_DT1).append(";").append(YEAR_DT2).append(";").toString();
//
//                    String SET_LANG = jsonObject.getString("SET_LANG");
//                    String SOS_KEY_DELAY = jsonObject.getString("SOS_KEY_DELAY");
//                    String SOS_PHONE_NAME1 = jsonObject.getString("SOS_PHONE_NAME1");
//                    String SOS_PHONE_NAME2 = jsonObject.getString("SOS_PHONE_NAME2");
//                    String SOS_PHONE_NAME3 = jsonObject.getString("SOS_PHONE_NAME3");
//                    String SOS_PHONE_NO1 = jsonObject.getString("SOS_PHONE_NO1");
//                    String SOS_PHONE_NO2 = jsonObject.getString("SOS_PHONE_NO2");
//                    String SOS_PHONE_NO3 = jsonObject.getString("SOS_PHONE_NO3");
//                    String SOS_SMS = jsonObject.getString("SOS_SMS");
//                    String TIMEZONE = jsonObject.getString("TIMEZONE");
//                    String WHITE_LIST_ON = jsonObject.getString("WHITE_LIST_ON");
//
//                    String WHITE_PHONE_LIST = jsonObject.getString("WHITE_PHONE_LIST");
//                    JSONArray WHITE_PHONE_array = new JSONArray(WHITE_PHONE_LIST);
//                    StringBuffer phone_buf = new StringBuffer();
//                    String PHONE="";
//                    for(int i = 0; i<WHITE_PHONE_array.length(); i++){
//                        JSONObject echo_objt = WHITE_PHONE_array.getJSONObject(i);
//                        WHITE_PHONE_ITEMNO = echo_objt.getString("ITEMNO");
//                        WHITE_PHONE_PHONE = echo_objt.getString("PHONE");
//                        PHONE = phone_buf.append(WHITE_PHONE_PHONE).append(";").toString();
//                    }
//
//                    up.up_0x11(MyApplication.context, ITEMNO, CHECKSUM, FALL_DETECT, FALL_PHONE_NAME1, FALL_PHONE_NAME2,
//                            FALL_PHONE_NAME3, FALL_PHONE_NO1, FALL_PHONE_NO2, FALL_PHONE_NO3, FALL_SMS, FAMILY_PHONE_NAME1,
//                            FAMILY_PHONE_NAME2, FAMILY_PHONE_NAME3, FAMILY_PHONE_NO1, FAMILY_PHONE_NO2, FAMILY_PHONE_NO3,
//                            FMLY_KEY_DELAY, MED_TIME_1, SWITCH_DT2, TIME, WEEK1, WEEK2, WEEK3, WEEK4, WEEK5, "Default",
//                            "Default", MED_TIME_2, MED_TIME_3, MED_TIME_4, MED_TIME_5, NON_DISTRUB, PERSONAL_INFO, ACCOUNTID,
//                            AGE, BIRTHDATE_DD, BIRTHDATE_MM, BIRTHDATE_YY, HEIGHT, IDEAL_WEIGHT, NAME, SEX, STEP, UNIT, WEIGHT,
//                            RETURN_DT, DAY, MONTH, YEAR, SET_LANG, SOS_KEY_DELAY, SOS_PHONE_NAME1, SOS_PHONE_NAME2,
//                            SOS_PHONE_NAME3, SOS_PHONE_NO1, SOS_PHONE_NO2, SOS_PHONE_NO3, SOS_SMS, TIMEZONE, WHITE_LIST_ON,
//                            ELECTRIC_FENCE, PHONE);
//
//                    language_type = SharedPreferences_status.Get_Language(MyApplication.context);
//                    if(!(SET_LANG.equals(language_type))){
//                        SharedPreferences_status.save_Language(MyApplication.context, language_type);
////						 if(language_type.equals("2")){
////								checkedItem = 0;
////								setLocale(mLangArray[checkedItem], mLocateArray[checkedItem]);
////								Log.e("語系切換", "語系切換");
////							}else if(language_type.equals("3")){
////								checkedItem = 1;
////								setLocale(mLangArray[checkedItem], mLocateArray[checkedItem]);
////								Log.e("語系切換", "語系切換");
////							}else if(language_type.equals("0")){
////								checkedItem = 2;
////								setLocale(mLangArray[checkedItem], mLocateArray[checkedItem]);
////								Log.e("語系切換", "語系切換");
////							}
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                //接收各個硬體設定
//                Receiver();
//                Statuscancel();
//
//                Log.e(Controller.type_0x11, "封包:0x11完成!!!!!!!!!!");
//
////				new SendFrequence_x29("");
//
////				AlarmManager alarmManager2 = (AlarmManager) MyApplication.context.getSystemService(Context.ALARM_SERVICE);
////				Intent intent2 = new Intent(BaseActivity.context, FenceMessage_x29_AlarmManager.class);
////				PendingIntent pendingIntent2 = PendingIntent.getBroadcast(MyApplication.context, 0,intent2, 0);
////				if(alarmManager2 != null) alarmManager2.cancel(pendingIntent2);
////				long triggerAtTime2 = new Date().getTime() +1000;
////				alarmManager2.set(AlarmManager.RTC_WAKEUP, triggerAtTime2, pendingIntent2);
//
//                break;
//
//            case type80090300:
//                try {
//                    jsonArray = new JSONArray(entity);
//                    jsonObject = new JSONObject();
//                    for(int i = 0; i < jsonArray.length(); i++){
//                        jsonObject =  jsonArray.getJSONObject(i);
//                    }
////					Log.e("40", Data);
//                    String SERVER_IP_LIST = jsonObject.getString("SERVER_IP_LIST");
//                    String SERVER_DNS = jsonObject.getString("SERVER_DNS");
//                    up.up_0x40(MyApplication.context, ITEMNO, SERVER_IP_LIST, SERVER_DNS);
//                    SharedPreferences_status.Set_Status0x40(MyApplication.context, "0");
//                    MyApplication.cro.LoData(MyApplication.context, Controller.type_0x2A);
//                } catch (JSONException e1) {
//                    e1.printStackTrace();
//                }
//
//                Log.e(Controller.type_0x40, "封包:0x40完成!!!!!!!!!!");
//                break;
//
//            case type80090400:
//                try {
//                    jsonArray = new JSONArray(entity);
//                    jsonObject = new JSONObject();
//                    for(int i = 0; i < jsonArray.length(); i++){
//                        jsonObject =  jsonArray.getJSONObject(i);
//                    }
//                    StringBuffer buf = new StringBuffer();
//
//                    String DISPLAY_PHOTO = jsonObject.getString("FAMILY_PHOTO");
//                    Log.e("DISPLAY_PHOTO", DISPLAY_PHOTO);
//                    JSONArray PHOTO_array = new JSONArray(DISPLAY_PHOTO);
//                    for(int i = 0; i<PHOTO_array.length(); i++){
//                        JSONObject PHOTO_objt = PHOTO_array.getJSONObject(i);
//                        String photo = PHOTO_objt.getString("URL").trim();
//                        if(photo.trim().equals("") | photo == null){
//                            photo = "0x00";
//                        }
//                        buf.append(photo).append(";");
//                    }
//                    String photo = buf.toString();
//                    up.up_0x4B(MyApplication.context, ITEMNO, "Default", photo);
//
//                    DAO dao = new  DAO();
//                    VO_0x4B data_4B = null;
//                    ArrayList<VO_0x4B> get0x4B = dao.getdata_0x4B(MyApplication.context);
//                    for(int i=0; i<get0x4B.size(); i++){
//                        data_4B = get0x4B.get(i);
//                    }
//                    String Image_URL = data_4B.getFAMILY_PHOTO();
//                    final String[] Img_array = Image_URL.split(";");
//                    new Thread(){
//                        public void run(){
//                            Looper.prepare();
//                            try {
//                                new ImgDownload(BitmapFactory.decodeStream(new URL("http://"+Img_array[0]).openStream()), "1");
//                            } catch (MalformedURLException e) {
//                                e.printStackTrace();
//                            } catch (IOException e) {
//                                System.gc();
//                                e.printStackTrace();
//                            }
//                            Looper.loop();
//                        }
//                    }.start();
//                    new Thread(){
//                        public void run(){
//                            Looper.prepare();
//                            try {
//                                new ImgDownload(BitmapFactory.decodeStream(new URL("http://"+Img_array[1]).openStream()), "2");
//                            } catch (MalformedURLException e) {
//                                e.printStackTrace();
//                            } catch (IOException e) {
//                                System.gc();
//                                e.printStackTrace();
//                            }
//                            Looper.loop();
//                        }
//                    }.start();
//                    new Thread(){
//                        public void run(){
//                            Looper.prepare();
//                            try {
//                                new ImgDownload(BitmapFactory.decodeStream(new URL("http://"+Img_array[2]).openStream()), "3");
//                            } catch (MalformedURLException e) {
//                                e.printStackTrace();
//                            } catch (IOException e) {
//                                System.gc();
//                                e.printStackTrace();
//                            }
//                            Looper.loop();
//                        }
//                    }.start();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                Log.e(Controller.type_0x4B, "封包:0x4B完成!!!!!!!!!!");
//                break;
//
//            case type80090500:
//                try {
//                    jsonArray = new JSONArray(entity);
//                    jsonObject = new JSONObject();
//                    for(int i = 0; i < jsonArray.length(); i++){
//                        jsonObject =  jsonArray.getJSONObject(i);
//                    }
//
//                    StringBuffer buf = new StringBuffer();
//
//                    String DISPLAY_PHOTO = jsonObject.getString("DISPLAY_PHOTO");
//                    JSONArray PHOTO_array = new JSONArray(DISPLAY_PHOTO);
//                    for(int i = 0; i<PHOTO_array.length(); i++){
//                        JSONObject PHOTO_objt = PHOTO_array.getJSONObject(i);
//                        String photo = PHOTO_objt.getString("URL");
//                        if(photo.trim().equals("") | photo == null) photo = "0x00";
//                        buf.append(photo).append(";");
//                    }
//                    String photo = buf.toString();
//                    Log.e(Controller.type_0x4C, photo);
//
//
//
//                    up.up_0x4C(MyApplication.context, ITEMNO, "Default", photo);
//
//                    DAO dao = new  DAO();
//                    VO_0x4C data_4C = null;
//                    ArrayList<VO_0x4C> get0x4C = dao.getdata_0x4C(MyApplication.context);
//                    for(int i=0; i<get0x4C.size(); i++){
//                        data_4C = get0x4C.get(i);
//                    }
//                    String Image_URL = data_4C.getDISPLAY_PHOTO();
//                    final String[] Img_array = Image_URL.split(";");
//                    new Thread(){
//                        public void run(){
//                            Looper.prepare();
//                            try {
//                                new ImgDownload(BitmapFactory.decodeStream(new URL("http://"+Img_array[0]).openStream()),
//                                        "gallery_img1");
//                            } catch (MalformedURLException e) {
//                                e.printStackTrace();
//                            } catch (IOException e) {
//                                System.gc();
//                                e.printStackTrace();
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                            Looper.loop();
//                        }
//                    }.start();
//                    new Thread(){
//                        public void run(){
//                            Looper.prepare();
//                            try {
//                                new ImgDownload(BitmapFactory.decodeStream(new URL("http://"+Img_array[1]).openStream()),
//                                        "gallery_img2");
//                            } catch (MalformedURLException e) {
//                                e.printStackTrace();
//                            } catch (IOException e) {
//                                System.gc();
//                                e.printStackTrace();
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                            Looper.loop();
//                        }
//                    }.start();
//                    new Thread(){
//                        public void run(){
//                            Looper.prepare();
//                            try {
//                                new ImgDownload(BitmapFactory.decodeStream(new URL("http://"+Img_array[2]).openStream()),
//                                        "gallery_img3");
//                            } catch (MalformedURLException e) {
//                                e.printStackTrace();
//                            } catch (IOException e) {
//                                System.gc();
//                                e.printStackTrace();
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                            Looper.loop();
//                        }
//                    }.start();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                Log.e(Controller.type_0x4C, "封包:0x4C完成!!!!!!!!!!");
//                break;
//
//            case type4d000:
//                try {
//                    String GROUP_ID = null;
//                    String NAME= null;
//                    String PHONE= null;
//                    String PHOTO_URL= null;
//                    jsonObject = new JSONObject(entity);
//                    Data =  jsonObject.getString("Retrun_Data");
//                    String PHONE_LIST = new JSONObject(Data).getString("PHONE_LIST");
//                    JSONArray PHONE_array = new JSONArray(PHONE_LIST);
//                    for(int i = 0; i<PHONE_array.length(); i++){
//                        JSONObject PHONE_objt = PHONE_array.getJSONObject(i);
//                        GROUP_ID = PHONE_objt.getString("GROUP_ID");
//                        NAME = PHONE_objt.getString("NAME");
//                        PHONE = PHONE_objt.getString("PHONE");
//                        PHOTO_URL = PHONE_objt.getString("PHOTO_URL");
//                    }
//                    up.up_0x4D(MyApplication.context, ITEMNO, GROUP_ID, NAME, PHONE, PHOTO_URL, PHONE_LIST);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//                Log.e(Controller.type_0x4D, "封包:0x4D完成!!!!!!!!!!");
//                break;

            case type80130100:
                Log.e(Controller.type_0x20, "封包:0x20完成!!!!!!!!!!");
                break;
            case type80130200:
                try{
                    /**
                     *緊急求救結束後狀態解除
                     *@param sos_type
                     *row 784
                     *@date 2015-09-22
                     */
//                    PhoneCallReceiver.sos_type = null;
//                    NetReceiver.mHandler.removeCallbacks(NetReceiver.mRunnable);
//                    BaseActivity.mContext.stopService(new Intent(BaseActivity.mContext, NetReceiver.class));
                    MyApplication.cro.delete_0x30();
                }catch(Exception e){
                    e.printStackTrace();
                }
                Log.e(Controller.type_0x30, "封包:0x30完成!!!!!!!!!!");
                break;
//            case type80150100:
//                CallPhone_0x3F.count = 0;
//                Log.e(Controller.type_0x3F, "封包:0x3F完成!!!!!!!!!!");
//                break;
//            case type80130600:
//                Log.e(Controller.type_0x29, "封包:0x29完成!!!!!!!!!!");
//                MyApplication.cro.delete_0x29();
//                break;
//            case type80072100:
//                Log.e(Controller.type_800721, "封包:取得無動作800721完成!!!!!!!!!!");
//                try {
//                    if(SharedPreferences_status.getNo_movement_detection(MyApplication.getAppContext())){
//                        NoMovementDetectionManager.close_no_movement_detection(MyApplication.getAppContext());
//                    }
//
//                    jsonArray = new JSONArray(entity);
//                    jsonObject = jsonArray.getJSONObject(0);
//                    //0正常 1錯誤
//                    if(jsonObject.getString("status").equals("0")){
//                        //0關閉 1開啟
//                        if (jsonObject.getInt("nonmovementStatus") == 1) {
//                            SharedPreferences_status.setNo_movement_detection(
//                                    MyApplication.getAppContext(), true);
//                            String startTime = jsonObject.getString("startTime");
//                            String endTime = jsonObject.getString("endTime");
//                            String weekly = jsonObject.getString("weekly");
//                            //存入偏好設定
//                            SharedPreferences_status.setNo_movement_detection_startTime(MyApplication.getAppContext(), startTime);
//                            SharedPreferences_status.setNo_movement_detection_endTime(MyApplication.getAppContext(), endTime);
//                            SharedPreferences_status.setNo_movement_detection_weekly(MyApplication.getAppContext(), weekly);
//                            MyApplication.log(this.getClass().getName(), "'jsonObject' = " + jsonObject);
//                            MyApplication.log(this.getClass().getName(), "'startTime' = " + startTime);
//                            MyApplication.log(this.getClass().getName(), "'endTime' = " + endTime);
//                            MyApplication.log(this.getClass().getName(), "'weekly' = " + weekly);
//
//                            NoMovementDetectionManager.open_no_movement_detection(MyApplication.getAppContext(), startTime
//                                    , endTime, weekly);
//                        } else {
//                            SharedPreferences_status.setNo_movement_detection(
//                                    MyApplication.getAppContext(), false);
//                            NoMovementDetectionManager.close_no_movement_detection(MyApplication.getAppContext());
//                        }
//                    }else{
//                        MyApplication.logE(this.getClass().getName(), jsonObject.getString("msg"));
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                break;
//            case type80131000:
//
//                Log.e(Controller.type_0x18, "封包:0x18完成!!!!!!!!!!");
//                break;
//            case type80100200:
//                Log.e(Controller.type_801002, "封包:儲存硬體設定完成801002!!!!!!!!!!");
//                MyApplication.cro.LoData(MyApplication.context, Controller.type_0x7F);
//                break;
//            case type80130500:
//                break;
//            case type80090800:
//                Log.e(Controller.type_801002, "封包:儲存白名單設定完成800908!!!!!!!!!!");
//                MyApplication.cro.LoData(MyApplication.context, Controller.type_0x7F);
//                break;
//            case type80161000:
//                try {
//                    jsonArray = new JSONArray(entity);
//                    jsonObject = new JSONObject();
//                    for(int i = 0; i < jsonArray.length(); i++){
//                        jsonObject =  jsonArray.getJSONObject(i);
//                    }
//                    String[] array={"0"};
//                    String activitySwitch = jsonObject.getString("activitySwitch").trim();
//                    String stepRange = jsonObject.getString("stepRange").trim();
//                    String stepTotal = jsonObject.getString("stepTotal").trim();
//                    String weight = jsonObject.getString("weight").trim();
//
//                    if(activitySwitch.equals("") | activitySwitch == null) activitySwitch = "on";
//                    if(stepRange.equals("") | stepRange == null) stepRange = "60";
//                    if(stepTotal.equals("") | stepTotal == null) stepTotal = "5000";
//                    if(weight.equals("") | weight == null) weight = "60";
//
//                    Jianbudevice_update j = new Jianbudevice_update();
//                    j.update_SettingPedometer(MyApplication.context, array, "", "", "", "",
//                            stepRange, weight, "", stepTotal, "", "");
//
//                    SharedPreferences_status.saveClouds_status(MyApplication.context, activitySwitch);
//
//                    if(activitySwitch.equals("on")){
//                        SharedPreferences_status.saveTogButton(MyApplication.context, true);
//                    }else{
//                        SharedPreferences_status.saveTogButton(MyApplication.context, false);
//                    }
//
//                    Http_PostData.handler.sendEmptyMessage(1);
//                    PedometerService.CheckSync();
//                } catch (Exception e1) {
////					e1.printStackTrace();
//                }
//                MyApplication.fall_cro.LoData(MyApplication.context, Fall_Controller.APItype_851002);
//                Log.e(Controller.type_801610, "封包:取得健步器設定801610完成!!!!!!!!!!");
//                break;
//            case type80161100:
//                Log.e(Controller.type_801611, "封包:儲存健步器設定801611完成!!!!!!!!!!");
//                Http_PostData.handler.sendEmptyMessage(1);
//                PedometerService.CheckSync();
//                break;
//            default:
//                try{
//                    Log.e("上傳失敗", "封包:上傳失敗!!");
//                    Log.e("上傳失敗", "錯誤代碼:"+Type);
//                    DataSync.DataSync_progressDialog_cancel();
//                    Controller.ProgressLodingSetting_Cancel(Controller.type_0x11);
//                }catch(Exception e){
//
//                }
//                break;
        }

        if(Type.equals("80130500")){
//            Log.e(Controller.type_0x28, "封包:0x28完成!!!!!!!!!!");
            progressDialog.cancel();
        }else{
            if(progressDialog != null){
                progressDialog.cancel();
            }
//            CallPhone_0x3F.count = 0;
        }
    }

//    private void Receiver(){
//        intent = new Intent(SettingsReceiver.SettingMsg);
//        bundle = new Bundle();
//        bundle.putString("HardwareSettting", Status);
//        intent.putExtras(bundle);
//        BaseActivity.mContext.sendBroadcast(intent);
//    }

//    private void setLocale(String mLang, String mLoc) {
//        Resources res = MyApplication.context.getResources();
//        // Change locale settings in the app.
//        DisplayMetrics dm = res.getDisplayMetrics();
//        Configuration conf = res.getConfiguration();
//        conf.locale = new Locale(mLang, mLoc);
//        res.updateConfiguration(conf, dm);
//        // Call setter with reflection
//        LangSetter.changeLanguageSettings(MyApplication.context, conf.locale);
//    }

    private void Statuscancel(){
        Status="";
    }

    public static void progressDialog(ProgressDialog progressdialog){
        progressDialog = progressdialog;
    }

    public static void SettingsType(String status){
        Status = status;
    }

    public static void PedHandler(Handler handlers){
        handler = handlers;
    }

    public static void ClockChangeMsg1(Handler handlers){
        mhandler1 = handlers;
    }

    public static void ClockChangeMsg2(Handler handlers){
        mhandler2 = handlers;
    }

    public static void ClockChangeMsg3(Handler handlers){
        mhandler3 = handlers;
    }

    public static void ClockChangeMsg4(Handler handlers){
        mhandler4 = handlers;
    }

    public static void ClockChangeMsg5(Handler handlers){
        mhandler5 = handlers;
    }

    public static String getErrorInfoFromException(Exception e) {
        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            return "\r\n" + sw.toString() + "\r\n";
        } catch (Exception e2) {
            return "bad getErrorInfoFromException";
        }
    }

}

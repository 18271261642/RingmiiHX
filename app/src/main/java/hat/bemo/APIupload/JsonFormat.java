package hat.bemo.APIupload;

import android.annotation.SuppressLint;
import android.os.Looper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by apple on 2017/11/10.
 */

public class JsonFormat {
    private JSONObject j;

    @SuppressLint("SdCardPath")
    public String jsonformat(String type, String... prams){
        JSONObject job = null;
        Controller.ESettings_type Etype = Controller.ESettings_type.ByStr(type);
        switch(Etype){

            case type_0x2A:
                try {
                    job = new JSONObject();
                    job.put("token", prams[0]);
                    job.put("device", prams[1]);
                    job.put("appid", prams[2]);
                    job.put("account", prams[3]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case type_0x2B:
                try {
                    job = new JSONObject();
                    job.put("account", prams[0]);
                    job.put("ALERT_TYPE", prams[1]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case type_0x2E:

                try {
                    job = new JSONObject();
                    JSONObject job2 = new JSONObject();
                    JSONObject job3 = null;
                    JSONArray ary = new JSONArray();
                    String[] detail1 = null;
                    String[] detail2 = null;
                    String[] detail3 = null;
                    String[] detail4 = null;
                    String[] detail5 = null;

                    job.put("PWR_ON_DATE", prams[0]);
//					job.put("FW_BUILD_DATE", prams[1]);
                    job.put("FW_800", prams[1]);
                    job.put("VOLTAGE", prams[2]);
                    job.put("VOLTAGE_PERCENT", prams[3]);

                    detail1 = split(prams[4]);
                    String mcc = detail1[0];
                    if(mcc == null || mcc.equals("")){
                        mcc = "0";
                    }

                    detail2 = split(prams[5]);
                    String mnc = detail2[0];
                    if(mnc == null || mnc.equals("")){
                        mnc = "0";
                    }

                    detail3 = split(prams[6]);
                    String lcc = detail3[0];
                    if(lcc == null || lcc.equals("")){
                        lcc = "0";
                    }

                    detail4 = split(prams[7]);
                    String cell_id = detail4[0];
                    if(cell_id == null || cell_id.equals("")){
                        cell_id = "0";
                    }

                    detail5 = split(prams[8]);
                    String rssi = detail5[0];
                    if(rssi == null || rssi.equals("")){
                        rssi = "0";
                    }

                    job2.put("MCC", mcc);
                    job2.put("MNC", mnc);
                    job2.put("LAC", lcc);
                    job2.put("CELL_ID", cell_id);
                    job2.put("RSSI", rssi);
                    job.put("CELLER", job2);


                    detail1 = split(prams[4]);
                    detail2 = split(prams[5]);
                    detail3 = split(prams[6]);
                    detail4 = split(prams[7]);
                    detail5 = split(prams[8]);


                    for(int j=0; j<detail1.length; j++){

                        job3 = new JSONObject();

                        detail1 = split(prams[4]);
                        String mccs = detail1[j];
                        if(mccs == null || mccs.equals("")){
                            mccs = "0";
                        }
                        job3.put("MCC", mccs);

                        detail2 = split(prams[5]);
                        String mncs = detail2[j];
                        if(mncs == null || mncs.equals("")){
                            mncs = "0";
                        }
                        job3.put("MNC", mncs);

                        detail3 = split(prams[6]);
                        String lccs = detail3[j];
                        if(lccs == null || lccs.equals("")){
                            lccs = "0";
                        }
                        job3.put("LAC", lccs);

                        detail4 = split(prams[7]);
                        String cell_ids = detail4[j];
                        if(cell_ids == null || cell_ids.equals("")){
                            cell_ids = "0";
                        }
                        job3.put("CELL_ID", cell_ids);

                        detail5 = split(prams[8]);
                        String rssis = detail5[j];
                        if(rssis == null || rssis.equals("")){
                            rssis = "0";
                        }
                        job3.put("RSSI", rssis);
                        ary.put(job3);
                    }

                    job.put("MUTLICELL", ary);
                    job.put("CREATE_DATE", prams[9]);
                    job.put("account", prams[10]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case type_0x2F:
                try {
                    job = new JSONObject();
                    JSONObject job2 = new JSONObject();
                    JSONObject job3 = null;
                    JSONArray ary = new JSONArray();
                    String[] detail1 = null;
                    String[] detail2 = null;
                    String[] detail3 = null;
                    String[] detail4 = null;
                    String[] detail5 = null;

                    job.put("PWR_OFF_DATE", prams[0]);
                    job.put("PWR_OFF_TYPE", prams[1]);
                    job.put("VOLTAGE", prams[2]);
                    job.put("VOLTAGE_PERCENT", prams[3]);

                    job.put("GPS_ACCURACY", prams[4]);
                    job.put("GPS_LAT", prams[5]);
                    job.put("GPS_LNG", prams[6]);
                    job.put("GPS_STATUS", prams[7]);

                    detail1 = split(prams[8]);
                    String mcc = detail1[0];
                    if(mcc == null || mcc.equals("")){
                        mcc = "0";
                    }

                    detail2 = split(prams[9]);
                    String mnc = detail2[0];
                    if(mnc == null || mnc.equals("")){
                        mnc = "0";
                    }

                    detail3 = split(prams[10]);
                    String lcc = detail3[0];
                    if(lcc == null || lcc.equals("")){
                        lcc = "0";
                    }

                    detail4 = split(prams[11]);
                    String cell_id = detail4[0];
                    if(cell_id == null || cell_id.equals("")){
                        cell_id = "0";
                    }

                    detail5 = split(prams[12]);
                    String rssi = detail5[0];
                    if(rssi == null || rssi.equals("")){
                        rssi = "0";
                    }

                    job2.put("MCC", mcc);
                    job2.put("MNC", mnc);
                    job2.put("LAC", lcc);
                    job2.put("CELL_ID", cell_id);
                    job2.put("RSSI", rssi);
                    job.put("CELLER", job2);

                    detail1 = split(prams[4]);
                    detail2 = split(prams[5]);
                    detail3 = split(prams[6]);
                    detail4 = split(prams[7]);
                    detail5 = split(prams[8]);

                    for(int j=0; j<detail1.length; j++){

                        job3 = new JSONObject();

                        detail1 = split(prams[8]);
                        String mccs = detail1[j];
                        if(mccs == null || mccs.equals("")){
                            mccs = "0";
                        }
                        job3.put("MCC", mccs);

                        detail2 = split(prams[9]);
                        String mncs = detail2[j];
                        if(mncs == null || mncs.equals("")){
                            mncs = "0";
                        }
                        job3.put("MNC", mncs);

                        detail3 = split(prams[10]);
                        String lccs = detail3[j];
                        if(lccs == null || lccs.equals("")){
                            lccs = "0";
                        }
                        job3.put("LAC", lccs);

                        detail4 = split(prams[11]);
                        String cell_ids = detail4[j];
                        if(cell_ids == null || cell_ids.equals("")){
                            cell_ids = "0";
                        }
                        job3.put("CELL_ID", cell_ids);

                        detail5 = split(prams[12]);
                        String rssis = detail5[j];
                        if(rssis == null || rssis.equals("")){
                            rssis = "0";
                        }
                        job3.put("RSSI", rssis);
                        ary.put(job3);
                    }
                    job.put("MUTLICELL", ary);
                    job.put("CREATE_DATE", prams[13]);
                    job.put("account", prams[14]);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case type_0x01:

                try {
                    job = new JSONObject();
                    JSONObject job2 = null;
                    JSONObject job3 = null;
                    JSONObject job4 = null;
                    JSONObject job5 = null;
                    JSONArray ary = new JSONArray();
                    JSONArray ary2 = null;
                    JSONArray ary3 = null;
                    String[] detail1 = null;
                    String[] detail2 = null;
                    String[] detail3 = null;
                    String[] detail4 = null;
                    String[] detail5 = null;

                    detail1 = split(prams[4]);
                    detail2 = split(prams[5]);
                    detail3 = split(prams[6]);
                    detail4 = split(prams[7]);
                    detail5 = split(prams[8]);

                    for(int i = 0; i < Size; i++){
                        job2 = new JSONObject();
                        job4 = new JSONObject();
                        ary2 = new JSONArray();
                        ary3 = new JSONArray();

                        job2.put("PR_DATE", prams[1]);
                        job2.put("VOLTAGE", prams[2]);
                        job2.put("VOLTAGE_PERCENT", prams[3]);

                        for(int j=0; j<detail1.length; j++){

                            job3 = new JSONObject();
                            detail1 = split(prams[4]);
                            String lac = detail1[j];
                            if(lac == null || lac.equals("")){
                                lac = "0";
                            }
                            job3.put("LAC", lac);

                            detail2 = split(prams[5]);
                            String mnc = detail2[j];
                            if(mnc == null || mnc.equals("")){
                                mnc = "0";
                            }
                            job3.put("MNC", mnc);

                            detail3 = split(prams[6]);
                            String rssi = detail3[j];
                            if(rssi == null || rssi.equals("")){
                                rssi = "0";
                            }
                            job3.put("RSSI", rssi);

                            detail4 = split(prams[7]);
                            String cell_ids = detail4[j];
                            if(cell_ids == null || cell_ids.equals("")){
                                cell_ids = "0";
                            }
                            job3.put("CELL_ID", cell_ids);

                            detail5 = split(prams[8]);
                            String mcc = detail5[j];
                            if(mcc == null || mcc.equals("")){
                                mcc = "0";
                            }
                            job3.put("MCC", mcc);
                            ary2.put(job3);
                        }
                        job2.put("MUTLICELL", ary2);

                        job2.put("GSensor_AVG", prams[9]);
                        job2.put("GSensor_MAX", prams[10]);
                        job2.put("PPM", prams[11]);

                        detail1 = split(prams[4]);
                        String lac = detail1[0];
                        if(lac == null || lac.equals("")){
                            lac = "0";
                        }
                        job4.put("LAC", lac);

                        detail2 = split(prams[5]);
                        String mnc = detail2[0];
                        if(mnc == null || mnc.equals("")){
                            mnc = "0";
                        }
                        job4.put("MNC", mnc);

                        detail3 = split(prams[6]);
                        String rssi = detail3[0];
                        if(rssi == null || rssi.equals("")){
                            rssi = "0";
                        }
                        job4.put("RSSI", rssi);

                        detail4 = split(prams[7]);
                        String cell_id = detail4[0];
                        if(cell_id == null || cell_id.equals("")){
                            cell_id = "0";
                        }
                        job4.put("CELL_ID", cell_id);

                        detail5 = split(prams[8]);
                        String mcc = detail5[0];
                        if(mcc == null || mcc.equals("")){
                            mcc = "0";
                        }
                        job4.put("MCC", mcc);

                        job2.put("CELLER", job4);

                        job2.put("GPS_STATUS", prams[12]);
                        job2.put("GPS_LAT", prams[13]);
                        job2.put("GPS_LNG", prams[14]);
                        job2.put("GPS_ADDRESS", prams[15]);
                        job2.put("GPS_ACCURACY", prams[16]);

                        detail1 = null;
                        detail2 = null;
                        detail3 = null;

                        detail1 = split(prams[18]);
                        detail2 = split(prams[19]);
                        detail3 = split(prams[20]);
                        for(int j=0; j<detail1.length; j++){
                            if(j <= 9){
                                job5 = new JSONObject();

                                detail1 = split(prams[18]);
                                String wifi_mac = detail1[j];
                                job5.put("WIFI_MAC", wifi_mac);

                                detail2 = split(prams[19]);
                                String wifi_dB = detail2[j];
                                job5.put("WIFI_Signal_dB", wifi_dB);

                                detail3 = split(prams[20]);
                                String wifi_channel = detail3[j];
                                job5.put("WIFI_Channel", wifi_channel);
                                ary3.put(job5);
                            }
                        }
                        job2.put("MULTIWIFI", ary3);
                        ary.put(job2);
                    }
                    job.put("PRS", ary);
                    job.put("PR_COUNT", prams[0]);
                    job.put("CREATE_DATE", prams[1]);
                    job.put("account", prams[21]);

                    j = job;
                    new Thread(){
                        public void run(){
                            Looper.prepare();
                            try {
//                                saveTxt("0x01", j.toString(), Controller.CreateDate, ImgDownload.ALBUM_PATH_SD);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            Looper.loop();
                        }
                    }.start();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case type_0x7F:
                try {
                    job = new JSONObject();
                    job.put("CHECKSUM_10", prams[0]);
                    job.put("CHECKSUM_11", prams[1]);
                    job.put("account", prams[2]);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

            case type_0x10:
                try {
                    job = new JSONObject();
                    job.put("account", prams[0]);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

            case type_0x11:
                try {
                    job = new JSONObject();
                    job.put("account", prams[0]);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

            case type_0x18:
                try{
                    job = new JSONObject();
                    JSONObject job2 = null;
                    JSONObject job3 = new JSONObject();
                    JSONObject job4 = null;
                    JSONArray ary = new JSONArray();
                    JSONArray ary2 = new JSONArray();
                    String[] detail1 = null;
                    String[] detail2 = null;
                    String[] detail3 = null;
                    String[] detail4 = null;
                    String[] detail5 = null;

                    detail1 = split(prams[0]);
                    detail2 = split(prams[1]);
                    detail3 = split(prams[2]);
                    detail4 = split(prams[3]);
                    detail5 = split(prams[4]);

                    for(int j=0; j<detail1.length; j++){
                        job2 = new JSONObject();
                        detail1 = split(prams[0]);
                        String lac = detail1[j];
                        if(lac == null || lac.equals("")){
                            lac = "0";
                        }
                        job2.put("LAC", lac);

                        detail2 = split(prams[1]);
                        String mnc = detail2[j];
                        if(mnc == null || mnc.equals("")){
                            mnc = "0";
                        }
                        job2.put("MNC", mnc);

                        detail3 = split(prams[2]);
                        String rssi = detail3[j];
                        if(rssi == null || rssi.equals("")){
                            rssi = "0";
                        }
                        job2.put("RSSI", rssi);

                        detail4 = split(prams[3]);
                        String cell_ids = detail4[j];
                        if(cell_ids == null || cell_ids.equals("")){
                            cell_ids = "0";
                        }
                        job2.put("CELL_ID", cell_ids);

                        detail5 = split(prams[4]);
                        String mcc = detail5[j];
                        if(mcc == null || mcc.equals("")){
                            mcc = "0";
                        }
                        job2.put("MCC", mcc);
                        ary.put(job2);
                    }
                    job.put("MUTLICELL", ary);

                    detail1 = split(prams[0]);
                    String lac = detail1[0];
                    if(lac == null || lac.equals("")){
                        lac = "0";
                    }
                    job3.put("LAC", lac);

                    detail2 = split(prams[1]);
                    String mnc = detail2[0];
                    if(mnc == null || mnc.equals("")){
                        mnc = "0";
                    }
                    job3.put("MNC", mnc);

                    detail3 = split(prams[2]);
                    String rssi = detail3[0];
                    if(rssi == null || rssi.equals("")){
                        rssi = "0";
                    }
                    job3.put("RSSI", rssi);

                    detail4 = split(prams[3]);
                    String cell_id = detail4[0];
                    if(cell_id == null || cell_id.equals("")){
                        cell_id = "0";
                    }
                    job3.put("CELL_ID", cell_id);

                    detail5 = split(prams[4]);
                    String mcc = detail5[0];
                    if(mcc == null || mcc.equals("")){
                        mcc = "0";
                    }
                    job3.put("MCC", mcc);
                    job.put("CELLER", job3);

                    job.put("GPS_STATUS", prams[5]);
                    job.put("GPS_ACCURACY", prams[6]);
                    job.put("GPS_ADDRESS", prams[7]);
                    job.put("GPS_LNG", prams[8]);
                    job.put("GPS_LAT", prams[9]);
                    job.put("TRACKING_DATE", prams[10]);

                    detail1 = null;
                    detail2 = null;
                    detail3 = null;

                    detail1 = split(prams[11]);
                    detail2 = split(prams[12]);
                    detail3 = split(prams[13]);
                    for(int j=0; j<detail1.length; j++){
                        if(j <= 9){
                            job4 = new JSONObject();

                            detail1 = split(prams[11]);
                            String wifi_mac = detail1[j];
                            job4.put("WIFI_MAC", wifi_mac);

                            detail2 = split(prams[12]);
                            String wifi_dB = detail2[j];
                            job4.put("WIFI_Signal_dB", wifi_dB);

                            detail3 = split(prams[13]);
                            String wifi_channel = detail3[j];
                            job4.put("WIFI_Channel", wifi_channel);
                            ary2.put(job4);
                        }
                    }
                    job.put("MULTIWIFI", ary2);
                    job.put("CREATE_DATE", prams[14]);
                    job.put("account", prams[15]);
                }catch(Exception e){
                    e.printStackTrace();
                }
                break;

            case type_0x40:
                try {
//					JSONObject job2 = null;
                    job = new JSONObject();
                    job.put("account", prams[0]);
//					JSONArray ary = new JSONArray();
//
//					job.put("SERVER_DNS", prams[0]);
//
//					String[] prams_ary = {prams[1], prams[2], prams[3]};
//
//					for(int i = 0; i<3; i++){
//						job2 = new JSONObject();
//						job2.put("SERVER_IP_PORT", prams_ary[i]);
//						ary.put(job2);
//					}
//					job.put("SERVER_IP_LIST", ary);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

            case type_0x4B:
                try {
//					JSONObject job2 = null;
                    job = new JSONObject();
                    job.put("account", prams[0]);
//					JSONArray ary = new JSONArray();
//
//					String[] itemno_ary = {prams[0], prams[1], prams[2]};
//					String[] photo_ary = {prams[3], prams[4], prams[5]};
//
//					for(int i = 0; i<3; i++){
//						job2 = new JSONObject();
//						job2.put("ITEMNO", itemno_ary[i]);
//						job2.put("URL", photo_ary[i]);
//						ary.put(job2);
//					}
//					job.put("FAMILY_PHOTO", ary);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

            case type_0x4C:
                try {
//					JSONObject job2 = null;
                    job = new JSONObject();
                    job.put("account", prams[0]);
//					JSONArray ary = new JSONArray();
//
//					String[] itemno_ary = {prams[0], prams[1], prams[2]};
//					String[] photo_ary = {prams[3], prams[4], prams[5]};
//
//					for(int i = 0; i<3; i++){
//						job2 = new JSONObject();
//						job2.put("ITEMNO", itemno_ary[i]);
//						job2.put("URL", photo_ary[i]);
//						ary.put(job2);
//					}
//					job.put("DISPLAY_PHOTO", ary);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

            case type_0x4D:
                try {
//					JSONObject job2 = null;
                    job = new JSONObject();
                    job.put("account", prams[0]);
//					JSONArray ary = new JSONArray();
//
//					String[] group_id_ary = {prams[0], prams[1], prams[2]};
//					String[] itemno_ary = {prams[3], prams[4], prams[5]};
//					String[] name_ary = {prams[6], prams[7], prams[8]};
//					String[] phone_ary = {prams[9], prams[10], prams[11]};
//					String[] photo_url_ary = {prams[12], prams[13], prams[14]};
//
//					for(int i = 0; i<3; i++){
//						job2 = new JSONObject();
//						job2.put("GROUP_ID", group_id_ary[i]);
//						job2.put("ITEMNO", itemno_ary[i]);
//						job2.put("NAME", name_ary[i]);
//						job2.put("PHONE", phone_ary[i]);
//						job2.put("PHOTO_URL", photo_url_ary[i]);
//						ary.put(job2);
//					}
//					job.put("PHONE_LIST", ary);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

            case type_0x28:
                try {
                    JSONObject job2 = null;
                    JSONObject job3 = null;
                    JSONObject job4 = null;
                    job = new JSONObject();
                    job2 = new JSONObject();
                    JSONArray ary = new JSONArray();
                    JSONArray ary2 = new JSONArray();
                    String[] detail1 = null;
                    String[] detail2 = null;
                    String[] detail3 = null;
                    String[] detail4 = null;
                    String[] detail5 = null;
                    String[] detail6 = null;
                    String[] detail7 = null;
                    String[] detail8 = null;

                    detail1 = split(prams[12]);
                    detail2 = split(prams[13]);
                    detail3 = split(prams[14]);

                    detail4 = split(prams[15]);
                    detail5 = split(prams[16]);
                    detail6 = split(prams[17]);
                    detail7 = split(prams[18]);
                    detail8 = split(prams[19]);

                    String CELL_ID_C = detail4[0];
                    job2.put("CELL_ID", CELL_ID_C);
                    String LAC_C = detail5[0];
                    job2.put("LAC", LAC_C);
                    String MCC_C = detail6[0];
                    job2.put("MCC", MCC_C);
                    String MNC_C = detail7[0];
                    job2.put("MNC", MNC_C);
                    String RSSI_C = detail8[0];
                    job2.put("RSSI", RSSI_C);

                    job.put("CELLER", job2);

                    job.put("CREATE_DATE", prams[5]);
                    job.put("EMG_DATE", prams[6]);
                    job.put("GPS_ACCURACY", prams[7]);
                    job.put("GPS_ADDRESS", prams[8]);
                    job.put("GPS_LAT", prams[9]);
                    job.put("GPS_LNG", prams[10]);
                    job.put("GPS_STATUS", prams[11]);

                    for(int j=0; j<detail1.length; j++){

                        if(j <= 9){
                            job3 = new JSONObject();
                            detail1 = split(prams[12]);
                            String WIFI_MAC = detail1[j];
                            job3.put("WIFI_MAC", WIFI_MAC);

                            detail2 = split(prams[13]);
                            String WIFI_Signal_dB = detail2[j];
                            job3.put("WIFI_Signal_dB", WIFI_Signal_dB);

                            detail3 = split(prams[14]);
                            String WIFI_Channel = detail3[j];
                            job3.put("WIFI_Channel",WIFI_Channel);
                            ary.put(job3);
                        }
                    }
                    job.put("MULTIWIFI", ary);

                    for(int j=0; j<detail4.length; j++){
                        job4 = new JSONObject();
                        detail4 = split(prams[15]);
                        String CELL_ID = detail4[j];
                        job4.put("CELL_ID", CELL_ID);

                        detail5 = split(prams[16]);
                        String LAC = detail5[j];
                        job4.put("LAC", LAC);

                        detail6 = split(prams[17]);
                        String MCC = detail6[j];
                        job4.put("MCC", MCC);

                        detail7 = split(prams[18]);
                        String MNC = detail7[j];
                        job4.put("MNC", MNC);

                        detail8 = split(prams[19]);
                        String RSSI = detail8[j];
                        job4.put("RSSI", RSSI);
                        ary2.put(job4);
                    }
                    job.put("MUTLICELL", ary2);
                    job.put("account", prams[20]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case type_0x20:
                try {
                    job = new JSONObject();
                    JSONObject job2 = new JSONObject();
                    JSONObject job3 = null;
                    JSONObject job4 = new JSONObject();
                    JSONArray ary = new JSONArray();
                    JSONArray ary2 = new JSONArray();
                    String[] detail1 = null;
                    String[] detail2 = null;
                    String[] detail3 = null;
                    String[] detail4 = null;
                    String[] detail5 = null;

                    detail1 = split(prams[0]);
                    detail2 = split(prams[1]);
                    detail3 = split(prams[2]);
                    detail4 = split(prams[3]);
                    detail5 = split(prams[4]);

                    String CELL_ID = detail1[0];
                    String LAC = detail2[0];
                    String MCC = detail3[0];
                    String MNC = detail4[0];
                    String RSSI = detail5[0];

                    job2.put("CELL_ID", CELL_ID);
                    job2.put("LAC", LAC);
                    job2.put("MCC", MCC);
                    job2.put("MNC", MNC);
                    job2.put("RSSI", RSSI);

                    job.put("CREATE_DATE", prams[5]);
                    job.put("EMG_DATE", prams[6]);
                    job.put("GPS_ACCURACY", prams[7]);
                    job.put("GPS_ADDRESS", prams[8]);
                    job.put("GPS_LAT", prams[9]);
                    job.put("GPS_LNG", prams[10]);
                    job.put("GPS_STATUS",prams[11]);

                    detail1 = split(prams[12]);
                    detail2 = split(prams[13]);
                    detail3 = split(prams[14]);
                    for(int j=0; j<detail1.length; j++){
                        if(j <= 9){
                            job3 = new JSONObject();
                            detail1 = split(prams[12]);
                            String WIFI_Channel = detail1[j];
                            job3.put("WIFI_Channel", WIFI_Channel);
                            detail2 = split(prams[13]);
                            String WIFI_MAC = detail2[j];
                            job3.put("WIFI_MAC", WIFI_MAC);
                            detail3 = split(prams[14]);
                            String WIFI_Signal_dB = detail3[j];
                            job3.put("WIFI_Signal_dB",WIFI_Signal_dB);
                            ary.put(job3);
                        }
                    }
                    job.put("MULTIWIFI",ary);

                    detail1 = split(prams[0]);
                    detail2 = split(prams[1]);
                    detail3 = split(prams[2]);
                    detail4 = split(prams[3]);
                    detail5 = split(prams[4]);
                    for(int j=0; j<detail1.length; j++){
                        job4 = new JSONObject();
                        detail1 = split(prams[0]);
                        String CELL_ID_S = detail1[j];
                        job4.put("CELL_ID", CELL_ID_S);
                        detail2 = split(prams[1]);
                        String LAC_S = detail2[j];
                        job4.put("LAC", LAC_S);
                        detail3 = split(prams[2]);
                        String MCC_S = detail3[j];
                        job4.put("MCC",MCC_S);
                        detail4 = split(prams[3]);
                        String MNC_S = detail4[j];
                        job4.put("MNC",MNC_S);
                        detail5 = split(prams[4]);
                        String RSSI_S;
                        System.out.println(RSSI_S = detail5[j]);
                        job4.put("RSSI",RSSI_S);
                    }
                    ary2.put(job4);
                    job.put("MUTLICELL",ary2);

                    job.put("CELLER", job2);
                    job.put("account", prams[20]);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case type_0x30:
                try {
                    job = new JSONObject();
                    JSONObject job2 = new JSONObject();
                    JSONObject job3 = null;
                    JSONObject job4 = new JSONObject();
                    JSONArray ary = new JSONArray();
                    JSONArray ary2 = new JSONArray();
                    String[] detail1 = null;
                    String[] detail2 = null;
                    String[] detail3 = null;
                    String[] detail4 = null;
                    String[] detail5 = null;

                    detail1 = split(prams[0]);
                    detail2 = split(prams[1]);
                    detail3 = split(prams[2]);
                    detail4 = split(prams[3]);
                    detail5 = split(prams[4]);

                    String CELL_ID = detail1[0];
                    String LAC = detail2[0];
                    String MCC = detail3[0];
                    String MNC = detail4[0];
                    String RSSI = detail5[0];

                    job2.put("CELL_ID", CELL_ID);
                    job2.put("LAC", LAC);
                    job2.put("MCC", MCC);
                    job2.put("MNC", MNC);
                    job2.put("RSSI", RSSI);

                    job.put("CREATE_DATE", prams[5]);
                    job.put("EMG_DATE", prams[6]);
                    job.put("GPS_ACCURACY", prams[7]);
                    job.put("GPS_ADDRESS", prams[8]);
                    job.put("GPS_LAT", prams[9]);
                    job.put("GPS_LNG", prams[10]);
                    job.put("GPS_STATUS",prams[11]);

                    detail1 = split(prams[12]);
                    detail2 = split(prams[13]);
                    detail3 = split(prams[14]);
                    for(int j=0; j<detail1.length; j++){
                        if(j <= 9){
                            job3 = new JSONObject();
                            detail1 = split(prams[12]);
                            String WIFI_Channel = detail1[j];
                            job3.put("WIFI_Channel", WIFI_Channel);
                            detail2 = split(prams[13]);
                            String WIFI_MAC = detail2[j];
                            job3.put("WIFI_MAC", WIFI_MAC);
                            detail3 = split(prams[14]);
                            String WIFI_Signal_dB = detail3[j];
                            job3.put("WIFI_Signal_dB",WIFI_Signal_dB);
                            ary.put(job3);
                        }
                    }
                    job.put("MULTIWIFI",ary);

                    detail1 = split(prams[0]);
                    detail2 = split(prams[1]);
                    detail3 = split(prams[2]);
                    detail4 = split(prams[3]);
                    detail5 = split(prams[4]);
                    for(int j=0; j<detail1.length; j++){
                        job4 = new JSONObject();
                        detail1 = split(prams[0]);
                        String CELL_ID_S = detail1[j];
                        job4.put("CELL_ID", CELL_ID_S);
                        detail2 = split(prams[1]);
                        String LAC_S = detail2[j];
                        job4.put("LAC", LAC_S);
                        detail3 = split(prams[2]);
                        String MCC_S = detail3[j];
                        job4.put("MCC",MCC_S);
                        detail4 = split(prams[3]);
                        String MNC_S = detail4[j];
                        job4.put("MNC",MNC_S);
                        detail5 = split(prams[4]);
                        String RSSI_S;
                        System.out.println(RSSI_S = detail5[j]);
                        job4.put("RSSI",RSSI_S);
                    }
                    ary2.put(job4);
                    job.put("MUTLICELL",ary2);

                    job.put("CELLER", job2);
                    job.put("account", prams[20]);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case type_0x29:
                job = new JSONObject();
                JSONObject job2 = null;
                JSONObject job3 = null;
                JSONObject job4 = null;
                JSONObject job5 = null;
                JSONArray ary = new JSONArray();
                JSONArray ary2 = null;
                JSONArray ary3 = null;
                String[] detail1 = null;
                String[] detail2 = null;
                String[] detail3 = null;
                String[] detail4 = null;
                String[] detail5 = null;

                try {
                    job.put("CREATE_DATE", prams[0]);

                    detail1 = split(prams[3]);
                    String mcc = detail1[0];
                    if(mcc == null || mcc.equals("")){
                        mcc = "0";
                    }

                    detail2 = split(prams[4]);
                    String mnc = detail2[0];
                    if(mnc == null || mnc.equals("")){
                        mnc = "0";
                    }

                    detail3 = split(prams[2]);
                    String lac = detail3[0];
                    if(lac == null || lac.equals("")){
                        lac = "0";
                    }

                    detail4 = split(prams[1]);
                    String cell_id = detail4[0];
                    if(cell_id == null || cell_id.equals("")){
                        cell_id = "0";
                    }

                    detail5 = split(prams[5]);
                    String rssi = detail5[0];
                    if(rssi == null || rssi.equals("")){
                        rssi = "0";
                    }

                    for(int i = 0; i < Size; i++){
                        ary2 = new JSONArray();
                        ary3 = new JSONArray();
                        job2 = new JSONObject();
                        job3 = new JSONObject();

                        job3.put("CELL_ID", cell_id);
                        job3.put("LAC", lac);
                        job3.put("MCC", mcc);
                        job3.put("MNC", mnc);
                        job3.put("RSSI", rssi);
                        job2.put("CELLER", job3);

                        job2.put("EF_DATE", prams[6]);
                        job2.put("ELECTRIC_FENCE_ITEMNO", prams[7]);
                        job2.put("GPS_ACCURACY", prams[8]);
                        job2.put("GPS_ADDRESS", prams[9]);
                        job2.put("GPS_LAT", prams[10]);
                        job2.put("GPS_LNG", prams[11]);
                        job2.put("GPS_STATUS", prams[12]);
                        job2.put("GSensor_AVG", prams[13]);
                        job2.put("GSensor_MAX", prams[14]);

                        detail1 = null;
                        detail2 = null;
                        detail3 = null;

                        detail1 = split(prams[15]);
                        detail2 = split(prams[16]);
                        detail3 = split(prams[17]);

                        for(int j=0; j<detail1.length; j++){
                            if(j <= 9){
                                job4 = new JSONObject();
                                detail1 = split(prams[15]);
                                String wifi_channel = detail1[j];
                                job4.put("WIFI_Channel", wifi_channel);

                                detail2 = split(prams[16]);
                                String wifi_mac = detail2[j];
                                job4.put("WIFI_MAC", wifi_mac);

                                detail3 = split(prams[17]);
                                String wifi_dB = detail3[j];
                                job4.put("WIFI_Signal_dB", wifi_dB);
                                ary2.put(job4);
                            }
                        }
                        job2.put("MULTIWIFI", ary2);

                        detail1 = split(prams[3]);
                        detail2 = split(prams[4]);
                        detail3 = split(prams[2]);
                        detail4 = split(prams[1]);
                        detail5 = split(prams[5]);

                        for(int j=0; j<detail1.length; j++){

                            job5 = new JSONObject();

                            detail1 = split(prams[3]);
                            String mccs = detail1[j];
                            if(mccs == null || mccs.equals("")){
                                mccs = "0";
                            }
                            job5.put("MCC", mccs);

                            detail2 = split(prams[4]);
                            String mncs = detail2[j];
                            if(mncs == null || mncs.equals("")){
                                mncs = "0";
                            }
                            job5.put("MNC", mncs);

                            detail3 = split(prams[2]);
                            String lccs = detail3[j];
                            if(lccs == null || lccs.equals("")){
                                lccs = "0";
                            }
                            job5.put("LAC", lccs);

                            detail4 = split(prams[1]);
                            String cell_ids = detail4[j];
                            if(cell_ids == null || cell_ids.equals("")){
                                cell_ids = "0";
                            }
                            job5.put("CELL_ID", cell_ids);

                            detail5 = split(prams[5]);
                            String rssis = detail5[j];
                            if(rssis == null || rssis.equals("")){
                                rssis = "0";
                            }
                            job5.put("RSSI", rssis);
                            ary3.put(job5);
                        }
                        job2.put("MUTLICELL", ary3);

                        job2.put("PPM", prams[23]);
                        job2.put("TEMP", prams[24]);
                        job2.put("VOLTAGE", prams[25]);
                        job2.put("VOLTAGE_PERCENT", prams[26]);
                        ary.put(job2);
                    }

                    job.put("EFS", ary);
                    job.put("EF_COUNT", prams[27]);
                    job.put("account", prams[28]);

                    j = job;
                    new Thread(){
                        public void run(){
                            Looper.prepare();
                            try {
//                                saveTxt("0x29", j.toString(), Controller.CreateDate, ImgDownload.ALBUM_PATH_SD);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            Looper.loop();
                        }
                    }.start();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case type_0x3F:
                try {
                    job = new JSONObject();
                    job2 = new JSONObject();
                    ary = new JSONArray();

                    job2.put("PHONE", "+886"+prams[0]);
                    job2.put("START_TIME", prams[1]);
                    job2.put("END_TIME", prams[2]);
                    job2.put("CALL_TYPE", prams[3]);
                    ary.put(job2);
                    job.put("CREATE_DATE", prams[4]);
                    job.put("DATA", ary);
                    job.put("account", prams[5]);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

            case type_800721:
                try {
                    job = new JSONObject();
                    job.put("account", prams[0]);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

            case type_801002:
                try {
                    job = new JSONObject();
                    job.put("account", prams[0]);
                    job.put("number", prams[1]);
                    job.put("value", prams[2]);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

            case type_800908:
                try {
                    job = new JSONObject();
                    job.put("account", prams[0]);
                    job.put("on_off", prams[1]);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

            case type_801610:
                try {
                    job = new JSONObject();
                    job.put("account", prams[0]);
                    job.put("weight", prams[1]);
                    job.put("stepRange", prams[2]);
                    job.put("stepTotal", prams[3]);
                    job.put("activitySwitch", prams[4]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case type_801611:
                try {
                    job = new JSONObject();
                    job.put("account", prams[0]);
                    job.put("weight", prams[1]);
                    job.put("stepRange", prams[2]);
                    job.put("stepTotal", prams[3]);
                    job.put("activitySwitch", prams[4]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }

        Log.e("job", job.toString());
        return job.toString();
    }


    public static int Size;
    public static void JsonSize(int size){
        Size = size;
    }

    public static String[] split(String prams){
        String[] detail = prams.split(";");
        return detail;
    }

    public static boolean saveTxt(String type, String job, String filename, String path){
        //sd卡检测
//		String sdStatus = Environment.getExternalStorageState();
//		if(!sdStatus.equals(Environment.MEDIA_MOUNTED)){
//		    Toast.makeText(context, "SD 卡不可用", Toast.LENGTH_SHORT).show();
//		    return false;
//		}
        //检测文件夹是否存在
        File file = new File(path);
        file.exists();
        file.mkdirs();
        String p = path+File.separator+filename+".txt";
        FileOutputStream outputStream = null;
        try {
            //创建文件，并写入内容
            outputStream = new FileOutputStream(new File(p));
            String msg = new String(type+":"+job);
            outputStream.write(msg.getBytes("UTF-8"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            if(outputStream!=null){
                try {
                    outputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

}

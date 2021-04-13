package hat.bemo.updata;

import android.util.Log;

import com.google.gson.Gson;
import com.guider.health.apilib.BuildConfig;
import com.guider.healthring.MyApp;
import com.guider.healthring.util.OkHttpTool;
import com.guider.healthring.util.SharedPreferencesUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.guider.healthring.BuildConfig.APIHDURL;

/**
 * Created by Admin
 * Date 2019/10/24
 */
public class UpdateGuiderData {
    private static final String TAG = "UpdateGuiderData";


    /**
     * int sbp: 收缩压
     * 			int dbp: 舒张压
     * 			DateTime testTime: 测量时间，ISO8601
     * 			String deviceCode: 手环MAC
     */
    //上传血压
    public static void uploadBloodData(String sbp,String dpb,String testTime,String deviceMac){
        long accountId = (long) SharedPreferencesUtils.getParam(MyApp.getContext(),"accountIdGD",0L);
        if(accountId == 0)
            return;
        List<Map<String,Object>> bloodList = new ArrayList<>();
        Map<String,Object> maps = new HashMap<>();
        maps.put("accountId",accountId);
        maps.put("sbp",dpb);
        maps.put("dbp",sbp);
        maps.put("testTime",testTime);
        maps.put("deviceCode",deviceMac);
        bloodList.add(maps);
        Log.e(TAG,"---------bemo血压参数="+maps.toString());
        // http://apihd.guiderhealth.com/
        OkHttpTool.getInstance().doRequest(APIHDURL + "api/v1/bloodpressure", new Gson().toJson(bloodList), "", new OkHttpTool.HttpResult() {
            @Override
            public void onResult(String result) {
                Log.e(TAG,"--------bemo血压上传="+result);
            }
        });

    }


    /**
     * 血氧上传
     * long accountId : 用户账号id
     * 			int bo: 血氧值
     * 			DateTime testTime: 测量时间，ISO8601,
     * 			String deviceCode: 手环MAC
     * @param spo2
     * @param deviceCode
     * @param testTime
     */
    public static void uploadOxygenData(String spo2,String deviceCode,String testTime){
        long accountId = (long) SharedPreferencesUtils.getParam(MyApp.getContext(),"accountIdGD",0L);
        if(accountId == 0)
            return;
        List<Map<String,Object>> spo2List = new ArrayList<>();
        Map<String,Object> maps = new HashMap<>();
        maps.put("accountId",accountId);
        maps.put("bo",spo2);
        maps.put("testTime",testTime);
        maps.put("deviceCode",deviceCode);
        spo2List.add(maps);
        Log.e(TAG,"---------bemo血氧参数="+maps.toString());
        // http://apihd.guiderhealth.com/
        OkHttpTool.getInstance().doRequest(APIHDURL + "api/v1/bloodoxygen", new Gson().toJson(spo2List), "", new OkHttpTool.HttpResult() {
            @Override
            public void onResult(String result) {
                Log.e(TAG,"--------bemo血氧上传="+result);
            }
        });

    }



    //体温上传

    /**
     *long accountId : 用户账号id
     * 			float bodyTemp: 体温值
     * 			DateTime testTime: 测量时间，ISO8601,
     * 			String deviceCode: 手环MAC
     */
    public static void uploadBodyTemp(String bodyTemp,String testTime,String mac){
        long accountId = (long) SharedPreferencesUtils.getParam(MyApp.getContext(),"accountIdGD",0L);
        if(accountId == 0)
            return;
        List<Map<String,Object>> listMap = new ArrayList<>();
        Map<String,Object> maps = new HashMap<>();
        maps.put("accountId",accountId);
        maps.put("bodyTemp",bodyTemp);
        maps.put("testTime",testTime);
        maps.put("deviceCode",mac);
        listMap.add(maps);
        Log.e(TAG,"---------bemo体温参数="+maps.toString());
        // http://apihd.guiderhealth.com/
        OkHttpTool.getInstance().doRequest(APIHDURL + "api/v1/bodytemp", new Gson().toJson(listMap), "", new OkHttpTool.HttpResult() {
            @Override
            public void onResult(String result) {
                Log.e(TAG,"--------bemo体温上传返回="+result);
        }
        });

    }


    //血糖上传

    /**
     *
     long accountId : 用户账号id
     enum bsTime:  "RANDOM" 随机
     int bloodSpeed : 肱动脉血流速度
     float bs : 血糖值
     float hemoglobin : 血红蛋白
     DateTime testTime: 测量时间，ISO8601,
     String deviceCode: 手环MAC
     */
    public static void updateBloodSugarData(String bs,String time,String mac){
        long accountId = (long) SharedPreferencesUtils.getParam(MyApp.getContext(),"accountIdGD",0L);
        if(accountId == 0)
            return;
        List<Map<String,Object>> listMap = new ArrayList<>();
        Map<String,Object> maps = new HashMap<>();
        maps.put("accountId",accountId);
        maps.put("bsTime","RANDOM");
        maps.put("bs",bs);
        maps.put("testTime",time);
        maps.put("deviceCode",mac);
        listMap.add(maps);
        Log.e(TAG,"---------bemo血糖参数="+maps.toString());
        // http://apihd.guiderhealth.com/
        OkHttpTool.getInstance().doRequest(APIHDURL + "api/v1/bloodsugar", new Gson().toJson(listMap), "", new OkHttpTool.HttpResult() {
            @Override
            public void onResult(String result) {
                Log.e(TAG,"-------bemo血糖上传返回="+result);
            }
        });
    }

}

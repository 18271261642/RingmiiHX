package com.aliyun.rtcdemo.activity;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSON;
import com.alivc.rtc.AliRtcAuthInfo;
import com.alivc.rtc.AliRtcEngine;
import com.alivc.rtc.AliRtcEngineImpl;
import com.aliyun.rtcdemo.R;
import com.aliyun.rtcdemo.bean.RTCAuthInfo;
import com.aliyun.rtcdemo.presenter.AliRtcLoginPresenter;
import com.guider.health.apilib.ApiUtil;
import com.guider.health.common.core.DateUtil;
import com.guider.health.common.core.NetIp;
import com.guider.health.common.core.UserManager;
import com.guider.health.common.net.net.RestService;
import com.guider.health.common.net.net.RetrofitLogInterceptor;
//import com.guider.health.common.core.NetIp;
//import com.guider.health.common.net.net.RestService;
//import com.guider.health.common.net.net.RetrofitLogInterceptor;



import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LoginActivity extends AppCompatActivity {
    /**
     * 申请requestcode
     */
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_EXTERNAL_STORAGE
           };
    //登录按钮
    Button login;
    //用户名
    private String mUserName;
    //频道号
    String mChannel;
    private AliRtcEngineImpl mAliRtcEngine;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        //初始化view
        initView();
        //添加监听
        addListener();



    }
    //初始化view
    private void initView(){
         login = (Button) findViewById(R.id.login);
    }
    //添加监听
    private void addListener(){
        login.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                requestPermission();
            }
        });
    }

    //申请访问sdcard权限
    private void requestPermission() {
        if((ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)||
                (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)||
                (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)||
                (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                ){
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        }else{
            initEngine();
            //跳转视频页面
                jumpNext();

        }
    }
    @SuppressLint("NewApi") @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_EXTERNAL_STORAGE) {
            if ((grantResults[0] == PackageManager.PERMISSION_GRANTED)&&
                    (grantResults[1] == PackageManager.PERMISSION_GRANTED)&&
                    (grantResults[2] == PackageManager.PERMISSION_GRANTED)&&
                    (grantResults[3] == PackageManager.PERMISSION_GRANTED)
                    ) {
                initEngine();
                //跳转视频页面
                 jumpNext();


            } else {
                //提示用户手动打开权限

                Toast.makeText(this, "请求权限要打开，否则不能正常使用！", Toast.LENGTH_LONG).show();

            }
        }
    }
    private void initEngine() {
        mAliRtcEngine = AliRtcEngine.getInstance(this);

    }
    /**
     * 随机生成用户名
     * @return
     */
    private String randomName() {
        Random rd = new Random();
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            // 你想生成几个字符
            str.append((char) (Math.random() * 26 + 'a'));
        }
        return str.toString();
    }
    //跳转视频页面
    private void jumpNext(){
        int id = UserManager.getInstance().getAccountId();
        if (id != 0){
            getId(id, 129);
        }






//        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("alirtcsample://chat"));
//        //用户名
//        mUserName = randomName();
//        //频道号
//        mChannel ="122" ;
//
//        RTCAuthInfo rtcAuthInfo=new RTCAuthInfo();
//        Bundle b = new Bundle();
//
//        //用户名
//        b.putString("username", mUserName);
//        b.putString("channel", mChannel);
//
//        b.putSerializable("rtcAuthInfo", rtcAuthInfo);
//        intent.putExtras(b);
//        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
                //退出时释放AliRtcEngine
        if (mAliRtcEngine != null) {
            mAliRtcEngine.leaveChannel();
            mAliRtcEngine = null;
        }
    }

    private void getId(final int from, final int to){
        try {

            Map<String, Object> map = new HashMap<>();
            map.put("fromAccountId", from);
            map.put("toAccountId", to);

            Retrofit retrofit = new Retrofit.Builder().baseUrl(NetIp.BASE_URL).client(ApiUtil.getOkHttpClient()).build();
            RestService restService = retrofit.create(RestService.class);
            Call<ResponseBody> call = restService.get("api/v1/consultchat", map);

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        String result = response.body().string();

                        Log.i("haix", "|_____用户信息请求: "+result);

                        JSONObject jo = JSON.parseObject(result);
                        int code = jo.getInteger("code");


                        JSONObject data = jo.getJSONObject("data");

                        int id = data.getInteger("id");

                        useId(from, to, id);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                    Log.i("haix", "错误: "+t.toString());
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void useId(final int from, final int to, int id){
        try {

            Date date = new Date(System.currentTimeMillis());

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.CHINA);
            format.setTimeZone(TimeZone.getTimeZone("GMT+0"));
            String dateString = DateUtil.dateToString(DateUtil.utcNow(), "yyyy-MM-dd'T'HH:mm:ss'Z'");

            int[] ids = {to};
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("chatId", id);
            jsonObject.put("fromAccountId", from);
            jsonObject.put("pushSenceTypeId", 1);
            jsonObject.put("rtcType", "AUDIO");
            jsonObject.put("toAccountIds", ids);



            final RequestBody requestBody =
                    RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toJSONString());


            Retrofit retrofit = new Retrofit.Builder().baseUrl(NetIp.BASE_URL).client(ApiUtil.getOkHttpClient()).build();
            RestService restService = retrofit.create(RestService.class);
            Call<ResponseBody> call = restService.postOnlyBody("api/v1/rtc", requestBody);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {



                        String result = response.body().string();

                        com.alibaba.fastjson.JSONObject jo = JSON.parseObject(result);
                        int code = jo.getInteger("code");


                        if (code >= 0) {

                            JSONObject data = jo.getJSONObject("data");

                            if (data != null){

                                String appid = data.getString("appid");
                                //String[] gslb = data.getObject("gslb", );
                                String channelId = data.getString("channelId");
                                String channelKey = data.getString("channelKey");
                                String nonce = data.getString("nonce");
                                long timestamp = data.getLong("timestamp");
                                String fromUserName = data.getString("fromUserName");
                                JSONObject paramAliRtcUser = data.getJSONObject("paramAliRtcUser");
                                if (paramAliRtcUser == null){
                                    return;
                                }
                                JSONObject ff = paramAliRtcUser.getJSONObject(from+"");
                                int ff_accountId = ff.getInteger("accountId");
                                String ff_userid = ff.getString("userid");
                                String ff_username = ff.getString("username");
                                String ff_password = ff.getString("password");

                                JSONObject tt = paramAliRtcUser.getJSONObject(to+"");
                                int tt_accountId = tt.getInteger("accountId");
                                String tt_userid = tt.getString("userid");
                                String tt_username = tt.getString("username");
                                String tt_password = tt.getString("password");


//                                String[] str  =  {"https://rgslb.rtc.aliyuncs.com"};
//                                AliRtcAuthInfo userInfo = new AliRtcAuthInfo();
//                                userInfo.setAppid("wpbd464h");
//                                userInfo.setNonce("CK-2bdcdc3fa8359572cd90a3c7dd1d4301");
//                                userInfo.setTimestamp(1562488353);
//                                userInfo.setUserId("e9895aba-a501-4991-b421-e4fbdb8bd51e");
//                                userInfo.setGslb(str);
//                                userInfo.setToken("dd1600f29bafe220c98ef42c364afc2f24832d6b57b1f5615b838289aad6c459");
//                                userInfo.setConferenceId("ZdHOTw-13");


                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("alirtcsample://chat"));
                                //用户名
                                mUserName = randomName();
                                //频道号
                                mChannel ="122" ;

                                RTCAuthInfo rtcAuthInfo=new RTCAuthInfo();
                                Bundle b = new Bundle();

                                //用户名
                                b.putString("username", mUserName);
                                b.putString("appid", appid);
                                b.putString("nonce", nonce);
                                b.putLong("timestamp", timestamp);
                                b.putString("userid", tt_userid);
                                //b.putString("gslb", gslb);
                                b.putString("password", tt_password);
                                b.putString("channelId", channelId);

                                b.putSerializable("rtcAuthInfo", rtcAuthInfo);
                                intent.putExtras(b);
                                startActivity(intent);

                            }
                        }


                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /*
    private static final int TIME_OUT = 60;
    private OkHttpClient OK_HTTP_CLIENT = new OkHttpClient.Builder()
            .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
            .addInterceptor(new RetrofitLogInterceptor())
            .build();
     */
}

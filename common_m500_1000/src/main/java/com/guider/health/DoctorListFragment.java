package com.guider.health;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.rtcdemo.bean.RTCAuthInfo;
import com.guider.health.all.R;
import com.guider.health.apilib.ApiCallBack;
import com.guider.health.apilib.ApiUtil;
import com.guider.health.apilib.IGuiderApi;
import com.guider.health.apilib.model.DoctorInfo;
import com.guider.health.common.core.BaseFragment;
import com.guider.health.common.core.Config;
import com.guider.health.common.core.DateUtil;
import com.guider.health.common.core.NetIp;
import com.guider.health.common.core.UserManager;
import com.guider.health.common.net.net.RestService;
import com.guider.health.widget.DoctorAdapter;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class DoctorListFragment extends BaseFragment {

    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.view_doctor_list, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (view.findViewById(R.id.middle_line) != null) {
            view.findViewById(R.id.middle_line).setVisibility(View.GONE);
            setHomeEvent(view.findViewById(R.id.home), Config.HOME_DEVICE);

            ((TextView) view.findViewById(R.id.title)).setText("选择咨询医生");
            view.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pop();

                }
            });
        }
        getDoctoryList();
    }

    private void getDoctoryList() {
        showDialog("正在获取医生信息");
        ApiUtil.createApi(IGuiderApi.class).getDoctorList(UserManager.getInstance().getAccountId()).enqueue(new ApiCallBack<List<DoctorInfo>>(_mActivity) {
            @Override
            public void onApiResponse(Call<List<DoctorInfo>> call, Response<List<DoctorInfo>> response) {
                super.onApiResponse(call, response);
                hideDialog();
                List<DoctorInfo> body = response.body();
                setDoctorList(body);
            }

            @Override
            public void onFailure(Call<List<DoctorInfo>> call, Throwable t) {
                super.onFailure(call, t);
                hideDialog();
                Toast.makeText(_mActivity, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

//        ArrayList<DoctorInfo> list = new ArrayList<>();
//        list.add(new DoctorBean(R.mipmap.icon_d1, "唐云强", "主治医生", true));
//        list.add(new DoctorBean(R.mipmap.icon_d2, "金川", "主任医师", false));
//        list.add(new DoctorBean(R.mipmap.icon_d3, "吕跃", "主治医生", false));
//        list.add(new DoctorBean(R.mipmap.icon_d4, "袁周", "副主任医师", false));
//        list.add(new DoctorBean(R.mipmap.icon_d5, "徐玲玲", "副主任医师", false));
//        list.add(new DoctorBean(R.mipmap.icon_d6, "王华曦", "主治医师", false));
//        list.add(new DoctorBean(R.mipmap.icon_d7, "马为", "主治医师", false));
//        list.add(new DoctorBean(R.mipmap.icon_d8, "甘照宇", "副主治医师", false));
//        setDoctorList(list);
    }

    DoctorAdapter doctorAdapter;

    private void setDoctorList(List<DoctorInfo> list) {
        GridView grDoctor = view.findViewById(R.id.gv_doctor);
        doctorAdapter = new DoctorAdapter(list);
        grDoctor.setAdapter(new DoctorAdapter(list));
        grDoctor.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                requestPermission(doctorAdapter.getItem(i));
            }
        });
    }

    /**
     * 申请requestcode
     */
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.RECORD_AUDIO,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
    };
    //登录按钮
    Button login;
    //用户名
    private String mUserName;
    //频道号
    String mChannel;
    //private AliRtcEngineImpl mAliRtcEngine;

//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(com.aliyun.rtcdemo.R.layout.login);
//        //初始化view
//        initView();
//        //添加监听
//        addListener();
//
//    }
//    //初始化view
//    private void initView(){
//        login = (Button) findViewById(com.aliyun.rtcdemo.R.id.login);
//    }
//    //添加监听
//    private void addListener(){
//        login.setOnClickListener(new View.OnClickListener(){
//
//            @Override
//            public void onClick(View v) {
//                requestPermission();
//            }
//        });
//    }

    //申请访问sdcard权限
    private void requestPermission(DoctorInfo doctorInfo) {
        if ((ContextCompat.checkSelfPermission(_mActivity, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(_mActivity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(_mActivity, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(_mActivity, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        ) {
            ActivityCompat.requestPermissions(_mActivity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        } else {
            //initEngine();
            //跳转视频页面
            jumpNext(doctorInfo);

        }
    }

    @SuppressLint("NewApi")
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_EXTERNAL_STORAGE) {
            if ((grantResults[0] == PackageManager.PERMISSION_GRANTED) &&
                    (grantResults[1] == PackageManager.PERMISSION_GRANTED) &&
                    (grantResults[2] == PackageManager.PERMISSION_GRANTED) &&
                    (grantResults[3] == PackageManager.PERMISSION_GRANTED)
            ) {
                //initEngine();
                //跳转视频页面
//                jumpNext();
            } else {
                //提示用户手动打开权限

                Toast.makeText(_mActivity, getResources().getString(R.string.request_per), Toast.LENGTH_LONG).show();

            }
        }
    }
//    private void initEngine() {
//        mAliRtcEngine = AliRtcEngine.getInstance(this);
//
//    }

    /**
     * 随机生成用户名
     *
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
    private void jumpNext(DoctorInfo doctorInfo) {

        showDialog();
        getId(UserManager.getInstance().getAccountId(), doctorInfo.getAccountId());


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


    private void getId(final int from, final int to) {
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

                        Log.i("haix", "|_____用户信息请求: " + result);

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
                    hideDialog();
                    Log.i("haix", "错误: " + t.toString());
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void useId(final int from, final int to, int id) {
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
            // isDev = 0非开发模式  1开发模式
            Call<ResponseBody> call = restService.postOnlyBody("api/v1/rtc?isDev=0", requestBody);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {


                        hideDialog();
                        String result = response.body().string();

                        JSONObject jo = JSON.parseObject(result);
                        int code = jo.getInteger("code");


                        if (code >= 0) {

                            JSONObject data = jo.getJSONObject("data");

                            if (data != null) {

                                String appid = data.getString("appid");
                                //String[] gslb = data.getObject("gslb", );
                                String channelId = data.getString("channelId");
                                String channelKey = data.getString("channelKey");
                                String nonce = data.getString("nonce");
                                long timestamp = data.getLong("timestamp");
                                String fromUserName = data.getString("fromUserName");
                                JSONObject paramAliRtcUser = data.getJSONObject("paramAliRtcUser");
                                if (paramAliRtcUser == null) {
                                    return;
                                }
                                JSONObject ff = paramAliRtcUser.getJSONObject(from + "");
                                int ff_accountId = ff.getInteger("accountId");
                                String ff_userid = ff.getString("userid");
                                String ff_username = ff.getString("username");
                                String ff_password = ff.getString("password");

                                JSONObject tt = paramAliRtcUser.getJSONObject(to + "");
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

                                try {
                                    Class rtcClass = Class.forName(Config.RTC_ACTIVITY);
                                    Intent intent = new Intent(_mActivity, rtcClass);
                                    //用户名
                                    mUserName = randomName();

                                    RTCAuthInfo rtcAuthInfo = new RTCAuthInfo();
                                    Bundle b = new Bundle();

                                    //用户名
                                    b.putString("username", mUserName);
                                    b.putString("appid", appid);
                                    b.putString("nonce", nonce);
                                    b.putLong("timestamp", timestamp);
                                    b.putString("userid", ff_userid);
                                    //b.putString("gslb", gslb);
                                    b.putString("password", ff_password);
                                    b.putString("channelId", channelId);

                                    b.putSerializable("rtcAuthInfo", rtcAuthInfo);
                                    intent.putExtras(b);
                                    startActivity(intent);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        }


                    } catch (IOException e) {

                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    hideDialog();
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

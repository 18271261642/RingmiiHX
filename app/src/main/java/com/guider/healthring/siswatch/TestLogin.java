package com.guider.healthring.siswatch;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.guider.healthring.R;
import com.guider.healthring.util.Md5Util;
import com.guider.healthring.util.SharedPreferencesUtils;
import com.guider.healthring.util.URLs;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/7/18.
 */

public class TestLogin extends AppCompatActivity implements  View.OnClickListener{

    EditText testName;
    EditText testPwd;
    Button testLoginbtn;
    Button testTestbtn;

    Calendar calendar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_login);
        initViewIds();
        calendar = Calendar.getInstance();

    }

    private void  initViewIds(){
        testName = findViewById(R.id.test_name);
        testPwd = findViewById(R.id.test_pwd);
        testLoginbtn = findViewById(R.id.test_loginbtn);
        testTestbtn = findViewById(R.id.test_testbtn);
        testLoginbtn.setOnClickListener(this);
        testTestbtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.test_loginbtn:
                String name = testName.getText().toString().trim();
                String pwd = testPwd.getText().toString().trim();
                doLoginData(name, pwd);
                break;
            case R.id.test_testbtn:
//                final TimePickerView timePickerView = new TimePickerView(TestLogin.this);
//                timePickerView.show();
//                timePickerView.setTimepListener(new TimePickerView.OnTimePickerViewClickListener() {
//
//                    @Override
//                    public void doSure(String dateTime) {
//                        ToastUtil.showToast(TestLogin.this,dateTime+"ss");
//                        timePickerView.dismiss();
//                    }
//                });
                TimePickerDialog timePickerDialog = new TimePickerDialog(TestLogin.this, new TimePickerDialog.OnTimeSetListener() {
                    @SuppressLint("NewApi")
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        Log.e("TestLogin","--------------------"+timePicker.getHour()+":"+timePicker.getMinute());
                    }
                }, calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),true);
                timePickerDialog.show();
                break;
        }
    }

    private void doLoginData(final String name, final String pwd) {
        Log.e("TestLogin", "----userid----" + SharedPreferencesUtils.readObject(this, "userId"));
        JSONObject jsonObject = new JSONObject();
        Map<String, String> map = new HashMap<>();
        map.put("phone", name);
        map.put("pwd", Md5Util.Md532(pwd));
        try {
            //用户登录
//            jsonObject.put("phone",name);
//            jsonObject.put("pwd",Md5Util.Md532(pwd));
            //查询用户信息6c7a72f9c9334673a393c220aa48e5f5
//            jsonObject.put("userId", "6c7a72f9c9334673a393c220aa48e5f5");
//            jsonObject.put("deviceCode", "CB:FC:54:D5:C6:A4");
            // jsonObject.put("date","2017-07-19");

            jsonObject.put("userId", "a71cac2eec714c8397df7d5a962f1925");
            jsonObject.put("deviceCode", "3F:40:04:10:A8:4F");
            jsonObject.put("startDate", "2017-07-13");
            jsonObject.put("endDate","2017-07-20");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        String dataStepUrl = URLs.HTTPs + URLs.GET_WATCH_DATA_DATA;
        String tojson = new Gson().toJson(map);
        String queryUserData = URLs.HTTPs + URLs.getUserInfo; //查询用户信息
        String loginData = URLs.HTTPs + URLs.logon; //用户登录
        String dataStep = URLs.HTTPs + URLs.getSportH;  //获取当日运动步数
        String getMyUsinfo = URLs.HTTPs + URLs.myInfo;  //达标总数

        final RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        JsonRequest<JSONObject> jsonObjectJsonRequest = new JsonObjectRequest(Request.Method.POST, dataStepUrl, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("TestLogin", "---11---response---" + response.toString());
                if (response != null) {
                    Log.e("TestLogin", "---22---response---" + response.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TestLogin", "------error---" + error.getMessage());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json; charset=UTF-8");
                return headers;
            }
        };
        requestQueue.add(jsonObjectJsonRequest);

    }



}

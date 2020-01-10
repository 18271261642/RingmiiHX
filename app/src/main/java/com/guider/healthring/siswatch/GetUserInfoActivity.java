package com.guider.healthring.siswatch;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.google.gson.Gson;
import com.guider.healthring.Commont;
import com.guider.healthring.MyApp;
import com.guider.healthring.bean.UserInfoBean;
import com.guider.healthring.util.SharedPreferencesUtils;
import com.guider.healthring.util.URLs;
import com.guider.healthring.w30s.utils.httputils.RequestPressent;
import com.guider.healthring.w30s.utils.httputils.RequestView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2017/11/1.
 */

public class GetUserInfoActivity extends WatchBaseActivity implements RequestView {

    private RequestPressent requestPressent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestPressent = new RequestPressent();
        requestPressent.attach(this);


        sendRecordApp();
    }


    //获取用户信息
    private void sendRecordApp() {
        String userId = (String) SharedPreferencesUtils.readObject(GetUserInfoActivity.this, "userId");
        if (userId == null)
            return;
        if (requestPressent != null) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("userId", userId);
                requestPressent.getRequestJSONObject(0x01, Commont.FRIEND_BASE_URL + URLs.getUserInfo,
                        this, jsonObject.toString(), 1);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }



    @Override
    public void showLoadDialog(int what) {

    }

    @Override
    public void successData(int what, Object object, int daystag) {
        if(object == null)
            return;
        try {
            JSONObject jsonObject = new JSONObject(object.toString());
            if(!jsonObject.has("code"))
                return;
            if(jsonObject.getInt("code") == 200){
                if(what == 0x01){
                    analysisUserInfo(jsonObject);
                }
            }


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void failedData(int what, Throwable e) {

    }

    @Override
    public void closeLoadDialog(int what) {

    }

    private void analysisUserInfo(JSONObject jsonObject) {
        try {
            String data = jsonObject.getString("data");
            UserInfoBean userInfoBean = new Gson().fromJson(data, UserInfoBean.class);
            if(userInfoBean != null){
                SharedPreferencesUtils.saveObject(GetUserInfoActivity.this, Commont.USER_ID_DATA, userInfoBean.getUserid());
                //保存一下用户性别
                SharedPreferencesUtils.setParam(GetUserInfoActivity.this,Commont.USER_SEX,userInfoBean.getSex());
                String height = userInfoBean.getHeight();
                if (height.contains("cm")) {
                    String newHeight = height.substring(0, height.length() - 2);
                    SharedPreferencesUtils.setParam(GetUserInfoActivity.this, Commont.USER_HEIGHT, newHeight.trim());
                } else {
                    SharedPreferencesUtils.setParam(GetUserInfoActivity.this, Commont.USER_HEIGHT, height.trim());
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}

package com.guider.baselib.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;

import com.guider.baselib.device.standard.Constant;
import com.guider.baselib.utils.DateUtil;
import com.guider.health.apilib.ApiCallBack;
import com.guider.health.apilib.ApiUtil;
import com.guider.health.apilib.IGuiderApi;
import com.guider.health.apilib.model.DoctorInfo;
import com.guider.health.apilib.model.UserInfo;
import com.orhanobut.logger.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by haix on 2019/6/21.
 */

public class UserManager {

    private static UserManager userManager = new UserManager();

    public static UserManager getInstance() {
        Log.i("uuuuser", userManager + "--实例");
        return userManager;
    }

    private int accountId = 0;

    public String getDotorAccountId() {
        return dotorAccountId;
    }

    public void setDotorAccountId(String dotorAccountId) {
        this.dotorAccountId = dotorAccountId;
    }

    public int getAccountId() {
        if (Constant.isDebugOfUser) {
            return 859;
        }
        return accountId;
    }

    public void setAccountId(int accountId) {
        Logger.i("设置accountId...." + accountId);
        this.accountId = accountId;
    }

    private String dotorAccountId = "0";
    private String doctorPhone;
    private String doctorPass;
    private String token = null;
    private Bitmap bitmap;
    private String name;
    private String sex;
    private String nation;
    private String birth;///1999-22-22
    private String addr;
    private String idcode;
    private String issue;
    private String beginData;
    private String endData;
    private String cardId;
    private String headUrl;
    private String phone;

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getHeadUrl() {
        return headUrl;
    }

    public void setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }

    public String getBirth() {
        return TextUtils.isEmpty(birth) ? "1970-01-01" : birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getIdcode() {
        return idcode;
    }

    public void setIdcode(String idcode) {
        this.idcode = idcode;
    }

    public String getIssue() {
        return issue;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }

    public String getBeginData() {
        return beginData;
    }

    public void setBeginData(String beginData) {
        this.beginData = beginData;
    }

    public String getEndData() {
        return endData;
    }

    public void setEndData(String endData) {
        this.endData = endData;
    }

    private int id = 0;

    private int height = 0;
    private int weight = 0;

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        Log.i("uuuuser", weight + "--改变体重");
        this.weight = weight;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    private DoctorInfo doctorInfo;

    public DoctorInfo getDoctorInfo() {
        return doctorInfo;
    }

    public UserManager setDoctorInfo(DoctorInfo doctorInfo) {
        this.doctorInfo = doctorInfo;
        return this;
    }

    public String getDoctorPhone() {
        return doctorPhone;
    }

    public String getDoctorPass() {
        return doctorPass;
    }

    public void init(JSONObject data) {
        if (data == null) {
            return;
        }
        try {
            UserManager.getInstance().setId(data.getInt("id"));
            UserManager.getInstance().setHeight(data.getInt("height"));
            UserManager.getInstance().setWeight(data.getInt("weight"));
            UserManager.getInstance().setAccountId(data.getInt("accountId"));

            String name = data.getString("name");
            if (name != null) {
                UserManager.getInstance().setName(name);
            } else {
                UserManager.getInstance().setName("");
            }

            String gender = data.getString("gender");
            if (gender != null) {
                if ("MAN".equals(gender)) {
                    gender = "男";
                } else {
                    gender = "女";
                }
                UserManager.getInstance().setSex(gender);
            } else {
                UserManager.getInstance().setSex("");
            }

            String birthday = data.getString("birthday");
            if (birthday != null) {
                if (birthday.length() > 10) {
                    birthday = birthday.substring(0, 10);
                }
                UserManager.getInstance().setBirth(birthday);
            } else {
                UserManager.getInstance().setBirth("");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getUserInfoOnServer(final Context context) {
        ApiUtil.createApi(IGuiderApi.class , false).getUserInfo(getAccountId() + "")
                .enqueue(new ApiCallBack<UserInfo>(context) {
                    @Override
                    public void onApiResponse(Call<UserInfo> call, Response<UserInfo> response) {
                        super.onApiResponse(call, response);
                        UserInfo body = response.body();
                        if (body != null) {
                            // 用户信息获取成功
                            setName(body.getName());
                            setSex("男".equals(body.getGender()) || "MAN".equals(body.getGender()) ? "男" : "女");
                            setBirth(body.getBirthday());
                            setWeight(body.getWeight());
                            setHeight(body.getHeight());
                        }
                    }
                });
    }

    public void synchronizeInfo(final Context context) {
        if (getAccountId() >= 0) {
            Logger.i("同步信息");
            ApiUtil.createApi(IGuiderApi.class , false).getUserInfo(getAccountId() + "")
                    .enqueue(new ApiCallBack<UserInfo>(context) {
                        @Override
                        public void onApiResponse(Call<UserInfo> call, Response<UserInfo> response) {
                            super.onApiResponse(call, response);
                            UserInfo body = response.body();
                            if (body != null) {
                                // 用户信息获取成功
//                                name = body.getName();
//                                sex = body.getGender().equals("MAN") ? "男" : "女";
//                                birth = body.getBirthday();
//                                weight = body.getWeight();
//                                height = body.getHeight();
                                Logger.i("获取信息" + body.getWeight() + "");
                                _synchroniseInfo(context , body);
                            }
                        }
                    });
        }
    }

    private void _synchroniseInfo(final Context context , UserInfo userInfo) {
        try {
            if (!TextUtils.isEmpty(name)) {
                userInfo.setName(name);
            }
            if (!TextUtils.isEmpty(birth)) {
                userInfo.setBirthday(DateUtil.
                        getDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm")
                                .parse(birth + " 08:00")));
            }
            if (weight > 0) {
                userInfo.setWeight(weight);
            }
            if (height > 0) {
                userInfo.setHeight(height);
            }
            if (!TextUtils.isEmpty(sex)) {
                userInfo.setGender("男".equals(sex) || "MAN".equals(sex) ? "MAN" : "WOMAN");
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Logger.i("开始同步信息" + userInfo.getWeight() + "");
        ApiUtil.createApi(IGuiderApi.class , false).simpUserInfo(userInfo)
                .enqueue(new ApiCallBack<String>(context){
                    @Override
                    public void onApiResponse(Call<String> call, Response<String> response) {
                        super.onApiResponse(call, response);
                        Logger.i("用户信息同步成功" + response.body());
//                        Toast.makeText(context, "用户信息同步成功", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}

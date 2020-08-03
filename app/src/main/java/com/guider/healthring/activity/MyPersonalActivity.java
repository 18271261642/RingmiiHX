package com.guider.healthring.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.FileProvider;

import com.aigestudio.wheelpicker.widgets.DatePick;
import com.aigestudio.wheelpicker.widgets.ProfessionPick;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.flipboard.bottomsheet.BottomSheetLayout;
import com.flipboard.bottomsheet.commons.MenuSheetView;
import com.google.gson.Gson;
import com.guider.health.apilib.BuildConfig;
import com.guider.healthring.Commont;
import com.guider.healthring.MyApp;
import com.guider.healthring.R;
import com.guider.healthring.b30.view.B30SkinColorView;
import com.guider.healthring.bean.GuiderUserInfo;
import com.guider.healthring.bean.User;
import com.guider.healthring.bean.UserInfoBean;
import com.guider.healthring.imagepicker.PickerBuilder;
import com.guider.healthring.net.OkHttpObservable;
import com.guider.healthring.rxandroid.CommonSubscriber;
import com.guider.healthring.rxandroid.DialogSubscriber;
import com.guider.healthring.rxandroid.SubscriberOnNextListener;
import com.guider.healthring.siswatch.WatchBaseActivity;
import com.guider.healthring.siswatch.utils.Base64BitmapUtil;
import com.guider.healthring.siswatch.utils.FileOperUtils;
import com.guider.healthring.siswatch.utils.WatchUtils;
import com.guider.healthring.util.ImageTool;
import com.guider.healthring.util.LocalizeTool;
import com.guider.healthring.util.MaterialDialogUtil;
import com.guider.healthring.util.OkHttpTool;
import com.guider.healthring.util.SharedPreferencesUtils;
import com.guider.healthring.util.ToastUtil;
import com.guider.healthring.util.URLs;
import com.guider.healthring.w30s.utils.httputils.RequestPressent;
import com.guider.healthring.w30s.utils.httputils.RequestView;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
import com.veepoo.protocol.listener.data.ICustomSettingDataListener;
import com.veepoo.protocol.model.enums.EFunctionStatus;
import com.veepoo.protocol.model.settings.CustomSetting;
import com.veepoo.protocol.model.settings.CustomSettingData;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

/**
 * Created by thinkpad on 2017/3/8.
 * 个人信息
 */

public class MyPersonalActivity extends WatchBaseActivity implements RequestView, View.OnClickListener {

    private static final String TAG = "MyPersonalActivity";

    private static final int GET_CAMERA_REQUEST_CODE = 1001;

    private final static String SKIN_COLOR_KEY = "skin_position";


    //    @BindView(R.id.tv_title)
//    TextView tvTitle;
    CircleImageView mineLogoIv;
    TextView nicknameTv;
    TextView sexTv;
    TextView heightTv;
    TextView weightTv;
    TextView birthdayTv;
    BottomSheetLayout bottomSheetLayout;
    RelativeLayout personalAvatarRelayout;
    RelativeLayout nicknameRelayoutPersonal;
    RelativeLayout sexRelayout;
    RelativeLayout heightRelayout;
    RelativeLayout weightRelayout;
    RelativeLayout birthdayRelayout;
    //单位设置的RadioGroup
    RadioGroup radioGroupUnti;
    //单位设置的布局
    LinearLayout personalUnitLin;
    RadioButton radioKm;
    RadioButton radioMi;
    TextView guiderUserInfoTv;
    //显示的肤色
    ImageView defaultSkinColorImg;
    private String nickName, sex, height, weight, birthday, flag;
    private DialogSubscriber dialogSubscriber;
    private boolean isSubmit;

    private CommonSubscriber commonSubscriber;
    private SubscriberOnNextListener subscriberOnNextListener;
    private ArrayList<String> heightList;
    private ArrayList<String> weightList;

    private int userSex = 1;
    private int userHeight = 170;
    private int userWeitht = 60;


    boolean w30sunit = true;
    private String bleMac;
    private String urlImagePath = "";//上传图片后的返回地址

    /**
     * 本地化帮助类
     */
    private LocalizeTool mLocalTool;
    /**
     * 请求回来的参数,或者要提交的
     */
    private UserInfo mUserInfo = null;
    /**
     * Json帮助类
     */
    private Gson gson = new Gson();
    private RequestQueue requestQueue;
    private RequestPressent requestPressent;


    private Uri mCutUri;


    //肤色选择的view
    private B30SkinColorView b30SkinColorView;


    //盖德user
    private GuiderUserInfo guiderUserInfo = null;


    private UserInfoBean userInfoBean = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);
        initViewIds();

        initViews();


        initData();
    }

    private void initViewIds() {
        mineLogoIv = findViewById(R.id.mine_logo_iv_personal);
        nicknameTv = findViewById(R.id.nickname_tv);
        sexTv = findViewById(R.id.sex_tv);
        heightTv = findViewById(R.id.height_tv);
        weightTv = findViewById(R.id.weight_tv);
        birthdayTv = findViewById(R.id.birthday_tv);
        bottomSheetLayout = findViewById(R.id.bottomsheet);
        personalAvatarRelayout = findViewById(R.id.personal_avatar_relayout);
        nicknameRelayoutPersonal = findViewById(R.id.nickname_relayout_personal);
        sexRelayout = findViewById(R.id.sex_relayout);
        heightRelayout = findViewById(R.id.height_relayout);
        weightRelayout = findViewById(R.id.weight_relayout);
        birthdayRelayout = findViewById(R.id.birthday_relayout);
        radioGroupUnti = findViewById(R.id.radioGroup_unti);
        personalUnitLin = findViewById(R.id.personal_UnitLin);
        radioKm = findViewById(R.id.radio_km);
        radioMi = findViewById(R.id.radio_mi);
        guiderUserInfoTv = findViewById(R.id.guiderUserInfoTv);
        defaultSkinColorImg = findViewById(R.id.defaultSkinColorImg);
        personalAvatarRelayout.setOnClickListener(this);
        findViewById(R.id.nickname_relayout_personal).setOnClickListener(this);
        sexRelayout.setOnClickListener(this);
        heightRelayout.setOnClickListener(this);
        weightRelayout.setOnClickListener(this);
        birthdayRelayout.setOnClickListener(this);
        findViewById(R.id.skinColorRel).setOnClickListener(this);
    }

    private void initData() {
        requestQueue = NoHttp.newRequestQueue(1);
        requestPressent = new RequestPressent();
        requestPressent.attach(this);

        //获取用户数据
        getUserInfoData();


        getGadiDeUserInfoData();
    }

    //获取盖德的用户信息
    private void getGadiDeUserInfoData() {
        try {
            long accountId = (long) SharedPreferencesUtils.getParam(MyPersonalActivity.this, "accountIdGD", 0L);
            if (accountId == 0)
                return;
            String guiderUrl = BuildConfig.APIURL + "api/v1/userinfo?accountId=" + accountId; // http://api.guiderhealth.com/
            if (requestPressent != null) {
                requestPressent.getRequestJSONObjectGet(11, guiderUrl, this, 11);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (requestPressent != null)
            requestPressent.detach();
    }


    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences share = getSharedPreferences("nickName", 0);
        String name = share.getString("name", "");
        if (!WatchUtils.isEmpty(name)) {
            nicknameTv.setText(name);
        }
    }


    private void initViews() {
//        tvTitle.setText(R.string.personal_info);
        findViewById(R.id.personal_info_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        findViewById(R.id.personal_info_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                savePersonData();
            }
        });
        heightList = new ArrayList<>();
        weightList = new ArrayList<>();
        bleMac = (String) SharedPreferencesUtils.readObject(MyPersonalActivity.this, "mylanya");
        if (!WatchUtils.isEmpty(bleMac) && bleMac.equals("bozlun")) {    //H8手表
            personalUnitLin.setVisibility(View.VISIBLE);
        } else {
            personalUnitLin.setVisibility(View.GONE);
        }
        radioGroupUnti.setOnCheckedChangeListener(new MyCheckChangeListener());
        mLocalTool = new LocalizeTool(this);
        w30sunit = mLocalTool.getMetricSystem();
        if (w30sunit) {
            radioKm.setChecked(true);
            radioMi.setChecked(false);
        } else {
            radioKm.setChecked(false);
            radioMi.setChecked(true);
        }


        //设置选择列表
        setListData();

        //肤色显示
        showSkinColorData();


        subscriberOnNextListener = (SubscriberOnNextListener<String>) result -> {
            if (WatchUtils.isEmpty(result)) return;
            try {
                JSONObject jsonObject = new JSONObject(result);
                String resultCode = jsonObject.getString("resultCode");
                System.out.print("resultCode" + resultCode);
                Log.e("MyPerson", "----resultCode--" + resultCode + "-isSubmit----" + result);
                if ("001".equals(resultCode)) {
                    //urlImagePath = jsonObject.getString("url");
                    Log.e("MyPerson", "----resultCode--" +
                            resultCode + "-urlImagePath----" + urlImagePath);
                    //ToastUtil.showShort(MyPersonalActivity.this, getString(R.string.submit_success));
                } else {
                    ToastUtil.showShort(MyPersonalActivity.this,
                            getString(R.string.submit_fail));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        };


        AndPermission.with(MyPersonalActivity.this)
                .runtime()
                .permission(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .onGranted(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {

                    }
                })
                .onDenied(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {

                    }
                }).start();

    }

    //显示肤色
    private void showSkinColorData() {
        int colorPosition = (int) SharedPreferencesUtils.getParam(
                MyPersonalActivity.this, SKIN_COLOR_KEY, 2);
        defaultSkinColorImg.setImageResource(B30SkinColorView.imgStr[colorPosition]);
    }

    private void setListData() {
        heightList.clear();
        weightList.clear();
        if (w30sunit) {     //公制
            for (int i = 120; i < 231; i++) {
                heightList.add(i + " cm");
            }
            for (int i = 20; i < 200; i++) {
                weightList.add(i + " kg");
            }
        } else {  // 英制
            for (int i = 44; i < 100; i++) {
                heightList.add(i + " in");
            }
            for (int i = 20; i < 220; i++) {
                weightList.add(i + " lb");
            }
        }
    }


    // 获取用户数据
    private void getUserInfoData() {
        String url = Commont.FRIEND_BASE_URL + URLs.getUserInfo;
        if (requestPressent != null) {
            HashMap<String, String> map = new HashMap<>();
            map.put("userId", (String) SharedPreferencesUtils.readObject(
                    MyPersonalActivity.this, "userId"));
            String mapJson = gson.toJson(map);
            requestPressent.getRequestJSONObject(0x01, url,
                    this, mapJson, 11);
        }
    }

    /**
     * 修改用户数据
     */
    private void savePersonData() {
        if (mUserInfo == null) return;
        String mapjson = gson.toJson(mUserInfo);
        dialogSubscriber = new DialogSubscriber(subscriberOnNextListener, this);
        OkHttpObservable.getInstance().getData(dialogSubscriber,
                URLs.HTTPs + URLs.yonghuziliao, mapjson);
    }

    /**
     * 刷新所以得数据（名字和头像）
     */
    public void shuaxin() {
        isSubmit = false;
        HashMap<String, String> map = new HashMap<>();
        map.put("userId", (String) SharedPreferencesUtils.readObject(
                MyPersonalActivity.this, "userId"));
        String mapjson = gson.toJson(map);
        commonSubscriber = new CommonSubscriber(subscriberOnNextListener, this);
        OkHttpObservable.getInstance().getData(commonSubscriber,
                URLs.HTTPs + URLs.getUserInfo, mapjson);
    }

    @Override
    public void onClick(View view) {
        String userId = (String) SharedPreferencesUtils.readObject(MyPersonalActivity.this, "userId");
        SharedPreferences share = getSharedPreferences("Login_id", 0);
        int isoff = share.getInt("id", 0);
        if (!WatchUtils.isEmpty(userId)) {
            switch (view.getId()) {
                case R.id.personal_avatar_relayout://  修改头像
                    if (guiderUserInfo == null) return;
                    if (AndPermission.hasAlwaysDeniedPermission(
                            MyPersonalActivity.this, Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        chooseImgForUserHead(); //选择图片来源
                    } else {
                        AndPermission.with(MyPersonalActivity.this)
                                .runtime()
                                .permission(Manifest.permission.CAMERA,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                .onGranted(new Action<List<String>>() {
                                    @Override
                                    public void onAction(List<String> data) {

                                    }
                                })
                                .onDenied(new Action<List<String>>() {
                                    @Override
                                    public void onAction(List<String> data) {

                                    }
                                }).start();

                    }
                    break;
                case R.id.nickname_relayout_personal:
                    // if (mUserInfo == null) return;
                    if (guiderUserInfo == null) return;
                    Intent nameIntent = new Intent(
                            MyPersonalActivity.this, ModifyNickNameActivity.class);
                    nameIntent.putExtra("name", nicknameTv.getText().toString());


                    startActivityForResult(nameIntent, 1000);
                    break;
                case R.id.sex_relayout:
                    //if (mUserInfo == null) return;
                    if (guiderUserInfo == null) return;
                    showSexDialog();
                    break;
                case R.id.height_relayout:      // 身高
                    //if (mUserInfo == null) return;
                    if (guiderUserInfo == null) return;
                    String userH = heightTv.getText().toString();
                    if (w30sunit) { //公制
                        ProfessionPick professionPopWin = new ProfessionPick.Builder(
                                MyPersonalActivity.this, profession -> {
                                    heightTv.setText(profession);
                                    guiderUserInfo.setHeight(
                                            Integer.parseInt(profession.substring(0, 3).trim()));
                                    updateGuiderUserInfo(guiderUserInfo);
                                    if (mUserInfo != null) {
                                        mUserInfo.height = profession.substring(0, 3);// 记录一下提交要用
                                        flag = "height";
    //                                    heightTv.setText(profession);
    //                                    height = profession.substring(0, 3);
                                        String uHeight = profession.substring(0, 3).trim();
                                        modifyPersonData(uHeight);
                                    }
                                }).textConfirm(getResources().getString(R.string.confirm)) //text of confirm button
                                .textCancel(getResources().getString(R.string.cancle))
                                .btnTextSize(16) // button text size
                                .viewTextSize(25) // pick view text size
                                .colorCancel(Color.parseColor("#999999")) //color of cancel button
                                .colorConfirm(Color.parseColor("#009900"))//color of confirm button
                                .setProvinceList(heightList) //min year in loop
                                .dateChose(StringUtils.substringBefore(userH,
                                        "cm") + " cm") // date chose when init popwindow
                                .build();
                        professionPopWin.showPopWin(MyPersonalActivity.this);
                    } else {      //英制
                        ProfessionPick professionPopWin = new ProfessionPick.Builder(
                                MyPersonalActivity.this, profession -> {
                                    heightTv.setText(profession);
                                    guiderUserInfo.setHeight(Integer.parseInt(
                                            profession.substring(0, 3).trim()));
                                    heightTv.setText(profession);
                                    String tmpHeight = StringUtils.substringBefore(profession,
                                            "in").trim();
    //                                    Log.e(TAG, "---tmpHeight--" + tmpHeight);
                                    flag = "height";
                                    //height = profession.substring(0, 3);
                                    //1,英寸转cm
                                    double tmpCal = WatchUtils.mul(Double.valueOf(tmpHeight), 2.5);
                                    //截取小数点前的数据
                                    int beforeTmpCal = Integer.parseInt(
                                            StringUtils.substringBefore(String.valueOf(tmpCal),
                                                    ".").trim());
                                    //截取小数点后的数据
                                    String afterTmpCal = StringUtils.substringAfter(
                                            String.valueOf(tmpCal), ".").trim();
                                    //判断小数点后一位是否》=5
                                    int lastAterTmpCal = Integer.parseInt(
                                            afterTmpCal.length() >= 1 ? afterTmpCal.substring(0, 1) : "0");
    //                                    Log.e(TAG, "----lastAterTmpCal--=" + lastAterTmpCal);
                                    if (lastAterTmpCal >= 5) {
                                        guiderUserInfo.setHeight((beforeTmpCal + 1));
                                        if (mUserInfo != null) {
                                            mUserInfo.height = (beforeTmpCal + 1) + "";
                                        }

                                    } else {
                                        guiderUserInfo.setHeight(beforeTmpCal);
                                        if (mUserInfo != null) {
                                            mUserInfo.height = beforeTmpCal + "";
                                        }

                                    }
                                    updateGuiderUserInfo(guiderUserInfo);
                                    if (mUserInfo != null) {
                                        modifyPersonData(mUserInfo.height);
                                    }
    //                                    Log.e(TAG, "---tmpHeight-height-" + height);

                                }).textConfirm(getResources().getString(R.string.confirm)) //text of confirm button
                                .textCancel(getResources().getString(R.string.cancle))
                                .btnTextSize(16) // button text size
                                .viewTextSize(25) // pick view text size
                                .colorCancel(Color.parseColor("#999999")) //color of cancel button
                                .colorConfirm(Color.parseColor("#009900"))//color of confirm button
                                .setProvinceList(heightList) //min year in loop
                                .dateChose(heightTv.getText().toString()) // date chose when init popwindow
                                .build();
                        professionPopWin.showPopWin(MyPersonalActivity.this);
                    }

                    break;
                case R.id.weight_relayout:  //体重
                    //if (mUserInfo == null) return;
                    if (guiderUserInfo == null) return;
                    String uWeight = weightTv.getText().toString();
                    if (w30sunit) { //公制
                        ProfessionPick weightPopWin = new ProfessionPick.Builder(
                                MyPersonalActivity.this, profession -> {
                                    weightTv.setText(profession);

                                    guiderUserInfo.setWeight(Integer.parseInt(
                                            profession.substring(0, 3).trim()));

                                    updateGuiderUserInfo(guiderUserInfo);
                                    if (mUserInfo != null) {
                                        mUserInfo.weight = profession.substring(0, 3);// 记录一下提交要用
                                        flag = "weight";
                                        weight = profession.substring(0, 3);
                                        modifyPersonData(weight);
                                    }

                                }).textConfirm(getResources().getString(R.string.confirm)) //text of confirm button
                                .textCancel(getResources().getString(R.string.cancle))
                                .btnTextSize(16) // button text size
                                .viewTextSize(25) // pick view text size
                                .colorCancel(Color.parseColor("#999999")) //color of cancel button
                                .colorConfirm(Color.parseColor("#009900"))//color of confirm button
                                .setProvinceList(weightList) //min year in loop
                                .dateChose(StringUtils.substringBefore(uWeight,
                                        "kg") + " kg") // date chose when init popwindow
                                .build();
                        weightPopWin.showPopWin(MyPersonalActivity.this);
                    } else {
                        //英制体重
                        ProfessionPick weightPopWin = new ProfessionPick.Builder(
                                MyPersonalActivity.this, profession -> {
                                    weightTv.setText(profession);
                                    flag = "weight";
                                    String tmpWeid = StringUtils.substringBefore(
                                            profession, "lb").trim();
                                    double calWeid = WatchUtils.mul(Double.valueOf(tmpWeid), 0.454);
                                    //截取小数点前的数据
                                    String beforeCalWeid =
                                            StringUtils.substringBefore(
                                                    String.valueOf(calWeid), ".");
                                    //截取后小数点后的数据
                                    String afterCalWeid = StringUtils.substringAfter(
                                            String.valueOf(calWeid), ".");
                                    int lastNum = Integer.parseInt(
                                            afterCalWeid.length() >= 1 ? afterCalWeid.substring(0, 1) : "0");
    //                                    Log.e(TAG, "----lastNum=" + lastNum);
                                    //判断小数点后一位是否大于5
                                    if (lastNum >= 5) {
                                        guiderUserInfo.setWeight(
                                                Integer.parseInt(beforeCalWeid.trim()) + 1);
                                        if (mUserInfo != null) {
                                            mUserInfo.weight = String.valueOf(
                                                    Integer.parseInt(beforeCalWeid.trim()) + 1);
                                        }

                                    } else {
                                        guiderUserInfo.setWeight(
                                                Integer.parseInt(beforeCalWeid.trim()));
                                        if (mUserInfo != null) {
                                            mUserInfo.weight = beforeCalWeid.trim();
                                        }

                                    }
                                    if (mUserInfo != null) {
                                        modifyPersonData(mUserInfo.weight);
                                    }
                                    // weight = profession.substring(0, 3);
                                    updateGuiderUserInfo(guiderUserInfo);
                                }).textConfirm(getResources().getString(R.string.confirm)) //text of confirm button
                                .textCancel(getResources().getString(R.string.cancle))
                                .btnTextSize(16) // button text size
                                .viewTextSize(25) // pick view text size
                                .colorCancel(Color.parseColor("#999999")) //color of cancel button
                                .colorConfirm(Color.parseColor("#009900"))//color of confirm button
                                .setProvinceList(weightList) //min year in loop
                                .dateChose(weightTv.getText().toString()) // date chose when init popwindow
                                .build();
                        weightPopWin.showPopWin(MyPersonalActivity.this);
                    }

                    break;
                case R.id.birthday_relayout:    //生日
                    final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);

                    //if (mUserInfo == null) return;
                    if (guiderUserInfo == null) return;
                    DatePick pickerPopWin = new DatePick.Builder(
                            MyPersonalActivity.this, (year, month, day, dateDesc) -> {
                                birthdayTv.setText(dateDesc);
                                guiderUserInfo.setBirthday(dateDesc + "T00:00:00Z");
                                updateGuiderUserInfo(guiderUserInfo);
                                if (mUserInfo != null) {
                                    mUserInfo.birthday = dateDesc;
                                    flag = "birthday";
                                    birthday = dateDesc;
                                    modifyPersonData(dateDesc);//
                                }

                            }).textConfirm(getResources().getString(R.string.confirm)) //text of confirm button
                            .textCancel(getResources().getString(R.string.cancle)) //text of cancel button
                            .btnTextSize(16) // button text size
                            .viewTextSize(25) // pick view text size
                            .colorCancel(Color.parseColor("#999999")) //color of cancel button
                            .colorConfirm(Color.parseColor("#009900"))//color of confirm button
                            .minYear(1900) //min year in loop
                            .maxYear(2050) // max year in loop
                            .dateChose(birthdayTv.getText().toString()) // date chose when init popwindow
                            .build();
                    pickerPopWin.showPopWin(MyPersonalActivity.this);


                    break;
                case R.id.skinColorRel: //肤色选择
                    b30SkinColorView = new B30SkinColorView(this);
                    b30SkinColorView.show();
                    b30SkinColorView.setB30SkinColorListener(
                            new B30SkinColorView.B30SkinColorListener() {
                        @Override
                        public void doSureSkinClick(int selectImgId, int position) {
                            b30SkinColorView.dismiss();
                            defaultSkinColorImg.setImageResource(selectImgId);

                            //保存选择的肤色下标
                            SharedPreferencesUtils.setParam(MyPersonalActivity.this,
                                    SKIN_COLOR_KEY, position);
                            if (position == 4 || position == 5) {
                                setSwtchStutas(false);
                            } else {
                                setSwtchStutas(true);
                            }

                        }

                        @Override
                        public void doCancleSkinClick() {
                            b30SkinColorView.dismiss();
                        }
                    });
                    break;
            }

        }

    }


    /**
     * 黑色皮肤关闭检测
     */
    EFunctionStatus isFindePhone = EFunctionStatus.SUPPORT_CLOSE;//控制查找手机UI
    EFunctionStatus isStopwatch = EFunctionStatus.SUPPORT_CLOSE;////秒表
    EFunctionStatus isWear = EFunctionStatus.SUPPORT_CLOSE;//佩戴检测
    EFunctionStatus isCallPhone = EFunctionStatus.SUPPORT_CLOSE;//来电
    EFunctionStatus isHelper = EFunctionStatus.SUPPORT_CLOSE;//SOS 求救
    EFunctionStatus isDisAlert = EFunctionStatus.SUPPORT_CLOSE;//断开
    boolean isSystem = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(),
            Commont.ISSystem, true);//是否为公制
    boolean is24Hour = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(),
            Commont.IS24Hour, true);//是否为24小时制
    boolean isAutomaticHeart = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(),
            Commont.ISAutoHeart, true);//自动测量心率
    boolean isAutomaticBoold = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(),
            Commont.ISAutoBp, true);//自动测量血压
    boolean isSecondwatch = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(),
            Commont.ISSecondwatch, true);//秒表
    boolean isWearCheck = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(),
            Commont.ISWearcheck, true);//佩戴
    boolean isFindPhone = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(),
            Commont.ISFindPhone, false);//查找手机
    boolean CallPhone = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(),
            Commont.ISCallPhone, true);//来电
    boolean isDisconn = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(),
            Commont.ISDisAlert, false);//断开连接提醒
    boolean isSos = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(),
            Commont.ISHelpe, false);//sos


    private void setSwtchStutas(boolean isOpen) {
        //查找手机
        if (isFindPhone) {
            isFindePhone = EFunctionStatus.SUPPORT_OPEN;
        } else {
            isFindePhone = EFunctionStatus.SUPPORT_CLOSE;
        }

        //秒表
        if (isSecondwatch) {
            isStopwatch = EFunctionStatus.SUPPORT_OPEN;
        } else {
            isStopwatch = EFunctionStatus.SUPPORT_CLOSE;
        }


        //佩戴检测
        if (isOpen) {
            SharedPreferencesUtils.setParam(MyApp.getContext(),
                    Commont.ISWearcheck, true);//佩戴
            isWear = EFunctionStatus.SUPPORT_OPEN;
        } else {
            SharedPreferencesUtils.setParam(MyApp.getContext(),
                    Commont.ISWearcheck, false);//佩戴
            isWear = EFunctionStatus.SUPPORT_CLOSE;
        }
        //来电
        if (CallPhone) {
            isCallPhone = EFunctionStatus.SUPPORT_OPEN;
        } else {
            isCallPhone = EFunctionStatus.SUPPORT_CLOSE;
        }
        //断开
        if (isDisconn) {
            isDisAlert = EFunctionStatus.SUPPORT_OPEN;
        } else {
            isDisAlert = EFunctionStatus.SUPPORT_CLOSE;
        }
        //sos
        if (isSos) {
            isHelper = EFunctionStatus.SUPPORT_OPEN;
        } else {
            isHelper = EFunctionStatus.SUPPORT_CLOSE;
        }

        Log.i("bbbbbbbbo", "MyPersonalActivity");
        showLoadingDialog(getResources().getString(R.string.dlog));
        new Handler().postDelayed(() -> {
            MyApp.getInstance().getVpOperateManager().changeCustomSetting(i -> {

            }, new ICustomSettingDataListener() {
                @Override
                public void OnSettingDataChange(CustomSettingData customSettingData) {
//                        Log.e(TAG, " ===  开关改变 " + customSettingData.toString());
                    closeLoadingDialog();


                }
            }, new CustomSetting(true,//isHaveMetricSystem
                    isSystem, //isMetric
                    is24Hour,//is24Hour
                    isAutomaticHeart, //isOpenAutoHeartDetect
                    isAutomaticBoold,//isOpenAutoBpDetect
                    EFunctionStatus.UNSUPPORT,//isOpenSportRemain
                    EFunctionStatus.UNSUPPORT,//isOpenVoiceBpHeart
                    isFindePhone,//isOpenFindPhoneUI
                    isStopwatch,//isOpenStopWatch
                    EFunctionStatus.UNSUPPORT,//isOpenSpo2hLowRemind
                    isWear,//isOpenWearDetectSkin
                    isCallPhone,//isOpenAutoInCall
                    EFunctionStatus.UNKONW,//isOpenAutoHRV
                    isDisAlert,//isOpenDisconnectRemind
                    isHelper));//isOpenSOS


        }, 200);
    }


    //选择图片
    private void chooseImgForUserHead() {
        MenuSheetView menuSheetView =
                new MenuSheetView(MyPersonalActivity.this,
                        MenuSheetView.MenuType.LIST, R.string.select_photo, item -> {
                            if (bottomSheetLayout.isSheetShowing()) {
                                bottomSheetLayout.dismissSheet();
                            }
                            switch (item.getItemId()) {
                                case R.id.take_camera:
                                    cameraPic();
                                    break;
                                case R.id.take_Album:   //相册
                                    getImage();
    //                                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
    //                                intent.setType("image/*");
    //                                startActivityForResult(intent,120);
                                    break;
                                case R.id.cancle:
                                    break;
                            }
                            return true;
                        });
        menuSheetView.inflateMenu(R.menu.menu_takepictures);
        bottomSheetLayout.showWithSheetView(menuSheetView);
    }

    private void showSexDialog() {
        MaterialDialogUtil.INSTANCE.showDialogWithItems(this,
                R.string.select_sex, R.array.select_sex, R.string.select,
                new Function1<Integer, Unit>() {
                    @Override
                    public Unit invoke(Integer which) {
                        //0表示男,1表示女
                        if (which == 0) {
                            guiderUserInfo.setGender("MAN");
                            sexTv.setText(getResources().getString(R.string.sex_nan));
                            if (mUserInfo != null) {
                                mUserInfo.sex = "M";// 记录一下,提交的时候用
                            }

                        } else {
                            guiderUserInfo.setGender("WOMAN");
                            if (mUserInfo != null) {
                                mUserInfo.sex = "F";
                            }
                            sexTv.setText(getResources().getString(R.string.sex_nv));
                        }
                        if (mUserInfo != null) {
                            flag = "sex";
                            modifyPersonData(mUserInfo.sex);
                        }

                        updateGuiderUserInfo(guiderUserInfo);
                        return null;
                    }
                }
        );
    }

    //相册选择
    private void getImage() {
        new PickerBuilder(MyPersonalActivity.this, PickerBuilder.SELECT_FROM_GALLERY)
                .setOnImageReceivedListener(imageUri -> {
                    //设置头像
                    RequestOptions mRequestOptions =
                            RequestOptions.circleCropTransform()
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true);
                    Glide.with(MyPersonalActivity.this).
                            load(imageUri).apply(mRequestOptions).into(mineLogoIv);

                    uploadPic(ImageTool.getRealFilePath(
                            MyPersonalActivity.this, imageUri), 1);
//                        if (mUserInfo!=null)mUserInfo.image = ImageTool.getRealFilePath(MyPersonalActivity.this, imageUri)；

                    uploadGuiderPic(ImageTool.getRealFilePath(
                            MyPersonalActivity.this, imageUri));
                })
                .setImageName("headImg")
                .setImageFolderName("NewBluetoothStrap")
                .setCropScreenColor(Color.CYAN)
                .setOnPermissionRefusedListener(() -> {
                })
                .start();
    }


    //上传盖德图片
    private void uploadGuiderPic(String path) {

        Log.e(TAG, "------图片地址=" + path);

        String guiderImgUrl = BuildConfig.APIURL + "upload/file";
        OkHttpTool.getInstance().doRequestUploadFile(guiderImgUrl,
                new File(path).getName(), path, "11", result -> {
                    Log.e(TAG, "---盖德上传图片返回=" + result);
                    if (WatchUtils.isNetRequestSuccess(result, 0)) {
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            String data = jsonObject.getString("data");
                            if (data == null)
                                return;
                            guiderUserInfo.setHeadUrl(data);    //用户的头像地址
                            updateGuiderUserInfo(guiderUserInfo);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                });
    }


    //修改盖德用户信息
    private void updateGuiderUserInfo(GuiderUserInfo guser) {
        guser.setAddr("");
        guser.setCardId("");
        guser.setBirthday(birthdayTv.getText().toString() + "T00:00:00Z");
        String userUrl = BuildConfig.APIURL + "api/v1/usersimpleinfo"; // http://api.guiderhealth.com/
        if (requestPressent != null) {
            Log.e(TAG, "-------盖德参数=" + gson.toJson(guser));
            requestPressent.getRequestPutJsonObject(12, userUrl,
                    this, gson.toJson(guser), 12);

        }
        urlImagePath = guser.getHeadUrl();


        saveUser(nicknameTv.getText().toString().trim(),
                guser.getGender(),
                guser.getHeight(),
                guser.getWeight(), birthday);

    }


    /**
     * 上传头像图片
     *
     * @param flag 0_Base64 1_路径
     */
    private void uploadPic(String filePath, int flag) {
//        Log.e(TAG, "----上传图片=" + filePath);
        isSubmit = false;
//        HashMap<String, Object> map = new HashMap<>();
//        map.put("userId", SharedPreferencesUtils.readObject(this,Commont.USER_ID_DATA));
//        if (flag == 0) {
//            map.put("image", filePath);
//        } else {
//            map.put("image", ImageTool.GetImageStr(filePath));
//        }
//        String mapjson = gson.toJson(map);
//        Log.e(TAG, "----上传图片mapjson=" + mapjson);
//        dialogSubscriber = new DialogSubscriber(subscriberOnNextListener, MyPersonalActivity.this);
//        OkHttpObservable.getInstance().getData(dialogSubscriber, URLs.HTTPs + URLs.ziliaotouxiang, mapjson);


        HashMap<String, Object> map = new HashMap<>();
        if (userInfoBean != null && !TextUtils.isEmpty(userInfoBean.getUserid()))
            map.put("userId", userInfoBean.getUserid());
        if (flag == 0) {
            map.put("image", filePath);
        } else {
            map.put("image", ImageTool.GetImageStr(filePath));
        }
        String mapjson = gson.toJson(map);
        //Log.e(TAG, "----上传图片mapjson=" + mapjson);
        if (requestPressent != null)
            requestPressent.getRequestJSONObject(0x03,
                    Commont.FRIEND_BASE_URL + URLs.ziliaotouxiang,
                    MyPersonalActivity.this, mapjson, 3);

    }

    //完善用户资料
    private void modifyPersonData(String val) {
        isSubmit = true;
        HashMap<String, Object> map = new HashMap<>();
        map.put("userId", SharedPreferencesUtils.readObject(
                MyPersonalActivity.this, Commont.USER_ID_DATA));
        map.put(flag, val);
        String mapjson = gson.toJson(map);
//        Log.e(TAG, "-----mapJson=" + mapjson);
        dialogSubscriber = new DialogSubscriber(subscriberOnNextListener,
                MyPersonalActivity.this);
        OkHttpObservable.getInstance().getData(dialogSubscriber,
                URLs.HTTPs + URLs.yonghuziliao, mapjson);


        /***
         * 同步保存一下
         */
        //身高
        int resultHeight = 0;
        //体重
        int resultWeight = 0;
        String b15pUHeight = heightTv.getText().toString();
        String b15pUWeight = weightTv.getText().toString();

        if (w30sunit) {   //公制
            resultHeight = Integer.parseInt(StringUtils.substringBefore(
                    b15pUHeight, "cm").trim());
            resultWeight = Integer.parseInt(StringUtils.substringBefore(
                    b15pUWeight, "kg").trim());
        } else {  //英制
            resultHeight = Integer.parseInt(StringUtils.substringBefore(
                    b15pUHeight, "in").trim());
            resultWeight = Integer.parseInt(StringUtils.substringBefore(
                    b15pUWeight, "lb").trim());
        }

        //生日
        String birthday = birthdayTv.getText().toString().trim();
        String b15pSex = mUserInfo.sex == null ? "M" : mUserInfo.sex;
        //设置固件的个人信息
        //setUserData((b15pSex.equals("M") ? 0 : 1), resultHeight, resultWeight, resultAge);
        saveUser(nicknameTv.getText().toString().trim(),
                (b15pSex.equals("M") ? "MAN" : "WOMAN"),
                resultHeight,
                resultWeight, birthday);
    }

    private void saveUser(String nickName, String sex,
                          int resultHeight, int resultWeight, String birthday) {
        /**
         * birthday : 1995-06-15
         * image : http://47.90.83.197/image/2018/11/24/1543019974622.jpg
         * nickName : hello RaceFitPro
         * sex : M
         * weight : 60 kg
         * equipment : B30
         * userId : 0d56716e5629475882d4f4bfc7c51420
         * mac : E7:A7:0F:11:BE:B5
         * phone : 14791685830
         * height : 170 cm
         */
        User user = new User();
        user.setBirthday(birthday);
        user.setImage(urlImagePath);
        user.setNickName(nickName);
        user.setSex(sex);
        user.setWeight(resultWeight + "");
        user.setPhone("");
        user.setHeight(resultHeight + "");
        String s = new Gson().toJson(user);
        String userDetailedDataTYPE = (String) SharedPreferencesUtils.readObject(
                MyApp.getContext(), "UserDetailedDataTYPE");
        WatchUtils.setIsWxLogin(userDetailedDataTYPE, s);
    }


    @Override
    public void showLoadDialog(int what) {

    }

    @Override
    public void successData(int what, Object object, int daystag) {
//        Log.e(TAG, "----obj=" + object.toString());
        if (object == null)
            return;
        Log.e(TAG, "-----obj=" + what + "---obj=" + object.toString());
        if (what == 0x01) {
            initUserInfo(object.toString());
        } else if (what == 11) {   //获取盖德用户信息并显示
            showGaideUserInfo(object.toString());
        } else if (what == 12) {
            try {
                if (WatchUtils.isNetRequestSuccess(object.toString(), 0)) {
                    ToastUtil.showToast(this, "修改成功");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void failedData(int what, Throwable e) {
//        Log.e(TAG, "----fail=" + e.getMessage());
    }

    @Override
    public void closeLoadDialog(int what) {

    }


    //显示盖德的信息
    @SuppressLint("SetTextI18n")
    private void showGaideUserInfo(String userStr) {
        if (WatchUtils.isNetRequestSuccess(userStr, 0)) {
            try {
                JSONObject jsonObject = new JSONObject(userStr);
                String dataStr = jsonObject.getString("data");
                guiderUserInfo = gson.fromJson(dataStr, GuiderUserInfo.class);
                if (guiderUserInfo == null)
                    return;
                // guiderUserInfoTv.setText("用户信息:"+dataStr);
                urlImagePath = guiderUserInfo.getHeadUrl();
                //昵称
                nicknameTv.setText(guiderUserInfo.getName());
                //性别
                String sexStr = guiderUserInfo.getGender();
                sexTv.setText(sexStr.equals("WOMAN") ? getResources().getString(R.string.sex_nv) : getResources().getString(R.string.sex_nan));
                //身高
                heightTv.setText(guiderUserInfo.getHeight() + "cm");
                //体重
                weightTv.setText(guiderUserInfo.getWeight() + "kg");
                //生日
                birthdayTv.setText(guiderUserInfo.getBirthday() + "");

                //头像
                //mineLogoIv
                String hearUrl = guiderUserInfo.getHeadUrl();
                if (hearUrl != null) {
                    RequestOptions mRequestOptions = RequestOptions.circleCropTransform()
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true);
                    Glide.with(this).load(hearUrl)
                            .apply(mRequestOptions).into(mineLogoIv);//头像
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }


    //博之轮账号
    private void initUserInfo(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            if (!jsonObject.has("code"))
                return;
            if (jsonObject.getInt("code") == 200) {
                userInfoBean = new Gson().fromJson(
                        jsonObject.getString("data"), UserInfoBean.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    class MyCheckChangeListener implements RadioGroup.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (group.getId()) {
                case R.id.radioGroup_unti:
                    if (checkedId == R.id.radio_km) {
                        //  MyApp.getmW30SBLEManage().setUnit(1);// 0=英制，1=公制
                        w30sunit = true;
                        setListData();
                    } else if (checkedId == R.id.radio_mi) {
                        // MyApp.getmW30SBLEManage().setUnit(0);// 0=英制，1=公制
                        w30sunit = false;
                        setListData();
                    }
                    mLocalTool.putMetricSystem(w30sunit);
                    break;
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        Log.e(TAG, "-----result-=" + requestCode + "--resu=" + resultCode);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1000:// 改昵称回来
                    String nickName = data.getStringExtra("name");
                    nicknameTv.setText(nickName);
                    guiderUserInfo.setName(nickName);
                    updateGuiderUserInfo(guiderUserInfo);
                    if (mUserInfo == null)
                        return;
                    mUserInfo.nickName = nickName;// 记录一下,到时提交用
                    flag = "nickName";
                    modifyPersonData(nickName);
                    break;
                case 120: //从相册图片后返回的uri
                    //启动裁剪
                    if (data != null) {
                        handlerImageOnKitKat(data);
                    }

                    //startActivityForResult(CutForPhoto(data.getData()),111);
                    break;
                case 1001: //相机返回的 uri
                    //启动裁剪
                    String path = getExternalCacheDir().getPath();
//                    Log.e(TAG, "----裁剪path=" + path);
                    String name = "output.png";
                    startActivityForResult(CutForCamera(path, name), 111);
                    break;
                case 111:
                    try {
                        //获取裁剪后的图片，并显示出来
                        Bitmap bitmap = BitmapFactory.decodeStream(
                                this.getContentResolver().openInputStream(mCutUri));
                        //showImg.setImageBitmap(bitmap);
                        mineLogoIv.setImageBitmap(bitmap);
                        uploadPic(Base64BitmapUtil.bitmapToBase64(bitmap), 0);

                        String finalPhotoName =
                                Environment.getExternalStorageDirectory() +
                                        "/" + "NewBluetoothStrap/headImg" + "_" +
                                        new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
                                                .format(new Date(System.currentTimeMillis()))
                                        + ".jpg";
                        //上传盖德后台图片
                        //Log.e(TAG,"-------")
                        File flieP = FileOperUtils.saveFiles(bitmap, finalPhotoName);
                        uploadGuiderPic(flieP.getPath());

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }


    /**
     * 打开相机
     */
    private void cameraPic() {
        //创建一个file，用来存储拍照后的照片
        File outputfile = new File(getExternalCacheDir().getPath(), "output.png");
        try {
            if (outputfile.exists()) {
                outputfile.delete();//删除
            }
            outputfile.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Uri imageuri;
        if (Build.VERSION.SDK_INT >= 24) {
//            imageuri = FileProvider.getUriForFile(MyPersonalActivity.this,
//                    getPackageName(), //可以是任意字符串
//                    outputfile);
//            imageuri = FileProvider.getUriForFile(MyPersonalActivity.this,
//                    "com.guider.ringmiihx.fileprovider", //可以是任意字符串
//                    outputfile);
            //"com.guider.ringmiihx.fileprovider"
            imageuri = FileProvider.getUriForFile(MyPersonalActivity.this,
                    getPackageName() + ".fileProvider", //可以是任意字符串
                    outputfile);
        } else {
            imageuri = Uri.fromFile(outputfile);
        }
        //启动相机程序
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageuri);
        startActivityForResult(intent, 1001);
    }


    /**
     * 拍照之后，启动裁剪
     *
     * @param camerapath 路径
     * @param imgname    img 的名字
     * @return
     */

    private Intent CutForCamera(String camerapath, String imgname) {
        try {
            //设置裁剪之后的图片路径文件
            File cutfile = new File(getExternalCacheDir().getPath(),
                    "cutcamera.png"); //随便命名一个
            if (cutfile.exists()) {
                //如果已经存在，则先删除,这里应该是上传到服务器，然后再删除本地的，没服务器，只能这样了
                cutfile.delete();
            }
            cutfile.createNewFile();
            //初始化 uri
            Uri imageUri = null; //返回来的 uri
            Uri outputUri = null; //真实的 uri
            Intent intent = new Intent("com.android.camera.action.CROP");
            //拍照留下的图片
            File camerafile = new File(camerapath, imgname);
            if (Build.VERSION.SDK_INT >= 24) {
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                imageUri = FileProvider.getUriForFile(MyPersonalActivity.this,
//                        getPackageName(),
//                        camerafile);
//                imageUri = FileProvider.getUriForFile(MyPersonalActivity.this,
//                        "com.guider.ringmiihx.fileprovider",
//                        camerafile);

                imageUri = FileProvider.getUriForFile(MyPersonalActivity.this,
                        getPackageName() + ".fileProvider",
                        camerafile);
            } else {
                imageUri = Uri.fromFile(camerafile);
            }
            outputUri = Uri.fromFile(cutfile);
            //把这个 uri 提供出去，就可以解析成 bitmap了
            mCutUri = outputUri;
            // crop为true是设置在开启的intent中设置显示的view可以剪裁
            intent.putExtra("crop", true);
            // aspectX,aspectY 是宽高的比例，这里设置正方形
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            //设置要裁剪的宽高
            intent.putExtra("outputX", 150);
            intent.putExtra("outputY", 150);
            intent.putExtra("scale", true);
            //如果图片过大，会导致oom，这里设置为false
            intent.putExtra("return-data", false);
            if (imageUri != null) {
                intent.setDataAndType(imageUri, "image/*");
            }
            if (outputUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
            }
            intent.putExtra("noFaceDetection", true);
            //压缩图片
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
            return intent;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 图片裁剪
     *
     * @param uri
     * @return
     */
    private Intent CutForPhoto(Uri uri) {
//        Log.e(TAG, "-----相册选择uri=" + uri);
        try {
            //直接裁剪
            Intent intent = new Intent("com.android.camera.action.CROP");
            //设置裁剪之后的图片路径文件
            File cutfile = new File(getExternalCacheDir().getPath(),
                    "cutcamera.png"); //随便命名一个
            if (cutfile.exists()) { //如果已经存在，则先删除,这里应该是上传到服务器，然后再删除本地的，没服务器，只能这样了
                cutfile.delete();
            }
            cutfile.createNewFile();
            //初始化 uri
            Uri imageUri = uri; //返回来的 uri
            Uri outputUri = null; //真实的 uri
//            Log.d(TAG, "CutForPhoto: " + cutfile);
            outputUri = Uri.fromFile(cutfile);
            mCutUri = outputUri;
//            Log.d(TAG, "mCameraUri: " + mCutUri);
            // crop为true是设置在开启的intent中设置显示的view可以剪裁
            intent.putExtra("crop", true);
            // aspectX,aspectY 是宽高的比例，这里设置正方形
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            //设置要裁剪的宽高
            intent.putExtra("outputX", 150); //200dp
            intent.putExtra("outputY", 150);
            intent.putExtra("scale", true);
            //如果图片过大，会导致oom，这里设置为false
            intent.putExtra("return-data", false);
            if (imageUri != null) {
                intent.setDataAndType(imageUri, "image/*");
            }
            if (outputUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
            }
            intent.putExtra("noFaceDetection", true);
            //压缩图片
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
            return intent;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    @SuppressLint("NewApi")
    private void handlerImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this, uri)) {
            //如果是document类型的Uri,则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];//解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.parseLong(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            //如果是content类型的URI，则使用普通方式处理
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            //如果是file类型的Uri,直接获取图片路径即可
            imagePath = uri.getPath();
        }
//        Log.e(TAG, "---imagePath=" + imagePath);

        if (imagePath != null) {
            //CutForPhoto(Uri.fromFile(new File(imagePath)));

            startActivityForResult(CutForPhoto(Uri.fromFile(new File(imagePath))), 111);
        }

    }


    private String getImagePath(Uri uri, String selection) {
        String path = null;
        //通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private class UserInfoResult {
        String resultCode;
        UserInfo userInfo;
    }

    private class UserInfo {
        String userId;
        String image;
        String nickName;
        String sex;//"M","F"
        String height;//"170 cm"
        String weight;//"60 kg"
        String birthday;//"2000-06-15"
    }

}

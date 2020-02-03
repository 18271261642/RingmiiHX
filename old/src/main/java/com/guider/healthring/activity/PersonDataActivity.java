package com.guider.healthring.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.aigestudio.wheelpicker.widgets.DatePick;
import com.aigestudio.wheelpicker.widgets.ProfessionPick;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.flipboard.bottomsheet.BottomSheetLayout;
import com.flipboard.bottomsheet.commons.MenuSheetView;
import com.google.gson.Gson;
import com.guider.healthring.Commont;
import com.guider.healthring.R;
import com.guider.healthring.bean.GuiderUserInfo;
import com.guider.healthring.imagepicker.PickerBuilder;
import com.guider.healthring.rxandroid.DialogSubscriber;
import com.guider.healthring.rxandroid.SubscriberOnNextListener;
import com.guider.healthring.siswatch.NewSearchActivity;
import com.guider.healthring.siswatch.WatchBaseActivity;
import com.guider.healthring.siswatch.utils.Base64BitmapUtil;
import com.guider.healthring.siswatch.utils.FileOperUtils;
import com.guider.healthring.siswatch.utils.WatchUtils;
import com.guider.healthring.util.Common;
import com.guider.healthring.util.ImageTool;
import com.guider.healthring.util.OkHttpTool;
import com.guider.healthring.util.SharedPreferencesUtils;
import com.guider.healthring.util.ToastUtil;
import com.guider.healthring.util.URLs;
import com.guider.healthring.w30s.utils.httputils.RequestPressent;
import com.guider.healthring.w30s.utils.httputils.RequestView;
import com.guider.healthring.widget.SwitchIconView;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RequestExecutor;

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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.guider.healthring.util.Common.userInfo;

/**
 * Created by thinkpad on 2017/3/4.
 * 完善个人信息,注册账号成功后进入的页面
 */

public class PersonDataActivity extends WatchBaseActivity implements RequestView {
    private static final String TAG = "PersonDataActivity";

    private static final int GET_OPENCAMERA_CODE = 100;

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.head_img)
    CircleImageView headImg;
    @BindView(R.id.code_et)
    EditText codeEt;
    @BindView(R.id.brithdayval_tv)
    TextView brithdayvalTv;
    @BindView(R.id.heightval_tv)
    TextView heightvalTv;
    @BindView(R.id.weightval_tv)
    TextView weightvalTv;
    @BindView(R.id.man_iconview)
    SwitchIconView manIconview;
    @BindView(R.id.women_iconview)
    SwitchIconView womenIconview;
    @BindView(R.id.bottomsheet)
    BottomSheetLayout bottomSheetLayout;

    private String height, weight, sexVal, brithdayVal, nickName;
    private DialogSubscriber dialogSubscriber;
    private SubscriberOnNextListener<String> subscriberOnNextListener;
    private boolean isSubmit;
    private ArrayList<String> heightList;
    private ArrayList<String> weightList;

    private Uri mCutUri;


    private RequestPressent requestPressent;
    //盖德用户实体类
    private GuiderUserInfo guiderUserInfo = new GuiderUserInfo();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_persondata);
        ButterKnife.bind(this);


        initViews();

        initData();

    }

    private void initData() {
        requestPressent = new RequestPressent();
        requestPressent.attach(this);
    }


    private void initViews() {
        tvTitle.setText(R.string.mine_data);
        manIconview.switchState();
        sexVal = "M";
        heightList = new ArrayList<>();
        weightList = new ArrayList<>();
        for (int i = 120; i < 231; i++) {
            heightList.add(i + " cm");
        }
        for (int i = 20; i < 200; i++) {
            weightList.add(i + " kg");
        }

        //请求打开相机的权限

        AndPermission.with(PersonDataActivity.this)
                .runtime()
                .permission(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .onDenied(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {

                    }
                })
                .onDenied(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {

                    }
                })
                .rationale(new Rationale<List<String>>() {
                    @Override
                    public void showRationale(Context context, List<String> data, RequestExecutor executor) {

                    }
                }).start();

        subscriberOnNextListener = new SubscriberOnNextListener<String>() {
            @Override
            public void onNext(String result) {
//                Log.e("PersonDataActivity", "--result--" + result);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String resultCode = jsonObject.getString("resultCode");

//                    Log.e("PersonDataActivity", "------result----" + resultCode + "---" + jsonObject.getString("resultCode"));

                    if ("001".equals(resultCode)) {
                        if (isSubmit) {
//                            Log.e("PersonDataActivity", "----333------");
                            userInfo.setNickName(nickName);
                            userInfo.setSex(sexVal);
                            userInfo.setBirthday(brithdayVal);
                            userInfo.setHeight(height);
                            userInfo.setWeight(weight);
                            startActivity(new Intent(PersonDataActivity.this, NewSearchActivity.class));
                            finish();
                        } else {
//                            Log.e("PersonDataActivity", "----444------");
                            String imageUrl = jsonObject.optString("url");
                            userInfo.setImage(imageUrl);// .error(R.mipmap.touxiang)
//                            //   .error(R.drawable.piece_dot)
//                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                                    .centerCrop()
                            RequestOptions mRequestOptions = RequestOptions.circleCropTransform().diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .skipMemoryCache(true);
                            Glide.with(PersonDataActivity.this).load(imageUrl)
                                    .apply(mRequestOptions)
                                    .into(headImg);
                        }
                    } else {
                        ToastUtil.showShort(PersonDataActivity.this, getString(R.string.submit_fail));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_next, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        startActivity(new Intent(PersonDataActivity.this, NewSearchActivity.class));
        finish();
        return super.onOptionsItemSelected(item);

    }

    @OnClick({R.id.head_img, R.id.selectbirthday_relayout,
            R.id.selectheight_relayout, R.id.selectweight_relayout,
            R.id.confirmcompelte_btn, R.id.man_iconview, R.id.women_iconview})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.head_img: //选择头像
                if (AndPermission.hasPermissions(PersonDataActivity.this, Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    chooseHeadImg();
                } else {
                    AndPermission.with(PersonDataActivity.this)
                            .runtime()
                            .permission(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            .onGranted(new Action<List<String>>() {
                                @Override
                                public void onAction(List<String> data) {
                                    chooseHeadImg();
                                }
                            })
                            .onDenied(new Action<List<String>>() {
                                @Override
                                public void onAction(List<String> data) {

                                }
                            })
                            .start();
                }


                break;
            case R.id.selectbirthday_relayout:  //生日
                final DatePick pickerPopWin = new DatePick.Builder(PersonDataActivity.this, new DatePick.OnDatePickedListener() {
                    @Override
                    public void onDatePickCompleted(int year, int month, int day, String dateDesc) {
                        brithdayVal = dateDesc;
                        brithdayvalTv.setText(dateDesc);
                    }
                }).textConfirm(getResources().getString(R.string.confirm)) //text of confirm button
                        .textCancel(getResources().getString(R.string.cancle)) //text of cancel button
                        .btnTextSize(16) // button text size
                        .viewTextSize(25) // pick view text size
                        .colorCancel(Color.parseColor("#999999")) //color of cancel button
                        .colorConfirm(Color.parseColor("#009900"))//color of confirm button
                        .minYear(1900) //min year in loop
                        .maxYear(2020) // max year in loop
                        .dateChose("2000-06-15") // date chose when init popwindow
                        .build();
                pickerPopWin.showPopWin(PersonDataActivity.this);
                break;
            case R.id.selectheight_relayout:    //身高
                ProfessionPick professionPopWin = new ProfessionPick.Builder(PersonDataActivity.this, new ProfessionPick.OnProCityPickedListener() {
                    @Override
                    public void onProCityPickCompleted(String profession) {
                        height = profession;
                        heightvalTv.setText(height);
                        guiderUserInfo.setWeight(Integer.valueOf(StringUtils.substringBefore(profession,"cm").trim()));

                    }
                }).textConfirm(getResources().getString(R.string.confirm)) //text of confirm button
                        .textCancel(getResources().getString(R.string.cancle)) //text of cancel button
                        .btnTextSize(16) // button text size
                        .viewTextSize(25) // pick view text size
                        .colorCancel(Color.parseColor("#999999")) //color of cancel button
                        .colorConfirm(Color.parseColor("#009900"))//color of confirm button
                        .setProvinceList(heightList) //min year in loop
                        .dateChose("170 cm") // date chose when init popwindow
                        .build();
                professionPopWin.showPopWin(PersonDataActivity.this);
                break;
            case R.id.selectweight_relayout:    //体重
                ProfessionPick weightPopWin = new ProfessionPick.Builder(PersonDataActivity.this, new ProfessionPick.OnProCityPickedListener() {
                    @Override
                    public void onProCityPickCompleted(String profession) {
                        weight = profession;
                        weightvalTv.setText(profession);
                        guiderUserInfo.setWeight(Integer.valueOf(StringUtils.substringBefore(profession,"kg").trim()));
                    }
                }).textConfirm(getResources().getString(R.string.confirm)) //text of confirm button
                        .textCancel(getResources().getString(R.string.cancle)) //text of cancel button
                        .btnTextSize(16) // button text size
                        .viewTextSize(25) // pick view text size
                        .colorCancel(Color.parseColor("#999999")) //color of cancel button
                        .colorConfirm(Color.parseColor("#009900"))//color of confirm button
                        .setProvinceList(weightList) //min year in loop
                        .dateChose("60 kg") // date chose when init popwindow
                        .build();
                weightPopWin.showPopWin(PersonDataActivity.this);
                break;
            case R.id.confirmcompelte_btn:  //完成

                String uName = codeEt.getText().toString();
                String birthTxt = brithdayvalTv.getText().toString();
                String heightTxt = heightvalTv.getText().toString();
                String weightTxt = weightvalTv.getText().toString();
                if (TextUtils.isEmpty(uName)) {
                    ToastUtil.showShort(PersonDataActivity.this, getString(R.string.write_nickname));
                    return;
                }
                if (TextUtils.isEmpty(birthTxt)) {
                    ToastUtil.showShort(PersonDataActivity.this, getString(R.string.select_brithday));
                    return;
                }
                if (TextUtils.isEmpty(heightTxt)) {
                    ToastUtil.showShort(PersonDataActivity.this, getString(R.string.select_height));
                    return;
                }
                if (TextUtils.isEmpty(weightTxt)) {
                    ToastUtil.showShort(PersonDataActivity.this, getString(R.string.select_weight));
                    return;
                }
                submitGuiderUserInfoData();
                //完成

                submitPersonData(uName, birthTxt, heightTxt, weightTxt);



//                //完成
//                nickName = codeEt.getText().toString();
//                if (TextUtils.isEmpty(nickName)) {
//                    ToastUtil.showShort(PersonDataActivity.this, getString(R.string.write_nickname));
//                } else if (TextUtils.isEmpty(brithdayVal)) {
//                    ToastUtil.showShort(PersonDataActivity.this, getString(R.string.select_brithday));
//                } else if (TextUtils.isEmpty(height)) {
//                    ToastUtil.showShort(PersonDataActivity.this, getString(R.string.select_height));
//                } else if (TextUtils.isEmpty(weight)) {
//                    ToastUtil.showShort(PersonDataActivity.this, getString(R.string.select_weight));
//                } else {
//
//
//                }
                break;
            case R.id.man_iconview:
                sexVal = "M";
                guiderUserInfo.setGender("MAN");
                if (!manIconview.isIconEnabled()) {
                    manIconview.switchState();
                    womenIconview.setIconEnabled(false);
                }
                break;
            case R.id.women_iconview:
                sexVal = "F";
                guiderUserInfo.setGender("WOMAN");
                if (!womenIconview.isIconEnabled()) {
                    womenIconview.switchState();
                    manIconview.setIconEnabled(false);
                }
                break;
        }
    }

    //提交盖德编辑用户信息接口
    private void submitGuiderUserInfoData() {
        try {
            long guiderAccountId = (long) SharedPreferencesUtils.getParam(this,"accountIdGD",0L);
            if(guiderAccountId == 0)
                return;
            guiderUserInfo.setAccountId((int) guiderAccountId);
            guiderUserInfo.setAddr("");
            guiderUserInfo.setCardId("");

            String birthdarStr = brithdayvalTv.getText().toString();
            guiderUserInfo.setBirthday(birthdarStr+"T00:00:00Z");
            String userUrl = "http://api.guiderhealth.com/api/v1/usersimpleinfo";
            if(requestPressent != null){
                //Log.e(TAG,"-------盖德参数="+new Gson().toJson(guiderUserInfo));
                requestPressent.getRequestPutJsonObject(12,userUrl,this,new Gson().toJson(guiderUserInfo),12);

            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    //头像选择
    private void chooseHeadImg() {
        MenuSheetView menuSheetView =
                new MenuSheetView(PersonDataActivity.this, MenuSheetView.MenuType.LIST, R.string.select_photo, new MenuSheetView.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (bottomSheetLayout.isSheetShowing()) {
                            bottomSheetLayout.dismissSheet();
                        }
                        switch (item.getItemId()) {
                            case R.id.take_camera:  //拍照
                                // getImage(PickerBuilder.SELECT_FROM_CAMERA);
                                cameraPic();
                                break;
                            case R.id.take_Album:   //相册选择
                                getImage(PickerBuilder.SELECT_FROM_GALLERY);
//                                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                                intent.setType("image/*");
//                                startActivityForResult(intent,120);
                                break;
                            case R.id.cancle:
                                break;
                        }
                        return true;
                    }
                });
        menuSheetView.inflateMenu(R.menu.menu_takepictures);
        bottomSheetLayout.showWithSheetView(menuSheetView);
    }


    private void getImage(int type) {
        new PickerBuilder(PersonDataActivity.this, type)
                .setOnImageReceivedListener(new PickerBuilder.onImageReceivedListener() {
                    @Override
                    public void onImageReceived(Uri imageUri) {
                        headImg.setImageURI(imageUri);
                        uploadPic(ImageTool.getRealFilePath(PersonDataActivity.this, imageUri), 1);
                        //上传盖德平台头像
                        uploadGuiderPic(ImageTool.getRealFilePath(PersonDataActivity.this, imageUri));
                    }
                })
                .setImageName("headImg")
                .setImageFolderName("NewBluetoothStrap")
                .setCropScreenColor(Color.CYAN)
                .setOnPermissionRefusedListener(new PickerBuilder.onPermissionRefusedListener() {
                    @Override
                    public void onPermissionRefused() {

                    }
                })
                .start();
    }


    //上传盖德图片
    private void uploadGuiderPic(String path) {
        String guiderImgUrl = "http://api.guiderhealth.com/upload/file";
        OkHttpTool.getInstance().doRequestUploadFile(guiderImgUrl, new File(path).getName(), path, "11", new OkHttpTool.HttpResult() {
            @Override
            public void onResult(String result) {
                Log.e(TAG,"---盖德上传图片返回="+result);
                if(WatchUtils.isNetRequestSuccess(result,0)){
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        String data = jsonObject.getString("data");
                        if(data == null)
                            return;
//                        guiderUserInfo.setHeadUrl(data);    //用户的头像地址
//                        updateGuiderUserInfo(guiderUserInfo);
                        guiderUserInfo.setHeadUrl(data);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

            }
        });
    }




    //上传我平台用户头像
    private void uploadPic(String filePath, int flag) {
//        Gson gson = new Gson();
//        HashMap<String, Object> map = new HashMap<>();
//        map.put("userId", Common.customer_id);
//        if (flag == 0) {  //相机拍摄
//            map.put("image", filePath);
//        } else {//图片选择
//            map.put("image", ImageTool.GetImageStr(filePath));
//        }
//
//        String mapjson = gson.toJson(map);
////        Log.e("PersonDataActivity", "----111------" + mapjson);
//        dialogSubscriber = new DialogSubscriber(subscriberOnNextListener, PersonDataActivity.this);
//        OkHttpObservable.getInstance().getData(dialogSubscriber, URLs.HTTPs + URLs.ziliaotouxiang, mapjson);





        Gson gson = new Gson();
        HashMap<String, Object> map = new HashMap<>();
        map.put("userId", Common.customer_id);
        if (flag == 0) {  //相机拍摄
            map.put("image", filePath);
        } else {//图片选择
            map.put("image", ImageTool.GetImageStr(filePath));
        }

        String mapjson = gson.toJson(map);
        if (requestPressent != null)
            requestPressent.getRequestJSONObject(0x01, Commont.FRIEND_BASE_URL + URLs.ziliaotouxiang, PersonDataActivity.this, mapjson, 1);



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        Log.e(TAG, "-----result-=" + requestCode + "--resu=" + resultCode);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 120: //从相册图片后返回的uri
                    if (data != null) {
                        handlerImageOnKitKat(data);
                    }
                    //启动裁剪
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
                        if (mCutUri != null) {
                            //获取裁剪后的图片，并显示出来
                            Bitmap bitmap = BitmapFactory.decodeStream(
                                    this.getContentResolver().openInputStream(mCutUri));
                            //showImg.setImageBitmap(bitmap);
                            headImg.setImageBitmap(bitmap);
//                            RequestOptions mRequestOptions = RequestOptions.circleCropTransform().diskCacheStrategy(DiskCacheStrategy.ALL)
//                                    .skipMemoryCache(true);
//                            Glide.with(PersonDataActivity.this).
//                                    load(mCutUri).apply(mRequestOptions).into(headImg);
                            // uploadPic(ImageTool.getRealFilePath(PersonDataActivity.this, mCutUri));
                            uploadPic(Base64BitmapUtil.bitmapToBase64(bitmap), 0);


                            String finalPhotoName =
                                    Environment.getExternalStorageDirectory()+"/"+"NewBluetoothStrap/headImg"+"_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date(System.currentTimeMillis()))
                                            + ".jpg";
                            //上传盖德后台图片
                            //Log.e(TAG,"-------")
                            File flieP = FileOperUtils.saveFiles(bitmap,finalPhotoName);
                            uploadGuiderPic(flieP.getPath());

                        }

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }

    }

    private void submitPersonData(String uName, String birthTxt, String heightTxt, String weightTxt) {
//        isSubmit = true;
//        Gson gson = new Gson();
//        HashMap<String, Object> map = new HashMap<>();
////        map.put("userId", B18iCommon.customer_id);
//        map.put("userId", Common.customer_id);
//        map.put("sex", sexVal);
//        map.put("nickName", nickName);
//        map.put("height", height);
//        map.put("weight", weight);
//        map.put("birthday", brithdayVal);
//        String mapjson = gson.toJson(map);
////        Log.e("PersonDataActivity", "----222------" + mapjson);
//        dialogSubscriber = new DialogSubscriber(subscriberOnNextListener, PersonDataActivity.this);
//        OkHttpObservable.getInstance().getData(dialogSubscriber, URLs.HTTPs + URLs.yonghuziliao, mapjson);



        Gson gson = new Gson();
        HashMap<String, Object> map = new HashMap<>();
        map.put("userId", Common.customer_id);
        map.put("sex", sexVal);
        map.put("nickName", uName);
        map.put("height", heightTxt);
        map.put("weight", weightTxt);
        map.put("birthday", birthTxt);
        String mapjson = gson.toJson(map);
        if (requestPressent != null) {
            requestPressent.getRequestJSONObject(0x02, Commont.FRIEND_BASE_URL + URLs.yonghuziliao, PersonDataActivity.this, mapjson, 2);
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
//            imageuri = FileProvider.getUriForFile(PersonDataActivity.this,
//                    "com.guider.ringmiihx.fileprovider", //可以是任意字符串
//                    outputfile);
            imageuri = FileProvider.getUriForFile(PersonDataActivity.this,
                    getPackageName()+".fileProvider", //可以是任意字符串
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
            if (cutfile.exists()) { //如果已经存在，则先删除,这里应该是上传到服务器，然后再删除本地的，没服务器，只能这样了
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
//                imageUri = FileProvider.getUriForFile(PersonDataActivity.this,
//                        "com.guider.ringmiihx.fileprovider",
//                        camerafile);
                imageUri = FileProvider.getUriForFile(PersonDataActivity.this,
                        getPackageName()+".fileProvider",
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
            Log.d(TAG, "CutForPhoto: " + cutfile);
            outputUri = Uri.fromFile(cutfile);
            mCutUri = outputUri;
            Log.d(TAG, "mCameraUri: " + mCutUri);
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
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(requestPressent != null)
            requestPressent.detach();
    }

    @Override
    public void showLoadDialog(int what) {

    }

    @Override
    public void successData(int what, Object object, int daystag) {
        if (object == null)
            return;
        if (object.toString().contains("<html>"))
            return;
        try {
            JSONObject jsonObject = new JSONObject(object.toString());
            switch (what) {
                case 0x01:  //头像
                    if(!jsonObject.has("code"))
                        return;
                    if(jsonObject.getInt("code") == 200){
                        ToastUtil.showToast(PersonDataActivity.this,getResources().getString(R.string.submit_success));
                    }else{
                        ToastUtil.showToast(PersonDataActivity.this,jsonObject.getString("msg") + jsonObject.getString("data"));
                    }
                    break;
                case 0x02:  //完善用户信息
                    analysisUserStr(jsonObject);
                    break;
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




    //完善用户信息
    private void analysisUserStr(JSONObject jsonObject) {
        try {
            if (!jsonObject.has("code"))
                return;
            if (jsonObject.getInt("code") == 200) {
                ToastUtil.showToast(PersonDataActivity.this,getResources().getString(R.string.modify_success));
                startActivity(NewSearchActivity.class);
                finish();
            } else {
                ToastUtil.showToast(PersonDataActivity.this, jsonObject.getString("data"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

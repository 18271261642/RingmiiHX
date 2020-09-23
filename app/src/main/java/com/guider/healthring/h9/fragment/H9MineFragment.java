package com.guider.healthring.h9.fragment;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.cardview.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.guider.healthring.B18I.b18isystemic.B18IAppSettingActivity;
import com.guider.healthring.B18I.b18isystemic.B18IRankingListActivity;
import com.guider.healthring.B18I.b18isystemic.B18ISettingActivity;
import com.guider.healthring.B18I.b18isystemic.B18ITargetSettingActivity;
import com.guider.healthring.B18I.b18isystemic.FindFriendActivity;
import com.guider.healthring.Commont;
import com.guider.healthring.R;
import com.guider.healthring.activity.MyPersonalActivity;
import com.guider.healthring.bleutil.MyCommandManager;
import com.guider.healthring.net.OkHttpObservable;
import com.guider.healthring.rxandroid.CommonSubscriber;
import com.guider.healthring.rxandroid.SubscriberOnNextListener;
import com.guider.healthring.siswatch.NewSearchActivity;
import com.guider.healthring.siswatch.utils.WatchUtils;
import com.guider.healthring.util.SharedPreferencesUtils;
import com.guider.healthring.util.URLs;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @aboutContent:
 * @author： 安
 * @crateTime: 2017/9/27 16:35
 * @mailBox: an.****.life@gmail.com
 * @company: 东莞速成科技有限公司
 */

public class H9MineFragment extends Fragment implements View.OnClickListener{

    View b18iMineView;

    //头像显示ImageView
    ImageView userImageHead;
    //用户名称显示TextView
    TextView userName;
    //总公里数显示TextView
    TextView totalKilometers;
    //日均步数显示TextView
    TextView equalStepNumber;
    //达标天数显示TextView
    TextView standardDay;
    //排行榜
    CardView privatemodeCardview;


    private CommonSubscriber commonSubscriber, commonSubscriber2;
    private SubscriberOnNextListener subscriberOnNextListener, subscriberOnNextListener2;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        b18iMineView = inflater.inflate(R.layout.fragment_b18i_mine, container, false);
        initViewIds();

        initData();
        //获取用户信息
        getUserInfoData();
        //获取数据展示
        getUserSportData();
        return b18iMineView;
    }

    private void initViewIds() {
        userImageHead = b18iMineView.findViewById(R.id.userImageHead);
        userName = b18iMineView.findViewById(R.id.userName);
        totalKilometers = b18iMineView.findViewById(R.id.totalKilometers);
        equalStepNumber = b18iMineView.findViewById(R.id.equalStepNumber);
        standardDay = b18iMineView.findViewById(R.id.standardDay);
        privatemodeCardview = b18iMineView.findViewById(R.id.privatemode_cardview);
        privatemodeCardview.setOnClickListener(this);
        b18iMineView.findViewById(R.id.targetSetting).setOnClickListener(this);
        b18iMineView.findViewById(R.id.personalData).setOnClickListener(this);
        b18iMineView.findViewById(R.id.smartAlert).setOnClickListener(this);
        b18iMineView.findViewById(R.id.findFriends).setOnClickListener(this);
        b18iMineView.findViewById(R.id.mineSetting).setOnClickListener(this);
        userImageHead.setOnClickListener(this);
    }

    private void initData() {
        //我的数据返回
        subscriberOnNextListener = new SubscriberOnNextListener<String>() {
            @Override
            public void onNext(String result) {//{"userInfo":{"birthday":"1994-12-27","image":"http://47.90.83.197/image/2017/07/31/1501490388771.jpg","nickName":"孙建华","sex":"M","weight":"60 kg","userId":"5c2b58f0681547a0801d4d4ac8465f82","phone":"15916947377","height":"175 cm"},"resultCode":"001"}
                Log.e("mine", "-----个人信息-----" + result);
                if (!WatchUtils.isEmpty(result)) {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        if (jsonObject.getString("resultCode").equals("001")) {
                            JSONObject myInfoJsonObject = jsonObject.getJSONObject("userInfo");
                            if (myInfoJsonObject != null) {
                                userName.setText("" + myInfoJsonObject.getString("nickName") + "");
                                String imgHead = myInfoJsonObject.getString("image");
                                if (!WatchUtils.isEmpty(imgHead)) {
                                    RequestOptions mRequestOptions = RequestOptions.circleCropTransform().diskCacheStrategy(DiskCacheStrategy.NONE)
                                            .skipMemoryCache(true);
                                    //头像
                                    Glide.with(getActivity()).load(imgHead).apply(mRequestOptions).into(userImageHead);    //头像
                                }
                                String userHeight = myInfoJsonObject.getString("height");
                                if (userHeight != null) {
                                    if (userHeight.contains("cm")) {
                                        String newHeight = userHeight.substring(0, userHeight.length() - 2);
                                        SharedPreferencesUtils.setParam(getActivity(), "userheight", newHeight.trim());
                                    } else {
                                        SharedPreferencesUtils.setParam(getActivity(), "userheight", userHeight.trim());
                                    }
                                }
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        };

        //显示数据返回
        //数据返回
        subscriberOnNextListener2 = new SubscriberOnNextListener<String>() {
            @Override
            public void onNext(String result) { //{"myInfo":{"distance":48.3,"count":2,"stepNumber":1582},"resultCode":"001"}
                Log.e("mine", "------result----" + result);
                if (!WatchUtils.isEmpty(result)) {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        if (jsonObject.getInt("resultCode") == 001) {
                            JSONObject myInfoJsonObject = jsonObject.getJSONObject("myInfo");
                            if (myInfoJsonObject != null) {
                                String distances = myInfoJsonObject.getString("distance");
                                if (!WatchUtils.isEmpty(distances)) {
                                    //总公里数
                                    totalKilometers.setText("" + distances + "");
                                }
                                String counts = myInfoJsonObject.getString("count");
                                if (!WatchUtils.isEmpty(myInfoJsonObject.getString("count"))) {
                                    //达标天数
                                    standardDay.setText("" + myInfoJsonObject.getString("count") + "");
                                }
                                String stepNums = myInfoJsonObject.getString("stepNumber");
                                if (!WatchUtils.isEmpty(stepNums)) {
                                    //平均步数
                                    equalStepNumber.setText("" + myInfoJsonObject.getString("stepNumber") + "");
                                }
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }

    //获取用户信息
    private void getUserInfoData() {
        String url = URLs.HTTPs + URLs.getUserInfo; //查询用户信息
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("userId", SharedPreferencesUtils.readObject(getActivity(), "userId"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        commonSubscriber = new CommonSubscriber(subscriberOnNextListener, getActivity());
        OkHttpObservable.getInstance().getData(commonSubscriber, url, jsonObj.toString());
    }

    //获取显示的数据
    private void getUserSportData() {
        String myInfoUrl = URLs.HTTPs + URLs.myInfo;
        JSONObject js = new JSONObject();
        try {
            js.put("userId", SharedPreferencesUtils.readObject(getActivity(), "userId"));
            js.put("deviceCode", SharedPreferencesUtils.readObject(getActivity(), Commont.BLEMAC));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        commonSubscriber2 = new CommonSubscriber(subscriberOnNextListener2, getActivity());
        OkHttpObservable.getInstance().getData(commonSubscriber2, myInfoUrl, js.toString());
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.userImageHead:    //点击头像
                startActivity(new Intent(getActivity(), MyPersonalActivity.class));
                break;
            case R.id.privatemode_cardview://排行榜
                startActivity(new Intent(getActivity(), B18IRankingListActivity.class).putExtra("is18i", "H9"));
                break;
            case R.id.targetSetting://目标设置
                startActivity(new Intent(getActivity(), B18ITargetSettingActivity.class).putExtra("is18i", "H9"));
                break;
            case R.id.personalData://个人资料
                //  startActivity(new Intent(getActivity(), MinePersonDataActivity.class).putExtra("is18i", "H9"));
                startActivity(new Intent(getActivity(), MyPersonalActivity.class));
                break;
            case R.id.smartAlert://功能设置-----改----》mine_dev_image
                if (MyCommandManager.DEVICENAME != null) {    //已连接
                    startActivity(new Intent(getActivity(), B18ISettingActivity.class).putExtra("is18i", "H9"));
                } else {
                    startActivity(new Intent(getContext(), NewSearchActivity.class));
                    getActivity().finish();
                }
                break;
            case R.id.findFriends://查找朋友
                startActivity(new Intent(getActivity(), FindFriendActivity.class).putExtra("is18i", "H9"));
                break;
            case R.id.mineSetting://设置
                startActivity(new Intent(getActivity(), B18IAppSettingActivity.class).putExtra("is18i", "H9"));
                break;
        }
    }
}
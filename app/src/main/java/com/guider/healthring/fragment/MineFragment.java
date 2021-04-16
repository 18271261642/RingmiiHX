package com.guider.healthring.fragment;

import android.util.Log;

import com.google.gson.Gson;
import com.guider.health.common.utils.StringUtil;
import com.guider.healthring.MyApp;
import com.guider.healthring.bean.BlueUser;
import com.guider.healthring.bean.TypeUserDatas;
import com.guider.healthring.bean.WXUserBean;
import com.guider.healthring.siswatch.LazyFragment;
import com.guider.healthring.siswatch.utils.WatchUtils;
import com.guider.healthring.util.SharedPreferencesUtils;

public class MineFragment extends LazyFragment {
    private static final String TAG = "MineFragment";

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");

        // 用户信息
        String userDetailedData = (String) SharedPreferencesUtils.readObject(MyApp.getContext(), "UserDetailedData");
        TypeUserDatas typeUserDatas = null;
        if (!WatchUtils.isEmpty(userDetailedData)) {
            Log.i(TAG, userDetailedData);
            typeUserDatas = new Gson().fromJson(userDetailedData, TypeUserDatas.class);
        }
        if (typeUserDatas != null) {
            Log.i(TAG, "setUserInfo");
            String phoneType = "LOGION_PHONE";
            String dataJson = typeUserDatas.getDataJson();
            WXUserBean wxUserBean = new Gson().fromJson(dataJson, WXUserBean.class);
            if (!StringUtil.isEmpty(wxUserBean.getNickname())
                    || !StringUtil.isEmpty(wxUserBean.getHeadimgurl())) {
                Log.i(TAG, wxUserBean.getNickname() + " - " + wxUserBean.getHeadimgurl());
                setUserInfo(wxUserBean.getNickname(), wxUserBean.getHeadimgurl());
            } else {
                BlueUser blueUser = new Gson().fromJson(dataJson, BlueUser.class);
                Log.i(TAG, blueUser.getNickName() + " - " + blueUser.getImage());
                setUserInfo(blueUser.getNickName(), blueUser.getImage());
            }
        }
    }

    protected void setUserInfo(String name, String headUrl) {

    }
}

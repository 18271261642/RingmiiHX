package com.guider.baselib.bean;

import android.text.TextUtils;
import android.util.Log;

import com.guider.baselib.device.standard.StandardCallback;
import com.guider.health.apilib.ApiCallBack;
import com.guider.health.apilib.ApiUtil;
import com.guider.health.apilib.IUserHDApi;
import com.guider.health.apilib.model.hd.standard.StandardRequestBean;
import com.guider.health.apilib.model.hd.standard.StandardResultBean;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public abstract class BaseDataSave {

    protected String cardShowStr; // 整体的判定结论 , 用于结果页的卡片显示 , 比如说血压测量 , 这个就是高压个低压一起判定出的血压偏高之类的

    protected abstract void onStandardFinish(List<StandardResultBean> standardResultBean);

    public abstract void startStandardRun(StandardCallback callback);

    protected int getArrow(String str) {
        if (TextUtils.isEmpty(str)) {
            return 0;
        }
        if (str.contains("高")) {
            if (str.contains("轻度")) {
                return 1;
            } else if (str.contains("中度")) {
                return 2;
            } else if (str.contains("重度")) {
                return 3;
            } else {
                return 1;
            }

        } else if (str.contains("低")) {
            if (str.contains("轻度")) {
                return -1;
            } else if (str.contains("中度")) {
                return -2;
            } else if (str.contains("重度")) {
                return -3;
            } else {
                return -1;
            }
        } else {
            return 0;
        }
    }

    protected void standardFromServer(final String who , List<StandardRequestBean> standardRequestBean, final StandardCallback callback) {
        ApiUtil.createHDApi(IUserHDApi.class).getStandard(standardRequestBean).enqueue(
                new ApiCallBack<List<StandardResultBean>>(){
                    @Override
                    public void onApiResponse(Call<List<StandardResultBean>> call, Response<List<StandardResultBean>> response) {
                        super.onApiResponse(call, response);
                        if (response == null || response.body() == null || response.body().size() == 0) {
                            callback.onResult(false);
                            return;
                        }
                        onStandardFinish(response.body());
                        for (StandardResultBean datum : response.body()) {
                            Log.i("BaseDataSave On Server", "----解析-----" + datum.getType());
                        }
                        callback.onResult(true);
                    }

                    @Override
                    public void onFailure(Call<List<StandardResultBean>> call, Throwable t) {
                        super.onFailure(call, t);
                        callback.onResult(false);
                    }
                }
        );
    }

    public String getCardShowStr() {
        return cardShowStr;
    }

    public BaseDataSave setCardShowStr(String cardShowStr) {
        this.cardShowStr = cardShowStr;
        return this;
    }
}

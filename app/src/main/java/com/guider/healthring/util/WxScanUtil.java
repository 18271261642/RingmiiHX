package com.guider.healthring.util;

import android.content.Context;

import com.guider.health.apilib.ApiCallBack;
import com.guider.health.apilib.ApiUtil;
import com.guider.health.apilib.IGuiderApi;
import com.guider.health.common.BuildConfig;
import com.guider.health.common.utils.AppUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class WxScanUtil {
    public static <T> boolean handle(Context context, long accountId, IWxScan iWxScan) {
        // 1 检测微信是否安装
        if (!(AppUtils.isWeixinAvilible(context) || BuildConfig.DEBUG)){
            iWxScan.onError();
            return false;
        }
        // 2. 若微信安装访问后台接口
        ApiUtil.createApi(IGuiderApi.class)
                .getUserBindWxPublic("wx6054546f7e4e24b6", accountId)
                .enqueue(new ApiCallBack<String>() {
                    @Override
                    public void onApiResponse(Call<String> call, Response<String> response) {
                        String flag = response.body();
                        if (flag.equals("true"))
                            iWxScan.onError();
                        else
                            iWxScan.onOk();
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        super.onFailure(call, t);
                        iWxScan.onError();
                    }
                });
        return true;
    }

    public interface IWxScan {
        void onError();
        void onOk();
    }
}

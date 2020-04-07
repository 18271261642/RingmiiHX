package com.guider.libbase.other;

import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.guider.health.common.core.SPUtils;
import com.guider.health.common.core.UserManager;

/**
 * Created by haix on 2019/8/11.
 */
public class BaseAndroidInterface {
    private final String TAG = BaseAndroidInterface.class.getSimpleName();
    private Context mContext;
    private IWebviewAgent mIWebviewAgent;

    public BaseAndroidInterface(Context context, IWebviewAgent iWebviewAgent) {
        mContext = context;
        mIWebviewAgent = iWebviewAgent;
    }

    @JavascriptInterface
    public void getUserInfo(String method) {
        Log.i("haix", "getUserInfo: " + method);
        long accountId = (long) SPUtils.get(mContext, "accountIdGD", 0L);
        String token = (String) SPUtils.get(mContext, "tokenGD", "1");
        UserManager.getInstance().setAccountId((int) accountId);
        UserManager.getInstance().setToken(token);

        if ("".equals(token)) {
            token = null;
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("accountId", accountId);
        jsonObject.put("token", token);
        // mIWebviewAgent.callJs(method, jsonObject.toJSONString());
    }

    @JavascriptInterface
    public void testResult(String re) {
        synchronized (BaseAndroidInterface.class) {
            Object ob = JSON.toJSON(false);
            mIWebviewAgent.callJs(re, ob.toString());
        }
    }
}

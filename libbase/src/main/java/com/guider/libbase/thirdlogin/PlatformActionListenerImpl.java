package com.guider.libbase.thirdlogin;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.guider.health.common.utils.JsonUtil;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;

public class PlatformActionListenerImpl implements PlatformActionListener {
    private static String TAG = "PlatformActionListenerImpl";
    private Context mContext;

    public PlatformActionListenerImpl(Context context) {
        mContext = context;
    }
    @Override
    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
        Log.i(TAG, JsonUtil.toStr(hashMap));
    }

    @Override
    public void onError(Platform platform, final int i, final Throwable throwable) {
        runOnUIThread(new Runnable() {
            @Override
            public void run() {
                throwable.printStackTrace();
                Toast.makeText(mContext, "登陆失败" + i, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onCancel(Platform platform, int i) {
        runOnUIThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mContext, "登陆取消了", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void runOnUIThread(Runnable action) {
        Activity activity = (Activity) mContext;
        activity.runOnUiThread(action);
    }
}

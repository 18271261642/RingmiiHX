package com.guider.libbase.thirdlogin;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.guider.health.common.utils.JsonUtil;
import com.guider.libbase.R;

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
                Toast.makeText(mContext, mContext.getResources().getString(R.string.login_failed), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onCancel(Platform platform, int i) {
        runOnUIThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mContext, mContext.getResources().getString(R.string.login_cancelled), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void runOnUIThread(Runnable action) {
        Activity activity = (Activity) mContext;
        activity.runOnUiThread(action);
    }
}

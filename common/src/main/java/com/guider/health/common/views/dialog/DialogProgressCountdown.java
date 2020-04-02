package com.guider.health.common.views.dialog;

import android.content.Context;
import android.os.CountDownTimer;
import android.util.Log;

public class DialogProgressCountdown {
    private static String TAG = DialogProgressCountdown.class.getSimpleName();
    private Context mContext;
    private CountDownTimer mCountDownTimer;
    private DialogProgress mDialogProgress;

    public DialogProgressCountdown(Context context) {
        mContext = context;
    }

    public void showDialog(long millisInFuture, long countDownInterval, final Runnable onFinish) {
        mDialogProgress = new DialogProgress(mContext, null);
        mDialogProgress.showDialog();
        mDialogProgress.setTvText((millisInFuture / 1000) + "S");
        mCountDownTimer = new CountDownTimer(millisInFuture, countDownInterval) {
            @Override
            public void onTick(long l) {
                // Log.i(TAG, "" + l);
                mDialogProgress.setTvText((l / 1000) + "S");
            }

            @Override
            public void onFinish() {
                mDialogProgress.hideDialog();
                if (onFinish != null) onFinish.run();
            }
        };
        mCountDownTimer.start();
    }

    public void hideDialog() {
        mDialogProgress.hideDialog();
        mCountDownTimer.cancel();
    }
}

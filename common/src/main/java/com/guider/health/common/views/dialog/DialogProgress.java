package com.guider.health.common.views.dialog;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.TextView;

import com.guider.health.common.R;

public class DialogProgress {
    private static String TAG = DialogProgress.class.getSimpleName();

    private Dialog dialog;
    private TextView tvPic, tvText;
    private RotateAnimation myAlphaAnimation;

    private Context mContext;
    private IDialogProgress mIDialogProgress;

    public DialogProgress(Context context, IDialogProgress iDialogProgress) {
        mContext = context;
        mIDialogProgress = iDialogProgress;

        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_progress, null);
        tvPic = view.findViewById(R.id.ecg_loading_pic);
        tvText = view.findViewById(R.id.ecg_loading_text);

        dialog = new Dialog(mContext);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        // 去掉标题线
        dialog.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(false);

        // 背景透明
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.gravity = Gravity.CENTER; // 居中位置
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        window.setWindowAnimations(R.style.mystyle);  // 添加动画

        initAnimation();
    }

    public void showDialog() {
        if (dialog.isShowing())
            return;
        if (mIDialogProgress != null)
            mIDialogProgress.onStart();
        dialog.show();
        myAlphaAnimation.startNow();
    }

    public void hideDialog() {
        if (!dialog.isShowing())
            return;
        if (mIDialogProgress != null)
            mIDialogProgress.onEnd();
        dialog.dismiss();
    }

    public void setTvText(String text) {
        tvText.setText(text);
    }

    // 初始化动画
    public void initAnimation() {
        myAlphaAnimation = new RotateAnimation(0f, 360f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);// 设置图片动画属性，各参数说明可参照api
        myAlphaAnimation.setRepeatCount(-1);// 设置旋转重复次数，即转几圈
        myAlphaAnimation.setDuration(500);// 设置持续时间，注意这里是每一圈的持续时间，如果上面设置的圈数为3，持续时间设置1000，则图片一共旋转3秒钟
        myAlphaAnimation.setInterpolator(new LinearInterpolator());// 设置动画匀速改变。相应的还有AccelerateInterpolator、DecelerateInterpolator、CycleInterpolator等
        tvPic.setAnimation(myAlphaAnimation);// 设置imageview的动画，也可以myImageView.startAnimation(myAlphaAnimation)
        myAlphaAnimation.setAnimationListener(new Animation.AnimationListener() { // 设置动画监听事件
            @Override
            public void onAnimationStart(Animation arg0) {
                // TODO Auto-generated method stub
                Log.i(TAG, "onAnimationStart");
            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
                // Log.i(TAG, "onAnimationRepeat");
            }

            // 图片旋转结束后触发事件，这里启动新的activity
            @Override
            public void onAnimationEnd(Animation arg0) {
                // TODO Auto-generated method stub
                Log.i(TAG, "onAnimationEnd");
            }
        });
    }

    public interface IDialogProgress {
        void onStart();
        void onEnd();
    }
}

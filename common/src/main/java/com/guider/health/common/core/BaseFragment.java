package com.guider.health.common.core;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.guider.health.common.R;


/**
 * Created by haix on 2019/6/22.
 */

public class BaseFragment extends MySupportFragment{

//    private Dialog dialog;

    protected Handler baseHandler = new Handler(Looper.getMainLooper());
    private RotateAnimation baseAlphaAnimation;
    private View baseTextview;




    @Override
    public void onDestroy() {
        super.onDestroy();

        baseHandler.removeCallbacksAndMessages(null);
    }

    protected void setHomeEvent(View home, final String tag){
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    popTo(Class.forName(tag), false);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        // TODO Auto-generated method stub
        super.onHiddenChanged(hidden);
        // 显示 hidden 为 false
        if (!hidden) {
            if (_mActivity instanceof BaseActivity) {
                ((BaseActivity) _mActivity).setCurrentFragment(this);
            }

            if (getView() != null) {
                InputMethodManager imm = (InputMethodManager) _mActivity
                        .getSystemService(_mActivity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
            }

        }

    }


    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof BaseActivity) {
            ((BaseActivity) activity).setCurrentFragment(this);
        }
    }




    public boolean backPressed() {

        return false;
    }

    public void hideSoftInput() {
        InputMethodManager inputMethodManager = (InputMethodManager) _mActivity
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        try {
            if (inputMethodManager.isActive()) {
                inputMethodManager.hideSoftInputFromWindow(_mActivity
                        .getCurrentFocus().getWindowToken(), 0);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }


    protected Dialog dialog;

    protected void showDialog(String msg){
        if (dialog != null && dialog.isShowing()){
            return;
        }
        dialog = new Dialog(_mActivity);
        //去掉标题线
        dialog.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.common_dialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        baseTextview = dialog.findViewById(R.id.loading);
        TextView tvMsg = dialog.findViewById(R.id.msg);
        if (msg == null) {
            tvMsg.setVisibility(View.GONE);
        } else {
            tvMsg.setVisibility(View.VISIBLE);
            tvMsg.setText(msg);
        }
        //背景透明
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();

        baseAnimation();

        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.gravity = Gravity.CENTER; // 居中位置
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        window.setWindowAnimations(R.style.mystyle);  //添加动画
    }

    protected void showDialog(){
        showDialog(null);
    }


    protected void hideDialog(){
        if (dialog != null && getView() != null && dialog.isShowing()){
            hideBottomUIMenu();
            dialog.dismiss();
            dialog = null;
            if (baseAlphaAnimation != null){
                baseAlphaAnimation.cancel();
            }
        }

    }

    //全屏
    protected void hideBottomUIMenu() {

        if (_mActivity != null){
            if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) {
                View v = _mActivity.getWindow().getDecorView();
                v.setSystemUiVisibility(View.GONE);
            } else if (Build.VERSION.SDK_INT >= 19) {
                View decorView = _mActivity.getWindow().getDecorView();
                int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
                decorView.setSystemUiVisibility(uiOptions);
            }
        }

    }

    public void baseAnimation(){
        baseAlphaAnimation=new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);//设置图片动画属性，各参数说明可参照api
        baseAlphaAnimation.setRepeatCount(-1);//设置旋转重复次数，即转几圈
        baseAlphaAnimation.setDuration(1000);//设置持续时间，注意这里是每一圈的持续时间，如果上面设置的圈数为3，持续时间设置1000，则图片一共旋转3秒钟
        baseAlphaAnimation.setInterpolator(new LinearInterpolator());//设置动画匀速改变。相应的还有AccelerateInterpolator、DecelerateInterpolator、CycleInterpolator等
        baseTextview.setAnimation(baseAlphaAnimation);//设置imageview的动画，也可以myImageView.startAnimation(myAlphaAnimation)
        baseAlphaAnimation.setAnimationListener(new Animation.AnimationListener() { //设置动画监听事件
            @Override
            public void onAnimationStart(Animation arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
                // TODO Auto-generated method stub

            }
            //图片旋转结束后触发事件，这里启动新的activity
            @Override
            public void onAnimationEnd(Animation arg0) {
                // TODO Auto-generated method stub
//                Intent i2 = new Intent(StartActivity.this, MainActivity.class);
//                startActivity(i2);
            }
        });

        baseAlphaAnimation.startNow();
    }
}

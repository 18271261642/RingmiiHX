package com.guider.glu.view;


import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.guider.glu.R;
import com.guider.glu.presenter.GLUServiceManager;
import com.guider.health.common.core.BaseFragment;

/**
 * Created by haix on 2019/6/23.
 */

public class GLUFragment extends BaseFragment implements GLUViewInterface {

    protected final static int TONEXT = 111;
    protected final static int TOHOME = 112;
    protected final static int TOBACK = 113;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        GLUServiceManager.getInstance().setViewObject(this);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            GLUServiceManager.getInstance().setViewObject(this);
        }
    }

    @Override
    public void connectFailed(int status) {

    }

    @Override
    public void startConnect() {

    }

    @Override
    public void connectSuccess() {

    }

    @Override
    public void startInitTick(String time) {

    }

    @Override
    public void stopInit() {

    }

    @Override
    public void initTimeOut() {

    }

    @Override
    public void showMeasureTime(String time) {

    }

    @Override
    public void showMeasureRemind(String remind) {

    }

    @Override
    public void measureFinish() {

    }

    @Override
    public void uploadPersonalInfoSucceed() {

    }

    @Override
    public void uploadPersonalInfoFailed() {

    }

    @Override
    public void measureErrorAndCloseBlueConnect() {

    }

    @Override
    public void goMeasure() {

    }

    @Override
    public void look_error(String error) {

    }

    @Override
    public void showMeasureTime(int time) {

    }

    @Override
    public void insertFingerToMeasure() {

    }

    @Override
    public void time60Begin() {

    }

    @Override
    public void finger_time(int time) {

    }


    protected void showGLUDialog(final String result) {
        showGLUDialog(R.layout.glu_dialog, result);
    }


    protected Dialog dialog;
    protected void showGLUDialog(int layout, String result){
        dialog = new Dialog(_mActivity);
        dialog.setCancelable(false);
        //去掉标题线
        dialog.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
        dialog.setContentView(layout);
        dialog.setCanceledOnTouchOutside(false);
        TextView textView = dialog.findViewById(R.id.showTime);
        //int i = Integer.valueOf(result)/10;
        textView.setText(result);
        //背景透明
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();

        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.gravity = Gravity.CENTER; // 居中位置
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        window.setWindowAnimations(R.style.mystyle);  //添加动画
    }

    protected void hideGLUDialog(){
        if (dialog != null && getView() != null){
            dialog.dismiss();
        }

    }
}

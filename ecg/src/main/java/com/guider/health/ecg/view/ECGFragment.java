package com.guider.health.ecg.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.guider.health.common.core.BaseFragment;
import com.guider.health.ecg.R;
import com.guider.health.ecg.presenter.ECGServiceManager;

/**
 * Created by haix on 2019/6/23.
 */

public class ECGFragment extends BaseFragment implements ECGViewInterface {


    protected final static int TONEXT = 111;
    protected final static int TOHOME = 112;
    protected final static int TOBACK = 113;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ECGServiceManager.getInstance().setViewObject(this);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if (!hidden) {

            ECGServiceManager.getInstance().setViewObject(this);
        }


    }


    @Override
    public void connectFaile(int code) {

    }

    @Override
    public void connectSuccess() {

    }

    @Override
    public void scanFailed() {

    }

    @Override
    public void measureTime(int time) {

    }

    @Override
    public void measureWare(int ware) {

    }

    @Override
    public void measureComplete() {

    }

    @Override
    public void onAnalysisTime(int time) {

    }

    @Override
    public void onAnalysisEnd() {

    }

    @Override
    public void onAnalysisResult(String result) {

    }

    @Override
    public void powerLow(String power) {

    }

    @Override
    public void measureFailed(String msg) {

    }

    @Override
    public void measureTimeOut(String msg , boolean isNeedRemeasure) {

    }


    @Override
    public Context getViewContext() {
        return null;
    }


    protected void showDialog(View view) {
        dialog = new Dialog(_mActivity);
        // 去掉标题线
        dialog.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        // 背景透明
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
}

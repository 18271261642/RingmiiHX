package com.guider.health.bp.view;

import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.guider.health.bp.R;
import com.guider.health.bp.presenter.BPServiceManager;
import com.guider.health.common.core.BaseFragment;

/**
 * Created by haix on 2019/6/24.
 */

public class BPFragment extends BaseFragment implements BPVIewInterface {


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        BPServiceManager.getInstance().setViewObject(this);
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            BPServiceManager.getInstance().setViewObject(this);
        }
    }

    @Override
    public void connectAndMessureIsOK() {

    }

    @Override
    public void startUploadData() {

    }

    @Override
    public void connectNotSuccess() {

    }

    protected void showDialog(int layout) {
        dialog = new Dialog(_mActivity);
        // 去掉标题线
        dialog.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
        dialog.setContentView(layout);
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

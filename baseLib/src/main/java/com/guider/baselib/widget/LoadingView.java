package com.guider.baselib.widget;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.guider.baselib.R;

public class LoadingView extends Dialog {
    private ImageView iv_loading;

    public LoadingView(Context context, boolean outSideCancelable) {
        super(context, R.style.my_dialog);
        initView(context, outSideCancelable);
    }

    @SuppressLint("InflateParams")
    private void initView(Context context, boolean outSideCancelable) {
        this.setCanceledOnTouchOutside(outSideCancelable);
        View contentView = LayoutInflater.from(context).inflate(
                R.layout.view_loading, null, false);
        this.setContentView(contentView);
        iv_loading = findViewById(R.id.iv_loading);
        Animation anim = AnimationUtils.loadAnimation(context, R.anim.loading_anim);
        anim.setInterpolator(new LinearInterpolator());
        anim.setRepeatCount(Animation.INFINITE);
        iv_loading.setAnimation(anim);
        iv_loading.startAnimation(anim);
    }

    @Override
    public void cancel() {
        super.cancel();
        if (iv_loading != null)
            iv_loading.clearAnimation();
    }
}

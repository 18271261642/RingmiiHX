package com.aliyun.rtcdemo.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import androidx.annotation.NonNull;
import android.view.Gravity;
import android.widget.FrameLayout;

import com.aliyun.rtcdemo.utils.DensityUtils;

import org.webrtc.sdk.SophonSurfaceView;

public class SophonContainer extends FrameLayout {

    private SophonSurfaceView mRemoteView;

    public SophonContainer(@NonNull Context context) {
        super(context);
        initView(context);
    }

    private void initView(Context context) {
        initSelfView(context);
        initRemoteView(context);
    }

    private void initSelfView(Context context) {
        LayoutParams layoutParams = new LayoutParams(DensityUtils.dip2px(context, 182),
                DensityUtils.dip2px(context, 142));
        layoutParams.setMargins(20, 20, 20, 0);
        this.setLayoutParams(layoutParams);
        this.setBackgroundColor(Color.WHITE);
    }

    private void initRemoteView(Context context) {
        mRemoteView = new SophonSurfaceView(context);
        LayoutParams layoutParams = new LayoutParams(DensityUtils.dip2px(context, 180),
                DensityUtils.dip2px(context, 140));
        layoutParams.gravity = Gravity.CENTER;
        mRemoteView.setLayoutParams(layoutParams);
        mRemoteView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        mRemoteView.setZOrderOnTop(true);
        mRemoteView.setZOrderMediaOverlay(true);
        this.addView(mRemoteView);

    }

    public SophonSurfaceView getRemoteView() {
        return mRemoteView;
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (mRemoteView != null) {
            mRemoteView.setVisibility(visibility);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mRemoteView.getHolder().getSurface().release();
        mRemoteView = null;
    }
}
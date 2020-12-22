package com.aliyun.rtcdemo.bean;

import com.alivc.rtc.AliRtcEngine;
import org.webrtc.sdk.SophonSurfaceView;

public class ChartUserBean {

    public String mUserId;

    public SophonSurfaceView mCameraSurface;
    public SophonSurfaceView mScreenSurface;
    public String mUserName;
    public boolean mIsOB;

    public boolean mIsSubscribeAudio;
    public boolean mClickableAudio;

    public boolean mIsSubscribeCamera;
    public boolean mClickableCamera;

    public boolean mIsSubscribeDual;
    public boolean mClickableDual;

    public boolean mIsSubscribeScreen;
    public boolean mClickableScreen;

    public boolean mIsMute;

    public boolean mShowControl;

    //是否镜像
    public boolean mIsFlip;

    //待指标暴露后再去获取数据
    public String mVideoBitrate;
    public String mVideoResolution;
    public String mVideoFPS;

    //stats
    public String mStats;


}

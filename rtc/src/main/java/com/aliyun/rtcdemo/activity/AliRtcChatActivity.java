package com.aliyun.rtcdemo.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.alivc.rtc.AliRtcAuthInfo;
import com.alivc.rtc.AliRtcEngine;
import com.alivc.rtc.AliRtcEngine.AliRtcVideoTrack;
import com.alivc.rtc.AliRtcEngineEventListener;
import com.alivc.rtc.AliRtcEngineNotify;
import com.alivc.rtc.AliRtcRemoteUserInfo;
import com.aliyun.rtcdemo.R;
import com.aliyun.rtcdemo.base.AliBaseActivity;
import com.aliyun.rtcdemo.bean.ChartUserBean;
import com.aliyun.rtcdemo.bean.RTCAuthInfo;
import com.aliyun.rtcdemo.service.ForegroundService;
import com.aliyun.rtcdemo.utils.AliRtcConstants;
import com.aliyun.rtcdemo.utils.AppUtils;
import com.aliyun.rtcdemo.utils.DensityUtils;

import org.webrtc.alirtcInterface.AliParticipantInfo;
import org.webrtc.alirtcInterface.AliSubscriberInfo;
import org.webrtc.sdk.SophonSurfaceView;

import java.util.HashMap;
import java.util.Map;

import static com.alivc.rtc.AliRtcEngine.AliRtcAudioTrack.AliRtcAudioTrackNo;
import static com.alivc.rtc.AliRtcEngine.AliRtcRenderMode.AliRtcRenderModeAuto;
import static com.alivc.rtc.AliRtcEngine.AliRtcVideoTrack.AliRtcVideoTrackBoth;
import static com.alivc.rtc.AliRtcEngine.AliRtcVideoTrack.AliRtcVideoTrackCamera;
import static com.alivc.rtc.AliRtcEngine.AliRtcVideoTrack.AliRtcVideoTrackNo;
import static com.alivc.rtc.AliRtcEngine.AliRtcVideoTrack.AliRtcVideoTrackScreen;
import static com.aliyun.rtcdemo.utils.AliRtcConstants.SOPHON_RESULT_SIGNAL_HEARTBEAT_TIMEOUT;
import static com.aliyun.rtcdemo.utils.AliRtcConstants.SOPHON_SERVER_ERROR_POLLING;

/**
 * 音视频通话的activity
 */
public class AliRtcChatActivity extends AliBaseActivity {
    private static final String TAG = AliRtcChatActivity.class.getName();

    /**
     * 用户名
     */
    String mUsername;
    /**
     * 频道名
     */
    String mChannel;
    /**
     * rtcAuthInfo
     */
    RTCAuthInfo mRtcAuthInfo;
    private TextView mFinish;
    private TextView mJoinChannel;
    private TextView loading;
    /**
     * 本地流播放view
     */
    private SophonSurfaceView mLocalView;
    /**
     * 远程流播放view双列集合，uid，远程view
     */
    private Map<String, AliRtcVideoTrack> videoCanvasMap;
    /**
     * SDK提供的对音视频通话处理的引擎类
     */
    private AliRtcEngine mAliRtcEngine;
    private AliRtcEngine.AliVideoCanvas mAliVideoCanvas;
    private boolean mGrantPermission;
    private boolean mIsJoinChannel = false;
    private Bundle mBundle;
    /**
     * 记录用户的集合
     */
    private Map<String, AliRtcVideoTrack> mRemoteTrackMap = new HashMap<>();

    private ChartUserAdapter mUserListApdater;
    private RecyclerView mChartUserListView;
    private String appid;
    private String nonce;
    private long timestamp;
    private String tt_userid;
    private String ff_password;
    private String channelId;
    private String[] gslb;
    private String userid;
    private String password;


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            //setPortraitView();
        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //setLandscapeView();
//            if (mLocalView != null){
//                mLocalView.setRotation(-90);
//            }

        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.alirtc_activity_chat);
//        initEngine();

        getIntentData();

        initView();

        mJoinChannel.setSelected(true);
    }
    private void initEngine() {
        mAliRtcEngine = AliRtcEngine.getInstance(this);


    }
    private void getIntentData() {


//        b.putString("username", mUserName);
//        b.putString("appid", appid);
//        b.putString("nonce", nonce);
//        b.putString("timestamp", timestamp);
//        b.putString("userid", tt_userid);
//        b.putString("gslb", gslb);
//        b.putString("password", ff_password);
//        b.putString("channelId", channelId);

        mBundle = getIntent().getExtras();
        //用户名
        mUsername = mBundle.getString("username");
        //频道
        appid = mBundle.getString("appid");
        nonce = mBundle.getString("nonce");
        timestamp = mBundle.getLong("timestamp");
        userid = mBundle.getString("userid");
        //gslb = mBundle.getStringArray("gslb");
        password = mBundle.getString("password");
        channelId = mBundle.getString("channelId");
        //rtcAuthInfo
        mRtcAuthInfo = (RTCAuthInfo)mBundle.getSerializable("rtcAuthInfo");
    }

    private void initView() {
        loading = findViewById(R.id.loading);
        mFinish = findViewById(R.id.tv_finish);
        mFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                over();
            }
        });
        mJoinChannel = findViewById(R.id.tv_join_channel);
        mJoinChannel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mIsJoinChannel) {
                    over();
                }else {
                    join();
                }
            }
        });
        mLocalView = findViewById(R.id.sf_local_view);
        //mLocalView.setRotation(180);
        View parentView = findViewById(R.id.chart_parent);
        parentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOverButtoun();
            }
        });
        if (AliRtcConstants.BRAND_OPPO.equalsIgnoreCase(Build.BRAND) && AliRtcConstants.MODEL_OPPO_R17.equalsIgnoreCase(
            Build.MODEL)) {
            parentView.setPadding(0, DensityUtils.dip2px(this, 20), 0, 0);
        }
//        mFinish.setOnClickListener(this);
//        mJoinChannel.setOnClickListener(this);

        mUserListApdater = new ChartUserAdapter(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOverButtoun();
            }
        });
        mChartUserListView = findViewById(R.id.chart_content_userlist);
        mChartUserListView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOverButtoun();
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mChartUserListView.setLayoutManager(layoutManager);
        mChartUserListView.addItemDecoration(new BaseRecyclerViewAdapter.DividerGridItemDecoration(
            getResources().getDrawable(R.drawable.chart_content_userlist_item_divider)));
        DefaultItemAnimator anim = new DefaultItemAnimator();
        anim.setSupportsChangeAnimations(false);
        mChartUserListView.setItemAnimator(anim);
        mChartUserListView.setAdapter(mUserListApdater);
        mChartUserListView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(View view) {
                Log.e(TAG, "onChildViewAttachedToWindow : " + view);

            }

            @Override
            public void onChildViewDetachedFromWindow(View view) {
                Log.e(TAG, "onChildViewDetachedFromWindow : " + view);
            }
        });
    }

    private void join() {
        if (mGrantPermission) {
            joinChannel();
        } else {
            setUpSplash();
        }
    }

    private void over() {
        //销毁服务
        if (null != mForeServiceIntent && AppUtils.isServiceRunning(AliRtcChatActivity.this.getApplication(),
                ForegroundService.class.getName())) {
            stopService(mForeServiceIntent);
        }
        //离会
        if (mAliRtcEngine != null) {
            System.out.println("=========sdk版本: "+ mAliRtcEngine.getSdkVersion());
            mAliRtcEngine.setRtcEngineNotify(null);
            mAliRtcEngine.setRtcEngineEventListener(null);
            mAliRtcEngine.stopPreview();
            mAliRtcEngine.leaveChannel();
            mIsJoinChannel = false;
            mAliRtcEngine = null;
        }
        finish();
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            hideOverButtoun();
        }
    };
    private void showOverButtoun() {
        if (mJoinChannel.isSelected()) {
            return;
        }
        mJoinChannel.setSelected(true);
        mJoinChannel.animate().alpha(1).setDuration(400).start();
        handler.sendEmptyMessageDelayed(0, 3000);
    }

    private void hideOverButtoun() {
        if (!mJoinChannel.isSelected()) {
            return;
        }
        mJoinChannel.setSelected(false);
        mJoinChannel.animate().alpha(0).setDuration(400).start();
    }


    @Override
    protected void onResume() {
        super.onResume();
        initValues();
        join();
    }

    private void initValues() {
        if (this.checkPermission(Manifest.permission.CAMERA) || checkPermission(
            Manifest.permission.MODIFY_AUDIO_SETTINGS)) {
            Toast.makeText(this.getApplicationContext(), "需要开启权限才可进行观看", Toast.LENGTH_SHORT).show();
            mGrantPermission = false;
            return;
        }
        mGrantPermission = true;
        // 防止初始化过多
        if (mAliRtcEngine == null) {
            //实例化,必须在主线程进行。
            mAliRtcEngine = AliRtcEngine.getInstance(getApplicationContext());
            //设置事件的回调监听
            mAliRtcEngine.setRtcEngineEventListener(mEventListener);
            //设置接受通知事件的回调
            mAliRtcEngine.setRtcEngineNotify(mEngineNotify);
            //外放声音
            mAliRtcEngine.enableSpeakerphone(true);


            //初始化存储map
            videoCanvasMap = new HashMap<>(16);
            // 初始化本地视图
            initLocalView();
            //开启预览
        }


        startPreview();
    }

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("onPause=======================");
        if (mAliRtcEngine == null) {
            return;
        }
        try {
            mAliRtcEngine.stopPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mLocalView.setVisibility(View.GONE);
    }

    private boolean checkPermission(String permission) {
        try {
            int i = ActivityCompat.checkSelfPermission(this, permission);
            if (i != PackageManager.PERMISSION_GRANTED) {
                return true;
            }
        } catch (RuntimeException e) {
            return true;
        }
        return false;
    }

    private void startPreview() {
        if (mAliRtcEngine == null) {
            return;
        }
        try {
            mAliRtcEngine.startPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mLocalView.setVisibility(View.VISIBLE);
    }

    private void initLocalView() {
        mLocalView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        mLocalView.setZOrderOnTop(false);
        mLocalView.setZOrderMediaOverlay(false);
        mAliVideoCanvas = new AliRtcEngine.AliVideoCanvas();
        mAliVideoCanvas.view = mLocalView;
        mAliVideoCanvas.renderMode = AliRtcRenderModeAuto;
        if (mAliRtcEngine != null) {
            mAliRtcEngine.setLocalViewConfig(mAliVideoCanvas, AliRtcVideoTrackCamera);
        }
    }

    private void joinChannel() {
        if (mAliRtcEngine == null) {
            return;
        }
        this.mIsJoinChannel = true;
//        AliRtcAuthInfo userInfo = new AliRtcAuthInfo();
//        userInfo.setAppid(mRtcAuthInfo.data.appid);
//        userInfo.setNonce(mRtcAuthInfo.data.nonce);
//        userInfo.setTimestamp(mRtcAuthInfo.data.timestamp);
//        userInfo.setUserId(mRtcAuthInfo.data.userid);
//        userInfo.setGslb(mRtcAuthInfo.data.gslb);
//        userInfo.setToken(mRtcAuthInfo.data.token);
//        userInfo.setConferenceId(mChannel);

//        String[] str  =  {"https://rgslb.rtc.aliyuncs.com"};
//        AliRtcAuthInfo userInfo = new AliRtcAuthInfo();
//
//        userInfo.setAppid("wpbd464h");
//        userInfo.setNonce("CK-2bdcdc3fa8359572cd90a3c7dd1d4301");
//        userInfo.setTimestamp(1562488353);
//        userInfo.setUserId("e9895aba-a501-4991-b421-e4fbdb8bd51e");
//        userInfo.setGslb(str);
//        userInfo.setToken("dd1600f29bafe220c98ef42c364afc2f24832d6b57b1f5615b838289aad6c459");
//        userInfo.setConferenceId("ZdHOTw-13");


        String[] str  =  {"https://rgslb.rtc.aliyuncs.com"};
        AliRtcAuthInfo userInfo = new AliRtcAuthInfo();


        //Log.i("haix", "appid: "+appid + "nonce: "+nonce + " timestamp: "+timestamp + )
        userInfo.setAppid(appid);
        userInfo.setNonce(nonce);
        userInfo.setTimestamp(timestamp);
        userInfo.setUserId(userid);
        userInfo.setGslb(str);
        userInfo.setToken(password);
        userInfo.setConferenceId(channelId);

        mAliRtcEngine.setAutoPublish(true, true);
        mAliRtcEngine.joinChannel(userInfo, mUsername);

    }

    private void addOrOBRemoteUser(String uid) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //判断用户是否在线
                if (null == mAliRtcEngine) {
                    return;
                }
                AliRtcRemoteUserInfo remoteUserInfo = mAliRtcEngine.getUserInfo(uid);
                if (remoteUserInfo != null) {
                    mUserListApdater.updateData(convertRemoteUserToOB(remoteUserInfo), true);
                }
            }
        });
    }

    private void removeRemoteUser(String uid) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(AliRtcChatActivity.this, "对方已挂断", Toast.LENGTH_SHORT).show();
                mUserListApdater.remoteData(uid, true);
            }
        });
    }

    private void updateRemoteDisplay(String uid, AliRtcEngine.AliRtcAudioTrack at, AliRtcVideoTrack vt) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (null == mAliRtcEngine) {
                    return;
                }
                AliRtcVideoTrack aliRtcVideoTrack = videoCanvasMap.get(uid);
                AliRtcRemoteUserInfo remoteUserInfo = mAliRtcEngine.getUserInfo(uid);
                // 如果没有，说明已经退出了或者不存在。则不需要添加，并且删除
                if (remoteUserInfo == null) {
                    // remote user exit room
                    Log.e(TAG, "updateRemoteDisplay remoteUserInfo = null, uid = " + uid);
                    videoCanvasMap.remove(uid);
                    return;
                }
                //change
                AliRtcEngine.AliVideoCanvas cameraCanvas = remoteUserInfo.getCameraCanvas();
                AliRtcEngine.AliVideoCanvas screenCanvas = remoteUserInfo.getScreenCanvas();
                //视频情况
                if (vt == AliRtcVideoTrackNo && aliRtcVideoTrack != AliRtcVideoTrackNo) {
                    //没有视频流
                    cameraCanvas = null;
                    screenCanvas = null;
                } else if (vt == AliRtcVideoTrackCamera && aliRtcVideoTrack != AliRtcVideoTrackCamera) {
                    //相机流
                    screenCanvas = null;
                    cameraCanvas = createCanvasIfNull(cameraCanvas);
                    mAliRtcEngine.setRemoteViewConfig(cameraCanvas, uid, AliRtcVideoTrackCamera);
                } else if (vt == AliRtcVideoTrackScreen && aliRtcVideoTrack != AliRtcVideoTrackScreen) {
                    //屏幕流
                    cameraCanvas = null;
                    screenCanvas = createCanvasIfNull(screenCanvas);
                    mAliRtcEngine.setRemoteViewConfig(screenCanvas, uid, AliRtcVideoTrackScreen);
                } else if (vt == AliRtcVideoTrackBoth && aliRtcVideoTrack != AliRtcVideoTrackBoth) {
                    //多流
                    cameraCanvas = createCanvasIfNull(cameraCanvas);
                    mAliRtcEngine.setRemoteViewConfig(cameraCanvas, uid, AliRtcVideoTrackCamera);
                    screenCanvas = createCanvasIfNull(screenCanvas);
                    mAliRtcEngine.setRemoteViewConfig(screenCanvas, uid, AliRtcVideoTrackScreen);
                } else {
                    return;
                }
                videoCanvasMap.put(uid, vt);
                ChartUserBean chartUserBean = convertRemoteUserInfo(remoteUserInfo, cameraCanvas, screenCanvas);
                mUserListApdater.updateData(chartUserBean, true);

            }
        });
    }

    private ChartUserBean convertRemoteUserToOB(AliRtcRemoteUserInfo remoteUserInfo) {
        String uid = remoteUserInfo.getUserID();
        ChartUserBean ret = mUserListApdater.createDataIfNull(uid);
        ret.mUserId = uid;
        ret.mUserName = remoteUserInfo.getDisplayName();
        ret.mIsOB = true;
        ret.mClickableAudio = false;
        ret.mClickableCamera = false;
        ret.mClickableDual = false;
        ret.mClickableScreen = false;
        return ret;
    }

    private AliRtcEngine.AliVideoCanvas createCanvasIfNull(AliRtcEngine.AliVideoCanvas canvas) {
        if (canvas == null || canvas.view == null) {
            canvas = new AliRtcEngine.AliVideoCanvas();
            SophonSurfaceView surfaceView = new SophonSurfaceView(this);
            surfaceView.setZOrderOnTop(true);
            surfaceView.setZOrderMediaOverlay(true);
            canvas.view = surfaceView;
            canvas.renderMode = AliRtcRenderModeAuto;
        }
        return canvas;
    }

    private ChartUserBean convertRemoteUserInfo(AliRtcRemoteUserInfo remoteUserInfo,
        AliRtcEngine.AliVideoCanvas cameraCanvas,
        AliRtcEngine.AliVideoCanvas screenCanvas) {
        String uid = remoteUserInfo.getUserID();
        ChartUserBean ret = mUserListApdater.createDataIfNull(uid);
        ret.mUserId = remoteUserInfo.getUserID();
        ret.mUserName = remoteUserInfo.getDisplayName();

        ret.mIsSubscribeAudio = remoteUserInfo.isRequestSubAudio();
        ret.mClickableAudio = remoteUserInfo.isHasAudio();

        ret.mIsSubscribeCamera = remoteUserInfo.isRequestCamera();
        ret.mClickableCamera = (remoteUserInfo.isHasCameraMaster() || remoteUserInfo.isHasCameraSlave())
            && !mAliRtcEngine.isAudioOnly();

        ret.mIsSubscribeDual = remoteUserInfo.isRequestCameraMaster() || (remoteUserInfo.isHasCameraMaster()
            && !remoteUserInfo.isHasCameraSlave());
        ret.mClickableDual = remoteUserInfo.isSubCamera() && remoteUserInfo.isHasCameraMaster() && remoteUserInfo
            .isHasCameraSlave() &&
            !mAliRtcEngine.isAudioOnly();

        ret.mIsSubscribeScreen = remoteUserInfo.isRequestScreenSharing();
        ret.mClickableScreen = remoteUserInfo.isHasScreenSharing() && !mAliRtcEngine.isAudioOnly();

        ret.mIsMute = remoteUserInfo.isMuteAudioPlaying();

        ret.mCameraSurface = cameraCanvas != null ? cameraCanvas.view : null;

        ret.mScreenSurface = screenCanvas != null ? screenCanvas.view : null;

        ret.mIsOB = false;

        return ret;
    }

    /**
     * 特殊错误码回调的处理方法
     *
     * @param error 错误码
     */
    private void processOccurError(int error) {
        switch (error) {
            case SOPHON_SERVER_ERROR_POLLING:
            case SOPHON_RESULT_SIGNAL_HEARTBEAT_TIMEOUT:
                noSessionExit(error);
                break;
            default:
                break;
        }
    }

    /**
     * 错误处理
     *
     * @param error 错误码
     */
    private void noSessionExit(int error) {
        runOnUiThread(() -> new AlertDialog.Builder(AliRtcChatActivity.this)
            .setTitle("ErrorCode : " + error)
            .setMessage("网络超时，请退出房间")
            .setPositiveButton("确定", (dialog, which) -> {
                dialog.dismiss();
                onBackPressed();
            })
            .create()
            .show());
    }

//    @Override
//    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.tv_finish:
//                //销毁服务
//                if (null != mForeServiceIntent && AppUtils.isServiceRunning(this.getApplicationContext(),
//                        ForegroundService.class.getName())) {
//                    stopService(mForeServiceIntent);
//                }
//                //离会
//                if (mAliRtcEngine != null) {
//                    mAliRtcEngine.setRtcEngineNotify(null);
//                    mAliRtcEngine.setRtcEngineEventListener(null);
//                    mAliRtcEngine.stopPreview();
//                    mAliRtcEngine.leaveChannel();
//                    mIsJoinChannel = false;
//                    mAliRtcEngine = null;
//                }
//                finish();
//                break;
//            case R.id.tv_join_channel:
//                if (mGrantPermission) {
//                    joinChannel();
//                } else {
//                    setUpSplash();
//                }
//                break;
//            default:
//                break;
//        }
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRemoteTrackMap = null;
        videoCanvasMap = null;
        //退出时释放AliRtcEngine
        if (mAliRtcEngine != null) {
            mAliRtcEngine.leaveChannel();
            mAliRtcEngine = null;
        }
    }

    private Intent mForeServiceIntent;
    private AliRtcEngineEventListener mEventListener = new AliRtcEngineEventListener() {
        @Override
        public void onJoinChannelResult(int i) {
            Log.i("haix", "加入频道回调: "+i);
            runOnUiThread(() -> {

                if (i == 0) {

                    //开启前台服务
                    if (null == mForeServiceIntent) {
                        mForeServiceIntent = new Intent(AliRtcChatActivity.this,
                            ForegroundService.class);
                        mForeServiceIntent.putExtras(mBundle);
                    }
                    startService(mForeServiceIntent);
                }
            });
        }

        @Override
        public void onLeaveChannelResult(int i) {
            Log.i("haix", "离开频道回调: "+i);
        }

        @Override
        public void onPublishResult(int i, String s) {

        }

        @Override
        public void onUnpublishResult(int i) {

        }

        @Override
        public void onSubscribeResult(String s, int i, AliRtcVideoTrack aliRtcVideoTrack,
            AliRtcEngine.AliRtcAudioTrack aliRtcAudioTrack) {
            if (i == 0) {
                updateRemoteDisplay(s, aliRtcAudioTrack, aliRtcVideoTrack);
            }
        }

        @Override
        public void onUnsubscribeResult(int i, String s) {
            updateRemoteDisplay(s, AliRtcAudioTrackNo, AliRtcVideoTrackNo);
        }

        @Override
        public void onNetworkQualityChanged(AliRtcEngine.AliRtcNetworkQuality aliRtcNetworkQuality) {
            Log.i("haix", "网络状态变化时回调: "+aliRtcNetworkQuality.toString());
        }

        @Override
        public void onOccurWarning(int i) {
            Log.i("haix", "warning回调: "+i);
        }

        @Override
        public void onOccurError(int error) {
            Log.i("haix", "error回调: "+error);
            //错误处理
            processOccurError(error);
        }
    };

    private AliRtcEngineNotify mEngineNotify = new AliRtcEngineNotify() {
        @Override
        public void onRemoteUserUnPublish(AliRtcEngine aliRtcEngine, String s) {
            Log.i("haix", "当远端用户停止发布时回调: "+s);
            updateRemoteDisplay(s, AliRtcAudioTrackNo, AliRtcVideoTrackNo);
        }

        @Override
        public void onRemoteUserOnLineNotify(String s) {
            Log.i("haix", "远端用户上线回调: "+s);
            //远端用户上线回调
            AliRtcChatActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    hideOverButtoun();
                    loading.setVisibility(View.GONE);
                    Toast.makeText(AliRtcChatActivity.this, "即将接通视频...", Toast.LENGTH_LONG).show();

                }
            });
            addOrOBRemoteUser(s);
        }

        @Override
        public void onRemoteUserOffLineNotify(String s) {

            Log.i("haix", "远端用户下线回调: "+s);
            removeRemoteUser(s);
            over();
        }

        @Override
        public void onRemoteTrackAvailableNotify(String s, AliRtcEngine.AliRtcAudioTrack aliRtcAudioTrack,
            AliRtcVideoTrack aliRtcVideoTrack) {
            Log.i("haix", "远端用户音视频流发生变化时回调: "+s);
            updateRemoteDisplay(s, aliRtcAudioTrack, aliRtcVideoTrack);
        }

        @Override
        public void onSubscribeChangedNotify(String s, AliRtcEngine.AliRtcAudioTrack aliRtcAudioTrack,
            AliRtcVideoTrack aliRtcVideoTrack) {
            Log.i("haix", "订阅结果回调: "+s);
        }

        @Override
        public void onParticipantSubscribeNotify(AliSubscriberInfo[] aliSubscriberInfos, int i) {

        }

        @Override
        public void onFirstFramereceived(String s, String s1, String s2, int i) {

        }

        @Override
        public void onParticipantUnsubscribeNotify(AliParticipantInfo[] aliParticipantInfos, int i) {

        }

        @Override
        public void onBye(int i) {
            Log.i("haix", "被服务器踢出或者频道关闭时回调: "+i);
        }
    };
}

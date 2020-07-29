package cn.wuweikang.view;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.guider.health.common.core.HearRate12;
import com.guider.health.common.device.DeviceInit;
import com.guider.health.common.utils.SkipClick;
import com.wehealth.ecg.jni.analyse.EcgAnalyse;
import com.wehealth.ecg.jni.filter.EcgFilter;
import com.wehealth.ecg.jni.heartrate.EcgHRDetect;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import ble.BleClient;
import cn.wuweikang.BTConnectStreamThread;
import cn.wuweikang.ECGDataAnalyse;
import cn.wuweikang.FileUtil;
import cn.wuweikang.R;
import cn.wuweikang.StringUtil;
import cn.wuweikang.WuweikangData;

/**
 * Created by haix on 2019/7/29.
 */

public class MeasureDevice extends Dao12Interface implements WuweikangData.GetEcgDataCallback {

    public LinkedList<int[]> ecgDataBuffer = new LinkedList<>();
    private List<int[]> mdlists = new ArrayList<>();
    private int[] paceRecBuffer;

    SurfaceHolder sfhWave;
    final static int STEP = 10;

    final static int MARGIN = 4;
    final static int LEADNUM = 12;
    private int waveGain = 2;
    private int waveDisplayType = 0;
    private int waveSingleDisplay_Switch = 0;
    private int waveSpeed = 0;
    private int stepCount = 0;
    private SoundPool sndPool = null;
    private int[] soundPoolId = new int[4];
    private boolean soundOpen = true;
    private int baseY[], baseX[];
    public int currentX, screenWidth, screenHeight;
    private int paceMaker = 2147483647;
    private int oldY[] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private Canvas canvas, waveCanvas;
    private Paint mPaint, pacePaint, greenPaint, greyPaint, redPaint, subGridPaint, topGridPaint;
    private Bitmap backMap, waveMap, bodyLeadMap;
    private SampleDotIntNew sampleDot[];
    private String leadName[] = {"I", "II", "III", "avR", "avL", "avF", "V1", "V2", "V3", "V4", "V5", "V6"};
    private float[] leadX = new float[]{319, 339, 346, 353, 235, 323, 310, 251, 306, 251};//导联点的x坐标
    private float[] leadY = new float[]{138, 157, 157, 157, 158, 158, 193, 120, 120, 193};//导联点的y坐标
    private List<Integer> dot0, dot1, dot2, dot3, dot4, dot5, dot6, dot7, dot8, dot9, dot10, dot11, dot13;
    private List<int[]> bufferList = new ArrayList<>();
    private List<int[]> bufferAuto = new ArrayList<>();
    private float bodyImgW, bodyImgH, percentH, bodyPercentW, side;//人体导联图宽、高，人体图放大或者缩小多少倍，人体图放大后的宽度，人体图两边剩余多少

    private Timer mTimer;
    private MyTimeTask mTimerTask;

//    private ProgressDialog progressDialog;
    private BluetoothAdapter mBtAdapter;

    private boolean MyService_RunFlag = false;
    private BTConnectStreamThread btConnecThread;
    private long ecg2Device_time = -1;

    private boolean bodyLeadState = false;//导联状态 脱落为true
    private boolean bodyLeadState_GREEN = false;//导联首次全部连上  为true
    private int bodyLeadState_GREEN_First = 0; //导联全部连上，等于9时，表示首次连上，显示导联图
    private int bodyLead_Off = 0;
    private boolean isRestartDraw = false;//是否重新绘制背景、心电曲线
    private boolean isDrawECGWave = false;//绘制开关
    private boolean[] isFirstDrawWave = new boolean[]{false, false, false, false, false, false, false, false, false, false, false, false}; //是否首次绘制
    private boolean bodyLeadStateOff = false;//导联脱落标识
    private boolean isFileSave = false;//保存文件标识
    //    private boolean bodyLeadStateSkip = false;//不管导联状态，直接跳过，进入心电曲线画图
    private long playBodyLeadOff_ID = 0;//播放导联脱落音频时间    循环播放导联脱落的音频ID
    private boolean[] bodyLeaData = new boolean[]{true, true, true, true, true, true, true, true, true};
    private int saveCount = 0;
    private int measureTime = 10;
    private int saveFileTotalCount = 500 * measureTime, timeSecondCount, timeMinuteCount, timeHourCount, paceVisible;
    private long bodyLeadStateStart = 0;
    private int baseLineState = -1;//基线是否稳定  0为稳定
    public int saveFileManualCount = 0;
    private int fenbianlv = 1;
    private String patientPhone;
    public final String ECG_DATA_SAVE_NAME = "filename";

    static {
        System.loadLibrary("ecglib");
    }

    private int callData_count = 0;
    private EcgFilter ecgFilter;
    private short FilterBase, FilterMC, FilterAC, FilterLP;
    private EcgHRDetect hrDetect;
    private EcgAnalyse ecgAnalyse;

    private final int DRAW_ECG_WAVE = 1000;
    private final int BT_CONNECT_FAILED = 997;
    private final int BT_CONNECTED = 996;
    private final int BT_CONNECT_DEVICE_ERROR = 899;
    private final int HEART_NUM = 992;
    private final int BODY_LEAD_STATE_OFF = 898;
    private final int SAVE_PDFXML_FILE = 897;
    private final int SAVE_PDFXML_TIMECOUNT = 896;
    private final int SWITCH_SPEED_DRAW_WAVE = 804;
    private final int DRAW_BODY_LEAD_STATE = 803;
    private final int DRAW_BODY_LEAD_START = 802;
    private final int DISMISS_PROGRESS_DIALOG = 801;
    private final int SHOW_PROGRESS_DIALOG = 800;
    private final int DRAW_BODY_LEAD_READSTATE = 805;
    private final int SAVE_DATA_DISTURB = 810;
    private final int DEVICE_ISNOT_YOUR = 811;
    private final int DEVICEPHONE_ISERROR = 812;
    private final int SAVE_PDFXML_FILE_FAIL = 700;


    private TextView result_disu;
    private TextView result_zengyi;
    private TextView result_gongpin;
    private TextView result_jixian;
    private TextView result_jidian;
    private TextView result_ditong;
    private TextView result;
    private TextView result_miao;
    SurfaceView sfvWave;
    private String TAG = "WwkEcgActivity";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        result_disu = (TextView) view.findViewById(R.id.result_disu);
        result_zengyi = (TextView) view.findViewById(R.id.result_zengyi);
        result_gongpin = (TextView) view.findViewById(R.id.result_gongpin);
        result_jixian = (TextView) view.findViewById(R.id.result_jixian);
        result_jidian = (TextView) view.findViewById(R.id.result_jidian);
        result_ditong = (TextView) view.findViewById(R.id.result_ditong);
        result = (TextView) view.findViewById(R.id.result);
        result_miao = (TextView) view.findViewById(R.id.result_miao);
        sfvWave = view.findViewById(R.id.surface);

        view.findViewById(R.id.skip).setVisibility(View.VISIBLE);
        view.findViewById(R.id.skip).setOnClickListener(new SkipClick(this , DeviceInit.DEV_ECG_12));

        clearSaveCache();
        WuweikangData.getInstance().setGetEcgData(this);

        initView();
        initData();
    }

    public void toNext() {
        BleClient.instance().disconnect(BleClient.instance().popStagingDevice());
        WuweikangData.getInstance().disconnect();
        start(new ResultMeasure());
    }


    private void log(String msg) {
        Log.i(TAG, msg);
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {

        @Override
        public void handleMessage( Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case DRAW_ECG_WAVE://开始测量心电，并计时
                    if (_mActivity.isFinishing()) {
                        return;
                    }
//                    progressDialog.dismiss();
                    clearCache();
                    mTimerTask = new MyTimeTask();
                    mTimer.schedule(mTimerTask, 4, 26);
                    isRestartDraw = true;
                    break;
                case BT_CONNECT_FAILED://连接失败
                    if (_mActivity.isFinishing()) {
                        return;
                    }
//                    progressDialog.dismiss();
                    String reson = (String) msg.obj;
                    break;
                case DEVICE_ISNOT_YOUR:
                    if (_mActivity.isFinishing()) {
                        return;
                    }
//                    progressDialog.dismiss();
                    break;
                case BT_CONNECTED:
                    if (_mActivity.isFinishing()) {
                        return;
                    }
//                    progressDialog.dismiss();
                    break;
                case HEART_NUM://心率显示
                    int heart = (Integer) msg.obj;
                    if (heart > 0) {
                        result.setText(String.valueOf(heart));
                    } else if (heart < 0) {
                        result.setText("---");
                    }
                    break;
                case SAVE_PDFXML_FILE:
                    // todo 数据保存完毕
                    if (_mActivity.isFinishing()) {
                        return;
                    }
                    toNext();
                    break;

                case SAVE_PDFXML_FILE_FAIL:
                    // todo 有干扰,数据保存失败
                    if (_mActivity.isFinishing()) {
                        return;
                    }
                    restart();
                    break;

                case SAVE_DATA_DISTURB:
                    if (_mActivity.isFinishing()) {
                        return;
                    }
//                    progressDialog.dismiss();
                    // todo 保存数据失败 ?
                    break;
                case SAVE_PDFXML_TIMECOUNT://测量心电时的计时显示
                    result_miao.setText(measureTime - timeSecondCount + getResources().getString(R.string.wwk_second));
                    break;
                case BT_CONNECT_DEVICE_ERROR:
                    isDrawECGWave = false;
                    if (mTimer != null) {
                        mTimer.cancel();
                    }
                    //蓝牙接收数据出现故障，请重新开始测试
                    break;
                case SHOW_PROGRESS_DIALOG:
                    if (_mActivity.isFinishing()) {
                        return;
                    }
//                    if (progressDialog != null) {
//                        String dialog_message = (String) msg.obj;
//                        progressDialog.setMessage(dialog_message);
//                        if (!progressDialog.isShowing()) {
//                            progressDialog.show();
//                        }
//                    }
                    break;
                case DISMISS_PROGRESS_DIALOG:
                    if (_mActivity.isFinishing()) {
                        return;
                    }
//                    progressDialog.dismiss();
                    break;
                case DRAW_BODY_LEAD_STATE://导联连接成功后， 延时两秒钟
                    long end = System.currentTimeMillis();
                    if (end - bodyLeadStateStart >= 1950) {//是延时两秒钟
                        initAnalyse();
                        baseLineState = -1;
                        isDrawECGWave = false;
                        bodyLeadState_GREEN = false;
                        isRestartDraw = true;
                        isFileSave = true;
                        clearCache();
                        sndPool.play(soundPoolId[2], 1.0F, 1.0F, 1, 0, 1.0F);
                    }
                    break;
//                case DRAW_BODY_LEAD_START://导联连接成功，开始测量心电
//                    if (playBodyLeadOff_ID != -1) {
//                        sndPool.stop(playBodyLeadOff_ID);
//                        playBodyLeadOff_ID = -1;
//                    }
//                    break;
                case BODY_LEAD_STATE_OFF://导联脱落时播放音频  绘制导联脱落的画面
                    isFileSave = false;
                    baseLineState = -1;
                    long currenTime = System.currentTimeMillis();
                    if ((currenTime - playBodyLeadOff_ID) > 1860)
                        sndPool.play(soundPoolId[1], 1.0F, 1.0F, 1, 0, 1.0F);
                    playBodyLeadOff_ID = System.currentTimeMillis();
                    break;
                case SWITCH_SPEED_DRAW_WAVE://切换纸速
                    if (_mActivity.isFinishing()) {
                        return;
                    }
//                    progressDialog.dismiss();
                    isDrawECGWave = false;
                    break;
                case DRAW_BODY_LEAD_READSTATE:
                    drawBodyLeadReadState();
                    break;
                case DEVICEPHONE_ISERROR:
                    if (_mActivity.isFinishing()) {
                        return;
                    }
//                    progressDialog.dismiss();
                    clearCache();
                    clearSaveCache();
                    initAnalyse();
                    baseLineState = -1;
                    currentX = 30;
                    isFirstDrawWave = new boolean[]{false, false, false, false, false, false, false, false, false, false, false, false};
                    isRestartDraw = true;
                    break;
                default:
                    break;
            }
        }
    };

    private void restart() {
        Toast.makeText(_mActivity, _mActivity.getResources().getString(R.string.wwk_keeep_hoding), Toast.LENGTH_SHORT).show();
        startWithPop(new MeasureDevice());
    }

    private void initView() {
        int height = getResources().getDisplayMetrics().heightPixels;
        if (height <= 800) {
            fenbianlv = 1;
        } else if (height > 800) {
            fenbianlv = 2;
        }

    }

    private void initData() {//获取PopupWindow中View的宽高
        saveFileTotalCount = 500 * measureTime;
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);

        baseY = new int[LEADNUM];
        currentX = 30;
        baseX = new int[LEADNUM];
        initCanvas();
        initSampleDot();

        hrDetect = new EcgHRDetect();
        ecgAnalyse = new EcgAnalyse();
        ecgAnalyse.Axis = new int[3];
        ecgAnalyse.ecgResult = new int[12];
        ecgFilter = new EcgFilter();
        FilterBase = 3;
        FilterMC = 12;
        FilterLP = 36;
        FilterAC = 22;
        initAnalyse();

        sndPool = new SoundPool(8, AudioManager.STREAM_MUSIC, 0);

        try {
            soundPoolId[0] = sndPool.load(getResources().getAssets().openFd("sounds/heart_beep.ogg"), 1);
            soundPoolId[1] = sndPool.load(getResources().getAssets().openFd("sounds/lead_off.ogg"), 1);
            soundPoolId[2] = sndPool.load(getResources().getAssets().openFd("sounds/start_ad.ogg"), 1);
            soundPoolId[3] = sndPool.load(getResources().getAssets().openFd("sounds/stop_ad.ogg"), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        mTimer = new Timer();
//        progressDialog = new ProgressDialog(_mActivity);
//        progressDialog.setMessage("正在连接蓝牙设备");
//        WindowManager.LayoutParams params = progressDialog.getWindow().getAttributes();
//        progressDialog.getWindow().setGravity(Gravity.TOP);
//        params.y = 60;
//        progressDialog.getWindow().setAttributes(params);
//        progressDialog.setCancelable(false);

        paceRecBuffer = new int[saveFileTotalCount];

        MyService_RunFlag = true;

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                clearCache();
                mTimerTask = new MyTimeTask();
                mTimer.schedule(mTimerTask, 4, 26);
                isRestartDraw = true;
            }
        }, 2000);

    }

    private void initCanvas() {
        bodyImgW = 290;
        bodyImgH = 264;
        sfhWave = sfvWave.getHolder();
        /* 绘制前获取控件尺寸 */
        sfvWave.post(new Runnable() {
            @Override
            public void run() {
                screenWidth = sfvWave.getMeasuredWidth();
                screenHeight = sfvWave.getMeasuredHeight();
                percentH = screenHeight / bodyImgH;
                bodyPercentW = percentH * bodyImgW;
                side = screenWidth - bodyPercentW;
                for (int i = 0; i < 10; i++) {
                    leadX[i] = (leadX[i] - 136) * percentH + side / 2;
                    leadY[i] = leadY[i] * percentH;
                }
                backMap = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_8888);
                waveMap = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_8888);
                waveCanvas = new Canvas(waveMap);
                bodyLeadMap = BitmapFactory.decodeResource(getResources(), R.mipmap.luoti);
                waveCanvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                handler.sendEmptyMessageDelayed(DRAW_BODY_LEAD_READSTATE, 100);
            }
        });
        mPaint = new Paint();
        greenPaint = new Paint();
        redPaint = new Paint();
        greyPaint = new Paint();
        greenPaint.setColor(Color.GREEN);
        greyPaint.setColor(Color.parseColor("#666666"));
        greyPaint.setStrokeWidth(1);
        redPaint.setColor(Color.RED);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth((float) 1.5);
        pacePaint = new Paint();
        pacePaint.setStrokeWidth(1.5f);
        pacePaint.setColor(Color.RED);
        subGridPaint = new Paint();
        topGridPaint = new Paint();
        topGridPaint.setColor(Color.parseColor("#C6E4FC"));// 粗线
        topGridPaint.setStrokeWidth((float) 1.5);
        subGridPaint.setColor(Color.parseColor("#c7e5fd")); // 细线
    }

    private void initAnalyse() {
        hrDetect.initHr(3495.2533333f * 3);
        ecgFilter.initFilter(FilterBase, FilterMC, FilterAC, FilterLP);
        ecgFilter.initBaseLineJudge();
    }


    /**
     * 给设备发送停止命令
     **/
    private void stopDataSource() {
        if (btConnecThread != null) {
            btConnecThread.stopBlueTooth();
        }
        if (mTimerTask != null) {
            mTimerTask.cancel();
        }
        bodyLeadState_GREEN_First = 0;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (MyService_RunFlag) {
            isDrawECGWave = false;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (MyService_RunFlag) {
            isDrawECGWave = true;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            stopDataSource();
        } catch (Exception e) {
        }

        try {
            if (sndPool != null) {
//                sndPool.stop(playBodyLeadOff_ID);
                for (int i = 0; i < soundPoolId.length; i++) {
                    sndPool.unload(soundPoolId[i]);
                }
                sndPool.release();
            }
        } catch (Exception e) {
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopDataSource();
        try {
            if (sndPool != null) {
                for (int i = 0; i < soundPoolId.length; i++) {
                    sndPool.unload(soundPoolId[i]);
                }
                sndPool.release();
            }
        } catch (Exception e) {
        }
        try {
            if (backMap != null && !backMap.isRecycled()) {
                backMap.recycle();
                backMap = null;
            }
            if (waveMap != null && !waveMap.isRecycled()) {
                waveMap.recycle();
                waveMap = null;
            }
            if (bodyLeadMap != null && !bodyLeadMap.isRecycled()) {
                bodyLeadMap.recycle();
                bodyLeadMap = null;
            }
            System.gc();
        } catch (Exception e) {
        }
    }

    /**
     * 画曲线图
     **/
    private void SimpleDraw(List<int[]> datas) {
        DrawEcgWave(datas);
        currentX += stepCount;
        if (waveDisplayType == 0) {
            if (currentX >= (screenWidth / 2)) {
                currentX = 30;
            }
        } else {
            if (currentX >= screenWidth) {
                currentX = 30;
            }
        }
    }

    /**
     * 画波形图
     **/
    private void DrawEcgWave(List<int[]> datas) {
        if (baseLineState == -1) {
            mPaint.setColor(Color.parseColor("#faa519"));
        } else {
            mPaint.setColor(Color.BLUE);
        }
        List<int[]> leaData = StringUtil.getEcgData(datas);
        int lockStep = 0;
        if (waveDisplayType == 2) {
            dot13 = sampleDot[12].SnapshotSample(leaData.get(waveSingleDisplay_Switch));
            if (dot13 == null || dot13.isEmpty()) {
                stepCount = 0;
                return;
            }
            lockStep = dot13.size();
        } else {
            dot0 = sampleDot[0].SnapshotSample(leaData.get(0));//
            dot1 = sampleDot[1].SnapshotSample(leaData.get(1));//
            dot2 = sampleDot[2].SnapshotSample(leaData.get(2));//
            dot3 = sampleDot[3].SnapshotSample(leaData.get(3));//
            dot4 = sampleDot[4].SnapshotSample(leaData.get(4));//
            dot5 = sampleDot[5].SnapshotSample(leaData.get(5));//
            dot6 = sampleDot[6].SnapshotSample(leaData.get(6));//
            dot7 = sampleDot[7].SnapshotSample(leaData.get(7));//
            dot8 = sampleDot[8].SnapshotSample(leaData.get(8));//
            dot9 = sampleDot[9].SnapshotSample(leaData.get(9));//
            dot10 = sampleDot[10].SnapshotSample(leaData.get(10));//
            dot11 = sampleDot[11].SnapshotSample(leaData.get(11));//
            if (dot0 == null || dot0.isEmpty()) {
                stepCount = 0;
                return;
            }
            lockStep = dot0.size();
        }
        stepCount = lockStep;
        if (stepCount > 0) {
            Rect rect = new Rect();
            if (currentX == 30) {
                rect.set(currentX, 0, currentX + MARGIN + stepCount + 6 * MARGIN, screenHeight);
            } else {
                rect.set(currentX + MARGIN, 0, currentX + MARGIN + stepCount + 5 * MARGIN, screenHeight);
            }

            waveCanvas.drawBitmap(backMap, rect, rect, null);
            if (waveDisplayType == 0) {
                if (currentX == 30) {
                    rect.set(currentX + baseX[6], 0, currentX + MARGIN + baseX[6] + stepCount + 6 * MARGIN, screenHeight);
                } else {
                    rect.set(currentX + MARGIN + baseX[6], 0, currentX + MARGIN + baseX[6] + stepCount + 5 * MARGIN, screenHeight);
                }
                waveCanvas.drawBitmap(backMap, rect, rect, null);
            }
        }
        if (waveDisplayType == 2) {
            int step = 0;
            for (int k = 0; k < lockStep; k++) {
                DrawLeadWave(dot13.get(k), 5, currentX + step);
                step++;
            }
        } else {
            int step = 0;
            for (int k = 0; k < lockStep; k++) {
                DrawLeadWave(dot0.get(k), 0, currentX + step);
                DrawLeadWave(dot1.get(k), 1, currentX + step);
                DrawLeadWave(dot2.get(k), 2, currentX + step);
                DrawLeadWave(dot3.get(k), 3, currentX + step);
                DrawLeadWave(dot4.get(k), 4, currentX + step);
                DrawLeadWave(dot5.get(k), 5, currentX + step);
                DrawLeadWave(dot6.get(k), 6, currentX + step);
                DrawLeadWave(dot7.get(k), 7, currentX + step);
                DrawLeadWave(dot8.get(k), 8, currentX + step);
                DrawLeadWave(dot9.get(k), 9, currentX + step);
                DrawLeadWave(dot10.get(k), 10, currentX + step);
                DrawLeadWave(dot11.get(k), 11, currentX + step);
                step++;
            }
        }

        if (stepCount > 0) {
            canvas = sfhWave.lockCanvas();
            if (canvas != null) {
                canvas.drawColor(getResources().getColor(R.color.white));

                canvas.drawBitmap(waveMap, 0, 0, null);
                DrawWaveTag();
                sfhWave.unlockCanvasAndPost(canvas);
            }
        }
    }

    /**
     * 波形图的具体画法
     **/
    private void DrawLeadWave(int da, int leadNum, int offset) {
        int y;
        int oldX;
        int i;
        oldX = offset;
        i = offset + 1;
        if (isFirstDrawWave[leadNum]) {
            if (da == paceMaker) {
                y = oldY[leadNum];
                waveCanvas.drawLine(oldX + MARGIN + baseX[leadNum],
                        oldY[leadNum] - 15,// + baseY[leadNum]
                        oldX + MARGIN + baseX[leadNum],
                        oldY[leadNum] + 15,// + baseY[leadNum]
                        pacePaint);
            } else {
                y = (baseY[leadNum] + (-da) / (105 * waveGain) * fenbianlv); //getYLead(leadNum, da); //192  192 * 2
                waveCanvas.drawLine(oldX + MARGIN + baseX[leadNum],
                        oldY[leadNum],// + baseY[leadNum]
                        i + MARGIN + baseX[leadNum],
                        y,// + baseY[leadNum]
                        mPaint);
            }
            oldY[leadNum] = y;
        } else {
            isFirstDrawWave[leadNum] = true;
            if (da == paceMaker) {
                y = oldY[leadNum];
            } else {
                y = (baseY[leadNum] + (-da) / (105 * waveGain) * fenbianlv); //getYLead(leadNum, da); //192  192 * 2
            }
            oldY[leadNum] = y;
        }
    }

    private void drawBodyLeadReadState() {
        canvas = sfhWave.lockCanvas();
        int circleRadius = 6 * fenbianlv;
        if (canvas != null) {
            canvas.drawColor(Color.BLACK);
            Rect rect = new Rect();
            rect.set((int) side / 2, 0, (int) (side / 2 + bodyPercentW), screenHeight);//(int)bodyPercentW
            canvas.drawBitmap(bodyLeadMap, null, rect, null);
            sfhWave.unlockCanvasAndPost(canvas);
        }
    }

    private void drawBodyLeadSpot() {
        int circleRadius = 6 * fenbianlv;
        redPaint.setAntiAlias(false);
        greenPaint.setAntiAlias(false);
        canvas = sfhWave.lockCanvas();
        if (canvas != null) {
            canvas.drawColor(Color.BLACK);
            Rect rect = new Rect();
            rect.set((int) side / 2, 0, (int) (side / 2 + bodyPercentW), screenHeight);//(int)bodyPercentW
            canvas.drawBitmap(bodyLeadMap, null, rect, null);
            for (int i = 0; i < bodyLeaData.length; i++) {
                if (bodyLeaData[i]) {
                    canvas.drawCircle(leadX[i], leadY[i], circleRadius, redPaint);
                } else {
                    canvas.drawCircle(leadX[i], leadY[i], circleRadius, greenPaint);
                }
            }
            canvas.drawCircle(leadX[9], leadY[9], circleRadius, greenPaint);
            sfhWave.unlockCanvasAndPost(canvas);
        }
    }

    /**
     * 确定抽点
     * todo 重要
     **/
    private void initSampleDot() {
        sampleDot = new SampleDotIntNew[LEADNUM + 1];
        for (int i = 0; i < LEADNUM + 1; i++) {
            sampleDot[i] = new SampleDotIntNew(500);//, 126 / (waveSpeed + 1)
        }
        if (fenbianlv == 1) {//手机分辨率小于等于800时
            initDeSampleDot(125 / (waveSpeed + 1));
        } else {
            initDeSampleDot(248 / (waveSpeed + 1));
        }
    }

    private void initDeSampleDot(int desDot) {
        for (int i = 0; i < LEADNUM + 1; i++) {
            sampleDot[i].setDesSampleDot(desDot);
        }
    }

    /**
     * 绘制背景
     **/
    private void DrawBackBmp() {
        if (waveDisplayType == 0) {
            for (int i = 0; i < LEADNUM; i++) {
                baseY[i] = screenHeight / LEADNUM * 2 * (i % 6 + 1) - 50;
            }
            for (int i = 0; i < LEADNUM; i++) {
                if (i < 6) {
                    baseX[i] = 0;
                } else {
                    baseX[i] = screenWidth / 2 + MARGIN;
                }
            }
        } else {
            for (int i = 0; i < LEADNUM; i++) {
                baseY[i] = screenHeight / LEADNUM * (i % 12 + 1) - 20;
            }
            for (int i = 0; i < LEADNUM; i++) {
                baseX[i] = 0;
            }
        }
        canvas = new Canvas(backMap);
        // 绘制背景
        canvas.drawColor(Color.parseColor("#ffffff"));
        // 绘制格子
        DrawVerticalLine(fenbianlv * 5);
        DrawHorizontalLine(fenbianlv * 5);
        if (waveDisplayType == 0) {
            DrawWaveGain(0, baseY[2] + 20);
            DrawWaveGain(baseX[8], baseY[2] + 20);
        } else {
            DrawWaveGain(0, baseY[5] + 20);
        }
        canvas = new Canvas(waveMap);
        canvas.drawBitmap(backMap, 0, 0, null);
    }

    private void DrawWaveGain(int x, int y) {
        canvas.drawLine(x + MARGIN,
                y + 5,
                x + MARGIN + 5,
                y + 5,
                greyPaint);

        canvas.drawLine(x + MARGIN + 5,
                y + 5,
                x + MARGIN + 5,
                y - (20 / waveGain) * 5 * fenbianlv + 5,
                greyPaint);

        canvas.drawLine(x + MARGIN + 5,
                y - (20 / waveGain) * 5 * fenbianlv + 5,
                x + MARGIN + 5 + 10,
                y - (20 / waveGain) * 5 * fenbianlv + 5,
                greyPaint);

        canvas.drawLine(x + MARGIN + 5 + 10,
                y + 5,
                x + MARGIN + 5 + 10,
                y - (20 / waveGain) * 5 * fenbianlv + 5,
                greyPaint);

        canvas.drawLine(x + MARGIN + 5 + 10,
                y + 5,
                x + MARGIN + 5 + 10 + 5,
                y + 5,
                greenPaint);
    }

    /**
     * 画水平线条
     **/
    private void DrawVerticalLine(int step) {
        int j = 0;
        for (int i = 0; i <= screenHeight; i += step) {
            if (j == 0) {
                canvas.drawLine(MARGIN,
                        i + MARGIN,
                        screenWidth + step,
                        i + MARGIN,
                        topGridPaint);
            } else {
                canvas.drawLine(MARGIN,
                        i + MARGIN,
                        screenWidth + step,
                        i + MARGIN,
                        subGridPaint);
            }
            j++;
            if (j >= 5)
                j = 0;
        }
    }

    /**
     * 画垂直线条
     **/
    private void DrawHorizontalLine(int step) {
        int j = 0;
        for (int i = 0; i <= screenWidth; i += step) {
            if (j == 0) {
                canvas.drawLine(i + MARGIN,
                        MARGIN,
                        i + MARGIN,
                        screenHeight + step,
                        topGridPaint);
            } else {
                canvas.drawLine(i + MARGIN,
                        MARGIN,
                        i + MARGIN,
                        screenWidth + step,
                        subGridPaint);
            }
            j++;
            if (j >= 5)
                j = 0;
        }
    }

    /**
     * 画波形名称
     **/
    private void DrawWaveTag() {
        greyPaint.setTextSize(18f);
        int mY;
        if (waveDisplayType == 0) {
            mY = 22;
        } else {
            mY = 5;
        }
        if (waveDisplayType == 2) {
            canvas.drawText(leadName[waveSingleDisplay_Switch], baseX[3] + 10, baseY[3] - mY, greyPaint);
        } else {
            for (int i = 0; i < LEADNUM; i++) {
                canvas.drawText(leadName[i], baseX[i] + 10, baseY[i] - mY, greyPaint);
            }
        }
    }

    private void clearSaveCache() {
        baseLineState = -1;
        bufferAuto.clear();
        saveCount = 0;
    }

    /**
     * 清除缓存空间
     **/
    private void clearCache() {
        synchronized (ecgDataBuffer) {
            ecgDataBuffer.clear();
        }
        currentX = 30;
    }

    @Override
    public void getEcgData(int[] data, int len, boolean[] leadState, boolean pace) {
        for (int i = 0; i < leadState.length; i++) {
            if (leadState[i]) {//有导联脱落
                bodyLeadState = true;
                isRestartDraw = true;
                bodyLeadState_GREEN_First = 0;
                break;
            }
            bodyLeadState = false;
            bodyLeadState_GREEN_First++;
        }
        if (bodyLeadState) {//导联脱落，绘制导联图；
//            if (!bodyLeadStateOff) {//第一次
//                handler.sendEmptyMessage(BODY_LEAD_STATE_OFF);
//                handler.removeMessages(DRAW_BODY_LEAD_STATE);
//                bodyLeadStateOff = true;
//            }
            if (bodyLead_Off == 300) {
                handler.sendEmptyMessage(BODY_LEAD_STATE_OFF);
                handler.removeMessages(DRAW_BODY_LEAD_STATE);
            }
            if (bodyLead_Off % 1500 == 0) {
                handler.sendEmptyMessage(BODY_LEAD_STATE_OFF);
            }
            bodyLead_Off++;
            bodyLeaData = Arrays.copyOf(leadState, leadState.length);
            clearCache();
            clearSaveCache();
            return;
        }
        if (bodyLeadState_GREEN_First == 9) {//首次连接成功后，显示导联连接成功两秒中
            bodyLead_Off = 0;
//            bodyLeadStateOff = false;
            bodyLeadStateStart = System.currentTimeMillis();
            isDrawECGWave = true;
            bodyLeadState_GREEN = true;
//            handler.sendEmptyMessage(DRAW_BODY_LEAD_START);
            bodyLeaData = Arrays.copyOf(leadState, leadState.length);
            handler.sendEmptyMessageDelayed(DRAW_BODY_LEAD_STATE, 2000);
            return;
        }
        if (bodyLeadState_GREEN_First > 50000) {//防止数据太大
            bodyLeadState_GREEN_First = 27;
        }
        int[] tempData = new int[data.length];
        for (int i = 0; i < data.length; i++) {
            tempData[i] = data[i];
        }
        ecgFilter.filter(data, 1, 12);
        if (baseLineState == -1) {
            baseLineState = ecgFilter.isBaseLineStable(data, 1, 12);
        }
        int heart = hrDetect.hrDetect(data, 1, 12);
        if (heart > 0) {
            try {
                if (soundOpen) {
                    sndPool.play(soundPoolId[0], 1.0F, 1.0F, 1, 0, 1.0F);
                }
            } catch (Exception e) {
            }
            Message msg = handler.obtainMessage(HEART_NUM);
            msg.obj = heart;
            handler.sendMessage(msg);
        }
        if (isFileSave && baseLineState == 0) {//保存的数据需要滤波处理
            bufferAuto.add(data);
            if (paceRecBuffer.length > saveCount) {
                if (pace) {
                    paceRecBuffer[saveCount] = 1;
                } else {
                    paceRecBuffer[saveCount] = 0;
                }
                saveCount++;
            }

            if (saveCount % 500 == 0) {
                timeSecondCount = saveCount / 500;
                handler.sendEmptyMessage(SAVE_PDFXML_TIMECOUNT);
            }
            if (saveCount == saveFileTotalCount) {
                sndPool.play(soundPoolId[3], 1.0F, 1.0F, 1, 0, 1.0F);
                stopDataSource();
                isFileSave = false;
                isDrawECGWave = true;
                List<int[]> bufferLists = bufferAuto;
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("ecgDataBuffer", bufferLists);
                map.put("patientPhone", String.valueOf(patientPhone));
                map.put("paceBuffer", paceRecBuffer);
                new Thread(new AutoTask(map)).start();
            }
        }
        if (isDrawECGWave) {//绘制开关
            return;
        }
        bufferList.add(data);
        callData_count++;
        if (callData_count > 15) {
            synchronized (ecgDataBuffer) {
                ecgDataBuffer.addAll(bufferList);
                bufferList.clear();
            }
            callData_count = 0;
        }
    }

    class MyTimeTask extends TimerTask {

        @Override
        public void run() {
            if (bodyLeadState || bodyLeadState_GREEN) {//导联脱落；首次导联状态全部连接时
                waveCanvas.drawColor(Color.BLACK);
                drawBodyLeadSpot();
                return;
            }
            if (isRestartDraw) {
                hrDetect.initHr(3495.2533333f * 3);
                DrawBackBmp();
                isRestartDraw = false;
            }

            if (isDrawECGWave) {
                mdlists.clear();
                return;
            }
            if (ecgDataBuffer.size() > (STEP)) {
                synchronized (ecgDataBuffer) {
                    int num = ecgDataBuffer.size();
                    for (int i = num - 1; i >= 0; i--) {
                        mdlists.add(ecgDataBuffer.removeFirst());
                    }
                }
                SimpleDraw(mdlists);
                mdlists.clear();
            }
        }
    }

    private class AutoTask implements Runnable {
        private Map<String, Object> map;

        public AutoTask(Map<String, Object> mapDatas) {
            map = mapDatas;
        }

        @SuppressWarnings("unchecked")
        @Override
        public void run() {
            handler.sendEmptyMessage(SHOW_PROGRESS_DIALOG);

            List<int[]> ecgDataBuffer = (List<int[]>) map.get("ecgDataBuffer");
            int[] paceBuffer = (int[]) map.get("paceBuffer");

            Map<String, String> analyses = new HashMap<String, String>();
            int[] ecgAnas = StringUtil.getEcgDataINTs(ecgDataBuffer);//数据转换

            EcgAnalyse ecgAnalyse = new EcgAnalyse();
            ecgAnalyse.Axis = new int[3];
            ecgAnalyse.ecgResult = new int[12];

            // 自动分析结论
            ecgAnalyse.initEcgAnalyseLib(3495.2533333f * 3);
            Log.i("wuweikangResult", ecgDataBuffer.size() + " -------ecgDataBuffer ");
            Log.i("wuweikangResult", paceBuffer.length + " ----------paceBuffer ");
            Log.i("wuweikangResult", ecgAnas.length + " ----------ecgAnas ");
//            int analyseResult = ecgAnalyse.analyseEcgData(ecgAnalyse, ecgAnas, ecgDataBuffer.size(), 12, paceBuffer, 0, 0);
            int analyseResult = ecgAnalyse.analyseEcgData(ecgAnalyse, ecgAnas, ecgDataBuffer.size(), 12, paceBuffer, 0, 0);
            if (analyseResult == 1) {
                // TODO 测量中有干扰，提醒用户重新测量
                Log.i("wuweikangResult", ecgAnas.length + " ----------有干扰..... ");
                stopDataSource();
                handler.sendEmptyMessage(SAVE_PDFXML_FILE_FAIL);
                return;
            }

            int[] axis = ecgAnalyse.Axis;
            int[] resultCode = ecgAnalyse.ecgResult;//结论数组，已经写好了工具类
            Set<String> set = ECGDataAnalyse.getAnalyseResult(getContext(), resultCode);
            Log.i("wuweikangResult", set == null ? "null" : set.size() + " ----------获取结论 ");
            int typEcg = ecgAnalyse.typeEcg;//结论的大类
            float version = ecgAnalyse.getEcgDllVersion();//心电分析库的版本名

            String classResult = "";
            if (typEcg == 500) {
                classResult = ECGDataAnalyse.getAnalyseResultType(getContext(), 0); // ECGDataAnalyse.analyseType[0];
            }
            if (typEcg == 501) {
                classResult = ECGDataAnalyse.getAnalyseResultType(getContext(), 1); // ECGDataAnalyse.analyseType[1];
            }
            if (typEcg == 502) {
                classResult = ECGDataAnalyse.getAnalyseResultType(getContext(), 2); // ECGDataAnalyse.analyseType[2];
            }
            if (typEcg == 503) {
                classResult = ECGDataAnalyse.getAnalyseResultType(getContext(), 3); // ECGDataAnalyse.analyseType[3];
            }

            int hr = ecgAnalyse.HR;
            int pr = ecgAnalyse.PR;
            int qrs = ecgAnalyse.QRS;
            int qt = ecgAnalyse.QT;
            int qtc = ecgAnalyse.QTc;
            int rr = ecgAnalyse.RR;
            float rv5 = ((float) ecgAnalyse.RV5) / 1000;
            float sv1 = ((float) ecgAnalyse.SV1) / 1000;
            analyses.put("HeartRate", String.valueOf(hr));
            analyses.put("PRInterval", String.valueOf(pr));
            analyses.put("RRInterval", String.valueOf(rr));
            analyses.put("QRSDuration", String.valueOf(qrs));
            analyses.put("QTD", String.valueOf(qt));
            analyses.put("QTC", String.valueOf(qtc));
            analyses.put("RV5", String.valueOf(rv5));
            analyses.put("SV1", String.valueOf(sv1));
            analyses.put("RV5SV1", String.valueOf((Math.abs(rv5) + Math.abs(sv1))));
            analyses.put("PAxis", String.valueOf(axis[0]));
            analyses.put("QRSAxis", String.valueOf(axis[1]));
            analyses.put("TAxis", String.valueOf(axis[2]));

            if ("0".equals(String.valueOf(hr))) {
                // TODO 测量中有干扰，提醒用户重新测量
                Log.i("wuweikangResult", ecgAnas.length + " ----------有干扰..... ");
                stopDataSource();
                handler.sendEmptyMessage(SAVE_PDFXML_FILE_FAIL);
                return;
            }

            String Auto_Result = "";
            for (String str : set) {
                Auto_Result += str + " ";
            }
            analyses.put("Auto_Result", Auto_Result);
            analyses.put("Auto_Class", classResult);

            Iterator<Map.Entry<String, String>> iterator = analyses.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> next = iterator.next();
                Log.i("wuweikangResult", next.getKey() + " -- " + next.getValue());
            }
            HearRate12 instance = HearRate12.getInstance();
            instance.setHeartRate(String.valueOf(hr));
            instance.setPrInterval(String.valueOf(pr));
            instance.setRrInterval(String.valueOf(rr));
            instance.setQrsDuration(String.valueOf(qrs));
            instance.setQtd(String.valueOf(qt));
            instance.setQtc(String.valueOf(qtc));
            instance.setRv5(String.valueOf(rv5));
            instance.setSv1(String.valueOf(sv1));
            instance.setPaxis(String.valueOf(axis[0]));
            instance.setQrsAxis(String.valueOf(axis[1]));
            instance.setTaxis(String.valueOf(axis[2]));
            instance.setAnalysisResults(classResult);
            instance.setAnalysisList(set);
            instance.setEcgData(ecgDataBuffer);

            Map<String, StringBuffer> dataString = StringUtil.praseIntDataToString(ecgDataBuffer);
            String xmlFilePath = FileUtil.saveToXMLFile(analyses, dataString);
            handler.sendEmptyMessage(SAVE_PDFXML_FILE);
        }
    }
}

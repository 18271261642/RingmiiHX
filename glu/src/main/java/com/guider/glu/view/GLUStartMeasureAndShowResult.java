package com.guider.glu.view;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.guider.glu.R;
import com.guider.glu.model.GLUMeasureModel;
import com.guider.glu.presenter.GLUServiceManager;
import com.guider.health.common.core.Glucose;
import com.guider.health.common.core.Judgement;
import com.guider.health.common.core.ParamHealthRangeAnlysis;
import com.guider.health.common.device.DeviceInit;
import com.guider.health.common.device.IUnit;
import com.guider.health.common.device.standard.StandardCallback;
import com.guider.health.common.utils.SkipClick;
import com.guider.health.common.utils.UnitUtil;
import com.guider.health.common.views.RoundProgress;

import java.util.ArrayList;
import java.util.List;

/**
 * 初始化
 * Created by haix on 2019/6/12.
 */

public class GLUStartMeasureAndShowResult extends GLUFragment {

    private View view;
    private RotateAnimation myAlphaAnimation;

    private RoundProgress progressRound;
    private TextView progressNum , loadingText;
    private ImageView loading;
    private boolean isStart = false;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.glu_measure, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            return;
        }

        view.findViewById(R.id.home).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                _mActivity.setResult(112);

                _mActivity.finish();
                System.exit(0);
            }
        });

        ((TextView) view.findViewById(R.id.title)).setText(getResources().getString(R.string.device_test));
        view.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                _mActivity.setResult(113);

                _mActivity.finish();
                System.exit(0);
            }
        });

        view.findViewById(R.id.glu_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myAlphaAnimation != null) {
                    myAlphaAnimation.cancel();
                    myAlphaAnimation = null;
                }

                GLUServiceManager.getInstance().stopDeviceConnect();

                _mActivity.setResult(113);

                _mActivity.finish();
                System.exit(0);


            }
        });

        view.findViewById(R.id.skip).setVisibility(View.VISIBLE);
        view.findViewById(R.id.skip).setOnClickListener(new SkipClick(this , DeviceInit.DEV_GLU , true));


        promp = view.findViewById(R.id.promp);
        progressRound = view.findViewById(R.id.progress);
        progressNum = view.findViewById(R.id.progress_num);
        progressRound.setStartAngle(270);
        progressRound.setProgressColor(R.color.color_F18937);
        progressRound.setProgress(1);

        // 开始初始化
        loadingText = view.findViewById(R.id.loading_text);
        loading = view.findViewById(R.id.loading);
        animation(loading);
        myAlphaAnimation.start();
        GLUServiceManager.getInstance().startMeassure();

    }

    @Override
    public void startInitTick(final String time) {
        super.startInitTick(time);
        _mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadingText.setText(time);
            }
        });
    }

    @Override
    public void stopInit() {
        super.stopInit();
        _mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                view.findViewById(R.id.glu_touch_tip).setVisibility(View.VISIBLE);
                view.findViewById(R.id.loadingroot).setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void initTimeOut() {
        super.initTimeOut();
        _mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                promp.setText(getResources().getString(R.string.init_error));
                view.findViewById(R.id.glu_cancel).setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void showMeasureTime(String time) {
        if ("59".equals(time)) {
            // 成功开始测量
            isStart = true;
            promp.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
            view.findViewById(R.id.glu_touch_tip).setVisibility(View.GONE);
            view.findViewById(R.id.loadingroot).setVisibility(View.VISIBLE);
            view.findViewById(R.id.tip_icon).setVisibility(View.GONE);
            view.findViewById(R.id.loadinglayout).setVisibility(View.VISIBLE);
        }
        loadingText.setText(time);
//        else if ("9".equals(time) && !isStart) {
//            // 开始失败
//            view.findViewById(R.id.glu_touch_tip).setVisibility(View.VISIBLE);
//            view.findViewById(R.id.loadingroot).setVisibility(View.GONE);
//        }

        if ("0".equals(time)) {
            myAlphaAnimation.cancel();
            view.findViewById(R.id.loadinglayout).setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void finger_time(int time) {
        super.finger_time(time);
        // 等待手指伸入的时间
        progressRound.setProgress(time / 10.0f);
        progressNum.setText(time + "");
    }

    private TextView promp;

    @Override
    public void showMeasureRemind(String remind) {

        if (!TextUtils.isEmpty(remind) && remind.contains(GLUMeasureModel.GLU_TIP)) {
            view.findViewById(R.id.glu_touch_tip).setVisibility(View.VISIBLE);
            view.findViewById(R.id.loadingroot).setVisibility(View.GONE);
        } else {
            view.findViewById(R.id.glu_touch_tip).setVisibility(View.GONE);
            view.findViewById(R.id.loadingroot).setVisibility(View.VISIBLE);
        }
        promp.setText(remind);

    }

    @Override
    public void measureErrorAndCloseBlueConnect() {
        if (myAlphaAnimation != null) {
            myAlphaAnimation.cancel();
        }


        _mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                promp.setText(promp.getText().toString() + "\n" + getResources().getString(R.string.reboot_device));
                promp.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
                view.findViewById(R.id.loadinglayout).setVisibility(View.GONE);
                view.findViewById(R.id.glu_touch_tip).setVisibility(View.GONE);
                view.findViewById(R.id.tip_icon).setVisibility(View.VISIBLE);
                view.findViewById(R.id.glu_cancel).setVisibility(View.VISIBLE);
                view.findViewById(R.id.loadingroot).setVisibility(View.VISIBLE);
                Toast.makeText(_mActivity, getResources().getString(R.string.test_error), Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public void measureFinish() {
        try {

            List<ParamHealthRangeAnlysis> list = new ArrayList<>();

            ParamHealthRangeAnlysis paramHealthRangeAnlysis1 = new ParamHealthRangeAnlysis();
            paramHealthRangeAnlysis1.setType(ParamHealthRangeAnlysis.BLOODSUGAR);
            if (Glucose.getInstance().getFoodTime() == 0) {
                //饭钱
                paramHealthRangeAnlysis1.setBsTime(ParamHealthRangeAnlysis.FPG);
            } else {
                //饭后
                paramHealthRangeAnlysis1.setBsTime(ParamHealthRangeAnlysis.TWOHPPG);
            }

            paramHealthRangeAnlysis1.setValue1(GLUServiceManager.getInstance().getGlucoseResult());

            list.add(paramHealthRangeAnlysis1);

            ParamHealthRangeAnlysis paramHealthRangeAnlysis2 = new ParamHealthRangeAnlysis();
            paramHealthRangeAnlysis2.setType(ParamHealthRangeAnlysis.BLOODOXYGEN);
            paramHealthRangeAnlysis2.setValue1(Glucose.getInstance().getOxygenSaturation());

            list.add(paramHealthRangeAnlysis2);

            List<String> results = Judgement.healthDataAnlysis(list);


            if (results != null && results.size() == 2) {
                Glucose.getInstance().setIndexGlucose(results.get(0));
                Glucose.getInstance().setIndexOxygen(results.get(1));
            }

            IUnit iUnit = UnitUtil.getIUnit(_mActivity);
            double value = iUnit.getGluShowValue(GLUServiceManager.getInstance().getGlucoseResult(), 2);
            showGLUDialog( value+ "");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 测量完毕后同步信息
        Glucose.getInstance().startStandardRun(new StandardCallback() {
            @Override
            public void onResult(boolean isFinish) {

                isFinishStandard = 0b10 | isFinishStandard;
                toNext();
            }
        });

        baseHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                isFinishStandard = 0b01 | isFinishStandard;
                toNext();
            }
        }, 5000);


    }

    private int isFinishStandard = 0b00;
    private synchronized void toNext() {
        if (isFinishStandard == 0b11) {
            hideDialog();
            Intent i = new Intent();
            i.putExtra("result", Glucose.getInstance());

            _mActivity.setResult(111, i);

            _mActivity.finish();
            System.exit(0);
        }
    }


    public void animation(ImageView imageView) {
        myAlphaAnimation = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);//设置图片动画属性，各参数说明可参照api
        myAlphaAnimation.setRepeatCount(-1);//设置旋转重复次数，即转几圈
        myAlphaAnimation.setDuration(500);//设置持续时间，注意这里是每一圈的持续时间，如果上面设置的圈数为3，持续时间设置1000，则图片一共旋转3秒钟
        myAlphaAnimation.setInterpolator(new LinearInterpolator());//设置动画匀速改变。相应的还有AccelerateInterpolator、DecelerateInterpolator、CycleInterpolator等
        imageView.setAnimation(myAlphaAnimation);//设置imageview的动画，也可以myImageView.startAnimation(myAlphaAnimation)
    }

}

package com.gaider.proton.view;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.guider.health.apilib.model.hd.HeartStateMeasure_Hd;
import com.guider.health.common.cache.MeasureDataUploader;
import com.guider.health.common.core.Config;
import com.guider.health.common.core.HearRateHd;
import com.guider.health.common.core.RouterPathManager;
import com.guider.health.common.views.TipTitleView;
import com.guider.health.ecg.R;
import com.guider.health.ecg.view.ECGFragment;
import com.proton.ecgcard.algorithm.bean.AlgorithmResult;
import com.proton.ecgcard.connector.EcgCardManager;

import me.yokeyword.fragmentation.ISupportFragment;

/**
 * 红豆心电
 */
public class ProtonEcgResult extends ECGFragment {


    private View view;

    private TipTitleView tipView;
    private LinearLayout llJiankang;
    private TextView yinjiufengxianNum;
    private FrameLayout yjF;
    private ImageView yinjiufengxianTag;
    private TextView yj1;
    private TextView yj2;
    private TextView yj3;
    private TextView yj4;
    private TextView yj5;
    private TextView qingxubianhuaNum;
    private FrameLayout qxF;
    private ImageView qingxubianhuaTag;
    private TextView qx1;
    private TextView qx2;
    private TextView qx3;
    private TextView qx4;
    private TextView qx5;
    private TextView jingshenyaliNum;
    private FrameLayout jsF;
    private ImageView jingshenyaliTag;
    private TextView js1;
    private TextView js2;
    private TextView js3;
    private TextView js4;
    private TextView tizhiNum;
    private FrameLayout tzF;
    private ImageView tizhiTag;
    private TextView tz1;
    private TextView tz2;
    private TextView tz3;
    private TextView tz4;
    AlgorithmResult data;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.proton_result, container, false);
        EcgCardManager.init(_mActivity.getApplicationContext());
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setHomeEvent(view.findViewById(R.id.home), Config.HOME_DEVICE);
        ((TextView) view.findViewById(R.id.title)).setText("操作指南");
        TipTitleView tips = view.findViewById(R.id.tip_view);
        tips.setTips("心电测量", "输入参数", "开始测量", "结果展示");
        tips.toTip(3);
        view.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pop();
            }
        });

        view.findViewById(R.id.next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (RouterPathManager.Devices.size() > 0) {
                    String fragmentPath = RouterPathManager.Devices.remove();

                    try {
                        popTo(Class.forName(Config.HOME_DEVICE), false);

                        start((ISupportFragment) Class.forName(fragmentPath).newInstance());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                } else {

                    try {

                        popTo(Class.forName(Config.HOME_DEVICE), false);
                        start((ISupportFragment) Class.forName(Config.END_FRAGMENT).newInstance());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        });


        tipView = view.findViewById(R.id.tip_view);
        llJiankang = view.findViewById(R.id.ll_jiankang);
        yinjiufengxianNum = view.findViewById(R.id.yinjiufengxian_num);
        yjF = view.findViewById(R.id.yj_f);
        yinjiufengxianTag = view.findViewById(R.id.yinjiufengxian_tag);
        yj1 = view.findViewById(R.id.yj_1);
        yj2 = view.findViewById(R.id.yj_2);
        yj3 = view.findViewById(R.id.yj_3);
        yj4 = view.findViewById(R.id.yj_4);
        yj5 = view.findViewById(R.id.yj_5);
        qingxubianhuaNum = view.findViewById(R.id.qingxubianhua_num);
        qxF = view.findViewById(R.id.qx_f);
        qingxubianhuaTag = view.findViewById(R.id.qingxubianhua_tag);
        qx1 = view.findViewById(R.id.qx_1);
        qx2 = view.findViewById(R.id.qx_2);
        qx3 = view.findViewById(R.id.qx_3);
        qx4 = view.findViewById(R.id.qx_4);
        qx5 = view.findViewById(R.id.qx_5);
        jingshenyaliNum = view.findViewById(R.id.jingshenyali_num);
        jsF = view.findViewById(R.id.js_f);
        jingshenyaliTag = view.findViewById(R.id.jingshenyali_tag);
        js1 = view.findViewById(R.id.js_1);
        js2 = view.findViewById(R.id.js_2);
        js3 = view.findViewById(R.id.js_3);
        js4 = view.findViewById(R.id.js_4);
        tizhiNum = view.findViewById(R.id.tizhi_num);
        tzF = view.findViewById(R.id.tz_f);
        tizhiTag = view.findViewById(R.id.tizhi_tag);
        tz1 = view.findViewById(R.id.tz_1);
        tz2 = view.findViewById(R.id.tz_2);
        tz3 = view.findViewById(R.id.tz_3);
        tz4 = view.findViewById(R.id.tz_4);

        Bundle arguments = getArguments();
        data = (AlgorithmResult) arguments.getSerializable("data");
        if (data == null) {
            return;
        }

        uploadData();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                inflaterData();
            }
        }, 1000);
    }

    private void uploadData() {
        HeartStateMeasure_Hd heartStateMeasure_hd = new HeartStateMeasure_Hd();
        heartStateMeasure_hd.setAlcholRiskIndex(data.getAlcholRiskIndex());
        heartStateMeasure_hd.setAlcholRiskScore(data.getAlcholRiskScore());
        heartStateMeasure_hd.setAtrialFibrillation(data.getAtrialFibrillation());
        heartStateMeasure_hd.setBodyFatIndex(data.getBodyFatIndex());
        heartStateMeasure_hd.setBodyFatRatio(data.getBodyFatRatio());
        heartStateMeasure_hd.setEmotionIndex(data.getEmotionIndex());
        heartStateMeasure_hd.setEmotionScore(data.getEmotionScore());
        heartStateMeasure_hd.setHeartRate(data.getHeartRate());
        heartStateMeasure_hd.setPrematureBeat(data.getPrematureBeat());
        heartStateMeasure_hd.setRRpercent(data.getRRpercent());
        heartStateMeasure_hd.setStressIndex(data.getStressIndex());
        heartStateMeasure_hd.setStressScore(data.getStressScore());
        MeasureDataUploader.getInstance(_mActivity).uploadEcd12(
                HearRateHd.getInstance().getDeviceAddress() , heartStateMeasure_hd, data.getHeartRate() + "");

    }

    private void inflaterData() {

        // 心率
        int heartRate = data.getHeartRate();
        HearRateHd.getInstance().setHeartRate(heartRate + "");

        // 饮酒风险 •-1 错误; 0 中低; 1 中; 2 中高; 3 高; 4 极高
        int alcholRiskIndex = data.getAlcholRiskIndex();
        // -1 错误; 30 ~ 100 正常值
        int alcholRiskScore = data.getAlcholRiskScore();
        HearRateHd.getInstance().setAlcholRiskIndex(alcholRiskIndex);
        HearRateHd.getInstance().setAlcholRiskScore(alcholRiskScore);


        // 精神压力 •-1 错误; 0 正常; 1 轻度; 2 中度; 3 重度
        int stressIndex = data.getStressIndex();
        // -1 错误; 5 ~ 100 正常值
        int stressScore = data.getStressScore();
        HearRateHd.getInstance().setStressIndex(stressIndex);
        HearRateHd.getInstance().setStressScore(stressScore);

        // 情绪变化 •-1 错误; 0 抑郁; 1 沮丧; 2 正常; 3 紧张; 4 焦虑
        int emotionIndex = data.getEmotionIndex();
        // •实时情绪得分: -100 ~ 100 正常值
        int emotionScore = data.getEmotionScore();
        HearRateHd.getInstance().setEmotionIndex(emotionIndex);
        HearRateHd.getInstance().setEmotionScore(emotionScore);

        // 体脂率
        double bodyFatRatio = data.getBodyFatRatio();
        //•体脂率指标：-1 错误; 0 偏瘦; 1 标准; 2 偏胖; 3 过胖
        int bodyFatIndex = data.getBodyFatIndex();
        HearRateHd.getInstance().setBodyFatRatio((int) (bodyFatRatio * 100));
        HearRateHd.getInstance().setBodyFatIndex(bodyFatIndex);

        // prematureBeat>0 代表早搏
        int prematureBeat = data.getPrematureBeat();
        // atrialFibrillation > 0 代表房颤
        int atrialFibrillation = data.getAtrialFibrillation();
        HearRateHd.getInstance().setPrematureBeat(prematureBeat);
        HearRateHd.getInstance().setAtrialFibrillation(atrialFibrillation);

        // todo 设置饮酒风险
        yinjiufengxianNum.setText(alcholRiskScore + "");
        setScoreColor("饮酒风险", alcholRiskScore, yinjiufengxianNum);
        setOffset((alcholRiskScore - 30) / 100f, yinjiufengxianTag, yjF);
        setLevel(alcholRiskIndex, yj1, yj2, yj3, yj4, yj5);

        // todo 设置精神压力
        jingshenyaliNum.setText(stressScore + "");
        setScoreColor("精神压力", stressScore, jingshenyaliNum);
        setOffset(alcholRiskScore / 100f, jingshenyaliTag, jsF);
        setLevel(stressIndex, js1, js2, js3, js4);

        // todo 设置情绪变化
        qingxubianhuaNum.setText(emotionScore + "");
        setScoreColor("情绪变化", emotionScore, qingxubianhuaNum);
        setOffset((emotionScore > 0 ? emotionScore + 100 : Math.abs(emotionScore)) / 200f, qingxubianhuaTag, qxF);
        setLevel(emotionIndex, qx1, qx2, qx3, qx4, qx5);

        // todo 设置体脂
        tizhiNum.setText(Double.NaN == bodyFatRatio ? "--" : (int)(bodyFatRatio * 100) + "%");
        if (Double.NaN == bodyFatRatio) {
            return;
        }
        setScoreColor("体脂率", (int) bodyFatRatio, tizhiNum);
        setOffset((float) (bodyFatRatio / 40f), tizhiTag, tzF);
        setLevel(bodyFatIndex, tz1, tz2, tz3, tz4);
    }

    private void setScoreColor(String name, int score, TextView textView) {
        switch (name) {
            case "饮酒风险":
                if (score < 40) {
                    textView.setTextColor(Color.parseColor("#7CCD94"));
                } else if (score < 55) {
                    textView.setTextColor(Color.parseColor("#E98F56"));
                } else {
                    textView.setTextColor(Color.parseColor("#FA6266"));
                }
                break;
            case "精神压力":
                if (score < 40) {
                    textView.setTextColor(Color.parseColor("#7CCD94"));
                } else if (score < 60) {
                    textView.setTextColor(Color.parseColor("#E98F56"));
                } else {
                    textView.setTextColor(Color.parseColor("#FA6266"));
                }
                break;
            case "情绪变化":
                if (score < -30) {
                    textView.setTextColor(Color.parseColor("#E98F56"));
                } else if (score < 30) {
                    textView.setTextColor(Color.parseColor("#7CCD94"));
                } else {
                    textView.setTextColor(Color.parseColor("#FA6266"));
                }
                break;
            case "体脂率":
                if (score < 20) {
                    textView.setTextColor(Color.parseColor("#E98F56"));
                } else if (score < 30) {
                    textView.setTextColor(Color.parseColor("#7CCD94"));
                } else {
                    textView.setTextColor(Color.parseColor("#E98F56"));
                }
                break;
        }
    }

    private void setLevel(int index, TextView... textViews) {
        for (TextView textView : textViews) {
            if ((index + "").equals(textView.getTag())) {
                textView.setTextColor(Color.parseColor("#ffffff"));
                String s = textView.getText().toString();
                if (s.equals("中低") || s.equals("正常")) {
                    textView.setBackgroundResource(R.drawable.splash_lan);
                } else {
                    textView.setBackgroundResource(R.drawable.splash_hong);
                }
                break;
            }
        }
    }


    private void setOffset(float progress, View view, View parentView) {
        progress = progress <= 0 ? 0 : progress;
        progress = progress >= 0.9f ? 0.9f : progress;
        int width = parentView.getWidth();
        int i = (int) (width * progress);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) view.getLayoutParams();
        layoutParams.setMargins(i, 0, 0, 0);
        view.setLayoutParams(layoutParams);
    }

}

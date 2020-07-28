package com.guider.health.bp.view.yfbp;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.guider.health.bp.R;
import com.guider.health.bp.view.BPFragment;
import com.guider.health.bp.view.FinishClick;
import com.guider.health.bp.view.LevelViewUtil;
import com.guider.health.bp.view.TipTitleView;
import com.guider.health.common.cache.MeasureDataUploader;
import com.guider.health.common.core.Config;
import com.guider.health.common.core.HeartPressYf;
import com.guider.health.common.core.JudgeResult;
import com.guider.health.common.core.Judgement;
import com.guider.health.common.core.ParamHealthRangeAnlysis;
import com.guider.health.common.device.standard.Constant;
import com.guider.health.common.device.standard.StandardCallback;

import java.util.ArrayList;
import java.util.List;

import ble.BleClient;

/**
 * 这是动脉硬化款的血压仪四川云峰的
 * Created by haix on 2019/6/25.
 */

public class BPOperaterReminderResult extends BPFragment {

    private View view;

    private TextView tvDongmaiYinghua, tvShousuo, tvShuzhang, tvBmi, tvMaibo;
    private LinearLayout levelDonmai, levelXueya, levelBmi, levelMaibo;
    private LevelViewUtil dongmai, xueya, bmi, maibo;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.bp_yf_meassure_result, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            return;
        }
        initView();

        setResult();

        BleClient.instance().getClassicClient().close();
        BleClient.instance().disconnect(BleClient.instance().popStagingDevice());
    }

    String[] dongmaiStr;
    private void initView() {
        setHomeEvent(view.findViewById(R.id.home), Config.HOME_DEVICE);
        ((TextView) view.findViewById(R.id.title)).setText("测量结果");
        view.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pop();
            }
        });
        TipTitleView tips = view.findViewById(R.id.tips);
        tips.setTips("动脉硬化测量","开机提醒","蓝牙连接", "信息录入", "操作指南", "测量结果");
        tips.toTip(5);
        view.findViewById(R.id.bp_result_next).setOnClickListener(new FinishClick(this));

        tvDongmaiYinghua = view.findViewById(R.id.tv_dongmaiyinghua);
        tvShousuo = view.findViewById(R.id.tv_shousuoya);
        tvShuzhang = view.findViewById(R.id.tv_shuzhangya);
        tvBmi = view.findViewById(R.id.tv_bmi);
        tvMaibo = view.findViewById(R.id.tv_maibo);
        levelDonmai = view.findViewById(R.id.level_dongmaiyinghua);
        levelXueya = view.findViewById(R.id.level_xueya);
        levelBmi = view.findViewById(R.id.level_bmi);
        levelMaibo = view.findViewById(R.id.level_maibo);
        dongmai = new LevelViewUtil();
        dongmaiStr = new String[]{
                "正常",
                Constant.HEALTHRANGE_ART_CRITICAL,
                Constant.HEALTHRANGE_ART_MILD,
                Constant.HEALTHRANGE_ART_MEDIUM,
                Constant.HEALTHRANGE_ART_SEVERE
        };
        dongmai.init(levelDonmai, dongmaiStr, new String[]{
                "#7ABC5B", "#7156FB", "#F6CC7B", "#FB8C56", "#F35D57"
        });
        xueya = new LevelViewUtil();
        xueya.init(levelXueya, new String[]{
                "正常", "异常"
        }, new String[]{
                "#7ABC5B", "#F35D57"
        });
        bmi = new LevelViewUtil();
        bmi.init(levelBmi, new String[]{
                "偏瘦", "正常", "肥胖"
        }, new String[]{
                "#F6CC7B", "#7ABC5B", "#F35D57"
        });
        maibo = new LevelViewUtil();
        maibo.init(levelMaibo, new String[]{
                "偏低", "正常", "偏高"
        }, new String[]{
                "#F6CC7B", "#7ABC5B", "#F35D57"
        });
    }

    private void setResult() {
        HeartPressYf result = HeartPressYf.getInstance();
        tvDongmaiYinghua.setText(result.getASI());
        tvShuzhang.setText(result.getDbp());
        tvShousuo.setText(result.getSbp());
        tvBmi.setText(result.getBMI());
        tvMaibo.setText(result.getHeart());
        HeartPressYf.getInstance().startStandardRun(new StandardCallback() {
            @Override
            public void onResult(boolean isFinish) {
                if (BPOperaterReminderResult.this.isDetached()) {
                    return;
                }
                if (isFinish) {
                    setServer();
                } else {
                    setLocal();
                }
            }
        });
        MeasureDataUploader.getInstance(_mActivity).uploadBP(
                HeartPressYf.getInstance().getDeviceAddress(),
                HeartPressYf.getInstance().getDbp(),
                HeartPressYf.getInstance().getSbp(),
                HeartPressYf.getInstance().getHeart()
        );
        MeasureDataUploader.getInstance(_mActivity).uploadArt(
                HeartPressYf.getInstance().getDeviceAddress(),
                0,
                0,
                Integer.valueOf(HeartPressYf.getInstance().getASI()),
                Integer.valueOf(HeartPressYf.getInstance().getDbp()),
                Integer.valueOf(HeartPressYf.getInstance().getSbp()),
                Integer.valueOf(HeartPressYf.getInstance().getHeart())
        );
        MeasureDataUploader.getInstance(_mActivity).uploadHeartBpm(
                Integer.valueOf(HeartPressYf.getInstance().getHeart())
        );
    }

    private void setServer() {
        boolean sbpNormal = HeartPressYf.getInstance().getStr_sbp().contains("正常");
        boolean dbpNormal = HeartPressYf.getInstance().getStr_dbp().contains("正常");
        xueya.setLevel(sbpNormal && dbpNormal ? 0 : 1);
        for (int i = 0; i < dongmaiStr.length; i++) {
            if (dongmaiStr[i].equals(HeartPressYf.getInstance().getStr_ASI())) {
                dongmai.setLevel(i);
                break;
            }
        }
        if (HeartPressYf.getInstance().get_BMI() == 0) {
            bmi.setLevel(1);
        } else {
            if (HeartPressYf.getInstance().get_BMI() > 0) {
                bmi.setLevel(2);
            } else {
                bmi.setLevel(0);
            }
        }
        if (HeartPressYf.getInstance().get_heart() == 0) {
            maibo.setLevel(1);
        } else {
            if (HeartPressYf.getInstance().get_heart() > 0) {
                maibo.setLevel(2);
            } else {
                maibo.setLevel(0);
            }
        }
    }

    private void setLocal() {
        HeartPressYf result = HeartPressYf.getInstance();

        ParamHealthRangeAnlysis para = new ParamHealthRangeAnlysis();
        para.setType(ParamHealthRangeAnlysis.BLOODPRESSURE);
        para.setValue1(Integer.valueOf(result.getSbp()));
        para.setValue2(Integer.valueOf(result.getDbp()));
        JudgeResult r = Judgement.healthDataAnlysis(para);
        xueya.setLevel(r.getDirection() == 0 ? 0 : 1);

        ParamHealthRangeAnlysis para1 = new ParamHealthRangeAnlysis();
        para1.setType(ParamHealthRangeAnlysis.DMYH);
        para1.setValue1(Integer.valueOf(result.getASI()));
        JudgeResult r1 = Judgement.healthDataAnlysis(para1);
        dongmai.setLevel(r1.getDirection());

        ParamHealthRangeAnlysis para2 = new ParamHealthRangeAnlysis();
        para2.setType(ParamHealthRangeAnlysis.BMI);
        para2.setValue1(Integer.valueOf(result.getBMI()));
        JudgeResult r2 = Judgement.healthDataAnlysis(para2);
        int direction = r2.getDirection();
        if (direction == 0) {
            bmi.setLevel(1);
        } else {
            if (direction > 0) {
                bmi.setLevel(2);
            } else {
                bmi.setLevel(0);
            }
        }

        ParamHealthRangeAnlysis para3 = new ParamHealthRangeAnlysis();
        para3.setType(ParamHealthRangeAnlysis.HEARTBEAT);
        para3.setValue1(Integer.valueOf(result.getHeart()));
        JudgeResult r3 = Judgement.healthDataAnlysis(para3);
        maibo.setLevel(r3.getDirection() == 0 ? 0 : 1);
        int direction2 = r3.getDirection();
        if (direction2 == 0) {
            maibo.setLevel(1);
        } else {
            if (direction2 > 0) {
                maibo.setLevel(2);
            } else {
                maibo.setLevel(0);
            }
        }

        List<ParamHealthRangeAnlysis> list = new ArrayList<>();

        ParamHealthRangeAnlysis paramHealthRangeAnlysis1 = new ParamHealthRangeAnlysis();
        paramHealthRangeAnlysis1.setType(ParamHealthRangeAnlysis.DMYH);
        paramHealthRangeAnlysis1.setValue1(HeartPressYf.getInstance().getASI());
        list.add(paramHealthRangeAnlysis1);


        List<String> results = Judgement.healthDataAnlysis(list);
        if (results != null) {
            HeartPressYf.getInstance().setCardShowStr(results.get(0));
        }
    }


}

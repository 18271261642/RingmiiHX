package com.guider.health.bp.view.avebp;

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
import com.guider.health.common.core.HeartPressAve;
import com.guider.health.common.core.JudgeResult;
import com.guider.health.common.core.Judgement;
import com.guider.health.common.core.MyUtils;
import com.guider.health.common.core.ParamHealthRangeAnlysis;
import com.guider.health.common.core.UserManager;
import com.guider.health.common.device.standard.Constant;
import com.guider.health.common.device.standard.StandardCallback;

/**
 * 这是动脉硬化款的血压仪四川云峰的
 * Created by haix on 2019/6/25.
 */

public class BPOperaterReminderResult extends BPFragment {

    private View view;

    private TextView tvShousuo, tvShuzhang, tvMaibo;
    private LinearLayout levelXueya, levelMaibo;
    private LevelViewUtil xueya, maibo;
    private LinearLayout llAvi, llApi;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.bp_ave_meassure_result, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            return;
        }
        initView();

        setStandard();
    }

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
        tips.setTips("动脉硬化测量","操作指南", "蓝牙连接", "测量结果");
        tips.toTip(3);
        view.findViewById(R.id.bp_result_next).setOnClickListener(new FinishClick(this));

        tvShousuo = view.findViewById(R.id.tv_shousuoya);
        tvShuzhang = view.findViewById(R.id.tv_shuzhangya);
        tvMaibo = view.findViewById(R.id.tv_dongmaiyinghua);
        levelXueya = view.findViewById(R.id.level_xueya);
        levelMaibo = view.findViewById(R.id.level_dongmaiyinghua);
        llAvi = view.findViewById(R.id.ll_avi);
        llApi = view.findViewById(R.id.ll_api);
        xueya = new LevelViewUtil();
        maibo = new LevelViewUtil();
        xueya.init(levelXueya, new String[]{
                "正常", "异常"
        }, new String[]{
                "#7ABC5B", "#F6CC7B"
        });
        maibo.init(levelMaibo, new String[]{
                "偏低", "正常", "偏高"
        }, new String[]{
                "#F6CC7B", "#7ABC5B", "#F35D57"
        });
    }

    /**
     * 获取评级
     *
     * @param value 需要定位的值
     * @param ls    评级标准 比如 60 <= 正常值 < 100 = {60 , 100}
     * @return
     */
    int getLevel(double value, double[] ls) {
        for (int i = 0; i < ls.length; i++) {
            if (value < ls[i]) {
                return i;
            }
        }
        return ls.length;
    }

    private void setStandard() {
        HeartPressAve result = HeartPressAve.getInstance();
        tvShuzhang.setText(result.getDbp());
        tvShousuo.setText(result.getSbp());
        tvMaibo.setText(result.getHeart());
        int avi = Integer.valueOf(result.getAvi());
        int api = Integer.valueOf(result.getApi());
        ((TextView) llAvi.getChildAt(0)).setText(avi + "");
        ((TextView) llApi.getChildAt(0)).setText(api + "");
        showDialog();
        HeartPressAve.getInstance().startStandardRun(new StandardCallback() {
            @Override
            public void onResult(boolean isFinish) {
                hideDialog();
                if (BPOperaterReminderResult.this.isDetached()) {
                    return;
                }
                if (isFinish) {
                    setStandardOfServer();
                } else {
                    setStandardOfLocal();
                }
            }
        });
        MeasureDataUploader.getInstance(_mActivity).uploadArt(
                HeartPressAve.getInstance().getDeviceAddress(),
                Integer.valueOf(HeartPressAve.getInstance().getAvi()),
                Integer.valueOf(HeartPressAve.getInstance().getApi()),
                0,
                Integer.valueOf(HeartPressAve.getInstance().getDbp()),
                Integer.valueOf(HeartPressAve.getInstance().getSbp()),
                Integer.valueOf(HeartPressAve.getInstance().getHeart())
        );
        MeasureDataUploader.getInstance(_mActivity).uploadBP(
                HeartPressAve.getInstance().getDeviceAddress(),
                HeartPressAve.getInstance().getDbp(),
                HeartPressAve.getInstance().getSbp(),
                HeartPressAve.getInstance().getHeart()
        );
        MeasureDataUploader.getInstance(_mActivity).uploadHeartBpm(
                Integer.valueOf(HeartPressAve.getInstance().getHeart())
        );
    }

    private void setStandardOfServer() {
        // 设置Avi
        if (HeartPressAve.getInstance().get_avi() > 0) {
            ((TextView) llAvi.getChildAt(1)).setText(Constant.HEALTHRANGE_HIGH);
            llAvi.setBackgroundResource(R.drawable.shape_ave_red);
        } else {
            ((TextView) llAvi.getChildAt(1)).setText(Constant.HEALTHRANGE_NORMAL);
            llAvi.setBackgroundResource(R.drawable.shape_ave_blue);
        }
        // 设置血压
        boolean sbpNormal = HeartPressAve.getInstance().getStr_sbp().contains("正常");
        boolean dbpNormal = HeartPressAve.getInstance().getStr_dbp().contains("正常");
        xueya.setLevel(sbpNormal && dbpNormal ? 0 : 1);

        // 设置心率
        int direction2 = HeartPressAve.getInstance().get_heart();
        if (direction2 == 0) {
            maibo.setLevel(1);
        } else {
            if (direction2 > 0) {
                maibo.setLevel(2);
            } else {
                maibo.setLevel(0);
            }
        }
    }

    private void setStandardOfLocal() {
        HeartPressAve result = HeartPressAve.getInstance();

        // AVI
        ParamHealthRangeAnlysis aviPara = new ParamHealthRangeAnlysis();
        aviPara.setType(ParamHealthRangeAnlysis.AVI);
        aviPara.setValue1(MyUtils.getAgeFromBirthTime(UserManager.getInstance().getBirth()));
        aviPara.setValue2(Integer.valueOf(result.getAvi()));
        JudgeResult aviR = Judgement.healthDataAnlysis(aviPara);
        HeartPressAve.getInstance().set_avi(aviR.getDirection());
        switch (aviR.getDirection()) {
            case 0:
                ((TextView) llAvi.getChildAt(1)).setText("正常");
                llAvi.setBackgroundResource(R.drawable.shape_ave_blue);
                HeartPressAve.getInstance().setStr_avi("正常");
                break;
            case 1:
                ((TextView) llAvi.getChildAt(1)).setText("偏高");
                llAvi.setBackgroundResource(R.drawable.shape_ave_red);
                HeartPressAve.getInstance().setStr_avi("偏高");
                break;
            case -1:
                ((TextView) llAvi.getChildAt(1)).setText("偏低");
                llAvi.setBackgroundResource(R.drawable.shape_ave_red);
                HeartPressAve.getInstance().setStr_avi("偏低");
                break;
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

        ParamHealthRangeAnlysis para = new ParamHealthRangeAnlysis();
        para.setType(ParamHealthRangeAnlysis.BLOODPRESSURE);
        para.setValue1(Integer.valueOf(result.getSbp()));
        para.setValue2(Integer.valueOf(result.getDbp()));
        JudgeResult r = Judgement.healthDataAnlysis(para);
        xueya.setLevel(r.getDirection() == 0 ? 0 : 1);

    }

}

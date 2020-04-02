package com.guider.health.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.guider.health.common.core.ForaGlucose;
import com.guider.health.common.core.Glucose;
import com.guider.health.common.core.HearRate;
import com.guider.health.common.core.HearRate12;
import com.guider.health.common.core.HearRateHd;
import com.guider.health.common.core.HeartPressAve;
import com.guider.health.common.core.HeartPressBp;
import com.guider.health.common.core.HeartPressCx;
import com.guider.health.common.core.HeartPressMbb_88;
import com.guider.health.common.core.HeartPressMbb_9804;
import com.guider.health.common.core.HeartPressYf;
import com.guider.health.common.device.DeviceInit;

import java.util.ArrayList;
import java.util.List;

public class ResultListView extends LinearLayout {
    ArrayList<BaseResultViewHolder> holderList;

    RequestCallback callback;

    private int requestNum;

    public ResultListView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        holderList = new ArrayList<>();
    }

    public void setDeviceList(List<String> deviceList) {
        for (int i = 0; i < deviceList.size(); i++) {
            String s = deviceList.get(i);
            createView(s);
        }

    }

    private void createView(String s) {
        BaseResultViewHolder resultViewHolder;
        switch (s) {
            case DeviceInit.DEV_BP:             // 移动版
                if (TextUtils.isEmpty(HeartPressBp.getInstance().getSbp())) {
                    return;
                }
                resultViewHolder = new ViewHolderOfBp(this);
                break;
            case DeviceInit.DEV_BP_MBB_88:             // 脉搏波 88
                if (TextUtils.isEmpty(HeartPressMbb_88.getInstance().getSbp())) {
                    return;
                }
                resultViewHolder = new ViewHolderOfMbb88(this);
                break;
            case DeviceInit.DEV_BP_MBB_9804:             // 脉搏波98
                if (TextUtils.isEmpty(HeartPressMbb_9804.getInstance().getSbp())) {
                    return;
                }
                resultViewHolder = new ViewHolderOfMbb98(this);
                break;
            case DeviceInit.DEV_BP_CX:          // 插线版
                if (TextUtils.isEmpty(HeartPressCx.getInstance().getSbp())) {
                    return;
                }
                resultViewHolder = new ViewHolderOfCx(this);
                break;
            case DeviceInit.DEV_BP_YF:            // 云峰:
                if (TextUtils.isEmpty(HeartPressYf.getInstance().getSbp())) {
                    return;
                }
                resultViewHolder = new ViewHolderOfYf(this);
                break;
            case DeviceInit.DEV_BP_AVE:        // AVE:
                if (TextUtils.isEmpty(HeartPressAve.getInstance().getSbp())) {
                    return;
                }
                resultViewHolder = new ViewHolderOfAve(this);
                break;
            case DeviceInit.DEV_ECG_6:          // 6导:
                if (TextUtils.isEmpty(HearRate.getInstance().getHeartRate())) {
                    return;
                }
                resultViewHolder = new ViewHolderOfEcg6(this);
                break;
            case DeviceInit.DEV_ECG_HD:          // 红豆:
                if (TextUtils.isEmpty(HearRateHd.getInstance().getHeartRate())) {
                    return;
                }
                resultViewHolder = new ViewHolderOfEcgHd(this);
                break;
            case DeviceInit.DEV_GLU:            // 血糖:
                if (TextUtils.isEmpty(Glucose.getInstance().getSpeed())) {
                    return;
                }
                resultViewHolder = new ViewHolderOfGlu(this);
                break;
            case DeviceInit.DEV_ECG_12:          // 12导:
                if (TextUtils.isEmpty(HearRate12.getInstance().getHeartRate())) {
                    return;
                }
                resultViewHolder = new ViewHolderOfEcg12(this);
                break;
            case DeviceInit.DEV_FORA_GLU:            // 福尔血糖:
                if (ForaGlucose.getForaGluInstance().getGlucose() <= 0) {
                    return;
                }
                resultViewHolder = new ViewHolderOfForaGlu(this);
                break;
            default:
                resultViewHolder = new ViewHolderOfNull(this);
        }
        this.addView(resultViewHolder.view);
        LayoutParams layoutParams = (LayoutParams) resultViewHolder.view.getLayoutParams();
        if (getChildCount() != 0) {
            layoutParams.setMargins(dip2px(14f), 0, 0, 0);
        }
        resultViewHolder.setResult(null);
        holderList.add(resultViewHolder);
        if (resultViewHolder.hasData()) {
            requestNum++;
        }
    }

    private int dip2px(float dp) {
        float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public void startRequest(RequestCallback callback) {
        this.callback = callback;
        for (BaseResultViewHolder resultViewHolder : holderList) {
            resultViewHolder.request(callback);
        }
    }

    public int getNeedRequestNum() {
        return requestNum;
    }
}

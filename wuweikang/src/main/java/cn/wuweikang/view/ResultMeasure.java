package cn.wuweikang.view;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.guider.health.common.cache.MeasureDataUploader;
import com.guider.health.common.core.HearRate12;

import java.util.Iterator;
import java.util.Set;

import ble.BleClient;
import cn.wuweikang.R;
import cn.wuweikang.WuweikangData;

/**
 * Created by haix on 2019/7/29.
 */

public class ResultMeasure extends Dao12Interface {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        BleClient.instance().disconnect(BleClient.instance().popStagingDevice());
        WuweikangData.getInstance().disconnect();
        //下一步
        view.findViewById(R.id.connect_next).setOnClickListener(new FinishClick(this));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            view.findViewById(R.id.reminderScrollview).setNestedScrollingEnabled(true);
        }
        setHeart(HearRate12.getInstance().getHeartRate());
        setResult(HearRate12.getInstance().getAnalysisList());
        setOther(HearRate12.getInstance().getAnalysisResults());

        MeasureDataUploader.getInstance(_mActivity).uploadEcd12(HearRate12.getInstance().getDeviceAddress(), HearRate12.getInstance());
    }


    public void setHeart(String heart) {
        if (!TextUtils.isEmpty(heart)) {
            ((TextView) view.findViewById(R.id.result)).setText(heart);
        }
    }

    public void setResult(Set<String> list) {
        if (list == null || list.size() <= 0) {
            return;
        }
        StringBuffer re = new StringBuffer();
        Iterator<String> iterator = list.iterator();
        while (iterator.hasNext()) {
            String next = iterator.next();
            if (iterator.hasNext()) {
                re.append(next).append("\n");
            } else {
                re.append(next);
            }
        }
        ((TextView) view.findViewById(R.id.reminder)).setText(re.toString());
    }


    public void setOther(String ohter) {
        if (!TextUtils.isEmpty(ohter)) {
            ((TextView) view.findViewById(R.id.text2)).setText("* " + ohter + " *");
        }
    }

}

package com.guider.health.adapter;

import android.view.ViewGroup;
import android.widget.TextView;

import com.guider.health.all.R;
import com.guider.health.common.core.HearRateHd;
import com.guider.health.common.device.Unit;

/**
 * 移动版血压仪
 */
public class ViewHolderOfEcgHd extends BaseResultViewHolder {

    TextView tvName;

    public ViewHolderOfEcgHd(ViewGroup viewGroup) {
        super(viewGroup);
        tvName = view.findViewById(R.id.name);
        tvName.setText(getName());
        view.setBackgroundResource(R.mipmap.long_icon4);
        //        View rootView = view.findViewById(R.id.root);
    }

    @Override
    protected int getRequestNum() {
        return 1;
    }

    @Override
    protected int getLayout() {
        return R.layout.item_ecg_hd;
    }

    @Override
    protected String getName() {
        return getContext().getResources().getString(R.string.ecg);
    }

    @Override
    void setResult(Object result) {
        if (hasData()) {
            Unit unit = new Unit();
            HearRateHd instance = HearRateHd.getInstance();
            TooLazyToWrite.setTextView(view, R.id.xinlv, instance.getHeartRate() + unit.heart);
            TooLazyToWrite.setTextView(view, R.id.xinlvjiankang, instance.getAlcholRiskScore() + "分");
            TooLazyToWrite.setTextView(view, R.id.yali, instance.getStressScore() + "分");
            TooLazyToWrite.setTextView(view, R.id.pilao, instance.getEmotionScore() + "分");
            TooLazyToWrite.setTextView(view, R.id.tizhi, instance.getBodyFatRatio() + "%");
        }
    }

    @Override
    public void request(final RequestCallback callback) {
        if (requestStatus == REQUEST_STATUS_OK) {
            return;
        }
    }

    @Override
    public boolean hasData() {
        return HearRateHd.getInstance().isTag();
    }
}

package com.guider.health.adapter;

import android.view.ViewGroup;
import android.widget.TextView;
import com.guider.health.all.R;
import com.guider.health.common.core.ForaBO;

/**
 * 福尔血糖
 */
public class ViewHolderOfForaBO extends BaseResultViewHolder {

    TextView tvName;

    public ViewHolderOfForaBO(ViewGroup viewGroup) {
        super(viewGroup);
        tvName = view.findViewById(R.id.name);
        tvName.setText(getName());
        view.setBackgroundResource(R.mipmap.long_icon1);
    }

    @Override
    protected int getRequestNum() {
        return 1;
    }

    @Override
    protected int getLayout() {
        return R.layout.item_fora_bo;
    }

    @Override
    protected String getName() {
        return getContext().getResources().getString(R.string.blood_oxygen);
    }

    @Override
    void setResult(Object result) {
        if (hasData()) {
            ForaBO instance = ForaBO.getForaBOInstance();
            TooLazyToWrite.setTextView(view, R.id.fora_bo, instance.getValue() + "%");
            TooLazyToWrite.setTextView(view, R.id.fora_hb, instance.getHeartBeat() + "bpm");

            TooLazyToWrite.setTextView(view, R.id.status, instance.getCardShowStr());

            // 建议
            int rcId;
            if (instance.getValue() > 90)
                rcId = R.string.common_bo_suggest_90;
            else if (instance.getValue() >= 80 && instance.getValue() <= 89)
                rcId = R.string.common_bo_suggest_80_89;
            else if (instance.getValue() >= 70 && instance.getValue() <= 79)
                rcId = R.string.common_bo_suggest_70_79;
            else if (instance.getValue() >= 60 && instance.getValue() <= 69)
                rcId = R.string.common_bo_suggest_60_69;
            else rcId = R.string.common_bo_suggest_59;
            TooLazyToWrite.setTextView(view, R.id.describe, getContext().getString(rcId));
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
        return ForaBO.getForaBOInstance().isTag();
    }
}

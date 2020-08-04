package com.guider.health.adapter;

import android.view.ViewGroup;
import android.widget.TextView;
import com.guider.health.all.R;
import com.guider.health.common.core.ForaET;

/**
 * 福尔耳温
 */
public class ViewHolderOfForaET extends BaseResultViewHolder {

    private TextView tvName;

    public ViewHolderOfForaET(ViewGroup viewGroup) {
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
        return R.layout.item_fora_et;
    }

    @Override
    protected String getName() {
        return getContext().getResources().getString(R.string.ear_temp);
    }

    @Override
    void setResult(Object result) {
        if (hasData()) {
            ForaET instance = ForaET.getForaETInstance();
            TooLazyToWrite.setTextView(view, R.id.fora_glu, instance.getValue() + "°C");
            TooLazyToWrite.setTextView(view, R.id.status, instance.getCardShowStr());

            // 建议
            int rcid = 0;
            if (instance.getValue() < 36)
                rcid = R.string.common_temp_suggest_36;
            else if (instance.getValue() >= 36 && instance.getValue() <= 37.2)
                rcid = R.string.common_temp_suggest_36_37_2;
            else if (instance.getValue() >= 37.3 && instance.getValue() <= 38)
                rcid = R.string.common_temp_suggest_37_3_38;
            else if (instance.getValue() >= 38.1 && instance.getValue() <= 39)
                rcid = R.string.common_temp_suggest_38_1_39;
            else if (instance.getValue() >= 39.1 && instance.getValue() <= 41)
                rcid = R.string.common_temp_suggest_39_1_41;
            else if (instance.getValue() > 41)
                rcid = R.string.common_temp_suggest_41;
            TooLazyToWrite.setTextView(view, R.id.describe, getContext().getString(rcid));
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
        return ForaET.getForaETInstance().isTag();
    }
}

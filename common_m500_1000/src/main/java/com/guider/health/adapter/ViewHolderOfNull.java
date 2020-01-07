package com.guider.health.adapter;

import android.view.ViewGroup;
import android.widget.TextView;

import com.guider.health.all.R;
import com.guider.health.common.core.HeartPressBp;

/**
 * 移动版血压仪
 */
public class ViewHolderOfNull extends BaseResultViewHolder {

    TextView tvName;

    public ViewHolderOfNull(ViewGroup viewGroup) {
        super(viewGroup);
        tvName = view.findViewById(R.id.name);
        tvName.setText(getName());
        view.setBackgroundResource(R.mipmap.long_icon1);
    }

    @Override
    protected int getRequestNum() {
        return 2;
    }

    @Override
    protected int getLayout() {
        return R.layout.item_ave2000;
    }

    @Override
    protected String getName() {
        return "Null";
    }

    @Override
    void setResult(Object result) {

    }


    @Override
    public void request(RequestCallback callback) {
        callback.onRequestFinish(getName() , "" , RequestCallback.CODE_OK);
    }

    @Override
    public boolean hasData() {
        return HeartPressBp.getInstance().isTag();
    }
}

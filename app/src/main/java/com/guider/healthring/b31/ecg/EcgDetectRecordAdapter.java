package com.guider.healthring.b31.ecg;

import android.content.Context;

import com.google.gson.Gson;
import com.guider.healthring.R;
import com.guider.healthring.b31.ecg.bean.EcgDetectStateBean;
import com.guider.healthring.b31.ecg.bean.EcgSourceBean;
import com.guider.healthring.w30s.adapters.CommonRecyclerAdapter;
import com.guider.healthring.w30s.adapters.MyViewHolder;
import java.util.List;

/**
 * ecg测量记录展示adapter
 * Created by Admin
 * Date 2021/4/21
 */
class EcgDetectRecordAdapter extends CommonRecyclerAdapter<EcgSourceBean> {


    public EcgDetectRecordAdapter(Context context, List<EcgSourceBean> data, int layoutId) {
        super(context, data, layoutId);
    }

    @Override
    public void convert(MyViewHolder holder, EcgSourceBean item) {
        String ecgDetectState = item.getEcgDetectStateBeanStr();
        EcgDetectStateBean ecgDetectStateBean = new Gson().fromJson(ecgDetectState,EcgDetectStateBean.class);
        holder.setText(R.id.itemEcgRecordTimeTv,item.getDetectTime());
        holder.setText(R.id.itemEcgRecordHeartTv,(ecgDetectStateBean.getHr2() == 0 ?"--":ecgDetectStateBean.getHr2())+" 次/分");
        holder.setText(R.id.itemEcgRecordQtTv,(ecgDetectStateBean.getQtc() == 0 ? "--" : ecgDetectStateBean.getQtc()) +" 毫秒");
        holder.setText(R.id.itemEcgRecordHrvTv,(ecgDetectStateBean.getHrv() == 0 || ecgDetectStateBean.getHrv() == 255 ? "--" : ecgDetectStateBean.getHr2())+" 毫秒");

    }
}
